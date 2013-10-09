
package jQuizShow.data;

import junit.framework.TestCase;

/**
 *
 * @author lameesfakhurji
 */
public class AnswerTest extends TestCase {
    
    public AnswerTest(String text) {
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
     * Test of setAnswer method, of class Answer.
     */
    public void testSetAnswer() {
        
        String text = "text";
        Answer instance = new Answer(text);
        instance.setAnswer(text);
        
    }

    /**
     * Test of setCorrect method, of class Answer.
     */
    public void testSetCorrect() {
        
        boolean isCorrect = false;
        Answer instance = new Answer();
        instance.setCorrect(isCorrect);
        
    }

    /**
     * Test of getAnswer method, of class Answer.
     */
    public void testGetAnswer() {
        
        Answer instance = new Answer();
        instance.getAnswer();
        
        
    }

    /**
     * Test of toString method, of class Answer.
     */
    public void testToString() {
        
        Answer instance = new Answer();
        instance.toString();
        
    }

    /**
     * Test of getCorrect method, of class Answer.
     */
    public void testGetCorrect() {
        
        Answer instance = new Answer();
        instance.getCorrect();
        
        
    }
}
