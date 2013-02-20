package edu.rutgers.MOST.presentation;

import java.awt.Dimension;
import javax.swing.JComboBox;

//from http://www.javakb.com/Uwe/Forum.aspx/java-gui/2584/JComboBox-with-wider-drop-down-popup
//avoids using tooltips since dropbox resizes for longest component
//works in FindReplace but for some reason, does not work in ReactionEditor
 public class SizedComboBox extends JComboBox {
   private boolean realSize;

   // Add whatever constructors you need.

    // This assumes getSize() will only be called by doLayout()
    // or ComboPopup.show().
    public Dimension getSize() {
      Dimension size = super.getSize();
      if(realSize)
         return size;
      Dimension pref_size = getPreferredSize();
      if(pref_size.width < size.width)
         pref_size.width = size.width;
      return pref_size;
   }

    public void doLayout() {
      realSize = true;
      super.doLayout();
      realSize = false;
   }
}
