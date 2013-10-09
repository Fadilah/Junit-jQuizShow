/*
 * QuizPanel.java
 *
 * Created on February 3, 2004, 9:15 PM
 *
 * $Id: QuizPanel.java,v 1.2 2007/02/05 04:24:19 sdchen Exp $
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
 *    $Log: QuizPanel.java,v $
 *    Revision 1.2  2007/02/05 04:24:19  sdchen
 *    Replaced deprecated isFocusTraversable() with setFocusable(boolean s).
 *
 *    Revision 1.1  2004/04/02 06:02:00  sdchen
 *    Snapshot of jQuizShow 1.1 alpha06 development
 *
 *
 */

package jQuizShow.quiz;

import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.io.*;
import java.util.*;

import jQuizShow.*;
import jQuizShow.event.*;

/**
 * Main quiz panel (based on JPanel).  Multiple, independent instances of
 * this can be created, providing cloned views of the quiz.
 *
 * @author  Steve
 */
public class QuizPanel
        extends
            javax.swing.JPanel
        implements
            jQuizShow.event.GameStateChangeListener,
            jQuizShow.event.GameUpdateListener
    {

    private static final int QUESTION_NUM_ID = 100;
    private static final int QUESTION_ID = 101;
    private static final int TIMER_ID = 200;
    private static final int COMMENT_ID = 201;
    private static final int TRANSITION_ID = 300;
    
    private static GameConfig m_gameConfig;
    
    private static Color  m_bkgColor;
    
    private static BufferedImage  m_bkgImage;
    
    /** Creates new form QuizPanel */
    public QuizPanel()
    {
        // Remove any layout manager.  This uses calculated absolute coordinates.
        setLayout(null);
        
        // Get the game configuration instance
        m_gameConfig = GameConfig.getInstance();

        m_quizState = QuizState.getInstance(true);
        m_quizState.addGameStateChangeListener(this);
        m_quizState.addGameUpdateListener(this);
        
        m_answerLabelTextVector = new Vector();
        m_answerTextVector = new Vector();
        
        /* Listen for resize events */
        addComponentListener(new java.awt.event.ComponentAdapter()
        {
            public void componentResized(java.awt.event.ComponentEvent evt)
            {
                Component  component = evt.getComponent();
                Dimension  size = component.getSize();
                
                float    xRatio = 1.0f;
                float    yRatio = 1.0f;
                
                if (m_bkgImage != null && size.width > 0 && size.height > 0)
                {
                    // Scale image to fit the entire window
                    m_image = m_bkgImage.getScaledInstance(size.width,
                            size.height, Image.SCALE_DEFAULT);

                    // Adjust the positions and sizes of the text boxes
                    if (m_quizSkin != null
                            && m_imageSize != null
                            && m_imageSize.getWidth() > 0
                            && m_imageSize.getHeight() > 0)
                    {
                        xRatio = (float) (size.getWidth() / m_imageSize.getWidth());
                        yRatio = (float) (size.getHeight() / m_imageSize.getHeight());
                    }
                }
                
                Rectangle    origBounds;
                Rectangle    newBounds;

                origBounds = m_quizSkin.getQuestionLabelBox().getBounds();
                newBounds = rescale(xRatio, yRatio, origBounds);
                m_questionLabelText.setBounds(newBounds);

                origBounds = m_quizSkin.getQuestionBox().getBounds();
                newBounds = rescale(xRatio, yRatio, origBounds);
                m_questionText.setBounds(newBounds);

                origBounds = m_quizSkin.getTimerBox().getBounds();
                newBounds = rescale(xRatio, yRatio, origBounds);
                m_timerText.setBounds(newBounds);

                origBounds = m_quizSkin.getCommentBox().getBounds();
                newBounds = rescale(xRatio, yRatio, origBounds);
                m_commentText.setBounds(newBounds);

                origBounds = m_quizSkin.getTransitionBox().getBounds();
                newBounds = rescale(xRatio, yRatio, origBounds);
                m_transitionText.setBounds(newBounds);

                for (int  i = 0; i < m_numAnswers; i++)
                {
                    origBounds = m_quizSkin.getAnswerLabelBox(i).getBounds();
                    newBounds = rescale(xRatio, yRatio, origBounds);
                    ((ResizableText) m_answerLabelTextVector.get(i)).setBounds(newBounds);

                    origBounds = m_quizSkin.getAnswerBox(i).getBounds();
                    newBounds = rescale(xRatio, yRatio, origBounds);
                    ((ResizableText) m_answerTextVector.get(i)).setBounds(newBounds);
                }
                
                invalidate();
            }
        });

        // Add listener for keyboard events
        addKeyListener(new java.awt.event.KeyAdapter()
        {
            public void keyPressed(java.awt.event.KeyEvent evt)
            {
                int  keyCode = evt.getKeyCode();
                char  keyChar = evt.getKeyChar();

                if (m_gameInputEvent == null)
                    m_gameInputEvent = new GameInputEvent(this);

                m_gameInputEvent.setType(GameInputEnum.KEY_TYPED);
                m_gameInputEvent.setKeyCode(keyCode, keyChar);
                
                m_quizState.fireGameInputEvent(m_gameInputEvent);
                return;
            }
        } );
        
        setFocusable(true);
    }
   
    
    private Rectangle  rescale(float  xRatio, float  yRatio, Rectangle  origBounds)
    {
        Rectangle  newBounds = new Rectangle();
        newBounds.x = Math.round(origBounds.x * xRatio);
        newBounds.y = Math.round(origBounds.y * yRatio);
        newBounds.width = Math.round(origBounds.width * xRatio);
        newBounds.height = Math.round(origBounds.height * yRatio);
        
        return newBounds;
    }
    
    public void  setSkin(QuizSkin  skin)
    {
        int    i;
        
        if (skin == null || skin == m_quizSkin)
            return;
        
        m_quizSkin = skin;
        
        m_bkgImage = skin.getBkgImage();
        m_bkgColor = skin.getBkgColor();
        
        if (m_bkgColor != null)
            setBackground(m_bkgColor);

        if (m_bkgImage != null)
        {
            m_imageSize = new Dimension(m_bkgImage.getWidth(),
                    m_bkgImage.getHeight());
        }
        
        removeAll();        // Removes all old components
        
        // Create the ResizableText instances for the skin
        TextBoxConfig    bounds;
        
        bounds = skin.getQuestionLabelBox();
        m_questionLabelText = new ResizableText(QUESTION_NUM_ID,
                bounds.fontName, bounds.fontStyle);
        m_questionLabelText.setDefaultTextColor(bounds.fontColor,
                bounds.bgColor);
        m_questionLabelText.setFadeInDelay(bounds.fadeInDelay);
        m_questionLabelText.setRowColumnHint(bounds.rows, bounds.columns);
        add(m_questionLabelText);
        
        bounds = skin.getQuestionBox();
        m_questionText = new ResizableText(QUESTION_ID,
                bounds.fontName, bounds.fontStyle);
        m_questionText.setDefaultTextColor(bounds.fontColor,
                bounds.bgColor);
        m_questionText.setFadeInDelay(bounds.fadeInDelay);
        m_questionText.setRowColumnHint(bounds.rows, bounds.columns);
        add(m_questionText);
        
        bounds = skin.getTimerBox();
        m_timerText = new ResizableText(TIMER_ID,
                bounds.fontName, bounds.fontStyle);
        m_timerText.setDefaultTextColor(bounds.fontColor,
                bounds.bgColor);
        m_timerText.setFadeInDelay(bounds.fadeInDelay);
        m_timerText.setRowColumnHint(bounds.rows, bounds.columns);
        add(m_timerText);
        
        bounds = skin.getCommentBox();
        m_commentText = new ResizableText(COMMENT_ID,
                bounds.fontName, bounds.fontStyle);
        m_commentText.setDefaultTextColor(bounds.fontColor,
                bounds.bgColor);
        m_commentText.setFadeInDelay(bounds.fadeInDelay);
        m_commentText.setRowColumnHint(bounds.rows, bounds.columns);
        add(m_commentText);
        
        bounds = skin.getTransitionBox();
        m_transitionText = new ResizableText(TRANSITION_ID,
                bounds.fontName, bounds.fontStyle);
        m_transitionText.setDefaultTextColor(bounds.fontColor,
                bounds.bgColor);
        m_transitionText.setFadeInDelay(bounds.fadeInDelay);
        m_transitionText.setRowColumnHint(bounds.rows, bounds.columns);
        m_transitionText.centerText(true);
        add(m_transitionText);
        
        m_numAnswers = m_quizSkin.getNumAnswers();

        m_answerTextGroup = new ResizableTextGroup();
        
        m_answerTextVector = new Vector();
        
        for (i = 0; i < m_numAnswers; i++)
        {
            ResizableText  rtext;
            
            bounds = skin.getAnswerLabelBox(i);
            rtext = new ResizableText(i + 10,
                    bounds.fontName, bounds.fontStyle);
            rtext.setDefaultTextColor(bounds.fontColor, bounds.bgColor);
            rtext.setFadeInDelay(bounds.fadeInDelay);
            rtext.setRowColumnHint(bounds.rows, bounds.columns);
            m_answerLabelTextVector.add(rtext);
            add(rtext);
            
            // Add the QuizState instance as a listener for answer clicked events.
            rtext.addGameInputListener(m_quizState);

            m_answerTextGroup.addResizableText(rtext);
            
            bounds = skin.getAnswerBox(i);
            rtext = new ResizableText(i,
                    bounds.fontName, bounds.fontStyle);
            rtext.setDefaultTextColor(bounds.fontColor, bounds.bgColor);
            rtext.setFadeInDelay(bounds.fadeInDelay);
            rtext.setRowColumnHint(bounds.rows, bounds.columns);
            m_answerTextVector.add(rtext);
            add(rtext);
            
            // Add the QuizState instance as a listener for answer clicked events.
            rtext.addGameInputListener(m_quizState);
            
            m_answerTextGroup.addResizableText(rtext);
        }
    }
    

    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();   // Create a copy of "g"

        Rectangle  bounds = getBounds();

        super.paintComponent(g);

        if (m_bkgImage != null)
        {
            g2d.drawImage(m_image, 0, 0, m_image.getWidth(null),
                    m_image.getHeight(null), null);
        }

        g2d.dispose();      // Clean up copy of "g"
    }

    
    protected void paintChildren(Graphics g)
    {
        m_answerTextGroup.autoSize();
        
        super.paintChildren(g);
    }

    
    public void gameStateChanged(jQuizShow.event.GameStateChangeEvent evt)
    {
        QuizStateEnum    type = (QuizStateEnum) evt.getType();
        
        if (type == QuizStateEnum.IDLE)
        {
            highlightSelection(null);
        }
        else if (type == QuizStateEnum.SET_CURRENT_LEVEL)
        {
        }
        else if (type == QuizStateEnum.SET_QUESTION_TIMER_LIMIT)
        {
        }
        else if (type == QuizStateEnum.NEW_GAME)
        {
            clearScreen();
            highlightSelection(null);
        }
        else if (type == QuizStateEnum.WAIT_TO_START_ROUND)
        {
            clearScreen();

            highlightSelection(null);
        }
        else if (type == QuizStateEnum.DISPLAY_QUESTION)
        {            
            Object  args[] = new Object[1];

            m_currentLevel = m_quizState.getCurrentLevel();

            String  questionID = m_quizState.getQuestionID();
            
            if (questionID == null || questionID.length() == 0)
                args[0] = new Integer(m_currentLevel + 1);
            else
                args[0] = questionID;

            m_questionLabelText.setText(Main.getMessage("quiz_question_label", args));

            String  question = m_quizState.getQuestion();
            
            m_questionText.setText(question);
        }
        else if (type == QuizStateEnum.DISPLAY_ANSWER)
        {
            for (int  i = 0; i < m_numAnswers; i++)
            {
                ResizableText  rText;

                rText = (ResizableText) m_answerLabelTextVector.get(i);
                rText.setText(String.valueOf((char) ('A' + i)) + " :");
                
                String  answer = m_quizState.getAnswer(i);
                
                rText = (ResizableText) m_answerTextVector.get(i);
                rText.setText(answer);
            }
        }
        else if (type == QuizStateEnum.WAIT_FOR_ANSWER)
        {
        }
        else if (type == QuizStateEnum.WAIT_TO_REVEAL_ANSWER)
        {
        }
        else if (type == QuizStateEnum.ANSWER_WAS_CORRECT ||
                type == QuizStateEnum.ANSWER_WAS_WRONG)
        {
            ResizableText  answerBox;

            int  index = m_quizState.getCorrectAnswer();

            if (index < 0 || index >= m_numAnswers)
                return;

            answerBox = (ResizableText) m_answerTextVector.get(index);

            answerBox.setTextColor(Color.black, Color.green);
        }
        else if (type == QuizStateEnum.SET_SHOW_ANSWERS_MODE)
        {
            if (m_quizState.getToggleState(QuizState.ANSWER_MODE) == true)
            {
                enableTextFadeIn(false);
            }
            else
            {
                enableTextFadeIn(true);
            }
        }
        else if (type == QuizStateEnum.END_OF_GAME)
        {
            highlightSelection(null);
            clearScreen();
        }
    }

    
    private void  clearScreen()
    {
        m_questionLabelText.setText(null);
        m_questionText.setText(null);
        m_timerText.setText(null);
        m_commentText.setText(null);

        for (int  i = 0; i < m_numAnswers; i++)
        {
            ((ResizableText) m_answerLabelTextVector.get(i)).setText(null);
            ((ResizableText) m_answerTextVector.get(i)).setText(null);
        }
    }

    
    private void  showResizableText(boolean  flag)
    {
        m_questionLabelText.setVisible(flag);
        m_questionText.setVisible(flag);
        m_timerText.setVisible(flag);
        m_commentText.setVisible(flag);

        for (int  i = 0; i < m_numAnswers; i++)
        {
            ((ResizableText) m_answerLabelTextVector.get(i)).setVisible(flag);
            ((ResizableText) m_answerTextVector.get(i)).setVisible(flag);
        }
    }

    
    private void  enableTextFadeIn(boolean  flag)
    {
        m_questionLabelText.enableFadeInDelay(flag);
        m_questionText.enableFadeInDelay(flag);
        m_timerText.enableFadeInDelay(flag);
        m_commentText.enableFadeInDelay(flag);

        for (int  i = 0; i < m_numAnswers; i++)
        {
            ((ResizableText) m_answerLabelTextVector.get(i)).enableFadeInDelay(flag);
            ((ResizableText) m_answerTextVector.get(i)).enableFadeInDelay(flag);
        }
    }
    
    
    public void gameUpdated(jQuizShow.event.GameUpdateEvent evt)
    {
        QuizUpdateEnum  type = (QuizUpdateEnum) evt.getType();
        
        if (type == QuizUpdateEnum.NOP)
        {
        }
        else if (type == QuizUpdateEnum.TRANSITION_MSG)
        {
            String  msg = m_quizState.getTransitionMessage();
            
            if (msg == null)
            {
                showResizableText(true);
                m_transitionText.setVisible(false);
            }
            else
            {
                showResizableText(false);
                m_transitionText.setVisible(true);
                m_transitionText.setText(msg);
            }
        }
        else if (type == QuizUpdateEnum.SELECTED_ANSWER)
        {
            ResizableText  answerBox;
            
            int  index = m_quizState.getSelectedAnswer();
            
            if (index == QuizState.NO_SELECTED_ANSWER ||
                    index < 0 || index >= m_numAnswers)
            {
                answerBox = null;
            }
            else
            {
                answerBox = (ResizableText) m_answerTextVector.get(index);
            }
            
            highlightSelection(answerBox);           
        }
        else if (type == QuizUpdateEnum.UPDATE_QUESTION_CLOCK)
        {
            java.util.Calendar  cal = java.util.Calendar.getInstance();

            int  timeLimit = m_quizState.getQuestionTimerLimit();
            int  elapsedTime = m_quizState.getQuestionTimerTime();
            
            cal.set(0, 0, 0, 0, 0);
            cal.set(java.util.Calendar.SECOND,
                    timeLimit - elapsedTime);

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

            m_timerText.setText(timeStr);
        }
        else if (type == QuizUpdateEnum.TOGGLE_STATE)
        {
        }
        else if (type == QuizUpdateEnum.STATUS_MSG)
        {
            m_commentText.setText(m_quizState.getStatusMessage());
        }
    }


    /** Highlight the specified "box".  If box == null, unhighlight all.
     */
    private void highlightSelection(ResizableText  box)
    {
        for (int  i = 0; i < m_numAnswers; i++)
        {
            ResizableText  answerBox = (ResizableText) m_answerTextVector.get(i);
            
            if (answerBox == box)
            {
                // Highlight the box
                answerBox.setTextColor(Color.black, Color.orange);
            }
            else
            {
                // Reset to normal colors (unhighlight)
                answerBox.restoreDefaultColors();
            }
        }
    }

    
    // Variables declaration
    
    private QuizSkin  m_quizSkin;

    private Image  m_image;

    private QuizState  m_quizState;

    private static GameInputEvent  m_gameInputEvent;
    
    private Dimension  m_imageSize;
    
    private boolean    m_fillWindow = true;

    private int  m_numAnswers = 0;

    private int  m_currentLevel;
    
    private ResizableTextGroup  m_answerTextGroup;
    
    private ResizableText    m_questionLabelText;
    private ResizableText    m_questionText;
    private ResizableText    m_timerText;
    private ResizableText    m_commentText;
    private ResizableText    m_transitionText;

    private Vector  m_answerLabelTextVector;      // Vector of ResizableText for answer labels
    private Vector  m_answerTextVector;      // Vector of ResizableText for answers
    
    // End of variables declaration
}
