/*
 * GamePanel.java
 *
 * Created on May 4, 2001, 10:16 PM
 *
 * $Id: GamePanel.java,v 1.1 2004/04/02 06:01:59 sdchen Exp $
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
 *    $Log: GamePanel.java,v $
 *    Revision 1.1  2004/04/02 06:01:59  sdchen
 *    Snapshot of jQuizShow 1.1 alpha06 development
 *
 *    Revision 1.3  2002/07/06 06:13:38  sdchen
 *    Completed implementation of a workaround for Java2 1.4 KeyEvent processing
 *    change.  With Java2 1.4, the GameScreen must have focus in order for
 *    jQuizShow to receive keyboard events.  This is a change from v1.3 behavior
 *    which passed uncaught KeyEvents up to the parent classes (Main's JFrame).
 *
 *    Revision 1.2  2002/07/05 07:00:12  sdchen
 *    Changed helpline JLabels to GameLabels.  Added this as a GameInputListener
 *    for these GameLabels, allowing them to be mouse selectable.
 *
 *    Revision 1.1.1.1  2002/04/13 17:01:21  sdchen
 *    Initial import of the jQuizShow sources from local development directory.
 *
 *
 */

package jQuizShow.game.classic;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import jQuizShow.*;
import jQuizShow.game.*;
import jQuizShow.event.*;

/**
 *
 * @author  Steven D. Chen
 */
public class GamePanel extends javax.swing.JPanel
        implements GameInputListener,
            GameStateChangeListener,
            GameUpdateListener
{
    /** Creates new form GamePanel */
    public GamePanel(int  maxAnswers)
    {
        initComponents ();

        setBackground(Color.black);
        
        m_maxAnswers = maxAnswers;

        m_gameState = GameState.getInstance(false);
        m_gameState.addGameStateChangeListener(this);
        m_gameState.addGameUpdateListener(this);
        
        // Initialize the GameInputEvent and the listener list
        m_evt = new GameInputEvent(this);
        m_eventListenerList = new EventListenerList();
        
        // Initialize the Question/Answers GameText components
        GridBagConstraints  gridBagConstraints = new GridBagConstraints();

        // Question timer
        m_questionTimer = new GameTimer();
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 100.0;
        gridBagConstraints.weighty = 100.0;
        add(m_questionTimer, gridBagConstraints);

        // Question box
        m_questionBox = new GameText(0, new Dimension(32, 2));
        
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 200.0;
        gridBagConstraints.weighty = 200.0;
        answerPanel.add(m_questionBox, gridBagConstraints);

        // Answer boxes
        m_answerBoxes = new Vector();
     
        for (int  i = 0; i < m_maxAnswers; i++)
        {
            Dimension  rowcol = new Dimension(30, 1);
            
            GameText  answerBox = new GameText(i + 1, rowcol);
                        
            gridBagConstraints.gridx = i % 2;
            gridBagConstraints.gridy = i / 2 + 2;
            gridBagConstraints.gridwidth = 1;
            gridBagConstraints.gridheight = 1;
            gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints.weightx = 100.0;
            gridBagConstraints.weighty = 100.0;
            answerPanel.add(answerBox, gridBagConstraints);

            // Add the answer box to the vector
            m_answerBoxes.add(answerBox);

            // Listen for answer selection or other hot keys
            answerBox.addGameInputListener(this);
        }

        GameConfig  gameConfig = GameConfig.getInstance();
        
        String  iconBaseDir = gameConfig.getConfig("iconBaseDirectory");

        URL  iconURL;
        
        if ((iconURL = ClassLoader.getSystemResource(iconBaseDir + "JQSPhone.gif")) != null)
            phoneLabel.setIcon(new ImageIcon(iconURL));
        
        if ((iconURL = ClassLoader.getSystemResource(iconBaseDir + "JQSNoPhone.gif")) != null)
            phoneLabel.setDisabledIcon(new ImageIcon(iconURL));
        
        if ((iconURL = ClassLoader.getSystemResource(iconBaseDir + "JQSFifty.gif")) != null)
            fiftyLabel.setIcon(new ImageIcon(iconURL));
        
        if ((iconURL = ClassLoader.getSystemResource(iconBaseDir + "JQSNoFifty.gif")) != null)
            fiftyLabel.setDisabledIcon(new ImageIcon(iconURL));
        
        if ((iconURL = ClassLoader.getSystemResource(iconBaseDir + "JQSPoll.gif")) != null)
            pollLabel.setIcon(new ImageIcon(iconURL));
        
        if ((iconURL = ClassLoader.getSystemResource(iconBaseDir + "JQSNoPoll.gif")) != null)
            pollLabel.setDisabledIcon(new ImageIcon(iconURL));

	// Add this as a listener for GameLabel input events
	phoneLabel.addGameInputListener(this);
	fiftyLabel.addGameInputListener(this);
	pollLabel.addGameInputListener(this);
    }

    
    /** Process any GameStateChanged events */
    public void gameStateChanged(GameStateChangeEvent evt) {
        GameStateEnum  type = (GameStateEnum) evt.getType();

        if (type == GameStateEnum.NEW_GAME ||
                type == GameStateEnum.WAIT_TO_START_ROUND ||
                type == GameStateEnum.TRANSITION_LEVEL ||
                type == GameStateEnum.SET_CURRENT_LEVEL)
        {
            // Synch the lifeline states with the GameState
            updateIconStates();
            
            // Unhighlight any highlighted lifeline icon
            highlightIcon(GameState.NO_SELECTED_LIFELINE);
            
            // Set the level name
            setLevelTitle(m_gameState.getLevelTitle());

            // Clear the question & answers
            m_questionBox.setText("", "");

            for (int index = 0; index < m_answerBoxes.size(); index++)
            {
                GameText    answerBox;
                
                answerBox = (GameText) m_answerBoxes.get(index);
                answerBox.setText("", "");
            }

            // Unhighlight any answers
            highlightSelection(null);
            
            // Set the status text label
            setStatusText(m_gameState.getStatusMessage());
        }
        else if (type == GameStateEnum.LIFELINE_SELECTED)
        {
            highlightIcon(m_gameState.getSelectedLifeline());
        }
        else if (type == GameStateEnum.LIFELINE_END)
        {
            highlightIcon(GameState.NO_SELECTED_LIFELINE);
            updateIconStates();
        }
        else if (type == GameStateEnum.DISPLAY_QUESTION)
        {
            m_questionBox.setText("", m_gameState.getQuestion());

            m_questionTimer.setTimer(m_gameState.getQuestionTimerLimit(),
                    0, true);
        }
        else if (type == GameStateEnum.DISPLAY_ANSWER ||
                type == GameStateEnum.FIFTY_FIFTY)
        {
            GameText  answerBox;
            
            for (int  index = 0; index < m_answerBoxes.size(); index++)
            {
                answerBox = (GameText) m_answerBoxes.get(index);

                if (m_gameState.getAnswerVisible(index))
                {
                    String  answer = m_gameState.getAnswer(index);
                    
                    if (answer == null || answer.length() == 0)
                        answerBox.setText("", "");
                    else
                    {
                        Character  letter = new Character((char) ('A' + index));
                        answerBox.setText(letter + ": ", answer);
                    }
                }
                else
                {
                    answerBox.setText("", "");
                }
            }
        }
        else if (type == GameStateEnum.ANSWER_WAS_CORRECT ||
                type == GameStateEnum.ANSWER_WAS_WRONG ||
                type == GameStateEnum.WALKAWAY)
        {
            GameText  answerBox;

            int  index = m_gameState.getCorrectAnswer();

            if (index < 0 || index >= m_answerBoxes.size())
                return;

            answerBox = (GameText) m_answerBoxes.get(index);

            answerBox.setTextColor(Color.red, Color.black,
                    Color.green);
        }
        else if (type == GameStateEnum.SET_QUESTION_TIMER_LIMIT)
        {
            int     limit = m_gameState.getQuestionTimerLimit();
            
            m_questionTimer.setTimer(limit, 0, true);
            
            if (limit == 0)
                m_questionTimer.setVisible(false);
            else
                m_questionTimer.setVisible(true);
        }
    }    


    /** Process any GameUpdate events */
    public void gameUpdated(GameUpdateEvent evt) {
        GameUpdateEnum  type = evt.getType();
        
        if (type == GameUpdateEnumGame.UPDATE_QUESTION_CLOCK)
        {
            m_questionTimer.setTime(m_gameState.getQuestionTimerTime());
        }
        else if (type == GameUpdateEnumGame.SELECTED_ANSWER)
        {
            GameText  answerBox;
            
            int  index = m_gameState.getSelectedAnswer();
            
            if (index == GameState.NO_SELECTED_ANSWER ||
                    index < 0 || index >= m_answerBoxes.size())
            {
                answerBox = null;
            }
            else
            {
                answerBox = (GameText) m_answerBoxes.get(index);
            }
            
            highlightSelection(answerBox);
        }
        else if (type == GameUpdateEnumGame.SELECTED_LIFELINE)
        {
            highlightIcon(m_gameState.getSelectedLifeline());
        }
        else if (type == GameUpdateEnumGame.TOGGLE_STATE)
        {
            updateIconStates();
        }
        else if (type == GameUpdateEnumGame.STATUS_MSG)
        {
            setStatusText(m_gameState.getStatusMessage());
        }
    }    
    
    
    /** Set the current level name and draw it */
    private void  setLevelTitle(String  name)
    {
        levelLabel.setText(name);
        return;
    }

    
    /** Set the status message */
    private void  setStatusText(String  text)
    {
        statusText.setText(text != null ? text : "");
        return;
    }
    
    
    /** Highlights an icon */
    private void  highlightIcon(int  index)
    {
        Border  highlightBorder = BorderFactory.createLineBorder(Color.yellow, 2);
        Border  normalBorder = BorderFactory.createLineBorder(Color.black, 2);
        
        switch (index)
        {
            case GameState.FIFTY_FIFTY :
            {
                fiftyLabel.setBorder(highlightBorder);
                phoneLabel.setBorder(normalBorder);
                pollLabel.setBorder(normalBorder);
            }
            break;
            
            case GameState.PHONE_A_FRIEND :
            {
                fiftyLabel.setBorder(normalBorder);
                phoneLabel.setBorder(highlightBorder);
                pollLabel.setBorder(normalBorder);
            }
            break;
            
            case GameState.ASK_THE_AUDIENCE :
            {
                fiftyLabel.setBorder(normalBorder);
                phoneLabel.setBorder(normalBorder);
                pollLabel.setBorder(highlightBorder);
            }
            break;
            
            case GameState.NO_SELECTED_LIFELINE :
            default :
            {
                fiftyLabel.setBorder(normalBorder);
                phoneLabel.setBorder(normalBorder);
                pollLabel.setBorder(normalBorder);
            }
            break;
        }
    }
    
    
    /** Update the lifeline icons based on the current GameState */
    private void  updateIconStates()
    {
        Icon  icon;

        boolean state;
        
        state = m_gameState.getToggleState(GameState.FIFTY_FIFTY);
        fiftyLabel.setEnabled(state);

        state = m_gameState.getToggleState(GameState.PHONE_A_FRIEND);
        phoneLabel.setEnabled(state);

        state = m_gameState.getToggleState(GameState.ASK_THE_AUDIENCE);
        pollLabel.setEnabled(state);
        
        return;
    }

    
    /** Select an answer.  Optionally, if index < 0, deslect all */
    public void  selectAnswer(int  index)
    {
        if (index >= m_answerBoxes.size())
            return;

        GameText  answerBox = null;
        
        if (index >= 0)
            answerBox = (GameText) m_answerBoxes.get(index);

        highlightSelection(answerBox);
        
        return;
    }

    
    /** Process mouse selections from the GameText boxes.  If it is
     * from a valid answerBox, translate it to the corresponding letter
     * key selection and fire off an event to the listeners. */
    public void gameInputReceived(GameInputEvent evt)
    {
        Object  source = evt.getSource();

	if (evt.getType() == GameInputEnum.MOUSE_BUTTON_1)
	{
	    for (int  index = 0; index < m_maxAnswers; index++)
	    {
		GameText  answerBox = (GameText) m_answerBoxes.get(index);
	       
		if (answerBox == source)
		{
		    m_evt.setType(GameInputEnum.ANSWER_SELECTED);
		    m_evt.setSelectedIndex(index);
		    m_gameState.fireGameInputEvent(m_evt);
		}
	    }

	    if (source == fiftyLabel)
	    {
		m_evt.setType(GameInputEnum.LIFELINE_SELECTED);
		m_evt.setSelectedIndex(GameState.FIFTY_FIFTY);
		m_gameState.fireGameInputEvent(m_evt);
	    }
	    else if (source == phoneLabel)
	    {
		m_evt.setType(GameInputEnum.LIFELINE_SELECTED);
		m_evt.setSelectedIndex(GameState.PHONE_A_FRIEND);
		m_gameState.fireGameInputEvent(m_evt);
	    }
	    else if (source == pollLabel)
	    {
		m_evt.setType(GameInputEnum.LIFELINE_SELECTED);
		m_evt.setSelectedIndex(GameState.ASK_THE_AUDIENCE);
		m_gameState.fireGameInputEvent(m_evt);
	    }
	}
    }


    /** Highlight the specified "box".  If box == null, unhighlight all.
     */
    private void highlightSelection(GameText  box)
    {
        for (int  i = 0; i < m_maxAnswers; i++)
        {
            GameText  answerBox = (GameText) m_answerBoxes.get(i);
            
            if (answerBox == box)
            {
                // Highlight the box
                answerBox.setTextColor(Color.red, Color.black,
                        Color.orange);
            }
            else
            {
                // Reset to normal colors (unhighlight)
                answerBox.setTextColor();
            }
        }
    }


    /** This method is called from within the constructor to
     * initialize the form.
     */
    private void initComponents() {
        levelLabel = new JLabel();
        fiftyLabel = new GameLabel();
        phoneLabel = new GameLabel();
        pollLabel = new GameLabel();
        answerPanel = new JPanel();
        statusText = new JTextArea();
        
        setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gridBagConstraints1;
        
        setForeground(java.awt.Color.white);
        setBackground(java.awt.Color.black);
        levelLabel.setText("Level");
        levelLabel.setForeground(java.awt.Color.orange);
        levelLabel.setBackground(java.awt.Color.black);
        levelLabel.setFont(new java.awt.Font("Arial", 0, 18));
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 1;
        gridBagConstraints1.gridy = 1;
        gridBagConstraints1.gridwidth = 4;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.weightx = 200.0;
        gridBagConstraints1.weighty = 100.0;
        add(levelLabel, gridBagConstraints1);
        
        fiftyLabel.setForeground(java.awt.Color.white);
        fiftyLabel.setBackground(java.awt.Color.black);
        fiftyLabel.setBorder(new javax.swing.border.LineBorder(java.awt.Color.black, 2));
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 6;
        gridBagConstraints1.gridy = 1;
        gridBagConstraints1.weightx = 50.0;
        gridBagConstraints1.weighty = 100.0;
        add(fiftyLabel, gridBagConstraints1);
        
        phoneLabel.setForeground(java.awt.Color.white);
        phoneLabel.setBackground(java.awt.Color.black);
        phoneLabel.setBorder(new javax.swing.border.LineBorder(java.awt.Color.black, 2));
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 7;
        gridBagConstraints1.gridy = 1;
        gridBagConstraints1.weightx = 50.0;
        gridBagConstraints1.weighty = 100.0;
        add(phoneLabel, gridBagConstraints1);
        
        pollLabel.setForeground(java.awt.Color.white);
        pollLabel.setBackground(java.awt.Color.black);
        pollLabel.setBorder(new javax.swing.border.LineBorder(java.awt.Color.black, 2));
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 8;
        gridBagConstraints1.gridy = 1;
        gridBagConstraints1.weightx = 50.0;
        gridBagConstraints1.weighty = 100.0;
        add(pollLabel, gridBagConstraints1);
        
        answerPanel.setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gridBagConstraints2;
        
        answerPanel.setForeground(java.awt.Color.white);
        answerPanel.setBackground(java.awt.Color.black);
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 1;
        gridBagConstraints1.gridy = 4;
        gridBagConstraints1.gridwidth = 8;
        gridBagConstraints1.gridheight = 5;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints1.weightx = 200.0;
        gridBagConstraints1.weighty = 10000.0;
        add(answerPanel, gridBagConstraints1);
        
        statusText.setWrapStyleWord(true);
        statusText.setLineWrap(true);
        statusText.setForeground(java.awt.Color.white);
        statusText.setText("Status Text");
        statusText.setBackground(java.awt.Color.black);
        statusText.setDisabledTextColor(java.awt.Color.white);
        statusText.setEnabled(false);
        statusText.setRequestFocusEnabled(false);
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 1;
        gridBagConstraints1.gridy = 10;
        gridBagConstraints1.gridwidth = 8;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.weightx = 100.0;
        gridBagConstraints1.weighty = 100.0;
        add(statusText, gridBagConstraints1);
        
    }


    // Variables declaration - do not modify
    private JLabel levelLabel;
    private GameLabel fiftyLabel;
    private GameLabel phoneLabel;
    private GameLabel pollLabel;
    private JPanel answerPanel;
    private JTextArea statusText;
    // End of variables declaration
    
    private GameState  m_gameState;     // Game state instance

    private GameTimer  m_questionTimer;     // Question timer
    private GameText  m_questionBox;      // Question box
    private Vector  m_answerBoxes;        // Answer boxes
    
    private int  m_maxAnswers;          // Max number of possible answers
        
    private GameInputEvent  m_evt = null;
    protected EventListenerList  m_eventListenerList;   // Listeners interested in a selection
}
