
package jQuizShow.event;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

 // @author balshafa

public class GameStateChangeEnumTest extends TestCase {
    
    public GameStateChangeEnumTest(String test) {
        super(test);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(GameStateChangeEnumTest.class);
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

   
     // Test of toString method, of class GameStateChangeEnum.
    
    public void testToString() {
        GameStateChangeEnum instance = new GameStateChangeEnumImpl();
        String expResult = "";
        String result = instance.toString();
        assertEquals(expResult, result);      
    }

   
      //Test of equals method, of class GameStateChangeEnum.
    
    public void testEquals() {
        Object that = new Object();
        GameStateChangeEnum instance = new GameStateChangeEnumImpl();
        boolean expResult = false;
        boolean result = instance.equals(that);
        assertEquals(expResult, result);   
    }

   
     // Test of hashCode method, of class GameStateChangeEnum.
    
    public void testHashCode() {
        GameStateChangeEnum instance = new GameStateChangeEnumImpl();
        int expResult = 0;
        int result = instance.hashCode();
        assertEquals(expResult, result);   
    }

    public class GameStateChangeEnumImpl implements GameStateChangeEnum {

        public String toString() {
            return "";
        }

        public boolean equals(Object that) {
            return false;
        }

        public int hashCode() {
            return 0;
        }
    }
}
