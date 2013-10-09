/*
 * GameController.java
 *
 * Created on April 29, 2001, 8:56 PM
 *
 * $Id: GameController.java,v 1.2 2007/02/05 03:55:48 sdchen Exp $
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
 *    $Log: GameController.java,v $
 *    Revision 1.2  2007/02/05 03:55:48  sdchen
 *    Removed CR (Ctrl-M) from lines.
 *
 *    Revision 1.1  2004/04/02 06:01:59  sdchen
 *    Snapshot of jQuizShow 1.1 alpha06 development
 *
 *    Revision 1.7  2002/08/15 04:43:26  sdchen
 *    Internationalization of source code.  Main.getMessage() is the primary
 *    routine to get the localized message strings.
 *
 *    Revision 1.6  2002/07/06 06:13:38  sdchen
 *    Completed implementation of a workaround for Java2 1.4 KeyEvent processing
 *    change.  With Java2 1.4, the GameScreen must have focus in order for
 *    jQuizShow to receive keyboard events.  This is a change from v1.3 behavior
 *    which passed uncaught KeyEvents up to the parent classes (Main's JFrame).
 *
 *    Revision 1.5  2002/06/23 04:18:38  sdchen
 *    Changed game control key definitions to only use standard ASCII
 *    characters.  Supports non-101+ key keyboards (e.g. laptops) and
 *    also moves toward support for i18n.
 *
 *    Revision 1.4  2002/06/06 05:25:29  sdchen
 *    Changed to display a dialog if no more questions for current difficulty
 *    level.  Also, output countdown of num questions remaining to the
 *    main status label.
 *
 *    Revision 1.3  2002/06/04 02:53:09  sdchen
 *    Added code to play SHOW_QUESTION and SHOW_ANSWER sounds in the appropriate
 *    locations.
 *
 *    Revision 1.2  2002/05/28 00:29:42  sdchen
 *    Added question file database name to message output.
 *    Moved "difficultyBreakpoint" output to DEBUG_BASIC level.
 *
 *    Revision 1.1.1.1  2002/04/13 17:01:19  sdchen
 *    Initial import of the jQuizShow sources from local development directory.
 *
 *
 */

package jQuizShow.game.classic;

/** Primary event manager class for the classic game mode. This class
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
import jQuizShow.game.*;
import jQuizShow.net.*;
import jQuizShow.util.*;
import jQuizShow.event.*;

public class GameController
        implements
            GameModeChangeListener,
            GameStateChangeListener,
            GameInputListener,
            GameTimerListener
{
    /** Gets the GameController singleton */
    public static GameController  getInstance()
    {
        if (m_gameControllerSingleton == null)
            m_gameControllerSingleton = new GameController();
        
        return m_gameControllerSingleton;
    }
    
    /** Creates new GameController */
    private GameController()
    {
        // Instantiate the QuestionTimer inner class
        m_questionTimer = new QuestionTimer();

        m_desktopPane = Main.getDesktopPane();
        
        m_gameState = GameState.getInstance(false);

        // Add self as listeners to the required events
        m_gameState.addGameModeChangeListener(this);
        m_gameState.addGameStateChangeListener(this);
        m_gameState.addGameInputListener(this);

        // Set the default game state
        m_state = GamePhaseEnum.S_INACTIVE;
        
        // Get the GameConfig singleton instance
        m_gameConfig = GameConfig.getInstance();

        // Get the SoundPlayer singleton
        m_soundPlayer = SoundPlayer.getInstance();

        // Get the default question timer limit from the Config
        m_questionTimerLimit = m_gameConfig.getIntConfig("questionTimerLimit", 60);

        // Get the max number of possible answers
        m_maxNumberOfAnswers = m_gameState.getMaxNumberOfAnswers();
        
        // Get the max number of levels
        m_maxNumberOfLevels = m_gameConfig.getNumLevels();
        
        // Get the level titles
        m_titles = new String[m_maxNumberOfLevels + 1];
        
        for (int  i = 1; i <= m_maxNumberOfLevels; i++)
            m_titles[i - 1] = Main.getMessage("msg_trying_for")
	    		+ "  " + m_gameConfig.getConfig("levelName" + i,
			Main.getMessage("msg_level") + " " + i);

        m_titles[m_maxNumberOfLevels] = Main.getMessage("msg_congratulations");

        // Get the max lifeline timer time
        m_lifelineTimerLimit = m_gameConfig.getIntConfig("lifelineTimerLimit", 30);
        
        // Create instances of the StatePacket and EventPacket.
        m_statePacket = new StatePacket();
        m_eventPacket = new EventPacket();
        
        // Get the PacketProcessor instance
        m_packetProcessor = PacketProcessor.getInstance();

        /* Make a local copy of the transition levels */
	int  numTransitions = m_gameConfig.getIntConfig("numTransitions");

        m_transitions = new int[numTransitions];

	for (int  i = 1; i <= numTransitions; i++)
	    m_transitions[i-1] = m_gameConfig.getIntConfig("transitionLevel" + i);

        // Start at first level
        m_currentLevel = 0;

        // Initialize the final level (>= 0 when win/lose)
        m_finalLevel = -1;
    }


    /** Load questions from the specified database file
     */
    public int  loadQuestions(String  filename)
            throws FileNotFoundException, IOException
    {
        // Load the questions from the Q & A database
        String  questionFilepath = null;
        QuestionLoader  qLoader;

        int  numQuestions = 0;
        
        String  encoding = m_gameConfig.getConfig("questionEncoding");

        String  usedQuestionFilename = m_gameConfig.getConfig("usedQuestionFile", "usedQuestions.txt");

        m_questionList = null;          // Clear current questions
        
        questionFilepath = FileUtils.searchForFile(filename);

	System.out.println(Main.getMessage("msg_reading_questions")
		+ "  " + questionFilepath);

	qLoader = QuestionLoader.getInstance();
        qLoader.setUsedQuestionFilename(usedQuestionFilename);
	qLoader.setEncoding(encoding);
        qLoader.setDebugMode(m_gameConfig.getIntConfig("debugMode"));

        m_questionList = qLoader.readQuestions(questionFilepath);

	System.out.println(Main.getMessage("msg_game_questions_read"));

        /* Get local copies of the difficulty breakpoint information defined
         * in the configuration file. */
        try
        {
            m_numBreakpoints = m_gameConfig.getIntConfig("numDifficultyBreakpoints");

            m_breakpoints = new int[m_numBreakpoints];

            for (int  i = 1; i <= m_numBreakpoints; i++)
            {
                m_breakpoints[i-1] = m_gameConfig.getIntConfig("difficultyBreakpoint" + i);

		if ((m_gameConfig.getIntConfig("debugMode") & GameConfig.DEBUG_BASIC) != 0)
		    System.out.println("    difficultyBreakpoint" + i + " = " + m_breakpoints[i-1]);
            }
        }
        catch (NumberFormatException  e)
        {
            System.out.println(Main.getMessage("msg_invalid_breakpoint"));
            
            m_numBreakpoints = m_questionList.getNumLists();

            m_breakpoints = new int[m_numBreakpoints];

            for (int  i = 1; i <= m_numBreakpoints; i++)
            {
                m_breakpoints[i-1] = m_maxNumberOfLevels / (m_numBreakpoints + 1) * i;
                
		if ((m_gameConfig.getIntConfig("debugMode") & GameConfig.DEBUG_BASIC) != 0)
		    System.out.println("    difficultyBreakpoint" + i + " = " + m_breakpoints[i-1]);
            }
        }
        
        // Count the total number of questions
        for (int  difficulty = 0; difficulty <= m_numBreakpoints; difficulty++)
        {
            numQuestions += m_questionList.getNumQuestionsRemaining(difficulty);
        }
        
        return numQuestions;
    }
    
    
    /** Start a new game */
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
        m_gameState.setToggleState(GameState.ANSWERS_SELECTABLE, false);
        m_gameState.setToggleState(GameState.ASK_THE_AUDIENCE, true);
        m_gameState.setToggleState(GameState.FIFTY_FIFTY, true);
        m_gameState.setToggleState(GameState.PHONE_A_FRIEND, true);
        m_gameState.setToggleState(GameState.SCORES_SHOWN, true);
        m_gameState.setToggleState(GameState.TRANSITION_SCREEN_SHOWN, true);
        m_gameState.setToggleState(GameState.LIFELINE_TIMER_SHOWN, false);

        m_eventPacket.setType(GameUpdateEnumGame.TOGGLE_STATE);
        m_eventPacket.setToggleStates(m_gameState.getToggleStates());
        
        m_packetProcessor.processEventPacket(m_eventPacket);    // Do it

        /* Set the question timer limit */
        m_statePacket.setType(GameStateEnum.SET_QUESTION_TIMER_LIMIT);
        m_statePacket.setQuestionTimerLimit(m_questionTimerLimit);

        m_packetProcessor.processStatePacket(m_statePacket);    // Do it

        /* Initialize for a new game */
        m_statePacket.setType(GameStateEnum.NEW_GAME);
        m_statePacket.setMaxNumberOfLevels(m_maxNumberOfLevels);
        m_statePacket.setCurrentLevel(0);

        m_packetProcessor.processStatePacket(m_statePacket);    // Do it

        // Set the starting message
        m_eventPacket.setType(GameUpdateEnumGame.TRANSITION_MSG);
        m_eventPacket.setTransitionMessage(Main.getMessage("msg_ready"));

        m_packetProcessor.processEventPacket(m_eventPacket);    // Do it

        /* Clear the status text */
        m_eventPacket.setType(GameUpdateEnumGame.STATUS_MSG);
        m_eventPacket.setStatusMessage("");

        m_packetProcessor.processEventPacket(m_eventPacket);    // Do it

        // Reset the level being attempted
        m_eventPacket.setType(GameUpdateEnumGame.HIGHLIGHT_LEVEL);
        m_eventPacket.setHighlightLevel(0);

        m_packetProcessor.processEventPacket(m_eventPacket);    // Do it

        // Initialize the final level (>= 0 when win/lose)
        m_finalLevel = -1;

        // Stop all sounds
        m_soundPlayer.stop(SoundPlayer.ALL_CHANNELS);
        
        // Play new player sound
        m_soundPlayer.loadSound(SoundIDEnum.NEW_PLAYER,
                SoundPlayer.FOREGROUND_CHANNEL, false);
        m_soundPlayer.start(SoundPlayer.FOREGROUND_CHANNEL);
        
        // Set the initial phase
        m_state = GamePhaseEnum.S_WAITING_TO_START;
    }
    
    
    /** Stops (aborts) the game. */
    public void stopGame()
    {
        // Stop the game - go to inactive mode
        m_state = GamePhaseEnum.S_INACTIVE;

        // Stop all sounds
        m_soundPlayer.stop(SoundPlayer.ALL_CHANNELS);

        // Inform everyone this is the end of the game
        m_statePacket.setType(GameStateEnum.END_OF_GAME);

        m_packetProcessor.processStatePacket(m_statePacket);    // Do it
    }
    
    
    /** Discards the current in-game question */
    public void discardQuestion(int  newLevel)
    {
        if (newLevel >= 0 && newLevel < m_maxNumberOfLevels)
            m_currentLevel = newLevel;

        if (m_currentLevel == m_maxNumberOfLevels)
            m_currentLevel = m_maxNumberOfLevels - 1;   // Can't redo winning level
        
        /* Initialize the final level (>= 0 when win/lose) */
        m_finalLevel = -1;

        /* Set the initial phase */
        m_state = GamePhaseEnum.S_WAITING_TO_START;

        nextPhase();
    }

    
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
    }    


    public void gameStateChanged(GameStateChangeEvent evt)
    {
        GameStateEnum  type = (GameStateEnum) evt.getType();
        
        if (type == GameStateEnum.SET_QUESTION_TIMER_LIMIT)
        {
            // Get the limit from the global instance
            m_questionTimerLimit = GameState.getInstance(false).getQuestionTimerLimit();
            
            // Set the question timer limit
            m_questionTimer.setupTimer(m_questionTimerLimit, 0);
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
            if (m_state == GamePhaseEnum.S_ANSWERS_DISPLAYED ||
                    m_state == GamePhaseEnum.S_ANSWER_SELECTED)
            {
                /* An answer was selected.  Get its index. */
                m_selectedIndex = evt.getSelectedIndex();

                if (m_gameState.getAnswerVisible(m_selectedIndex) == true)
                {
                    m_state = GamePhaseEnum.S_ANSWER_SELECTED;

                    /* Transition to next phase */
                    nextPhase();
                }
            }
        }
        else if (evtType == GameInputEnum.LIFELINE_SELECTED)
        {
            if (m_state == GamePhaseEnum.S_ANSWERS_DISPLAYED || m_state == GamePhaseEnum.S_ANSWER_SELECTED ||
                    m_state == GamePhaseEnum.S_CONFIRM_LIFELINE)
            {
                int  lifeline = evt.getSelectedIndex();
                
                if (lifeline == GameState.FIFTY_FIFTY ||
                        lifeline == GameState.ASK_THE_AUDIENCE ||
                        lifeline == GameState.PHONE_A_FRIEND)
                {
                    // Valid lifeline.  Check if active.
                    if (m_gameState.getToggleState(lifeline) == true)
                    {
                        m_lifelineSelected = lifeline;

                        // Show lifeline selected
                        m_eventPacket.setType(GameUpdateEnumGame.SELECTED_LIFELINE);
                        m_eventPacket.setSelectedLifeline(m_lifelineSelected);
                        
                        m_packetProcessor.processEventPacket(m_eventPacket);    // Do it
                        
                        m_state = GamePhaseEnum.S_CONFIRM_LIFELINE;
                        nextPhase();
                    }
                }
            }
        }
        else if (evtType == GameInputEnum.KEY_TYPED)
        {
            char  keyChar = Character.toLowerCase(evt.getKeyChar());
            
            if (m_answers != null && keyChar >= 'a' && keyChar <= ('a' + m_answers.length - 1))
            {
                if (m_state == GamePhaseEnum.S_ANSWERS_DISPLAYED ||
                        m_state == GamePhaseEnum.S_ANSWER_SELECTED)
                {
                    /* An answer was selected.  Get its index. */
                    m_selectedIndex = keyChar - 'a';

                    if (m_gameState.getAnswerVisible(m_selectedIndex) == true)
                    {
                        m_state = GamePhaseEnum.S_ANSWER_SELECTED;

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
                    
                    case '`' :         // Back-quote - Toggle score screen
                    {
                        /* Toggle the visibility of the game score panel */
                        m_gameState.setToggleState(GameState.SCORES_SHOWN, ! m_gameState.getToggleState(GameState.SCORES_SHOWN));

                        m_eventPacket.setType(GameUpdateEnumGame.TOGGLE_STATE);
                        m_eventPacket.setToggleStates(m_gameState.getToggleStates());
                        
                        m_packetProcessor.processEventPacket(m_eventPacket);    // Do it

                        m_soundPlayer.loadSound(SoundIDEnum.SHOW_SCORE_SCREEN,
                                SoundPlayer.FOREGROUND_CHANNEL, false);
                        m_soundPlayer.start(SoundPlayer.FOREGROUND_CHANNEL);
                        break;
                    }

                    case '\n' :         // Enter key -- "Final answer"
                    {
                        /* Confirm key -- validity depending on the current game state
                         * and context (e.g. if an answer is selected)
                         */
                        if (m_state == GamePhaseEnum.S_ANSWER_SELECTED)
                        {
                            m_state = GamePhaseEnum.S_ANSWER_FINALIZED;
                            nextPhase();
                        }
                        else if (m_state == GamePhaseEnum.S_CONFIRM_LIFELINE)
                        {
                            m_state = GamePhaseEnum.S_LIFELINE_CONFIRMED;
                            nextPhase();
                        }
                        else if (m_state == GamePhaseEnum.S_PHONE_A_FRIEND_TIMER)
                        {
                            closeLifelineTimer();
                        }
                        break;
                    }

                    case '=' :         // Equals (=) -- Walk away
                    {
                        /* Check if the key is valid first, then ask for confirmation
                         * that the player wants to "walk away".
                         */
                        if (m_gameState.getToggleState(GameState.GAME_IN_SESSION) == true &&
                                (m_state == GamePhaseEnum.S_ANSWERS_DISPLAYED || m_state == GamePhaseEnum.S_ANSWER_SELECTED))
                        {
                            /* Ask to confirm the "Walk Away" */
                            if (JOptionPane.showConfirmDialog(m_desktopPane,
					Main.getMessage("msg_walk_away"),
                                        Main.getMessage("title_confirmation"),
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE) ==
                                    JOptionPane.YES_OPTION)
                            {
                                m_soundPlayer.stop(SoundPlayer.ALL_CHANNELS);

                                // Set the "game in session" flag false -- allow to
                                // still guess an answer and see if it is right, but
                                // no winnings and no sounds.
                                m_gameState.setToggleState(GameState.GAME_IN_SESSION, false);

                                m_eventPacket.setType(GameUpdateEnumGame.TOGGLE_STATE);
                                m_eventPacket.setToggleStates(m_gameState.getToggleStates());

                                m_packetProcessor.processEventPacket(m_eventPacket);    // Do it

                                // Set the final level variable to the current level
                                m_finalLevel = m_gameState.getCurrentLevel();

                                /* Highlight the walk-away winnings (one less than current) */
                                m_eventPacket.setType(GameUpdateEnumGame.HIGHLIGHT_LEVEL);
                                m_eventPacket.setHighlightLevel(m_finalLevel - 1);

                                m_packetProcessor.processEventPacket(m_eventPacket);    // Do it

                                // Set for next state
                                m_state = GamePhaseEnum.S_WALKAWAY;
                            }
                        }
                        break;
                    }

                    case '\b' :         // Backspace key
                    {
                        /* This key is used clear the selected lifeline or
			 * to backout a supposedly "final" answer.  The
			 * latter is against the "official" rules, but...
                         */
                        if (m_state == GamePhaseEnum.S_CORRECT_ANSWER ||
                                m_state == GamePhaseEnum.S_WRONG_ANSWER)
                        {
                            /* Re-enable selection */
                            m_gameState.setToggleState(GameState.ANSWERS_SELECTABLE, true);

                            m_eventPacket.setType(GameUpdateEnumGame.TOGGLE_STATE);
                            m_eventPacket.setToggleStates(m_gameState.getToggleStates());

                            m_packetProcessor.processEventPacket(m_eventPacket);    // Do it

                            // Clear the selection
                            m_eventPacket.setType(GameUpdateEnumGame.SELECTED_ANSWER);
                            m_eventPacket.setSelectedAnswer(-1);

                            m_packetProcessor.processEventPacket(m_eventPacket);    // Do it

                            m_state = GamePhaseEnum.S_ANSWERS_DISPLAYED;

                            m_questionTimer.startTimer();        // Restart the question timer

                            nextPhase();
                        }
                        else if (m_state == GamePhaseEnum.S_CONFIRM_LIFELINE)
                        {
                            m_state = GamePhaseEnum.S_LIFELINE_ABORTED;
                            nextPhase();
                        }
                        break;
                    }

                    case ' ' :          // Space key -- next phase
                    {
                        /* "Transition to next phase" key */
                        nextPhase();
                        break;
                    }

                    case ',' :       // Comma (,) = fifty-fifty
                    {
                        if (m_state == GamePhaseEnum.S_ANSWERS_DISPLAYED || m_state == GamePhaseEnum.S_ANSWER_SELECTED ||
                                m_state == GamePhaseEnum.S_CONFIRM_LIFELINE)
                        {
                            if (m_gameState.getToggleState(GameState.FIFTY_FIFTY) == true)
                            {
                                m_lifelineSelected = GameState.FIFTY_FIFTY;

                                m_eventPacket.setType(GameUpdateEnumGame.SELECTED_LIFELINE);
                                m_eventPacket.setSelectedLifeline(m_lifelineSelected);

                                m_packetProcessor.processEventPacket(m_eventPacket);    // Do it

                                m_state = GamePhaseEnum.S_CONFIRM_LIFELINE;
                                nextPhase();
                            }
                        }
                        break;
                    }

                    case '.' :       // Period (.) = phone a friend
                    {
                        if (m_state == GamePhaseEnum.S_ANSWERS_DISPLAYED || m_state == GamePhaseEnum.S_ANSWER_SELECTED ||
                                m_state == GamePhaseEnum.S_CONFIRM_LIFELINE)
                        {
                            if (m_gameState.getToggleState(GameState.PHONE_A_FRIEND) == true)
                            {
                                m_lifelineSelected = GameState.PHONE_A_FRIEND;

                                m_eventPacket.setType(GameUpdateEnumGame.SELECTED_LIFELINE);
                                m_eventPacket.setSelectedLifeline(m_lifelineSelected);

                                m_packetProcessor.processEventPacket(m_eventPacket);    // Do it

                                m_state = GamePhaseEnum.S_CONFIRM_LIFELINE;
                                nextPhase();
                            }
                        }
                        break;
                    }

                    case '/' :       // Forward slash (/) = poll the audience
                    {
                        if (m_state == GamePhaseEnum.S_ANSWERS_DISPLAYED || m_state == GamePhaseEnum.S_ANSWER_SELECTED ||
                                m_state == GamePhaseEnum.S_CONFIRM_LIFELINE)
                        {
                            if (m_gameState.getToggleState(GameState.ASK_THE_AUDIENCE) == true)
                            {
                                m_lifelineSelected = GameState.ASK_THE_AUDIENCE;

                                m_eventPacket.setType(GameUpdateEnumGame.SELECTED_LIFELINE);
                                m_eventPacket.setSelectedLifeline(m_lifelineSelected);

                                m_packetProcessor.processEventPacket(m_eventPacket);    // Do it

                                m_state = GamePhaseEnum.S_CONFIRM_LIFELINE;
                                nextPhase();
                            }
                        }
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
            System.out.println("--> GameController::nextPhase() = " + m_state);
        
        if (m_state == GamePhaseEnum.S_INACTIVE)
        {
            /* No change.  Must be set to an active state for a transition to occur */
        }
        else if (m_state == GamePhaseEnum.S_WAITING_TO_START)
        {
            /* Reset the question timer */
            m_questionTimer.setupTimer(-1, 0);

            // Set the "game in session" flag
            m_gameState.setToggleState(GameState.GAME_IN_SESSION, true);

            /* Highlight the level being attempted */
            m_eventPacket.setType(GameUpdateEnumGame.HIGHLIGHT_LEVEL);
            m_eventPacket.setHighlightLevel(m_currentLevel);

            m_packetProcessor.processEventPacket(m_eventPacket);    // Do it

            // Display the Question screen
            m_gameState.setToggleState(GameState.TRANSITION_SCREEN_SHOWN, false);

            m_eventPacket.setType(GameUpdateEnumGame.TOGGLE_STATE);
            m_eventPacket.setToggleStates(m_gameState.getToggleStates());
            
            m_packetProcessor.processEventPacket(m_eventPacket);

            int  transitionLevel;

            for (transitionLevel = 0; transitionLevel < m_transitions.length &&
                    m_currentLevel >= m_transitions[transitionLevel];
                    transitionLevel++)
            {
                /* DUMMY */;
            }

            if (transitionLevel > 0)
            {
                m_soundPlayer.loadSound(SoundIDEnum.SHOW_QUESTION,
                        SoundPlayer.FOREGROUND_CHANNEL, false);
                m_soundPlayer.start(SoundPlayer.FOREGROUND_CHANNEL);
            }

            SoundIDEnum  bkgSoundId = SoundIDEnum.BACKGROUND1;
            
            switch (transitionLevel)
            {
                case 0:
                    bkgSoundId = SoundIDEnum.BACKGROUND1;
                    break;
                    
                case 1:
                    bkgSoundId = SoundIDEnum.BACKGROUND2;
                    break;
                    
                case 2:
                    bkgSoundId = SoundIDEnum.BACKGROUND3;
                    break;
                    
                case 3:
                    bkgSoundId = SoundIDEnum.BACKGROUND4;
                    break;
                    
                case 4:
                    bkgSoundId = SoundIDEnum.BACKGROUND5;
                    break;
                    
                case 5:
                    bkgSoundId = SoundIDEnum.BACKGROUND6;
                    break;
                    
                default:
                    bkgSoundId = SoundIDEnum.BACKGROUND1;
                    break;
            }

            /* Start the background sound if it isn't playing */
            if (m_soundPlayer.isPlaying(SoundPlayer.BACKGROUND_CHANNEL) == false ||
                    m_bkgSoundId != bkgSoundId)
            {
                m_bkgSoundId = bkgSoundId;
                m_soundPlayer.loadSound(m_bkgSoundId,
                        SoundPlayer.BACKGROUND_CHANNEL, true);
                m_soundPlayer.start(SoundPlayer.BACKGROUND_CHANNEL);
            }

            // Hide all answers
            for (int  i = 0; i < m_maxNumberOfAnswers; i++)
            {
                m_gameState.setAnswerVisible(i, false);
            }

            // Fire an event to update the game

            m_statePacket.setType(GameStateEnum.WAIT_TO_START_ROUND);
            m_statePacket.setQuestion("");
            m_statePacket.setAnswers(null);
            
            m_statePacket.setAnswersVisible(m_gameState.getAnswersVisible());
            m_statePacket.setCurrentLevel(m_currentLevel);
            m_statePacket.setLevelTitle(m_titles[m_currentLevel]);
            
            m_packetProcessor.processStatePacket(m_statePacket);    // Do it
            
            m_state = GamePhaseEnum.S_STARTING_NEXT_ROUND;
        }
        else if (m_state == GamePhaseEnum.S_STARTING_NEXT_ROUND)
        {
            /* Start the next round.  First, get a question from the available
             * questions list */

            String[]  answerStr;        // Shuffled list of possible answers
            
            int  difficulty;

            for (difficulty = 0; difficulty < m_numBreakpoints &&
                    m_currentLevel >= m_breakpoints[difficulty]; difficulty++)
                /* DUMMY */;

            /* Reset the question timer */
            m_questionTimer.setupTimer(-1, 0);

            // Display the Question screen
            m_gameState.setToggleState(GameState.TRANSITION_SCREEN_SHOWN, false);

            m_eventPacket.setType(GameUpdateEnumGame.TOGGLE_STATE);
            m_eventPacket.setToggleStates(m_gameState.getToggleStates());

            m_packetProcessor.processEventPacket(m_eventPacket);    // Do it
            
            // Clear the transition text
            m_eventPacket.setType(GameUpdateEnumGame.TRANSITION_MSG);
            m_eventPacket.setTransitionMessage(null);

            m_packetProcessor.processEventPacket(m_eventPacket);    // Do it

            {
                int  numRemaining = m_questionList.getNumQuestionsRemaining(difficulty);

                if (numRemaining < 3)
                {
                    if (numRemaining == 0)
                    {
		        String  msg = Main.getMessage("err_no_more_questions")
                                + " " + difficulty;

                        System.out.println(msg);

			Main.setStatusLabel(msg);

			JOptionPane.showMessageDialog(m_desktopPane, msg);

                        m_state = GamePhaseEnum.S_INACTIVE;
                        
                        return;
                    }
                    else
                    {
			Object	args[] = 
			    	{
				    new Integer(difficulty),
				    new Integer(numRemaining)
				};

                        String  msg = Main.getMessage("warn_low_on_questions",
				args);

                        System.out.println(msg);

			Main.setStatusLabel(msg);
                    }
                }

                m_question = m_questionList.getQuestion(difficulty);

                /* Add the question to the used question file so it won't be
                 * reused.
                 */
                try
                {
                    m_gameState.addQuestionToUsedQuestionFile(m_question);
                }
                catch (java.io.IOException  e)
                {
                    System.out.println(Main.getMessage("warn_used_question_file"));
                }

                AnswerList  answers = m_question.getAnswers();
                
                /* Get the array of sorted/shuffled possible answers */
                m_answers = answers.getPossibleAnswers();

                answerStr = new String[m_answers.length];

                for (int  i = 0; i < m_answers.length; i++)
                {
                    answerStr[i] = m_answers[i].getAnswer();
                }
            }

            /* Zero the number of answers currently displayed counter. */
            m_numAnswersDisplayed = 0;

            /* Disable answer selections */
            m_gameState.setToggleState(GameState.ANSWERS_SELECTABLE, false);

            m_eventPacket.setType(GameUpdateEnumGame.TOGGLE_STATE);
            m_eventPacket.setToggleStates(m_gameState.getToggleStates());

            m_packetProcessor.processEventPacket(m_eventPacket);    // Do it

            /* Clear any selected answer index */
            m_eventPacket.setType(GameUpdateEnumGame.SELECTED_ANSWER);
            m_eventPacket.setSelectedAnswer(GameState.NO_SELECTED_ANSWER);

            m_packetProcessor.processEventPacket(m_eventPacket);    // Do it

            /* Play the "show question" sound */
	    m_soundPlayer.loadSound(SoundIDEnum.SHOW_QUESTION,
		    SoundPlayer.FOREGROUND_CHANNEL, false);
	    m_soundPlayer.start(SoundPlayer.FOREGROUND_CHANNEL);

            // Set the question and possible answers
            m_statePacket.setType(GameStateEnum.DISPLAY_QUESTION);
            m_statePacket.setQuestion(m_question.getQuestion());
            m_statePacket.setAnswers(answerStr);

            m_packetProcessor.processStatePacket(m_statePacket);    // Do it

            /* Switch state flag to next state */
            m_state = GamePhaseEnum.S_QUESTION_DISPLAYED;
        }
        else if (m_state == GamePhaseEnum.S_QUESTION_DISPLAYED ||
                m_state == GamePhaseEnum.S_DISPLAYING_ANSWERS)
        {
            /* Show the next answer */
            m_gameState.setAnswerVisible(m_numAnswersDisplayed, true);

            m_statePacket.setType(GameStateEnum.DISPLAY_ANSWER);
            m_statePacket.setAnswersVisible(m_gameState.getAnswersVisible());
            
            m_packetProcessor.processStatePacket(m_statePacket);    // Do it

            /* Play the "show answer" sound */
	    m_soundPlayer.loadSound(SoundIDEnum.SHOW_ANSWER,
		    SoundPlayer.FOREGROUND_CHANNEL, false);
	    m_soundPlayer.start(SoundPlayer.FOREGROUND_CHANNEL);
            
            if (++m_numAnswersDisplayed >= m_answers.length)
            {
                /* All answers now displayed. */
                m_statePacket.setType(GameStateEnum.WAIT_FOR_ANSWER);

                m_packetProcessor.processStatePacket(m_statePacket);    // Do it
                
                /* Start the timer, if used */
                m_questionTimer.startTimer();

                /* Switch state flag to next state */
                m_state = GamePhaseEnum.S_ANSWERS_DISPLAYED;
            }
        }
        else if (m_state == GamePhaseEnum.S_ANSWERS_DISPLAYED)
        {
            /* This state does not change until an answer is selected.
             * See the gameInputReceived() */
        }
        else if (m_state == GamePhaseEnum.S_ANSWER_SELECTED)
        {
            /* This state does not change until the Enter key is pressed.
             * See the gameInputReceived() */


            // Highlight the selection
            m_eventPacket.setType(GameUpdateEnumGame.SELECTED_ANSWER);
            m_eventPacket.setSelectedAnswer(m_selectedIndex);
            
            m_packetProcessor.processEventPacket(m_eventPacket);

            // Play selected sound
            m_soundPlayer.loadSound(SoundIDEnum.SELECTION_MADE, SoundPlayer.FOREGROUND_CHANNEL, false);
            m_soundPlayer.start(SoundPlayer.FOREGROUND_CHANNEL);
        }
        else if (m_state == GamePhaseEnum.S_ANSWER_FINALIZED)
        {
            if (m_gameState.getToggleState(GameState.GAME_IN_SESSION) == true)
            {
                /* Sound is only played if the game is active (not active if in "Walk away" mode) */
                m_soundPlayer.loadSound(SoundIDEnum.ANSWER_FINALIZED, SoundPlayer.FOREGROUND_CHANNEL, false);
                m_soundPlayer.start(SoundPlayer.FOREGROUND_CHANNEL);
            }

            /* Fire off an event to GameState listeners to update their state */
            m_gameState.fireGameStateChangeEvent(this, GameStateEnum.WAIT_TO_REVEAL_ANSWER);

             /* Check if the answer is correct or incorrect.  This method
             * sets the next phase based on the result.
             */
            processFinalAnswer();
        }
        else if (m_state == GamePhaseEnum.S_CORRECT_ANSWER)
        {
            m_questionTimer.stopTimer();        // Stop the question timer

            if (m_gameState.getToggleState(GameState.GAME_IN_SESSION) == true)
            {
                // Play the correct answer sound (only done if game is in session [not Walk Away mode])
                m_soundPlayer.loadSound(SoundIDEnum.ANSWER_CORRECT, SoundPlayer.FOREGROUND_CHANNEL, false);
                m_soundPlayer.start(SoundPlayer.FOREGROUND_CHANNEL);
            }

            // If there is a note associated with the question, display it now.
            String  note = m_question.getNote();
            
            if (note != null && note.length() > 0)
            {
                m_eventPacket.setType(GameUpdateEnumGame.STATUS_MSG);
                m_eventPacket.setStatusMessage(note);
                
                m_packetProcessor.processEventPacket(m_eventPacket);    // Do it
            }
            
            /* Fire off an event to GameState listeners to update their state */
            m_statePacket.setType(GameStateEnum.ANSWER_WAS_CORRECT);
            m_statePacket.setCorrectAnswer(m_selectedIndex);

            m_packetProcessor.processStatePacket(m_statePacket);    // Do it

            /* Switch state flag to next state */
            m_state = GamePhaseEnum.S_RESET_FOR_NEXT_ROUND;
        }
        else if (m_state == GamePhaseEnum.S_WRONG_ANSWER)
        {
            int     correctAnswerIndex = GameState.NO_CORRECT_ANSWER;
            
            m_questionTimer.stopTimer();        // Stop the question timer

            m_soundPlayer.stop(SoundPlayer.BACKGROUND_CHANNEL);

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
                m_eventPacket.setType(GameUpdateEnumGame.STATUS_MSG);
                m_eventPacket.setStatusMessage(note);
                
                m_packetProcessor.processEventPacket(m_eventPacket);    // Do it
            }

            {
                /* Set game not active */
                m_gameState.setToggleState(GameState.GAME_IN_SESSION, false);

                /* Set the finalLevel variable to the previous transition/goal level */
                int  questionNum = m_currentLevel;

                int  prevLevel = 0;

                for (int  transLevel = 0; transLevel < m_transitions.length; transLevel++)
                {
                    if (questionNum >= prevLevel && questionNum < m_transitions[transLevel])
                    {
                        if (transLevel > 0)
                            m_finalLevel = m_transitions[transLevel - 1];
                        else
                            m_finalLevel = 0;
                        break;
                    }

                    prevLevel = m_transitions[transLevel];
                }

                /* Set the ScoreScreen highlight bar to this level */
                m_eventPacket.setType(GameUpdateEnumGame.HIGHLIGHT_LEVEL);
                m_eventPacket.setHighlightLevel(m_finalLevel - 1);

                m_packetProcessor.processEventPacket(m_eventPacket);    // Do it

                /* Fire off an event to GameState listeners to update their state */
            }

            /* Fire off an event to GameState listeners to update their state */
            m_statePacket.setType(GameStateEnum.ANSWER_WAS_WRONG);
            m_statePacket.setCorrectAnswer(correctAnswerIndex);
            m_statePacket.setCurrentLevel(m_finalLevel);
            
            m_packetProcessor.processStatePacket(m_statePacket);    // Do it

            /* Switch state flag to next state */
            m_state = GamePhaseEnum.S_ENDING_GAME;
        }
        else if (m_state == GamePhaseEnum.S_RESET_FOR_NEXT_ROUND)
        {
            int  questionNum;

            /* If m_finalLevel >= 0, then the game is over/stopped. */
            if (m_finalLevel >= 0)
            {
                /* Set the question number to the final level number */
                questionNum = m_finalLevel;
            }
            else
            {
                /* Increment to next question */
                questionNum = ++m_currentLevel;
            }

            // Set the current level
            m_statePacket.setType(GameStateEnum.SET_CURRENT_LEVEL);
            m_statePacket.setCurrentLevel(questionNum);
            
            m_packetProcessor.processStatePacket(m_statePacket);    // Do it
            
            /* Set the Transition panel text to the current level title. */
            String  title = m_gameConfig.getConfig("levelName" + questionNum);

            if (title == null)
                title = "$ 0";

            // Send event to update the transition text
            m_eventPacket.setType(GameUpdateEnumGame.TRANSITION_MSG);
            m_eventPacket.setTransitionMessage(title);

            m_packetProcessor.processEventPacket(m_eventPacket);    // Do it

            // Send event to display the transition msg
            m_gameState.setToggleState(GameState.TRANSITION_SCREEN_SHOWN, true);

            m_eventPacket.setType(GameUpdateEnumGame.TOGGLE_STATE);
            m_eventPacket.setToggleStates(m_gameState.getToggleStates());

            m_packetProcessor.processEventPacket(m_eventPacket);    // Do it

            /* Reset the question timer */
            m_questionTimer.setupTimer(-1, 0);

            // Hide all answers
            for (int  i = 0; i < m_maxNumberOfAnswers; i++)
                m_gameState.setAnswerVisible(i, false);

            /* Fire off an event to GameState listeners to update their state */
            m_statePacket.setType(GameStateEnum.TRANSITION_LEVEL);
            m_statePacket.setCorrectAnswer(GameState.NO_CORRECT_ANSWER);
            m_statePacket.setQuestion("");
            m_statePacket.setAnswers(null);
            
            m_statePacket.setAnswersVisible(m_gameState.getAnswersVisible());
            m_statePacket.setLevelTitle(m_titles[m_currentLevel]);
            
            m_packetProcessor.processStatePacket(m_statePacket);    // Do it

            if (m_gameState.getToggleState(GameState.GAME_IN_SESSION) == true)
            {
                if (questionNum >= m_maxNumberOfLevels)
                {
                    /* Player has reached the top!  Stop background sound and play
                     * special sound.  Set game inactive.
                     */
                    m_soundPlayer.stop(SoundPlayer.BACKGROUND_CHANNEL);

                    m_soundPlayer.loadSound(SoundIDEnum.WINNER,
                            SoundPlayer.FOREGROUND_CHANNEL, false);
                    m_soundPlayer.start(SoundPlayer.FOREGROUND_CHANNEL);

                    m_gameState.setToggleState(GameState.GAME_IN_SESSION, false);
                }
                else
                {
                    /* Check if a transition point has been reached */
                    for (int  transLevel = 0; transLevel < m_transitions.length; transLevel++)
                    {
                        if (questionNum == m_transitions[transLevel])
                        {
                            // Major transition reached.  Stop background sound and play
                            // special sound.
                            m_soundPlayer.stop(SoundPlayer.BACKGROUND_CHANNEL);

                            m_soundPlayer.loadSound(SoundIDEnum.GOAL_REACHED,
                                    SoundPlayer.FOREGROUND_CHANNEL, false);
                            m_soundPlayer.start(SoundPlayer.FOREGROUND_CHANNEL);
                        }
                    }
                }
            }
            
            /* Switch state flag to next state */
            m_state = GamePhaseEnum.S_TRANSITION_PANEL_SHOWN;
        }
        else if (m_state == GamePhaseEnum.S_WALKAWAY)
        {
            // In "Walkaway" mode -- display the correct answer
            int     correctAnswerIndex = GameState.NO_CORRECT_ANSWER;

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
                m_eventPacket.setType(GameUpdateEnumGame.STATUS_MSG);
                m_eventPacket.setStatusMessage(note);
                
                m_packetProcessor.processEventPacket(m_eventPacket);    // Do it
            }

            /* Fire off an event to GameState listeners to update their state */
            m_statePacket.setType(GameStateEnum.WALKAWAY);
            m_statePacket.setCorrectAnswer(correctAnswerIndex);
            m_statePacket.setCurrentLevel(m_finalLevel);
            
            m_packetProcessor.processStatePacket(m_statePacket);    // Do it
            
            // Switch state flag to next state
            m_state = GamePhaseEnum.S_ENDING_GAME;
        }   
        else if (m_state == GamePhaseEnum.S_ENDING_GAME)
        {
            // Ending game -- Display the winnings and play "Winner" sound


            // Set the current level
            m_statePacket.setType(GameStateEnum.SET_CURRENT_LEVEL);
            m_statePacket.setCurrentLevel(m_finalLevel);
            
            m_packetProcessor.processStatePacket(m_statePacket);    // Do it
            
            /* Set the Transition panel text to the current level title. */
            String  title = m_gameConfig.getConfig("levelName" + m_finalLevel);

            if (title == null)
                title = "$ 0";

            // Send event to update the transition text
            m_eventPacket.setType(GameUpdateEnumGame.TRANSITION_MSG);
            m_eventPacket.setTransitionMessage(title);

            m_packetProcessor.processEventPacket(m_eventPacket);    // Do it

            // Send event to display the transition msg
            m_gameState.setToggleState(GameState.TRANSITION_SCREEN_SHOWN, true);

            m_eventPacket.setType(GameUpdateEnumGame.TOGGLE_STATE);
            m_eventPacket.setToggleStates(m_gameState.getToggleStates());

            m_packetProcessor.processEventPacket(m_eventPacket);    // Do it

            // Play the "Winner" sound
            m_soundPlayer.loadSound(SoundIDEnum.WINNER,
                    SoundPlayer.FOREGROUND_CHANNEL, false);
            m_soundPlayer.start(SoundPlayer.FOREGROUND_CHANNEL);

            // Switch state flag to next state
            m_state = GamePhaseEnum.S_TRANSITION_PANEL_SHOWN;
        }
        else if (m_state == GamePhaseEnum.S_TRANSITION_PANEL_SHOWN)
        {
            // Clear the status message
            m_eventPacket.setType(GameUpdateEnumGame.STATUS_MSG);
            m_eventPacket.setStatusMessage("");
            
            m_packetProcessor.processEventPacket(m_eventPacket);    // Do it

            if (m_gameState.getToggleState(GameState.GAME_IN_SESSION) == true)
            {
                // Transition to the next level
                m_state = GamePhaseEnum.S_WAITING_TO_START;
                nextPhase();
            }
            else
            {
                // Game over - go to inactive mode
                m_state = GamePhaseEnum.S_INACTIVE;

                // Stop all sounds
                m_soundPlayer.stop(SoundPlayer.ALL_CHANNELS);

                // Inform everyone this is the end of the game
                m_statePacket.setType(GameStateEnum.END_OF_GAME);

                m_packetProcessor.processStatePacket(m_statePacket);    // Do it
            }
        }
        else if (m_state == GamePhaseEnum.S_CONFIRM_LIFELINE)
        {
            /* Unselect any currently selected answers */
            m_eventPacket.setType(GameUpdateEnumGame.SELECTED_ANSWER);
            m_eventPacket.setSelectedAnswer(-1);

            m_packetProcessor.processEventPacket(m_eventPacket);    // Do it

            /* Disable answer selection until abort of choice or lifeline used.
             */
            m_gameState.setToggleState(GameState.ANSWERS_SELECTABLE, false);

            m_eventPacket.setType(GameUpdateEnumGame.TOGGLE_STATE);
            m_eventPacket.setToggleStates(m_gameState.getToggleStates());
            
            m_packetProcessor.processEventPacket(m_eventPacket);    // Do it

            /* Highlight the selected lifeline icon */
            m_eventPacket.setType(GameUpdateEnumGame.SELECTED_LIFELINE);
            m_eventPacket.setSelectedLifeline(m_lifelineSelected);
            
            m_packetProcessor.processEventPacket(m_eventPacket);    // Do it
        }
        else if (m_state == GamePhaseEnum.S_LIFELINE_ABORTED)
        {
            /* Unhighlight the selected lifeline */
            m_eventPacket.setType(GameUpdateEnumGame.SELECTED_LIFELINE);
            m_eventPacket.setSelectedLifeline(GameState.NO_SELECTED_LIFELINE);
            
            m_packetProcessor.processEventPacket(m_eventPacket);    // Do it

            /* Re-enable answer selection. */
            m_gameState.setToggleState(GameState.ANSWERS_SELECTABLE, true);

            m_eventPacket.setType(GameUpdateEnumGame.TOGGLE_STATE);
            m_eventPacket.setToggleStates(m_gameState.getToggleStates());
            
            m_packetProcessor.processEventPacket(m_eventPacket);    // Do it

            m_state = GamePhaseEnum.S_ANSWERS_DISPLAYED;
        }
        else if (m_state == GamePhaseEnum.S_LIFELINE_CONFIRMED)
        {
            /* Pause the question timer */
            m_questionTimer.stopTimer();

            switch (m_lifelineSelected)
            {
                case GameState.FIFTY_FIFTY :
                {
                    performFiftyFifty();
                }
                break;

                case GameState.PHONE_A_FRIEND :
                {
                    performPhoneAFriend();
                }
                break;

                case GameState.ASK_THE_AUDIENCE :
                {
                    performAskTheAudience();
                }
                break;
            }
        }
        else if (m_state == GamePhaseEnum.S_PHONE_A_FRIEND_TIMER)
        {
            /* This state is executed when the Phone-a-friend timer is
             * shown.  This state does not change until the Scroll Lock
             * key is pressed.  See the gameActionPerformed() function.
             */

            /* Stop/start the timer. */
            if (m_lifelineTimer.isPaused())
                m_lifelineTimer.startTimer();
            else
                m_lifelineTimer.stopTimer();
        }
        else if (m_state == GamePhaseEnum.S_WAITING_FOR_POLL_COMPLETION)
        {
            /* The player selected "Ask the audience".  The waiting
             * sound is playing until the Space bar is hit to transition
             * here.  Stop the sound and disable the lifeline.
             */

            m_soundPlayer.loadSound(SoundIDEnum.ASK_THE_AUDIENCE_END,
                    SoundPlayer.EFFECTS_CHANNEL, false);

            m_soundPlayer.stop(SoundPlayer.FOREGROUND_CHANNEL);
            m_soundPlayer.start(SoundPlayer.EFFECTS_CHANNEL);

            m_state = GamePhaseEnum.S_LIFELINE_USED;
            nextPhase();
        }
        else if (m_state == GamePhaseEnum.S_LIFELINE_USED)
        {
            /* Unhighlight the selected lifeline */
            m_eventPacket.setType(GameUpdateEnumGame.SELECTED_LIFELINE);
            m_eventPacket.setSelectedLifeline(GameState.NO_SELECTED_LIFELINE);
            
            m_packetProcessor.processEventPacket(m_eventPacket);    // Do it

            /* Disable the used lifeline */
            m_gameState.setToggleState(m_lifelineSelected, false);

            /* Re-enable answer selection. */
            m_gameState.setToggleState(GameState.ANSWERS_SELECTABLE, true);

            m_eventPacket.setType(GameUpdateEnumGame.TOGGLE_STATE);
            m_eventPacket.setToggleStates(m_gameState.getToggleStates());
            
            m_packetProcessor.processEventPacket(m_eventPacket);    // Do it

            /* Resume the question timer */
            m_questionTimer.startTimer();

            /* Resume the background sound */
            m_soundPlayer.start(SoundPlayer.BACKGROUND_CHANNEL);

            // Send the lifeline end state
            m_statePacket.setType(GameStateEnum.LIFELINE_END);

            m_packetProcessor.processStatePacket(m_statePacket);    // Do it

            m_state = GamePhaseEnum.S_ANSWERS_DISPLAYED;
        }
    }
    
    
    private void  performFiftyFifty()
    {
        m_soundPlayer.loadSound(SoundIDEnum.FIFTY_FIFTY_LIFELINE, SoundPlayer.FOREGROUND_CHANNEL, false);
        m_soundPlayer.start(SoundPlayer.FOREGROUND_CHANNEL);

        /* Randomly eliminate two of the three wrong answers,
         * leaving one right and one wrong answer.  This
         * code randomly chooses the wrong answer to keep,
         * effectively yielding the same results. */
	java.util.Random  rand = new java.util.Random();

        int  wrongAnswerToLeave = rand.nextInt(3) + 1;  // 1, 2 or 3

        for (int  i = 0; i < m_answers.length; i++)
        {
            if (m_answers[i].getCorrect())
                continue;       // Skip the correct answer
            else
                wrongAnswerToLeave--;

            if (wrongAnswerToLeave == 0)
                continue;       // This is the wrong answer to keep
            else
                m_gameState.setAnswerVisible(i, false);
        }

        // Send the Fifty-fifty state
        m_statePacket.setType(GameStateEnum.FIFTY_FIFTY);
        m_statePacket.setAnswersVisible(m_gameState.getAnswersVisible());
        
        m_packetProcessor.processStatePacket(m_statePacket);    // Do it

        m_state = GamePhaseEnum.S_LIFELINE_USED;

        nextPhase();
    }
    

    private void  performPhoneAFriend()
    {
        m_soundPlayer.stop(SoundPlayer.FOREGROUND_CHANNEL);
        m_soundPlayer.stop(SoundPlayer.BACKGROUND_CHANNEL);

	/* Play the Phone-a-friend start sound */
        m_soundPlayer.loadSound(SoundIDEnum.PHONE_A_FRIEND_LIFELINE,
		SoundPlayer.FOREGROUND_CHANNEL, false);
	m_soundPlayer.start(SoundPlayer.FOREGROUND_CHANNEL);

        /* Pre-load the clock tick sound into the effects channel */
        m_soundPlayer.loadSound(SoundIDEnum.CLOCK_TICK, SoundPlayer.EFFECTS_CHANNEL,
                false);

	/* Clear the "clock-tick" loaded flag so that the tick sound
	 * will be loaded the first time it is played.
	 */
	m_clockTickLoaded = false;
        
        if (m_lifelineTimer == null)
        {
            m_lifelineTimer = new GameTimer();
            m_lifelineTimer.addGameTimerListener(this);
        }
        
        m_lifelineTimer.setTimer(m_lifelineTimerLimit, 0, false);

        m_statePacket.setType(GameStateEnum.PHONE_A_FRIEND);
        m_statePacket.setLifelineTimerLimit(m_lifelineTimerLimit);
        
        m_packetProcessor.processStatePacket(m_statePacket);
        
        m_state = GamePhaseEnum.S_PHONE_A_FRIEND_TIMER;
    }

    /** Called when a game timer event occurs  */
    public void gameTimerActionPerformed(GameTimerEvent evt) {
        if (evt.getType() == GameTimerEvent.TIMER_ELAPSED)
        {
            // Update the lifeline clock
            m_eventPacket.setType(GameUpdateEnumGame.UPDATE_LIFELINE_CLOCK);
            m_eventPacket.setLifelineTimerTime(m_lifelineTimer.getTime());

            m_packetProcessor.processEventPacket(m_eventPacket);    // Do it

            // Play the "Time's up!" sound
            m_soundPlayer.loadSound(SoundIDEnum.LIFELINE_TIME_UP,
                    SoundPlayer.EFFECTS_CHANNEL, false);
            m_soundPlayer.start(SoundPlayer.EFFECTS_CHANNEL);
        }
    }
    
    /** Called whenever each second while timer is running  */
    public void gameTimerOneSecond(GameTimerEvent evt)
    {
        // Update the lifeline clock
        m_eventPacket.setType(GameUpdateEnumGame.UPDATE_LIFELINE_CLOCK);
        m_eventPacket.setLifelineTimerTime(m_lifelineTimer.getTime());
        
        m_packetProcessor.processEventPacket(m_eventPacket);    // Do it

        m_soundPlayer.start(SoundPlayer.EFFECTS_CHANNEL);
    }

    private void  closeLifelineTimer()
    {
        m_soundPlayer.stop(SoundPlayer.ALL_CHANNELS);

        m_lifelineTimer.stopTimer();
        
        m_state = GamePhaseEnum.S_LIFELINE_USED;
        nextPhase();
    }
    
    private void  performAskTheAudience()
    {
        m_soundPlayer.stop(SoundPlayer.ALL_CHANNELS);
        
        m_soundPlayer.loadSound(SoundIDEnum.ASK_THE_AUDIENCE_LIFELINE,
	        SoundPlayer.FOREGROUND_CHANNEL, true);
        m_soundPlayer.start(SoundPlayer.FOREGROUND_CHANNEL);

        // Send the Ask The Audience state
        m_statePacket.setType(GameStateEnum.ASK_THE_AUDIENCE);
        
        m_packetProcessor.processStatePacket(m_statePacket);    // Do it
        
        m_state = GamePhaseEnum.S_WAITING_FOR_POLL_COMPLETION;
    }
    
    
    /** Called when the player confirms the selected answer is the final
     * answer. */
    private void processFinalAnswer()
    {
        if (m_answers[m_selectedIndex].getCorrect())
            m_state = GamePhaseEnum.S_CORRECT_ANSWER;
        else
            m_state = GamePhaseEnum.S_WRONG_ANSWER;

        return;
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
            m_timer.stop();

            // If timeLimit >= 0, use its value, else use previous limit
            if (timeLimit >= 0)
                m_timeLimit = timeLimit;
            
            m_elapsedTime = currentTime;

            /* Sanity checks */
            if (m_timeLimit < 0)
                m_timeLimit = 0;

            if (currentTime > m_timeLimit)
                m_elapsedTime = m_timeLimit;

            m_eventPacket.setType(GameUpdateEnumGame.UPDATE_QUESTION_CLOCK);
            m_eventPacket.setQuestionTimerTime(m_elapsedTime);
            
            m_packetProcessor.processEventPacket(m_eventPacket);    // Do it
        }
    
        /** Starts the timer running */
        public void startTimer()
        {
            if (m_timeLimit == 0)
                return;
            
            m_timer.setInitialDelay(1000);
            m_timer.start();
            m_paused = false;

            m_eventPacket.setType(GameUpdateEnumGame.UPDATE_QUESTION_CLOCK);
            m_eventPacket.setQuestionTimerTime(m_elapsedTime);
            
            m_packetProcessor.processEventPacket(m_eventPacket);    // Do it
        }

        /** Stops/pauses the timer */
        public void stopTimer()
        {
            m_timer.stop();
            m_paused = true;

            m_eventPacket.setType(GameUpdateEnumGame.UPDATE_QUESTION_CLOCK);
            m_eventPacket.setQuestionTimerTime(m_elapsedTime);
            
            m_packetProcessor.processEventPacket(m_eventPacket);    // Do it
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
        
        public void actionPerformed(ActionEvent  evt)
        {
            if (m_timeLimit == 0)
                return;
            
            if (evt.getSource() == m_timer)
            {
                m_elapsedTime++;

                if (m_elapsedTime >= m_timeLimit)
                {
                    /* Time elapsed.  Stop timer and fire off an event. */
                    m_timer.stop();
                    m_paused = true;
                    m_elapsedTime = m_timeLimit;

                    if (m_state != GamePhaseEnum.S_CORRECT_ANSWER &&
                            m_state != GamePhaseEnum.S_WRONG_ANSWER)
                    {
                        // Play the "Time's up!" sound
                        m_soundPlayer.loadSound(SoundIDEnum.QUESTION_TIME_UP,
                                SoundPlayer.EFFECTS_CHANNEL, false);
                        m_soundPlayer.start(SoundPlayer.EFFECTS_CHANNEL);
                    }
                }

                m_eventPacket.setType(GameUpdateEnumGame.UPDATE_QUESTION_CLOCK);
                m_eventPacket.setQuestionTimerTime(m_elapsedTime);

                m_packetProcessor.processEventPacket(m_eventPacket);    // Do it
            }
        }
        
        private Timer  m_timer;
        
        private int  m_timeLimit;
        
        private int  m_elapsedTime;
        
        private boolean  m_paused;
    }

    static GameController  m_gameControllerSingleton = null;        // Singleton
    
    private GamePhaseEnum  m_state;               /* Current game phase */

    private StatePacket     m_statePacket;
    private EventPacket     m_eventPacket;
    
    private PacketProcessor m_packetProcessor;

    private GameModeEnum  m_gameMode = GameModeEnum.STANDALONE;     /* Standalone/master/slave node */
    
    private JDesktopPane  m_desktopPane;
    
    private QuestionTimer  m_questionTimer;
    private int  m_questionTimerLimit;
    
    private GameConfig  m_gameConfig;
    
    private GameState  m_gameState;

    private jQuizShow.util.SoundPlayer  m_soundPlayer;
    
    private GameTimer  m_lifelineTimer;
    private int  m_lifelineTimerLimit;   // Max time for lifeline timer
    
    private int  m_selectedIndex;         /* Index of selected answer */
    
    private int  m_lifelineSelected;        /* Index of lifeline that was selected */

    private int  m_maxNumberOfLevels;       // Max. number of possible levels (questions)
    
    private int  m_numAnswersDisplayed;     /* Index of last answer displayed */

    private int  m_finalLevel;          /* Set to level # that player ends with */
    
    private int  m_maxNumberOfAnswers;      /* Max. number of possible answers */
   
    private int  m_currentLevel;        /* Current question level */
    
    private QuestionList  m_questionList;   /* Question list */
    
    private Question  m_question;       /* Current question */
    private Answer[]  m_answers;        /* Current possible answers */
    private int  m_correctAnswer;      /* Index of correct answer */
    
    private int  m_numBreakpoints;    /* Number of difficulty levels */
    private int[]  m_breakpoints;     /* Level numbers at which difficulty changes */

    private int[]  m_transitions;	/* Transition levels */
    private SoundIDEnum  m_bkgSoundId;		/* Current bkg sound ID */

    private String[]  m_titles;     /* Level titles */

    private boolean  m_clockTickLoaded;
}
