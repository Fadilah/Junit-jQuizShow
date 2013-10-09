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
public class PacketProcessorTest extends TestCase {
    
    public PacketProcessorTest(String text) {
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
     * Test of getInstance method, of class PacketProcessor.
     */
    public void testGetInstance() {
        
        PacketProcessor expResult = null;
        PacketProcessor result = PacketProcessor.getInstance();
        
    }

    /**
     * Test of setInstance method, of class PacketProcessor.
     */
    public void testSetInstance() {
        
        PacketProcessor singleton = null;
        PacketProcessor.setInstance(singleton);
        
    }

    /**
     * Test of processStatePacket method, of class PacketProcessor.
     */
    public void testProcessStatePacket() {
        
        StatePacket info = null;
        PacketProcessor instance = new PacketProcessorImpl();
        instance.processStatePacket(info);
        
    }

    /**
     * Test of processEventPacket method, of class PacketProcessor.
     */
    public void testProcessEventPacket() {
        
        EventPacket info = null;
        PacketProcessor instance = new PacketProcessorImpl();
        instance.processEventPacket(info);
        
    }

    /**
     * Test of processModeChangePacket method, of class PacketProcessor.
     */
    public void testProcessModeChangePacket() {
        
        ModeChangePacket info = null;
        PacketProcessor instance = new PacketProcessorImpl();
        instance.processModeChangePacket(info);
        
    }

    public class PacketProcessorImpl extends PacketProcessor {

        public void processStatePacket(StatePacket info) {
        }

        public void processEventPacket(EventPacket info) {
        }

        public void processModeChangePacket(ModeChangePacket info) {
        }
    }
}
