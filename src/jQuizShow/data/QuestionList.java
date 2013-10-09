/*
 * QuestionList.java
 *
 * Created on March 2, 2001, 9:59 AM
 *
 * $Id: QuestionList.java,v 1.1 2004/04/02 06:01:59 sdchen Exp $
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
 *    $Log: QuestionList.java,v $
 *    Revision 1.1  2004/04/02 06:01:59  sdchen
 *    Snapshot of jQuizShow 1.1 alpha06 development
 *
 *    Revision 1.1.1.1  2002/04/13 17:01:28  sdchen
 *    Initial import of the jQuizShow sources from local development directory.
 *
 *
 */

package jQuizShow.data;


import java.util.*;


/**
 * The QuestionList manages the database of questions and their possible
 * answers.  A QuestionList can contain multiple sub-lists. These sub-lists
 * are used to organize the questions into their difficulty levels, as used by
 * the jQuizShow game modes.
 *
 * @author  Steve Chen
 *
 */
public class QuestionList {

    /** Creates new QuestionList */
    public QuestionList()
    {
        m_lists = new ArrayList(m_numLists);
        
        for (int i = 0; i < m_numLists; i++)
            m_lists.add(new ArrayList(100));
    }

    
    /**
     * Adds the Question to the database. It is added to the appropriate
     * level sub-list based on the Question's difficulty level.
     */
    public void addQuestion(Question  question)
    {
        // Check if difficult level is greater than the current number
        // of lists of questions.  If it is, grow the number of
        // lists to cover this number.
        int     difficulty = question.getDifficulty();
        
        if (difficulty >= m_numLists)
        {
            // Grow the list
            for (int i = m_numLists; i <= difficulty; i++)
                m_lists.add(new ArrayList(100));
            
            m_numLists = difficulty;
        }
        
        ArrayList       list;
        
        list = (ArrayList) m_lists.get(difficulty);
        list.add(question);
    }

    /**
     * For the specified difficulty, get the indexed question from the
     * database.  This is used in quiz mode when the order of the
     * questions have meaning and must be preserved.  This does NOT
     * alter the contents of the database.
     */
    public Question  getQuestion(int  difficulty, int  questionIndex)
    {
        ArrayList       list;
        
        if (difficulty < 0 || difficulty >= m_numLists)
            throw new ArrayIndexOutOfBoundsException();
        
        list = (ArrayList) m_lists.get(difficulty);
        
        if (questionIndex >= list.size())
            throw new NoSuchElementException();
        
        return (Question) list.get(questionIndex);
    }

    /**
     * Gets the next question of the specified difficulty from the
     * question database.  NOTE:  This also removes the question from
     * the database.
     */
    public Question  getQuestion(int  difficulty)
    {
        ArrayList       list;
        
        if (difficulty < 0 || difficulty >= m_numLists)
            throw new ArrayIndexOutOfBoundsException();
        
        list = (ArrayList) m_lists.get(difficulty);
        
        if (list.isEmpty())
            throw new NoSuchElementException();
        
        return (Question) list.remove(0);
    }

    /**
     * Number of difficulty lists.
     */
    public int  getNumLists()
    {
        return m_numLists;
    }

    
    /**
     * Returns the number of questions remaining for the given difficulty
     * level.
     */
    public int  getNumQuestionsRemaining(int  difficulty)
    {
        ArrayList       list;
        
        if (difficulty < 0 || difficulty >= m_numLists)
            throw new ArrayIndexOutOfBoundsException();
        
        list = (ArrayList) m_lists.get(difficulty);
        
        return (list.size());
    }

    /**
     * Shuffles the questions in each of the difficulty levels.  This is
     * typically executed after the question database is loaded (game mode).
     */
    public void  shuffleQuestions()
    {
        for (int  difficulty = 0; difficulty < m_numLists; difficulty++)
        {
            ArrayList       list;
            
            list = (ArrayList) m_lists.get(difficulty);
            Collections.shuffle(list);
        }
    }

    
    /*
     * Class variable definitions
     */
    
    private ArrayList       m_lists;        //** An array of lists of questions
    
    private int             m_numLists = 5;
}
