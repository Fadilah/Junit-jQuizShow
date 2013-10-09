/*
 * QuizStateEnum.java
 *
 * Created on January 30, 2004, 6:21 PM
 *
 * $Id: QuizStateEnum.java,v 1.1 2004/04/02 06:02:00 sdchen Exp $
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
 *    $Log: QuizStateEnum.java,v $
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

import java.util.*;

import jQuizShow.event.*;

public final class QuizStateEnum implements GameStateChangeEnum
{
    // ArrayList of enumerations (used for readResolve())
    private static ArrayList  privateList = new ArrayList();

    /* Define the "enumeration" values */
    public static final QuizStateEnum IDLE = new QuizStateEnum("IDLE");
    public static final QuizStateEnum SET_CURRENT_LEVEL = new QuizStateEnum("SET_CURRENT_LEVEL");
    public static final QuizStateEnum SET_QUESTION_TIMER_LIMIT = new QuizStateEnum("SET_QUESTION_TIMER_LIMIT");
    public static final QuizStateEnum NEW_GAME = new QuizStateEnum("NEW_GAME");
    public static final QuizStateEnum WAIT_TO_START_ROUND = new QuizStateEnum("WAIT_TO_START_ROUND");
    public static final QuizStateEnum DISPLAY_QUESTION = new QuizStateEnum("DISPLAY_QUESTION");
    public static final QuizStateEnum DISPLAY_ANSWER = new QuizStateEnum("DISPLAY_ANSWER");
    public static final QuizStateEnum WAIT_FOR_ANSWER = new QuizStateEnum("WAIT_FOR_ANSWER");
    public static final QuizStateEnum WAIT_TO_REVEAL_ANSWER = new QuizStateEnum("WAIT_TO_REVEAL_ANSWER");
    public static final QuizStateEnum ANSWER_WAS_CORRECT = new QuizStateEnum("ANSWER_WAS_CORRECT");
    public static final QuizStateEnum ANSWER_WAS_WRONG = new QuizStateEnum("ANSWER_WAS_WRONG");
    public static final QuizStateEnum QUESTION_TIMER_EXPIRED = new QuizStateEnum("QUESTION_TIMER_EXPIRED");
    public static final QuizStateEnum END_OF_GAME = new QuizStateEnum("END_OF_GAME");
    public static final QuizStateEnum SET_SHOW_ANSWERS_MODE = new QuizStateEnum("SET_SHOW_ANSWERS_MODE");
    
    /** Creates new QuizStateEnum (private) */
    private QuizStateEnum(String name) {
        this.name = new String(name);
        privateList.add(this);          // Add this enumeration to the list
        ordinal = privateList.indexOf(this);
    }

    public String  toString() {
        return name;
    }
    
    // Prevent subclasses from overriding Object.equals
    public final boolean equals(Object that) {
        return super.equals(that);
    }

    public final int hashCode() {
        return super.hashCode();
    }
    
    // Override readResolve() to prevent duplicate constants from
    // coexisting as a result of deserialization.
    private Object  readResolve() throws java.io.ObjectStreamException {
        return privateList.get(ordinal);     // Canonicalize
    }
    
    //*** Private Data
    
    // Assign an ordinal to this enumeration
    // @serial Ordinal number of enumeration
    private int ordinal;

    // Name of this enumeration
    private transient final String name;
}
