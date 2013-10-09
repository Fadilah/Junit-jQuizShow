/*
 * MainQuizUI.java
 *
 * Created on January 26, 2004, 9:54 PM
 *
 * $Id: MainQuizUI.java,v 1.2 2007/02/05 03:55:49 sdchen Exp $
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
 *    $Log: MainQuizUI.java,v $
 *    Revision 1.2  2007/02/05 03:55:49  sdchen
 *    Removed CR (Ctrl-M) from lines.
 *
 *    Revision 1.1  2004/04/02 06:02:00  sdchen
 *    Snapshot of jQuizShow 1.1 alpha06 development
 *
 *
 */

package jQuizShow.quiz;

/**
 *
 * @author  Steven D. Chen
 */

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.imageio.*;
import javax.swing.*;
import javax.swing.event.*;

import jQuizShow.*;
import jQuizShow.event.*;
import jQuizShow.quiz.*;
import jQuizShow.net.*;
import jQuizShow.util.*;


public class MainQuizUI
        extends MainSubset
        implements 
            GameModeChangeListener,
            GameStateChangeListener,
            GameUpdateListener

{
    private static final int FRAME_MULT = 50;

    private static final String  QUIZ_DEFAULT_SKIN = "quizSkin";

    private static MainQuizUI  m_singletonInstance;


    /**
     * Initializes this Singleton class.  It must be called once prior to
     * the initial use in order to properly set the getInstance() method
     * of the parent class.
     */    
    public static void  initializeClass()
    {
        if (m_singletonInstance == null)
            m_singletonInstance = new MainQuizUI();
        else
        {
            // Set the singleton instance of the MainSubset to "this".
            MainSubset.setInstance(m_singletonInstance);

            // Create the main menu.
            m_singletonInstance.initMainMenu();
        }

        // Set the initial size and position of the main frame.
        {
            Rectangle  bounds = m_desktopPane.getBounds();
            Point    location = new Point(0, 0);
            m_singletonInstance.m_mainFrame.setSize(bounds.width, bounds.height);
            m_singletonInstance.m_mainFrame.setLocation(location);
        }
        
        m_singletonInstance.m_mainFrame.show();

        return;
    }
    
    
    /** Private constructor -- only callable from within the
     * class.  This is called once when the singleton instance is
     * created.  Thus, any one-time only initialization should be done
     * here.
     */
    private MainQuizUI()
    {
        m_singletonInstance = this;
        
        // Set the singleton instance of the MainSubset to "this".
        MainSubset.setInstance(m_singletonInstance);

        // Get a reference to Main
        m_main = Main.getInstance();

        // Get the GameConfig singleton instance
        m_gameConfig = GameConfig.getInstance();
              
        // Get a reference to the main desktop pane
        m_desktopPane = Main.getDesktopPane();

        // Initialize the SoundPlayer instance.  This will preload the
	// sound effects.
        SoundPlayer  soundPlayer = SoundPlayer.getInstance();

        // Get the PacketProcessor instance
        PacketProcessorQuiz.initialize();
        m_packetProcessor = PacketProcessor.getInstance();
         
        // Get the SoundPlayer singleton
        m_soundPlayer = SoundPlayer.getInstance();
        
        // Create the game controller instance
        m_quizController = QuizController.getInstance();
        
        // Get the QuizState instance
        m_quizState = QuizState.getInstance(false);

        // Add event listeners
        m_quizState.addGameStateChangeListener(this);
        m_quizState.addGameModeChangeListener(this);
        m_quizState.addGameUpdateListener(this);

        // Set the default quiz delay values
        m_quizState.setDelayValue(QuizState.DELAY_AT_START,
                m_gameConfig.getIntConfig("quizDelayAtStart", 2));
        m_quizState.setDelayValue(QuizState.DELAY_AFTER_QUESTION,
                m_gameConfig.getIntConfig("quizDelayAfterQuestion", 5));
        m_quizState.setDelayValue(QuizState.DELAY_AFTER_TIMER_EXPIRES,
                m_gameConfig.getIntConfig("quizDelayAfterTimerExpires", 5));
   
        // Create an instace of the EventPacket
        m_eventPacket = new EventPacket();
        
        // Create the vector for the quiz skins
        m_quizSkins = new Vector();

        // Create the SoundCtrlDialog
        m_soundCtrlDialog = new SoundCtrlDialog();
        addWithOffset(m_soundCtrlDialog);

        // Create the QuizTimerControlDialog
        m_timerCtrlDialog = new QuizTimerControlDialog();
        m_timerCtrlDialog.setSize(500, 300);
        addWithOffset(m_timerCtrlDialog);

        // Create the QuizQuestionNumberDialog
        m_questionNumDialog = new QuizQuestionNumberDialog();
        m_questionNumDialog.setSize(240, 100);
        addWithOffset(m_questionNumDialog);
        
        // Initialize the default skin
        m_quizSkin = new QuizSkin();

        try
        {
            String  defaultSkin = m_gameConfig.getConfig(QUIZ_DEFAULT_SKIN,
                    "standard.skin");
            
            m_quizSkin.load("Standard", defaultSkin);
            
            m_quizSkins.add(m_quizSkin);
        }
        catch (java.io.IOException  io_e)
        {
            System.out.println(io_e.getMessage());
        }
                
        // Create the main frame and its child QuizPanel
        m_mainFrame = new JInternalFrame("QuizScreen", true, false, true, false);
        
        m_quizPanel = new QuizPanel();
        m_quizPanel.setSkin(m_quizSkin);
        
        m_mainFrame.getContentPane().add(m_quizPanel, java.awt.BorderLayout.CENTER);
        
        // Add listener for resize events
        m_desktopPane.addComponentListener(new java.awt.event.ComponentAdapter()
        {
            public void componentResized(java.awt.event.ComponentEvent evt)
            {
                Dimension   newSize = new Dimension();
                Point       newLocation = new Point();
                
                Component  component = evt.getComponent();

                Dimension  frameSize = component.getSize();

                if (m_mainFrame != null)
                {
                    // Set the initial size and location of the main QuizPanel frame
                    newSize.width = frameSize.width;
                    newSize.height = frameSize.height;
                    newLocation.x = 0;
                    newLocation.y = 0;

                    m_mainFrame.setLocation(newLocation);
                    m_mainFrame.setSize(newSize);
                }
            }
        } );

    }


    /**
     * Primary initialization method.  Called every time this subset is
     * (re)loaded.
     */
    public void  initialize()
    {
        m_desktopPane.add(m_mainFrame);
        
	// Create the main menu.
        initMainMenu();

        // Disable the game control menu options
        updateGameControlOptions(false);

        // Load the default questions from the Q & A database
        loadQuestions(m_gameConfig.getConfig("quizFile",
		"Data/Quiz.txt"));
    }


    /**
     * Starts a new game.
     */
    public void  startNewGame()
    {
        // Show the main window
        m_mainFrame.setVisible(true);

        m_quizController.setShowAnswersMode(showAnswersMode.isSelected());

        m_quizController.startGame();
    }


    /**
     * Uninstalls this subset from the system.  This is called prior to
     * switching game modes to "close" the subset in preparation for
     * loading a new subset.
     */
    public void uninstall() {
        m_quizController.stopGame();
        
        m_desktopPane.remove(m_mainFrame);
        
        if (m_cloneWindow != null)
            m_cloneWindow.setVisible(false);
    }    

    
    /**
     * Loads a new question database for the subset.
     */
    public void  loadQuestionDatabase()
    {
        String  filename;
        
        if (m_fileChooser == null)
	{
	    /* Create a new JFileChooser instance and initialize it
	     * to start in the "user.dir", or if undefined, the
	     * "user.home" directory.
	     */
            m_fileChooser = new JFileChooser(System.getProperty("user.dir",
	            System.getProperty("user.home")));
	}
        
        int  returnVal = m_fileChooser.showOpenDialog(m_desktopPane);
        
        if (returnVal == JFileChooser.APPROVE_OPTION)
        {
            File file = m_fileChooser.getSelectedFile();

            int  numQuestions = loadQuestions(file.getAbsolutePath());
            
            if (numQuestions > 0)
	    {
		Object  args[] =
			{
			    new Integer(numQuestions)
			};

	    	String	   msg = Main.getMessage("msg_questions_read", args);

                JOptionPane.showMessageDialog(m_desktopPane, msg);
	    }
        }
    }

    
    /** Load the questions from the specified database file */
    public int  loadQuestions(String  filename)
    {
	Object  args[] =
		{
		    filename
		};
        try
        {
            int  numQuestions = m_quizController.loadQuestions(filename);
            
            if (numQuestions <= 0)
            {
                String  message = Main.getMessage("warn_no_questions_found",
			args);

                System.out.println(message);

                JOptionPane.showMessageDialog(m_desktopPane, message);
            }
            
            return numQuestions;
        }
        catch (FileNotFoundException  e)
        {
            String  message = Main.getMessage("warn_question_file_not_found",
		    args);

            System.out.println(message);

            JOptionPane.showMessageDialog(m_desktopPane, message);
	}
        catch (IOException  e)
        {
            String  message = Main.getMessage("warn_question_file_io",
		    args);

            System.out.println(message);
            System.out.println(e.getMessage());

            JOptionPane.showMessageDialog(m_desktopPane, message);
        }

        return 0;
    }

    
    /**
     * Listener for GameModeChangeEvents
     */
    public void gameModeChanged(GameModeChangeEvent evt) {
        GameModeEnum  evtType = evt.getType();
        
        if (evtType == GameModeEnum.STANDALONE ||
                evtType == GameModeEnum.MASTER)
        {
            // Enable control menus in standalone/master mode
            updateGameControlOptions(false);
        }
        else if (evtType == GameModeEnum.SLAVE)
        {
            // Disble control menus in slave mode
            updateGameControlOptions(false);
        }

        
        return;
    }
    
    /**
     * Listener for GameStateChangeEvents
     */
    public void  gameStateChanged(GameStateChangeEvent  evt)
    {
        GameStateChangeEnum  type = evt.getType();
        GameModeEnum  mode = m_quizState.getGameMode();

        if (type == QuizStateEnum.IDLE)
        {
            updateGameControlOptions(false);
        }
        else if (type == QuizStateEnum.NEW_GAME && mode != GameModeEnum.SLAVE)
        {
            updateGameControlOptions(true);
        }
        else if (type == QuizStateEnum.END_OF_GAME)
        {
            updateGameControlOptions(false);

            // Hide the windows
            m_mainFrame.setVisible(false);

            if (m_cloneWindow != null)
                m_cloneWindow.setVisible(false);
        }
    }

    
    /** Listener for GameUpdateEvents
     */
    public void gameUpdated(GameUpdateEvent evt) {
        QuizUpdateEnum  type = (QuizUpdateEnum) evt.getType();
    }
    

    
    /** Enable/disable the game controller menu options.  Used when switching
     * between Master and Slave modes
     */
    private void  updateGameControlOptions(boolean  state)
    {
        resetLifeline.setEnabled(state);
        setLevel.setEnabled(state);
        
        return;
    }

    
    private void  addWithOffset(JInternalFrame  component)
    {
        m_desktopPane.add(component);
        component.setLocation(m_frameOffset * FRAME_MULT, m_frameOffset * FRAME_MULT);
        m_frameOffset++;
    }
        

    /** This method is called from within the constructor to
     * initialize the main menu bar for this UI.
     */
    private void initMainMenu() {
        mainMenuBar = new MainMenuBar();
        
        normalQuiz = new javax.swing.JRadioButtonMenuItem();
        showAnswers = new javax.swing.JRadioButtonMenuItem();
        showAnswersMode = new javax.swing.JRadioButtonMenuItem();
        quizModeButtonGroup = new javax.swing.ButtonGroup();
        optionsMenu = new javax.swing.JMenu();
        cloneWindow = new javax.swing.JMenuItem();
        resetLifeline = new javax.swing.JMenuItem();
        setLevel = new javax.swing.JMenuItem();
        questionTimer = new javax.swing.JMenuItem();
        soundCtrl = new javax.swing.JMenuItem();
        skinsMenu = new javax.swing.JMenu();
        helpMenu = new javax.swing.JMenu();
        usingJQS = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator3 = new javax.swing.JSeparator();
        aboutJQS = new javax.swing.JMenuItem();

        optionsMenu.setText(Main.getMessage("menu_options"));
        
        normalQuiz.setText(Main.getMessage("menu_quiz_normal"));
        normalQuiz.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quizModeActionPerformed(evt);
            }
        });
        normalQuiz.setSelected(true);
        
        quizModeButtonGroup.add(normalQuiz);
        
        optionsMenu.add(normalQuiz);
        
        showAnswers.setText(Main.getMessage("menu_show_answers"));
        showAnswers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quizModeActionPerformed(evt);
            }
        });

        quizModeButtonGroup.add(showAnswers);
        
        optionsMenu.add(showAnswers);
        
        showAnswersMode.setText(Main.getMessage("menu_answer_mode"));
        showAnswersMode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quizModeActionPerformed(evt);
            }
        });

        quizModeButtonGroup.add(showAnswersMode);
        
        optionsMenu.add(showAnswersMode);

        optionsMenu.add(jSeparator1);
        
        cloneWindow.setText(Main.getMessage("menu_clone_window"));
        cloneWindow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (m_cloneWindow == null)
                {
                    m_cloneWindow = new JFrame("jQuizShow - Quiz Window");
                    Rectangle  bounds = m_desktopPane.getBounds();
                    Point    location = new Point(0, 0);
                    m_cloneWindow.setSize(bounds.width, bounds.height);
                    m_cloneWindow.setLocation(location);
        
                    m_quizPanelClone = new QuizPanel();
                    m_quizPanelClone.setSkin(m_quizSkin);

                    m_cloneWindow.getContentPane().add(m_quizPanelClone, java.awt.BorderLayout.CENTER);
                }
                
                m_cloneWindow.setVisible(true);
            }
        });
        
        optionsMenu.add(cloneWindow);

        optionsMenu.add(jSeparator2);
        
        setLevel.setText(Main.getMessage("menu_set_level"));
        setLevel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_questionNumDialog.setVisible(true);
                m_questionNumDialog.toFront();
            }
        });
        
        optionsMenu.add(setLevel);
        
        questionTimer.setText(Main.getMessage("menu_set_question_timer_time"));
        questionTimer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                questionTimerActionPerformed(evt);
            }
        });
        
        optionsMenu.add(questionTimer);
        
        soundCtrl.setText(Main.getMessage("menu_sound"));
        soundCtrl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                soundCtrlActionPerformed(evt);
            }
        });
        
        optionsMenu.add(soundCtrl);

        skinsMenu.setText(Main.getMessage("menu_skins"));
        skinsMenu.setEnabled(false);

        for (Iterator  iter = m_quizSkins.iterator(); iter.hasNext(); /**/)
        {
            QuizSkin    skin = (QuizSkin) iter.next();

            JMenuItem  menuItem = new JMenuItem();
            
            menuItem.setText(skin.getName());
            menuItem.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    skinsActionPerformed(evt);
                }
            });
            
            skinsMenu.add(menuItem);
        }
        
        optionsMenu.add(skinsMenu);
        
        mainMenuBar.add(optionsMenu);
        
        helpMenu.setText(Main.getMessage("menu_help"));

        usingJQS.setText(Main.getMessage("menu_using_jqs"));
        usingJQS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                usingJQSActionPerformed(evt);
            }
        });
        
        mainMenuBar.add(helpMenu);
                      
        m_main.setJMenuBar(mainMenuBar);
    }


    private void usingJQSActionPerformed(java.awt.event.ActionEvent evt) {
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
            
            addWithOffset(m_helpFrame);

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
        m_helpFrame.toFront();
    }


    private void quizModeActionPerformed(java.awt.event.ActionEvent evt) {
        if (evt.getSource() == normalQuiz)
        {
            m_quizController.setShowAnswers(false);
            m_quizController.setShowAnswersMode(false);
        }
        else if (evt.getSource() == showAnswers)
        {
            m_quizController.setShowAnswers(true);
            m_quizController.setShowAnswersMode(false);
        }
        else if (evt.getSource() == showAnswersMode)
        {
            m_quizController.setShowAnswers(false);
            m_quizController.setShowAnswersMode(true);
        }
    }

    private void soundCtrlActionPerformed(java.awt.event.ActionEvent evt) {
        m_soundCtrlDialog.show();
        m_soundCtrlDialog.toFront();
    }

    private void questionTimerActionPerformed(java.awt.event.ActionEvent evt) {
        m_timerCtrlDialog.show();
        m_timerCtrlDialog.toFront();
    }

    private void skinsActionPerformed(java.awt.event.ActionEvent evt) {

    }
    
    
    private Main  m_main;
    
    private JMenuBar  mainMenuBar;
    private JMenu  optionsMenu;
    private JRadioButtonMenuItem  normalQuiz;
    private JRadioButtonMenuItem  showAnswers;
    private JRadioButtonMenuItem  showAnswersMode;
    private ButtonGroup  quizModeButtonGroup;
    private JMenuItem  setQuestion;
    private JMenuItem  cloneWindow;
    private JMenuItem  resetLifeline;
    private JMenuItem  setLevel;
    private JMenuItem  questionTimer;
    private JMenuItem  soundCtrl;
    private JMenu  skinsMenu;
    private JMenuItem  standardSkin;
    private JMenu  helpMenu;
    private JMenuItem  usingJQS;
    private JSeparator  jSeparator1;
    private JSeparator  jSeparator2;
    private JSeparator  jSeparator3;
    private JMenuItem  aboutJQS;

    private JInternalFrame  m_mainFrame;
    
    private static MainQuizUI  m_quizShow;

    private static JDesktopPane  m_desktopPane;
    
    private static MessageWindow   m_messageWindow;

    private static GameInputEvent  m_gameInputEvent;

    private static JFrame  m_cloneWindow = null;
    
    private QuizController  m_quizController;

    private QuizPanel  m_quizPanel;
    private QuizPanel  m_quizPanelClone;

    private QuizQuestionNumberDialog  m_questionNumDialog;
    
    private QuizTimerControlDialog  m_timerCtrlDialog;
    
    private SoundCtrlDialog  m_soundCtrlDialog;

    private ConnectionDialog  m_connectionDialog;

    private static GameConfig      m_gameConfig;
    
    private QuizState       m_quizState;

    private JInternalFrame  m_lifelineTimerFrame;
    
    private SoundPlayer  m_soundPlayer;
    
    private PacketProcessor  m_packetProcessor;

    private EventPacket  m_eventPacket;
    
    private JInternalFrame  m_helpFrame;
    private JTextArea  m_helpPane;
    private JFileChooser  m_fileChooser;

    private Vector  m_quizSkins;
    private QuizSkin  m_quizSkin;
    
    private static ResourceBundle  m_localeBundle;
    private static ResourceBundle  m_defaultBundle;

    private static MessageFormat  m_msgFormat;

    private static int  m_debugMode;
    
    private static int  m_frameOffset = 0;
}
