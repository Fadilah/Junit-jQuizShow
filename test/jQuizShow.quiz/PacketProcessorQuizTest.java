
package jQuizShow.quiz;

import jQuizShow.net.EventPacket;
import jQuizShow.net.ModeChangePacket;
import jQuizShow.net.StatePacket;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

 // @author balshafa

public class PacketProcessorQuizTest extends TestCase {
    
    public PacketProcessorQuizTest(String test) {
        super(test);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(PacketProcessorQuizTest.class);
        return suite;
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    
     // Test of initialize method, of class PacketProcessorQuiz.
    
    public void testInitialize() {
        PacketProcessorQuiz.initialize();
        
    }

    
     // Test of processStatePacket method, of class PacketProcessorQuiz.

    public void testProcessStatePacket() {
       
        StatePacket info = new StatePacket();
        PacketProcessorQuiz instance = new PacketProcessorQuiz();
        instance.processStatePacket(info);       
    }

   
     // Test of processEventPacket method, of class PacketProcessorQuiz.
    
    public void testProcessEventPacket() {
        EventPacket info = new EventPacket();
        PacketProcessorQuiz instance = new  PacketProcessorQuiz();
        instance.processEventPacket(info);       
    }

    
     // Test of processModeChangePacket method, of class PacketProcessorQuiz.
    
    public void testProcessModeChangePacket() {
        ModeChangePacket info = new ModeChangePacket();
        PacketProcessorQuiz instance = new  PacketProcessorQuiz();
        instance.processModeChangePacket(info);
    }
}
