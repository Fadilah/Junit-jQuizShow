/*
 * QuizLoader.java
 *
 * Created on February 7, 2004, 9:56 PM
 *
 * $Id: QuizLoader.java,v 1.2 2007/02/05 04:53:57 sdchen Exp $
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
 *    $Log: QuizLoader.java,v $
 *    Revision 1.2  2007/02/05 04:53:57  sdchen
 *    Removed end-of-line CR (Ctrl-M) from source code.
 *
 *    Revision 1.1  2004/04/02 06:01:59  sdchen
 *    Snapshot of jQuizShow 1.1 alpha06 development
 *
 *
 */

package jQuizShow.data;

/**
    Loads the quiz from the specified local file or URL.
*/

import java.io.*;
import java.net.*;
import java.util.*;

import jQuizShow.*;
import jQuizShow.data.*;
import jQuizShow.util.*;

public class QuizLoader
{
    private static final int INVALID_QUESTION_FILE = -1;
    private static final int QUESTION_ADDED = 0;
    private static final int BLANK_LINE = 1;
    private static final int INVALID_INDEX = 2;
    private static final int NOT_ENOUGH_ANSWERS = 4;

    public static QuizLoader  getInstance()
    {
	if (m_singletonInstance == null)
	    m_singletonInstance = new QuizLoader();

	return m_singletonInstance;
    }

    private QuizLoader()
    {
    }

    public void  setDebugMode(int  debugMode)
    {
        m_debugMode = debugMode;
    }

    public void  setEncoding(String  encoding)
    {
	if (encoding != null)
	    m_encoding = new String(encoding);
    }
    
    public QuestionList readQuestions(String  questionFilename) throws IOException
    {
        // Read the list of "used" questions, if any
        BufferedReader  fin;

        String      line;

        Set  usedQuestions = new HashSet();

        m_qList = new QuestionList();

        // Read the questions
        int     skipCount = 0;
        int     questionCount = 0;
        int     line_num = 0;
        int     err;
        
        fin = openFile(questionFilename);
        
        while ((line = fin.readLine()) != null)
        {
            line_num++;
            
            if (line.startsWith("#") || line.length() == 0)
                continue;

            // Parse the line
            if ((err = parseLine(line, usedQuestions)) == QUESTION_ADDED)
                questionCount++;
            else if (err == BLANK_LINE)
                /* */;
            else if (err == INVALID_INDEX)
	    {
		Object	args[] =
		    	{
			    new Integer(line_num)
			};

                System.out.println(Main.getMessage("msg_invalid_difficulty",
			args));
	    }
            else
	    {
		Object	args[] =
		    	{
			    new Integer(line_num)
			};

                System.out.println(Main.getMessage("msg_invalid_question",
			args));
	    }
        }

	{
	    Object	args[] =
		    {
			new Integer(skipCount),
			new Integer(questionCount)
		    };

	    System.out.println(Main.getMessage("msg_num_questions_skipped",
		    args));
	    System.out.println(Main.getMessage("msg_num_questions", args));
	}
        
        fin.close();        

        return m_qList;
    }
    
    public boolean  isValidFile(String  questionFilename)
    {
        BufferedReader  fin;

        String      line;

	m_qList = null;		// Don't store questions

        if (questionFilename == null)
            return false;
        
	try
	{
	    fin = openFile(questionFilename);

	    // Read the questions
	    int     skipCount = 0;
	    int     questionCount = 0;
	    int     line_num = 0;
	    int     err;
	    
	    while ((line = fin.readLine()) != null)
	    {
		line_num++;
		
		if (line.startsWith("#") || line.length() == 0)
		    continue;

		// Parse the line
		err = parseLine(line, null);
		
		if (err != QUESTION_ADDED && err != BLANK_LINE)
		{
		    fin.close();

		    return false;
		}
	    }

	    fin.close();
	}
	catch (IOException  io_e)
	{
	    return false;
	}

	return true;
    }

    
    private BufferedReader  openFile(String  filename)
            throws IOException
    {
        BufferedReader  fin;
        
	if (m_encoding != null)
	{
	    // Open the specified file using the specified encoding.
	    fin = new BufferedReader(
		    new InputStreamReader(FileUtils.openFile(filename),
		    m_encoding));
	}
	else
	{
	    // Open the specified file using the default encoding.
	    fin = new BufferedReader(
		    new InputStreamReader(FileUtils.openFile(filename)));
	}
        
        return (fin);
    }
    

    /** Parse a line in the Question Database file.  The format of the
      * file is:
      * <pre>
      *      questionID   question   answer1   [answer2  [...]]  correct_answer_idx  [Note]
      * </pre>
      * Each field is separated by a tab.  Double-quote literals in the
      * question/answer fields must be doubled (e.g. "").
      */

    private int parseLine(String  s, Set  usedQuestions) throws IOException
    {
        StringTokenizer tok = new StringTokenizer(s, "\t", true);

        String          id = null;
        
        int             difficulty = 0;
        int             column = 1;
        int             correctIdx = -1;
        int             numAnswers = 0;
        
        boolean         parsingAnswers = false;
        
        if (s.length() == 0)
            return BLANK_LINE;

        // Create a Question instance for the question and its answers
        Question question = new Question(null, difficulty);
        
        while (tok.hasMoreTokens())
        {
            String  token = tok.nextToken();

            if (token.equals("\t"))
            {
                column++;
                continue;
            }
            
            String str = StringFilter.filterString(token);

            if (column == 1 && str.startsWith("#"))
                return BLANK_LINE;          // Comment line

            // Determine if the parse state has changed
            if (column == m_colID)
            {
                question.setQuestionID(str);
            }
            else if (column == m_colQuestion)
            {
                question.setQuestion(str);
                parsingAnswers = false;     // Clear parsing answers state
            }
            else if (column == m_colCorrectIdx)
            {
                int     idx;
                
                try {
                    idx = Integer.parseInt(str);

                    if (idx < 1)
                        return INVALID_INDEX;
                }
                catch (NumberFormatException  e)
                {
                    return INVALID_INDEX;
                }

                correctIdx = idx - 1;       // Convert natural idx to array idx
                parsingAnswers = false;     // Clear parsing answers state
            }
            else if (column == m_colReference)
            {
                question.setNote(str);
                parsingAnswers = false;     // Clear parsing answers state
            }
            else if (column == m_colAnswers)
            {
                parsingAnswers = true;      // Start parsing answers state
            }

            // If in answer parsing state, add this as an answer.
            if (parsingAnswers)
            {
                // Add this answer
                Answer answer = new Answer(str);

                // Add the answer to the Question
                question.addAnswer(answer);
                
                numAnswers++;
            }
        }

        
        // Add the question only if there are a reasonable number of possible
        // answers and the correct question index is valid.
        if (numAnswers > 2 && correctIdx >= 0 && correctIdx < numAnswers)
        {            
            if (m_qList != null)
                m_qList.addQuestion(question);

            AnswerList  answers = question.getAnswers();
            
            answers.setCorrectAnswer(correctIdx);
            
            return QUESTION_ADDED;
        }
        else
            return NOT_ENOUGH_ANSWERS;
    }


    /**
     *
     * Test program entry point
     *
     */
     public static void main(String[]  args) throws IOException
     {
        if (args.length != 3)
        {
            System.out.println("usage:  question_filename  used_question_filename  encoding");
            return;
        }
        
	QuestionLoader  ql = QuestionLoader.getInstance();

	ql.setUsedQuestionFilename(args[1]);
	ql.setEncoding(args[2]);
	
        ql.setDebugMode(1);
        
        QuestionList        questionList;
        
        questionList = ql.readQuestions(args[0]);
        
        // Output the questions, sorted by difficulty
        Question    question;
        
        int     numDifficulty = questionList.getNumLists();
       
        for (int difficulty = 0; difficulty < numDifficulty; difficulty++)
        {
            while (true)
            {
                if (questionList.getNumQuestionsRemaining(difficulty) == 0)
                    break;
                
                question = questionList.getQuestion(difficulty);
                
                System.out.println(question.toString());

                try
                {
                    AnswerList  answerList = question.getAnswers();

                    Answer[]  answers = answerList.getPossibleAnswers();

                    for (int  i = 0; i < answers.length; i++)
                    {
                        System.out.println("  " + i + ") " + answers[i].getAnswer());
                    }
                }
                catch (NoSuchElementException  e)
                {
                    // No answer -- skip it
                }
            }
        }
     }

    private static QuizLoader   	m_singletonInstance = null;
    private static String		m_encoding;

    private File		m_fileInst;	// File instance

    private QuestionList	m_qList;
    
    private int                 m_colID = 1;
    private int                 m_colQuestion = 2;
    private int                 m_colAnswers = 3;
    private int                 m_colCorrectIdx = 7;
    private int                 m_colReference = 8;
    
    private int                 m_debugMode;
}
