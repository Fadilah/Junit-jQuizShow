/*
 * QuizController.java
 *
 * Created on January 28, 2004, 9:12 PM
 *
 * $Id: QuizController.java,v 1.2 2007/02/05 03:55:49 sdchen Exp $
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
 *    $Log: QuizController.java,v $
 *    Revision 1.2  2007/02/05 03:55:49  sdchen
 *    Removed CR (Ctrl-M) from lines.
 *
 *    Revision 1.1  2004/04/02 06:02:00  sdchen
 *    Snapshot of jQuizShow 1.1 alpha06 development
 *
 *
 */

package jQuizShow.quiz;

/** Primary event manager class for the quiz mode. This class
 * maintains the game states and handles the user input events.
 *
 * @author  Steven D. Chen
 * @version 
 */

import java.lang.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import jQuizShow.*;
import jQuizShow.data.*;
import jQuizShow.net.*;
import jQuizShow.util.*;
import jQuizShow.event.*;

public class QuizController
        implements
            GameModeChangeListener,
            GameStateChangeListener,
            GameInputListener,
            GameTimerListener
{
    /** Gets the QuizController singleton */
    public static QuizController  getInstance()
    {
        if (m_QuizControllerSingleton == null)
            m_QuizControllerSingleton = new QuizController();
        
        return m_QuizControllerSingleton;
    }
    
    /** Creates new QuizController */
    private QuizController()
    {
        // Instantiate the QuestionTimer inner class
        m_questionTimer = new QuestionTimer();

        m_desktopPane = Main.getDesktopPane();
        
        m_quizState = QuizState.getInstance(false);

        // Add self as listeners to the required events
        m_quizState.addGameModeChangeListener(this);
        m_quizState.addGameStateChangeListener(this);
        m_quizState.addGameInputListener(this);

        // Create the game timer instance.  This timer is not visible, but
        // used for internal timing events.
        m_gameTimer = new GameTimer();
        m_gameTimer.addGameTimerListener(this);

        // Set the default game state
        m_state = QuizPhaseEnum.S_INACTIVE;
        
        // Get the GameConfig singleton instance
        m_gameConfig = GameConfig.getInstance();

        // Get the SoundPlayer singleton
        m_soundPlayer = SoundPlayer.getInstance();

        // Get the default question timer limit
        m_questionTimerLimit = m_gameConfig.getIntConfig("quizQuestionTimerLimit", 30);

        // Get the max number of possible answers
        m_maxNumberOfAnswers = m_quizState.getMaxNumberOfAnswers();
        
        // Create instances of the StatePacket and EventPacket.
        m_statePacket = new StatePacket();
        m_eventPacket = new EventPacket();
        
        // Get the PacketProcessor instance
        m_packetProcessor = PacketProcessor.getInstance();

        // Start at first level
        m_currentLevel = 0;

        /* Set the question timer limit */
        m_statePacket.setType(QuizStateEnum.SET_QUESTION_TIMER_LIMIT);
        m_statePacket.setQuestionTimerLimit(m_questionTimerLimit);

        m_packetProcessor.processStatePacket(m_statePacket);    // Do it
    }


    /**
     * Load questions from the specified database file
     */
    public int  loadQuestions(String  filename)
            throws FileNotFoundException, IOException
    {
        // Load the questions from the Q & A database
        String  questionFilepath = null;
        QuizLoader  qLoader;
        
        stopGame();
        
        String  encoding = m_gameConfig.getConfig("questionEncoding");

        m_questionList = null;          // Clear current questions
        
        questionFilepath = FileUtils.searchForFile(filename);

	System.out.println(Main.getMessage("msg_reading_questions")
		+ "  " + questionFilepath);

	qLoader = QuizLoader.getInstance();
	qLoader.setEncoding(encoding);
        qLoader.setDebugMode(m_gameConfig.getIntConfig("debugMode"));

        m_questionList = qLoader.readQuestions(questionFilepath);

	System.out.println(Main.getMessage("msg_quiz_questions_read"));

        // Get the total number of questions
        m_numberOfQuestions = m_questionList.getNumQuestionsRemaining(0);
        
        return m_numberOfQuestions;
    }
    
    
    /**
     * Start a new game
     */
    public void startGame()
    {
        if (m_gameMode == GameModeEnum.SLAVE)
        {
            /* Display "is slave" message */
            JOptionPane.showMessageDialog(m_desktopPane,
		    Main.getMessage("msg_slave_game_start"));
            return;
        }

        if (m_questionList == null)
        {
            JOptionPane.showMessageDialog(m_desktopPane,
		    Main.getMessage("msg_start_no_questions"));
            return;
        }       
        
        // Start at first level
        m_currentLevel = 0;

        // Reset the toggle states
        m_quizState.setToggleState(QuizState.ANSWERS_SELECTABLE, false);

        m_eventPacket.setType(QuizUpdateEnum.TOGGLE_STATE);
        m_eventPacket.setToggleStates(m_quizState.getToggleStates());
        
        m_packetProcessor.processEventPacket(m_eventPacket);    // Do it

        /* Initialize for a new game */
        m_statePacket.setType(QuizStateEnum.NEW_GAME);
        m_statePacket.setMaxNumberOfLevels(m_numberOfQuestions);
        m_statePacket.setCurrentLevel(0);

        m_packetProcessor.processStatePacket(m_statePacket);    // Do it

        // Set the starting message
        m_eventPacket.setType(QuizUpdateEnum.TRANSITION_MSG);
        m_eventPacket.setTransitionMessage(Main.getMessage("msg_ready"));

        m_packetProcessor.processEventPacket(m_eventPacket);    // Do it

        /* Clear the status text */
        m_eventPacket.setType(QuizUpdateEnum.STATUS_MSG);
        m_eventPacket.setStatusMessage("");

        m_packetProcessor.processEventPacket(m_eventPacket);    // Do it

        // Stop all sounds
        m_soundPlayer.stop(SoundPlayer.ALL_CHANNELS);
        
        // Play new player sound
        m_soundPlayer.loadSound(SoundIDEnum.NEW_PLAYER,
                SoundPlayer.FOREGROUND_CHANNEL, false);
        m_soundPlayer.start(SoundPlayer.FOREGROUND_CHANNEL);

        
        m_soundPlayer.loadSound(SoundIDEnum.BACKGROUND1,
                SoundPlayer.BACKGROUND_CHANNEL, true);
/*
        // Set the background soundtrack playlist
        SoundIDEnum[]       playList =
        {
            SoundIDEnum.BACKGROUND4,
            SoundIDEnum.BACKGROUND5,
            SoundIDEnum.BACKGROUND6
        };
        
        m_soundPlayer.loadPlayList(playList);
*/
        
        // Set the initial phase
        m_state = QuizPhaseEnum.S_WAITING_TO_START;
    }
    
    
    /** Stops (aborts) the game. */
    public void stopGame()
    {
        // Stop the game - go to inactive mode
        m_state = QuizPhaseEnum.S_INACTIVE;

        // Stop all sounds
        m_soundPlayer.stop(SoundPlayer.ALL_CHANNELS);

        // Stop the timers
        m_questionTimer.stopTimer();
        m_gameTimer.stopTimer();

        // Clear the "game in session" flag
        m_quizState.setToggleState(QuizState.GAME_IN_SESSION, false);

        // Inform everyone this is the end of the game
        m_statePacket.setType(QuizStateEnum.END_OF_GAME);

        m_packetProcessor.processStatePacket(m_statePacket);    // Do it
    }

    
    /**
     * Set the quiz mode to be either normal or "show answers" mode.
     *
     * @param showAnswers   true = show answers mode, false = normal quiz mode
     */
    public void setShowAnswersMode(boolean  showAnswers)
    {
        // Set the show answers mode state.
        m_quizState.setToggleState(QuizState.ANSWER_MODE, showAnswers);

        // Inform everyone that the show answers mode has changed.
        m_statePacket.setType(QuizStateEnum.SET_SHOW_ANSWERS_MODE);

        m_packetProcessor.processStatePacket(m_statePacket);    // Do it

        if (m_quizState.getToggleState(QuizState.GAME_IN_SESSION) == false)
            return;

        // Stop the timers
        m_questionTimer.stopTimer();
        m_gameTimer.stopTimer();

        /* Set the initial phase */
        m_state = QuizPhaseEnum.S_WAITING_TO_START;

        nextPhase();
    }

    
    /**
     * Sets whether the answers should be shown or not after the timer runs out.
     *
     * @param showAnswers   true = show answers, false = questions only
     */
    public void setShowAnswers(boolean  showAnswers)
    {
        // Set the show answers mode state.
        m_quizState.setToggleState(QuizState.SHOW_ANSWERS, showAnswers);
    }

    
    /**
     * Sets the current quiz question.
     */
    public void  setQuestion(String  id) throws IllegalArgumentException
    {
        Question  question;
        
        if (m_quizState.getToggleState(QuizState.GAME_IN_SESSION) == false)
            throw new IllegalArgumentException(Main.getMessage("err_quiz_not_in_session"));

        for (int  newLevel = 0; newLevel < m_numberOfQuestions; newLevel++)
        {
            question = m_questionList.getQuestion(0, newLevel);
            
            if (id.compareToIgnoreCase(question.getQuestionID()) == 0)
            {
                setQuestionImpl(newLevel);
                return;
            }
        }

        try
        {
            int  newLevel = Integer.parseInt(id);
            
            if (newLevel >= 0 && newLevel <= m_numberOfQuestions)
            {
                setQuestionImpl(newLevel);
                return;
            }
        }
        catch (NumberFormatException  nf_e)
        {
        }
        
        throw new IllegalArgumentException(Main.getMessage("err_quiz_no_matching_question"));
    }

    
    private void  setQuestionImpl(int  questionIdx)
    {
        // Matching question found -- set as the current question
        m_currentLevel = questionIdx;

        // Stop the timers
        m_questionTimer.stopTimer();
        m_gameTimer.stopTimer();

        /* Set the initial phase */
        m_state = QuizPhaseEnum.S_WAITING_TO_START;

        nextPhase();

        return;
    }
    

    /**
     * Changes the game mode. Implements the GameModeChangeListener interface.
     */
    public void gameModeChanged(GameModeChangeEvent evt) {
        GameModeEnum  evtType = evt.getType();

        m_gameMode = evtType;
        
        if (evtType == GameModeEnum.STANDALONE ||
                evtType == GameModeEnum.MASTER)
        {
            // Reset to standalone/master mode
        }
        else if (evtType == GameModeEnum.SLAVE)
        {
            // Reset for slave mode
        }
        else if (evtType == GameModeEnum.RESET)
        {
            stopGame();
        }
    }    


    public void gameStateChanged(GameStateChangeEvent evt)
    {
        QuizStateEnum  type = (QuizStateEnum) evt.getType();
        
        if (type == QuizStateEnum.SET_QUESTION_TIMER_LIMIT)
        {
            // Get the limit from the global instance
            m_questionTimerLimit = QuizState.getInstance(false).getQuestionTimerLimit();
            
            // Set the question timer limit
            m_questionTimer.setupTimer(m_questionTimerLimit, 0);
        }
        else if (type == QuizStateEnum.QUESTION_TIMER_EXPIRED)
        {
            if (m_quizState.getToggleState(QuizState.ANSWER_MODE) == false)
            {
                if (m_quizState.getToggleState(QuizState.SHOW_ANSWERS) == true)
                {
                    // In show answers mode, a space bar in this phase will
                    // cause a jump to the "correct answer" phase.
                    for (int  index = 0; index < m_answers.length; index++)
                    {
                        if (m_answers[index].getCorrect())
                        {
                            // Set the correct answer
                            m_selectedIndex = index;
                            break;
                        }
                    }

                    // Switch state flag to next state
                    m_state = QuizPhaseEnum.S_CORRECT_ANSWER;

                    nextPhase();
                }
                else
                {
                    // Switch state flag to next state
                    m_state = QuizPhaseEnum.S_RESET_FOR_NEXT_ROUND;

                    nextPhase();
                }
            }
        }
    }
    
    
    /** Process game input events.  Examples include:  selection of an
     * answer (letter key), hot-key pressed.
     */
    public void gameInputReceived(GameInputEvent evt)
    {
        GameInputEnum  evtType = evt.getType();

        if (m_gameMode == GameModeEnum.SLAVE)
            return;     // In slave mode -- ignore all inputs
        
        if (evtType == GameInputEnum.ANSWER_SELECTED)
        {
            if (m_quizState.getToggleState(QuizState.ANSWER_MODE) == true &&
                    (m_state == QuizPhaseEnum.S_ANSWERS_DISPLAYED ||
                    m_state == QuizPhaseEnum.S_ANSWER_SELECTED))
            {
                /* An answer was selected.  Get its index. */
                m_selectedIndex = evt.getSelectedIndex();

                if (m_quizState.getAnswerVisible(m_selectedIndex) == true)
                {
                    m_state = QuizPhaseEnum.S_ANSWER_SELECTED;

                    /* Transition to next phase */
                    nextPhase();
                }
            }
        }
        else if (evtType == GameInputEnum.KEY_TYPED)
        {            
            char  keyChar = Character.toLowerCase(evt.getKeyChar());
            int  keyCode = evt.getKeyCode();
            
            if (keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_KP_LEFT)
            {
                if (m_currentLevel > 0)
                    setQuestionImpl(m_currentLevel - 1);
            }
            else if (keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_KP_RIGHT)
            {
                if (m_currentLevel < m_numberOfQuestions - 1)
                    setQuestionImpl(m_currentLevel + 1);
            }
            else if (m_answers != null && keyChar >= 'a' && keyChar <= ('a' + m_answers.length - 1))
            {
                if (m_quizState.getToggleState(QuizState.ANSWER_MODE) == true &&
                        (m_state == QuizPhaseEnum.S_ANSWERS_DISPLAYED ||
                        m_state == QuizPhaseEnum.S_ANSWER_SELECTED))
                {
                    /* An answer was selected.  Get its index. */
                    m_selectedIndex = keyChar - 'a';

                    if (m_quizState.getAnswerVisible(m_selectedIndex) == true)
                    {
                        m_state = QuizPhaseEnum.S_ANSWER_SELECTED;

                        /* Transition to next phase */
                        nextPhase();
                    }
                }
            }
            else
            {
                /* Process special event keys */
                switch (keyChar)
                {
		    // Sound clip hotkeys (Numpad 0-9)
                    case '0' :
                    {
                        m_soundPlayer.loadSound(SoundIDEnum.CLIP_0,
                                SoundPlayer.FOREGROUND_CHANNEL, false);
                        m_soundPlayer.start(SoundPlayer.FOREGROUND_CHANNEL);
                        break;
                    }
                    
                    case '1' :
                    {
                        m_soundPlayer.loadSound(SoundIDEnum.CLIP_1,
                                SoundPlayer.FOREGROUND_CHANNEL, false);
                        m_soundPlayer.start(SoundPlayer.FOREGROUND_CHANNEL);
                        break;
                    }
                    
                    case '2' :
                    {
                        m_soundPlayer.loadSound(SoundIDEnum.CLIP_2,
                                SoundPlayer.FOREGROUND_CHANNEL, false);
                        m_soundPlayer.start(SoundPlayer.FOREGROUND_CHANNEL);
                        break;
                    }
                    
                    case '3' :
                    {
                        m_soundPlayer.loadSound(SoundIDEnum.CLIP_3,
                                SoundPlayer.FOREGROUND_CHANNEL, false);
                        m_soundPlayer.start(SoundPlayer.FOREGROUND_CHANNEL);
                        break;
                    }
                    
                    case '4' :
                    {
                        m_soundPlayer.loadSound(SoundIDEnum.CLIP_4,
                                SoundPlayer.FOREGROUND_CHANNEL, false);
                        m_soundPlayer.start(SoundPlayer.FOREGROUND_CHANNEL);
                        break;
                    }
                    
                    case '5' :
                    {
                        m_soundPlayer.loadSound(SoundIDEnum.CLIP_5,
                                SoundPlayer.FOREGROUND_CHANNEL, false);
                        m_soundPlayer.start(SoundPlayer.FOREGROUND_CHANNEL);
                        break;
                    }
                    
                    case '6' :
                    {
                        m_soundPlayer.loadSound(SoundIDEnum.CLIP_6,
                                SoundPlayer.FOREGROUND_CHANNEL, false);
                        m_soundPlayer.start(SoundPlayer.FOREGROUND_CHANNEL);
                        break;
                    }
                    
                    case '7' :
                    {
                        m_soundPlayer.loadSound(SoundIDEnum.CLIP_7,
                                SoundPlayer.FOREGROUND_CHANNEL, false);
                        m_soundPlayer.start(SoundPlayer.FOREGROUND_CHANNEL);
                        break;
                    }
                    
                    case '8' :
                    {
                        m_soundPlayer.loadSound(SoundIDEnum.CLIP_8,
                                SoundPlayer.FOREGROUND_CHANNEL, false);
                        m_soundPlayer.start(SoundPlayer.FOREGROUND_CHANNEL);
                        break;
                    }
                    
                    case '9' :
                    {
                        m_soundPlayer.loadSound(SoundIDEnum.CLIP_9,
                                SoundPlayer.FOREGROUND_CHANNEL, false);
                        m_soundPlayer.start(SoundPlayer.FOREGROUND_CHANNEL);
                        break;
                    }
                    
                    case '\n' :         // Enter key -- "Final answer"
                    {
                        /* Confirm key -- validity depending on the current game state
                         * and context (e.g. if an answer is selected)
                         */
                        if (m_state == QuizPhaseEnum.S_ANSWER_SELECTED)
                        {
                            m_state = QuizPhaseEnum.S_ANSWER_FINALIZED;
                            nextPhase();
                        }
                        break;
                    }

                    case '\b' :         // Backspace key
                    {
                        /* This key is used clear the selected lifeline or
			 * to backout a supposedly "final" answer.  The
			 * latter is against the "official" rules, but...
                         */
                        if (m_state == QuizPhaseEnum.S_ANSWER_SELECTED
                                || m_state == QuizPhaseEnum.S_CORRECT_ANSWER
                                || m_state == QuizPhaseEnum.S_WRONG_ANSWER)
                        {
                            /* Re-enable selection */
                            m_quizState.setToggleState(QuizState.ANSWERS_SELECTABLE, true);

                            m_eventPacket.setType(QuizUpdateEnum.TOGGLE_STATE);
                            m_eventPacket.setToggleStates(m_quizState.getToggleStates());

                            m_packetProcessor.processEventPacket(m_eventPacket);    // Do it

                            // Clear the selection
                            m_eventPacket.setType(QuizUpdateEnum.SELECTED_ANSWER);
                            m_eventPacket.setSelectedAnswer(-1);

                            m_packetProcessor.processEventPacket(m_eventPacket);    // Do it

                            m_state = QuizPhaseEnum.S_ANSWERS_DISPLAYED;

                            m_questionTimer.startTimer();        // Restart the question timer
                        }
                        break;
                    }

                    case '.' :          // Period key -- toggle question timer pause
                    {
                        if (m_quizState.getToggleState(QuizState.ANSWER_MODE) == false)
                        {
                            if (m_questionTimer.isActive())
                            {
                                if (m_questionTimer.isPaused())
                                    m_questionTimer.startTimer();
                                else
                                    m_questionTimer.stopTimer();
                            }
                        }
                        break;
                    }
                    
                    case ' ' :          // Space key -- next phase
                    {
                        /* "Transition to next phase" key */
                        nextPhase();
                        break;
                    }
                }
            }
        }
    }

    
    /** Transition to the next game phase */
    private void nextPhase()
    {
        if ((m_gameConfig.getIntConfig("debugMode") & GameConfig.DEBUG_BASIC) != 0)
            System.out.println("--> QuizController::nextPhase() = " + m_state);

        if (m_state == QuizPhaseEnum.S_INACTIVE)
        {
            /* No change.  Must be set to an active state for a transition to occur */
        }
        else if (m_state == QuizPhaseEnum.S_WAITING_TO_START)
        {       
            // Set the "game in session" flag
            m_quizState.setToggleState(QuizState.GAME_IN_SESSION, true);

            {
                m_soundPlayer.loadSound(SoundIDEnum.SHOW_QUESTION,
                        SoundPlayer.FOREGROUND_CHANNEL, false);
                m_soundPlayer.start(SoundPlayer.FOREGROUND_CHANNEL);
            }

            if (m_soundPlayer.isPlaying(SoundPlayer.BACKGROUND_CHANNEL) == false)
            {
                m_soundPlayer.start(SoundPlayer.BACKGROUND_CHANNEL);              
            }

            /* Start the background sound if it isn't playing */
            if (m_soundPlayer.isPlaying(SoundPlayer.BACKGROUND_CHANNEL) == false &&
                    m_bkgSoundId != null)
            {
                m_bkgSoundId = SoundIDEnum.BACKGROUND1;
                
                m_soundPlayer.loadSound(m_bkgSoundId,
                        SoundPlayer.BACKGROUND_CHANNEL, false);
                m_soundPlayer.start(SoundPlayer.BACKGROUND_CHANNEL);
            }

            // Hide all answers
            for (int  i = 0; i < m_maxNumberOfAnswers; i++)
            {
                m_quizState.setAnswerVisible(i, false);
            }

            // Set the Transition panel text to the current level title.
            {
                String  title;
            
                Question  question = m_questionList.getQuestion(0, m_currentLevel);

                Object	args[] = 
                        {
                            question.getQuestionID()
                        };

                title = Main.getMessage("msg_quiz_transition", args);

                if (title != null)
                {
                    // Send event to update the transition text
                    m_eventPacket.setType(QuizUpdateEnum.TRANSITION_MSG);
                    m_eventPacket.setTransitionMessage(title);

                    m_packetProcessor.processEventPacket(m_eventPacket);    // Do it
                }
            }
            
            // Set up for next question
            {
                int  numRemaining = m_questionList.getNumQuestionsRemaining(0);

                if (numRemaining == 0)
                {
                    String  msg = Main.getMessage("err_no_more_questions")
                            + " " + 0;

                    System.out.println(msg);

                    Main.setStatusLabel(msg);

                    JOptionPane.showMessageDialog(m_desktopPane, msg);

                    m_state = QuizPhaseEnum.S_INACTIVE;

                    return;
                }

                m_question = m_questionList.getQuestion(0, m_currentLevel);
            }

            // Fire an event to update the game

            m_statePacket.setType(QuizStateEnum.WAIT_TO_START_ROUND);
            m_statePacket.setCorrectAnswer(QuizState.NO_CORRECT_ANSWER);
            m_statePacket.setQuestion("");
            m_statePacket.setAnswers(null);

            m_statePacket.setAnswersVisible(m_quizState.getAnswersVisible());
            m_statePacket.setCurrentLevel(m_currentLevel);
            m_statePacket.setQuestionID(m_question.getQuestionID());

            m_packetProcessor.processStatePacket(m_statePacket);    // Do it

            m_state = QuizPhaseEnum.S_STARTING_NEXT_ROUND;
            
            // If in quiz mode, start timer for transition delay
            if (m_quizState.getToggleState(QuizState.ANSWER_MODE) == false)
            {
                m_gameTimer.setTimer(
                    m_quizState.getDelayValue(QuizState.DELAY_AT_START),
                    0, true);
                m_gameTimer.startTimer();
            }
        }
        else if (m_state == QuizPhaseEnum.S_STARTING_NEXT_ROUND)
        {
            /* Start the next round.  First, get a question from the available
             * questions list */

            String[]  answerStr;        // Shuffled list of possible answers

            // Clear the transition text
            m_eventPacket.setType(QuizUpdateEnum.TRANSITION_MSG);
            m_eventPacket.setTransitionMessage(null);

            m_packetProcessor.processEventPacket(m_eventPacket);    // Do it

            {

                AnswerList  answers = m_question.getAnswers();

                /* Get the array of possible answers */
                m_answers = answers.getOrderedAnswers();

                answerStr = new String[m_answers.length];

                for (int  i = 0; i < m_answers.length; i++)
                {
                    answerStr[i] = m_answers[i].getAnswer();
                }
            }

            /* Zero the number of answers currently displayed counter. */
            m_numAnswersDisplayed = 0;

            /* Disable answer selections */
            m_quizState.setToggleState(QuizState.ANSWERS_SELECTABLE, false);

            m_eventPacket.setType(QuizUpdateEnum.TOGGLE_STATE);
            m_eventPacket.setToggleStates(m_quizState.getToggleStates());

            m_packetProcessor.processEventPacket(m_eventPacket);    // Do it

            /* Clear any selected answer index */
            m_eventPacket.setType(QuizUpdateEnum.SELECTED_ANSWER);
            m_eventPacket.setSelectedAnswer(QuizState.NO_SELECTED_ANSWER);

            m_packetProcessor.processEventPacket(m_eventPacket);    // Do it

            /* Play the "show question" sound */
            m_soundPlayer.loadSound(SoundIDEnum.SHOW_QUESTION,
                    SoundPlayer.FOREGROUND_CHANNEL, false);
            m_soundPlayer.start(SoundPlayer.FOREGROUND_CHANNEL);

            // Set the question and possible answers
            m_statePacket.setType(QuizStateEnum.DISPLAY_QUESTION);
            m_statePacket.setQuestion(m_question.getQuestion());
            m_statePacket.setAnswers(answerStr);

            m_packetProcessor.processStatePacket(m_statePacket);    // Do it

            /* Switch state flag to next state */
            m_state = QuizPhaseEnum.S_QUESTION_DISPLAYED;
            
            // If in quiz mode, start timer for transition delay
            if (m_quizState.getToggleState(QuizState.ANSWER_MODE) == false)
            {
                m_gameTimer.setTimer(
                    m_quizState.getDelayValue(QuizState.DELAY_AFTER_QUESTION),
                    0, true);
                m_gameTimer.startTimer();
            }
            else
                nextPhase();
        }
        else if (m_state == QuizPhaseEnum.S_QUESTION_DISPLAYED)
        {
            for (m_numAnswersDisplayed = 0;
                    m_numAnswersDisplayed < m_answers.length;
                    m_numAnswersDisplayed++)
            {
                // Show the next answer
                m_quizState.setAnswerVisible(m_numAnswersDisplayed, true);
            }

            m_statePacket.setType(QuizStateEnum.DISPLAY_ANSWER);
            m_statePacket.setAnswersVisible(m_quizState.getAnswersVisible());

            m_packetProcessor.processStatePacket(m_statePacket);    // Do it

            // Play the "show answer" sound
            m_soundPlayer.loadSound(SoundIDEnum.SHOW_ANSWER,
                    SoundPlayer.FOREGROUND_CHANNEL, false);
            m_soundPlayer.start(SoundPlayer.FOREGROUND_CHANNEL);

            // All answers now displayed.
            m_statePacket.setType(QuizStateEnum.WAIT_FOR_ANSWER);

            m_packetProcessor.processStatePacket(m_statePacket);    // Do it

            if (m_quizState.getToggleState(QuizState.ANSWER_MODE) == false)
            {
                // Start the question timer in quiz mode.  There is no
                // time limit in show answer mode.               
                m_questionTimer.setupTimer(-1, 0);  // Reset the question timer
                m_questionTimer.startTimer();
            }
            
            // Switch state flag to next state
            m_state = QuizPhaseEnum.S_ANSWERS_DISPLAYED;
        }
        else if (m_state == QuizPhaseEnum.S_ANSWERS_DISPLAYED)
        {
            if (m_quizState.getToggleState(QuizState.ANSWER_MODE) == true)
            {
                // In show answers mode, a space bar in this phase will
                // cause a jump to the "correct answer" phase.
                for (int  index = 0; index < m_answers.length; index++)
                {
                    if (m_answers[index].getCorrect())
                    {
                        // Set the correct answer
                        m_selectedIndex = index;
                        break;
                    }
                }

                // Switch state flag to next state
                m_state = QuizPhaseEnum.S_CORRECT_ANSWER;
                
                nextPhase();
            }
            else
            {
                /* Input is ignored until the timer runs out */
            }
        }
        else if (m_state == QuizPhaseEnum.S_ANSWER_SELECTED)
        {
            // This state does not change until the Enter key is pressed.
            // See the gameInputReceived()

            
            // Highlight the selection
            m_eventPacket.setType(QuizUpdateEnum.SELECTED_ANSWER);
            m_eventPacket.setSelectedAnswer(m_selectedIndex);

            m_packetProcessor.processEventPacket(m_eventPacket);

            // Play selected sound
            m_soundPlayer.loadSound(SoundIDEnum.SELECTION_MADE, SoundPlayer.FOREGROUND_CHANNEL, false);
            m_soundPlayer.start(SoundPlayer.FOREGROUND_CHANNEL);
        }
        else if (m_state == QuizPhaseEnum.S_ANSWER_FINALIZED)
        {
            // Play answer finalized sound.
            m_soundPlayer.loadSound(SoundIDEnum.ANSWER_FINALIZED, SoundPlayer.FOREGROUND_CHANNEL, false);
            m_soundPlayer.start(SoundPlayer.FOREGROUND_CHANNEL);

            /* Fire off an event to QuizState listeners to update their state */
            m_quizState.fireGameStateChangeEvent(this, QuizStateEnum.WAIT_TO_REVEAL_ANSWER);

             /* Check if the answer is correct or incorrect.  This method
             * sets the next phase based on the result.
             */
            processFinalAnswer();
        }
        else if (m_state == QuizPhaseEnum.S_CORRECT_ANSWER)
        {
            m_questionTimer.stopTimer();        // Stop the question timer

            // Play the correct answer sound.
            m_soundPlayer.loadSound(SoundIDEnum.ANSWER_CORRECT, SoundPlayer.FOREGROUND_CHANNEL, false);
            m_soundPlayer.start(SoundPlayer.FOREGROUND_CHANNEL);

            // If there is a note associated with the question, display it now.
            String  note = m_question.getNote();

            if (note != null && note.length() > 0)
            {
                m_eventPacket.setType(QuizUpdateEnum.STATUS_MSG);
                m_eventPacket.setStatusMessage(note);

                m_packetProcessor.processEventPacket(m_eventPacket);    // Do it
            }

            /* Fire off an event to QuizState listeners to update their state */
            m_statePacket.setType(QuizStateEnum.ANSWER_WAS_CORRECT);
            m_statePacket.setCorrectAnswer(m_selectedIndex);

            m_packetProcessor.processStatePacket(m_statePacket);    // Do it

            /* Switch state flag to next state */
            m_state = QuizPhaseEnum.S_RESET_FOR_NEXT_ROUND;
            
            // If in quiz mode, jump to next phase
            if (m_quizState.getToggleState(QuizState.SHOW_ANSWERS) == true)
                nextPhase();
        }
        else if (m_state == QuizPhaseEnum.S_WRONG_ANSWER)
        {
            int     correctAnswerIndex = QuizState.NO_CORRECT_ANSWER;

            m_questionTimer.stopTimer();        // Stop the question timer

            // Play the wrong answer sound
            m_soundPlayer.loadSound(SoundIDEnum.ANSWER_WRONG, SoundPlayer.FOREGROUND_CHANNEL, false);
            m_soundPlayer.start(SoundPlayer.FOREGROUND_CHANNEL);

            for (int  index = 0; index < m_answers.length; index++)
            {
                if (m_answers[index].getCorrect())
                {
                    // Set the correct answer
                    correctAnswerIndex = index;
                    break;
                }
            }

            // If there is a note associated with the question, display it now.
            String  note = m_question.getNote();

            if (note != null && note.length() > 0)
            {
                m_eventPacket.setType(QuizUpdateEnum.STATUS_MSG);
                m_eventPacket.setStatusMessage(note);

                m_packetProcessor.processEventPacket(m_eventPacket);    // Do it
            }

            /* Fire off an event to QuizState listeners to update their state */
            m_statePacket.setType(QuizStateEnum.ANSWER_WAS_WRONG);
            m_statePacket.setCorrectAnswer(correctAnswerIndex);

            m_packetProcessor.processStatePacket(m_statePacket);    // Do it

            /* Switch state flag to next state */
            m_state = QuizPhaseEnum.S_RESET_FOR_NEXT_ROUND;
            
            if (m_quizState.getToggleState(QuizState.SHOW_ANSWERS) == true)
                nextPhase();
        }
        else if (m_state == QuizPhaseEnum.S_RESET_FOR_NEXT_ROUND)
        {
            int  questionNum;

            /* Increment to next question */
            questionNum = ++m_currentLevel;

            // Set the current level
            m_statePacket.setType(QuizStateEnum.SET_CURRENT_LEVEL);
            m_statePacket.setCurrentLevel(questionNum);

            m_packetProcessor.processStatePacket(m_statePacket);    // Do it

            // Hide all answers
            for (int  i = 0; i < m_maxNumberOfAnswers; i++)
                m_quizState.setAnswerVisible(i, false);

            /* Fire off an event to QuizState listeners to update their state */
            m_statePacket.setCorrectAnswer(QuizState.NO_CORRECT_ANSWER);
            m_statePacket.setQuestion("");
            m_statePacket.setAnswers(null);

            m_statePacket.setAnswersVisible(m_quizState.getAnswersVisible());

            m_packetProcessor.processStatePacket(m_statePacket);    // Do it

            if (questionNum >= m_numberOfQuestions)
            {
                // Last question was displayed!  Switch state flag to end of game state.
                m_state = QuizPhaseEnum.S_ENDING_GAME;
            }
            else
            {
                // Switch state flag to next state
                m_state = QuizPhaseEnum.S_TRANSITION_TO_NEXT_ROUND;
            }
            
            // If in quiz mode, start timer for transition delay
            if (m_quizState.getToggleState(QuizState.ANSWER_MODE) == false)
            {
                m_gameTimer.setTimer(
                    m_quizState.getDelayValue(QuizState.DELAY_AFTER_TIMER_EXPIRES),
                    0, true);
                m_gameTimer.startTimer();
            }
            else
                nextPhase();
        }
        else if (m_state == QuizPhaseEnum.S_ENDING_GAME)
        {
            // Ending game -- Display the winnings and play "Winner" sound

            // Stop background sound
            m_soundPlayer.stop(SoundPlayer.BACKGROUND_CHANNEL);

            // Play the "Winner" sound
            m_soundPlayer.loadSound(SoundIDEnum.WINNER,
                    SoundPlayer.FOREGROUND_CHANNEL, false);
            m_soundPlayer.start(SoundPlayer.FOREGROUND_CHANNEL);

            // Switch game to inactive mode
            m_quizState.setToggleState(QuizState.GAME_IN_SESSION, false);

            /* Set the Transition panel text to the current level title. */
            String  title = Main.getMessage("msg_quiz_over");

            if (title != null)
            {
                // Send event to update the transition text
                m_eventPacket.setType(QuizUpdateEnum.TRANSITION_MSG);
                m_eventPacket.setTransitionMessage(title);

                m_packetProcessor.processEventPacket(m_eventPacket);    // Do it
            }

            // Switch state flag to next state
            m_state = QuizPhaseEnum.S_TRANSITION_TO_NEXT_ROUND;
        }
        else if (m_state == QuizPhaseEnum.S_TRANSITION_TO_NEXT_ROUND)
        {
            // Clear the status message
            m_eventPacket.setType(QuizUpdateEnum.STATUS_MSG);
            m_eventPacket.setStatusMessage("");

            m_packetProcessor.processEventPacket(m_eventPacket);    // Do it

            if (m_quizState.getToggleState(QuizState.GAME_IN_SESSION) == true)
            {
                // Transition to the next level
                m_state = QuizPhaseEnum.S_WAITING_TO_START;
                nextPhase();
            }
            else
            {
                // Game over - go to inactive mode
                m_state = QuizPhaseEnum.S_INACTIVE;

                // Stop all sounds
                m_soundPlayer.stop(SoundPlayer.ALL_CHANNELS);

                // Inform everyone this is the end of the game
                m_statePacket.setType(QuizStateEnum.END_OF_GAME);

                m_packetProcessor.processStatePacket(m_statePacket);    // Do it
            }
        }

        // If in quiz mode (i.e. not in "show answer mode"), the system
        // runs on its own once started.
        if (m_quizState.getToggleState(QuizState.ANSWER_MODE) == false)
        {

        }
    }
   
    
    /** Called when the player confirms the selected answer is the final
     * answer. */
    private void processFinalAnswer()
    {
        if (m_answers[m_selectedIndex].getCorrect())
            m_state = QuizPhaseEnum.S_CORRECT_ANSWER;
        else
            m_state = QuizPhaseEnum.S_WRONG_ANSWER;

        return;
    }

    
    /** Called when a game timer event occurs */
    public void gameTimerActionPerformed(GameTimerEvent  evt)
    {
        if (evt.getType() == GameTimerEvent.TIMER_ELAPSED)
        {
            if (m_quizState.getToggleState(QuizState.ANSWER_MODE) == false)
            {
                nextPhase();
            }
        }
    }
    
    /** Called whenever each second while timer is running */
    public void gameTimerOneSecond(GameTimerEvent  evt)
    {
    }
    
    private class QuestionTimer
            implements ActionListener
    {
        public  QuestionTimer()
        {
            m_timer = new Timer(1000, this);
            m_timer.setRepeats(true);
        }

        /** Setup/reset the timer.  If timeLimit < 0, use the time limit
         * that was used last time.
         */
        public void  setupTimer(int  timeLimit, int  currentTime)
        {
            // If timeLimit >= 0, use its value, else use previous limit
            if (timeLimit >= 0)
                m_timeLimit = timeLimit;
            
            m_elapsedTime = currentTime;

            /* Sanity checks */
            if (m_timeLimit < 0)
                m_timeLimit = 0;

            if (currentTime > m_timeLimit)
                m_elapsedTime = m_timeLimit;

            m_eventPacket.setType(QuizUpdateEnum.UPDATE_QUESTION_CLOCK);
            m_eventPacket.setQuestionTimerTime(m_elapsedTime);
            
            m_packetProcessor.processEventPacket(m_eventPacket);    // Do it
        }
    
        /** Starts the timer running */
        public void startTimer()
        {
            if (m_timeLimit == 0)
                m_timer.setInitialDelay(10);
            else
                m_timer.setInitialDelay(1000);
            
            m_timer.start();
            m_paused = false;

            m_eventPacket.setType(QuizUpdateEnum.UPDATE_QUESTION_CLOCK);
            m_eventPacket.setQuestionTimerTime(m_elapsedTime);
            
            m_packetProcessor.processEventPacket(m_eventPacket);    // Do it
        }

        /** Stops/pauses the timer */
        public void stopTimer()
        {
            m_timer.stop();
            m_paused = true;
        }

        /** Gets the current time limit */
        public int  getLimit()
        {
            return m_timeLimit;
        }

        /** Gets the current elapsed time */
        public int  getElapsedTime()
        {
            return m_elapsedTime;
        }

        /** Gets the current pause state */
        public boolean  isPaused()
        {
            return m_paused;
        }

        /** Gets the current timer active state */
        public boolean  isActive()
        {
            return (m_elapsedTime < m_timeLimit);
        }
        
        public void actionPerformed(ActionEvent  evt)
        {
            if (evt.getSource() == m_timer)
            {
                m_elapsedTime++;

                if (m_elapsedTime >= m_timeLimit)
                {
                    /* Time elapsed.  Stop timer and fire off an event. */
                    m_timer.stop();
                    m_paused = true;
                    m_elapsedTime = m_timeLimit;

                    if (m_state != QuizPhaseEnum.S_CORRECT_ANSWER &&
                            m_state != QuizPhaseEnum.S_WRONG_ANSWER)
                    {
                        // Play the "Time's up!" sound
                        m_soundPlayer.loadSound(SoundIDEnum.QUESTION_TIME_UP,
                                SoundPlayer.EFFECTS_CHANNEL, false);
                        m_soundPlayer.start(SoundPlayer.EFFECTS_CHANNEL);

                        // Set the question timer expired event
                        m_statePacket.setType(QuizStateEnum.QUESTION_TIMER_EXPIRED);

                        m_packetProcessor.processStatePacket(m_statePacket);    // Do it
                    }
                }

                m_eventPacket.setType(QuizUpdateEnum.UPDATE_QUESTION_CLOCK);
                m_eventPacket.setQuestionTimerTime(m_elapsedTime);

                m_packetProcessor.processEventPacket(m_eventPacket);    // Do it
            }
        }
        
        private Timer  m_timer;
        
        private int  m_timeLimit;
        
        private int  m_elapsedTime;
        
        private boolean  m_paused;
    }

    static QuizController  m_QuizControllerSingleton = null;        // Singleton
    
    private GameTimer  m_gameTimer;
    
    private QuizPhaseEnum  m_state;               /* Current game phase */

    private StatePacket     m_statePacket;
    private EventPacket     m_eventPacket;
    
    private PacketProcessor m_packetProcessor;

    private GameModeEnum  m_gameMode = GameModeEnum.STANDALONE;     /* Standalone/master/slave node */
    
    private JDesktopPane  m_desktopPane;
    
    private QuestionTimer  m_questionTimer;
    
    private GameConfig  m_gameConfig;
    
    private QuizState  m_quizState;

    private jQuizShow.util.SoundPlayer  m_soundPlayer;
    
    private int  m_questionTimerLimit;       // Number of seconds for question countdown timer
    
    private int  m_selectedIndex;         /* Index of selected answer */

    private int  m_numberOfQuestions;       // Max. number of possible levels (questions)
    
    private int  m_numAnswersDisplayed;     /* Index of last answer displayed */
    
    private int  m_maxNumberOfAnswers;      /* Max. number of possible answers */
   
    private int  m_currentLevel;        /* Current question level */
    
    private QuestionList  m_questionList;   /* Question list */
    
    private Question  m_question;       /* Current question */
    private Answer[]  m_answers;        /* Current possible answers */
;
    
    private SoundIDEnum  m_bkgSoundId;		/* Current bkg sound ID */

}
