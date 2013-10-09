/*
 * Answer.java
 *
 * Created on October 29, 2000, 8:30 PM
 *
 * $Id: Answer.java,v 1.1 2004/04/02 06:01:59 sdchen Exp $
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
 *    $Log: Answer.java,v $
 *    Revision 1.1  2004/04/02 06:01:59  sdchen
 *    Snapshot of jQuizShow 1.1 alpha06 development
 *
 *    Revision 1.1.1.1  2002/04/13 17:01:12  sdchen
 *    Initial import of the jQuizShow sources from local development directory.
 *
 *
 */

package jQuizShow.data;


/** Class used to store an answer to a question.  This class also stores
 * information regarding whether this answer is the correct answer or not.
 * @author Steven D. Chen
 * @version 1.0
 */
public class Answer {

    /** Creates new Answer */
    public Answer() {
    }

    /** Creates new Answer */
    public Answer(String  text) {
	setAnswer(text);
    }

    /** Sets the answer text
     * @param text Answer string
     */
    public void setAnswer(String  text) {
        this.answer = text;
    }
    
    /** Sets the "correct" flag.  This flag marks this answer as the correct answer.
     * @param isCorrect True if this answer is correct
     */
    public void setCorrect(boolean  isCorrect) {
        this.isCorrect = isCorrect;
    }
    
    /** Returns the answer string
     * @return Answer text string
     */
    public String getAnswer() {
        return answer;
    }

    public String  toString() {
        return getAnswer();
    }
    
    /** Returns whether this is the correct answer or not
     * @return True = correct answer, else False
     */
    public boolean getCorrect() {
        return isCorrect;
    }
    
    /** Answer text
     */
    private String  answer;
    /** True = correct answer
     */
    private boolean  isCorrect;
}
