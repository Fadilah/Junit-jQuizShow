/*
 * QuizUpdateEnum.java
 *
 * Created on January 30, 2004, 10:15 PM
 *
 * $Id: QuizUpdateEnum.java,v 1.1 2004/04/02 06:02:00 sdchen Exp $
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
 *    $Log: QuizUpdateEnum.java,v $
 *    Revision 1.1  2004/04/02 06:02:00  sdchen
 *    Snapshot of jQuizShow 1.1 alpha06 development
 *
 *
 */

package jQuizShow.quiz;

/**
 *
 * @author  Steven D. Chen
 */

import java.util.*;

public final class QuizUpdateEnum implements jQuizShow.event.GameUpdateEnum, java.io.Serializable
{
    // ArrayList of enumerations (used for readResolve())
    private static ArrayList  privateList = new ArrayList();

    /* Define the "enumeration" values */
    public static final QuizUpdateEnum NOP = new QuizUpdateEnum("NOP");
    public static final QuizUpdateEnum TRANSITION_MSG = new QuizUpdateEnum("TRANSITION_MSG");
    public static final QuizUpdateEnum SELECTED_ANSWER = new QuizUpdateEnum("SELECTED_ANSWER");
    public static final QuizUpdateEnum SELECTED_LIFELINE = new QuizUpdateEnum("SELECTED_LIFELINE");
    public static final QuizUpdateEnum UPDATE_QUESTION_CLOCK = new QuizUpdateEnum("UPDATE_QUESTION_CLOCK");
    public static final QuizUpdateEnum UPDATE_LIFELINE_CLOCK = new QuizUpdateEnum("UPDATE_LIFELINE_CLOCK");
    public static final QuizUpdateEnum TOGGLE_STATE = new QuizUpdateEnum("TOGGLE_STATE");
    public static final QuizUpdateEnum STATUS_MSG = new QuizUpdateEnum("STATUS_MSG");
    
    /** Creates new QuizUpdateEnum (private) */
    private QuizUpdateEnum(String name) {
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
