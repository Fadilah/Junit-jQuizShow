/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jQuizShow.net;

import jQuizShow.event.GameModeEnum;
import junit.framework.TestCase;

/**
 *
 * @author lameesfakhurji
 */
public class ModeChangePacketTest extends TestCase {
    
    public ModeChangePacketTest(String text) {
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
     * Test of getMode method, of class ModeChangePacket.
     */
    public void testGetMode() {
        
        ModeChangePacket instance = new ModeChangePacket();
        GameModeEnum expResult = null;
        GameModeEnum result = instance.getMode();
        
    }

    /**
     * Test of setMode method, of class ModeChangePacket.
     */
    public void testSetMode() {
        
        GameModeEnum mode = null;
        ModeChangePacket instance = new ModeChangePacket();
        instance.setMode(mode);
        
    }
}
