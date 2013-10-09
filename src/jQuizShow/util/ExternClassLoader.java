/*
 * ExternClassLoader.java
 *
 * Created on June 17, 2002, 7:59 PM
 *
 * $Id: ExternClassLoader.java,v 1.1 2004/04/02 06:02:00 sdchen Exp $
 *
 *============================================================================
 *
 * Copyright (C) 2002  Steven D. Chen
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
 *    $Log: ExternClassLoader.java,v $
 *    Revision 1.1  2004/04/02 06:02:00  sdchen
 *    Snapshot of jQuizShow 1.1 alpha06 development
 *
 *    Revision 1.1  2002/06/22 18:58:31  sdchen
 *    Extends ClassLoader for loading an external class from a file.
 *
 *
 */

package jQuizShow.util;

import java.io.*;
import java.util.*;
import java.util.jar.*;

/**
 *
 * @author  Steven D. Chen
 *
 */

public class ExternClassLoader extends ClassLoader
{
    /** Constructor for loading classes from a JarFile.
     */
    public ExternClassLoader(String basePath, JarFile  jarFile)
    {
	if (basePath == null)
	    m_classBasePath = "";
	else
	    m_classBasePath = basePath;

	m_jarFile = jarFile;
    }

    /** Constructor for loading classes from a file or URL.
     */
    public ExternClassLoader(String basePath)
    {
	if (basePath == null)
	    m_classBasePath = "";
	else
	    m_classBasePath = basePath;
    }

    /** Overrides the ClassLoader's findClass().
     */
    public Class  findClass(String name)
	    throws ClassNotFoundException
    {
    	byte[]  bytes = loadClassData(name);

	return defineClass(name, bytes, 0, bytes.length);
    }

    /** Loads the bytes from the class file.
     */
    private byte[]  loadClassData(String name)
	    throws ClassNotFoundException
    {
        String  filename;
        String  filePath;

	filename = m_classBasePath + name.replace('.', File.separatorChar) +
		".class";

        try
	{
	    filePath = FileUtils.searchForFile(filename, m_jarFile);
	}
	catch (FileNotFoundException  fnf_e)
	{
	    throw new ClassNotFoundException("class '" + name + "' not found.");
	}

        InputStream  fin = null;

        try
	{
	    fin = FileUtils.openFile(filePath, m_jarFile);

	    // NOTE:  InputStream.available() is only valid for "small"
	    // files.  But, what is "small"?
	    int  nbytes = fin.available();
	    int  bytesRead = 0;

	    byte  buffer[] = new byte[nbytes];

	    while (bytesRead < nbytes)
	    {
		bytesRead += fin.read(buffer, bytesRead, nbytes - bytesRead);
	    }

	    return buffer;
	}
	catch (IOException  io_e)
	{
	    try
	    {
		if (fin != null)
		    fin.close();
	    }
	    catch (Exception  e)
	    {
	    }

	    throw new ClassNotFoundException("reading class file " +
		    name + " (" + io_e + ")");
	}
    }

    private String  m_classBasePath;
    private JarFile  m_jarFile;

}
