/*
 * DecryptInputStream.java
 *
 * Created on June 18, 2002, 8:18 PM
 *
 * $Id: DecryptInputStream.java,v 1.1 2004/04/02 06:01:59 sdchen Exp $
 *
 *============================================================================
 *
 * Copyright (C) 2002  Steven D. Chen
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
 *    $Log: DecryptInputStream.java,v $
 *    Revision 1.1  2004/04/02 06:01:59  sdchen
 *    Snapshot of jQuizShow 1.1 alpha06 development
 *
 *    Revision 1.1  2002/06/22 19:00:11  sdchen
 *    Extends FilterInputStream to allow for a null constructor and a
 *    method to set the InputStream.
 *
 *
 */

package jQuizShow;

import java.io.*;

/** This class is a wrapper around the FilterInputStream.  Extend
 * this class and override the "read(...)" functions to implement
 * your own custom input stream decryption algorithm.
 *
 * @author  Steven D. Chen
 */

public class DecryptInputStream
        extends FilterInputStream
{
    public DecryptInputStream()
    {
	super(null);
    }

    public void  setInputStream(InputStream  input)
    {
	in = input;
    }
}
