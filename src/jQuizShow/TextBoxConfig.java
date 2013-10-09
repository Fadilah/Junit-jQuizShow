/*
 * TextBoxBounds.java
 *
 * Created on February 14, 2004, 10:34 PM
 *
 * $Id: TextBoxConfig.java,v 1.1 2004/04/02 06:01:59 sdchen Exp $
 *
 *============================================================================
 *
 * Copyright (C) 2004  Steven D. Chen
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
 *    $Log: TextBoxConfig.java,v $
 *    Revision 1.1  2004/04/02 06:01:59  sdchen
 *    Snapshot of jQuizShow 1.1 alpha06 development
 *
 *
 */

package jQuizShow;

/**
 *
 * @author  Steven Chen
 */

import java.awt.*;

public class TextBoxConfig extends java.awt.Rectangle {
    
    public String  fontName = "SansSerif";
    public int  fontStyle = Font.BOLD;
    
    public Color  fontColor;      // RGB 24-bit text font color
    public Color  bgColor;        // RGB 24-bit background color (-1 = transparent)
    
    public int  fadeInDelay = 0;    // Delay in milliseconds for text fade-in effect
    public int  rows = 0;           // Rows hint
    public int  columns = 0;        // Columns hint
    
    /** Creates a new instance of TextBoxBounds */
    public TextBoxConfig() {
        setRect(10, 10, 200, 40);
    }
    
    public TextBoxConfig(int x, int y, int w, int h) {
        setRect(x, y, w, h);
    }
}
