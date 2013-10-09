/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jQuizShow.net;

import junit.framework.TestCase;

/**
 *
 * @author lameesfakhurji
 */
public class ConnectionInterfaceTest extends TestCase {
    
    public ConnectionInterfaceTest(String text) {
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
     * Test of start method, of class ConnectionInterface.
     */
    public void testStart() {
        
        ConnectionInterface instance = new ConnectionInterfaceImpl();
        instance.start();
        
    }

    /**
     * Test of stop method, of class ConnectionInterface.
     */
    public void testStop() {
        
        ConnectionInterface instance = new ConnectionInterfaceImpl();
        instance.stop();
        
    }

    /**
     * Test of done method, of class ConnectionInterface.
     */
    public void testDone() {
        
        ConnectionInterface instance = new ConnectionInterfaceImpl();
        boolean expResult = false;
        boolean result = instance.done();
        assertEquals(expResult, result);
        
    }

    /**
     * Test of getNote method, of class ConnectionInterface.
     */
    public void testGetNote() {
        
        ConnectionInterface instance = new ConnectionInterfaceImpl();
        String expResult = "";
        String result = instance.getNote();
        assertEquals(expResult, result);
        
    }

    /**
     * Test of getProgress method, of class ConnectionInterface.
     */
    public void testGetProgress() {
        
        ConnectionInterface instance = new ConnectionInterfaceImpl();
        int expResult = 0;
        int result = instance.getProgress();
        assertEquals(expResult, result);
        
    }

    public class ConnectionInterfaceImpl implements ConnectionInterface {

        public void start() {
        }

        public void stop() {
        }

        public boolean done() {
            return false;
        }

        public String getNote() {
            return "";
        }

        public int getProgress() {
            return 0;
        }
    }
}
