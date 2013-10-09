/*
 * GameInputEvent.java
 *
 * Created on April 23, 2001, 8:58 PM
 *
 * $Id: GameInputEvent.java,v 1.1 2004/04/02 06:01:59 sdchen Exp $
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
 *    $Log: GameInputEvent.java,v $
 *    Revision 1.1  2004/04/02 06:01:59  sdchen
 *    Snapshot of jQuizShow 1.1 alpha06 development
 *
 *    Revision 1.1.1.1  2002/04/13 17:01:44  sdchen
 *    Initial import of the jQuizShow sources from local development directory.
 *
 *
 */

package jQuizShow.event;

/**
 *
 * @author  Steven D. Chen
 * @version 
 */

public class GameInputEvent
{
    /** Creates new GameInputEvent */
    public GameInputEvent(Object object)
    {
        this.object = object;
    }

    public Object  getSource()
    {
        return this.object;
    }
   
    public GameInputEnum  getType()
    {
        return this.type;
    }

    public int  getKeyCode()
    {
        return this.keyCode;
    }

    public char  getKeyChar()
    {
        return this.keyChar;
    }

    public int  getSelectedIndex()
    {
        return this.selectedIndex;
    }
    
    public void  setType(GameInputEnum  type)
    {
        this.type = type;
    }

    public void  setKeyCode(int  keyCode, char  keyChar)
    {
        this.keyCode = keyCode;
        this.keyChar = keyChar;
    }
    
    public void  setSelectedIndex(int  index)
    {
        this.selectedIndex = index;
    }
    
    private Object  object;          // Object initiating the event
    private GameInputEnum  type;    // GameInput type
    private int  keyCode;           // Key code
    private char  keyChar;           // Associated character
    private int  selectedIndex;      // Index of selected answer
}
