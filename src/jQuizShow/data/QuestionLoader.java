/*
 * QuestionLoader.java
 *
 * Created on March 2, 2001, 9:59 AM
 *
 * $Id: QuestionLoader.java,v 1.2 2007/02/05 04:53:57 sdchen Exp $
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
 *    $Log: QuestionLoader.java,v $
 *    Revision 1.2  2007/02/05 04:53:57  sdchen
 *    Removed end-of-line CR (Ctrl-M) from source code.
 *
 *    Revision 1.1  2004/04/02 06:01:59  sdchen
 *    Snapshot of jQuizShow 1.1 alpha06 development
 *
 *    Revision 1.4  2002/08/15 04:43:26  sdchen
 *    Internationalization of source code.  Main.getMessage() is the primary
 *    routine to get the localized message strings.
 *
 *    Revision 1.3  2002/06/06 05:21:17  sdchen
 *    Added option for reading file using a specified character encoding.
 *
 *    Revision 1.2  2002/05/30 04:27:43  sdchen
 *    Added check for negative difficulty level -- returns INVALID_DIFFICULTY.
 *    Added source comments regarding the expected format for a question entry.
 *
 *    Revision 1.1.1.1  2002/04/13 17:01:28  sdchen
 *    Initial import of the jQuizShow sources from local development directory.
 *
 *
 */

package jQuizShow.data;

/**
    Loads the questions from the specified local file or URL.
*/

import java.io.*;
import java.net.*;
import java.util.*;

import jQuizShow.*;
import jQuizShow.data.*;
import jQuizShow.util.*;

public class QuestionLoader
{
    private static final int INVALID_QUESTION_FILE = -1;
    private static final int QUESTION_ADDED = 0;
    private static final int BLANK_LINE = 1;
    private static final int INVALID_DIFFICULTY = 2;
    private static final int QUESTION_ALREADY_USED = 3;
    private static final int NOT_ENOUGH_ANSWERS = 4;

    public static QuestionLoader  getInstance()
    {
	if (m_singletonInstance == null)
	    m_singletonInstance = new QuestionLoader();

	return m_singletonInstance;
    }

    private QuestionLoader()
    {
    }

    public void  setDebugMode(int  debugMode)
    {
        m_debugMode = debugMode;
    }

    public void  setUsedQuestionFilename(String  usedQuestionFilename)
    {
        m_usedQuestionFilename = new String(usedQuestionFilename);
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

        try
        {
            int     numUsed = 0;
            
            fin = openFile(m_usedQuestionFilename);

            while ((line = fin.readLine()) != null)
            {
                if (line.startsWith("#"))
                    continue;

                usedQuestions.add(new String(line));
                numUsed++;
            }

            if (m_debugMode != 0)
	    {
		Object	args[] =
		    	{
			    new Integer(numUsed)
			};

                System.out.println(Main.getMessage("msg_num_used_questions",
			args));
	    }
            
            fin.close();
        }
        catch (IOException  e)
        {
            // No file.  No problem.  It just means no questions are used.
            if (m_debugMode != 0)
	    {
		Object	args[] =
		    	{
			    m_usedQuestionFilename
			};

                System.out.println(
			Main.getMessage("msg_no_used_questions",
			args));
	    }
        }
            

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
            else if (err == QUESTION_ALREADY_USED)
                skipCount++;
            else if (err == BLANK_LINE)
                /* */;
            else if (err == INVALID_DIFFICULTY)
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
        
        m_qList.shuffleQuestions();

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
      *      difficulty_level   question   answer1   [answer2  [...]]  [#Note]
      * </pre>
      * Each field is separated by a tab.  Double-quote literals in the
      * question/answer fields must be doubled (e.g. "").
      */

    private int parseLine(String  s, Set  usedQuestions) throws IOException
    {
        StringTokenizer tok = new StringTokenizer(s, "\t");

        String          questionStr;
        int		count = 0;
        int             difficulty;

        if (s.length() == 0)
            return BLANK_LINE;
        
        try {
            difficulty = Integer.parseInt(tok.nextToken());

            /* The valid range for difficulty levels is from 0 (easy)
	     * to 'numDifficultyBreakpoints' (hardest).  Thus, there
	     * are in actuality 'numDifficultyBreakpoints + 1' levels.
	     * There is no check on the upper limit to silently
	     * accomodate databases designed around different number
	     * of difficulty levels.
	     */
	    if (difficulty < 0)
	        return INVALID_DIFFICULTY;
        }
        catch (NumberFormatException  e)
        {
            return INVALID_DIFFICULTY;
        }
        
        // Read in the question
        questionStr = StringFilter.filterString(tok.nextToken());

        if (usedQuestions != null && usedQuestions.contains(questionStr))
            return QUESTION_ALREADY_USED;       // If question already used, skip it

        // Create a Question instance for the question and its answers
        Question question = new Question(questionStr, difficulty);
        
        while (tok.hasMoreTokens())
        {
            String str = StringFilter.filterString(tok.nextToken());
            
            if (str.startsWith("#"))
            {
                // A "note" entry begins with a "#"
                question.setNote(str.substring(1));
            }
            else
            {
                // Add this answer
                Answer answer = new Answer(str);

                // The first answer in the DB is the correct answer.  Mark it so.
                if (count++ == 0)
                    answer.setCorrect(true);

                // Add the answer to the Question
                question.addAnswer(answer);
            }
        }
        
        if (count > 2)
        {
            // Add the question only if there are a reasonable number of possible answers
	    if (m_qList != null)
		m_qList.addQuestion(question);

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

    private static QuestionLoader	m_singletonInstance = null;
    private static String		m_encoding;
    private static String		m_usedQuestionFilename;

    private File		m_fileInst;	// File instance

    private QuestionList	m_qList;
    
    private int                 m_debugMode;
}
