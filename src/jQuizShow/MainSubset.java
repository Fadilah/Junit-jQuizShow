/*
 * MainSubset.java
 *
 * Created on January 18, 2001, 7:14 PM
 *
 * $Id: MainSubset.java,v 1.2 2007/02/05 03:55:48 sdchen Exp $
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
 *    $Log: MainSubset.java,v $
 *    Revision 1.2  2007/02/05 03:55:48  sdchen
 *    Removed CR (Ctrl-M) from lines.
 *
 *    Revision 1.1  2004/04/02 06:01:59  sdchen
 *    Snapshot of jQuizShow 1.1 alpha06 development
 *
 *
 */

package jQuizShow;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import jQuizShow.event.*;
import jQuizShow.net.*;
import jQuizShow.util.*;

/**
 * This is the interface that defines the top-level "program entry" class for
 * the major jQuizShow program subsets.  All subsets MUST be derived from this
 * interface in order to be run from jQuizShow.
 *
 * The class should be implemented using the Singleton design pattern.
 *
 * @author  Steven D. Chen
 */

public abstract class MainSubset
{   
    /**
     * Gets the singleton instance for this subset.
     */
    public static MainSubset getInstance()
    {
        if (m_instance == null)
            System.out.println("ERROR -- MainSubset.setInstance() was not initialized!");
        
        return m_instance;
    }
    
    /**
     * Sets the singleton instance. The derived class must call this method
     * to set a reference to its Singleton instance so that the other
     * classes can reference it.
     */
    protected static void setInstance(MainSubset  instance)
    {
        m_instance = instance;
        
        return;
    }

    
    /**
     * Initializes this Singleton class.  It must be called once prior to
     * the initial use in order to properly set the getInstance() method
     * of the parent class.
     *
     * ---> NOTE:  This must be defined in the child class.
     *
     * public static void initializeClass();
    */
    
    
    /**
     * Initialize the subset.  At a minimum, the implementation should
     * create the main menu bar and add it to the main window via
     * Main.setMenuBar().
     */
    public abstract void initialize();

    
    /**
     * Loads a new question database for the subset.
     */
    public abstract void  loadQuestionDatabase();

    
    /**
     * Starts a new "game".
     */
    public abstract void  startNewGame();

    
    /**
     * Uninstalls this subset from the system.  This is called prior to
     * switching game modes to "close" the subset in preparation for
     * loading a new subset.
     */
    public abstract void  uninstall();

    
    /** Sets the game mode (standalone, master or slave)
     */
    public void  setGameMode(GameModeEnum  newGameMode)
    {
        if (m_restricted)
            throw new UnsupportedOperationException("Restricted mode");

        // Fire event only if mode is changed
        if (m_gameMode != newGameMode)
        {
            m_gameMode = newGameMode;
            
            this.fireGameModeChangeEvent(this, m_gameMode);
        }
        
        return;
    }

    
    // Methods for managing and firing the GameInputEvent
    public void addGameModeChangeListener(GameModeChangeListener  l)
    {
        m_GameModeChangeListenerList.add(GameModeChangeListener.class, l);
    }
    
    public void removeGameModeChangeListener(GameModeChangeListener  l)
    {
        m_GameModeChangeListenerList.remove(GameModeChangeListener.class, l);
    }

    public void  fireGameModeChangeEvent(Object  source, GameModeEnum  type)
    {
        // Guaranteed to return a non-null array
        Object[] listeners = m_GameModeChangeListenerList.getListenerList();

        GameModeChangeEvent  event = new GameModeChangeEvent();
        event.setSource(source);
        event.setType(type);
        
        // Store a local copy of this event for sending to remote nodes
        
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
            if (listeners[i] == GameModeChangeListener.class &&
                    listeners[i] != source)
            {
                ((GameModeChangeListener)listeners[i+1]).gameModeChanged(event);
            }
        }        
    }

    
    /*
     * Private variables definitions
     */
    
    /**
     * Stores a reference to the singleton instance.
     */
    private static MainSubset    m_instance; 
    
    /**
     * Game mode (standalone, master or slave)
     */
    private GameModeEnum    m_gameMode; 

    private boolean  m_restricted = false;

    private EventListenerList  m_GameModeChangeListenerList = new EventListenerList();
}
