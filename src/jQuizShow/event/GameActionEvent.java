/*
 * GameActionEvent.java
 *
 * Created on April 23, 2001, 8:58 PM
 *
 * $Id: GameActionEvent.java,v 1.1 2004/04/02 06:01:59 sdchen Exp $
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
 *    $Log: GameActionEvent.java,v $
 *    Revision 1.1  2004/04/02 06:01:59  sdchen
 *    Snapshot of jQuizShow 1.1 alpha06 development
 *
 *    Revision 1.1.1.1  2002/04/13 17:01:33  sdchen
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

public class GameActionEvent
{
    static final int  EVENT_SELECTED = 1;
    static final int  EVENT_KEY_TYPED = 2;
    
    /** Creates new GameActionEvent */
    public GameActionEvent(Object  object, int  ident)
    {
        this.object = object;
        this.id = ident;
    }

    public Object  getSource()
    {
        return this.object;
    }

    public int  getId()
    {
        return this.id;
    }
    
    public int  getType()
    {
        return this.type;
    }

    public char  getKeyChar()
    {
        return this.keyChar;
    }

    public int  getSelectedIndex()
    {
        return this.selectedIndex;
    }
    
    void  setType(int  type)
    {
        this.type = type;
    }

    void  setKeyChar(char  keyChar)
    {
        this.keyChar = keyChar;
    }

    void  setSelectedIndex(int  index)
    {
        this.selectedIndex = index;
    }
    
    private Object  object;          // Object initiating the event
    private int  id;                 // Id of the event object
    private int  type;               // Event type
    private char  keyChar;           // Associated character
    private int  selectedIndex;      // Index of selected answer
}
