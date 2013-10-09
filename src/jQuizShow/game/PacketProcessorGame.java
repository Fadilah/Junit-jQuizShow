/*
 * PacketProcessorGame.java
 *
 * Created on November 3, 2001, 10:09 PM
 *
 * $Id: PacketProcessorGame.java,v 1.1 2004/04/02 06:01:59 sdchen Exp $
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
 *    $Log: PacketProcessorGame.java,v $
 *    Revision 1.1  2004/04/02 06:01:59  sdchen
 *    Snapshot of jQuizShow 1.1 alpha06 development
 *
 *    Revision 1.1.1.1  2002/04/13 17:01:33  sdchen
 *    Initial import of the jQuizShow sources from local development directory.
 *
 *
 */

package jQuizShow.game;

/**
 *
 * @author  Steven D. Chen
 * @version 
 */

import jQuizShow.*;
import jQuizShow.event.*;
import jQuizShow.net.*;

public class PacketProcessorGame extends PacketProcessor
{
    /**
     * Initializes this Singleton class.  It must be called once prior to
     * the initial use in order to properly set the getInstance() method
     * of the parent class.
     */
    public static void  initialize()
    {
        if (m_singletonPacketProcessorGame == null)
            m_singletonPacketProcessorGame = new PacketProcessorGame();
        
        PacketProcessor.setInstance(m_singletonPacketProcessorGame);
        
        return;
    }
    
    /**
     * Creates new PacketProcessorGame (private)
     */
    private PacketProcessorGame() {
        m_gameState = GameState.getInstance(false);
        m_gameConfig = GameConfig.getInstance();
    }

    
    /** Process the received game state changed packet.  This is sent
     * at the start of a round and contains all the information
     * needed for the round.
     */
    public void processStatePacket(StatePacket  info)
    {
        String[]  answers;
        
        GameStateEnum  type = (GameStateEnum) info.getType();

        boolean  debugOn = (m_gameConfig.getIntConfig("debugMode") & GameConfig.DEBUG_NETWORK_SETUP) != 0;

        m_gameState.setGameStateEnum(type);
        
        answers = info.getAnswers();
        
        if (debugOn)
        {
            System.out.println("--> PacketProcessorGame::processStatePacket(" + type + ")");
        }

        // Process special game states
        if (type == GameStateEnum.NEW_GAME)
        {
            m_gameState.setMaxNumberOfLevels(info.getMaxNumberOfLevels());
            m_gameState.setCurrentLevel(info.getCurrentLevel());

            if ((m_gameConfig.getIntConfig("debugMode") & GameConfig.DEBUG_NETWORK_SETUP) != 0)
                System.out.println("  Max number of levels = " + info.getMaxNumberOfLevels());
        }
        else if (type == GameStateEnum.WAIT_TO_START_ROUND)
        {
            m_gameState.setCurrentLevel(info.getCurrentLevel());
            m_gameState.setAnswersVisible(info.getAnswersVisible());
            m_gameState.setLevelTitle(info.getLevelTitle());
            m_gameState.setStatusMessage(null);

            if ((m_gameConfig.getIntConfig("debugMode") & GameConfig.DEBUG_NETWORK_SETUP) != 0)
                System.out.println("  Current level = " + info.getCurrentLevel());
        }
        else if (type == GameStateEnum.DISPLAY_QUESTION)
        {
            m_gameState.setCurrentQuestion(info.getQuestion(), answers);

            if (debugOn)
            {
                System.out.println("  Question # " + info.getCurrentLevel() + ": " + info.getQuestion());

                for (int  i = 0; i < answers.length; i++)
                    System.out.println("    Answer " + i + " = " + answers[i]);

                System.out.println("  Question timer limit = " + info.getQuestionTimerLimit());
            }
        }
        else if (type == GameStateEnum.DISPLAY_ANSWER)
        {
            m_gameState.setAnswersVisible(info.getAnswersVisible());
        }
        else if (type == GameStateEnum.WAIT_FOR_ANSWER)
        {
            m_gameState.setToggleState(GameState.ANSWERS_SELECTABLE, true);
        }
        else if (type == GameStateEnum.WAIT_TO_REVEAL_ANSWER)
        {
            m_gameState.setToggleState(GameState.ANSWERS_SELECTABLE, false);
        }
        else if (type == GameStateEnum.ANSWER_WAS_CORRECT)
        {
            m_gameState.setCorrectAnswer(info.getCorrectAnswer());

            if (debugOn)
                System.out.println("  Correct answer = " + info.getCorrectAnswer());
        }
        else if (type == GameStateEnum.ANSWER_WAS_WRONG)
        {
            m_gameState.setCorrectAnswer(info.getCorrectAnswer());
            m_gameState.setCurrentLevel(info.getCurrentLevel());

            if (debugOn)
                System.out.println("  Correct answer = " + info.getCorrectAnswer());
        }
        else if (type == GameStateEnum.GRAND_PRIZE_WON)
        {
        }
        else if (type == GameStateEnum.TRANSITION_LEVEL)
        {
            m_gameState.setCurrentLevel(info.getCurrentLevel());
            m_gameState.setAnswersVisible(info.getAnswersVisible());
            m_gameState.setLevelTitle(info.getLevelTitle());
            m_gameState.setCurrentQuestion(info.getQuestion(), answers);

            if ((m_gameConfig.getIntConfig("debugMode") & GameConfig.DEBUG_NETWORK_SETUP) != 0)
                System.out.println("  Current level = " + info.getCurrentLevel());
        }
        else if (type == GameStateEnum.WALKAWAY)
        {
            m_gameState.setCorrectAnswer(info.getCorrectAnswer());
            m_gameState.setCurrentLevel(info.getCurrentLevel());

            if (debugOn)
                System.out.println("  Correct answer = " + info.getCorrectAnswer());
        }
        else if (type == GameStateEnum.LIFELINE_SELECTED)
        {
        }
        else if (type == GameStateEnum.FIFTY_FIFTY)
        {
            m_gameState.setAnswersVisible(info.getAnswersVisible());
        }
        else if (type == GameStateEnum.PHONE_A_FRIEND)
        {
            m_gameState.setLifelineTimerLimit(info.getLifelineTimerLimit());
            m_gameState.setLifelineTimerTime(0);

            m_gameState.setToggleState(GameState.LIFELINE_TIMER_SHOWN, true);
            
            if (debugOn)
                System.out.println("  Lifeline timer limit = " + info.getLifelineTimerLimit());
        }
        else if (type == GameStateEnum.ASK_THE_AUDIENCE)
        {
        }
        else if (type == GameStateEnum.LIFELINE_END)
        {
            m_gameState.setToggleState(GameState.LIFELINE_TIMER_SHOWN, false);
        }
        else if (type == GameStateEnum.SET_CURRENT_LEVEL)
        {
            m_gameState.setCurrentLevel(info.getCurrentLevel());
        }
        else if (type == GameStateEnum.SET_QUESTION_TIMER_LIMIT)
        {
            m_gameState.setQuestionTimerLimit(info.getQuestionTimerLimit());
        }
        else if (type == GameStateEnum.END_OF_GAME)
        {
        }
        else
        {
            throw new UnsupportedOperationException("Unhandled GameStateEnum = " + type);
        }
        
        // Fire off notification of this state change
        m_gameState.fireGameStateChangeEvent(this, info.getType());
    }

    /** Process the received game update event packet.
     */
    public void processEventPacket(EventPacket  info)
    {
        GameUpdateEnumGame         type = (GameUpdateEnumGame) info.getType();

        boolean  debugOn = (m_gameConfig.getIntConfig("debugMode") & GameConfig.DEBUG_NETWORK_SETUP) != 0;
        
        if (debugOn)
        {
            if (type != GameUpdateEnumGame.UPDATE_QUESTION_CLOCK &&
                    type != GameUpdateEnumGame.UPDATE_LIFELINE_CLOCK)
            {
                System.out.println("--> PacketProcessorGame::processEventPacket(" + type + ")");
            }
        }

        // Process special event states
        if (type == GameUpdateEnumGame.UPDATE_QUESTION_CLOCK)
        {
            m_gameState.setQuestionTimerTime(info.getQuestionTimerTime());
        }
        else if (type == GameUpdateEnumGame.UPDATE_LIFELINE_CLOCK)
        {
            m_gameState.setLifelineTimerTime(info.getLifelineTimerTime());
        }
        else if (type == GameUpdateEnumGame.TOGGLE_STATE)
        {
            m_gameState.setToggleStates(info.getToggleStates());
        }
        else if (type == GameUpdateEnumGame.HIGHLIGHT_LEVEL)
        {
            m_gameState.setHighlightLevel(info.getHighlightLevel());

            if (debugOn)
                System.out.println("  Highlight level = " + info.getLifelineTimerTime());
        }
        else if (type == GameUpdateEnumGame.SELECTED_ANSWER)
        {
            m_gameState.setSelectedAnswer(info.getSelectedAnswer());
            
            if (debugOn)
                System.out.println("  Selected answer = " + info.getSelectedAnswer());
        }
        else if (type == GameUpdateEnumGame.SELECTED_LIFELINE)
        {
            m_gameState.setSelectedLifeline(info.getSelectedLifeline());
            
            if (debugOn)
                System.out.println("  Selected lifeline = " + info.getSelectedLifeline());
        }
        else if (type == GameUpdateEnumGame.TRANSITION_MSG)
        {
            m_gameState.setTransitionMessage(info.getTransitionMessage());
            
            if (debugOn)
                System.out.println("  Transition message: " + info.getTransitionMessage());
        }
        else if (type == GameUpdateEnumGame.STATUS_MSG)
        {
            m_gameState.setStatusMessage(info.getStatusMessage());
            
            if (debugOn)
                System.out.println("  Status message: " + info.getStatusMessage());
        }
        else
        {
            throw new UnsupportedOperationException("Unhandled GameUpdateEnumGame = " + type);
        }
        
        // Fire off notification of this event
        m_gameState.fireGameUpdateEvent(this, type);
    }

    /** Process the received game update event packet.
     */
    public void processModeChangePacket(ModeChangePacket  info)
    {
        GameModeEnum  mode = info.getMode();
        
        if ((m_gameConfig.getIntConfig("debugMode") & GameConfig.DEBUG_NETWORK_EVENTS) != 0)
        {
            System.out.println("--> GameState::processModeChangePacket(" + mode + ")");
        }

        // Process special event states

        
        // Fire off notification of this event
        m_gameState.fireGameModeChangeEvent(this, mode);
    }

    private static PacketProcessorGame     m_singletonPacketProcessorGame;

    private GameState  m_gameState;
    private GameConfig  m_gameConfig;
}
