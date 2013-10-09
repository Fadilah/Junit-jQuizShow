/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jQuizShow.net;

import jQuizShow.event.GameStateChangeEnum;
import java.util.BitSet;
import junit.framework.TestCase;

/**
 *
 * @author lameesfakhurji
 */
public class StatePacketTest extends TestCase {
    
    public StatePacketTest(String text) {
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
     * Test of getType method, of class StatePacket.
     */
    public void testGetType() {
        
        StatePacket instance = new StatePacket();
        instance.getType();
        
    }

    /**
     * Test of getMaxNumberOfLevels method, of class StatePacket.
     */
    public void testGetMaxNumberOfLevels() {
        
        StatePacket instance = new StatePacket();
        instance.getMaxNumberOfLevels();
        
    }

    /**
     * Test of getCurrentLevel method, of class StatePacket.
     */
    public void testGetCurrentLevel() {
        
        StatePacket instance = new StatePacket();
        instance.getCurrentLevel();
        
    }

    /**
     * Test of getLevelTitle method, of class StatePacket.
     */
    public void testGetLevelTitle() {
        
        StatePacket instance = new StatePacket();
        instance.getLevelTitle();
        
    }

    /**
     * Test of getQuestionID method, of class StatePacket.
     */
    public void testGetQuestionID() {System.out.println("getQuestionID");
        StatePacket instance = new StatePacket();
        instance.getQuestionID();
        
    }

    /**
     * Test of getQuestion method, of class StatePacket.
     */
    public void testGetQuestion() {
        
        StatePacket instance = new StatePacket();
        instance.getQuestion();
        
    }

    /**
     * Test of getAnswers method, of class StatePacket.
     */
    public void testGetAnswers() {
        
        StatePacket instance = new StatePacket();
        instance.getAnswers();
        
    }

    /**
     * Test of getQuestionTimerLimit method, of class StatePacket.
     */
    public void testGetQuestionTimerLimit() {
        
        StatePacket instance = new StatePacket();
        instance.getQuestionTimerLimit();
       
    }

    /**
     * Test of getLifelineTimerLimit method, of class StatePacket.
     */
    public void testGetLifelineTimerLimit() {
        
        StatePacket instance = new StatePacket();
        instance.getLifelineTimerLimit();
        
    }

    /**
     * Test of getAnswersVisible method, of class StatePacket.
     */
    public void testGetAnswersVisible() {
        
        StatePacket instance = new StatePacket();
        instance.getAnswersVisible();
        
    }

    /**
     * Test of getCorrectAnswer method, of class StatePacket.
     */
    public void testGetCorrectAnswer() {
       
        StatePacket instance = new StatePacket();
        instance.getCorrectAnswer();
        
    }

    /**
     * Test of setType method, of class StatePacket.
     */
    public void testSetType() {
        
        GameStateChangeEnum type = null;
        StatePacket instance = new StatePacket();
        instance.setType(type);
        
    }

    /**
     * Test of setMaxNumberOfLevels method, of class StatePacket.
     */
    public void testSetMaxNumberOfLevels() {
       
        int max = 0;
        StatePacket instance = new StatePacket();
        instance.setMaxNumberOfLevels(max);
        
    }

    /**
     * Test of setCurrentLevel method, of class StatePacket.
     */
    public void testSetCurrentLevel() {
        
        int level = 0;
        StatePacket instance = new StatePacket();
        instance.setCurrentLevel(level);
        
    }

    /**
     * Test of setLevelTitle method, of class StatePacket.
     */
    public void testSetLevelTitle() {
        
        String title = "";
        StatePacket instance = new StatePacket();
        instance.setLevelTitle(title);
        
    }

    /**
     * Test of setQuestionID method, of class StatePacket.
     */
    public void testSetQuestionID() {
        
        String id = "";
        StatePacket instance = new StatePacket();
        instance.setQuestionID(id);
        
    }

    /**
     * Test of setQuestion method, of class StatePacket.
     */
    public void testSetQuestion() {
        
        String question = "";
        StatePacket instance = new StatePacket();
        instance.setQuestion(question);
        
    }

    /**
     * Test of setAnswers method, of class StatePacket.
     */
    public void testSetAnswers() {
       
        String[] answers = null;
        StatePacket instance = new StatePacket();
        instance.setAnswers(answers);
        
    }

    /**
     * Test of setQuestionTimerLimit method, of class StatePacket.
     */
    public void testSetQuestionTimerLimit() {
        
        int seconds = 0;
        StatePacket instance = new StatePacket();
        instance.setQuestionTimerLimit(seconds);
        
    }

    /**
     * Test of setLifelineTimerLimit method, of class StatePacket.
     */
    public void testSetLifelineTimerLimit() {
        
        int seconds = 0;
        StatePacket instance = new StatePacket();
        instance.setLifelineTimerLimit(seconds);
       
    }

    /**
     * Test of setAnswersVisible method, of class StatePacket.
     */
    public void testSetAnswersVisible() {
     
        BitSet answersVisible = null;
        StatePacket instance = new StatePacket();
        instance.setAnswersVisible(answersVisible);
        
    }

    /**
     * Test of setCorrectAnswer method, of class StatePacket.
     */
    public void testSetCorrectAnswer() {
       
        int index = 0;
        StatePacket instance = new StatePacket();
        instance.setCorrectAnswer(index);
        
    }
}
