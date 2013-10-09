/*
 * ScorePanel.java
 *
 * Created on May 8, 2001, 9:14 PM
 *
 * $Id: ScorePanel.java,v 1.2 2007/02/05 04:05:34 sdchen Exp $
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
 *    $Log: ScorePanel.java,v $
 *    Revision 1.2  2007/02/05 04:05:34  sdchen
 *    Replaced deprecated show() with setVisible(bool  s)
 *
 *    Revision 1.1  2004/04/02 06:01:59  sdchen
 *    Snapshot of jQuizShow 1.1 alpha06 development
 *
 *    Revision 1.1.1.1  2002/04/13 17:01:29  sdchen
 *    Initial import of the jQuizShow sources from local development directory.
 *
 *
 */

package jQuizShow.game.classic;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;


/**
 *
 * @author  Steven D. Chen
 */
public class ScorePanel extends javax.swing.JPanel
{
    public static final int HIGHLIGHT_OFF = -1;
    
    /** Creates new form ScorePanel */
    public ScorePanel(String[]  titles) {
        initComponents();

        m_titles = titles;

        m_indexColor = Color.orange;
        m_indicatorColor = Color.white;
        m_textColor = Color.orange;
        m_background = Color.red;
        m_highlightColor = Color.white;
        
        m_highlightTitles = new BitSet(m_titles.length);
        
        /* Listen for resize events */
        addComponentListener(new java.awt.event.ComponentAdapter()
        {
            public void componentResized(java.awt.event.ComponentEvent evt)
            {
                Component  component = evt.getComponent();
                Graphics  g = component.getGraphics();
                m_size = component.getSize();

                m_parentBG = component.getParent().getBackground();
                
                /* Scale the font to fit to the size of the actual panel
                 *
                 * Make initial guess too large
                 */
                int  ptSize = (int) (m_size.width / 5.0f);

                for (int  loop = 0; loop < 10; loop++)
                {
                    m_font = new Font("Helvetica", Font.BOLD, ptSize);
                    m_fontMetrics = g.getFontMetrics(m_font);
                    
                    int  fontHeight = m_fontMetrics.getHeight();
                    
                    int  height = m_titles.length * fontHeight;
                    
                    if (height > m_size.height)
                    {
                        ptSize *= (1.0f - (height - m_size.height) / ((float) height));
                        continue;
                    }

                    int  i;

                    for (i = 0; i < m_titles.length; i++)
                    {
                        int  width = m_fontMetrics.stringWidth(m_titles[i] + "88") +
                                fontHeight + 2 * m_fontMetrics.stringWidth("W");

                        if (width > m_size.width)
                        {
                            ptSize *= (1.0f - (width - m_size.width) / ((float) width));
                            break;
                        }
                    }
                    
                    if (i != m_titles.length)
                        continue;
                    else
                        break;
                }

                component.repaint();
            }
        });
    }

    /** Set the current question level. */
    public void  setCurrentLevel(int  level)
    {
        m_currentLevel = level;
        repaint();
    }

    /** Sets the highlight state of the specified level. */
    public void  setHighlight(int  index, boolean  highlight)
    {
        if (index < 0 || index > m_highlightTitles.size())
            return;
        
        if (highlight)
            m_highlightTitles.set(index);
        else
            m_highlightTitles.clear(index);

        return;
    }

    /** Draw the highlight border index */
    public void  setHighlightBorder(int  index)
    {
        if (index == HIGHLIGHT_OFF ||
                (index >= 0 && index < m_titles.length))
        {
            m_highlightBorderIndex = index;
        }
        
        repaint();
        return;
    }
    
    public void paintComponent(Graphics  g)
    {
        Graphics2D  g2 = (Graphics2D) g;
        
        final int  borderWidth = 6;
        
        // Save the original (current) graphics transform
        AffineTransform  origTransform = g2.getTransform();
        
        // Clear everything
        g2.setBackground(m_parentBG);
        g2.clearRect(0, 0, m_size.width, m_size.height);
    
        Dimension  size = new Dimension(m_size);

        g2.translate(0, borderWidth);
        
        size.height -= borderWidth * 2;

        int  lineHeight = m_fontMetrics.getMaxAscent();
        
        g2.setFont(m_font);
        
        Point2D.Float origin = new Point2D.Float(5.0f, lineHeight);

        float  spacing = (float) size.height  / m_titles.length;
        
        // Draw the text

        Point2D.Float pen = new Point2D.Float(origin.x, origin.y + m_lineHeight);
    
        for (int  line = m_titles.length; line > 0; line--)
        {
            String  title;
            
            int     width;

            boolean  highlight = m_highlightTitles.get(line);
            
            pen.x = origin.x;

            // If m_highlightBorderIndex is explicitly set, draw highlight box if this is the
            // specified line.
            if (m_highlightBorderIndex != HIGHLIGHT_OFF && m_highlightBorderIndex == line - 1)
            {
                Rectangle2D rect2D = new Rectangle2D.Float(0,
                        pen.y - m_fontMetrics.getMaxAscent(),
                        m_size.width,
                        m_fontMetrics.getMaxAscent() + m_fontMetrics.getMaxDescent());

                g2.setColor(m_background);
                g2.fill(rect2D);

                GeneralPath  path = new GeneralPath();
                
                path.moveTo(0, pen.y - m_fontMetrics.getMaxAscent());
                path.lineTo(m_size.width, pen.y - m_fontMetrics.getMaxAscent());
                path.moveTo(m_size.width, pen.y + m_fontMetrics.getMaxDescent());
                path.lineTo(0, pen.y + m_fontMetrics.getMaxDescent());

                g2.setColor(m_highlightColor);
                g2.draw(path);
            }

            // Draw the level number

            if (highlight)
                g2.setColor(m_highlightColor);
            else
                g2.setColor(m_indexColor);

            width = m_fontMetrics.stringWidth("88");
            
            g2.drawString(String.valueOf(line), pen.x, pen.y);
            
            pen.x += width + m_fontMetrics.stringWidth("W");

            // Draw the "diamond"

            if (m_currentLevel >= line)
            {
                GeneralPath  path = new GeneralPath();

                float  halfHeight = lineHeight * 0.5f;

                if (highlight)
                    g2.setColor(m_highlightColor);
                else
                    g2.setColor(m_indicatorColor);
                
                path.moveTo(pen.x, pen.y - halfHeight);
                path.lineTo(pen.x + halfHeight, pen.y - lineHeight);
                path.lineTo(pen.x + lineHeight, pen.y - halfHeight);
                path.lineTo(pen.x + halfHeight, pen.y);
                path.closePath();
                
                g2.fill(path);
            }
            
            pen.x += lineHeight + m_fontMetrics.stringWidth("W");
            
            // Draw the level description
            
            if (highlight)
                g2.setColor(m_highlightColor);
            else
                g2.setColor(m_textColor);

            title = m_titles[line - 1];
            
            width = m_fontMetrics.stringWidth(title);
            
            g2.drawString(title, pen.x, pen.y);
            
            pen.y += spacing;
        }
        
        // Restore the original graphics transform
        g2.setTransform(origTransform);
    }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        
        setLayout(new java.awt.BorderLayout());
        
        setForeground(java.awt.Color.white);
        setBackground(java.awt.Color.black);
    }//GEN-END:initComponents


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

        String[]  titles =
        {
            "$ 100",
            "$ 200",
            "$ 300",
            "$ 500",
            "$ 1,000,000",
            "$ This is a really long line"
        };
        
        ScorePanel  scorePanel = new ScorePanel(titles);
        
        jframe.getContentPane().add(scorePanel);
        jframe.setVisible(true);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

    private String[]  m_titles;   // Ordered list of titles to display

    private BitSet  m_highlightTitles;   // Mask of bits to highlight

    private float  m_lineHeight;
    
    private Font  m_font;           // Font for text

    private FontMetrics  m_fontMetrics;  // Font metrics
    
    private Dimension  m_size;      // Current dimension of JPanel
    
    private int  m_currentLevel;    // Current question level
    
    private int  m_highlightBorderIndex = HIGHLIGHT_OFF;  // Line to highlight w/bar

    private Color  m_parentBG;      // Parent component's background color

    private Color  m_indexColor;      // Index number's color
    
    private Color  m_indicatorColor;  // Color for level indicator
    
    private Color  m_textColor;       // Color for description text

    private Color  m_background;    // Background color (for highlighting)
    
    private Color  m_highlightColor;    // Highlight text color
}
