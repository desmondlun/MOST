package edu.rutgers.MOST.presentation;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.xml.stream.XMLStreamException;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.HighlightPredicate;

import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLReader;

import edu.rutgers.MOST.config.ConfigConstants;
import edu.rutgers.MOST.config.LocalConfig;
import edu.rutgers.MOST.data.DatabaseCopier;
import edu.rutgers.MOST.data.DatabaseCreator;
import edu.rutgers.MOST.data.FBAModel;
import edu.rutgers.MOST.data.MetaboliteFactory;
import edu.rutgers.MOST.data.MetabolitesMetaColumnManager;
import edu.rutgers.MOST.data.ModelReaction;
import edu.rutgers.MOST.data.ReactionFactory;
import edu.rutgers.MOST.data.ReactionsMetaColumnManager;
import edu.rutgers.MOST.data.SBMLMetabolite;
import edu.rutgers.MOST.data.SBMLModelReader;
import edu.rutgers.MOST.data.SBMLReaction;
import edu.rutgers.MOST.data.TextMetabolitesModelReader;
import edu.rutgers.MOST.data.TextMetabolitesWriter;
import edu.rutgers.MOST.data.TextReactionsModelReader;
import edu.rutgers.MOST.data.TextReactionsWriter;
import edu.rutgers.MOST.logic.ReactionParser;
import edu.rutgers.MOST.optimization.FBA.Optimize;
import edu.rutgers.MOST.optimization.solvers.GurobiSolver;

import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.*;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.List;
import gurobi.GRB;
import gurobi.GRBException;
import gurobi.GRBVar;

import org.apache.log4j.Logger;

//import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;

import layout.TableLayout;

public class GraphicalInterface extends JFrame {
	//log4j
	static Logger log = Logger.getLogger(GraphicalInterface.class);

	public static JXTable reactionsTable = new JXTable();
	public static JXTable metabolitesTable = new JXTable();
	public static JTextArea outputTextArea = new JTextArea();

	//set tabs south (bottom) = 3
	public JTabbedPane tabbedPane = new JTabbedPane(3); 

	public static DefaultListModel<String> listModel = new DefaultListModel();
	public static FileList fileList = new FileList();
	static JScrollPane fileListPane = new JScrollPane(fileList);	

	private Task task;	
	public final ProgressBar progressBar = new ProgressBar();	
	javax.swing.Timer t = new javax.swing.Timer(1000, new TimeListener());

	public static boolean highlightUnusedMetabolites;	
	public static boolean showPrompt;	

	public static int currentRow;

	public void setCurrentRow(int currentRow){
		this.currentRow = currentRow;
	}

	public static int getCurrentRow() {
		return currentRow;
	}

	public static int currentFileListRow;

	public void setCurrentFileListRow(int currentFileListRow){
		this.currentFileListRow = currentFileListRow;
	}

	public static int getCurrentFileListRow() {
		return currentFileListRow;
	}

	public static int copyColumn;

	public void setCopyColumn(int copyColumn){
		this.copyColumn = copyColumn;
	}

	public static int getCopyColumn() {
		return copyColumn;
	}

    public static int reactionsSortColumnIndex;
	
	public void setReactionsSortColumnIndex(int reactionsSortColumnIndex){
		this.reactionsSortColumnIndex = reactionsSortColumnIndex;
	}
	
	public static int getReactionsSortColumnIndex() {
		return reactionsSortColumnIndex;
	}
	
	public static SortOrder reactionsSortOrder;
	
	public void setReactionsSortOrder(SortOrder reactionsSortOrder){
		this.reactionsSortOrder = reactionsSortOrder;
	}
	
	public static SortOrder getReactionsSortOrder() {
		return reactionsSortOrder;
	}
	
    public static int metabolitesSortColumnIndex;
	
	public void setMetabolitesSortColumnIndex(int metabolitesSortColumnIndex){
		this.metabolitesSortColumnIndex = metabolitesSortColumnIndex;
	}
	
	public static int getMetabolitesSortColumnIndex() {
		return metabolitesSortColumnIndex;
	}
	
	public static SortOrder metabolitesSortOrder;
	
	public void setMetabolitesSortOrder(SortOrder metabolitesSortOrder){
		this.metabolitesSortOrder = metabolitesSortOrder;
	}
	
	public static SortOrder getMetabolitesSortOrder() {
		return metabolitesSortOrder;
	}
	
	public static String databaseName;

	public static void setDatabaseName(String databaseName) {

		LocalConfig localConfig = LocalConfig.getInstance();
		localConfig.setDatabaseName(databaseName);
	}

	public static String getDatabaseName() {
		LocalConfig localConfig = LocalConfig.getInstance();
		return localConfig.getDatabaseName();
	}

	//excel requires a full path at all times
	public static String excelPath;

	public void setExcelPath(String excelPath) {
		this.excelPath = excelPath;
	}

	public static String getExcelPath() {
		return excelPath;
	}

	public static String dbPath;

	public void setDBPath(String dbPath) {
		this.dbPath = dbPath;
	}

	public static String getDBPath() {
		return dbPath;
	}

	public static String dbFilename;

	public void setDBFilename(String dbFilename) {
		this.dbFilename = dbFilename;
	}

	public static String getDBFilename() {
		return dbFilename;
	}

	public static File SBMLFile;

	public void setSBMLFile(File SBMLFile) {
		this.SBMLFile = SBMLFile;
	}

	public static File getSBMLFile() {
		return SBMLFile;
	}

	public static File metabolitesCSVFile;

	public void setMetabolitesCSVFile(File metabolitesCSVFile) {
		this.metabolitesCSVFile = metabolitesCSVFile;
	}

	public static File getMetabolitesCSVFile() {
		return metabolitesCSVFile;
	}

	public static File reactionsCSVFile;

	public void setReactionsCSVFile(File reactionsCSVFile) {
		this.reactionsCSVFile = reactionsCSVFile;
	}

	public static File getReactionsCSVFile() {
		return reactionsCSVFile;
	}

	public static String optimizePath;

	public void setOptimizePath(String optimizePath) {
		this.optimizePath = optimizePath;
	}

	public static String getOptimizePath() {
		return optimizePath;
	}

	public static String reactionString;

	public void setReactionString(String reactionString) {
		this.reactionString = reactionString;
	}

	public static String getReactionString() {
		return reactionString;
	}

	public static ReactionInterface reactionInterface;

	public void setReactionInterface(ReactionInterface reactionInterface) {
		this.reactionInterface = reactionInterface;
	}

	public static ReactionInterface getReactionInterface() {
		return reactionInterface;
	}

	public static Character splitCharacter;

	public void setSplitCharacter(Character splitCharacter) {
		this.splitCharacter = splitCharacter;
	}

	public static Character getSplitCharacter() {
		return splitCharacter;
	}

	public static String extension;

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public static String getExtension() {
		return extension;
	}

	public static String participatingMetabolite;

	public void setParticipatingMetabolite(String participatingMetabolite) {
		this.participatingMetabolite = participatingMetabolite;
	}

	public static String getParticipatingMetabolite() {
		return participatingMetabolite;
	}

	public final JCheckBoxMenuItem highlightUnusedMetabolitesItem = new JCheckBoxMenuItem("Highlight Unused Metabolites");
	
	ArrayList<Image> icons;

	public void setIconsList(ArrayList<Image> icons) {
		this.icons = icons;
	}

	public ArrayList<Image> getIconsList() {
		return icons;
	}    

	public static OutputPopout popout;

	public void setPopout(OutputPopout popout) {
		this.popout = popout;
	}

	public static OutputPopout getPopout() {
		return popout;
	}

	public static ReactionColAddRenameInterface reactionColAddRenameInterface;
	
	public void setReactionColAddRenameInterface(ReactionColAddRenameInterface reactionColAddRenameInterface) {
		this.reactionColAddRenameInterface = reactionColAddRenameInterface;
	}

	public static ReactionColAddRenameInterface getReactionColAddRenameInterface() {
		return reactionColAddRenameInterface;
	}
	
    public static MetaboliteColAddRenameInterface metaboliteColAddRenameInterface;
	
	public void setMetaboliteColAddRenameInterface(MetaboliteColAddRenameInterface metaboliteColAddRenameInterface) {
		this.metaboliteColAddRenameInterface = metaboliteColAddRenameInterface;
	}

	public static MetaboliteColAddRenameInterface getMetaboliteColAddRenameInterface() {
		return metaboliteColAddRenameInterface;
	}
	
	@SuppressWarnings("unchecked")
	public GraphicalInterface(final Connection con)
	throws SQLException {
		
		//System.out.println("max memory " + java.lang.Runtime.getRuntime().maxMemory());

		LocalConfig.getInstance().setProgress(0);
		progressBar.pack();
		progressBar.setIconImages(icons);
		progressBar.setSize(200, 70);
		progressBar.setTitle("Loading...");
		progressBar.progress.setIndeterminate(true);
		progressBar.setVisible(false);

		setDatabaseName(ConfigConstants.DEFAULT_DATABASE_NAME);
		LocalConfig.getInstance().setLoadedDatabase(ConfigConstants.DEFAULT_DATABASE_NAME);

		setTitle(GraphicalInterfaceConstants.TITLE + " - " + ConfigConstants.DEFAULT_DATABASE_NAME);

		final ArrayList<Image> icons = new ArrayList<Image>(); 
		icons.add(new ImageIcon("etc/most16.jpg").getImage()); 
		icons.add(new ImageIcon("etc/most32.jpg").getImage());
		setIconsList(icons);
		
		setReactionsSortColumnIndex(0);
		setMetabolitesSortColumnIndex(0);
		setReactionsSortOrder(SortOrder.ASCENDING);
		setMetabolitesSortOrder(SortOrder.ASCENDING);
		
		listModel.addElement(GraphicalInterfaceConstants.DEFAULT_DATABASE_NAME);
		
		/**************************************************************************/
		//set up fileList
		/**************************************************************************/

		fileList.setModel(listModel);
		fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); 

		fileList.addListSelectionListener(new ListSelectionListener() 
		{ 
			public void valueChanged(ListSelectionEvent e) 
			{  
				fileList.deleteItem.setEnabled(false);
				fileList.clearItem.setEnabled(false);
				if(fileList.getSelectedIndex() == 0) {
					clearOutputPane();
					if (getPopout() != null) {
						getPopout().clear();
					}				  
					String fileString = "jdbc:sqlite:" + getDatabaseName() + ".db";
					LocalConfig.getInstance().setLoadedDatabase(getDatabaseName());
					try {
						Class.forName("org.sqlite.JDBC");
						Connection con = DriverManager.getConnection(fileString);			    
						highlightUnusedMetabolites = false;
						highlightUnusedMetabolitesItem.setState(false);
						setUpMetabolitesTable(con);
						setUpReactionsTable(con);
						setTitle(GraphicalInterfaceConstants.TITLE + " - " + getDatabaseName());	
					} catch (ClassNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (SQLException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}	
				} else {
					if (fileList.getSelectedValue() != null) {
						//gets the full path of optimize
						String optimizePath = getOptimizePath();
						if (optimizePath.contains("\\")) {
							optimizePath = optimizePath.substring(0, optimizePath.lastIndexOf("\\") + 1) + fileList.getSelectedValue().toString();
						} else {
							optimizePath = fileList.getSelectedValue().toString();
						}
						setOptimizePath(optimizePath);
						if (getOptimizePath().endsWith(fileList.getSelectedValue().toString())) {
							loadOutputPane(getOptimizePath() + ".log");
							if (getPopout() != null) {
								getPopout().load(getOptimizePath() + ".log");
							}	

							String fileString = "jdbc:sqlite:" + getOptimizePath() + ".db";
							LocalConfig.getInstance().setLoadedDatabase(getOptimizePath());
							try {
								Class.forName("org.sqlite.JDBC");
								Connection con = DriverManager.getConnection(fileString);			    
								highlightUnusedMetabolites = false;
								highlightUnusedMetabolitesItem.setState(false);
								setUpMetabolitesTable(con);
								setUpReactionsTable(con);
								setTitle(GraphicalInterfaceConstants.TITLE + " - " + getOptimizePath());	
							} catch (ClassNotFoundException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (SQLException e2) {
								// TODO Auto-generated catch block
								e2.printStackTrace();
							}			  
						} 
					}

				}        	  
			} 
		});   

		fileList.deleteItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) { 
				String fileString = "jdbc:sqlite:" + getDatabaseName() + ".db";
				LocalConfig.getInstance().setLoadedDatabase(getDatabaseName());
				try {
					Class.forName("org.sqlite.JDBC");
					Connection con = DriverManager.getConnection(fileString);			    
					setUpMetabolitesTable(con);
					setUpReactionsTable(con);
					setTitle(GraphicalInterfaceConstants.TITLE + " - " + getDatabaseName());
					clearOutputPane();
				} catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (SQLException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}				
			}
		});

		fileList.clearItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) { 
				setUpTables();				
			}
		});

		final JPopupMenu outputPopupMenu = new JPopupMenu(); 
		JMenuItem popOutItem = new JMenuItem("Pop Out");
		outputPopupMenu.add(popOutItem);
		popOutItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) { 	
				OutputPopout popout = new OutputPopout();
				setPopout(popout);
				if (getOptimizePath() != null) {
					popout.load(getOptimizePath() + ".log");
				}							
			}
		});

		outputTextArea.addMouseListener(new MouseAdapter() {

			public void mousePressed(MouseEvent e)  {check(e);}
			public void mouseReleased(MouseEvent e) {check(e);}

			public void check(MouseEvent e) {
				if (e.isPopupTrigger()) { //if the event shows the menu
					outputPopupMenu.show(outputTextArea, e.getX(), e.getY()); 
				}
			}
		});	

		/**************************************************************************/
		//create menu bar
		/**************************************************************************/
		JMenuBar menuBar = new JMenuBar();

		setJMenuBar(menuBar);

		JMenu modelMenu = new JMenu("Model");
		modelMenu.setMnemonic(KeyEvent.VK_M);

		JMenuItem loadSBMLItem = new JMenuItem("Load SBML");
		modelMenu.add(loadSBMLItem);
		loadSBMLItem.setMnemonic(KeyEvent.VK_L);
		loadSBMLItem.addActionListener(new LoadSBMLAction());

		JMenuItem loadCSVMetabolitesItem = new JMenuItem("Load CSV Metabolites");
		modelMenu.add(loadCSVMetabolitesItem);
		loadCSVMetabolitesItem.setToolTipText("Metabolites File must be loaded before Reactions File");
		loadCSVMetabolitesItem.setMnemonic(KeyEvent.VK_C);
		loadCSVMetabolitesItem.addActionListener(new LoadCSVMetabolitesTableAction());

		JMenuItem loadCSVReactionsItem = new JMenuItem("Load CSV Reactions");
		modelMenu.add(loadCSVReactionsItem);
		loadCSVReactionsItem.setToolTipText("Metabolites File must be loaded before Reactions File");
		loadCSVReactionsItem.setMnemonic(KeyEvent.VK_R);
		loadCSVReactionsItem.addActionListener(new LoadCSVReactionsTableAction());

		JMenuItem loadSQLItem = new JMenuItem("Load SQLite");
		modelMenu.add(loadSQLItem);
		loadSQLItem.setMnemonic(KeyEvent.VK_Q);
		loadSQLItem.addActionListener(new LoadSQLiteItemAction());
		
		modelMenu.addSeparator();

		JMenuItem saveCSVMetabolitesItem = new JMenuItem("Save As CSV Metabolites");
		modelMenu.add(saveCSVMetabolitesItem);
		saveCSVMetabolitesItem.setMnemonic(KeyEvent.VK_O);
		saveCSVMetabolitesItem.addActionListener(new SaveCSVMetabolitesItemAction());

		JMenuItem saveCSVReactionsItem = new JMenuItem("Save As CSV Reactions");
		modelMenu.add(saveCSVReactionsItem);
		saveCSVReactionsItem.setMnemonic(KeyEvent.VK_N);
		saveCSVReactionsItem.addActionListener(new SaveCSVReactionsItemAction());

		JMenuItem saveSQLiteItem = new JMenuItem("Save As SQLite");
		modelMenu.add(saveSQLiteItem);
		saveSQLiteItem.setMnemonic(KeyEvent.VK_A);
		saveSQLiteItem.addActionListener(new SaveSQLiteItemAction());

		modelMenu.addSeparator();

		JMenuItem clearItem = new JMenuItem("Clear");
		modelMenu.add(clearItem);
		clearItem.setMnemonic(KeyEvent.VK_E);
		clearItem.addActionListener(new ClearAction());

		menuBar.add(modelMenu);

		//Simulate menu
		JMenu simulateMenu = new JMenu("Simulate");
		simulateMenu.setMnemonic(KeyEvent.VK_S);

		JMenuItem fbaItem = new JMenuItem("FBA");
		simulateMenu.add(fbaItem);

		fbaItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {
                //load original db into tables
				fileList.removeSelectionInterval(0, listModel.size());
				String fileString1 = "jdbc:sqlite:" + getDatabaseName() + ".db";
				LocalConfig.getInstance().setLoadedDatabase(getDatabaseName());
				try {
					Class.forName("org.sqlite.JDBC");
					Connection con = DriverManager.getConnection(fileString1);			    
					highlightUnusedMetabolites = false;
					highlightUnusedMetabolitesItem.setState(false);
					setUpMetabolitesTable(con);
					setUpReactionsTable(con);
					setTitle(GraphicalInterfaceConstants.TITLE + " - " + getDatabaseName());	
				} catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (SQLException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}

				Date date = new Date();
				Format formatter;
				formatter = new SimpleDateFormat("_yyMMdd_HHmmss");
				String dateTimeStamp = formatter.format(date);

				DatabaseCopier copier = new DatabaseCopier();
				String optimizePath = "";
				if (getDatabaseName().contains("\\")) {
					optimizePath = (getDatabaseName().substring(0,
							getDatabaseName().lastIndexOf("\\") + 1))
							+ GraphicalInterfaceConstants.OPTIMIZATION_PREFIX
							+ (getDatabaseName().substring(
									getDatabaseName().lastIndexOf("\\") + 1) + dateTimeStamp);
				} else {
					optimizePath = GraphicalInterfaceConstants.OPTIMIZATION_PREFIX
					+ getDatabaseName() + dateTimeStamp;
				}

				copier.copyDatabase(getDatabaseName(), optimizePath);
				listModel
				.addElement(GraphicalInterfaceConstants.OPTIMIZATION_PREFIX
						+ (getDatabaseName()
								.substring(
										getDatabaseName().lastIndexOf(
												"\\") + 1) + dateTimeStamp));

				
				setOptimizePath(optimizePath);

               
				// DEGEN: Begin optimization

				ReactionFactory aFactory = new ReactionFactory();
				Vector<ModelReaction> reactions = aFactory.getAllReactions(
						"SBML", getDatabaseName());
				FBAModel model = new FBAModel();
				for (int i = 0; i < reactions.size(); i++) {
					model.addReaction(reactions.elementAt(i));
				}
				Vector<Integer> objReactions = aFactory.getObjectiveFunctions(
						"SBML", getDatabaseName());

				model.setBiologicalObjective(objReactions);
				log.debug("create an optimize");

				Optimize opt = new Optimize();
				opt.setDatabaseName(getOptimizePath());// should be optimizePath
														// once the copier is
														// implemented
				opt.setFBAModel(model);
				log.debug("about to optimize");
				opt.optimize();
				log.debug("optimization complete");
				//End optimization
				StringBuffer outputText = new StringBuffer();
				List<GRBVar> vars = ((GurobiSolver) opt.getSolver()).getVars();
				for (int i = 0; i < vars.size(); i++) {
					// This only works for a Gurobi solver
					try {
						Integer reactionId = Integer.valueOf(vars.get(i).get(
								GRB.StringAttr.VarName));
						SBMLReaction aReaction = (SBMLReaction) aFactory
								.getReactionById(reactionId, "SBML",
										getOptimizePath());
						outputText.append("\nReaction:"
								+ aReaction.getReactionAbbreviation()
								+ " Flux: " + vars.get(i).get(GRB.DoubleAttr.X));

						//DB Update: Set the flux of the reaction based on the optimization
							aReaction.setFluxValue(vars.get(i).get(GRB.DoubleAttr.X));
							aReaction.update();
						//End DB Update
					} catch (GRBException ex) {
						ex.printStackTrace();

					}
				}

				Writer writer = null;
				try {

					outputText.append("\nmax objective flux:"
							+ opt.getmaxFlux());
					File file = new File(optimizePath + ".log");
					writer = new BufferedWriter(new FileWriter(file));
					writer.write(outputText.toString());

				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						if (writer != null) {
							writer.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});

		
		menuBar.add(simulateMenu);

		//Edit menu
		JMenu editMenu = new JMenu("Edit");
		editMenu.setMnemonic(KeyEvent.VK_E);

		highlightUnusedMetabolitesItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				boolean state = highlightUnusedMetabolitesItem.getState();
				if (state == true) {
					highlightUnusedMetabolites = true;
				} else {
					highlightUnusedMetabolites = false;
				}
				String fileString = "jdbc:sqlite:" + LocalConfig.getInstance().getLoadedDatabase() + ".db";
				try {
					Class.forName("org.sqlite.JDBC");
					Connection con = DriverManager.getConnection(fileString);
					setUpMetabolitesTable(con);
					setUpReactionsTable(con);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}   
			}
		});
		editMenu.add(highlightUnusedMetabolitesItem);

		JMenuItem deleteUnusedItem = new JMenuItem("Delete All Unused Metabolites");
		editMenu.add(deleteUnusedItem);
		deleteUnusedItem.setMnemonic(KeyEvent.VK_D);

		deleteUnusedItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				MetaboliteFactory mFactory = new MetaboliteFactory();
				mFactory.deleteAllUnusedMetabolites(LocalConfig.getInstance().getLoadedDatabase());
				try {
					String fileString = "jdbc:sqlite:" + LocalConfig.getInstance().getLoadedDatabase() + ".db";
					Class.forName("org.sqlite.JDBC");
					Connection con = DriverManager.getConnection(fileString);					    	
					setUpMetabolitesTable(con);	
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				highlightUnusedMetabolites = false;
				highlightUnusedMetabolitesItem.setState(false);
			}
		});   

		editMenu.addSeparator(); 

		JMenuItem addReacRowItem = new JMenuItem("Add Row to Reactions Table");
		editMenu.add(addReacRowItem);
		addReacRowItem.setMnemonic(KeyEvent.VK_R);

		ActionListener addReacColSubmitButtonActionListener = new ActionListener() {
		//addReacRowItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				DatabaseCreator creator = new DatabaseCreator();
				creator.addReactionRow(LocalConfig.getInstance().getLoadedDatabase());
				String fileString = "jdbc:sqlite:" + LocalConfig.getInstance().getLoadedDatabase() + ".db";
				try {
					Class.forName("org.sqlite.JDBC");
					Connection con = DriverManager.getConnection(fileString);
					setUpReactionsTable(con);					
					//set focus to id cell in new row in order to set row visible
					int id = reactionsTable.getModel().getRowCount();
					int viewRow = GraphicalInterface.reactionsTable.convertRowIndexToView(id - 1);
					reactionsTable.changeSelection(viewRow, 0, false, false);
					reactionsTable.requestFocus();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};  
		
		addReacRowItem.addActionListener(addReacColSubmitButtonActionListener);

		JMenuItem addMetabRowItem = new JMenuItem("Add Row to Metabolites Table");      
		editMenu.add(addMetabRowItem); 
		addMetabRowItem.setMnemonic(KeyEvent.VK_M);

		addMetabRowItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				DatabaseCreator creator = new DatabaseCreator();
				creator.addMetaboliteRow(LocalConfig.getInstance().getLoadedDatabase());
				String fileString = "jdbc:sqlite:" + LocalConfig.getInstance().getLoadedDatabase() + ".db";
				try {
					Class.forName("org.sqlite.JDBC");
					Connection con = DriverManager.getConnection(fileString);
					setUpMetabolitesTable(con);
					
					//set focus to id cell in new row in order to set row visible
					int id = metabolitesTable.getModel().getRowCount();
					int viewRow = GraphicalInterface.metabolitesTable.convertRowIndexToView(id - 1);
					metabolitesTable.changeSelection(viewRow, 0, false, false);
					metabolitesTable.requestFocus();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		editMenu.addSeparator();
		
		JMenuItem addReacColumnItem = new JMenuItem("Add Column to Reactions Table");
		editMenu.add(addReacColumnItem);
		addReacColumnItem.setMnemonic(KeyEvent.VK_C);
		
		addReacColumnItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				String fileString = "jdbc:sqlite:" + LocalConfig.getInstance().getLoadedDatabase() + ".db";
				try {
					Class.forName("org.sqlite.JDBC");
					Connection con = DriverManager.getConnection(fileString);			    
					ReactionColAddRenameInterface reactionColAddRenameInterface = new ReactionColAddRenameInterface(con);
					setReactionColAddRenameInterface(reactionColAddRenameInterface);
					reactionColAddRenameInterface.setTitle(GraphicalInterfaceConstants.COLUMN_ADD_INTERFACE_TITLE);
					reactionColAddRenameInterface.setIconImages(icons);
					reactionColAddRenameInterface.setSize(350, 170);
					reactionColAddRenameInterface.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
					reactionColAddRenameInterface.setLocationRelativeTo(null);
					reactionColAddRenameInterface.setVisible(true);					
				} catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (SQLException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
			}
		});
		
		ActionListener addColSubmitButtonActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent prodActionEvent) {
				String fileString = "jdbc:sqlite:" + LocalConfig.getInstance().getLoadedDatabase() + ".db";
				try {
					getReactionColAddRenameInterface().addColumnToMeta(LocalConfig.getInstance().getLoadedDatabase());
					getReactionColAddRenameInterface().textField.setText("");
					getReactionColAddRenameInterface().setVisible(false);
					getReactionColAddRenameInterface().dispose();
					Class.forName("org.sqlite.JDBC");
					Connection con = DriverManager.getConnection(fileString);
					setUpReactionsTable(con);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		
		reactionColAddRenameInterface.submitButton.addActionListener(addColSubmitButtonActionListener);
		
		JMenuItem addMetabColumnItem = new JMenuItem("Add Column to Metabolites Table");
		editMenu.add(addMetabColumnItem);
		addMetabColumnItem.setMnemonic(KeyEvent.VK_O);
		
		addMetabColumnItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				String fileString = "jdbc:sqlite:" + LocalConfig.getInstance().getLoadedDatabase() + ".db";
				try {
					Class.forName("org.sqlite.JDBC");
					Connection con = DriverManager.getConnection(fileString);			    
					MetaboliteColAddRenameInterface metaboliteColAddRenameInterface = new MetaboliteColAddRenameInterface(con);
					setMetaboliteColAddRenameInterface(metaboliteColAddRenameInterface);
					metaboliteColAddRenameInterface.setTitle(GraphicalInterfaceConstants.COLUMN_ADD_INTERFACE_TITLE);
					metaboliteColAddRenameInterface.setIconImages(icons);
					metaboliteColAddRenameInterface.setSize(350, 170);
					metaboliteColAddRenameInterface.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
					metaboliteColAddRenameInterface.setLocationRelativeTo(null);
					metaboliteColAddRenameInterface.setVisible(true);					
				} catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (SQLException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
			}
		});
		
		ActionListener addMetabColSubmitButtonActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent prodActionEvent) {
				String fileString = "jdbc:sqlite:" + LocalConfig.getInstance().getLoadedDatabase() + ".db";
				try {
					getMetaboliteColAddRenameInterface().addColumnToMeta(LocalConfig.getInstance().getLoadedDatabase());
					getMetaboliteColAddRenameInterface().textField.setText("");
					getMetaboliteColAddRenameInterface().setVisible(false);
					getMetaboliteColAddRenameInterface().dispose();
					Class.forName("org.sqlite.JDBC");
					Connection con = DriverManager.getConnection(fileString);
					setUpMetabolitesTable(con);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		
		metaboliteColAddRenameInterface.submitButton.addActionListener(addMetabColSubmitButtonActionListener);
		
		menuBar.add(editMenu);

		//Optimize menu
		JMenu optimizeMenu = new JMenu("Optimize");
		optimizeMenu.setMnemonic(KeyEvent.VK_O);

		JMenuItem gdbbItem = new JMenuItem("GDBB");
		optimizeMenu.add(gdbbItem);

		menuBar.add(optimizeMenu);

		JMenu helpMenu = new JMenu("Help");
		helpMenu.setMnemonic(KeyEvent.VK_H);

		JMenuItem viewHelpTopics = new JMenuItem("View Help Topics");
		helpMenu.add(viewHelpTopics);
		viewHelpTopics.setMnemonic(KeyEvent.VK_H);

		viewHelpTopics.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {
				java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
				if( !desktop.isSupported( java.awt.Desktop.Action.BROWSE ) ) {
					//System.err.println( "Desktop doesn't support the browse action (fatal)" );
					//System.exit( 1 );
					JOptionPane.showMessageDialog(null,                
							"Default Browser Error. Default Browser May Not Be Set On This System.",                
							"Default Browser Error",                                
							JOptionPane.ERROR_MESSAGE); 
				}

				try{ 
					String url = GraphicalInterfaceConstants.HELP_TOPICS_URL;  
					java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));  
				}  
				catch (java.io.IOException e) {  
					JOptionPane.showMessageDialog(null,                
							"This URL may not exist. Check internet connection.",                
							"URL Not Found",                                
							JOptionPane.ERROR_MESSAGE);   
				}
			}    	     
		});

		JMenuItem aboutBox = new JMenuItem("About MOST");
		helpMenu.add(aboutBox);
		aboutBox.setMnemonic(KeyEvent.VK_A);

		aboutBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {
				JOptionPane.showMessageDialog(null,                
						"MOST - Metabolic Optimization and Simulation Tool." +
						" Version 1.0.0",                
						"About MOST",                                
						JOptionPane.INFORMATION_MESSAGE);
			}    	     
		});

		menuBar.add(helpMenu);

		/**************************************************************************/
		//end menu bar
		/**************************************************************************/

		/**************************************************************************/
		//create blank tables, set models and layouts
		/**************************************************************************/
		
		ActionListener reactionsCopyActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				reactionsCopy();
			}
		};
		
		ActionListener reactionsPasteActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				reactionsPaste();
			}
		};
		
		ActionListener reactionsClearActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				reactionsClear();
			}
		};
		
		KeyStroke reacCopy = KeyStroke.getKeyStroke(KeyEvent.VK_C,ActionEvent.CTRL_MASK,false);       
		KeyStroke reacPaste = KeyStroke.getKeyStroke(KeyEvent.VK_V,ActionEvent.CTRL_MASK,false); 
		KeyStroke reacClear = KeyStroke.getKeyStroke(KeyEvent.VK_E,ActionEvent.CTRL_MASK,false); 
		
		setUpReactionsTable(con);
		TableCellListener tcl = new TableCellListener(reactionsTable, rAction);
		ReactionsPopupListener reactionsPopupListener = new ReactionsPopupListener();
		reactionsTable.addMouseListener(reactionsPopupListener);
		reactionsTable.setRowHeight(20);
		reactionsTable.registerKeyboardAction(reactionsCopyActionListener,reacCopy,JComponent.WHEN_FOCUSED); 
		reactionsTable.registerKeyboardAction(reactionsPasteActionListener,reacPaste,JComponent.WHEN_FOCUSED); 
		reactionsTable.registerKeyboardAction(reactionsClearActionListener,reacClear,JComponent.WHEN_FOCUSED); 
		
		ActionListener metabolitesCopyActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				metabolitesCopy();
			}
		};
		
		ActionListener metabolitesPasteActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				metabolitesPaste();
			}
		};
		
		ActionListener metabolitesClearActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				metabolitesClear();
			}
		};
		
		KeyStroke metabCopy = KeyStroke.getKeyStroke(KeyEvent.VK_C,ActionEvent.CTRL_MASK,false);       
		KeyStroke metabPaste = KeyStroke.getKeyStroke(KeyEvent.VK_V,ActionEvent.CTRL_MASK,false);
		KeyStroke metabClear = KeyStroke.getKeyStroke(KeyEvent.VK_E,ActionEvent.CTRL_MASK,false);
		
		setUpMetabolitesTable(con);
		TableCellListener mtcl = new TableCellListener(metabolitesTable, mAction);
		MetabolitesPopupListener metabolitesPopupListener = new MetabolitesPopupListener();
		metabolitesTable.addMouseListener(metabolitesPopupListener);
		metabolitesTable.setRowHeight(20);
		metabolitesTable.registerKeyboardAction(metabolitesCopyActionListener,metabCopy,JComponent.WHEN_FOCUSED); 
		metabolitesTable.registerKeyboardAction(metabolitesPasteActionListener,metabPaste,JComponent.WHEN_FOCUSED); 
		metabolitesTable.registerKeyboardAction(metabolitesClearActionListener,metabClear,JComponent.WHEN_FOCUSED); 

		/************************************************************************/
		//end blank tables, set models and layouts
		/************************************************************************/

		/************************************************************************/
		//set frame layout 
		/************************************************************************/

		JScrollPane scrollPaneReac = new JScrollPane(reactionsTable);
		tabbedPane.addTab(GraphicalInterfaceConstants.DEFAULT_REACTION_TABLE_TAB_NAME, scrollPaneReac);
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_R);

		JScrollPane scrollPaneMetab = new JScrollPane(metabolitesTable);
		tabbedPane.addTab(GraphicalInterfaceConstants.DEFAULT_METABOLITE_TABLE_TAB_NAME, scrollPaneMetab);
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_B);  	  

		JScrollPane outputPane = new JScrollPane(outputTextArea);

		double border = 10;
		double size[][] =
		{{border, TableLayout.FILL, 20, 0.20, border},  //Columns
				{border, TableLayout.FILL, 20, 0.20, border}}; // Rows  

		setLayout (new TableLayout(size)); 

		add (tabbedPane, "1, 1, 1, 1"); // Left
		add (fileListPane, "3, 1, 1, 3"); // Right
		add (outputPane, "1, 3, 1, 1"); // Bottom

		setBackground(Color.lightGray);
	}
	/********************************************************************************/
	//end layout
	/********************************************************************************/

	/********************************************************************************/
	//methods
	/********************************************************************************/

	public void positionColumn(JXTable table,int col_Index) {
		table.moveColumn(table.getColumnCount()-1, col_Index);
	}

	public void selectCell(int row,int col, JXTable table)
	{
		if(row!=-1 && col !=-1)            
		{
			table.setRowSelectionInterval(row,row);
			table.setColumnSelectionInterval(col,col);
		}
	}

	//from http://www.roseindia.net/java/example/java/swing/ChangeColumnName.shtml  
	public void ChangeName(JXTable table, int col_index, String col_name){
		table.getColumnModel().getColumn(col_index).setHeaderValue(col_name);
	}
	/*******************************************************************************/
	//end methods
	/*******************************************************************************/

	/*******************************************************************************/
	//load methods and actions
	/*******************************************************************************/ 
	class LoadSBMLAction implements ActionListener {
		public void actionPerformed(ActionEvent ae) { 
			progressBar.progress.setValue(0);
			LocalConfig.getInstance().setProgress(0);	  
			loadSetUp();
			JTextArea output = null;
			JFileChooser fileChooser = new JFileChooser(); 
			//... Open a file dialog.
			int retval = fileChooser.showOpenDialog(output);
			if (retval == JFileChooser.APPROVE_OPTION) {
				//... The user selected a file, get it, use it.
				File file = fileChooser.getSelectedFile();          	
				String rawFilename = fileChooser.getSelectedFile().getName();
				String filename = "";
				if (!rawFilename.endsWith(".xml") && !rawFilename.endsWith(".sbml")) {
					JOptionPane.showMessageDialog(null,                
							"Not a Valid SBML File.",                
							"Invalid SBML File",                                
							JOptionPane.ERROR_MESSAGE);
				} else {
					fileList.setSelectedIndex(-1);
					listModel.clear();
					fileList.setModel(listModel);
					if (rawFilename.endsWith(".xml")) {
						filename = rawFilename.substring(0, rawFilename.length() - 4);
					} else {
						filename = rawFilename.substring(0, rawFilename.length() - 5);
					}
					setSBMLFile(file);
					setDatabaseName(filename); 
					LocalConfig.getInstance().setLoadedDatabase(filename);
					LocalConfig.getInstance().setProgress(0);
					progressBar.setVisible(true);

					t.start();

					task = new Task();
					task.execute();
				}

			}
		}
	}

	class LoadSQLiteItemAction implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			loadSetUp();
			JTextArea output = null;
			JFileChooser fileChooser = new JFileChooser();
			//... Open a file dialog.
			int retval = fileChooser.showOpenDialog(output);
			if (retval == JFileChooser.APPROVE_OPTION) {
				//... The user selected a file, get it, use it.
				String rawFilename = fileChooser.getSelectedFile().getName();
				if (!rawFilename.endsWith(".db")) {
					JOptionPane.showMessageDialog(null,                
							"Not a Valid Database File.",                
							"Invalid Database File",                                
							JOptionPane.ERROR_MESSAGE);
				} else {
					fileList.setSelectedIndex(-1);
					listModel.clear();
					fileList.setModel(listModel);
					String filename = rawFilename.substring(0, rawFilename.length() - 3);
					String rawPath = fileChooser.getSelectedFile().getPath();
					String path = rawPath.substring(0, rawPath.length() - 3);
					setDatabaseName(path);
					LocalConfig.getInstance().setLoadedDatabase(path);
					setUpTables(); 
				}

			}
		}
	}

	public void loadMetabolitesTextFile() {
		loadSetUp();
		JTextArea output = null;
		JFileChooser fileChooser = new JFileChooser();
		//... Open a file dialog.
		int retval = fileChooser.showOpenDialog(output);
		if (retval == JFileChooser.APPROVE_OPTION) {
			//... The user selected a file, get it, use it.
			File file = fileChooser.getSelectedFile();    	    	
			String rawFilename = fileChooser.getSelectedFile().getName();
			if (!rawFilename.endsWith(".csv")) {
				JOptionPane.showMessageDialog(null,                
						"Not a Valid CSV File.",                
						"Invalid CSV File",                                
						JOptionPane.ERROR_MESSAGE);
			} else {
				fileList.setSelectedIndex(-1);
				listModel.clear();
				fileList.setModel(listModel);
				setMetabolitesCSVFile(file);
				String filename = rawFilename.substring(0, rawFilename.length() - 4);      	
				setDatabaseName(filename);
				LocalConfig.getInstance().setLoadedDatabase(filename);
				try {
					String fileString = "jdbc:sqlite:" + getDatabaseName() + ".db";
					Class.forName("org.sqlite.JDBC");
					Connection con = DriverManager.getConnection(fileString);

					LocalConfig.getInstance().setMetabolitesNextRowCorrection(0);

					TextMetabolitesModelReader reader = new TextMetabolitesModelReader();
					ArrayList<String> columnNamesFromFile = reader.columnNamesFromFile(file, 0);
					MetaboliteColumnNameInterface columnNameInterface = new MetaboliteColumnNameInterface(con, columnNamesFromFile);

					columnNameInterface.setModal(true);
					columnNameInterface.setIconImages(icons);

					columnNameInterface.setSize(600, 360);
					columnNameInterface.setResizable(false);
					columnNameInterface.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
					columnNameInterface.setLocationRelativeTo(null);
					columnNameInterface.setVisible(true);
					columnNameInterface.setAlwaysOnTop(true);

				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}		
			}

		}	  
	}       

	public void loadReactionsTextFile() {
		loadSetUp();
		JTextArea output = null;
		JFileChooser fileChooser = new JFileChooser();
		//... Open a file dialog.
		int retval = fileChooser.showOpenDialog(output);
		if (retval == JFileChooser.APPROVE_OPTION) {
			//... The user selected a file, get it, use it.
			File file = fileChooser.getSelectedFile();
			String rawFilename = fileChooser.getSelectedFile().getName();
			if (!rawFilename.endsWith(".csv") && !rawFilename.endsWith(".txt")) {
				if (!rawFilename.endsWith(".csv")) {
					JOptionPane.showMessageDialog(null,                
							"Not a Valid CSV File.",                
							"Invalid CSV File",                                
							JOptionPane.ERROR_MESSAGE);
				}

				
			} else {
				fileList.setSelectedIndex(-1);
				listModel.clear();
				fileList.setModel(listModel);
				setReactionsCSVFile(file);
				try {
					String fileString = "jdbc:sqlite:" + getDatabaseName() + ".db";
					Class.forName("org.sqlite.JDBC");
					Connection con = DriverManager.getConnection(fileString);

					LocalConfig.getInstance().setReactionsNextRowCorrection(0);

					TextReactionsModelReader reader = new TextReactionsModelReader();			    
					ArrayList<String> columnNamesFromFile = reader.columnNamesFromFile(file, 0);
					ReactionColumnNameInterface columnNameInterface = new ReactionColumnNameInterface(con, columnNamesFromFile);

					columnNameInterface.setModal(true);
					columnNameInterface.setIconImages(icons);

					columnNameInterface.setSize(600, 510);
					columnNameInterface.setResizable(false);
					columnNameInterface.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
					columnNameInterface.setLocationRelativeTo(null);
					columnNameInterface.setVisible(true);	
					columnNameInterface.setAlwaysOnTop(true);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}         		
		}	  
	}  


	class LoadCSVMetabolitesTableAction implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			setSplitCharacter(',');
			setExtension(".csv");
			loadMetabolitesTextFile();
			t.start();
		}
	}

	class LoadCSVReactionsTableAction implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			setSplitCharacter(',');
			setExtension(".csv");
			loadReactionsTextFile();
			t.start();
		}
	}

	/*******************************************************************************/
	//save methods and actions
	/*******************************************************************************/
	
	class SaveCSVMetabolitesItemAction implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			setSplitCharacter(',');
			saveMetabolitesTextFileChooser(); 	  
		}
	}

	public void saveMetabolitesTextFile(String path, String filename) {
		String oldName = getDatabaseName();
		TextMetabolitesWriter writer = new TextMetabolitesWriter();
		writer.write(path, LocalConfig.getInstance().getLoadedDatabase());				    
		setTitle(GraphicalInterfaceConstants.TITLE + " - " + filename);  
		fileList.setSelectedIndex(-1);
		//list holds dateTime stamps from items in old fileList
		ArrayList<String> suffixList = new ArrayList();
		for (int i = 1; i < listModel.size(); i++) {
			//length of dateTime stamp is 14
			String suffix = listModel.get(i).substring(listModel.get(i).length() - 14);
			suffixList.add(suffix);
		}
		listModel.clear();
		listModel.addElement(filename);
		DatabaseCopier copier = new DatabaseCopier();
		//copies files and assigns new names based on name of saved file to
		//refresh fileList with new names
		for (int i = 0; i < suffixList.size(); i++) {
			listModel.addElement(GraphicalInterfaceConstants.OPTIMIZATION_PREFIX + filename + suffixList.get(i));
			copier.copyDatabase(GraphicalInterfaceConstants.OPTIMIZATION_PREFIX + oldName + suffixList.get(i), GraphicalInterfaceConstants.OPTIMIZATION_PREFIX + filename + suffixList.get(i));
			copier.copyLogFile(GraphicalInterfaceConstants.OPTIMIZATION_PREFIX + oldName + suffixList.get(i), GraphicalInterfaceConstants.OPTIMIZATION_PREFIX + filename + suffixList.get(i));
		}

		fileList.setModel(listModel);
		clearOutputPane();
	}

	public void saveMetabolitesTextFileChooser() {
		JTextArea output = null;
		JFileChooser fileChooser = new JFileChooser(new File(getDatabaseName()));
		boolean done = false;
		while (!done) {
			//... Open a file dialog.
			int retval = fileChooser.showSaveDialog(output);
			if (retval == JFileChooser.CANCEL_OPTION) {
				done = true;
			}
			if (retval == JFileChooser.APPROVE_OPTION) {            	  
				//... The user selected a file, get it, use it.
				String path = fileChooser.getSelectedFile().getPath();
				String filename = fileChooser.getSelectedFile().getName();
				//checks if filename endswith .csv else renames file to end with .csv
				if (!path.endsWith(".csv")) {
					path = path + ".csv";
				}

				File file = new File(path);
				if (path == null) {
					done = true;
				} else {        	    	  
					if (file.exists()) {
						int confirmDialog = JOptionPane.showConfirmDialog(fileChooser, "Replace existing file?");
						if (confirmDialog == JOptionPane.YES_OPTION) {
							done = true;

							saveMetabolitesTextFile(path, filename);

						} else if (confirmDialog == JOptionPane.NO_OPTION) {        		    	  
							done = false;
						} else {
							done = true;
						}       		    	  
					} else {
						done = true;

						saveMetabolitesTextFile(path, filename);
					}
				}			                  	  
			}
		}
	}

	class SaveCSVReactionsItemAction implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			setSplitCharacter(',');
			saveReactionsTextFileChooser();  
		}
	}

	public void saveReactionsTextFile(String path, String filename) {
		String oldName = getDatabaseName();
		TextReactionsWriter writer = new TextReactionsWriter();
		writer.write(path, LocalConfig.getInstance().getLoadedDatabase());				    
		setTitle(GraphicalInterfaceConstants.TITLE + " - " + filename);  
		fileList.setSelectedIndex(-1);
		//list holds dateTime stamps from items in old fileList
		ArrayList<String> suffixList = new ArrayList();
		for (int i = 1; i < listModel.size(); i++) {
			//length of dateTime stamp is 14
			String suffix = listModel.get(i).substring(listModel.get(i).length() - 14);
			suffixList.add(suffix);
		}
		listModel.clear();
		listModel.addElement(filename);
		DatabaseCopier copier = new DatabaseCopier();
		for (int i = 0; i < suffixList.size(); i++) {
			listModel.addElement(GraphicalInterfaceConstants.OPTIMIZATION_PREFIX + filename + suffixList.get(i));
			copier.copyDatabase(GraphicalInterfaceConstants.OPTIMIZATION_PREFIX + oldName + suffixList.get(i), GraphicalInterfaceConstants.OPTIMIZATION_PREFIX + filename + suffixList.get(i));
			copier.copyLogFile(GraphicalInterfaceConstants.OPTIMIZATION_PREFIX + oldName + suffixList.get(i), GraphicalInterfaceConstants.OPTIMIZATION_PREFIX + filename + suffixList.get(i));
		}

		fileList.setModel(listModel);
		clearOutputPane();
	}

	public void saveReactionsTextFileChooser() {
		JTextArea output = null;
		JFileChooser fileChooser = new JFileChooser(new File(getDatabaseName()));
		boolean done = false;
		while (!done) {
			//... Open a file dialog.
			int retval = fileChooser.showSaveDialog(output);
			if (retval == JFileChooser.CANCEL_OPTION) {
				done = true;
			}
			if (retval == JFileChooser.APPROVE_OPTION) {            	  
				//... The user selected a file, get it, use it.
				String path = fileChooser.getSelectedFile().getPath();
				String filename = fileChooser.getSelectedFile().getName();
				//checks if filename endswith .csv else renames file to end with .csv
				if (!path.endsWith(".csv")) {
					path = path + ".csv";
				}

				File file = new File(path);
				if (path == null) {
					done = true;
				} else {        	    	  
					if (file.exists()) {
						int confirmDialog = JOptionPane.showConfirmDialog(fileChooser, "Replace existing file?");
						if (confirmDialog == JOptionPane.YES_OPTION) {
							done = true;

							saveReactionsTextFile(path, filename);

						} else if (confirmDialog == JOptionPane.NO_OPTION) {        		    	  
							done = false;
						} else {
							done = true;
						}       		    	  
					} else {
						done = true;

						saveReactionsTextFile(path, filename);
					}
				}			                  	  
			}
		}
	}

	public void saveSQLiteFile(){
		String oldName = getDatabaseName();
		DatabaseCopier copier = new DatabaseCopier();
		//filename is rawFilename - extension
		String filename = getDBFilename().substring(0, getDBFilename().length() - 3);

		//compare strings - same = 0
		if (getDatabaseName().compareTo(getDBPath()) == 0) {
			//creates a backup copy to prevent overwriting a db file
			copier.copyDatabase(getDatabaseName(), getDBPath() + GraphicalInterfaceConstants.DB_COPIER_SUFFIX);
		} else {  		  
			copier.copyDatabase(getDatabaseName(), getDBPath());
			//when db is created in a blank interface, this code
			//creates a copy of db so if user modifies it in the future, 
			//there is a revert option or backup copy, since db is
			//able to be modified in real time
			if (getDatabaseName() == ConfigConstants.DEFAULT_DATABASE_NAME) {
				copier.copyDatabase(getDatabaseName(), getDBPath() + GraphicalInterfaceConstants.DB_COPIER_SUFFIX);
			}
			setDatabaseName(getDBPath());    	  
			try {
				String fileString = "jdbc:sqlite:" + getDatabaseName() + ".db";
				Class.forName("org.sqlite.JDBC");
				Connection con = DriverManager.getConnection(fileString);	
				setUpReactionsTable(con);			    
				setUpMetabolitesTable(con);
				setTitle(GraphicalInterfaceConstants.TITLE + " - " + filename);	
				fileList.setSelectedIndex(-1);
				//list holds dateTime stamps from items in old fileList
				ArrayList<String> suffixList = new ArrayList();
				for (int i = 1; i < listModel.size(); i++) {
					//length of dateTime stamp is 14
					String suffix = listModel.get(i).substring(listModel.get(i).length() - 14);
					suffixList.add(suffix);
				}
				listModel.clear();
				listModel.addElement(filename);
				for (int i = 0; i < suffixList.size(); i++) {
					listModel.addElement(GraphicalInterfaceConstants.OPTIMIZATION_PREFIX + filename + suffixList.get(i));
					copier.copyDatabase(GraphicalInterfaceConstants.OPTIMIZATION_PREFIX + oldName + suffixList.get(i), GraphicalInterfaceConstants.OPTIMIZATION_PREFIX + filename + suffixList.get(i));
					copier.copyLogFile(GraphicalInterfaceConstants.OPTIMIZATION_PREFIX + oldName + suffixList.get(i), GraphicalInterfaceConstants.OPTIMIZATION_PREFIX + filename + suffixList.get(i));
				}

				fileList.setModel(listModel);
				clearOutputPane();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void saveSQLiteFileChooser() {
		JTextArea output = null;
		JFileChooser fileChooser = new JFileChooser(new File(getDatabaseName()));
		boolean done = false;
		while (!done) {
			//... Open a file dialog.
			int retval = fileChooser.showSaveDialog(output);
			if (retval == JFileChooser.CANCEL_OPTION) {
				done = true;
			}
			if (retval == JFileChooser.APPROVE_OPTION) {            	  
				//... The user selected a file, get it, use it.
				String rawPath = fileChooser.getSelectedFile().getPath();
				setDBPath(rawPath);
				String rawFilename = fileChooser.getSelectedFile().getName();
				//checks if filename endswith .db else renames file to end with .db
				if (!rawPath.endsWith(".db")) {
					rawPath = rawPath + ".db";
				}
				if (!rawFilename.endsWith(".db")) {
					setDBFilename(rawFilename + ".db");
				} else {
					setDBFilename(rawFilename);
				}

				File file = new File(rawPath);
				if (rawPath == null) {
					done = true;
				} else {        	    	  
					if (file.exists()) {
						int confirmDialog = JOptionPane.showConfirmDialog(fileChooser, "Replace existing file?");
						if (confirmDialog == JOptionPane.YES_OPTION) {
							done = true;

							saveSQLiteFile();

						} else if (confirmDialog == JOptionPane.NO_OPTION) {        		    	  
							done = false;
						} else {
							done = true;
						}       		    	  
					} else {
						done = true;

						saveSQLiteFile();
					}
				}			                  	  
			}
		}
	}

	class SaveSQLiteItemAction implements ActionListener {
		@SuppressWarnings("unused")
		public void actionPerformed(ActionEvent ae) {
			saveSQLiteFileChooser();
		}
	};

	class ClearAction implements ActionListener {
		public void actionPerformed(ActionEvent cae) {
			loadSetUp();
			try {
				Class.forName("org.sqlite.JDBC");       
				DatabaseCreator databaseCreator = new DatabaseCreator();
				setDatabaseName(ConfigConstants.DEFAULT_DATABASE_NAME);
				LocalConfig.getInstance().setLoadedDatabase(ConfigConstants.DEFAULT_DATABASE_NAME);
				Connection con = DriverManager.getConnection("jdbc:sqlite:" + ConfigConstants.DEFAULT_DATABASE_NAME + ".db");
				databaseCreator.createDatabase(LocalConfig.getInstance().getDatabaseName());
				databaseCreator.addRows(LocalConfig.getInstance().getDatabaseName(), GraphicalInterfaceConstants.BLANK_DB_NUMBER_OF_ROWS, GraphicalInterfaceConstants.BLANK_DB_NUMBER_OF_ROWS);
				setUpReactionsTable(con);	
				setUpMetabolitesTable(con);
				metabolitesTable.changeSelection(0, 0, false, false);
				metabolitesTable.requestFocus();
				reactionsTable.changeSelection(0, 0, false, false);
				reactionsTable.requestFocus();
				setTitle(GraphicalInterfaceConstants.TITLE + " - " + ConfigConstants.DEFAULT_DATABASE_NAME);
				listModel.clear();
				fileList.setModel(listModel);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	//based on code from http://tips4java.wordpress.com/2009/06/07/table-cell-listener/
	Action rAction = new AbstractAction()
	{
		public void actionPerformed(ActionEvent ae)
		{		  
			TableCellListener tcl = (TableCellListener)ae.getSource();
			updateReactionsDatabaseRow(tcl.getRow(), Integer.parseInt((String) (reactionsTable.getModel().getValueAt(tcl.getRow(), 0))), "SBML", LocalConfig.getInstance().getLoadedDatabase());
            System.out.println(tcl.getRow());
            System.out.println(Integer.parseInt((String) (reactionsTable.getModel().getValueAt(tcl.getRow(), 0))));
			
            if (tcl.getColumn() == GraphicalInterfaceConstants.REACTION_STRING_COLUMN) {  
				if (tcl.getOldValue() != tcl.getNewValue()) {
					highlightUnusedMetabolites = false;
					highlightUnusedMetabolitesItem.setState(false);
					ReactionFactory aFactory = new ReactionFactory();

					SBMLReaction aReaction = (SBMLReaction)aFactory.getReactionById(Integer.parseInt((String) (reactionsTable.getModel().getValueAt(tcl.getRow(), 0))), "SBML", LocalConfig.getInstance().getLoadedDatabase());
					ReactionParser parser = new ReactionParser();
				    		
					try {
						//updates reaction_reactant and reaction_product tables
						if (parser.isValid(aReaction.getReactionString())) {
							ArrayList reactantsAndProducts = parser.parseReaction(aReaction.getReactionString(), Integer.parseInt((String) (reactionsTable.getModel().getValueAt(tcl.getRow(), 0))), LocalConfig.getInstance().getLoadedDatabase());
							aReaction.setReactantsList((ArrayList) reactantsAndProducts.get(0));
							aReaction.clearReactants();
							aReaction.updateReactants();
							if (reactantsAndProducts.size() > 1) {
								aReaction.setProductsList((ArrayList) reactantsAndProducts.get(1));
								aReaction.clearProducts();
								aReaction.updateProducts();
							}			 	    
						}
					} catch (Throwable t) {
						System.out.println("Invalid reaction");
						LocalConfig.getInstance().getInvalidReactions().add(aReaction.getReactionString());
					}	
					try {	
						//updates used status of metabolites
						ReactionFactory rFactory = new ReactionFactory();
						MetaboliteFactory mFactory = new MetaboliteFactory();
						if (parser.isValid(tcl.getOldValue()) && parser.isValid(tcl.getNewValue())) {
							ArrayList<Integer> oldIdList = parser.speciesIdList(tcl.getOldValue(), LocalConfig.getInstance().getLoadedDatabase());
							ArrayList<Integer> newIdList = parser.speciesIdList(tcl.getNewValue(), LocalConfig.getInstance().getLoadedDatabase());
							//checks for added metabolites if both reactions are valid
							for (int j = 0; j < newIdList.size(); j++) {
								if (!oldIdList.contains(newIdList.get(j))) {
									mFactory.setMetaboliteUsedValue(newIdList.get(j), LocalConfig.getInstance().getLoadedDatabase(), "true");		    
								}
							}
							//checks for removed metabolites if both reactions are valid
							for (int i = 0; i < oldIdList.size(); i++) {
								if (!newIdList.contains(oldIdList.get(i))) {
									//check if metabolite id is not used by other reactions before changing to false
									if ((rFactory.reactantUsedCount(oldIdList.get(i), LocalConfig.getInstance().getLoadedDatabase()) + rFactory.productUsedCount(oldIdList.get(i), LocalConfig.getInstance().getLoadedDatabase())) == 0) {
										mFactory.setMetaboliteUsedValue(oldIdList.get(i), LocalConfig.getInstance().getLoadedDatabase(), "false"); 
									}     				    
								}
							}
							//if old reaction not valid, new reaction is just parsed and metabolites added.
						} else if (!parser.isValid(tcl.getOldValue()) && parser.isValid(tcl.getNewValue())) {
							ArrayList<Integer> newIdList = parser.speciesIdList(tcl.getNewValue(), LocalConfig.getInstance().getLoadedDatabase());
							for (int j = 0; j < newIdList.size(); j++) {
								mFactory.setMetaboliteUsedValue(newIdList.get(j), LocalConfig.getInstance().getLoadedDatabase(), "true");
							}   		  
						}      
					} catch (Throwable t) {
						System.out.println("Invalid reaction");
					}					 		  
				}
			}
			//resets metabolite table to show any changed values of used column
			//probably not necessary since this column is not visible
			String fileString = "jdbc:sqlite:" + LocalConfig.getInstance().getLoadedDatabase() + ".db";
			try {
				Class.forName("org.sqlite.JDBC");
				Connection con = DriverManager.getConnection(fileString);
				setUpMetabolitesTable(con);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	Action mAction = new AbstractAction()
	{
		public void actionPerformed(ActionEvent e)
		{   	  
			TableCellListener mtcl = (TableCellListener)e.getSource();
			updateMetabolitesDatabaseRow(mtcl.getRow(), Integer.parseInt((String) (metabolitesTable.getModel().getValueAt(mtcl.getRow(), 0))), "SBML", LocalConfig.getInstance().getLoadedDatabase()); 
			System.out.println(mtcl.getRow());
            System.out.println(Integer.parseInt((String) (metabolitesTable.getModel().getValueAt(mtcl.getRow(), 0))));
		}
	};

	/*****************************************************************************/
	//end Actions
	/*****************************************************************************/

	/*******************************************************************************/
	//table layouts
	/*******************************************************************************/
	//used in numerical columns so they are sorted by value and not as strings
	Comparator numberComparator = new Comparator() {
        public int compare(Object o1, Object o2) {
            Double d1 = Double.valueOf(o1 == null ? "0" : (String)o1);
            Double d2 = Double.valueOf(o2 == null ? "0" : (String)o2);
            return d1.compareTo(d2);
        }
    };
	
	private class ReactionsRowListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent event) {
			if (event.getValueIsAdjusting()) {
				return;
			}
		}
	}

	private class ReactionsColumnListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent event) {
			if (event.getValueIsAdjusting()) {
				return;
			}
		}
	}
	
	class ReactionsColumnHeaderListener extends MouseAdapter {
		  public void mouseClicked(MouseEvent evt) {
			  int r = reactionsTable.getSortedColumnIndex();
			  setReactionsSortColumnIndex(r);
			  setReactionsSortOrder(reactionsTable.getSortOrder(reactionsTable.getSortedColumnIndex()));
		  }
	}	  
	
	HighlightPredicate participatingPredicate = new HighlightPredicate() {
		public boolean isHighlighted(Component renderer ,ComponentAdapter adapter) {
			int viewRow = GraphicalInterface.reactionsTable.convertRowIndexToModel(adapter.row);
			if (reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTION_STRING_COLUMN) != null && (reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTION_STRING_COLUMN).toString().startsWith(GraphicalInterface.getParticipatingMetabolite() + " ") || reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTION_STRING_COLUMN).toString().endsWith(" " + GraphicalInterface.getParticipatingMetabolite()) || reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTION_STRING_COLUMN).toString().contains(" " + GraphicalInterface.getParticipatingMetabolite() + " "))) {  
				return true;
			}					
			return false;
		}
	};

	ColorHighlighter participating = new ColorHighlighter(participatingPredicate, Color.GREEN, null);
	
	HighlightPredicate invalidReactionPredicate = new HighlightPredicate() {
		public boolean isHighlighted(Component renderer ,ComponentAdapter adapter) {
			if (adapter.getValue() != null && LocalConfig.getInstance().getInvalidReactions().contains(adapter.getValue().toString())) {
				return true;
			}					
			return false;
		}
	};
	
	ColorHighlighter invalidReaction = new ColorHighlighter(invalidReactionPredicate, Color.RED, null);
	
	public void setReactionsTableLayout() {

		reactionsTable.getSelectionModel().addListSelectionListener(new ReactionsRowListener());
		reactionsTable.getColumnModel().getSelectionModel().
		addListSelectionListener(new ReactionsColumnListener());

		reactionsTable.setAutoResizeMode(JXTable.AUTO_RESIZE_OFF);
		reactionsTable.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		//allows individual cells to be selected as default
		reactionsTable.setColumnSelectionAllowed(true);
		reactionsTable.setRowSelectionAllowed(true); 
		reactionsTable.setCellSelectionEnabled(true);
		
		reactionsTable.getColumnExt("id").setComparator(numberComparator);
		reactionsTable.getColumnExt("flux_value").setComparator(numberComparator);
		reactionsTable.getColumnExt("lower_bound").setComparator(numberComparator);
		reactionsTable.getColumnExt("upper_bound").setComparator(numberComparator);
		reactionsTable.getColumnExt("biological_objective").setComparator(numberComparator);
		
		reactionsTable.getTableHeader().addMouseListener(new ReactionsColumnHeaderListener());
		
		reactionsTable.addHighlighter(participating);
		reactionsTable.addHighlighter(invalidReaction);
		
		ColumnHeaderToolTips tips = new ColumnHeaderToolTips();
		for (int c = 0; c < reactionsTable.getColumnCount(); c++) {
			TableColumn col = reactionsTable.getColumnModel().getColumn(c);
			if (c == GraphicalInterfaceConstants.BIOLOGICAL_OBJECTIVE_COLUMN) {
				tips.setToolTip(col, GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES[GraphicalInterfaceConstants.BIOLOGICAL_OBJECTIVE_COLUMN]);
			}	
			if (c == GraphicalInterfaceConstants.REVERSIBLE_COLUMN) {
				tips.setToolTip(col, GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES[GraphicalInterfaceConstants.REVERSIBLE_COLUMN]);
			}
			if (c == GraphicalInterfaceConstants.KO_COLUMN) {
				tips.setToolTip(col, GraphicalInterfaceConstants.KNOCKOUT_TOOLTIP);
			}      
		}
		reactionsTable.getTableHeader().addMouseMotionListener(tips);	
		reactionsTable.getTableHeader().addMouseListener(new ReactionsHeaderPopupListener());

		//from http://www.java2s.com/Tutorial/Java/0240__Swing/thelastcolumnismovedtothefirstposition.htm
		reactionsTable.getTableHeader().setReorderingAllowed(false);  
		
		ReactionsMetaColumnManager reactionsMetaColumnManager = new ReactionsMetaColumnManager();
		int metaColumnCount = reactionsMetaColumnManager.getMetaColumnCount(LocalConfig.getInstance().getDatabaseName());	
		
		int r = reactionsTable.getModel().getColumnCount();		
		for (int i = 0; i < r; i++) {
			//set background of id column to grey
			ColorTableCellRenderer reacGreyRenderer = new ColorTableCellRenderer();
			ReactionsTableCellRenderer reacRenderer = new ReactionsTableCellRenderer();
			
			TableColumn column = reactionsTable.getColumnModel().getColumn(i);
			if (i==GraphicalInterfaceConstants.DB_REACTIONS_ID_COLUMN) {
				//sets column not visible
				column.setMaxWidth(0);
				column.setMinWidth(0); 
				column.setWidth(0); 
				column.setPreferredWidth(0);
				ChangeName(reactionsTable, GraphicalInterfaceConstants.DB_REACTIONS_ID_COLUMN, 
						GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES[GraphicalInterfaceConstants.DB_REACTIONS_ID_COLUMN]); 
				column.setCellRenderer(reacGreyRenderer);
				//sets color of id column to grey
				reacGreyRenderer.setHorizontalAlignment(JLabel.CENTER);	
			} else {
				column.setCellRenderer(reacRenderer);
			}
			if (i==GraphicalInterfaceConstants.KO_COLUMN) {
				column.setPreferredWidth(GraphicalInterfaceConstants.KO_WIDTH); 
				ChangeName(reactionsTable, GraphicalInterfaceConstants.KO_COLUMN, 
						GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES[GraphicalInterfaceConstants.KO_COLUMN]); 
			}
			if (i==GraphicalInterfaceConstants.FLUX_VALUE_COLUMN) {
				ChangeName(reactionsTable, GraphicalInterfaceConstants.FLUX_VALUE_COLUMN, 
						GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES[GraphicalInterfaceConstants.FLUX_VALUE_COLUMN]); 
			}
			if (i==GraphicalInterfaceConstants.REACTION_ABBREVIATION_COLUMN) {
				column.setPreferredWidth(GraphicalInterfaceConstants.REACTION_ABBREVIATION_WIDTH);//2
				ChangeName(reactionsTable, GraphicalInterfaceConstants.REACTION_ABBREVIATION_COLUMN, 
						GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES[GraphicalInterfaceConstants.REACTION_ABBREVIATION_COLUMN]); 
			}
			if (i==GraphicalInterfaceConstants.REACTION_NAME_COLUMN) {
				column.setPreferredWidth(GraphicalInterfaceConstants.REACTION_NAME_WIDTH);
				ChangeName(reactionsTable, GraphicalInterfaceConstants.REACTION_NAME_COLUMN, 
						GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES[GraphicalInterfaceConstants.REACTION_NAME_COLUMN]);     
			}
			if (i==GraphicalInterfaceConstants.REACTION_STRING_COLUMN) {
				column.setPreferredWidth(GraphicalInterfaceConstants.REACTION_STRING_WIDTH);//3  
				ChangeName(reactionsTable, GraphicalInterfaceConstants.REACTION_STRING_COLUMN, 
						GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES[GraphicalInterfaceConstants.REACTION_STRING_COLUMN]); 
			}
			if (i==GraphicalInterfaceConstants.REVERSIBLE_COLUMN) {
				column.setPreferredWidth(GraphicalInterfaceConstants.REVERSIBLE_WIDTH);        //4
				ChangeName(reactionsTable, GraphicalInterfaceConstants.REVERSIBLE_COLUMN, 
						GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES[GraphicalInterfaceConstants.REVERSIBLE_COLUMN]); 
			}

			if (i==GraphicalInterfaceConstants.BIOLOGICAL_OBJECTIVE_COLUMN) {
				ChangeName(reactionsTable, GraphicalInterfaceConstants.BIOLOGICAL_OBJECTIVE_COLUMN, 
						GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES[GraphicalInterfaceConstants.BIOLOGICAL_OBJECTIVE_COLUMN]); 
			}
			if (i==GraphicalInterfaceConstants.LOWER_BOUND_COLUMN) {
				ChangeName(reactionsTable, GraphicalInterfaceConstants.LOWER_BOUND_COLUMN, 
						GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES[GraphicalInterfaceConstants.LOWER_BOUND_COLUMN]);          
			}
			if (i==GraphicalInterfaceConstants.UPPER_BOUND_COLUMN) {
				ChangeName(reactionsTable, GraphicalInterfaceConstants.UPPER_BOUND_COLUMN, 
						GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES[GraphicalInterfaceConstants.UPPER_BOUND_COLUMN]);          
			} 	  
			//set alignment of columns with numerical values to right, and default width
			if (i==GraphicalInterfaceConstants.BIOLOGICAL_OBJECTIVE_COLUMN || i==GraphicalInterfaceConstants.LOWER_BOUND_COLUMN || i==GraphicalInterfaceConstants.UPPER_BOUND_COLUMN || i==GraphicalInterfaceConstants.FLUX_VALUE_COLUMN) {	  
				reacRenderer.setHorizontalAlignment(JLabel.RIGHT); 
				column.setPreferredWidth(GraphicalInterfaceConstants.DEFAULT_WIDTH);
			} 
			
			if (i==GraphicalInterfaceConstants.REACTION_META1_COLUMN) {
				column.setPreferredWidth(GraphicalInterfaceConstants.REACTION_META_DEFAULT_WIDTH);
				if (i < (metaColumnCount + GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES.length)) {
					ChangeName(reactionsTable, GraphicalInterfaceConstants.REACTION_META1_COLUMN, 
							reactionsMetaColumnManager.getColumnName(LocalConfig.getInstance().getDatabaseName(), 1));     
				} else {
					ChangeName(reactionsTable, GraphicalInterfaceConstants.REACTION_META1_COLUMN, "J");	    					 
				}
			}
			if (i==GraphicalInterfaceConstants.REACTION_META2_COLUMN) {
				column.setPreferredWidth(GraphicalInterfaceConstants.REACTION_META_DEFAULT_WIDTH);
				if (i < (metaColumnCount + GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES.length)) {
					ChangeName(reactionsTable, GraphicalInterfaceConstants.REACTION_META2_COLUMN, 
							reactionsMetaColumnManager.getColumnName(LocalConfig.getInstance().getDatabaseName(), 2));     
				} else {
					ChangeName(reactionsTable, GraphicalInterfaceConstants.REACTION_META2_COLUMN, "K");	    					 
				}
			}
			if (i==GraphicalInterfaceConstants.REACTION_META3_COLUMN) {
				column.setPreferredWidth(GraphicalInterfaceConstants.REACTION_META_DEFAULT_WIDTH);
				if (i < (metaColumnCount + GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES.length)) {
					ChangeName(reactionsTable, GraphicalInterfaceConstants.REACTION_META3_COLUMN, 
							reactionsMetaColumnManager.getColumnName(LocalConfig.getInstance().getDatabaseName(), 3));     
				} else {
					ChangeName(reactionsTable, GraphicalInterfaceConstants.REACTION_META3_COLUMN, "L");	    					 
				}
			}
			if (i==GraphicalInterfaceConstants.REACTION_META4_COLUMN) {
				column.setPreferredWidth(GraphicalInterfaceConstants.REACTION_META_DEFAULT_WIDTH);
				if (i < (metaColumnCount + GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES.length)) {
					ChangeName(reactionsTable, GraphicalInterfaceConstants.REACTION_META4_COLUMN, 
							reactionsMetaColumnManager.getColumnName(LocalConfig.getInstance().getDatabaseName(), 4));     
				} else {
					ChangeName(reactionsTable, GraphicalInterfaceConstants.REACTION_META4_COLUMN, "M");	    					 
				}
			}
			if (i==GraphicalInterfaceConstants.REACTION_META5_COLUMN) {
				column.setPreferredWidth(GraphicalInterfaceConstants.REACTION_META_DEFAULT_WIDTH);
				if (i < (metaColumnCount + GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES.length)) {
					ChangeName(reactionsTable, GraphicalInterfaceConstants.REACTION_META5_COLUMN, 
							reactionsMetaColumnManager.getColumnName(LocalConfig.getInstance().getDatabaseName(), 5));     
				} else {
					ChangeName(reactionsTable, GraphicalInterfaceConstants.REACTION_META5_COLUMN, "N");	    					 
				}
			}
			if (i==GraphicalInterfaceConstants.REACTION_META6_COLUMN) {
				column.setPreferredWidth(GraphicalInterfaceConstants.REACTION_META_DEFAULT_WIDTH);
				if (i < (metaColumnCount + GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES.length)) {
					ChangeName(reactionsTable, GraphicalInterfaceConstants.REACTION_META6_COLUMN, 
							reactionsMetaColumnManager.getColumnName(LocalConfig.getInstance().getDatabaseName(), 6));     
				} else {
					ChangeName(reactionsTable, GraphicalInterfaceConstants.REACTION_META6_COLUMN, "O");	    					 
				}
			}
			if (i==GraphicalInterfaceConstants.REACTION_META7_COLUMN) {
				column.setPreferredWidth(GraphicalInterfaceConstants.REACTION_META_DEFAULT_WIDTH);
				if (i < (metaColumnCount + GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES.length)) {
					ChangeName(reactionsTable, GraphicalInterfaceConstants.REACTION_META7_COLUMN, 
							reactionsMetaColumnManager.getColumnName(LocalConfig.getInstance().getDatabaseName(), 7));     
				} else {
					ChangeName(reactionsTable, GraphicalInterfaceConstants.REACTION_META7_COLUMN, "P");
				}
			}
			if (i==GraphicalInterfaceConstants.REACTION_META8_COLUMN) {
				column.setPreferredWidth(GraphicalInterfaceConstants.REACTION_META_DEFAULT_WIDTH);
				if (i < (metaColumnCount + GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES.length)) {
					ChangeName(reactionsTable, GraphicalInterfaceConstants.REACTION_META8_COLUMN, 
							reactionsMetaColumnManager.getColumnName(LocalConfig.getInstance().getDatabaseName(), 8));     
				} else {
					ChangeName(reactionsTable, GraphicalInterfaceConstants.REACTION_META8_COLUMN, "Q");
				}
			}
			if (i==GraphicalInterfaceConstants.REACTION_META9_COLUMN) {
				column.setPreferredWidth(GraphicalInterfaceConstants.REACTION_META_DEFAULT_WIDTH);
				if (i < (metaColumnCount + GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES.length)) {
					ChangeName(reactionsTable, GraphicalInterfaceConstants.REACTION_META9_COLUMN, 
							reactionsMetaColumnManager.getColumnName(LocalConfig.getInstance().getDatabaseName(), 9));     
				} else {
					ChangeName(reactionsTable, GraphicalInterfaceConstants.REACTION_META9_COLUMN, "R");	
				}
			}
			if (i==GraphicalInterfaceConstants.REACTION_META10_COLUMN) {
				column.setPreferredWidth(GraphicalInterfaceConstants.REACTION_META_DEFAULT_WIDTH);
				if (i < (metaColumnCount + GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES.length)) {
					ChangeName(reactionsTable, GraphicalInterfaceConstants.REACTION_META10_COLUMN, 
							reactionsMetaColumnManager.getColumnName(LocalConfig.getInstance().getDatabaseName(), 10));     
				} else {
					ChangeName(reactionsTable, GraphicalInterfaceConstants.REACTION_META10_COLUMN, "S");
				}
			}
			if (i==GraphicalInterfaceConstants.REACTION_META11_COLUMN) {
				column.setPreferredWidth(GraphicalInterfaceConstants.REACTION_META_DEFAULT_WIDTH);
				if (i < (metaColumnCount + GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES.length)) {
					ChangeName(reactionsTable, GraphicalInterfaceConstants.REACTION_META11_COLUMN, 
							reactionsMetaColumnManager.getColumnName(LocalConfig.getInstance().getDatabaseName(), 11));     
				} else {
					ChangeName(reactionsTable, GraphicalInterfaceConstants.REACTION_META11_COLUMN, "T");
				}
			}
			if (i==GraphicalInterfaceConstants.REACTION_META12_COLUMN) {
				column.setPreferredWidth(GraphicalInterfaceConstants.REACTION_META_DEFAULT_WIDTH);
				if (i < (metaColumnCount + GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES.length)) {
					ChangeName(reactionsTable, GraphicalInterfaceConstants.REACTION_META12_COLUMN, 
							reactionsMetaColumnManager.getColumnName(LocalConfig.getInstance().getDatabaseName(), 12));     
				} else {
					ChangeName(reactionsTable, GraphicalInterfaceConstants.REACTION_META12_COLUMN, "U");
				}
			}
			if (i==GraphicalInterfaceConstants.REACTION_META13_COLUMN) {
				column.setPreferredWidth(GraphicalInterfaceConstants.REACTION_META_DEFAULT_WIDTH);
				if (i < (metaColumnCount + GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES.length)) {
					ChangeName(reactionsTable, GraphicalInterfaceConstants.REACTION_META13_COLUMN, 
							reactionsMetaColumnManager.getColumnName(LocalConfig.getInstance().getDatabaseName(), 13));     
				} else {
					ChangeName(reactionsTable, GraphicalInterfaceConstants.REACTION_META13_COLUMN, "V");
				}
			}
			if (i==GraphicalInterfaceConstants.REACTION_META14_COLUMN) {
				column.setPreferredWidth(GraphicalInterfaceConstants.REACTION_META_DEFAULT_WIDTH);
				if (i < (metaColumnCount + GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES.length)) {
					ChangeName(reactionsTable, GraphicalInterfaceConstants.REACTION_META14_COLUMN, 
							reactionsMetaColumnManager.getColumnName(LocalConfig.getInstance().getDatabaseName(), 14));     
				} else {
					ChangeName(reactionsTable, GraphicalInterfaceConstants.REACTION_META14_COLUMN, "W");
				}
			}
			if (i==GraphicalInterfaceConstants.REACTION_META15_COLUMN) {
				column.setPreferredWidth(GraphicalInterfaceConstants.REACTION_META_DEFAULT_WIDTH);
				if (i < (metaColumnCount + GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES.length)) {
					ChangeName(reactionsTable, GraphicalInterfaceConstants.REACTION_META15_COLUMN, 
							reactionsMetaColumnManager.getColumnName(LocalConfig.getInstance().getDatabaseName(), 15));     
				} else {
					ChangeName(reactionsTable, GraphicalInterfaceConstants.REACTION_META15_COLUMN, "X");
				}
			}			
		}
	}

	private class MetabolitesRowListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent event) {
			if (event.getValueIsAdjusting()) {
				return;
			}
		}
	}

	private class MetabolitesColumnListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent event) {
			if (event.getValueIsAdjusting()) {
				return;
			}
		}
	}
	
	class MetabolitesColumnHeaderListener extends MouseAdapter {
		  public void mouseClicked(MouseEvent evt) {
			  int m = metabolitesTable.getSortedColumnIndex();
			  setMetabolitesSortColumnIndex(m);
			  setMetabolitesSortOrder(metabolitesTable.getSortOrder(metabolitesTable.getSortedColumnIndex()));
		  }
	}
	
	HighlightPredicate unusedPredicate = new HighlightPredicate() {
		public boolean isHighlighted(Component renderer ,ComponentAdapter adapter) {
			int viewRow = GraphicalInterface.metabolitesTable.convertRowIndexToModel(adapter.row);
			if (highlightUnusedMetabolites == true && metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.USED_COLUMN).toString().equals("false")) {					
				return true;
			}					
			return false;
		}
	};
	
	ColorHighlighter unused = new ColorHighlighter(unusedPredicate, Color.YELLOW, null);
	
	public void setMetabolitesTableLayout() {	 
		metabolitesTable.getSelectionModel().addListSelectionListener(new MetabolitesRowListener());
		metabolitesTable.getColumnModel().getSelectionModel().
		addListSelectionListener(new MetabolitesColumnListener());

		metabolitesTable.setAutoResizeMode(JXTable.AUTO_RESIZE_OFF);
		metabolitesTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		//allows individual cells to be selected as default
		metabolitesTable.setColumnSelectionAllowed(true);
		metabolitesTable.setRowSelectionAllowed(true); 
		metabolitesTable.setCellSelectionEnabled(true);
		
		metabolitesTable.getColumnExt("id").setComparator(numberComparator);
		metabolitesTable.getColumnExt("charge").setComparator(numberComparator);
				
		metabolitesTable.getTableHeader().addMouseListener(new MetabolitesColumnHeaderListener());
		metabolitesTable.getTableHeader().addMouseListener(new MetabolitesHeaderPopupListener());
				
		metabolitesTable.addHighlighter(unused);
		
		ColumnHeaderToolTips tips = new ColumnHeaderToolTips();
		for (int c = 0; c < metabolitesTable.getColumnCount(); c++) {
			TableColumn col = metabolitesTable.getColumnModel().getColumn(c);	
			if (c == GraphicalInterfaceConstants.CHARGE_COLUMN) {
				tips.setToolTip(col, GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES[GraphicalInterfaceConstants.CHARGE_COLUMN]);     
			}
			if (c == GraphicalInterfaceConstants.BOUNDARY_COLUMN) {
				tips.setToolTip(col, GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES[GraphicalInterfaceConstants.BOUNDARY_COLUMN]);     
			}
		}
		metabolitesTable.getTableHeader().addMouseMotionListener(tips);	  

		int m = metabolitesTable.getModel().getColumnCount();
		for (int w = 0; w < m; w++) {
			MetabolitesMetaColumnManager metabolitesMetaColumnManager = new MetabolitesMetaColumnManager();
			ColorTableCellRenderer metabGreyRenderer = new ColorTableCellRenderer();
			MetabolitesTableCellRenderer metabRenderer = new MetabolitesTableCellRenderer();
			int metabMetaColumnCount = metabolitesMetaColumnManager.getMetaColumnCount(LocalConfig.getInstance().getDatabaseName());	

			TableColumn column = metabolitesTable.getColumnModel().getColumn(w);
			if (w==GraphicalInterfaceConstants.DB_METABOLITE_ID_COLUMN) {
				//sets column not visible
				column.setMaxWidth(0);
				column.setMinWidth(0); 
				column.setWidth(0); 
				column.setPreferredWidth(0);
				ChangeName(metabolitesTable, GraphicalInterfaceConstants.DB_METABOLITE_ID_COLUMN, 
						GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES[GraphicalInterfaceConstants.DB_METABOLITE_ID_COLUMN]);     
				column.setCellRenderer(metabGreyRenderer);
				metabGreyRenderer.setHorizontalAlignment(JLabel.CENTER);
			} else {	    		  
				column.setCellRenderer(metabRenderer); 	    		  
			}
			if (w==GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN) {
				column.setPreferredWidth(GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_WIDTH);
				ChangeName(metabolitesTable, GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN, 
						GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES[GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN]);     
			}
			if (w==GraphicalInterfaceConstants.METABOLITE_NAME_COLUMN) {
				column.setPreferredWidth(GraphicalInterfaceConstants.METABOLITE_NAME_WIDTH);
				ChangeName(metabolitesTable, GraphicalInterfaceConstants.METABOLITE_NAME_COLUMN, 
						GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES[GraphicalInterfaceConstants.METABOLITE_NAME_COLUMN]);     
			} 
			if (w==GraphicalInterfaceConstants.CHARGE_COLUMN) {
				column.setPreferredWidth(GraphicalInterfaceConstants.CHARGE_WIDTH);
				ChangeName(metabolitesTable, GraphicalInterfaceConstants.CHARGE_COLUMN, 
						GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES[GraphicalInterfaceConstants.CHARGE_COLUMN]);     
				metabRenderer.setHorizontalAlignment(JLabel.RIGHT);
			}
			if (w==GraphicalInterfaceConstants.COMPARTMENT_COLUMN) {
				column.setPreferredWidth(GraphicalInterfaceConstants.COMPARTMENT_WIDTH);
				ChangeName(metabolitesTable, GraphicalInterfaceConstants.COMPARTMENT_COLUMN, 
						GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES[GraphicalInterfaceConstants.COMPARTMENT_COLUMN]);     
			}	    	  	    	  
			if (w==GraphicalInterfaceConstants.BOUNDARY_COLUMN) {
				column.setPreferredWidth(GraphicalInterfaceConstants.BOUNDARY_WIDTH);
				ChangeName(metabolitesTable, GraphicalInterfaceConstants.BOUNDARY_COLUMN, 
						GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES[GraphicalInterfaceConstants.BOUNDARY_COLUMN]);     
			} 

			if (w==GraphicalInterfaceConstants.METABOLITE_META1_COLUMN) {
				column.setPreferredWidth(GraphicalInterfaceConstants.METABOLITE_META_DEFAULT_WIDTH);
				if (w < (metabMetaColumnCount + GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES.length)) {
					ChangeName(metabolitesTable, GraphicalInterfaceConstants.METABOLITE_META1_COLUMN, 
							metabolitesMetaColumnManager.getColumnName(LocalConfig.getInstance().getDatabaseName(), 1));     
				} else {
					ChangeName(metabolitesTable, GraphicalInterfaceConstants.METABOLITE_META1_COLUMN, "E");	    					 
				}
			}
			if (w==GraphicalInterfaceConstants.METABOLITE_META2_COLUMN) {
				column.setPreferredWidth(GraphicalInterfaceConstants.METABOLITE_META_DEFAULT_WIDTH);
				if (w < (metabMetaColumnCount + GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES.length)) {
					ChangeName(metabolitesTable, GraphicalInterfaceConstants.METABOLITE_META2_COLUMN, 
							metabolitesMetaColumnManager.getColumnName(LocalConfig.getInstance().getDatabaseName(), 2));     
				} else {
					ChangeName(metabolitesTable, GraphicalInterfaceConstants.METABOLITE_META2_COLUMN, "F");	    					 
				}
			}
			if (w==GraphicalInterfaceConstants.METABOLITE_META3_COLUMN) {
				column.setPreferredWidth(GraphicalInterfaceConstants.METABOLITE_META_DEFAULT_WIDTH);
				if (w < (metabMetaColumnCount + GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES.length)) {
					ChangeName(metabolitesTable, GraphicalInterfaceConstants.METABOLITE_META3_COLUMN, 
							metabolitesMetaColumnManager.getColumnName(LocalConfig.getInstance().getDatabaseName(), 3));     
				} else {
					ChangeName(metabolitesTable, GraphicalInterfaceConstants.METABOLITE_META3_COLUMN, "G");	    					 
				}
			}
			if (w==GraphicalInterfaceConstants.METABOLITE_META4_COLUMN) {
				column.setPreferredWidth(GraphicalInterfaceConstants.METABOLITE_META_DEFAULT_WIDTH);
				if (w < (metabMetaColumnCount + GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES.length)) {
					ChangeName(metabolitesTable, GraphicalInterfaceConstants.METABOLITE_META4_COLUMN, 
							metabolitesMetaColumnManager.getColumnName(LocalConfig.getInstance().getDatabaseName(), 4));     
				} else {
					ChangeName(metabolitesTable, GraphicalInterfaceConstants.METABOLITE_META4_COLUMN, "H");	    					 
				}
			}
			if (w==GraphicalInterfaceConstants.METABOLITE_META5_COLUMN) {
				column.setPreferredWidth(GraphicalInterfaceConstants.METABOLITE_META_DEFAULT_WIDTH);
				if (w < (metabMetaColumnCount + GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES.length)) {
					ChangeName(metabolitesTable, GraphicalInterfaceConstants.METABOLITE_META5_COLUMN, 
							metabolitesMetaColumnManager.getColumnName(LocalConfig.getInstance().getDatabaseName(), 5));     
				} else {
					ChangeName(metabolitesTable, GraphicalInterfaceConstants.METABOLITE_META5_COLUMN, "I");	    					 
				}
			}
			if (w==GraphicalInterfaceConstants.METABOLITE_META6_COLUMN) {
				column.setPreferredWidth(GraphicalInterfaceConstants.METABOLITE_META_DEFAULT_WIDTH);
				if (w < (metabMetaColumnCount + GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES.length)) {
					ChangeName(metabolitesTable, GraphicalInterfaceConstants.METABOLITE_META6_COLUMN, 
							metabolitesMetaColumnManager.getColumnName(LocalConfig.getInstance().getDatabaseName(), 6));     
				} else {
					ChangeName(metabolitesTable, GraphicalInterfaceConstants.METABOLITE_META6_COLUMN, "J");	    					 
				}
			}
			if (w==GraphicalInterfaceConstants.METABOLITE_META7_COLUMN) {
				column.setPreferredWidth(GraphicalInterfaceConstants.METABOLITE_META_DEFAULT_WIDTH);
				if (w < (metabMetaColumnCount + GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES.length)) {
					ChangeName(metabolitesTable, GraphicalInterfaceConstants.METABOLITE_META7_COLUMN, 
							metabolitesMetaColumnManager.getColumnName(LocalConfig.getInstance().getDatabaseName(), 7));     
				} else {
					ChangeName(metabolitesTable, GraphicalInterfaceConstants.METABOLITE_META7_COLUMN, "K");	    					 
				}
			}
			if (w==GraphicalInterfaceConstants.METABOLITE_META8_COLUMN) {
				column.setPreferredWidth(GraphicalInterfaceConstants.METABOLITE_META_DEFAULT_WIDTH);
				if (w < (metabMetaColumnCount + GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES.length)) {
					ChangeName(metabolitesTable, GraphicalInterfaceConstants.METABOLITE_META8_COLUMN, 
							metabolitesMetaColumnManager.getColumnName(LocalConfig.getInstance().getDatabaseName(), 8));     
				} else {
					ChangeName(metabolitesTable, GraphicalInterfaceConstants.METABOLITE_META8_COLUMN, "L");	    					 
				}
			}
			if (w==GraphicalInterfaceConstants.METABOLITE_META9_COLUMN) {
				column.setPreferredWidth(GraphicalInterfaceConstants.METABOLITE_META_DEFAULT_WIDTH);
				if (w < (metabMetaColumnCount + GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES.length)) {
					ChangeName(metabolitesTable, GraphicalInterfaceConstants.METABOLITE_META9_COLUMN, 
							metabolitesMetaColumnManager.getColumnName(LocalConfig.getInstance().getDatabaseName(), 9));     
				} else {
					ChangeName(metabolitesTable, GraphicalInterfaceConstants.METABOLITE_META9_COLUMN, "M");	    					 
				}
			}
			if (w==GraphicalInterfaceConstants.METABOLITE_META10_COLUMN) {				
				column.setPreferredWidth(GraphicalInterfaceConstants.METABOLITE_META_DEFAULT_WIDTH);
				if (w < (metabMetaColumnCount + GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES.length)) {
					ChangeName(metabolitesTable, GraphicalInterfaceConstants.METABOLITE_META10_COLUMN, 
							metabolitesMetaColumnManager.getColumnName(LocalConfig.getInstance().getDatabaseName(), 10));     
				} else {
					ChangeName(metabolitesTable, GraphicalInterfaceConstants.METABOLITE_META10_COLUMN, "N");	    					 
				}
			} 
			if (w==GraphicalInterfaceConstants.METABOLITE_META11_COLUMN) {
				column.setPreferredWidth(GraphicalInterfaceConstants.METABOLITE_META_DEFAULT_WIDTH);
				if (w < (metabMetaColumnCount + GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES.length)) {
					ChangeName(metabolitesTable, GraphicalInterfaceConstants.METABOLITE_META11_COLUMN, 
							metabolitesMetaColumnManager.getColumnName(LocalConfig.getInstance().getDatabaseName(), 11));     
				} else {
					ChangeName(metabolitesTable, GraphicalInterfaceConstants.METABOLITE_META11_COLUMN, "O");	    					 
				}
			}
			if (w==GraphicalInterfaceConstants.METABOLITE_META12_COLUMN) {
				column.setPreferredWidth(GraphicalInterfaceConstants.METABOLITE_META_DEFAULT_WIDTH);
				if (w < (metabMetaColumnCount + GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES.length)) {
					ChangeName(metabolitesTable, GraphicalInterfaceConstants.METABOLITE_META12_COLUMN, 
							metabolitesMetaColumnManager.getColumnName(LocalConfig.getInstance().getDatabaseName(), 12));     
				} else {
					ChangeName(metabolitesTable, GraphicalInterfaceConstants.METABOLITE_META12_COLUMN, "P");	    					 
				}
			}
			if (w==GraphicalInterfaceConstants.METABOLITE_META13_COLUMN) {
				column.setPreferredWidth(GraphicalInterfaceConstants.METABOLITE_META_DEFAULT_WIDTH);
				if (w < (metabMetaColumnCount + GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES.length)) {
					ChangeName(metabolitesTable, GraphicalInterfaceConstants.METABOLITE_META13_COLUMN, 
							metabolitesMetaColumnManager.getColumnName(LocalConfig.getInstance().getDatabaseName(), 13));     
				} else {
					ChangeName(metabolitesTable, GraphicalInterfaceConstants.METABOLITE_META13_COLUMN, "Q");	    					 
				}
			}
			if (w==GraphicalInterfaceConstants.METABOLITE_META14_COLUMN) {
				column.setPreferredWidth(GraphicalInterfaceConstants.METABOLITE_META_DEFAULT_WIDTH);
				if (w < (metabMetaColumnCount + GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES.length)) {
					ChangeName(metabolitesTable, GraphicalInterfaceConstants.METABOLITE_META14_COLUMN, 
							metabolitesMetaColumnManager.getColumnName(LocalConfig.getInstance().getDatabaseName(), 14));     
				} else {
					ChangeName(metabolitesTable, GraphicalInterfaceConstants.METABOLITE_META14_COLUMN, "R");	    					 
				}
			}
			if (w==GraphicalInterfaceConstants.METABOLITE_META15_COLUMN) {				
				column.setPreferredWidth(GraphicalInterfaceConstants.METABOLITE_META_DEFAULT_WIDTH);
				if (w < (metabMetaColumnCount + GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES.length)) {
					ChangeName(metabolitesTable, GraphicalInterfaceConstants.METABOLITE_META15_COLUMN, 
							metabolitesMetaColumnManager.getColumnName(LocalConfig.getInstance().getDatabaseName(), 15));     
				} else {
					ChangeName(metabolitesTable, GraphicalInterfaceConstants.METABOLITE_META15_COLUMN, "S");	    					 
				}
			} 
			if (w==GraphicalInterfaceConstants.USED_COLUMN) {
				//sets column not visible
				column.setMaxWidth(0);
				column.setMinWidth(0); 
				column.setWidth(0); 
				column.setPreferredWidth(0);
			} 
		}
	}
	/************************************************************************************/
	//end table layouts
	/************************************************************************************/

	/************************************************************************************/
	//header context menus
	/************************************************************************************/
	
	public class ReactionsHeaderPopupListener extends MouseAdapter {

		public void maybeShowPopup(MouseEvent e) {
			if (e.isPopupTrigger() && reactionsTable.isEnabled()) {
				Point p = new Point(e.getX(), e.getY());
				int col = reactionsTable.columnAtPoint(p);
				JPopupMenu reactionsHeaderContextMenu = createReactionsHeaderContextMenu(col);
				// ... and show it
				if (reactionsHeaderContextMenu != null
						&& reactionsHeaderContextMenu.getComponentCount() > 0) {
					reactionsHeaderContextMenu.show(reactionsTable, p.x, p.y);
				}
			}
		}

		public void mousePressed(MouseEvent e) {
			maybeShowPopup(e);
		}

		public void mouseReleased(MouseEvent e) {
			maybeShowPopup(e);
		}
	}
	
	private JPopupMenu createReactionsHeaderContextMenu(final int columnIndex) {
		JPopupMenu reactionsHeaderContextMenu = new JPopupMenu();
		
		JMenuItem deleteColumnMenu = new JMenuItem();
		deleteColumnMenu.setText("Delete Column");
		//core columns cannot be deleted
		if (columnIndex < 10) {
			deleteColumnMenu.setEnabled(false);
		}
		deleteColumnMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				reactionsTable.getColumnExt(columnIndex).setVisible(false);		
			}
		});
		reactionsHeaderContextMenu.add(deleteColumnMenu);	
		
		return reactionsHeaderContextMenu;
		
	}
	
	public class MetabolitesHeaderPopupListener extends MouseAdapter {

		public void maybeShowPopup(MouseEvent e) {
			if (e.isPopupTrigger() && metabolitesTable.isEnabled()) {
				Point p = new Point(e.getX(), e.getY());
				int col = metabolitesTable.columnAtPoint(p);
				JPopupMenu metabolitesHeaderContextMenu = createMetabolitesHeaderContextMenu(col);
				// ... and show it
				if (metabolitesHeaderContextMenu != null
						&& metabolitesHeaderContextMenu.getComponentCount() > 0) {
					metabolitesHeaderContextMenu.show(metabolitesTable, p.x, p.y);
				}
			}
		}

		public void mousePressed(MouseEvent e) {
			maybeShowPopup(e);
		}

		public void mouseReleased(MouseEvent e) {
			maybeShowPopup(e);
		}
	}
	
	private JPopupMenu createMetabolitesHeaderContextMenu(final int columnIndex) {
		JPopupMenu metabolitesHeaderContextMenu = new JPopupMenu();
		
		JMenuItem deleteColumnMenu = new JMenuItem();
		deleteColumnMenu.setText("Delete Column");
		//core columns cannot be deleted
		if (columnIndex < 6) {
			deleteColumnMenu.setEnabled(false);
		}
		deleteColumnMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				metabolitesTable.getColumnExt(columnIndex).setVisible(false);	
			}
		});
		metabolitesHeaderContextMenu.add(deleteColumnMenu);	
		
		return metabolitesHeaderContextMenu;
		
	}
	
	/************************************************************************************/
	//end header context menus
	/************************************************************************************/	
		
	/*******************************************************************************/
	//Reactions Table context menus
	/*******************************************************************************/	
	
	//based on code from http://www.javakb.com/Uwe/Forum.aspx/java-programmer/21291/popupmenu-for-a-cell-in-a-JXTable
	public class ReactionsPopupListener extends MouseAdapter {

		public void maybeShowPopup(MouseEvent e) {
			if (e.isPopupTrigger() && reactionsTable.isEnabled()) {
				Point p = new Point(e.getX(), e.getY());
				int col = reactionsTable.columnAtPoint(p);
				int row = reactionsTable.rowAtPoint(p);
				setCurrentRow(row);

				if (row >= 0 && row < reactionsTable.getRowCount()) {
					cancelCellEditing();            
					// create popup menu...
					if (col == GraphicalInterfaceConstants.REACTION_STRING_COLUMN) {
						JPopupMenu reactionsContextMenu = createReactionsContextMenu(row, col);
						if (reactionsContextMenu != null
								&& reactionsContextMenu.getComponentCount() > 0) {
							reactionsContextMenu.show(reactionsTable, p.x, p.y);
						}
					} else {
						JPopupMenu contextMenu = createContextMenu(row, col);
						// ... and show it
						if (contextMenu != null
								&& contextMenu.getComponentCount() > 0) {
							contextMenu.show(reactionsTable, p.x, p.y);
						}
					}
				}
			}
		}

		public void mousePressed(MouseEvent e) {
			maybeShowPopup(e);
		}

		public void mouseReleased(MouseEvent e) {
			maybeShowPopup(e);
		}
	}

	private void cancelCellEditing() {
		CellEditor ce = reactionsTable.getCellEditor();
		if (ce != null) {
			ce.cancelCellEditing();
		}
	}

	private JPopupMenu createReactionsContextMenu(final int rowIndex,
			final int columnIndex) {
		JPopupMenu reactionsContextMenu = new JPopupMenu();
		
		JMenuItem selectColMenu = new JMenuItem();
		selectColMenu.setText("Select Column(s)");
		selectColMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectReactionsColumns();
			}
		});
		reactionsContextMenu.add(selectColMenu);	
		
		JMenuItem selectAllMenu = new JMenuItem();
		selectAllMenu.setText("Select All");
		selectAllMenu.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_A, ActionEvent.CTRL_MASK));
		selectAllMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reactionsTable.selectAll();
				selectReactionsRows();
			}
		});
		reactionsContextMenu.add(selectAllMenu);
		
		reactionsContextMenu.addSeparator();
			
		JMenuItem copyMenu = new JMenuItem();
		copyMenu.setText("Copy");
		copyMenu.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_C, ActionEvent.CTRL_MASK));
		copyMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reactionsCopy();
			}
		});
		reactionsContextMenu.add(copyMenu);

		JMenuItem pasteMenu = new JMenuItem();
		pasteMenu.setText("Paste");
		pasteMenu.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_V, ActionEvent.CTRL_MASK));
		if (isClipboardContainingText(this)
				&& reactionsTable.getModel().isCellEditable(rowIndex, columnIndex)) {
			pasteMenu.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					reactionsPaste();
				}
			});
		} else {
			pasteMenu.setEnabled(false);
		}
		reactionsContextMenu.add(pasteMenu);
		
		JMenuItem fillUpMenu = new JMenuItem();
		fillUpMenu.setText("Fill Up");
		//if fill is done in the sorted row it will sort after fill  
		if (getReactionsSortColumnIndex() == reactionsTable.getSelectedColumn()) {
			fillUpMenu.setEnabled(false);
		}
		fillUpMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int row=reactionsTable.getSelectedRow();
				reactionsFill(0, row);
			}
		});
		reactionsContextMenu.add(fillUpMenu);
		
		JMenuItem fillDownMenu = new JMenuItem();
		fillDownMenu.setText("Fill Down");
		//if fill is done in the sorted row it will sort after fill  
		if (getReactionsSortColumnIndex() == reactionsTable.getSelectedColumn()) {
			fillDownMenu.setEnabled(false);
		}
		fillDownMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int row=reactionsTable.getSelectedRow();
				reactionsFill(row + 1, reactionsTable.getRowCount());
			}
		});
		reactionsContextMenu.add(fillDownMenu);

		JMenuItem clearMenu = new JMenuItem();
		clearMenu.setText("Clear Contents");
		clearMenu.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_E, ActionEvent.CTRL_MASK));
		clearMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reactionsClear();
			}
		});
		reactionsContextMenu.add(clearMenu);
		
		reactionsContextMenu.addSeparator();

		JMenuItem editMenu = new JMenuItem();
		editMenu.setText("Edit");   	   
		editMenu.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent e) {
				setCurrentRow(rowIndex);
				try {
					Class.forName("org.sqlite.JDBC");
					Connection con = DriverManager.getConnection("jdbc:sqlite:" + LocalConfig.getInstance().getLoadedDatabase() + ".db");
					ReactionInterface reactionInterface = new ReactionInterface(con);
					setReactionInterface(reactionInterface);
					reactionInterface.setIconImages(icons);
					reactionInterface.setSize(1000, 240);
					reactionInterface.setResizable(false);
					reactionInterface.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
					reactionInterface.setLocationRelativeTo(null);
					reactionInterface.setVisible(true);
					reactionInterface.submitButton.addActionListener(submitButtonActionListener);
				} catch (ClassNotFoundException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				} catch (SQLException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
			}
		});
		reactionsContextMenu.add(editMenu);
		
		reactionsContextMenu.addSeparator();
		
		JMenuItem deleteRowMenu = new JMenuItem();
		deleteRowMenu.setText("Delete Row(s)");
		deleteRowMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				reactionsDeleteRows();			
			}
		});
		reactionsContextMenu.add(deleteRowMenu);		

		return reactionsContextMenu;
	}

	private JPopupMenu createContextMenu(final int rowIndex,
			final int columnIndex) {
		JPopupMenu contextMenu = new JPopupMenu();

		JMenuItem selectRowMenu = new JMenuItem();
		selectRowMenu.setText("Select Row(s)");
		if (columnIndex > 1) {
			selectRowMenu.setEnabled(false);
		}
		selectRowMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectReactionsRows();
			}
		});
		contextMenu.add(selectRowMenu);	
		
		JMenuItem selectColMenu = new JMenuItem();
		selectColMenu.setText("Select Column(s)");
		selectColMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectReactionsColumns();
			}
		});
		contextMenu.add(selectColMenu);		
		
		JMenuItem selectAllMenu = new JMenuItem();
		selectAllMenu.setText("Select All");
		selectAllMenu.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_A, ActionEvent.CTRL_MASK));
		selectAllMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reactionsTable.selectAll();
				selectReactionsRows();
			}
		});
		contextMenu.add(selectAllMenu);
		
		contextMenu.addSeparator();
		
		JMenuItem copyMenu = new JMenuItem();
		copyMenu.setText("Copy");
		copyMenu.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_C, ActionEvent.CTRL_MASK));
		copyMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reactionsCopy();
			}
		});
		contextMenu.add(copyMenu);

		JMenuItem pasteMenu = new JMenuItem();
		pasteMenu.setText("Paste");
		pasteMenu.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_V, ActionEvent.CTRL_MASK));
		if (isClipboardContainingText(this)
				&& reactionsTable.getModel().isCellEditable(rowIndex, columnIndex)) {
			pasteMenu.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					reactionsPaste();
				}
			});
		} else {
			pasteMenu.setEnabled(false);
		}
		contextMenu.add(pasteMenu);

		JMenuItem fillUpMenu = new JMenuItem();
		fillUpMenu.setText("Fill Up");
		//if fill is done in the sorted row it will sort after fill  
		if (getReactionsSortColumnIndex() == reactionsTable.getSelectedColumn()) {
			fillUpMenu.setEnabled(false);
		}
		fillUpMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int row=reactionsTable.getSelectedRow();
				reactionsFill(0, row);
			}
		});
		contextMenu.add(fillUpMenu);
		
		JMenuItem fillDownMenu = new JMenuItem();
		fillDownMenu.setText("Fill Down");
		//if fill is done in the sorted row it will sort after fill  
		if (getReactionsSortColumnIndex() == reactionsTable.getSelectedColumn()) {
			fillDownMenu.setEnabled(false);
		}
		fillDownMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int row=reactionsTable.getSelectedRow();
				reactionsFill(row + 1, reactionsTable.getRowCount());
			}
		});
		contextMenu.add(fillDownMenu);
		
		JMenuItem clearMenu = new JMenuItem();
		clearMenu.setText("Clear Contents");
		clearMenu.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_E, ActionEvent.CTRL_MASK));
		clearMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reactionsClear();
			}
		});
		contextMenu.add(clearMenu);
		
		contextMenu.addSeparator();
		
		JMenuItem deleteRowMenu = new JMenuItem();
		deleteRowMenu.setText("Delete Row(s)");
		deleteRowMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				reactionsDeleteRows();		
			}
		});
		contextMenu.add(deleteRowMenu);	
		
		JMenuItem deleteColumnMenu = new JMenuItem();
		deleteColumnMenu.setText("Delete Columns(s)");
		//core columns cannot be deleted
		if (columnIndex < 10) {
			deleteColumnMenu.setEnabled(false);
		}
		deleteColumnMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				int numcols=reactionsTable.getSelectedColumnCount(); 
				int startCol=(reactionsTable.getSelectedColumns())[0];
				for (int i = startCol + numcols - 1; i >= startCol; i--) {
					reactionsTable.getColumnExt(i).setVisible(false);
				}			
			}
		});
		contextMenu.add(deleteColumnMenu);

		return contextMenu;
	}  
	/****************************************************************************/
	// end Reactions Table context menus
	/****************************************************************************/

	/****************************************************************************/
	// Metabolites Table context menus
	/****************************************************************************/

	public class MetabolitesPopupListener extends MouseAdapter {

		public void maybeShowPopup(MouseEvent e) {
			if (e.isPopupTrigger() && metabolitesTable.isEnabled()) {
				Point p = new Point(e.getX(), e.getY());
				int col = metabolitesTable.columnAtPoint(p);
				int row = metabolitesTable.rowAtPoint(p);
				setCurrentRow(row);

				if (row >= 0 && row < metabolitesTable.getRowCount()) {
					cancelCellEditing();            
					// create popup menu...

					if (col == GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN) {
						JPopupMenu abbrevContextMenu = createMetaboliteAbbreviationContextMenu(row, col);
						// ... and show it
						if (abbrevContextMenu != null
								&& abbrevContextMenu.getComponentCount() > 0) {
							abbrevContextMenu.show(metabolitesTable, p.x, p.y);
						}
					} else {
						JPopupMenu contextMenu = createMetabolitesContextMenu(row, col);
						// ... and show it
						if (contextMenu != null
								&& contextMenu.getComponentCount() > 0) {
							contextMenu.show(metabolitesTable, p.x, p.y);
						}	            	
					}
				}
			}
		}

		public void mousePressed(MouseEvent e) {
			maybeShowPopup(e);
		}

		public void mouseReleased(MouseEvent e) {
			maybeShowPopup(e);
		}
	}

	private JPopupMenu createMetaboliteAbbreviationContextMenu(final int rowIndex,
			final int columnIndex) {
		JPopupMenu contextMenu = new JPopupMenu();

		JMenuItem selectRowMenu = new JMenuItem();
		selectRowMenu.setText("Select Row(s)");
		if (columnIndex > 1) {
			selectRowMenu.setEnabled(false);
		}
		selectRowMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectMetabolitesRows();
			}
		});
		contextMenu.add(selectRowMenu);	
		
		JMenuItem selectColMenu = new JMenuItem();
		selectColMenu.setText("Select Column(s)");
		selectColMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectMetabolitesColumns();
			}
		});
		contextMenu.add(selectColMenu);		
		
		JMenuItem selectAllMenu = new JMenuItem();
		selectAllMenu.setText("Select All");
		selectAllMenu.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_A, ActionEvent.CTRL_MASK));
		selectAllMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				metabolitesTable.selectAll();
				selectMetabolitesRows();
			}
		});
		contextMenu.add(selectAllMenu);
		
		contextMenu.addSeparator();
		
		JMenuItem copyMenu = new JMenuItem();
		copyMenu.setText("Copy");
		copyMenu.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_C, ActionEvent.CTRL_MASK));
		copyMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int viewRow = GraphicalInterface.metabolitesTable.convertRowIndexToModel(GraphicalInterface.metabolitesTable.getSelectedRow());
				metabolitesCopy();
			}
		});
		contextMenu.add(copyMenu);

		JMenuItem pasteMenu = new JMenuItem();
		pasteMenu.setText("Paste");
		pasteMenu.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_V, ActionEvent.CTRL_MASK));
		if (isClipboardContainingText(this)
				&& metabolitesTable.getModel().isCellEditable(rowIndex, columnIndex)) {
			pasteMenu.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					metabolitesPaste();
				}
			});
		} else {
			pasteMenu.setEnabled(false);
		}
		contextMenu.add(pasteMenu);	
		
		contextMenu.addSeparator();

		//final JCheckBoxMenuItem participatingReactionsMenu = new JCheckBoxMenuItem();
		final JMenuItem participatingReactionsMenu = new JMenuItem();
		participatingReactionsMenu.setText("Highlight Participating Reactions");
		String abbreviation = (String) metabolitesTable.getModel().getValueAt(rowIndex,
				columnIndex);
		if (abbreviation == null) {
			participatingReactionsMenu.setEnabled(false);
		}
		participatingReactionsMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int viewRow = metabolitesTable.convertRowIndexToModel(rowIndex);
				//int viewRow = metabolitesTable.convertRowIndexToModel(metabolitesTable.getSelectedRow());
				setParticipatingMetabolite((String) metabolitesTable.getModel().getValueAt(viewRow, columnIndex));
				System.out.println(getParticipatingMetabolite());
			}
		});
		contextMenu.add(participatingReactionsMenu);

		final JMenuItem unhighlightParticipatingReactionsMenu = new JMenuItem();
		unhighlightParticipatingReactionsMenu.setText("Unhighlight Participating Reactions");
		if (abbreviation == null || getParticipatingMetabolite() == null || getParticipatingMetabolite() == "   ") {
			unhighlightParticipatingReactionsMenu.setEnabled(false);
		}
		unhighlightParticipatingReactionsMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				//to make sure that if user entered two spaces in reaction equation - still no highlight results
				setParticipatingMetabolite("   "); 
				String fileString = "jdbc:sqlite:" + LocalConfig.getInstance().getLoadedDatabase() + ".db";
				try {
					Class.forName("org.sqlite.JDBC");
					Connection con = DriverManager.getConnection(LocalConfig.getInstance().getLoadedDatabase());
					setUpMetabolitesTable(con);
					setUpReactionsTable(con);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			}
		});
		contextMenu.add(unhighlightParticipatingReactionsMenu);

		contextMenu.addSeparator();

		JMenuItem deleteRowMenu = new JMenuItem();
		deleteRowMenu.setText("Delete Row(s)");

		if (GraphicalInterface.metabolitesTable.getSelectedRow() > -1) {
			int viewRow = GraphicalInterface.metabolitesTable.convertRowIndexToModel(GraphicalInterface.metabolitesTable.getSelectedRow());
			final int id = (Integer.valueOf((String) GraphicalInterface.metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.DB_METABOLITE_ID_COLUMN)));		
			final MetaboliteFactory mFactory = new MetaboliteFactory();
			if (mFactory.isUnused(id, LocalConfig.getInstance().getLoadedDatabase())) {
				deleteRowMenu.setEnabled(true);
				deleteRowMenu.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						metaboliteDeleteRows();				
					}
				});
			} else {
				deleteRowMenu.setEnabled(false);
			}
		} else {
			deleteRowMenu.setEnabled(false);
		} 

		contextMenu.add(deleteRowMenu);

		return contextMenu;
	}

	private JPopupMenu createMetabolitesContextMenu(final int rowIndex,
			final int columnIndex) {
		JPopupMenu contextMenu = new JPopupMenu();

		JMenuItem selectRowMenu = new JMenuItem();
		selectRowMenu.setText("Select Row(s)");
		if (columnIndex > 1) {
			selectRowMenu.setEnabled(false);
		}
		//selectRowMenu.setAccelerator(KeyStroke.getKeyStroke(
		        //KeyEvent.VK_A, ActionEvent.CTRL_MASK));
		selectRowMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectMetabolitesRows();
			}
		});
		contextMenu.add(selectRowMenu);	
		
		JMenuItem selectColMenu = new JMenuItem();
		selectColMenu.setText("Select Column(s)");
		//selectColMenu.setAccelerator(KeyStroke.getKeyStroke(
		        //KeyEvent.VK_A, ActionEvent.CTRL_MASK));
		selectColMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectMetabolitesColumns();
			}
		});
		contextMenu.add(selectColMenu);		
		
		JMenuItem selectAllMenu = new JMenuItem();
		selectAllMenu.setText("Select All");
		selectAllMenu.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_A, ActionEvent.CTRL_MASK));
		selectAllMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				metabolitesTable.selectAll();
				selectMetabolitesRows();
			}
		});
		contextMenu.add(selectAllMenu);
		
		contextMenu.addSeparator();
		
		JMenuItem copyMenu = new JMenuItem();
		copyMenu.setText("Copy");
		copyMenu.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_C, ActionEvent.CTRL_MASK));
		copyMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int viewRow = GraphicalInterface.metabolitesTable.convertRowIndexToModel(GraphicalInterface.metabolitesTable.getSelectedRow());
				metabolitesCopy();
			}
		});
		contextMenu.add(copyMenu);

		JMenuItem pasteMenu = new JMenuItem();
		pasteMenu.setText("Paste");
		pasteMenu.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_V, ActionEvent.CTRL_MASK));
		if (isClipboardContainingText(this)
				&& metabolitesTable.getModel().isCellEditable(rowIndex, columnIndex)) {
			pasteMenu.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					metabolitesPaste();
				}
			});
		} else {
			pasteMenu.setEnabled(false);
		}
		contextMenu.add(pasteMenu);

		JMenuItem fillUpMenu = new JMenuItem();
		fillUpMenu.setText("Fill Up");
		//if fill is done in the sorted row it will sort after fill  
		if (getMetabolitesSortColumnIndex() == metabolitesTable.getSelectedColumn()) {
			fillUpMenu.setEnabled(false);
		}
		fillUpMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int row=metabolitesTable.getSelectedRow();
				metabolitesFill(0, row);
			}
		});
		contextMenu.add(fillUpMenu);
		
		JMenuItem fillDownMenu = new JMenuItem();
		fillDownMenu.setText("Fill Down");
		if (getMetabolitesSortColumnIndex() == metabolitesTable.getSelectedColumn()) {
			fillDownMenu.setEnabled(false);
		}
		fillDownMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int row=metabolitesTable.getSelectedRow();
				metabolitesFill(row + 1, metabolitesTable.getRowCount());
			}
		});
		contextMenu.add(fillDownMenu);
		
		JMenuItem clearMenu = new JMenuItem();
		clearMenu.setText("Clear Contents");
		clearMenu.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_E, ActionEvent.CTRL_MASK));
		clearMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				metabolitesClear();
			}
		});
		contextMenu.add(clearMenu);
		
		contextMenu.addSeparator();


		JMenuItem deleteRowMenu = new JMenuItem();
		deleteRowMenu.setText("Delete Row(s)");

		if (GraphicalInterface.metabolitesTable.getSelectedRow() > -1) {
			int viewRow = GraphicalInterface.metabolitesTable.convertRowIndexToModel(GraphicalInterface.metabolitesTable.getSelectedRow());
			final int id = (Integer.valueOf((String) GraphicalInterface.metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.DB_METABOLITE_ID_COLUMN)));		
			final MetaboliteFactory mFactory = new MetaboliteFactory();
			if (mFactory.isUnused(id, LocalConfig.getInstance().getLoadedDatabase())) {
				deleteRowMenu.setEnabled(true);
				deleteRowMenu.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						metaboliteDeleteRows();				
					}
				});
			} else {
				deleteRowMenu.setEnabled(false);
			}
		} else {
			deleteRowMenu.setEnabled(false);
		} 
		
		JMenuItem deleteColumnMenu = new JMenuItem();
		deleteColumnMenu.setText("Delete Column(s)");
		//core columns cannot be deleted
		if (columnIndex < 6) {
			deleteColumnMenu.setEnabled(false);
		}
		deleteColumnMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				int numcols=metabolitesTable.getSelectedColumnCount(); 
				int startCol=(metabolitesTable.getSelectedColumns())[0];
				for (int i = startCol + numcols - 1; i >= startCol; i--) {
					metabolitesTable.getColumnExt(i).setVisible(false);
				}		
			}
		});
		contextMenu.add(deleteColumnMenu);
		
		contextMenu.add(deleteRowMenu);

		return contextMenu;
	}  

	/****************************************************************************/
	// end Metabolites Table context menus
	/****************************************************************************/

	/*******************************************************************************/
	//end context menu  
	/*******************************************************************************/  

	//listens for submit button event in ReactionInterface
	ActionListener submitButtonActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent ae) {
			int viewRow = GraphicalInterface.reactionsTable.convertRowIndexToModel(GraphicalInterface.reactionsTable.getSelectedRow());
		    int id = (Integer.valueOf((String) GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.DB_REACTIONS_ID_COLUMN)));
		    MetaboliteFactory mFactory = new MetaboliteFactory();
			ReactionFactory rFactory = new ReactionFactory();	
		    SBMLReaction aReaction = (SBMLReaction)rFactory.getReactionById(id, "SBML", LocalConfig.getInstance().getLoadedDatabase()); 
		    reactionInterface.setReactionEquation(reactionInterface.reactionField.getText());
			reactionsTable.getModel().setValueAt(reactionInterface.getReactionEquation(), viewRow, GraphicalInterfaceConstants.REACTION_STRING_COLUMN);
			if (reactionInterface.getReactionEquation().contains("<") || (reactionInterface.getReactionEquation().contains("=") && !reactionInterface.getReactionEquation().contains(">"))) {
				reactionsTable.getModel().setValueAt("true", viewRow, GraphicalInterfaceConstants.REVERSIBLE_COLUMN);
			} else if (reactionInterface.getReactionEquation().contains("-->") || reactionInterface.getReactionEquation().contains("->") || reactionInterface.getReactionEquation().contains("=>")) {
				reactionsTable.getModel().setValueAt("false", viewRow, GraphicalInterfaceConstants.REVERSIBLE_COLUMN);		    		
			}
			updateReactionsDatabaseRow(viewRow, Integer.parseInt((String) (reactionsTable.getModel().getValueAt(viewRow, 0))), "SBML", LocalConfig.getInstance().getLoadedDatabase());
					
			ReactionParser parser = new ReactionParser();
			if (parser.isValid(aReaction.getReactionString())) {
				System.out.println(aReaction.getReactionString());	
				ArrayList reactantsAndProducts = parser.parseReaction(aReaction.getReactionString(), id, LocalConfig.getInstance().getLoadedDatabase());
				aReaction.setReactantsList((ArrayList) reactantsAndProducts.get(0));
				for (int i = 0; i < aReaction.getReactantsList().size(); i++) {
					System.out.println(aReaction.getReactantsList().get(i));
				}
				aReaction.clearReactants();
				aReaction.updateReactants();
				if (reactantsAndProducts.size() > 1) {
					aReaction.setProductsList((ArrayList) reactantsAndProducts.get(1));
					aReaction.clearProducts();
					aReaction.updateProducts();
				} else {
					aReaction.clearProducts();
				}
			}		    

			if (parser.isValid(reactionInterface.getOldReaction()) && parser.isValid(reactionInterface.getReactionEquation())) {
				ArrayList<Integer> oldIdList = parser.speciesIdList(reactionInterface.getOldReaction(), LocalConfig.getInstance().getLoadedDatabase());
				ArrayList<Integer> newIdList = parser.speciesIdList(reactionInterface.getReactionEquation(), LocalConfig.getInstance().getLoadedDatabase());
				//adds new species to used_metabolites table
				for (int j = 0; j < newIdList.size(); j++) {
					if (!oldIdList.contains(newIdList.get(j))) {
						mFactory.setMetaboliteUsedValue(newIdList.get(j), LocalConfig.getInstance().getLoadedDatabase(), "true"); 		    
					}
				}
				//removes deleted species from used_metabolites if not required by other rxns
				for (int i = 0; i < oldIdList.size(); i++) {
					if (!newIdList.contains(oldIdList.get(i))) {
						//check if metabolite id is not used by other reactions before removing
						if ((rFactory.reactantUsedCount(oldIdList.get(i), LocalConfig.getInstance().getLoadedDatabase()) + rFactory.productUsedCount(oldIdList.get(i), LocalConfig.getInstance().getLoadedDatabase())) == 0) {
							mFactory.setMetaboliteUsedValue(oldIdList.get(i), LocalConfig.getInstance().getLoadedDatabase(), "false"); 
						}     				    
					}
				}    
			} else if (!parser.isValid(reactionInterface.getOldReaction()) && parser.isValid(reactionInterface.getReactionEquation())) {
				ArrayList<Integer> newIdList = parser.speciesIdList(reactionInterface.getReactionEquation(), LocalConfig.getInstance().getLoadedDatabase());
				for (int j = 0; j < newIdList.size(); j++) {
					mFactory.setMetaboliteUsedValue(newIdList.get(j), LocalConfig.getInstance().getLoadedDatabase(), "true"); 		          			    
				}		    	
			}
			//resets metabolite table to show any changed values of used column
			//necessary for MetabolitesTableCellRenderer to highlight correct cells
			String fileString = "jdbc:sqlite:" + LocalConfig.getInstance().getLoadedDatabase() + ".db";
			try {
				Class.forName("org.sqlite.JDBC");
				Connection con = DriverManager.getConnection(fileString);
				setUpMetabolitesTable(con);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}; 


	/***********************************************************************************/
	//clipboard
	/***********************************************************************************/

	//from http://www.javakb.com/Uwe/Forum.aspx/java-programmer/21291/popupmenu-for-a-cell-in-a-JXTable
	private static String getClipboardContents(Object requestor) {
		Transferable t = Toolkit.getDefaultToolkit()
		.getSystemClipboard().getContents(requestor);
		if (t != null) {
			DataFlavor df = DataFlavor.stringFlavor;
			if (df != null) {
				try {
					Reader r = df.getReaderForText(t);
					char[] charBuf = new char[512];
					StringBuffer buf = new StringBuffer();
					int n;
					while ((n = r.read(charBuf, 0, charBuf.length)) > 0) {
						buf.append(charBuf, 0, n);
					}
					r.close();
					return (buf.toString());
				} catch (IOException ex) {
					ex.printStackTrace();
				} catch (UnsupportedFlavorException ex) {
					ex.printStackTrace();
				}
			}
		}
		return null;
	}

	private static boolean isClipboardContainingText(Object requestor) {
		Transferable t = Toolkit.getDefaultToolkit()
		.getSystemClipboard().getContents(requestor);
		return t != null
		&& (t.isDataFlavorSupported(DataFlavor.stringFlavor) || t
				.isDataFlavorSupported(DataFlavor.plainTextFlavor));
	}
	
	private static void setClipboardContents(String s) {
	      StringSelection selection = new StringSelection(s);
	      Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
	            selection, selection);
	   }

	/*****************************************************************************/
	//end clipboard
	/******************************************************************************/

	/******************************************************************************/
	//reload tables methods
	/******************************************************************************/
	
	public void setUpMetabolitesTable(Connection con) {

		try {
			MetabolitesDatabaseTableModel metabModel = new MetabolitesDatabaseTableModel(con, new String("select * from metabolites;"));
			metabolitesTable.setModel(metabModel);
			setMetabolitesTableLayout();
			
			if (getMetabolitesSortColumnIndex() >= 0) {
				metabolitesTable.setSortOrder(getMetabolitesSortColumnIndex(), getMetabolitesSortOrder());
			} else {
				setMetabolitesSortColumnIndex(0);
				setMetabolitesSortOrder(SortOrder.ASCENDING);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	   
	}

	public void setUpReactionsTable(Connection con) {

		try {
			ReactionsDatabaseTableModel reacModel = new ReactionsDatabaseTableModel(con, new String("select * from reactions"));
			reactionsTable.setModel(reacModel);
			setReactionsTableLayout();
			
			if (getReactionsSortColumnIndex() >= 0) {
				reactionsTable.setSortOrder(getReactionsSortColumnIndex(), getReactionsSortOrder());
			} else {
				setReactionsSortColumnIndex(0);
				setReactionsSortOrder(SortOrder.ASCENDING);
			}
			/*
			int r = reactionsTable.getModel().getColumnCount();
			ReactionsMetaColumnManager reactionsMetaColumnManager = new ReactionsMetaColumnManager();
			int metaColumnCount = reactionsMetaColumnManager.getMetaColumnCount(LocalConfig.getInstance().getDatabaseName());	
			
			for (int j = r - 1; j >= GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES.length; j--) {
				if (j > metaColumnCount) {
					reactionsTable.getColumnExt(j).setVisible(false);	
				}
			}
            */
			setParticipatingMetabolite("   ");			

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	   
	}

	//method used only when loading
	public void setUpTables() {
		String titleName = "";
		if (getDatabaseName().contains("\\")) {
			titleName = getDatabaseName().substring(getDatabaseName().lastIndexOf("\\") + 1, getDatabaseName().length());
		} else {
			titleName = getDatabaseName();
		}
		try {
			String fileString = "jdbc:sqlite:" + getDatabaseName() + ".db";
			Class.forName("org.sqlite.JDBC");
			Connection con = DriverManager.getConnection(fileString);			    
			setUpMetabolitesTable(con);
			setUpReactionsTable(con);
			//set focus to top left
			metabolitesTable.changeSelection(0, 1, false, false);
			metabolitesTable.requestFocus();
			reactionsTable.changeSelection(0, 1, false, false);
			reactionsTable.requestFocus();
			setTitle(GraphicalInterfaceConstants.TITLE + " - " + titleName);
			listModel.addElement(titleName);
			fileList.setModel(listModel);
			setReactionsSortColumnIndex(0);
			setMetabolitesSortColumnIndex(0);
			setReactionsSortOrder(SortOrder.ASCENDING);
			setMetabolitesSortOrder(SortOrder.ASCENDING);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//sets parameters to initial values on load
	public void loadSetUp() {
		clearOutputPane();
		if (getPopout() != null) {
			popout.dispose();
		}
		/*
		if (getPopout() != null) {
			getPopout().clear();
		}
		*/
		showPrompt = true;
		highlightUnusedMetabolites = false;
		highlightUnusedMetabolitesItem.setState(false); 
		setReactionsSortColumnIndex(0);
		setMetabolitesSortColumnIndex(0);
		LocalConfig.getInstance().getInvalidReactions().clear();
	}

	/******************************************************************************/
	//end reload tables methods
	/******************************************************************************/
	
	/******************************************************************************/
	//update database methods, called when table rows changed 
	/******************************************************************************/

	public void updateReactionsDatabaseRow(int rowIndex, Integer reactionId, String sourceType, String databaseName)  {

		try { 
			ReactionFactory aFactory = new ReactionFactory();

			SBMLReaction aReaction = (SBMLReaction)aFactory.getReactionById(reactionId, sourceType, databaseName); 
			aReaction.setKnockout((String) reactionsTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.KO_COLUMN));			

			if (reactionsTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.FLUX_VALUE_COLUMN) != null) {
				try {
					aReaction.setFluxValue(Double.valueOf((String) reactionsTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.FLUX_VALUE_COLUMN)));
				}
			    catch ( NumberFormatException nfe ) {
			       System.out.println( "Number format exception" );
			    }				
			}

			aReaction.setReactionAbbreviation((String) reactionsTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.REACTION_ABBREVIATION_COLUMN));
			aReaction.setReactionName((String) reactionsTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.REACTION_NAME_COLUMN));
			aReaction.setReactionString((String) reactionsTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.REACTION_STRING_COLUMN));		    

			if (aReaction.getReactionString() != null) {
				if (aReaction.getReactionString().contains("<") || (aReaction.getReactionString().contains("=") && !aReaction.getReactionString().contains(">"))) {
					aReaction.setReversible("true");
				} else if (aReaction.getReactionString().contains("-->") || aReaction.getReactionString().contains("->") || aReaction.getReactionString().contains("=>")) {
					aReaction.setReversible("false");		    		
				}				
			} 

			//string cannot be cast to double but valueOf works, from http://www.java-examples.com/convert-java-string-double-example		    
			if (reactionsTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.LOWER_BOUND_COLUMN) != null) {
				try {
					aReaction.setLowerBound(Double.valueOf((String) reactionsTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.LOWER_BOUND_COLUMN)));
				}
			    catch ( NumberFormatException nfe ) {
			       System.out.println( "Number format exception" );
			    }					
			} else {
				aReaction.setLowerBound(-999999);
			}
			if (reactionsTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.UPPER_BOUND_COLUMN) != null) {
				try {
					aReaction.setUpperBound(Double.valueOf((String) reactionsTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.UPPER_BOUND_COLUMN)));
				}
			    catch ( NumberFormatException nfe ) {
			       System.out.println( "Number format exception" );
			    }				
			} else {
				aReaction.setUpperBound(999999);
			}
			if (reactionsTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.BIOLOGICAL_OBJECTIVE_COLUMN) != null) {
				try {
					aReaction.setBiologicalObjective(Double.valueOf((String) reactionsTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.BIOLOGICAL_OBJECTIVE_COLUMN)));
				}
			    catch ( NumberFormatException nfe ) {
			       System.out.println( "Number format exception" );
			    }				
			} else {
				aReaction.setBiologicalObjective(0);
			}
			
			aReaction.setMeta1((String) reactionsTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.REACTION_META1_COLUMN));			
			aReaction.setMeta2((String) reactionsTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.REACTION_META2_COLUMN));
			aReaction.setMeta3((String) reactionsTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.REACTION_META3_COLUMN));			
			aReaction.setMeta4((String) reactionsTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.REACTION_META4_COLUMN));			
			aReaction.setMeta5((String) reactionsTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.REACTION_META5_COLUMN));			
			aReaction.setMeta6((String) reactionsTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.REACTION_META6_COLUMN));			
			aReaction.setMeta7((String) reactionsTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.REACTION_META7_COLUMN));			
			aReaction.setMeta8((String) reactionsTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.REACTION_META8_COLUMN));			
			aReaction.setMeta9((String) reactionsTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.REACTION_META9_COLUMN));			
			aReaction.setMeta10((String) reactionsTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.REACTION_META10_COLUMN));			
			aReaction.setMeta11((String) reactionsTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.REACTION_META11_COLUMN));			
			aReaction.setMeta12((String) reactionsTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.REACTION_META12_COLUMN));			
			aReaction.setMeta13((String) reactionsTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.REACTION_META13_COLUMN));			
			aReaction.setMeta14((String) reactionsTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.REACTION_META14_COLUMN));			
			aReaction.setMeta15((String) reactionsTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.REACTION_META15_COLUMN));			

			aReaction.update();

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();			
		}
	}

	public void updateMetabolitesDatabaseRow(int rowIndex, Integer metaboliteId, String sourceType, String databaseName)  {
		try {
			MetaboliteFactory aFactory = new MetaboliteFactory();
			SBMLMetabolite aMetabolite = (SBMLMetabolite)aFactory.getMetaboliteById(Integer.parseInt((String) metabolitesTable.getModel().getValueAt(rowIndex, 0)), "SBML", LocalConfig.getInstance().getLoadedDatabase()); 

			aMetabolite.setMetaboliteAbbreviation((String) metabolitesTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN));
			aMetabolite.setMetaboliteName((String) metabolitesTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.METABOLITE_NAME_COLUMN));
			aMetabolite.setCharge((String) metabolitesTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.CHARGE_COLUMN));
			aMetabolite.setCompartment((String) metabolitesTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.COMPARTMENT_COLUMN));
			aMetabolite.setBoundary((String) metabolitesTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.BOUNDARY_COLUMN));
			aMetabolite.setMeta1((String) metabolitesTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.METABOLITE_META1_COLUMN));
			aMetabolite.setMeta2((String) metabolitesTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.METABOLITE_META2_COLUMN));
			aMetabolite.setMeta3((String) metabolitesTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.METABOLITE_META3_COLUMN));
			aMetabolite.setMeta4((String) metabolitesTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.METABOLITE_META4_COLUMN));
			aMetabolite.setMeta5((String) metabolitesTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.METABOLITE_META5_COLUMN));
			aMetabolite.setMeta6((String) metabolitesTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.METABOLITE_META6_COLUMN));
			aMetabolite.setMeta7((String) metabolitesTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.METABOLITE_META7_COLUMN));
			aMetabolite.setMeta8((String) metabolitesTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.METABOLITE_META8_COLUMN));
			aMetabolite.setMeta9((String) metabolitesTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.METABOLITE_META9_COLUMN));
			aMetabolite.setMeta10((String) metabolitesTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.METABOLITE_META10_COLUMN));
			aMetabolite.setMeta11((String) metabolitesTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.METABOLITE_META11_COLUMN));
			aMetabolite.setMeta12((String) metabolitesTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.METABOLITE_META12_COLUMN));
			aMetabolite.setMeta13((String) metabolitesTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.METABOLITE_META13_COLUMN));
			aMetabolite.setMeta14((String) metabolitesTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.METABOLITE_META14_COLUMN));
			aMetabolite.setMeta15((String) metabolitesTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.METABOLITE_META15_COLUMN));
			aMetabolite.setUsed((String) metabolitesTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.USED_COLUMN));

			aMetabolite.update();

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();			
		}    
	}

	/******************************************************************************/
	//end update database methods
	/******************************************************************************/
	
	/*******************************************************************************/
	//fileList methods
	/*******************************************************************************/
	
	//based on http://www.java2s.com/Code/Java/File-Input-Output/Textfileviewer.htm
	public static void loadOutputPane(String path) {
		File file;
		FileReader in = null;

		try {
			file = new File(path); 
			in = new FileReader(file); 
			char[] buffer = new char[4096]; // Read 4K characters at a time
			int len; 
			outputTextArea.setText(""); 
			while ((len = in.read(buffer)) != -1) { // Read a batch of chars
				String s = new String(buffer, 0, len); 
				outputTextArea.append(s); 
			}
			outputTextArea.setCaretPosition(0); 
		}

		catch (IOException e) {

		}

		finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
			}
		}
	}

	public void clearOutputPane() {
		outputTextArea.setText(""); 
	}

	/*******************************************************************************/
	//end fileList methods
	/*******************************************************************************/
	
	/*******************************************************************************/
	//progressBar methods
	/*******************************************************************************/
	
	class Task extends SwingWorker<Void, Void> {

		@Override
		public void done() {

		}

		@Override
		protected Void doInBackground() throws Exception {
			int progress = 0;
			SBMLDocument doc = new SBMLDocument();
			SBMLReader reader = new SBMLReader();
			try {		  
				doc = reader.readSBML(GraphicalInterface.getSBMLFile());
				SBMLModelReader modelReader = new SBMLModelReader(doc);
				modelReader.setDatabaseName(LocalConfig.getInstance().getDatabaseName());
				modelReader.load();
			} catch (FileNotFoundException e) {
				JOptionPane.showMessageDialog(null,                
						"File does not exist.",                
						"File does not exist.",                                
						JOptionPane.ERROR_MESSAGE);
				//e.printStackTrace();
			} catch (XMLStreamException e) {
				JOptionPane.showMessageDialog(null,                
						"This File is not a Valid SBML File.",                
						"Invalid SBML File.",                                
						JOptionPane.ERROR_MESSAGE);
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}	
			while (progress < 100) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException ignore) {
				}			
			}				
			t.stop();
			return null;
		}
	}

	class TimeListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (LocalConfig.getInstance().getProgress() > 0) {
				progressBar.progress.setIndeterminate(false);
			}			
			progressBar.progress.setValue(LocalConfig.getInstance().getProgress());
			progressBar.progress.repaint();
			if (LocalConfig.getInstance().getProgress() == 100) {
				setUpTables();
				t.stop();
				progressBar.setVisible(false);
				progressBar.dispose();
				LocalConfig.getInstance().setProgress(0);
				progressBar.progress.setIndeterminate(true);
			}
		}
	}

	/*******************************************************************************/
	//end progressBar methods
	/*******************************************************************************/
	
	/**************************************************************************/
	//reactionsTable context menu methods
	/**************************************************************************/
	//These methods are based partly on code from ExcelAdapter
	//http://stackoverflow.com/questions/4671657/how-to-copy-content-of-the-jtable-to-clipboard
	
	public void selectReactionsRows() {
		reactionsTable.setColumnSelectionAllowed(false);
		reactionsTable.setRowSelectionAllowed(true);
		StringBuffer sbf=new StringBuffer();
		int numrows = reactionsTable.getSelectedRowCount(); 
		System.out.println(numrows);
		LocalConfig.getInstance().setNumberCopiedRows(numrows);
		int[] rowsselected=reactionsTable.getSelectedRows();  
		for (int i = 0; i < numrows; i++) {
			//starts at 1 to avoid reading hidden db id column
			for (int j = 1; j < reactionsTable.getColumnCount() - 1; j++) 
			{ 
				if (reactionsTable.getValueAt(rowsselected[i], j) != null) {
					sbf.append(reactionsTable.getValueAt(rowsselected[i], j));
				} else {
					sbf.append(" ");
				}
				if (j < reactionsTable.getColumnCount()-1) sbf.append("\t"); 
			} 
			sbf.append("\n"); 
		}  
		setClipboardContents(sbf.toString());
		System.out.println(sbf.toString());
	}
	
	public void selectReactionsColumns() {
		reactionsTable.setColumnSelectionAllowed(true);
		reactionsTable.setRowSelectionAllowed(false);
		StringBuffer sbf=new StringBuffer();
		int numcols = reactionsTable.getSelectedColumnCount(); 
		LocalConfig.getInstance().setNumberCopiedRows(reactionsTable.getRowCount());
		int[] colsselected=reactionsTable.getSelectedColumns();  
		for (int i = 0; i < reactionsTable.getRowCount(); i++) {
			for (int j = 0; j < numcols; j++) 
			{ 
				if (reactionsTable.getValueAt(i, colsselected[j]) != null) {
					sbf.append(reactionsTable.getValueAt(i, colsselected[j]));
				} else {
					sbf.append(" ");
				}
				if (j<numcols-1) sbf.append("\t"); 
			} 
			sbf.append("\n"); 
		}  
		setClipboardContents(sbf.toString());
		System.out.println(sbf.toString());
	}
	
	public void reactionsCopy() {
		StringBuffer sbf=new StringBuffer(); 
		// Check to ensure we have selected only a contiguous block of 
		// cells 
		int numcols=reactionsTable.getSelectedColumnCount(); 
		int numrows=reactionsTable.getSelectedRowCount(); 
		LocalConfig.getInstance().setNumberCopiedRows(numrows);
		int[] rowsselected=reactionsTable.getSelectedRows(); 
		int[] colsselected=reactionsTable.getSelectedColumns(); 
		if (!((numrows-1==rowsselected[rowsselected.length-1]-rowsselected[0] && 
				numrows==rowsselected.length) && 
				(numcols-1==colsselected[colsselected.length-1]-colsselected[0] && 
						numcols==colsselected.length))) 
		{ 
			JOptionPane.showMessageDialog(null, "Invalid Copy Selection", 
					"Invalid Copy Selection", 
					JOptionPane.ERROR_MESSAGE); 
			return; 
		} 
		for (int i=0;i<numrows;i++) 
		{ 
			for (int j=0;j<numcols;j++) 
			{ 
				if (reactionsTable.getValueAt(rowsselected[i],colsselected[j]) != null) {
					sbf.append(reactionsTable.getValueAt(rowsselected[i],colsselected[j]));
				} else {
					sbf.append(" ");
				}
				if (j<numcols-1) sbf.append("\t"); 
			} 
			sbf.append("\n"); 
		}  
		setClipboardContents(sbf.toString());
	}

	public void reactionsPaste() {
		String trstring = getClipboardContents(GraphicalInterface.this);
		int startRow=(reactionsTable.getSelectedRows())[0]; 
		int startCol=(reactionsTable.getSelectedColumns())[0];
		if (LocalConfig.getInstance().getNumberCopiedRows() != null && reactionsTable.getSelectedRows().length >= LocalConfig.getInstance().getNumberCopiedRows()) {
			if (LocalConfig.getInstance().getNumberCopiedRows() > 0) {
				//if selected area is larger than copied area, it will fill the same cell
				//contents repeatedly until end of selection, based on integer division
				//with no remainder
				int quotient = reactionsTable.getSelectedRows().length/LocalConfig.getInstance().getNumberCopiedRows();
				for (int r = 0; r < quotient; r++) {
					try 
					{ 
						trstring = getClipboardContents(GraphicalInterface.this);
						StringTokenizer st1=new StringTokenizer(trstring,"\n"); 
						for(int i=0;st1.hasMoreTokens();i++) 
						{ 
							String rowstring=st1.nextToken(); 
							StringTokenizer st2=new StringTokenizer(rowstring,"\t"); 
							for(int j=0;st2.hasMoreTokens();j++) 
							{ 
								String value=(String)st2.nextToken(); 
								int viewRow = 0;
								if (startRow+i< reactionsTable.getRowCount()  && 
										startCol+j< reactionsTable.getColumnCount()) 
									viewRow = GraphicalInterface.reactionsTable.convertRowIndexToModel(startRow+i);
								reactionsTable.setValueAt(value,startRow+i,startCol+j);
								updateReactionsDatabaseRow(viewRow, Integer.parseInt((String) (reactionsTable.getModel().getValueAt(viewRow, 0))), "SBML", LocalConfig.getInstance().getLoadedDatabase());
							} 
						} 
					} 
					catch(Exception ex){
						//ex.printStackTrace();
						System.out.println("Paste error");
					} 
					startRow += LocalConfig.getInstance().getNumberCopiedRows();
				}
			}
			//if selected area is smaller than copied area, fills in copied area
			//from first selected cell as upper left
		} else {
			try 
			{ 
				trstring = getClipboardContents(GraphicalInterface.this);
				StringTokenizer st1=new StringTokenizer(trstring,"\n"); 
				for(int i=0;st1.hasMoreTokens();i++) 
				{ 
					String rowstring=st1.nextToken(); 
					StringTokenizer st2=new StringTokenizer(rowstring,"\t"); 
					for(int j=0;st2.hasMoreTokens();j++) 
					{ 
						String value=(String)st2.nextToken(); 
						int viewRow = 0;
						if (startRow+i< reactionsTable.getRowCount()  && 
								startCol+j< reactionsTable.getColumnCount()) 
							viewRow = GraphicalInterface.reactionsTable.convertRowIndexToModel(startRow+i);
						reactionsTable.setValueAt(value,startRow+i,startCol+j);
						updateReactionsDatabaseRow(viewRow, Integer.parseInt((String) (reactionsTable.getModel().getValueAt(viewRow, 0))), "SBML", LocalConfig.getInstance().getLoadedDatabase());
					} 
				} 
			} 
			catch(Exception ex){
				//ex.printStackTrace();
				System.out.println("Paste error");
			} 
		}
		reactionsTable.setColumnSelectionAllowed(true);
		reactionsTable.setRowSelectionAllowed(true);
	}

	public void reactionsClear() {
		int startRow=(reactionsTable.getSelectedRows())[0]; 
		int startCol=(reactionsTable.getSelectedColumns())[0];
		try 
		{ 			
			for(int i=0; i < reactionsTable.getSelectedRows().length ;i++) 
			{ 
				for(int j=0; j < reactionsTable.getSelectedColumns().length ;j++) 
				{ 
					int viewRow = 0;
					if (startRow+i< reactionsTable.getRowCount()  && 
							startCol+j< reactionsTable.getColumnCount()) 
						viewRow = GraphicalInterface.reactionsTable.convertRowIndexToModel(startRow+i);
					reactionsTable.setValueAt(" ",startRow+i,startCol+j);
					updateReactionsDatabaseRow(viewRow, Integer.parseInt((String) (reactionsTable.getModel().getValueAt(viewRow, 0))), "SBML", LocalConfig.getInstance().getLoadedDatabase());
				} 
			} 
		} 
		catch(Exception ex){
			//ex.printStackTrace();
			System.out.println("Clear error");
		}
		reactionsTable.setColumnSelectionAllowed(true);
		reactionsTable.setRowSelectionAllowed(true);
	}

	//only works for one row, if multiple rows, would need to deal with
	//remainders (see paste - quotient). usually fill is used to fill one
	//value or row of values, not alternating rows
	public void reactionsFill(int start, int end) {
		StringBuffer sbf=new StringBuffer(); 
		// Check to ensure we have selected only a contiguous block of 
		// cells 
		int numcols=reactionsTable.getSelectedColumnCount(); 
		int row=reactionsTable.getSelectedRow(); 
		int[] colsselected=reactionsTable.getSelectedColumns(); 
		if (!(numcols-1==colsselected[colsselected.length-1]-colsselected[0] && 
						numcols==colsselected.length)) 
		{ 
			JOptionPane.showMessageDialog(null, "Invalid Copy Selection", 
					"Invalid Copy Selection", 
					JOptionPane.ERROR_MESSAGE); 
			return; 
		} 
			for (int j=0;j<numcols;j++) 
			{ 
				if (reactionsTable.getValueAt(row,colsselected[j]) != null) {
					sbf.append(reactionsTable.getValueAt(row,colsselected[j]));
				} else {
					sbf.append(" ");
				}
				if (j<numcols-1) sbf.append("\t"); 
			} 			
		setClipboardContents(sbf.toString());
		String trstring = getClipboardContents(GraphicalInterface.this);
		int startCol=(reactionsTable.getSelectedColumns())[0];	
		for (int r = start; r < end; r++) {
			try 
			{ 
					StringTokenizer st = new StringTokenizer(trstring,"\t"); 
					for(int j=0;st.hasMoreTokens();j++) 
					{ 
						String value=(String)st.nextToken();
						int	viewRow = GraphicalInterface.reactionsTable.convertRowIndexToModel(r);
						reactionsTable.setValueAt(value,r,startCol+j);
						updateReactionsDatabaseRow(viewRow, Integer.parseInt((String) (reactionsTable.getModel().getValueAt(viewRow, 0))), "SBML", LocalConfig.getInstance().getLoadedDatabase());
					} 
				
			} 
			catch(Exception ex){
				//ex.printStackTrace();
				System.out.println("Fill error");
			}
		}
	}

	public void reactionsDeleteRows() {
		int rowIndexStart = reactionsTable.getSelectedRow();
		int rowIndexEnd = reactionsTable.getSelectionModel().getMaxSelectionIndex();
		ArrayList<Integer> deleteIds = new ArrayList<Integer>();
		for (int r = rowIndexStart; r <= rowIndexEnd; r++) {
			int viewRow = GraphicalInterface.reactionsTable.convertRowIndexToModel(r);
			int id = (Integer.valueOf((String) GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.DB_REACTIONS_ID_COLUMN)));
			deleteIds.add(id);
		}
		for (int d = 0; d < deleteIds.size(); d++) {			
			ReactionFactory aFactory = new ReactionFactory();

			SBMLReaction aReaction = (SBMLReaction)aFactory.getReactionById(deleteIds.get(d), "SBML", LocalConfig.getInstance().getLoadedDatabase()); 

			if (aReaction.getReactionString() != null) {
				MetaboliteFactory mFactory = new MetaboliteFactory();
				ReactionParser parser = new ReactionParser();
				ArrayList<Integer> oldIdList = parser.speciesIdList(aReaction.getReactionString(), LocalConfig.getInstance().getLoadedDatabase());

				for (int i = 0; i < oldIdList.size(); i++) {
					//check if metabolite id is not used by other reactions before setting to false
					if ((aFactory.reactantUsedCount(oldIdList.get(i), LocalConfig.getInstance().getLoadedDatabase()) + aFactory.productUsedCount(oldIdList.get(i), LocalConfig.getInstance().getLoadedDatabase())) == 1) {
						mFactory.setMetaboliteUsedValue(oldIdList.get(i), LocalConfig.getInstance().getLoadedDatabase(), "false"); 
					}     				    	    			    
				}  
			}

			DatabaseCreator creator = new DatabaseCreator();
			creator.deleteReactionRow(LocalConfig.getInstance().getLoadedDatabase(), deleteIds.get(d));

			String fileString = "jdbc:sqlite:" + LocalConfig.getInstance().getLoadedDatabase() + ".db";
			try {
				Class.forName("org.sqlite.JDBC");
				Connection con = DriverManager.getConnection(fileString);
				setUpReactionsTable(con);
				setUpMetabolitesTable(con);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		reactionsTable.setColumnSelectionAllowed(true);
		reactionsTable.setRowSelectionAllowed(true);
	}
	
	/**************************************************************************/
	//end reactionsTable context menu methods
	/**************************************************************************/

	/**************************************************************************/
	//metabolitesTable context menu methods
	/**************************************************************************/
	
	public void selectMetabolitesRows() {
		metabolitesTable.setColumnSelectionAllowed(false);
		metabolitesTable.setRowSelectionAllowed(true);
		StringBuffer sbf=new StringBuffer();
		int numrows = metabolitesTable.getSelectedRowCount(); 
		LocalConfig.getInstance().setNumberCopiedRows(numrows);
		int[] rowsselected=metabolitesTable.getSelectedRows();  
		for (int i = 0; i < numrows; i++) {
			//starts at 1 to avoid reading hidden db id column 
			//and column count -2 to avoid reading hidden used column
			for (int j = 1; j < metabolitesTable.getColumnCount() - 2; j++) 
			{ 
				if (metabolitesTable.getValueAt(rowsselected[i], j) != null) {
					sbf.append(metabolitesTable.getValueAt(rowsselected[i], j));
				} else {
					sbf.append(" ");
				}
				if (j < metabolitesTable.getColumnCount()-1) sbf.append("\t"); 
			} 
			sbf.append("\n"); 
		}  
		setClipboardContents(sbf.toString());
		System.out.println(sbf.toString());
	}
	
	public void selectMetabolitesColumns() {
		metabolitesTable.setColumnSelectionAllowed(true);
		metabolitesTable.setRowSelectionAllowed(false);
		LocalConfig.getInstance().setNumberCopiedRows(metabolitesTable.getRowCount());
		StringBuffer sbf=new StringBuffer();
		int numcols = metabolitesTable.getSelectedColumnCount(); 
		int[] colsselected=metabolitesTable.getSelectedColumns();  
		for (int i = 0; i < metabolitesTable.getRowCount(); i++) {
			for (int j = 0; j < numcols; j++) 
			{ 
				if (metabolitesTable.getValueAt(i, colsselected[j]) != null) {
					sbf.append(metabolitesTable.getValueAt(i, colsselected[j]));
				} else {
					sbf.append(" ");
				}
				if (j<numcols-1) sbf.append("\t"); 
			} 
			sbf.append("\n"); 
		}  
		setClipboardContents(sbf.toString());
		System.out.println(sbf.toString());
	}
	
	public void metabolitesCopy() {
		StringBuffer sbf=new StringBuffer(); 
		// Check to ensure we have selected only a contiguous block of 
		// cells 
		int numcols=metabolitesTable.getSelectedColumnCount(); 
		int numrows=metabolitesTable.getSelectedRowCount(); 
		LocalConfig.getInstance().setNumberCopiedRows(numrows);
		int[] rowsselected=metabolitesTable.getSelectedRows(); 
		int[] colsselected=metabolitesTable.getSelectedColumns(); 
		if (!((numrows-1==rowsselected[rowsselected.length-1]-rowsselected[0] && 
				numrows==rowsselected.length) && 
				(numcols-1==colsselected[colsselected.length-1]-colsselected[0] && 
						numcols==colsselected.length))) 
		{ 
			JOptionPane.showMessageDialog(null, "Invalid Copy Selection", 
					"Invalid Copy Selection", 
					JOptionPane.ERROR_MESSAGE); 
			return; 
		} 
		for (int i=0;i<numrows;i++) 
		{ 
			for (int j=0;j<numcols;j++) 
			{ 
				if (metabolitesTable.getValueAt(rowsselected[i],colsselected[j]) != null) {
					sbf.append(metabolitesTable.getValueAt(rowsselected[i],colsselected[j]));
				} else {
					sbf.append(" ");
				}
				if (j<numcols-1) sbf.append("\t"); 
			} 
			sbf.append("\n"); 
		}  
		setClipboardContents(sbf.toString());
	}

	public void metabolitesPaste() {
		String trstring = getClipboardContents(GraphicalInterface.this);
		int startRow=(metabolitesTable.getSelectedRows())[0]; 
		int startCol=(metabolitesTable.getSelectedColumns())[0];
		if (LocalConfig.getInstance().getNumberCopiedRows() != null && metabolitesTable.getSelectedRows().length >= LocalConfig.getInstance().getNumberCopiedRows()) {
			if (LocalConfig.getInstance().getNumberCopiedRows() > 0) {
				//if selected area is larger than copied area, it will fill the same cell
				//contents repeatedly until end of selection, based on integer division
				//with no remainder
				int quotient = metabolitesTable.getSelectedRows().length/LocalConfig.getInstance().getNumberCopiedRows();
				for (int r = 0; r < quotient; r++) {
					try 
					{ 
						trstring = getClipboardContents(GraphicalInterface.this);
						StringTokenizer st1=new StringTokenizer(trstring,"\n"); 
						for(int i=0;st1.hasMoreTokens();i++) 
						{ 
							String rowstring=st1.nextToken(); 
							StringTokenizer st2=new StringTokenizer(rowstring,"\t"); 
							for(int j=0;st2.hasMoreTokens();j++) 
							{ 
								String value=(String)st2.nextToken(); 
								int viewRow = 0;
								if (startRow+i< metabolitesTable.getRowCount()  && 
										startCol+j< metabolitesTable.getColumnCount()) 
									viewRow = GraphicalInterface.metabolitesTable.convertRowIndexToModel(startRow+i);
								metabolitesTable.setValueAt(value,startRow+i,startCol+j);
								updateMetabolitesDatabaseRow(viewRow, Integer.parseInt((String) (metabolitesTable.getModel().getValueAt(viewRow, 0))), "SBML", LocalConfig.getInstance().getLoadedDatabase());
							} 
						} 
					} 
					catch(Exception ex){
						//ex.printStackTrace();
						System.out.println("Paste error");
					} 
					startRow += LocalConfig.getInstance().getNumberCopiedRows();
				}
			}
			//if selected area is smaller than copied area, fills in copied area
			//from first selected cell as upper left
		} else {
			try 
			{ 
				trstring = getClipboardContents(GraphicalInterface.this);
				StringTokenizer st1=new StringTokenizer(trstring,"\n"); 
				for(int i=0;st1.hasMoreTokens();i++) 
				{ 
					String rowstring=st1.nextToken(); 
					StringTokenizer st2=new StringTokenizer(rowstring,"\t"); 
					for(int j=0;st2.hasMoreTokens();j++) 
					{ 
						String value=(String)st2.nextToken(); 
						int viewRow = 0;
						if (startRow+i< metabolitesTable.getRowCount()  && 
								startCol+j< metabolitesTable.getColumnCount()) 
							viewRow = GraphicalInterface.metabolitesTable.convertRowIndexToModel(startRow+i);
						metabolitesTable.setValueAt(value,startRow+i,startCol+j);
						updateMetabolitesDatabaseRow(viewRow, Integer.parseInt((String) (metabolitesTable.getModel().getValueAt(viewRow, 0))), "SBML", LocalConfig.getInstance().getLoadedDatabase());
					} 
				} 
			} 
			catch(Exception ex){
				//ex.printStackTrace();
				System.out.println("Paste error");
			} 
		}
	}

	public void metabolitesClear() {
		int startRow=(metabolitesTable.getSelectedRows())[0]; 
		int startCol=(metabolitesTable.getSelectedColumns())[0];
		if (startCol != GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN) {
			try 
			{ 			
				for(int i=0; i < metabolitesTable.getSelectedRows().length ;i++) 
				{ 
					for(int j=0; j < metabolitesTable.getSelectedColumns().length ;j++) 
					{ 
						int viewRow = 0;
						if (startRow+i< metabolitesTable.getRowCount()  && 
								startCol+j< metabolitesTable.getColumnCount()) 
							viewRow = GraphicalInterface.metabolitesTable.convertRowIndexToModel(startRow+i);
						metabolitesTable.setValueAt(" ",startRow+i,startCol+j);
						updateMetabolitesDatabaseRow(viewRow, Integer.parseInt((String) (metabolitesTable.getModel().getValueAt(viewRow, 0))), "SBML", LocalConfig.getInstance().getLoadedDatabase());
					} 
				} 
			} 
			catch(Exception ex){
				//ex.printStackTrace();
				System.out.println("Clear error");
			}
		} else {
			System.out.println("Cannot clear Metab abbrev column, some may be used.");
		}
	}

	//only works for one row, if multiple rows, would need to deal with
	//remainders (see paste - quotient). usually fill is used to fill one
	//value or row of values, not alternating rows
	public void metabolitesFill(int start, int end) {
		StringBuffer sbf=new StringBuffer(); 
		// Check to ensure we have selected only a contiguous block of 
		// cells 
		int numcols=metabolitesTable.getSelectedColumnCount(); 
		int row=metabolitesTable.getSelectedRow(); 
		int[] colsselected=metabolitesTable.getSelectedColumns(); 
		if (!(numcols-1==colsselected[colsselected.length-1]-colsselected[0] && 
						numcols==colsselected.length)) 
		{ 
			JOptionPane.showMessageDialog(null, "Invalid Copy Selection", 
					"Invalid Copy Selection", 
					JOptionPane.ERROR_MESSAGE); 
			return; 
		} 
			for (int j=0;j<numcols;j++) 
			{ 
				if (metabolitesTable.getValueAt(row,colsselected[j]) != null) {
					sbf.append(metabolitesTable.getValueAt(row,colsselected[j]));
				} else {
					sbf.append(" ");
				}
				if (j<numcols-1) sbf.append("\t"); 
			} 			
		setClipboardContents(sbf.toString());
		String trstring = getClipboardContents(GraphicalInterface.this);
		int startCol=(metabolitesTable.getSelectedColumns())[0];	
		for (int r = start; r < end; r++) {
			try 
			{ 
					StringTokenizer st = new StringTokenizer(trstring,"\t"); 
					for(int j=0;st.hasMoreTokens();j++) 
					{ 
						String value=(String)st.nextToken();
						int	viewRow = GraphicalInterface.metabolitesTable.convertRowIndexToModel(r);
						metabolitesTable.setValueAt(value,r,startCol+j);
						updateMetabolitesDatabaseRow(viewRow, Integer.parseInt((String) (metabolitesTable.getModel().getValueAt(viewRow, 0))), "SBML", LocalConfig.getInstance().getLoadedDatabase());
					} 
				
			} 
			catch(Exception ex){
				//ex.printStackTrace();
				System.out.println("Fill error");
			}
		}
	}


	public void metaboliteDeleteRows() {
		MetaboliteFactory mFactory = new MetaboliteFactory();
		int rowIndexStart = metabolitesTable.getSelectedRow();
		int rowIndexEnd = metabolitesTable.getSelectionModel().getMaxSelectionIndex();
		ArrayList<Integer> deleteIds = new ArrayList<Integer>();
		for (int r = rowIndexStart; r <= rowIndexEnd; r++) {
			int viewRow = GraphicalInterface.metabolitesTable.convertRowIndexToModel(r);
			int id = (Integer.valueOf((String) GraphicalInterface.metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.DB_METABOLITE_ID_COLUMN)));
			deleteIds.add(id);
		}
		for (int d = 0; d < deleteIds.size(); d++) {
			if (mFactory.isUnused(deleteIds.get(d), LocalConfig.getInstance().getLoadedDatabase())) {
				DatabaseCreator creator = new DatabaseCreator();
				creator.deleteMetabolitesRow(LocalConfig.getInstance().getLoadedDatabase(), deleteIds.get(d));
				String fileString = "jdbc:sqlite:" + LocalConfig.getInstance().getLoadedDatabase() + ".db";
				try {
					Class.forName("org.sqlite.JDBC");
					Connection con = DriverManager.getConnection(fileString);
					setUpMetabolitesTable(con);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				System.out.println("Row " + deleteIds.get(d) + " cannot be deleted since it is used");
			}
		}
	}
	
	/**************************************************************************/
	//end metabolitesTable context menu methods
	/**************************************************************************/

	public static void main(String[] args) throws Exception {
		Class.forName("org.sqlite.JDBC");       
		DatabaseCreator databaseCreator = new DatabaseCreator();
		setDatabaseName(ConfigConstants.DEFAULT_DATABASE_NAME);
		LocalConfig.getInstance().setLoadedDatabase(ConfigConstants.DEFAULT_DATABASE_NAME);
		Connection con = DriverManager.getConnection("jdbc:sqlite:" + LocalConfig.getInstance().getDatabaseName() + ".db");
		databaseCreator.createDatabase(LocalConfig.getInstance().getDatabaseName());
		databaseCreator.addRows(LocalConfig.getInstance().getDatabaseName(), GraphicalInterfaceConstants.BLANK_DB_NUMBER_OF_ROWS, GraphicalInterfaceConstants.BLANK_DB_NUMBER_OF_ROWS);

		//based on code from http://stackoverflow.com/questions/6403821/how-to-add-an-image-to-a-jframe-title-bar
		final ArrayList<Image> icons = new ArrayList<Image>(); 
		icons.add(new ImageIcon("etc/most16.jpg").getImage()); 
		icons.add(new ImageIcon("etc/most32.jpg").getImage());

		GraphicalInterface frame = new GraphicalInterface(con);	   

		frame.setIconImages(icons);
		frame.setSize(1000, 600);
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		showPrompt = true;

	}
}


