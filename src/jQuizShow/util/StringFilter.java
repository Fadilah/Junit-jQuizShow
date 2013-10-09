/*
 * StringFilter.java
 *
 * Created on March 17, 2001, 10:12 PM
 *
 * $Id: StringFilter.java,v 1.1 2004/04/02 06:02:00 sdchen Exp $
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
 *    $Log: StringFilter.java,v $
 *    Revision 1.1  2004/04/02 06:02:00  sdchen
 *    Snapshot of jQuizShow 1.1 alpha06 development
 *
 *    Revision 1.1.1.1  2002/04/13 17:01:32  sdchen
 *    Initial import of the jQuizShow sources from local development directory.
 *
 *
 */


package jQuizShow.util;


/**
 *
 * @author  Steve
 * @version 
 */
public class StringFilter {

    /** Creates new StringFilter */
    public StringFilter() {
    }

    public static String  filterString(String  input)
    {
        StringBuffer    newString = new StringBuffer();
        
        int     length = input.length();
        
        for (int  i = 0; i < length; i++)
        {
            if (input.charAt(i) == '"')
            {
                // Double quotes encountered.  If it is a single quote,
                // skip over it.
                if ((i + 1) < length && input.charAt(i + 1) == '"')
                {
                    // Translate two double quotes in series to a single quote
                    newString.append('"');
                    i++;
                }
            }
            else
            {
                newString.append(input.charAt(i));
            }
        }
        
        return (newString.toString());
    }
}
