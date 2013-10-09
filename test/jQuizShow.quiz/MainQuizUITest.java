
package jQuizShow.quiz;

import jQuizShow.event.GameModeChangeEvent;
import jQuizShow.event.GameStateChangeEvent;
import jQuizShow.event.GameUpdateEvent;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


 // @author balshafa
 
public class MainQuizUITest extends TestCase {
    
    public MainQuizUITest(String test) {
        super(test);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(MainQuizUITest.class);
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

    
      //Test of initializeClass method, of class MainQuizUI.
     
    public void testInitializeClass() {
        MainQuizUI.initializeClass();
      
    }

   
      //Test of initialize method, of class MainQuizUI.
    
    public void testInitialize() {
        MainQuizUI instance = new MainQuizUI();
        instance.initialize();
        
    }

   
      // Test of startNewGame method, of class MainQuizUI.
    
    public void testStartNewGame() {
        MainQuizUI instance = new MainQuizUI();
        instance.startNewGame();
        
    }

    
      // Test of uninstall method, of class MainQuizUI.
     
    public void testUninstall() {
        MainQuizUI instance = new MainQuizUI();
        instance.uninstall();       
    }

    
     //Test of loadQuestionDatabase method, of class MainQuizUI.
     
    public void testLoadQuestionDatabase() {
        MainQuizUI instance = new MainQuizUI();
        instance.loadQuestionDatabase();       
    }

   
      // Test of loadQuestions method, of class MainQuizUI.
   
    public void testLoadQuestions() {
        //System.out.println("loadQuestions");
        String filename = "";
        MainQuizUI instance = new MainQuizUI();
        int expResult = 0;
        int result = instance.loadQuestions(filename);
        assertEquals(expResult, result);        
    }

    
     // Test of gameModeChanged method, of class MainQuizUI.
    
    public void testGameModeChanged() {
        GameModeChangeEvent evt = new GameModeChangeEvent();
        MainQuizUI instance = new MainQuizUI();
        instance.gameModeChanged(evt);       
    }

   
     //Test of gameStateChanged method, of class MainQuizUI.
    
    public void testGameStateChanged() {
        GameStateChangeEvent evt = new GameStateChangeEvent();
        MainQuizUI instance = new MainQuizUI();
        instance.gameStateChanged(evt);
        
    }

  
      //Test of gameUpdated method, of class MainQuizUI.
    
    public void testGameUpdated() {
        GameUpdateEvent evt = new GameUpdateEvent();
        MainQuizUI instance = new MainQuizUI();
        instance.gameUpdated(evt);
        
    }
}
