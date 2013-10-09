/*
 * PacketProcessor.java
 *
 * Created on January 17, 2004, 5:40 PM
 *
 * $Id: PacketProcessor.java,v 1.1 2004/04/02 06:01:59 sdchen Exp $
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
 *    $Log: PacketProcessor.java,v $
 *    Revision 1.1  2004/04/02 06:01:59  sdchen
 *    Snapshot of jQuizShow 1.1 alpha06 development
 *
 *
 */

package jQuizShow.net;

/**
 *
 * @author  Steven D. Chen
 * @version 
 */

import jQuizShow.event.*;

public abstract class PacketProcessor {

    /**
     * Get the Singleton instance of the implementation.
     */
    public static PacketProcessor getInstance()
    {
        return m_singleton;
    }
    
    /**
     * Sets the Singleton instance of the implementation.
     */
    public static void setInstance(PacketProcessor  singleton)
    {
        m_singleton = singleton;
    }  

    /**
     * Process the received game state changed packet.  This is sent
     * at the start of a round and contains all the information
     * needed for the round.
     */
    public abstract void processStatePacket(StatePacket  info);

    /**
     * Process the received game update event packet.
     */
    public abstract void processEventPacket(EventPacket  info);

    /**
     * Process the received game update event packet.
     */
    public abstract void processModeChangePacket(ModeChangePacket  info);
    
    /**
     * Stores the singleton instance.
     */
    private static PacketProcessor  m_singleton;
}
