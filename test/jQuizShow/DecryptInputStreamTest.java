

package jQuizShow;
//package jQuizShow.DecryptInputStream;
import java.io.*;
import junit.framework.TestCase;

public class DecryptInputStreamTest extends TestCase {
    
    public DecryptInputStreamTest() {
        super(null);
    }
    
 
    public void  testSetInputStream() 
    
    {
        
        InputStream input = new InputStream() {

            @Override
            public int read() throws IOException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
        DecryptInputStream instance = new DecryptInputStream();
        instance.setInputStream(input);
        //assertEquals(input,);
    }
    
    
}
   