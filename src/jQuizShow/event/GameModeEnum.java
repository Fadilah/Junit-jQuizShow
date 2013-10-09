/*
 * GameModeEnum.java
 *
 * Created on November 9, 2001, 10:15 PM
 *
 * $Id: GameModeEnum.java,v 1.1 2004/04/02 06:01:59 sdchen Exp $
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
 *    $Log: GameModeEnum.java,v $
 *    Revision 1.1  2004/04/02 06:01:59  sdchen
 *    Snapshot of jQuizShow 1.1 alpha06 development
 *
 *    Revision 1.1.1.1  2002/04/13 17:01:44  sdchen
 *    Initial import of the jQuizShow sources from local development directory.
 *
 *
 */

package jQuizShow.event;

/**
 *
 * @author  Steven D. Chen
 * @version 
 */

import java.util.*;

public final class GameModeEnum implements java.io.Serializable
{
    // ArrayList of enumerations (used for readResolve())
    private static ArrayList  privateList = new ArrayList();

    /* Define the "enumeration" values */
    public static final GameModeEnum STANDALONE = new GameModeEnum("STANDALONE");
    public static final GameModeEnum MASTER = new GameModeEnum("MASTER");
    public static final GameModeEnum SLAVE = new GameModeEnum("SLAVE");
    public static final GameModeEnum RESET = new GameModeEnum("RESET");
    
    /** Creates new GameModeEnum (private) */
    private GameModeEnum(String name) {
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
