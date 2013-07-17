package edu.rutgers.MOST.presentation;

import java.awt.*;
import java.awt.event.*;
import java.awt.font.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
 
//from http://www.coderanch.com/t/343889/GUI/java/Drop-Menu-Buttons
public class OptionComponent extends JPanel {
    String text;
    String imagePath;
    GeneralPath arrow;
    int left;
    boolean firstTime = true;
    boolean isSelected = false;
    public boolean buttonClicked = false;
    private JScrollPopupMenu popupMenu;
    private String lastMenuItem;
    //private JPopupMenu popupMenu;
 
    public JScrollPopupMenu getPopupMenu() {
    //public JPopupMenu getPopupMenu() {
		return popupMenu;
	}

    public void setPopupMenu(JScrollPopupMenu popupMenu) {
	//public void setPopupMenu(JPopupMenu popupMenu) {
		this.popupMenu = popupMenu;
	}

	public String getLastMenuItem() {
		return lastMenuItem;
	}

	public void setLastMenuItem(String lastMenuItem) {
		this.lastMenuItem = lastMenuItem;
	}

	OptionComponent(String text, String imagePath) {
    	JScrollPopupMenu popupMenu = new JScrollPopupMenu();
    	//JPopupMenu popupMenu = new JPopupMenu();
    	setPopupMenu(popupMenu);
        this.text = text;
        if (this.text.equals("image")) {
        	this.text = "     ";
        }
        setBackground(UIManager.getColor("Menu.background"));
        setForeground(UIManager.getColor("Menu.foreground"));
        setOpaque(false);
        addMouseListener(ml);
    }
 
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        //int h = 16;
        int h = getHeight();
        Font font = UIManager.getFont("Menu.font");
        g2.setFont(font);
        FontRenderContext frc = g2.getFontRenderContext();
        Rectangle2D r = font.getStringBounds(text, frc);
        float sx = 5f;
        float sy = (float)((h + r.getHeight())/2) -
                       font.getLineMetrics(text, frc).getDescent();
        g2.drawString(text, sx, sy);
        double x = sx + r.getWidth() + sx;
        if(isSelected) {
            g2.setPaint(Color.gray);
            g2.draw(new Line2D.Double(x, 0, x, h));
            g2.setPaint(Color.white);
            g2.draw(new Line2D.Double(x+1, 0, x+1, h));
            g2.setPaint(Color.gray);
            g2.draw(new Rectangle2D.Double(0, 0, getSize().width-1, h-1));
        }
        float ax = (float)(x + sx);
        if(firstTime)
            createArrow(ax, h);        
        if (isEnabled()) {
        	g2.setPaint(UIManager.getColor("Menu.foreground"));
        	g2.fill(arrow);
        } else {
        	g2.setPaint(Color.LIGHT_GRAY);
        	g2.fill(arrow);
        }        
        ax += 10f + sx;
        if(firstTime) {
            setSize((int)ax, h);         // initial sizing
            setPreferredSize(getSize());
            setMaximumSize(getSize());   // resizing behavior
            left = (int)x + 1;           // for mouse listener
            firstTime = false;
        }
    }
 
    private void createArrow(float x, int h) {
        arrow = new GeneralPath();
        arrow.moveTo(x, h/3f);
        arrow.lineTo(x + 10f, h/3f);
        arrow.lineTo(x + 5f, h*2/3f);
        arrow.closePath();
    }
 
    private JMenuBar getMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(this);
        return menuBar;
    }
 
    public static void main(String[] args) {
        //OptionComponent test = new OptionComponent("draw", "");
        OptionComponent test = new OptionComponent("image", "");
        //from http://www.java-forums.org/new-java/4477-how-add-images-jpanels.html
        String path = "etc/toolbarIcons/Sideways_Arrow_Icon16r.png";
        JLabel label = new JLabel(new ImageIcon(path));
        label.setBorder(BorderFactory.createEmptyBorder(0,0,0,20));
		label.setAlignmentX(LEFT_ALIGNMENT);
        test.add(label);  
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setJMenuBar(test.getMenuBar());
        f.getContentPane();
        f.setSize(200,100);
        f.setLocation(200,200);
        f.setVisible(true);
    }
 
    private MouseListener ml = new MouseAdapter() {
        //JPopupMenu popupMenu = getPopupMenu();
 
        public void mousePressed(MouseEvent e) {
        	if (isEnabled()) {
        		if(e.getX() <= left) {
        			buttonClicked = true;
        			//System.out.println("button clicked");
        		} else {
        			buttonClicked = false;
        			getPopupMenu().setBackground(new Color(190, 190, 190));
        			setLastMenuItem("Cancel");
        			getPopupMenu().setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(0, 0, GraphicalInterfaceConstants.UNDO_BORDER_HEIGHT, 0), getLastMenuItem(), TitledBorder.LEFT, TitledBorder.BOTTOM));
        			getPopupMenu().show(OptionComponent.this, 0, getHeight());
        		}                    
        	}            
        }
 
        public void mouseEntered(MouseEvent e) {
            isSelected = true;
            repaint();
        }
 
        public void mouseExited(MouseEvent e) {
            isSelected = false;
            repaint();
        }
    };
 
    /*
    public JPopupMenu getPopupMenu() {
        JPopupMenu popupMenu = new JPopupMenu();
        String[] ids = { "test" };
        for(int j = 0; j < ids.length; j++) {
            JMenuItem item = new JMenuItem(ids[j]);
            popupMenu.add(item);
        }
        popupMenu.addSeparator();
        JMenuItem cancelItem = new JMenuItem("Cancel");
        popupMenu.add(cancelItem);
        return popupMenu;
    }
    */
    
}

