/*
 * GameScreen.java
 *
 * Created on February 23, 2001, 12:41 PM
 *
 * $Id: GameScreen.java,v 1.3 2007/02/05 04:24:19 sdchen Exp $
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
 *    $Log: GameScreen.java,v $
 *    Revision 1.3  2007/02/05 04:24:19  sdchen
 *    Replaced deprecated isFocusTraversable() with setFocusable(boolean s).
 *
 *    Revision 1.2  2007/02/05 03:55:48  sdchen
 *    Removed CR (Ctrl-M) from lines.
 *
 *    Revision 1.1  2004/04/02 06:01:59  sdchen
 *    Snapshot of jQuizShow 1.1 alpha06 development
 *
 *    Revision 1.2  2002/07/06 06:13:38  sdchen
 *    Completed implementation of a workaround for Java2 1.4 KeyEvent processing
 *    change.  With Java2 1.4, the GameScreen must have focus in order for
 *    jQuizShow to receive keyboard events.  This is a change from v1.3 behavior
 *    which passed uncaught KeyEvents up to the parent classes (Main's JFrame).
 *
 *    Revision 1.1.1.1  2002/04/13 17:01:21  sdchen
 *    Initial import of the jQuizShow sources from local development directory.
 *
 *
 */

package jQuizShow.game.classic;

/**
 *
 * @author  Steve
 * @version 
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import jQuizShow.*;
import jQuizShow.game.*;
import jQuizShow.event.*;

public class GameScreen extends javax.swing.JInternalFrame
        implements GameUpdateListener
{
    public static final int GAME_PANEL = 0;
    public static final int TRANSITION_PANEL = 1;
    
    public GameScreen(Point  initLocation, Dimension  initSize, GameState  gameState)
    {
        super("Game Screen", true, false, true, true);
        setSize(initSize);
        setLocation(initLocation);

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        
        m_gameState = gameState;
        m_gameState.addGameUpdateListener(this);

        m_evt = new GameInputEvent(this);
        
        m_maxAnswers = m_gameState.getMaxNumberOfAnswers();
        
        initComponents ();

        m_tabbedPane = new JTabbedPane();
        m_tabbedPane.setTabPlacement(SwingConstants.RIGHT);
        getContentPane().add(m_tabbedPane);
        
        // Add the GamePanel to the tabbed pane
        m_gamePanel = new GamePanel(m_maxAnswers);
        m_tabbedPane.addTab("Q", m_gamePanel);

        // Add the TransitionPanel to the tabbed pane
        m_transitionPanel = new TransitionPanel();
        m_tabbedPane.addTab("T", m_transitionPanel);

        // Listen for keyboard events
        addKeyListener(new java.awt.event.KeyAdapter()
        {
            public void keyPressed(java.awt.event.KeyEvent evt)
            {
                int  keyCode = evt.getKeyCode();
                char  keyChar = evt.getKeyChar();

                m_evt.setType(GameInputEnum.KEY_TYPED);
		m_evt.setKeyCode(keyCode, keyChar);
		m_gameState.fireGameInputEvent(m_evt);

		return;
            }
        });
        
        setFocusable(true);
    }

    
    public void gameUpdated(GameUpdateEvent evt) {
        GameUpdateEnum  type = evt.getType();

        if (type == GameUpdateEnumGame.TOGGLE_STATE)
        {
            if (m_gameState.getToggleState(GameState.TRANSITION_SCREEN_SHOWN))
            {
                m_tabbedPane.setSelectedComponent(m_transitionPanel);
                show();
            }
            else
            {
                m_tabbedPane.setSelectedComponent(m_gamePanel);
                show();
            }
        }
    }    
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     */
    private void initComponents() {
        
        getContentPane().setLayout(new java.awt.GridLayout(1, 0));
        
    }
    
    protected JTabbedPane  m_tabbedPane;

    protected GamePanel  m_gamePanel;                 // Primary question/answer panel

    protected TransitionPanel  m_transitionPanel;
    
    private GameState  m_gameState;     // GameState instance (model)
    
    private int  m_maxAnswers;          // Max number of answers

    private GameInputEvent  m_evt = null;
}
