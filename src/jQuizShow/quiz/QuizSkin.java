/*
 * QuizSkin.java
 *
 * Created on February 4, 2004, 7:22 PM
 *
 * $Id: QuizSkin.java,v 1.1 2004/04/02 06:02:00 sdchen Exp $
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
 *    $Log: QuizSkin.java,v $
 *    Revision 1.1  2004/04/02 06:02:00  sdchen
 *    Snapshot of jQuizShow 1.1 alpha06 development
 *
 *
 */

package jQuizShow.quiz;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import javax.imageio.*;

import jQuizShow.*;
import jQuizShow.util.*;

/**
 *
 * @author  Steve
 */
public class QuizSkin extends QuizShowSkin {
    
    private static final String PROP_BACKGROUND_IMAGE = "backgroundImage";
    
    private static final String PROP_QUESTION_LABEL_BASE = "questionLabel";
    private static final String PROP_QUESTION_BASE = "question";
    private static final String PROP_TIMER_BASE = "timer";
    private static final String PROP_COMMENT_BASE = "comment";
    private static final String PROP_ANSWER_LABEL_BASE = "answerLabel_";
    private static final String PROP_ANSWER_BASE = "answer_";
    private static final String PROP_TRANSITION_BASE = "transition";

    private static final String X_SUFFIX = ".x";
    private static final String Y_SUFFIX = ".y";
    private static final String WIDTH_SUFFIX = ".width";
    private static final String HEIGHT_SUFFIX = ".height";
    private static final String FADE_IN_DELAY_SUFFIX = ".fadeInDelay";
    private static final String ROWS_SUFFIX = ".rows";
    private static final String COLUMNS_SUFFIX = ".columns";
    
    private static final String FONT_NAME_SUFFIX = ".fontName";
    private static final String FONT_BOLD_SUFFIX = ".fontBold";
    private static final String FONT_ITALIC_SUFFIX = ".fontItalic";   

    private static final String FONT_COLOR_SUFFIX = ".fontColor";
    private static final String BG_COLOR_SUFFIX = ".bgColor";

    private static final String PROP_NUM_ANSWERS = "numAnswers";
    
    /** Creates a new instance of QuizSkin */
    public QuizSkin() {
    }
    
    public void load(String name, String filename) throws IOException
    {
        m_name = name;
        m_filename = filename;
        
        Properties  props = new Properties();
        
        String  property;
        
        props.load(FileUtils.openFile(filename));

        // Load the background image
        String  filepath = props.getProperty(PROP_BACKGROUND_IMAGE);

        if (filepath != null)
        {
            try
            {
                filepath = FileUtils.searchForFile(filepath);

                System.out.println("Loading background image:  " + filepath);
                
                BufferedImage  image = ImageIO.read(FileUtils.openFile(filepath));
                
                setBkgImage(image);
                
                System.out.println("Background image loaded.");
            }
            catch (FileNotFoundException  fnf_e)
            {
                System.out.println("Background image -- FileNotFoundException.");
            }
            catch (IOException  io_e)
            {
                System.out.println("Background image -- IOException.");
            }
        }

        // Set the bounds for the resizable text regions.
        TextBoxConfig  bounds;

        bounds = getBounds(props, PROP_QUESTION_LABEL_BASE);
        setQuestionLabelBox(bounds);
        
        bounds = getBounds(props, PROP_QUESTION_BASE);
        setQuestionBox(bounds);
        
        bounds = getBounds(props, PROP_TIMER_BASE);
        setTimerBox(bounds);
        
        bounds = getBounds(props, PROP_COMMENT_BASE);
        setCommentBox(bounds);
        
        bounds = getBounds(props, PROP_TRANSITION_BASE);
        setTransitionBox(bounds);
        
        property = props.getProperty(PROP_NUM_ANSWERS, "4");
        int  numAnswers = Integer.decode(property).intValue();
        
        this.setNumAnswers(numAnswers);
        
        for (int  i = 0; i < numAnswers; i++)
        {
            bounds = getBounds(props, PROP_ANSWER_LABEL_BASE + (i + 1));
            addAnswerLabelBox(bounds);

            bounds = getBounds(props, PROP_ANSWER_BASE + (i + 1));
            addAnswerBox(bounds);
        }
    }
 
    private TextBoxConfig  getBounds(Properties  props, String  propBase)
    {
        TextBoxConfig  bounds = new TextBoxConfig();

        String  property;
        
        if ((property = props.getProperty(propBase + X_SUFFIX)) != null)
            bounds.x = Integer.decode(property).intValue();
        if ((property = props.getProperty(propBase + Y_SUFFIX)) != null)
            bounds.y = Integer.decode(property).intValue();
        if ((property = props.getProperty(propBase + WIDTH_SUFFIX)) != null)
            bounds.width = Integer.decode(property).intValue();
        if ((property = props.getProperty(propBase + HEIGHT_SUFFIX)) != null)
            bounds.height = Integer.decode(property).intValue();
        if ((property = props.getProperty(propBase + FADE_IN_DELAY_SUFFIX)) != null)
            bounds.fadeInDelay = Integer.decode(property).intValue();
        if ((property = props.getProperty(propBase + ROWS_SUFFIX)) != null)
            bounds.rows = Integer.decode(property).intValue();
        if ((property = props.getProperty(propBase + COLUMNS_SUFFIX)) != null)
            bounds.columns = Integer.decode(property).intValue();

                // Get the font preferences
        bounds.fontName = "SansSerif";
        bounds.fontStyle = Font.BOLD;
        bounds.fontColor = Color.YELLOW;
        bounds.bgColor = null;
        
        if ((property = props.getProperty(propBase + FONT_NAME_SUFFIX)) != null)
            bounds.fontName = property;

        if ((property = props.getProperty(propBase + FONT_BOLD_SUFFIX)) != null)
        {
            if (Integer.decode(property).intValue() != 0)
                bounds.fontStyle = Font.BOLD;
            else
                bounds.fontStyle = Font.PLAIN;
        }        

        if ((property = props.getProperty(propBase + FONT_ITALIC_SUFFIX)) != null)
        {
            if (Integer.decode(property).intValue() != 0)
                bounds.fontStyle |= Font.ITALIC;
        }        

        if ((property = props.getProperty(propBase + FONT_COLOR_SUFFIX)) != null)
            bounds.fontColor = new Color(Integer.decode(property).intValue());

        if ((property = props.getProperty(propBase + BG_COLOR_SUFFIX)) != null)
        {
            int  value = Integer.decode(property).intValue();
            
            if (value < 0)
                bounds.bgColor = null;
            else
                bounds.bgColor = new Color(value);
        }
        
        return bounds;
    }
    
    
    public void  setBkgColor(Color  color)
    {
        super.setBkgColor(color);
    }

    public void  setBkgImage(BufferedImage  image)
    {
        super.setBkgImage(image);
    }    

}
