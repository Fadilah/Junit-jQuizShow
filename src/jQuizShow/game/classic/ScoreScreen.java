/*
 * ScoreScreen.java
 *
 * Created on May 8, 2001, 8:50 PM
 *
 * $Id: ScoreScreen.java,v 1.2 2007/02/06 05:20:21 sdchen Exp $
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
 *    $Log: ScoreScreen.java,v $
 *    Revision 1.2  2007/02/06 05:20:21  sdchen
 *    Added key listener to overcome keyboard focus problem.
 *
 *    Revision 1.1  2004/04/02 06:01:59  sdchen
 *    Snapshot of jQuizShow 1.1 alpha06 development
 *
 *    Revision 1.2  2002/06/06 05:23:25  sdchen
 *    Removed lifeline icons from this screen.  Redundant with the GameScreen
 *    and removing it allows the ScoreScreen size to be more flexible (icons
 *    did not resize).
 *
 *    Revision 1.1.1.1  2002/04/13 17:01:30  sdchen
 *    Initial import of the jQuizShow sources from local development directory.
 *
 *
 */

package jQuizShow.game.classic;

/** This class is responsible for managing the Score Screen, the
 * window which displays the player's current accomplishment level.
 *
 * @author  Steven D. Chen
 */

import java.net.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

import jQuizShow.*;
import jQuizShow.game.*;
import jQuizShow.event.*;

public class ScoreScreen extends JInternalFrame
        implements GameStateChangeListener,
            GameUpdateListener
{
    /** Creates a new ScoreScreen */
    public ScoreScreen(Point  initLocation, Dimension  initSize, GameState  gameState)
    {
        super("Score Screen", true, true, false, true);

        setSize(initSize);
        setLocation(initLocation);

        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

        getContentPane().setBackground(Color.black);
        
        m_gameState = gameState;
        m_gameState.addGameStateChangeListener(this);
        m_gameState.addGameUpdateListener(this);
        
        // Get the level titles and initialize the ScorePanel with them.
        GameConfig  gameConfig = GameConfig.getInstance();
        
        int  numLevels = gameConfig.getNumLevels();
        
        String[]  titles = new String[numLevels];
        
        for (int  i = 1; i <= numLevels; i++)
            titles[i - 1] = gameConfig.getConfig("levelName" + i, "Level " + i);

        m_scorePanel = new ScorePanel(titles);

        // Get the level numbers to highlight, if any
        for (int  i = 1; i <= numLevels; i++)
        {
            int     level;
            
            if ((level = gameConfig.getIntConfig("transitionLevel" + i, -1)) > 0)
                m_scorePanel.setHighlight(level, true);
            else
                break;
        }

        getContentPane().add(m_scorePanel);

        // Listen for keyboard events
        addKeyListener(new java.awt.event.KeyAdapter()
        {
            public void keyPressed(java.awt.event.KeyEvent evt)
            {
                int  keyCode = evt.getKeyCode();
                char  keyChar = evt.getKeyChar();
                
                GameState  gState = GameState.getInstance(false);
                
                GameInputEvent  event = new GameInputEvent(this);

                event.setType(GameInputEnum.KEY_TYPED);
                event.setKeyCode(keyCode, keyChar);
                gState.fireGameInputEvent(event);
            }
        } );
        
        setFocusable(true);        
    }
    
    
    /** Listener for GameStateChange events
     */
    public void  gameStateChanged(GameStateChangeEvent  evt)
    {
        GameStateEnum  type = (GameStateEnum) evt.getType();
        
        if (type == GameStateEnum.NEW_GAME ||
                type == GameStateEnum.WAIT_TO_START_ROUND ||
                type == GameStateEnum.ANSWER_WAS_WRONG ||
                type == GameStateEnum.SET_CURRENT_LEVEL)
        {
            // Highlight the current highlight level
            m_scorePanel.setHighlightBorder(m_gameState.getHighlightLevel());

            m_scorePanel.setCurrentLevel(m_gameState.getCurrentLevel());
        }

        return;
    }


    /** Listener for GameUpdateEvents
     */
    public void gameUpdated(GameUpdateEvent evt) {
        GameUpdateEnum  type = evt.getType();
        
        if (type == GameUpdateEnumGame.HIGHLIGHT_LEVEL)
        {
            m_scorePanel.setHighlightBorder(m_gameState.getHighlightLevel());
        }    
        else if (type == GameUpdateEnumGame.TOGGLE_STATE)
        {
            boolean  state;
            
            state = m_gameState.getToggleState(GameState.SCORES_SHOWN);
            
            if (state != m_scoreShown)
            {
                setVisible(state);
                
                m_scoreShown = state;
            }
        }
    }    

    // Variables declaration
    private ScorePanel  m_scorePanel;
    private GameState  m_gameState;
    
    private boolean  m_scoreShown = false;
}
