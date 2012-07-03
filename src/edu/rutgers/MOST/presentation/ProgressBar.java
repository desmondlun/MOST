package edu.rutgers.MOST.presentation;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

//based on code from http://www.java2s.com/Code/Java/Swing-JFC/CreateaProgressBar.htm
public class ProgressBar extends JFrame {
	
	private Double numberOfReactions;
	private Double currentReactionNumber;
	
	public void setNumberOfReactions(Double numberOfReactions) {
		this.numberOfReactions = numberOfReactions;
	}

	public Double getNumberOfReactions() {
		return numberOfReactions;
	}
	
	public void setCurrentReactionNumber(Double currentReactionNumber) {
		this.currentReactionNumber = currentReactionNumber;
	}

	public Double getCurrentReactionNumber() {
		return currentReactionNumber;
	}	
	
	
    JProgressBar current = new JProgressBar(0, 2000);
    int num = 0;
    public ProgressBar() {
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      JPanel pane = new JPanel();
      current.setValue(0);
      current.setStringPainted(true);
      current.setIndeterminate(true);
      pane.add(current);
      setContentPane(pane);
    }
    
    public void iterate() {
    	while (num < 2000) {
        //while (num < this.getNumberOfReactions()) {
            current.setValue(num);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
        }
        //num = Integer.valueOf(this.getCurrentReactionNumber().toString());    
        num += 95;
      }
    }

    public static void main(String[] arguments) {
	  ProgressBar frame = new ProgressBar();
      frame.pack();
      frame.setVisible(true);
      frame.iterate();
      frame.setVisible(false);
    }
}

