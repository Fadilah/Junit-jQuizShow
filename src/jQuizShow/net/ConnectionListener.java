/*
 * ConnectionListener.java
 *
 * Created on June 9, 2001, 9:32 PM
 *
 * $Id: ConnectionListener.java,v 1.1 2004/04/02 06:01:59 sdchen Exp $
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
 *    $Log: ConnectionListener.java,v $
 *    Revision 1.1  2004/04/02 06:01:59  sdchen
 *    Snapshot of jQuizShow 1.1 alpha06 development
 *
 *    Revision 1.2  2002/08/15 04:43:26  sdchen
 *    Internationalization of source code.  Main.getMessage() is the primary
 *    routine to get the localized message strings.
 *
 *    Revision 1.1.1.1  2002/04/13 17:01:40  sdchen
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

public class ConnectionListener extends Thread {

    /** Creates new ConnectionListener */
    public ConnectionListener(ObjectInputStream  objInput) {
        m_objInput = objInput;
        
        m_packetProcessor = PacketProcessor.getInstance();
    }

    public void run() {
        while (true) {
            try
            {
                Object          object;
                
                object = m_objInput.readObject();
                
                if (object.getClass() == StatePacket.class)
                {
                    StatePacket     statePacket = (StatePacket) object;

                    // Update the local GameState singleton using the received info
                    m_packetProcessor.processStatePacket(statePacket);
                }
                else if (object.getClass() == EventPacket.class)
                {
                    EventPacket     eventPacket = (EventPacket) object;
                    
                    // Update the local GameState singleton using the received info
                    m_packetProcessor.processEventPacket(eventPacket);
                }
                else if (object.getClass() == ModeChangePacket.class)
                {
                    ModeChangePacket     modeChangePacket = (ModeChangePacket) object;
                    
                    // Update the local GameState singleton using the received info
                    m_packetProcessor.processModeChangePacket(modeChangePacket);
                }

            }
            catch (OptionalDataException  e)
            {
                System.out.println("OptionalDataException:  " + e.getMessage());
            }
            catch (ClassNotFoundException  e)
            {
                System.out.println("ClassNotFoundException:  " + e.getMessage());
            }
            catch (IOException  e)
            {
                System.out.println("IOException:  " + e.getMessage());

                JOptionPane.showMessageDialog(Main.getMain(),
			Main.getMessage("msg_connect_lost"),
                        Main.getMessage("title_error"),
			JOptionPane.ERROR_MESSAGE);

                // Clean up
                try {
                    ConnectionSlave.getInstance().close();
                }
                catch (IOException  exc)
                {
                    System.out.println("IOException [close()]:  " + exc.getMessage());
                }
                
                // Exit loop
                return;
            }
            
            try
            {
                Thread.sleep(20);
            }
            catch (InterruptedException  e)
            {
                // Okay
            }
        }
    }

    private ObjectInputStream m_objInput;

    private PacketProcessor  m_packetProcessor;
}
