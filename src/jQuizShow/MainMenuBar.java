/*
 * MainMenuBar.java
 *
 * Created on January 17, 2004  6:51 PM
 *
 * $Id: MainMenuBar.java,v 1.2 2007/02/05 03:55:48 sdchen Exp $
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
 *    $Log: MainMenuBar.java,v $
 *    Revision 1.2  2007/02/05 03:55:48  sdchen
 *    Removed CR (Ctrl-M) from lines.
 *
 *    Revision 1.1  2004/04/02 06:01:59  sdchen
 *    Snapshot of jQuizShow 1.1 alpha06 development
 *
 *
 */

package jQuizShow;

/**
 *
 * @author  Steven D. Chen
 */

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import jQuizShow.event.*;
import jQuizShow.net.*;
import jQuizShow.util.*;
import jQuizShow.game.classic.*;
import jQuizShow.quiz.*;

public class MainMenuBar extends JMenuBar
{
    public MainMenuBar()
    {
        initComponents();
        
        m_mainSubset = MainSubset.getInstance();
    }
    

    /** This method is called from within the constructor to preload the
     * menubar with the standard menus (e.g. File, Help).
     */
    private void initComponents() {
        mainMenuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        newGame = new javax.swing.JMenuItem();

        styleMenu = new javax.swing.JMenu();
        classicStyle = new javax.swing.JMenuItem();
        customStyle = new javax.swing.JMenuItem();
        quizStyle = new javax.swing.JMenuItem();
        
        randomPlayer = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        addRemoteDisplay = new javax.swing.JMenuItem();
        loadQuestion = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        exitGame = new javax.swing.JMenuItem();

        viewMenu = new javax.swing.JMenu();
        showMessages = new javax.swing.JMenuItem();

        helpMenu = new javax.swing.JMenu();
        usingJQS = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JSeparator();
        aboutJQS = new javax.swing.JMenuItem();

        /*
         * File menu options
         */
        fileMenu.setText(Main.getMessage("menu_file"));

        newGame.setText(Main.getMessage("menu_new_game"));
        newGame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newGameActionPerformed(evt);
            }
        });
        
        fileMenu.add(newGame);
        
        styleMenu.setText(Main.getMessage("menu_select_style"));

        classicStyle.setText(Main.getMessage("menu_classic_style"));
        classicStyle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                classicStyleActionPerformed(evt);
            }
        });

        styleMenu.add(classicStyle);

        quizStyle.setText(Main.getMessage("menu_quiz_style"));
        quizStyle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quizStyleActionPerformed(evt);
            }
        });

        styleMenu.add(quizStyle);

        customStyle.setText(Main.getMessage("menu_custom_style"));
        customStyle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                customStyleActionPerformed(evt);
            }
        });

        styleMenu.add(customStyle);
        
        fileMenu.add(styleMenu);
        
        randomPlayer.setText(Main.getMessage("menu_random_player"));
        randomPlayer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                randomPlayerActionPerformed(evt);
            }
        });
        
        fileMenu.add(randomPlayer);
        
        fileMenu.add(jSeparator2);
        
        addRemoteDisplay.setText(Main.getMessage("menu_add_remote"));
        addRemoteDisplay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addRemoteDisplayActionPerformed(evt);
            }
        });
        
        fileMenu.add(addRemoteDisplay);
        
        loadQuestion.setText(Main.getMessage("menu_load_questions"));
        loadQuestion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadQuestionActionPerformed(evt);
            }
        });
        
        fileMenu.add(loadQuestion);
        
        fileMenu.add(jSeparator1);
        
        exitGame.setText(Main.getMessage("menu_exit"));
        
        exitGame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitGameActionPerformed(evt);
            }
        });
        
        fileMenu.add(exitGame);
        add(fileMenu);

        /*
         * View menu options
         */
        viewMenu.setText(Main.getMessage("menu_view"));
        showMessages.setText(Main.getMessage("menu_show_msg_win"));
        showMessages.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showMessagesActionPerformed(evt);
            }
        });
        
        viewMenu.add(showMessages);
        add(viewMenu);

        /*
         * Help menu items
         */
        helpMenu.setText(Main.getMessage("menu_help"));
        usingJQS.setText(Main.getMessage("menu_using_jqs"));
        usingJQS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                usingJQSActionPerformed(evt);
            }
        });
        
        helpMenu.add(usingJQS);
        helpMenu.add(jSeparator3);
        aboutJQS.setText(Main.getMessage("menu_about"));
        aboutJQS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutJQSActionPerformed(evt);
            }
        });
        
        helpMenu.add(aboutJQS);
        mainMenuBar.add(helpMenu);        
    }
   

    private void randomPlayerActionPerformed(java.awt.event.ActionEvent evt)
    {
	Main.showRandomPlayerSelector();
    }

    private void loadQuestionActionPerformed(java.awt.event.ActionEvent evt)
    {
        MainSubset.getInstance().loadQuestionDatabase();
    }

    private void usingJQSActionPerformed(java.awt.event.ActionEvent evt)
    {
        if (m_helpFrame == null)
        {
	    String  filename = m_gameConfig.getConfig("helpFile", "JQSHelp.txt");
	    String  helpFilename = filename;

            
            m_helpFrame = new JInternalFrame("Help", true, true, true, true);
            m_helpFrame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
            m_helpFrame.setSize(600, 400);
            
            m_helpPane = new JTextArea();
            m_helpPane.setDisabledTextColor(java.awt.Color.black);
            m_helpPane.setEnabled(false);
            
            JScrollPane  jScrollPanel = new JScrollPane();
            jScrollPanel.setViewportView(m_helpPane);

            m_helpFrame.getContentPane().add(jScrollPanel);
            

            // Locate and read the help file
            try
            {
                String  line;

		helpFilename = FileUtils.searchForFile(filename);
                
                BufferedReader  fin = new BufferedReader(
                        new InputStreamReader(FileUtils.openFile(helpFilename)));
                
                while ((line = fin.readLine()) != null)
                {

                    m_helpPane.append(line + "\n");
                }
            }
	    catch (FileNotFoundException  fnf_e)
	    {
                m_helpPane.append(Main.getMessage("err_help_not_found"));
                m_helpPane.append(fnf_e.getMessage());
	    }
            catch (IOException  e)
            {
		Object  args[] =
			{
			    helpFilename
			};

                m_helpPane.append(Main.getMessage("err_help_file_io", args));
            }
        }

        m_helpFrame.setVisible(true);
    }

    private void addRemoteDisplayActionPerformed(java.awt.event.ActionEvent evt) {
        m_connectionDialog.show();
    }

    private void showMessagesActionPerformed(java.awt.event.ActionEvent evt) {
        Main.showMessageDialog();
    }

    private void aboutJQSActionPerformed(java.awt.event.ActionEvent evt) {
        Main.showAboutJQSDialog();
    }

    private void exitGameActionPerformed(java.awt.event.ActionEvent evt) {
        Main.confirmProgramExit();
    }

    private void newGameActionPerformed(java.awt.event.ActionEvent evt) {
        m_mainSubset.startNewGame();
    }

    private void classicStyleActionPerformed(java.awt.event.ActionEvent evt) {
        // Uninstall the current subset
        m_mainSubset.uninstall();

        // Initialize the subset
        MainClassicUI.initializeClass();
        m_mainSubset = MainSubset.getInstance();

        // Display this subset's name in the main window title
        Main.setTitleString("Classic Mode");
        
        // Initialize the game system.
        m_mainSubset.initialize();
    }

    private void quizStyleActionPerformed(java.awt.event.ActionEvent evt) {
        // Uninstall the current subset
        m_mainSubset.uninstall();

        // Initialize the subset
        MainQuizUI.initializeClass();
        m_mainSubset = MainSubset.getInstance();

        // Display this subset's name in the main window title
        Main.setTitleString("Quiz Mode");
				   
        // Initialize the game system.
        m_mainSubset.initialize();
    }

    private void customStyleActionPerformed(java.awt.event.ActionEvent evt) {
    }


    private JMenuBar  mainMenuBar;
    private JMenu  fileMenu;
    private JMenuItem  newGame;
    private JMenuItem  selectStyle;
    private JMenu  styleMenu;
    private JMenuItem  classicStyle;
    private JMenuItem  quizStyle;
    private JMenuItem  customStyle;
    private JMenuItem  randomPlayer;
    private JSeparator  jSeparator2;
    private JMenuItem  addRemoteDisplay;
    private JMenuItem  loadQuestion;
    private JSeparator  jSeparator1;
    private JMenuItem  exitGame;
    private JMenu  viewMenu;
    private JMenuItem  showMessages;
    private JMenu  helpMenu;
    private JMenuItem  usingJQS;
    private JSeparator  jSeparator3;
    private JMenuItem  aboutJQS;

    private MainSubset  m_mainSubset;
    
    private ConnectionDialog  m_connectionDialog;

    private GameConfig      m_gameConfig;
       
    private JInternalFrame  m_helpFrame;
    private JTextArea  m_helpPane;
    private JFileChooser  m_fileChooser;
}
