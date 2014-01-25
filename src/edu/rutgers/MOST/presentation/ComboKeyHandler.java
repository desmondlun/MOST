package edu.rutgers.MOST.presentation;

import java.awt.EventQueue;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collections;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTextField;

//http://java-swing-tips.blogspot.com/2009/01/create-auto-suggest-jcombobox.html
class ComboKeyHandler extends KeyAdapter{
    private final JComboBox<String> comboBox;
    private final Vector<String> list = new Vector<String>();
    public ComboKeyHandler(JComboBox<String> combo) {
        this.comboBox = combo;
        for(int i=0;i<comboBox.getModel().getSize();i++) {
            list.addElement((String)comboBox.getItemAt(i));
        }
    }
    private boolean shouldHide = false;
    @Override public void keyTyped(final KeyEvent e) {
        EventQueue.invokeLater(new Runnable() {
            @Override public void run() {
                String text = ((JTextField)e.getSource()).getText();
                if(text.length()==0) {
                    setSuggestionModel(comboBox, new DefaultComboBoxModel<String>(list), "");
                    comboBox.hidePopup();
                }else{
                    ComboBoxModel<String> m = getSuggestedModel(list, text);
                    if(m.getSize()==0 || shouldHide) {
                        comboBox.hidePopup();
                    }else{
                        setSuggestionModel(comboBox, m, text);
                        comboBox.showPopup();
                    }
                }
            }
        });
    }
    @Override public void keyPressed(KeyEvent e) {
        JTextField textField = (JTextField)e.getSource();
        String text = textField.getText();
        shouldHide = false;
        switch(e.getKeyCode()) {
          case KeyEvent.VK_RIGHT:
            for(String s: list) {
                if(s.startsWith(text)) {
                    textField.setText(s);
                    return;
                }
            }
            break;
          case KeyEvent.VK_ENTER:
            if(!list.contains(text)) {
                list.addElement(text);
                Collections.sort(list);
                //setSuggestionModel(comboBox, new DefaultComboBoxModel(list), text);
                setSuggestionModel(comboBox, getSuggestedModel(list, text), text);
            }
            shouldHide = true;
            break;
          case KeyEvent.VK_ESCAPE:
            shouldHide = true;
            break;
        }
    }
    private static void setSuggestionModel(JComboBox<String> comboBox, ComboBoxModel<String> mdl, String str) {
        comboBox.setModel(mdl);
        comboBox.setSelectedIndex(-1);
        ((JTextField)comboBox.getEditor().getEditorComponent()).setText(str);
    }
    private static ComboBoxModel<String> getSuggestedModel(Vector<String> list, String text) {
        DefaultComboBoxModel<String> m = new DefaultComboBoxModel<String>();
        for(String s: list) {
            if(s.startsWith(text)) m.addElement(s);
        }
        return m;
    }
}


