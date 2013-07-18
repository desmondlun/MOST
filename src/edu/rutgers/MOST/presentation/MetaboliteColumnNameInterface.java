package edu.rutgers.MOST.presentation;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import edu.rutgers.MOST.config.ConfigConstants;
import edu.rutgers.MOST.config.LocalConfig;
import edu.rutgers.MOST.data.DatabaseCreator;
import edu.rutgers.MOST.data.MetabolitesMetaColumnManager;
import edu.rutgers.MOST.data.TextMetabolitesModelReader;

public class MetaboliteColumnNameInterface  extends JFrame {

	public JComboBox<String> cbMetaboliteAbbreviation = new JComboBox<String>();
	public JComboBox<String> cbMetaboliteName = new JComboBox<String>();
	public JComboBox<String> cbCharge = new JComboBox<String>();
	public JComboBox<String> cbCompartment = new JComboBox<String>();
	public JComboBox<String> cbBoundary = new JComboBox<String>();

	public JButton okButton = new JButton("     OK     ");
	public JButton cancelButton = new JButton("  Cancel  ");
	public JButton prevRowButton = new JButton("Previous Row");
	public JButton nextRowButton = new JButton(" Next Row ");
	public JLabel rowLabel = new JLabel();
	
	public static ArrayList<String> columnNamesFromFile;

	public static ArrayList<String> getColumnNamesFromFile() {
		return columnNamesFromFile;
	}

	public void setColumnNamesFromFile(ArrayList<String> columnNamesFromFile) {
		this.columnNamesFromFile = columnNamesFromFile;
	}

	private Task task;

	public final ProgressBar progressBar = new ProgressBar();

	javax.swing.Timer timer = new javax.swing.Timer(1000, new TimeListener());

	public MetaboliteColumnNameInterface(final Connection con, ArrayList<String> columnNamesFromFile)
	throws SQLException {

		prevRowButton.setEnabled(false);
		LocalConfig.getInstance().setMetabolitesNextRowCorrection(0);
		rowLabel.setText("   row " + (LocalConfig.getInstance().getMetabolitesNextRowCorrection() + 1));
		
		final ArrayList<Image> icons = new ArrayList<Image>(); 
		icons.add(new ImageIcon("etc/most16.jpg").getImage()); 
		icons.add(new ImageIcon("etc/most32.jpg").getImage());
		
		getRootPane().setDefaultButton(okButton);

		LocalConfig.getInstance().setProgress(0);
		progressBar.pack();
		progressBar.setIconImages(icons);
		progressBar.setSize(GraphicalInterfaceConstants.PROGRESS_BAR_WIDTH, GraphicalInterfaceConstants.PROGRESS_BAR_HEIGHT);
		//progressBar.setSize(200, 75);
		progressBar.setTitle("Loading...");
		progressBar.setLocationRelativeTo(null);
		progressBar.setVisible(false);

		setColumnNamesFromFile(columnNamesFromFile);

		setTitle(ColumnInterfaceConstants.METABOLITES_COLUMN_NAME_INTERFACE_TITLE);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		//setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		LocalConfig.getInstance().setMetabolitesNextRowCorrection(0);

		cbMetaboliteAbbreviation.setEditable(true);	
		cbMetaboliteName.setEditable(true);
		cbCharge.setEditable(true);	
		cbCompartment.setEditable(true);
		cbBoundary.setEditable(true);

		cbMetaboliteAbbreviation.setPreferredSize(new Dimension(250, 30));
		cbMetaboliteAbbreviation.setMaximumSize(new Dimension(250, 30));
		cbMetaboliteAbbreviation.setMinimumSize(new Dimension(250, 30));

		cbMetaboliteName.setPreferredSize(new Dimension(250, 30));
		cbMetaboliteName.setMaximumSize(new Dimension(250, 30));
		cbMetaboliteName.setMinimumSize(new Dimension(250, 30));

		cbCharge.setPreferredSize(new Dimension(250, 30));
		cbCharge.setMaximumSize(new Dimension(250, 30));
		cbCharge.setMinimumSize(new Dimension(250, 30));

		cbCompartment.setPreferredSize(new Dimension(250, 30));
		cbCompartment.setMaximumSize(new Dimension(250, 30));
		cbCompartment.setMinimumSize(new Dimension(250, 30));

		cbBoundary.setPreferredSize(new Dimension(250, 30));
		cbBoundary.setMaximumSize(new Dimension(250, 30));
		cbBoundary.setMinimumSize(new Dimension(250, 30));

		populateNamesFromFileBoxes(columnNamesFromFile);
		
		/*
		JTextField fieldMetaboliteAbbreviation = (JTextField)cbMetaboliteAbbreviation.getEditor().getEditorComponent();
		fieldMetaboliteAbbreviation.addKeyListener(new ComboKeyHandler(cbMetaboliteAbbreviation));

		JTextField fieldMetaboliteName = (JTextField)cbMetaboliteName.getEditor().getEditorComponent();
		fieldMetaboliteName.addKeyListener(new ComboKeyHandler(cbMetaboliteName));

		JTextField fieldCharge = (JTextField)cbCharge.getEditor().getEditorComponent();
		fieldCharge.addKeyListener(new ComboKeyHandler(cbCharge));

		JTextField fieldCompartment = (JTextField)cbCompartment.getEditor().getEditorComponent();
		fieldCompartment.addKeyListener(new ComboKeyHandler(cbCompartment));

		JTextField fieldBoundary = (JTextField)cbBoundary.getEditor().getEditorComponent();
		fieldBoundary.addKeyListener(new ComboKeyHandler(cbBoundary));
        */
		//populateNamesFromFileBoxes(columnNamesFromFile);
		
		//box layout
		Box vb = Box.createVerticalBox();

		Box hbLabels = Box.createHorizontalBox();
		Box hb1 = Box.createHorizontalBox();
		Box hb2 = Box.createHorizontalBox();
		Box hb3 = Box.createHorizontalBox();
		Box hb4 = Box.createHorizontalBox();
		Box hb5 = Box.createHorizontalBox();

		Box hbTop = Box.createHorizontalBox();	    	    
		Box hbMetaboliteAbbreviationLabel = Box.createHorizontalBox();	    
		Box hbMetaboliteAbbreviation = Box.createHorizontalBox();
		Box hbMetaboliteNameLabel = Box.createHorizontalBox();	    
		Box hbMetabolite = Box.createHorizontalBox();
		Box hbChargeLabel = Box.createHorizontalBox();	    
		Box hbCharge = Box.createHorizontalBox();
		Box hbCompartmentLabel = Box.createHorizontalBox();	    
		Box hbCompartment = Box.createHorizontalBox();
		Box hbBoundaryLabel = Box.createHorizontalBox();	    
		Box hbBoundary = Box.createHorizontalBox();

		Box hbRequiredLabel = Box.createHorizontalBox();
		Box hbButton = Box.createHorizontalBox();

		//top label
		JLabel topLabel = new JLabel();
		topLabel.setText(ColumnInterfaceConstants.METABOLITES_TOP_LABEL);
		topLabel.setSize(new Dimension(300, 30));
		//top, left, bottom. right
		topLabel.setBorder(BorderFactory.createEmptyBorder(20,30,20,200));
		topLabel.setAlignmentX(LEFT_ALIGNMENT);

		hbTop.add(topLabel);	
		hbTop.setAlignmentX(LEFT_ALIGNMENT);

		hbLabels.add(hbTop);

		//metabolite Abbreviation Label and combo
		JLabel metaboliteAbbreviationLabel = new JLabel();
		metaboliteAbbreviationLabel.setText(ColumnInterfaceConstants.METABOLITE_ABBREVIATION_LABEL);
		metaboliteAbbreviationLabel.setSize(new Dimension(250, 20));
		metaboliteAbbreviationLabel.setMinimumSize(new Dimension(250, 20));
		metaboliteAbbreviationLabel.setBorder(BorderFactory.createEmptyBorder(0,0,10,80));
		metaboliteAbbreviationLabel.setAlignmentX(LEFT_ALIGNMENT);
		//metaboliteAbbreviationLabel.setAlignmentY(TOP_ALIGNMENT);	    	    

		JPanel panelMetaboliteAbbreviationLabel = new JPanel();
		panelMetaboliteAbbreviationLabel.setLayout(new BoxLayout(panelMetaboliteAbbreviationLabel, BoxLayout.X_AXIS));
		panelMetaboliteAbbreviationLabel.add(metaboliteAbbreviationLabel);
		panelMetaboliteAbbreviationLabel.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));

		hbMetaboliteAbbreviationLabel.add(panelMetaboliteAbbreviationLabel);
		hbMetaboliteAbbreviationLabel.setAlignmentX(LEFT_ALIGNMENT);

		JPanel panelMetaboliteAbbreviation = new JPanel();
		panelMetaboliteAbbreviation.setLayout(new BoxLayout(panelMetaboliteAbbreviation, BoxLayout.X_AXIS));
		panelMetaboliteAbbreviation.add(cbMetaboliteAbbreviation);
		panelMetaboliteAbbreviation.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
		panelMetaboliteAbbreviation.setAlignmentX(RIGHT_ALIGNMENT);

		hbMetaboliteAbbreviation.add(panelMetaboliteAbbreviation);
		hbMetaboliteAbbreviation.setAlignmentX(RIGHT_ALIGNMENT);

		hb1.add(hbMetaboliteAbbreviationLabel);
		hb1.add(hbMetaboliteAbbreviation);

		//metabolite Name Label and combo
		JLabel metaboliteNameLabel = new JLabel();
		metaboliteNameLabel.setText(ColumnInterfaceConstants.METABOLITE_NAME_LABEL);
		metaboliteNameLabel.setSize(new Dimension(250, 20));	
		metaboliteNameLabel.setMinimumSize(new Dimension(250, 20));
		metaboliteNameLabel.setBorder(BorderFactory.createEmptyBorder(0,0,0,80));
		metaboliteNameLabel.setAlignmentX(LEFT_ALIGNMENT);

		JPanel panelMetaboliteNameLabel = new JPanel();
		panelMetaboliteNameLabel.setLayout(new BoxLayout(panelMetaboliteNameLabel, BoxLayout.X_AXIS));
		panelMetaboliteNameLabel.add(metaboliteNameLabel);
		panelMetaboliteNameLabel.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));

		hbMetaboliteNameLabel.add(panelMetaboliteNameLabel);
		hbMetaboliteNameLabel.setAlignmentX(LEFT_ALIGNMENT);

		JPanel panelMetaboliteName = new JPanel();
		panelMetaboliteName.setLayout(new BoxLayout(panelMetaboliteName, BoxLayout.X_AXIS));
		panelMetaboliteName.add(cbMetaboliteName);
		panelMetaboliteName.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
		panelMetaboliteName.setAlignmentX(RIGHT_ALIGNMENT);

		hbMetabolite.add(panelMetaboliteName);
		hbMetabolite.setAlignmentX(RIGHT_ALIGNMENT);

		hb2.add(hbMetaboliteNameLabel);
		hb2.add(hbMetabolite);

		//charge label and combo
		JLabel chargeLabel = new JLabel();
		chargeLabel.setText(ColumnInterfaceConstants.CHARGE_LABEL);
		chargeLabel.setSize(new Dimension(250, 20));
		chargeLabel.setMinimumSize(new Dimension(250, 20));
		chargeLabel.setBorder(BorderFactory.createEmptyBorder(0,0,0,80));
		chargeLabel.setAlignmentX(LEFT_ALIGNMENT);

		JPanel panelChargeLabel = new JPanel();
		panelChargeLabel.setLayout(new BoxLayout(panelChargeLabel, BoxLayout.X_AXIS));
		panelChargeLabel.add(chargeLabel);
		panelChargeLabel.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));

		hbChargeLabel.add(panelChargeLabel);
		hbChargeLabel.setAlignmentX(LEFT_ALIGNMENT);

		JPanel panelCharge = new JPanel();
		panelCharge.setLayout(new BoxLayout(panelCharge, BoxLayout.X_AXIS));
		panelCharge.add(cbCharge);
		panelCharge.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
		panelCharge.setAlignmentX(RIGHT_ALIGNMENT);

		hbCharge.add(panelCharge);
		hbCharge.setAlignmentX(RIGHT_ALIGNMENT);

		hb3.add(hbChargeLabel);
		hb3.add(hbCharge);

		//compartment label and combo
		JLabel compartmentLabel = new JLabel();
		compartmentLabel.setText(ColumnInterfaceConstants.COMPARTMENT_LABEL);
		compartmentLabel.setSize(new Dimension(250, 20));
		compartmentLabel.setMinimumSize(new Dimension(250, 20));
		compartmentLabel.setBorder(BorderFactory.createEmptyBorder(0,0,0,80));
		compartmentLabel.setAlignmentX(LEFT_ALIGNMENT);

		JPanel panelCompartmentLabel = new JPanel();
		panelCompartmentLabel.setLayout(new BoxLayout(panelCompartmentLabel, BoxLayout.X_AXIS));
		panelCompartmentLabel.add(compartmentLabel);
		panelCompartmentLabel.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));

		hbCompartmentLabel.add(panelCompartmentLabel);
		hbCompartmentLabel.setAlignmentX(LEFT_ALIGNMENT);

		JPanel panelCompartment = new JPanel();
		panelCompartment.setLayout(new BoxLayout(panelCompartment, BoxLayout.X_AXIS));
		panelCompartment.add(cbCompartment);
		panelCompartment.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
		panelCompartment.setAlignmentX(RIGHT_ALIGNMENT);

		hbCompartment.add(panelCompartment);
		hbCompartment.setAlignmentX(RIGHT_ALIGNMENT);

		hb4.add(hbCompartmentLabel);
		hb4.add(hbCompartment);

		//boundary label and combo
		JLabel boundaryLabel = new JLabel();
		boundaryLabel.setText(ColumnInterfaceConstants.BOUNDARY_LABEL);
		boundaryLabel.setSize(new Dimension(250, 20));	
		boundaryLabel.setMinimumSize(new Dimension(250, 20));
		boundaryLabel.setBorder(BorderFactory.createEmptyBorder(0,0,0,80));
		boundaryLabel.setAlignmentX(LEFT_ALIGNMENT);

		JPanel panelBoundaryLabel = new JPanel();
		panelBoundaryLabel.setLayout(new BoxLayout(panelBoundaryLabel, BoxLayout.X_AXIS));
		panelBoundaryLabel.add(boundaryLabel);
		panelBoundaryLabel.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));

		hbBoundaryLabel.add(panelBoundaryLabel);
		hbBoundaryLabel.setAlignmentX(LEFT_ALIGNMENT);

		JPanel panelBoundary = new JPanel();
		panelBoundary.setLayout(new BoxLayout(panelBoundary, BoxLayout.X_AXIS));
		panelBoundary.add(cbBoundary);
		panelBoundary.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
		panelBoundary.setAlignmentX(RIGHT_ALIGNMENT);

		hbBoundary.add(panelBoundary);
		hbBoundary.setAlignmentX(RIGHT_ALIGNMENT);

		hb5.add(hbBoundaryLabel);
		hb5.add(hbBoundary);

		JLabel required = new JLabel(ColumnInterfaceConstants.REQUIRED_LABEL);
		hbRequiredLabel.add(required);
		
		okButton.setMnemonic(KeyEvent.VK_O);
		JLabel blank = new JLabel("    "); 
		cancelButton.setMnemonic(KeyEvent.VK_C);
		JLabel blank1 = new JLabel("    ");
		prevRowButton.setMnemonic(KeyEvent.VK_P);
		JLabel blank2 = new JLabel("    ");
		nextRowButton.setMnemonic(KeyEvent.VK_N);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(okButton);
		buttonPanel.add(blank);
		buttonPanel.add(cancelButton);
		buttonPanel.add(blank1);
		buttonPanel.add(prevRowButton);
		buttonPanel.add(blank2);
		buttonPanel.add(nextRowButton);
		buttonPanel.add(rowLabel);
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(10,20,20,20));

		hbButton.add(buttonPanel);

		vb.add(hbLabels);
		vb.add(hb1);
		vb.add(hb2);
		vb.add(hb3);
		vb.add(hb4);
		vb.add(hb5);
		vb.add(hbRequiredLabel);
		vb.add(hbButton);

		add(vb);

		ActionListener okButtonActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent prodActionEvent) {
				if (cbMetaboliteAbbreviation.getSelectedIndex() == -1) {
					JOptionPane.showMessageDialog(null,                
							ColumnInterfaceConstants.BLANK_METABOLITE_FIELDS_ERROR_MESSAGE,
							ColumnInterfaceConstants.BLANK_METABOLITE_FIELDS_ERROR_TITLE,                                
							JOptionPane.ERROR_MESSAGE);
				} else if (cbMetaboliteAbbreviation.getSelectedItem().toString().toLowerCase().equals(GraphicalInterfaceConstants.METAB_ABBREVIATION_NOT_FILTER[0]) ||
						cbMetaboliteAbbreviation.getSelectedItem().toString().toLowerCase().equals(GraphicalInterfaceConstants.METAB_ABBREVIATION_NOT_FILTER[1]) ||
						cbMetaboliteAbbreviation.getSelectedItem().toString().toLowerCase().equals(GraphicalInterfaceConstants.METAB_ABBREVIATION_NOT_FILTER[2])) {
					JOptionPane.showMessageDialog(null,                
							"Invalid name for Metabolite Abbreviation column.",
							"Column Name Error",                                
							JOptionPane.ERROR_MESSAGE);
				} else {
					//add metacolumn names to db
					MetabolitesMetaColumnManager metabolitesMetaColumnManager = new MetabolitesMetaColumnManager();
					ArrayList<String> metaColumnNames = new ArrayList();
					ArrayList<Integer> usedIndices = new ArrayList();
					ArrayList<Integer> metaColumnIndexList = new ArrayList();

					if (getColumnNamesFromFile().contains(cbMetaboliteAbbreviation.getSelectedItem())) {
						LocalConfig.getInstance().setMetaboliteAbbreviationColumnIndex(getColumnNamesFromFile().indexOf(cbMetaboliteAbbreviation.getSelectedItem()));
						usedIndices.add(getColumnNamesFromFile().indexOf(cbMetaboliteAbbreviation.getSelectedItem()));
					}
					if (cbMetaboliteName.getSelectedIndex() == -1) {
						LocalConfig.getInstance().getHiddenMetabolitesColumns().add(GraphicalInterfaceConstants.METABOLITE_NAME_COLUMN);
					} else if (getColumnNamesFromFile().contains(cbMetaboliteName.getSelectedItem())) {
						LocalConfig.getInstance().setMetaboliteNameColumnIndex(getColumnNamesFromFile().indexOf(cbMetaboliteName.getSelectedItem()));
						usedIndices.add(getColumnNamesFromFile().indexOf(cbMetaboliteName.getSelectedItem()));
					}
					if (cbCharge.getSelectedIndex() == -1) {
						LocalConfig.getInstance().getHiddenMetabolitesColumns().add(GraphicalInterfaceConstants.CHARGE_COLUMN);
					} else if (getColumnNamesFromFile().contains(cbCharge.getSelectedItem())) {
						LocalConfig.getInstance().setChargeColumnIndex(getColumnNamesFromFile().indexOf(cbCharge.getSelectedItem()));
						usedIndices.add(getColumnNamesFromFile().indexOf(cbCharge.getSelectedItem()));
					}
					if (cbCompartment.getSelectedIndex() == -1) {
						LocalConfig.getInstance().getHiddenMetabolitesColumns().add(GraphicalInterfaceConstants.COMPARTMENT_COLUMN);
					} else if (getColumnNamesFromFile().contains(cbCompartment.getSelectedItem())) {
						LocalConfig.getInstance().setCompartmentColumnIndex(getColumnNamesFromFile().indexOf(cbCompartment.getSelectedItem()));
						usedIndices.add(getColumnNamesFromFile().indexOf(cbCompartment.getSelectedItem()));
					}
					if (getColumnNamesFromFile().contains(cbBoundary.getSelectedItem())) {
						LocalConfig.getInstance().setBoundaryColumnIndex(getColumnNamesFromFile().indexOf(cbBoundary.getSelectedItem()));
						usedIndices.add(getColumnNamesFromFile().indexOf(cbBoundary.getSelectedItem()));
					}
					for (int i = 0; i < getColumnNamesFromFile().size(); i++) {
						if (!usedIndices.contains(i)) {
							metaColumnNames.add(getColumnNamesFromFile().get(i));
							metaColumnIndexList.add(getColumnNamesFromFile().indexOf(getColumnNamesFromFile().get(i)));
						} 
					}
					DatabaseCreator creator = new DatabaseCreator();
					creator.createDatabase(LocalConfig.getInstance().getDatabaseName());
					metabolitesMetaColumnManager.addColumnNames(LocalConfig.getInstance().getDatabaseName(), metaColumnNames);
					LocalConfig.getInstance().setMetabolitesMetaColumnIndexList(metaColumnIndexList);

					setVisible(false);
					dispose();

					progressBar.setVisible(true);

					timer.start();

					task = new Task();
					task.execute();
				}				
			}
		};

		okButton.addActionListener(okButtonActionListener);

		ActionListener cancelButtonActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent prodActionEvent) {
				setVisible(false);
				dispose();
				/*				
				//this is a hack, same as clear method in gui
				if (LocalConfig.getInstance().getCurrentConnection() != null) {
					try {
						LocalConfig.getInstance().getCurrentConnection().close();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				try {
					Class.forName("org.sqlite.JDBC");       
					DatabaseCreator databaseCreator = new DatabaseCreator();
					LocalConfig.getInstance().setDatabaseName(ConfigConstants.DEFAULT_DATABASE_NAME);
					Connection con = DriverManager.getConnection("jdbc:sqlite:" + ConfigConstants.DEFAULT_DATABASE_NAME + ".db");
					LocalConfig.getInstance().setCurrentConnection(con);
					databaseCreator.createDatabase(LocalConfig.getInstance().getDatabaseName());
					databaseCreator.addRows(LocalConfig.getInstance().getDatabaseName(), GraphicalInterfaceConstants.BLANK_DB_METABOLITE_ROW_COUNT, GraphicalInterfaceConstants.BLANK_DB_REACTION_ROW_COUNT);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				*/
			}
		};

		cancelButton.addActionListener(cancelButtonActionListener);

		ActionListener prevRowButtonActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent prodActionEvent) {				
				TextMetabolitesModelReader reader = new TextMetabolitesModelReader();
				int correction = LocalConfig.getInstance().getMetabolitesNextRowCorrection();
				if (correction > 0) {
					LocalConfig.getInstance().setMetabolitesNextRowCorrection(correction - 1);	
					rowLabel.setText("   row " + (LocalConfig.getInstance().getMetabolitesNextRowCorrection() + 1));
					ArrayList<String> newColumnNamesFromFile = reader.columnNamesFromFile(LocalConfig.getInstance().getMetabolitesCSVFile(), LocalConfig.getInstance().getMetabolitesNextRowCorrection());
					setColumnNamesFromFile(newColumnNamesFromFile);
					populateNamesFromFileBoxes(newColumnNamesFromFile);
				} else {
					prevRowButton.setEnabled(false);
				}
				nextRowButton.setEnabled(true);
			}
		};
		
		prevRowButton.addActionListener(prevRowButtonActionListener);
		
		ActionListener nextRowButtonActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent prodActionEvent) {				
				TextMetabolitesModelReader reader = new TextMetabolitesModelReader();
				int correction = LocalConfig.getInstance().getMetabolitesNextRowCorrection();
				if ((correction + 1) < reader.numberOfLines(LocalConfig.getInstance().getMetabolitesCSVFile())) {
					LocalConfig.getInstance().setMetabolitesNextRowCorrection(correction + 1);	
					rowLabel.setText("   row " + (LocalConfig.getInstance().getMetabolitesNextRowCorrection() + 1));
					ArrayList<String> newColumnNamesFromFile = reader.columnNamesFromFile(LocalConfig.getInstance().getMetabolitesCSVFile(), LocalConfig.getInstance().getMetabolitesNextRowCorrection());
					setColumnNamesFromFile(newColumnNamesFromFile);
					populateNamesFromFileBoxes(newColumnNamesFromFile);
				} else {
					nextRowButton.setEnabled(false);
				}
				prevRowButton.setEnabled(true);
			}
		};

		nextRowButton.addActionListener(nextRowButtonActionListener);

	} 	 

	public void populateNamesFromFileBoxes(ArrayList<String> columnNamesFromFile) {

		LocalConfig.getInstance().setMetaboliteAbbreviationColumnIndex(-1);
		LocalConfig.getInstance().setMetaboliteNameColumnIndex(-1);
		LocalConfig.getInstance().setCompartmentColumnIndex(-1);
		LocalConfig.getInstance().setChargeColumnIndex(-1);
		LocalConfig.getInstance().setBoundaryColumnIndex(-1);
		cbMetaboliteAbbreviation.removeAllItems();
		cbMetaboliteName.removeAllItems();
		cbCharge.removeAllItems();
		cbCompartment.removeAllItems();
		cbBoundary.removeAllItems();
		//add all column names to from file comboboxes
		for (int c = 0; c < columnNamesFromFile.size(); c++) { 
			cbMetaboliteAbbreviation.addItem(columnNamesFromFile.get(c));
			cbMetaboliteName.addItem(columnNamesFromFile.get(c));
			cbCharge.addItem(columnNamesFromFile.get(c));
			cbCompartment.addItem(columnNamesFromFile.get(c));
			cbBoundary.addItem(columnNamesFromFile.get(c));
		}
		cbCompartment.setSelectedIndex(-1);
		cbCharge.setSelectedIndex(-1);
		cbBoundary.setSelectedIndex(-1);
		for (int c = 0; c < columnNamesFromFile.size(); c++) { 
			//filters to match column names from file to required column names in table		
			if((columnNamesFromFile.get(c).toLowerCase()).contains(GraphicalInterfaceConstants.COMPARTMENT_FILTER[0])) {
				cbCompartment.setSelectedIndex(c);
				LocalConfig.getInstance().setCompartmentColumnIndex(c);	
			} else if((columnNamesFromFile.get(c).toLowerCase()).contains(GraphicalInterfaceConstants.CHARGE_FILTER[0]) && !(columnNamesFromFile.get(c).toLowerCase()).contains(GraphicalInterfaceConstants.CHARGE_NOT_FILTER[0])) {
				cbCharge.setSelectedIndex(c);
				LocalConfig.getInstance().setChargeColumnIndex(c);
			} else if((columnNamesFromFile.get(c).toLowerCase()).contains(GraphicalInterfaceConstants.BOUNDARY_FILTER[0])) {
				cbBoundary.setSelectedIndex(c);
				LocalConfig.getInstance().setBoundaryColumnIndex(c);
			}  
		}

		//first two columns are recommended to be column 1 and 2: abbreviation (id), and name
		cbMetaboliteAbbreviation.setSelectedIndex(0);
		LocalConfig.getInstance().setMetaboliteAbbreviationColumnIndex(0);
		cbMetaboliteName.setSelectedIndex(1);	
		LocalConfig.getInstance().setMetaboliteNameColumnIndex(1);
	}

	class Task extends SwingWorker<Void, Void> {

		@Override
		public void done() {

		}

		@Override
		protected Void doInBackground() throws Exception {
			int progress = 0;
			TextMetabolitesModelReader reader = new TextMetabolitesModelReader();
			reader.load(LocalConfig.getInstance().getMetabolitesCSVFile(), LocalConfig.getInstance().getDatabaseName());	
			while (progress < 100) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException ignore) {
				}			
			}
			timer.stop();
			return null;
		}
	}

	class TimeListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			progressBar.progress.setValue(LocalConfig.getInstance().getProgress());
			progressBar.progress.repaint();
			if (LocalConfig.getInstance().getProgress() == 100) {
				progressBar.setVisible(false);
				progressBar.dispose();
			}
		}
	}

	public static void main(String[] args) throws Exception {
		Class.forName("org.sqlite.JDBC");       
		DatabaseCreator databaseCreator = new DatabaseCreator();
		Connection con = DriverManager.getConnection("jdbc:sqlite:" + "untitled" + ".db");

		//based on code from http:stackoverflow.com/questions/6403821/how-to-add-an-image-to-a-jframe-title-bar
		final ArrayList<Image> icons = new ArrayList<Image>(); 
		icons.add(new ImageIcon("images/most16.jpg").getImage()); 
		icons.add(new ImageIcon("images/most32.jpg").getImage());

		ArrayList<String> list = new ArrayList();
		list.add("test1");
		list.add("test2");
		list.add("test3");

		MetaboliteColumnNameInterface frame = new MetaboliteColumnNameInterface(con, list);

		frame.setIconImages(icons);
		frame.setSize(600, 360);
		frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}



