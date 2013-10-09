/*
 * FileUtils.java
 *
 * Created on May 21, 2001, 8:48 PM
 *
 * $Id: FileUtils.java,v 1.2 2007/02/05 03:55:48 sdchen Exp $
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
 *    $Log: FileUtils.java,v $
 *    Revision 1.2  2007/02/05 03:55:48  sdchen
 *    Removed CR (Ctrl-M) from lines.
 *
 *    Revision 1.1  2004/04/02 06:02:00  sdchen
 *    Snapshot of jQuizShow 1.1 alpha06 development
 *
 *    Revision 1.6  2002/08/15 04:43:26  sdchen
 *    Internationalization of source code.  Main.getMessage() is the primary
 *    routine to get the localized message strings.
 *
 *    Revision 1.5  2002/06/24 04:52:33  sdchen
 *    Fixed to use default JAR file if jarFile arg of openFile() is NULL.
 *
 *    Revision 1.4  2002/05/28 00:16:09  sdchen
 *    *** empty log message ***
 *
 *    Revision 1.3  2002/05/23 05:06:51  sdchen
 *    *** empty log message ***
 *
 *    Revision 1.2  2002/05/07 05:36:48  sdchen
 *    *** empty log message ***
 *
 *    Revision 1.1.1.1  2002/04/13 17:01:13  sdchen
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
import java.util.zip.*;
import java.net.*;

import jQuizShow.*;

public class FileUtils
{
    public static final String  DEFAULT_JAR_FILE = "jQuizShow.jar";
    
    /** Creates new FileUtils
     * NOTE:  This is never called since all methods in this class
     * are static
     */
    private FileUtils()
    {
    }

    /** Search for the filename.  The filename is searched using the
     * the following rules:
     *
     *     1) the filename as is (absolute, or relative to current),
     *     2) a URL,
     *     3) in the defined "searchDirs".
     *     4) in the jQuizShow jar archive,
     *
     * It return the file path if found, else raise FileNotFoundException.
     */
    public static String searchForFile(String filename)
            throws FileNotFoundException
    {
	return (searchForFile(filename, null));
    }


    /** Search for the filename.  The filename is searched using the
     * the following rules:
     *
     *     1) the filename as is (absolute, or relative to current),
     *     2) a URL,
     *     3) in the defined "searchDirs".
     *     4) in the jar archive,
     *
     * It return the file path if found, else raise FileNotFoundException.
     */
    public static String searchForFile(String filename, JarFile  jarFile)
            throws FileNotFoundException
    {
        String[]  searchDirs = {
                    "$user.dir",		// Run directory
                    "$JQUIZSHOW" + File.separatorChar,
                    "$user.dir"  + File.separatorChar + "contrib",
                    "$user.dir"  + File.separatorChar + "jQuizShow",
                    "$user.home"		// User's home dir
        };

        StringBuffer  expandedPaths = new StringBuffer();

	Properties  sysprops = System.getProperties();

	//
	// Get the debugMode flag
	//
	GameConfig  gameConfig = GameConfig.getInstance();
	
	m_debugMode = gameConfig.getIntConfig("debugMode", 0);

	//
	// First pass initialization -- open the default jQuizShow jar file.
	//
        if (m_firstPass == true)
        {
	    //
            // Check if the jar file exists.  If it does open it.
	    //
	    String	str = sysprops.getProperty("java.class.path");

            try
            {
	        if (str.endsWith(".jar") == false)
		    str = DEFAULT_JAR_FILE;

		m_jarFile = new JarFile(str);
            }
            catch (IOException  e)
            {
                m_jarFile = null;
                
                if ((m_debugMode & GameConfig.DEBUG_FILE_IO) != 0)
                    System.out.println("WARNING -- unable to open jar file:  " + str);
            }
            finally
            {
                m_firstPass = false;
            }
        }

	if (jarFile == null)
	    jarFile = m_jarFile;

        if ((m_debugMode & GameConfig.DEBUG_FILE_IO) != 0)
            System.out.println("FileUtils.searchForFile(" + filename + ")");
        
        File    filePath;

	//
	// Check the obvious -- the path passed in
	//
	filePath = new File(filename);
	
	if (filePath.exists())
	    return filename;        // File found.  Return it.
	else
	    expandedPaths.append("    " + filename + "\n");
        
	//
        // Check it as if it was an URL
	//
	try
	{
	    URL url = new URL(filename);
	    return filename;
	}
	catch (MalformedURLException  e)
	{
	    //
            // Not a valid URL.  Try the search dirs.
	    //
            for (int  i = 0; i < searchDirs.length; i++)
            {
                String  path;

                if (searchDirs[i].startsWith("$"))
                {
                    int  sepIndex = searchDirs[i].indexOf(File.separatorChar);
                    
                    if (sepIndex >= 0)
                    {
                        String  expanded = sysprops.getProperty(
                                searchDirs[i].substring(1, sepIndex));
                        
                        if (expanded == null)
                            continue;       // Variable undefined -- skip this

                        path = expanded + searchDirs[i].substring(sepIndex);
                    }
                    else
                    {
                        path = sysprops.getProperty(searchDirs[i].substring(1));
                        
                        if (path == null)
                            continue;       // Variable undefined -- skip this
                    }
                }
                else
                    path = searchDirs[i];

                filePath = new File(path + File.separatorChar + filename);

                if (filePath.exists())
                    return filePath.toString();

                expandedPaths.append("    " + path + "\n");
            }

	    //
	    // Check if it is in the jar file
	    //
	    if (jarFile != null)
	    {

		//
		// Jar files only use "non-DOS" path separators.  Translate
		// DOS backslash separators into slash separators.
		//
		String  stdFilename = filename.replace('\\', '/');
		
		if (jarFile.getJarEntry(stdFilename) != null)
		    return "jar:" + stdFilename;       // Yes.  Return name with "jar:" prepended.
		else
		    expandedPaths.append("    " + jarFile.getName() + "\n");
	    }

            String  message = "ERROR!  Unable to find file '" +
                    filename + "'.  Locations searched:\n\n" +
                    expandedPaths;

            throw new FileNotFoundException(message);
	}
    }


    /** Open the file/jar/URL and return an InputStream to it.  This
      * method searches the jQuizShow JAR file.
     */
    public static InputStream openFile(String filepath)
            throws IOException
    {
	return (openFile(filepath, null));
    }


    /** Open the file/jar/URL and return an InputStream to it.
     */
    public static InputStream openFile(String filepath, JarFile  jarFile)
            throws IOException
    {
	// Get the debugMode flag
	GameConfig  gameConfig = GameConfig.getInstance();
	
	m_debugMode = gameConfig.getIntConfig("debugMode", 0);

	if (jarFile == null)
	    jarFile = m_jarFile;

        if ((m_debugMode & GameConfig.DEBUG_FILE_IO) != 0)
	{
	    String	jarName;

	    if (jarFile == null)
		jarName = "null";
	    else
		jarName = jarFile.getName();

            System.out.println("FileUtils.openFile(" + filepath + ", " +
		    jarName + ")");
	}

        try
	{
	    URL url = new URL(filepath);

            URLConnection  urlConn = url.openConnection();

            return urlConn.getInputStream();
	}
	catch (MalformedURLException  e)
        {
            // Not a URL.  Check files
            try
            {
		// Check the JAR file, if specified.
                if (filepath.startsWith("jar:"))
                {

		    if (jarFile == null)
		    {
			throw new IOException("File in JAR specified but no JAR file provided:  " + filepath);
		    }

                    // Search the jar file for the filepath
                    ZipEntry  zipEntry = jarFile.getEntry(filepath.substring(4));
                    
                    if (zipEntry != null)
                        return jarFile.getInputStream(zipEntry);
                }
            }
            catch (FileNotFoundException ex)
            {
                // Not an error -- drop through and check for a file
            }
            
            try
            {
                InputStream  inStream = new FileInputStream(filepath);

                return inStream;
            }
            catch (FileNotFoundException ex)
            {
                if ((m_debugMode & GameConfig.DEBUG_FILE_IO) != 0)
                    System.out.println("File not found in FileUtils.openFile():  " + filepath);
    
                throw new IOException("File not found:  " + filepath);
            }
        }
    }
    
    private static boolean  m_firstPass = true;
    
    private static JarFile  m_jarFile;
    
    private static int  m_debugMode;
}
