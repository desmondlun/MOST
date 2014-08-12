package edu.rutgers.MOST.presentation;

/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 

//package components;

/*
 * This code is based on an example provided by Richard Stanford, 
 * a tutorial reader.
 */

import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import edu.rutgers.MOST.config.LocalConfig;
import edu.rutgers.MOST.data.Solution;
import edu.rutgers.MOST.presentation.GraphicalInterface;

public class DynamicTree extends JPanel implements TreeSelectionListener {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected DefaultMutableTreeNode rootNode;
    protected DefaultTreeModel treeModel;
    protected JTree tree;
    protected DefaultMutableTreeNode currentParent;
    
    public static int row;

	public static int getRow() {
		return row;
	}

	public static void setRow(int row) {
		DynamicTree.row = row;
	}

	public JPopupMenu jPopupMenu = new JPopupMenu();       
//	public JMenuItem saveAsCSVItem = new JMenuItem("Save As CSV Reactions");    
//	public JMenuItem saveAsSBMLItem = new JMenuItem("Save As SBML");
//	public JMenuItem saveAllItem = new JMenuItem("Save All Optimizations");
	public JMenuItem deleteItem = new JMenuItem("Delete");
	public JMenuItem clearItem = new JMenuItem("Delete All Optimizations");
	
    public DefaultMutableTreeNode getCurrentParent() {
		return currentParent;
	}

	public void setCurrentParent(DefaultMutableTreeNode currentParent) {
		this.currentParent = currentParent;
	}

	private Toolkit toolkit = Toolkit.getDefaultToolkit();

    public DynamicTree() {
        super(new GridLayout(1,0));
        
        rootNode = new DefaultMutableTreeNode();
        treeModel = new DefaultTreeModel(rootNode);
        treeModel.addTreeModelListener(new MyTreeModelListener());
        tree = new JTree(treeModel);       
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        
        //Listen for when the selection changes.
        tree.addTreeSelectionListener(this);

        tree.setEditable(false);
        tree.setShowsRootHandles(true);
        tree.setRootVisible(false);
        
        //Enable tool tips.
        ToolTipManager.sharedInstance().registerComponent(tree);
        
        //Set the icon for leaf nodes.
        // This icon does not exist, but having a null icon apparently removes the default
        // image for a tree of a piece of paper with a folded corner
        // see - http://docs.oracle.com/javase/tutorial/uiswing/components/tree.html
        ImageIcon leafIcon = new ImageIcon("etc/DNA60h.jpg");
        if (leafIcon != null) {
        	ToolTipTreeCellRenderer renderer = new ToolTipTreeCellRenderer();
            //DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
            //renderer.setLeafIcon(leafIcon);
        	renderer.setIcon(leafIcon);
            tree.setCellRenderer(renderer);
        }

        JScrollPane scrollPane = new JScrollPane(tree);
        add(scrollPane);
        
        tree.addMouseListener(new MouseAdapter() {
    		public void mousePressed(MouseEvent e)  {check(e);}
    		public void mouseReleased(MouseEvent e) {check(e);}

			public void check(MouseEvent e) {
    			if (e.isPopupTrigger()) { //if the event shows the menu
    				int selRow = tree.getRowForLocation(e.getX(), e.getY());
    		        TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
    		        if((selRow != -1) && (selRow != 0)) {
    		        	setRow(selRow);
    		        	// http://www.javadocexamples.com/java_source/org/gui4j/core/listener/Gui4jMouseListenerTree.java.html
    		        	tree.setSelectionPath(selPath);
    		        	
    					deleteItem.setEnabled(true);
    					clearItem.setEnabled(true);
        				jPopupMenu.show((JTree) e.getSource(), e.getX(), e.getY()); //and show the menu
    		        }
    			}
    		}
    	});
		
		jPopupMenu.add(deleteItem);
		deleteItem.setEnabled(false);
		deleteItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) { 
				if (getRow() > 0) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode)
							tree.getLastSelectedPathComponent();
					treeModel.removeNodeFromParent(node);
					setNodeSelected(0);
				}				
			}
		});
		
		jPopupMenu.add(clearItem);
		clearItem.setEnabled(false);
		clearItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {
				GraphicalInterface.listModel.clear();
				GraphicalInterface.listModel.addElement(LocalConfig.getInstance().getModelName());
				clearButFirst();
				GraphicalInterface.outputTextArea.setText("");
				LocalConfig.getInstance().getOptimizationFilesList().clear();
				setNodeSelected(0);
			}
		});
		
        tree.add(jPopupMenu);
    }

	protected void clearButFirst() {
		// TODO Auto-generated method stub
		rootNode.removeAllChildren();
		addObject(new Solution(LocalConfig.getInstance().getModelName(), LocalConfig.getInstance().getModelName()));
		treeModel.reload();
	}

	/** Remove all nodes except the root node. */
    public void clear() {
        rootNode.removeAllChildren();
        treeModel.reload();
    }
    
    /** Remove the currently selected node. */
    public void removeCurrentNode() {
        TreePath currentSelection = tree.getSelectionPath();
        if (currentSelection != null) {
            DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)
                         (currentSelection.getLastPathComponent());
            MutableTreeNode parent = (MutableTreeNode)(currentNode.getParent());
            if (parent != null) {
                treeModel.removeNodeFromParent(currentNode);
                return;
            }
        } 

        // Either there was no selection, or the root was selected.
        toolkit.beep();
    }

    /** Add child to the currently selected node. */
    public DefaultMutableTreeNode addObject(Object child) {
        DefaultMutableTreeNode parentNode = null;
            parentNode = rootNode;
        return addObject(parentNode, child, true);
    }

    public JTree getTree() {
		return tree;
	}

	public DefaultMutableTreeNode getRootNode() {
		return rootNode;
	}

	public DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent,
                                            Object child) {
        return addObject(parent, child, false);
    }

    public DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent,
                                            Object child, 
                                            boolean shouldBeVisible) {
        DefaultMutableTreeNode childNode = 
                new DefaultMutableTreeNode(child);

        if (parent == null) {
            parent = rootNode;
        }
	
        //It is key to invoke this on the TreeModel, and NOT DefaultMutableTreeNode
        treeModel.insertNodeInto(childNode, parent, 
                                 parent.getChildCount());

        //Make sure the user can see the lovely new node.
        if (shouldBeVisible) {
            tree.scrollPathToVisible(new TreePath(childNode.getPath()));
        }
        return childNode;
    }

    class MyTreeModelListener implements TreeModelListener {
        public void treeNodesChanged(TreeModelEvent e) {
            DefaultMutableTreeNode node;
            node = (DefaultMutableTreeNode)(e.getTreePath().getLastPathComponent());

            /*
             * If the event lists children, then the changed
             * node is the child of the node we've already
             * gotten.  Otherwise, the changed node and the
             * specified node are the same.
             */

                int index = e.getChildIndices()[0];
                node = (DefaultMutableTreeNode)(node.getChildAt(index));

            //System.out.println("The user has finished editing the node.");
            //System.out.println("New value: " + node.getUserObject());
        }
        public void treeNodesInserted(TreeModelEvent e) {
        }
        public void treeNodesRemoved(TreeModelEvent e) {
        }
        public void treeStructureChanged(TreeModelEvent e) {
        }
    }
    
    /** Returns an ImageIcon, or null if the path was invalid. */
    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = DynamicTree.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

	@Override
	public void valueChanged(TreeSelectionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public void setNodeSelected(int id) {
		javax.swing.tree.TreePath path = this.tree.getPathForRow(id);//pass the selected id here  
		tree.setSelectionPath(path);  
		tree.scrollPathToVisible(path);
	}
	
	public void print(DefaultTreeModel treeModel) {
		tree.setModel(treeModel);
		StringBuffer rowElement = new StringBuffer();  
		for (int i = 0; i < tree.getRowCount(); i++) {  
			TreePath path = tree.getPathForRow(i);  
			int level = path.getPathCount();  
			//rowElement.delete(0, rowElement.length());  
			for (int j = 0; j < level; j++) {  
				rowElement.append("    ");  
			}  
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) path  
					.getLastPathComponent();  
			if (!node.isLeaf()) {  
				rowElement.append(tree.isCollapsed(i) ? "+ " : "- ");  
			}  
			rowElement.append(node.toString());  

		} 
	}
	
}
