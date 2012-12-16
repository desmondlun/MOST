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
import edu.rutgers.MOST.logic.ReactionParser;

public class ReactionInterface extends JFrame {

	JButton okButton = new JButton("   OK   ");
	JButton clearButton = new JButton(" Clear ");
	final JTextField reactionField = new JTextField();

	private String reactantString;
	private int numReactantFields;
	private int numProductFields;
	private String arrowString;
	private String productString;
	private String reactionEquation;
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

		//temporary table model to populate metabolite combo boxes, only holds data for metabolite boxes 
		final JTable tempTable = new JTable();
		tempTable.setModel(new MetabolitesDatabaseTableModel(conn, new String("select * from metabolites")));
		ArrayList<String> metabList = new ArrayList<String>();
		for (int m = 0; m < tempTable.getModel().getRowCount(); m++) {
			metabList.add((String) tempTable.getModel().getValueAt(m, 1));
		}
		
		int viewRow = GraphicalInterface.reactionsTable.convertRowIndexToModel(GraphicalInterface.getCurrentRow());
		String reactionEquation = ((String) GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTION_STRING_COLUMN));
		
		if (((String) GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REVERSIBLE_COLUMN)).compareTo("true") == 0) {
			setArrowString("<==>");
		} else {
			setArrowString("-->");
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
			
			for (int m = 0; m < metabList.size(); m++) {
				cbReactant[i].addItem(metabList.get(m));
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

			cbReactant[i].setSelectedIndex(-1);
			reactantEditor[i] = new JTextField();
			reactantEditor[i] = (JTextField)cbReactant[i].getEditor().getEditorComponent();
			reactantEditor[i].addKeyListener(new ComboKeyHandler(cbReactant[i]));
			
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
					if (getReactantString() == null || getReactantString().length() == 0) {
						reactionField.setText(getArrowString() + " " + getProductString());
						okButton.setEnabled(true);
					} else if (getProductString() == null || getProductString().length() == 0) {
						reactionField.setText(getReactantString() + " " + getArrowString());
						okButton.setEnabled(true);
					} else {
						reactionField.setText(getReactantString() + " " + getArrowString() + " " + getProductString());
						okButton.setEnabled(true);
					}
				}
			};
			reactantCoeffField[i].addActionListener(reactantsActionListener);
			cbReactant[i].addActionListener(reactantsActionListener);
		} 

		//end reactant combo boxes
		/***************************************************************************/

		//create product combo boxes
		for (int j = 0; j < numProductFields; j++) {
			cbProduct[j] = new JComboBox<String>();
			cbProduct[j].setEditable(true);
			
			for (int m = 0; m < metabList.size(); m++) {
				cbProduct[j].addItem(metabList.get(m));
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
			
			cbProduct[j].setSelectedIndex(-1);
			productEditor[j] = new JTextField();
			productEditor[j] = (JTextField)cbProduct[j].getEditor().getEditorComponent();
			productEditor[j].addKeyListener(new ComboKeyHandler(cbProduct[j]));

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
					if (getReactantString() == null || getReactantString().length() == 0) {
						reactionField.setText(getArrowString() + " " + prodString);
						okButton.setEnabled(true);
					} else if (getProductString() == null || getProductString().length() == 0) {
						reactionField.setText(getReactantString() + " " + getArrowString());
						okButton.setEnabled(true);
					} else {
						reactionField.setText(getReactantString() + " " + getArrowString() + " " + prodString);
						okButton.setEnabled(true);
					}
				}
			};
			productCoeffField[j].addActionListener(productsActionListener);
			cbProduct[j].addActionListener(productsActionListener);
		} 

		/*****************************************************************************/
		//end create box layout
		/*****************************************************************************/

		/*****************************************************************************/
		//populate text fields of combo boxes with existing reactants and products and
		//create reaction string from these species
		/*****************************************************************************/

		ReactionParser parser = new ReactionParser();
		if (parser.isValid(reactionEquation)) {
			setOldReaction(reactionEquation);
			ArrayList<ArrayList<String>> reactants = parser.reactionList(reactionEquation.trim()).get(0);
			//reactions of the type ==> b will be size 1, assigned the value [0] in parser
			if (reactants.get(0).size() == 1) {				
			} else {
				for (int r = 0; r < reactants.size(); r++) {
					if (reactants.get(r).size() == 2) {
						String stoicStr = (String) reactants.get(r).get(0);
						if (!(Double.valueOf(stoicStr) == 1)) {
							if (stoicStr.endsWith(".0")) {
								stoicStr = stoicStr.substring(0, stoicStr.length() - 2);
							}
							reactantCoeffField[r].setText(stoicStr);
						}
						String reactant = (String) reactants.get(r).get(1);
						cbReactant[r].setSelectedItem(reactant);
					}
				}			
			}
			ArrayList<ArrayList<String>> products = parser.reactionList(reactionEquation.trim()).get(1);
			//reactions of the type ==> b will be size 1, assigned the value [0] in parser
			if (products.get(0).size() == 1) {				
			} else {
				for (int p = 0; p < products.size(); p++) {
					if (products.get(p).size() == 2) {
						String stoicStr = (String) products.get(p).get(0);
						if (!(Double.valueOf(stoicStr) == 1)) {
							if (stoicStr.endsWith(".0")) {
								stoicStr = stoicStr.substring(0, stoicStr.length() - 2);
							}
							productCoeffField[p].setText(stoicStr);
						}
						String product = (String) products.get(p).get(1);
						cbProduct[p].setSelectedItem(product);
					}
				}			
			}
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

		okButton.setMnemonic(KeyEvent.VK_O);
		JLabel blank = new JLabel("      ");    
		clearButton.setMnemonic(KeyEvent.VK_E);
		JLabel blank2 = new JLabel("      ");
		JButton cancelButton = new JButton("Cancel");
		cancelButton.setMnemonic(KeyEvent.VK_C);

		JPanel lowerPanel = new JPanel();
		lowerPanel.setLayout(new BoxLayout(lowerPanel, BoxLayout.X_AXIS));
		lowerPanel.add(okButton);
		lowerPanel.add(blank);
		lowerPanel.add(cancelButton);
		lowerPanel.add(blank2);		
		lowerPanel.add(clearButton);
		lowerPanel.setBorder(BorderFactory.createEmptyBorder(0,20,10,20));

		hbButton.add(lowerPanel);

		hbButton.add(lowerPanel);

		vbAll.add(hbReacProd);
		vbAll.add(hbReaction);
		vbAll.add(lowerPanel);
		add(vbAll);

		//create listeners

		ActionListener revActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent revActionEvent) {		
				ReactionFactory rFactory = new ReactionFactory("SBML", LocalConfig.getInstance().getLoadedDatabase());			
				cbProduct[0].grabFocus();
				if (trueButton.isSelected()) {
					setArrowString(" <==> ");
				} else if (falseButton.isSelected()) {
					setArrowString("-->");
				}
				if (getReactantString()!= null && getProductString() != null) {
					reactionField.setText(getReactantString() + " " + getArrowString() + " " + getProductString());	
				} else if (getReactantString()!= null) {
					reactionField.setText(getReactantString() + " " + getArrowString());
				} else {
					reactionField.setText("");
				}
			}
		};

		ActionListener cancelButtonActionListener = new ActionListener() {
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
			
				reactionField.setText("");
				okButton.setEnabled(false);
				trueButton.setSelected(false);
				falseButton.setSelected(true);    	    
				//sends cursor to first reactant coefficient combobox
				cbReactant[0].grabFocus();
			}
		};        

		trueButton.addActionListener(revActionListener);
		falseButton.addActionListener(revActionListener);

		clearButton.addActionListener(clearButtonActionListener);
		cancelButton.addActionListener(cancelButtonActionListener);		
		
	}

}

