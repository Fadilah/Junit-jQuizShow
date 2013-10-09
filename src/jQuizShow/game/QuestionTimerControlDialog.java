/*
 * QuestionTimerControlDialog.java
 *
 * Created on May 18, 2001, 6:34 PM
 *
 * $Id: QuestionTimerControlDialog.java,v 1.1 2004/04/02 06:01:59 sdchen Exp $
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
 *    $Log: QuestionTimerControlDialog.java,v $
 *    Revision 1.1  2004/04/02 06:01:59  sdchen
 *    Snapshot of jQuizShow 1.1 alpha06 development
 *
 *    Revision 1.2  2002/08/15 04:43:26  sdchen
 *    Internationalization of source code.  Main.getMessage() is the primary
 *    routine to get the localized message strings.
 *
 *    Revision 1.1.1.1  2002/04/13 17:01:28  sdchen
 *    Initial import of the jQuizShow sources from local development directory.
 *
 *
 */

package jQuizShow.game;

/**
 *
 * @author  Steven D. Chen
 */

import jQuizShow.*;
import jQuizShow.net.*;
import jQuizShow.event.*;

public class QuestionTimerControlDialog extends javax.swing.JInternalFrame
        implements GameStateChangeListener
{
    
    /** Creates new form GameLevelDialog */
    public QuestionTimerControlDialog() {
        super(Main.getMessage("label_question_timer"),
		false, true, false, true);

        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);

        initComponents();
        pack();
        
        m_gameState = GameState.getInstance();
        
        m_gameState.addGameStateChangeListener(this);
        
        levelSlider.setMaximum(GameState.QUESTION_TIMER_MAX_TIME);
    }

    public void gameStateChanged(GameStateChangeEvent evt) {
        GameStateEnum  type = (GameStateEnum) evt.getType();
        
        if (type == GameStateEnum.SET_QUESTION_TIMER_LIMIT)
        {
            levelSlider.setValue(m_gameState.getQuestionTimerLimit());
        }
    }    

    public void show()
    {
        /*
         * Bug workaround -- 2nd and subsequent sliders are not drawn properly
         * (Java 1.3.1).
         */
        if (firstShow)
        {
            levelSlider.updateUI();
            firstShow = false;
        }
        
        super.show();
    }    

    /** This method is called from within the constructor to
     * initialize the form.
     */
    private void initComponents() {
        sliderLabel = new javax.swing.JLabel();
        levelSlider = new javax.swing.JSlider();
        buttonPanel = new javax.swing.JPanel();
        applyButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();

        getContentPane().setLayout(new java.awt.BorderLayout());
        
        sliderLabel.setText(Main.getMessage("label_question_slider"));
        sliderLabel.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(20, 20, 20, 20)));
        getContentPane().add(sliderLabel, java.awt.BorderLayout.NORTH);
        
        levelSlider.setSnapToTicks(true);
        levelSlider.setMinorTickSpacing(15);
        levelSlider.setPaintLabels(true);
        levelSlider.setPaintTicks(true);
        levelSlider.setMajorTickSpacing(60);
        levelSlider.setMaximum(300);
        levelSlider.setValue(0);
        levelSlider.setBorder(new javax.swing.border.CompoundBorder(new javax.swing.border.BevelBorder(javax.swing.border.BevelBorder.LOWERED), new javax.swing.border.EmptyBorder(new java.awt.Insets(20, 20, 20, 20))));
        getContentPane().add(levelSlider, java.awt.BorderLayout.CENTER);
        
        applyButton.setText(Main.getMessage("label_apply"));
        applyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applyButtonActionPerformed(evt);
            }
        });
        
        buttonPanel.add(applyButton);
        
        closeButton.setText(Main.getMessage("label_close"));
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });
        
        buttonPanel.add(closeButton);
        
        getContentPane().add(buttonPanel, java.awt.BorderLayout.SOUTH);
    }

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {
        setVisible(false);
    }

    private void applyButtonActionPerformed(java.awt.event.ActionEvent evt)
    {
        // Set the new timer limit
        StatePacket  statePacket = new StatePacket();

        statePacket.setType(GameStateEnum.SET_QUESTION_TIMER_LIMIT);
        statePacket.setQuestionTimerLimit(levelSlider.getValue());

        PacketProcessor.getInstance().processStatePacket(statePacket);  // Do it
    }

    // Variables declaration
    private javax.swing.JLabel sliderLabel;
    private javax.swing.JSlider levelSlider;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton applyButton;
    private javax.swing.JButton closeButton;

    private GameState m_gameState;
    
    private boolean firstShow = true;
}
