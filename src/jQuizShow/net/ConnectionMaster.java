/*
 * ConnectionMaster.java
 *
 * Created on June 9, 2001, 3:42 PM
 *
 * $Id: ConnectionMaster.java,v 1.2 2007/01/28 05:17:52 sdchen Exp $
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
 *    $Log: ConnectionMaster.java,v $
 *    Revision 1.2  2007/01/28 05:17:52  sdchen
 *    Made reference to SwingWorker to explicitly use jQuizShow.util.SwingWorker to avoid conflict with javax.util.SwingWorker.
 *
 *    Revision 1.1  2004/04/02 06:01:59  sdchen
 *    Snapshot of jQuizShow 1.1 alpha06 development
 *
 *    Revision 1.2  2002/08/15 04:43:26  sdchen
 *    Internationalization of source code.  Main.getMessage() is the primary
 *    routine to get the localized message strings.
 *
 *    Revision 1.1.1.1  2002/04/13 17:01:43  sdchen
 *    Initial import of the jQuizShow sources from local development directory.
 *
 *
 */

package jQuizShow.net;

/**
 *
 * @author  Steven D. Chen
 * @version 
 */

import java.io.*;
import java.net.*;
import javax.swing.*;

import jQuizShow.*;
import jQuizShow.game.*;
import jQuizShow.util.*;
import jQuizShow.event.*;

public class ConnectionMaster implements ConnectionInterface,
        GameStateChangeListener,
        GameUpdateListener
{
  
    /** Creates new ConnectionMaster */
    public ConnectionMaster(String host, int port) {
        setHostAndPort(host, port);
        
        if (m_main == null)
            m_main = MainSubset.getInstance();
    }

    public void setHostAndPort(String  host, int  port)
    {
        m_hostname = new String(host);
        m_port = port;
    }

    public void start()
    {
        m_done = false;
        
        final jQuizShow.util.SwingWorker worker = new jQuizShow.util.SwingWorker() {
            public Object construct()
            {
                try
                {
                    open();
                }
                catch (IOException  exception)
                {
		    Object  args[] =
			    {
				exception.getLocalizedMessage()
			    };

                    String  message = Main.getMessage("msg_err_master_open",
			    args);

                    JOptionPane.showMessageDialog(Main.getMain(),
			    message,
                            Main.getMessage("title_error"),
			    JOptionPane.ERROR_MESSAGE);

                    return null;
                }

                m_progress = 100;
                
                return this;
            }
        };
        worker.start();
    }
    
    public void stop()
    {
    }
    
    public String getNote()
    {
        return m_note;
    }
    
    public int  getProgress()
    {
        return m_progress;
    }

    public boolean done()
    {
        return m_done;
    }
    
    private void open()
            throws UnknownHostException, IOException
    {
	Object  args[] =
		{
		    m_hostname,
		    new Integer(m_port)
		};

        m_progress = 30;
        m_note = Main.getMessage("msg_master_connect", args);

        System.out.println(m_note);
        
        m_socket = new Socket(m_hostname, m_port);
        
        m_progress = 60;
        m_note = Main.getMessage("msg_master_connect_setup");

        m_objOutput = new ObjectOutputStream(m_socket.getOutputStream());

        // Set to MASTER mode
        m_progress = 80;
        m_note = Main.getMessage("msg_master_setup");

        PacketProcessor  packetProcessor = PacketProcessor.getInstance();
        
        ModeChangePacket  modePacket = new ModeChangePacket();
        modePacket.setMode(GameModeEnum.MASTER);
        
        packetProcessor.processModeChangePacket(modePacket);    // Do it
        
        m_progress = 100;
        m_note = Main.getMessage("msg_master_ready");
        m_done = true;
        
        System.out.println(Main.getMessage("msg_master_done", args));
    }
    

    public void close()
            throws IOException
    {
        IOException    saveException = null;

	Object  args[] =
		{
		    m_hostname,
		    new Integer(m_port)
		};

        
        // Reset to STANDALONE mode
        PacketProcessor  packetProcessor = PacketProcessor.getInstance();
        
        ModeChangePacket  modePacket = new ModeChangePacket();
        modePacket.setMode(GameModeEnum.STANDALONE);
        
        packetProcessor.processModeChangePacket(modePacket);    // Do it

        System.out.println(Main.getMessage("msg_closing_connect", args));

        // Close the connection
        if (m_objOutput != null)
        {
            try {
                m_objOutput.close();
            }
            catch (IOException  e)
            {
                saveException = e;
            }
            finally
            {
                m_objOutput = null;
            }
        }
        
        if (m_socket != null)
        {
            try {
                m_socket.close();
            }
            catch (IOException  e)
            {
                saveException = e;
            }
            finally
            {
                m_socket = null;
            }
        }
        
        if (saveException != null)
            throw saveException;
    }
    
    public void gameStateChanged(GameStateChangeEvent evt)
    {
        if (m_objOutput == null)
            return;
        
        try
        {
            m_objOutput.reset();		// Clear so a new copy of the object will be sent.

            GameStateChangeEnum  eventType = evt.getType();
            
            if (m_statePacket == null)
                m_statePacket = new StatePacket();

            // Fill the packet object with data
/*
            m_statePacket.setType(eventType);
            m_statePacket.setMaxNumberOfLevels(m_gameState.getMaxNumberOfLevels());
            m_statePacket.setCurrentLevel(m_gameState.getCurrentLevel());
            m_statePacket.setLevelTitle(m_gameState.getLevelTitle());
            m_statePacket.setQuestion(m_gameState.getQuestion());
            m_statePacket.setAnswers(m_gameState.getAnswers());
            m_statePacket.setQuestionTimerLimit(m_gameState.getQuestionTimerLimit());
            m_statePacket.setLifelineTimerLimit(m_gameState.getLifelineTimerLimit());
            m_statePacket.setAnswersVisible(m_gameState.getAnswersVisible());
            m_statePacket.setCorrectAnswer(m_gameState.getCorrectAnswer());
*/            
            m_objOutput.writeObject(m_statePacket);
        }
        catch (IOException  e)
        {
	    Object  args[] =
		    {
			m_hostname
		    };

	    String  msg = Main.getMessage("err_sending_state", args);

            System.out.println(msg);
            System.out.println(e.getLocalizedMessage());

            JOptionPane.showMessageDialog(Main.getMain(),
		    msg,
                    Main.getMessage("title_error"),
		    JOptionPane.ERROR_MESSAGE);

            // Clean up
            try {
		System.out.println(Main.getMessage("msg_closing_connect")
			+ " " + m_hostname + ":" + m_port);

                close();
            }
            catch (IOException  exc)
            {
                System.out.println("IOException:  " + exc.getMessage());
            }
        }
    }

    public void gameUpdated(GameUpdateEvent  evt)
    {
        if (m_objOutput == null)
            return;
        
        try
        {
            m_objOutput.reset();		// Clear so a new copy of the object will be sent.

            GameUpdateEnum  eventType = evt.getType();
            
            if (m_eventPacket == null)
                m_eventPacket = new EventPacket();

            // Fill the packet object with data
            m_eventPacket.setType(eventType);
/*
            m_eventPacket.setSelectedAnswer(m_gameState.getSelectedAnswer());
            m_eventPacket.setSelectedLifeline(m_gameState.getSelectedLifeline());
            m_eventPacket.setHighlightLevel(m_gameState.getHighlightLevel());
            m_eventPacket.setTransitionMessage(m_gameState.getTransitionMessage());
            m_eventPacket.setQuestionTimerTime(m_gameState.getQuestionTimerTime());
            m_eventPacket.setLifelineTimerTime(m_gameState.getLifelineTimerTime());
            m_eventPacket.setToggleStates(m_gameState.getToggleStates());
            m_eventPacket.setStatusMessage(m_gameState.getStatusMessage());
*/            
            m_objOutput.writeObject(m_eventPacket);
        }
        catch (IOException  e)
        {
	    String  msg = Main.getMessage("err_sending_state")
		    + " " + m_hostname;

            System.out.println(msg);
            System.out.println(e.getLocalizedMessage());

            JOptionPane.showMessageDialog(Main.getMain(),
		    msg,
                    Main.getMessage("title_error"),
		    JOptionPane.ERROR_MESSAGE);

            // Clean up
            try {
		System.out.println(Main.getMessage("msg_closing_connect")
			+ " " + m_hostname + ":" + m_port);

                close();
            }
            catch (IOException  exc)
            {
                System.out.println("IOException:  "
			+ exc.getLocalizedMessage());
            }
        }
    }

    private MainSubset  m_main;
    
    private String  m_hostname;
    
    private int  m_port;
    
    private String  m_note;
    
    private int  m_progress = 0;

    private boolean  m_done = false;
    
    private Socket m_socket = null;
    
    private ObjectOutputStream  m_objOutput;
    
    private StatePacket     m_statePacket = null;
    private EventPacket     m_eventPacket = null;
    
    private int m_type;
}
