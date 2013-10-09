
package jQuizShow.event;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


//@author balshafa

public class GameStateChangeListenerTest extends TestCase {
    
    public GameStateChangeListenerTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(GameStateChangeListenerTest.class);
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

    
    // Test of gameStateChanged method, of class GameStateChangeListener.
    
    public void testGameStateChanged() {
        GameStateChangeEvent evt = null;
        GameStateChangeListener instance = new GameStateChangeListenerImpl();
        instance.gameStateChanged(evt);
       
    }

    public class GameStateChangeListenerImpl implements GameStateChangeListener {

        public void gameStateChanged(GameStateChangeEvent evt) {
        }
    }
}
