
// (updateSize) is private method and called by paintComponent, so it tested

package jQuizShow;
import java.lang.reflect.Field;

import jQuizShow.event.GameTimerEvent;
import jQuizShow.event.GameTimerListener;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.image.ImageObserver;
import java.lang.reflect.Field;
import java.text.AttributedCharacterIterator;
import static junit.framework.Assert.assertEquals;
import junit.framework.TestCase;


public class GameTimerTest extends TestCase {
    
    public GameTimerTest(String test) {
        super(test);
    }
    
   
    /**
     * Sets up the timer.
     *
     *@param timeLimit - End time in seconds for the timer
     *@param currentTime - Current number of seconds elapsed
     *@param countdown - "true" will show a count down timer vs. a count up timer
     * there are three cases
     *1- case if limit time less than 0 
     *2- case if current time > limit time 
     *3- it will not go inside if Statements
     *  setTimer(timeLimit, currentTime, countdown) */
    
   
    public void testSetTimer() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
         GameTimer instance = new GameTimer();
      Field val = GameTimer.class.getDeclaredField("m_timeLimit");
      val.setAccessible(true);
      Field val2 = GameTimer.class.getDeclaredField("m_elapsedTime");
      val2.setAccessible(true);
      Field val3 = GameTimer.class.getDeclaredField("m_countdown");
      val3.setAccessible(true);
      
     // if limit < 0 , and current = -1 = time limit
      
     instance.setTimer(-1, -1, true);
     String fieldValue = (String) val.get(instance).toString();
     String fieldValue2 = (String) val2.get(instance).toString();
     boolean fieldValue3 = Boolean.valueOf(val3.get(instance).toString()) ;
      assertEquals( "timeLimit= 0"   + ", elapsedTime= -1" +", countdown= true"  , 
            "timeLimit= " +  fieldValue + ", elapsedTime= " + fieldValue2 +", countdown= " 
              + fieldValue3 );
      
       
    }

    public void testSetTimer2() throws IllegalArgumentException, IllegalArgumentException, IllegalAccessException, NoSuchFieldException {
      GameTimer instance = new GameTimer();
      Field val = GameTimer.class.getDeclaredField("m_timeLimit");
      val.setAccessible(true);
      Field val2 = GameTimer.class.getDeclaredField("m_elapsedTime");
      val2.setAccessible(true);
      Field val3 = GameTimer.class.getDeclaredField("m_countdown");
      val3.setAccessible(true);
      
     // if limit < 0 , and current = 40 > time limit
      
     instance.setTimer(-1, 40, true);
     String fieldValue = (String) val.get(instance).toString();
     String fieldValue2 = (String) val2.get(instance).toString();
     boolean fieldValue3 = Boolean.valueOf(val3.get(instance).toString()) ;
      assertEquals( "timeLimit= 0"   + ", elapsedTime= -1" +", countdown= true"  , 
            "timeLimit= " +  fieldValue + ", elapsedTime= " + fieldValue2 +", countdown= " 
              + fieldValue3 );
      
     
       
    }
    
    public void testSetTimer3() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
         GameTimer instance = new GameTimer();
      Field val = GameTimer.class.getDeclaredField("m_timeLimit");
      val.setAccessible(true);
      Field val2 = GameTimer.class.getDeclaredField("m_elapsedTime");
      val2.setAccessible(true);
      Field val3 = GameTimer.class.getDeclaredField("m_countdown");
      val3.setAccessible(true);
      
     // if limit = 30 , and current = 40 > time limit
      
     instance.setTimer(30, 40, true);
     String fieldValue = (String) val.get(instance).toString();
     String fieldValue2 = (String) val2.get(instance).toString();
     boolean fieldValue3 = Boolean.valueOf(val3.get(instance).toString()) ;
      assertEquals( "timeLimit= 30"   + ", elapsedTime= 30" +", countdown= true"  , 
            "timeLimit= " +  fieldValue + ", elapsedTime= " + fieldValue2 +", countdown= " 
              + fieldValue3 );
      
       
    }
    
    public void testSetTimer4() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
         GameTimer instance = new GameTimer();
      Field val = GameTimer.class.getDeclaredField("m_timeLimit");
      val.setAccessible(true);
      Field val2 = GameTimer.class.getDeclaredField("m_elapsedTime");
      val2.setAccessible(true);
      Field val3 = GameTimer.class.getDeclaredField("m_countdown");
      val3.setAccessible(true);
      
     // if limit =30 , and current = 30 = time limit , count down= false
      
     instance.setTimer(30, 30, false);
     String fieldValue = (String) val.get(instance).toString();
     String fieldValue2 = (String) val2.get(instance).toString();
     boolean fieldValue3 = Boolean.valueOf(val3.get(instance).toString()) ;
      assertEquals( "timeLimit= 30"   + ", elapsedTime= 30" +", countdown= false"  , 
            "timeLimit= " +  fieldValue + ", elapsedTime= " + fieldValue2 +", countdown= " 
              + fieldValue3 );
      
       
    }
    
    public void testSetTimer5() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
         GameTimer instance = new GameTimer();
      Field val = GameTimer.class.getDeclaredField("m_timeLimit");
      val.setAccessible(true);
      Field val2 = GameTimer.class.getDeclaredField("m_elapsedTime");
      val2.setAccessible(true);
      Field val3 = GameTimer.class.getDeclaredField("m_countdown");
      val3.setAccessible(true);
      
     // if limit = 30 , and current = 20 < time limit
      
     instance.setTimer(30, 20, true);
     String fieldValue = (String) val.get(instance).toString();
     String fieldValue2 = (String) val2.get(instance).toString();
     boolean fieldValue3 = Boolean.valueOf(val3.get(instance).toString()) ;
      assertEquals( "timeLimit= 30"   + ", elapsedTime= 20" +", countdown= true"  , 
            "timeLimit= " +  fieldValue + ", elapsedTime= " + fieldValue2 +", countdown= " 
              + fieldValue3 );
      
       
    }
    
    
    public void testSetTime() throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException {
       
        GameTimer instance = new GameTimer();
        // if current time >= 0 and current time <= limit time
        instance.setTimer(30, 20, true);// to set limit time to 30
        Field val = GameTimer.class.getDeclaredField("m_timeLimit");
        val.setAccessible(true);
        Field val2 = GameTimer.class.getDeclaredField("m_elapsedTime");
        val2.setAccessible(true);
        instance.setTime(2);// 2 second >0 , too guarntee it goes inside if statement
        String fieldValue = (String) val2.get(instance).toString();
        
        assertEquals("elapsedTime= 2", "elapsedTime= " + fieldValue);
    }

    
    public void testStartTimer() {
        
        GameTimer instance = new GameTimer();
        instance.startTimer();
       
    }

    
    public void testStopTimer() {
        
        GameTimer instance = new GameTimer();
        instance.stopTimer();
       
    }

        /** Sets the main colors of the timer */

    public void testSetColor_3args() {
       
        GameTimer instance = new GameTimer();
        instance.setColor(Color.BLUE, Color.WHITE, Color.YELLOW);
      
    }

    
    public void testSetColor_5args() {
       //choosing random colors
        GameTimer instance = new GameTimer();
        instance.setColor(Color.BLUE, Color.WHITE, Color.YELLOW, Color.RED,Color.CYAN);
       
    }

   
    public void testIsPaused() {
       
        GameTimer instance = new GameTimer();
        instance.isPaused();
       
    }

    
    public void testGetTime() {
        
        GameTimer instance = new GameTimer();
        instance.getTime();
        
    }

    public void testAddGameTimerListener() {
        
        GameTimerListener l = new GameTimerListener() {

            @Override
            public void gameTimerActionPerformed(GameTimerEvent evt) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void gameTimerOneSecond(GameTimerEvent evt) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
        GameTimer instance = new GameTimer();
        instance.addGameTimerListener(l);
       
    }

   
    public void testRemoveGameTimerListener() {
        
        GameTimerListener l = new GameTimerListener() {

            @Override
            public void gameTimerActionPerformed(GameTimerEvent evt) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void gameTimerOneSecond(GameTimerEvent evt) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
        GameTimer instance = new GameTimer();
        instance.removeGameTimerListener(l);
        
    }

    
    //second case :if type != GameTimerEvent.TIMER_CLOCK_TICK
    public void testFireGameTimerEvent() {
       
       
        GameTimer instance = new GameTimer();
       // first case: if type =GameTimerEvent.TIMER_CLOCK_TICK
        instance.fireGameTimerEvent(GameTimerEvent.TIMER_CLOCK_TICK);
       //second case :if type != GameTimerEvent.TIMER_CLOCK_TICK
        int type = GameTimerEvent.TIMER_CLOCK_TICK +2; //to ensure it is not equal to timer tick.
        instance.fireGameTimerEvent(type);
        
    }

    
    public void testActionPerformed() {
        Object obj= new Object();
        int id =1;
        String str ="text";
        ActionEvent evt = new ActionEvent(obj, id, str);
        GameTimer instance = new GameTimer();
        instance.actionPerformed(evt);
        
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
        };
        GameTimer instance = new GameTimer();
        instance.paintComponent(g);
       
    }

    
    public void testMain() {
        
        String[] args = null;
        GameTimer.main(args);
        
    }
}
