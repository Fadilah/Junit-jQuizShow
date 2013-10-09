/*
 * GameTimerListener.java
 *
 * Created on May 4, 2001, 8:30 PM
 *
 * $Id: GameTimerListener.java,v 1.1 2004/04/02 06:01:59 sdchen Exp $
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
 *    $Log: GameTimerListener.java,v $
 *    Revision 1.1  2004/04/02 06:01:59  sdchen
 *    Snapshot of jQuizShow 1.1 alpha06 development
 *
 *    Revision 1.1.1.1  2002/04/13 17:01:27  sdchen
 *    Initial import of the jQuizShow sources from local development directory.
 *
 *
 */

package jQuizShow.event;

import java.util.*;

/**
 *
 * @author  Steven D. Chen
 * @version 
 */

public interface GameTimerListener extends EventListener
{
    /** Called when a game timer event occurs */
    public void gameTimerActionPerformed(GameTimerEvent  evt);
    
    /** Called whenever each second while timer is running */
    public void gameTimerOneSecond(GameTimerEvent  evt);
}
