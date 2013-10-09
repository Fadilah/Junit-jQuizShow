/*
 * EventPacket.java
 *
 * Created on August 15, 2001, 6:45 PM
 *
 * $Id: EventPacket.java,v 1.1 2004/04/02 06:01:59 sdchen Exp $
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
 *    $Log: EventPacket.java,v $
 *    Revision 1.1  2004/04/02 06:01:59  sdchen
 *    Snapshot of jQuizShow 1.1 alpha06 development
 *
 *    Revision 1.1.1.1  2002/04/13 17:01:43  sdchen
 *    Initial import of the jQuizShow sources from local development directory.
 *
 *
 */

package jQuizShow.net;

/**
 *
 * @author  Steven D. Chen
 * @version 1.0
 */

import java.io.*;
import java.util.*;

import jQuizShow.*;
import jQuizShow.event.*;

public class EventPacket implements Serializable {
    
    /** Creates new EventPacket */
    public EventPacket() {
    }

    
    public GameUpdateEnum getType() {
        return m_gameUpdateEnum;
    }
    
    public int getSelectedAnswer() {
        return m_selectedAnswer;
    }
    
    public int getSelectedLifeline() {
        return m_selectedLifeline;
    }
    
    public int getHighlightLevel() {
        return m_highlightLevel;
    }
    
    public String getTransitionMessage() {
        return m_transitionMessage;
    }
    
    public String getStatusMessage() {
        return m_statusMessage;
    }
    
    public int getQuestionTimerTime() {
        return m_questionTimerTime;
    }
    
    public int getLifelineTimerTime() {
        return m_lifelineTimerTime;
    }

    public BitSet getToggleStates() {
        return m_toggleStates;
    }

    
    /*
     * Manipulators
     */
    
    public void setType(GameUpdateEnum  type) {
        m_gameUpdateEnum = type;
    }
    
    public void setSelectedAnswer(int  index) {
        m_selectedAnswer = index;
    }
    
    public void setSelectedLifeline(int  index) {
        m_selectedLifeline = index;
    }
    
    public void setHighlightLevel(int  index) {
        m_highlightLevel = index;
    }
    
    public void setTransitionMessage(String  message) {
        m_transitionMessage = message;
    }
    
    public void setStatusMessage(String  message) {
        m_statusMessage = message;
    }
    
    public void setQuestionTimerTime(int  seconds) {
        m_questionTimerTime = seconds;
    }
    
    public void setLifelineTimerTime(int  seconds) {
        m_lifelineTimerTime = seconds;
    }

    public void setToggleStates(BitSet  toggleStates) {
        m_toggleStates = toggleStates;
    }

    private GameUpdateEnum  m_gameUpdateEnum;
    
    private int  m_selectedAnswer;

    private int  m_selectedLifeline;
    private int  m_highlightLevel;
    
    private String  m_transitionMessage;
    private String  m_statusMessage;

    private int  m_questionTimerTime;
    private int  m_lifelineTimerTime;

    private BitSet  m_toggleStates;
}
