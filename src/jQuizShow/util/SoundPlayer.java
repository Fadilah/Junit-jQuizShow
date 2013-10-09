/*
 * SoundPlayer.java
 *
 * Created on May 19, 2001, 4:36 PM
 *
 * $Id: SoundPlayer.java,v 1.1 2004/04/02 06:02:00 sdchen Exp $
 *
 *============================================================================
 *
 * Copyright (C) 2001  Steven D. Chen
 *
 * This file is part of jQuizShow.
 *
 * jQuizShow is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * jQuizShow is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License (GPL)
 * along with jQuizShow; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *============================================================================
 *
 * Modifications:
 *
 *    $Log: SoundPlayer.java,v $
 *    Revision 1.1  2004/04/02 06:02:00  sdchen
 *    Snapshot of jQuizShow 1.1 alpha06 development
 *
 *    Revision 1.6  2002/08/15 04:43:26  sdchen
 *    Internationalization of source code.  Main.getMessage() is the primary
 *    routine to get the localized message strings.
 *
 *    Revision 1.5  2002/06/24 04:54:01  sdchen
 *    Added code to override the sound_BaseDirectory property with the property
 *    specified in the sound.ini file (if read).  If option not specified, the
 *    base directory is "".
 *
 *    Revision 1.4  2002/06/23 04:14:57  sdchen
 *    Added code to support JAR-specific sound file formats.
 *
 *    Revision 1.3  2002/06/06 03:24:40  sdchen
 *    Added code to close the AudioStream after testing and loading the clip.
 *
 *    Revision 1.2  2002/05/28 00:21:28  sdchen
 *    Added code to allow the user to specify a JAR file containing a sound.ini
 *    and associated sound files.
 *
 *    Revision 1.1.1.1  2002/04/13 17:01:32  sdchen
 *    Initial import of the jQuizShow sources from local development directory.
 *
 *
 */

package jQuizShow.util;

/**
 *
 * @author  Steven D. Chen
 * @version 1.0
 */


import java.io.*;
import java.util.*;
import java.util.jar.*;
import java.lang.reflect.*;
import javax.sound.sampled.*;

import jQuizShow.*;

public class SoundPlayer
        implements LineListener
{
    /* Sound channels */
    public static final int FOREGROUND_CHANNEL = 0;
    public static final int BACKGROUND_CHANNEL = 1;
    public static final int EFFECTS_CHANNEL = 2;
    public static final int ALL_CHANNELS = 3;

    private static final String  SOUND_CFG_FILE = "sound.ini";
    private static final String  DECODER_CLASS = "FileDecoder";

    
    /** Creates/gets the SoundPlayer singleton instance.
     */
    public static SoundPlayer getInstance()
    {
        if (m_soundPlayerSingleton == null)
            m_soundPlayerSingleton = new SoundPlayer();
        
        return m_soundPlayerSingleton;
    }
    
    /** Creates the singleton of this class.
     */
    private SoundPlayer()
    {
	String  jarFilename;

        if (AudioSystem.getMixer(null) == null)
	{
            System.out.println(Main.getMessage("warn_sound_unavailable"));
	    m_soundAvailable = false;
	}
	else
	    m_soundAvailable = true;

        m_playList = new LinkedList();
        
        /* Get the names of the audio files */
        m_audioFiles = new String[SoundIDEnum.length()];

        GameConfig  gameConfig = GameConfig.getInstance();

        m_debugMode = gameConfig.getIntConfig("debugMode", 0);

	if (gameConfig.getIntConfig("soundEnabled", 1) == 0)
	    m_soundAvailable = false;
        
	if (m_soundAvailable == false)
	    return;

        m_baseDir = gameConfig.getConfig("sound_BaseDirectory", "");
        m_baseDir = m_baseDir.replace('/', File.separatorChar);

        jarFilename = gameConfig.getConfig("sound_JarFile", "");
        jarFilename = jarFilename.replace('/', File.separatorChar);

	File  jarFile = new File(jarFilename);
        
	if (jarFilename.endsWith(".jar") && jarFile.canRead())
	{
	    // JAR file specified.  Open it.
	    {
		Object  args[] =
			{
			    jarFilename
			};

		System.out.println(Main.getMessage("msg_using_add_on_sound",
			args));
	    }

            try
            {
		m_jarFile = new JarFile(jarFilename);
            }
            catch (IOException  e)
            {
		Object  args[] =
			{
			    jarFilename
			};

                m_jarFile = null;
                
		System.out.println(Main.getMessage("err_reading_sound_jar",
			args));

		m_soundAvailable = false;
		return;
            }

	    // Read the SOUND_CFG_FILE file in the JAR.  It is an error
	    // if it is missing.
	    String  filepath;

	    try
	    {
		m_jarSoundConfig = new Properties();

                filepath = FileUtils.searchForFile(SOUND_CFG_FILE,
			m_jarFile);
		
		InputStream  fin = FileUtils.openFile(filepath, m_jarFile);

		m_jarSoundConfig.load(fin);

		fin.close();

		// Get the sound_BaseDirectory from the JAR's sound.ini.
		m_baseDir = m_jarSoundConfig.getProperty("sound_BaseDirectory", "");
		m_baseDir.trim();
		m_baseDir = m_baseDir.replace('/', File.separatorChar);
	    }
	    catch (FileNotFoundException  fnf_e)
	    {
		Object  args[] =
			{
			    SOUND_CFG_FILE,
			    m_jarFile.getName()
			};

		System.out.println(Main.getMessage("err_missing_sound_cfg",
			args));

		m_soundAvailable = false;
		return;
	    }
	    catch (IOException  io_e)
	    {
		Object  args[] =
			{
			    SOUND_CFG_FILE,
			    m_jarFile.getName()
			};

		System.out.println(Main.getMessage("err_reading_sound_cfg",
			args));

		m_soundAvailable = false;
		return;
	    }

	    /* Check if there is a SoundDecoder class in the JAR.
	     * If there is, load it so it can be used to decode
	     * the sound/music files.
	     */
	    try
	    {
		filepath = FileUtils.searchForFile(DECODER_CLASS + ".class",
			m_jarFile);

		ExternClassLoader  eClassLoader = new ExternClassLoader(null,
		        m_jarFile);

                try
		{
		    Class  decoder = eClassLoader.loadClass(DECODER_CLASS);
		    
		    Object  obj = decoder.newInstance();

		    m_decoder = (DecryptInputStream) obj;
		}
		catch (ClassNotFoundException  cnf_e)
		{
		    System.out.println(Main.getMessage("err_invalid_decoder_class"));
		    System.out.println(cnf_e.getLocalizedMessage());
		    m_soundAvailable = false;
		    return;
		}
		catch (InstantiationException  i_e)
		{
		    System.out.println(Main.getMessage("err_inst_decoder_class"));
		    System.out.println(i_e.getLocalizedMessage());
		    m_soundAvailable = false;
		    return;
		}
		catch (IllegalAccessException  ia_e)
		{
		    System.out.println(Main.getMessage("err_access_decoder_class"));
		    System.out.println(ia_e.getLocalizedMessage());
		    m_soundAvailable = false;
		    return;
		}
		catch (ClassCastException  cc_e)
		{
		    System.out.println(Main.getMessage("err_cast_decoder_class"));
		    System.out.println(cc_e.getLocalizedMessage());
		    m_soundAvailable = false;
		    return;
		}
	    }
	    catch (FileNotFoundException  fnf_e)
	    {
	        /* Okay if it does not exist. */
	    }
	}

	if (m_baseDir.length() > 0 &&
		m_baseDir.endsWith(File.separator) == false)
	{
            m_baseDir = new String(m_baseDir + File.separator);
	}

        /* Attempt to load each of the specified audio files.
	 * Disable those that are null or invalid.
	 */
        for (Iterator  iter = SoundIDEnum.ANSWER_CORRECT.iterator(); iter.hasNext(); /**/ )
        {
            String  filename;
	    
            SoundIDEnum   id = (SoundIDEnum) iter.next();
            
	    if (m_jarSoundConfig != null)
	    {
		filename = m_jarSoundConfig.getProperty(id.toString());
		if (filename != null)
                    filename.trim();
	    }
	    else
		filename = gameConfig.getConfig(id.toString());
            
            if (filename == null || filename.length() == 0)
                continue;
            
            String  filepath;
            
            try
            {
                filepath = FileUtils.searchForFile(m_baseDir + filename,
			m_jarFile);

                try
                {
                    AudioInputStream  audioStream = null;

		    BufferedInputStream  fin = new BufferedInputStream(
			    FileUtils.openFile(filepath, m_jarFile));

		    if (m_decoder != null)
		    {
		        /* Decoder class specified.  Use it to decode the
			 * InputStream.
			 */
			if ((m_debugMode & GameConfig.DEBUG_SOUND) != 0)
			    System.out.println("Decoding " + filepath);

		        try
			{
			    m_decoder.setInputStream(fin);

			    audioStream = AudioSystem.getAudioInputStream(
			    	    m_decoder);
			}
			catch (Throwable  e)
			{
			    Object  args[] =
				    {
					e.getLocalizedMessage()
				    };
			    System.out.println(Main.getMessage("err_decoding",
				    args));
			}
		    }
		    else
		    {
		        /* Load the specified audio file */
			audioStream = AudioSystem.getAudioInputStream(fin);
		    }

                    if (audioStream != null)
		    {
			m_audioFiles[id.value()] = filepath;

			audioStream.close();
		    }
                }
                catch (UnsupportedAudioFileException  e)
                {
		    Object  args[] =
			    {
				filename
			    };

                    System.out.println(Main.getMessage("err_sound_fmt", args));
                }
                catch (IOException  e)
                {
		    Object  args[] =
			    {
				filename
			    };

                    System.out.println(Main.getMessage("err_sound_io", args));
                    System.out.println(e.getLocalizedMessage());
                }
            }
            catch (FileNotFoundException  e)
            {
		Object  args[] =
			{
			    id.toString()
			};

                System.out.println(Main.getMessage("warn_sound_missing", args));
            }
        }
        
        m_currentClip = new Clip[m_channels.length];
        m_loop = new boolean[m_channels.length];
        
        /* Initialize the gain levels */

        m_gainLevels = new int[m_channels.length];
        
        for (int  i = 0; i < m_gainLevels.length; i++)
            m_gainLevels[i] = 70;           // Default gain level

        int  gain;
        
        gain = gameConfig.getIntConfig("volumeForeground", 70);
        gain = gain < 0 ? 0 : (gain > 100 ? 100 : gain);
        m_gainLevels[FOREGROUND_CHANNEL] = gain;
        m_gainLevels[EFFECTS_CHANNEL] = gain;
        
        gain = gameConfig.getIntConfig("volumeBackground", 70);
        gain = gain < 0 ? 0 : (gain > 100 ? 100 : gain);
        m_gainLevels[BACKGROUND_CHANNEL] = gain;
    }

    /** Load a sound into the jukebox */
    public void  loadSound(SoundIDEnum  soundId, int  type, boolean  loop)
    {
        if (m_soundAvailable == false || type < 0 || type >= m_currentClip.length)
            return;

        if (m_currentClip[type] != null)
        {
            stop(type);
            m_currentClip[type] = null;
        }
        
        if (m_audioFiles[soundId.value()] == null)
        {
            return;
        }

        String  filepath = m_audioFiles[soundId.value()];
        
        try
        {
	    BufferedInputStream  fin = new BufferedInputStream(
	    	    FileUtils.openFile(filepath, m_jarFile));

	    if (m_decoder != null)
	    {
		/* Decoder class specified.  Use it to decode the
		 * InputStream.
		 */
		m_decoder.setInputStream(fin);

		m_audioStream = AudioSystem.getAudioInputStream(m_decoder);
	    }
	    else
	    {
		/* Load the specified audio file */
		m_audioStream = AudioSystem.getAudioInputStream(fin);
	    }
        }
        catch (UnsupportedAudioFileException  e)
        {
	    Object  args[] =
		    {
			filepath
		    };

            System.out.println(Main.getMessage("err_sound_fmt", args));
            return;
        }
        catch (IOException  e)
        {
	    Object  args[] =
		    {
			filepath
		    };

            System.out.println(Main.getMessage("err_sound_io", args));
            return;
        }

        AudioFormat  format = m_audioStream.getFormat();

        try
        {
            DataLine.Info info = new DataLine.Info(
                    Clip.class, m_audioStream.getFormat(), 
                    ((int) m_audioStream.getFrameLength() *
                      format.getFrameSize()));

            m_currentClip[type] = (Clip) AudioSystem.getLine(info);
            m_currentClip[type].addLineListener(this);

            m_currentClip[type].open(m_audioStream);

            m_loop[type] = loop;
            
            setClipGain(type, m_gainLevels[type]);
        }
        catch (LineUnavailableException  e)
        {
	    Object  args[] =
		    {
			filepath,
			soundChannels[type]
		    };

            System.out.println(Main.getMessage("err_line_unavailable", args));
            m_currentClip[type] = null;
            
            stop(ALL_CHANNELS);
            
            m_soundAvailable = false;
            return;
        }
        catch (IOException  e)
        {
	    Object  args[] =
		    {
			filepath
		    };

            System.out.println(Main.getMessage("err_sound_io", args));
            m_currentClip[type] = null;
        }

	/* Close the audio stream since it was loaded when the Clip was
	 * opened.
	 */
        try
	{
	    m_audioStream.close();
	}
	catch (IOException  ie)
	{
	    Object  args[] =
		    {
			filepath
		    };

            System.out.println(Main.getMessage("err_closing_stream", args));
	    System.out.println(ie.getLocalizedMessage());
	}
    }

    /**
     * Load a playlist into the jukebox.  This will play the specified
     * SoundIDEnum sequence, one after the other.
     */
    public void  loadPlayList(SoundIDEnum[]  soundIDs)
    {
        if (m_soundAvailable == false || soundIDs.length == 0)
            return;

        if (m_currentClip[BACKGROUND_CHANNEL] != null)
        {
            stop(BACKGROUND_CHANNEL);
            m_currentClip[BACKGROUND_CHANNEL] = null;
        }
        
        m_playList.clear();
        
        for (int  idx = 0; idx < soundIDs.length; idx++)
        {
            m_playList.add(soundIDs[idx]);
        }
        
        m_iter = m_playList.iterator();
        loadSound((SoundIDEnum) m_iter.next(), BACKGROUND_CHANNEL, false);
    }
    
    public void  start(int  type)
    {
        if (m_soundAvailable == false || type < 0 ||
                (type >= m_currentClip.length && type != ALL_CHANNELS))
            return;

        int  first = 0;
        int  last = m_currentClip.length;
        
        if (type != ALL_CHANNELS)
        {
            first = type;
            last = type + 1;
        }

        for (int  i = first; i < last; i++)
        {
            if (m_currentClip[i] != null && m_currentClip[i].isRunning() == false)
            {
                m_currentClip[i].setFramePosition(0);
            
                if (m_loop[i])
                    m_currentClip[type].loop(Clip.LOOP_CONTINUOUSLY);
                
                m_currentClip[i].start();
            }
        }

        return;
    }

    public void stop(int  type)
    {
        if (m_soundAvailable == false || type < 0 ||
                (type >= m_currentClip.length && type != ALL_CHANNELS))
            return;

        int  first = 0;
        int  last = m_currentClip.length;
        
        if (type != ALL_CHANNELS)
        {
            first = type;
            last = type + 1;
        }

        for (int  i = first; i < last; i++)
        {
            if (m_currentClip[i] != null)
            {
                // If this is the background channel and the playlist is not
                // empty, clear the playlist.
                if (i == BACKGROUND_CHANNEL && m_playList.isEmpty() == false)
                    m_playList.clear();
            
                // Stop the clip on this channel
                m_currentClip[i].stop();
            }
        }

        return;
    }

    public boolean getMute()
    {
        return m_muted;
    }

    public void setMute(boolean  mute)
    {
        if (m_soundAvailable == false)
            return;

        m_muted = mute;

        /* Set the levels -- if muted, setClipGain() will take care of it. */
        for (int  i = 0; i < m_currentClip.length; i++)
            setClipGain(i, m_gainLevels[i]);

        return;
    }

    public boolean  isPlaying(int  type)
    {
        if (m_soundAvailable == false || type < 0 || type >= m_currentClip.length)
            return false;

        if (m_currentClip[type] != null)
            return m_currentClip[type].isRunning();
        else
            return false;
    }

    
    public int  getGain(int  type)
    {
        // Sanity checks
        if (m_soundAvailable == false || type < 0 || type >= m_gainLevels.length)
            return 0;

        return m_gainLevels[type];
    }
    
    
    public void  setGain(int  type, int  level)
    {
        if (m_soundAvailable == false || type < 0 ||
                (type >= m_currentClip.length && type != ALL_CHANNELS))
            return;
        
        if (level < 0)
            level = 0;
        else if (level > 100)
            level = 100;
        
        int  first = 0;
        int  last = m_currentClip.length;

        for (int  i = 0; i < m_currentClip.length; i++)
        {
            m_gainLevels[type] = level;

            setClipGain(type, m_gainLevels[type]);
        }
    }

    
    private void  setClipGain(int  type, int  level)
    {
        // Sanity checks
        if (m_soundAvailable == false || type < 0 ||
                (type >= m_currentClip.length && type != ALL_CHANNELS))
            return;
        
        if (level < 0 || m_muted)
            level = 0;
        else if (level > 100)
            level = 100;

        double value = level / 100.0;

        int  first = 0;
        int  last = m_currentClip.length;
        
        if (type != ALL_CHANNELS)
        {
            first = type;
            last = type + 1;
        }
        
        for (int  i = first; i < last; i++)
        {
            if (m_currentClip[i] == null)
                continue;
            
            try
            {
                FloatControl gainControl = 
                        (FloatControl) m_currentClip[i].getControl(FloatControl.Type.MASTER_GAIN);
                float dB = (float) 
                    (Math.log(value == 0.0 ? 0.0001 : value) / Math.log(10.0) * 20.0);

                gainControl.setValue(dB);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }
    
    
    public void update(javax.sound.sampled.LineEvent lineEvent)
    {
        if ((m_debugMode & GameConfig.DEBUG_SOUND) != 0)
            System.out.println("LineEvent:  " + lineEvent.toString());
        
        // Check if a playlist sample has ended.  If there are more samples
        // in the playlist, start the next one playing.
        if (lineEvent.getType() == LineEvent.Type.STOP &&
                lineEvent.getLine() == m_currentClip[BACKGROUND_CHANNEL] &&
                m_playList.isEmpty() == false)
        {
            if (m_iter.hasNext() == false)
            {
                // End of list -- resplay from top
                m_iter = m_playList.iterator();
            }

            SoundIDEnum  id = (SoundIDEnum) m_iter.next();

            m_currentClip[BACKGROUND_CHANNEL] = null;    // Clear so stop() won't call this again
            
            loadSound((SoundIDEnum) id, BACKGROUND_CHANNEL, false);
            start(SoundPlayer.BACKGROUND_CHANNEL);
        }
    }


    private static SoundPlayer  m_soundPlayerSingleton;      // Singleton

    private boolean  m_soundAvailable = false;
    
    private AudioInputStream  m_audioStream;
    
    private Clip[]  m_currentClip;
    
    private int  m_gainLevels[];         // Last gain levels used

    private boolean  m_loop[];

    private LinkedList  m_playList = null;         // Background music playlist
    private Iterator  m_iter = null;               // Current position in playlist
    
    private String[]  m_audioFiles;

    private String  m_baseDir;

    private FloatControl  m_gainControl;

    private boolean  m_muted = false;

    private boolean  m_soundEnabled = true;
    
    private int  m_debugMode = 0;

    private JarFile  m_jarFile;

    private Properties  m_jarSoundConfig;

    private DecryptInputStream  m_decoder = null;

    private static final int m_channels[] =
    {
        BACKGROUND_CHANNEL,
        FOREGROUND_CHANNEL,
        EFFECTS_CHANNEL
    };

    
    /** Channel names.
     * NOTE:  These MUST match the m_channels[] above!
     */
    private static final String[]  soundChannels =
    {
        "Foreground",
        "Background",
        "Effects",
    };
}
