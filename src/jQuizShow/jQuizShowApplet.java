/*
 * jQuizShowApplet.java
 *
 * Created on October 19, 2002, 4:02 PM
 *
 * $Id: jQuizShowApplet.java,v 1.2 2007/02/05 03:55:48 sdchen Exp $
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
 *    $Log: jQuizShowApplet.java,v $
 *    Revision 1.2  2007/02/05 03:55:48  sdchen
 *    Removed CR (Ctrl-M) from lines.
 *
 *    Revision 1.1  2004/04/02 06:01:59  sdchen
 *    Snapshot of jQuizShow 1.1 alpha06 development
 *
 *
 */

package jQuizShow;

import javax.swing.*;
import java.util.*;
import java.util.jar.*;
import java.util.zip.*;
import java.net.*;

/** This is the main class for the JApplet interface for jQuizShow.
 *
 * @author  Steven D. Chen
 * @version 1.1
 */
public class jQuizShowApplet extends JApplet
{
    /** Initialize the jQuizShow applet.
     */
    public void  init()
    {
    }


    /** Start running the jQuizShow applet.
     */
    public void  start()
    {
    }


    /** Stop running the jQuizShow applet.
     */
    public void  stop()
    {
    }


    /** Return the information about this applet.
     */
    public String  getAppletInfo()
    {
	return "Name:  jQuizShow\n" +
	    "Copyright:  2001-2004 by Steven D. Chen\n" +
	    "License:  GNU General Public License\n" +
	    "Website:  http://quizshow.sourceforge.net";
    }


    /** Return the APPLET parameter information.
     */
    public String[][]  getParameterInfo()
    {
	String[][]  info = {
	    {"dbfile", "string", "question database filename"}
	};

	return info;
    }
}

