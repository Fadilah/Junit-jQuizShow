/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jQuizShow.net;

import jQuizShow.event.GameUpdateEnum;
import java.util.BitSet;
import junit.framework.TestCase;

/**
 *
 * @author lameesfakhurji
 */
public class EventPacketTest extends TestCase {
    
    public EventPacketTest(String text) {
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
     * Test of getType method, of class EventPacket.
     */
    public void testGetType() {
        
        EventPacket instance = new EventPacket();
        instance.getType();
        
        
    }

    /**
     * Test of getSelectedAnswer method, of class EventPacket.
     */
    public void testGetSelectedAnswer() {
        
        EventPacket instance = new EventPacket();
        instance.getSelectedAnswer();
        
        
    }

    /**
     * Test of getSelectedLifeline method, of class EventPacket.
     */
    public void testGetSelectedLifeline() {
        
        EventPacket instance = new EventPacket();
        instance.getSelectedLifeline();
        
        
    }

    /**
     * Test of getHighlightLevel method, of class EventPacket.
     */
    public void testGetHighlightLevel() {
        
        EventPacket instance = new EventPacket();
        instance.getHighlightLevel();
        
 
        
    }

    /**
     * Test of getTransitionMessage method, of class EventPacket.
     */
    public void testGetTransitionMessage() {
        
        EventPacket instance = new EventPacket();
        instance.getTransitionMessage();
        
        
    }

    /**
     * Test of getStatusMessage method, of class EventPacket.
     */
    public void testGetStatusMessage() {
        
        EventPacket instance = new EventPacket();
        instance.getStatusMessage();
        
        
    }

    /**
     * Test of getQuestionTimerTime method, of class EventPacket.
     */
    public void testGetQuestionTimerTime() {
        
        EventPacket instance = new EventPacket();
        instance.getQuestionTimerTime();
        
        
    }

    /**
     * Test of getLifelineTimerTime method, of class EventPacket.
     */
    public void testGetLifelineTimerTime() {
        
        EventPacket instance = new EventPacket();
        instance.getLifelineTimerTime();
       
        
    }

    /**
     * Test of getToggleStates method, of class EventPacket.
     */
    public void testGetToggleStates() {
        
        EventPacket instance = new EventPacket();
        instance.getToggleStates();
        
        
    }

    /**
     * Test of setType method, of class EventPacket.
     */
    public void testSetType() {
        
        GameUpdateEnum type = null;
        EventPacket instance = new EventPacket();
        instance.setType(type);
        
    }

    /**
     * Test of setSelectedAnswer method, of class EventPacket.
     */
    public void testSetSelectedAnswer() {
        
        int index = 0;
        EventPacket instance = new EventPacket();
        instance.setSelectedAnswer(index);
       
    }

    /**
     * Test of setSelectedLifeline method, of class EventPacket.
     */
    public void testSetSelectedLifeline() {
      
        int index = 0;
        EventPacket instance = new EventPacket();
        instance.setSelectedLifeline(index);
        
    }

    /**
     * Test of setHighlightLevel method, of class EventPacket.
     */
    public void testSetHighlightLevel() {
        
        int index = 0;
        EventPacket instance = new EventPacket();
        instance.setHighlightLevel(index);
        
    }

    /**
     * Test of setTransitionMessage method, of class EventPacket.
     */
    public void testSetTransitionMessage() {
        
        String message = "";
        EventPacket instance = new EventPacket();
        instance.setTransitionMessage(message);
        
    }

    /**
     * Test of setStatusMessage method, of class EventPacket.
     */
    public void testSetStatusMessage() {
        
        String message = "";
        EventPacket instance = new EventPacket();
        instance.setStatusMessage(message);
        
    }

    /**
     * Test of setQuestionTimerTime method, of class EventPacket.
     */
    public void testSetQuestionTimerTime() {
        
        int seconds = 0;
        EventPacket instance = new EventPacket();
        instance.setQuestionTimerTime(seconds);
        
    }

    /**
     * Test of setLifelineTimerTime method, of class EventPacket.
     */
    public void testSetLifelineTimerTime() {
        
        int seconds = 0;
        EventPacket instance = new EventPacket();
        instance.setLifelineTimerTime(seconds);
        
    }

    /**
     * Test of setToggleStates method, of class EventPacket.
     */
    public void testSetToggleStates() {
        
        BitSet toggleStates = null;
        EventPacket instance = new EventPacket();
        instance.setToggleStates(toggleStates);
       
    }
}
