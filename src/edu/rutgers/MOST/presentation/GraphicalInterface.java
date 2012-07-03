package edu.rutgers.MOST.presentation;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.xml.stream.XMLStreamException;

import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLReader;

import edu.rutgers.MOST.config.ConfigConstants;
import edu.rutgers.MOST.config.LocalConfig;
import edu.rutgers.MOST.data.DatabaseCopier;
import edu.rutgers.MOST.data.DatabaseCreator;
import edu.rutgers.MOST.data.Excel97Reader;
import edu.rutgers.MOST.data.FBAModel;
import edu.rutgers.MOST.data.MetaboliteFactory;
import edu.rutgers.MOST.data.MetabolitesMetaColumnManager;
import edu.rutgers.MOST.data.ModelReaction;
import edu.rutgers.MOST.data.ReactionFactory;
import edu.rutgers.MOST.data.ReactionsMetaColumnManager;
import edu.rutgers.MOST.data.SBMLMetabolite;
import edu.rutgers.MOST.data.SBMLModelReader;
import edu.rutgers.MOST.data.SBMLReactant;
import edu.rutgers.MOST.data.SBMLReaction;
import edu.rutgers.MOST.data.TextMetabolitesModelReader;
import edu.rutgers.MOST.data.TextReactionsModelReader;
import edu.rutgers.MOST.logic.ReactionParser;
import edu.rutgers.MOST.optimization.FBA.Optimize;
import edu.rutgers.MOST.optimization.solvers.GurobiSolver;

import java.awt.Color;
import java.awt.Event;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
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
import java.awt.event.MouseMotionAdapter;
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
import java.util.Date;
import java.util.List;
import java.util.Vector;
import gurobi.GRB;
import gurobi.GRBException;
import gurobi.GRBVar;

import org.apache.log4j.Logger;

import layout.TableLayout;

public class GraphicalInterface extends JFrame {
	//log4j
	static Logger log = Logger.getLogger(GraphicalInterface.class);
	
	public static JTable reactionsTable = new JTable();
	public static JTable metabolitesTable = new JTable();
	public static JTextArea outputTextArea = new JTextArea();
	public DefaultListModel<String> listModel = new DefaultListModel();
	//set tabs south (bottom) = 3
    public JTabbedPane tabbedPane = new JTabbedPane(3); 
    JScrollPane fileListPane = new JScrollPane(fileList);

	public static FileList fileList = new FileList();
	
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
	
	public static Integer getIdFromCurrentRow(int row) {
		   
	    int id = Integer.valueOf(reactionsTable.getModel().getValueAt(row, 0).toString());		   
	    return id;	   
	}
	
	public static Integer getMetaboliteIdFromCurrentRow(int row) {
		   
	    int id = Integer.valueOf(metabolitesTable.getModel().getValueAt(row, 0).toString());		   
	    return id;	   
	}
	
	public static String participatingMetabolite;
	
	public void setParticipatingMetabolite(String participatingMetabolite) {
		this.participatingMetabolite = participatingMetabolite;
	}
	
	public static String getParticipatingMetabolite() {
		return participatingMetabolite;
	}
	
	public static boolean highlightUnusedMetabolites;
	
	public static boolean showPrompt;
	
	final JCheckBoxMenuItem highlightUnusedMetabolitesItem = new JCheckBoxMenuItem("Highlight Unused Metabolites");
	
	ArrayList<Image> icons;
	
	public void setIconsList(ArrayList<Image> icons) {
		this.icons = icons;
	}
	
	public ArrayList<Image> getIconsList() {
		return icons;
	}
	
  @SuppressWarnings("unchecked")
public GraphicalInterface(final Connection con)
      throws SQLException {

	  setDatabaseName(ConfigConstants.DEFAULT_DATABASE_NAME);
	  
	  setTitle(GraphicalInterfaceConstants.TITLE + " - " + ConfigConstants.DEFAULT_DATABASE_NAME);
	  
	  setDefaultMetaboliteColumnNames();
	  setDefaultReactionColumnNames();
	  
	  final ArrayList<Image> icons = new ArrayList<Image>(); 
	  icons.add(new ImageIcon("etc/most16.jpg").getImage()); 
	  icons.add(new ImageIcon("etc/most32.jpg").getImage());
	  setIconsList(icons);
	  
	  /**************************************************************************/
	  //set up fileList
	  /**************************************************************************/
	  
	  fileList.setModel(listModel);
      fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); 
      
      fileList.addListSelectionListener(new ListSelectionListener() 
      { 
          public void valueChanged(ListSelectionEvent e) 
          {    
              System.out.println(fileList.getSelectedValue().toString());
              if (getOptimizePath().endsWith(fileList.getSelectedValue().toString())) {
            	  loadOutputPane(getOptimizePath() + ".log");
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

      JMenuItem loadSQLItem = new JMenuItem("Load SQLite");
      modelMenu.add(loadSQLItem);
      loadSQLItem.setMnemonic(KeyEvent.VK_Q);
      loadSQLItem.addActionListener(new LoadSQLiteItemAction());
      
      JMenuItem loadCSVMetabolitesItem = new JMenuItem("Load CSV Metabolites");
      modelMenu.add(loadCSVMetabolitesItem);
      loadCSVMetabolitesItem.setToolTipText("Metabolites File must be loaded before Reactions File");
      loadCSVMetabolitesItem.setMnemonic(KeyEvent.VK_C);
      loadCSVMetabolitesItem.addActionListener(new LoadCSVMetabolitesTableAction());
      
      JMenuItem loadTabDelimitedMetabolitesItem = new JMenuItem("Load Tab Delimited Metabolites");
      modelMenu.add(loadTabDelimitedMetabolitesItem);
      loadTabDelimitedMetabolitesItem.setToolTipText("Metabolites File must be loaded before Reactions File");
      loadTabDelimitedMetabolitesItem.setMnemonic(KeyEvent.VK_T);
      loadTabDelimitedMetabolitesItem.addActionListener(new LoadTabDelimitedMetabolitesTableAction());
      
      JMenuItem loadCSVReactionsItem = new JMenuItem("Load CSV Reactions");
      modelMenu.add(loadCSVReactionsItem);
      loadCSVReactionsItem.setToolTipText("Metabolites File must be loaded before Reactions File");
      loadCSVReactionsItem.setMnemonic(KeyEvent.VK_R);
      loadCSVReactionsItem.addActionListener(new LoadCSVReactionsTableAction());
      
      JMenuItem loadTabDelimitedReactionsItem = new JMenuItem("Load Tab Delimited Reactions");
      modelMenu.add(loadTabDelimitedReactionsItem);
      loadTabDelimitedReactionsItem.setToolTipText("Metabolites File must be loaded before Reactions File");
      loadTabDelimitedReactionsItem.setMnemonic(KeyEvent.VK_B);
      loadTabDelimitedReactionsItem.addActionListener(new LoadTabDelimitedReactionsTableAction());
      
      JMenuItem loadExcel97Item = new JMenuItem("Load Excel 97-2003 Workbook");
      modelMenu.add(loadExcel97Item);
      loadExcel97Item.setMnemonic(KeyEvent.VK_X);
      loadExcel97Item.addActionListener(new LoadExcel97Action());
      
      modelMenu.addSeparator();
      
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
				opt.setDatabaseName(getDatabaseName());// should be optimizePath
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
										getDatabaseName());
						outputText.append("\nReaction:"
								+ aReaction.getReactionAbbreviation()
								+ " Flux: " + vars.get(i).get(GRB.DoubleAttr.X));
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
    		  String fileString = "jdbc:sqlite:" + getDatabaseName() + ".db";
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
       });
      editMenu.add(highlightUnusedMetabolitesItem);
      
      JMenuItem deleteUnusedItem = new JMenuItem("Delete All Unused Metabolites");
      editMenu.add(deleteUnusedItem);
      deleteUnusedItem.setMnemonic(KeyEvent.VK_D);
    
      deleteUnusedItem.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent ae) {
        	  MetaboliteFactory mFactory = new MetaboliteFactory();
        	  mFactory.deleteAllUnusedMetabolites(getDatabaseName());
        	  try {
					String fileString = "jdbc:sqlite:" + getDatabaseName() + ".db";
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
      
      addReacRowItem.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent ae) {
              DatabaseCreator creator = new DatabaseCreator();
 	    	  creator.addReactionRow(getDatabaseName(), (reactionsTable.getModel().getRowCount() + 1));
 	    	  String fileString = "jdbc:sqlite:" + getDatabaseName() + ".db";
 	    	  try {
 				Class.forName("org.sqlite.JDBC");
 			    Connection con = DriverManager.getConnection(fileString);
 			    setUpReactionsTable(con);
 			    //set focus to id cell in new row in order to set row visible
 			    reactionsTable.changeSelection(reactionsTable.getModel().getRowCount() + 2, 0, false, false);
 			    reactionsTable.requestFocus();
 	            } catch (ClassNotFoundException e) {
 				     // TODO Auto-generated catch block
 				     e.printStackTrace();
 			    } catch (SQLException e) {
 				    // TODO Auto-generated catch block
 				    e.printStackTrace();
 			    }
          }
      });   	  
        	          	  
      JMenuItem addMetabRowItem = new JMenuItem("Add Row to Metabolites Table");      
      editMenu.add(addMetabRowItem); 
      addMetabRowItem.setMnemonic(KeyEvent.VK_M);
      
      addMetabRowItem.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent ae) {
        	  DatabaseCreator creator = new DatabaseCreator();
 	    	  creator.addMetaboliteRow(getDatabaseName(), (metabolitesTable.getModel().getRowCount() + 1));
 	    	  String fileString = "jdbc:sqlite:" + getDatabaseName() + ".db";
 	    	  try {
 				Class.forName("org.sqlite.JDBC");
 			    Connection con = DriverManager.getConnection(fileString);
 			    setUpMetabolitesTable(con);
 			    //set focus to id cell in new row in order to set row visible
 			    metabolitesTable.changeSelection(metabolitesTable.getModel().getRowCount() + 2, 0, false, false);
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
      
      menuBar.add(editMenu);
      
      //Optimize menu
      JMenu optimizeMenu = new JMenu("Optimize");
      optimizeMenu.setMnemonic(KeyEvent.VK_O);
    
      JMenuItem gdbbItem = new JMenuItem("GDBB");
      optimizeMenu.add(gdbbItem);
      
      //JMenuItem gdlsItem = new JMenuItem("GDLS");
      //optimizeMenu.add(gdlsItem);
    
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
	  setUpReactionsTable(con);
	  TableCellListener tcl = new TableCellListener(reactionsTable, rAction);
	  ReactionsPopupListener reactionsPopupListener = new ReactionsPopupListener();
	  reactionsTable.addMouseListener(reactionsPopupListener);
	  reactionsTable.setRowHeight(20);

      setUpMetabolitesTable(con);
      TableCellListener mtcl = new TableCellListener(metabolitesTable, mAction);
      MetabolitesPopupListener metabolitesPopupListener = new MetabolitesPopupListener();
	  metabolitesTable.addMouseListener(metabolitesPopupListener);
      metabolitesTable.setRowHeight(20);
      /************************************************************************/
      //end blank tables, set models and layouts
      /************************************************************************/
      
      /************************************************************************/
      //set frame layout 
      /************************************************************************/
      
      JScrollPane scrollPaneReac = new JScrollPane(reactionsTable);
      tabbedPane.addTab(GraphicalInterfaceConstants.DEFAULT_REACTION_TABLE_TAB_NAME, scrollPaneReac);
      //tabbedPane.setMnemonicAt(0, KeyEvent.VK_R);
     
      JScrollPane scrollPaneMetab = new JScrollPane(metabolitesTable);
      tabbedPane.addTab(GraphicalInterfaceConstants.DEFAULT_METABOLITE_TABLE_TAB_NAME, scrollPaneMetab);
      //tabbedPane.setMnemonicAt(1, KeyEvent.VK_B);
    	  
	  //JScrollPane fileListPane = new JScrollPane(fileList);	  	  
	  
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
  
  public void positionColumn(JTable table,int col_Index) {
	   table.moveColumn(table.getColumnCount()-1, col_Index);
  }

  public void selectCell(int row,int col, JTable table)
  {
     if(row!=-1 && col !=-1)            
     {
         table.setRowSelectionInterval(row,row);
         table.setColumnSelectionInterval(col,col);
     }
  }
    
  //from http://www.roseindia.net/java/example/java/swing/ChangeColumnName.shtml  
  public void ChangeName(JTable table, int col_index, String col_name){
	   table.getColumnModel().getColumn(col_index).setHeaderValue(col_name);
  }
  /*******************************************************************************/
  //end methods
  /*******************************************************************************/
  
  /*******************************************************************************/
  //Actions
  /*******************************************************************************/
  class LoadSBMLAction implements ActionListener {
      public void actionPerformed(ActionEvent ae) {
    	  loadSetUp();
    	  JTextArea output = null;
    	  JFileChooser fileChooser = new JFileChooser();
    	  SBMLDocument doc = new SBMLDocument();
    	  SBMLReader reader = new SBMLReader();  
          //... Open a file dialog.
          int retval = fileChooser.showOpenDialog(output);
          if (retval == JFileChooser.APPROVE_OPTION) {
              //... The user selected a file, get it, use it.
          	File file = fileChooser.getSelectedFile();
          
          	ProgressBar progress = new ProgressBar();
          	progress.setIconImages(icons);
			progress.setSize(200, 75);
			progress.setTitle("Loading...");
			progress.setVisible(true);
          	
          	String rawFilename = fileChooser.getSelectedFile().getName();
          	String filename = rawFilename.substring(0, rawFilename.length() - 4);
          	setDatabaseName(filename);         	
              try {
					doc = reader.readSBML(file);
					SBMLModelReader modelReader = new SBMLModelReader(doc);
					modelReader.setDatabaseName(filename);
					modelReader.load();
					String fileString = "jdbc:sqlite:" + getDatabaseName() + ".db";
					setUpTables(getDatabaseName());
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
				progress.setVisible(false);
				progress.dispose();
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
        	  
        	  ProgressBar progress = new ProgressBar();
        	  progress.setIconImages(icons);
			  progress.setSize(200, 75);
		      progress.setTitle("Loading...");
			  progress.setVisible(true);
        	  
              //... The user selected a file, get it, use it.
          	  String rawFilename = fileChooser.getSelectedFile().getName();
          	  String filename = rawFilename.substring(0, rawFilename.length() - 3);
          	  String rawPath = fileChooser.getSelectedFile().getPath();
          	  String path = rawPath.substring(0, rawPath.length() - 3);
          	  setDatabaseName(path);
              setUpTables(filename);
		      progress.setVisible(false);
		      progress.dispose();
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
    	setMetabolitesCSVFile(file);
    	    	
      	String rawFilename = fileChooser.getSelectedFile().getName();
      	String filename = rawFilename.substring(0, rawFilename.length() - 4);      	
      	setDatabaseName(filename);
          try {
				String fileString = "jdbc:sqlite:" + getDatabaseName() + ".db";
				Class.forName("org.sqlite.JDBC");
			    Connection con = DriverManager.getConnection(fileString);
			    TextMetabolitesModelReader reader = new TextMetabolitesModelReader();
			    ArrayList<String> columnNamesFromFile = reader.columnNamesFromFile(file);
			    MetaboliteColumnNameInterface columnNameInterface = new MetaboliteColumnNameInterface(con, columnNamesFromFile);
			    
			    columnNameInterface.setModal(true);
			    columnNameInterface.setIconImages(icons);
			    
			    columnNameInterface.setSize(600, 300);
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
 
  public void loadReactionsTextFile() {
	  loadSetUp();
	  JTextArea output = null;
	  JFileChooser fileChooser = new JFileChooser();
      //... Open a file dialog.
      int retval = fileChooser.showOpenDialog(output);
      if (retval == JFileChooser.APPROVE_OPTION) {
          //... The user selected a file, get it, use it.
    	File file = fileChooser.getSelectedFile();
    	setReactionsCSVFile(file);
    	    	      
          try {
				String fileString = "jdbc:sqlite:" + getDatabaseName() + ".db";
				Class.forName("org.sqlite.JDBC");
			    Connection con = DriverManager.getConnection(fileString);
			    TextReactionsModelReader reader = new TextReactionsModelReader();			    
			    ArrayList<String> columnNamesFromFile = reader.columnNamesFromFile(file);
			    ReactionColumnNameInterface columnNameInterface = new ReactionColumnNameInterface(con, columnNamesFromFile);
			    
			    columnNameInterface.setModal(true);
			    columnNameInterface.setIconImages(icons);
			    
			    columnNameInterface.setSize(600, 420);
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
  
  class LoadCSVMetabolitesTableAction implements ActionListener {
	  public void actionPerformed(ActionEvent ae) {
		  setSplitCharacter(',');
		  setExtension(".csv");
		  loadMetabolitesTextFile(); 
		  setUpTables(getDatabaseName());
      }
  }
  
  class LoadTabDelimitedMetabolitesTableAction implements ActionListener {
	  public void actionPerformed(ActionEvent ae) {
		  setSplitCharacter('\t');
		  setExtension(".txt");
		  loadMetabolitesTextFile();
		  setUpTables(getDatabaseName());
      }
  }
  
  class LoadCSVReactionsTableAction implements ActionListener {
	  public void actionPerformed(ActionEvent ae) {
		  setSplitCharacter(',');
		  setExtension(".csv");
		  loadReactionsTextFile();
		  setUpTables(getDatabaseName());
      }
  }
  
  class LoadTabDelimitedReactionsTableAction implements ActionListener {
	  public void actionPerformed(ActionEvent ae) {
		  setSplitCharacter('\t');
		  setExtension(".txt");
		  loadReactionsTextFile();
		  setUpTables(getDatabaseName());
      }
  }
  
  class LoadExcel97Action implements ActionListener {
      public void actionPerformed(ActionEvent ae) {
    	  loadExcel97File();
    	  setUpTables(getDatabaseName());
      }
  };
  
  public void loadExcel97File() {
	  loadSetUp();
	  JTextArea output = null;
	  JFileChooser fileChooser = new JFileChooser();
      //... Open a file dialog.
      int retval = fileChooser.showOpenDialog(output);
      if (retval == JFileChooser.APPROVE_OPTION) {
          //... The user selected a file, get it, use it.
    	  String file = fileChooser.getSelectedFile().getPath(); 
    	  setExcelPath(file);
    	  String rawFilename = fileChooser.getSelectedFile().getName();
    	  String filename = rawFilename.substring(0, rawFilename.length() - 4);
    	  System.out.println("filename " + filename);
          setDatabaseName(filename);
          System.out.println("Excel file " + file);
          System.out.println("raw " + rawFilename);
    	  if (!file.endsWith(".xls")) {
    		  JOptionPane.showMessageDialog(null,                
	    				"Not a Valid Excel File.",                
	    				"Invalid Excel File",                                
	    				JOptionPane.ERROR_MESSAGE);
    	  } else {
    		  ArrayList<String> sheetNames = new ArrayList();
    		  
    		  Excel97Reader reader = new Excel97Reader(); 
    		  String fileString = "jdbc:sqlite:" + getDatabaseName() + ".db";
  			  try {
				Class.forName("org.sqlite.JDBC");
				Connection con = DriverManager.getConnection(fileString);
				if (reader.hasStandardSheetNames(file)) {
			    	  sheetNames.add(GraphicalInterfaceConstants.STANDARD_SHEET_NAMES[0]);
			    	  sheetNames.add(GraphicalInterfaceConstants.STANDARD_SHEET_NAMES[1]);
			    	  LocalConfig.getInstance().setSheetNamesList(sheetNames);
			    	  ArrayList<String> metaboliteColumnNames = reader.metaboliteColumnNamesFromFile(getExcelPath(), sheetNames);			    	  
			    	  ArrayList<String> reactionColumnNames = reader.reactionColumnNamesFromFile(getExcelPath(), sheetNames);
			    	  ExcelColumnNameInterface columnNameInterface = new ExcelColumnNameInterface(con, metaboliteColumnNames, reactionColumnNames);
			    	  
			    	  columnNameInterface.setModal(true);
					  columnNameInterface.setIconImages(icons);
					    
					  columnNameInterface.setSize(600, 650);
					  columnNameInterface.setResizable(false);
					  columnNameInterface.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
					  columnNameInterface.setLocationRelativeTo(null);
					  columnNameInterface.setVisible(true);
					  columnNameInterface.setAlwaysOnTop(true);
			    	  //loadExcelFile(getExcelPath(), rawFilename, sheetNames);
			      } else {	
			    	  LocalConfig.getInstance().setSheetNamesList(reader.sheetNames(file));
			    	  ExcelSheetInterface sheetInterface = new ExcelSheetInterface(con);
			    	  
			    	  sheetInterface.populateBoxes(LocalConfig.getInstance().getSheetNamesList());
			    	  sheetInterface.setModal(true);
			    	  sheetInterface.setIconImages(icons);
			    	  sheetInterface.setSize(500, 230);
			    	  sheetInterface.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			    	  sheetInterface.setLocationRelativeTo(null);
			    	  sheetInterface.setVisible(true);
			    	  sheetInterface.setAlwaysOnTop(true);
			    	 
			      }
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
  
  public void saveSQLiteFile(){
	  DatabaseCopier copier = new DatabaseCopier();
	  //filename is rawFilename - extension
	  String filename = getDBFilename().substring(0, getDBFilename().length() - 3);
	  System.out.println("db n " + getDBFilename());
	  
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
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
  	  }
  }
  
  public void saveFile() {
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
        	  System.out.println("raw p " + rawPath);
        	  setDBPath(rawPath);
          	  String rawFilename = fileChooser.getSelectedFile().getName();
          	  
          	  
          	  System.out.println(getDBFilename());
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
    		    		  ProgressBar progress = new ProgressBar();
    		    		  progress.setIconImages(icons);
    	          		  progress.setSize(200, 75);
    	          		  progress.setTitle("Saving...");
    	          		  progress.setVisible(true);
    	          		  
    		    		  saveSQLiteFile();
    		    		  progress.setVisible(false);
    		    		  progress.dispose();
    		    	  } else if (confirmDialog == JOptionPane.NO_OPTION) {        		    	  
    		    		  done = false;
    		    	  } else {
    		    		  done = true;
    		    	  }       		    	  
    		      } else {
    		    	  done = true;
    		    	  ProgressBar progress = new ProgressBar();
    		    	  progress.setIconImages(icons);
	          		  progress.setSize(200, 75);
	          		  progress.setTitle("Saving...");
	          		  progress.setVisible(true);
	          		  
	          		  saveSQLiteFile();
		    		  progress.setVisible(false);
		    		  progress.dispose();
    		      }
    	      }			                  	  
          }
      }
  }
  
  class SaveSQLiteItemAction implements ActionListener {
      @SuppressWarnings("unused")
	public void actionPerformed(ActionEvent ae) {
    	  saveFile();
      }
  };
  
  class ClearAction implements ActionListener {
      public void actionPerformed(ActionEvent cae) {
    	  loadSetUp();
    	  try {
    		  Class.forName("org.sqlite.JDBC");       
    		  DatabaseCreator databaseCreator = new DatabaseCreator();
    		  setDatabaseName(ConfigConstants.DEFAULT_DATABASE_NAME);
    		  Connection con = DriverManager.getConnection("jdbc:sqlite:" + ConfigConstants.DEFAULT_DATABASE_NAME + ".db");
    		  databaseCreator.createDatabase(LocalConfig.getInstance().getDatabaseName());
    		  databaseCreator.addRows(LocalConfig.getInstance().getDatabaseName(), GraphicalInterfaceConstants.BLANK_DB_NUMBER_OF_ROWS, GraphicalInterfaceConstants.BLANK_DB_NUMBER_OF_ROWS);
    		  setUpReactionsTable(con);	
		      setUpMetabolitesTable(con);
			  setTitle(GraphicalInterfaceConstants.TITLE + " - " + ConfigConstants.DEFAULT_DATABASE_NAME);
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
      public void actionPerformed(ActionEvent e)
      {		  
          TableCellListener tcl = (TableCellListener)e.getSource();
          updateReactionsDatabaseRow(tcl.getRow(), Integer.parseInt((String) (reactionsTable.getModel().getValueAt(tcl.getRow(), 0))), "SBML", getDatabaseName());
    	  
          if (tcl.getColumn() == GraphicalInterfaceConstants.REACTION_STRING_COLUMN) {  
        	  if (tcl.getOldValue() != tcl.getNewValue()) {
        		  ReactionParser parser = new ReactionParser();
        		  ReactionFactory rFactory = new ReactionFactory();
        		  if (parser.isValid(tcl.getOldValue()) && parser.isValid(tcl.getNewValue())) {
        			  ArrayList<Integer> oldIdList = parser.speciesIdList(tcl.getOldValue(), getDatabaseName());
        			  ArrayList<Integer> newIdList = parser.speciesIdList(tcl.getNewValue(), getDatabaseName());
        			  for (int j = 0; j < newIdList.size(); j++) {
            			  if (!oldIdList.contains(newIdList.get(j))) {
            				  //check that id is not present before adding
            				  if(rFactory.usedMetaboliteCount(newIdList.get(j), getDatabaseName()) == 0) {
            				      rFactory.addIdToUsedMetabolites(newIdList.get(j), getDatabaseName());
            				  }		    
            			  }
            		  }
        			  //removes deleted species from used_metabolites if not required by other rxns
        		      for (int i = 0; i < oldIdList.size(); i++) {
        			      if (!newIdList.contains(oldIdList.get(i))) {
        				      //check if metabolite id is not used by other reactions before removing
        				      if ((rFactory.reactantUsedCount(oldIdList.get(i), getDatabaseName()) + rFactory.productUsedCount(oldIdList.get(i), getDatabaseName())) == 0) {
        				         rFactory.removeIdFromUsedMetabolites(oldIdList.get(i), getDatabaseName());
        				      }     				    
        			      }
        		      }
        		  } else if (!parser.isValid(tcl.getOldValue()) && parser.isValid(tcl.getNewValue())) {
        			  ArrayList<Integer> newIdList = parser.speciesIdList(tcl.getNewValue(), getDatabaseName());
        			  for (int j = 0; j < newIdList.size(); j++) {
            			  if(rFactory.usedMetaboliteCount(newIdList.get(j), getDatabaseName()) == 0) {
        				      rFactory.addIdToUsedMetabolites(newIdList.get(j), getDatabaseName());
        				  }
            		  }   		  
        		  }       		  
        	  }
          }           
      }
  };
  
  Action mAction = new AbstractAction()
  {
      public void actionPerformed(ActionEvent e)
      {   	  
          TableCellListener mtcl = (TableCellListener)e.getSource();
          updateMetabolitesDatabaseRow(mtcl.getRow(), Integer.parseInt((String) (metabolitesTable.getModel().getValueAt(mtcl.getRow(), 0))), "SBML", getDatabaseName());  
	  }
  };
  
  /*****************************************************************************/
  //end Actions
  /*****************************************************************************/
    
  /*******************************************************************************/
  //table layouts
  /*******************************************************************************/
  public void setReactionsTableLayout() {
	  reactionsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	  reactionsTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	  //allows individual cells to be selected as default
	  reactionsTable.setColumnSelectionAllowed(true);
	  reactionsTable.setRowSelectionAllowed(true); 
	    
	  JTableHeader header = reactionsTable.getTableHeader();

	  ColumnHeaderToolTips tips = new ColumnHeaderToolTips();
	  for (int c = 0; c < reactionsTable.getColumnCount(); c++) {
	      TableColumn col = reactionsTable.getColumnModel().getColumn(c);
	      if (c == GraphicalInterfaceConstants.BIOLOGICAL_OBJECTIVE_COLUMN) {
	    	  tips.setToolTip(col, LocalConfig.getInstance().getBiologicalObjectiveColumnName());
	      }	
	      if (c == GraphicalInterfaceConstants.REVERSIBLE_COLUMN) {
	    	  tips.setToolTip(col, LocalConfig.getInstance().getReversibleColumnName());
	      }
	      if (c == GraphicalInterfaceConstants.KO_COLUMN) {
	    	  tips.setToolTip(col, GraphicalInterfaceConstants.KNOCKOUT_TOOLTIP);
	      }	      
	  }
	  header.addMouseMotionListener(tips);
	  
	  
	  //from http://www.java2s.com/Tutorial/Java/0240__Swing/thelastcolumnismovedtothefirstposition.htm
	  reactionsTable.getTableHeader().setReorderingAllowed(false);  
	  
	  int r = reactionsTable.getModel().getColumnCount();
	  for (int i = 0; i < r; i++) {	
		  ReactionsMetaColumnManager reactionsMetaColumnManager = new ReactionsMetaColumnManager();
	      //set background of id column to grey
	      ColorTableCellRenderer reacGreyRenderer = new ColorTableCellRenderer();
	      ReactionsTableCellRenderer reacRenderer = new ReactionsTableCellRenderer();
	      MetabolitesTableCellRenderer metabRenderer = new MetabolitesTableCellRenderer();
    	  int metaColumnCount = reactionsMetaColumnManager.getMetaColumnCount(LocalConfig.getInstance().getDatabaseName());	
	  	
	      TableColumn column = reactionsTable.getColumnModel().getColumn(i);
	          if (i==GraphicalInterfaceConstants.DB_REACTIONS_ID_COLUMN) {
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
	    				  LocalConfig.getInstance().getKnockoutColumnName());
	    		  JComboBox koCombo = new JComboBox(GraphicalInterfaceConstants.BOOLEAN_VALUES);
	    		  column.setCellEditor(new DefaultCellEditor(koCombo));
	    	  }
	          if (i==GraphicalInterfaceConstants.FLUX_VALUE_COLUMN) {
	    		  ChangeName(reactionsTable, GraphicalInterfaceConstants.FLUX_VALUE_COLUMN, 
	    				  LocalConfig.getInstance().getFluxValueColumnName());
	    	  }
	    	  if (i==GraphicalInterfaceConstants.REACTION_ABBREVIATION_COLUMN) {
	    		  column.setPreferredWidth(GraphicalInterfaceConstants.REACTION_ABBREVIATION_WIDTH);//2
	    		  ChangeName(reactionsTable, GraphicalInterfaceConstants.REACTION_ABBREVIATION_COLUMN, 
	    				  LocalConfig.getInstance().getReactionAbbreviationColumnName());
	    	  }
	    	  if (i==GraphicalInterfaceConstants.REACTION_NAME_COLUMN) {
	    		  column.setPreferredWidth(GraphicalInterfaceConstants.REACTION_NAME_WIDTH);
	    		  ChangeName(reactionsTable, GraphicalInterfaceConstants.REACTION_NAME_COLUMN, 
	    				  LocalConfig.getInstance().getReactionNameColumnName());
	    	  }
	    	  if (i==GraphicalInterfaceConstants.REACTION_STRING_COLUMN) {
	    		  column.setPreferredWidth(GraphicalInterfaceConstants.REACTION_STRING_WIDTH);//3  
	    		  ChangeName(reactionsTable, GraphicalInterfaceConstants.REACTION_STRING_COLUMN, 
	    				  LocalConfig.getInstance().getReactionEquationColumnName());
	    	  }
	    	  if (i==GraphicalInterfaceConstants.REVERSIBLE_COLUMN) {
	    		  column.setPreferredWidth(GraphicalInterfaceConstants.REVERSIBLE_WIDTH);        //4
	    		  ChangeName(reactionsTable, GraphicalInterfaceConstants.REVERSIBLE_COLUMN, 
	    				  LocalConfig.getInstance().getReversibleColumnName());
	    		  //JComboBox revCombo = new JComboBox(GraphicalInterfaceConstants.BOOLEAN_VALUES);
	    		  //column.setCellEditor(new DefaultCellEditor(revCombo));
	    	  }	    	  
	    	  if (i==GraphicalInterfaceConstants.LOWER_BOUND_COLUMN) {
	    		  ChangeName(reactionsTable, GraphicalInterfaceConstants.LOWER_BOUND_COLUMN, 
	    				  LocalConfig.getInstance().getLowerBoundColumnName());
	    	  }
	    	  if (i==GraphicalInterfaceConstants.UPPER_BOUND_COLUMN) {
	    		  ChangeName(reactionsTable, GraphicalInterfaceConstants.UPPER_BOUND_COLUMN, 
	    				  LocalConfig.getInstance().getUpperBoundColumnName());
	    	  }
	    	  if (i==GraphicalInterfaceConstants.BIOLOGICAL_OBJECTIVE_COLUMN) {
	    		  ChangeName(reactionsTable, GraphicalInterfaceConstants.BIOLOGICAL_OBJECTIVE_COLUMN, 
	    				  LocalConfig.getInstance().getBiologicalObjectiveColumnName());
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
  
  public void setMetabolitesTableLayout() {	  
	  metabolitesTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	  metabolitesTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	  //allows individual cells to be selected as default
	  metabolitesTable.setColumnSelectionAllowed(true);
	  metabolitesTable.setRowSelectionAllowed(true); 
	    
	  JTableHeader header = metabolitesTable.getTableHeader();

	  ColumnHeaderToolTips tips = new ColumnHeaderToolTips();
	  for (int c = 0; c < metabolitesTable.getColumnCount(); c++) {
	      TableColumn col = metabolitesTable.getColumnModel().getColumn(c);	
	      if (c == GraphicalInterfaceConstants.CHARGE_COLUMN) {
	    	  tips.setToolTip(col, LocalConfig.getInstance().getChargeColumnName());
	      }
	  }
	  header.addMouseMotionListener(tips);
	  
	  int m = metabolitesTable.getModel().getColumnCount();
	  for (int w = 0; w < m; w++) {
		      MetabolitesMetaColumnManager metabolitesMetaColumnManager = new MetabolitesMetaColumnManager();
	    	  ColorTableCellRenderer metabGreyRenderer = new ColorTableCellRenderer();
	    	  MetabolitesTableCellRenderer metabRenderer = new MetabolitesTableCellRenderer();
	    	  int metabMetaColumnCount = metabolitesMetaColumnManager.getMetaColumnCount(LocalConfig.getInstance().getDatabaseName());	
	    	  
	    	  TableColumn column = metabolitesTable.getColumnModel().getColumn(w);
	    	  if (w==GraphicalInterfaceConstants.DB_METABOLITE_ID_COLUMN) {	
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
	    			  ChangeName(metabolitesTable, GraphicalInterfaceConstants.METABOLITE_META10_COLUMN, "O");	    					 
	    		  }
	    	  }    	    	  
	    }	  
  }
  /************************************************************************************/
  //end table layouts
  /************************************************************************************/

  /*******************************************************************************/
  //Reactions Table context menus
  /*******************************************************************************/
  
  //based on code from http://www.javakb.com/Uwe/Forum.aspx/java-programmer/21291/popupmenu-for-a-cell-in-a-JTable
  public class ReactionsPopupListener extends MouseAdapter {
  
    public void maybeShowPopup(MouseEvent e) {
      if (e.isPopupTrigger() && reactionsTable.isEnabled()) {
         Point p = new Point(e.getX(), e.getY());
         int col = reactionsTable.columnAtPoint(p);
         int row = reactionsTable.rowAtPoint(p);
         setCurrentRow(row);

         // translate table index to model index
         //int mcol = reactionsTable.getColumn(reactionsTable.getColumnName(col)).getModelIndex();

         if (row >= 0 && row < reactionsTable.getRowCount()) {
            cancelCellEditing();            
            // create popup menu...
            
            if (col == GraphicalInterfaceConstants.REACTION_STRING_COLUMN) {
            
                JPopupMenu reactionsContextMenu = createReactionsContextMenu(row,
                      col);
                if (reactionsContextMenu != null
                        && reactionsContextMenu.getComponentCount() > 0) {
                	reactionsContextMenu.show(reactionsTable, p.x, p.y);
                  }
            } else {
            	JPopupMenu contextMenu = createContextMenu(row,
                        col);
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

	       JMenuItem copyMenu = new JMenuItem();
	       copyMenu.setText("Copy");
	       copyMenu.addActionListener(new ActionListener() {
	          public void actionPerformed(ActionEvent e) {
	             Object value = reactionsTable.getModel().getValueAt(rowIndex,
	                   columnIndex);
	             setClipboardContents(value == null ? "" : value
	                   .toString());
	          }
	       });
	       reactionsContextMenu.add(copyMenu);

	       JMenuItem pasteMenu = new JMenuItem();
	       pasteMenu.setText("Paste");
	       if (isClipboardContainingText(this)
	             && reactionsTable.getModel().isCellEditable(rowIndex, columnIndex)) {
	          pasteMenu.addActionListener(new ActionListener() {
	             public void actionPerformed(ActionEvent e) {
	                String value = getClipboardContents(GraphicalInterface.this);
	                reactionsTable.getModel().setValueAt(value, rowIndex,
	                      columnIndex);
	                updateReactionsDatabaseRow(rowIndex, Integer.valueOf((String) (reactionsTable.getModel().getValueAt(rowIndex, 0))), "SBML", getDatabaseName());      
	             }
	          });
	       } else {
	          pasteMenu.setEnabled(false);
	       }
	       reactionsContextMenu.add(pasteMenu);
	       
	       JMenuItem editMenu = new JMenuItem();
	       editMenu.setText("Edit");   	   
	       editMenu.addActionListener(new ActionListener() {
	    	   @SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent e) {
	    	    	try {
						Class.forName("org.sqlite.JDBC");
						Connection con = DriverManager.getConnection("jdbc:sqlite:" + getDatabaseName() + ".db");
						ReactionInterface reactionInterface = new ReactionInterface(con);
						setReactionInterface(reactionInterface);
												
						reactionInterface.setIconImages(icons);
						reactionInterface.setSize(1000, 240);
						reactionInterface.setResizable(false);
						reactionInterface.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
						reactionInterface.setLocationRelativeTo(null);
						reactionInterface.setVisible(true);
						//reactionInterface.setModalityType(ModalityType.APPLICATION_MODAL); 
						//needs to be a dialog for this to work, but does not work anyway
						//reactionInterface.setAlwaysOnTop(true);
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
	       
	       JMenuItem addRowMenu = new JMenuItem();
	       addRowMenu.setText("Add Row");
	       if ((getCurrentRow() + 1) == reactionsTable.getModel().getRowCount()) {
	    	   addRowMenu.addActionListener(new ActionListener() {
	             public void actionPerformed(ActionEvent ae) {
	            	 DatabaseCreator creator = new DatabaseCreator();
	  	    	   creator.addReactionRow(getDatabaseName(), (getCurrentRow() + 2));
	  	    	   String fileString = "jdbc:sqlite:" + getDatabaseName() + ".db";
	  	    	   try {
	  				Class.forName("org.sqlite.JDBC");
	  			    Connection con = DriverManager.getConnection(fileString);
	  			    setUpReactionsTable(con);
	  			    //set focus to id cell in new row in order to set row visible
	  			    reactionsTable.changeSelection(getCurrentRow() + 1, 0, false, false);
	  			    reactionsTable.requestFocus();
	  	            } catch (ClassNotFoundException e) {
	  				     // TODO Auto-generated catch block
	  				     e.printStackTrace();
	  			    } catch (SQLException e) {
	  				    // TODO Auto-generated catch block
	  				    e.printStackTrace();
	  			    }
	             }
	          });
	       } else {
	    	   addRowMenu.setEnabled(false);
	       }

	       reactionsContextMenu.add(addRowMenu);
	       
	       JMenuItem deleteRowMenu = new JMenuItem();
	       deleteRowMenu.setText("Delete Row");
	       deleteRowMenu.addActionListener(new ActionListener() {
	          public void actionPerformed(ActionEvent ae) {
	        	  ReactionFactory aFactory = new ReactionFactory();

	  		      SBMLReaction aReaction = (SBMLReaction)aFactory.getReactionById(getIdFromCurrentRow(getCurrentRow()), "SBML", getDatabaseName()); 
	        	  ReactionParser parser = new ReactionParser();
	        	  ArrayList<Integer> oldIdList = parser.speciesIdList(aReaction.getReactionString(), getDatabaseName());
	        	  
	        	  for (int i = 0; i < oldIdList.size(); i++) {
	    				//check if metabolite id is not used by other reactions before removing
	    				if ((aFactory.reactantUsedCount(oldIdList.get(i), getDatabaseName()) + aFactory.productUsedCount(oldIdList.get(i), getDatabaseName())) == 1) {
	    				    aFactory.removeIdFromUsedMetabolites(oldIdList.get(i), getDatabaseName());
	    				}     				    	    			    
	    		    }   
	        	  
	              DatabaseCreator creator = new DatabaseCreator();
	              creator.deleteReactionRow(getDatabaseName(), getIdFromCurrentRow(getCurrentRow()));
	      	      //aFactory.listUsedMetabolites(getDatabaseName());
	              String fileString = "jdbc:sqlite:" + getDatabaseName() + ".db";
	  	    	   try {
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
	       });
	       reactionsContextMenu.add(deleteRowMenu);

		return reactionsContextMenu;
	  }
   
   private JPopupMenu createContextMenu(final int rowIndex,
	          final int columnIndex) {
	       JPopupMenu contextMenu = new JPopupMenu();

	       JMenuItem copyMenu = new JMenuItem();
	       copyMenu.setText("Copy");
	       copyMenu.addActionListener(new ActionListener() {
	          public void actionPerformed(ActionEvent e) {
	             Object value = reactionsTable.getModel().getValueAt(rowIndex,
	                   columnIndex);
	             setClipboardContents(value == null ? "" : value
	                   .toString());
	             //System.out.println(getClipboardContents(GraphicalInterface.this));
	          }
	       });
	       contextMenu.add(copyMenu);

	       JMenuItem pasteMenu = new JMenuItem();
	       pasteMenu.setText("Paste");
	       if (isClipboardContainingText(this)
	             && reactionsTable.getModel().isCellEditable(rowIndex, columnIndex)) {
	          pasteMenu.addActionListener(new ActionListener() {
	             public void actionPerformed(ActionEvent e) {
	                String value = getClipboardContents(GraphicalInterface.this);
	                reactionsTable.getModel().setValueAt(value, rowIndex,
	                      columnIndex);
	                updateReactionsDatabaseRow(rowIndex, Integer.valueOf((String) (reactionsTable.getModel().getValueAt(rowIndex, 0))), "SBML", getDatabaseName());      
	             }
	          });
	       } else {
	          pasteMenu.setEnabled(false);
	       }
	       contextMenu.add(pasteMenu);
	       
	       contextMenu.addSeparator();
	       
	       JMenuItem addRowMenu = new JMenuItem();
	       addRowMenu.setText("Add Row");
	       if ((getCurrentRow() + 1) == reactionsTable.getModel().getRowCount()) {
	    	   addRowMenu.addActionListener(new ActionListener() {
	             public void actionPerformed(ActionEvent ae) {
	            	 DatabaseCreator creator = new DatabaseCreator();
	  	    	   creator.addReactionRow(getDatabaseName(), (getCurrentRow() + 2));
	  	    	   String fileString = "jdbc:sqlite:" + getDatabaseName() + ".db";
	  	    	   try {
	  				Class.forName("org.sqlite.JDBC");
	  			    Connection con = DriverManager.getConnection(fileString);
	  			    setUpReactionsTable(con);
	  			    //set focus to id cell in new row in order to set row visible
	  			    reactionsTable.changeSelection(getCurrentRow() + 1, 0, false, false);
	  			    reactionsTable.requestFocus();
	  	            } catch (ClassNotFoundException e) {
	  				     // TODO Auto-generated catch block
	  				     e.printStackTrace();
	  			    } catch (SQLException e) {
	  				    // TODO Auto-generated catch block
	  				    e.printStackTrace();
	  			    }
	             }
	          });
	       } else {
	    	   addRowMenu.setEnabled(false);
	       }
	       
	       contextMenu.add(addRowMenu);
	       
	       JMenuItem deleteRowMenu = new JMenuItem();
	       deleteRowMenu.setText("Delete Row");
	       deleteRowMenu.addActionListener(new ActionListener() {
		          public void actionPerformed(ActionEvent ae) {
		        	  ReactionFactory aFactory = new ReactionFactory();

		  		      SBMLReaction aReaction = (SBMLReaction)aFactory.getReactionById(getIdFromCurrentRow(getCurrentRow()), "SBML", getDatabaseName()); 
		        	  ReactionParser parser = new ReactionParser();
		        	  ArrayList<Integer> oldIdList = parser.speciesIdList(aReaction.getReactionString(), getDatabaseName());
		        	  
		        	  for (int i = 0; i < oldIdList.size(); i++) {
		    				//check if metabolite id is not used by other reactions before removing
		    				if ((aFactory.reactantUsedCount(oldIdList.get(i), getDatabaseName()) + aFactory.productUsedCount(oldIdList.get(i), getDatabaseName())) == 1) {
		    				    aFactory.removeIdFromUsedMetabolites(oldIdList.get(i), getDatabaseName());
		    				}     				    	    			    
		    		    }   
		        	  
		              DatabaseCreator creator = new DatabaseCreator();
		              creator.deleteReactionRow(getDatabaseName(), getIdFromCurrentRow(getCurrentRow()));
		      	      //aFactory.listUsedMetabolites(getDatabaseName());
		              String fileString = "jdbc:sqlite:" + getDatabaseName() + ".db";
		  	    	   try {
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
		       });
	       contextMenu.add(deleteRowMenu);	       
	       
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
	            	JPopupMenu abbrevContextMenu = createMetaboliteAbbreviationContextMenu(row,
	                        col);
	            	// ... and show it
	            	if (abbrevContextMenu != null
	            			&& abbrevContextMenu.getComponentCount() > 0) {
	            		abbrevContextMenu.show(metabolitesTable, p.x, p.y);
	            	}
	            } else {
	            	JPopupMenu contextMenu = createMetabolitesContextMenu(row,
	                        col);
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

	       JMenuItem copyMenu = new JMenuItem();
	       copyMenu.setText("Copy");
	       copyMenu.addActionListener(new ActionListener() {
	          public void actionPerformed(ActionEvent e) {
	             Object value = metabolitesTable.getModel().getValueAt(rowIndex,
	                   columnIndex);
	             setClipboardContents(value == null ? "" : value
	                   .toString());
	             //System.out.println(getClipboardContents(GraphicalInterface.this));
	          }
	       });
	       contextMenu.add(copyMenu);

	       JMenuItem pasteMenu = new JMenuItem();
	       pasteMenu.setText("Paste");
	       if (isClipboardContainingText(this)
	             && metabolitesTable.getModel().isCellEditable(rowIndex, columnIndex)) {
	          pasteMenu.addActionListener(new ActionListener() {
	             public void actionPerformed(ActionEvent e) {
	                String value = getClipboardContents(GraphicalInterface.this);
	                metabolitesTable.getModel().setValueAt(value, rowIndex,
	                      columnIndex);
	                updateMetabolitesDatabaseRow(rowIndex, Integer.valueOf((String) (metabolitesTable.getModel().getValueAt(rowIndex, 0))), "SBML", getDatabaseName());      
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
			   setParticipatingMetabolite((String) metabolitesTable.getModel().getValueAt(rowIndex, columnIndex));
		       }
		   });
	       contextMenu.add(participatingReactionsMenu);
	       
	       final JMenuItem unhighlightParticipatingReactionsMenu = new JMenuItem();
	       unhighlightParticipatingReactionsMenu.setText("Unhighlight Participating Reactions");
	       if (abbreviation == null || getParticipatingMetabolite() == null || getParticipatingMetabolite() == "   ") {
	    	   unhighlightParticipatingReactionsMenu.setEnabled(false);
	       }
	       unhighlightParticipatingReactionsMenu.addActionListener(new ActionListener() {
		   public void actionPerformed(ActionEvent e) {
			   //to make sure that if user entered two spaces in reaction equation - still no highlight results
			   setParticipatingMetabolite("   "); 
		       }
		   });
	       contextMenu.add(unhighlightParticipatingReactionsMenu);
	       
	       contextMenu.addSeparator();
	       
	       JMenuItem addRowMenu = new JMenuItem();
	       addRowMenu.setText("Add Row");
	       if ((getCurrentRow() + 1) == metabolitesTable.getModel().getRowCount()) {
	    	   addRowMenu.addActionListener(new ActionListener() {
	             public void actionPerformed(ActionEvent ae) {
	            	 DatabaseCreator creator = new DatabaseCreator();
	  	    	   creator.addMetaboliteRow(getDatabaseName(), (getCurrentRow() + 2));
	  	    	   String fileString = "jdbc:sqlite:" + getDatabaseName() + ".db";
	  	    	   try {
	  				Class.forName("org.sqlite.JDBC");
	  			    Connection con = DriverManager.getConnection(fileString);
	  			    setUpMetabolitesTable(con);
	  			    //set focus to id cell in new row in order to set row visible
	  			    metabolitesTable.changeSelection(getCurrentRow() + 1, 0, false, false);
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
	       } else {
	    	   addRowMenu.setEnabled(false);
	       }

	       contextMenu.add(addRowMenu);
	       
	       JMenuItem deleteRowMenu = new JMenuItem();
	       deleteRowMenu.setText("Delete Row");
	       
	       MetaboliteFactory mFactory = new MetaboliteFactory();
	       if (mFactory.isUnused(getMetaboliteIdFromCurrentRow(getCurrentRow()), getDatabaseName())) {
	    	   deleteRowMenu.setEnabled(true);
	    	   deleteRowMenu.addActionListener(new ActionListener() {
	 	          public void actionPerformed(ActionEvent ae) {
	 	             DatabaseCreator creator = new DatabaseCreator();
	 	             creator.deleteMetabolitesRow(getDatabaseName(), getMetaboliteIdFromCurrentRow(getCurrentRow()));
	 	             String fileString = "jdbc:sqlite:" + getDatabaseName() + ".db";
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
	 	       });
	       } else {
	    	   deleteRowMenu.setEnabled(false);
	       }
	       
	       contextMenu.add(deleteRowMenu);
	       
	       return contextMenu;
	  }
   
   private JPopupMenu createMetabolitesContextMenu(final int rowIndex,
	          final int columnIndex) {
	       JPopupMenu contextMenu = new JPopupMenu();

	       JMenuItem copyMenu = new JMenuItem();
	       copyMenu.setText("Copy");
	       copyMenu.addActionListener(new ActionListener() {
	          public void actionPerformed(ActionEvent e) {
	             Object value = metabolitesTable.getModel().getValueAt(rowIndex,
	                   columnIndex);
	             setClipboardContents(value == null ? "" : value
	                   .toString());
	             //System.out.println(getClipboardContents(GraphicalInterface.this));
	          }
	       });
	       contextMenu.add(copyMenu);

	       JMenuItem pasteMenu = new JMenuItem();
	       pasteMenu.setText("Paste");
	       if (isClipboardContainingText(this)
	             && metabolitesTable.getModel().isCellEditable(rowIndex, columnIndex)) {
	          pasteMenu.addActionListener(new ActionListener() {
	             public void actionPerformed(ActionEvent e) {
	                String value = getClipboardContents(GraphicalInterface.this);
	                metabolitesTable.getModel().setValueAt(value, rowIndex,
	                      columnIndex);
	                updateMetabolitesDatabaseRow(rowIndex, Integer.valueOf((String) (metabolitesTable.getModel().getValueAt(rowIndex, 0))), "SBML", getDatabaseName());      
	             }
	          });
	       } else {
	          pasteMenu.setEnabled(false);
	       }
	       contextMenu.add(pasteMenu);
	       
	       contextMenu.addSeparator();
	       	       
	       JMenuItem addRowMenu = new JMenuItem();
	       addRowMenu.setText("Add Row");
	       if ((getCurrentRow() + 1) == metabolitesTable.getModel().getRowCount()) {
	    	   addRowMenu.addActionListener(new ActionListener() {
	             public void actionPerformed(ActionEvent ae) {
	            	 DatabaseCreator creator = new DatabaseCreator();
	  	    	   creator.addMetaboliteRow(getDatabaseName(), (getCurrentRow() + 2));
	  	    	   String fileString = "jdbc:sqlite:" + getDatabaseName() + ".db";
	  	    	   try {
	  				Class.forName("org.sqlite.JDBC");
	  			    Connection con = DriverManager.getConnection(fileString);
	  			    setUpMetabolitesTable(con);
	  			    //set focus to id cell in new row in order to set row visible
	  			    metabolitesTable.changeSelection(getCurrentRow() + 1, 0, false, false);
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
	       } else {
	    	   addRowMenu.setEnabled(false);
	       }

	       contextMenu.add(addRowMenu);
	       
	       JMenuItem deleteRowMenu = new JMenuItem();
	       deleteRowMenu.setText("Delete Row");
	       
	       MetaboliteFactory mFactory = new MetaboliteFactory();
	       if (mFactory.isUnused(getMetaboliteIdFromCurrentRow(getCurrentRow()), getDatabaseName())) {
	    	   deleteRowMenu.setEnabled(true);
	    	   deleteRowMenu.addActionListener(new ActionListener() {
	 	          public void actionPerformed(ActionEvent ae) {
	 	             DatabaseCreator creator = new DatabaseCreator();
	 	             creator.deleteMetabolitesRow(getDatabaseName(), getMetaboliteIdFromCurrentRow(getCurrentRow()));
	 	             String fileString = "jdbc:sqlite:" + getDatabaseName() + ".db";
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
	 	       });
	       } else {
	    	   deleteRowMenu.setEnabled(false);
	       }
	       
	       
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
		public void actionPerformed(ActionEvent e) {
			reactionInterface.setReactionEquation(reactionInterface.reactionField.getText());
		    reactionsTable.getModel().setValueAt(reactionInterface.getReactionEquation(), getCurrentRow(), GraphicalInterfaceConstants.REACTION_STRING_COLUMN);
		    updateReactionsDatabaseRow(getCurrentRow(), Integer.parseInt((String) (reactionsTable.getModel().getValueAt(getCurrentRow(), 0))), "SBML", getDatabaseName());
		    
		    ReactionFactory rFactory = new ReactionFactory();
            SBMLReaction aReaction = (SBMLReaction)rFactory.getReactionById(getIdFromCurrentRow(getCurrentRow()), "SBML", getDatabaseName()); 
            ReactionParser parser = new ReactionParser();
		    if (parser.isValid(aReaction.getReactionString())) {
	    		    ArrayList reactantsAndProducts = parser.parseReaction(reactionInterface.getReactionEquation(), getIdFromCurrentRow(getCurrentRow()));
	    		    aReaction.setReactantsList((ArrayList) reactantsAndProducts.get(0));
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
		    	ArrayList<Integer> oldIdList = parser.speciesIdList(reactionInterface.getOldReaction(), getDatabaseName());
			    ArrayList<Integer> newIdList = parser.speciesIdList(reactionInterface.getReactionEquation(), getDatabaseName());
			    //adds new species to used_metabolites table
      		    for (int j = 0; j < newIdList.size(); j++) {
      			    if (!oldIdList.contains(newIdList.get(j))) {
      				    //check that id is not present before adding
      				    if(rFactory.usedMetaboliteCount(newIdList.get(j), getDatabaseName()) == 0) {
      				    	rFactory.addIdToUsedMetabolites(newIdList.get(j), getDatabaseName());
      				    }		    
      			    }
      		    }
      		    //removes deleted species from used_metabolites if not required by other rxns
    		    for (int i = 0; i < oldIdList.size(); i++) {
    			    if (!newIdList.contains(oldIdList.get(i))) {
    				    //check if metabolite id is not used by other reactions before removing
    				    if ((rFactory.reactantUsedCount(oldIdList.get(i), getDatabaseName()) + rFactory.productUsedCount(oldIdList.get(i), getDatabaseName())) == 0) {
    				    	rFactory.removeIdFromUsedMetabolites(oldIdList.get(i), getDatabaseName());
    				    }     				    
    			    }
    		    }    
		    } else if (!parser.isValid(reactionInterface.getOldReaction()) && parser.isValid(reactionInterface.getReactionEquation())) {
		    	ArrayList<Integer> newIdList = parser.speciesIdList(reactionInterface.getReactionEquation(), getDatabaseName());
		    	for (int j = 0; j < newIdList.size(); j++) {
      				//check that id is not present before adding
      				if(rFactory.usedMetaboliteCount(newIdList.get(j), getDatabaseName()) == 0) {
      				    rFactory.addIdToUsedMetabolites(newIdList.get(j), getDatabaseName());
      				}		          			    
      		    }		    	
		    }		    
	    }
	}; 

    
  /***********************************************************************************/
  //clipboard
  /***********************************************************************************/
  
  //from http://www.javakb.com/Uwe/Forum.aspx/java-programmer/21291/popupmenu-for-a-cell-in-a-JTable
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
  
    public void setUpMetabolitesTable(Connection con) {
        	   
		try {
			MetabolitesDatabaseTableModel metabModel = new MetabolitesDatabaseTableModel(con, new String("select * from metabolites"));
			metabolitesTable.setModel(metabModel);
			setMetabolitesTableLayout();
			RowSorter<TableModel> sorter =
		          new TableRowSorter<TableModel>(metabModel);
			metabolitesTable.setRowSorter(sorter);
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
			setParticipatingMetabolite("   ");
			RowSorter<TableModel> sorter =
		          new TableRowSorter<TableModel>(reacModel);
			reactionsTable.setRowSorter(sorter);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	   
    }
   
    public void setUpTables(String filename) {
    	try {
			String fileString = "jdbc:sqlite:" + getDatabaseName() + ".db";
			Class.forName("org.sqlite.JDBC");
		    Connection con = DriverManager.getConnection(fileString);			    
		    setUpMetabolitesTable(con);
		    setUpReactionsTable(con);
		    //set focus to top left
			metabolitesTable.changeSelection(0, 0, false, false);
			metabolitesTable.requestFocus();
			reactionsTable.changeSelection(0, 0, false, false);
			reactionsTable.requestFocus();
			setTitle(GraphicalInterfaceConstants.TITLE + " - " + filename);
			tabbedPane.setTitleAt(0, GraphicalInterfaceConstants.DEFAULT_REACTION_TABLE_TAB_NAME);
			tabbedPane.setTitleAt(1, GraphicalInterfaceConstants.DEFAULT_METABOLITE_TABLE_TAB_NAME);
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
    
    //sets parameters to initial values on load
    public void loadSetUp() {
    	showPrompt = true;
  	    highlightUnusedMetabolites = false;
  	    highlightUnusedMetabolitesItem.setState(false);  	    
    }
    
    public void setDefaultMetaboliteColumnNames() {
    	LocalConfig.getInstance().setMetaboliteAbbreviationColumnName(GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES[GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN]);
    	LocalConfig.getInstance().setMetaboliteNameColumnName(GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES[GraphicalInterfaceConstants.METABOLITE_NAME_COLUMN]);
    	LocalConfig.getInstance().setChargeColumnName(GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES[GraphicalInterfaceConstants.CHARGE_COLUMN]); 
    	LocalConfig.getInstance().setCompartmentColumnName(GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES[GraphicalInterfaceConstants.COMPARTMENT_COLUMN]); 	
    }
    
    public void setDefaultReactionColumnNames() {
    	LocalConfig.getInstance().setReactionAbbreviationColumnName(GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES[GraphicalInterfaceConstants.REACTION_ABBREVIATION_COLUMN]); 
    	LocalConfig.getInstance().setReactionNameColumnName(GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES[GraphicalInterfaceConstants.REACTION_NAME_COLUMN]);  
    	LocalConfig.getInstance().setReactionEquationColumnName(GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES[GraphicalInterfaceConstants.REACTION_STRING_COLUMN]); 
    	LocalConfig.getInstance().setKnockoutColumnName(GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES[GraphicalInterfaceConstants.KO_COLUMN]);   
    	LocalConfig.getInstance().setFluxValueColumnName(GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES[GraphicalInterfaceConstants.FLUX_VALUE_COLUMN]); 
    	LocalConfig.getInstance().setReversibleColumnName(GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES[GraphicalInterfaceConstants.REVERSIBLE_COLUMN]); 
    	LocalConfig.getInstance().setLowerBoundColumnName(GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES[GraphicalInterfaceConstants.LOWER_BOUND_COLUMN]); 
    	LocalConfig.getInstance().setUpperBoundColumnName(GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES[GraphicalInterfaceConstants.UPPER_BOUND_COLUMN]);     
    	LocalConfig.getInstance().setBiologicalObjectiveColumnName(GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES[GraphicalInterfaceConstants.BIOLOGICAL_OBJECTIVE_COLUMN]); 
    }
    
   /******************************************************************************/
   //update database methods, called when table rows changed 
   /******************************************************************************/
   
   public void updateReactionsDatabaseRow(int rowIndex, Integer reactionId, String sourceType, String databaseName)  {

	    try { 
	    	ReactionFactory aFactory = new ReactionFactory();

		    SBMLReaction aReaction = (SBMLReaction)aFactory.getReactionById(reactionId, sourceType, databaseName); 
		    aReaction.setKnockout((String) reactionsTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.KO_COLUMN));			
		    
		    if (reactionsTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.FLUX_VALUE_COLUMN) != null) {
		        aReaction.setFluxValue(Double.valueOf((String) reactionsTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.FLUX_VALUE_COLUMN)));
		    }
		    
		    aReaction.setReactionAbbreviation((String) reactionsTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.REACTION_ABBREVIATION_COLUMN));
		    aReaction.setReactionName((String) reactionsTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.REACTION_NAME_COLUMN));
		    aReaction.setReactionString((String) reactionsTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.REACTION_STRING_COLUMN));		    
		    
		    if (aReaction.getReactionString() != null) {
		    	if (aReaction.getReactionString().contains("<") || aReaction.getReactionString().contains("=")) {
		    		aReaction.setReversible("true");
		    	} else if (aReaction.getReactionString().contains("-->") || aReaction.getReactionString().contains("->")) {
		    		aReaction.setReversible("false");		    		
		    	}
		    		
		    	ReactionParser parser = new ReactionParser();
		    	if (parser.isValid(aReaction.getReactionString())) {
		    		ArrayList reactantsAndProducts = parser.parseReaction(aReaction.getReactionString(), reactionId);
		    		aReaction.setReactantsList((ArrayList) reactantsAndProducts.get(0));
			    	aReaction.clearReactants();
			    	aReaction.updateReactants();
			    	if (reactantsAndProducts.size() > 1) {
			    		aReaction.setProductsList((ArrayList) reactantsAndProducts.get(1));
			    		aReaction.clearProducts();
			    		aReaction.updateProducts();
			    	}			 	    
			    	Class.forName("org.sqlite.JDBC");       
		  		    Connection con = DriverManager.getConnection("jdbc:sqlite:" + databaseName + ".db");
			    	//refresh metabolites table in case any new metabolites are added
		  		    setUpMetabolitesTable(con);
		    	} else {
		    		JOptionPane.showMessageDialog(null,                
		    				"Not a valid reaction.",                
		    				"Invalid Reaction Warning",                                
		    				JOptionPane.ERROR_MESSAGE);                              

		    		aReaction.clearReactants();
			    	aReaction.clearProducts();
		    	}
		    } else {
		    	aReaction.clearReactants();
		    	aReaction.clearProducts();
		    }
		  
		    //string cannot be cast to double but valueOf works, from http://www.java-examples.com/convert-java-string-double-example		    
		    if (reactionsTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.LOWER_BOUND_COLUMN) != null) {
		        aReaction.setLowerBound(Double.valueOf((String) reactionsTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.LOWER_BOUND_COLUMN)));
		    } else {
		    	aReaction.setLowerBound(-999999);
		    }
		    if (reactionsTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.UPPER_BOUND_COLUMN) != null) {
		        aReaction.setUpperBound(Double.valueOf((String) reactionsTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.UPPER_BOUND_COLUMN)));
		    } else {
		    	aReaction.setUpperBound(999999);
		    }
		    if (reactionsTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.BIOLOGICAL_OBJECTIVE_COLUMN) != null) {
		        aReaction.setBiologicalObjective(Double.valueOf((String) reactionsTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.BIOLOGICAL_OBJECTIVE_COLUMN)));
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
			
			//aFactory.listUsedMetabolites(getDatabaseName());
			
			String fileString = "jdbc:sqlite:" + databaseName + ".db";
			Class.forName("org.sqlite.JDBC");
		    Connection con = DriverManager.getConnection(fileString);
            setUpReactionsTable(con);
			
	    } catch (Exception e1) {
		    // TODO Auto-generated catch block
		    e1.printStackTrace();			
        }
	}
   
   public void updateMetabolitesDatabaseRow(int rowIndex, Integer metaboliteId, String sourceType, String databaseName)  {
   	try {
		    MetaboliteFactory aFactory = new MetaboliteFactory();
		    SBMLMetabolite aMetabolite = (SBMLMetabolite)aFactory.getMetaboliteById(Integer.parseInt((String) metabolitesTable.getModel().getValueAt(rowIndex, 0)), "SBML", getDatabaseName()); 
		  
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
		    
		    aMetabolite.update();
		  
	  } catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();			
     }    
   }
   
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

   
   /******************************************************************************/
   //end update database methods
   /******************************************************************************/
   
   public static void main(String[] args) throws Exception {
	   Class.forName("org.sqlite.JDBC");       
	   DatabaseCreator databaseCreator = new DatabaseCreator();
	   setDatabaseName(ConfigConstants.DEFAULT_DATABASE_NAME);
	   Connection con = DriverManager.getConnection("jdbc:sqlite:" + LocalConfig.getInstance().getDatabaseName() + ".db");
	   databaseCreator.createDatabase(LocalConfig.getInstance().getDatabaseName());
	   databaseCreator.addRows(LocalConfig.getInstance().getDatabaseName(), GraphicalInterfaceConstants.BLANK_DB_NUMBER_OF_ROWS, GraphicalInterfaceConstants.BLANK_DB_NUMBER_OF_ROWS);
	   
	   //based on code from http://stackoverflow.com/questions/6403821/how-to-add-an-image-to-a-jframe-title-bar
	   final ArrayList<Image> icons = new ArrayList<Image>(); 
       icons.add(new ImageIcon("etc/most16.jpg").getImage()); 
       icons.add(new ImageIcon("etc/most32.jpg").getImage());
       
	   GraphicalInterface frame = new GraphicalInterface(con);	   
	   
	   frame.setIconImages(icons);
	   //frame.setIconImage(new ImageIcon("etc/most16.jpg").getImage());
	   frame.setSize(1000, 600);
	   frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
	   frame.setLocationRelativeTo(null);
	   frame.setVisible(true);
	   
	   ReactionFactory aFactory = new ReactionFactory();
	   aFactory.listUsedMetabolites(getDatabaseName());
	   
	   showPrompt = true;

	   loadOutputPane("C://testing.log");
  }
}


