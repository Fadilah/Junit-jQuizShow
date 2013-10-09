/*
 * RandomSelector.java
 *
 * Created on December 11, 2001, 7:23 PM
 *
 * $Id: RandomSelector.java,v 1.3 2007/02/05 04:24:19 sdchen Exp $
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
 *    $Log: RandomSelector.java,v $
 *    Revision 1.3  2007/02/05 04:24:19  sdchen
 *    Replaced deprecated isFocusTraversable() with setFocusable(boolean s).
 *
 *    Revision 1.2  2007/02/05 04:05:34  sdchen
 *    Replaced deprecated show() with setVisible(bool  s)
 *
 *    Revision 1.1  2004/04/02 06:01:59  sdchen
 *    Snapshot of jQuizShow 1.1 alpha06 development
 *
 *    Revision 1.3  2002/08/15 04:43:26  sdchen
 *    Internationalization of source code.  Main.getMessage() is the primary
 *    routine to get the localized message strings.
 *
 *    Revision 1.2  2002/05/23 04:43:33  sdchen
 *    *** empty log message ***
 *
 *    Revision 1.1.1.1  2002/04/13 17:01:14  sdchen
 *    Initial import of the jQuizShow sources from local development directory.
 *
 *
 */

package jQuizShow;

/** This class is used to support the random player selector option.
 * The code here is rudimentary and needs to be improved and cleaned
 * up; this was a hasty implementation to satisfy a short-term need.
 * The format of the input file is:
 *
 * <pre>
 *       class   player_name    friends
 *
 * where,
 *       class		= Number from 1 - 9 specifying the player's class
 *       player_name    = Name of the player
 *       friends        = Friend or list of friends for "phone a friend"
 * </pre>
 *
 * The three fields above must be separated by one or more TABs.
 * The "class" is used to form groups of players.  For example:
 *
 * <pre>
 *     1 = Jr. high girls
 *     2 = Jr. high boys
 *     3 = Sr. high girls
 *     4 = Sr. high boys
 * </pre>
 *
 * Example entries:
 *
 * <pre>
 *     1    John Doe        Billy Doe, Brad Doe, Brian Doe
 *     1    Jimmy Doe       Chris Doe, Colby Doe, Cassandra Doe
 *     2    Jane Doe        Stephanie Doe, Sally Doe, Susan Doe
 *     2    Jamie Doe       Penny Doe, Polly Doe, Patricia Doe
 * </pre>
 *
 * @author  Steven D. Chen
 */

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import jQuizShow.util.*;
import jQuizShow.game.classic.*;

public class RandomSelector extends JInternalFrame
{
    public static final String PLAYERS_FILE_KEYWORD = "playersFile";
    public static final String PLAYERS_DEFAULT_FILE = "players.txt";

    public static final String USED_PLAYERS_FILE_KEYWORD = "usedPlayersFile";
    public static final String USED_PLAYERS_DEFAULT_FILE = "usedPlayers.txt";

    private static final int PLAYER_ADDED = 0;
    private static final int BLANK_LINE = 1;
    private static final int INVALID_CLASS = 2;
    private static final int PLAYER_ALREADY_USED = 3;
    private static final int NOT_ENOUGH_FIELDS = 4;

    private static final int MAX_NUM_CATEGORIES = 10;

    private static final int TIMER_DELAY = 250;     // Delay in ms
    
    /** Creates new RandomSelector */
    public RandomSelector() {
        super("Random Selector", false, true, false, true);

        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);

        initComponents();

        // Initialize the ArrayList
        m_playerCategories = new ArrayList(MAX_NUM_CATEGORIES);

        m_gameConfig = GameConfig.getInstance();

        // Get the SoundPlayer singleton
        m_soundPlayer = SoundPlayer.getInstance();

        // Listen for keyboard events
        addKeyListener(new java.awt.event.KeyAdapter()
        {
            public void keyPressed(java.awt.event.KeyEvent evt)
            {
                int  keyCode = evt.getKeyCode();
                char  keyChar = evt.getKeyChar();
                int  category;

                if (m_timer == null)
                {
                    if (keyCode == KeyEvent.VK_ENTER && m_randomIndex >= 0)
                    {
                        // Accept this player
                        SelectorData  player = (SelectorData) m_currentList.remove(m_randomIndex);    // Remove from list
                        
                        // Add this player to the "used" player list
                        try
                        {
                            addPlayerToUsedPlayerFile(player);
                        }
                        catch (IOException  e)
                        {
                            System.out.println(Main.getMessage("err_random_write"));
			    System.out.println(e.getLocalizedMessage());
                        }
                        
                        m_player = null;
                        
                        setVisible(false);        // Hide this window

                        m_gameText.setText("", "");
                        m_gameText.setTextColor();      // Reset colors
                        return;
                    }
                    
                    category = Character.getNumericValue(keyChar);

                    if (category <= 0 || category >= m_playerCategories.size())
                        return;     // Invalid category # -- do nothing

                    m_currentList = (ArrayList) m_playerCategories.get(category);

                    if (m_currentList == null || m_currentList.size() == 0)
		    {
		        m_statusText.setText(Main.getMessage("err_no_players"));
			System.out.println(Main.getMessage("err_no_players"));
                        return;     // Empty list -- do nothing
		    }

                    // Create a timer to randomly select and display the player names.
                    m_randomIndex = -1;		// Invalidate the index

		    m_statusText.setText(Main.getMessage("msg_space_select"));

                    m_timer = new javax.swing.Timer(TIMER_DELAY, new ActionListener()
                    {
                        public void actionPerformed(ActionEvent evt)
                        {
                            int     num = m_randomIndex;

                            if (m_currentList.size() == 1)
                                m_randomIndex = 0;                  // Only person left
                            else
                            {
                                while (num == m_randomIndex)        // Select randomly from everyone but current
                                    num = m_random.nextInt(m_currentList.size());
                            
                                m_randomIndex = num;
                            }
                            
                            m_player = (SelectorData) m_currentList.get(m_randomIndex);
                            
                            m_gameText.setText("", m_player.getPlayer());
                        }
                    });
                    
                    m_timer.start();

                    m_soundPlayer.loadSound(SoundIDEnum.RANDOM_SELECTOR_BKG,
                            SoundPlayer.BACKGROUND_CHANNEL, true);
                    m_soundPlayer.start(SoundPlayer.BACKGROUND_CHANNEL);
                }
                else if (m_player != null && keyCode == KeyEvent.VK_SPACE)
                {
                    m_timer.stop();
                    m_timer = null;
		    
		    if (m_playerCategories.size() <= 1)
		    {
			m_statusText.setText(Main.getMessage("msg_enter_select1"));
		    }
		    else
		    {
			Object  args[] =
			        {
				    new Integer(m_playerCategories.size() - 1)
				};

			m_statusText.setText(Main.getMessage("msg_enter_select2",
				args));
		    }
                    
                    m_gameText.setTextColor(Color.white, Color.cyan, Color.black);
                    m_gameText.setText(m_player.getPlayer() + ": ", m_player.getFriends());

                    m_soundPlayer.stop(SoundPlayer.BACKGROUND_CHANNEL);

                    m_soundPlayer.loadSound(SoundIDEnum.RANDOM_SELECTOR_DONE,
                            SoundPlayer.FOREGROUND_CHANNEL, false);
                    m_soundPlayer.start(SoundPlayer.FOREGROUND_CHANNEL);
                }
            }
        });
    }

    
    /** Override the show() method so we can request focus when shown.
     */
    public void  isVisible(boolean  s)
    {
        super.setVisible(s);
        
        setFocusable(true);
    }
    
    /** Reads the available players database file. */
    public void  readDatabase(String  playersFilename, String  eliminatedPlayersFilename)
            throws IOException
    {
        // Read the list of "used" players, if any
        BufferedReader  fin;

        String      line;

        Set  usedPlayers = new HashSet();

        try
        {
            int     numUsed = 0;
            
            fin = openFile(eliminatedPlayersFilename);

            while ((line = fin.readLine()) != null)
            {
                if (line.startsWith("#"))
                    continue;

                usedPlayers.add(new String(line));
                numUsed++;
            }

            if ((m_gameConfig.getIntConfig("debugMode") & GameConfig.DEBUG_BASIC) != 0)
                System.out.println("Number of 'eliminated' players:  " + numUsed);
            
            fin.close();
        }
        catch (IOException  e)
        {
            // No file.  No problem.  It just means all players are valid.
            if ((m_gameConfig.getIntConfig("debugMode") & GameConfig.DEBUG_BASIC) != 0)
                System.out.println("Eliminated player list not found:  " + eliminatedPlayersFilename);
        }
            

        // Read the player database
        int     skipCount = 0;
        int     playerCount = 0;
        int     line_num = 0;
        int     err;
        
	fin = openFile(playersFilename);
        
        while ((line = fin.readLine()) != null)
        {
            line_num++;
            
            if (line.startsWith("#") || line.length() == 0)
                continue;

            // Parse the line
            if ((err = parseLine(line, usedPlayers)) == PLAYER_ADDED)
                playerCount++;
            else if (err == PLAYER_ALREADY_USED)
                skipCount++;
            else if (err == BLANK_LINE)
                /* */;
            else if (err == INVALID_CLASS)
	    {
		Object  args[] =
			{
			    new Integer(line_num),
			    line
			};

                System.out.println(Main.getMessage("err_invalid_class", args));
	    }
            else
	    {
		Object  args[] =
			{
			    new Integer(line_num),
			    line
			};

                System.out.println(Main.getMessage("err_invalid_player", args));
	    }
        }

	{
	    Object  args[] =
		    {
			new Integer(skipCount),
			new Integer(playerCount)
		    };

	    System.out.println(Main.getMessage("msg_num_players_skipped", args));
	    System.out.println(Main.getMessage("msg_num_players", args));
	}
        
        fin.close();
        
	if (m_playerCategories.size() <= 1)
	{
	    m_statusText.setText(Main.getMessage("msg_start_randomizer1"));
	}
	else
	{
	    Object  args[] =
		    {
			new Integer(m_playerCategories.size() - 1)
		    };

	    m_statusText.setText(Main.getMessage("msg_start_randomizer2",
		    args));
	}
    }

    
    /** This method appends the player to the "usedPlayerFile" so that
     * the player won't be reused the next time the game is started.
     */
    private void  addPlayerToUsedPlayerFile(SelectorData  player) throws IOException
    {
        if (m_gameConfig.getIntConfig("testMode") != 0)
            return;             // Don't write to file in test mode
        
        String  usedFilename = m_gameConfig.getConfig(USED_PLAYERS_FILE_KEYWORD,
                USED_PLAYERS_DEFAULT_FILE);
        
        File fileInst = new File(usedFilename);

        BufferedWriter  fout = new BufferedWriter(
                    new FileWriter(fileInst.toString(), true));

        fout.write(player.getPlayer() + "\n");
        
        fout.close();
    }

    
    /** Searches for the file and opens it. */
    private BufferedReader  openFile(String  filename)
            throws IOException
    {
        BufferedReader  fin;
        
        fin = new BufferedReader(
                new InputStreamReader(FileUtils.openFile(filename)));
        
        return (fin);
    }

    
    private int parseLine(String  s, Set  usedPlayers) throws IOException
    {
        StringTokenizer tok = new StringTokenizer(s, "\t");

        String          playerStr;
        int		count = 0;
        int             category;

        if (s.length() == 0)
            return BLANK_LINE;
        
        try {
            category = Integer.parseInt(tok.nextToken());
        }
        catch (NumberFormatException  e)
        {
            return INVALID_CLASS;
        }

	if (category < 1)
	    return INVALID_CLASS;

        if (category >= m_playerCategories.size() && category < MAX_NUM_CATEGORIES)
        {
            while (m_playerCategories.size() <= category)
                m_playerCategories.add(new ArrayList());
        }

        ArrayList  playerList = (ArrayList) m_playerCategories.get(category);
        
        // Read in the player
        playerStr = StringFilter.filterString(tok.nextToken());

        if (usedPlayers.contains(playerStr))
            return PLAYER_ALREADY_USED;       // If player already used, skip it

        if (tok.hasMoreTokens())
        {
            String friends = StringFilter.filterString(tok.nextToken());
            
            // Create a SelectorData instance for the player and his friends
            SelectorData  player = new SelectorData(playerStr, friends);

            playerList.add(player);

	    Object  args[] =
		    {
			playerStr,
			friends
		    };

            System.out.println(Main.getMessage("msg_display_players", args));
            
            return PLAYER_ADDED;
        }
        else
            return NOT_ENOUGH_FIELDS;
    }

    
    
    /** This method is called from within the constructor to
     * initialize the frame.
     */
    private void initComponents() {
        getContentPane().setLayout(new java.awt.BorderLayout());
        
        setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(50, 0, 50, 0)));
        setForeground(java.awt.Color.white);
        setBackground(java.awt.Color.black);
        
        m_gameText = new GameText(594, new Dimension(30, 1));
        m_gameText.centerText(true);

        getContentPane().add(m_gameText, java.awt.BorderLayout.CENTER);
        
	m_statusText = new JTextArea();
        m_statusText.setWrapStyleWord(true);
        m_statusText.setLineWrap(true);
        m_statusText.setForeground(java.awt.Color.white);
        m_statusText.setText("");
        m_statusText.setBackground(java.awt.Color.black);
        m_statusText.setDisabledTextColor(java.awt.Color.white);
        m_statusText.setEnabled(false);
        m_statusText.setRequestFocusEnabled(false);

        getContentPane().add(m_statusText, java.awt.BorderLayout.SOUTH);
        
        pack();
    }

    
    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {
        setVisible(false);
    }
   
    private GameConfig  m_gameConfig;

    public void setFocusable(boolean b) {
    }

    private jQuizShow.util.SoundPlayer  m_soundPlayer;
    
    private ArrayList  m_playerCategories;

    private ArrayList  m_currentList;
    
    private GameText  m_gameText;

    private JTextArea  m_statusText;

    private SelectorData  m_player;
    
    private javax.swing.Timer  m_timer = null;
    
    private Random  m_random = new Random();
    
    private int  m_randomIndex = -1;
    private int  m_randomCategory = -1;
}
