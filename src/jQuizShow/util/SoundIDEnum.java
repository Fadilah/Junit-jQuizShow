/*
 * SoundIDEnum.java
 *
 * Created on March 24, 2004  7:22 PM
 *
 * $Id: SoundIDEnum.java,v 1.1 2004/04/02 06:02:00 sdchen Exp $
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
 *    $Log: SoundIDEnum.java,v $
 *    Revision 1.1  2004/04/02 06:02:00  sdchen
 *    Snapshot of jQuizShow 1.1 alpha06 development
 *
 *
 */

package jQuizShow.util;

/**
 *
 * @author  Steven D. Chen
 * @version 
 */

import java.util.*;

public final class SoundIDEnum
{
    // ArrayList of enumerations (used for readResolve())
    private static ArrayList  privateList = new ArrayList();

    /* Define the "enumeration" values */
    public static final SoundIDEnum GAME_THEME_SONG = new SoundIDEnum("sound_GameThemeSong");
    public static final SoundIDEnum SHOW_SCORE_SCREEN = new SoundIDEnum("sound_ShowScoreScreen");
    public static final SoundIDEnum HIDE_SCORE_SCREEN = new SoundIDEnum("sound_HideScoreScreen");
    public static final SoundIDEnum SHOW_GAME_SCREEN = new SoundIDEnum("sound_ShowGameScreen");
    public static final SoundIDEnum HIDE_GAME_SCREEN = new SoundIDEnum("sound_HideGameScreen");
    public static final SoundIDEnum NEW_PLAYER = new SoundIDEnum("sound_NewPlayer");
    public static final SoundIDEnum SHOW_QUESTION = new SoundIDEnum("sound_ShowQuestion");
    public static final SoundIDEnum SHOW_ANSWER = new SoundIDEnum("sound_ShowAnswer");
    public static final SoundIDEnum ANSWER_FINALIZED = new SoundIDEnum("sound_AnswerFinalized");
    public static final SoundIDEnum ANSWER_CORRECT = new SoundIDEnum("sound_AnswerCorrect");
    public static final SoundIDEnum ANSWER_WRONG = new SoundIDEnum("sound_AnswerWrong");
    public static final SoundIDEnum GOAL_REACHED = new SoundIDEnum("sound_GoalReached");
    public static final SoundIDEnum FIFTY_FIFTY_LIFELINE = new SoundIDEnum("sound_FiftyFiftyLifeline");
    public static final SoundIDEnum PHONE_A_FRIEND_LIFELINE = new SoundIDEnum("sound_PhoneAFriendLifeline");
    public static final SoundIDEnum ASK_THE_AUDIENCE_LIFELINE = new SoundIDEnum("sound_AskTheAudienceLifeline");
    public static final SoundIDEnum ASK_THE_AUDIENCE_END = new SoundIDEnum("sound_AskTheAudienceEnd");
    public static final SoundIDEnum CLOCK_TICK = new SoundIDEnum("sound_ClockTick");
    public static final SoundIDEnum QUESTION_TIME_UP = new SoundIDEnum("sound_QuestionTimeUp");
    public static final SoundIDEnum LIFELINE_TIME_UP = new SoundIDEnum("sound_LifelineTimeUp");
    public static final SoundIDEnum BACKGROUND1 = new SoundIDEnum("sound_Background1");
    public static final SoundIDEnum BACKGROUND2 = new SoundIDEnum("sound_Background2");
    public static final SoundIDEnum BACKGROUND3 = new SoundIDEnum("sound_Background3");
    public static final SoundIDEnum BACKGROUND4 = new SoundIDEnum("sound_Background4");
    public static final SoundIDEnum BACKGROUND5 = new SoundIDEnum("sound_Background5");
    public static final SoundIDEnum BACKGROUND6 = new SoundIDEnum("sound_Background6");
    public static final SoundIDEnum WINNER = new SoundIDEnum("sound_Winner");
    public static final SoundIDEnum SELECTION_MADE = new SoundIDEnum("sound_SelectionMade");
    public static final SoundIDEnum RANDOM_SELECTOR_BKG = new SoundIDEnum("sound_RandomSelectorBkg");
    public static final SoundIDEnum RANDOM_SELECTOR_DONE = new SoundIDEnum("sound_RandomSelectorDone");
    public static final SoundIDEnum CLIP_0 = new SoundIDEnum("sound_Clip_0");
    public static final SoundIDEnum CLIP_1 = new SoundIDEnum("sound_Clip_1");
    public static final SoundIDEnum CLIP_2 = new SoundIDEnum("sound_Clip_2");
    public static final SoundIDEnum CLIP_3 = new SoundIDEnum("sound_Clip_3");
    public static final SoundIDEnum CLIP_4 = new SoundIDEnum("sound_Clip_4");
    public static final SoundIDEnum CLIP_5 = new SoundIDEnum("sound_Clip_5");
    public static final SoundIDEnum CLIP_6 = new SoundIDEnum("sound_Clip_6");
    public static final SoundIDEnum CLIP_7 = new SoundIDEnum("sound_Clip_7");
    public static final SoundIDEnum CLIP_8 = new SoundIDEnum("sound_Clip_8");
    public static final SoundIDEnum CLIP_9 = new SoundIDEnum("sound_Clip_9");
                                     
    /** Creates new SoundIDEnum (private) */           
    private SoundIDEnum(String name) {
        this.name = new String(name);
        privateList.add(this);          // Add this enumeration to the list
        ordinal = privateList.indexOf(this);
    }

    
    /**
     * Returns the string name associated with this enumeration.
     */
    public String  toString() {
        return name;
    }

    
    /**
     * Returns the value associated with this enumeration.
     */
    public int  value() {
        return ordinal;
    }

    
    /**
     * Prevent subclasses from overriding Object.equals
     */
    public final boolean equals(Object that) {
        return super.equals(that);
    }

    
    /**
     * Returns the number of enumerations.
     */
    public static int  length() {
        return privateList.size();
    }
    
    
    /**
     * Returns a unique hash code for this instance.
     */
    public final int hashCode() {
        return super.hashCode();
    }


    private class EnumIterator implements Iterator
    {
        EnumIterator()
        {
            next = (SoundIDEnum) privateList.get(0);
        }

        EnumIterator(SoundIDEnum  obj)
        {
            next = obj;
        }
        
        public boolean hasNext()
        {
            return next != null;
        }

        public Object next()
        {
            SoundIDEnum  current = next;

            if (next == null)
                throw new NoSuchElementException();
                
            int  index = privateList.indexOf(current);
            
            if (index < privateList.size() - 1)
                next = (SoundIDEnum) privateList.get(index + 1);
            else
                next = null;
            
            return current;
        }

        public void remove()
        {
            return;         // Not implemented
        }
        
        private SoundIDEnum  next;
    }

    
    /**
     * Returns the iterator for this enumeration.
     */
    public Iterator  iterator() {
        return new EnumIterator();
    }

    
    /**
     * Returns the iterator for this enumeration starting with the specified.
     */
    public Iterator  iteratorStart() {
        return new EnumIterator(this);
    }        
    
    
    //*** Private Data
    
    /** Ordinal associated with this enumeration */
    private int ordinal;

    /** Name of this enumeration */
    private transient final String name;
}
