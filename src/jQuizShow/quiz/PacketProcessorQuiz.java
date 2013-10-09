/*
 * PacketProcessorQuiz.java
 *
 * Created on November 3, 2001, 10:09 PM
 *
 * $Id: PacketProcessorQuiz.java,v 1.1 2004/04/02 06:02:00 sdchen Exp $
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
 *    $Log: PacketProcessorQuiz.java,v $
 *    Revision 1.1  2004/04/02 06:02:00  sdchen
 *    Snapshot of jQuizShow 1.1 alpha06 development
 *
 *
 */

package jQuizShow.quiz;

/**
 *
 * @author  Steven D. Chen
 * @version 
 */

import jQuizShow.*;
import jQuizShow.event.*;
import jQuizShow.net.*;

public class PacketProcessorQuiz extends PacketProcessor
{
    /**
     * Initializes this Singleton class.  It must be called once prior to
     * the initial use in order to properly set the getInstance() method
     * of the parent class.
     */
    public static void  initialize()
    {
        if (m_singletonPacketProcessorGame == null)
            m_singletonPacketProcessorGame = new PacketProcessorQuiz();
        
        PacketProcessor.setInstance(m_singletonPacketProcessorGame);
        
        return;
    }
    
    /**
     * Creates new PacketProcessorQuiz (private)
     */
    private PacketProcessorQuiz() {
        m_gameState = QuizState.getInstance(false);
        m_gameConfig = GameConfig.getInstance();
    }

    
    /** Process the received game state changed packet.  This is sent
     * at the start of a round and contains all the information
     * needed for the round.
     */
    public void processStatePacket(StatePacket  info)
    {
        String[]  answers;
        
        QuizStateEnum  type = (QuizStateEnum) info.getType();

        boolean  debugOn = (m_gameConfig.getIntConfig("debugMode") & GameConfig.DEBUG_NETWORK_SETUP) != 0;

        m_gameState.setGameStateEnum(type);
        
        answers = info.getAnswers();
        
        if (debugOn)
        {
            System.out.println("--> PacketProcessorQuiz::processStatePacket(" + type + ")");
        }

        // Process special game states
        if (type == QuizStateEnum.NEW_GAME)
        {
            m_gameState.setMaxNumberOfLevels(info.getMaxNumberOfLevels());
            m_gameState.setCurrentLevel(info.getCurrentLevel());

            if ((m_gameConfig.getIntConfig("debugMode") & GameConfig.DEBUG_NETWORK_SETUP) != 0)
                System.out.println("  Max number of levels = " + info.getMaxNumberOfLevels());
        }
        else if (type == QuizStateEnum.WAIT_TO_START_ROUND)
        {
            m_gameState.setCurrentLevel(info.getCurrentLevel());
            m_gameState.setAnswersVisible(info.getAnswersVisible());
            m_gameState.setLevelTitle(info.getLevelTitle());
            m_gameState.setStatusMessage(null);
            m_gameState.setQuestionID(info.getQuestionID());

            if ((m_gameConfig.getIntConfig("debugMode") & GameConfig.DEBUG_NETWORK_SETUP) != 0)
                System.out.println("  Current level = " + info.getCurrentLevel());
        }
        else if (type == QuizStateEnum.DISPLAY_QUESTION)
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
        else if (type == QuizStateEnum.DISPLAY_ANSWER)
        {
            m_gameState.setAnswersVisible(info.getAnswersVisible());
        }
        else if (type == QuizStateEnum.WAIT_FOR_ANSWER)
        {
            m_gameState.setToggleState(QuizState.ANSWERS_SELECTABLE, true);
        }
        else if (type == QuizStateEnum.WAIT_TO_REVEAL_ANSWER)
        {
            m_gameState.setToggleState(QuizState.ANSWERS_SELECTABLE, false);
        }
        else if (type == QuizStateEnum.ANSWER_WAS_CORRECT)
        {
            m_gameState.setCorrectAnswer(info.getCorrectAnswer());

            if (debugOn)
                System.out.println("  Correct answer = " + info.getCorrectAnswer());
        }
        else if (type == QuizStateEnum.ANSWER_WAS_WRONG)
        {
            m_gameState.setCorrectAnswer(info.getCorrectAnswer());
            m_gameState.setCurrentLevel(info.getCurrentLevel());

            if (debugOn)
                System.out.println("  Correct answer = " + info.getCorrectAnswer());
        }
        else if (type == QuizStateEnum.SET_CURRENT_LEVEL)
        {
            m_gameState.setCurrentLevel(info.getCurrentLevel());
        }
        else if (type == QuizStateEnum.SET_QUESTION_TIMER_LIMIT)
        {
            m_gameState.setQuestionTimerLimit(info.getQuestionTimerLimit());
        }
        else if (type == QuizStateEnum.END_OF_GAME)
        {
        }
        else if (type == QuizStateEnum.QUESTION_TIMER_EXPIRED)
        {
        }
        else if (type == QuizStateEnum.SET_SHOW_ANSWERS_MODE)
        {
        }
        else
        {
            throw new UnsupportedOperationException("Unhandled QuizStateEnum = " + type);
        }
        
        // Fire off notification of this state change
        m_gameState.fireGameStateChangeEvent(this, info.getType());
    }

    /** Process the received game update event packet.
     */
    public void processEventPacket(EventPacket  info)
    {
        QuizUpdateEnum         type = (QuizUpdateEnum) info.getType();

        boolean  debugOn = (m_gameConfig.getIntConfig("debugMode") & GameConfig.DEBUG_NETWORK_SETUP) != 0;
        
        if (debugOn)
        {
            if (type != QuizUpdateEnum.UPDATE_QUESTION_CLOCK &&
                    type != QuizUpdateEnum.UPDATE_LIFELINE_CLOCK)
            {
                System.out.println("--> PacketProcessorQuiz::processEventPacket(" + type + ")");
            }
        }

        // Process special event states
        if (type == QuizUpdateEnum.UPDATE_QUESTION_CLOCK)
        {
            m_gameState.setQuestionTimerTime(info.getQuestionTimerTime());
        }
        else if (type == QuizUpdateEnum.TOGGLE_STATE)
        {
            m_gameState.setToggleStates(info.getToggleStates());
        }
        else if (type == QuizUpdateEnum.SELECTED_ANSWER)
        {
            m_gameState.setSelectedAnswer(info.getSelectedAnswer());
            
            if (debugOn)
                System.out.println("  Selected answer = " + info.getSelectedAnswer());
        }
        else if (type == QuizUpdateEnum.TRANSITION_MSG)
        {
            m_gameState.setTransitionMessage(info.getTransitionMessage());
            
            if (debugOn)
                System.out.println("  Transition message: " + info.getTransitionMessage());
        }
        else if (type == QuizUpdateEnum.STATUS_MSG)
        {
            m_gameState.setStatusMessage(info.getStatusMessage());
            
            if (debugOn)
                System.out.println("  Status message: " + info.getStatusMessage());
        }
        else
        {
            throw new UnsupportedOperationException("Unhandled QuizUpdateEnum = " + type);
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
            System.out.println("--> QuizState::processModeChangePacket(" + mode + ")");
        }

        // Process special event states

        
        // Fire off notification of this event
        m_gameState.fireGameModeChangeEvent(this, mode);
    }

    private static PacketProcessorQuiz     m_singletonPacketProcessorGame;

    private QuizState  m_gameState;
    private GameConfig  m_gameConfig;
}
