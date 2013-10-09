/*
 * SoundCtrlDialog.java
 *
 * Created on May 25, 2001, 8:34 PM
 *
 * $Id: SoundCtrlDialog.java,v 1.1 2004/04/02 06:01:59 sdchen Exp $
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
 *    $Log: SoundCtrlDialog.java,v $
 *    Revision 1.1  2004/04/02 06:01:59  sdchen
 *    Snapshot of jQuizShow 1.1 alpha06 development
 *
 *    Revision 1.2  2002/08/15 04:43:26  sdchen
 *    Internationalization of source code.  Main.getMessage() is the primary
 *    routine to get the localized message strings.
 *
 *    Revision 1.1.1.1  2002/04/13 17:01:31  sdchen
 *    Initial import of the jQuizShow sources from local development directory.
 *
 *
 */

package jQuizShow;

/**
 *
 * @author  Steven D. Chen
 */

import javax.swing.*;
import javax.swing.event.*;

import jQuizShow.util.*;

public class SoundCtrlDialog extends javax.swing.JInternalFrame
        implements ChangeListener
{

    /** Creates new form SoundCtrlDialog */
    public SoundCtrlDialog() {
        super(Main.getMessage("label_sound"), true, true, false, true);

        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);

        initComponents();
        
        foregroundSlider.addChangeListener(this);
        backgroundSlider.addChangeListener(this);

        pack();
    }

    
    /** Override the show() method so we can set the dialog to the
     * current values.
     */
    public void show()
    {
        SoundPlayer  soundPlayer = SoundPlayer.getInstance();

        foregroundSlider.setValue(soundPlayer.getGain(SoundPlayer.FOREGROUND_CHANNEL));
        backgroundSlider.setValue(soundPlayer.getGain(SoundPlayer.BACKGROUND_CHANNEL));

        muteCheckBox.setSelected(soundPlayer.getMute());

        /*
         * Bug workaround -- 2nd and subsequent sliders are not drawn properly
         * (Java 1.3.1).
         */
        if (firstShow)
        {
            foregroundSlider.updateUI();
            backgroundSlider.updateUI();
            firstShow = false;
        }
        
        super.show();
    }

    
    /** Process the JSlider change event */
    public void  stateChanged(ChangeEvent  e)
    {
        JSlider  source = (JSlider) e.getSource();

        SoundPlayer  soundPlayer = SoundPlayer.getInstance();

        if (source == backgroundSlider)
        {
            soundPlayer.setGain(SoundPlayer.BACKGROUND_CHANNEL, source.getValue());
        }
        else
        {
            soundPlayer.setGain(SoundPlayer.FOREGROUND_CHANNEL, source.getValue());
            soundPlayer.setGain(SoundPlayer.EFFECTS_CHANNEL, source.getValue());
        }
    }
   
    
    /** This method is called from within the constructor to
     * initialize the form.
     */
    private void initComponents() {
        buttonPane = new javax.swing.JPanel();
        closeButton = new javax.swing.JButton();
        centerPane = new javax.swing.JPanel();
        foregroundLabel = new javax.swing.JLabel();
        foregroundSlider = new javax.swing.JSlider();
        backgroundLabel = new javax.swing.JLabel();
        backgroundSlider = new javax.swing.JSlider();
        muteCheckBox = new javax.swing.JCheckBox();
        
        getContentPane().setLayout(new java.awt.BorderLayout());
        
        closeButton.setText(Main.getMessage("label_close"));
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });
        
        buttonPane.add(closeButton);
        
        getContentPane().add(buttonPane, java.awt.BorderLayout.SOUTH);
        
        centerPane.setLayout(new javax.swing.BoxLayout(centerPane, javax.swing.BoxLayout.Y_AXIS));
        
        centerPane.setBorder(new javax.swing.border.CompoundBorder(new javax.swing.border.BevelBorder(javax.swing.border.BevelBorder.LOWERED), new javax.swing.border.EmptyBorder(new java.awt.Insets(20, 20, 20, 20))));
        foregroundLabel.setText(Main.getMessage("label_foreground"));
        foregroundLabel.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(10, 10, 10, 10)));
        centerPane.add(foregroundLabel);
        
        foregroundSlider.setSnapToTicks(true);
        foregroundSlider.setMinorTickSpacing(5);
        foregroundSlider.setPaintLabels(true);
        foregroundSlider.setPaintTicks(true);
        foregroundSlider.setMajorTickSpacing(25);
        centerPane.add(foregroundSlider);
        
        backgroundLabel.setText(Main.getMessage("label_background"));
        backgroundLabel.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(10, 10, 10, 10)));
        backgroundLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        centerPane.add(backgroundLabel);
        
        backgroundSlider.setSnapToTicks(true);
        backgroundSlider.setMinorTickSpacing(5);
        backgroundSlider.setPaintLabels(true);
        backgroundSlider.setPaintTicks(true);
        backgroundSlider.setMajorTickSpacing(25);
        centerPane.add(backgroundSlider);
        
        muteCheckBox.setText(Main.getMessage("label_mute"));
        muteCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                muteCheckBoxActionPerformed(evt);
            }
        });
        
        centerPane.add(muteCheckBox);
        
        getContentPane().add(centerPane, java.awt.BorderLayout.CENTER);
        
        pack();
    }

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {
        setVisible(false);
    }

    private void muteCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {
        JCheckBox  source = (JCheckBox) evt.getSource();
        
        SoundPlayer  soundPlayer = SoundPlayer.getInstance();
        soundPlayer.setMute(source.isSelected());
    }



    // Variables declaration
    private javax.swing.JPanel buttonPane;
    private javax.swing.JButton closeButton;
    private javax.swing.JPanel centerPane;
    private javax.swing.JLabel foregroundLabel;
    private javax.swing.JSlider foregroundSlider;
    private javax.swing.JLabel backgroundLabel;
    private javax.swing.JSlider backgroundSlider;
    private javax.swing.JCheckBox muteCheckBox;

    private boolean firstShow = true;
}
