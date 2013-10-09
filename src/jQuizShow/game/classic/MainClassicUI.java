/*
 * MainClassicUI.java
 *
 * Created on January 20, 2004, 7:56 PM
 *
 * $Id: MainClassicUI.java,v 1.2 2007/02/05 03:55:49 sdchen Exp $
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
 *    $Log: MainClassicUI.java,v $
 *    Revision 1.2  2007/02/05 03:55:49  sdchen
 *    Removed CR (Ctrl-M) from lines.
 *
 *    Revision 1.1  2004/04/02 06:01:59  sdchen
 *    Snapshot of jQuizShow 1.1 alpha06 development
 *
 *
 */

package jQuizShow.game.classic;

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

import jQuizShow.*;
import jQuizShow.event.*;
import jQuizShow.game.*;
import jQuizShow.net.*;
import jQuizShow.util.*;


public class MainClassicUI
    extends
        MainSubset
    implements
        GameTimerListener,
        GameModeChangeListener,
        GameStateChangeListener,
        GameUpdateListener
        
{
    private static final int FRAME_MULT = 50;

    private static MainClassicUI  m_singletonInstance;

    /**
     * Initializes this Singleton class.  It must be called once prior to
     * the initial use in order to properly set the getInstance() method
     * of the parent class.
     */
    
    public static void  initializeClass()
    {
        if (m_singletonInstance == null)
            m_singletonInstance = new MainClassicUI();
        else
        {
            // Set the singleton instance of the MainSubset to "this".
            MainSubset.setInstance(m_singletonInstance);

            // Create the main menu.
            m_singletonInstance.initMainMenu();
        }
                
        return;
    }
    
    
    /** Private constructor -- only callable from within the
     * class.  This is called once when the singleton instance is
     * created.  Thus, any one-time only initialization should be done
     * here.
     */
    private MainClassicUI()
    {
        m_singletonInstance = this;
        
        // Set the singleton instance of the MainSubset to "this".
        MainSubset.setInstance(m_singletonInstance);

        // Get a reference to Main
        m_main = Main.getInstance();
        
        // Get a reference to the main desktop pane
        m_desktopPane = Main.getDesktopPane();

        // Get the GameConfig singleton instance
        m_gameConfig = GameConfig.getInstance();
        
        // Initialize the SoundPlayer instance.  This will preload the
	// sound effects.
        SoundPlayer  soundPlayer = SoundPlayer.getInstance();

        // Get the PacketProcessor instance
        PacketProcessorGame.initialize();
        m_packetProcessor = PacketProcessor.getInstance();
         
        // Get the SoundPlayer singleton
        m_soundPlayer = SoundPlayer.getInstance();
        
        // Create the game controller instance
        m_gameController = GameController.getInstance();

        
        // Get the GameState instance
        m_gameState = GameState.getInstance(false);

        // Add event listeners
        m_gameState.addGameModeChangeListener(this);
        m_gameState.addGameStateChangeListener(this);
        m_gameState.addGameUpdateListener(this);
   
        // Create an instance of the EventPacket
        m_eventPacket = new EventPacket();

        // Create the JInternalFrame components that make up the actual game
        // display(s).
        {
            // Create and set the initial size and location of the GameScreen
            Dimension  screenSize = m_desktopPane.getSize();

            Dimension   initSize = new Dimension();

            Point       initLocation = new Point();

            {
                initSize.width = screenSize.width;
                initSize.height = (int) (screenSize.height * 0.4);
                initLocation.x = 0;
                initLocation.y = screenSize.height - initSize.height;

                m_gameScreen = new GameScreen(initLocation, initSize, m_gameState);
            }
            
            // Create and set the initial size and location of the ScoreScreen
            {
                initSize.width = (int) (screenSize.width * 0.25);
                initSize.height = (int) (screenSize.height * 0.6);
                initLocation.x = (int) (screenSize.width * 0.75);
                initLocation.y = 0;

                m_scoreScreen = new ScoreScreen(initLocation, initSize, m_gameState);
            }

            // Create and set the initial size and location of the Lifeline timer
            {
                // Set the initial size and location of the ScoreScreen
                initSize.height = (int) (screenSize.height * 0.3f);
                initSize.width = initSize.height;

                m_lifelineTimerFrame = new JInternalFrame("Timer", true, false, false, false);

                m_lifelineTimerFrame.setSize(initSize);
                m_lifelineTimerFrame.setLocation(0, 0);
                m_lifelineTimerFrame.setVisible(false);

                m_lifelineTimerFrame.setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);

                m_lifelineTimer = new GameTimer();
                m_lifelineTimer.addGameTimerListener(this);

                m_lifelineTimerFrame.getContentPane().add(m_lifelineTimer);

                // Listen for frame hidden event -- VetoableChangeListener did not work right in 1.3.1
                m_lifelineTimerFrame.addComponentListener(new ComponentAdapter()
                {
                    public void componentHidden(ComponentEvent  event)
                    {
                        m_lifelineTimer.stopTimer();
                        m_lifelineTimerFrame.setVisible(false);
                    }
                } );
            }
        }

        
        // Add listener for resize events
        m_desktopPane.addComponentListener(new java.awt.event.ComponentAdapter()
        {
            public void componentResized(java.awt.event.ComponentEvent evt)
            {
                Dimension   newSize = new Dimension();
                Point       newLocation = new Point();
                
                Component  component = evt.getComponent();

                Dimension  frameSize = component.getSize();

                if (m_gameScreen != null)
                {
                    // Set the initial size and location of the GameScreen
                    newSize.width = frameSize.width;
                    newSize.height = (int) (frameSize.height * 0.4);
                    newLocation.x = 0;
                    newLocation.y = frameSize.height - newSize.height;

                    m_gameScreen.setLocation(newLocation);
                    m_gameScreen.setSize(newSize);
                }
                
                if (m_scoreScreen != null)
                {
                    // Set the initial size and location of the ScoreScreen
                    newSize.width = (int) (frameSize.width * 0.25);
                    newSize.height = (int) (frameSize.height * 0.6);
                    newLocation.x = (int) (frameSize.width * 0.75);
                    newLocation.y = 0;

                    m_scoreScreen.setLocation(newLocation);
                    m_scoreScreen.setSize(newSize);
                }
            }
        } );

        // Instantiate the dialogs
        m_lifelineDialog = new LifeLineOverrideDialog();
        addWithOffset(m_lifelineDialog);

        m_levelDialog = new GameLevelDialog();
        addWithOffset(m_levelDialog);

        m_questionTimerDialog = new QuestionTimerControlDialog();
        addWithOffset(m_questionTimerDialog);

        m_soundCtrlDialog = new SoundCtrlDialog();
        addWithOffset(m_soundCtrlDialog);

        m_aboutDialog = new QuizShowAboutDialog();
        addWithOffset(m_aboutDialog);
    }

    /**
     * Primary initialization method.  Called every time this subset is
     * (re)loaded.
     */
    public void  initialize()
    {
        // Load the questions from the Q & A database
        loadQuestions(m_gameConfig.getConfig("questionFile",
		"Data/MillionaireDefaultUS.txt"));

        m_desktopPane.add(m_gameScreen);
        m_desktopPane.add(m_scoreScreen);
        m_desktopPane.add(m_lifelineTimerFrame);

	// Create the main menu.
        initMainMenu();

        // Disable the game control menu options
        updateGameControlOptions(false);
    }

    
    /**
     * Starts a new game.
     */
    public void  startNewGame()
    {
        m_gameController.startGame();
        m_gameScreen.show();
        m_scoreScreen.show();
    }

       
    /**
     * Uninstalls this subset from the system.  This is called prior to
     * switching game modes to "close" the subset in preparation for
     * loading a new subset.
     */
    public void uninstall() {
        m_gameController.stopGame();

        m_desktopPane.remove(m_gameScreen);
        m_desktopPane.remove(m_scoreScreen);
        m_desktopPane.remove(m_lifelineTimerFrame);
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
            int  numQuestions = m_gameController.loadQuestions(filename);
            
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
    
    
    /** Called when a game timer event occurs  */
    public void gameTimerActionPerformed(GameTimerEvent evt) {
        if (evt.getType() == GameTimerEvent.TIMER_ELAPSED)
        {
        }
    }
    
    
    /** Called each second while timer is running  */
    public void gameTimerOneSecond(GameTimerEvent evt)
    {
        m_eventPacket.setType(GameUpdateEnumGame.UPDATE_LIFELINE_CLOCK);
        m_eventPacket.setLifelineTimerTime(m_lifelineTimer.getTime());
        
        m_packetProcessor.processEventPacket(m_eventPacket);
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
            newPlayer.setEnabled(true);
            updateGameControlOptions(false);
        }
        else if (evtType == GameModeEnum.SLAVE)
        {
            // Disble control menus in slave mode
            newPlayer.setEnabled(false);
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
        GameModeEnum  mode = m_gameState.getGameMode();

        if (type == GameStateEnum.IDLE)
        {
            updateGameControlOptions(false);
        }
        else if (type == GameStateEnum.NEW_GAME && mode != GameModeEnum.SLAVE)
        {
            updateGameControlOptions(true);
        }
        else if (type == GameStateEnum.PHONE_A_FRIEND)
        {
            // Set the timer parameters and make it visible
            m_lifelineTimer.setTimer(m_gameState.getLifelineTimerLimit(),
                    0, true);

            m_lifelineTimerFrame.setVisible(true);
        }
        else if (type == GameStateEnum.LIFELINE_END)
        {
            m_lifelineTimerFrame.setVisible(
                    m_gameState.getToggleState(GameState.LIFELINE_TIMER_SHOWN));
        }
        else if (type == GameStateEnum.END_OF_GAME)
        {
            updateGameControlOptions(false);

            // Hide the game windows
            m_gameScreen.setVisible(false);
            m_scoreScreen.setVisible(false);
        }
    }

    
    /** Listener for GameUpdateEvents
     */
    public void gameUpdated(GameUpdateEvent evt) {
        GameUpdateEnumGame  type = (GameUpdateEnumGame) evt.getType();
        
        if (type == GameUpdateEnumGame.UPDATE_LIFELINE_CLOCK)
        {
            m_lifelineTimer.setTime(m_gameState.getLifelineTimerTime());
        }
        else if (type == GameUpdateEnumGame.TOGGLE_STATE)
        {            
            m_lifelineTimerFrame.setVisible(
                    m_gameState.getToggleState(GameState.LIFELINE_TIMER_SHOWN));
        }
    }
    

    
    /** Enable/disable the game controller menu options.  Used when switching
     * between Master and Slave modes
     */
    private void  updateGameControlOptions(boolean  state)
    {
        skipQuestion.setEnabled(state);
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
        
        optionsMenu = new javax.swing.JMenu();
        skipQuestion = new javax.swing.JMenuItem();
        resetLifeline = new javax.swing.JMenuItem();
        setLevel = new javax.swing.JMenuItem();
        questionTimer = new javax.swing.JMenuItem();
        soundCtrl = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        usingJQS = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JSeparator();
        aboutJQS = new javax.swing.JMenuItem();
        
        optionsMenu.setText(Main.getMessage("menu_options"));
        skipQuestion.setText(Main.getMessage("menu_skip_question"));
        skipQuestion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                skipQuestionActionPerformed(evt);
            }
        });
        
        optionsMenu.add(skipQuestion);
        resetLifeline.setText(Main.getMessage("menu_reset_helpline"));
        resetLifeline.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetLifelineActionPerformed(evt);
            }
        });
        
        optionsMenu.add(resetLifeline);
        setLevel.setText(Main.getMessage("menu_set_level"));
        setLevel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setLevelActionPerformed(evt);
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
        mainMenuBar.add(optionsMenu);
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


    private void soundCtrlActionPerformed(java.awt.event.ActionEvent evt) {
        m_soundCtrlDialog.show();
        m_soundCtrlDialog.toFront();
    }

    private void questionTimerActionPerformed(java.awt.event.ActionEvent evt) {
        m_questionTimerDialog.show();
        m_questionTimerDialog.toFront();
    }

    private void aboutJQSActionPerformed(java.awt.event.ActionEvent evt) {
        m_aboutDialog.show();
        m_aboutDialog.toFront();
    }

    private void setLevelActionPerformed(java.awt.event.ActionEvent evt) {
        m_levelDialog.show();
        m_levelDialog.toFront();
    }

    private void resetLifelineActionPerformed(java.awt.event.ActionEvent evt) {
        m_lifelineDialog.show();
        m_lifelineDialog.toFront();
    }

    private void skipQuestionActionPerformed(java.awt.event.ActionEvent evt) {
        // Discards the current question.  Does not otherwise affect the game state.
        if (JOptionPane.showInternalConfirmDialog(m_desktopPane,
	        Main.getMessage("msg_discard_question"),
                Main.getMessage("title_confirm_discard"),
		JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) ==
                JOptionPane.YES_OPTION)
        {
            m_gameController.discardQuestion(-1);
        }
    }
    
    
    private Main  m_main;
    
    private JMenuBar  mainMenuBar;
    private JMenu  fileMenu;
    private JMenuItem  newPlayer;
    private JMenuItem  randomPlayer;
    private JSeparator  jSeparator2;
    private JMenuItem  addRemoteDisplay;
    private JMenuItem  loadQuestion;
    private JSeparator  jSeparator1;
    private JMenuItem  exitGame;
    private JMenu  viewMenu;
    private JMenuItem  showMessages;
    private JMenu  optionsMenu;
    private JMenuItem  skipQuestion;
    private JMenuItem  resetLifeline;
    private JMenuItem  setLevel;
    private JMenuItem  questionTimer;
    private JMenuItem  soundCtrl;
    private JMenu  helpMenu;
    private JMenuItem  usingJQS;
    private JSeparator  jSeparator3;
    private JMenuItem  aboutJQS;
    
    private static MainClassicUI  m_quizShow;

    private static JDesktopPane  m_desktopPane;
    
    private static MessageWindow   m_messageWindow;
    
    private final String    m_configFile = "config.ini";

    private GameScreen      m_gameScreen = null;
    
    private ScoreScreen     m_scoreScreen;
    
    private QuizShowAboutDialog  m_aboutDialog;
    
    private LifeLineOverrideDialog  m_lifelineDialog;
    
    private GameLevelDialog  m_levelDialog;

    private QuestionTimerControlDialog  m_questionTimerDialog;

    private SoundCtrlDialog  m_soundCtrlDialog;

    private GameConfig      m_gameConfig;
    
    private GameState       m_gameState;
 
    private GameController  m_gameController;

    private JInternalFrame  m_lifelineTimerFrame;

    private GameTimer  m_lifelineTimer;
    
    private SoundPlayer  m_soundPlayer;
    
    private PacketProcessor  m_packetProcessor;

    private EventPacket  m_eventPacket;
    
    private JInternalFrame  m_helpFrame;
    private JTextArea  m_helpPane;
    private JFileChooser  m_fileChooser;

    private static ResourceBundle  m_localeBundle;
    private static ResourceBundle  m_defaultBundle;

    private static MessageFormat  m_msgFormat;

    private static int  m_debugMode;
    
    private static int  m_frameOffset = 0;
}
