/*
 * ContribManager.java
 *
 * Created on September 17, 2002, 5:34 PM
 *
 * $Id: ContribManager.java,v 1.2 2007/02/05 03:55:48 sdchen Exp $
 *
 *============================================================================
 *
 * Copyright (C) 2002-2004  Steven D. Chen
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
 *    $Log: ContribManager.java,v $
 *    Revision 1.2  2007/02/05 03:55:48  sdchen
 *    Removed CR (Ctrl-M) from lines.
 *
 *    Revision 1.1  2004/04/02 06:02:00  sdchen
 *    Snapshot of jQuizShow 1.1 alpha06 development
 *
 *
 */

package jQuizShow.util;

import java.io.*;
import java.util.*;
import java.util.jar.*;
import java.util.zip.*;
import java.net.*;

import jQuizShow.*;
import jQuizShow.data.*;

/** This class is used for managing the contents of the "contrib" directory.
 *
 * @author  Steven D. Chen
 * @version 1.0
 */
public class ContribManager
{
    public static final String  PROPERTY_FILE = "jQuizShowAddOn.ini";

    public static final String  ADD_ON_TYPE = "type";
    public static final String  ADD_ON_FILENAME = "addOnFilename";


    /** Gets the ContribManager singleton instance.
     */
    public static ContribManager  getInstance()
    {
	if (m_singletonInstance == null)
	    m_singletonInstance = new ContribManager();

	return (m_singletonInstance);
    }
    

    /** Creates a new ContribManager instance
     *
     * NOTE:  This is private to support the singleton pattern.
     */
    private ContribManager()
    {
	m_database = new LinkedList();

	m_gameConfig = GameConfig.getInstance();
    }


    public class DirFilter
	implements FilenameFilter
    {
	public DirFilter(String  suffix)
	{
	    if (suffix != null)
		m_suffix = suffix;
	}

	public boolean  accept(File  dir, String  name)
	{
	    return name.endsWith(m_suffix);
	}

	private String  m_suffix = ".jar";
    }


    /** Search and catalog the Contrib directory.
     */
    public void  initialize(String  dirPath)
    {
        File	dir = new File(dirPath);

	File[]	files;

	DirFilter  filter;

	QuestionLoader  qLoader = QuestionLoader.getInstance();

	if (dir.exists() == false)
	    return;		// Does not exist

	if (dir.isDirectory() == false)
	    return;		// Not a directory

	{
	    Object  msgArgs[] =
		    {
			dirPath,
			null
		    };

	    String  message = Main.getMessage("contrib_scanning",
		    msgArgs);

	    System.out.println(message);
	}

	// Create a list of *.jar files in the directory.  JAR files
	// are the basic exchange format for all add-ons, except for
	// the question databases.
	filter = new DirFilter(".jar");

	files = dir.listFiles(filter);

	// Open and read the PROPERTY_FILE that should exist in each
	// JAR file.  If this file does not exist or does not
	// contain the correct information it is not a valid
	// jQuizShow add-on.  If it is valid, add the file to the
	// contrib database.
	for (int  idx = 0; idx < files.length; idx++)
	{
	    if ((m_gameConfig.getIntConfig("debugMode") & GameConfig.DEBUG_BASIC) != 0)
	    {
		System.out.println("--> jar:  " + files[idx]);
	    }

	    readPropertyFile(files[idx]);
	}

	// Create a list of (*.txt) in the directory.  These files
	// will be checked to see if they are jQuizShow question
	// files.  Those that are will be added to the list of
	// available questions databases.
	filter = new DirFilter(".txt");

	files = dir.listFiles(filter);

	for (int  idx = 0; idx < files.length; idx++)
	{
	    if ((m_gameConfig.getIntConfig("debugMode") & GameConfig.DEBUG_BASIC) != 0)
	    {
		System.out.println("--> txt:  " + files[idx]);

		if (qLoader.isValidFile(files[idx].toString()))
		{
		}
	    }

	}
    }


    /** Load the add-ons.
     */
    public void  load()
    {
	Properties		prop;

	ListIterator		iter;

	iter = m_database.listIterator(0);

	while (iter.hasNext())
	{
	    String  type;

	    prop = (Properties) iter.next();

	    type = prop.getProperty(ADD_ON_TYPE);

	}
    }


    /** Dump the Contrib database contents.
     */
    public void  dump()
    {
	Properties		prop;

	ListIterator		iter;

	iter = m_database.listIterator(0);

	while (iter.hasNext())
	{
	    prop = (Properties) iter.next();

	    System.out.println("File:  " + prop.getProperty(ADD_ON_FILENAME));
	    System.out.println("    Type:  " + prop.getProperty(ADD_ON_TYPE));
	}

	return;
    }


    /** Read the specified property file and add the information to the
      * add-on database.
     */
    private void  readPropertyFile(File  jarFilename)
    {
	Properties	prop = new Properties();

	Object  errArgs[] =
		{
		    jarFilename.toString(),
		    null
		};

	JarFile  jarFile;

	try
	{
	    jarFile = new JarFile(jarFilename);
	}
	catch (IOException  io_e)
	{
	    String  message = Main.getMessage("warn_contrib_jar_open",
		    errArgs);

	    System.out.println(message);

	    return;
	}

	// Check for and read the PROPERTY_FILE from the JAR file
	try
	{
	    prop.load(FileUtils.openFile(PROPERTY_FILE, jarFile));
	}
	catch (IOException  io_e)
	{
	    Object  args[] =
		    {
			jarFilename.toString(),
			PROPERTY_FILE
		    };

	    String  message = Main.getMessage("warn_contrib_config_missing",
		    args);

	    System.out.println(message);

	    return;
	}

	// Check the file for key properties.  The JAR file is invalid
	// if any of them do not exist.
	if (prop.getProperty(ADD_ON_TYPE) == null)
	{
	    errArgs[1] = ADD_ON_TYPE;

	    String  message = Main.getMessage("warn_contrib_bad_config",
		    errArgs);

	    System.out.println(message);

	    return;
	}

	// Store the JAR file name as a property
	prop.setProperty(ADD_ON_FILENAME, jarFile.toString());

	// Store the property file and the JAR file reference in the
	// database.
	m_database.add(prop);

	return;
    }


    private static ContribManager  m_singletonInstance = null;

    private static GameConfig  m_gameConfig;
    
    private static int  m_debugMode;

    private static List  m_database;
}
