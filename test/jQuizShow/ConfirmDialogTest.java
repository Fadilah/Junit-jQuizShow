
/*there are some private methods but when we test 
confirmDialog method, they will be tested because confirm dialog calls 
the private method [initComponents()], and this method calls the other method
which is [CloseDialog]. theses private method don't have a specific input to 
test it inside them */

package jQuizShow;

import junit.framework.TestCase;

public class ConfirmDialogTest extends TestCase {
  
    // Creating new form ConfirmDialog testing

    public ConfirmDialogTest(String test) {
        super(test);
    }
    
  
    // args is command line arguments
    // main testing 
    
    public void testMain() {
        String[] args = new String[1];
        ConfirmDialog.main(args);
    }
}
