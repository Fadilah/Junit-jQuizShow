/*
 * GameStateEnum.java
 *
 * Created on October 6, 2001, 6:21 PM
 *
 * $Id: GameStateEnum.java,v 1.1 2004/04/02 06:01:59 sdchen Exp $
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
 *    $Log: GameStateEnum.java,v $
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
 * @version 
 */

import java.util.*;

import jQuizShow.event.*;

public final class GameStateEnum
        implements GameStateChangeEnum
{
    // ArrayList of enumerations (used for readResolve())
    private static ArrayList  privateList = new ArrayList();

    /* Define the "enumeration" values */
    public static final GameStateEnum IDLE = new GameStateEnum("IDLE");
    public static final GameStateEnum SET_CURRENT_LEVEL = new GameStateEnum("SET_CURRENT_LEVEL");
    public static final GameStateEnum SET_QUESTION_TIMER_LIMIT = new GameStateEnum("SET_QUESTION_TIMER_LIMIT");
    public static final GameStateEnum NEW_GAME = new GameStateEnum("NEW_GAME");
    public static final GameStateEnum WAIT_TO_START_ROUND = new GameStateEnum("WAIT_TO_START_ROUND");
    public static final GameStateEnum DISPLAY_QUESTION = new GameStateEnum("DISPLAY_QUESTION");
    public static final GameStateEnum DISPLAY_ANSWER = new GameStateEnum("DISPLAY_ANSWER");
    public static final GameStateEnum WAIT_FOR_ANSWER = new GameStateEnum("WAIT_FOR_ANSWER");
    public static final GameStateEnum WAIT_TO_REVEAL_ANSWER = new GameStateEnum("WAIT_TO_REVEAL_ANSWER");
    public static final GameStateEnum ANSWER_WAS_CORRECT = new GameStateEnum("ANSWER_WAS_CORRECT");
    public static final GameStateEnum TRANSITION_LEVEL = new GameStateEnum("TRANSITION_LEVEL");
    public static final GameStateEnum ANSWER_WAS_WRONG = new GameStateEnum("ANSWER_WAS_WRONG");
    public static final GameStateEnum LIFELINE_SELECTED = new GameStateEnum("LIFELINE_SELECTED");
    public static final GameStateEnum ASK_THE_AUDIENCE = new GameStateEnum("ASK_THE_AUDIENCE");
    public static final GameStateEnum FIFTY_FIFTY = new GameStateEnum("FIFTY_FIFTY");
    public static final GameStateEnum PHONE_A_FRIEND = new GameStateEnum("PHONE_A_FRIEND");
    public static final GameStateEnum LIFELINE_END = new GameStateEnum("LIFELINE_END");
    public static final GameStateEnum WALKAWAY = new GameStateEnum("WALKAWAY");
    public static final GameStateEnum GRAND_PRIZE_WON = new GameStateEnum("GRAND_PRIZE_WON");
    public static final GameStateEnum END_OF_GAME = new GameStateEnum("END_OF_GAME");
    
    /** Creates new GameStateEnum (private) */
    private GameStateEnum(String  name) {
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
