/*
 * ResizableText.java
 *
 * Created on February 11, 2004  7:04 PM
 *
 * $Id: ResizableText.java,v 1.2 2007/02/05 04:05:34 sdchen Exp $
 *
 *============================================================================
 *
 * Copyright (C) 2004  Steven D. Chen
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
 *    $Log: ResizableText.java,v $
 *    Revision 1.2  2007/02/05 04:05:34  sdchen
 *    Replaced deprecated show() with setVisible(bool  s)
 *
 *    Revision 1.1  2004/04/02 06:01:59  sdchen
 *    Snapshot of jQuizShow 1.1 alpha06 development
 *
 *
 */

package jQuizShow;

import java.awt.*;
import java.awt.geom.*;
import java.awt.font.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.*;
import java.text.*;
import javax.swing.event.*;
import java.util.*;

import jQuizShow.event.*;

/**
 *
 * @author  Steven D. Chen
 */
public class ResizableText
        extends JPanel
        implements ActionListener
{
    private static final int  FADER_DELAY = 50;
    
    private static final int  LEFT_MARGIN = 0;
    private static final int  TOP_MARGIN = 0;
    private static final int  BOTTOM_MARGIN = 0;

    private static final int  WRAP_BIAS = 15;    // Bias to word wrapping
    
    private static final boolean  Debug = true;
    
    /** Creates new ResizableText */

    public ResizableText(int id)
    {
        initialize(id, "SansSerif", Font.BOLD);
    }

    
    public ResizableText(int id, String  fontName, int  fontStyle)
    {
        initialize(id, fontName, fontStyle);
    }


    private void  initialize(int  id, String  fontName, int  fontStyle)
    {
        m_id = id;

        m_gameConfig = GameConfig.getInstance();
        
        m_text = new String();

        m_timer = new javax.swing.Timer(FADER_DELAY, this);
        
        m_fontName = fontName;
        m_fontStyle = fontStyle;

        m_renderBounds = new Dimension();
        
        restoreDefaultColors();
       
        // Initialize the GameActionEvent and the listener list
        m_evt = new GameInputEvent(this);
        m_eventListenerList = new EventListenerList();

        // Add a listener for component resize events
        addComponentListener(new java.awt.event.ComponentAdapter()
        {
            public void componentResized(java.awt.event.ComponentEvent evt)
            {
                if (m_columnHint > 0)
                {
                    // Calculate new font size based on the number of
                    // columns to display.
                    
                    // Save the current text
                    String  saveText = m_text;

                    StringBuffer  buf = new StringBuffer();
                    buf.setLength(m_columnHint);

                    for (int  idx = 0; idx < m_columnHint; idx++)
                        buf.setCharAt(idx, 'W');

                    m_text = buf.toString();

                    calcFont();
                    
                    m_maxPointSize = m_pointSize;

                    m_text = saveText;
                }

                // Recalculate size for optimum font (for the current text string)
                calcFont();
            }
        });

        
        // Add a listener for mouse events
        addMouseListener(new MouseAdapter()
        {
            public void mousePressed(MouseEvent  evt)
            {
                m_evt.setType(GameInputEnum.ANSWER_SELECTED);
                m_evt.setSelectedIndex(m_id);
                fireGameInputEvent(m_evt);
            }
        } );
    }

    
    /**
     * Get the object's assigned ID.
     */
    public int  getId()
    {
        return m_id;
    }
    

    /**
     * Adds the specified object as a listener for game input events
     * (e.g. mouse clicks) associated with this instance.
     */
    public void addGameInputListener(GameInputListener  l)
    {
        m_eventListenerList.add(GameInputListener.class, l);
    }
    
    
    /**
     * Removes the specified object from the game input listener list.
     */
    public void removeGameInputListener(GameInputListener  l)
    {
        m_eventListenerList.remove(GameInputListener.class, l);
    }
    
    
    protected void  fireGameInputEvent(GameInputEvent  evt)
    {
        // Guaranteed to return a non-null array
        Object[] listeners = m_eventListenerList.getListenerList();

        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
            if (listeners[i] == GameInputListener.class)
            {
                ((GameInputListener)listeners[i+1]).gameInputReceived(evt);
            }
        }        
    }

    
    /**
     * Set number of columns to display.  This is a hint which is used to
     * calculate the approximate maximum point size to display the given
     * number of columns.  When this is accomplished, the font will not
     * exceed the calculated maximum size until this is reset.
     *
     * @param rows  maximum number of rows to display (0 = no limit)
     * @param columns  columns to display (0 = no limit)
     */
    public void setRowColumnHint(int  rows, int  columns)
    {
        // Clear the max point size initially.
        m_maxPointSize = 0;
        
        m_rowHint = rows;
        m_columnHint = columns;

        // Recalculate the font to show text at new size
        calcFont();
        repaint();
    }

    
    /**
     * Sets the text to display.
     */
    public void setText(String text)
    {
        if (text != null)
            m_text = new String(text);
        else
            m_text = null;

        m_timer.stop();
        
        calcFont();
        
        if (m_fadeDuration > 0)
        {
            m_alphaDelta = 1.0f / m_fadeDuration * FADER_DELAY;
            m_elapsedTime = 0;      // Reset elapsed time
            m_alpha = 0.0f;         // Set alpha to be initially totally transparent
            m_timer.start();        // Start the fade-in timer
        }
        else
            m_alpha = 1.0f;
        
        repaint();
        
        return;
    }

    
    /**
     * Sets the default colors used for rendering the text and background
     * (if opaque).  These are used when restoreDefaultColors() is called.
     */
    public void setDefaultTextColor(Color  textColor, Color  bgColor)
    {
        m_textColorDefault = textColor;
        m_bgColorDefault = bgColor;
        
        restoreDefaultColors();
    }
    
    
    /**
     * Resets the colors to those stored by setDefaultTextColor().
     */
    public void restoreDefaultColors()
    {
        setTextColor(m_textColorDefault, m_bgColorDefault);
    }

    
    /**
     * Sets the text and opaque background (if used) to the specified colors.
     */
    public void setTextColor(Color textColor, Color  bgColor)
    {
        m_textColor = textColor;
        
        if (bgColor == null)
            setOpaque(false);
        else
        {
            m_bgColor = bgColor;
            setOpaque(true);
        }

        repaint();
    }

    
    /**
     * Sets the fade-in delay time (in milliseconds).  If this is <= 0, the text
     * is displayed instantly (i.e. no fade-in).
     */
    public void  setFadeInDelay(int  msecs)
    {
        m_timer.stop();

        if (msecs > 0)
            m_fadeDuration = msecs;
        else
            m_fadeDuration = 0;
    }

    
    /**
     * Enables/disables the fade-in delay.
     */
    public void  enableFadeInDelay(boolean  enable)
    {
        m_fadeEnabled = enable;
    }

    
    /**
     * Sets the font name and style for the text.  The 'fontStyle' uses the
     * values defined for the java.awt.Font class (i.e. Font.PLAIN, Font.BOLD,
     * Font.ITALIC, or Font.BOLD | Font.ITALIC).
     */
    public void  setTextFont(String  fontName, int  fontStyle)
    {
        m_fontName = fontName;
        m_fontStyle = fontStyle;
    }
    
    
    /**
     * Centers the text horizontally if set "true".
     */
    public void centerText(boolean  center)
    {
        m_center = center;
    }

    
    /**
     * Draws the ResizableText as an opaque rectangle.  By default, the
     * text is drawn transparently.  The background color is used as the
     * color for the background rectangle.
     */
    public void setOpaque(boolean  state)
    {
        m_opaque = state;
    }

    
    /**
     * Sets the point size to be used for the text.  If 0, the text
     * size will automatically scaled to fit the bounding box.
     */
    public void setPointSize(int ptSize)
    {
        if (ptSize <= 0)
            calcFont();
        else if (ptSize != m_pointSize)
            setFontToSize(ptSize);
    }

    
    /**
     * Gets the point size that was calculated that would cause the text
     * to fill the bounding box.  This is used by the ResizableTextGroup
     * to set all fonts in the group to the same size.
     */
    public int getPointSize()
    {
        return m_pointSize;
    }


    /** Calculates the font needed to render the text as large as possible
     * in the given space.
     */
    private void  calcFont()
    {
        Graphics  g = getGraphics();
        
        if (g == null)
            return;

        Dimension  size = getSize();

        if (m_text != null && m_text.length() > 0)
        {
            int  ptSize = size.height - TOP_MARGIN - BOTTOM_MARGIN;

            int  rows;
            int  loop;
        
            // Limit font size to the maximum allowed size
            if (m_maxPointSize > 0 && ptSize > m_maxPointSize)
                ptSize = m_maxPointSize;

            for (loop = 0; loop < 20; loop++)
            {
                rows = setFontToSize(ptSize);
                
                if (m_renderBounds.height < size.height
                        && (m_rowHint == 0 || rows <= m_rowHint))
                    break;      // Text fits within the specified parameters.  Exit loop.

                // Calculate a new guess for the font size
                ptSize = (int) (ptSize * (1 - 1.0 / (loop + 3)));
            }

            if (Debug || loop == 20)
            {
                System.out.println("calcFont(" + m_id +
                        ") width/height=" + getWidth() + "/" + getHeight()
                        + ", loop=" + (loop + 1)
                        + ", ptSize=" + ptSize);
            }
            
            m_pointSize = ptSize;
        }
    }


    /** Sets the font size to the specified size.
     *
     * @param ptSize  Font point size
     *
     * @return Number of rows the text will span
     */
    private int  setFontToSize(int  ptSize)
    {
        Graphics  g = getGraphics();

        float height = 0.0f;

        float width = 0.0f;

        int  rows = 0;
        
        if (g == null)
            return 0;
        
        Graphics2D g2d = (Graphics2D) g.create();   // Create a copy of "g"

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        Dimension  size = new Dimension();

        size.width = getWidth();
        size.height = getHeight() - TOP_MARGIN - BOTTOM_MARGIN;
        
        if (m_text != null && m_text.length() > 0)
        {
            float wrappingWidth = size.width - LEFT_MARGIN - WRAP_BIAS;

            m_font = new Font(m_fontName, m_fontStyle, ptSize);

            m_attrString = new AttributedString(m_text);
            m_attrString.addAttribute(TextAttribute.FONT, m_font);           

            m_attrIter = m_attrString.getIterator();

            FontRenderContext  frc = g2d.getFontRenderContext();

            m_measurer = new LineBreakMeasurer(m_attrIter, frc);

            height = 0.0f;
            width = 0.0f;

            m_measurer.setPosition(0);


            int  i;

            // Calculate the offset in order to center the text vertically.
            while (m_measurer.getPosition() < m_attrIter.getEndIndex())
            {
                TextLayout layout = m_measurer.nextLayout(wrappingWidth);

                height += layout.getAscent() + layout.getDescent() +
                        layout.getLeading();

                float  advance = layout.getAdvance();

                if (advance > width)
                    width = advance;
                
                rows++;
            }
            
            if (Debug)
            {
                System.out.println("setFontToSize(" + m_id
                        + ") -- width/height="
                        + getWidth()
                        + "/" + getHeight()
                        + ", ptSize=" + ptSize
                        + ", width=" + width
                        + ", height=" + height
                        + ", rows=" + rows);
            }

            m_renderBounds.width = (int) width;
            m_renderBounds.height = (int) height;
        }

        return rows;
    }

    
    public void paintComponent(Graphics  g)
    {
        Graphics2D g2d = (Graphics2D) g.create();   // Create a copy of "g"

        BufferedImage buffImg = new BufferedImage(getWidth(), getHeight(),
                                    BufferedImage.TYPE_INT_ARGB);
        Graphics2D gbi = buffImg.createGraphics();

        gbi.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        Dimension  size = this.getSize();

        if ((m_opaque && m_bgColor != null) ||
                (m_gameConfig.getIntConfig("debugMode") & GameConfig.DEBUG_SHOW_TEXT_BOXES) != 0)
        {
            // Clear everything
            if (m_bgColor == null)
                gbi.setBackground(new Color(0xf0e0d0));
            else
                gbi.setBackground(m_bgColor);
            
            gbi.clearRect(0, 0, size.width, size.height);
        }
        
        Point2D.Float pen = new Point2D.Float(LEFT_MARGIN, TOP_MARGIN);
        
        if (m_fadeEnabled && m_alpha < 1.0f)
        {
            gbi.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                    m_alpha));
        }
        
        // Draw the text
        
        if (m_text != null && m_text.length() > 0)
        {
            gbi.setColor(m_textColor);
            gbi.setFont(m_font);

            float wrappingWidth = size.width - LEFT_MARGIN - WRAP_BIAS;
            
            pen.y = (size.height - m_renderBounds.height) / 2;

            // Draw the text
            m_measurer.setPosition(0);

            float  xOffset = 0.0f;

            if (m_center)
                xOffset = (wrappingWidth - m_renderBounds.width) * 0.5f;

            while (m_measurer.getPosition() < m_attrIter.getEndIndex())
            {
                TextLayout layout = m_measurer.nextLayout(wrappingWidth);
                pen.y += (layout.getAscent());
                layout.draw(gbi, pen.x + xOffset, pen.y);
                pen.y += layout.getDescent() + layout.getLeading();
            }
        }

        g2d.drawImage(buffImg, null, 0, 0);
        
        g2d.dispose();      // Clean up copy of "g"
    }
    
    
    /**
     * Listener callback for Timer events.
     */
    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == m_timer)
        {
            m_elapsedTime++;

            m_alpha += m_alphaDelta;
            
            if (m_elapsedTime >= m_fadeDuration || m_alpha > 1.0f)
            {
                /* Time elapsed (or full opaque).  Stop timer. */
                m_timer.stop();
                m_alpha = 1.0f;      // Set to full opaque
            }

            repaint();
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

        JFrame  jframe = new JFrame();

        jframe.addWindowListener(new java.awt.event.WindowAdapter()
        {
            public void windowClosing(java.awt.event.WindowEvent evt)
            {
                System.exit(0);
            }
        });

        initSize.width = (int) (screenSize.width * 0.5);
        initSize.height = (int) (screenSize.height * 0.2f);
        
        System.out.println("Dimension = " + initSize);
        
        jframe.setSize(initSize);
        jframe.setLocation(50, 50);
        
        ResizableText  text = new ResizableText(111);
        
        text.setText("The quick brown fox jumps over the lazy dog but was scared away when he saw the grumpy cat");
        text.setOpaque(true);
        text.centerText(false);

        text.setTextColor(Color.YELLOW, Color.BLACK);
        
        jframe.getContentPane().add(text);
        jframe.setVisible(true);
    }
    
    private GameConfig  m_gameConfig;   // GameConfig instance
    
    private int  m_id;              // Instance ID

    private int  m_pointSize = 0;       // Font point size

    private int  m_rowHint = 0;      // Maximum number of rows to display -- used to calc a m_maxPointSize (0 = no limit)
    private int  m_columnHint = 0;      // Number of columns to display -- used to calc m_maxPointSize (0 = no limit)
    private int  m_maxPointSize = 0;    // Maximum point size that will be used (0 = no limit)
    
    private Dimension  m_renderBounds;    // Bounds for the rendered text
    
    private boolean  m_center = false;      // Center text if true

    private boolean  m_opaque = false;       // Transparent or opaque

    private String  m_text;         // Text string

    private String  m_numLines;     // Number of text lines

    private javax.swing.Timer  m_timer;     // Fade-in timer
    private boolean  m_fadeEnabled = true;  // true = fade-in delay enabled
    private int  m_fadeDuration = 0;       // Fade-in time duration (ms)
    private int  m_elapsedTime = 0;       // Current elapsed time (ms)
    private float  m_alphaDelta;      // Delta alpha for each timer tick.
    private float  m_alpha = 1.0f;    // Current alpha value (0.0 - 1.0)
    
    private String  m_fontName = "SansSerif";     // Font name
    private int  m_fontStyle = Font.BOLD;       // Font style
    
    private Font  m_font;           // Font for text

    private AttributedString  m_attrString = null;
    private AttributedCharacterIterator  m_attrIter = null;
    private LineBreakMeasurer  m_measurer = null;
    
    private Color  m_textColor = Color.YELLOW;
    private Color  m_bgColor = Color.BLACK;
    
    private Color  m_textColorDefault = Color.YELLOW;
    private Color  m_bgColorDefault = null;
    
    private Color  m_parentBG;      // Parent component's background color
    
    private GameInputEvent  m_evt = null;
    protected EventListenerList  m_eventListenerList;   // Listeners interested in a selection
}
