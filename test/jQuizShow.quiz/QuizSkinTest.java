
package jQuizShow.quiz;

import java.awt.Color;
import java.awt.image.BufferedImage;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


 // @author balshafa


public class QuizSkinTest extends TestCase {
    
    public QuizSkinTest(String test) {
        super(test);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(QuizSkinTest.class);
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

    
    

    
     // Test of setBkgColor method, of class QuizSkin.
     
    public void testSetBkgColor() {
        //System.out.println("setBkgColor");
        Color color = null;
        QuizSkin instance = new QuizSkin();
        instance.setBkgColor(color);
        
    }

   
     // Test of setBkgImage method, of class QuizSkin.
   
    public void testSetBkgImage() {
        BufferedImage image = null;
        QuizSkin instance = new QuizSkin();
        instance.setBkgImage(image);
       
    }
}
