/*
 * QuizQuestionNumberDialog.java
 *
 * Created on March 13, 2004, 10:14 PM
 */

package jQuizShow.quiz;

import jQuizShow.*;
import jQuizShow.event.*;

/**
 *
 * @author  Steve
 */
public class QuizQuestionNumberDialog
        extends javax.swing.JInternalFrame
        implements
            GameStateChangeListener
{   
    /** Creates new form QuizQuestionNumberDialog */
    public QuizQuestionNumberDialog() {
        super(Main.getMessage("title_quiz_question_num_dialog"), false, true, false, false);

        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        
        m_quizState = QuizState.getInstance(false);
        
        initComponents();

        int     number = m_quizState.getCurrentLevel();

        numberText.setText(String.valueOf(number + 1));
        
        m_quizState.addGameStateChangeListener(this);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        centerPanel = new javax.swing.JPanel();
        questionLabel = new javax.swing.JLabel();
        numberText = new javax.swing.JTextField();
        southPanel = new javax.swing.JPanel();
        applyButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();

        setMinimumSize(new java.awt.Dimension(250, 80));
        setPreferredSize(new java.awt.Dimension(250, 80));
        centerPanel.setLayout(new java.awt.GridBagLayout());

        centerPanel.setBorder(new javax.swing.border.EtchedBorder());
        centerPanel.setMinimumSize(new java.awt.Dimension(200, 60));
        centerPanel.setPreferredSize(new java.awt.Dimension(200, 60));
        questionLabel.setText(Main.getMessage("label_quiz_question_num"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1000.0;
        gridBagConstraints.weighty = 1000.0;
        centerPanel.add(questionLabel, gridBagConstraints);

        numberText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                numberTextActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1000.0;
        gridBagConstraints.weighty = 1000.0;
        centerPanel.add(numberText, gridBagConstraints);

        getContentPane().add(centerPanel, java.awt.BorderLayout.CENTER);

        applyButton.setText(Main.getMessage("label_apply"));
        applyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applyButtonActionPerformed(evt);
            }
        });

        southPanel.add(applyButton);

        closeButton.setText(Main.getMessage("label_close"));
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        southPanel.add(closeButton);

        getContentPane().add(southPanel, java.awt.BorderLayout.SOUTH);

        pack();
    }//GEN-END:initComponents

    private void numberTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_numberTextActionPerformed
        setLevel();
    }//GEN-LAST:event_numberTextActionPerformed

    private void applyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applyButtonActionPerformed
        setLevel();
    }//GEN-LAST:event_applyButtonActionPerformed

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        setVisible(false);
    }//GEN-LAST:event_closeButtonActionPerformed

    
    private void  setLevel()
    {
        int  max = m_quizState.getMaxNumberOfLevels();
        int  num = 0;
        
        try
        {
            QuizController  quizCtrl = QuizController.getInstance();
            
            quizCtrl.setQuestion(numberText.getText());
        }
        catch (IllegalArgumentException  ia_e)
        {
            javax.swing.JOptionPane.showMessageDialog(this, ia_e.getMessage());
        }
    }
    
    
    public void gameStateChanged(GameStateChangeEvent evt) {
        GameStateChangeEnum  type = evt.getType();
        
        if (type == QuizStateEnum.NEW_GAME ||
                type == QuizStateEnum.WAIT_TO_START_ROUND ||
                type == QuizStateEnum.ANSWER_WAS_WRONG ||
                type == QuizStateEnum.SET_CURRENT_LEVEL)
        {
            int     number = m_quizState.getCurrentLevel();
            
            numberText.setText(String.valueOf(number + 1));
        }
    }    
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton applyButton;
    private javax.swing.JPanel centerPanel;
    private javax.swing.JButton closeButton;
    private javax.swing.JTextField numberText;
    private javax.swing.JLabel questionLabel;
    private javax.swing.JPanel southPanel;
    // End of variables declaration//GEN-END:variables

    private QuizState m_quizState;
    
}
