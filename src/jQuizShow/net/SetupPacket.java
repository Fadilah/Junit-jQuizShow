/*
 * SetupPacket.java
 *
 * Created on August 15, 2001, 6:44 PM
 *
 * $Id: SetupPacket.java,v 1.1 2004/04/02 06:01:59 sdchen Exp $
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
 *    $Log: SetupPacket.java,v $
 *    Revision 1.1  2004/04/02 06:01:59  sdchen
 *    Snapshot of jQuizShow 1.1 alpha06 development
 *
 *    Revision 1.1.1.1  2002/04/13 17:01:33  sdchen
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

public class SetupPacket implements Serializable {

        /* Values for "m_gameState" */
    public static final int NEW_GAME        = 1;
    public static final int NEW_QUESTION    = 2;

    public int  m_gameState;        /* See above for defined values */
    
    public int  m_maxQuestionNumber;
    public int  m_questionNumber;

    public int  m_iconSelectedIndex;
    public int  m_levelHighlightIndex;
    
    public String  m_question;
    public String[]  m_answers;

    public int  m_questionTimerLimit;
    public int  m_questionTimerTime;
    
    public int  m_highlightLevel;
    
    /** Creates new SetupPacket */
    public SetupPacket() {
    }

}
