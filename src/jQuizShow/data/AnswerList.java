/*
 * AnswerList.java
 *
 * Created on March 2, 2001, 9:59 AM
 *
 * $Id: AnswerList.java,v 1.1 2004/04/02 06:01:59 sdchen Exp $
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
 *    $Log: AnswerList.java,v $
 *    Revision 1.1  2004/04/02 06:01:59  sdchen
 *    Snapshot of jQuizShow 1.1 alpha06 development
 *
 *    Revision 1.1.1.1  2002/04/13 17:01:12  sdchen
 *    Initial import of the jQuizShow sources from local development directory.
 *
 *
 */

package jQuizShow.data;


import java.util.*;


/**
 *
 * @author  Steve Chen
 * @version $Version$
 */
public class AnswerList {

    /** Creates new AnswerList */
    public AnswerList()
    {
        m_list = new ArrayList(4);
    }

    public void addAnswer(Answer  answer)
    {
        m_list.add(answer);
    }

    
    /**
     * Sets the isCorrect flag of the correct answer.
     */
    public void  setCorrectAnswer(int  index)
    {
        if (index < 0 || index >= m_list.size())
            return;
        
        for (int  i = 0; i < m_list.size(); i++)
        {
            Answer  answer = (Answer) m_list.get(i);
            
            if (i == index)
                answer.setCorrect(true);
            else
                answer.setCorrect(false);
        }
        
        return;
    }

    
    /**
     * Returns a shuffled array of possible answers.
     */
    public Answer[]  getPossibleAnswers()
    {
        ArrayList       answers;
        
        if (m_list.isEmpty())
            throw new NoSuchElementException();

        answers = (ArrayList) m_list.clone();
        Collections.shuffle(answers);
        
        return ((Answer[]) answers.toArray(new Answer[0]));
    }

    /**
     * Returns an array of answers in the order they were specified in
     * the database.
     */
    public Answer[]  getOrderedAnswers()
    {
        ArrayList       answers;
        
        if (m_list.isEmpty())
            throw new NoSuchElementException();

        answers = (ArrayList) m_list.clone();
        
        return ((Answer[]) answers.toArray(new Answer[0]));
    }

    
    /*
     * Class variable definitions
     */
    
    private ArrayList       m_list;        //** An array of Answers
    private int             m_correctIndex = 0;     // Index of correct answer (default = 0)
}
