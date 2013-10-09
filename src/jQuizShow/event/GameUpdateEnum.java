/*
 * GameUpdateEnum.java
 *
 * Created on January 17, 2004, 1:54 PM
 *
 * $Id: GameUpdateEnum.java,v 1.1 2004/04/02 06:01:59 sdchen Exp $
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
 *    $Log: GameUpdateEnum.java,v $
 *    Revision 1.1  2004/04/02 06:01:59  sdchen
 *    Snapshot of jQuizShow 1.1 alpha06 development
 *
 *
 */

package jQuizShow.event;

/**
 * This defines the interface used to implement the game updates enumeration
 * type/class. The game update enumeration defines each of the possible
 * game-related events that would require update of the game UI throughout
 * its lifecycle.  Different UI's may define the set of events differently,
 * however they must all use implment this interface in order to function
 * under the framework of the primary game engine.
 *
 * @author  Steven D. Chen
 * @version 
 */

public interface GameUpdateEnum extends java.io.Serializable
{
    public String  toString();
    
    public boolean equals(Object that);
    
    public int hashCode();
}

