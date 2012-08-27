package edu.rutgers.MOST.presentation;

import javax.swing.*;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JFrame;

import edu.rutgers.MOST.config.LocalConfig;
import edu.rutgers.MOST.data.ReactionFactory;
import edu.rutgers.MOST.data.SBMLProduct;
import edu.rutgers.MOST.data.SBMLReactant;
import edu.rutgers.MOST.data.SBMLReaction;
import edu.rutgers.MOST.logic.ReactionParser;

public class ReactionInterface extends JFrame {

	JButton submitButton = new JButton("Submit");
	JButton clearButton = new JButton(" Clear ");
	final JTextField reactionField = new JTextField();

	private String reactantString;
	private int numReactantFields;
	private int numProductFields;
	private int reactantCount;
	private int productCount;
	private String arrowString;
	private String productString;
	private String reactionEquation;
	private ArrayList reactantsList;
	private ArrayList productsList;
	private String oldReaction;

	static String databaseName;

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public static String getDatabaseName() {
		return databaseName;
	}

	public void setReactantString(String reactantString) {
		this.reactantString = reactantString;
	}

	public String getReactantString() {
		return reactantString;
	}

	public void setProductString(String productString) {
		this.productString = productString;
	}

	public String getProductString() {
		return productString;
	}

	public void setNumReactantFields(int numReactantFields) {
		this.numReactantFields = numReactantFields;
	}

	public int getNumReactantFields() {
		return numReactantFields;
	}

	public void setProductCount(int productCount) {
		this.productCount = productCount;
	}

	public int getProductCount() {
		return productCount;
	}

	public void setReactantCount(int reactantCount) {
		this.reactantCount = reactantCount;
	}

	public int getReactantCount() {
		return reactantCount;
	}

	public void setNumProductFields(int numProductFields) {
		this.numProductFields = numProductFields;
	}

	public int getNumProductFields() {
		return numProductFields;
	}

	public void setArrowString(String arrowString) {
		this.arrowString = arrowString;
	}

	public String getArrowString() {
		return arrowString;
	}

	public void setReactionEquation(String reactionEquation) {
		this.reactionEquation = reactionEquation;
	}

	public String getReactionEquation() {
		return reactionEquation;
	}

	public void setReactantsList(ArrayList<SBMLReactant> reactantsList) {
		this.reactantsList = reactantsList;
	}

	public ArrayList<SBMLReactant> getReactantsList() {
		return reactantsList;
	}

	public void setProductsList(ArrayList<SBMLProduct> productsList) {
		this.productsList = productsList;
	}

	public ArrayList<SBMLProduct> getProductsList() {
		return productsList;
	}

	public Integer getIdFromCurrentRow(int row) {

		int id = Integer.valueOf(GraphicalInterface.reactionsTable.getModel().getValueAt(row, 0).toString());		   
		return id;	   
	}

	public void setOldReaction(String oldReaction) {
		this.oldReaction = oldReaction;
	}

	public String getOldReaction() {
		return oldReaction;
	}

	// private final JList names;
	public ReactionInterface(final Connection con)
	throws SQLException {

		setTitle("Reaction Editor");

		if (((String) GraphicalInterface.reactionsTable.getModel().getValueAt(GraphicalInterface.getCurrentRow(), GraphicalInterfaceConstants.REVERSIBLE_COLUMN)).compareTo("true") == 0) {
			setArrowString("<==>");
		} else {
			setArrowString("-->");
		} 

		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setDatabaseName(LocalConfig.getInstance().getLoadedDatabase());
		Connection conn = DriverManager.getConnection("jdbc:sqlite:" + getDatabaseName() + ".db");

		//these should be equal, otherwise layout is flawed
		//100 an arbitrary number, should be large enough to accomodate the large reaction
		setNumReactantFields(100);
		setNumProductFields(100);
		setReactantCount(0);
		setProductCount(0);

		//temporary table model to populate metabolite combo boxes, only holds data for metabolite boxes 
		final JTable tempTable = new JTable();
		tempTable.setModel(new MetabolitesDatabaseTableModel(conn, new String("select * from metabolites")));
		final int metabCount = tempTable.getModel().getRowCount();

		//get id from row selected in graphical interface
		//int id = getIdFromCurrentRow(GraphicalInterface.getCurrentRow());

		ReactionParser parser = new ReactionParser();
		if (GraphicalInterface.reactionsTable.getModel().getValueAt(GraphicalInterface.getCurrentRow(), GraphicalInterfaceConstants.REACTION_STRING_COLUMN) != null) {
			if (parser.isValid((GraphicalInterface.reactionsTable.getModel().getValueAt(GraphicalInterface.getCurrentRow(), GraphicalInterfaceConstants.REACTION_STRING_COLUMN)).toString())) {
				setOldReaction((GraphicalInterface.reactionsTable.getModel().getValueAt(GraphicalInterface.getCurrentRow(), GraphicalInterfaceConstants.REACTION_STRING_COLUMN)).toString());
				ArrayList reactantsAndProducts = parser.parseReaction((GraphicalInterface.reactionsTable.getModel().getValueAt(GraphicalInterface.getCurrentRow(), GraphicalInterfaceConstants.REACTION_STRING_COLUMN)).toString(), getIdFromCurrentRow(GraphicalInterface.getCurrentRow()), LocalConfig.getInstance().getLoadedDatabase());
				setReactantsList((ArrayList) reactantsAndProducts.get(0));
				setReactantCount(getReactantsList().size());
				//reactionField.setText(aReaction.getReactionString());
				//checks if reaction equation has products
				if (reactantsAndProducts.size() > 1) {
					setProductsList((ArrayList) reactantsAndProducts.get(1));
					setProductCount(getProductsList().size());
				}        	
			}
		} else {
			reactionField.setText("");
		}

		/*************************************************************************/
		//create Box layout
		/*************************************************************************/

		//reactants
		final Box hbReactant[] = new Box[numReactantFields];  //array of reactants 
		Box vbLabeledReactant[] = new Box[numReactantFields];
		final Box hbReactants = Box.createHorizontalBox();    

		//reversible box
		Box vbRev = Box.createVerticalBox(); 

		//products
		Box hbProduct[] = new Box[numProductFields]; //array of products
		Box vbLabeledProduct[] = new Box[numProductFields];
		Box hbProducts = Box.createHorizontalBox();    

		Box hbReacProd = Box.createHorizontalBox(); //holds reactants, rev, and products boxes (top box)
		Box hbReaction = Box.createHorizontalBox(); //holds reaction (middle box)  
		Box hbButton = Box.createHorizontalBox(); //holds buttons (bottom box)
		Box vbAll = Box.createVerticalBox();    

		final String reactantCoeff[] = new String[numReactantFields];
		final String reactant[] = new String[numProductFields];
		final String productCoeff[] = new String[numReactantFields];
		final String product[] = new String[numProductFields];

		//labels for comboboxes
		String reacNum[] = new String[numReactantFields];
		JLabel reactantLabel[] = new JLabel[numReactantFields];

		String prodNum[] = new String[numReactantFields];
		JLabel productLabel[] = new JLabel[numReactantFields];

		//comboboxes
		final JComboBox cbReactant[] = new JComboBox[numReactantFields];    
		final JComboBox cbProduct[] = new JComboBox[numProductFields];

		final JTextField reactantCoeffField[] = new JTextField[numReactantFields];
		final JTextField reactantEditor[] = new JTextField[numReactantFields];
		final JTextField productCoeffField[] = new JTextField[numProductFields];    
		final JTextField productEditor[] = new JTextField[numProductFields];

		JPanel panelReactants = new JPanel();
		panelReactants.setLayout(new BoxLayout(panelReactants, BoxLayout.X_AXIS));

		panelReactants.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));

		JPanel panelProducts = new JPanel();
		panelProducts.setLayout(new BoxLayout(panelProducts, BoxLayout.X_AXIS));

		panelProducts.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));

		final JRadioButton trueButton = new JRadioButton("True");
		final JRadioButton falseButton = new JRadioButton("False");

		//create reactant combo boxes
		for (int i = 0; i < numReactantFields; i++) {
			cbReactant[i] = new JComboBox<String>();
			cbReactant[i].setEditable(true);

			//set all comboboxes disabled except first pair for blank tables (size = 0)
			//or up to size of reactantsList if reactants table is populated

			if (GraphicalInterface.reactionsTable.getModel().getValueAt(GraphicalInterface.getCurrentRow(), GraphicalInterfaceConstants.REACTION_STRING_COLUMN) != null) {
				if (i > getReactantCount()) {
					cbReactant[i].setEnabled(false);
				}
			}

			reactantCoeffField[i] = new JTextField();
			reactantCoeffField[i].setPreferredSize(new Dimension(50, 20));
			cbReactant[i].setPreferredSize(new Dimension(150, 20));

			//create label
			reacNum[i] = "          Reactant " + (i + 1);

			reactantLabel[i] = new JLabel();
			reactantLabel[i].setText(reacNum[i]);
			reactantLabel[i].setAlignmentX(CENTER_ALIGNMENT);

			//create reactant box
			hbReactant[i] = Box.createHorizontalBox(); 

			JPanel panelReactant[] = new JPanel[numReactantFields];    
			panelReactant[i] = new JPanel();
			panelReactant[i].setLayout(new BoxLayout(panelReactant[i], BoxLayout.X_AXIS));
			panelReactant[i].add(reactantCoeffField[i]);
			panelReactant[i].add(cbReactant[i]);
			panelReactant[i].setBorder(BorderFactory.createEmptyBorder(10,20,20,0));

			hbReactant[i].add(panelReactant[i]);

			//create labeled box
			vbLabeledReactant[i] = Box.createVerticalBox();

			vbLabeledReactant[i].add(new JLabel(" "));
			vbLabeledReactant[i].add(reactantLabel[i]);
			vbLabeledReactant[i].add(hbReactant[i]);

			//add labeled boxes to reactants panel
			panelReactants.add(vbLabeledReactant[i]);

			ActionListener reactantsActionListener = new ActionListener() {
				public void actionPerformed(ActionEvent actionEvent) {
					String reactantSelection[] = new String[numReactantFields];
					String reactant[] = new String[numReactantFields];
					ArrayList tempReactantsList = new ArrayList();
					for (int h = 0; h < numReactantFields; h++) {
						reactant[h] = "";

						reactantSelection[h] = (String) cbReactant[h].getSelectedItem();
						if (reactantSelection[h] != null) {
							if (reactantCoeffField[h].getText().length() > 0) {
								reactant[h] = reactantCoeffField[h].getText() + " " + reactantSelection[h];
							} else {
								reactant[h] = reactantSelection[h];
							}
							tempReactantsList.add(reactant[h]);
						}      	    	      
					}  

					//set reaction equation into text box
					String reacString = "";

					for (int i = 0; i < tempReactantsList.size(); i++) {
						if (i == 0) {
							reacString += (String) tempReactantsList.get(i);
						} else {
							reacString += " + " + (String) tempReactantsList.get(i);
						}
					}

					setReactantString(reacString);
					if (getReactantString() != null && getProductString() != null) {
						reactionField.setText(getReactantString() + " " + getArrowString() + " " + getProductString());
						submitButton.setEnabled(true);
					} else {
						reactionField.setText(reacString);
					}

					//enable and populate next combobox
					if ((tempReactantsList.size()) < numReactantFields) {
						if (cbReactant[tempReactantsList.size()].getItemCount() == 0) {
							//add metabolite list to second reactant combobox
							for (int y = 0; y < metabCount; y++) {
								if (tempTable.getModel().getValueAt(y, 1) != null) {
									String metabName = tempTable.getModel().getValueAt(y, 1).toString();
									cbReactant[tempReactantsList.size()].addItem(metabName);
								}
							}
							cbReactant[tempReactantsList.size()].setEnabled(true);
							cbReactant[tempReactantsList.size()].setSelectedIndex(-1);
							reactantEditor[tempReactantsList.size()] = new JTextField();
							reactantEditor[tempReactantsList.size()] = (JTextField)cbReactant[tempReactantsList.size()].getEditor().getEditorComponent();
							reactantEditor[tempReactantsList.size()].addKeyListener(new ComboKeyHandler(cbReactant[tempReactantsList.size()]));
						}
					}
				}
			};
			reactantCoeffField[i].addActionListener(reactantsActionListener);
			cbReactant[i].addActionListener(reactantsActionListener);
		} 

		//add metabolite list to first reactant combobox
		if (cbReactant[0].getItemCount() == 0) {
			for (int y = 0; y < metabCount; y++) {
				if (tempTable.getModel().getValueAt(y, 1) != null) {
					if (tempTable.getModel().getValueAt(y, 1) != null) {
						String metabName = tempTable.getModel().getValueAt(y, 1).toString();
						cbReactant[0].addItem(metabName);
					}
				}
			}
		}

		cbReactant[0].setSelectedIndex(-1);
		reactantEditor[0] = new JTextField();
		reactantEditor[0] = (JTextField)cbReactant[0].getEditor().getEditorComponent();
		reactantEditor[0].addKeyListener(new ComboKeyHandler(cbReactant[0]));
		//end reactant combo boxes
		/***************************************************************************/


		//create product combo boxes
		for (int j = 0; j < numProductFields; j++) {
			cbProduct[j] = new JComboBox<String>();
			cbProduct[j].setEditable(true);

			//set all comboboxes disabled except first pair for blank tables (size = 0)
			//or up to size of productsList if products table is populated
			if (GraphicalInterface.reactionsTable.getModel().getValueAt(GraphicalInterface.getCurrentRow(), GraphicalInterfaceConstants.REACTION_STRING_COLUMN) != null) {
				if (j > getProductCount()) {
					cbProduct[j].setEnabled(false);
				}
			}                    

			productCoeffField[j] = new JTextField();
			productCoeffField[j].setPreferredSize(new Dimension(50, 20));
			cbProduct[j].setPreferredSize(new Dimension(150, 20));        

			//create label
			prodNum[j] = "          Product " + (j + 1);

			productLabel[j] = new JLabel();
			productLabel[j].setText(prodNum[j]);
			productLabel[j].setAlignmentX(CENTER_ALIGNMENT);

			//create product box
			hbProduct[j] = Box.createHorizontalBox(); 

			JPanel panelProduct[] = new JPanel[numProductFields];    
			panelProduct[j] = new JPanel();
			panelProduct[j].setLayout(new BoxLayout(panelProduct[j], BoxLayout.X_AXIS));
			panelProduct[j].add(productCoeffField[j]);
			panelProduct[j].add(cbProduct[j]);
			panelProduct[j].setBorder(BorderFactory.createEmptyBorder(10,20,20,0));

			hbProduct[j].add(panelProduct[j]);

			//create labeled box
			vbLabeledProduct[j] = Box.createVerticalBox();

			vbLabeledProduct[j].add(new JLabel(" "));
			vbLabeledProduct[j].add(productLabel[j]);
			vbLabeledProduct[j].add(hbProduct[j]);

			//add labeled boxes to products panel
			panelProducts.add(vbLabeledProduct[j]);

			ActionListener productsActionListener = new ActionListener() {
				public void actionPerformed(ActionEvent actionEvent) {
					String productSelection[] = new String[numProductFields];
					String product[] = new String[numProductFields];
					ArrayList tempProductsList = new ArrayList();
					for (int h = 0; h < numProductFields; h++) {
						product[h] = "";	      
						productSelection[h] = (String) cbProduct[h].getSelectedItem();
						if (productSelection[h] != null) {
							if (productCoeffField[h].getText().length() > 0) {
								product[h] = productCoeffField[h].getText() + " " + productSelection[h];
							} else {
								product[h] = productSelection[h];
							}
							tempProductsList.add(product[h]);
						}      	    	      
					}

					//set reaction equation into text box
					String prodString = "";

					for (int i = 0; i < tempProductsList.size(); i++) {
						if (i == 0) {
							prodString += (String) tempProductsList.get(i);
						} else {
							prodString += " + " + " " + (String) tempProductsList.get(i);
						}
					}

					setProductString(prodString);
					if (getReactantString() != null && prodString != null) {
						reactionField.setText(getReactantString() + " " + getArrowString() + " " + prodString);
						submitButton.setEnabled(true);
					}

					//enable and populate next combobox
					if ((tempProductsList.size()) < numProductFields) {
						if (cbProduct[tempProductsList.size()].getItemCount() == 0) {
							//add metabolite list to second reactant combobox
							for (int y = 0; y < metabCount; y++) {
								if (tempTable.getModel().getValueAt(y, 1) != null) {
									String metabName = tempTable.getModel().getValueAt(y, 1).toString();
									cbProduct[tempProductsList.size()].addItem(metabName);
								}
							}
							cbProduct[tempProductsList.size()].setEnabled(true);
							cbProduct[tempProductsList.size()].setSelectedIndex(-1);
							productEditor[tempProductsList.size()] = new JTextField();
							productEditor[tempProductsList.size()] = (JTextField)cbProduct[tempProductsList.size()].getEditor().getEditorComponent();
							productEditor[tempProductsList.size()].addKeyListener(new ComboKeyHandler(cbProduct[tempProductsList.size()]));
						}
					}
				}
			};
			productCoeffField[j].addActionListener(productsActionListener);
			cbProduct[j].addActionListener(productsActionListener);
		} 

		//add metabolite list to first product combobox
		if (cbProduct[0].getItemCount() == 0) {
			for (int y = 0; y < metabCount; y++) {
				if (tempTable.getModel().getValueAt(y, 1) != null) {
					if (tempTable.getModel().getValueAt(y, 1) != null) {
						String metabName = tempTable.getModel().getValueAt(y, 1).toString();
						cbProduct[0].addItem(metabName);
					}
				}
			}
		}

		cbProduct[0].setSelectedIndex(-1);
		productEditor[0] = new JTextField();
		productEditor[0] = (JTextField)cbProduct[0].getEditor().getEditorComponent();
		productEditor[0].addKeyListener(new ComboKeyHandler(cbProduct[0]));

		/*****************************************************************************/
		//end create box layout
		/*****************************************************************************/

		/*****************************************************************************/
		//populate text fields of combo boxes with existing reactants and products and
		//create reaction string from these species
		/*****************************************************************************/


		for (int i = 0; i < getReactantCount(); i++ ) {
			if (getReactantsList().get(i).getStoic() != 1) {
				double reacCoeff = (getReactantsList().get(i).getStoic());
				String rc = Double.toString(reacCoeff);
				if (rc.endsWith(".0")) {
					rc = rc.substring(0, rc.length() - 2);
				}
				reactantCoeffField[i].setText(rc);
			}
			cbReactant[i].setSelectedItem(getReactantsList().get(i).getMetaboliteAbbreviation());
		}

		for (int j = 0; j < getProductCount(); j++ ) {
			if (getProductsList().get(j).getStoic() != 1) {
				double prodCoeff = (getProductsList().get(j).getStoic());
				String pc = Double.toString(prodCoeff);
				if (pc.endsWith(".0")) {
					pc = pc.substring(0, pc.length() - 2);
				}
				productCoeffField[j].setText(pc);
			}
			cbProduct[j].setSelectedItem(getProductsList().get(j).getMetaboliteAbbreviation());
		}


		/*****************************************************************************/
		//end populate text fields of combo boxes
		/*****************************************************************************/

		//add reactants panel to scrollpane
		JScrollPane reactantPane = new JScrollPane(panelReactants);
		hbReactants.add(reactantPane);
		//set scroll speed
		reactantPane.getHorizontalScrollBar().setUnitIncrement(30);

		JScrollPane productPane = new JScrollPane(panelProducts);
		hbProducts.add(productPane);
		productPane.getHorizontalScrollBar().setUnitIncrement(30);

		//Reversible panel     
		JLabel labelRev = new JLabel("Reversible");

		JPanel panelRev = new JPanel();
		panelRev.setLayout(new BoxLayout(panelRev, BoxLayout.Y_AXIS));

		panelRev.add(trueButton);
		panelRev.add(falseButton);

		//trueButton.setMnemonic(KeyEvent.VK_T);//bug - clears first reactant in reactionEquation
		trueButton.setEnabled(true);  
		//falseButton.setMnemonic(KeyEvent.VK_F);//bug - clears first reactant in reactionEquation
		falseButton.setEnabled(true); 

		if (((String) GraphicalInterface.reactionsTable.getModel().getValueAt(GraphicalInterface.getCurrentRow(), GraphicalInterfaceConstants.REVERSIBLE_COLUMN)).compareTo("true") == 0) {
			trueButton.setSelected(true);
		} else {
			falseButton.setSelected(true);
		} 

		vbRev.add(labelRev);
		vbRev.add(panelRev);

		ButtonGroup bg = new ButtonGroup();
		bg.add(trueButton);
		bg.add(falseButton);    

		//add reactants, reversible, and products boxes to reacprod (top) box
		hbReacProd.add(hbReactants);
		hbReacProd.add(vbRev);
		hbReacProd.add(hbProducts);   

		//add reaction field to scroll pane
		JScrollPane reactionPane = new JScrollPane(reactionField);
		reactionPane.setBorder(BorderFactory.createEmptyBorder(10,10,10,20));
		reactionPane.getHorizontalScrollBar().setUnitIncrement(30);

		//add reaction pane to reaction (middle) box
		hbReaction.add(reactionPane);

		if (GraphicalInterface.reactionsTable.getModel().getValueAt(GraphicalInterface.getCurrentRow(), GraphicalInterfaceConstants.REACTION_STRING_COLUMN) != null) {
			if (getProductCount() > 0) {
				submitButton.setEnabled(true);
			} else {
				submitButton.setEnabled(false);
			}
		}

		//submitButton.setEnabled(false);
		submitButton.setMnemonic(KeyEvent.VK_S);
		JLabel blank = new JLabel("      ");    
		clearButton.setMnemonic(KeyEvent.VK_C);
		JLabel blank2 = new JLabel("      ");
		JButton exitButton = new JButton("  Exit  ");
		exitButton.setMnemonic(KeyEvent.VK_X);

		JPanel lowerPanel = new JPanel();
		lowerPanel.setLayout(new BoxLayout(lowerPanel, BoxLayout.X_AXIS));
		lowerPanel.add(submitButton);
		lowerPanel.add(blank);
		lowerPanel.add(clearButton);
		lowerPanel.add(blank2);
		lowerPanel.add(exitButton);
		lowerPanel.setBorder(BorderFactory.createEmptyBorder(0,20,10,20));

		hbButton.add(lowerPanel);

		vbAll.add(hbReacProd);
		vbAll.add(hbReaction);
		vbAll.add(lowerPanel);
		add(vbAll);

		//create listeners

		ActionListener revActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent revActionEvent) {
				int id = getIdFromCurrentRow(GraphicalInterface.getCurrentRow());
				ReactionFactory rFactory = new ReactionFactory();			
				SBMLReaction aReaction = (SBMLReaction)rFactory.getReactionById(id, "SBML", getDatabaseName());
				cbProduct[0].grabFocus();
				if (trueButton.isSelected()) {
					setArrowString(" <==> ");
					aReaction.setReversible("true");
					GraphicalInterface.reactionsTable.getModel().setValueAt("true", GraphicalInterface.getCurrentRow(), GraphicalInterfaceConstants.REVERSIBLE_COLUMN);
				} else if (falseButton.isSelected()) {
					setArrowString("-->");
					aReaction.setReversible("false");
					GraphicalInterface.reactionsTable.getModel().setValueAt("false", GraphicalInterface.getCurrentRow(), GraphicalInterfaceConstants.REVERSIBLE_COLUMN);
				}
				if (getReactantString()!= null && getProductString() != null) {
					reactionField.setText(getReactantString() + " " + getArrowString() + " " + getProductString());	
				} else {
					reactionField.setText(getReactantString());
				}
			}
		};

		ActionListener exitButtonActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent prodActionEvent) {
				setVisible(false);
				dispose();
			}
		}; 

		ActionListener clearButtonActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent prodActionEvent) {				    	  
				for (int i = 0; i < numReactantFields; i++) {
					//cbReacCoeff[i].setSelectedIndex(-1);
					cbReactant[i].setSelectedIndex(-1);
					cbProduct[i].setSelectedIndex(-1);
				}
				/*
			for (int i = 0; i < reactantsCount; i++) {
				reactantCoeffField[i].setText("");
				reactantEditor[i].setText("");
			}
			for (int i = 1; i < numReactantFields; i++) {
				//cbReacCoeff[i].setEnabled(false);
	    	    //cbReactant[i].setEnabled(false);
			}
            for (int i = 0; i < numProductFields; i++) {
                //cbProdCoeff[i].setSelectedIndex(-1);
				cbProduct[i].setSelectedIndex(-1);
				//cbProdCoeff[i].setEnabled(false);
	    	    //cbProduct[i].setEnabled(false);				
			}            
            for (int i = 0; i < productsCount; i++) {
            	productCoeffField[i].setText("");
            	productEditor[i].setText("");
			}
				 */

				reactionField.setText("");
				submitButton.setEnabled(false);
				trueButton.setSelected(false);
				falseButton.setSelected(true);    	    
				//sends cursor to first reactant coefficient combobox
				cbReactant[0].grabFocus();
			}
		};        

		trueButton.addActionListener(revActionListener);
		falseButton.addActionListener(revActionListener);

		//submitButton.addActionListener(submitButtonActionListener);
		clearButton.addActionListener(clearButtonActionListener);
		exitButton.addActionListener(exitButtonActionListener);


	}

}

