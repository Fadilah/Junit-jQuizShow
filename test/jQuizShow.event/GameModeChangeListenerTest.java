
package jQuizShow.event;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


 
 // @author balshafa
 
public class GameModeChangeListenerTest extends TestCase {
    
    public GameModeChangeListenerTest(String test) {
        super(test);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(GameModeChangeListenerTest.class);
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

   
    // Test of gameModeChanged method, of class GameModeChangeListener.
  
    public void testGameModeChanged() {
        GameModeChangeEvent evt = new GameModeChangeEvent();
        GameModeChangeListener instance = new GameModeChangeListenerImpl();
        instance.gameModeChanged(evt);
    }

    public class GameModeChangeListenerImpl implements GameModeChangeListener {

        public void gameModeChanged(GameModeChangeEvent evt) {
        }
    }
}
