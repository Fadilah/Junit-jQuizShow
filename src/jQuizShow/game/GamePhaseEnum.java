/*
 * GamePhaseEnum.java
 *
 * Created on November 4, 2001, 9:52 PM
 *
 * $Id: GamePhaseEnum.java,v 1.1 2004/04/02 06:01:59 sdchen Exp $
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
 *    $Log: GamePhaseEnum.java,v $
 *    Revision 1.1  2004/04/02 06:01:59  sdchen
 *    Snapshot of jQuizShow 1.1 alpha06 development
 *
 *    Revision 1.1.1.1  2002/04/13 17:01:44  sdchen
 *    Initial import of the jQuizShow sources from local development directory.
 *
 *
 */

package jQuizShow.game;

/**
 *
 * @author  Steven D. Chen
 * @version 1.0
 */

import java.util.*;

public final class GamePhaseEnum implements java.io.Serializable
{
    // ArrayList of enumerations (used for readResolve())
    private static ArrayList  privateList = new ArrayList();

    /* Define the "enumeration" values */
    public static final GamePhaseEnum S_INACTIVE = new GamePhaseEnum("S_INACTIVE");
    public static final GamePhaseEnum S_WAITING_TO_START = new GamePhaseEnum("S_WAITING_TO_START");
    public static final GamePhaseEnum S_STARTING_NEXT_ROUND = new GamePhaseEnum("S_STARTING_NEXT_ROUND");
    public static final GamePhaseEnum S_QUESTION_DISPLAYED = new GamePhaseEnum("S_QUESTION_DISPLAYED");
    public static final GamePhaseEnum S_DISPLAYING_ANSWERS = new GamePhaseEnum("S_DISPLAYING_ANSWERS");
    public static final GamePhaseEnum S_ANSWERS_DISPLAYED = new GamePhaseEnum("S_ANSWERS_DISPLAYED");
    public static final GamePhaseEnum S_ANSWER_SELECTED = new GamePhaseEnum("S_ANSWER_SELECTED");
    public static final GamePhaseEnum S_ANSWER_FINALIZED = new GamePhaseEnum("S_ANSWER_FINIALIZED");
    public static final GamePhaseEnum S_CORRECT_ANSWER = new GamePhaseEnum("S_CORRECT_ANSWER");
    public static final GamePhaseEnum S_WRONG_ANSWER = new GamePhaseEnum("S_WRONG_ANSWER");
    public static final GamePhaseEnum S_RESET_FOR_NEXT_ROUND = new GamePhaseEnum("S_RESET_FOR_NEXT_ROUND");
    public static final GamePhaseEnum S_WALKAWAY = new GamePhaseEnum("S_WALKAWAY");
    public static final GamePhaseEnum S_ENDING_GAME = new GamePhaseEnum("S_ENDING_GAME");
    public static final GamePhaseEnum S_TRANSITION_PANEL_SHOWN = new GamePhaseEnum("S_TRANSITION_PANEL_SHOWN");
    public static final GamePhaseEnum S_CONFIRM_LIFELINE = new GamePhaseEnum("S_CONFIRM_LIFELINE");
    public static final GamePhaseEnum S_LIFELINE_ABORTED = new GamePhaseEnum("S_LIFELINE_ABORTED");
    public static final GamePhaseEnum S_LIFELINE_CONFIRMED = new GamePhaseEnum("S_LIFELINE_CONFIMED");
    public static final GamePhaseEnum S_LIFELINE_USED = new GamePhaseEnum("S_LIFELINE_USED");
    public static final GamePhaseEnum S_PHONE_A_FRIEND_TIMER = new GamePhaseEnum("S_PHONE_A_FRIEND_TIMER");
    public static final GamePhaseEnum S_WAITING_FOR_POLL_COMPLETION = new GamePhaseEnum("S_WAITING_FOR_POLL_COMPLETION");
    
    /** Creates new GamePhaseEnum (private) */
    private GamePhaseEnum(String name) {
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
