/*
 * QuizShowSkin.java
 *
 * Created on February 4, 2004, 7:30 PM
 *
 * $Id: QuizShowSkin.java,v 1.1 2004/04/02 06:01:59 sdchen Exp $
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
 *    $Log: QuizShowSkin.java,v $
 *    Revision 1.1  2004/04/02 06:01:59  sdchen
 *    Snapshot of jQuizShow 1.1 alpha06 development
 *
 *
 */

package jQuizShow;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;

/**
 *
 * @author  Steve
 */
public abstract class QuizShowSkin {
    
    private static final int DEFAULT_BKG_COLOR = 0xFFFFFF;
    
    /** Creates a new instance of QuizShowSkin */
    public QuizShowSkin() {
        m_answerBoxes = new Vector();
        m_answerLabelBoxes = new Vector();
    }
    
    abstract public void load(String name, String filename) throws IOException;

    public String  getName() {
        return m_name;
    }
    
    protected void  setName(String  name) {
        m_name = name;
    }
    
    public String  getFilename() {
        return m_filename;
    }

    protected void  setFilename(String  filename) {
        m_filename = filename;
    }
    
    public Color getBkgColor() {
        if (m_bkgColor == null)
            return new Color(DEFAULT_BKG_COLOR);
        else
            return m_bkgColor;
    }
 
    protected void  setBkgColor(Color  color)
    {
        m_bkgColor = color;
    }

    public BufferedImage getBkgImage() {
        return m_bkgImage;
    }
 
    protected void  setBkgImage(BufferedImage  image)
    {
        m_bkgImage = image;
    }    
    
    public TextBoxConfig getQuestionLabelBox() {
        return m_questionLabelBox;
    }

    protected void  setQuestionLabelBox(TextBoxConfig  box) {
        m_questionLabelBox = box;
    }
    
    public TextBoxConfig getQuestionBox() {
        return m_questionBox;
    }

    protected void  setQuestionBox(TextBoxConfig  box) {
        m_questionBox = box;
    }
    
    public TextBoxConfig getTimerBox() {
        return m_timerBox;
    }

    protected void  setTimerBox(TextBoxConfig  box) {
        m_timerBox = box;
    }
    
    public TextBoxConfig getCommentBox() {
        return m_commentBox;
    }

    protected void  setCommentBox(TextBoxConfig  box) {
        m_commentBox = box;
    }
    
    public TextBoxConfig getTransitionBox() {
        return m_transtionBox;
    }

    protected void  setTransitionBox(TextBoxConfig  box) {
        m_transtionBox = box;
    }

    public int getNumAnswers() {
        return m_numAnswers;
    }

    protected void  setNumAnswers(int  numAnswers)
    {
        m_numAnswers = numAnswers;
    }
    
    public TextBoxConfig getAnswerLabelBox(int answerNum) {
        if (answerNum >= 0 && answerNum < m_answerBoxes.size())
            return (TextBoxConfig) m_answerLabelBoxes.get(answerNum);
        else
            return null;
    } 
    
    protected void  addAnswerLabelBox(TextBoxConfig  bounds) {
        m_answerLabelBoxes.add(bounds);
        return;
    }
    
    public TextBoxConfig getAnswerBox(int answerNum) {
        if (answerNum >= 0 && answerNum < m_answerBoxes.size())
            return (TextBoxConfig) m_answerBoxes.get(answerNum);
        else
            return null;
    } 
    
    protected void  addAnswerBox(TextBoxConfig  bounds) {
        m_answerBoxes.add(bounds);
        return;
    }

    
    protected String m_name;
    
    protected String m_filename;
    
    private Color m_bkgColor;
    
    private BufferedImage m_bkgImage;

    private TextBoxConfig  m_questionLabelBox;
    private TextBoxConfig  m_questionBox;

    private TextBoxConfig  m_timerBox;
    private TextBoxConfig  m_commentBox;
    private TextBoxConfig  m_transtionBox;
    
    private int  m_numAnswers = 4;
    
    private Vector  m_answerLabelBoxes;
    private Vector  m_answerBoxes;   
}
