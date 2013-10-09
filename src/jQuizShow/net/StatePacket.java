/*
 * StatePacket.java
 *
 * Created on August 15, 2001, 6:44 PM
 *
 * $Id: StatePacket.java,v 1.1 2004/04/02 06:01:59 sdchen Exp $
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
 *    $Log: StatePacket.java,v $
 *    Revision 1.1  2004/04/02 06:01:59  sdchen
 *    Snapshot of jQuizShow 1.1 alpha06 development
 *
 *    Revision 1.1.1.1  2002/04/13 17:01:45  sdchen
 *    Initial import of the jQuizShow sources from local development directory.
 *
 *
 */

package jQuizShow.net;

/**
 *
 * @author  Steven D. Chen
 * @version 1.0
 */

import java.io.*;
import java.util.*;

import jQuizShow.*;
import jQuizShow.event.*;

public class StatePacket implements Serializable {
    
    /** Creates new SetupPacket */
    public StatePacket() {
    }

    /*
     * Accessors
     */
    public GameStateChangeEnum  getType() {
        return m_gameStateEnum;
    }

    public int  getMaxNumberOfLevels() {
        return m_maxNumberOfLevels;
    }

    public int  getCurrentLevel() {
        return m_currentLevel;
    }

    public String  getLevelTitle() {
        return m_levelTitle;
    }
    
    public String  getQuestionID() {
        return m_questionID;
    }
    
    public String  getQuestion() {
        return m_question;
    }

    public String[]  getAnswers() {
        return m_answers;
    }

    public int  getQuestionTimerLimit() {
        return m_questionTimerLimit;
    }

    public int  getLifelineTimerLimit() {
        return m_lifelineTimerLimit;
    }

    public BitSet getAnswersVisible() {
        return m_answersVisible;
    }

    public int getCorrectAnswer() {
        return m_correctAnswer;
    }

    
    /*
     * Manipulators
     */
    
    public void  setType(GameStateChangeEnum  type) {
        m_gameStateEnum = type;
    }
    
    public void  setMaxNumberOfLevels(int  max) {
        m_maxNumberOfLevels = max;
    }

    public void  setCurrentLevel(int  level) {
        m_currentLevel = level;
    }

    public void  setLevelTitle(String  title) {
        m_levelTitle = title;
    }
    
    public void  setQuestionID(String  id) {
        m_questionID = id;
    }
    
    public void  setQuestion(String  question) {
        m_question = question;
    }

    public void  setAnswers(String[]  answers) {
        m_answers = answers;
    }

    public void  setQuestionTimerLimit(int  seconds) {
        m_questionTimerLimit = seconds;
    }

    public void  setLifelineTimerLimit(int  seconds) {
        m_lifelineTimerLimit = seconds;
    }

    public void setAnswersVisible(BitSet  answersVisible) {
        m_answersVisible = answersVisible;
    }

    public void setCorrectAnswer(int  index) {
        m_correctAnswer = index;
    }
    
    private GameStateChangeEnum  m_gameStateEnum;
    
    private int  m_maxNumberOfLevels;
    private int  m_currentLevel;

    private String  m_questionID;
    private String  m_question;
    private String[]  m_answers;

    private String  m_levelTitle;
    
    private int  m_questionTimerLimit;

    private int  m_lifelineTimerLimit;

    private BitSet  m_answersVisible;

    private int  m_correctAnswer;
}
