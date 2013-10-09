/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jQuizShow.net;

import static jQuizShow.net.ConnectionSlave.getInstance;
import junit.framework.TestCase;

/**
 *
 * @author lameesfakhurji
 */
public class ConnectionSlaveTest extends TestCase {
    
    public ConnectionSlaveTest(String text) {
        super(text);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of getInstance method, of class ConnectionSlave.
     */
    public void testGetInstance_int() {
        
        int myPort = 0;
        ConnectionSlave.getInstance(myPort);
        
        
    }

    /**
     * Test of getInstance method, of class ConnectionSlave.
     */
    public void testGetInstance_0args() {
        
        ConnectionSlave.getInstance();
        
       
    }

    /**
     * Test of getPort method, of class ConnectionSlave.
     */
    public void testGetPort() {
        
        ConnectionSlave instance = getInstance();
        instance.getPort();
        
        
    }

    /**
     * Test of start method, of class ConnectionSlave.
     */
    public void testStart() {
        
        ConnectionSlave instance = getInstance();
        instance.start();
        
    }

    /**
     * Test of stop method, of class ConnectionSlave.
     */
    public void testStop() {
        
        ConnectionSlave instance = getInstance();
        instance.stop();
        
    }

    /**
     * Test of getNote method, of class ConnectionSlave.
     */
    public void testGetNote() {
        
        ConnectionSlave instance = getInstance();
        instance.getNote();
        
    }

    /**
     * Test of getProgress method, of class ConnectionSlave.
     */
    public void testGetProgress() {
        
        ConnectionSlave instance = getInstance();
        instance.getProgress();
        
        
    }

    /**
     * Test of done method, of class ConnectionSlave.
     */
    public void testDone() {
        
        ConnectionSlave instance = getInstance();
        instance.done();
        
        
    }

    /**
     * Test of open method, of class ConnectionSlave.
     */
    public void testOpen() throws Exception {
        
        ConnectionSlave instance = getInstance();
        instance.open();
       
    }

    /**
     * Test of isConnected method, of class ConnectionSlave.
     */
    public void testIsConnected() {
       
        ConnectionSlave instance = getInstance();
        instance.isConnected();
        
        
    }

    /**
     * Test of close method, of class ConnectionSlave.
     */
    public void testClose() throws Exception {
        
        ConnectionSlave instance = getInstance();
        instance.close();
        
    }
}
