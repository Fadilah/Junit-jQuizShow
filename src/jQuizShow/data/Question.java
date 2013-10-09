/*
 * Question.java
 *
 * Created on October 29, 2000, 8:30 PM
 *
 * $Id: Question.java,v 1.1 2004/04/02 06:01:59 sdchen Exp $
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
 *    $Log: Question.java,v $
 *    Revision 1.1  2004/04/02 06:01:59  sdchen
 *    Snapshot of jQuizShow 1.1 alpha06 development
 *
 *    Revision 1.1.1.1  2002/04/13 17:01:27  sdchen
 *    Initial import of the jQuizShow sources from local development directory.
 *
 *
 */


package jQuizShow.data;


/**
 *
 * @author  Steve
 * @version 
 */
public class Question extends Object {

    /** Creates new Question */
    public Question() {
        m_answers = new AnswerList();
    }

    public Question(String  question, int  difficulty) {
        m_answers = new AnswerList();
        setQuestion(question);
        m_difficulty = difficulty;
    }

    public Question(String  question, int  difficulty, AnswerList  answers) {
        m_answers = new AnswerList();
        setQuestion(question);
        m_difficulty = difficulty;
        m_answers = answers;
    }

    /**
     * Get the question identifier.  In the Quiz mode, it is the
     * "question number" that is displayed.
     */
    public String getQuestionID() {
        return m_questionID;
    }

    /**
     * Get the question string.
     */
    public String getQuestion() {
        return m_question;
    }

    /**
     * Get the reference note associated with the question, if any.
     */
    public String getNote() {
        return m_note;
    }
    
    /**
     * Gets the list of possible answers for this question.
     */
    public AnswerList getAnswers() {
        return m_answers;
    }

    
    public void setQuestionID(String  id) {
        m_questionID = id;
    }
    
    public void setQuestion(String  question, int difficulty) {
        setQuestion(question);    
        m_difficulty = difficulty;
    }
    
    public void setQuestion(String  question) {
        m_question = question;
    }
    
    public void setNote(String  note) {
        m_note = note;
    }
    
    public void addAnswer(Answer  answer) {
        m_answers.addAnswer(answer);
    }
    
    public String toString() {
        return getQuestion();
    }
    
    public void setDifficulty(int  difficulty)
    {
        m_difficulty = difficulty;
    }
    
    public int getDifficulty()
    {
        return (m_difficulty);
    }
    
    private String      m_questionID = null;
    private String      m_question = null;
    private String      m_note = null;
    private AnswerList  m_answers = null;
    private int         m_difficulty = 0;     //** Difficulty level (0-x)
}
