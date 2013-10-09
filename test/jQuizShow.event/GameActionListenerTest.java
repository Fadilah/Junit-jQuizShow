
package jQuizShow.event;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

//@author balshafa

public class GameActionListenerTest extends TestCase {
    
    public GameActionListenerTest(String test) {
        super(test);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(GameActionListenerTest.class);
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

    // Test of gameActionPerformed method, of class GameActionListener.
     
    public void testGameActionPerformed() {
        GameActionEvent evt = null;
        GameActionListener instance = new GameActionListenerImpl();
        instance.gameActionPerformed(evt);
    }

    public class GameActionListenerImpl implements GameActionListener {

        public void gameActionPerformed(GameActionEvent evt) {
        }
    }
}
