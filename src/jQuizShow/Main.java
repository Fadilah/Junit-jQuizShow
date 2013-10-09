/*
 * Main.java
 *
 * Created on April 1, 2001, 2:56 PM
 *
 * $Id: Main.java,v 1.4 2007/02/05 04:54:26 sdchen Exp $
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
 *    $Log: Main.java,v $
 *    Revision 1.4  2007/02/05 04:54:26  sdchen
 *    Updated copyright date range.
 *
 *    Revision 1.3  2007/02/05 04:05:34  sdchen
 *    Replaced deprecated show() with setVisible(bool  s)
 *
 *    Revision 1.2  2007/02/05 03:55:48  sdchen
 *    Removed CR (Ctrl-M) from lines.
 *
 *    Revision 1.1  2004/04/02 06:01:59  sdchen
 *    Snapshot of jQuizShow 1.1 alpha06 development
 *
 *    Revision 1.11  2002/08/15 04:43:26  sdchen
 *    Internationalization of source code.  Main.getMessage() is the primary
 *    routine to get the localized message strings.
 *
 *    Revision 1.10  2002/07/29 04:19:37  sdchen
 *    Removed unneeded "javax.swing." prefix for class data declarations.
 *
 *    Revision 1.9  2002/07/14 05:31:07  sdchen
 *    Snapshot of conversion to InputMap/KeyMap/ActionMap for keyboard hot keys.
 *
 *    Revision 1.8  2002/07/05 06:54:59  sdchen
 *    Put call to Main.appendMessage() into a Runnable passed into
 *    SwingUtilities.invokeLater() so that it would be invoked in the event
 *    thread and thus append properly to the associated JText.  Removed
 *    desktopPane var -- changed all references to use m_desktopPane.
 *
 *    Revision 1.7  2002/06/22 19:57:31  sdchen
 *    Removed unused m_questionList.
 *
 *    Revision 1.6  2002/06/18 05:00:58  sdchen
 *    Fixed NullPointerException on startup -- attempted to execute setStatusLabel()
 *    prior to initialization of dependent variable.
 *
 *    Revision 1.5  2002/06/06 04:50:03  sdchen
 *    Made setStatusLabel public static.  Changed to start JFileChooser in "user.dir"
 *    or "user.home" if "user.dir" undefined.  Changed to use "questionEncoding"
 *    if specified to read in character encoded files.
 *
 *    Revision 1.4  2002/05/28 03:44:57  sdchen
 *    Fixed bug loading JQSHelp.txt from JAR file.
 *
 *    Revision 1.3  2002/05/28 00:24:54  sdchen
 *    Added code to output Exception.getMessage() when config file read files.
 *    Also, added -sysprops option to output the system properties to aid in
 *    debugging.
 *
 *    Revision 1.2  2002/05/23 05:05:30  sdchen
 *    *** empty log message ***
 *
 *    Revision 1.1.1.1  2002/04/13 17:01:40  sdchen
 *    Initial import of the jQuizShow sources from local development directory.
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
import jQuizShow.game.classic.*;
import jQuizShow.quiz.*;
import jQuizShow.net.*;
import jQuizShow.util.*;


public class Main extends JFrame
{
    public static final String JQS_VERSION = "1.1";	// Version number

    private static final int FRAME_MULT = 50;

    private static final String CONTRIB_DIR = "contrib/";

    private static final String RESOURCE_BASENAME = "contrib/jQuizShowLocale";

    private static final String JQS_LOG_FILE = "JQS_log.txt";
    
    private static final Main  m_singletonInstance = new Main();  // Create the singleton instance
    
    /** Gets the singleton instance
     */
    public static Main getInstance() {
        return m_singletonInstance;
    }

    /** Returns a reference to the main desktop pane.
     */
    public static JDesktopPane  getDesktopPane() {
        return m_desktopPane;
    }

    /** Returns a reference to the main desktop pane.
     */
    public static void  setTitleString(String  subset)
    {
        // Display the hostname in the title
        try
        {
            InetAddress  inetAddr = InetAddress.getLocalHost();

            m_singletonInstance.setTitle("jQuizShow - " + subset
                    + " [Hostname: " + inetAddr.getHostName()
                    + " (" + inetAddr.getHostAddress() + ")]");
        }
        catch (UnknownHostException  evt)
        {
            m_singletonInstance.setTitle("jQuizShow - " + subset);
        }
        catch (SecurityException  se)
	{
            m_singletonInstance.setTitle("jQuizShow - " + subset);
	}
    }
    
    
    /** Private constructor -- only callable from within the
     * class.  This is called when the singleton instance is
     * created.
     */

    private Main() {

	// Load the default resource bundle
	try
	{
	    m_defaultBundle = ResourceBundle.getBundle(RESOURCE_BASENAME);
           //System.out.println("helo" + RESOURCE_BASENAME);
	    if (m_defaultBundle == null)
	    {
		System.out.println("ERROR!  Default resource bundle (" + RESOURCE_BASENAME + ") is missing");
		System.exit(1);
	    }

	    m_msgFormat = new MessageFormat("");
	}
	catch (MissingResourceException  mr_e)
	{
	    System.out.println("ERROR!  The default resource bundle (" + RESOURCE_BASENAME + ") is missing");
	    System.out.println("        from the installation.  Please reinstall.  Program aborted.");

	    System.exit(1);
	}

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

	// Create the main window and its UI components
        initComponents();

        // Configure the desktopPane instance
        m_desktopPane.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
        
        // Display the Version
        versionLabel.setText("Version " + Main.JQS_VERSION);

     
        // Create the message window
        m_messageWindow = new MessageWindow();
        addWithOffset(m_messageWindow);
    }
    
    /** Primary initialization method.  This will create the top level
      * window and all desktop windows.
      */
    private void  initJQS()
    {
        Toolkit     tk = Toolkit.getDefaultToolkit();

        Dimension   screenSize = tk.getScreenSize();

        Dimension   initSize = new Dimension();
        
        Point       initLocation = new Point();
        
        setLocation(0, 0);
        setSize(screenSize);        // Full screen

	// If specified, use the locale specified in the config file
	if (m_gameConfig.getConfig("localeLanguage") != null)
	{
	    initLocaleResourceBundle(
		    m_gameConfig.getConfig("localeLanguage"),
		    m_gameConfig.getConfig("localeCountry"),
		    m_gameConfig.getConfig("localeVariant"));
	}

        setTitleString("");     // No subset name yet
        
        // Initialize the SoundPlayer instance.  This will preload the
	// sound effects.
        SoundPlayer  soundPlayer = SoundPlayer.getInstance();
   
        // Create an instace of the EventPacket
        m_eventPacket = new EventPacket();
        
        // Get the SoundPlayer singleton
        m_soundPlayer = SoundPlayer.getInstance();

        // Instantiate the dialogs

        m_soundCtrlDialog = new SoundCtrlDialog();
        addWithOffset(m_soundCtrlDialog);

        m_aboutDialog = new QuizShowAboutDialog();
        addWithOffset(m_aboutDialog);

        m_connectionDialog = new ConnectionDialog();
        addWithOffset(m_connectionDialog);

        m_randomSelector = new RandomSelector();
        addWithOffset(m_randomSelector);
    }

    
    /** Load the questions from the specified database file */

    
    /** Sets the "status" label string.  The status label is located on
      * the bottom left border of the main window.
      */
    public static void setStatusLabel(String  text) {
        if (text == null)
            m_quizShow.statusLabel.setText("");
        else
            m_quizShow.statusLabel.setText(text);
    }

    
    /**
     * Appends the string to the message log.  A newline is appended to
     * end of the message.  The System.out() is redefined in main()
     * to call this when debug mode is on.
     */
    private static void  appendMessage(String  message)
    {
        m_messageWindow.append(message + "\n");
    }

    
    /**
     * Called from other classes to show the message dialog.
     */
    public static void showMessageDialog()
    {
        m_messageWindow.show();
    }

    
    /**
     * Called from other classes to show the "About" dialog
     */
    public static void showAboutJQSDialog()
    {
        m_aboutDialog.show();
    }

    
    public void  addWithOffset(JInternalFrame  component)
    {
        m_desktopPane.add(component);
        component.setLocation(m_frameOffset * FRAME_MULT, m_frameOffset * FRAME_MULT);
        m_frameOffset++;
    }
    
    
    private void  loadConfig()
    {
        // Get the GameConfig singleton instance
        m_gameConfig = GameConfig.getInstance();

        String  configFilepath = null;
        
        try
        {
            configFilepath = FileUtils.searchForFile(m_configFile);

            m_gameConfig.loadGameConfig(configFilepath);
            
            m_debugMode = m_gameConfig.getIntConfig("debugMode");
        }
        catch (FileNotFoundException  e)
        {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        catch (IOException  e)
        {
	    Object  args[] =
		    {
			configFilepath
		    };

            System.out.println(Main.getMessage("err_config_file_io", args));
            System.out.println(e.getLocalizedMessage());

            System.exit(1);           
        }
    }
    

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {
        southPanel = new javax.swing.JPanel();
        statusLabel = new javax.swing.JLabel();
        versionLabel = new javax.swing.JLabel();
        m_desktopPane = new javax.swing.JDesktopPane();
        
        setTitle("jQuizShow");
        setBackground(java.awt.Color.black);

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });
        
        southPanel.setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gridBagConstraints1;
        
        statusLabel.setText("");
        statusLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.weightx = 100.0;
        gridBagConstraints1.weighty = 100.0;
        southPanel.add(statusLabel, gridBagConstraints1);
        
        versionLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 2;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints1.weightx = 100.0;
        gridBagConstraints1.weighty = 100.0;
        southPanel.add(versionLabel, gridBagConstraints1);
        
        getContentPane().add(southPanel, java.awt.BorderLayout.SOUTH);
        
        m_desktopPane.setForeground(java.awt.Color.white);
        m_desktopPane.setBackground(java.awt.Color.black);
        getContentPane().add(m_desktopPane, java.awt.BorderLayout.CENTER);
        
        pack();
    }

    public static void  setMenuBar(MainMenuBar  menuBar)
    {
        m_quizShow.setJMenuBar(menuBar);
    }

    public static void  showRandomPlayerSelector()
    {
	String  playersFile = m_gameConfig.getConfig(RandomSelector.PLAYERS_FILE_KEYWORD,
		RandomSelector.PLAYERS_DEFAULT_FILE);

	String  usedPlayersFile = m_gameConfig.getConfig(RandomSelector.USED_PLAYERS_FILE_KEYWORD,
		RandomSelector.USED_PLAYERS_DEFAULT_FILE);

        try
        {
            m_randomSelector.readDatabase(playersFile, usedPlayersFile);
      
	    m_randomSelector.show();
        }
        catch (IOException  e)
        {
	    Object  args[] =
		    {
			playersFile
		    };

	    String	msg = Main.getMessage("err_file_missing_unreadable", args);

            JOptionPane.showMessageDialog(m_desktopPane, msg);
        }
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
    }

    /** Exit the Application */
    private void exitForm(java.awt.event.WindowEvent evt) {
        confirmProgramExit();
    }

    public static void  confirmProgramExit()
    {
        // Confirm the exit
        
        GameConfig  gameConfig = GameConfig.getInstance();
        
        if (gameConfig.getIntConfig("testMode") == 0)
        {
            // Normal mode -- confirm exit
            if (JOptionPane.showConfirmDialog(m_quizShow,
	    	    Main.getMessage("msg_confirm_exit"),
                    Main.getMessage("title_confirm_exit"),
		    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) ==
                    JOptionPane.YES_OPTION)
            {
                System.exit(0);
            }
        }
        else
        {
            // Test mode -- just exit
            System.exit(0);
        }
    }


    /**
    * Gets the main Component
    */
    public static Component  getMain()
    {
        return m_quizShow;
    }


    /**
    * Gets the locale message associated with the specified template key.
    * The key specifies a template which is to be filled using the
    * arguments provided.
    */
    public static String  getMessage(String  key, Object  args[])
    {
        ResourceBundle  localeBundle;

        if (m_localeBundle != null)
	    localeBundle = m_localeBundle;
	else
	    localeBundle = m_defaultBundle;

        try
	{
	    m_msgFormat.applyPattern(localeBundle.getString(key));
	}
	catch (MissingResourceException  mr_e)
	{
	    try
	    {
	        m_msgFormat.applyPattern(m_defaultBundle.getString(key));
	    }
	    catch (MissingResourceException  mr2_e)
	    {
	        System.out.println("Resource bundle template key is undefined:  "
			+ key);

		return "<" + key + ">";
	    }
	}

	return m_msgFormat.format(args);
    }


    /**
    * Gets the locale message associated with the specified key.
    */
    public static String  getMessage(String  key)
    {
        ResourceBundle  localeBundle;

        if (m_localeBundle != null)
	    localeBundle = m_localeBundle;
	else
	    localeBundle = m_defaultBundle;

        try
	{
	    return localeBundle.getString(key);
	}
	catch (MissingResourceException  mr_e)
	{
	    try
	    {
	        return m_defaultBundle.getString(key);
	    }
	    catch (MissingResourceException  mr2_e)
	    {
	        System.out.println("Resource bundle key is undefined:  "
			+ key);
		return "<" + key + ">";
	    }
	}
    }


    /**
    * Loads the locale-specific resource bundle.
    */
    public void  initLocaleResourceBundle(
		    String  language,
		    String  country,
		    String  variant
	    )
    {
        Locale  locale;

	if (country == null)
	    country = "";
	
	if (variant != null)
	    locale = new Locale(language, country, variant);
	else
	    locale = new Locale(language, country);

        try
	{
	    m_localeBundle = ResourceBundle.getBundle(RESOURCE_BASENAME,
	            locale);

	    m_msgFormat.setLocale(locale);
	}
	catch (MissingResourceException  mr_e)
	{
	    Object  args[] =
		    {
			locale.getDisplayName()
		    };

	    System.out.println(Main.getMessage("warn_locale_not_found", args));

	    m_localeBundle = null;

	    m_msgFormat.setLocale(locale);
	}

	return;
    }

    
    
    /**
     * Parse any command line arguments.
     */
    private void  parseArgs(String  args[])
    {
        int		i;

	for (i = 0; i < args.length; i++)
	{
	    Object  arguments[] =
		    {
			args[i]
		    };

	    if (args[i].equals("-sysprops"))
	    {
	        // Output the system properties -- for debugging
		Properties  sysprops = System.getProperties();
		sysprops.list(System.out);

		System.exit(0);
	    }
	    else
	    {
	        System.out.println(Main.getMessage("warn_unknown_argument",
			arguments));
	    }
	}
    }
    
    
    /**
    * Main program entry point
    *
    * @param args the command line arguments
    */
    public static void main(String args[])
    {
        // Output the copyright notice
        System.out.println("");
        System.out.println("jQuizShow " + JQS_VERSION + ", Copyright (C) 2001-2007 Steven D. Chen");
        System.out.println("jQuizShow comes with ABSOLUTELY NO WARRANTY.  This is free software,");
        System.out.println("and you are welcome to redistribute it under certain conditions.");
        System.out.println("See the LICENSE.txt file for details.");
        System.out.println("");

	// Get a reference to the singleton instance of class Main.
        m_quizShow = Main.getInstance();

	// Parse any command line arguments
        m_quizShow.parseArgs(args);

	// Read the game configuration file
        m_quizShow.loadConfig();

        // Modify the standard output stream to redirect stdout messages
        // to the MessageWindow.
        PrintStream  optPrint = new PrintStream(System.out)
        {
            boolean firstPass = true;

            PrintStream  fileOut;
            
            public void println(String s)
            {
	        // This is used to ensure msg is appended in the
		// AWT event dispatching thread since the msg window
		// is a Swing component.
		final String  finalStr = new String(s);

	        Runnable  doAppendMsg = new Runnable()
		{
		    public void run()
		    {
			Main.appendMessage(finalStr);
		    }
		};

		SwingUtilities.invokeLater(doAppendMsg);

                if ((m_debugMode & GameConfig.DEBUG_MSGS_TO_STDOUT) != 0)
		{
                    super.println(s);
		}

                if ((m_debugMode & GameConfig.DEBUG_MSGS_TO_LOG) != 0)
		{
                    if (firstPass)
                    {
                        try
                        {
                            File outputFile = new File(JQS_LOG_FILE);

                            fileOut = new PrintStream(new FileOutputStream(outputFile));
                        }
                        catch (FileNotFoundException  fnf_e)
                        {
                            System.out.println("ERROR -- Failed opening log file:  "
                                    + JQS_LOG_FILE);
                        }

                        firstPass = false;
                    }

                    fileOut.println(s);
                }
            }
        };

        System.setOut(optPrint);

	// Examine the contents of the "CONTRIB_DIR" directory.
	ContribManager  contribMgr = ContribManager.getInstance();

	contribMgr.initialize(CONTRIB_DIR);

	// Initialize the game UI
        m_quizShow.initJQS();

        // Start with the last "style" used, else default to the Classic UI
        MainClassicUI.initializeClass();
        m_quizShow.m_mainSubset = MainSubset.getInstance();

        m_quizShow.setTitleString("Classic Game");
        
        // Initialize the game system.
        m_quizShow.m_mainSubset.initialize();

	// Set up the Main instance to be properly "shown" from
	// the event dispatch thread.
	//
	// Apparently, most examples (even the Java Tech Tips and
	// tutorials) were wrong in executing Frame.show() from
	// the main thread.  See the December 8, 2003 Java Tech
	// Tips from http://java.sun.com for further information.
	Runnable  runJQuizShow = new RunJQuizShow(m_quizShow);
	EventQueue.invokeLater(runJQuizShow);
    }

    // Runnable class that is used to ensure that the JFrame
    // is rendered in the event dispatch thread.
    private static class RunJQuizShow
	implements Runnable
    {
	final Main    m_main;
	
	public RunJQuizShow(Main  main)
	{
	    m_main = main;
	}

	public void run()
	{
	    // Display the main screen
	    m_main.setVisible(true);
	}
    }

    private JPanel  southPanel;
    private JLabel  statusLabel;
    private JLabel  versionLabel;
    
    private static Main  m_quizShow;

    private static MainSubset  m_mainSubset;
    
    private static JDesktopPane  m_desktopPane;
    
    private static MessageWindow   m_messageWindow;

    private static GameInputEvent  m_gameInputEvent;
    
    private final String    m_configFile = "config.ini";
    
    private static QuizShowAboutDialog  m_aboutDialog;
    
    private SoundCtrlDialog  m_soundCtrlDialog;

    private ConnectionDialog  m_connectionDialog;

    private static GameConfig      m_gameConfig;

    private JInternalFrame  m_lifelineTimerFrame;
    
    private SoundPlayer  m_soundPlayer;
    
    private PacketProcessor  m_packetProcessor;

    private EventPacket  m_eventPacket;
    
    private JInternalFrame  m_helpFrame;
    private JTextArea  m_helpPane;
    private static JFileChooser  m_fileChooser;
    private static RandomSelector  m_randomSelector;

    private static ResourceBundle  m_localeBundle;
    private static ResourceBundle  m_defaultBundle;

    private static MessageFormat  m_msgFormat;

    private static int  m_debugMode;
    
    private static int  m_frameOffset = 0;
}
