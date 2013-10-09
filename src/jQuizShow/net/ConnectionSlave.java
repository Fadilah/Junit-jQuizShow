/*
 * ConnectionSlave.java
 *
 * Created on June 9, 2001, 6:21 PM
 *
 * $Id: ConnectionSlave.java,v 1.1 2004/04/02 06:01:59 sdchen Exp $
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
 *    $Log: ConnectionSlave.java,v $
 *    Revision 1.1  2004/04/02 06:01:59  sdchen
 *    Snapshot of jQuizShow 1.1 alpha06 development
 *
 *    Revision 1.3  2002/08/15 04:43:26  sdchen
 *    Internationalization of source code.  Main.getMessage() is the primary
 *    routine to get the localized message strings.
 *
 *    Revision 1.2  2002/06/06 04:45:43  sdchen
 *    Made Main.setStatusLabel() static -- resulting change here.
 *
 *    Revision 1.1.1.1  2002/04/13 17:01:42  sdchen
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

public class ConnectionSlave implements ConnectionInterface
{
    static public ConnectionSlave  getInstance(int  myPort)
    {
        ConnectionSlave  instance = getInstance();
        
        instance.setPort(myPort);
        
        return instance;
    }


    static public ConnectionSlave  getInstance()
    {
        if (m_singletonInstance == null)
            m_singletonInstance = new ConnectionSlave();
        
        return m_singletonInstance;
    }

    
    private ConnectionSlave() {
    }
    
    /** Creates new ConnectionSlave */
    private void setPort(int myPort) {
        m_myPort = myPort;
    }

    public int getPort() {
        return m_myPort;
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
                    String  message = "IOException ConnectionSlave.open().  "
                            + exception.getLocalizedMessage();

                    JOptionPane.showMessageDialog(Main.getMain(), message,
                            Main.getMessage("title_error"),
			    JOptionPane.ERROR_MESSAGE);

                    return null;
                }
                
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
    
    public void open()
            throws IOException
    {
        InetAddress  inetAddr;
        
        if (m_serverSocket == null)
            m_serverSocket = new ServerSocket(m_myPort);

        m_progress = 20;
	{
	    Object  args[] =
		    {
			new Integer(m_myPort)
		    };

	    m_note = Main.getMessage("msg_slave_connect", args);
	}
        
        // Wait for a connection from the controller
        m_socket = m_serverSocket.accept();
        
        // Connected.  Get info regarding the remote connection
        inetAddr = m_socket.getInetAddress();
        m_hostname = inetAddr.getHostName();
        m_port = m_socket.getPort();
        
        m_progress = 40;

	{
	    Object  args[] =
		    {
			m_hostname,
		        inetAddr.getHostAddress()
		    };

	    m_note = Main.getMessage("msg_slave_established", args);
	}
        
        Main.setStatusLabel(m_note);
	System.out.println(m_note);
        
        m_objInput = new ObjectInputStream(m_socket.getInputStream());

        m_progress = 60;
        m_note = Main.getMessage("msg_slave_connect_setup");
        
        m_listener = new ConnectionListener(m_objInput);

        // Start the ConnectionListener
        m_progress = 80;
        m_note = Main.getMessage("msg_slave_listener_setup");
        
        m_listener.start();

        // Set to SLAVE mode
        m_progress = 90;
        m_note = Main.getMessage("msg_slave_mode");

        PacketProcessor  packetProcessor = PacketProcessor.getInstance();
        
        ModeChangePacket  modePacket = new ModeChangePacket();
//        modePacket.setMode(GameModeEnum.SLAVE);
        
        packetProcessor.processModeChangePacket(modePacket);    // Do it
        
        m_progress = 100;
        m_note = Main.getMessage("msg_slave_ready");
        m_done = true;

        return;
    }
    
    public boolean isConnected() {
        return (m_objInput != null);
    }
    
    public void close()
            throws IOException
    {
        // Reset to STANDALONE mode
        PacketProcessor  packetProcessor = PacketProcessor.getInstance();
        
        ModeChangePacket  modePacket = new ModeChangePacket();
//        modePacket.setMode(GameModeEnum.STANDALONE);
        
        packetProcessor.processModeChangePacket(modePacket);    // Do it

        System.out.println(
		Main.getMessage("msg_closing_connect")
		+ " " + m_hostname + ":" + m_port);
        
        Main.setStatusLabel(null);      // Clear the status label
        
        if (m_objInput != null)
        {
            m_objInput.close();
            m_objInput = null;
        }

        if (m_socket != null)
        {
            m_socket.close();
            m_socket = null;
        }

        if (m_serverSocket != null)
        {
            m_serverSocket.close();
            m_serverSocket = null;
        }
    }



    private static ConnectionSlave  m_singletonInstance;
    
    private String m_hostname;
    
    private int m_port;
    
    private int m_myPort;

    private int  m_progress = 0;
    
    private String  m_note;
    
    private boolean m_done = false;
    
    private ServerSocket  m_serverSocket;
    
    private Socket  m_socket;
    
    private ObjectInputStream m_objInput;
    
    private ConnectionListener  m_listener;
}
