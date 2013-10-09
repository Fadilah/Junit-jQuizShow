/*
 * TransitionPanel.java
 *
 * Created on May 23, 2001, 7:42 PM
 *
 * $Id: TransitionPanel.java,v 1.1 2004/04/02 06:01:59 sdchen Exp $
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
 * You should have received a copy of the GNU General Public License
 * along with jQuizShow; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *============================================================================
 *
 * Modifications:
 *
 *    $Log: TransitionPanel.java,v $
 *    Revision 1.1  2004/04/02 06:01:59  sdchen
 *    Snapshot of jQuizShow 1.1 alpha06 development
 *
 *    Revision 1.1.1.1  2002/04/13 17:01:32  sdchen
 *    Initial import of the jQuizShow sources from local development directory.
 *
 *
 */

package jQuizShow;

/**
 *
 * @author  Steven D. Chen
 */

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

import jQuizShow.*;
import jQuizShow.game.*;
import jQuizShow.event.*;


public class TransitionPanel extends JPanel
        implements GameUpdateListener

{
    /** Creates new form TransitionPanel */
    public TransitionPanel() {
        initComponents();
        
        m_gameState = GameState.getInstance(false);

        // Add myself as a listener
        m_gameState.addGameUpdateListener(this);
    }


    /** Handle any updates.
     */
    public void gameUpdated(GameUpdateEvent evt)
    {
        GameUpdateEnum  type = evt.getType();
        
        if (type == GameUpdateEnumGame.TRANSITION_MSG)
        {
            String  text = m_gameState.getTransitionMessage();

            if (text == null || text.length() == 0)
                m_gameText.setVisible(false);
            else
            {
                m_gameText.setVisible(true);
                m_gameText.setText("", text);
            }
        }
        else if (type == GameUpdateEnumGame.STATUS_MSG)
        {
            String  text = m_gameState.getStatusMessage();

            if (text == null || text.length() == 0)
                m_statusText.setVisible(false);
            else
            {
                m_statusText.setVisible(true);
                m_statusText.setText(text);
            }
        }
    }    

    
    /** This method is called from within the constructor to
     * initialize the form.
     */
    private void initComponents() {
        
        setLayout(new java.awt.BorderLayout());
        
        setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(50, 0, 50, 0)));
        setForeground(java.awt.Color.white);
        setBackground(java.awt.Color.black);
        
        m_gameText = new GameText(777, new Dimension(12, 1));
        m_gameText.centerText(true);
        
        add(m_gameText, BorderLayout.CENTER);
        
        m_statusText = new JTextArea();
        
        m_statusText.setWrapStyleWord(true);
        m_statusText.setLineWrap(true);
        m_statusText.setForeground(java.awt.Color.white);
        m_statusText.setText("Status Text");
        m_statusText.setBackground(java.awt.Color.black);
        m_statusText.setDisabledTextColor(java.awt.Color.white);
        m_statusText.setEnabled(false);
        m_statusText.setRequestFocusEnabled(false);
        
        add(m_statusText, BorderLayout.SOUTH);
    }

    
    // Variables declaration

    private GameState  m_gameState;
    
    private GameText  m_gameText;
    private JTextArea  m_statusText;
}
