/*
 (initialize) is private method and called by GameTest Method, so it is examined here
 * because we already tested GameText
 */
package jQuizShow;

import java.lang.reflect.Field;
import jQuizShow.event.GameInputEvent;
import jQuizShow.event.GameInputListener;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
//import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.image.ImageObserver;
import java.text.AttributedCharacterIterator;
import static junit.framework.Assert.assertEquals;
import junit.framework.TestCase;


public class GameTextTest extends TestCase {
    
    public GameTextTest(String test) {
        super(test);
    }
    
  
    public void testSetFontBasename() throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
     
      
      GameText instance = new GameText(1);
      Field val = GameText.class.getDeclaredField("m_fontBasename");
      val.setAccessible(true);
      instance.setFontBasename("Arial");
      String fieldValue = (String) val.get(instance);
     
      assertEquals("Arial", fieldValue);
     }

    
    public void testGetId() {
      int id = 234; 
      GameText instance = new GameText(id);
      assertEquals(id,instance.getId());
    }

    
    public void testAddGameInputListener() {
        
        GameInputListener l = new GameInputListener() {

            @Override
            public void gameInputReceived(GameInputEvent evt) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
        GameText instance = new GameText(1);
        instance.addGameInputListener(l);
    }

    public void testRemoveGameInputListener() {
       GameInputListener l = new GameInputListener() {
//GameInputListener is abstract
            @Override
            public void gameInputReceived(GameInputEvent evt) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
        GameText instance = new GameText(1);
        instance.removeGameInputListener(l);
        
    }

   
    public void testFireGameInputEvent() {
        Object obj= null;
        GameInputEvent evt = new GameInputEvent(obj);
        GameText instance = new GameText(1);
        instance.fireGameInputEvent(evt);
       
    }

    
    public void testSetText() throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
       
      GameText instance = new GameText(1);
      Field val1 = GameText.class.getDeclaredField("m_label");
      val1.setAccessible(true);
      Field val2 = GameText.class.getDeclaredField("m_text");
      val2.setAccessible(true);
      instance.setText("Label", "text");
      String fieldValue1 = (String) val1.get(instance);
      String fieldValue2 = (String) val2.get(instance);

      assertEquals("Label, text", fieldValue1 +", " + fieldValue2);
     
       
  
    }

    public void testSetTextColor_0args() {
       
        GameText instance = new GameText(1);
        instance.setTextColor();
        
    }

    
    public void testSetTextColor_3args() {
        
       GameText instance = new GameText(1);
        instance.setTextColor(Color.blue, Color.green, Color.black);
       
    }

   
    public void testSetBorderColor() {
      
        GameText instance = new GameText(1);
        instance.setBorderColor(Color.black);
    
    }

    
    public void testCenterText() throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
       
        GameText instance = new GameText(1);
      Field val3 = GameText.class.getDeclaredField("m_center");
      val3.setAccessible(true);
      instance.centerText(true);
      boolean fieldValue =  Boolean.valueOf(val3.get(instance).toString()) ;
     
      assertEquals(true, fieldValue);
       
        
    }

   
    public void testPaintComponent() {
       
        Graphics g = new Graphics() {

            @Override
            public Graphics create() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void translate(int i, int i1) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public Color getColor() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void setColor(Color color) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void setPaintMode() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void setXORMode(Color color) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public Font getFont() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void setFont(Font font) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public FontMetrics getFontMetrics(Font font) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public Rectangle getClipBounds() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void clipRect(int i, int i1, int i2, int i3) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void setClip(int i, int i1, int i2, int i3) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public Shape getClip() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void setClip(Shape shape) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void copyArea(int i, int i1, int i2, int i3, int i4, int i5) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void drawLine(int i, int i1, int i2, int i3) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void fillRect(int i, int i1, int i2, int i3) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void clearRect(int i, int i1, int i2, int i3) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void drawRoundRect(int i, int i1, int i2, int i3, int i4, int i5) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void fillRoundRect(int i, int i1, int i2, int i3, int i4, int i5) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void drawOval(int i, int i1, int i2, int i3) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void fillOval(int i, int i1, int i2, int i3) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void drawArc(int i, int i1, int i2, int i3, int i4, int i5) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void fillArc(int i, int i1, int i2, int i3, int i4, int i5) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void drawPolyline(int[] ints, int[] ints1, int i) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void drawPolygon(int[] ints, int[] ints1, int i) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void fillPolygon(int[] ints, int[] ints1, int i) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void drawString(String string, int i, int i1) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void drawString(AttributedCharacterIterator aci, int i, int i1) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public boolean drawImage(Image image, int i, int i1, ImageObserver io) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public boolean drawImage(Image image, int i, int i1, int i2, int i3, ImageObserver io) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public boolean drawImage(Image image, int i, int i1, Color color, ImageObserver io) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public boolean drawImage(Image image, int i, int i1, int i2, int i3, Color color, ImageObserver io) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public boolean drawImage(Image image, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7, ImageObserver io) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public boolean drawImage(Image image, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7, Color color, ImageObserver io) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void dispose() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        } ;
        
        //m_label.length()
        GameText instance = new GameText(1);
        instance.paintComponent(g);
        
    }

    
    public void testMain() {
       //random value
        String[] args = null;
        GameText.main(args);
      
    }
}
