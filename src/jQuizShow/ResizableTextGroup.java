/*
 * ResizableTextGroup.java
 *
 * Created on March 7, 2004, 6:01 PM
 *
 * $Id: ResizableTextGroup.java,v 1.1 2004/04/02 06:01:59 sdchen Exp $
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
 *    $Log: ResizableTextGroup.java,v $
 *    Revision 1.1  2004/04/02 06:01:59  sdchen
 *    Snapshot of jQuizShow 1.1 alpha06 development
 *
 *
 */

package jQuizShow;

/**
 *
 * @author  Steve
 */

import java.util.*;

public class ResizableTextGroup {

    private LinkedList  m_list = null;
    
    /** Creates a new instance of ResizableTextGroup */
    public ResizableTextGroup()
    {
        m_list = new LinkedList();
    }
    
    public void addResizableText(ResizableText rtext)
    {
        if (rtext != null)
            m_list.add(rtext);
        
        return;
    }
    
    public void removeResizableText(ResizableText rtext)
    {
        m_list.remove(rtext);
    }
    
    public void autoSize()
    {
        ResizableText    rtext;

        int     minPointSize = 9999999;
        
        for (Iterator  iter = m_list.iterator(); iter.hasNext(); /**/)
        {
            int    pointSize;
            
            rtext = (ResizableText) iter.next();
            
            if ((pointSize = rtext.getPointSize()) < minPointSize)
                minPointSize = pointSize;
        }
        
        for (Iterator  iter = m_list.iterator(); iter.hasNext(); /**/)
        {
            rtext = (ResizableText) iter.next();
            rtext.setPointSize(minPointSize);
        }
    }

    
    public void setSize(int ptSize)
    {
        ResizableText    rtext;

        for (Iterator  iter = m_list.iterator(); iter.hasNext(); /**/)
        {
            rtext = (ResizableText) iter.next();          
            rtext.setPointSize(ptSize);
        }
    }
    
}
