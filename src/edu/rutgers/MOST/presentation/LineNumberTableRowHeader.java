package edu.rutgers.MOST.presentation;

import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

// from http://www.javarichclient.com/display-line-numbers-jtable/
public class LineNumberTableRowHeader extends JComponent {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JTable table;
    private final JScrollPane scrollPane;

    public LineNumberTableRowHeader(JScrollPane jScrollPane, JTable table) {
        this.scrollPane = jScrollPane;
        this.table = table;        
        this.table.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent tme) {
                LineNumberTableRowHeader.this.repaint();
            }
        });

        this.table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent lse) {
                LineNumberTableRowHeader.this.repaint();
            }
        });

        this.scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent ae) {
                LineNumberTableRowHeader.this.repaint();
            }
        });

        setPreferredSize(new Dimension(50, 100));
  
        // This gives y position when mouse moved over row header. need to figure out how to
        // highlight cell(s) and select appropriate cells in selected table
//        MouseMotionAdapter mml = new MouseMotionAdapter() {
//    		public void mouseMoved( MouseEvent e) {
//    			int y = e.getY();
//    			System.out.println(y);
//    		}
//    	};
//    	
//    	this.addMouseMotionListener(mml);
        
    }

    
    
    protected void paintComponent(Graphics g) {
        Point viewPosition = scrollPane.getViewport().getViewPosition();
        Dimension viewSize = scrollPane.getViewport().getViewSize();
        if (getHeight() < viewSize.height) {
            Dimension size = getPreferredSize();
            size.height = viewSize.height;
            setSize(size);
            setPreferredSize(size);
    	}

        super.paintComponent(g);

        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());

        FontMetrics fm = g.getFontMetrics();

        for(int r=0; r < table.getRowCount(); r++) {
            Rectangle cellRect = table.getCellRect(r, 0, false);

            boolean rowSelected = table.isRowSelected(r);

            if (rowSelected) {
                g.setColor(table.getSelectionBackground());
                g.fillRect(0, cellRect.y, getWidth(), cellRect.height);
            }

            if ((cellRect.y + cellRect.height) - viewPosition.y >= 0 && cellRect.y < viewPosition.y + viewSize.height) {
                g.setColor(table.getGridColor());
                g.drawLine(0, cellRect.y+cellRect.height, getWidth(), cellRect.y+cellRect.height);
                g.setColor(rowSelected ? table.getSelectionForeground() : getForeground());
                String s = Integer.toString(r+1);
                g.drawString(s, getWidth()-fm.stringWidth(s)-8, cellRect.y+cellRect.height - fm.getDescent());
            }
        }

        if (table.getShowVerticalLines()) {
            g.setColor(table.getGridColor());
            g.drawRect(0, 0, getWidth()-1, getHeight()-1);
        }
    }
}

