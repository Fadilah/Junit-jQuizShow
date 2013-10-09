/*
 * GameTimer.java
 *
 * Created on April 28, 2001, 12:05 PM
 *
 * $Id: GameTimer.java,v 1.3 2007/02/06 05:20:21 sdchen Exp $
 *
 *============================================================================
 *
 * Copyright (C) 2001  Steven D. Chen
 *
 * This file is part of jQuizShow.
 *
 * jQuizShow is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * jQuizShow is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License (GPL)
 * along with jQuizShow; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *============================================================================
 *
 * Modifications:
 *
 *    $Log: GameTimer.java,v $
 *    Revision 1.3  2007/02/06 05:20:21  sdchen
 *    Added key listener to overcome keyboard focus problem.
 *
 *    Revision 1.2  2007/02/05 04:05:34  sdchen
 *    Replaced deprecated show() with setVisible(bool  s)
 *
 *    Revision 1.1  2004/04/02 06:01:59  sdchen
 *    Snapshot of jQuizShow 1.1 alpha06 development
 *
 *    Revision 1.1.1.1  2002/04/13 17:01:26  sdchen
 *    Initial import of the jQuizShow sources from local development directory.
 *
 *
 */

package jQuizShow;

/**
 *
 * @author  Steven D. Chen
 * @version 
 */

import java.lang.Math.*;
import java.text.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.event.*;

import jQuizShow.game.*;
import jQuizShow.event.*;

public class GameTimer extends JPanel
        implements ActionListener
{
    /** Creates new GameTimer */
    public GameTimer()
    {
        m_clockFaceColor = Color.darkGray;
        m_clockHandColor = Color.orange;
        m_clockTailColor = Color.lightGray;
        m_textColor = Color.white;
        m_backgroundColor = Color.black;
        
        m_timer = new Timer(1000, this);
        m_timer.setRepeats(true);
        
        m_evt = new GameTimerEvent(this, 0);
        m_GameTimerListenerList = new EventListenerList();

        /* Capture resize events */
        addComponentListener(new ComponentAdapter()
        {
            public void  componentResized(ComponentEvent  evt)
            {
                Component component = evt.getComponent();

		updateSize(component);
                
                repaint();
            }
        } );

        // Listen for keyboard events
        addKeyListener(new java.awt.event.KeyAdapter()
        {
            public void keyPressed(java.awt.event.KeyEvent evt)
            {
                int  keyCode = evt.getKeyCode();
                char  keyChar = evt.getKeyChar();
                
                GameState  gState = GameState.getInstance(false);
                
                GameInputEvent  event = new GameInputEvent(this);

                event.setType(GameInputEnum.KEY_TYPED);
                event.setKeyCode(keyCode, keyChar);
                gState.fireGameInputEvent(event);
            }
        } );
        
        setFocusable(true);
    }        
    
    /**
     * Sets up the timer.
     *
     *@param timeLimit - End time in seconds for the timer
     *@param currentTime - Current number of seconds elapsed
     *@param countdown - "true" will show a count down timer vs. a count up timer
     */
    public void setTimer(int timeLimit, int  currentTime, boolean  countdown)
    {
        stopTimer();
        m_timeLimit = timeLimit;
        m_elapsedTime = currentTime;
        m_countdown = countdown;

        /* Sanity checks */
        if (m_timeLimit < 0)
            m_timeLimit = 0;
        
        if (currentTime > timeLimit)
            m_elapsedTime = timeLimit;

        return;
    }
    
    /** Set the current timer time.  This is used primarily for driving the
     * timer manually.
     */
    public void setTime(int  currentTime)
    {
         if (currentTime >= 0 && currentTime <= m_timeLimit)
        {
            m_elapsedTime = currentTime;
            repaint();
        }
    }
    
    /** Starts the timer running */
    public void startTimer()
    {
        if (m_timeLimit == 0)
            m_timer.setInitialDelay(10);
        else
            m_timer.setInitialDelay(1000);
        
        m_timer.start();
        m_paused = false;

        /* Send event to any listener */
        fireGameTimerEvent(GameTimerEvent.TIMER_STARTED);
    }

    /** Stops/pauses the timer */
    public void stopTimer()
    {
        m_timer.stop();
        m_paused = true;

        /* Send event to any listener */
        fireGameTimerEvent(GameTimerEvent.TIMER_STOPPED);
    }

    /** Sets the main colors of the timer */
    public void setColor(Color  faceColor, Color  handColor, Color  textColor)
    {
        m_clockFaceColor = new Color(faceColor.getRGB());
        m_clockHandColor = new Color(handColor.getRGB());
        m_textColor = new Color(textColor.getRGB());
    }

    /** Sets all the colors for the timer */
    public void setColor(Color  faceColor, Color  handColor, Color  textColor,
            Color  tailColor, Color  backgroundColor)
    {
        m_clockTailColor = new Color(tailColor.getRGB());
        m_backgroundColor = new Color(backgroundColor.getRGB());

        setColor(faceColor, handColor, textColor);
    }

    /** Returns "true" if the timer is paused */
    public boolean isPaused()
    {
        return m_paused;
    }

    /** Gets the current time */
    public int getTime() {
        if (m_countdown)
            return  m_timeLimit - m_elapsedTime;
        else
            return m_elapsedTime;
    }

    public void addGameTimerListener(GameTimerListener  l)
    {
        m_GameTimerListenerList.add(GameTimerListener.class, l);
    }
    
    public void removeGameTimerListener(GameTimerListener  l)
    {
        m_GameTimerListenerList.remove(GameTimerListener.class, l);
    }
    
    protected void  fireGameTimerEvent(int  type)
    {
        // Guaranteed to return a non-null array
        Object[] listeners = m_GameTimerListenerList.getListenerList();

        m_evt.setType(type);
        
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
            if (listeners[i] == GameTimerListener.class)
            {
                if (type == GameTimerEvent.TIMER_CLOCK_TICK)
                    ((GameTimerListener)listeners[i+1]).gameTimerOneSecond(m_evt);
                else
                    ((GameTimerListener)listeners[i+1]).gameTimerActionPerformed(m_evt);
            }
        }        
    }

    public void actionPerformed(ActionEvent  evt)
    {
        if (evt.getSource() == m_timer)
        {
            m_elapsedTime++;

            if (m_elapsedTime >= m_timeLimit)
            {
                /* Time elapsed.  Stop timer and fire off an event. */
                m_timer.stop();
                m_paused = true;
                m_elapsedTime = m_timeLimit;

                /* Send event to any listener */
                fireGameTimerEvent(GameTimerEvent.TIMER_ELAPSED);
            }
            else
            {
                /* Send clock tick event to any listener */
                fireGameTimerEvent(GameTimerEvent.TIMER_CLOCK_TICK);
            }

            repaint();
        }
    }


    /**
     * Get the size of the component and get a new font if required.
     */
    private void  updateSize(Component  component)
    {
	Graphics  g = component.getGraphics();

	Dimension  dim = component.getSize();

	int  minDim = Math.min(dim.width, dim.height);
	
	int  ptSize = (int) (minDim * 0.9f / 3);
	
	if (m_font == null || ptSize != m_ptSize)
	{
	    m_font = new Font("Helvetica", Font.BOLD, ptSize);
	    m_fontMetrics = g.getFontMetrics(m_font);
	    m_ptSize = ptSize;
	}
    }

    
    protected void  paintComponent(Graphics  g)
    {
        JPanel x;

        Graphics2D  g2 = (Graphics2D) g;
        
        Dimension size = getSize();
	
	// This code is needed here because JInternalFrames do not get
	// a resize event when first drawn.
	if (m_firstShown)
	{
	    updateSize(this);
	    m_firstShown = false;
	}
        
        // Clear everything
        g2.setBackground(m_backgroundColor);
        g2.clearRect(0, 0, size.width, size.height);
        
        float  minDim = Math.min(size.width, size.height);

        final float  lineThickness = minDim * 0.1f;
        
        Rectangle2D  bounds = new Rectangle2D.Float(
                lineThickness, lineThickness,
                minDim - lineThickness * 2, minDim - lineThickness * 2);

        // Draw the analog clock "face" outline

        {
            Arc2D  arc = new Arc2D.Float(bounds, 0.0f, 360.0f, Arc2D.OPEN);

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(m_clockFaceColor);
            g2.fill(arc);
        }
        
        // Draw the elapsed (used) time portion

        {
            g2.setColor(m_clockTailColor);

            g2.setStroke(new BasicStroke(lineThickness * 0.2f, BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_ROUND));

            float arcAngle = m_elapsedTime / (float) m_timeLimit * 360.0f;

            Arc2D  arc = new Arc2D.Float(bounds, 90.0f, -arcAngle, Arc2D.OPEN);

            g2.draw(arc);
        }
        // Draw the remaining time portion

        {
            g2.setColor(m_clockHandColor);

            g2.setStroke(new BasicStroke(lineThickness, BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_ROUND));

            float arcAngle = (m_timeLimit - m_elapsedTime) / (float) m_timeLimit * 360.0f;

            Arc2D  arc = new Arc2D.Float(bounds, 90.0f, arcAngle, Arc2D.OPEN);

            g2.draw(arc);
        }
        
        // Draw the digital time clock
        
        java.util.Calendar  cal = java.util.Calendar.getInstance();

        cal.set(0, 0, 0, 0, 0);
        cal.set(java.util.Calendar.SECOND,
                m_countdown ? (m_timeLimit - m_elapsedTime) :
                               m_elapsedTime);

        int  minutes = cal.get(java.util.Calendar.MINUTE);
        int  seconds = cal.get(java.util.Calendar.SECOND);
        
        StringBuffer  strb = new StringBuffer();
        
        if (minutes != 0)
            strb.append(minutes);

        strb.append(":");
        
        if (seconds < 10)
            strb.append("0");
        
        strb.append(seconds);
        
        String  timeStr = strb.toString();
        
        Point2D.Float center = new Point2D.Float(
                minDim * 0.5f, minDim * 0.5f);
        
        if (m_fontMetrics != null)
        {
            int width = m_fontMetrics.stringWidth(timeStr);

            float h = center.y + m_fontMetrics.getAscent() * 0.4f;

            g2.setColor(m_textColor);
            g2.setFont(m_font);
            g2.drawString(timeStr, center.x - width * 0.55f, h);
        }
    }

    /**
     * Test program
     * @param args the command line arguments
    */
    public static void main (String args[]) {
        Toolkit     tk = Toolkit.getDefaultToolkit();
        Dimension   screenSize = tk.getScreenSize();
        
        Dimension   initSize = new Dimension();
        
        Point       initLocation = new Point();

        JFrame  timerFrame = new JFrame();

        timerFrame.addWindowListener(new java.awt.event.WindowAdapter()
        {
            public void windowClosing(java.awt.event.WindowEvent evt)
            {
                System.exit(0);
            }
        });

        initSize.width = (int) (screenSize.width * 0.4);
        initSize.height = (int) (screenSize.height * 0.4f);
        
        timerFrame.setSize(initSize);
        timerFrame.setLocation(50, 50);
        
        GameTimer  gameTimer = new GameTimer();
        
        timerFrame.getContentPane().add(gameTimer);
        timerFrame.setVisible(true);

        gameTimer.setTimer(30, 5, true);
        gameTimer.startTimer();
    }
    
    private int m_timeLimit;
    
    private int m_elapsedTime;
    
    private boolean m_countdown;
    
    private boolean m_paused;

    private Timer  m_timer;
    
    private Font  m_font;
    
    private FontMetrics  m_fontMetrics;
    
    private int  m_ptSize;
    
    private Color  m_clockFaceColor;
    private Color  m_clockTailColor;
    private Color  m_clockHandColor;
    private Color  m_textColor;
    private Color  m_backgroundColor;

    private GameTimerEvent  m_evt = null;

    protected EventListenerList  m_GameTimerListenerList;   // Listeners of time up event
    
    private boolean  m_firstShown = true;
}
