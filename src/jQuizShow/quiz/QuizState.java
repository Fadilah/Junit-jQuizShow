/*
 * QuizState.java
 *
 * Created on January 31, 2004, 5:36 PM
 *
 * $Id: QuizState.java,v 1.1 2004/04/02 06:02:00 sdchen Exp $
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
 *    $Log: QuizState.java,v $
 *    Revision 1.1  2004/04/02 06:02:00  sdchen
 *    Snapshot of jQuizShow 1.1 alpha06 development
 *
 *
 */

package jQuizShow.quiz;

/** Stores the current state of the QuizShow game in session.
 * @author Steven D. Chen
 * @version 1.0
 */

import java.io.*;
import java.util.*;
import javax.swing.event.*;

import jQuizShow.*;
import jQuizShow.event.*;

public class QuizState
    implements
        GameInputListener
{
    /* ---------------------------------------------------- */
    /* Define the public index values for the toggleStates. */
    /* ---------------------------------------------------- */

    /** Game-in-session flag */    
    public static final int GAME_IN_SESSION = 1;

    /** Toggle states to control starting and stopping of the game timer */
    public static final int START_QUESTION_TIMER = 2;
    public static final int STOP_QUESTION_TIMER = 3;

    /** Answer selectable state */
    public static final int ANSWERS_SELECTABLE = 4;

    /** Show answers after time expires */
    public static final int SHOW_ANSWERS = 5;
    
    /** Reveal answer quiz mode */
    public static final int ANSWER_MODE = 6;

    /* ---------------------------------------- */
    /* END definitions of toggleStates indices. */
    /* ---------------------------------------- */

    /** Max time limit for the timers (seconds) */
    public static final int QUESTION_TIMER_MAX_TIME = 300;

    /** Constant for setCorrectAnswer() to indicate no correct answer set */
    public static final int NO_CORRECT_ANSWER = -1;

    /** Constant for setSelectedAnswer() to indicate no selected answer */
    public static final int NO_SELECTED_ANSWER = -1;

    /** Constant for setHighlightLevel() to indicate none */
    public static final int NO_HIGHLIGHT_LEVEL = -1;

    /** Delay types for getDelayValue() */
    public static final int DELAY_AT_START = 0;
    public static final int DELAY_AFTER_QUESTION = 1;
    public static final int DELAY_AFTER_TIMER_EXPIRES = 2;
    public static final int NUM_DELAY_TYPES = 3;
    
    protected static EventListenerList  m_modeChangeListenerList = new EventListenerList();
    protected static EventListenerList  m_stateChangeListenerList = new EventListenerList();
    protected static EventListenerList  m_updateListenerList = new EventListenerList();
    protected static EventListenerList  m_inputListenerList = new EventListenerList();
    
    /** Gets the QuizState singleton instance */
    static QuizState  getInstance() {
        return getInstance(false);
    }

    
    /** Gets a QuizState instance.  If "false", gets the singleton,
     * else, creates a local, restricted instance.
     */
    public static QuizState  getInstance(boolean  restricted) {
        if (m_singletonGameState == null)
        {
            m_singletonGameState = new QuizState(false);
            m_singletonGameState.initialize();
        }
        
        if (restricted)
        {
            // A restricted QuizState is a local instance that has the local
            // m_restricted flag set "true".  This restricts the holder
            // of the instance from accessing certain functions.
            return new QuizState(true);
        }
        else
            return m_singletonGameState;
    }
    
    
    /**
     * Private default constructor
     */
    private QuizState(boolean restricted)
    {
        m_restricted = restricted;
    }

    
    /**
     * Private initializer
     */
    private void  initialize()
    {
        m_gameConfig = GameConfig.getInstance();
        
        m_toggleStates = new BitSet(32);
        m_answersVisible = new BitSet(26);
        
        m_correctAnswer = NO_CORRECT_ANSWER;
        
        // Set values from the config file, else use the defaults
        setMaxNumberOfAnswers(m_gameConfig.getIntConfig("maxNumberOfAnswers", 4));
    }
    
    
    // Accessors

    /** Get the current game state
     * @return game state
     */
    public GameStateChangeEnum  getGameStateEnum() {
        return m_gameStateEnum;
    }

    /** Get the current question number (level)
     * @return Level number (0 - last)
     */
    public int  getCurrentLevel() {
        return m_currentLevel;
    }

    /** Get the current "title" for this level.
     * @return Title
     */
    public String  getLevelTitle() {
        return m_levelTitle;
    }

    /** Returns the name of the current player
     * @return Name of the player
     */
    public String  getPlayerName() {
        return m_playerName;
    }

    /** Returns the maximum number of possible answers
     * @return Maximum number
     */
    public int  getMaxNumberOfAnswers()
    {
        return m_maxNumberOfAnswers;
    }

    /** Returns the maximum number of possible levels (questions)
     * @return Maximum number
     */
    public int  getMaxNumberOfLevels()
    {
        return m_maxNumberOfLevels;
    }
    
    /** Gets the current boolean state of the toggle
     */
    public boolean  getToggleState(int  index)
    {
        if (index < 0 || index > m_toggleStates.size())
            return false;
        
        return (m_toggleStates.get(index));
    }
    
    /** Gets the the current toggle states
     */
    public BitSet  getToggleStates()
    {
        return (m_toggleStates);
    }
    
    /** Returns "true" if the specified answer is visible
     */
    public boolean  getAnswerVisible(int  index)
    {
        if (index < 0 || index > m_answersVisible.size())
            return false;
        
        return (m_answersVisible.get(index));
    }
    
    /** Returns the entire answerVisible BitSet
     */
    public BitSet  getAnswersVisible()
    {
        return m_answersVisible;
    }

    /** Returns the ID of the current question.
     * @return Question ID string
     */
    public String  getQuestionID()
    {
        if (m_questionID == null)
            return "";
        else
            return m_questionID;
    }

    /** Returns the current question string
     * @return Question string
     */
    public String  getQuestion()
    {
        if (m_question == null)
            return "";
        else
            return m_question;
    }

    /** Returns the specified answer string
     * @param index Answer index
     *
     * @return Answer string
     */
    public String  getAnswer(int  index)
    {
        if (m_answers == null || index < 0 || index >= m_answers.length)
            return "";
        
        return m_answers[index];
    }

    /** Returns the specified answer string
     * @param index Answer index
     *
     * @return Answer string
     */
    public String[]  getAnswers()
    {
        return m_answers;
    }
    
    
    /** Gets the Transition screen message */
    public String  getTransitionMessage()
    {
        return m_transitionMessage;
    }
    
    /** Gets the status message */
    public String  getStatusMessage()
    {
        return m_statusMessage;
    }
    
    /** Gets the current question timer limit. */
    public int  getQuestionTimerLimit()
    {
        return m_questionTimerLimit;
    }
    
    /** Gets the requested delay value. */
    public int  getDelayValue(int  delayType)
    {
        if (delayType >= 0 && delayType < NUM_DELAY_TYPES)
            return m_delayValues[delayType];
        else
            return 0;
    }

    
    /** Gets the current question timer time (for manually driving the clock)
     */
    public int  getQuestionTimerTime()
    {
        return m_questionTimerTime;
    }

    
    /** Gets the index of the correct answer.  If < 0, not set yet.
     */
    public int  getCorrectAnswer()
    {
        return m_correctAnswer;
    }

    /** Gets the index of the currently selected answer.  If < 0, not set yet.
     */
    public int  getSelectedAnswer()
    {
        return m_selectedAnswer;
    }



    /** Returns GameModeChangeEvent type */
    public GameModeEnum  getGameMode()
    {
        return m_gameMode;
    }
    
    
    
    
    //
    // Manipulators
    //

    /** Sets the current game state
     */
    public void  setGameStateEnum(GameStateChangeEnum  type) {
        if (m_restricted)
            throw new UnsupportedOperationException("Restricted mode");

        m_gameStateEnum = type;
    }

    
    /** Sets the maximum number of possible answers
     */
    public void  setMaxNumberOfAnswers(int  number)
    {
        if (m_restricted)
            throw new UnsupportedOperationException("Restricted mode");

        if (number < 2 || number > 26)
            return;
        
        m_maxNumberOfAnswers = number;
        return;
    }

    
    /** Sets the maximum number of levels (questions)
     */
    public void  setMaxNumberOfLevels(int  number)
    {
        if (m_restricted)
            throw new UnsupportedOperationException("Restricted mode");

        if (number < 2)
            return;
        
        m_maxNumberOfLevels = number;
        return;
    }

    
    /** Set the current question (level) number
     */
    public void  setCurrentLevel(int  number) {
        if (m_restricted)
            throw new UnsupportedOperationException("Restricted mode");

        if (number < 0)
            number = 0;
        
        m_currentLevel = number;
        return;
    }

    /** Sets the current level "title".  This title describes what level the
     * contestant is "trying" for.
     */
    public void  setLevelTitle(String  title) {
        if (m_restricted)
            throw new UnsupportedOperationException("Restricted mode");

        m_levelTitle = title;
        return;
    }

    
    /** Increments the current question (level) number by the given increment
     */
    public int  incrementCurrentLevel(int  increment) {
        if (m_restricted)
            throw new UnsupportedOperationException("Restricted mode");

        int  number = m_currentLevel + increment;
        
        setCurrentLevel(number);
        
        return number;
    }
    
    /** Sets the name of the current player
     */
    public void  setPlayerName(String  name) {
        if (m_restricted)
            throw new UnsupportedOperationException("Restricted mode");

        m_playerName = new String(name);
        return;
    }
    
    /** Sets the state of the specified toggle
     */
    public void  setToggleState(int  index, boolean  state)
    {
        if (index < 0 || index > m_toggleStates.size())
            return;
        
        if (state)
            m_toggleStates.set(index);
        else
            m_toggleStates.clear(index);
        
        return;
    }
    
    /** Sets toggle states all at once
     */
    public void  setToggleStates(BitSet  states)
    {
        if (m_restricted)
            throw new UnsupportedOperationException("Restricted mode");

        m_toggleStates = states;
        return;
    }
    
    /** Sets the visible state of the specified answer
     */
    public void  setAnswerVisible(int  index, boolean  state)
    {
        if (index < 0 || index > m_answersVisible.size())
            return;
        
        if (state)
            m_answersVisible.set(index);
        else
            m_answersVisible.clear(index);
        
        return;
    }
    
    /** Sets answer visible states all at once
     */
    public void  setAnswersVisible(BitSet  states)
    {
        if (m_restricted)
            throw new UnsupportedOperationException("Restricted mode");

        m_answersVisible = states;
        return;
    }

    /**
     * Stores the current question's ID.
     */
    public void  setQuestionID(String  id)
    {
        if (m_restricted)
            throw new UnsupportedOperationException("Restricted mode");
        
        m_questionID = id;
    }
    
    
    /** Stores the current question and answer strings
     */
    public void  setCurrentQuestion(String  question, String[]  answers)
    {
        if (m_restricted)
            throw new UnsupportedOperationException("Restricted mode");

        if (question == null || answers == null)
        {
            m_question = null;
            m_answers = null;

            for (int i = 0; i < m_answersVisible.size(); i++)
                setAnswerVisible(i, false);
            
            setCorrectAnswer(-1);
        }
        else
        {
            m_question = new String(question);
            m_answers = new String[answers.length];

            System.arraycopy(answers, 0, m_answers, 0, answers.length);
        }
    }


    
    /** Stores the transition screen message string
     */
    public void  setTransitionMessage(String  message)
    {
        if (m_restricted)
            throw new UnsupportedOperationException("Restricted mode");

        if (message == null)
            m_transitionMessage = null;
        else
            m_transitionMessage = new String(message);
    }

    
    /** Stores the current question and answer strings
     */
    public void  setStatusMessage(String  message)
    {
        if (m_restricted)
            throw new UnsupportedOperationException("Restricted mode");

        if (message == null)
            m_statusMessage = null;
        else
            m_statusMessage = new String(message);
    }

    /** Stores a new question timer limit
     */
    public void  setQuestionTimerLimit(int  seconds)
    {
        if (m_restricted)
            throw new UnsupportedOperationException("Restricted mode");

        if (seconds >= 0 && seconds <= QUESTION_TIMER_MAX_TIME)
            m_questionTimerLimit = seconds;
        else
            m_questionTimerLimit = 0;       // Disable
    }

    /** Stores a new question timer limit
     */
    public void  setQuestionTimerTime(int  seconds)
    {
        if (m_restricted)
            throw new UnsupportedOperationException("Restricted mode");

        if (seconds >= 0 && seconds <= m_questionTimerLimit)
            m_questionTimerTime = seconds;
    }
    
    /** Gets the requested delay value. */
    public void  setDelayValue(int  delayType, int  value)
    {
        if (m_restricted)
            throw new UnsupportedOperationException("Restricted mode");

        if (delayType >= 0 && delayType < NUM_DELAY_TYPES)
            m_delayValues[delayType] = value;

        return;
    }

    
    /** Sets the index of the correct answer.
     */
    public void  setCorrectAnswer(int  index)
    {
        if (m_restricted)
            throw new UnsupportedOperationException("Restricted mode");

        if (m_answers == null || index < 0 || index >=  m_answers.length)
            m_correctAnswer = -1;
        else
            m_correctAnswer = index;
    }

    
    /** Sets index of the player's currently selected answer.
     */
    public void  setSelectedAnswer(int  index)
    {
        if (m_restricted)
            throw new UnsupportedOperationException("Restricted mode");

        if (m_answers == null || index < 0 || index >=  m_answers.length)
            m_selectedAnswer = -1;
        else
            m_selectedAnswer = index;
    }


    
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

    

    //
    // GameStateChangeEvent management functions
    //
    
    /** Adds the GameStateChangeEvent listener to the listener list.
     */
    public void addGameStateChangeListener(GameStateChangeListener  l)
    {
        m_stateChangeListenerList.add(GameStateChangeListener.class, l);
    }
    
    /** Removes the GameStateChangeEvent listener from the listener list.
     */
    public void removeGameStateChangeListener(GameStateChangeListener  l)
    {
        m_stateChangeListenerList.remove(GameStateChangeListener.class, l);
    }

    /** Fires a GameStateChangeEvent out to all listeners.
     */
    public void  fireGameStateChangeEvent(Object  source, GameStateChangeEnum  type)
    {
        // Guaranteed to return a non-null array
        Object[] listeners = m_stateChangeListenerList.getListenerList();

        GameStateChangeEvent  event = new GameStateChangeEvent();
        event.setSource(source);
        event.setType(type);
        
        // Store a local copy of this event for sending to remote nodes
        m_gameStateEnum = type;
        
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
            if (listeners[i] == GameStateChangeListener.class &&
                    listeners[i] != source)
            {
                ((GameStateChangeListener)listeners[i+1]).gameStateChanged(event);
            }
        }
    }


    //
    // GameUpdateEvent management functions
    //
    
    /** Adds the GameUpdateEvent listener to the listener list.
     */
    public void addGameUpdateListener(GameUpdateListener  l)
    {
        m_updateListenerList.add(GameUpdateListener.class, l);
    }
    
    /** Removes the GameUpdateEvent listener from the listener list.
     */
    public void removeGameUpdateListener(GameUpdateListener  l)
    {
        m_updateListenerList.remove(GameUpdateListener.class, l);
    }

    /** Fires a GameUpdateEvent out to all listeners.
     */
    public void  fireGameUpdateEvent(Object  source, GameUpdateEnum  type)
    {
        // Guaranteed to return a non-null array
        Object[] listeners = m_updateListenerList.getListenerList();

        GameUpdateEvent  event = new GameUpdateEvent();
        event.setSource(source);
        event.setType(type);
        
        // Store a local copy of this event for sending to remote nodes
        m_gameUpdateEnum = type;
        
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
            if (listeners[i] == GameUpdateListener.class &&
                    listeners[i] != source)
            {
                ((GameUpdateListener)listeners[i+1]).gameUpdated(event);
            }
        }
    }

    //
    // GameInputEvent management functions
    //

    /** Adds the GameInputEvent listener to the listener list.
     */
    public void addGameInputListener(GameInputListener  l)
    {
        m_inputListenerList.add(GameInputListener.class, l);
    }
    
    
    /** Removes the GameInputEvent listener from the listener list.
     */
    public void removeGameInputListener(GameInputListener  l)
    {
        m_inputListenerList.remove(GameInputListener.class, l);
    }
    
    
    /** Fires a GameInputEvent out to all listeners.
     */
    public void  fireGameInputEvent(GameInputEvent  event)
    {
        Object[] listeners = m_inputListenerList.getListenerList();

        if ((m_gameConfig.getIntConfig("debugMode") & GameConfig.DEBUG_INPUTS) != 0)
        {
            System.out.println("--> QuizState::fireGameInputEvent("
                    + event.getType() + "), keyCode="
                    + event.getKeyCode() + ", keyChar="
                    + event.getKeyChar() + ", selectedIndex="
                    + event.getSelectedIndex());
        }

        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
            if (listeners[i] == GameInputListener.class &&
                    listeners[i] != event.getSource())
            {
                ((GameInputListener)listeners[i+1]).gameInputReceived(event);
            }
        }        
    }

    
    /**
     * Implementation of the GameInputListener interface.  This is called by
     * input sources in order to inform QuizState listeners of inputs.
     */
    public void gameInputReceived(GameInputEvent evt)
    {
        fireGameInputEvent(evt);
    }

    
    // Methods for managing and firing the GameInputEvent
    public void addGameModeChangeListener(GameModeChangeListener  l)
    {
        m_modeChangeListenerList.add(GameModeChangeListener.class, l);
    }
    
    public void removeGameModeChangeListener(GameModeChangeListener  l)
    {
        m_modeChangeListenerList.remove(GameModeChangeListener.class, l);
    }

    public void  fireGameModeChangeEvent(Object  source, GameModeEnum  type)
    {
        // Guaranteed to return a non-null array
        Object[] listeners = m_modeChangeListenerList.getListenerList();

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
    
    private boolean  m_restricted = false;
    
    private static QuizState  m_singletonGameState = null;
    
    private static GameConfig  m_gameConfig = null;

    private static GameInputEnum  m_gameInputEnum = GameInputEnum.NOP;
    private static GameStateChangeEnum  m_gameStateEnum;
    private static GameUpdateEnum  m_gameUpdateEnum;
    
    private static GameModeEnum  m_gameMode = GameModeEnum.STANDALONE;
    
    private static String  m_playerName;

    private static int  m_maxNumberOfAnswers = 4;
    private static int  m_maxNumberOfLevels = 15;
    
    private static String  m_questionID;
    private static String  m_question;
    private static String[]  m_answers;

    private static String  m_transitionMessage;
    private static String  m_statusMessage;
    
    private static int  m_currentLevel;
    private static String  m_levelTitle;
    
    private static int  m_selectedAnswer;
    private static int  m_correctAnswer;

    private static BitSet  m_answersVisible;
    private static BitSet  m_toggleStates;
    
    private static int  m_questionTimerLimit = 0;
    private static int  m_questionTimerTime;
    private static int[]  m_delayValues = new int[NUM_DELAY_TYPES];

    private static int  m_keyCode;
    private static char  m_keyChar;
}
