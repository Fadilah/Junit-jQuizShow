/*
 * GameLabel.java
 *
 * Created on July 4, 2002, 2:15 PM
 *
 * $Id: GameLabel.java,v 1.1 2004/04/02 06:01:59 sdchen Exp $
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
 *    $Log: GameLabel.java,v $
 *    Revision 1.1  2004/04/02 06:01:59  sdchen
 *    Snapshot of jQuizShow 1.1 alpha06 development
 *
 *    Revision 1.1  2002/07/05 06:57:49  sdchen
 *    This class extends JLabel adding the capability to listen for mouse
 *    events.  Allows the label to be selectable by the mouse (e.g. for the
 *    helplines).
 *
 *
 */

package jQuizShow.game;

/** Extends JLabel allowing it to receive mouse pressed events.
 *
 * @author  Steven D. Chen
 * @version 1.0
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.text.*;
import javax.swing.event.*;
import java.util.*;

import jQuizShow.event.*;

public class GameLabel extends JLabel
{
    public GameLabel()
    {
        super();

	initializeMouseListener();
    }

    private void  initializeMouseListener()
    {
        // Initialize the GameActionEvent and the listener list
        m_evt = new GameInputEvent(this);
        m_eventListenerList = new EventListenerList();

        // Add a listener for mouse events
        addMouseListener(new MouseAdapter()
        {
            public void mousePressed(MouseEvent  evt)
            {
                m_evt.setType(GameInputEnum.MOUSE_BUTTON_1);
                fireGameInputEvent(m_evt);
            }
        } );
    }
    
    public void addGameInputListener(GameInputListener  l)
    {
        m_eventListenerList.add(GameInputListener.class, l);
    }
    
    public void removeGameInputListener(GameInputListener  l)
    {
        m_eventListenerList.remove(GameInputListener.class, l);
    }
    
    protected void  fireGameInputEvent(GameInputEvent  evt)
    {
        // Guaranteed to return a non-null array
        Object[] listeners = m_eventListenerList.getListenerList();

        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
            if (listeners[i] == GameInputListener.class)
            {
                ((GameInputListener)listeners[i+1]).gameInputReceived(evt);
            }
        }        
    }
    
    private GameInputEvent  m_evt = null;
    protected EventListenerList  m_eventListenerList;   // Listeners interested in a selection

}
