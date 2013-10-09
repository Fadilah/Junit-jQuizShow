/*
 * QuizShowAboutDialog.java
 *
 * Created on October 22, 2000, 6:03 PM
 *
 * $Id: QuizShowAboutDialog.java,v 1.3 2007/02/05 04:54:26 sdchen Exp $
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
 *    $Log: QuizShowAboutDialog.java,v $
 *    Revision 1.3  2007/02/05 04:54:26  sdchen
 *    Updated copyright date range.
 *
 *    Revision 1.2  2007/02/05 03:55:48  sdchen
 *    Removed CR (Ctrl-M) from lines.
 *
 *    Revision 1.1  2004/04/02 06:01:59  sdchen
 *    Snapshot of jQuizShow 1.1 alpha06 development
 *
 *    Revision 1.4  2002/08/15 04:43:26  sdchen
 *    Internationalization of source code.  Main.getMessage() is the primary
 *    routine to get the localized message strings.
 *
 *    Revision 1.3  2002/05/23 05:02:56  sdchen
 *    *** empty log message ***
 *
 *    Revision 1.2  2002/05/23 04:47:38  sdchen
 *    *** empty log message ***
 *
 *    Revision 1.1.1.1  2002/04/13 17:01:29  sdchen
 *    Initial import of the jQuizShow sources from local development directory.
 *
 *
 */

package jQuizShow;

/**
 *
 * @author  Steven D. Chen
 * @version 
 */

import java.awt.*;

public class QuizShowAboutDialog extends javax.swing.JInternalFrame {

    /** Creates new form QuizShowAboutDialog */
    public QuizShowAboutDialog() {
        super("About JQS", false, true, false, false);

        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);

        initComponents ();
        pack ();
    }

    
    public void  showIt()
    {
        super.show();
    }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     */
    private void initComponents() {
        programNameLabel = new javax.swing.JLabel();
        authorLabel = new javax.swing.JLabel();
        versionLabel = new javax.swing.JLabel();
        copyrightLabel = new javax.swing.JLabel();
        disclaimerLabel = new javax.swing.JLabel();
        closeButton = new javax.swing.JButton();
        
	GridBagConstraints	gridBagConstraints;

        setResizable(false);
        setBackground(java.awt.Color.lightGray);

        getContentPane().setLayout(new GridBagLayout());

        programNameLabel.setText("jQuizShow");
        programNameLabel.setForeground(java.awt.Color.blue);
        programNameLabel.setBackground(java.awt.Color.white);
        programNameLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        programNameLabel.setFont(new java.awt.Font("Dialog", 1, 36));

	gridBagConstraints = new GridBagConstraints();
	gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
	gridBagConstraints.weightx = 10.0;
	gridBagConstraints.weighty = 10.0;

        getContentPane().add(programNameLabel, gridBagConstraints);
        
        authorLabel.setText("by Steven D. Chen");
        authorLabel.setForeground(java.awt.Color.black);
        authorLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

	gridBagConstraints = new GridBagConstraints();
	gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
	gridBagConstraints.weightx = 10.0;
	gridBagConstraints.weighty = 10.0;
        getContentPane().add(authorLabel, gridBagConstraints);
        
        versionLabel.setText("<html><h3>jQuizShow version " + Main.JQS_VERSION + "</h3>");
        versionLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

	gridBagConstraints = new GridBagConstraints();
	gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
	gridBagConstraints.weightx = 10.0;
	gridBagConstraints.weighty = 10.0;
        getContentPane().add(versionLabel, gridBagConstraints);
        
        copyrightLabel.setText("Copyright (C) 2001-2007 Steven D. Chen");
        copyrightLabel.setForeground(java.awt.Color.black);
        copyrightLabel.setBackground(java.awt.Color.white);
        copyrightLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

	gridBagConstraints = new GridBagConstraints();
	gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
	gridBagConstraints.weightx = 10.0;
	gridBagConstraints.weighty = 10.0;
        getContentPane().add(copyrightLabel, gridBagConstraints);
        
        disclaimerLabel.setText("<html><center><h3>jQuizShow comes with ABSOLUTELY NO WARRANTY.</h3>"
		+ "This is free software, and you are welcome to distribute it"
                + "<br>under certain conditions; see the LICENSE.txt for details.</center>");
        disclaimerLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

	gridBagConstraints = new GridBagConstraints();
	gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
	gridBagConstraints.weightx = 10.0;
	gridBagConstraints.weighty = 10.0;
        getContentPane().add(disclaimerLabel, gridBagConstraints);
        
        closeButton.setText(Main.getMessage("label_close"));
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });
        
	gridBagConstraints = new GridBagConstraints();
	gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
	gridBagConstraints.weightx = 10.0;
	gridBagConstraints.weighty = 10.0;
	gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        getContentPane().add(closeButton, gridBagConstraints);
        
    }


    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {
        setVisible(false);
    }



    private javax.swing.JLabel programNameLabel;
    private javax.swing.JLabel versionLabel;
    private javax.swing.JLabel authorLabel;
    private javax.swing.JLabel copyrightLabel;
    private javax.swing.JLabel disclaimerLabel;
    private javax.swing.JLabel distribution1Label;
    private javax.swing.JLabel distribution2Label;
    private javax.swing.JButton closeButton;

}
