/*
 * GameConfig.java
 *
 * Created on October 31, 2000, 6:53 PM
 *
 * $Id: GameConfig.java,v 1.1 2004/04/02 06:01:59 sdchen Exp $
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
 *    $Log: GameConfig.java,v $
 *    Revision 1.1  2004/04/02 06:01:59  sdchen
 *    Snapshot of jQuizShow 1.1 alpha06 development
 *
 *    Revision 1.5  2002/08/15 04:43:26  sdchen
 *    Internationalization of source code.  Main.getMessage() is the primary
 *    routine to get the localized message strings.
 *
 *    Revision 1.4  2002/06/06 03:26:19  sdchen
 *    Removed exception handling code and handled "null" programmatically.
 *
 *    Revision 1.3  2002/06/04 02:55:21  sdchen
 *    Fixed bug -- NullPointerException occurred if property does not exist due
 *    to use of String.trim() on return value.  Catch this and output an error
 *    msg to System.out.
 *
 *    Revision 1.2  2002/05/16 05:13:46  sdchen
 *    Added String.trim() calls to remove whitespaces from returned values
 *
 *    Revision 1.1.1.1  2002/04/13 17:01:14  sdchen
 *    Initial import of the jQuizShow sources from local development directory.
 *
 *
 */

package jQuizShow;


/** Manages the jQuizShow configuration database. This database is read from
 * the primary configuration file.
 *
 * @author  Steven D. Chen
 */

import java.io.*;
import java.net.*;
import java.util.*;

import jQuizShow.util.*;

public class GameConfig
{
    // Bit masks for debugMode                                0xffffffff
    public static final int DEBUG_BASIC                     = 0x00000001;
    public static final int DEBUG_INPUTS                    = 0x00000002;
    public static final int DEBUG_FILE_IO                   = 0x00000008;
    public static final int DEBUG_SOUND                     = 0x00000010;
    public static final int DEBUG_NETWORK_SETUP             = 0x00000100;
    public static final int DEBUG_NETWORK_EVENTS            = 0x00000200;
    public static final int DEBUG_SHOW_TEXT_BOXES           = 0x00000400;
    public static final int DEBUG_MSGS_TO_STDOUT            = 0x10000000;
    public static final int DEBUG_MSGS_TO_LOG               = 0x20000000;
    
    // -----------------------------------------------------------------------
    //   Constructors
    // -----------------------------------------------------------------------
    
    public static GameConfig  getInstance() {
        if (singletonGameConfig == null)
            singletonGameConfig = new GameConfig();
        
        return singletonGameConfig;
    }

    private GameConfig() {
        m_properties = new Properties();
    }

    // -----------------------------------------------------------------------
    //   Accessors
    // -----------------------------------------------------------------------
    
    public int  getNumLevels()
    {
        return m_numLevels;
    }
    
    public String  getLevelName(int  level) {
        if (level < 0 || level >= m_numLevels)
            return "Error";
        
	return (m_properties.getProperty("levelName" + level,
		"Level " + level)).trim();
    }
    
    public String  getConfig(String  key)
    {
	String  prop = m_properties.getProperty(key);

	if (prop == null)
	    return null;
	else
	    return prop.trim();
    }
    
    public String  getConfig(String  key, String  defaultValue)
    {
	String  prop = m_properties.getProperty(key, defaultValue);

	if (prop == null)
	    return null;
	else
	    return prop.trim();
    }
    
    public int  getIntConfig(String  key)
    {
        return getIntConfig(key, 0);
    }
    
    public int  getIntConfig(String  key, int  defaultValue)
    {
        String  property;
        
        property = m_properties.getProperty(key);
        
        if (property != null)
        {
            return Integer.decode(property).intValue();
        }
        else
            return defaultValue;
    }
    
    // -----------------------------------------------------------------------
    //   Manipulators
    // -----------------------------------------------------------------------
        
    public int  loadGameConfig(String  filename) throws IOException
    {
        m_filename = new String(filename);
        
        InputStream  fin = FileUtils.openFile(m_filename);

        m_properties.load(fin);

        // Initialize key variables
        try
        {
            m_numLevels = (Integer.valueOf(m_properties.getProperty("numLevels"))).intValue();
        }
        catch (NumberFormatException  e)
        {
	    Object  args[] =
		    {
			m_filename
		    };

            System.out.println(Main.getMessage("err_num_levels", args));
            System.exit(2);
        }

        return 0;
    }
    
    void  unloadGameConfig() {
        m_properties.clear();
        return;
    }

    // -----------------------------------------------------------------------
    //   Static (class) data
    // -----------------------------------------------------------------------
    
    private static GameConfig  singletonGameConfig = null;
    
    // -----------------------------------------------------------------------
    //   Member data
    // -----------------------------------------------------------------------
    
    private String  m_filename;
    
    private Properties  m_properties;       // Game config properties
    
    private int  m_numLevels;               // Number of levels
}
