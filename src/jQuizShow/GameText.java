/*
 * GameText.java
 *
 * Created on April 21, 2001, 10:06 PM
 *
 * $Id: GameText.java,v 1.2 2007/02/05 04:05:34 sdchen Exp $
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
 *    $Log: GameText.java,v $
 *    Revision 1.2  2007/02/05 04:05:34  sdchen
 *    Replaced deprecated show() with setVisible(bool  s)
 *
 *    Revision 1.1  2004/04/02 06:01:59  sdchen
 *    Snapshot of jQuizShow 1.1 alpha06 development
 *
 *    Revision 1.1.1.1  2002/04/13 17:01:24  sdchen
 *    Initial import of the jQuizShow sources from local development directory.
 *
 *
 */

package jQuizShow;

import java.awt.*;
import java.awt.geom.*;
import java.awt.font.*;
import java.awt.event.*;
import javax.swing.*;
import java.text.*;
import javax.swing.event.*;
import java.util.*;

import jQuizShow.*;
import jQuizShow.event.*;

/**
 *
 * @author  Steven D. Chen
 * @version 1.0
 */
public class GameText extends JPanel {

    private static final String FONT_NAME_SUFFIX = "FontName";
    private static final String FONT_BOLD_SUFFIX = "FontBold";
    private static final String FONT_ITALIC_SUFFIX = "FontItalic";

    /** Creates new GameText */

    public GameText(int  id)
    {
        initialize(id, new Dimension(32, 2));
    }
    
    public GameText(int  id, Dimension  size)
    {
        initialize(id, size);
    }

    /**
     * Sets the basename that is used to look up the font settings
     * in the configuration file.  The desired font parameter is
     * specified by appending the appropriate suffix to the basename,
     * e.g. if the basename was "question", "questionFontName" would
     * be the name of the font for the questions.
     */
    public void   setFontBasename(String  basename)
    {
        m_fontBasename = basename;
    }
    
    private void  initialize(int  id, final Dimension  rowcol)
    {
        m_id = id;

        m_gameConfig = GameConfig.getInstance();
        
        m_label = new String();
        m_text = new String();
        
        setTextColor();
        setBorderColor(Color.blue);
        
        if (rowcol.width < 1 || rowcol.height < 1)
        {
            throw new IndexOutOfBoundsException();
        }
        
        // Initialize the GameActionEvent and the listener list
        m_evt = new GameInputEvent(this);
        m_eventListenerList = new EventListenerList();
        
        // Add a listener for component resize events
        addComponentListener(new java.awt.event.ComponentAdapter()
        {
            public void componentResized(java.awt.event.ComponentEvent evt)
            {
                Component  component = evt.getComponent();
                Graphics  g = component.getGraphics();
                m_size = component.getSize();

                m_parentBG = component.getParent().getBackground();

                // Get the font preferences
                m_fontName = m_gameConfig.getConfig(
                        m_fontBasename + FONT_NAME_SUFFIX, "SansSerif");
                
                if (m_gameConfig.getIntConfig(m_fontBasename + FONT_BOLD_SUFFIX, 1) != 0)
                    m_fontStyle = Font.BOLD;
                else
                    m_fontStyle = Font.PLAIN;
                
                if (m_gameConfig.getIntConfig(m_fontBasename + FONT_ITALIC_SUFFIX, 0) != 0)
                    m_fontStyle |= Font.ITALIC;

		StringBuffer strBuf = new StringBuffer((int) rowcol.width);

		for (int i = 0; i < rowcol.width; i++)
		{
		    strBuf.append('W');
		}

		String  testString = strBuf.toString();
                
                /* Scale the font to fit to the size of the actual panel
                 *
                 * Make initial guess too large
                 */
                int  ptSize = (int) (m_size.width / 5.0f);

                for (int  loop = 0; loop < 10; loop++)
                {
                    m_font = new Font(m_fontName, m_fontStyle, ptSize);
                    m_fontMetrics = g.getFontMetrics(m_font);
                    
                    int  height = rowcol.height * m_fontMetrics.getHeight();
                    
                    if (height > m_size.height)
                    {
                        ptSize *= (1.0f - (height - m_size.height) / ((float) height));
                        continue;
                    }

                    int  width = m_fontMetrics.stringWidth(testString) + (int) (height / 2.5 + 1.0);

                    if (width > m_size.width)
                    {
                        ptSize *= (1.0f - (width - m_size.width) / ((float) width));
                        continue;
                    }
                    
                    // Font size fitted to panel size.  Exit.
                    break;
                }

                component.repaint();
            }
        });
        
        // Add a listener for mouse events
        addMouseListener(new MouseAdapter()
        {
            public void mousePressed(MouseEvent  evt)
            {
                m_evt.setType(GameInputEnum.MOUSE_BUTTON_1);
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
    
    public void addGameInputListener(GameInputListener  l)
    {
        m_eventListenerList.add(GameInputListener.class, l);
    }
    
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
    
    public void setText(String  label, String text)
    {
        m_label = new String(label);
        m_text = new String(text);
        repaint();
        return;
    }

    public void setTextColor()
    {
        setTextColor(Color.orange, Color.white, Color.black);
    }

    public void setTextColor(Color  labelColor, Color textColor, Color  bgColor)
    {
        m_labelColor = new Color(labelColor.getRGB());
        m_textColor = new Color(textColor.getRGB());
        m_bgColor = new Color(bgColor.getRGB());
        repaint();
    }

    public void setBorderColor(Color color)
    {
        m_borderColor = new Color(color.getRGB());
        repaint();
    }

    public void centerText(boolean  center)
    {
        m_center = center;
    }
    
    public void paintComponent(Graphics  g)
    {
        Graphics2D  g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        
        if (m_fontMetrics == null)
            return;
        
        final int  borderWidth = m_fontMetrics.getAscent() / 5 + 1;
        
        // Save the original (current) graphics transform
        AffineTransform  origTransform = g2.getTransform();
        
        // Clear everything
        g2.setBackground(m_parentBG);
        g2.clearRect(0, 0, m_size.width, m_size.height);
    
        Dimension  size = new Dimension(m_size);

        g2.translate(0, borderWidth);
        
        size.height -= borderWidth * 2;

        // Draw the six-sided Question box
        GeneralPath  path = new GeneralPath();
        path.moveTo(size.width * 0.05f, size.height / 2.0f);
        path.lineTo(size.width * 0.05f + 15.0f, 0.0f);
        path.lineTo(size.width * 0.95f - 15.0f, 0.0f);
        path.lineTo(size.width * 0.95f, size.height / 2.0f);
        path.lineTo(size.width * 0.95f - 15.0f, size.height);
        path.lineTo(size.width * 0.05f + 15.0f, size.height);
        path.closePath();     
        
        // Draw the line extensions on left & right sides
        path.moveTo(0.0f, size.height / 2.0f);
        path.lineTo(size.width * 0.05f, size.height / 2.0f);
        path.moveTo(size.width * 0.95f, size.height / 2.0f);
        path.lineTo(size.width, size.height / 2.0f);
        
        // Draw the background

        g2.setColor(m_bgColor);
        
        g2.fill(path);

        // Draw the border
        
        g2.setColor(m_borderColor);
        g2.setStroke(new BasicStroke(borderWidth, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_ROUND));

        g2.draw(path);
        
        // Draw the label

        g2.setFont(m_font);
        
        Point2D.Float pen = new Point2D.Float(size.width * 0.1f, size.height * 0.5f);
        
        if (m_label.length() > 0)
        {
            g2.setColor(m_labelColor);

            int width = m_fontMetrics.stringWidth(m_label + "  ");
            float h = pen.y + m_fontMetrics.getAscent() * 0.5f;
            
            g2.drawString(m_label, pen.x, h);
            
            pen.x += width;
        }

        // Draw the text
        
        if (m_text.length() > 0)
        {
            g2.setColor(m_textColor);
            
            AttributedString  attrString = new AttributedString(m_text);
            attrString.addAttribute(TextAttribute.FONT, m_font);
            
            AttributedCharacterIterator  attrIter = attrString.getIterator();
            FontRenderContext  frc = g2.getFontRenderContext();
            
            LineBreakMeasurer measurer = new LineBreakMeasurer(
                    attrIter, frc);
            
            float wrappingWidth = size.width * 0.9f - pen.x;

            float height = 0.0f;

            float width = 0.0f;
            
            // Calculate the offset in order to center the text vertically.
            while (measurer.getPosition() < attrIter.getEndIndex())
            {
                TextLayout layout = measurer.nextLayout(wrappingWidth);
                height += layout.getAscent() + layout.getDescent() +
                        layout.getLeading();

                float  advance = layout.getAdvance();
                
                if (advance > width)
                    width = advance;
            }         

            pen.y -= height * 0.5f;

            // Draw the text
            measurer.setPosition(0);

            float  xOffset = 0.0f;
            
            if (m_center)
                xOffset = (wrappingWidth - width) * 0.5f;
            
            while (measurer.getPosition() < attrIter.getEndIndex())
            {
                TextLayout layout = measurer.nextLayout(wrappingWidth);
                pen.y += (layout.getAscent());
                layout.draw(g2, pen.x + xOffset, pen.y);
                pen.y += layout.getDescent() + layout.getLeading();
            }         
        }
        
        // Restore the original graphics transform
        g2.setTransform(origTransform);
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
        
        GameText  gameText = new GameText(111);
        
        gameText.setText("A:", "The quick brown fox jumps over the lazy dog but was scared away when he saw the grumpy cat");
        
        jframe.getContentPane().add(gameText);
        jframe.setVisible(true);
    }
    
    private GameConfig  m_gameConfig;   // GameConfig instance
    
    private int  m_id;              // Instance ID

    private boolean  m_center = false;      // Center text if true
    
    private String  m_label;        // Label string

    private String  m_text;         // Text string

    private String  m_numLines;     // Number of text lines

    private String  m_fontBasename;   // Config family name
    private String  m_fontName;     // Font name as specified in config file
    private int  m_fontStyle;       // Font style
    
    private Font  m_font;           // Font for text

    private FontMetrics  m_fontMetrics;  // Font metrics
    
    private Dimension  m_size;      // Current dimension of JPanel
    
    private Color  m_labelColor;
    
    private Color  m_textColor;
    
    private Color  m_bgColor;
    
    private Color  m_borderColor;
    
    private Color  m_parentBG;      // Parent component's background color
    
    private GameInputEvent  m_evt = null;
    protected EventListenerList  m_eventListenerList;   // Listeners interested in a selection
}
