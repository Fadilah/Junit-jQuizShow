/*
 * LifeLineOverrideDialog.java
 *
 * Created on May 15, 2001, 7:21 PM
 *
 * $Id: LifeLineOverrideDialog.java,v 1.1 2004/04/02 06:01:59 sdchen Exp $
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
 *    $Log: LifeLineOverrideDialog.java,v $
 *    Revision 1.1  2004/04/02 06:01:59  sdchen
 *    Snapshot of jQuizShow 1.1 alpha06 development
 *
 *    Revision 1.3  2002/08/15 04:43:26  sdchen
 *    Internationalization of source code.  Main.getMessage() is the primary
 *    routine to get the localized message strings.
 *
 *    Revision 1.2  2002/07/15 05:19:20  sdchen
 *    Removed code which set defaultCloseOperation() to DISPOSE_ON_CLOSE --
 *    this caused the dialog to no longer exist if the "X" icon is pressed.
 *
 *    Revision 1.1.1.1  2002/04/13 17:01:27  sdchen
 *    Initial import of the jQuizShow sources from local development directory.
 *
 *
 */

package jQuizShow.game;

/**
 *
 * @author  Steve
 */

import jQuizShow.*;
import jQuizShow.event.*;

public class LifeLineOverrideDialog extends javax.swing.JInternalFrame
        implements GameStateChangeListener,
        GameUpdateListener
        
{
    GameState m_gameState;
    
    /** Creates new form LifeLineOverrideDialog */
    public LifeLineOverrideDialog() {
        super("Lifeline", false, true, false, false);

        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);

        m_gameState = GameState.getInstance();

        m_gameState.addGameStateChangeListener(this);
        
        initComponents();
    }

    public void gameStateChanged(GameStateChangeEvent evt) {
        GameStateChangeEnum  type = evt.getType();
        
/*
 if (type == GameStateEnum.NEW_GAME ||
                type == GameStateEnum.WAIT_TO_START_ROUND ||
                type == GameStateEnum.LIFELINE_END)
*/
        {
            updateToggleStates();
        }
    }    

    public void gameUpdated(GameUpdateEvent evt) {
        GameUpdateEnum  type = evt.getType();

//        if (type == GameUpdateEnum.TOGGLE_STATE)
        {
            updateToggleStates();
        }
    }    

    
    private void  updateToggleStates()
    {
        fiftyCheckBox.setSelected(m_gameState.getToggleState(GameState.FIFTY_FIFTY));
        phoneCheckBox.setSelected(m_gameState.getToggleState(GameState.PHONE_A_FRIEND));
        pollCheckBox.setSelected(m_gameState.getToggleState(GameState.ASK_THE_AUDIENCE));

        return;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     */
    private void  initComponents() {
        jPanel1 = new javax.swing.JPanel();
        fiftyCheckBox = new javax.swing.JCheckBox();
        phoneCheckBox = new javax.swing.JCheckBox();
        pollCheckBox = new javax.swing.JCheckBox();
        closeButton = new javax.swing.JButton();
        
        setTitle("Lifeline Override");
        setName("lifelineDialog");
        
        jPanel1.setLayout(new java.awt.GridLayout(4, 1));
        
        jPanel1.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(10, 10, 10, 10)));
        fiftyCheckBox.setText(Main.getMessage("label_50_50"));
        fiftyCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxActionPerformed(evt);
            }
        });
        
        jPanel1.add(fiftyCheckBox);
        
        phoneCheckBox.setText(Main.getMessage("label_ask_friend"));
        phoneCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxActionPerformed(evt);
            }
        });
        
        jPanel1.add(phoneCheckBox);
        
        pollCheckBox.setText(Main.getMessage("label_ask_audience"));
        pollCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxActionPerformed(evt);
            }
        });
        
        jPanel1.add(pollCheckBox);
        
        closeButton.setText(Main.getMessage("label_close"));
        closeButton.setBorder(new javax.swing.border.BevelBorder(javax.swing.border.BevelBorder.RAISED));
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });
        
        jPanel1.add(closeButton);
        
        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);
        
        pack();
    }
    
    
    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {
        setVisible(false);
    }

    private void checkBoxActionPerformed(java.awt.event.ActionEvent evt) {
        m_gameState.setToggleState(GameState.FIFTY_FIFTY, fiftyCheckBox.isSelected());
        m_gameState.setToggleState(GameState.PHONE_A_FRIEND, phoneCheckBox.isSelected());
        m_gameState.setToggleState(GameState.ASK_THE_AUDIENCE, pollCheckBox.isSelected());
        
        m_gameState.fireGameStateChangeEvent(this, GameStateEnum.LIFELINE_END);
    }

    // Variables declaration
    private javax.swing.JPanel jPanel1;
    private javax.swing.JCheckBox fiftyCheckBox;
    private javax.swing.JCheckBox phoneCheckBox;
    private javax.swing.JCheckBox pollCheckBox;
    private javax.swing.JButton closeButton;

}
