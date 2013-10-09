/*
 * QuizPhaseEnum.java
 *
 * Created on January 30, 2004, 9:52 PM
 *
 * $Id: QuizPhaseEnum.java,v 1.1 2004/04/02 06:02:00 sdchen Exp $
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
 *    $Log: QuizPhaseEnum.java,v $
 *    Revision 1.1  2004/04/02 06:02:00  sdchen
 *    Snapshot of jQuizShow 1.1 alpha06 development
 *
 *
 */

package jQuizShow.quiz;

/**
 *
 * @author  Steven D. Chen
 *
 */

import java.util.*;

public final class QuizPhaseEnum implements java.io.Serializable
{
    // ArrayList of enumerations (used for readResolve())
    private static ArrayList  privateList = new ArrayList();

    /* Define the "enumeration" values */
    public static final QuizPhaseEnum S_INACTIVE = new QuizPhaseEnum("S_INACTIVE");
    public static final QuizPhaseEnum S_WAITING_TO_START = new QuizPhaseEnum("S_WAITING_TO_START");
    public static final QuizPhaseEnum S_STARTING_NEXT_ROUND = new QuizPhaseEnum("S_STARTING_NEXT_ROUND");
    public static final QuizPhaseEnum S_QUESTION_DISPLAYED = new QuizPhaseEnum("S_QUESTION_DISPLAYED");
    public static final QuizPhaseEnum S_ANSWERS_DISPLAYED = new QuizPhaseEnum("S_ANSWERS_DISPLAYED");
    public static final QuizPhaseEnum S_ANSWER_SELECTED = new QuizPhaseEnum("S_ANSWER_SELECTED");
    public static final QuizPhaseEnum S_ANSWER_FINALIZED = new QuizPhaseEnum("S_ANSWER_FINIALIZED");
    public static final QuizPhaseEnum S_CORRECT_ANSWER = new QuizPhaseEnum("S_CORRECT_ANSWER");
    public static final QuizPhaseEnum S_WRONG_ANSWER = new QuizPhaseEnum("S_WRONG_ANSWER");
    public static final QuizPhaseEnum S_RESET_FOR_NEXT_ROUND = new QuizPhaseEnum("S_RESET_FOR_NEXT_ROUND");
    public static final QuizPhaseEnum S_TRANSITION_TO_NEXT_ROUND = new QuizPhaseEnum("S_TRANSITION_TO_NEXT_ROUND");
    public static final QuizPhaseEnum S_QUESTION_TIMER_EXPIRED = new QuizPhaseEnum("S_QUESTION_TIMER_EXPIRED");
    public static final QuizPhaseEnum S_ENDING_GAME = new QuizPhaseEnum("S_ENDING_GAME");
    
    /** Creates new QuizPhaseEnum (private) */
    private QuizPhaseEnum(String name) {
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
