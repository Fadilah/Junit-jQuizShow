/*
 * ConnectionDialog.java
 *
 * Created on August 11, 2001, 4:00 PM
 *
 * $Id: ConnectionDialog.java,v 1.1 2004/04/02 06:01:59 sdchen Exp $
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
 *    $Log: ConnectionDialog.java,v $
 *    Revision 1.1  2004/04/02 06:01:59  sdchen
 *    Snapshot of jQuizShow 1.1 alpha06 development
 *
 *    Revision 1.2  2002/08/15 04:43:26  sdchen
 *    Internationalization of source code.  Main.getMessage() is the primary
 *    routine to get the localized message strings.
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
 */

import java.io.*;
import java.net.*;
import java.awt.event.*;
import javax.swing.*;

import jQuizShow.*;
import jQuizShow.event.*;

public class ConnectionDialog extends javax.swing.JInternalFrame
        implements ActionListener
{
    
    /** Creates new form ConnectionDialog */
    public ConnectionDialog() {
        super(Main.getMessage("title_connection_dialog"), true, false,
		false, false);

        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);

        initComponents();

        isMasterCheckBox.setSelected(false);
        hostnameLabel.setEnabled(false);
        hostnameText.setEnabled(false);
        
        portNumText.setText(String.valueOf(m_portNum));
    }

    public String getHostname() {
        return m_hostname;
    }    

    public int getPortNum() {
        return m_portNum;
    }
    

    private void initComponents() {
        textEntryPanel = new javax.swing.JPanel();
        hostnameLabel = new javax.swing.JLabel();
        hostnameText = new javax.swing.JTextField();
        portNumLabel = new javax.swing.JLabel();
        portNumText = new javax.swing.JTextField();
        controlSelectionPanel = new javax.swing.JPanel();
        isMasterCheckBox = new javax.swing.JCheckBox();
        okayCancelPanel = new javax.swing.JPanel();
        connectButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        
        getContentPane().setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gridBagConstraints1;
        
      
        textEntryPanel.setLayout(new java.awt.GridLayout(2, 2));
        
        hostnameLabel.setText(Main.getMessage("connect_hostname"));
        textEntryPanel.add(hostnameLabel);
        
        textEntryPanel.add(hostnameText);
        
        portNumLabel.setText(Main.getMessage("connect_port"));
        textEntryPanel.add(portNumLabel);
        
        textEntryPanel.add(portNumText);
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridy = 0;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.weightx = 100.0;
        gridBagConstraints1.weighty = 100.0;
        getContentPane().add(textEntryPanel, gridBagConstraints1);
        
        isMasterCheckBox.setText(Main.getMessage("run_as_master"));
        isMasterCheckBox.addActionListener(this);
        controlSelectionPanel.add(isMasterCheckBox);
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridy = 1;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.weightx = 100.0;
        gridBagConstraints1.weighty = 100.0;
        getContentPane().add(controlSelectionPanel, gridBagConstraints1);
        
        connectButton.setText(Main.getMessage("label_connect"));
	connectButton.setActionCommand("Connect");
        connectButton.addActionListener(this);
        okayCancelPanel.add(connectButton);
        
        cancelButton.setText(Main.getMessage("label_cancel"));
	cancelButton.setActionCommand("Cancel");
        cancelButton.addActionListener(this);
        okayCancelPanel.add(cancelButton);
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridy = 2;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.weightx = 100.0;
        gridBagConstraints1.weighty = 100.0;
        getContentPane().add(okayCancelPanel, gridBagConstraints1);
        
        pack();
    }

    /** Closes the dialog */
    public void actionPerformed(ActionEvent  e) {
        Object  source = e.getSource();


        if (source == cancelButton)
        {
            /* Just close window and return */
            doDefaultCloseAction();
            return;
        }
        else if (source == isMasterCheckBox)
        {
            if (isMasterCheckBox.isSelected())
            {
                hostnameLabel.setEnabled(true);
                hostnameText.setEnabled(true);
            }
            else
            {
                hostnameLabel.setEnabled(false);
                hostnameText.setEnabled(false);
            }
        }
        else
        {
            m_hostname = hostnameText.getText();

            String portNum = portNumText.getText();
            
            try
            {
                m_portNum = (int) Integer.parseInt(portNum);
            }
            catch (NumberFormatException  exc)
            {
                m_portNum = 0;
            }
            
            // Ensure input parameters are valid
            if (isMasterCheckBox.isSelected() && m_hostname.length() == 0)
            {
                JOptionPane.showInternalMessageDialog(this,
			Main.getMessage("err_host_not_specified"),
                        Main.getMessage("title_error"),
			JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (m_portNum < 512 || m_portNum > 32767)
            {
                JOptionPane.showInternalMessageDialog(this,
			Main.getMessage("err_invalid_port_number"),
                        Main.getMessage("title_error"),
			JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Close the dialog
            doDefaultCloseAction();

            if (m_statusPane == null)
                m_statusPane = new JOptionPane();
            
            if (e.getActionCommand().equals("Connect"))
            {
                String  taskTitle;
                
                if (isMasterCheckBox.isSelected())
                {
                    taskTitle = Main.getMessage("msg_connect_as_master") +
		            " (host="
                            + m_hostname + ")";
                }
                else
                {
                    try
                    {
                        InetAddress  inetAddr = InetAddress.getLocalHost();

			Object  args[] =
				{
				    inetAddr.getHostName(),
				    inetAddr.getHostAddress()
				};
                        
                        taskTitle = Main.getMessage("msg_connect_as_display",
				args);
                    }
                    catch (UnknownHostException  evt)
                    {
			Object  args[] =
				{
				    new Integer(m_portNum)
				};

		        // Failed getting host info.  Display port number.
                        taskTitle = Main.getMessage("msg_connect_as_display_port",
				args);
                    }
                }

                System.out.println(taskTitle);
                
                m_progressMonitor = new ProgressMonitor(Main.getMain(),
			taskTitle,
			Main.getMessage("title_status"),
			0, 100);
                
                m_progressMonitor.setProgress(0);
                m_progressMonitor.setMillisToDecideToPopup(10);
                m_progressMonitor.setMillisToPopup(0);
                
                m_timer = new Timer(500, new TimerListener());
                m_timer.start();
                
                if (isMasterCheckBox.isSelected())
                    runAsMaster();
                else
                    runAsSlave();
            }
        }
    }

    /** Setup master connection
     */
    private void  runAsMaster()
    {
        m_connection = new ConnectionMaster(m_hostname, m_portNum);

        m_connection.start();
        
        MainSubset.getInstance().setGameMode(GameModeEnum.MASTER);
    }
    
    /** Set-up slave connection
     */
    private void  runAsSlave()
    {
        m_connection = ConnectionSlave.getInstance(m_portNum);
        
        m_connection.start();
        
        MainSubset.getInstance().setGameMode(GameModeEnum.SLAVE);
    }

    /**
     * The actionPerformed method in this class
     * is called each time the Timer "goes off".
     */
    class TimerListener implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            if (m_progressMonitor.isCanceled() || m_connection.done())
            {
                m_progressMonitor.close();
                
                if (m_progressMonitor.isCanceled())
                    m_connection.stop();
                
                m_timer.stop();
                
                if (m_connection.done())
                {
                    String  message = Main.getMessage("msg_connect_established");

                    JOptionPane.showMessageDialog(Main.getMain(), message,
                            Main.getMessage("title_status"),
			    JOptionPane.INFORMATION_MESSAGE);

                    System.out.println(Main.getMessage(message));
                }
                else
                {
                    String  message = Main.getMessage("msg_connect_cancelled");

                    JOptionPane.showMessageDialog(Main.getMain(), message,
                            Main.getMessage("title_status"),
			    JOptionPane.INFORMATION_MESSAGE);

                    System.out.println(Main.getMessage(message));
                }
            }
            else
            {
                m_progressMonitor.setNote(m_connection.getNote());
                m_progressMonitor.setProgress(m_connection.getProgress());
            }
        }
    }
    
    // Variables declaration
    private javax.swing.JPanel textEntryPanel;
    private javax.swing.JLabel hostnameLabel;
    private javax.swing.JTextField hostnameText;
    private javax.swing.JLabel portNumLabel;
    private javax.swing.JTextField portNumText;
    private javax.swing.JPanel controlSelectionPanel;
    private javax.swing.JCheckBox isMasterCheckBox;
    private javax.swing.JPanel okayCancelPanel;
    private javax.swing.JButton connectButton;
    private javax.swing.JButton cancelButton;

    private ProgressMonitor  m_progressMonitor;
    private Timer  m_timer;
    
    private String m_hostname;
    
    private int m_portNum = 13536;
    
    private ConnectionInterface  m_connection;
        
    private JOptionPane     m_statusPane;
}
