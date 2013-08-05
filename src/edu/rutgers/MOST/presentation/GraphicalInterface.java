package edu.rutgers.MOST.presentation;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.xml.stream.XMLStreamException;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.HighlightPredicate;

import org.rutgers.MOST.tree.DynamicTree;
import org.rutgers.MOST.tree.DynamicTreeDemo;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLReader;

import edu.rutgers.MOST.config.ConfigConstants;
import edu.rutgers.MOST.config.LocalConfig;
import edu.rutgers.MOST.data.DatabaseCopier;
import edu.rutgers.MOST.data.DatabaseCreator;
import edu.rutgers.MOST.data.FBAModel;
import edu.rutgers.MOST.data.GDBBModel;
import edu.rutgers.MOST.data.JSBMLWriter;
import edu.rutgers.MOST.data.MetaboliteFactory;
import edu.rutgers.MOST.data.MetaboliteUndoItem;
import edu.rutgers.MOST.data.MetabolitesMetaColumnManager;
import edu.rutgers.MOST.data.MetabolitesUpdater;
import edu.rutgers.MOST.data.ObjectCloner;
import edu.rutgers.MOST.data.ReactionFactory;
import edu.rutgers.MOST.data.ReactionUndoItem;
import edu.rutgers.MOST.data.ReactionsMetaColumnManager;
import edu.rutgers.MOST.data.ReactionsUpdater;
import edu.rutgers.MOST.data.SBMLMetabolite;
import edu.rutgers.MOST.data.SBMLModelReader;
import edu.rutgers.MOST.data.SBMLReaction;
import edu.rutgers.MOST.data.SQLiteLoader;
import edu.rutgers.MOST.data.SettingsFactory;
import edu.rutgers.MOST.data.Solution;
import edu.rutgers.MOST.data.TextMetabolitesModelReader;
import edu.rutgers.MOST.data.TextMetabolitesWriter;
import edu.rutgers.MOST.data.TextReactionsModelReader;
import edu.rutgers.MOST.data.TextReactionsWriter;
import edu.rutgers.MOST.data.UndoConstants;
import edu.rutgers.MOST.logic.ReactionParser;
import edu.rutgers.MOST.optimization.FBA.FBA;
import edu.rutgers.MOST.optimization.GDBB.GDBB;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;

import layout.TableLayout;

public class GraphicalInterface extends JFrame {
	//log4j
	static Logger log = Logger.getLogger(GraphicalInterface.class);

	public static JToolBar toolbar = new JToolBar("Toolbar", JToolBar.HORIZONTAL);
	public static JButton openbutton = new JButton(new ImageIcon(GraphicalInterfaceConstants.OPEN_ICON_IMAGE_PATH));
	public static JButton savebutton = new JButton(new ImageIcon(GraphicalInterfaceConstants.SAVE_ICON_IMAGE_PATH));
	public static JButton copybutton = new JButton(new ImageIcon(GraphicalInterfaceConstants.COPY_ICON_IMAGE_PATH));
	public static JButton pastebutton = new JButton(new ImageIcon(GraphicalInterfaceConstants.PASTE_ICON_IMAGE_PATH));
	public static JButton findbutton = new JButton(new ImageIcon(GraphicalInterfaceConstants.FIND_ICON_IMAGE_PATH));
	public static OptionComponent undoSplitButton = new OptionComponent("image", "");
	public static OptionComponent redoSplitButton = new OptionComponent("image", "");	
	public static JLabel undoLabel = new JLabel(new ImageIcon(GraphicalInterfaceConstants.UNDO_ICON_IMAGE_PATH));
	public static JLabel redoLabel = new JLabel(new ImageIcon(GraphicalInterfaceConstants.REDO_ICON_IMAGE_PATH));
	public static JLabel undoGrayedLabel = new JLabel(new ImageIcon(GraphicalInterfaceConstants.UNDO_GRAYED_ICON_IMAGE_PATH));
	public static JLabel redoGrayedLabel = new JLabel(new ImageIcon(GraphicalInterfaceConstants.REDO_GRAYED_ICON_IMAGE_PATH));
	
	// Excel calls this a formula bar
	public static JTextField formulaBar = new JTextField();
	
	public static JXTable reactionsTable = new JXTable();	
	// based on http://www.coderanch.com/t/345041/GUI/java/Disabling-cell-JTable
	public static JXTable metabolitesTable = new JXTable(){  
		public boolean isCellEditable(int row, int column){	    	  
			Object o = getValueAt(row, column); 
			String s = (String) getValueAt(row, column); 
			int id = 0;
			if (column == GraphicalInterfaceConstants.DB_METABOLITE_ID_COLUMN) {				
				return false;
			} else if (fileList.getSelectedIndex() > 0) {
				return false;					
			} else if (column == GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN && o != null && LocalConfig.getInstance().getMetaboliteUsedMap().containsKey(s)) {
				id = Integer.valueOf((String) getValueAt(row, GraphicalInterfaceConstants.DB_METABOLITE_ID_COLUMN));
				if (LocalConfig.getInstance().getDuplicateIds().contains(id)) {
					return true;
				} else {
					return false;
				}				  
			}
			return true;  
		}  
	};  
	
	public static JTextArea outputTextArea = new JTextArea();
	
	public static JLabel statusBar = new JLabel();

	//set tabs south (bottom) = 3
	public static JTabbedPane tabbedPane = new JTabbedPane(3); 

	//Methods of saving current directory
	public static SettingsFactory curSettings;
	
	public static DefaultListModel<String> listModel = new DefaultListModel();
	public static FileList fileList = new FileList();
	static JScrollPane fileListPane = new JScrollPane(fileList);	

	protected GDBBTask gdbbTask;

	protected GraphicalInterface gi;
	
	private Task task;	
	public final ProgressBar progressBar = new ProgressBar();	
	javax.swing.Timer timer = new javax.swing.Timer(100, new TimeListener());
	
	private TextInputDemo textInput;

	/*****************************************************************************/
	// boolean values
	/*****************************************************************************/
	
	public static boolean showPrompt;
	// selection values
	public static boolean includeRxnColumnNames;
	public static boolean includeMtbColumnNames;
	// load values
	public static boolean isCSVFile;
	public static boolean validFile;
	// highlighting
	public static boolean highlightUnusedMetabolites;	
	public static boolean highlightParticipatingRxns;
	// listener values
	public static boolean selectedCellChanged;
	public static boolean formulaBarFocusGained;
	public static boolean tabChanged;
	// find-replace values 
	public static boolean findMode;
	public static boolean findButtonReactionsClicked;
	public static boolean findButtonMetabolitesClicked;
	public static boolean matchCase;
	public static boolean wrapAround;
	public static boolean searchSelectedArea;
	public static boolean searchBackwards;
	public static boolean reactionUpdateValid;
	public static boolean metaboliteUpdateValid;
	public static boolean replaceAllMode;
	public static boolean reactionsFindAll;
	public static boolean metabolitesFindAll;
	public static boolean throwNotFoundError;
	public static boolean changeReactionFindSelection;
	public static boolean changeMetaboliteFindSelection;
	// paste
	public static boolean validPaste;                 // used for error message when pasting non-valid values
	public static boolean pasting;
	public static boolean showDuplicatePrompt;
	public static boolean duplicateMetabOK;
	// other
	public static boolean showErrorMessage;
	public static boolean saveOptFile;
	public static boolean addReacColumn;              // used to scroll added column to visible
	public static boolean addMetabColumn;             // used to scroll added column to visible
	public static boolean duplicatePromptShown;		  // ensures "Duplicate Metabolite" prompt displayed once per event
	public static boolean renameMetabolite;           // if Rename menu action determines used, is set to true to for OK button action          
	public static boolean reactionsTableEditable;	  // if fileList item index > 0, is false
	public static boolean modelCollectionOKButtonClicked;
	public static boolean reactionCancelLoad;
	public static boolean isRoot;
	// close
	public static boolean exit;
	public static boolean saveDatabaseFile;

	/*****************************************************************************/
	// end boolean values
	/*****************************************************************************/

	/*****************************************************************************/
	// components
	/*****************************************************************************/		
	
	public final CSVLoadInterface csvLoadInterface = new CSVLoadInterface();
	
	private DynamicTreeDemo newContentPane;
	
    public static FindReplaceDialog findReplaceDialog;
	
	public void setFindReplaceDialog(FindReplaceDialog findReplaceDialog) {
		GraphicalInterface.findReplaceDialog = findReplaceDialog;
	}

	public static FindReplaceDialog getFindReplaceDialog() {
		return findReplaceDialog;
	}
	
    public static MetaboliteColAddRenameInterface metaboliteColAddRenameInterface;   
	
	public void setMetaboliteColAddRenameInterface(MetaboliteColAddRenameInterface metaboliteColAddRenameInterface) {
		GraphicalInterface.metaboliteColAddRenameInterface = metaboliteColAddRenameInterface;
	}

	public static MetaboliteColAddRenameInterface getMetaboliteColAddRenameInterface() {
		return metaboliteColAddRenameInterface;
	}
	
	public static MetaboliteColumnNameInterface metaboliteColumnNameInterface;
	
	public static MetaboliteColumnNameInterface getMetaboliteColumnNameInterface() {
		return metaboliteColumnNameInterface;
	}

	public static void setMetaboliteColumnNameInterface(
			MetaboliteColumnNameInterface metaboliteColumnNameInterface) {
		GraphicalInterface.metaboliteColumnNameInterface = metaboliteColumnNameInterface;
	}

	public static MetaboliteRenameInterface metaboliteRenameInterface;
	
	public void setMetaboliteRenameInterface(MetaboliteRenameInterface metaboliteRenameInterface) {
		GraphicalInterface.metaboliteRenameInterface = metaboliteRenameInterface;
	}

	public static MetaboliteRenameInterface getMetaboliteRenameInterface() {
		return metaboliteRenameInterface;
	}
	
	public static ModelCollectionTable modelCollectionTable;
	
	public static ModelCollectionTable getModelCollectionTable() {
		return modelCollectionTable;
	}

	public static void setModelCollectionTable(ModelCollectionTable modelCollectionTable) {
		GraphicalInterface.modelCollectionTable = modelCollectionTable;
	}

	public static OutputPopout popout;

	public void setPopout(OutputPopout popout) {
		GraphicalInterface.popout = popout;
	}

	public static OutputPopout getPopout() {
		return popout;
	}
	
    public static ReactionColAddRenameInterface reactionColAddRenameInterface;
	
	public void setReactionColAddRenameInterface(ReactionColAddRenameInterface reactionColAddRenameInterface) {
		GraphicalInterface.reactionColAddRenameInterface = reactionColAddRenameInterface;
	}

	public static ReactionColAddRenameInterface getReactionColAddRenameInterface() {
		return reactionColAddRenameInterface;
	}
		
	public static ReactionColumnNameInterface reactionColumnNameInterface;
	
	public static ReactionColumnNameInterface getReactionColumnNameInterface() {
		return reactionColumnNameInterface;
	}

	public static void setReactionColumnNameInterface(
			ReactionColumnNameInterface reactionColumnNameInterface) {
		GraphicalInterface.reactionColumnNameInterface = reactionColumnNameInterface;
	}

	public static ReactionEditor reactionEditor;

	public void setReactionEditor(ReactionEditor reactionEditor) {
		GraphicalInterface.reactionEditor = reactionEditor;
	}

	public static ReactionEditor getReactionEditor() {
		return reactionEditor;
	}	    
	
	/*****************************************************************************/
	// end components
	/*****************************************************************************/					

	/*****************************************************************************/
	// files, database files, and names 
	/*****************************************************************************/	
	
	public static String databaseName;

	public static void setDatabaseName(String databaseName) {

		LocalConfig localConfig = LocalConfig.getInstance();
		localConfig.setDatabaseName(databaseName);
	}

	public static String getDatabaseName() {
		LocalConfig localConfig = LocalConfig.getInstance();
		return localConfig.getDatabaseName();
	}

	public static String dbFilename;

	public void setDBFilename(String dbFilename) {
		GraphicalInterface.dbFilename = dbFilename;
	}

	public static String getDBFilename() {
		return dbFilename;
	}

	public static String dbPath;

	public void setDBPath(String dbPath) {
		GraphicalInterface.dbPath = dbPath;
	}

	public static String getDBPath() {
		return dbPath;
	}
	
	public static String extension;

	public void setExtension(String extension) {
		GraphicalInterface.extension = extension;
	}

	public static String getExtension() {
		return extension;
	}
	
	public static String optimizePath;

	public void setOptimizePath(String optimizePath) {
		GraphicalInterface.optimizePath = optimizePath;
	}

	public static String getOptimizePath() {
		return optimizePath;
	}
	
	public static File SBMLFile;

	public void setSBMLFile(File SBMLFile) {
		GraphicalInterface.SBMLFile = SBMLFile;
	}

	public static File getSBMLFile() {
		return SBMLFile;
	}
	
	/*****************************************************************************/
	// end files, database files, and names 
	/*****************************************************************************/
	
	/*****************************************************************************/
	// find replace
	/*****************************************************************************/
	
    public static ArrayList<ArrayList<Integer>> metabolitesFindLocationsList;
	
	public static ArrayList<ArrayList<Integer>> getMetabolitesFindLocationsList() {
		return metabolitesFindLocationsList;
	}

	public static void setMetabolitesFindLocationsList(
			ArrayList<ArrayList<Integer>> metabolitesFindLocationsList) {
		GraphicalInterface.metabolitesFindLocationsList = metabolitesFindLocationsList;
	}
	
    public static ArrayList<Integer> metabolitesReplaceLocation;
	
	public static ArrayList<Integer> getMetabolitesReplaceLocation() {
		return metabolitesReplaceLocation;
	}

	public static void setMetabolitesReplaceLocation(ArrayList<Integer> metabolitesReplaceLocation) {
		GraphicalInterface.metabolitesReplaceLocation = metabolitesReplaceLocation;
	}
	
    public static ArrayList<ArrayList<Integer>> reactionsFindLocationsList;
	
	public static ArrayList<ArrayList<Integer>> getReactionsFindLocationsList() {
		return reactionsFindLocationsList;
	}

	public static void setReactionsFindLocationsList(
			ArrayList<ArrayList<Integer>> reactionsFindLocationsList) {
		GraphicalInterface.reactionsFindLocationsList = reactionsFindLocationsList;
	}
	
	public static ArrayList<Integer> reactionsReplaceLocation;
	
	public static ArrayList<Integer> getReactionsReplaceLocation() {
		return reactionsReplaceLocation;
	}

	public static void setReactionsReplaceLocation(ArrayList<Integer> reactionsReplaceLocation) {
		GraphicalInterface.reactionsReplaceLocation = reactionsReplaceLocation;
	}
	
	public static String replaceAllError;

	public void setReplaceAllError(String replaceAllError) {
		GraphicalInterface.replaceAllError = replaceAllError;
	}

	public static String getReplaceAllError() {
		return replaceAllError;
	}
	
	/*****************************************************************************/
	// end find replace
	/*****************************************************************************/
		
	/*****************************************************************************/
	// menu items
	/*****************************************************************************/
	
	public final JMenuItem loadExistingItem = new JMenuItem(GraphicalInterfaceConstants.LOAD_FROM_MODEL_COLLECTION_TABLE_TITLE);
	public final JMenuItem saveSBMLItem = new JMenuItem("Save As SBML");
	public final JMenuItem saveCSVMetabolitesItem = new JMenuItem("Save As CSV Metabolites");
	public final JMenuItem saveCSVReactionsItem = new JMenuItem("Save As CSV Reactions");
	public final JMenuItem saveSQLiteItem = new JMenuItem("Save As SQLite");
	public final JMenuItem clearItem = new JMenuItem("Clear");
	public final JMenuItem fbaItem = new JMenuItem("FBA");
	public final JMenuItem gdbbItem = new JMenuItem("GDBB");
	public final JCheckBoxMenuItem highlightUnusedMetabolitesItem = new JCheckBoxMenuItem("Highlight Unused Metabolites");
	public final JMenuItem deleteUnusedItem = new JMenuItem("Delete All Unused Metabolites");
	public final JMenuItem findSuspiciousItem = new JMenuItem("Find Suspicious Metabolites");
	public final JMenuItem findReplaceItem = new JMenuItem("Find/Replace");
	public final JMenuItem addReacRowItem = new JMenuItem("Add Row to Reactions Table");
	public final JMenuItem addMetabRowItem = new JMenuItem("Add Row to Metabolites Table");
	public final JMenuItem addReacColumnItem = new JMenuItem("Add Column to Reactions Table");
	public final JMenuItem addMetabColumnItem = new JMenuItem("Add Column to Metabolites Table"); 
	public final JMenuItem editorMenu = new JMenuItem("Launch Reaction Editor");

	public final JMenuItem formulaBarCutItem = new JMenuItem("Cut");
	public final JMenuItem formulaBarCopyItem = new JMenuItem("Copy");
	public final JMenuItem formulaBarPasteItem = new JMenuItem("Paste");
	public final JMenuItem formulaBarDeleteItem = new JMenuItem("Delete");
	public final JMenuItem formulaBarSelectAllItem = new JMenuItem("Select All");
	
	public final JMenuItem outputCopyItem = new JMenuItem("Copy");
	public final JMenuItem outputSelectAllItem = new JMenuItem("Select All");
	
	public final JMenuItem unhighlightMenu = new JMenuItem("Unhighlight Participating Reactions");
	
	/*****************************************************************************/
	// end menu items
	/*****************************************************************************/	
	
	/*****************************************************************************/
	// misc
	/*****************************************************************************/
	
	public static int currentRow;

	public void setCurrentRow(int currentRow){
		GraphicalInterface.currentRow = currentRow;
	}

	public static int getCurrentRow() {
		return currentRow;
	}
		
	public static int currentColumn;

	public void setCurrentColumn(int currentColumn){
		GraphicalInterface.currentColumn = currentColumn;
	}

	public static int getCurrentColumn() {
		return currentColumn;
	}
	
	public static ArrayList<Image> icons;

	public void setIconsList(ArrayList<Image> icons) {
		this.icons = icons;
	}

	public static ArrayList<Image> getIconsList() {
		return icons;
	}    
	
	public static String loadErrorMessage;

	public void setLoadErrorMessage(String loadErrorMessage) {
		GraphicalInterface.loadErrorMessage = loadErrorMessage;
	}

	public static String getLoadErrorMessage() {
		return loadErrorMessage;
	}
	
	public static String oldReaction;

	public void setOldReaction(String oldReaction) {
		GraphicalInterface.oldReaction = oldReaction;
	}

	public static String getOldReaction() {
		return oldReaction;
	}
	
	public static String participatingMetabolite;

	public void setParticipatingMetabolite(String participatingMetabolite) {
		GraphicalInterface.participatingMetabolite = participatingMetabolite;
	}

	public static String getParticipatingMetabolite() {
		return participatingMetabolite;
	}
	
	public static String pasteError;

	public void setPasteError(String pasteError) {
		GraphicalInterface.pasteError = pasteError;
	}

	public static String getPasteError() {
		return pasteError;
	}
		
    public static int selectionMode;
	
	public void setSelectionMode(int selectionMode){
		GraphicalInterface.selectionMode = selectionMode;
	}
	
	public static int getSelectionMode() {
		return selectionMode;
	}
	
	public static String tableCellOldValue;

	public void setTableCellOldValue(String tableCellOldValue) {
		GraphicalInterface.tableCellOldValue = tableCellOldValue;
	}

	public static String getTableCellOldValue() {
		return tableCellOldValue;
	}    
	
    public static ArrayList<Integer> visibleMetabolitesColumns;
	
	public static ArrayList<Integer> getVisibleMetabolitesColumns() {
		return visibleMetabolitesColumns;
	}

	public static void setVisibleMetabolitesColumns(
			ArrayList<Integer> visibleMetabolitesColumns) {
		GraphicalInterface.visibleMetabolitesColumns = visibleMetabolitesColumns;
	}
	
	public static ArrayList<Integer> visibleReactionsColumns;

	public static ArrayList<Integer> getVisibleReactionsColumns() {
		return visibleReactionsColumns;
	}

	public static void setVisibleReactionsColumns(
			ArrayList<Integer> visibleReactionsColumns) {
		GraphicalInterface.visibleReactionsColumns = visibleReactionsColumns;
	}
	
	/*****************************************************************************/
	// end misc
	/*****************************************************************************/
		
	/*****************************************************************************/
	// sorting
	/*****************************************************************************/
	
    public static int metabolitesSortColumnIndex;
	
	public void setMetabolitesSortColumnIndex(int metabolitesSortColumnIndex){
		GraphicalInterface.metabolitesSortColumnIndex = metabolitesSortColumnIndex;
	}
	
	public static int getMetabolitesSortColumnIndex() {
		return metabolitesSortColumnIndex;
	}
	
	public static SortOrder metabolitesSortOrder;
	
	public void setMetabolitesSortOrder(SortOrder metabolitesSortOrder){
		GraphicalInterface.metabolitesSortOrder = metabolitesSortOrder;
	}
	
	public static SortOrder getMetabolitesSortOrder() {
		return metabolitesSortOrder;
	}
		
    public static int reactionsSortColumnIndex;
	
	public void setReactionsSortColumnIndex(int reactionsSortColumnIndex){
		GraphicalInterface.reactionsSortColumnIndex = reactionsSortColumnIndex;
	}
	
	public static int getReactionsSortColumnIndex() {
		return reactionsSortColumnIndex;
	}
	
	public static SortOrder reactionsSortOrder;
	
	public void setReactionsSortOrder(SortOrder reactionsSortOrder){
		GraphicalInterface.reactionsSortOrder = reactionsSortOrder;
	}
	
	public static SortOrder getReactionsSortOrder() {
		return reactionsSortOrder;
	}
	
	/*****************************************************************************/
	// end sorting
	/*****************************************************************************/
	
	// used in undo/redo when adding menu items actions, allows action to be
	// enabled or disabled, based on visibility
	public static final String ENABLE = "ENABLE";
	public static final String DISABLE = "DISABLE";
	
	public Integer undoCount;
	public Integer redoCount;
	
	// used for highlighting selected area in find mode
	public Integer selectedReactionsRowStartIndex;
	public Integer selectedReactionsRowEndIndex;
	public Integer selectedReactionsColumnStartIndex;
	public Integer selectedReactionsColumnEndIndex;
	public Integer selectedMetabolitesRowStartIndex;
	public Integer selectedMetabolitesRowEndIndex;
	public Integer selectedMetabolitesColumnStartIndex;
	public Integer selectedMetabolitesColumnEndIndex;
	
	@SuppressWarnings("unchecked")
	public GraphicalInterface(final Connection con)
	throws SQLException {
		gi = this;
		
		isRoot = true;
		
		// Tree Panel
		newContentPane = new DynamicTreeDemo(new DynamicTree() {
			private static final long serialVersionUID = 1L;

			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)
				tree.getLastSelectedPathComponent();

				if (node == null) return;
                
				//Solution nodeInfo = (Solution)node.getUserObject();
				if (node.isLeaf()) {
					// may need to account for path, currently in same directory as MOST directory
					if (node.getUserObject().toString().equals(LocalConfig.getInstance().getDatabaseName())) {
						enableMenuItems();
						clearOutputPane();
						if (getPopout() != null) {
							getPopout().clear();
						}	
						closeConnection();
						LocalConfig.getInstance().setLoadedDatabase(LocalConfig.getInstance().getDatabaseName());
						reloadTables(LocalConfig.getInstance().getLoadedDatabase());
						isRoot = true;
					} else {						
						if (node.getUserObject().toString() != null) {
							//gets the full path of optimize since it may not be in MOST directory
							String optimizePath = getOptimizePath();
							if (optimizePath.contains("\\")) {
								optimizePath = optimizePath.substring(0, optimizePath.lastIndexOf("\\") + 1) + fileList.getSelectedValue().toString();
							} else {
								optimizePath = node.getUserObject().toString();
							}
							setOptimizePath(optimizePath);
							if (getOptimizePath().endsWith(node.getUserObject().toString())) {
								loadOutputPane(getOptimizePath() + ".log");
								if (getPopout() != null) {
									getPopout().load(getOptimizePath() + ".log");
								}	
								disableMenuItems();								
								closeConnection();								
								LocalConfig.getInstance().setLoadedDatabase(getOptimizePath());
								reloadTables(LocalConfig.getInstance().getLoadedDatabase());
								isRoot = false;
							} 
						}
					}					
				}
			}
		});
	
		
		// this code must be before any components if the components
		// are going to have an image icon
		final ArrayList<Image> icons = new ArrayList<Image>(); 
		icons.add(new ImageIcon("etc/most16.jpg").getImage()); 
		icons.add(new ImageIcon("etc/most32.jpg").getImage());
		setIconsList(icons);
				
		textInput = new TextInputDemo(gi);
        textInput.setModal(true);
        textInput.setIconImages(icons);
        textInput.setTitle("GDBB");
        textInput.setSize(300, 250);
        textInput.setResizable(false);
        textInput.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        textInput.setLocationRelativeTo(null);
        textInput.setAlwaysOnTop(true);
        
		//System.out.println("max memory " + java.lang.Runtime.getRuntime().maxMemory());

		LocalConfig.getInstance().setProgress(0);
		progressBar.pack();
		progressBar.setIconImages(icons);
		progressBar.setSize(GraphicalInterfaceConstants.PROGRESS_BAR_WIDTH, GraphicalInterfaceConstants.PROGRESS_BAR_HEIGHT);		
		progressBar.setResizable(false);
		progressBar.setTitle("Loading...");
		progressBar.progress.setIndeterminate(true);
		progressBar.setLocationRelativeTo(null);
		progressBar.setVisible(false);
		
		csvLoadInterface.setIconImages(icons);					
		csvLoadInterface.setSize(600, 200);
		csvLoadInterface.setResizable(false);
		csvLoadInterface.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		csvLoadInterface.setLocationRelativeTo(null);		
		csvLoadInterface.setVisible(false);	
		csvLoadInterface.setModal(true);
		csvLoadInterface.addWindowListener(new WindowAdapter() {
	        public void windowClosing(WindowEvent evt) {
	        	csvLoadInterface.setVisible(false);
	        	clearTables();	        	
	        }
		});			
		csvLoadInterface.okButton.addActionListener(okButtonCSVLoadActionListener);
		csvLoadInterface.cancelButton.addActionListener(cancelButtonCSVLoadActionListener);
		
		setDatabaseName(ConfigConstants.DEFAULT_DATABASE_NAME);
		LocalConfig.getInstance().setLoadedDatabase(ConfigConstants.DEFAULT_DATABASE_NAME);

		setTitle(GraphicalInterfaceConstants.TITLE + " - " + ConfigConstants.DEFAULT_DATABASE_NAME);

		addWindowListener(new WindowAdapter() {
	        public void windowClosing(WindowEvent evt) {
	        	SaveChangesPrompt();
				closeConnection();
				if (exit) {
					// Exit the application
			        System.exit(0);	
				}	            	        	
	        }
		});
		
		setBooleanDefaults();
		setSortDefault();
		setUpCellSelectionMode();
		LocalConfig.getInstance().setMaxMetaboliteId(0);
		LocalConfig.getInstance().setReactionsLocationsListCount(0);
		LocalConfig.getInstance().setMetabolitesLocationsListCount(0);
		LocalConfig.getInstance().setNumberCopiedRows(1);
		LocalConfig.getInstance().setNumberCopiedColumns(1);
					
		outputTextArea.setEditable(false);
		
		listModel.addElement(GraphicalInterfaceConstants.DEFAULT_DATABASE_NAME);
		DynamicTreeDemo.treePanel.addObject(new Solution(GraphicalInterfaceConstants.DEFAULT_DATABASE_NAME, GraphicalInterfaceConstants.DEFAULT_DATABASE_NAME));
		
		// lists populated in file load
        ArrayList<Integer> blankMetabIds = new ArrayList<Integer>();
		LocalConfig.getInstance().setBlankMetabIds(blankMetabIds);	
		ArrayList<Integer> duplicateIds = new ArrayList<Integer>();
		LocalConfig.getInstance().setDuplicateIds(duplicateIds);
		Map<String, Object> metaboliteIdNameMap = new HashMap<String, Object>();
		LocalConfig.getInstance().setMetaboliteIdNameMap(metaboliteIdNameMap);
		Map<String, Object> metaboliteUsedMap = new HashMap<String, Object>();
		LocalConfig.getInstance().setMetaboliteUsedMap(metaboliteUsedMap);			
		ArrayList<Integer> suspiciousMetabolites = new ArrayList<Integer>();
		LocalConfig.getInstance().setSuspiciousMetabolites(suspiciousMetabolites);
		ArrayList<Integer> unusedList = new ArrayList<Integer>();
		LocalConfig.getInstance().setUnusedList(unusedList);
		
		// lists used in find and replace
		ArrayList<ArrayList<Integer>> metabolitesFindLocationsList = new ArrayList<ArrayList<Integer>>();
		setMetabolitesFindLocationsList(metabolitesFindLocationsList);
		ArrayList<ArrayList<Integer>> reactionsFindLocationsList = new ArrayList<ArrayList<Integer>>();
		setReactionsFindLocationsList(reactionsFindLocationsList);
		ArrayList<Integer> reactionsReplaceLocation = new ArrayList<Integer>();
		setReactionsReplaceLocation(reactionsReplaceLocation);
		ArrayList<Integer> metabolitesReplaceLocation = new ArrayList<Integer>();
		setMetabolitesReplaceLocation(metabolitesReplaceLocation);
		ArrayList<String> findEntryList = new ArrayList<String>();
		LocalConfig.getInstance().setFindEntryList(findEntryList);
		ArrayList<String> replaceEntryList = new ArrayList<String>();
		LocalConfig.getInstance().setReplaceEntryList(replaceEntryList);
		
		selectedReactionsRowStartIndex = 0;
		selectedReactionsRowEndIndex = 0;
		selectedReactionsColumnStartIndex = 1;
		selectedReactionsColumnEndIndex = 1;
		selectedMetabolitesRowStartIndex = 0;
		selectedMetabolitesRowEndIndex = 0;
		selectedMetabolitesColumnStartIndex = 1;
		selectedMetabolitesColumnEndIndex = 1;
		
		// miscellaneous lists
		ArrayList<Integer> participatingReactions = new ArrayList<Integer>();
		LocalConfig.getInstance().setParticipatingReactions(participatingReactions);
		ArrayList<String> optimizationFilesList = new ArrayList<String>();
		LocalConfig.getInstance().setOptimizationFilesList(optimizationFilesList);
		
		// hiding and deletion of columns
		ArrayList<Integer> hiddenReactionsColumns = new ArrayList<Integer>();
		LocalConfig.getInstance().setHiddenReactionsColumns(hiddenReactionsColumns);
		ArrayList<Integer> hiddenMetabolitesColumns = new ArrayList<Integer>();
		LocalConfig.getInstance().setHiddenMetabolitesColumns(hiddenMetabolitesColumns);
			
		// undo/redo
		Map<Object, Object> undoItemMap = new HashMap<Object, Object>();
		LocalConfig.getInstance().setUndoItemMap(undoItemMap);
		Map<Object, Object> redoItemMap = new HashMap<Object, Object>();
		LocalConfig.getInstance().setRedoItemMap(redoItemMap);
		
		undoCount = 1;
		redoCount = 1;
		LocalConfig.getInstance().setNumReactionTablesCopied(0);
		LocalConfig.getInstance().setNumMetabolitesTableCopied(0);
		LocalConfig.getInstance().setUndoMenuIndex(0);
		
		// undo/redo sort order
		ArrayList<Integer> reactionsSortColumns = new ArrayList<Integer>();		
		ArrayList<SortOrder> reactionsSortOrderList = new ArrayList<SortOrder>();		
		ArrayList<Integer> metabolitesSortColumns = new ArrayList<Integer>();		
		ArrayList<SortOrder> metabolitesSortOrderList = new ArrayList<SortOrder>();
		reactionsSortColumns.add(0);
		LocalConfig.getInstance().setReactionsSortColumns(reactionsSortColumns);
		reactionsSortOrderList.add(SortOrder.ASCENDING);
		LocalConfig.getInstance().setReactionsSortOrderList(reactionsSortOrderList);
		metabolitesSortColumns.add(0);
		LocalConfig.getInstance().setMetabolitesSortColumns(metabolitesSortColumns);
		metabolitesSortOrderList.add(SortOrder.ASCENDING);
		LocalConfig.getInstance().setMetabolitesSortOrderList(metabolitesSortOrderList);
		
		ArrayList<Integer> reactionsRedoSortColumns = new ArrayList<Integer>();		
		ArrayList<SortOrder> reactionsRedoSortOrderList = new ArrayList<SortOrder>();		
		ArrayList<Integer> metabolitesRedoSortColumns = new ArrayList<Integer>();		
		ArrayList<SortOrder> metabolitesRedoSortOrderList = new ArrayList<SortOrder>();
		LocalConfig.getInstance().setReactionsRedoSortColumns(reactionsRedoSortColumns);
		LocalConfig.getInstance().setReactionsRedoSortOrderList(reactionsRedoSortOrderList);
		LocalConfig.getInstance().setMetabolitesRedoSortColumns(metabolitesRedoSortColumns);
		LocalConfig.getInstance().setMetabolitesRedoSortOrderList(metabolitesRedoSortOrderList);
		
		ArrayList<Integer> addedMetabolites = new ArrayList<Integer>();
		LocalConfig.getInstance().setAddedMetabolites(addedMetabolites);
		
		/**************************************************************************/
		//set up fileList
		/**************************************************************************/

		/* 
		fileList.setModel(listModel);
		fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); 
		fileList.setSelectedIndex(0);

		fileList.addListSelectionListener(new ListSelectionListener() 
		{ 
			public void valueChanged(ListSelectionEvent e) 
			{  
				fileList.saveItem.setEnabled(false);
				fileList.saveAsCSVItem.setEnabled(false);
				fileList.saveAsSBMLItem.setEnabled(false);
				//fileList.saveAllItem.setEnabled(false);
				fileList.deleteItem.setEnabled(false);
				fileList.clearItem.setEnabled(false);
				if(fileList.getSelectedIndex() == 0) {
					enableMenuItems();
					clearOutputPane();
					if (getPopout() != null) {
						getPopout().clear();
					}	
					closeConnection();
					LocalConfig.getInstance().setLoadedDatabase(getDatabaseName());
					reloadTables(getDatabaseName());
				} else if (fileList.getSelectedIndex() == -1) {
					fileList.setSelectedIndex(0);
				} else {
					disableMenuItems();
					if (fileList.getSelectedValue() != null) {
						loadOptimization();
					}
				}        	  
			} 
		});   

		fileList.saveAsCSVItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) { 
				saveOptFile = true;	
				saveReactionsTextFileChooser();
			}
		});
			
		fileList.saveAsSBMLItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) { 
				// Add action here when save SBML works	
			}
		});
		
		fileList.deleteItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {
				deleteItemFromFileList();		
			}
		});

		fileList.clearItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {
				deleteAllItemsFromFileList();			
			}
		});
			
	   */		
		
		DynamicTreeDemo.treePanel.saveAsCSVItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) { 
				saveOptFile = true;	
				saveReactionsTextFileChooser();
			}
		});
				
		DynamicTreeDemo.treePanel.saveAsSBMLItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) { 
				// Add action here when save SBML works	
			}
		});
		
		DynamicTreeDemo.treePanel.deleteItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {
				deleteItemFromDynamicTree();		
			}
		});
				
		DynamicTreeDemo.treePanel.clearItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {
				deleteAllItemsFromDynamicTree();			
			}
		});

		final JPopupMenu outputPopupMenu = new JPopupMenu(); 
		outputPopupMenu.add(outputCopyItem);
		outputCopyItem.setEnabled(false);
		outputCopyItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) { 	
				setClipboardContents(outputTextArea.getSelectedText());							
			}
		});
		outputPopupMenu.add(outputSelectAllItem);
		outputSelectAllItem.setEnabled(false);
		outputSelectAllItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) { 
				outputTextArea.selectAll();							
			}
		});
		outputPopupMenu.addSeparator();
		JMenuItem popOutItem = new JMenuItem("Pop Out");
		outputPopupMenu.add(popOutItem);
		popOutItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) { 	
				OutputPopout popout = new OutputPopout();
				popout.setIconImages(icons);
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

		outputTextArea.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				fieldChangeAction();
			}
			public void removeUpdate(DocumentEvent e) {
				fieldChangeAction();
			}
			public void insertUpdate(DocumentEvent e) {
				fieldChangeAction();
			}
			public void fieldChangeAction() {
				if (outputTextArea.getText().length() > 0) {
					outputCopyItem.setEnabled(true);
					outputSelectAllItem.setEnabled(true);
				} else {
					outputCopyItem.setEnabled(false);
					outputSelectAllItem.setEnabled(false);
				}
			}
		});
		
		/**************************************************************************/
		// end set up output popout
		/**************************************************************************/		
		
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

		JMenuItem loadCSVItem = new JMenuItem("Load CSV");
		modelMenu.add(loadCSVItem);
		loadCSVItem.setMnemonic(KeyEvent.VK_C);
		loadCSVItem.addActionListener(new LoadCSVAction());
		
		JMenuItem loadSQLItem = new JMenuItem("Load SQLite");
		modelMenu.add(loadSQLItem);
		loadSQLItem.setMnemonic(KeyEvent.VK_Q);
		loadSQLItem.addActionListener(new LoadSQLiteItemAction());
		
		modelMenu.add(loadExistingItem);
		loadExistingItem.setMnemonic(KeyEvent.VK_Q);
		loadExistingItem.addActionListener(new LoadExistingItemAction());
		
		modelMenu.addSeparator();

		modelMenu.add(saveSBMLItem);
		saveSBMLItem.setMnemonic(KeyEvent.VK_O);
		saveSBMLItem.addActionListener(new SaveSBMLItemAction());
		
		modelMenu.add(saveCSVMetabolitesItem);
		saveCSVMetabolitesItem.setMnemonic(KeyEvent.VK_O);
		saveCSVMetabolitesItem.addActionListener(new SaveCSVMetabolitesItemAction());

		modelMenu.add(saveCSVReactionsItem);
		saveCSVReactionsItem.setMnemonic(KeyEvent.VK_N);
		saveCSVReactionsItem.addActionListener(new SaveCSVReactionsItemAction());
		
		modelMenu.add(saveSQLiteItem);
		saveSQLiteItem.setMnemonic(KeyEvent.VK_A);
		saveSQLiteItem.addActionListener(new SaveSQLiteItemAction());

		modelMenu.addSeparator();

		modelMenu.add(clearItem);
		clearItem.setMnemonic(KeyEvent.VK_E);
		clearItem.addActionListener(new ClearAction());

		modelMenu.addSeparator();
		
		JMenuItem exitItem = new JMenuItem("Exit");
		modelMenu.add(exitItem);
		exitItem.setMnemonic(KeyEvent.VK_X);
		exitItem.addActionListener(new ExitAction());
		
		menuBar.add(modelMenu);

		//Analysis menu
		JMenu analysisMenu = new JMenu("Analysis");
		analysisMenu.setMnemonic(KeyEvent.VK_A);
		
		analysisMenu.add(fbaItem);
		fbaItem.setMnemonic(KeyEvent.VK_F);
		
		fbaItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {
				Utilities u = new Utilities();
				closeConnection();
                //load original db into tables
				//fileList.removeSelectionInterval(0, listModel.size());
				LocalConfig.getInstance().setLoadedDatabase(getDatabaseName());

				reloadTables(getDatabaseName());
				highlightUnusedMetabolites = false;
				highlightUnusedMetabolitesItem.setState(false);
				
				String dateTimeStamp = u.createDateTimeStamp();

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

				// TODO: should be in the try/catch since if optimization fails
				// item should not be added to fileList
				copier.copyDatabase(getDatabaseName(), optimizePath);
				listModel.addElement(GraphicalInterfaceConstants.OPTIMIZATION_PREFIX
						+ (getDatabaseName().substring(getDatabaseName().lastIndexOf("\\") + 1) + dateTimeStamp));				
				LocalConfig.getInstance().getOptimizationFilesList().add(optimizePath);
				setOptimizePath(optimizePath);
				//fileList.setSelectedIndex(listModel.size() - 1);
			
//				String solutionName = GraphicalInterface.listModel.get(GraphicalInterface.listModel.getSize() - 1);
				DynamicTreeDemo.treePanel.addObject(new Solution(GraphicalInterface.listModel.get(GraphicalInterface.listModel.getSize() - 1)));
				DynamicTreeDemo.treePanel.setNodeSelected(GraphicalInterface.listModel.getSize() - 1);
				
				// Begin optimization

				FBAModel model = new FBAModel(getDatabaseName());

				// TODO should this be in a try/catch loop? so if it fails
				// error message can be displayed
				// also for Gurobi get key error, dialog instead of console printout
				log.debug("create an optimize");
				FBA fba = new FBA();
				fba.setFBAModel(model);
				log.debug("about to optimize");
				ArrayList<Double> soln = fba.run();
				log.debug("optimization complete");
				//End optimization
				
				ReactionFactory rFactory = new ReactionFactory("SBML", getOptimizePath());
				rFactory.setFluxes(soln);

				Writer writer = null;
				try {
					StringBuffer outputText = new StringBuffer();
					outputText.append("FBA\n");
					outputText.append(getDatabaseName() + "\n");
					outputText.append(model.getNumMetabolites() + " metabolites, " + model.getNumReactions() + " reactions\n");
					outputText.append("Maximum objective: "	+ fba.getMaxObj() + "\n");
					
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
				loadOutputPane(getOptimizePath() + ".log");
				if (getPopout() != null) {
					getPopout().load(getOptimizePath() + ".log");
				}
				closeConnection();
				String fileString = "jdbc:sqlite:" + getOptimizePath() + ".db";
				LocalConfig.getInstance().setLoadedDatabase(getOptimizePath());
				try {
					Class.forName("org.sqlite.JDBC");
					Connection con = DriverManager.getConnection(fileString);
					LocalConfig.getInstance().setCurrentConnection(con);
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
				//fileList.setSelectedIndex(listModel.size() - 1);
			}
			
		});
		
		menuBar.add(analysisMenu);

		analysisMenu.add(gdbbItem);
		
		gdbbItem.setMnemonic(KeyEvent.VK_G);

//		TODO Optimization using GDBB
		//	Action Listener for GDBB optimization
		gdbbItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {
				Utilities u = new Utilities();
				//load original db into tables
		        
				//fileList.removeSelectionInterval(0, listModel.size());
				//String fileString1 = "jdbc:sqlite:" + getDatabaseName() + ".db";
				closeConnection();
				reloadTables(getDatabaseName());
				highlightUnusedMetabolites = false;
				highlightUnusedMetabolitesItem.setState(false);
				LocalConfig.getInstance().setLoadedDatabase(getDatabaseName());
				/*
				try {
					Class.forName("org.sqlite.JDBC");
					Connection con = DriverManager.getConnection(fileString1);	
					LocalConfig.getInstance().setCurrentConnection(con);
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
				*/

				String dateTimeStamp = u.createDateTimeStamp();

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
				listModel.addElement(GraphicalInterfaceConstants.OPTIMIZATION_PREFIX
						+ (getDatabaseName().substring(getDatabaseName().lastIndexOf("\\") + 1) + dateTimeStamp));
				LocalConfig.getInstance().getOptimizationFilesList().add(optimizePath);
//				listModel.addElement((getDatabaseName().substring(getDatabaseName().lastIndexOf("\\") + 1)));
				
//				DynamicTreeDemo.treePanel.setCurrentParent(new DefaultMutableTreeNode(listModel.get(listModel.getSize() - 1)));
//				DynamicTreeDemo.treePanel.addObject(DynamicTreeDemo.treePanel.getCurrentParent());
				
				setOptimizePath(optimizePath);
				
				textInput.setObjectiveColumnNames(LocalConfig.getInstance().getLoadedDatabase());
				
		        textInput.setVisible(true);
			}
		});

		//Edit menu
		JMenu editMenu = new JMenu("Edit");
		editMenu.setMnemonic(KeyEvent.VK_E);

		editMenu.add(highlightUnusedMetabolitesItem);
		highlightUnusedMetabolitesItem.setMnemonic(KeyEvent.VK_H);
		
		highlightUnusedMetabolitesItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if (LocalConfig.getInstance().getUnusedList().size() > 0) {
					highlightUnusedMetabolitesItem.setEnabled(true);
				}
				tabbedPane.setSelectedIndex(1);
				boolean state = highlightUnusedMetabolitesItem.getState();
				if (state == true) {
					highlightUnusedMetabolites = true;
				} else {
					highlightUnusedMetabolites = false;
				}
				closeConnection();
				reloadTables(LocalConfig.getInstance().getLoadedDatabase());
			}
		});

		editMenu.add(deleteUnusedItem);
		deleteUnusedItem.setMnemonic(KeyEvent.VK_D);		

		deleteUnusedItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {				
				tabbedPane.setSelectedIndex(1);
				copyMetaboliteDatabaseTable();
				MetaboliteUndoItem undoItem = createMetaboliteUndoItem("", "", metabolitesTable.getSelectedRow(), metabolitesTable.getSelectedColumn(), 1, UndoConstants.DELETE_UNUSED, UndoConstants.METABOLITE_UNDO_ITEM_TYPE);
				undoItem.setTableCopyIndex(LocalConfig.getInstance().getNumMetabolitesTableCopied());
				setUndoOldCollections(undoItem);
				Map<String, Object> usedMap = LocalConfig.getInstance().getMetaboliteUsedMap();
				Map<String, Object> idMap = new HashMap<String, Object>();

				try {
					idMap = (Map<String, Object>) (ObjectCloner.deepCopy(LocalConfig.getInstance().getMetaboliteIdNameMap()));
					ArrayList<String> usedList = new ArrayList<String>(usedMap.keySet());
					ArrayList<String> idList = new ArrayList<String>(idMap.keySet());
					ArrayList<Integer> unusedList = new ArrayList<Integer>();
					// removes unused metabolites from idMap and populates list of
					// unused metabolite id's for deletion from table
					for (int i = 0; i < idList.size(); i++) {						
						if (!usedList.contains(idList.get(i))) {
							int id = (Integer) idMap.get(idList.get(i));
							idMap.remove(idList.get(i));
							unusedList.add(id); 
						}
					}
					LocalConfig.getInstance().setUnusedList(unusedList);				
					MetabolitesUpdater updater = new MetabolitesUpdater();				
					updater.deleteUnused(unusedList, LocalConfig.getInstance().getLoadedDatabase());
					closeConnection();				
					highlightUnusedMetabolites = false;
					highlightUnusedMetabolitesItem.setState(false);
					formulaBar.setText("");
					LocalConfig.getInstance().getUnusedList().clear();
					LocalConfig.getInstance().setMetaboliteIdNameMap(idMap);
					copyMetaboliteDatabaseTable();
					setUpMetabolitesUndo(undoItem);	
					reloadTables(LocalConfig.getInstance().getLoadedDatabase());
				} catch (Exception e3) {
					// TODO Auto-generated catch block
					e3.printStackTrace();
				}			
			}
		});   
		
		editMenu.add(findSuspiciousItem);
		findSuspiciousItem.setMnemonic(KeyEvent.VK_S);
		findSuspiciousItem.setEnabled(false);

		findSuspiciousItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {
				tabbedPane.setSelectedIndex(1);
				if (LocalConfig.getInstance().getSuspiciousMetabolites().size() > 0) {
					int firstId = LocalConfig.getInstance().getSuspiciousMetabolites().get(0);
					for (int r = 0; r < metabolitesTable.getRowCount(); r++) {
						int viewRow = metabolitesTable.convertRowIndexToModel(r);
						Integer cellValue = Integer.valueOf((String) metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.DB_METABOLITE_ID_COLUMN));
						if (cellValue == firstId) {
							setTableCellFocused(r, GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN, metabolitesTable);
						}	
					}
				}
			}    	     
		});
		
		editMenu.addSeparator(); 
		
		editMenu.add(findReplaceItem);
		findReplaceItem.setMnemonic(KeyEvent.VK_F);
		findReplaceItem.setEnabled(true);
		
		findReplaceItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {
				showFindReplace();
				findReplaceItem.setEnabled(false);
				findbutton.setEnabled(false);
			}    	     
		});
		
		editMenu.addSeparator();

		editMenu.add(addReacRowItem);
		addReacRowItem.setMnemonic(KeyEvent.VK_R);

		ActionListener addReacRowActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				tabbedPane.setSelectedIndex(0);
				DatabaseCreator creator = new DatabaseCreator();
				int id = creator.maxReactionId(LocalConfig.getInstance().getLoadedDatabase()) + 1;
				ReactionUndoItem undoItem = createReactionUndoItem("", "", reactionsTable.getSelectedRow(), reactionsTable.getSelectedColumn(), id, UndoConstants.ADD_ROW, UndoConstants.REACTION_UNDO_ITEM_TYPE);		
				creator.addReactionRow(LocalConfig.getInstance().getLoadedDatabase());
				closeConnection();
				reloadTables(LocalConfig.getInstance().getLoadedDatabase());
				//set focus to id cell in new row in order to set row visible
				int maxRow = reactionsTable.getModel().getRowCount();
				int viewRow = reactionsTable.convertRowIndexToView(maxRow - 1);
				setTableCellFocused(viewRow, 1, reactionsTable);
				setUpReactionsUndo(undoItem);
			}
		};  
		
		addReacRowItem.addActionListener(addReacRowActionListener);
   
		editMenu.add(addMetabRowItem); 
		addMetabRowItem.setMnemonic(KeyEvent.VK_M);
		
		addMetabRowItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				tabbedPane.setSelectedIndex(1);
				DatabaseCreator creator = new DatabaseCreator();
				int id = creator.maxMetaboliteId(LocalConfig.getInstance().getLoadedDatabase()) + 1;
				MetaboliteUndoItem undoItem = createMetaboliteUndoItem("", "", metabolitesTable.getSelectedRow(), metabolitesTable.getSelectedColumn(), id, UndoConstants.ADD_ROW, UndoConstants.METABOLITE_UNDO_ITEM_TYPE);
				setUndoOldCollections(undoItem);				
				creator.addMetaboliteRow(LocalConfig.getInstance().getLoadedDatabase());
				closeConnection();
				reloadTables(LocalConfig.getInstance().getLoadedDatabase());
				int maxRow = metabolitesTable.getModel().getRowCount();
				int viewRow = metabolitesTable.convertRowIndexToView(maxRow - 1);
				setTableCellFocused(viewRow, 1, metabolitesTable);
				setUpMetabolitesUndo(undoItem);
			}
		});
		
		editMenu.addSeparator();
		
		editMenu.add(addReacColumnItem);
		addReacColumnItem.setMnemonic(KeyEvent.VK_C);
		
		addReacColumnItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				setCurrentRow(reactionsTable.getSelectedRow());
				setCurrentColumn(reactionsTable.getSelectedColumn());
				tabbedPane.setSelectedIndex(0);
				closeConnection();
				LocalConfig.getInstance().addColumnInterfaceVisible = true;
				addReacColumnItem.setEnabled(false);
				addMetabColumnItem.setEnabled(false);
				try {
					Class.forName("org.sqlite.JDBC");
					Connection con = DriverManager.getConnection(createConnectionStatement(LocalConfig.getInstance().getLoadedDatabase()));
					LocalConfig.getInstance().setCurrentConnection(con);
					ReactionColAddRenameInterface reactionColAddRenameInterface = new ReactionColAddRenameInterface(con);
					setReactionColAddRenameInterface(reactionColAddRenameInterface);
					reactionColAddRenameInterface.setTitle(GraphicalInterfaceConstants.COLUMN_ADD_INTERFACE_TITLE);
					reactionColAddRenameInterface.setIconImages(icons);
					reactionColAddRenameInterface.setSize(350, 160);
					reactionColAddRenameInterface.setResizable(false);
					reactionColAddRenameInterface.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
					reactionColAddRenameInterface.setAlwaysOnTop(true);
					reactionColAddRenameInterface.setModal(true);
					reactionColAddRenameInterface.setLocationRelativeTo(null);
					reactionColAddRenameInterface.addWindowListener(new WindowAdapter() {
				        public void windowClosing(WindowEvent evt) {
				        	addReactionColumnCloseAction();
				        }
					});					
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

		ActionListener addColOKButtonActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent prodActionEvent) {
				closeConnection();
				// allows table to scroll to make added column visible
				addReacColumn = true;
				try {
					getReactionColAddRenameInterface().addColumnToMeta(LocalConfig.getInstance().getLoadedDatabase());
					getReactionColAddRenameInterface().textField.setText("");
					getReactionColAddRenameInterface().setVisible(false);
					getReactionColAddRenameInterface().dispose();
					Class.forName("org.sqlite.JDBC");
					Connection con = DriverManager.getConnection(createConnectionStatement(LocalConfig.getInstance().getLoadedDatabase()));
					LocalConfig.getInstance().setCurrentConnection(con);
					setUpReactionsTable(con);
					ReactionUndoItem undoItem = createReactionUndoItem("", "", getCurrentRow(), getCurrentColumn(), 1, UndoConstants.ADD_COLUMN, UndoConstants.REACTION_UNDO_ITEM_TYPE);
					ReactionsMetaColumnManager reactionsMetaColumnManager = new ReactionsMetaColumnManager();
					int metaColumnCount = reactionsMetaColumnManager.getMetaColumnCount(LocalConfig.getInstance().getDatabaseName());
					undoItem.setAddedColumnIndex(metaColumnCount + GraphicalInterfaceConstants.REACTIONS_DB_COLUMN_NAMES.length - 1);
					setUpReactionsUndo(undoItem);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				addReacColumn = false;
				addReactionColumnCloseAction();
			}
		};
		
		ActionListener addColCancelButtonActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent prodActionEvent) {
				closeConnection();
				addReactionColumnCloseAction();
			}
		};
		
		reactionColAddRenameInterface.okButton.addActionListener(addColOKButtonActionListener);
		reactionColAddRenameInterface.cancelButton.addActionListener(addColCancelButtonActionListener);
		
		editMenu.add(addMetabColumnItem);
		addMetabColumnItem.setMnemonic(KeyEvent.VK_O);
		
		addMetabColumnItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				setCurrentRow(metabolitesTable.getSelectedRow());
				setCurrentColumn(metabolitesTable.getSelectedColumn());
				tabbedPane.setSelectedIndex(1);
				closeConnection();
				LocalConfig.getInstance().addColumnInterfaceVisible = true;
				addReacColumnItem.setEnabled(false);
				addMetabColumnItem.setEnabled(false);
				try {
					Class.forName("org.sqlite.JDBC");
					Connection con = DriverManager.getConnection(createConnectionStatement(LocalConfig.getInstance().getLoadedDatabase()));	
					LocalConfig.getInstance().setCurrentConnection(con);
					MetaboliteColAddRenameInterface metaboliteColAddRenameInterface = new MetaboliteColAddRenameInterface(con);
					setMetaboliteColAddRenameInterface(metaboliteColAddRenameInterface);
					metaboliteColAddRenameInterface.setTitle(GraphicalInterfaceConstants.COLUMN_ADD_INTERFACE_TITLE);
					metaboliteColAddRenameInterface.setIconImages(icons);
					metaboliteColAddRenameInterface.setSize(350, 160);
					metaboliteColAddRenameInterface.setResizable(false);
					metaboliteColAddRenameInterface.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
					metaboliteColAddRenameInterface.setAlwaysOnTop(true);
					metaboliteColAddRenameInterface.setModal(true);
					metaboliteColAddRenameInterface.setLocationRelativeTo(null);
					metaboliteColAddRenameInterface.addWindowListener(new WindowAdapter() {
				        public void windowClosing(WindowEvent evt) {
				        	addMetaboliteColumnCloseAction();
				        }
					});
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
		
		ActionListener addMetabColOKButtonActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent prodActionEvent) {				
				closeConnection();
				// allows table to scroll to make added column visible
				addMetabColumn = true;
				try {
					getMetaboliteColAddRenameInterface().addColumnToMeta(LocalConfig.getInstance().getLoadedDatabase());
					getMetaboliteColAddRenameInterface().textField.setText("");
					getMetaboliteColAddRenameInterface().setVisible(false);
					getMetaboliteColAddRenameInterface().dispose();
					Class.forName("org.sqlite.JDBC");
					Connection con = DriverManager.getConnection(createConnectionStatement(LocalConfig.getInstance().getLoadedDatabase()));
					LocalConfig.getInstance().setCurrentConnection(con);
					setUpMetabolitesTable(con);
					MetaboliteUndoItem undoItem = createMetaboliteUndoItem("", "", getCurrentRow(), getCurrentColumn(), 1, UndoConstants.ADD_COLUMN, UndoConstants.METABOLITE_UNDO_ITEM_TYPE);		
					MetabolitesMetaColumnManager metabolitesMetaColumnManager = new MetabolitesMetaColumnManager();
					int metaColumnCount = metabolitesMetaColumnManager.getMetaColumnCount(LocalConfig.getInstance().getDatabaseName());
					undoItem.setAddedColumnIndex(metaColumnCount + GraphicalInterfaceConstants.METABOLITES_DB_COLUMN_NAMES.length - 1);
					setUpMetabolitesUndo(undoItem);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				addMetabColumn = false;
				addMetaboliteColumnCloseAction();
			}
		};
		
		ActionListener addMetabColCancelButtonActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent prodActionEvent) {
				closeConnection();
				addMetaboliteColumnCloseAction();
			}
		};
		
		metaboliteColAddRenameInterface.okButton.addActionListener(addMetabColOKButtonActionListener);
		metaboliteColAddRenameInterface.cancelButton.addActionListener(addMetabColCancelButtonActionListener);
		
		menuBar.add(editMenu);
		
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
							GraphicalInterfaceConstants.HELP_URL_NOT_FOUND_MESSAGE,                
							GraphicalInterfaceConstants.HELP_URL_NOT_FOUND_TITLE,                                
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
						GraphicalInterfaceConstants.ABOUT_BOX_TEXT,                
						GraphicalInterfaceConstants.ABOUT_BOX_TITLE,                                
						JOptionPane.INFORMATION_MESSAGE);
			}    	     
		});

		menuBar.add(helpMenu);

		/**************************************************************************/
		//end menu bar
		/**************************************************************************/

		/**************************************************************************/
		//set up toolbar
		/**************************************************************************/			
		
		toolbar.add(openbutton);
		setUpToolbarButton(openbutton);
		openbutton.setToolTipText("Load");
		toolbar.add(savebutton);
		setUpToolbarButton(savebutton);
		savebutton.setToolTipText("Save");	
		toolbar.add(copybutton);
		setUpToolbarButton(copybutton);
		copybutton.setToolTipText("Copy");
		copybutton.addActionListener(copyButtonActionListener);
		toolbar.add(pastebutton);
		setUpToolbarButton(pastebutton);
		pastebutton.setToolTipText("Paste");
		pastebutton.addActionListener(pasteButtonActionListener);
			
		toolbar.addSeparator();
		
		addImage(undoSplitButton, undoLabel);
		addImage(undoSplitButton, undoGrayedLabel);
		undoSplitButton.setToolTipText("Undo");	
		undoSplitButton.addMouseListener(undoButtonMouseListener);
		disableOptionComponent(undoSplitButton, undoLabel, undoGrayedLabel);
		toolbar.add(undoSplitButton);
		
		addImage(redoSplitButton, redoLabel);
		addImage(redoSplitButton, redoGrayedLabel);
		redoSplitButton.setToolTipText("Redo");
		redoSplitButton.addMouseListener(redoButtonMouseListener);
		disableOptionComponent(redoSplitButton, redoLabel, redoGrayedLabel);
		toolbar.add(redoSplitButton);		
		
		toolbar.add(findbutton);
		setUpToolbarButton(findbutton);
		findbutton.setToolTipText("Find/Replace");
		findbutton.addActionListener(findButtonActionListener);
		
		/**************************************************************************/
		//end set up toolbar
		/**************************************************************************/	
		
		/**************************************************************************/
		//set up tables
		/**************************************************************************/
		
		// register actions
		ActionListener reactionsCopyActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				reactionsCopy();
			}
		};
		
		ActionListener reactionsPasteActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				pasting = true;
				reactionsPaste();
				pasting = false;
				LocalConfig.getInstance().pastedReaction = false;
			}
		};
		
		ActionListener reactionsClearActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				reactionsClear();
			}
		};
		
		ActionListener reactionsFindActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {	
				if (!findMode) {
					showFindReplace();
				}
				findReplaceItem.setEnabled(false);
				findbutton.setEnabled(false);
			}
		};
		
		findReplaceDialog.findButton.addActionListener(findReactionsButtonActionListener);
		findReplaceDialog.findAllButton.addActionListener(findAllReactionsButtonActionListener);
		findReplaceDialog.replaceButton.addActionListener(replaceReactionsButtonActionListener);
		findReplaceDialog.replaceAllButton.addActionListener(replaceAllReactionsButtonActionListener);
		findReplaceDialog.replaceFindButton.addActionListener(replaceFindReactionsButtonActionListener);
		findReplaceDialog.doneButton.addActionListener(findDoneButtonActionListener);
		findReplaceDialog.caseCheckBox.addActionListener(matchCaseActionListener);
		findReplaceDialog.wrapCheckBox.addActionListener(wrapAroundActionListener);
		findReplaceDialog.selectedAreaCheckBox.addActionListener(selectedAreaActionListener);
		findReplaceDialog.backwardsCheckBox.addActionListener(searchBackwardsActionListener);
		
		KeyStroke reacCopy = KeyStroke.getKeyStroke(KeyEvent.VK_C,ActionEvent.CTRL_MASK,false);       
		KeyStroke reacPaste = KeyStroke.getKeyStroke(KeyEvent.VK_V,ActionEvent.CTRL_MASK,false); 		
		KeyStroke reacClear = KeyStroke.getKeyStroke(KeyEvent.VK_E,ActionEvent.CTRL_MASK,false); 
		KeyStroke reacFind = KeyStroke.getKeyStroke(KeyEvent.VK_F,ActionEvent.CTRL_MASK,false); 
		
		setUpReactionsTable(con);
		TableCellListener tcl = new TableCellListener(reactionsTable, reacAction);
		ReactionsPopupListener reactionsPopupListener = new ReactionsPopupListener();
		reactionsTable.addMouseListener(reactionsPopupListener);
		reactionsTable.setRowHeight(20);
		reactionsTable.registerKeyboardAction(reactionsCopyActionListener,reacCopy,JComponent.WHEN_FOCUSED); 
		reactionsTable.registerKeyboardAction(reactionsPasteActionListener,reacPaste,JComponent.WHEN_FOCUSED); 		
		reactionsTable.registerKeyboardAction(reactionsClearActionListener,reacClear,JComponent.WHEN_FOCUSED); 
		reactionsTable.registerKeyboardAction(reactionsFindActionListener,reacFind,JComponent.WHEN_IN_FOCUSED_WINDOW); 
		reactionsTable.registerKeyboardAction(reactionsFindActionListener,reacFind,JComponent.WHEN_FOCUSED); 
		
		// from http://www.java.net/node/651087
		// need tab to skip hidden columns		
		reactionsTable.getInputMap().put(KeyStroke.getKeyStroke("TAB"), "actionString");
		reactionsTable.getActionMap().put("actionString", new AbstractAction() {
			public void actionPerformed(ActionEvent ae) {
				// This overrides tab key and performs an action
				tabToNextVisibleCell(reactionsTable, getVisibleReactionsColumns());
			}
		});
		
		metabolitesTable.getInputMap().put(KeyStroke.getKeyStroke("TAB"), "actionString");
		metabolitesTable.getActionMap().put("actionString", new AbstractAction() {
			public void actionPerformed(ActionEvent ae) {
				// This overrides tab key and performs an action	
				tabToNextVisibleCell(metabolitesTable, getVisibleMetabolitesColumns());
			}
		});
		
		ActionListener metabolitesCopyActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				metabolitesCopy();
			}
		};
		
		ActionListener metabolitesPasteActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				pasting = true;
				metabolitesPaste();
				pasting = false;
			}
		};
		
		ActionListener metabolitesClearActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				metabolitesClear();
			}
		};
		
		ActionListener metabolitesFindActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				if (!findMode) {
					showFindReplace();
				}				
				findReplaceItem.setEnabled(false);
				findbutton.setEnabled(false);
			}
		};
		
		findReplaceDialog.findButton.addActionListener(findMetabolitesButtonActionListener);
		findReplaceDialog.findAllButton.addActionListener(findAllMetabolitesButtonActionListener);
		findReplaceDialog.replaceButton.addActionListener(replaceMetabolitesButtonActionListener);
		findReplaceDialog.replaceAllButton.addActionListener(replaceAllMetabolitesButtonActionListener);
		findReplaceDialog.replaceFindButton.addActionListener(replaceFindMetabolitesButtonActionListener);
		findReplaceDialog.doneButton.addActionListener(findDoneButtonActionListener);
		findReplaceDialog.caseCheckBox.addActionListener(matchCaseActionListener);
		findReplaceDialog.wrapCheckBox.addActionListener(wrapAroundActionListener);
		findReplaceDialog.selectedAreaCheckBox.addActionListener(selectedAreaActionListener);
		findReplaceDialog.backwardsCheckBox.addActionListener(searchBackwardsActionListener);
				
		KeyStroke metabCopy = KeyStroke.getKeyStroke(KeyEvent.VK_C,ActionEvent.CTRL_MASK,false);       
		KeyStroke metabPaste = KeyStroke.getKeyStroke(KeyEvent.VK_V,ActionEvent.CTRL_MASK,false);
		KeyStroke metabClear = KeyStroke.getKeyStroke(KeyEvent.VK_E,ActionEvent.CTRL_MASK,false);
		KeyStroke metabFind = KeyStroke.getKeyStroke(KeyEvent.VK_F,ActionEvent.CTRL_MASK,false);
		
		setUpMetabolitesTable(con);
		TableCellListener mtcl = new TableCellListener(metabolitesTable, metabAction);
		MetabolitesPopupListener metabolitesPopupListener = new MetabolitesPopupListener();
		metabolitesTable.addMouseListener(metabolitesPopupListener);
		metabolitesTable.setRowHeight(20);
		metabolitesTable.registerKeyboardAction(metabolitesCopyActionListener,metabCopy,JComponent.WHEN_FOCUSED); 
		metabolitesTable.registerKeyboardAction(metabolitesPasteActionListener,metabPaste,JComponent.WHEN_FOCUSED); 
		metabolitesTable.registerKeyboardAction(metabolitesClearActionListener,metabClear,JComponent.WHEN_FOCUSED);
		metabolitesTable.registerKeyboardAction(metabolitesFindActionListener,metabFind,JComponent.WHEN_IN_FOCUSED_WINDOW);
		metabolitesTable.registerKeyboardAction(metabolitesFindActionListener,metabFind,JComponent.WHEN_FOCUSED);

		setTableCellFocused(0, 1, metabolitesTable);
		setTableCellFocused(0, 1, reactionsTable);
		formulaBar.setText((String) reactionsTable.getModel().getValueAt(0, 1));     			
		
		DynamicTreeDemo.treePanel.setNodeSelected(0);
		
		/************************************************************************/
		//end set up tables
		/************************************************************************/

		/************************************************************************/
		//set up other components of gui
		/************************************************************************/
		
		formulaBar.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				setCellText();
				fieldChangeAction();
			}
			public void removeUpdate(DocumentEvent e) {
				setCellText();
				fieldChangeAction();
			}
			public void insertUpdate(DocumentEvent e) {
				setCellText();
				fieldChangeAction();
			}

			public void setCellText() {
				if (tabbedPane.getSelectedIndex() == 0 && reactionsTable.getSelectedRow() > -1 && reactionsTable.getSelectedColumn() > -1) {							
					int viewRow = reactionsTable.convertRowIndexToModel(reactionsTable.getSelectedRow());					
					if (formulaBarFocusGained) {
						reactionsTable.getModel().setValueAt(formulaBar.getText(), viewRow, reactionsTable.getSelectedColumn());    							
					}				
				} else if (tabbedPane.getSelectedIndex() == 1 && metabolitesTable.getSelectedRow() > -1 && metabolitesTable.getSelectedColumn() > -1) {		
					int viewRow = metabolitesTable.convertRowIndexToModel(metabolitesTable.getSelectedRow());
					if (formulaBarFocusGained) {
						try {
							metabolitesTable.getModel().setValueAt(formulaBar.getText(), viewRow, metabolitesTable.getSelectedColumn());    							
						} catch (Throwable t) {
							
						}					
					}
				} 
			}
			public void fieldChangeAction() {
				if (formulaBar.getText().length() > 0) {
					formulaBarCutItem.setEnabled(true);
					formulaBarCopyItem.setEnabled(true);
					formulaBarDeleteItem.setEnabled(true);
					formulaBarSelectAllItem.setEnabled(true);
				} else {
					formulaBarCutItem.setEnabled(false);
					formulaBarCopyItem.setEnabled(false);
					formulaBarDeleteItem.setEnabled(false);
					formulaBarSelectAllItem.setEnabled(false);
				}
			}
		});
		
		formulaBar.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				int key = e.getKeyCode();
				if (key == KeyEvent.VK_ENTER) {  
					// prevents editing of invisible id column for find events
					if (tabbedPane.getSelectedIndex() == 0 && reactionsTable.getSelectedRow() > -1 && reactionsTable.getSelectedColumn() > 0) {	
						try {
							updateReactionsCell();
						} catch (Throwable t) {
							
						}						
					} else if (tabbedPane.getSelectedIndex() == 1 && metabolitesTable.getSelectedRow() > -1 && metabolitesTable.getSelectedColumn() > 0) {
						try {
							updateMetabolitesCell();
						} catch (Throwable t) {
							
						}						
					} 
				}
			}
		}
		);
		
		formulaBar.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent arg0) {			
				if (tabbedPane.getSelectedIndex() == 0 && reactionsTable.getSelectedRow() > - 1) {
					int viewRow = reactionsTable.convertRowIndexToModel(reactionsTable.getSelectedRow());
					if (reactionsTable.getModel().getValueAt(viewRow, reactionsTable.getSelectedColumn()) != null) {
						setTableCellOldValue((String) reactionsTable.getModel().getValueAt(viewRow, reactionsTable.getSelectedColumn()));    			
					} else {
						setTableCellOldValue("");
					}
				} else if (tabbedPane.getSelectedIndex() == 1 && metabolitesTable.getSelectedRow() > - 1) {
					int viewRow = metabolitesTable.convertRowIndexToModel(metabolitesTable.getSelectedRow());
					if (metabolitesTable.getModel().getValueAt(viewRow, metabolitesTable.getSelectedColumn()) != null) {
						setTableCellOldValue((String) metabolitesTable.getModel().getValueAt(viewRow, metabolitesTable.getSelectedColumn()));    			
					} else {
						setTableCellOldValue("");
					}
				}
				
				formulaBarFocusGained = true;				
			}

			@Override
			public void focusLost(FocusEvent arg0) {
				formulaBarFocusGained = false;
				selectedCellChanged = true;	
			}
		});
			
		final JPopupMenu formulaBarPopupMenu = new JPopupMenu(); 
		formulaBarPopupMenu.add(formulaBarCutItem);
		formulaBarPopupMenu.add(formulaBarCopyItem);
		formulaBarPopupMenu.add(formulaBarPasteItem);
		formulaBarPopupMenu.add(formulaBarDeleteItem);
		formulaBarPopupMenu.addSeparator();
		formulaBarPopupMenu.add(formulaBarSelectAllItem);
		formulaBarCutItem.setEnabled(false);
		formulaBarCopyItem.setEnabled(false);
		formulaBarDeleteItem.setEnabled(false);
		formulaBarSelectAllItem.setEnabled(false);
		if (reactionsTable.getSelectedColumn() == GraphicalInterfaceConstants.REVERSIBLE_COLUMN || reactionsTable.getSelectedColumn() == GraphicalInterfaceConstants.REACTION_EQUN_NAMES_COLUMN) {
			formulaBarPasteItem.setEnabled(false);
		} else {
			formulaBarPasteItem.setEnabled(true);
		}
		formulaBarCutItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) { 
				setClipboardContents(formulaBar.getSelectedText());
				String selection = formulaBar.getSelectedText();	             
	            if(selection==null){
	                return;
	            }
	            formulaBar.replaceSelection("");				
			}
		});
		formulaBarCopyItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) { 	
				setClipboardContents(formulaBar.getSelectedText());
			}
		});
		formulaBarPasteItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) { 	
				try{
	                String clip_string = getClipboardContents(GraphicalInterface.this);
	                formulaBar.replaceSelection(clip_string);
	                 
	            }catch(Exception excpt){
	                 
	            }
			}
		});
		formulaBarDeleteItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) { 	
				formulaBar.setText("");
			}
		});
		formulaBarSelectAllItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) { 	
				formulaBar.selectAll();
			}
		});
		
		formulaBar.addMouseListener(new MouseAdapter() {

			public void mousePressed(MouseEvent e)  {check(e);}
			public void mouseReleased(MouseEvent e) {check(e);}

			public void check(MouseEvent e) {
				if (e.isPopupTrigger()) { //if the event shows the menu
					formulaBarPopupMenu.show(formulaBar, e.getX(), e.getY()); 
				}
			}
		});
		
		JScrollPane scrollPaneReac = new JScrollPane(reactionsTable);
		LineNumberTableRowHeader tableLineNumber = new LineNumberTableRowHeader(scrollPaneReac, reactionsTable);
		tableLineNumber.setBackground(new Color(240, 240, 240));
		scrollPaneReac.setRowHeaderView(tableLineNumber);
		JLabel rowLabel = new JLabel(GraphicalInterfaceConstants.ROW_HEADER_TITLE);
		rowLabel.setFont(rowLabel.getFont().deriveFont(Font.PLAIN));		
		scrollPaneReac.setCorner(JScrollPane.UPPER_LEFT_CORNER, rowLabel);
		scrollPaneReac.getCorner(JScrollPane.UPPER_LEFT_CORNER).addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				setSortDefault();
				reloadTables(LocalConfig.getInstance().getLoadedDatabase());
			}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
			public void mousePressed(MouseEvent arg0) {}
			public void mouseReleased(MouseEvent arg0) {}
		});
		tabbedPane.addTab(GraphicalInterfaceConstants.DEFAULT_REACTION_TABLE_TAB_NAME, scrollPaneReac);
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_R);

		JScrollPane scrollPaneMetab = new JScrollPane(metabolitesTable);
		LineNumberTableRowHeader tableMetabLineNumber = new LineNumberTableRowHeader(scrollPaneMetab, metabolitesTable);
		tableMetabLineNumber.setBackground(new Color(240, 240, 240));
		scrollPaneMetab.setRowHeaderView(tableMetabLineNumber);		
		JLabel metabRowLabel = new JLabel(GraphicalInterfaceConstants.ROW_HEADER_TITLE);
		metabRowLabel.setFont(rowLabel.getFont().deriveFont(Font.PLAIN));		
		scrollPaneMetab.setCorner(JScrollPane.UPPER_LEFT_CORNER, metabRowLabel);
		scrollPaneMetab.getCorner(JScrollPane.UPPER_LEFT_CORNER).addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				setSortDefault();
				reloadTables(LocalConfig.getInstance().getLoadedDatabase());
			}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
			public void mousePressed(MouseEvent arg0) {}
			public void mouseReleased(MouseEvent arg0) {}
		});
		tabbedPane.addTab(GraphicalInterfaceConstants.DEFAULT_METABOLITE_TABLE_TAB_NAME, scrollPaneMetab);
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_B);  	  
		
		tabbedPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				GraphicalInterface.this.requestFocus();
				tabChanged = true;
				int tabIndex = tabbedPane.getSelectedIndex();
				String reactionRow = Integer.toString((reactionsTable.getSelectedRow() + 1));
				String metaboliteRow = Integer.toString((metabolitesTable.getSelectedRow() + 1));
				if (tabIndex == 0 && reactionsTable.getSelectedRow() > - 1) {
					selectedCellChanged = true;
					if (LocalConfig.getInstance().getSuspiciousMetabolites().size() > 0) {
						setLoadErrorMessage("Model contains suspicious metabolites.");
						statusBar.setText("Row " + reactionRow + "                   " + getLoadErrorMessage());
					} else {
						statusBar.setText("Row " + reactionRow);
					}
					// prevents invisible id column from setting id in formulaBar for find events
					if (reactionsTable.getSelectedRow() > -1 && reactionsTable.getSelectedColumn() > 0) {
						int viewRow = reactionsTable.convertRowIndexToModel(reactionsTable.getSelectedRow());
		    			formulaBar.setText((String) reactionsTable.getModel().getValueAt(viewRow, reactionsTable.getSelectedColumn()));
		    			setTableCellOldValue(formulaBar.getText());
					} 
					enableOrDisableReactionsItems();
				} else if (tabIndex == 1 && metabolitesTable.getSelectedRow() > - 1) {
					selectedCellChanged = true;
					if (LocalConfig.getInstance().getSuspiciousMetabolites().size() > 0) {
						setLoadErrorMessage("Model contains suspicious metabolites.");
						statusBar.setText("Row " + metaboliteRow + "                   " + getLoadErrorMessage());
					} else {
						statusBar.setText("Row " + metaboliteRow);
					}					
					if (metabolitesTable.getSelectedRow() > -1 && metabolitesTable.getSelectedColumn() > 0) {
						int viewRow = metabolitesTable.convertRowIndexToModel(metabolitesTable.getSelectedRow());
						formulaBar.setText((String) metabolitesTable.getModel().getValueAt(viewRow, metabolitesTable.getSelectedColumn())); 
						setTableCellOldValue(formulaBar.getText());
					}
					enableOrDisableMetabolitesItems();
				} else {
					if (LocalConfig.getInstance().getSuspiciousMetabolites().size() > 0) {
						setLoadErrorMessage("Model contains suspicious metabolites.");
						statusBar.setText("Row 1" + "                   " + getLoadErrorMessage());
					} else {
						statusBar.setText("Row 1");
					}
					formulaBar.setText("");
				}
			}
		});
		
		JScrollPane outputPane = new JScrollPane(outputTextArea);
		statusBar.setFont(statusBar.getFont().deriveFont(Font.PLAIN));

		/************************************************************************/
		//end of set up other components of gui
		/************************************************************************/		
		
		/************************************************************************/
		//set frame layout - using TableLayout http://www.clearthought.info/sun/products/jfc/tsc/articles/tablelayout/Simple.html
		/************************************************************************/		
		
		double border = 10;
		double size[][] =
		{{border, TableLayout.FILL, 20, 0.20, border},  //Columns
				{border, 0.06, 10, 0.04, 10, TableLayout.FILL, 10, 0.15, 5, 0.02, border}}; // Rows

		setLayout (new TableLayout(size)); 

		add (toolbar, "0, 1, 4, 1");
		add (formulaBar, "1, 3, 1, 1");
		add (tabbedPane, "1, 5, 1, 1"); // Left
		add (newContentPane, "3, 3, 1, 7"); // Right
		add (outputPane, "1, 7, 1, 1"); // Bottom
		add (statusBar, "1, 9, 3, 1");

		setBackground(Color.lightGray);
	}
	/********************************************************************************/
	//end constructor and layout
	/********************************************************************************/
	
	/*******************************************************************************/
	//begin formulaBar methods and actions
	/*******************************************************************************/ 
	
	public void updateReactionsCell() {
		if (formulaBar.getText() != null) {
			LocalConfig.getInstance().reactionsTableChanged = true;
		}						
		int viewRow = reactionsTable.convertRowIndexToModel(reactionsTable.getSelectedRow());
		String newValue = formulaBar.getText();
		ReactionUndoItem undoItem = createReactionUndoItem(getTableCellOldValue(), newValue, reactionsTable.getSelectedRow(), reactionsTable.getSelectedColumn(), viewRow + 1, UndoConstants.TYPING, UndoConstants.REACTION_UNDO_ITEM_TYPE);
		updateReactionsCellIfValid(getTableCellOldValue(), newValue, viewRow, reactionsTable.getSelectedColumn());
		if (reactionUpdateValid) {
			setUpReactionsUndo(undoItem);
		}
	}
	
	public void updateMetabolitesCell() {
		if (formulaBar.getText() != null) {
			LocalConfig.getInstance().metabolitesTableChanged = true;
		}						
		int viewRow = metabolitesTable.convertRowIndexToModel(metabolitesTable.getSelectedRow());		
		String newValue = formulaBar.getText();
		// these variables are needed since after the table is reloaded, there is
		// no selected cell
		int row = metabolitesTable.getSelectedRow();
		int col = metabolitesTable.getSelectedColumn();
		MetaboliteUndoItem undoItem = createMetaboliteUndoItem(getTableCellOldValue(), newValue, row, col, viewRow + 1, UndoConstants.TYPING, UndoConstants.METABOLITE_UNDO_ITEM_TYPE);
		setUndoOldCollections(undoItem);
		updateMetabolitesCellIfValid(getTableCellOldValue(), newValue, viewRow, metabolitesTable.getSelectedColumn());	
		reloadTables(LocalConfig.getInstance().getLoadedDatabase());
		if (metaboliteUpdateValid) {
			setUpMetabolitesUndo(undoItem);
		}		
	}
	
	/*******************************************************************************/
	//end formulaBar methods and actions
	/*******************************************************************************/ 
	
	/*******************************************************************************/
	//begin Model menu methods and actions
	/*******************************************************************************/ 
	
	/*******************************************************************************/
	//load methods and actions
	/*******************************************************************************/ 
	
	class LoadSBMLAction implements ActionListener {
		public void actionPerformed(ActionEvent ae) { 
			SaveChangesPrompt();
			progressBar.progress.setValue(0);
			LocalConfig.getInstance().setProgress(0);	  
			JTextArea output = null;
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setDialogTitle("Load SBML File"); 
			fileChooser.setFileFilter(new SBMLFileFilter());
			fileChooser.setFileFilter(new XMLFileFilter());
			fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);			
			//TODO: test the possibility of a global FileChooser
			
			String lastSBML_path = curSettings.get("LastSBML");
			if (lastSBML_path == null) {
				lastSBML_path = ".";
			}
			fileChooser.setCurrentDirectory(new File(lastSBML_path));
			
			//... Open a file dialog.
			int retval = fileChooser.showOpenDialog(output);
			if (retval == JFileChooser.APPROVE_OPTION) {
				loadSetUp();
				//... The user selected a file, get it, use it.
				File file = fileChooser.getSelectedFile();          	
				String rawPathName = file.getAbsolutePath();
				curSettings.add("LastSBML", rawPathName);
				
				String rawFilename = file.getName();				
				if (!rawFilename.endsWith(".xml") && !rawFilename.endsWith(".sbml")) {
					JOptionPane.showMessageDialog(null,                
							"Not a Valid SBML File.",                
							"Invalid SBML File",                                
							JOptionPane.ERROR_MESSAGE);
					validFile = false;
				} else {
					//fileList.setSelectedIndex(-1);
					listModel.clear();
					//fileList.setModel(listModel);
					
					DynamicTreeDemo.treePanel.clear();
					
					String filename;
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

					timer.start();

					task = new Task();
					task.execute();
				}
			}
		}
	}

	class LoadCSVAction implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			SaveChangesPrompt();
			setExtension(".csv");	
			csvLoadInterface.textMetabField.setText("");
			csvLoadInterface.textReacField.setText("");
			LocalConfig.getInstance().setMetabolitesCSVFile(null);
			LocalConfig.getInstance().hasMetabolitesFile = false;
			LocalConfig.getInstance().hasReactionsFile = false;
			csvLoadInterface.okButton.setEnabled(false);
			csvLoadInterface.setVisible(true);	
		}
	}
	
	//listens for ok button event in CSVLoadInterface
	ActionListener okButtonCSVLoadActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent ae) {	
			csvLoadInterface.setVisible(false);
			csvLoadInterface.dispose();	
			loadSetUp();
			isCSVFile = true;
			loadCSV();
			progressBar.setVisible(false);
			progressBar.setTitle("Loading...");
			// Timer used by time listener to set up tables  
			// and set progress bar not visible
			timer.start();
		}
	}; 
	
	ActionListener cancelButtonCSVLoadActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent ae) {	
			csvLoadInterface.setVisible(false);
			clearTables();
		}
	};
	
	public void loadCSV() {	
		DynamicTreeDemo.treePanel.clear();
		//fileList.setSelectedIndex(-1);
		listModel.clear();
		//fileList.setModel(listModel); 
		closeConnection();
		try {			
			Class.forName("org.sqlite.JDBC");
			Connection con = DriverManager.getConnection(createConnectionStatement(getDatabaseName()));
			LocalConfig.getInstance().setCurrentConnection(con);

			LocalConfig.getInstance().setMetabolitesNextRowCorrection(0);

			if (LocalConfig.getInstance().getMetabolitesCSVFile() != null) {
				TextMetabolitesModelReader reader = new TextMetabolitesModelReader();
				ArrayList<String> columnNamesFromFile = reader.columnNamesFromFile(LocalConfig.getInstance().getMetabolitesCSVFile(), 0);
				MetaboliteColumnNameInterface columnNameInterface = new MetaboliteColumnNameInterface(con, columnNamesFromFile);
				setMetaboliteColumnNameInterface(columnNameInterface);
				getMetaboliteColumnNameInterface().setIconImages(icons);					
				getMetaboliteColumnNameInterface().setSize(600, 360);
				getMetaboliteColumnNameInterface().setResizable(false);
				getMetaboliteColumnNameInterface().setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
				getMetaboliteColumnNameInterface().setLocationRelativeTo(null);											
				getMetaboliteColumnNameInterface().addWindowListener(new WindowAdapter() {
			        public void windowClosing(WindowEvent evt) {
			        	metaboliteColumnNameCloseAction();	        	
			        }
				});
				getMetaboliteColumnNameInterface().cancelButton.addActionListener(cancelButtonCSVMetabLoadActionListener);
				getMetaboliteColumnNameInterface().setModal(true);
				getMetaboliteColumnNameInterface().setVisible(true);
			} else {
				LocalConfig.getInstance().setProgress(100);
			}

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public void metaboliteColumnNameCloseAction() {
		getMetaboliteColumnNameInterface().setVisible(false);
    	getMetaboliteColumnNameInterface().dispose();
    	clearTables();	
	}
	
	ActionListener cancelButtonCSVMetabLoadActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent ae) {
			metaboliteColumnNameCloseAction();
		}
	};
	
	class LoadSQLiteItemAction implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			SaveChangesPrompt();
			loadSetUp();
			DynamicTreeDemo.treePanel.clear();
			JTextArea output = null;
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setDialogTitle("Load SQLite Database File");
			fileChooser.setFileFilter(new SQLiteFileFilter());
			fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			
			String lastSQL_path = curSettings.get("LastSQL");
			if (lastSQL_path == null) {
				lastSQL_path = ".";
			}
			fileChooser.setCurrentDirectory(new File(lastSQL_path));
			
			//... Open a file dialog.
			int retval = fileChooser.showOpenDialog(output);
			if (retval == JFileChooser.APPROVE_OPTION) {
				//... The user selected a file, get it, use it.
				String rawFilename = fileChooser.getSelectedFile().getName();
				String rawPathName = fileChooser.getSelectedFile().getAbsolutePath();
				curSettings.add("LastSQL", rawPathName);
				if (!rawFilename.endsWith(".db")) {
					JOptionPane.showMessageDialog(null,                
							"Not a Valid Database File.",                
							"Invalid Database File",                                
							JOptionPane.ERROR_MESSAGE);
				} else {
					//fileList.setSelectedIndex(-1);
					listModel.clear();
					//fileList.setModel(listModel);
					//String filename = rawFilename.substring(0, rawFilename.length() - 3);
					String rawPath = fileChooser.getSelectedFile().getPath();
					String path = rawPath.substring(0, rawPath.length() - 3);
					setDatabaseName(path);
					DatabaseCopier copier = new DatabaseCopier();
					copier.copyDatabase(path, path + GraphicalInterfaceConstants.DB_COPIER_SUFFIX);
					LocalConfig.getInstance().setLoadedDatabase(path);
					setUpTables();
					DatabaseCreator creator = new DatabaseCreator();
					creator.copyTables(LocalConfig.getInstance().getLoadedDatabase(), tableCopySuffix(0));
					// check for invalid reactions and add to invalid list
					SQLiteLoader loader = new SQLiteLoader();
					ArrayList<String> invalidReactions = loader.invalidReactions(path);
					LocalConfig.getInstance().setInvalidReactions(invalidReactions);
									
					Map<String, Object> metaboliteIdNameMap = loader.metaboliteIdNameMap(path);
					LocalConfig.getInstance().setMetaboliteIdNameMap(metaboliteIdNameMap);
					
					Map<String, Object> metaboliteUsedNameMap = loader.metaboliteUsedMap(path);
					LocalConfig.getInstance().setMetaboliteUsedMap(metaboliteUsedNameMap);
					
					ArrayList<Integer> suspiciousMetabolites = loader.suspiciousMetabolites(path);
					LocalConfig.getInstance().setSuspiciousMetabolites(suspiciousMetabolites);
					if (suspiciousMetabolites.size() > 0) {
						setLoadErrorMessage("Model contains suspicious metabolites.");
						statusBar.setText("Row 1                   " + getLoadErrorMessage());
						findSuspiciousItem.setEnabled(true);
					} else {
						statusBar.setText("Row 1");
					}					
				}
			}
		}
	} 
	
	class LoadExistingItemAction implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			File f = new File("ModelCollection.csv");
			ModelCollectionTable mcTable = new ModelCollectionTable(f);
			mcTable.setIconImages(icons);
			mcTable.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
			//mcTable.setAlwaysOnTop(true);
			mcTable.setVisible(true);
			mcTable.setLocationRelativeTo(null);
			setModelCollectionTable(mcTable);
			mcTable.okButton.addActionListener(modelCollectionOKButtonActionListener);
			mcTable.cancelButton.addActionListener(modelCollectionCancelButtonActionListener);
			loadExistingItem.setEnabled(false);
			mcTable.addWindowListener(new WindowAdapter() {
		        public void windowClosing(WindowEvent evt) {
		        	loadExistingItem.setEnabled(true);
		        	getModelCollectionTable().setVisible(false);
		        	getModelCollectionTable().dispose();
		        }
			});	
		}
	}
		
	ActionListener modelCollectionOKButtonActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent ae) {			
			if (!modelCollectionOKButtonClicked) {
				SaveChangesPrompt();
				loadSetUp();
				progressBar.progress.setValue(0);
				LocalConfig.getInstance().setProgress(0);	
				String path = getModelCollectionTable().getPath();
				File file = new File(path);
				setSBMLFile(file);
				setDatabaseName(getModelCollectionTable().getFileName()); 
				LocalConfig.getInstance().setLoadedDatabase(getModelCollectionTable().getFileName());
				//loadExistingItem.setEnabled(true);
				LocalConfig.getInstance().setProgress(0);
				progressBar.setVisible(true);
				getModelCollectionTable().setExtendedState(getModelCollectionTable().ICONIFIED);
				timer.start();

				task = new Task();
				task.execute();
				modelCollectionOKButtonClicked = true;
			}			
		}
	};
	
	ActionListener modelCollectionCancelButtonActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent ae) {						
			loadExistingItem.setEnabled(true);						
		}
	};
	
	/*
	public void clearFileList() {
		fileList.setSelectedIndex(-1);
		listModel.clear();
		fileList.setModel(listModel);
	} 
	*/
	
	/*******************************************************************************/
	//end load methods and actions
	/*******************************************************************************/
		
	/*******************************************************************************/
	//save methods and actions
	/*******************************************************************************/
	
	class SaveSBMLItemAction implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			//System.out.println("Hello");
			try {
				JSBMLWriter jWrite = new JSBMLWriter();
				
				jWrite.formConnect(LocalConfig.getInstance());
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	class SaveCSVMetabolitesItemAction implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			saveMetabolitesTextFileChooser();
		}
	}

	public void saveMetabolitesTextFile(String path, String filename) {
		String oldName = getDatabaseName();
		TextMetabolitesWriter writer = new TextMetabolitesWriter();
		writer.write(path, LocalConfig.getInstance().getLoadedDatabase());				    		
		setTitle(GraphicalInterfaceConstants.TITLE + " - " + filename);
		//fileList.setSelectedIndex(-1);
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
			DynamicTreeDemo.treePanel.addObject(new Solution(GraphicalInterfaceConstants.OPTIMIZATION_PREFIX + filename + suffixList.get(i)));
			copier.copyDatabase(GraphicalInterfaceConstants.OPTIMIZATION_PREFIX + oldName + suffixList.get(i), GraphicalInterfaceConstants.OPTIMIZATION_PREFIX + filename + suffixList.get(i));
			copier.copyLogFile(GraphicalInterfaceConstants.OPTIMIZATION_PREFIX + oldName + suffixList.get(i), GraphicalInterfaceConstants.OPTIMIZATION_PREFIX + filename + suffixList.get(i));
		}

		//fileList.setModel(listModel);
		clearOutputPane();
	}

	public void saveMetabolitesTextFileChooser() {
		JTextArea output = null;
		JFileChooser fileChooser = new JFileChooser(new File(getDatabaseName()));
		fileChooser.setDialogTitle("Save CSV Metabolites File");
		fileChooser.setFileFilter(new CSVFileFilter());
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		
		String lastCSV_path = curSettings.get("LastCSV");
		if (lastCSV_path == null) {
			lastCSV_path = ".";	
		}
		fileChooser.setCurrentDirectory(new File(lastCSV_path));
		
		boolean done = false;
		while (!done) {
			//... Open a file dialog.
			int retval = fileChooser.showSaveDialog(output);
			if (retval == JFileChooser.CANCEL_OPTION) {
				done = true;
				exit = false;
			}
			if (retval == JFileChooser.APPROVE_OPTION) {
				//... The user selected a file, get it, use it.
				String rawPathName = fileChooser.getSelectedFile().getAbsolutePath();
				curSettings.add("LastCSV", rawPathName);

				LocalConfig.getInstance().hasMetabolitesFile = true;
				
				//checks if filename endswith .csv else renames file to end with .csv
				String path = fileChooser.getSelectedFile().getPath();
				String filename = fileChooser.getSelectedFile().getName();
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
			// Message box displayed if model contains invalid reactions
			if (LocalConfig.getInstance().getInvalidReactions().size() > 0) {
				Object[] options = {"    Yes    ", "    No    ",};
				int choice = JOptionPane.showOptionDialog(null, 
						GraphicalInterfaceConstants.INVALID_REACTIONS_ERROR_MESSAGE, 
						GraphicalInterfaceConstants.INVALID_REACTIONS_ERROR_TITLE, 
						JOptionPane.YES_NO_OPTION, 
						JOptionPane.QUESTION_MESSAGE, 
						null, options, options[0]);
				if (choice == JOptionPane.YES_OPTION) {
					saveReactionsTextFileChooser(); 
				}
				if (choice == JOptionPane.NO_OPTION) {

				}							
			} else {
				saveReactionsTextFileChooser(); 
			}
		}
	}

	public void saveReactionsTextFile(String path, String filename) {
		String oldName = getDatabaseName();
		TextReactionsWriter writer = new TextReactionsWriter();
		writer.write(path, LocalConfig.getInstance().getLoadedDatabase());				    
		setTitle(GraphicalInterfaceConstants.TITLE + " - " + filename);	
		if (!saveOptFile) {
			//fileList.setSelectedIndex(-1);
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

			//fileList.setModel(listModel);
			DynamicTreeDemo.treePanel.addObject(new Solution(GraphicalInterface.listModel.get(GraphicalInterface.listModel.getSize() - 1)));
			clearOutputPane();
		}	
		saveOptFile = false;
	}

	public void saveReactionsTextFileChooser() {
		JTextArea output = null;
		JFileChooser fileChooser = new JFileChooser(new File(getDatabaseName()));
		fileChooser.setDialogTitle("Save CSV Reactions File");
		fileChooser.setFileFilter(new CSVFileFilter());
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		
		String lastCSV_path = curSettings.get("LastCSV");
		if (lastCSV_path == null) {
			lastCSV_path = ".";	
		}
		fileChooser.setCurrentDirectory(new File(lastCSV_path));
		
		boolean done = false;
		while (!done) {
			//... Open a file dialog.
			File file = null;
			if (saveOptFile) {
				file = new File(listModel.getElementAt(fileList.getSelectedIndex()));
				fileChooser.setSelectedFile(file);
			}
			int retval = fileChooser.showSaveDialog(output);
			if (retval == JFileChooser.CANCEL_OPTION) {
				done = true;
				exit = false;
			}
			if (retval == JFileChooser.APPROVE_OPTION) {            	  
				//... The user selected a file, get it, use it.
				String rawPathName = fileChooser.getSelectedFile().getAbsolutePath();
				curSettings.add("LastCSV", rawPathName);
				
				LocalConfig.getInstance().hasReactionsFile = true;
				
				String path = "";
				String filename = "";
				
				//checks if filename endswith .csv else renames file to end with .csv
				path = fileChooser.getSelectedFile().getPath();
				filename = fileChooser.getSelectedFile().getName();
												
				if (!path.endsWith(".csv")) {
					path = path + ".csv";
				}
				
				file = new File(path);
				
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
		// copy database so if modified and user does not want to save changes
		// can recover original state
		copier.copyDatabase(getDatabaseName(), getDBPath());
		setDatabaseName(getDBPath()); 
		copier.copyDatabase(getDatabaseName(), getDBPath() + GraphicalInterfaceConstants.DB_COPIER_SUFFIX);
		closeConnection();
		reloadTables(getDatabaseName());
		//fileList.setSelectedIndex(-1);
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

		//fileList.setModel(listModel);
		clearOutputPane();

	}

	public void saveSQLiteFileChooser() {
		saveDatabaseFile = true;
		LocalConfig.getInstance().metabolitesTableChanged = false;
		LocalConfig.getInstance().reactionsTableChanged = false;
		JTextArea output = null;
		JFileChooser fileChooser = new JFileChooser(new File(getDatabaseName()));
		fileChooser.setDialogTitle("Save SQLite Database File");
		fileChooser.setFileFilter(new SQLiteFileFilter());
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		
		String lastSQL_path = curSettings.get("LastSQL");
		if (lastSQL_path == null) {
			lastSQL_path = ".";
		}
		fileChooser.setCurrentDirectory(new File(lastSQL_path));
		
		boolean done = false;
		while (!done) {			
			//... Open a file dialog.
			int retval = fileChooser.showSaveDialog(output);
			if (retval == JFileChooser.CANCEL_OPTION) {
				done = true;
				exit = false;
			}
			if (retval == JFileChooser.APPROVE_OPTION) {            	  
				//... The user selected a file, get it, use it.
				//TODO: check if any of these are redundant
				String rawPath = fileChooser.getSelectedFile().getPath();
				setDBPath(rawPath);
				String rawFilename = fileChooser.getSelectedFile().getName();
				String rawPathName = fileChooser.getSelectedFile().getAbsolutePath();
				curSettings.add("LastSQL", rawPathName);
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
			if (LocalConfig.getInstance().getInvalidReactions().size() > 0) {
				Object[] options = {"    Yes    ", "    No    ",};
				int choice = JOptionPane.showOptionDialog(null, 
						GraphicalInterfaceConstants.INVALID_REACTIONS_ERROR_MESSAGE, 
						GraphicalInterfaceConstants.INVALID_REACTIONS_ERROR_TITLE, 
						JOptionPane.YES_NO_OPTION, 
						JOptionPane.QUESTION_MESSAGE, 
						null, options, options[0]);
				if (choice == JOptionPane.YES_OPTION) {
					saveSQLiteFileChooser();
				}
				if (choice == JOptionPane.NO_OPTION) {

				}							
			} else {
				saveSQLiteFileChooser();
			}
		}
	};
	
	class SBMLFileFilter extends javax.swing.filechooser.FileFilter {
	    public boolean accept(File f) {
	        return f.isDirectory() || f.getName().toLowerCase().endsWith(".sbml");
	    }
	    
	    public String getDescription() {
	        return ".sbml files";
	    }
	}
	
	class XMLFileFilter extends javax.swing.filechooser.FileFilter {
	    public boolean accept(File f) {
	        return f.isDirectory() || f.getName().toLowerCase().endsWith(".xml");
	    }
	    
	    public String getDescription() {
	        return ".xml files";
	    }
	}
	
	class SQLiteFileFilter extends javax.swing.filechooser.FileFilter {
	    public boolean accept(File f) {
	        return f.isDirectory() || f.getName().toLowerCase().endsWith(".db");
	    }
	    
	    public String getDescription() {
	        return "SQLite .db files";
	    }
	}
	
	class CSVFileFilter extends javax.swing.filechooser.FileFilter {
	    public boolean accept(File f) {
	        return f.isDirectory() || f.getName().toLowerCase().endsWith(".csv");
	    }
	    
	    public String getDescription() {
	        return ".csv files";
	    }
	}
	
	/*******************************************************************************/
	// end save methods and actions
	/*******************************************************************************/
	
	class ClearAction implements ActionListener {
		public void actionPerformed(ActionEvent cae) {
			clearTables();
		}
	}
	
	public void clearTables() {
		SaveChangesPrompt();
		loadSetUp();
		closeConnection();
		try {
			Class.forName("org.sqlite.JDBC");       
			DatabaseCreator databaseCreator = new DatabaseCreator();
			setDatabaseName(ConfigConstants.DEFAULT_DATABASE_NAME);
			LocalConfig.getInstance().setLoadedDatabase(ConfigConstants.DEFAULT_DATABASE_NAME);
			Connection con = DriverManager.getConnection("jdbc:sqlite:" + ConfigConstants.DEFAULT_DATABASE_NAME + ".db");
			LocalConfig.getInstance().setCurrentConnection(con);
			databaseCreator.createDatabase(LocalConfig.getInstance().getDatabaseName());
			databaseCreator.addRows(LocalConfig.getInstance().getDatabaseName(), GraphicalInterfaceConstants.BLANK_DB_METABOLITE_ROW_COUNT, GraphicalInterfaceConstants.BLANK_DB_REACTION_ROW_COUNT);
			databaseCreator.copyTables(LocalConfig.getInstance().getDatabaseName(), tableCopySuffix(0));
			reloadTables(getDatabaseName());
			listModel.clear();
			listModel.addElement(ConfigConstants.DEFAULT_DATABASE_NAME);
			DynamicTreeDemo.treePanel.clear();
			DynamicTreeDemo.treePanel.addObject(new Solution(GraphicalInterface.listModel.get(GraphicalInterface.listModel.getSize() - 1), ConfigConstants.DEFAULT_DATABASE_NAME));			
			//fileList.setModel(listModel);
			//fileList.setSelectedIndex(0);
			setTableCellFocused(0, 1, metabolitesTable);
			setTableCellFocused(0, 1, reactionsTable);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	class ExitAction implements ActionListener {
		public void actionPerformed(ActionEvent cae) {
			SaveChangesPrompt();
			closeConnection();
			if (exit) {
				// Exit the application
		        System.exit(0);	
			}			
		}
	}
	
	public void SaveChangesPrompt() {
		Utilities util = new Utilities();
		boolean saveChanges = true;
		if (LocalConfig.getInstance().metabolitesTableChanged || LocalConfig.getInstance().reactionsTableChanged || LocalConfig.getInstance().getOptimizationFilesList().size() > 0) {
			Object[] options = {"  Yes  ", "   No   ", "Cancel"};
			String message = "";
			String suffix = " Save changes?";
			if (LocalConfig.getInstance().getOptimizationFilesList().size() > 0) {
				message = "Optimizations have not been saved. ";
			}
			if (LocalConfig.getInstance().metabolitesTableChanged && LocalConfig.getInstance().reactionsTableChanged) {
				message += "Reactions table and Metabolites table changed." + suffix;
			} else if (LocalConfig.getInstance().reactionsTableChanged) {
				message += "Reactions table changed." + suffix;
			} else if (LocalConfig.getInstance().metabolitesTableChanged) {
				message += "Metabolites table changed." + suffix;
			} else {
				message += suffix;
			}
			closeConnection();
			int choice = JOptionPane.showOptionDialog(null, 
					message, 
					"Save Changes?",  
					JOptionPane.YES_NO_CANCEL_OPTION, 
					JOptionPane.QUESTION_MESSAGE, 
					null, options, options[0]);
			//options[0] sets "Yes" as default button
			// interpret the user's choice	  
			if (choice == JOptionPane.YES_OPTION)
			{
				if (saveDatabaseFile) {
					saveSQLiteFileChooser();
				} else {
					if (LocalConfig.getInstance().metabolitesTableChanged) {
						saveMetabolitesTextFileChooser();
					}
					if (LocalConfig.getInstance().reactionsTableChanged) {
						saveReactionsTextFileChooser();
					}
				}				
				if (LocalConfig.getInstance().getOptimizationFilesList().size() > 0) {				
					for (int i = 0; i < LocalConfig.getInstance().getOptimizationFilesList().size(); i++) {
						// TODO: determine where and how to display these messages
						//System.out.println(LocalConfig.getInstance().getOptimizationFilesList().get(i) + ".db will be saved.");
					}
				}
				LocalConfig.getInstance().getOptimizationFilesList().clear();
				exit = true;
				//System.exit(0);
			}
			if (choice == JOptionPane.NO_OPTION)
			{
				//TODO: if "_orig" db exists rename to db w/out "_orig", delete db w/out "_orig"
				// or delete db
				deleteAllOptimizationFiles();
				exit = true;
				//System.exit(0);
				saveChanges = false;
			}
			if (choice == JOptionPane.CANCEL_OPTION) {
				exit = false;
			}
		}
		
		if (LocalConfig.getInstance().getLoadedDatabase().compareTo(GraphicalInterfaceConstants.DEFAULT_DATABASE_NAME) != 0) {
			if (!saveDatabaseFile) {
				closeConnection();
				util.deleteFileIfExists(LocalConfig.getInstance().getLoadedDatabase() + ".db");
			} else {
				if (saveChanges) {
					util.deleteFileIfExists(LocalConfig.getInstance().getLoadedDatabase() + GraphicalInterfaceConstants.DB_COPIER_SUFFIX + ".db");
				} else {
					util.deleteFileIfExists(LocalConfig.getInstance().getLoadedDatabase() + ".db");
					// rename file with GraphicalInterfaceConstants.DB_COPIER_SUFFIX to filename without suffix
					util.renameFile(LocalConfig.getInstance().getLoadedDatabase() + GraphicalInterfaceConstants.DB_COPIER_SUFFIX + ".db", LocalConfig.getInstance().getLoadedDatabase() + ".db");
				}
			}
		}			
	}	
	
	/*******************************************************************************/
	//end Model menu methods and actions
	/*******************************************************************************/ 
	
	/*******************************************************************************/
	//begin table methods and actions
	/*******************************************************************************/ 
	
	/*******************************************************************************/
	//table actions
	/*******************************************************************************/ 
		
	//based on code from http://tips4java.wordpress.com/2009/06/07/table-cell-listener/
	Action reacAction = new AbstractAction()
	{
		public void actionPerformed(ActionEvent ae)
		{		  	
			TableCellListener tcl = (TableCellListener)ae.getSource();
		
			if (tcl.getOldValue() != tcl.getNewValue()) {
				int id = Integer.parseInt((String) (reactionsTable.getModel().getValueAt(tcl.getRow(), 0)));
				ReactionUndoItem undoItem = createReactionUndoItem(tcl.getOldValue(), tcl.getNewValue(), reactionsTable.getSelectedRow(), tcl.getColumn(), id, UndoConstants.TYPING, UndoConstants.REACTION_UNDO_ITEM_TYPE);
				LocalConfig.getInstance().reactionsTableChanged = true;
				updateReactionsCellIfValid(tcl.getOldValue(), tcl.getNewValue(), tcl.getRow(), tcl.getColumn());
				if (reactionUpdateValid) {
					setUpReactionsUndo(undoItem);
				}
			}
		}
	};
	
	Action metabAction = new AbstractAction()
	{
		public void actionPerformed(ActionEvent e)
		{   	  
			TableCellListener mtcl = (TableCellListener)e.getSource();
			
			if (mtcl.getOldValue() != mtcl.getNewValue()) {				
				LocalConfig.getInstance().metabolitesTableChanged = true;
				int id = Integer.parseInt((String) (metabolitesTable.getModel().getValueAt(mtcl.getRow(), 0)));
				MetaboliteUndoItem undoItem = createMetaboliteUndoItem(mtcl.getOldValue(), mtcl.getNewValue(), metabolitesTable.getSelectedRow(), mtcl.getColumn(), id, UndoConstants.TYPING, UndoConstants.METABOLITE_UNDO_ITEM_TYPE);
				setUndoOldCollections(undoItem);
				updateMetabolitesCellIfValid(mtcl.getOldValue(), mtcl.getNewValue(), mtcl.getRow(), mtcl.getColumn());
				if (metaboliteUpdateValid) {
					setUpMetabolitesUndo(undoItem);
				}
			}			
		}
	};
    
	// updates reactions table with new value is valid, else reverts to old value
	public void updateReactionsCellIfValid(String oldValue, String newValue, int rowIndex, int colIndex) {		
		reactionUpdateValid = true;
		EntryValidator validator = new EntryValidator();
		LocalConfig.getInstance().editMode = true;
		int id = Integer.parseInt((String) (reactionsTable.getModel().getValueAt(rowIndex, 0)));		
		if (colIndex == GraphicalInterfaceConstants.REACTION_EQUN_ABBR_COLUMN && LocalConfig.getInstance().includesReactions) {				
			ReactionsUpdater updater = new ReactionsUpdater();
			//  if reaction is changed unhighlight unused metabolites since
			//  used status may change, same with participating reactions
			highlightUnusedMetabolites = false;
			highlightUnusedMetabolitesItem.setState(false);
			// if reaction is reversible, no need to check lower bound
			if (newValue.contains("<") || (newValue.contains("=") && !newValue.contains(">"))) {					
				updater.updateReactionEquations(id, oldValue, newValue, LocalConfig.getInstance().getLoadedDatabase());
				reactionsTable.getModel().setValueAt(newValue, rowIndex, GraphicalInterfaceConstants.REACTION_EQUN_ABBR_COLUMN);
				updateReactionsDatabaseRow(rowIndex, Integer.parseInt((String) (reactionsTable.getModel().getValueAt(rowIndex, 0))), "SBML", LocalConfig.getInstance().getLoadedDatabase());					
				// check if lower bound is >= 0 if reversible = false
			} else if (newValue.contains("-->") || newValue.contains("->") || newValue.contains("=>")) {
				// if lower bound < 0, display option dialog
				if (Double.valueOf((String) reactionsTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.LOWER_BOUND_COLUMN)) < 0)  {
					if (!replaceAllMode) {
						setFindReplaceAlwaysOnTop(false);
						Object[] options = {"    Yes    ", "    No    ",};
						int choice = JOptionPane.showOptionDialog(null, 
								GraphicalInterfaceConstants.LOWER_BOUND_ERROR_MESSAGE, 
								GraphicalInterfaceConstants.LOWER_BOUND_ERROR_TITLE, 
								JOptionPane.YES_NO_OPTION, 
								JOptionPane.QUESTION_MESSAGE, 
								null, options, options[0]);
						// set lower bound to 0 and set new equation
						if (choice == JOptionPane.YES_OPTION) {
							reactionsTable.getModel().setValueAt("0.0", rowIndex, GraphicalInterfaceConstants.LOWER_BOUND_COLUMN);
							reactionsTable.getModel().setValueAt("false", rowIndex, GraphicalInterfaceConstants.REVERSIBLE_COLUMN);
							reactionsTable.getModel().setValueAt(newValue, rowIndex, GraphicalInterfaceConstants.REACTION_EQUN_ABBR_COLUMN);
							updateReactionsDatabaseRow(rowIndex, Integer.parseInt((String) (reactionsTable.getModel().getValueAt(rowIndex, 0))), "SBML", LocalConfig.getInstance().getLoadedDatabase());	
							updater.updateReactionEquations(id, oldValue, newValue, LocalConfig.getInstance().getLoadedDatabase());
						}
						// set old equation
						if (choice == JOptionPane.NO_OPTION) {
							reactionsTable.getModel().setValueAt(oldValue, rowIndex, GraphicalInterfaceConstants.REACTION_EQUN_ABBR_COLUMN);
							updateReactionsDatabaseRow(rowIndex, Integer.parseInt((String) (reactionsTable.getModel().getValueAt(rowIndex, 0))), "SBML", LocalConfig.getInstance().getLoadedDatabase());					
						}
						setFindReplaceAlwaysOnTop(true);
						// if in replace all mode, just set lower bound to 0 and set new equation
					} else {
						reactionsTable.getModel().setValueAt("0.0", rowIndex, GraphicalInterfaceConstants.LOWER_BOUND_COLUMN);
						reactionsTable.getModel().setValueAt("false", rowIndex, GraphicalInterfaceConstants.REVERSIBLE_COLUMN);
						reactionsTable.getModel().setValueAt(newValue, rowIndex, GraphicalInterfaceConstants.REACTION_EQUN_ABBR_COLUMN);
						updateReactionsDatabaseRow(rowIndex, Integer.parseInt((String) (reactionsTable.getModel().getValueAt(rowIndex, 0))), "SBML", LocalConfig.getInstance().getLoadedDatabase());	
						updater.updateReactionEquations(id, oldValue, newValue, LocalConfig.getInstance().getLoadedDatabase());
					}
				} else {
					// lower bound >= 0, set new equation
					reactionsTable.getModel().setValueAt(newValue, rowIndex, GraphicalInterfaceConstants.REACTION_EQUN_ABBR_COLUMN);
					updateReactionsDatabaseRow(rowIndex, Integer.parseInt((String) (reactionsTable.getModel().getValueAt(rowIndex, 0))), "SBML", LocalConfig.getInstance().getLoadedDatabase());
					updater.updateReactionEquations(id, oldValue, newValue, LocalConfig.getInstance().getLoadedDatabase());
				}					
			} 
			// if "No" button clicked   
			if (LocalConfig.getInstance().noButtonClicked == true) {
				reactionsTable.getModel().setValueAt(updater.reactionEqunAbbr, rowIndex, GraphicalInterfaceConstants.REACTION_EQUN_ABBR_COLUMN);
				updateReactionsDatabaseRow(rowIndex, Integer.parseInt((String) (reactionsTable.getModel().getValueAt(rowIndex, 0))), "SBML", LocalConfig.getInstance().getLoadedDatabase());
			}
			LocalConfig.getInstance().noButtonClicked = false;

			closeConnection();
			reloadTables(LocalConfig.getInstance().getLoadedDatabase());
			//TODO: does this do anything?
			if (highlightParticipatingRxns) {
				scrollFirstParticipatingRxnToView();
			}
			if (LocalConfig.getInstance().getSuspiciousMetabolites().size() > 0) {
				setLoadErrorMessage("Model contains suspicious metabolites.");
				statusBar.setText("Row 1" + "                   " + getLoadErrorMessage());
			}
		} else if (colIndex == GraphicalInterfaceConstants.KO_COLUMN) {
			if (validator.validTrueEntry(newValue)) {
				reactionsTable.getModel().setValueAt(GraphicalInterfaceConstants.BOOLEAN_VALUES[1], rowIndex, GraphicalInterfaceConstants.KO_COLUMN);
			} else if (validator.validFalseEntry(newValue)) {
				reactionsTable.getModel().setValueAt(GraphicalInterfaceConstants.BOOLEAN_VALUES[0], rowIndex, GraphicalInterfaceConstants.KO_COLUMN);
			} else if (newValue != null) {				
				if (!replaceAllMode) {
					setFindReplaceAlwaysOnTop(false);
					JOptionPane.showMessageDialog(null,                
							GraphicalInterfaceConstants.BOOLEAN_VALUE_ERROR_MESSAGE,                
							GraphicalInterfaceConstants.BOOLEAN_VALUE_ERROR_TITLE,                               
							JOptionPane.ERROR_MESSAGE);
					setFindReplaceAlwaysOnTop(true);
				}				
				reactionUpdateValid = false;
				reactionsTable.getModel().setValueAt(oldValue, rowIndex, GraphicalInterfaceConstants.KO_COLUMN);
			}
			updateReactionsDatabaseRow(rowIndex, Integer.parseInt((String) (reactionsTable.getModel().getValueAt(rowIndex, 0))), "SBML", LocalConfig.getInstance().getLoadedDatabase());					
		} else if (colIndex == GraphicalInterfaceConstants.REVERSIBLE_COLUMN) {
			if (!replaceAllMode) {
				setFindReplaceAlwaysOnTop(false);
				JOptionPane.showMessageDialog(null, 
						GraphicalInterfaceConstants.REVERSIBLE_ERROR_MESSAGE,                
						GraphicalInterfaceConstants.REVERSIBLE_ERROR_TITLE, 					                               
						JOptionPane.ERROR_MESSAGE);
				setFindReplaceAlwaysOnTop(true);
			}			
			reactionUpdateValid = false;
			reactionsTable.getModel().setValueAt(oldValue, rowIndex, GraphicalInterfaceConstants.REVERSIBLE_COLUMN);
		} else if (colIndex == GraphicalInterfaceConstants.FLUX_VALUE_COLUMN || 
				colIndex == GraphicalInterfaceConstants.LOWER_BOUND_COLUMN || 
				colIndex == GraphicalInterfaceConstants.UPPER_BOUND_COLUMN || 
				colIndex == GraphicalInterfaceConstants.BIOLOGICAL_OBJECTIVE_COLUMN || 
				colIndex == GraphicalInterfaceConstants.SYNTHETIC_OBJECTIVE_COLUMN) {
			if (!validator.isNumber(newValue)) {
				if (!replaceAllMode) {
					setFindReplaceAlwaysOnTop(false);
					JOptionPane.showMessageDialog(null,                
							GraphicalInterfaceConstants.NUMERIC_VALUE_ERROR_TITLE,                
							GraphicalInterfaceConstants.NUMERIC_VALUE_ERROR_MESSAGE,                               
							JOptionPane.ERROR_MESSAGE);
					setFindReplaceAlwaysOnTop(true);
				}	
				reactionsTable.getModel().setValueAt(oldValue, rowIndex, colIndex);
				updateReactionsDatabaseRow(rowIndex, Integer.parseInt((String) (reactionsTable.getModel().getValueAt(rowIndex, 0))), "SBML", LocalConfig.getInstance().getLoadedDatabase());					
			} else {
				if (colIndex == GraphicalInterfaceConstants.LOWER_BOUND_COLUMN) { 
					Double lowerBound = Double.valueOf(newValue);
					Double upperBound = Double.valueOf((String) (reactionsTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.UPPER_BOUND_COLUMN)));
					String reversible = reactionsTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.REVERSIBLE_COLUMN).toString();
					if (!validator.lowerBoundReversibleValid(lowerBound, upperBound, reversible)) {
						setFindReplaceAlwaysOnTop(false);
						Object[] options = {"    Yes    ", "    No    ",};
						int choice = JOptionPane.showOptionDialog(null, 
								GraphicalInterfaceConstants.LOWER_BOUND_ERROR_MESSAGE, 
								GraphicalInterfaceConstants.LOWER_BOUND_ERROR_TITLE, 
								JOptionPane.YES_NO_OPTION, 
								JOptionPane.QUESTION_MESSAGE, 
								null, options, options[0]);
						if (choice == JOptionPane.YES_OPTION) {
							reactionsTable.getModel().setValueAt("0.0", rowIndex, GraphicalInterfaceConstants.LOWER_BOUND_COLUMN);
						}
						if (choice == JOptionPane.NO_OPTION) {
							reactionUpdateValid = false;
							reactionsTable.getModel().setValueAt(oldValue, rowIndex, GraphicalInterfaceConstants.LOWER_BOUND_COLUMN);
						}
						setFindReplaceAlwaysOnTop(true);
					} else if (lowerBound > upperBound) {
						setFindReplaceAlwaysOnTop(false);
						Object[] options = {"    Yes    ", "    No    ",};
						int choice = JOptionPane.showOptionDialog(null, 
								GraphicalInterfaceConstants.LOWER_BOUND_ERROR_MESSAGE2, 
								GraphicalInterfaceConstants.LOWER_BOUND_ERROR_TITLE, 
								JOptionPane.YES_NO_OPTION, 
								JOptionPane.QUESTION_MESSAGE, 
								null, options, options[0]);
						if (choice == JOptionPane.YES_OPTION) {
							reactionsTable.getModel().setValueAt("0.0", rowIndex, GraphicalInterfaceConstants.LOWER_BOUND_COLUMN);
						}
						if (choice == JOptionPane.NO_OPTION) {
							reactionUpdateValid = false;
							reactionsTable.getModel().setValueAt(oldValue, rowIndex, GraphicalInterfaceConstants.LOWER_BOUND_COLUMN);
						}
						setFindReplaceAlwaysOnTop(true);
					} else {
						reactionsTable.getModel().setValueAt(newValue, rowIndex, GraphicalInterfaceConstants.LOWER_BOUND_COLUMN);
					}
				}
				if (colIndex == GraphicalInterfaceConstants.UPPER_BOUND_COLUMN) { 
					Double lowerBound = Double.valueOf((String) (reactionsTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.LOWER_BOUND_COLUMN)));
					Double upperBound = Double.valueOf(newValue);
					if (upperBound < lowerBound) {
						setFindReplaceAlwaysOnTop(false);
						Object[] options = {"    Yes    ", "    No    ",};
						int choice = JOptionPane.showOptionDialog(null, 
								GraphicalInterfaceConstants.UPPER_BOUND_ERROR_MESSAGE, 
								GraphicalInterfaceConstants.UPPER_BOUND_ERROR_TITLE, 
								JOptionPane.YES_NO_OPTION, 
								JOptionPane.QUESTION_MESSAGE, 
								null, options, options[0]);
						if (choice == JOptionPane.YES_OPTION) {
							reactionsTable.getModel().setValueAt(GraphicalInterfaceConstants.UPPER_BOUND_DEFAULT_STRING, rowIndex, GraphicalInterfaceConstants.UPPER_BOUND_COLUMN);
						}
						if (choice == JOptionPane.NO_OPTION) {
							reactionUpdateValid = false;
							reactionsTable.getModel().setValueAt(oldValue, rowIndex, GraphicalInterfaceConstants.UPPER_BOUND_COLUMN);
						}	
						setFindReplaceAlwaysOnTop(true);
					} else {
						reactionsTable.getModel().setValueAt(newValue, rowIndex, GraphicalInterfaceConstants.UPPER_BOUND_COLUMN);
					}
				} 
				if (colIndex == GraphicalInterfaceConstants.FLUX_VALUE_COLUMN || 
						colIndex == GraphicalInterfaceConstants.BIOLOGICAL_OBJECTIVE_COLUMN ||
						colIndex == GraphicalInterfaceConstants.SYNTHETIC_OBJECTIVE_COLUMN) {
					reactionsTable.getModel().setValueAt(newValue, rowIndex, colIndex);
				}				
				updateReactionsDatabaseRow(rowIndex, Integer.parseInt((String) (reactionsTable.getModel().getValueAt(rowIndex, 0))), "SBML", LocalConfig.getInstance().getLoadedDatabase());
			}
		} else {
			// action for remaining columns
			reactionsTable.getModel().setValueAt(newValue, rowIndex, colIndex);
			updateReactionsDatabaseRow(rowIndex, Integer.parseInt((String) (reactionsTable.getModel().getValueAt(rowIndex, 0))), "SBML", LocalConfig.getInstance().getLoadedDatabase());					
		}
		LocalConfig.getInstance().editMode = false;
	}
	
	// updates metabolites table with new value is valid, else reverts to old value
	public void updateMetabolitesCellIfValid(String oldValue, String newValue, int rowIndex, int colIndex) {
		metaboliteUpdateValid = true;
		EntryValidator validator = new EntryValidator();
		int id = Integer.parseInt((String) (metabolitesTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.DB_METABOLITE_ID_COLUMN)));
		if (colIndex == GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN) { 
			// entry is duplicate
			if (LocalConfig.getInstance().getMetaboliteIdNameMap().containsKey(newValue)) {			
				setFindReplaceAlwaysOnTop(false);
				Object[] options = {"    Yes    ", "    No    ",};
				int choice = JOptionPane.showOptionDialog(null, 
						GraphicalInterfaceConstants.DUPLICATE_METABOLITE_MESSAGE, 
						GraphicalInterfaceConstants.DUPLICATE_METABOLITE_TITLE, 
						JOptionPane.YES_NO_OPTION, 
						JOptionPane.QUESTION_MESSAGE, 
						null, options, options[0]);
				if (choice == JOptionPane.YES_OPTION) {	
					metabolitesTable.getModel().setValueAt(newValue, rowIndex, GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN);
					if (!LocalConfig.getInstance().getDuplicateIds().contains(id)) {
						LocalConfig.getInstance().getDuplicateIds().add(id);
					}
					LocalConfig.getInstance().getMetaboliteIdNameMap().remove(oldValue);
				}
				if (choice == JOptionPane.NO_OPTION) {
					metaboliteUpdateValid = false;
					metabolitesTable.getModel().setValueAt(oldValue, rowIndex, GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN);
				}
				setFindReplaceAlwaysOnTop(true);
			// duplicate entry changed
			} else if (LocalConfig.getInstance().getDuplicateIds().contains(id)) {
				if (oldValue != newValue) {
					metabolitesTable.getModel().setValueAt(newValue, rowIndex, GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN);
					int index = LocalConfig.getInstance().getDuplicateIds().indexOf(id);
					LocalConfig.getInstance().getDuplicateIds().remove(index);
				}
				metabolitesTable.getModel().setValueAt(newValue, rowIndex, GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN);
			} else {
				//if a blank is entered remove key/value of old value
				if (newValue == null || newValue.length() == 0 || newValue.trim().equals("")) {
					LocalConfig.getInstance().getMetaboliteIdNameMap().remove(oldValue);
				// non-duplicate entry
				} else {
					if (newValue.trim() != null && newValue.trim().length() > 0) {
						LocalConfig.getInstance().getMetaboliteIdNameMap().remove(oldValue);
						LocalConfig.getInstance().getMetaboliteIdNameMap().put(newValue, Integer.parseInt((String) (metabolitesTable.getModel().getValueAt(rowIndex, 0))));
					}					
				}
				metabolitesTable.getModel().setValueAt(newValue, rowIndex, GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN);
			}
		// if not a number error message displayed
		} else if (colIndex == GraphicalInterfaceConstants.CHARGE_COLUMN) {
			if (!validator.isNumber(newValue)) {
				if (!replaceAllMode) {
					setFindReplaceAlwaysOnTop(false);
					JOptionPane.showMessageDialog(null,                
							GraphicalInterfaceConstants.NUMERIC_VALUE_ERROR_TITLE,                
							GraphicalInterfaceConstants.NUMERIC_VALUE_ERROR_MESSAGE,                               
							JOptionPane.ERROR_MESSAGE);
					setFindReplaceAlwaysOnTop(true);
				}				
				metaboliteUpdateValid = false;
				metabolitesTable.getModel().setValueAt(oldValue, rowIndex, colIndex);
			} else {
				metabolitesTable.getModel().setValueAt(newValue, rowIndex, colIndex);
			}
		} else if (colIndex == GraphicalInterfaceConstants.BOUNDARY_COLUMN) {
			if (validator.validTrueEntry(newValue)) {
				metabolitesTable.getModel().setValueAt(GraphicalInterfaceConstants.BOOLEAN_VALUES[1], rowIndex, GraphicalInterfaceConstants.BOUNDARY_COLUMN);
			} else if (validator.validFalseEntry(newValue)) {
				metabolitesTable.getModel().setValueAt(GraphicalInterfaceConstants.BOOLEAN_VALUES[0], rowIndex, GraphicalInterfaceConstants.BOUNDARY_COLUMN);
			} else if (newValue != null) {				
				if (!replaceAllMode) {
					setFindReplaceAlwaysOnTop(false);
					JOptionPane.showMessageDialog(null,                
							GraphicalInterfaceConstants.BOOLEAN_VALUE_ERROR_MESSAGE,                
							GraphicalInterfaceConstants.BOOLEAN_VALUE_ERROR_TITLE,                                
							JOptionPane.ERROR_MESSAGE);
					setFindReplaceAlwaysOnTop(true);
				}	
				metaboliteUpdateValid = false;
				metabolitesTable.getModel().setValueAt(oldValue, rowIndex, GraphicalInterfaceConstants.BOUNDARY_COLUMN);
			}
		} else {
			// action for remaining columns
			metabolitesTable.getModel().setValueAt(newValue, rowIndex, colIndex);
		}
		updateMetabolitesDatabaseRow(rowIndex, Integer.parseInt((String) (metabolitesTable.getModel().getValueAt(rowIndex, 0))), "SBML", LocalConfig.getInstance().getLoadedDatabase()); 	
	}	
	
	/*****************************************************************************/
	//end table actions
	/*****************************************************************************/

	/******************************************************************************/
	//reload tables methods
	/******************************************************************************/
	
	public void setUpMetabolitesTable(Connection con) {
		try {
			// enables or disables menu items depending on if there are unused items present
			createUnusedMetabolitesList();
			if (LocalConfig.getInstance().getUnusedList().size() > 0) {
				highlightUnusedMetabolitesItem.setEnabled(true);
				deleteUnusedItem.setEnabled(true);
			} else {
				highlightUnusedMetabolitesItem.setEnabled(false);
				deleteUnusedItem.setEnabled(false);
			}
			ArrayList<Integer> visibleMetabolitesColumns = visibleMetabolitesColumnList();
			setVisibleMetabolitesColumns(visibleMetabolitesColumns);
			if (LocalConfig.getInstance().getSuspiciousMetabolites().size() > 0) {
				setLoadErrorMessage("Model contains suspicious metabolites.");
				// selected row default at row 1 (index 0)
				statusBar.setText("Row 1" + "                   " + getLoadErrorMessage());
				findSuspiciousItem.setEnabled(true);
			} else {
				statusBar.setText("Row 1");
				findSuspiciousItem.setEnabled(false);
			}
		} catch (Throwable t) {
			
		}		
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
			statusBar.setText("Row 1");
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
			ArrayList<Integer> visibleReactionsColumns = visibleReactionsColumnList();
			setVisibleReactionsColumns(visibleReactionsColumns);
			// selected row default at row 1 (index 0)
			try {
				if (LocalConfig.getInstance().getSuspiciousMetabolites().size() > 0) {
					setLoadErrorMessage("Model contains suspicious metabolites.");
					// selected row default at row 1 (index 0)
					statusBar.setText("Row 1" + "                   " + getLoadErrorMessage());
				} else {
					statusBar.setText("Row 1");
				}
			} catch (Throwable t) {
				
			}			
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
		closeConnection();
		reloadTables(getDatabaseName());
		DatabaseCreator databaseCreator = new DatabaseCreator();
		databaseCreator.copyTables(LocalConfig.getInstance().getDatabaseName(), tableCopySuffix(0));
		listModel.addElement(titleName);
		//fileList.setModel(listModel);
		//fileList.setSelectedIndex(0);
		setSortDefault();
		//set focus to top left
		setTableCellFocused(0, 1, reactionsTable);
		setTableCellFocused(0, 1, metabolitesTable);
		if (LocalConfig.getInstance().getSuspiciousMetabolites().size() > 0) {
			setLoadErrorMessage("Model contains suspicious metabolites.");
			// selected row default at row 1 (index 0)
			statusBar.setText("Row 1" + "                   " + getLoadErrorMessage());
			findSuspiciousItem.setEnabled(true);
		} else {
			statusBar.setText("Row 1");
			findSuspiciousItem.setEnabled(false);
		}
	}

	//sets parameters to initial values on load
	public void loadSetUp() {
		if (getFindReplaceDialog() != null) {
			getFindReplaceDialog().dispose();
		}
		clearOutputPane();
		if (getPopout() != null) {
			popout.dispose();
		}
		setBooleanDefaults();
		clearConfigLists();
		undoSplitButton.setToolTipText("Undo");
		redoSplitButton.setToolTipText("Redo");
		disableOptionComponent(undoSplitButton, undoLabel, undoGrayedLabel);
		disableOptionComponent(redoSplitButton, undoLabel, undoGrayedLabel);
		undoCount = 1;
		redoCount = 1;
		LocalConfig.getInstance().setNumReactionTablesCopied(0);
		LocalConfig.getInstance().setNumMetabolitesTableCopied(0);
		showPrompt = true;
		LocalConfig.getInstance().pastedReaction = false;
		LocalConfig.getInstance().hasMetabolitesFile = false;
		highlightUnusedMetabolites = false;
		highlightUnusedMetabolitesItem.setState(false);
		setReactionsSortColumnIndex(0);
		setMetabolitesSortColumnIndex(0);
		LocalConfig.getInstance().setReactionsLocationsListCount(0);
		LocalConfig.getInstance().setMetabolitesLocationsListCount(0);		
		// default selection mode cells only
		setUpCellSelectionMode();	
	}

	public void setBooleanDefaults() {
		// selection values	
		includeRxnColumnNames = true;	
		includeMtbColumnNames = true;	
		// load values
		isCSVFile = false;
		validFile = true;
		// highlighting
		highlightParticipatingRxns = false;
		// listener values
		selectedCellChanged = false;
		formulaBarFocusGained = false;
		tabChanged = false;
		// find-replace values
		findMode = false;
		findButtonReactionsClicked = false;
		findButtonMetabolitesClicked = false;
		matchCase = false;
		wrapAround = false;
		searchSelectedArea = false;
		searchBackwards = false;
		reactionUpdateValid = true;
		metaboliteUpdateValid = true;
		replaceAllMode = false;
		reactionsFindAll = false;
		metabolitesFindAll = false;
		changeReactionFindSelection = true;
		changeMetaboliteFindSelection = true;
		// paste
		validPaste = true;
		pasting = false;
		showDuplicatePrompt = true;
		duplicateMetabOK = true;
		LocalConfig.getInstance().includesReactions = true;
		LocalConfig.getInstance().pastedReaction = false;
		// other
		showErrorMessage = true;
		saveOptFile = false;
		addReacColumn = false;
		addMetabColumn = false;
		duplicatePromptShown = false;
		reactionsTableEditable = true;
		renameMetabolite = false;			
		LocalConfig.getInstance().noButtonClicked = false;
		LocalConfig.getInstance().yesToAllButtonClicked = false;
		LocalConfig.getInstance().addReactantPromptShown = false;
		LocalConfig.getInstance().reactionsTableChanged = false;
		LocalConfig.getInstance().metabolitesTableChanged = false;
		exit = true;
		saveDatabaseFile = false;
		modelCollectionOKButtonClicked = false;
		LocalConfig.getInstance().reactionEditorVisible = false;
		LocalConfig.getInstance().loadExistingVisible = false;
		LocalConfig.getInstance().addColumnInterfaceVisible = false;
		reactionCancelLoad = false;
		isRoot = true;
	}
	
	public void clearConfigLists() {
		LocalConfig.getInstance().getInvalidReactions().clear();
		LocalConfig.getInstance().getBlankMetabIds().clear();
		LocalConfig.getInstance().getDuplicateIds().clear();
		LocalConfig.getInstance().getMetaboliteIdNameMap().clear();
		LocalConfig.getInstance().getMetaboliteUsedMap().clear();
		LocalConfig.getInstance().getSuspiciousMetabolites().clear();
		LocalConfig.getInstance().getUnusedList().clear();
		LocalConfig.getInstance().getOptimizationFilesList().clear();
		LocalConfig.getInstance().getHiddenReactionsColumns().clear();
		LocalConfig.getInstance().getHiddenMetabolitesColumns().clear();
        LocalConfig.getInstance().getAddedMetabolites().clear();
		
		LocalConfig.getInstance().getUndoItemMap().clear();
		LocalConfig.getInstance().getUndoItemMap().clear();
		
		// clear and reinitialize sort lists
		LocalConfig.getInstance().getReactionsSortColumns().clear();
		LocalConfig.getInstance().getReactionsSortColumns().add(0);
		LocalConfig.getInstance().getReactionsSortOrderList().clear();
		LocalConfig.getInstance().getReactionsSortOrderList().add(SortOrder.ASCENDING);
		LocalConfig.getInstance().getMetabolitesSortColumns().clear();
		LocalConfig.getInstance().getMetabolitesSortColumns().add(0);
		LocalConfig.getInstance().getMetabolitesSortOrderList().clear();
		LocalConfig.getInstance().getMetabolitesSortOrderList().add(SortOrder.ASCENDING);
	}
	
	public String createConnectionStatement(String databaseName) {
		return "jdbc:sqlite:" + databaseName + ".db";
	}
	
	public void closeConnection() {
		if (LocalConfig.getInstance().getCurrentConnection() != null) {
        	try {
				LocalConfig.getInstance().getCurrentConnection().close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        }
	}
	
	public void reloadTables(String databaseName) {
		try {
			Class.forName("org.sqlite.JDBC");
			Connection con = DriverManager.getConnection(createConnectionStatement(databaseName));
			LocalConfig.getInstance().setCurrentConnection(con);			
			setUpMetabolitesTable(con);
			setUpReactionsTable(con);
			if (databaseName.contains("\\")) {
				databaseName = databaseName.substring(databaseName.lastIndexOf("\\") + 1);
			}
			setTitle(GraphicalInterfaceConstants.TITLE + " - " + databaseName);
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
	}
	
	/******************************************************************************/
	//end reload tables methods
	/******************************************************************************/
	
	/******************************************************************************/
	//update database methods, called when table rows changed 
	/******************************************************************************/

	public void updateReactionsDatabaseRow(int rowIndex, Integer reactionId, String sourceType, String databaseName)  {

		try { 
			ReactionFactory aFactory = new ReactionFactory("SBML", LocalConfig.getInstance().getLoadedDatabase());

			SBMLReaction aReaction = (SBMLReaction)aFactory.getReactionById(reactionId); 
			aReaction.setKnockout((String) reactionsTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.KO_COLUMN));			
			aReaction.setFluxValue(Double.valueOf((String) reactionsTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.FLUX_VALUE_COLUMN)));
			aReaction.setReactionAbbreviation((String) reactionsTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.REACTION_ABBREVIATION_COLUMN));
			aReaction.setReactionName((String) reactionsTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.REACTION_NAME_COLUMN));
			aReaction.setReactionEqunAbbr((String) reactionsTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.REACTION_EQUN_ABBR_COLUMN));		    
			aReaction.setReactionEqunNames((String) reactionsTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.REACTION_EQUN_NAMES_COLUMN));		    
			
			if (aReaction.getReactionEqunAbbr() != null) {
				if (aReaction.getReactionEqunAbbr().contains("<") || (aReaction.getReactionEqunAbbr().contains("=") && !aReaction.getReactionEqunAbbr().contains(">"))) {
					aReaction.setReversible("true");
				} else if (aReaction.getReactionEqunAbbr().contains("-->") || aReaction.getReactionEqunAbbr().contains("->") || aReaction.getReactionEqunAbbr().contains("=>")) {
					aReaction.setReversible("false");		    		
				}				
			} 
			
			//string cannot be cast to double but valueOf works, from http://www.java-examples.com/convert-java-string-double-example		    
			aReaction.setLowerBound(Double.valueOf((String) reactionsTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.LOWER_BOUND_COLUMN)));
			aReaction.setUpperBound(Double.valueOf((String) reactionsTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.UPPER_BOUND_COLUMN)));
			aReaction.setBiologicalObjective(Double.valueOf((String) reactionsTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.BIOLOGICAL_OBJECTIVE_COLUMN)));
			aReaction.setSyntheticObjective(Double.valueOf((String) reactionsTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.SYNTHETIC_OBJECTIVE_COLUMN)));
			aReaction.setGeneAssociations((String) reactionsTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.GENE_ASSOCIATIONS_COLUMN));		    
			
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
			// prevents invisible id column from setting id in formulaBar for find events
			if (reactionsTable.getSelectedRow() > 0) {
				int viewRow = reactionsTable.convertRowIndexToModel(reactionsTable.getSelectedRow());
    			formulaBar.setText((String) reactionsTable.getModel().getValueAt(viewRow, reactionsTable.getSelectedColumn()));
			}
						
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();			
		}
	}

	public void updateMetabolitesDatabaseRow(int rowIndex, Integer metaboliteId, String sourceType, String databaseName)  {
		try {
			MetaboliteFactory aFactory = new MetaboliteFactory("SBML", LocalConfig.getInstance().getLoadedDatabase());
			SBMLMetabolite aMetabolite = (SBMLMetabolite)aFactory.getMetaboliteById(Integer.parseInt((String) metabolitesTable.getModel().getValueAt(rowIndex, 0))); 
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

			aMetabolite.update();
			// prevents invisible id column from setting id in formulaBar for find events
			if (metabolitesTable.getSelectedRow() > 0) {
				int viewRow = metabolitesTable.convertRowIndexToView(metabolitesTable.getSelectedRow());
				formulaBar.setText((String) metabolitesTable.getModel().getValueAt(viewRow, metabolitesTable.getSelectedColumn()));
			}
			
			// update id name map
			if (!LocalConfig.getInstance().getMetaboliteIdNameMap().containsKey((String) metabolitesTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN))) {
				if (metabolitesTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN) != null 
						&& ((String) metabolitesTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN)).trim().length() > 0) {
					LocalConfig.getInstance().getMetaboliteIdNameMap().put((String) metabolitesTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN), new Integer(metaboliteId));
				}				
			}
			// enables or disables unused metabolites menu items depending on if there are unused items present
			createUnusedMetabolitesList();			
			if (LocalConfig.getInstance().getUnusedList().size() > 0) {
				highlightUnusedMetabolitesItem.setEnabled(true);
				deleteUnusedItem.setEnabled(true);
			} else {
				highlightUnusedMetabolitesItem.setEnabled(false);
				deleteUnusedItem.setEnabled(false);
			}
			
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();			
		}    
	}

	/******************************************************************************/
	//end update database methods
	/******************************************************************************/
		
	/*******************************************************************************/
	//table layouts
	/*******************************************************************************/
	
	public void positionColumn(JXTable table,int col_Index) {
		table.moveColumn(table.getColumnCount()-1, col_Index);
	}
	
	//from http://www.roseindia.net/java/example/java/swing/ChangeColumnName.shtml  
	public void ChangeName(JXTable table, int col_index, String col_name){
		table.getColumnModel().getColumn(col_index).setHeaderValue(col_name);
	}
	
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
    		if (LocalConfig.getInstance().findReplaceFocusLost) {
				findButtonReactionsClicked = false;
				throwNotFoundError = false;
				if (getFindReplaceDialog() != null && !LocalConfig.getInstance().addReactantPromptShown) {
					getFindReplaceDialog().replaceButton.setEnabled(false);
					getFindReplaceDialog().replaceAllButton.setEnabled(false);
					getFindReplaceDialog().replaceFindButton.setEnabled(false);
				}
			}
    		String reactionRow = Integer.toString((reactionsTable.getSelectedRow() + 1));
    		if (LocalConfig.getInstance().getSuspiciousMetabolites().size() > 0) {
				setLoadErrorMessage("Model contains suspicious metabolites.");
				statusBar.setText("Row " + reactionRow + "                   " + getLoadErrorMessage());
			} else {
				statusBar.setText("Row " + reactionRow);
			}
    		if (reactionsTable.getRowCount() > 0 && reactionsTable.getSelectedRow() > -1 && tabbedPane.getSelectedIndex() == 0) {
    			enableOrDisableReactionsItems();
    			// if any cell selected any existing find all highlighting is unhighlighted
    			reactionsFindAll = false;
    			metabolitesFindAll = false;	
    			reactionsTable.repaint();
    			metabolitesTable.repaint();  
    			selectedCellChanged = true;
    			changeReactionFindSelection = true;
    			int viewRow = reactionsTable.convertRowIndexToModel(reactionsTable.getSelectedRow());
    			// prevents invisible id column from setting id in formulaBar for find events
    			if (reactionsTable.getSelectedColumn() != 0) {
    				try {
        				formulaBar.setText((String) reactionsTable.getModel().getValueAt(viewRow, reactionsTable.getSelectedColumn()));
        			} catch (Throwable t) {
        				
        			} 
    			}    			  						
    		} else {
    			formulaBar.setText("");
    		}
    		if (event.getValueIsAdjusting()) {
    			return;
    		}
    	}
    }

    private class ReactionsColumnListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent event) {
			if (LocalConfig.getInstance().findReplaceFocusLost) {
				findButtonReactionsClicked = false;
				throwNotFoundError = false;
				if (getFindReplaceDialog() != null && !LocalConfig.getInstance().addReactantPromptShown) {
					getFindReplaceDialog().replaceButton.setEnabled(false);
					getFindReplaceDialog().replaceAllButton.setEnabled(false);
					getFindReplaceDialog().replaceFindButton.setEnabled(false);
				}				
			}
			if (reactionsTable.getSelectedRow() > -1 && reactionsTable.getSelectedColumn() > -1 && tabbedPane.getSelectedIndex() == 0) {
				String reactionRow = Integer.toString((reactionsTable.getSelectedRow() + 1));
				if (LocalConfig.getInstance().getSuspiciousMetabolites().size() > 0) {
					setLoadErrorMessage("Model contains suspicious metabolites.");
					statusBar.setText("Row " + reactionRow + "                   " + getLoadErrorMessage());
				} else {
					statusBar.setText("Row " + reactionRow);
				}
				enableOrDisableReactionsItems();
				// if any cell selected any existing find all highlighting is unhighlighted
				reactionsFindAll = false;
				metabolitesFindAll = false;	
				reactionsTable.repaint();
				metabolitesTable.repaint();
				selectedCellChanged = true;	
				changeReactionFindSelection = true;
				int viewRow = reactionsTable.convertRowIndexToModel(reactionsTable.getSelectedRow());
				// prevents invisible id column from setting id in formulaBar for find events
				if (reactionsTable.getSelectedColumn() != 0) {
					formulaBar.setText((String) reactionsTable.getModel().getValueAt(viewRow, reactionsTable.getSelectedColumn()));				
				}				
			} else {
    			formulaBar.setText("");
    		} 
			if (event.getValueIsAdjusting()) {
				return;
			}
		}
	}
	
    // disables items if cell is non-editable
    public void enableOrDisableReactionsItems() {
    	if (reactionsTable.getSelectedColumn() == GraphicalInterfaceConstants.REVERSIBLE_COLUMN || reactionsTable.getSelectedColumn() == GraphicalInterfaceConstants.REACTION_EQUN_NAMES_COLUMN) {
			formulaBar.setEditable(false);
			formulaBar.setBackground(Color.WHITE);
			formulaBarPasteItem.setEnabled(false);
			pastebutton.setEnabled(false);
		}  else {
			if (isRoot) {
				formulaBar.setEditable(true);
				formulaBarPasteItem.setEnabled(true);
				pastebutton.setEnabled(true);
			}					
		}
    }
	
    // saves sort column and order so it is preserved when table is refreshed
	// after editing and updating database
    class ReactionsColumnHeaderListener extends MouseAdapter {
		  public void mouseClicked(MouseEvent evt) {
			  int r = reactionsTable.getSortedColumnIndex();
			  // bug: mouse listener sets multiple undo items for one event at times
			  // this fix works
			  if (r != getReactionsSortColumnIndex() || reactionsTable.getSortOrder(reactionsTable.getSortedColumnIndex()) != getReactionsSortOrder()) {
				  ReactionUndoItem undoItem = createReactionUndoItem("", "", reactionsTable.getSelectedRow(), reactionsTable.getSelectedColumn(), 1, UndoConstants.SORT, UndoConstants.REACTION_UNDO_ITEM_TYPE);
				  LocalConfig.getInstance().getReactionsSortColumns().add(r);
				  LocalConfig.getInstance().getReactionsSortOrderList().add(reactionsTable.getSortOrder(reactionsTable.getSortedColumnIndex()));
				  undoItem.setOldSortColumnIndex(LocalConfig.getInstance().getReactionsSortColumns().get(LocalConfig.getInstance().getReactionsSortColumns().size() - 2));
				  undoItem.setNewSortColumnIndex(LocalConfig.getInstance().getReactionsSortColumns().get(LocalConfig.getInstance().getReactionsSortColumns().size() - 1));
				  undoItem.setOldSortOrder(LocalConfig.getInstance().getReactionsSortOrderList().get(LocalConfig.getInstance().getReactionsSortOrderList().size() - 2));
				  undoItem.setNewSortOrder(LocalConfig.getInstance().getReactionsSortOrderList().get(LocalConfig.getInstance().getReactionsSortOrderList().size() - 1));
				  setUpReactionsUndo(undoItem);
			  }
			  setReactionsSortColumnIndex(r);
			  setReactionsSortOrder(reactionsTable.getSortOrder(reactionsTable.getSortedColumnIndex()));
		  }
	}	  
	
    HighlightPredicate reactionsSelectedAreaPredicate = new HighlightPredicate() {
		public boolean isHighlighted(Component renderer ,ComponentAdapter adapter) {
			if (findMode) {
				if (searchSelectedArea) {
					if (findButtonReactionsClicked) {
						if (adapter.row >= selectedReactionsRowStartIndex && adapter.row < selectedReactionsRowEndIndex && adapter.column >= selectedReactionsColumnStartIndex && adapter.column < selectedReactionsColumnEndIndex) {
							return true;
						}
					}					
				}
			}
																				
			return false;
		}
	};
	
	ColorHighlighter reactionsSelectedArea = new ColorHighlighter(reactionsSelectedAreaPredicate, GraphicalInterfaceConstants.SELECTED_AREA_COLOR, null);
	
	HighlightPredicate reactionFindAllPredicate = new HighlightPredicate() {
		public boolean isHighlighted(Component renderer ,ComponentAdapter adapter) {
			if (findMode) {
				if (reactionsFindAll) {
					for (int i = 0; i < getReactionsFindLocationsList().size(); i++) {
						if (adapter.row == getReactionsFindLocationsList().get(i).get(0) && adapter.column == getReactionsFindLocationsList().get(i).get(1)) {
							return true;
						}
					}
				} else {
					if (getReactionsReplaceLocation().size() > 0 && !changeReactionFindSelection) {
						if (adapter.row == getReactionsReplaceLocation().get(0) && adapter.column == getReactionsReplaceLocation().get(1)) {
							return true;
						}
					}				
				}
			}
																				
			return false;
		}
	};
	
	ColorHighlighter reactionFindAll = new ColorHighlighter(reactionFindAllPredicate, GraphicalInterfaceConstants.FIND_ALL_COLOR, null);
	
	HighlightPredicate participatingPredicate = new HighlightPredicate() {
		public boolean isHighlighted(Component renderer ,ComponentAdapter adapter) {
			int viewRow = reactionsTable.convertRowIndexToModel(adapter.row);
			int id = Integer.valueOf(reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.DB_REACTIONS_ID_COLUMN).toString());					
			if (highlightParticipatingRxns == true && LocalConfig.getInstance().getParticipatingReactions().contains(id)) {									
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
		
		// Comparator allows numerical columns to be sorted by numeric value and
		// not like strings
		reactionsTable.getColumnExt("id").setComparator(numberComparator);
		reactionsTable.getColumnExt("flux_value").setComparator(numberComparator);
		reactionsTable.getColumnExt("lower_bound").setComparator(numberComparator);
		reactionsTable.getColumnExt("upper_bound").setComparator(numberComparator);
		reactionsTable.getColumnExt("biological_objective").setComparator(numberComparator);
		reactionsTable.getColumnExt("synthetic_objective").setComparator(numberComparator);
		
		reactionsTable.addHighlighter(participating);
		reactionsTable.addHighlighter(invalidReaction);
		reactionsTable.addHighlighter(reactionsSelectedArea);
		reactionsTable.addHighlighter(reactionFindAll);		
		
		// these columns have names that are too long to fit in cell and need tooltips
		// also KO has Knockout as tooltip
		ColumnHeaderToolTips tips = new ColumnHeaderToolTips();
		for (int c = 0; c < reactionsTable.getColumnCount(); c++) {
			TableColumn col = reactionsTable.getColumnModel().getColumn(c);
			if (c == GraphicalInterfaceConstants.BIOLOGICAL_OBJECTIVE_COLUMN) {
				tips.setToolTip(col, GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES[GraphicalInterfaceConstants.BIOLOGICAL_OBJECTIVE_COLUMN]);
			}
			if (c == GraphicalInterfaceConstants.SYNTHETIC_OBJECTIVE_COLUMN) {
				tips.setToolTip(col, GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES[GraphicalInterfaceConstants.SYNTHETIC_OBJECTIVE_COLUMN]);
			}
			if (c == GraphicalInterfaceConstants.REVERSIBLE_COLUMN) {
				tips.setToolTip(col, GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES[GraphicalInterfaceConstants.REVERSIBLE_COLUMN]);
			}
			if (c == GraphicalInterfaceConstants.KO_COLUMN) {
				tips.setToolTip(col, GraphicalInterfaceConstants.KNOCKOUT_TOOLTIP);
			}
			if (c == GraphicalInterfaceConstants.GENE_ASSOCIATIONS_COLUMN) {
				tips.setToolTip(col, GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES[GraphicalInterfaceConstants.GENE_ASSOCIATIONS_COLUMN]);
			}
		}
		reactionsTable.getTableHeader().addMouseMotionListener(tips);
		reactionsTable.getTableHeader().addMouseListener(new ReactionsColumnHeaderListener());
		reactionsTable.getTableHeader().addMouseListener(new ReactionsHeaderPopupListener());	
		
		//from http://www.java2s.com/Tutorial/Java/0240__Swing/thelastcolumnismovedtothefirstposition.htm
		// columns cannot be rearranged by dragging
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
			if (i==GraphicalInterfaceConstants.REACTION_EQUN_ABBR_COLUMN) {
				column.setPreferredWidth(GraphicalInterfaceConstants.REACTION_EQUN_ABBR_WIDTH);//3  
				ChangeName(reactionsTable, GraphicalInterfaceConstants.REACTION_EQUN_ABBR_COLUMN, 
						GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES[GraphicalInterfaceConstants.REACTION_EQUN_ABBR_COLUMN]); 
			}
			if (i==GraphicalInterfaceConstants.REACTION_EQUN_NAMES_COLUMN) {
				column.setPreferredWidth(GraphicalInterfaceConstants.REACTION_EQUN_NAMES_WIDTH);//4  
				ChangeName(reactionsTable, GraphicalInterfaceConstants.REACTION_EQUN_NAMES_COLUMN, 
						GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES[GraphicalInterfaceConstants.REACTION_EQUN_NAMES_COLUMN]); 
			}
			if (i==GraphicalInterfaceConstants.REVERSIBLE_COLUMN) {
				column.setPreferredWidth(GraphicalInterfaceConstants.REVERSIBLE_WIDTH);        //5
				ChangeName(reactionsTable, GraphicalInterfaceConstants.REVERSIBLE_COLUMN, 
						GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES[GraphicalInterfaceConstants.REVERSIBLE_COLUMN]); 
			}
			if (i==GraphicalInterfaceConstants.LOWER_BOUND_COLUMN) {
				ChangeName(reactionsTable, GraphicalInterfaceConstants.LOWER_BOUND_COLUMN, 
						GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES[GraphicalInterfaceConstants.LOWER_BOUND_COLUMN]);          
			}
			if (i==GraphicalInterfaceConstants.UPPER_BOUND_COLUMN) {
				ChangeName(reactionsTable, GraphicalInterfaceConstants.UPPER_BOUND_COLUMN, 
						GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES[GraphicalInterfaceConstants.UPPER_BOUND_COLUMN]);          
			} 
			if (i==GraphicalInterfaceConstants.BIOLOGICAL_OBJECTIVE_COLUMN) {
				ChangeName(reactionsTable, GraphicalInterfaceConstants.BIOLOGICAL_OBJECTIVE_COLUMN, 
						GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES[GraphicalInterfaceConstants.BIOLOGICAL_OBJECTIVE_COLUMN]); 
			}
			if (i==GraphicalInterfaceConstants.SYNTHETIC_OBJECTIVE_COLUMN) {
				ChangeName(reactionsTable, GraphicalInterfaceConstants.SYNTHETIC_OBJECTIVE_COLUMN, 
						GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES[GraphicalInterfaceConstants.SYNTHETIC_OBJECTIVE_COLUMN]); 
			}
			//set alignment of columns with numerical values to right, and default width
			if (i==GraphicalInterfaceConstants.BIOLOGICAL_OBJECTIVE_COLUMN || i==GraphicalInterfaceConstants.LOWER_BOUND_COLUMN
					|| i==GraphicalInterfaceConstants.UPPER_BOUND_COLUMN || i==GraphicalInterfaceConstants.FLUX_VALUE_COLUMN || 
					i==GraphicalInterfaceConstants.SYNTHETIC_OBJECTIVE_COLUMN) {	  
				reacRenderer.setHorizontalAlignment(JLabel.RIGHT); 
				column.setPreferredWidth(GraphicalInterfaceConstants.DEFAULT_WIDTH);
			} 
			
			if (i==GraphicalInterfaceConstants.GENE_ASSOCIATIONS_COLUMN) {
				column.setPreferredWidth(GraphicalInterfaceConstants.REACTION_META_DEFAULT_WIDTH);        
				ChangeName(reactionsTable, GraphicalInterfaceConstants.GENE_ASSOCIATIONS_COLUMN, 
						GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES[GraphicalInterfaceConstants.GENE_ASSOCIATIONS_COLUMN]); 
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
			if (i >= metaColumnCount + GraphicalInterfaceConstants.REACTIONS_DB_COLUMN_NAMES.length || LocalConfig.getInstance().getHiddenReactionsColumns().contains(i)) {
				//sets column not visible
				column.setMaxWidth(0);
				column.setMinWidth(0); 
				column.setWidth(0); 
				column.setPreferredWidth(0);
			}
			// only scrolls all the way to the right when column added
			if (addReacColumn) {
				reactionsTable.scrollRectToVisible(reactionsTable.getCellRect(0, metaColumnCount + GraphicalInterfaceConstants.REACTIONS_DB_COLUMN_NAMES.length, false));
			}
		}
	}

	private class MetabolitesRowListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent event) {
			highlightParticipatingRxns = false;
			if (LocalConfig.getInstance().findReplaceFocusLost) {
				findButtonMetabolitesClicked = false;
				throwNotFoundError = false;
				if (getFindReplaceDialog() != null && !LocalConfig.getInstance().addReactantPromptShown) {
					getFindReplaceDialog().replaceButton.setEnabled(false);
					getFindReplaceDialog().replaceAllButton.setEnabled(false);
					getFindReplaceDialog().replaceFindButton.setEnabled(false);
				}				
			}
			String metaboliteRow = Integer.toString((metabolitesTable.getSelectedRow() + 1));
			try {
				if (LocalConfig.getInstance().getSuspiciousMetabolites().size() > 0) {
					setLoadErrorMessage("Model contains suspicious metabolites.");
					statusBar.setText("Row " + metaboliteRow + "                   " + getLoadErrorMessage());
				} else {
					statusBar.setText("Row " + metaboliteRow);
				}
			} catch (Throwable t) {
				
			}			
			if (metabolitesTable.getRowCount() > 0 && metabolitesTable.getSelectedRow() > -1 && tabbedPane.getSelectedIndex() == 1) {
				if (metabolitesTable.getSelectedRow() > -1) {
					int viewRow = metabolitesTable.convertRowIndexToModel(metabolitesTable.getSelectedRow());
					//String value = (String) metabolitesTable.getModel().getValueAt(viewRow, metabolitesTable.getSelectedColumn()); 
					enableOrDisableMetabolitesItems();
					// if any cell selected any existing find all highlighting is unhighlighted
					reactionsFindAll = false;
					metabolitesFindAll = false;	
					reactionsTable.repaint();
					metabolitesTable.repaint();			 
					selectedCellChanged = true;
					changeMetaboliteFindSelection = true;
					// prevents invisible id column from setting id in formulaBar for find events
					if (metabolitesTable.getSelectedColumn() != 0) {
						formulaBar.setText((String) metabolitesTable.getModel().getValueAt(viewRow, metabolitesTable.getSelectedColumn()));	
					}	    			
				} else {
					formulaBar.setText("");
				}
			}
			if (event.getValueIsAdjusting()) {
				return;
			}
		}
	}

	private class MetabolitesColumnListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent event) {
			if (LocalConfig.getInstance().findReplaceFocusLost) {
				findButtonMetabolitesClicked = false;
				throwNotFoundError = false;
				if (getFindReplaceDialog() != null && !LocalConfig.getInstance().addReactantPromptShown) {
					getFindReplaceDialog().replaceButton.setEnabled(false);
					getFindReplaceDialog().replaceAllButton.setEnabled(false);
					getFindReplaceDialog().replaceFindButton.setEnabled(false);
				}				
			}
			if (metabolitesTable.getSelectedRow() > -1 && metabolitesTable.getSelectedColumn() > -1 && tabbedPane.getSelectedIndex() == 1) {				
				int viewRow = metabolitesTable.convertRowIndexToModel(metabolitesTable.getSelectedRow());
				//String value = (String) metabolitesTable.getModel().getValueAt(viewRow, metabolitesTable.getSelectedColumn()); 
				enableOrDisableMetabolitesItems();
				// if any cell selected any existing find all highlighting is unhighlighted
				reactionsFindAll = false;
				metabolitesFindAll = false;	
				reactionsTable.repaint();
				metabolitesTable.repaint();								 
				selectedCellChanged = true;
				changeMetaboliteFindSelection = true;
				// prevents invisible id column from setting id in formulaBar for find events
				if (metabolitesTable.getSelectedColumn() != 0) {
					formulaBar.setText((String) metabolitesTable.getModel().getValueAt(viewRow, metabolitesTable.getSelectedColumn()));			
				}				
				if (event.getValueIsAdjusting()) {
					return;
				}
			} else {
    			formulaBar.setText("");
    		}			
		}
	}
	
	// disables items if cell is non-editable
    public void enableOrDisableMetabolitesItems() {
    	int viewRow = metabolitesTable.convertRowIndexToModel(metabolitesTable.getSelectedRow());
		String value = (String) metabolitesTable.getModel().getValueAt(viewRow, metabolitesTable.getSelectedColumn()); 
    	if (metabolitesTable.getSelectedColumn() == GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN && metabolitesTable.getModel().getValueAt(viewRow, metabolitesTable.getSelectedColumn()) != null && LocalConfig.getInstance().getMetaboliteUsedMap().containsKey(value)) {
			formulaBar.setEditable(false);
			formulaBar.setBackground(Color.WHITE);
			formulaBarPasteItem.setEnabled(false);
			pastebutton.setEnabled(false);
		}  else {
			if (isRoot) {
				formulaBar.setEditable(true);
				formulaBarPasteItem.setEnabled(true);
				pastebutton.setEnabled(true);
			}					
		}
    }
	
	class MetabolitesColumnHeaderListener extends MouseAdapter {
		  public void mouseClicked(MouseEvent evt) {
			  int m = metabolitesTable.getSortedColumnIndex();
			  // bug: mouse listener sets multiple undo items
			  // this fix works
			  if (m != getMetabolitesSortColumnIndex() || metabolitesTable.getSortOrder(metabolitesTable.getSortedColumnIndex()) != getMetabolitesSortOrder()) {
				  MetaboliteUndoItem undoItem = createMetaboliteUndoItem("", "", metabolitesTable.getSelectedRow(), metabolitesTable.getSelectedColumn(), 1, UndoConstants.SORT, UndoConstants.METABOLITE_UNDO_ITEM_TYPE);
				  setUndoOldCollections(undoItem);
				  LocalConfig.getInstance().getMetabolitesSortColumns().add(m);
				  LocalConfig.getInstance().getMetabolitesSortOrderList().add(metabolitesTable.getSortOrder(metabolitesTable.getSortedColumnIndex()));
				  undoItem.setOldSortColumnIndex(LocalConfig.getInstance().getMetabolitesSortColumns().get(LocalConfig.getInstance().getMetabolitesSortColumns().size() - 2));
				  undoItem.setNewSortColumnIndex(LocalConfig.getInstance().getMetabolitesSortColumns().get(LocalConfig.getInstance().getMetabolitesSortColumns().size() - 1));
				  undoItem.setOldSortOrder(LocalConfig.getInstance().getMetabolitesSortOrderList().get(LocalConfig.getInstance().getMetabolitesSortOrderList().size() - 2));
				  undoItem.setNewSortOrder(LocalConfig.getInstance().getMetabolitesSortOrderList().get(LocalConfig.getInstance().getMetabolitesSortOrderList().size() - 1));
				  setUpMetabolitesUndo(undoItem);
			  }
			  setMetabolitesSortColumnIndex(m);
			  setMetabolitesSortOrder(metabolitesTable.getSortOrder(metabolitesTable.getSortedColumnIndex()));
		  }
	}
	
	HighlightPredicate suspiciousPredicate = new HighlightPredicate() {
		public boolean isHighlighted(Component renderer ,ComponentAdapter adapter) {
			int viewRow = metabolitesTable.convertRowIndexToModel(adapter.row);			
			int id = Integer.valueOf(metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.DB_METABOLITE_ID_COLUMN).toString());					
			if (metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN) != null) {
				if (LocalConfig.getInstance().getSuspiciousMetabolites().contains(id)) {					
					return true;
				}
			}						
			return false;
		}
	};
	
	ColorHighlighter suspicious = new ColorHighlighter(suspiciousPredicate, Color.RED, null);
	
	HighlightPredicate unusedPredicate = new HighlightPredicate() {
		public boolean isHighlighted(Component renderer ,ComponentAdapter adapter) {
			int viewRow = metabolitesTable.convertRowIndexToModel(adapter.row);
			if (metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN) != null) {
				if (highlightUnusedMetabolites == true && !(LocalConfig.getInstance().getMetaboliteUsedMap().containsKey(metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN).toString()))) {					
					return true;
				}
			}						
			return false;
		}
	};
	
	ColorHighlighter unused = new ColorHighlighter(unusedPredicate, Color.YELLOW, null);
	
	HighlightPredicate duplicateMetabPredicate = new HighlightPredicate() {
		public boolean isHighlighted(Component renderer ,ComponentAdapter adapter) {
			int viewRow = metabolitesTable.convertRowIndexToModel(adapter.row);
			int id = Integer.valueOf(metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.DB_METABOLITE_ID_COLUMN).toString());					
			if (LocalConfig.getInstance().getDuplicateIds().contains(id)) {									
				return true;
			}						
			return false;
		}
	};
	
	ColorHighlighter duplicateMetab = new ColorHighlighter(duplicateMetabPredicate, Color.ORANGE, null);
	
	HighlightPredicate metabolitesSelectedAreaPredicate = new HighlightPredicate() {
		public boolean isHighlighted(Component renderer ,ComponentAdapter adapter) {
			if (findMode) {
				if (searchSelectedArea) {
					if (findButtonMetabolitesClicked) {
						if (adapter.row >= selectedMetabolitesRowStartIndex && adapter.row < selectedMetabolitesRowEndIndex && adapter.column >= selectedMetabolitesColumnStartIndex && adapter.column < selectedMetabolitesColumnEndIndex) {
							return true;
						}
					}					
				}
			}
																				
			return false;
		}
	};
	
	ColorHighlighter metabolitesSelectedArea = new ColorHighlighter(metabolitesSelectedAreaPredicate, GraphicalInterfaceConstants.SELECTED_AREA_COLOR, null);
	
	HighlightPredicate metaboliteFindAllPredicate = new HighlightPredicate() {
		public boolean isHighlighted(Component renderer ,ComponentAdapter adapter) {
			if (findMode) {
				if (metabolitesFindAll) {
					for (int i = 0; i < getMetabolitesFindLocationsList().size(); i++) {
						if (adapter.row == getMetabolitesFindLocationsList().get(i).get(0) && adapter.column == getMetabolitesFindLocationsList().get(i).get(1)) {
							return true;
						}
					}
				} else {
					if (getMetabolitesReplaceLocation().size() > 0 && !changeMetaboliteFindSelection) {
						if (adapter.row == getMetabolitesReplaceLocation().get(0) && adapter.column == getMetabolitesReplaceLocation().get(1)) {
							return true;
						}
					}				
				}
			}								
			return false;
		}
	};
	
	ColorHighlighter metaboliteFindAll = new ColorHighlighter(metaboliteFindAllPredicate, GraphicalInterfaceConstants.FIND_ALL_COLOR, null);
	
	public void setMetabolitesTableLayout() {	 
		metabolitesTable.getSelectionModel().addListSelectionListener(new MetabolitesRowListener());
		metabolitesTable.getColumnModel().getSelectionModel().
		addListSelectionListener(new MetabolitesColumnListener());

		metabolitesTable.setAutoResizeMode(JXTable.AUTO_RESIZE_OFF);
		metabolitesTable.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		
		metabolitesTable.getColumnExt("id").setComparator(numberComparator);
								
		metabolitesTable.addHighlighter(unused);
		metabolitesTable.addHighlighter(duplicateMetab);
		metabolitesTable.addHighlighter(suspicious);
		metabolitesTable.addHighlighter(metabolitesSelectedArea);
		metabolitesTable.addHighlighter(metaboliteFindAll);
		
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
		metabolitesTable.getTableHeader().addMouseListener(new MetabolitesColumnHeaderListener());
		metabolitesTable.getTableHeader().addMouseListener(new MetabolitesHeaderPopupListener());
		
		// from http://www.java2s.com/Tutorial/Java/0240__Swing/thelastcolumnismovedtothefirstposition.htm
		// columns cannot be rearranged by dragging 
		metabolitesTable.getTableHeader().setReorderingAllowed(false);  
		
		MetabolitesMetaColumnManager metabolitesMetaColumnManager = new MetabolitesMetaColumnManager();
		int metabMetaColumnCount = metabolitesMetaColumnManager.getMetaColumnCount(LocalConfig.getInstance().getDatabaseName());	
			
		int m = metabolitesTable.getModel().getColumnCount();
		for (int w = 0; w < m; w++) {
			ColorTableCellRenderer metabGreyRenderer = new ColorTableCellRenderer();
			MetabolitesTableCellRenderer metabRenderer = new MetabolitesTableCellRenderer();			
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
			if (w >= metabMetaColumnCount + GraphicalInterfaceConstants.METABOLITES_DB_COLUMN_NAMES.length || LocalConfig.getInstance().getHiddenMetabolitesColumns().contains(w)) {
				//sets column not visible
				column.setMaxWidth(0);
				column.setMinWidth(0); 
				column.setWidth(0); 
				column.setPreferredWidth(0);
			}
			// only scrolls all the way to the right when column added
			if (addMetabColumn) {
				metabolitesTable.scrollRectToVisible(metabolitesTable.getCellRect(0, metabMetaColumnCount + GraphicalInterfaceConstants.METABOLITES_DB_COLUMN_NAMES.length, false));
			}
		}
	}
	
	/************************************************************************************/
	//end table layouts
	/************************************************************************************/

	/******************************************************************************/
	//set table properties
	/******************************************************************************/
	
	public void setTableCellFocused(int row, int col, JXTable table) {
		table.changeSelection(row, col, false, false);
		table.requestFocus();
	}
	
	public void setUpColumnSelectionMode() {
		setSelectionMode(1);
		reactionsTable.setColumnSelectionAllowed(true);
		reactionsTable.setRowSelectionAllowed(false); 
		metabolitesTable.setColumnSelectionAllowed(true);
		metabolitesTable.setRowSelectionAllowed(false);
	}
	
	public void setUpRowSelectionMode() {
		setSelectionMode(2);
		reactionsTable.setColumnSelectionAllowed(false);
		reactionsTable.setRowSelectionAllowed(true); 
		metabolitesTable.setColumnSelectionAllowed(false);
		metabolitesTable.setRowSelectionAllowed(true);
	}
	
    public void setUpCellSelectionMode() {
    	setSelectionMode(0);
		reactionsTable.setColumnSelectionAllowed(false);
		reactionsTable.setRowSelectionAllowed(false); 
		reactionsTable.setCellSelectionEnabled(true);
		metabolitesTable.setColumnSelectionAllowed(false);
		metabolitesTable.setRowSelectionAllowed(false); 
		metabolitesTable.setCellSelectionEnabled(true);
	}
    
    // sets sorted by db id and ascending
    public void setSortDefault() {
    	setReactionsSortColumnIndex(0);
		setMetabolitesSortColumnIndex(0);
		setReactionsSortOrder(SortOrder.ASCENDING);
		setMetabolitesSortOrder(SortOrder.ASCENDING);
    }
	
	/*******************************************************************************/
	//end table methods and actions
	/*******************************************************************************/ 	
	
	/************************************************************************************/
	//table header context menus
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
		
		JMenuItem deleteColumnMenu = new JMenuItem("Delete Column");
		//core columns cannot be deleted - ko, flux, abbreviation, equation, bounds, objective
		if (columnIndex == GraphicalInterfaceConstants.REACTION_NAME_COLUMN || columnIndex == GraphicalInterfaceConstants.REVERSIBLE_COLUMN || columnIndex > 9) {
			deleteColumnMenu.setEnabled(true);
		} else {
			deleteColumnMenu.setEnabled(false);
		}
		deleteColumnMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				ReactionUndoItem undoItem = createReactionUndoItem("", "", reactionsTable.getSelectedRow(), reactionsTable.getSelectedColumn(), 1, UndoConstants.DELETE_COLUMN, UndoConstants.REACTION_UNDO_ITEM_TYPE);
				LocalConfig.getInstance().getHiddenReactionsColumns().add(columnIndex);				
				closeConnection();
				reloadTables(LocalConfig.getInstance().getLoadedDatabase()); 				
				undoItem.setDeletedColumnIndex(columnIndex);
				setUpReactionsUndo(undoItem);
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
		
		JMenuItem deleteColumnMenu = new JMenuItem("Delete Column");
		//core columns cannot be deleted - abbreviation and boundary
		if (columnIndex == GraphicalInterfaceConstants.METABOLITE_NAME_COLUMN || columnIndex == GraphicalInterfaceConstants.CHARGE_COLUMN || columnIndex == GraphicalInterfaceConstants.COMPARTMENT_COLUMN || columnIndex > 5) {
			deleteColumnMenu.setEnabled(true);
		} else {
			deleteColumnMenu.setEnabled(false);
		}
		deleteColumnMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				MetaboliteUndoItem undoItem = createMetaboliteUndoItem("", "", metabolitesTable.getSelectedRow(), metabolitesTable.getSelectedColumn(), 1, UndoConstants.DELETE_COLUMN, UndoConstants.METABOLITE_UNDO_ITEM_TYPE);
				setUndoOldCollections(undoItem);
				LocalConfig.getInstance().getHiddenMetabolitesColumns().add(columnIndex);	
				closeConnection();
				reloadTables(LocalConfig.getInstance().getLoadedDatabase());		
				undoItem.setDeletedColumnIndex(columnIndex);
				setUpMetabolitesUndo(undoItem);
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
					// create reaction equation column popup menu
					if (col == GraphicalInterfaceConstants.REACTION_EQUN_ABBR_COLUMN) {
						JPopupMenu reactionsContextMenu = createReactionsContextMenu(row, col);
						if (reactionsContextMenu != null
								&& reactionsContextMenu.getComponentCount() > 0) {
							reactionsContextMenu.show(reactionsTable, p.x, p.y);
						}
					} else {
						// create popup for remaining columns
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

	/*******************************************************************************/
	//begin reaction equation context menu
	/*******************************************************************************/	
	
	private JPopupMenu createReactionsContextMenu(final int rowIndex,
			final int columnIndex) {
		JPopupMenu contextMenu = createContextMenu(rowIndex, columnIndex);
		contextMenu.addSeparator();
		if (!reactionsTableEditable || LocalConfig.getInstance().reactionEditorVisible) {
			editorMenu.setEnabled(false);
		}
		editorMenu.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent e) {
				if (!LocalConfig.getInstance().reactionEditorVisible) {
					//setCurrentRow(rowIndex);
					closeConnection();
					try {
						Class.forName("org.sqlite.JDBC");
						Connection con = DriverManager.getConnection("jdbc:sqlite:" + LocalConfig.getInstance().getLoadedDatabase() + ".db");
						LocalConfig.getInstance().setCurrentConnection(con);
						ReactionEditor reactionEditor = new ReactionEditor(con);
						setReactionEditor(reactionEditor);
						reactionEditor.setIconImages(icons);
						reactionEditor.setSize(800, 520);
						reactionEditor.setResizable(false);
						reactionEditor.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
						//reactionEditor.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
						reactionEditor.setLocationRelativeTo(null);
						reactionEditor.setVisible(true);
						reactionEditor.okButton.addActionListener(okButtonActionListener);
						reactionEditor.cancelButton.addActionListener(cancelButtonActionListener);
						reactionEditor.addWindowListener(new WindowAdapter() {
					        public void windowClosing(WindowEvent evt) {
								getReactionEditor().setVisible(false);
								getReactionEditor().dispose();
					        	reactionEditorCloseAction();
					        }
						});
						LocalConfig.getInstance().reactionEditorVisible = true;
					} catch (ClassNotFoundException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					} catch (SQLException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
				}				
			}
		});
		contextMenu.add(editorMenu);

		return contextMenu;
	}

	//listens for ok button event in ReactionEditor
	ActionListener okButtonActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent ae) {
			boolean okToClose = true;
			int viewRow = reactionsTable.convertRowIndexToModel(reactionsTable.getSelectedRow());
		    int id = (Integer.valueOf((String) reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.DB_REACTIONS_ID_COLUMN)));
		    reactionEditor.setReactionEquation(reactionEditor.reactionArea.getText());
		    ReactionUndoItem undoItem = createReactionUndoItem(reactionEditor.getOldReaction(), reactionEditor.getReactionEquation(), reactionsTable.getSelectedRow(), GraphicalInterfaceConstants.REACTION_EQUN_ABBR_COLUMN, id, UndoConstants.TYPING, UndoConstants.REACTION_UNDO_ITEM_TYPE);
		    if (reactionEditor.getReactionEquation().contains("<") || (reactionEditor.getReactionEquation().contains("=") && !reactionEditor.getReactionEquation().contains(">"))) {
				reactionsTable.getModel().setValueAt(reactionEditor.getReactionEquation(), viewRow, GraphicalInterfaceConstants.REACTION_EQUN_ABBR_COLUMN);
				reactionsTable.getModel().setValueAt("true", viewRow, GraphicalInterfaceConstants.REVERSIBLE_COLUMN);
			} else if (reactionEditor.getReactionEquation().contains("-->") || reactionEditor.getReactionEquation().contains("->") || reactionEditor.getReactionEquation().contains("=>")) {
				if (Double.valueOf((String) reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.LOWER_BOUND_COLUMN)) < 0)  {
					JOptionPane.showMessageDialog(null,                
							GraphicalInterfaceConstants.IRREVERSIBLE_REACTION_ERROR_MESSAGE,                
							GraphicalInterfaceConstants.IRREVERSIBLE_REACTION_ERROR_TITLE,                               
							JOptionPane.ERROR_MESSAGE);
					okToClose = false;
				} else {
					reactionsTable.getModel().setValueAt(reactionEditor.getReactionEquation(), viewRow, GraphicalInterfaceConstants.REACTION_EQUN_ABBR_COLUMN);
					reactionsTable.getModel().setValueAt("false", viewRow, GraphicalInterfaceConstants.REVERSIBLE_COLUMN);		    		
				}				
			}
			updateReactionsDatabaseRow(viewRow, Integer.parseInt((String) (reactionsTable.getModel().getValueAt(viewRow, 0))), "SBML", LocalConfig.getInstance().getLoadedDatabase());
			
			ReactionsUpdater updater = new ReactionsUpdater();
			updater.updateReactionEquations(id, reactionEditor.getOldReaction(), reactionEditor.getReactionEquation(), LocalConfig.getInstance().getLoadedDatabase());
			setUpReactionsUndo(undoItem);
			//TODO: change to Cancel button, only 2 buttons: Yes, Cancel, Cancel button runs undo
			if (LocalConfig.getInstance().noButtonClicked) {
				reactionsTable.getModel().setValueAt(updater.reactionEqunAbbr, viewRow, GraphicalInterfaceConstants.REACTION_EQUN_ABBR_COLUMN);
				updateReactionsDatabaseRow(viewRow, Integer.parseInt((String) (reactionsTable.getModel().getValueAt(viewRow, 0))), "SBML", LocalConfig.getInstance().getLoadedDatabase());				
			}
			
			closeConnection();
			LocalConfig.getInstance().setLoadedDatabase(getDatabaseName());
			reloadTables(getDatabaseName());			
			
			if (highlightParticipatingRxns) {
				scrollFirstParticipatingRxnToView();
			}
			if (LocalConfig.getInstance().getSuspiciousMetabolites().size() > 0) {
				setLoadErrorMessage("Model contains suspicious metabolites.");
				statusBar.setText("Row 1" + "                   " + getLoadErrorMessage());
			}
			
			if (okToClose) {
				reactionEditor.setVisible(false);
				reactionEditor.dispose();
			}
			okToClose = true;
			LocalConfig.getInstance().reactionEditorVisible = false;
			editorMenu.setEnabled(true);
		}
	}; 
	
	ActionListener cancelButtonActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent ae) {
			reactionEditorCloseAction();
		}
	}; 
	
	public void reactionEditorCloseAction() {
		LocalConfig.getInstance().reactionEditorVisible = false;
		editorMenu.setEnabled(true);
	}
	
	/*******************************************************************************/
	//end reaction equation context menu
	/*******************************************************************************/	
	
	/*******************************************************************************/
	//begin reaction context menu for other columns
	/*******************************************************************************/	
		
	private JPopupMenu createContextMenu(final int rowIndex,
			final int columnIndex) {
		JPopupMenu contextMenu = new JPopupMenu();	
		
        JMenu selectMenu = new JMenu("Select");
		
		final JRadioButtonMenuItem selectColumns = new JRadioButtonMenuItem(
        "Select Column(s)");
		final JRadioButtonMenuItem selectRows = new JRadioButtonMenuItem(
        "Select Row(s)");
		final JRadioButtonMenuItem selectCells = new JRadioButtonMenuItem(
        "Select Cell(s)");

		ButtonGroup bgSelect = new ButtonGroup();
		bgSelect.add(selectColumns);
		bgSelect.add(selectRows);
		bgSelect.add(selectCells);
		if (getSelectionMode() == 0) {
			selectCells.setSelected(true);
		} else if (getSelectionMode() == 1) {
			selectColumns.setSelected(true);
		} else if (getSelectionMode() == 2) {
			selectRows.setSelected(true);
		}
				
		selectColumns.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (selectColumns.isSelected()) {
					setUpColumnSelectionMode();
				} 
			}
		});
		selectRows.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (selectRows.isSelected()) {
					setUpRowSelectionMode();
				} 
			}
		});
		selectCells.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (selectCells.isSelected()) {
					setUpCellSelectionMode();
				} 
			}
		});
		
        selectMenu.add(selectColumns);
        selectMenu.add(selectRows);
        selectMenu.add(selectCells);
        
        contextMenu.add(selectMenu);
		       
		JMenu selectAllMenu = new JMenu("Select All");
		
		final JRadioButtonMenuItem inclColNamesItem = new JRadioButtonMenuItem(
        "Include Column Names");
		final JRadioButtonMenuItem selectCellsOnly = new JRadioButtonMenuItem(
        "Selected Cells Only");

		ButtonGroup bg = new ButtonGroup();
		bg.add(inclColNamesItem);
		bg.add(selectCellsOnly);
		inclColNamesItem.setSelected(includeRxnColumnNames);
		selectCellsOnly.setSelected(!includeRxnColumnNames);
		
		inclColNamesItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (inclColNamesItem.isSelected()) {
					includeRxnColumnNames = true;
				} else {
					includeRxnColumnNames = false;
				}				
				includeReacColumnNamesAction();
			}
		});
		selectCellsOnly.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (selectCellsOnly.isSelected()) {
					includeRxnColumnNames = false;
				} else {
					includeRxnColumnNames = true;
				}				
				selectReacCellsOnlyAction();
			}
		});
        
        selectAllMenu.add(inclColNamesItem);
        selectAllMenu.add(selectCellsOnly);

        contextMenu.add(selectAllMenu);
		
		contextMenu.addSeparator();
		
		JMenuItem copyMenu = new JMenuItem("Copy");
		copyMenu.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_C, ActionEvent.CTRL_MASK));
		copyMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reactionsCopy();
			}
		});
		contextMenu.add(copyMenu);

		contextMenu.addSeparator();
		
		JMenuItem pasteMenu = new JMenuItem("Paste");
		pasteMenu.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_V, ActionEvent.CTRL_MASK));
		if (isClipboardContainingText(this)
				&& reactionsTable.getModel().isCellEditable(rowIndex, columnIndex)) {
			pasteMenu.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						pasting = true;
						reactionsPaste();
						pasting = false;
						LocalConfig.getInstance().pastedReaction = false;
					} catch (Throwable t) {
						
					}					
				}
			});
		} else {
			pasteMenu.setEnabled(false);
		}
		contextMenu.add(pasteMenu);
		
		JMenuItem clearMenu = new JMenuItem("Clear Contents");
		clearMenu.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_E, ActionEvent.CTRL_MASK));
		clearMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reactionsClear();
			}
		});
		contextMenu.add(clearMenu);
		
		contextMenu.addSeparator();
		
		JMenuItem deleteRowMenu = new JMenuItem("Delete Row(s)");
		deleteRowMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				reactionsDeleteRows();	
				formulaBar.setText("");
			}
		});
		contextMenu.add(deleteRowMenu);	
		
		contextMenu.addSeparator();
		
		unhighlightMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				highlightParticipatingRxns = false;
				reactionsTable.repaint();
			}
		});

		contextMenu.add(unhighlightMenu);	
		
		return contextMenu;
	}  

	public void includeReacColumnNamesAction() {
		reactionsTable.selectAll();			
		selectReactionsRows();
	}
	
	public void selectReacCellsOnlyAction() {
		reactionsTable.selectAll();			
		selectReactionsRows();
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
					// create popup menu for abbreviation column
					if (col == GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN ||
							col == GraphicalInterfaceConstants.METABOLITE_NAME_COLUMN) {
						JPopupMenu abbrevContextMenu = createMetaboliteAbbreviationContextMenu(row, col);
						// ... and show it
						if (abbrevContextMenu != null
								&& abbrevContextMenu.getComponentCount() > 0) {
							abbrevContextMenu.show(metabolitesTable, p.x, p.y);
						}
						// create popup menu for remaining columns	
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

	/****************************************************************************/
	// begin abbreviation column context menu
	/****************************************************************************/

	private JPopupMenu createMetaboliteAbbreviationContextMenu(final int rowIndex,
			final int columnIndex) {
		JPopupMenu contextMenu = createMetabolitesContextMenu(rowIndex, columnIndex);
		contextMenu.addSeparator();
		
		final int viewRow = metabolitesTable.convertRowIndexToModel(rowIndex);
		final int id = Integer.valueOf((String) metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.DB_METABOLITE_ID_COLUMN));		
		final String metabAbbrev = (String) metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN);
		final String metabName = (String) metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_NAME_COLUMN);
		
		JMenuItem renameMenu = new JMenuItem("Rename");
		if (columnIndex == GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN) {
			if (metabAbbrev == null || metabAbbrev.length() == 0) {
				renameMenu.setEnabled(false);
			}
		} else if (columnIndex == GraphicalInterfaceConstants.METABOLITE_NAME_COLUMN) {
			if (metabName == null || metabName.length() == 0) {
				renameMenu.setEnabled(false);
			}
		}
		contextMenu.add(renameMenu);
		
		renameMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				duplicatePromptShown = false;
				MetaboliteRenameInterface metaboliteRenameInterface = new MetaboliteRenameInterface();
				setMetaboliteRenameInterface(metaboliteRenameInterface);
				metaboliteRenameInterface.setTitle(GraphicalInterfaceConstants.RENAME_METABOLITE_INTERFACE_TITLE);
				metaboliteRenameInterface.setIconImages(icons);
				metaboliteRenameInterface.setSize(350, 160);
				metaboliteRenameInterface.setResizable(false);
				metaboliteRenameInterface.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
				metaboliteRenameInterface.setLocationRelativeTo(null);
				if (LocalConfig.getInstance().getMetaboliteUsedMap().containsKey(metabAbbrev) && !LocalConfig.getInstance().getDuplicateIds().contains(id)) {
					Object[] options = {"    Yes    ", "    No    ",};
					int choice = JOptionPane.showOptionDialog(null, 
							GraphicalInterfaceConstants.PARTICIPATING_METAB_RENAME_MESSAGE_PREFIX + 
								metabAbbrev + GraphicalInterfaceConstants.PARTICIPATING_METAB_RENAME_MESSAGE_SUFFIX, 
							GraphicalInterfaceConstants.PARTICIPATING_METAB_RENAME_TITLE, 
							JOptionPane.YES_NO_OPTION, 
							JOptionPane.QUESTION_MESSAGE, 
							null, options, options[0]);
					if (choice == JOptionPane.YES_OPTION) {
						renameMetabolite = true;
						metaboliteRenameInterface.setVisible(true);
					}
					if (choice == JOptionPane.NO_OPTION) {
						
					}
				// not necessary to use the interface to rename an unused or duplicate
				// metabolite but for consistency, when rename item clicked, interface
				// is displayed and functional. another option would be to disable the
				// menu item if these conditions are true but that may be confusing
				} else {
					metaboliteRenameInterface.setVisible(true);
				}
			}
		});
		
		//TODO: create action for metabolite name
		ActionListener metabRenameOKButtonActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent prodActionEvent) {
				String newName = "";
				metaboliteRenameInterface.setNewName(metaboliteRenameInterface.textField.getText());
				if (metaboliteRenameInterface.getNewName() != null && metaboliteRenameInterface.getNewName().length() > 0) {
					newName = metaboliteRenameInterface.getNewName();
					// check if duplicate metabolite
					if (LocalConfig.getInstance().getMetaboliteIdNameMap().containsKey(newName)) {							
						if (!duplicatePromptShown) {
							JOptionPane.showMessageDialog(null,                
									"Duplicate Metabolite.",                
									"Duplicate Metabolite",                                
									JOptionPane.ERROR_MESSAGE);
						}
						duplicatePromptShown = true;
					} else {
						// update id name map
						Object idValue = LocalConfig.getInstance().getMetaboliteIdNameMap().get(metabAbbrev);
						MetaboliteUndoItem undoItem = null;
						if (columnIndex == GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN) {
							undoItem = createMetaboliteUndoItem(metabAbbrev, newName, metabolitesTable.getSelectedRow(), 
									GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN, (Integer) idValue, UndoConstants.RENAME_METABOLITE, UndoConstants.METABOLITE_UNDO_ITEM_TYPE);
						} else if (columnIndex == GraphicalInterfaceConstants.METABOLITE_NAME_COLUMN) {
							undoItem = createMetaboliteUndoItem(metabName, newName, metabolitesTable.getSelectedRow(), 
									GraphicalInterfaceConstants.METABOLITE_NAME_COLUMN, (Integer) idValue, UndoConstants.RENAME_METABOLITE, UndoConstants.METABOLITE_UNDO_ITEM_TYPE);
						}
						// this only prevents a null pointer exception, lists updated in undo item
						setUndoOldCollections(undoItem);
						LocalConfig.getInstance().getMetaboliteIdNameMap().remove(metabAbbrev);	
						LocalConfig.getInstance().getMetaboliteIdNameMap().put(newName, idValue);
						if (renameMetabolite) { 
							MetaboliteFactory aFactory = new MetaboliteFactory("SBML", LocalConfig.getInstance().getLoadedDatabase());
							ArrayList<Integer> participatingReactions = aFactory.participatingReactions(newName);
							undoItem.setReactionIdList(participatingReactions);
							// update used list for new name
							Object value = LocalConfig.getInstance().getMetaboliteUsedMap().get(metabAbbrev);
							LocalConfig.getInstance().getMetaboliteUsedMap().remove(metabAbbrev);
							LocalConfig.getInstance().getMetaboliteUsedMap().put(newName, value);
							// rewrite reaction and check if valid
							ReactionsUpdater updater = new ReactionsUpdater();
							updater.rewriteReactions(participatingReactions, LocalConfig.getInstance().getLoadedDatabase());
						}							
						metabolitesTable.getModel().setValueAt(newName, viewRow, GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN);
						updateMetabolitesDatabaseRow(viewRow, Integer.parseInt((String) (metabolitesTable.getModel().getValueAt(viewRow, 0))), "SBML", LocalConfig.getInstance().getLoadedDatabase());					

						closeConnection();
						reloadTables(LocalConfig.getInstance().getLoadedDatabase());
						metaboliteRenameInterface.textField.setText("");
						metaboliteRenameInterface.setVisible(false);
						metaboliteRenameInterface.dispose();
						if (undoItem != null) {
							setUpMetabolitesUndo(undoItem);
						}	
					}						
				}
			}
		};
		
		metaboliteRenameInterface.okButton.addActionListener(metabRenameOKButtonActionListener);
		
		contextMenu.addSeparator();

		//TODO: replace these two menu items below with radio buttons
		final JMenuItem participatingReactionsMenu = new JMenuItem("Highlight Participating Reactions");
		if (columnIndex == GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN) {
			if (metabAbbrev == null || !LocalConfig.getMetaboliteUsedMap().containsKey(metabAbbrev)) {
				participatingReactionsMenu.setEnabled(false);
			}
		} else if (columnIndex == GraphicalInterfaceConstants.METABOLITE_NAME_COLUMN) {
			if (metabName == null || !LocalConfig.getMetaboliteUsedMap().containsKey(metabAbbrev)) {
				participatingReactionsMenu.setEnabled(false);
			}
		}		
		participatingReactionsMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				highlightParticipatingRxns = true;
				MetaboliteFactory aFactory = new MetaboliteFactory("SBML", LocalConfig.getInstance().getLoadedDatabase());	
				setParticipatingMetabolite(metabAbbrev);
				LocalConfig.getInstance().setParticipatingReactions(aFactory.participatingReactions(metabAbbrev));
				tabbedPane.setSelectedIndex(0);
				ArrayList<Integer> participatingReactions = aFactory.participatingReactions(metabAbbrev);
				// sort to get minimum
				Collections.sort(participatingReactions);
				// scroll first participating reaction into view
				if (participatingReactions.size() > 0) {
					int firstId = participatingReactions.get(0);
					for (int r = 0; r < reactionsTable.getRowCount(); r++) {
						int viewRow = reactionsTable.convertRowIndexToModel(r);
						Integer cellValue = Integer.valueOf((String) reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.DB_REACTIONS_ID_COLUMN));
						if (cellValue == firstId) {
							if (columnIndex == GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN) {
								setTableCellFocused(r, GraphicalInterfaceConstants.REACTION_EQUN_ABBR_COLUMN, reactionsTable);
							} else if (columnIndex == GraphicalInterfaceConstants.METABOLITE_NAME_COLUMN) {
								setTableCellFocused(r, GraphicalInterfaceConstants.REACTION_EQUN_NAMES_COLUMN, reactionsTable);
						    }				
						}	
					}
				}				
			}
		});
		contextMenu.add(participatingReactionsMenu);

		final JMenuItem unhighlightParticipatingReactionsMenu = new JMenuItem("Unhighlight Participating Reactions");
		if (metabAbbrev == null || !LocalConfig.getMetaboliteUsedMap().containsKey(metabAbbrev)) {
			unhighlightParticipatingReactionsMenu.setEnabled(false);
		}
		unhighlightParticipatingReactionsMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				highlightParticipatingRxns = false;
			}
		});
		contextMenu.add(unhighlightParticipatingReactionsMenu);

		return contextMenu;
	}
	
	/****************************************************************************/
	// end abbreviation column context menu
	/****************************************************************************/

	/****************************************************************************/
	// begin context menu for remaining columns
	/****************************************************************************/
	
	private JPopupMenu createMetabolitesContextMenu(final int rowIndex,
			final int columnIndex) {
		JPopupMenu contextMenu = new JPopupMenu();		
		
        JMenu selectMenu = new JMenu("Select");
		
		final JRadioButtonMenuItem selectColumns = new JRadioButtonMenuItem(
        "Select Column(s)");
		final JRadioButtonMenuItem selectRows = new JRadioButtonMenuItem(
        "Select Row(s)");
		final JRadioButtonMenuItem selectCells = new JRadioButtonMenuItem(
        "Select Cell(s)");

		ButtonGroup bgSelect = new ButtonGroup();
		bgSelect.add(selectColumns);
		bgSelect.add(selectRows);
		bgSelect.add(selectCells);
		if (getSelectionMode() == 0) {
			selectCells.setSelected(true);
		} else if (getSelectionMode() == 1) {
			selectColumns.setSelected(true);
		} else if (getSelectionMode() == 2) {
			selectRows.setSelected(true);
		}
				
		selectColumns.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (selectColumns.isSelected()) {
					setUpColumnSelectionMode();
				} 
			}
		});
		selectRows.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (selectRows.isSelected()) {
					setUpRowSelectionMode();
				} 
			}
		});
		selectCells.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (selectCells.isSelected()) {
					setUpCellSelectionMode();
				} 
			}
		});
		
        selectMenu.add(selectColumns);
        selectMenu.add(selectRows);
        selectMenu.add(selectCells);
        
        contextMenu.add(selectMenu);
		
		JMenu selectAllMenu = new JMenu("Select All");
		
		final JRadioButtonMenuItem inclColNamesItem = new JRadioButtonMenuItem(
        "Include Column Names");
		final JRadioButtonMenuItem selectCellsOnly = new JRadioButtonMenuItem(
        "Selected Cells Only");

		ButtonGroup bg = new ButtonGroup();
		bg.add(inclColNamesItem);
		bg.add(selectCellsOnly);
		inclColNamesItem.setSelected(includeMtbColumnNames);
		selectCellsOnly.setSelected(!includeMtbColumnNames);
		
		inclColNamesItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (inclColNamesItem.isSelected()) {
					includeMtbColumnNames = true;
				} else {
					includeMtbColumnNames = false;
				}				
				includeMetabColumnNamesAction();
			}
		});
		selectCellsOnly.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (selectCellsOnly.isSelected()) {
					includeMtbColumnNames = false;
				} else {
					includeMtbColumnNames = true;
				}				
				selectMetabCellsOnlyAction();
			}
		});        
        selectAllMenu.add(inclColNamesItem);
        selectAllMenu.add(selectCellsOnly);

        contextMenu.add(selectAllMenu);
		
		contextMenu.addSeparator();
		
		JMenuItem copyMenu = new JMenuItem("Copy");
		copyMenu.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_C, ActionEvent.CTRL_MASK));
		copyMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int viewRow = metabolitesTable.convertRowIndexToModel(metabolitesTable.getSelectedRow());
				metabolitesCopy();
			}
		});
		contextMenu.add(copyMenu);
		
		contextMenu.addSeparator();
		
		JMenuItem pasteMenu = new JMenuItem("Paste");
		pasteMenu.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_V, ActionEvent.CTRL_MASK));
		if (isClipboardContainingText(this)
				&& metabolitesTable.getModel().isCellEditable(rowIndex, columnIndex)) {
			pasteMenu.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						pasting = true;
						metabolitesPaste();
						pasting = false;
					} catch (Throwable t) {
						
					}
				}
			});
		} else {
			pasteMenu.setEnabled(false);
		}
		contextMenu.add(pasteMenu);

		JMenuItem clearMenu = new JMenuItem("Clear Contents");
		clearMenu.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_E, ActionEvent.CTRL_MASK));
		clearMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				metabolitesClear();
			}
		});
		contextMenu.add(clearMenu);
		
		contextMenu.addSeparator();

		JMenuItem deleteRowMenu = new JMenuItem("Delete Row(s)");

		if (metabolitesTable.getSelectedRow() > -1) {
			int viewRow = metabolitesTable.convertRowIndexToModel(metabolitesTable.getSelectedRow());
			int id = Integer.valueOf((String) metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.DB_METABOLITE_ID_COLUMN));			
			String metabAbbrev = (String) metabolitesTable.getModel().getValueAt(viewRow, 1);
			if (LocalConfig.getInstance().getMetaboliteUsedMap().containsKey(metabAbbrev) && !LocalConfig.getInstance().getDuplicateIds().contains(id)) {
				deleteRowMenu.setEnabled(false);
			} else {
				deleteRowMenu.setEnabled(true);
				deleteRowMenu.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						metaboliteDeleteRows();	
						formulaBar.setText("");
					}
				});
			}
		}
		
		contextMenu.add(deleteRowMenu);
		
		return contextMenu;
	}  

	public void includeMetabColumnNamesAction() {
		metabolitesTable.selectAll();			
		selectMetabolitesRows();
	}
	
	public void selectMetabCellsOnlyAction() {
		metabolitesTable.selectAll();			
		selectMetabolitesRows();
	}
	
	/*******************************************************************************/
	// end Metabolites Table context menus
	/*******************************************************************************/
	
	/*******************************************************************************/
	//end context menus  
	/*******************************************************************************/  	
	
	/*******************************************************************************/
	//clipboard
	/*******************************************************************************/

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

	/**************************************************************************/
	//reactionsTable context menu methods
	/**************************************************************************/
	//These methods are based partly on code from ExcelAdapter
	//http://stackoverflow.com/questions/4671657/how-to-copy-content-of-the-jtable-to-clipboard
	
	public void selectReactionsRows() {
		setClipboardContents("");
		ReactionsMetaColumnManager reactionsMetaColumnManager = new ReactionsMetaColumnManager();
		int metaColumnCount = reactionsMetaColumnManager.getMetaColumnCount(LocalConfig.getInstance().getLoadedDatabase());	
		
		StringBuffer sbf=new StringBuffer();
		int numrows = reactionsTable.getSelectedRowCount(); 
		LocalConfig.getInstance().setNumberCopiedRows(numrows);
		//for row selection all columns are selected except id column (hidden)
		LocalConfig.getInstance().setNumberCopiedColumns(reactionsTable.getColumnCount() - 1);
		int[] rowsselected=reactionsTable.getSelectedRows();  
		reactionsTable.changeSelection(rowsselected[0], 1, false, false);
		reactionsTable.changeSelection(rowsselected[numrows - 1], reactionsTable.getColumnCount(), false, true);
		reactionsTable.scrollColumnToVisible(1);		
		if (includeRxnColumnNames == true) {
			//add column names to clipboard
			for (int c = 1; c < GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES.length; c++) {
				if (getVisibleReactionsColumns().contains(c)) {
					sbf.append(GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES[c]);
					if (c < GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES.length - 1) {
						sbf.append("\t"); 
					}
				}				
			}
			if (metaColumnCount > 0) {
				for (int r = 1; r <= metaColumnCount; r++) {
					if (getVisibleReactionsColumns().contains(GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES.length + r - 1)) {
						sbf.append("\t");
						sbf.append(reactionsMetaColumnManager.getColumnName(LocalConfig.getInstance().getLoadedDatabase(), r));
					}					
				}
			}
			sbf.append("\n");
		}
				
		for (int i = 0; i < numrows; i++) {
			//starts at 1 to avoid reading hidden db id column
			for (int j = 1; j < reactionsTable.getColumnCount() - 1; j++) 
			{ 
				if (getVisibleReactionsColumns().contains(j)) {
					if (reactionsTable.getValueAt(rowsselected[i], j) != null) {
						sbf.append(reactionsTable.getValueAt(rowsselected[i], j));
					} else {
						sbf.append(" ");
					}
					if (j < reactionsTable.getColumnCount()-1) sbf.append("\t");
				}				 
			} 
			sbf.append("\n"); 
		}  
		setClipboardContents(sbf.toString());
		//System.out.println(sbf.toString());
	}
	
	public void reactionsCopy() {		
		int numcols=reactionsTable.getSelectedColumnCount(); 
		int numrows=reactionsTable.getSelectedRowCount(); 
		LocalConfig.getInstance().setNumberCopiedRows(numrows);
		LocalConfig.getInstance().setNumberCopiedColumns(numcols);
		int[] rowsselected=reactionsTable.getSelectedRows(); 
		int[] colsselected=reactionsTable.getSelectedColumns(); 
		
		// Clipboard already contains correct values for select all and include column names, do not change 
		if (rowsselected.length == reactionsTable.getRowCount() && colsselected.length == reactionsTable.getColumnCount() && includeRxnColumnNames) {
			
		} else {
			if (getSelectionMode() == 2) {
				LocalConfig.getInstance().includesReactions = true;
				if (getSelectionMode() == 2) {
					includeRxnColumnNames = false;				
				}
				selectReactionsRows();
			} else {
				StringBuffer sbf=new StringBuffer(); 
				ListSelectionModel selectionModel = reactionsTable.getSelectionModel();
				if (getSelectionMode() == 1) {
		        	//sets columns as selected    		
		    		selectionModel.setSelectionInterval(0, reactionsTable.getModel().getRowCount() - 1);
				}
				// Check to ensure we have selected only a contiguous block of cells
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
				for (int i=0;i<numrows;i++) { 
					for (int j=0;j<numcols;j++) {
						if (getVisibleReactionsColumns().contains(colsselected[j])) {
							if (reactionsTable.getValueAt(rowsselected[i],colsselected[j]) != null) {
								sbf.append(reactionsTable.getValueAt(rowsselected[i],colsselected[j]));
							} else {
								sbf.append(" ");
							}
							if (j<numcols-1) sbf.append("\t"); 
						}					
					} 
					sbf.append("\n"); 
				}  
				setClipboardContents(sbf.toString());
				//System.out.println(sbf.toString());			
			}
		}
	}

	public void reactionsPaste() {
		showErrorMessage = true;
		ReactionsUpdater updater = new ReactionsUpdater();
		ArrayList<Integer> rowList = new ArrayList<Integer>();
		ArrayList<Integer> reacIdList = new ArrayList<Integer>();
		ArrayList<String> oldReactionsList = new ArrayList<String>();
		String copiedString = getClipboardContents(GraphicalInterface.this);
		String[] s1 = copiedString.split("\n");
		int startRow = (reactionsTable.getSelectedRows())[0];
		int startCol = (reactionsTable.getSelectedColumns())[0];
		// if entire rows or columns copied and paste position is not at first
		// cell in row or column, error thrown since selection will not fit in area
		if (getSelectionMode() == 1 && startRow != 0) {
			JOptionPane.showMessageDialog(null,                
					GraphicalInterfaceConstants.PASTE_AREA_ERROR,                
					"Paste Error",                                
					JOptionPane.ERROR_MESSAGE);
		} else if (getSelectionMode() == 2 && startCol != 1) {
			JOptionPane.showMessageDialog(null,                
					GraphicalInterfaceConstants.PASTE_AREA_ERROR,                
					"Paste Error",                                
					JOptionPane.ERROR_MESSAGE);
		} else {
			// save old data before paste
			copyReactionsDatabaseTables();
			ReactionUndoItem undoItem = createReactionUndoItem("", "", startRow, startCol, 1, UndoConstants.PASTE, UndoConstants.REACTION_UNDO_ITEM_TYPE);
			undoItem.setTableCopyIndex(LocalConfig.getInstance().getNumReactionTablesCopied());
			if (LocalConfig.getInstance().getNumberCopiedRows() != null && reactionsTable.getSelectedRows().length >= LocalConfig.getInstance().getNumberCopiedRows()) {
				if (LocalConfig.getInstance().getNumberCopiedRows() > 0) {
					//if selected area is larger than copied area, it will fill the same cell
					//contents repeatedly until end of selection, based on integer division
					int quotient = reactionsTable.getSelectedRows().length/LocalConfig.getInstance().getNumberCopiedRows();
					int remainder = reactionsTable.getSelectedRows().length%LocalConfig.getInstance().getNumberCopiedRows();
					for (int q = 0; q < quotient; q++) {	
						//there are two for loops since when pasting into a sorted column the column  
						//sorts itself for each value inserted into table, causing row numbers to change 
						//and erroneous results. making lists of row numbers and db id's, then inserting 
						//values into table and db based on these lists solves this problem
						for (int r = 0; r < LocalConfig.getInstance().getNumberCopiedRows(); r++) {
							int row = reactionsTable.convertRowIndexToModel(startRow + r);
							rowList.add(row);
							int reacId = Integer.valueOf((String) reactionsTable.getModel().getValueAt(row, 0));
							reacIdList.add(reacId);
							String oldReaction = (String) reactionsTable.getModel().getValueAt(row, GraphicalInterfaceConstants.REACTION_EQUN_ABBR_COLUMN);
							setOldReaction(oldReaction);
							oldReactionsList.add(oldReaction);
						}
						pasteReactionRows(rowList, reacIdList, s1, startCol, q, LocalConfig.getInstance().getNumberCopiedRows(), s1.length);
						startRow += LocalConfig.getInstance().getNumberCopiedRows();					
					}
					for (int m = 0; m < remainder; m++) {
						int row = reactionsTable.convertRowIndexToModel(startRow + m);
						rowList.add(row);
						int reacId = Integer.valueOf((String) reactionsTable.getModel().getValueAt(row, 0));
						reacIdList.add(reacId);
						String oldReaction = (String) reactionsTable.getModel().getValueAt(row, GraphicalInterfaceConstants.REACTION_EQUN_ABBR_COLUMN);
						setOldReaction(oldReaction);
						oldReactionsList.add(oldReaction);
					}
					// remainder of s1 could be larger, use smaller value to avoid
					// array index error					
					int min = 0;
					if (s1.length < remainder) {
						min = s1.length;
					} else {
						min = remainder;
					}					
					// if remainder is larger, have to fill in difference with ""
					pasteReactionRows(rowList, reacIdList, s1, startCol, quotient, LocalConfig.getInstance().getNumberCopiedRows(), min);
					if (validPaste) {					
						updater.updateReactionRows(rowList, reacIdList, oldReactionsList, LocalConfig.getInstance().getLoadedDatabase());
						copyReactionsDatabaseTables();
						setUpReactionsUndo(undoItem);
					} else {
						if (showErrorMessage = true) {
							JOptionPane.showMessageDialog(null,                
									getPasteError(),                
									"Paste Error",                                
									JOptionPane.ERROR_MESSAGE);
						}	
						deleteReactionsPasteUndoItem();
						validPaste = true;
					}

					closeConnection();
					reloadTables(LocalConfig.getInstance().getLoadedDatabase());
				}
				//if selected area is smaller than copied area, fills in copied area
				//from first selected cell as upper left
			} else {
				for (int r = 0; r < LocalConfig.getInstance().getNumberCopiedRows(); r++) {
					int row = reactionsTable.convertRowIndexToModel(startRow + r);
					rowList.add(row);
					int reacId = Integer.valueOf((String) reactionsTable.getModel().getValueAt(row, 0));
					reacIdList.add(reacId);
					String oldReaction = (String) reactionsTable.getModel().getValueAt(row, GraphicalInterfaceConstants.REACTION_EQUN_ABBR_COLUMN);
					setOldReaction(oldReaction);
					oldReactionsList.add(oldReaction);
				}
				pasteReactionRows(rowList, reacIdList, s1, startCol, 0, LocalConfig.getInstance().getNumberCopiedRows(), s1.length);
				if (validPaste) {
					updater.updateReactionRows(rowList, reacIdList, oldReactionsList, LocalConfig.getInstance().getLoadedDatabase());
					copyReactionsDatabaseTables();
					setUpReactionsUndo(undoItem);
				} else {
					if (showErrorMessage = true) {
						JOptionPane.showMessageDialog(null,                
								getPasteError(),                
								"Paste Error",                                
								JOptionPane.ERROR_MESSAGE);
					}
					deleteReactionsPasteUndoItem();
					validPaste = true;
				}

				closeConnection();
				reloadTables(LocalConfig.getInstance().getLoadedDatabase());
			}
		}	
	}
	
	// used for invalid paste, invalid clear, and invalid replace all
	public void deleteReactionsPasteUndoItem() {
		DatabaseCreator creator = new DatabaseCreator();
		int numCopied = LocalConfig.getInstance().getNumReactionTablesCopied();	
		creator.deleteReactionsTables(LocalConfig.getInstance().getLoadedDatabase(), tableCopySuffix(numCopied));
		numCopied -= 1;
		LocalConfig.getInstance().setNumReactionTablesCopied(numCopied);
	}
	
	public void pasteReactionRows(ArrayList<Integer> rowList, ArrayList<Integer> reacIdList, String[] s1, int startCol, int multiplier, int numCopiedRows, int range) {
		// multiplier is used if number of selected paste rows is greater than
		// number of copied rows, so that start rows is incremented by number of
		// selected rows each loop - see quotient in reactionsPaste()
		// range is range to paste from String[] s1 to avoid index error
		// usually s1.length but for remainder it is min
		ArrayList<Integer> pasteColumns = new ArrayList<Integer>();
		// if copied row last entry is blank, split truncates selection, length will
		// be less than getNumberCopiedRows and throw ArrayOutOfBoundsError
		int diff = 0;  // if s1 length is less than getNumberCopiedRows(), will be assigned value " "
		if (s1.length < numCopiedRows) {
			diff = numCopiedRows - s1.length;
		}
		int count = 0;
		int p = 0;// number of pasted columns
		while (p < LocalConfig.getInstance().getNumberCopiedColumns() && (startCol + p + count) < reactionsTable.getColumnCount()) {			
			if (!getVisibleReactionsColumns().contains(startCol + p + count)) {
				count += 1;
			} else {
				pasteColumns.add(startCol + p + count);
				p += 1;
			}
		}
		// TODO: error if paste range exceeds visible columns?
		if (pasteColumns.contains(GraphicalInterfaceConstants.REACTION_EQUN_ABBR_COLUMN)) {
			LocalConfig.getInstance().includesReactions = true;
		} else {
			LocalConfig.getInstance().includesReactions = false;
		}		
		for (int r = 0; r < range; r++) {
			int viewRow = reactionsTable.convertRowIndexToView(rowList.get(multiplier * numCopiedRows + r));
			String[] rowstring = s1[r].split("\t");
			for (int c = 0; c < pasteColumns.size(); c++) {
				if (c < rowstring.length) {
					updateReactionsCellIfPasteValid(pasteColumns.get(c), viewRow, rowstring[c]);				
				} else {
					reactionsTable.setValueAt(" ", viewRow, pasteColumns.get(c));
				}			
			}
		}
		if (diff > 0) {
			for (int r = s1.length; r < numCopiedRows; r++) {
				int viewRow = reactionsTable.convertRowIndexToView(rowList.get(multiplier * numCopiedRows + r));
				for (int c = 0; c < pasteColumns.size(); c++) {
					// check if "" is a valid entry
					updateReactionsCellIfPasteValid(pasteColumns.get(c), viewRow, "");							
				}
			}
		}
	}

	public void updateReactionsCellIfPasteValid(int col, int row, String value) {
		if (isReactionsEntryValid(col, row, value)) {
			reactionsTable.setValueAt(value, row, col);			
		} else {
			validPaste = false;
		}
	}
	
	public boolean isReactionsEntryValid(int columnIndex, int viewRow, String value) {
		EntryValidator validator = new EntryValidator();
		if (columnIndex == GraphicalInterfaceConstants.FLUX_VALUE_COLUMN || 
				columnIndex == GraphicalInterfaceConstants.LOWER_BOUND_COLUMN ||
				columnIndex == GraphicalInterfaceConstants.UPPER_BOUND_COLUMN ||
				columnIndex == GraphicalInterfaceConstants.BIOLOGICAL_OBJECTIVE_COLUMN ||
				columnIndex == GraphicalInterfaceConstants.SYNTHETIC_OBJECTIVE_COLUMN) {
		    if (validator.isNumber(value)) {
		    	if (columnIndex == GraphicalInterfaceConstants.LOWER_BOUND_COLUMN && getSelectionMode() != 2) {
					Double lowerBound = Double.valueOf(value);
					Double upperBound = Double.valueOf((String) (reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.UPPER_BOUND_COLUMN)));
					if (reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REVERSIBLE_COLUMN).toString().compareTo("false") == 0 && lowerBound < 0) {					
						setPasteError(GraphicalInterfaceConstants.IRREVERSIBLE_REACTION_ERROR_MESSAGE);
						setReplaceAllError(GraphicalInterfaceConstants.IRREVERSIBLE_REACTION_ERROR_MESSAGE);
				        return false;					
					} else if (lowerBound > upperBound) {
						setPasteError(GraphicalInterfaceConstants.LOWER_BOUND_PASTE_ERROR);
						setReplaceAllError(GraphicalInterfaceConstants.LOWER_BOUND_REPLACE_ALL_ERROR);
				        return false;						
					}
				} else if (columnIndex == GraphicalInterfaceConstants.UPPER_BOUND_COLUMN && getSelectionMode() != 2) {				
					Double lowerBound = Double.valueOf((String) (reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.LOWER_BOUND_COLUMN)));
					Double upperBound = Double.valueOf(value);
					if (upperBound < lowerBound) {
						setPasteError(GraphicalInterfaceConstants.UPPER_BOUND_PASTE_ERROR);
						setReplaceAllError(GraphicalInterfaceConstants.UPPER_BOUND_REPLACE_ALL_ERROR);
				        return false;						
					}
				}
		    } else {
		    	setPasteError("Number format exception");
		    	setReplaceAllError("Number format exception");
		    	validPaste = false;
		    }
		} else if (columnIndex == GraphicalInterfaceConstants.KO_COLUMN) {
			if (value.compareTo("true") == 0 || value.compareTo("false") == 0) {
				return true;
			} else {
				setPasteError(GraphicalInterfaceConstants.INVALID_PASTE_BOOLEAN_VALUE);
				setReplaceAllError(GraphicalInterfaceConstants.INVALID_REPLACE_ALL_BOOLEAN_VALUE);
				return false;
			}
		} else if (columnIndex == GraphicalInterfaceConstants.REACTION_EQUN_ABBR_COLUMN && LocalConfig.getInstance().includesReactions) {
			if (value != null && value.trim().length() > 0) {
				if (value.contains("=") || value.contains(">")) {
					ReactionParser parser = new ReactionParser();
					if (!parser.isValid(value)) {
						setPasteError("Invalid Reaction Format");
						setReplaceAllError("Invalid Reaction Format");
						return false;
					} else {
						LocalConfig.getInstance().pastedReaction = true;
						int id = (Integer.valueOf((String) reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.DB_REACTIONS_ID_COLUMN)));	
						ReactionsUpdater updater = new ReactionsUpdater();
						updater.updateReactionEquations(id, getOldReaction(), value, LocalConfig.getInstance().getLoadedDatabase());
						return true;
					}				
				} else {
					setPasteError("Invalid Reaction Format");
					setReplaceAllError("Invalid Reaction Format");
					return false;
				}
			}
		}
		return true;
		 
	}
		
	public void reactionsClear() {
		int startRow=(reactionsTable.getSelectedRows())[0]; 
		int startCol=(reactionsTable.getSelectedColumns())[0];
		copyReactionsDatabaseTables();
		ReactionUndoItem undoItem = createReactionUndoItem("", "", startRow, startCol, 1, UndoConstants.CLEAR_CONTENTS, UndoConstants.REACTION_UNDO_ITEM_TYPE);
		undoItem.setTableCopyIndex(LocalConfig.getInstance().getNumReactionTablesCopied());		
		boolean valid = true;
		//TODO: add if column is reactionEqunAbbrs add to oldReactionsList
		ReactionsUpdater updater = new ReactionsUpdater();
		ArrayList<Integer> rowList = new ArrayList<Integer>();
		ArrayList<Integer> reacIdList = new ArrayList<Integer>();
		ArrayList<String> oldReactionsList = new ArrayList<String>();
				
		for (int r = 0; r < reactionsTable.getSelectedRows().length; r++) {
			int row = reactionsTable.convertRowIndexToModel(startRow + r);
			rowList.add(row);
			int reacId = Integer.valueOf((String) reactionsTable.getModel().getValueAt(row, 0));
			reacIdList.add(reacId);
			String oldReaction = (String) reactionsTable.getModel().getValueAt(row, GraphicalInterfaceConstants.REACTION_EQUN_ABBR_COLUMN);
			oldReactionsList.add(oldReaction);
		}
		// check if columns that require values will be cleared
		for(int j=0; j < reactionsTable.getSelectedColumns().length ;j++) { 
			if (startCol + j == GraphicalInterfaceConstants.KO_COLUMN || startCol + j == GraphicalInterfaceConstants.FLUX_VALUE_COLUMN || 
					startCol + j == GraphicalInterfaceConstants.LOWER_BOUND_COLUMN || startCol + j == GraphicalInterfaceConstants.UPPER_BOUND_COLUMN
					|| startCol + j == GraphicalInterfaceConstants.BIOLOGICAL_OBJECTIVE_COLUMN || startCol + j == GraphicalInterfaceConstants.REVERSIBLE_COLUMN ||
					startCol + j == GraphicalInterfaceConstants.SYNTHETIC_OBJECTIVE_COLUMN) {
				valid = false;
			}
		}
		if (valid) {
			for(int i=0; i < reactionsTable.getSelectedRows().length ;i++) { 
				for(int j=0; j < reactionsTable.getSelectedColumns().length ;j++) { 
					int viewRow = reactionsTable.convertRowIndexToView(rowList.get(i));
					reactionsTable.setValueAt(" ", viewRow, startCol + j);				
				} 
			}
			updater.updateReactionRows(rowList, reacIdList, oldReactionsList, LocalConfig.getInstance().getLoadedDatabase());	
			copyReactionsDatabaseTables(); 
			setUpReactionsUndo(undoItem);
		} else {
			JOptionPane.showMessageDialog(null,                
					GraphicalInterfaceConstants.CLEAR_ERROR_MESSAGE,                
					"Clear Error",                                
					JOptionPane.ERROR_MESSAGE);
			deleteReactionsPasteUndoItem();
		}
	}

	public void reactionsDeleteRows() {
		copyReactionsDatabaseTables();
		int rowIndexStart = reactionsTable.getSelectedRow();
		int rowIndexEnd = reactionsTable.getSelectionModel().getMaxSelectionIndex();
		ArrayList<Integer> deleteIds = new ArrayList<Integer>();
		ArrayList<String> deleteAbbreviations = new ArrayList<String>();
		ArrayList<String> deletedReactions = new ArrayList<String>();
		for (int r = rowIndexStart; r <= rowIndexEnd; r++) {
			int viewRow = reactionsTable.convertRowIndexToModel(r);
			int id = (Integer.valueOf((String) reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.DB_REACTIONS_ID_COLUMN)));
			deleteIds.add(id);
			String reactionString = (String) reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTION_EQUN_ABBR_COLUMN);
			deletedReactions.add(reactionString);			
		}
		ReactionUndoItem undoItem = createReactionUndoItem("", "", rowIndexStart, reactionsTable.getSelectedColumn(), deleteIds.get(0), UndoConstants.DELETE_ROW, UndoConstants.REACTION_UNDO_ITEM_TYPE);
		undoItem.setTableCopyIndex(LocalConfig.getInstance().getNumReactionTablesCopied());
		ReactionsUpdater updater = new ReactionsUpdater();
		updater.deleteRows(deleteIds, deletedReactions, LocalConfig.getInstance().getLoadedDatabase());
		copyReactionsDatabaseTables();
		setUpReactionsUndo(undoItem);
		
		closeConnection();
		reloadTables(LocalConfig.getInstance().getLoadedDatabase());
	}
	
	/**************************************************************************/
	//end reactionsTable context menu methods
	/**************************************************************************/

	/**************************************************************************/
	//metabolitesTable context menu methods
	/**************************************************************************/
	
	public void selectMetabolitesRows() {
		setClipboardContents("");
		MetabolitesMetaColumnManager metabolitesMetaColumnManager = new MetabolitesMetaColumnManager();
		int metaColumnCount = metabolitesMetaColumnManager.getMetaColumnCount(LocalConfig.getInstance().getLoadedDatabase());	

		StringBuffer sbf=new StringBuffer();
		int numrows = metabolitesTable.getSelectedRowCount(); 
		LocalConfig.getInstance().setNumberCopiedRows(numrows);
		//for row selection all columns are selected except id column(hidden)
		LocalConfig.getInstance().setNumberCopiedColumns(metabolitesTable.getColumnCount() - 1);
		int[] rowsselected=metabolitesTable.getSelectedRows();  
		
		metabolitesTable.changeSelection(rowsselected[0], 1, false, false);
		metabolitesTable.changeSelection(rowsselected[numrows - 1], metabolitesTable.getColumnCount(), false, true);
		metabolitesTable.scrollColumnToVisible(1);
		
		if (includeMtbColumnNames == true) {
			//add column names to clipboard
			for (int c = 1; c < GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES.length; c++) {
				if (getVisibleMetabolitesColumns().contains(c)) {
					sbf.append(GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES[c]);
					if (c < GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES.length - 1) {
						sbf.append("\t"); 
					}
				}				
			}
			if (metaColumnCount > 0) {
				for (int r = 1; r <= metaColumnCount; r++) {
					if (getVisibleMetabolitesColumns().contains(GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES.length + r - 1)) {
						sbf.append("\t");
						sbf.append(metabolitesMetaColumnManager.getColumnName(LocalConfig.getInstance().getLoadedDatabase(), r));
					}					
				}
			}
			sbf.append("\n");
		}
		
		for (int i = 0; i < numrows; i++) {
			//starts at 1 to avoid reading hidden db id column 
			for (int j = 1; j < metabolitesTable.getColumnCount() - 1; j++) 
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
		//System.out.println(sbf.toString());
	}
	
	public void metabolitesCopy() {
		int numcols=metabolitesTable.getSelectedColumnCount(); 
		int numrows=metabolitesTable.getSelectedRowCount(); 
		LocalConfig.getInstance().setNumberCopiedRows(numrows);
		LocalConfig.getInstance().setNumberCopiedColumns(numcols);
		int[] rowsselected=metabolitesTable.getSelectedRows(); 
		int[] colsselected=metabolitesTable.getSelectedColumns(); 
		
		// Clipboard already contains correct values for select all and include column names, do not change 
		if (rowsselected.length == metabolitesTable.getRowCount() && colsselected.length == metabolitesTable.getColumnCount() && includeMtbColumnNames) {
			
		} else {
			if (getSelectionMode() == 2) {
				if (getSelectionMode() == 2) {
					includeMtbColumnNames = false;				
				}
				selectMetabolitesRows();
			} else {
				StringBuffer sbf=new StringBuffer(); 
				ListSelectionModel selectionModel = metabolitesTable.getSelectionModel();
				if (getSelectionMode() == 1) {
		        	//sets columns as selected    		
		    		selectionModel.setSelectionInterval(0, metabolitesTable.getModel().getRowCount() - 1);
				} else if (getSelectionMode() == 2) {
					
				}				
				// Check to ensure we have selected only a contiguous block of cells 
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
				for (int i=0;i<numrows;i++) { 
					for (int j=0;j<numcols;j++) { 
						if (getVisibleMetabolitesColumns().contains(colsselected[j])) {
							if (metabolitesTable.getValueAt(rowsselected[i],colsselected[j]) != null) {
								sbf.append(metabolitesTable.getValueAt(rowsselected[i],colsselected[j]));
							} else {
								sbf.append(" ");
							}
							if (j<numcols-1) sbf.append("\t"); 
						}					
					} 
					sbf.append("\n");		 
				}  
				setClipboardContents(sbf.toString());
				//System.out.println(sbf.toString());
			}		
		}		
	}

	public void metabolitesPaste() {
		showErrorMessage = true;
		MetabolitesUpdater updater = new MetabolitesUpdater();
		ArrayList<Integer> rowList = new ArrayList<Integer>();
		ArrayList<Integer> metabIdList = new ArrayList<Integer>();
		String copiedString = getClipboardContents(GraphicalInterface.this);
		String[] s1 = copiedString.split("\n");
		int startRow = (metabolitesTable.getSelectedRows())[0];
		int startCol = (metabolitesTable.getSelectedColumns())[0];
		// if entire rows or columns copied and paste position is not at first
		// cell in row or column, error thrown since selection will not fit in area
		if (getSelectionMode() == 1 && startRow != 0) {
			JOptionPane.showMessageDialog(null,                
					GraphicalInterfaceConstants.PASTE_AREA_ERROR,                
					"Paste Error",                                
					JOptionPane.ERROR_MESSAGE);
		} else if (getSelectionMode() == 2 && startCol != 1) {
			JOptionPane.showMessageDialog(null,                
					GraphicalInterfaceConstants.PASTE_AREA_ERROR,                
					"Paste Error",                                
					JOptionPane.ERROR_MESSAGE);
		} else {
			// save old data before paste
			copyMetaboliteDatabaseTable();
			MetaboliteUndoItem undoItem = createMetaboliteUndoItem("", "", startRow, startCol, 1, UndoConstants.PASTE, UndoConstants.METABOLITE_UNDO_ITEM_TYPE);
			undoItem.setTableCopyIndex(LocalConfig.getInstance().getNumMetabolitesTableCopied());
			setUndoOldCollections(undoItem);			
			if (LocalConfig.getInstance().getNumberCopiedRows() != null && metabolitesTable.getSelectedRows().length >= LocalConfig.getInstance().getNumberCopiedRows()) {
				if (LocalConfig.getInstance().getNumberCopiedRows() > 0) {
					//if selected area is larger than copied area, it will fill the same cell
					//contents repeatedly until end of selection, based on integer division
					int quotient = metabolitesTable.getSelectedRows().length/LocalConfig.getInstance().getNumberCopiedRows();
					int remainder = metabolitesTable.getSelectedRows().length%LocalConfig.getInstance().getNumberCopiedRows();
					for (int q = 0; q < quotient; q++) {	
						//there are two for loops since when pasting into a sorted column the column  
						//sorts itself for each value inserted into table, causing row numbers to change 
						//and erroneous results. making lists of row numbers and db id's, then inserting 
						//values into table and db based on these lists solves this problem
						for (int r = 0; r < LocalConfig.getInstance().getNumberCopiedRows(); r++) {
							int row = metabolitesTable.convertRowIndexToModel(startRow + r);
							rowList.add(row);
							int metabId = Integer.valueOf((String) metabolitesTable.getModel().getValueAt(row, 0));
							metabIdList.add(metabId);
						}
						pasteMetaboliteRows(rowList, metabIdList, s1, startCol, q, LocalConfig.getInstance().getNumberCopiedRows(), s1.length);
						startRow += LocalConfig.getInstance().getNumberCopiedRows();
					}
					for (int m = 0; m < remainder; m++) {
						int row = metabolitesTable.convertRowIndexToModel(startRow + m);
						rowList.add(row);
						int metabId = Integer.valueOf((String) metabolitesTable.getModel().getValueAt(row, 0));
						metabIdList.add(metabId);
					}
					// remainder of s1 could be larger, use smaller value to avoid
					// array index error
					int min = 0;
					if (s1.length < remainder) {
						min = s1.length;
					} else {
						min = remainder;
					}
					// if remainder is larger, have to fill in difference with ""
					pasteMetaboliteRows(rowList, metabIdList, s1, startCol, quotient, LocalConfig.getInstance().getNumberCopiedRows(), min);
					if (validPaste) {						
						updater.updateMetaboliteRows(rowList, metabIdList, LocalConfig.getInstance().getLoadedDatabase());
						copyMetaboliteDatabaseTable();
						setUpMetabolitesUndo(undoItem);
					} else {
						if (showErrorMessage = true) {
							JOptionPane.showMessageDialog(null,                
									getPasteError(),                
									"Paste Error",                                
									JOptionPane.ERROR_MESSAGE);
						}
						deleteMetabolitesPasteUndoItem();
						validPaste = true;
					}					
				}
				//if selected area is smaller than copied area, fills in copied area
				//from first selected cell as upper left
			} else {
				for (int r = 0; r < LocalConfig.getInstance().getNumberCopiedRows(); r++) {
					int row = metabolitesTable.convertRowIndexToModel(startRow + r);
					rowList.add(row);
					int metabId = Integer.valueOf((String) metabolitesTable.getModel().getValueAt(row, 0));
					metabIdList.add(metabId);
				}
				pasteMetaboliteRows(rowList, metabIdList, s1, startCol, 0, LocalConfig.getInstance().getNumberCopiedRows(), s1.length);
				if (validPaste) {						
					updater.updateMetaboliteRows(rowList, metabIdList, LocalConfig.getInstance().getLoadedDatabase());
					copyMetaboliteDatabaseTable();
					setUpMetabolitesUndo(undoItem);
				} else {
					if (showErrorMessage = true) {
						JOptionPane.showMessageDialog(null,                
								getPasteError(),                
								"Paste Error",                                
								JOptionPane.ERROR_MESSAGE);
					}	
					deleteMetabolitesPasteUndoItem();
					validPaste = true;
				}	
			}
		}
		showDuplicatePrompt = true;
		duplicateMetabOK = true;
	}
	
	// used for invalid paste, invalid clear, and invalid replace all
	public void deleteMetabolitesPasteUndoItem() {
		DatabaseCreator creator = new DatabaseCreator();
		int numCopied = LocalConfig.getInstance().getNumMetabolitesTableCopied();	
		creator.deleteTable(LocalConfig.getInstance().getLoadedDatabase(), "metabolites" + tableCopySuffix(numCopied));
		numCopied -= 1;
		LocalConfig.getInstance().setNumMetabolitesTableCopied(numCopied);
	}
	
	public void pasteMetaboliteRows(ArrayList<Integer> rowList, ArrayList<Integer> metabIdList, String[] s1, int startCol, int multiplier, int numCopiedRows, int range) {
		// multiplier is used if number of selected paste rows is greater than
		// number of copied rows, so that start rows is incremented by number of
		// selected rows each loop - see quotient in metabolitesPaste()
		ArrayList<Integer> pasteColumns = new ArrayList<Integer>();
		// if copied row last entry is blank, split truncates selection, length will
		// be less than getNumberCopiedRows and throw ArrayOutOfBoundsError
		int diff = 0;  // if s1 length is less than getNumberCopiedRows(), will be assigned value
		if (s1.length < numCopiedRows) {
			diff = numCopiedRows - s1.length;
		}
		int count = 0;
		int p = 0;// number of pasted columns
		if (duplicateMetabOK) {
			while (p < LocalConfig.getInstance().getNumberCopiedColumns() && (startCol + p + count) < metabolitesTable.getColumnCount()) {			
				if (!getVisibleMetabolitesColumns().contains(startCol + p + count)) {
					count += 1;
				} else {
					pasteColumns.add(startCol + p + count);
					p += 1;
				}
			}
			for (int r = 0; r < range; r++) {	
				int viewRow = metabolitesTable.convertRowIndexToView(rowList.get(multiplier * numCopiedRows + r));			
				String[] rowstring = s1[r].split("\t");
				for (int c = 0; c < pasteColumns.size(); c++) {
					if (c < rowstring.length) {		
						updateMetabolitesCellIfPasteValid(pasteColumns.get(c), viewRow, rowstring[c]);
					} else {
						metabolitesTable.setValueAt(" ", viewRow, pasteColumns.get(c));
					}				
				}
			}
			if (diff > 0) {
				for (int r = s1.length; r < numCopiedRows; r++) {
					int viewRow = metabolitesTable.convertRowIndexToView(rowList.get(multiplier * numCopiedRows + r));
					for (int c = 0; c < pasteColumns.size(); c++) {
						updateMetabolitesCellIfPasteValid(pasteColumns.get(c), viewRow, "");			
					}
				}
			}
		}		
	}
	
	public void updateMetabolitesCellIfPasteValid(int col, int row, String value) {
		int id = Integer.valueOf((String) metabolitesTable.getModel().getValueAt(row, GraphicalInterfaceConstants.DB_METABOLITE_ID_COLUMN));		
		String metabAbbrev = (String) metabolitesTable.getModel().getValueAt(row, GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN);
		if (col == GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN) {
			if (LocalConfig.getInstance().getMetaboliteIdNameMap().containsKey(value)) {
				if (showDuplicatePrompt) {
					Object[] options = {"    Yes    ", "    No    ",};
					int choice = JOptionPane.showOptionDialog(null, 
							GraphicalInterfaceConstants.DUPLICATE_METABOLITE_MESSAGE, 
							GraphicalInterfaceConstants.DUPLICATE_METABOLITE_TITLE, 
							JOptionPane.YES_NO_OPTION, 
							JOptionPane.QUESTION_MESSAGE, 
							null, options, options[0]);
					if (choice == JOptionPane.YES_OPTION) {	//find here
						metabolitesTable.setValueAt(value, row, col);
						if (!LocalConfig.getInstance().getDuplicateIds().contains(id)) {
							LocalConfig.getInstance().getDuplicateIds().add(id);
						}
						LocalConfig.getInstance().getMetaboliteIdNameMap().remove(metabAbbrev);
						showDuplicatePrompt = false;
					}
					if (choice == JOptionPane.NO_OPTION) {
						showDuplicatePrompt = false;
						duplicateMetabOK = false;
						//validPaste = false;
					}
				} else {
					metabolitesTable.setValueAt(value, row, col);
					if (!LocalConfig.getInstance().getDuplicateIds().contains(id)) {
						LocalConfig.getInstance().getDuplicateIds().add(id);
					}
					LocalConfig.getInstance().getMetaboliteIdNameMap().remove(metabAbbrev);
				}
			} else {
				metabolitesTable.setValueAt(value, row, col);
				LocalConfig.getInstance().getMetaboliteIdNameMap().put(value, id);
			}
		} else if (isMetabolitesEntryValid(col, value)) {
			metabolitesTable.setValueAt(value, row, col);
		} else {
			validPaste = false;
		}	
	}

	public boolean isMetabolitesEntryValid(int columnIndex, String value) {
		if (columnIndex == GraphicalInterfaceConstants.CHARGE_COLUMN) {
			if (value != null && value.trim().length() > 0) {
				EntryValidator validator = new EntryValidator();
				if (!validator.isNumber(value)) {
					setPasteError("Number format exception");
			    	setReplaceAllError("Number format exception");
			        return false;
				}	           
			}			
		} else if (columnIndex == GraphicalInterfaceConstants.BOUNDARY_COLUMN) {
			if (value.compareTo("true") == 0 || value.compareTo("false") == 0) {
				return true;
			} else {
				setPasteError(GraphicalInterfaceConstants.INVALID_PASTE_BOOLEAN_VALUE);
				setReplaceAllError(GraphicalInterfaceConstants.INVALID_REPLACE_ALL_BOOLEAN_VALUE);
				return false;
			}
		} 	
		return true;
		 
	}
	
	public void metabolitesClear() {
		int startRow=(metabolitesTable.getSelectedRows())[0]; 
		int startCol=(metabolitesTable.getSelectedColumns())[0];
		copyMetaboliteDatabaseTable();
		MetaboliteUndoItem undoItem = createMetaboliteUndoItem("", "", startRow, startCol, 1, UndoConstants.CLEAR_CONTENTS, UndoConstants.METABOLITE_UNDO_ITEM_TYPE);
		undoItem.setTableCopyIndex(LocalConfig.getInstance().getNumMetabolitesTableCopied());
		setUndoOldCollections(undoItem);		
		boolean valid = true;
		MetabolitesUpdater updater = new MetabolitesUpdater();
		ArrayList<Integer> rowList = new ArrayList<Integer>();
		ArrayList<Integer> metabIdList = new ArrayList<Integer>();
		//TODO: Clear must throw an error if user attempts to clear
		//a used metabolite, but should be able to clear an unused
		//metabolite - see delete, also should not be able to clear boundary		
		if (startCol == GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN || 
				startCol == GraphicalInterfaceConstants.METABOLITE_NAME_COLUMN) {
			JOptionPane.showMessageDialog(null,                
					"Cannot clear Metab abbreviation or name column, some may be used.",                
					"Clear Error",                                
					JOptionPane.ERROR_MESSAGE);
			valid = false;
		} else {			
			for (int r = 0; r < metabolitesTable.getSelectedColumns().length; r++) {
				if (startCol + r == GraphicalInterfaceConstants.BOUNDARY_COLUMN) {
					valid = false;
				} 			
			}
			if (valid) {
				for (int r = 0; r < metabolitesTable.getSelectedRows().length; r++) {
					int row = metabolitesTable.convertRowIndexToModel(startRow + r);
					rowList.add(row);
					int metabId = Integer.valueOf((String) metabolitesTable.getModel().getValueAt(row, 0));
					metabIdList.add(metabId);
				}
				for(int i=0; i < metabolitesTable.getSelectedRows().length ;i++) { 
					for(int j=0; j < metabolitesTable.getSelectedColumns().length ;j++) { 					
						int viewRow = metabolitesTable.convertRowIndexToView(rowList.get(i));
						metabolitesTable.setValueAt(" ", viewRow, startCol + j);
					} 
				}
				updater.updateMetaboliteRows(rowList, metabIdList, LocalConfig.getInstance().getLoadedDatabase());				
				copyMetaboliteDatabaseTable();
				setUpMetabolitesUndo(undoItem);
			} else {
				JOptionPane.showMessageDialog(null,                
						GraphicalInterfaceConstants.CLEAR_ERROR_MESSAGE,                
						"Clear Error",                                
						JOptionPane.ERROR_MESSAGE);
				deleteMetabolitesPasteUndoItem();
			}			
		} 
	}

	public void metaboliteDeleteRows() {
		copyMetaboliteDatabaseTable();
		int rowIndexStart = metabolitesTable.getSelectedRow();
		int rowIndexEnd = metabolitesTable.getSelectionModel().getMaxSelectionIndex();
		ArrayList<Integer> deleteIds = new ArrayList<Integer>();
		boolean participant = false;
		for (int r = rowIndexStart; r <= rowIndexEnd; r++) {
			int viewRow = metabolitesTable.convertRowIndexToModel(r);
			String key = (String) metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN);
			int id = (Integer.valueOf((String) metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.DB_METABOLITE_ID_COLUMN)));
			// TODO use this same method for clear
			if (LocalConfig.getInstance().getMetaboliteUsedMap().containsKey(key) && !(LocalConfig.getInstance().getDuplicateIds().contains(id))) {
				if (!participant) {
					JOptionPane.showMessageDialog(null,                
							GraphicalInterfaceConstants.PARTICIPATING_METAB_ERROR_MESSAGE,
							GraphicalInterfaceConstants.PARTICIPATING_METAB_ERROR_TITLE,                                
							JOptionPane.ERROR_MESSAGE);
				}
				// may want to print to output pane if used as console
				//System.out.println(key + " cannot be deleted since it participates in one or more reactions.");
				// participating metabolite in selected rows
				participant = true; // prevents message from being displayed multiple times
			} else {
				LocalConfig.getInstance().getMetaboliteIdNameMap().remove(key);	
				deleteIds.add(id);
			}
		}
		MetaboliteUndoItem undoItem = createMetaboliteUndoItem("", "", rowIndexStart, 1, deleteIds.get(0), UndoConstants.DELETE_ROW, UndoConstants.METABOLITE_UNDO_ITEM_TYPE);
		undoItem.setTableCopyIndex(LocalConfig.getInstance().getNumMetabolitesTableCopied());
		setUndoOldCollections(undoItem);
		MetabolitesUpdater updater = new MetabolitesUpdater();
		updater.deleteRows(deleteIds, LocalConfig.getInstance().getLoadedDatabase());
		copyMetaboliteDatabaseTable(); 
		setUpMetabolitesUndo(undoItem);
		closeConnection();
		reloadTables(LocalConfig.getInstance().getLoadedDatabase());
	} 
	
	/**************************************************************************/
	//end metabolitesTable context menu methods
	/**************************************************************************/
	
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
				doc = reader.readSBML(getSBMLFile());
				SBMLModelReader modelReader = new SBMLModelReader(doc);
				modelReader.setDatabaseName(LocalConfig.getInstance().getDatabaseName());
				modelReader.load();
			} catch (FileNotFoundException e) {				
				JOptionPane.showMessageDialog(null,                
						"File does not exist.",                
						"File does not exist.",                                
						JOptionPane.ERROR_MESSAGE);					
				//e.printStackTrace();					
				progress = 100;
				progressBar.setVisible(false);
				modelCollectionOKButtonClicked = false;
			} catch (XMLStreamException e) {
				JOptionPane.showMessageDialog(null,                
						"This File is not a Valid SBML File.",                
						"Invalid SBML File.",                                
						JOptionPane.ERROR_MESSAGE);
				// TODO Auto-generated catch block
				//e.printStackTrace();
				progress = 100;
				progressBar.setVisible(false);
				modelCollectionOKButtonClicked = false;
			}	
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
	
	class GDBBTask extends SwingWorker<Void, Solution> {
		private GDBB gdbb;
		private GDBBModel model;
		private ReactionFactory rFactory;
		private Vector<String> uniqueGeneAssociations;
		private int knockoutOffset;
		private Writer writer;
		private StringBuffer outputText;
		private DatabaseCopier copier;
//		private MemoryDatabaseCopier copier;
		private ArrayList<Double> soln;
		private String dateTimeStamp;
		private String optimizePath;
		
		GDBBTask() {
			model = new GDBBModel(getDatabaseName());
		}
		
        @Override
        protected Void doInBackground() {
        	copier = new DatabaseCopier();
//        	copier = new MemoryDatabaseCopier();
        	rFactory = new ReactionFactory("SBML", getOptimizePath());
			uniqueGeneAssociations = rFactory.getUniqueGeneAssociations();
			
			outputText = new StringBuffer();
			
//			log.debug("create an optimize");
			gdbb = new GDBB();
			GDBB.intermediateSolution.clear();
			
			gdbb.setGDBBModel(model);
			gdbb.start();
			
			knockoutOffset = 4*model.getNumReactions() + model.getNumMetabolites();
			
			soln = new ArrayList<Double>();
			Format formatter;
			formatter = new SimpleDateFormat("_yyMMdd_HHmmss");
			Solution solution;
			
//			String oldOptimizaePath = getDatabaseName();
//			copier.copyDatabase(oldOptimizaePath, optimizePath, true);
			
	        while (gdbb.isAlive() || GDBB.intermediateSolution.size() > 0) {
	            if (GDBB.intermediateSolution.size() > 0) {
					dateTimeStamp = formatter.format(new Date());
					optimizePath = GraphicalInterfaceConstants.OPTIMIZATION_PREFIX + getDatabaseName() + dateTimeStamp;
					copier.copyDatabase(getDatabaseName(), optimizePath);
//					copier.copyDatabase(getDatabaseName(), optimizePath, false);
					listModel.addElement(GraphicalInterfaceConstants.OPTIMIZATION_PREFIX
							+ (getDatabaseName().substring(getDatabaseName().lastIndexOf("\\") + 1) + dateTimeStamp));				
					LocalConfig.getInstance().getOptimizationFilesList().add(optimizePath);
//					setOptimizePath(optimizePath);
					
					// need to lock if process is busy
					solution = GDBB.intermediateSolution.poll();
					solution.setDatabaseName(optimizePath);
	            	publish(solution);
	            }
	        }
            return null;
        }

        public GDBB getGdbb() {
			return gdbb;
		}

		public GDBBModel getModel() {
			return model;
		}

		public void setModel(GDBBModel model) {
			this.model = model;
		}

		@Override
        protected void process(List<Solution> solutions) {
			Solution solution = solutions.get(solutions.size() - 1);
			double[] x = solution.getKnockoutVector();
			double objectiveValue = solution.getObjectiveValue();
			
			String kString = "";
			soln.clear();
			for (int j = 0; j < x.length; j++) {
				soln.add(x[j]);
				if ((j >= knockoutOffset) && (x[j] >= 0.5)) {	// compiler optimizes: boolean short circuiting
					kString += "\n\t" + uniqueGeneAssociations.elementAt(j - knockoutOffset);
				}
			}
			
			rFactory = new ReactionFactory("SBML", solution.getDatabaseName());
			rFactory.setFluxes(new ArrayList<Double>(soln.subList(0, model.getNumReactions())));
			rFactory.setKnockouts(soln.subList(knockoutOffset, soln.size()));
			
			DynamicTreeDemo.treePanel.addObject((DefaultMutableTreeNode)DynamicTreeDemo.treePanel.getRootNode().getChildAt(DynamicTreeDemo.treePanel.getRootNode().getChildCount() - 1), solution, true);
			GraphicalInterface.outputTextArea.append("\n\n" + model.getNumMetabolites() + " metabolites, " + model.getNumReactions() + " reactions, " + model.getNumGeneAssociations() + " unique gene associations\n" + "Maximum synthetic objective: " + objectiveValue + "\nKnockouts:" + kString);
			DynamicTreeDemo.treePanel.setNodeSelected(DynamicTreeDemo.treePanel.getRootNode().getChildCount() - 1);
        }
        
        @Override
        protected void done() {
//        	System.out.println("GDBB is done!");
        	soln = gdbb.getSolution();
			
			log.debug("optimization complete");
			
			textInput.enableStart();
			textInput.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			
			writer = null;
			try {
//				outputText.append(getDatabaseName() + "\n");
				outputText.append(model.getNumMetabolites() + " metabolites, " + model.getNumReactions() + " reactions, " + model.getNumGeneAssociations() + " unique gene associations\n");
				outputText.append("Maximum synthetic objective: "	+ gdbb.getMaxObj() + "\n");
				outputText.append("knockouts: \n");
				
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
//			loadOutputPane(getOptimizePath() + ".log");
			if (getPopout() != null) {
				getPopout().load(getOptimizePath() + ".log");
			}				
			
			closeConnection();
			reloadTables(getOptimizePath());
			/*
			String fileString = "jdbc:sqlite:" + getOptimizePath() + ".db";
			LocalConfig.getInstance().setLoadedDatabase(getOptimizePath());
			try {
				Class.forName("org.sqlite.JDBC");
				Connection con = DriverManager.getConnection(fileString);
				LocalConfig.getInstance().setCurrentConnection(con);
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
			*/
			//fileList.setSelectedIndex(listModel.size() - 1);
			//	Reset GDBB Dialog
        }   
    }
	
	class TimeListener implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			if (LocalConfig.getInstance().getProgress() > 0) {
				progressBar.progress.setIndeterminate(false);
			}			
			progressBar.progress.setValue(LocalConfig.getInstance().getProgress());
			progressBar.progress.repaint();
			if (LocalConfig.getInstance().getProgress() == 100) {
				if (LocalConfig.getInstance().hasMetabolitesFile || !isCSVFile) {
					setUpTables();
					modelCollectionOKButtonClicked = false;
					DynamicTreeDemo.treePanel.clear();
					DynamicTreeDemo.treePanel.addObject(new Solution(GraphicalInterface.listModel.get(GraphicalInterface.listModel.getSize() - 1), getDatabaseName()));
					DynamicTreeDemo.treePanel.setNodeSelected(GraphicalInterface.listModel.getSize() - 1);
					progressBar.setVisible(false);
				}				
				timer.stop();
				// This appears redundant, but is the only way to not have an extra progress bar on screen
				progressBar.setVisible(false);
				progressBar.progress.setIndeterminate(true);
				if (isCSVFile && LocalConfig.getInstance().hasReactionsFile) {
					LocalConfig.getInstance().setProgress(0);
					progressBar.progress.setIndeterminate(true);
					//fileList.setSelectedIndex(-1);
					listModel.clear();
					//fileList.setModel(listModel);  					
					closeConnection();
					String fileString = "jdbc:sqlite:" + getDatabaseName() + ".db";
					String filename = LocalConfig.getInstance().getDatabaseName();
					try {						
						Class.forName("org.sqlite.JDBC");
						Connection con = DriverManager.getConnection(fileString);
						LocalConfig.getInstance().setCurrentConnection(con);

						LocalConfig.getInstance().setReactionsNextRowCorrection(0);

						TextReactionsModelReader reader = new TextReactionsModelReader();			    
						ArrayList<String> columnNamesFromFile = reader.columnNamesFromFile(LocalConfig.getInstance().getReactionsCSVFile(), 0);	
						ReactionColumnNameInterface columnNameInterface = new ReactionColumnNameInterface(con, columnNamesFromFile);
						setReactionColumnNameInterface(columnNameInterface);
						getReactionColumnNameInterface().setIconImages(icons);					
						getReactionColumnNameInterface().setSize(600, 600);
						getReactionColumnNameInterface().setResizable(false);
						getReactionColumnNameInterface().setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
						getReactionColumnNameInterface().setLocationRelativeTo(null);											
						getReactionColumnNameInterface().addWindowListener(new WindowAdapter() {
					        public void windowClosing(WindowEvent evt) {
					        	getReactionColumnNameInterface().setVisible(false);
					        	getReactionColumnNameInterface().dispose();
					        	if (LocalConfig.getInstance().hasMetabolitesFile) {
					        		csvReactionCancelLoadAction();
						        } else {
						        	clearTables();
						        }        	
					        }
						});
						getReactionColumnNameInterface().cancelButton.addActionListener(cancelButtonCSVReacLoadActionListener);
						getReactionColumnNameInterface().setModal(true);
						getReactionColumnNameInterface().setVisible(true);					
						// sets value to default and loads any new metabolites
						// from reactions file into metabolites table
						//LocalConfig.getInstance().hasMetabolitesFile = true;
						if (!reactionCancelLoad) {
							timer.start();
						} else {
							if (!LocalConfig.getInstance().hasMetabolitesFile) {
					        	clearTables();
					        } else {
								listModel.clear();
								listModel.addElement(filename);
						        //fileList.setModel(listModel);
						        LocalConfig.getInstance().setDatabaseName(filename);
						        LocalConfig.getInstance().setLoadedDatabase(filename);
						        reloadTables(filename);
					        }							
						}
						reactionCancelLoad = false;
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					isCSVFile = false;   // set to default value, also stops reactions from loading again
				}
			}
		}
	}

	ActionListener cancelButtonCSVReacLoadActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent ae) {
			csvReactionCancelLoadAction();
		}
	};
	
	public void csvReactionCancelLoadAction() {
		reactionCancelLoad = true;
		LocalConfig.getInstance().setProgress(100);
		progressBar.setVisible(false);
	}
	
	/*******************************************************************************/
	//end progressBar methods
	/*******************************************************************************/
	
	/*******************************************************************************/
	//find/replace methods
	/******************************************************************************/
	
	public void showFindReplace() {
		LocalConfig.getInstance().setReactionsLocationsListCount(0);
		LocalConfig.getInstance().setMetabolitesLocationsListCount(0);
		LocalConfig.getInstance().findFieldChanged = false;
		FindReplaceDialog findReplace = new FindReplaceDialog();
		setFindReplaceDialog(findReplace);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		
		int x = (screenSize.width - findReplace.getSize().width)/2;
		int y = (screenSize.height - findReplace.getSize().height)/2;
		
		findReplace.setIconImages(icons);
		findReplace.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		findReplace.setAlwaysOnTop(true);        
		//findReplace.setLocation(x + 420, y);
		// Find/Replace positioned at far right on screen so it does not obscure scroll bar
        findReplace.setLocation((screenSize.width - findReplace.getSize().width) - 10, y);
        findReplace.setVisible(true);
        findReplace.addWindowListener(new WindowAdapter() {
	        public void windowClosing(WindowEvent evt) {
	        	findReplaceCloseAction();
				getFindReplaceDialog().setVisible(false);
				getFindReplaceDialog().dispose();
	        }
		});
        findButtonReactionsClicked = false;
        findButtonMetabolitesClicked = false;
        // ensure states of boolean values match states of findReplace frame
        searchBackwards = false;
        matchCase = false;
        wrapAround = false;
        searchSelectedArea = false;
        findMode = true;
	}	
	
	// start reactions find replace
	
	public int reactionsFindStartIndex() {
		// always start at 0 when in column or row selection mode
		int startIndex = 0;
		// start from selected cell
		if (!searchSelectedArea) {
			if (reactionsTable.getSelectedRow() > -1 && reactionsTable.getSelectedColumn() > -1) {
				int row = reactionsTable.getSelectedRow();
				int col = reactionsTable.getSelectedColumn();
				startIndex = findStartIndex(row, col, getReactionsFindLocationsList());
			}				
		} else {
			if (searchBackwards) {
				startIndex = getReactionsFindLocationsList().size() - 1;
			} 		
		}
		return startIndex;
		
	}
	
	public int findStartIndex(int row, int col, ArrayList<ArrayList<Integer>> locationList) {
		int startIndex = 0;
		boolean sameCell = false;
		boolean sameRow = false;
		boolean rowGreater = false;			
		for (int i = 0; i < locationList.size(); i++) {					
			if (locationList.get(i).get(0) == row) { 
				if (locationList.get(i).get(1) == col) {
					sameCell = true;
				}
				if (locationList.get(i).get(1) > col) {	
					if (!sameRow) {
						startIndex = i;
					}
					sameRow = true;
				}
			} else if (!sameRow && locationList.get(i).get(0) > row) { 
				if (!rowGreater) {
					startIndex = i;
				}
				rowGreater = true;
			} 
		}			
		if (rowGreater || sameRow) {
			
		// if string not found after selected cell
		} else {
			if (wrapAround) {
			    startIndex = 0;
			} else {
				startIndex = -1;
			}				
		}
		// if search backwards, index will be 1 before than selected cell not 1 after
		if (searchBackwards) {
			if (sameCell) {
				startIndex -= 2;
			} else {
				startIndex -= 1;
			}				
		}
		if (startIndex == -1 && wrapAround) {
			startIndex = locationList.size() - 1;
		}		
		return startIndex;
		
	}
	
	ActionListener findReactionsButtonActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent ae) {
			if (tabbedPane.getSelectedIndex() == 0) {
				reactionsFindAction();
			}
			findButtonReactionsClicked = true;
		}
	};
	
	ActionListener replaceFindReactionsButtonActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent ae) {
			if (tabbedPane.getSelectedIndex() == 0) {
				reactionsFindAll = false;
				reactionsReplace();				
				reactionsFindNext();
			}
			// button works but even with this line of code, Find/Replace
			// does not regain focus after updating table.
			getFindReplaceDialog().requestFocus();
		}
	};
	
	public void reactionsFindAction() {
		reactionsFindAll = false;
		reactionsTable.repaint();
		ArrayList<ArrayList<Integer>> locationList = reactionsLocationsList();
		setReactionsFindLocationsList(locationList);
		// uses window listener for focus event and row column change to reset button
		// to not clicked if user changes selected cell.
		// for first click of find button, find starts from first cell after (or before when backwards)
		// that contains string, after this first click, find just iterates through list				
		if (!findButtonReactionsClicked) {
			if (reactionsFindStartIndex() > -1) {
				LocalConfig.getInstance().setReactionsLocationsListCount(reactionsFindStartIndex());
				reactionsFindNext();
			} else {
				notFoundAction();	
				if (wrapAround) {
					if (searchBackwards) {
						LocalConfig.getInstance().setReactionsLocationsListCount(getReactionsFindLocationsList().size() - 1);
					} else {
						LocalConfig.getInstance().setReactionsLocationsListCount(0);
					}
				}
			}					
		} else {
			reactionsFindNext();
		}
	}
	
	public void notFoundAction() {
		getFindReplaceDialog().setAlwaysOnTop(false);
		Object[] options = {"    Yes    ", "    No    ",};
		int choice = JOptionPane.showOptionDialog(null, 
				"MOST has not found the item you are searching for.\nDo you want to start over from the beginning?", 
				"Item Not Found", 
				JOptionPane.YES_NO_OPTION, 
				JOptionPane.QUESTION_MESSAGE, 
				null, options, options[0]);
		if (choice == JOptionPane.YES_OPTION) {
			wrapAround = true; 
			getFindReplaceDialog().wrapCheckBox.setSelected(true);
			if (searchBackwards) {
				LocalConfig.getInstance().setReactionsLocationsListCount(getReactionsFindLocationsList().size() - 1);
			} else {
				LocalConfig.getInstance().setReactionsLocationsListCount(0);
			}
		}
		if (choice == JOptionPane.NO_OPTION) {

		}
		getFindReplaceDialog().setAlwaysOnTop(true);
	}
	
	public void reactionsFindNext() {
		//reactionsFindAll = false;
		reactionsTable.repaint();
		ArrayList<ArrayList<Integer>> locationList = reactionsLocationsList();
		if (locationList.size() == 0) {
			notFoundAction();
			LocalConfig.getInstance().setReactionsLocationsListCount(0);
		} else {
			try {
				// set focus to an invisible cell to give appearance that find cell is 
				// only cell selected
				reactionsTable.changeSelection(locationList.get(LocalConfig.getInstance().getReactionsLocationsListCount()).get(0), 0, false, false);
				changeReactionFindSelection = false;
				setReactionsReplaceLocation(locationList.get(LocalConfig.getInstance().getReactionsLocationsListCount()));			
				reactionsTable.requestFocus();
				reactionsTable.scrollRectToVisible(reactionsTable.getCellRect(locationList.get(LocalConfig.getInstance().getReactionsLocationsListCount()).get(0), locationList.get(LocalConfig.getInstance().getReactionsLocationsListCount()).get(1), false));
				int viewRow = reactionsTable.convertRowIndexToModel(reactionsTable.getSelectedRow());
				formulaBar.setText((String) reactionsTable.getModel().getValueAt(viewRow, locationList.get(LocalConfig.getInstance().getReactionsLocationsListCount()).get(1)));
				getFindReplaceDialog().requestFocus();
				// if not at end of list increment, else start over
				int count = LocalConfig.getInstance().getReactionsLocationsListCount();
				if (!searchBackwards) {
					if (LocalConfig.getInstance().getReactionsLocationsListCount() < (getReactionsFindLocationsList().size() - 1)) {
						count += 1;
						LocalConfig.getInstance().setReactionsLocationsListCount(count);
					} else {
						if (wrapAround) {							
							count = 0;
							LocalConfig.getInstance().setReactionsLocationsListCount(count);							
						} else {							
							if (throwNotFoundError) {															
								notFoundAction();
								throwNotFoundError = false;
							}
							throwNotFoundError = true;
						}
					}
				} else {
					if (LocalConfig.getInstance().getReactionsLocationsListCount() > 0) {
						count -= 1;
						LocalConfig.getInstance().setReactionsLocationsListCount(count);
					} else {
						if (wrapAround) {							
							count = locationList.size() - 1;
							LocalConfig.getInstance().setReactionsLocationsListCount(count);							
						} else {							
							if (throwNotFoundError) {															
								notFoundAction();
								throwNotFoundError = false;
							}
							throwNotFoundError = true;
						}
					}
				}						
			} catch (Throwable t){
				LocalConfig.getInstance().setReactionsLocationsListCount(LocalConfig.getInstance().getReactionsLocationsListCount());
				if (locationList.size() > 0) {
					try {
						//reactionsTable.changeSelection(locationList.get(LocalConfig.getInstance().getReactionsLocationsListCount()).get(0), locationList.get(LocalConfig.getInstance().getReactionsLocationsListCount()).get(1), false, false);
						//reactionsTable.requestFocus();
					} catch (Throwable t1){
						
					}					
				}				
			}										
		}
		getFindReplaceDialog().requestFocus();
	}
	
	ActionListener findAllReactionsButtonActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent ae) {			
			if (tabbedPane.getSelectedIndex() == 0) {
				ArrayList<ArrayList<Integer>> locationList = reactionsLocationsList();
				setReactionsFindLocationsList(locationList);
				if (locationList.size() == 0) {
					notFoundAction();				
				} else {
					// set focus to first found item
					reactionsTable.changeSelection(locationList.get(0).get(0), locationList.get(0).get(1), false, false);
					reactionsTable.requestFocus();
					// enables highlighter
					reactionsFindAll = true;
					getFindReplaceDialog().requestFocus();
					
					closeConnection();
					reloadTables(LocalConfig.getInstance().getLoadedDatabase());
				}			
			}			
		}
	};
	
	public ArrayList<ArrayList<Integer>> reactionsLocationsList() {		
		int rowStartIndex = 0;
		int rowEndIndex = reactionsTable.getRowCount();
		// start with 1 to avoid including hidden id column
		int colStartIndex = 1;
		int colEndIndex = reactionsTable.getColumnCount();
		if (searchSelectedArea) {
			if (!findButtonReactionsClicked) {
				int numcols=reactionsTable.getSelectedColumnCount(); 
				int numrows=reactionsTable.getSelectedRowCount(); 
				int[] rowsselected=reactionsTable.getSelectedRows(); 
				int[] colsselected=reactionsTable.getSelectedColumns();
				if (numrows > 0 && getSelectionMode() != 1) {
					rowStartIndex = rowsselected[0];
					rowEndIndex = rowsselected[0] + numrows;
					selectedReactionsRowStartIndex = rowsselected[0];
					selectedReactionsRowEndIndex = rowsselected[0] + numrows;
				}			
				if (numcols > 0 && getSelectionMode() != 2) {
					colStartIndex = colsselected[0];
					colEndIndex = colsselected[0] + numcols;
					selectedReactionsColumnStartIndex = colsselected[0];
					selectedReactionsColumnEndIndex = colsselected[0] + numcols;
				}	
			}					
		} else {
			selectedReactionsRowStartIndex = 0;
			selectedReactionsRowEndIndex = reactionsTable.getRowCount();
			// start with 1 to avoid including hidden id column
			selectedReactionsColumnStartIndex = 1;
			selectedReactionsColumnEndIndex = reactionsTable.getColumnCount();
		}
		ArrayList<ArrayList<Integer>> reactionsLocationsList = new ArrayList<ArrayList<Integer>>();
		for (int r = selectedReactionsRowStartIndex; r < selectedReactionsRowEndIndex; r++) {					
			for (int c = selectedReactionsColumnStartIndex; c < selectedReactionsColumnEndIndex; c++) {					
				int viewRow = reactionsTable.convertRowIndexToModel(r);
				if (reactionsTable.getModel().getValueAt(viewRow, c) != null) {
					String cellValue = (String) reactionsTable.getModel().getValueAt(viewRow, c);
					String findValue = findReplaceDialog.getFindText();
					if (!matchCase) {
						cellValue = cellValue.toLowerCase();
						findValue = findValue.toLowerCase();						
					}
					if (cellValue.contains(findValue) && getVisibleReactionsColumns().contains(c)) {
						ArrayList<Integer> rowColumnList = new ArrayList<Integer>();
						rowColumnList.add(r);
						rowColumnList.add(c);
						reactionsLocationsList.add(rowColumnList);
					}					
				}
			}
		}
		return reactionsLocationsList;
		
	}
	
	ActionListener replaceReactionsButtonActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent ae) {	
			if (tabbedPane.getSelectedIndex() == 0) {
				reactionsReplace();				
			}
		}
	};
	
	public void reactionsReplace() {
		int viewRow = reactionsTable.convertRowIndexToModel(getReactionsReplaceLocation().get(0));
		String oldValue = (String) reactionsTable.getModel().getValueAt(viewRow, getReactionsReplaceLocation().get(1));		
		String newValue = replaceValue(oldValue, replaceLocation(oldValue));
		ReactionUndoItem undoItem = createReactionUndoItem(oldValue, newValue, reactionsTable.getSelectedRow(), getReactionsReplaceLocation().get(1), viewRow + 1, UndoConstants.REPLACE, UndoConstants.REACTION_UNDO_ITEM_TYPE);
		ArrayList<ArrayList<Integer>> locationList = reactionsLocationsList();
		if (replaceLocation(oldValue) > -1) {
			reactionsTable.getModel().setValueAt(newValue, viewRow, getReactionsReplaceLocation().get(1));
			updateReactionsCellIfValid(oldValue, newValue, viewRow, getReactionsReplaceLocation().get(1));
			if (reactionUpdateValid) {
				setUpReactionsUndo(undoItem);
			}
			setReactionsFindLocationsList(locationList);
			int count = LocalConfig.getInstance().getReactionsLocationsListCount();
			if (!searchBackwards) {
				if (LocalConfig.getInstance().getReactionsLocationsListCount() <= (getReactionsFindLocationsList().size() - 1)) {
					// if value changed in cell, when list recreated, will need to move back
					// since there will be one less value found. also if cell contains multiple
					// instances of find string, need to keep counter from advancing.
					if (reactionUpdateValid || oldValue.contains(findReplaceDialog.getFindText())) {
						count -= 1;
						LocalConfig.getInstance().setReactionsLocationsListCount(count);
					} 
				} else {
					count = 0;
					LocalConfig.getInstance().setReactionsLocationsListCount(count);
				}
			} else {
				if (LocalConfig.getInstance().getReactionsLocationsListCount() > 0) {
					if (reactionUpdateValid || oldValue.contains(findReplaceDialog.getFindText())) {
						// seems to be working without adjusting counter(?)
						//count += 1;
						//LocalConfig.getInstance().setReactionsLocationsListCount(count);
					}
				} else {
					if (locationList.size() > 1) {
						count = locationList.size() - 1;
						LocalConfig.getInstance().setReactionsLocationsListCount(count);
					} else {
						LocalConfig.getInstance().setReactionsLocationsListCount(0);
					}
				}
			}
			
		} else {
			//TODO: Display an error message here in the unlikely event that there is an error
			//System.out.println("String not found");
		}
		getFindReplaceDialog().requestFocus();
	}
	
	ActionListener replaceAllReactionsButtonActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent ae) {
			if (tabbedPane.getSelectedIndex() == 0) {
				copyReactionsDatabaseTables();
				ReactionUndoItem undoItem = createReactionUndoItem("", "", getReactionsFindLocationsList().get(0).get(0), getReactionsFindLocationsList().get(0).get(1), 1, UndoConstants.REPLACE_ALL, UndoConstants.REACTION_UNDO_ITEM_TYPE);
				undoItem.setTableCopyIndex(LocalConfig.getInstance().getNumReactionTablesCopied());
				replaceAllMode = true;
				showErrorMessage = true;
				ReactionsUpdater updater = new ReactionsUpdater();
				ArrayList<Integer> rowList = new ArrayList<Integer>();
				ArrayList<Integer> reacIdList = new ArrayList<Integer>();
				ArrayList<String> oldReactionsList = new ArrayList<String>();
				for (int i = 0; i < getReactionsFindLocationsList().size(); i++) {
					int viewRow = reactionsTable.convertRowIndexToModel(getReactionsFindLocationsList().get(i).get(0));
					int id = Integer.valueOf((String) reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.DB_METABOLITE_ID_COLUMN));		
					rowList.add(viewRow);
					int reacId = Integer.valueOf((String) reactionsTable.getModel().getValueAt(viewRow, 0));
					reacIdList.add(reacId);
					String oldEquation = (String) reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTION_EQUN_ABBR_COLUMN);
					oldReactionsList.add(oldEquation);
				}
				for (int i = 0; i < getReactionsFindLocationsList().size(); i++) {
					int viewRow = reactionsTable.convertRowIndexToModel(getReactionsFindLocationsList().get(i).get(0));
					String oldValue = (String) reactionsTable.getModel().getValueAt(viewRow, getReactionsFindLocationsList().get(i).get(1));					
					String replaceAllValue = "";
					if (matchCase) {
						replaceAllValue = oldValue.replaceAll(findReplaceDialog.getFindText(), findReplaceDialog.getReplaceText());
					} else {
						try {
							replaceAllValue = oldValue.replaceAll("(?i)" + findReplaceDialog.getFindText(), findReplaceDialog.getReplaceText());
						} catch (Throwable t){
							// catches regex error when () or [] are not in pairs
							validPaste = false;
						}						
					}
					if (isReactionsEntryValid(getReactionsFindLocationsList().get(i).get(1), viewRow, replaceAllValue)) {
						reactionsTable.setValueAt(replaceAllValue, viewRow, getReactionsFindLocationsList().get(i).get(1));	
					} else {
						validPaste = false;
					}		
				}
				if (validPaste) {
					updater.updateReactionRows(rowList, reacIdList, oldReactionsList, LocalConfig.getInstance().getLoadedDatabase());
					copyReactionsDatabaseTables();
					setUpReactionsUndo(undoItem);
				} else {
					if (showErrorMessage = true) {
						setFindReplaceAlwaysOnTop(false);
						JOptionPane.showMessageDialog(null,                
								getReplaceAllError(),                
								GraphicalInterfaceConstants.REPLACE_ALL_ERROR_TITLE,                                
								JOptionPane.ERROR_MESSAGE);
						setFindReplaceAlwaysOnTop(true);
					}	
					deleteReactionsPasteUndoItem();
					validPaste = true;
				}
			}
			
			closeConnection();
			// these two lines remove highlighting from find all
			ArrayList<ArrayList<Integer>> locationList = reactionsLocationsList();
			setReactionsFindLocationsList(locationList);
			
			reloadTables(LocalConfig.getInstance().getLoadedDatabase());
			// reset boolean values to default
			LocalConfig.getInstance().yesToAllButtonClicked = false;
			replaceAllMode = false;
			getFindReplaceDialog().requestFocus();
		}
	};
	
    // end reactions find replace
	
	/***********************************************************************************/
	// start metabolites find replace
	/***********************************************************************************/
	
	public int metabolitesFindStartIndex() {
		// always start at 0 when in column or row selection mode
		int startIndex = 0;
		// start from selected cell
		if (!searchSelectedArea) {
			if (metabolitesTable.getSelectedRow() > -1 && metabolitesTable.getSelectedColumn() > -1) {
				int row = metabolitesTable.getSelectedRow();
				int col = metabolitesTable.getSelectedColumn();
				startIndex = findStartIndex(row, col, getMetabolitesFindLocationsList());
			}				
		} else {
			if (searchBackwards) {
				startIndex = getMetabolitesFindLocationsList().size() - 1;
			} 		
		}
		return startIndex;
		
	}
	
	ActionListener findMetabolitesButtonActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent ae) {
			if (tabbedPane.getSelectedIndex() == 1) {
				metabolitesFindAction();
			}			
			findButtonMetabolitesClicked = true;				
		}
	};
	
	ActionListener replaceFindMetabolitesButtonActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent ae) {
			if (tabbedPane.getSelectedIndex() == 1) {
				metabolitesFindAll = false;
				metabolitesReplace();				
				metabolitesFindNext();
			}
			// button works but even with this line of code, Find/Replace
			// does not regain focus after updating table.
			getFindReplaceDialog().requestFocus();
		}
	};
	
	public void metabolitesFindAction() {
		metabolitesFindAll = false;
		metabolitesTable.repaint();
		ArrayList<ArrayList<Integer>> locationList = metabolitesLocationsList();
		setMetabolitesFindLocationsList(locationList);
		// uses window listener for focus event and row column change to reset button
		// to not clicked if user changes selected cell.
		// for first click of find button, find starts from first cell after (or before when backwards)
		// that contains string, after this first click, find just iterates through list				
		if (!findButtonMetabolitesClicked) {
			if (metabolitesFindStartIndex() > -1) {
				LocalConfig.getInstance().setMetabolitesLocationsListCount(metabolitesFindStartIndex());
				metabolitesFindNext();
			} else {
				notFoundAction();
				if (wrapAround) {
					if (searchBackwards) {
						LocalConfig.getInstance().setMetabolitesLocationsListCount(getMetabolitesFindLocationsList().size() - 1);
					} else {
						LocalConfig.getInstance().setMetabolitesLocationsListCount(0);
					}
				}
			}					
		} else {
			metabolitesFindNext();
		}
	}
	
	public void metabolitesFindNext() {
		//metabolitesFindAll = false;
		metabolitesTable.repaint();
		ArrayList<ArrayList<Integer>> locationList = metabolitesLocationsList();
		if (locationList.size() == 0) {
			notFoundAction();
			LocalConfig.getInstance().setMetabolitesLocationsListCount(0);
		} else {
			try {
				// set focus to an invisible cell to give appearance that find cell is 
				// only cell selected
				metabolitesTable.changeSelection(locationList.get(LocalConfig.getInstance().getMetabolitesLocationsListCount()).get(0), 0, false, false);
				changeMetaboliteFindSelection = false;
				setMetabolitesReplaceLocation(locationList.get(LocalConfig.getInstance().getMetabolitesLocationsListCount()));
				metabolitesTable.requestFocus();
				metabolitesTable.scrollRectToVisible(metabolitesTable.getCellRect(locationList.get(LocalConfig.getInstance().getMetabolitesLocationsListCount()).get(0), locationList.get(LocalConfig.getInstance().getMetabolitesLocationsListCount()).get(1), false));
				int viewRow = metabolitesTable.convertRowIndexToModel(metabolitesTable.getSelectedRow());
				formulaBar.setText((String) metabolitesTable.getModel().getValueAt(viewRow, locationList.get(LocalConfig.getInstance().getMetabolitesLocationsListCount()).get(1)));
				getFindReplaceDialog().requestFocus();
				// if not at end of list increment, else start over
				int count = LocalConfig.getInstance().getMetabolitesLocationsListCount();
				if (!searchBackwards) {
					if (LocalConfig.getInstance().getMetabolitesLocationsListCount() < (getMetabolitesFindLocationsList().size() - 1)) {
						count += 1;
						LocalConfig.getInstance().setMetabolitesLocationsListCount(count);
					} else {
						if (wrapAround) {							
							count = 0;
							LocalConfig.getInstance().setMetabolitesLocationsListCount(count);							
						} else {							
							if (throwNotFoundError) {															
								notFoundAction();
								throwNotFoundError = false;
							}
							throwNotFoundError = true;
						}
					}
				} else {
					if (LocalConfig.getInstance().getMetabolitesLocationsListCount() > 0) {
						count -= 1;
						LocalConfig.getInstance().setMetabolitesLocationsListCount(count);
					} else {
						if (wrapAround) {							
							count = getMetabolitesFindLocationsList().size() - 1;
							LocalConfig.getInstance().setMetabolitesLocationsListCount(count);							
						} else {							
							if (throwNotFoundError) {															
								notFoundAction();
								throwNotFoundError = false;
							}
							throwNotFoundError = true;
						}
					}
				}						
			} catch (Throwable t){
				LocalConfig.getInstance().setMetabolitesLocationsListCount(LocalConfig.getInstance().getMetabolitesLocationsListCount());
				try {
					//metabolitesTable.changeSelection(locationList.get(LocalConfig.getInstance().getMetabolitesLocationsListCount()).get(0), locationList.get(LocalConfig.getInstance().getMetabolitesLocationsListCount()).get(1), false, false);
					//metabolitesTable.requestFocus();
				} catch (Throwable t1){
					
				}				
			}										
		}			
	}
	
	ActionListener findAllMetabolitesButtonActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent ae) {	
			if (tabbedPane.getSelectedIndex() == 1) {
				ArrayList<ArrayList<Integer>> locationList = metabolitesLocationsList();
				setMetabolitesFindLocationsList(locationList);
				if (locationList.size() == 0) {
					notFoundAction();				
				} else {
					// set focus to first found item
					metabolitesTable.changeSelection(locationList.get(0).get(0), locationList.get(0).get(1), false, false);
					metabolitesTable.requestFocus();
					// enables highlighter
					metabolitesFindAll = true;	
					getFindReplaceDialog().requestFocus();
					
					closeConnection();
					reloadTables(LocalConfig.getInstance().getLoadedDatabase());
				}			
			}		
		}
	};
	
	public ArrayList<ArrayList<Integer>> metabolitesLocationsList() {		
		int rowStartIndex = 0;
		int rowEndIndex = metabolitesTable.getRowCount();
		// start with 1 to avoid including hidden id column
		int colStartIndex = 1;
		int colEndIndex = metabolitesTable.getColumnCount();
		if (searchSelectedArea) {
			if (!findButtonMetabolitesClicked) {
				int numcols=metabolitesTable.getSelectedColumnCount(); 
				int numrows=metabolitesTable.getSelectedRowCount(); 
				int[] rowsselected=metabolitesTable.getSelectedRows(); 
				int[] colsselected=metabolitesTable.getSelectedColumns();
				if (numrows > 0 && getSelectionMode() != 1) {
					rowStartIndex = rowsselected[0];
					rowEndIndex = rowsselected[0] + numrows;
					selectedMetabolitesRowStartIndex = rowsselected[0];
					selectedMetabolitesRowEndIndex = rowsselected[0] + numrows;
				}			
				if (numcols > 0 && getSelectionMode() != 2) {
					colStartIndex = colsselected[0];
					colEndIndex = colsselected[0] + numcols;
					selectedMetabolitesColumnStartIndex = colsselected[0];
					selectedMetabolitesColumnEndIndex = colsselected[0] + numcols;
				}	
			}					
		} else {
			selectedMetabolitesRowStartIndex = 0;
			selectedMetabolitesRowEndIndex = metabolitesTable.getRowCount();
			// start with 1 to avoid including hidden id column
			selectedMetabolitesColumnStartIndex = 1;
			selectedMetabolitesColumnEndIndex = metabolitesTable.getColumnCount();
		}
		ArrayList<ArrayList<Integer>> metabolitesLocationsList = new ArrayList<ArrayList<Integer>>();
		for (int r = selectedMetabolitesRowStartIndex; r < selectedMetabolitesRowEndIndex; r++) {					
			for (int c = selectedMetabolitesColumnStartIndex; c < selectedMetabolitesColumnEndIndex; c++) {					
				int viewRow = metabolitesTable.convertRowIndexToModel(r);
				if (metabolitesTable.getModel().getValueAt(viewRow, c) != null) {
					String cellValue = (String) metabolitesTable.getModel().getValueAt(viewRow, c);
					String findValue = findReplaceDialog.getFindText();
					if (!matchCase) {
						cellValue = cellValue.toLowerCase();
						findValue = findValue.toLowerCase();						
					}
					if (cellValue.contains(findValue) && getVisibleMetabolitesColumns().contains(c)) {
						ArrayList<Integer> rowColumnList = new ArrayList<Integer>();
						rowColumnList.add(r);
						rowColumnList.add(c);
						metabolitesLocationsList.add(rowColumnList);
					}					
				}
			}
		}
		return metabolitesLocationsList;
		
	}
	
	ActionListener replaceMetabolitesButtonActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent ae) {
			if (tabbedPane.getSelectedIndex() == 1) {
				metabolitesReplace();
			}
		}
	};
	
	public void metabolitesReplace() {
		int viewRow = metabolitesTable.convertRowIndexToModel(getMetabolitesReplaceLocation().get(0));
		String oldValue = (String) metabolitesTable.getModel().getValueAt(viewRow, getMetabolitesReplaceLocation().get(1));
		String newValue = replaceValue(oldValue, replaceLocation(oldValue));
		MetaboliteUndoItem undoItem = createMetaboliteUndoItem(oldValue, newValue, metabolitesTable.getSelectedRow(), getMetabolitesReplaceLocation().get(1), viewRow + 1, UndoConstants.REPLACE, UndoConstants.METABOLITE_UNDO_ITEM_TYPE);
		setUndoOldCollections(undoItem);
		if (replaceLocation(oldValue) > -1) {
			//String newValue = replaceValue(oldValue, replaceLocation(oldValue));
			metabolitesTable.getModel().setValueAt(newValue, viewRow, getMetabolitesReplaceLocation().get(1));
			updateMetabolitesCellIfValid(oldValue, newValue, viewRow, getMetabolitesReplaceLocation().get(1));
			if (metaboliteUpdateValid) {
				setUpMetabolitesUndo(undoItem);
			}
			setMetabolitesFindLocationsList(metabolitesLocationsList());
			int count = LocalConfig.getInstance().getMetabolitesLocationsListCount();
			if (!searchBackwards) {
				if (LocalConfig.getInstance().getMetabolitesLocationsListCount() < (getMetabolitesFindLocationsList().size() - 1)) {
					// if value changed in cell, when list recreated, will need to move back
					// since there will be one less value found. also if cell contains multiple
					// instances of find string, need to keep counter from advancing.
					if (metaboliteUpdateValid || oldValue.contains(findReplaceDialog.getFindText())) {
						count -= 1;
						LocalConfig.getInstance().setMetabolitesLocationsListCount(count);
					} 
				} else {
					count = 0;
					LocalConfig.getInstance().setMetabolitesLocationsListCount(count);
				}
			} else {
				if (LocalConfig.getInstance().getMetabolitesLocationsListCount() > 0) {
					if (metaboliteUpdateValid || oldValue.contains(findReplaceDialog.getFindText())) {
						// seems to be working without adjusting counter(?)
						//count += 1;
						//LocalConfig.getInstance().setMetabolitesLocationsListCount(count);
					}
				} else {
					if (metabolitesLocationsList().size() > 1) {
						count = metabolitesLocationsList().size() - 1;
						LocalConfig.getInstance().setMetabolitesLocationsListCount(count);
					} else {
						LocalConfig.getInstance().setMetabolitesLocationsListCount(0);
					}
				}
			}
			
		} else {
			//TODO: Display an error message here in the unlikely event that there is an error
			//System.out.println("String not found");
		}
		getFindReplaceDialog().requestFocus();
	}
	
	ActionListener replaceAllMetabolitesButtonActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent ae) {
			if (tabbedPane.getSelectedIndex() == 1) {
				copyMetaboliteDatabaseTable();
				MetaboliteUndoItem undoItem = createMetaboliteUndoItem("", "", getMetabolitesFindLocationsList().get(0).get(0), getMetabolitesFindLocationsList().get(0).get(1), 1, UndoConstants.REPLACE_ALL, UndoConstants.METABOLITE_UNDO_ITEM_TYPE);
				undoItem.setTableCopyIndex(LocalConfig.getInstance().getNumMetabolitesTableCopied());
				setUndoOldCollections(undoItem);				
				replaceAllMode = true;
				showErrorMessage = true;
				MetabolitesUpdater updater = new MetabolitesUpdater();
				ArrayList<Integer> rowList = new ArrayList<Integer>();
				ArrayList<Integer> metabIdList = new ArrayList<Integer>();
				for (int i = 0; i < getMetabolitesFindLocationsList().size(); i++) {
					int viewRow = metabolitesTable.convertRowIndexToModel(getMetabolitesFindLocationsList().get(i).get(0));
					int id = Integer.valueOf((String) metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.DB_METABOLITE_ID_COLUMN));		
					rowList.add(viewRow);
					int metabId = Integer.valueOf((String) metabolitesTable.getModel().getValueAt(viewRow, 0));
					metabIdList.add(metabId);
				}
				for (int i = 0; i < getMetabolitesFindLocationsList().size(); i++) {
					int viewRow = metabolitesTable.convertRowIndexToModel(getMetabolitesFindLocationsList().get(i).get(0));
					String metabAbbrev = (String) metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN);
					String oldValue = (String) metabolitesTable.getModel().getValueAt(viewRow, getMetabolitesFindLocationsList().get(i).get(1));
					String replaceAllValue = "";
					if (matchCase) {
						replaceAllValue = oldValue.replaceAll(findReplaceDialog.getFindText(), findReplaceDialog.getReplaceText());
					} else {
						try {
							replaceAllValue = oldValue.replaceAll("(?i)" + findReplaceDialog.getFindText(), findReplaceDialog.getReplaceText());
						} catch (Throwable t){
							// catches regex error when () or [] are not in pairs
							validPaste = false;
						}						
					}
					if (getMetabolitesFindLocationsList().get(i).get(1) == GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN) {
						if (LocalConfig.getInstance().getMetaboliteUsedMap().containsKey(oldValue) && !LocalConfig.getInstance().getDuplicateIds().contains(oldValue)) {
							setReplaceAllError(GraphicalInterfaceConstants.REPLACE_ALL_PARTICIPATING_ERROR_MESSAGE);
							//updater.updateMetaboliteRows(rowList, metabIdList, LocalConfig.getInstance().getLoadedDatabase());
							metabolitesTable.getModel().setValueAt(oldValue, viewRow, getMetabolitesFindLocationsList().get(i).get(1));
							validPaste = false;
						} else {
						    metabolitesTable.getModel().setValueAt(replaceAllValue, viewRow, getMetabolitesFindLocationsList().get(i).get(1));							
						}
					} else if (getMetabolitesFindLocationsList().get(i).get(1) == GraphicalInterfaceConstants.CHARGE_COLUMN) {
						replaceAllValue = oldValue.replaceAll(findReplaceDialog.getFindText(), findReplaceDialog.getReplaceText());
						if (replaceAllValue != null && replaceAllValue.trim().length() > 0) {
							validPaste = true;
							EntryValidator validator = new EntryValidator();
							if (!validator.isNumber(replaceAllValue)) {
								setReplaceAllError("Number format exception");
						    	validPaste = false;
						    	metabolitesTable.getModel().setValueAt(oldValue, viewRow, getMetabolitesFindLocationsList().get(i).get(1));	
							}
						} else {
							metabolitesTable.getModel().setValueAt("", viewRow, getMetabolitesFindLocationsList().get(i).get(1));
						}
						if (validPaste) {
							metabolitesTable.getModel().setValueAt(replaceAllValue, viewRow, getMetabolitesFindLocationsList().get(i).get(1));
						}						
					} else {
						if (isMetabolitesEntryValid(getMetabolitesFindLocationsList().get(i).get(1), replaceAllValue)) {
							metabolitesTable.setValueAt(replaceAllValue, viewRow, getMetabolitesFindLocationsList().get(i).get(1));			
						} else {
							validPaste = false;
						}	
					}			
				}
				if (validPaste) {						
					updater.updateMetaboliteRows(rowList, metabIdList, LocalConfig.getInstance().getLoadedDatabase());
					copyMetaboliteDatabaseTable();
					setUpMetabolitesUndo(undoItem);
				} else {
					if (showErrorMessage = true) {
						setFindReplaceAlwaysOnTop(false);
						JOptionPane.showMessageDialog(null,                
								getReplaceAllError(),                
								GraphicalInterfaceConstants.REPLACE_ALL_ERROR_TITLE,                                
								JOptionPane.ERROR_MESSAGE);
						setFindReplaceAlwaysOnTop(true);
					}
					deleteMetabolitesPasteUndoItem();
					validPaste = true;
				}				
			}
			
			closeConnection();
			// these two lines remove highlighting from find all
			ArrayList<ArrayList<Integer>> locationList = metabolitesLocationsList();
			setMetabolitesFindLocationsList(locationList);
			
			reloadTables(LocalConfig.getInstance().getLoadedDatabase());
			// reset boolean values to default
			LocalConfig.getInstance().yesToAllButtonClicked = false;
			replaceAllMode = false;
			getFindReplaceDialog().requestFocus();
		}
	};
	
	public Integer replaceLocation(String oldValue) {		
		// start index of find text in cell value
		int replaceLocation = 0;
		if (matchCase) {
			replaceLocation = oldValue.indexOf(findReplaceDialog.getFindText());
		} else {
			replaceLocation = oldValue.toLowerCase().indexOf(findReplaceDialog.getFindText().toLowerCase());
		}
		
		return replaceLocation;
		
	}
	
	public String replaceValue(String oldValue, int replaceLocation) {
		String replaceValue = "";
		int endIndex = replaceLocation + findReplaceDialog.getFindText().length();
		String replaceEnd = "";
		if (endIndex != oldValue.length()) {
			replaceEnd = oldValue.substring(endIndex);
		}
		try {
			replaceValue = oldValue.substring(0, replaceLocation) + findReplaceDialog.getReplaceText() + replaceEnd;
		} catch (Throwable t) {
			getFindReplaceDialog().replaceButton.setEnabled(false);
			getFindReplaceDialog().replaceFindButton.setEnabled(false);
			return "";
		}
		
		return replaceValue;
	}
	
	ActionListener matchCaseActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent actionEvent) {
			AbstractButton abstractButton = (AbstractButton) actionEvent.getSource();
			matchCase = abstractButton.getModel().isSelected();
		}
	};
	
	ActionListener wrapAroundActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent actionEvent) {
			AbstractButton abstractButton = (AbstractButton) actionEvent.getSource();
			wrapAround = abstractButton.getModel().isSelected();
		}
	};
	
	ActionListener selectedAreaActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent actionEvent) {
			AbstractButton abstractButton = (AbstractButton) actionEvent.getSource();
			searchSelectedArea = abstractButton.getModel().isSelected();
		}
	};
	
	ActionListener searchBackwardsActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent actionEvent) {
			AbstractButton abstractButton = (AbstractButton) actionEvent.getSource();
			searchBackwards = abstractButton.getModel().isSelected();
			if (tabbedPane.getSelectedIndex() == 0) {
				int count = LocalConfig.getInstance().getReactionsLocationsListCount();
				if (!searchBackwards && count > 0 && count < getReactionsFindLocationsList().size() - 1) {
					count += 1;
					LocalConfig.getInstance().setReactionsLocationsListCount(count);
				} else if (searchBackwards && count > 0 && count < getReactionsFindLocationsList().size() - 1) {
					count -= 1;
					LocalConfig.getInstance().setReactionsLocationsListCount(count);
				}
			} else if (tabbedPane.getSelectedIndex() == 1) {
				int count = LocalConfig.getInstance().getMetabolitesLocationsListCount();
				if (!searchBackwards && count > 0 && count < getMetabolitesFindLocationsList().size() - 1) {
					count += 1;
					LocalConfig.getInstance().setMetabolitesLocationsListCount(count);
				} else if (searchBackwards && count > 0 && count < getMetabolitesFindLocationsList().size() - 1) {
					count -= 1;
					LocalConfig.getInstance().setMetabolitesLocationsListCount(count);
				}
			}
			// only change cell if selected cell has been changed
			if (!findButtonReactionsClicked) {
				if (tabbedPane.getSelectedIndex() == 0) {
					if (searchBackwards) {	
						ArrayList<ArrayList<Integer>> locationList = reactionsLocationsList();
						setReactionsFindLocationsList(locationList);
						if (!findButtonReactionsClicked && getReactionsFindLocationsList().size() > 1) {
							LocalConfig.getInstance().setReactionsLocationsListCount(getReactionsFindLocationsList().size() - 1);
						}
					} else {
						LocalConfig.getInstance().setReactionsLocationsListCount(0);
					}
				} else if (tabbedPane.getSelectedIndex() == 1) {
					if (searchBackwards) {	
						ArrayList<ArrayList<Integer>> locationList = metabolitesLocationsList();
						setMetabolitesFindLocationsList(locationList);
						if (getMetabolitesFindLocationsList().size() > 1) {
							LocalConfig.getInstance().setMetabolitesLocationsListCount(getMetabolitesFindLocationsList().size() - 1);
						}
					} else {
						LocalConfig.getInstance().setMetabolitesLocationsListCount(0);
					}
				}
			} 
		}
	};
	
	ActionListener findDoneButtonActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent ae) {	
			findReplaceCloseAction();
		}
	};
	
	public void findReplaceCloseAction() {
		findMode = false;
		reactionsFindAll = false;
		metabolitesFindAll = false;	
		reactionsTable.repaint();
		metabolitesTable.repaint();	
		findReplaceItem.setEnabled(true);
		findbutton.setEnabled(true);
	}
	
	// This avoids conflicts with error messages being on top of component
	public void setFindReplaceAlwaysOnTop(boolean state) {
		if (getFindReplaceDialog() != null) {
			getFindReplaceDialog().setAlwaysOnTop(state);
		}
	}
	
	/*******************************************************************************/
	// end find/replace methods
	/******************************************************************************/
		
	public boolean isReactionsColumnVisible(int col) {
		ReactionsMetaColumnManager reactionsMetaColumnManager = new ReactionsMetaColumnManager();
		int reacMetaColumnCount = reactionsMetaColumnManager.getMetaColumnCount(LocalConfig.getInstance().getDatabaseName());
		if (col >= reacMetaColumnCount + GraphicalInterfaceConstants.REACTIONS_DB_COLUMN_NAMES.length || LocalConfig.getInstance().getHiddenReactionsColumns().contains(col)) {
			return false;
		}
		return true;
		
	}
	
	ArrayList visibleReactionsColumnList() {
		ArrayList<Integer> visibleReactionsColumnList = new ArrayList<Integer>();
		for (int i = 1; i < reactionsTable.getColumnCount(); i++) {
			if (isReactionsColumnVisible(i)) {
				visibleReactionsColumnList.add(i);
			}
		}
		return visibleReactionsColumnList;
		
	}
	
	public boolean isMetabolitesColumnVisible(int col) {
		MetabolitesMetaColumnManager metabolitesMetaColumnManager = new MetabolitesMetaColumnManager();
		int metabMetaColumnCount = metabolitesMetaColumnManager.getMetaColumnCount(LocalConfig.getInstance().getDatabaseName());
		if (col >= metabMetaColumnCount + GraphicalInterfaceConstants.METABOLITES_DB_COLUMN_NAMES.length || LocalConfig.getInstance().getHiddenMetabolitesColumns().contains(col)) {
			return false;
		}
		return true;
		
	}
	
	ArrayList visibleMetabolitesColumnList() {
		ArrayList<Integer> visibleMetabolitesColumnList = new ArrayList<Integer>();
		for (int i = 1; i < reactionsTable.getColumnCount(); i++) {
			if (isMetabolitesColumnVisible(i)) {
				visibleMetabolitesColumnList.add(i);
			}
		}
		return visibleMetabolitesColumnList;
		
	}
		
	public void deleteAllOptimizationFiles() {
		Utilities u = new Utilities();
		//TODO: if "_orig" db exists rename to db w/out "_orig", delete db w/out "_orig"
		// or delete db
		if (LocalConfig.getInstance().getOptimizationFilesList().size() > 0) {
			for (int i = 0; i < LocalConfig.getInstance().getOptimizationFilesList().size(); i++) {
				// TODO: determine where and how to display these messages, and actually delete these files
				//System.out.println(LocalConfig.getInstance().getOptimizationFilesList().get(i) + ".db will be deleted.");
				u.delete(LocalConfig.getInstance().getOptimizationFilesList().get(i) + ".db");
				File f = new File(LocalConfig.getInstance().getOptimizationFilesList().get(i) + ".log");
				if (f.exists()) {
					u.delete(LocalConfig.getInstance().getOptimizationFilesList().get(i) + ".log");
				}
				// TODO: Determine why MIP Files do not usually delete. (???)
				File f1 = new File(LocalConfig.getInstance().getOptimizationFilesList().get(i).substring(4) + "_MIP.log");						
				if (f1.exists()) {
					u.delete(LocalConfig.getInstance().getOptimizationFilesList().get(i).substring(4) + "_MIP.log");						
				}						
			}					
		}				
		LocalConfig.getInstance().getOptimizationFilesList().clear(); 
	}
	
	public void createUnusedMetabolitesList() {
		Map<String, Object> idMap = LocalConfig.getInstance().getMetaboliteIdNameMap();
		
		ArrayList<String> usedList = new ArrayList<String>(LocalConfig.getInstance().getMetaboliteUsedMap().keySet());
		ArrayList<String> idList = new ArrayList<String>(LocalConfig.getInstance().getMetaboliteIdNameMap().keySet());
		ArrayList<Integer> unusedList = new ArrayList<Integer>();
		// removes unused metabolites from idMap and populates list of
		// unused metabolite id's for deletion from table
		for (int i = 0; i < idList.size(); i++) {						
			if (!usedList.contains(idList.get(i))) {
				int id = (Integer) idMap.get(idList.get(i));
				unusedList.add(id); 
			}
		}
		LocalConfig.getInstance().setUnusedList(unusedList);
	}
		
	public void scrollFirstParticipatingRxnToView() {
		MetaboliteFactory aFactory = new MetaboliteFactory("SBML", LocalConfig.getInstance().getLoadedDatabase());	
		ArrayList<Integer> participatingReactions = aFactory.participatingReactions(getParticipatingMetabolite());
		LocalConfig.getInstance().setParticipatingReactions(participatingReactions);
		// sort to get minimum
		Collections.sort(participatingReactions);
		// scroll first participating reaction into view
		if (participatingReactions.size() > 0) {
			int viewRow = reactionsTable.convertRowIndexToView(participatingReactions.get(0) - 1);
			reactionsTable.changeSelection(viewRow, GraphicalInterfaceConstants.REACTION_EQUN_ABBR_COLUMN, false, false);
			reactionsTable.requestFocus();
		}
	}
	
	/******************************************************************************/
    // undo/redo
    /******************************************************************************/
	
	// adds image to OptionComponent
	public void addImage(OptionComponent comp, JLabel label) {
        label.setBorder(BorderFactory.createEmptyBorder(0,0,0,20));
		label.setAlignmentX(LEFT_ALIGNMENT);
        comp.add(label); 
    }
	
	public void enableOptionComponent(OptionComponent comp, JLabel label, JLabel grayedLabel) {
		comp.setEnabled(true);
		label.setVisible(true);
		grayedLabel.setVisible(false);
	}
	
    public void disableOptionComponent(OptionComponent comp, JLabel label, JLabel grayedLabel) {
    	comp.setEnabled(false);
    	comp.buttonClicked = false;
		label.setVisible(false);
		grayedLabel.setVisible(true);
	}
    
    public ReactionUndoItem createReactionUndoItem(String oldValue, String newValue, 
    		int row, int column, int id, String undoType, String undoItemType) {
    	if (oldValue == null) {
    		oldValue = "";
    	}
    	if (newValue == null) {
    		newValue = "";
    	}
    	ReactionUndoItem undoItem = new ReactionUndoItem();
    	undoItem.setDatabaseName(LocalConfig.getInstance().getLoadedDatabase());
    	undoItem.setOldValue(oldValue);
    	undoItem.setNewValue(newValue);
    	undoItem.setRow(row);
    	undoItem.setColumn(column);
    	undoItem.setId(id);
    	undoItem.setUndoType(undoType);
    	undoItem.setUndoItemType(undoItemType);
		return undoItem;
    	
    }
    
    public MetaboliteUndoItem createMetaboliteUndoItem(String oldValue, String newValue, 
    		int row, int column, int id, String undoType, String undoItemType) {
    	if (oldValue == null) {
    		oldValue = "";
    	}
    	if (newValue == null) {
    		newValue = "";
    	}
    	MetaboliteUndoItem undoItem = new MetaboliteUndoItem();
    	undoItem.setDatabaseName(LocalConfig.getInstance().getLoadedDatabase());
    	undoItem.setOldValue(oldValue);
    	undoItem.setNewValue(newValue);
    	undoItem.setRow(row);
    	undoItem.setColumn(column);
    	undoItem.setId(id);
    	undoItem.setUndoType(undoType);
    	undoItem.setUndoItemType(undoItemType);
    	// make deep copies of lists and maps to be restored if undo is executed		
		ArrayList<Integer> oldBlankMetabIds = null;
		ArrayList<Integer> oldDuplicateIds = null;
		Map<String, Object> oldMetaboliteIdNameMap = null;
		Map<String, Object> oldMetaboliteUsedMap = null;
		ArrayList<Integer> oldSuspiciousMetabolites = null;		
		ArrayList<Integer> oldUnusedList = null;
		
		try {
			oldBlankMetabIds = (ArrayList<Integer>)(ObjectCloner.deepCopy(LocalConfig.getInstance().getBlankMetabIds()));
			oldDuplicateIds = (ArrayList<Integer>)(ObjectCloner.deepCopy(LocalConfig.getInstance().getDuplicateIds()));
			oldMetaboliteIdNameMap = (Map<String, Object>) (ObjectCloner.deepCopy(LocalConfig.getInstance().getMetaboliteIdNameMap()));
			oldMetaboliteUsedMap = (Map<String, Object>) (ObjectCloner.deepCopy(LocalConfig.getInstance().getMetaboliteUsedMap()));
			oldSuspiciousMetabolites = (ArrayList<Integer>)(ObjectCloner.deepCopy(LocalConfig.getInstance().getSuspiciousMetabolites()));
			oldUnusedList = (ArrayList<Integer>)(ObjectCloner.deepCopy(LocalConfig.getInstance().getUnusedList()));
			undoItem.setOldBlankMetabIds(oldBlankMetabIds);
			undoItem.setOldDuplicateIds(oldDuplicateIds);
			undoItem.setOldMetaboliteIdNameMap(oldMetaboliteIdNameMap);
			undoItem.setOldMetaboliteUsedMap(oldMetaboliteUsedMap);
			undoItem.setOldSuspiciousMetabolites(oldSuspiciousMetabolites);
			undoItem.setOldUnusedList(oldUnusedList);
		} catch (Exception e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
		return undoItem;
    	
    }
    
    public void setUndoOldCollections(MetaboliteUndoItem undoItem) {
    	// make deep copies of lists and maps to be restored if undo is executed		
		ArrayList<Integer> oldBlankMetabIds = new ArrayList<Integer>();
		ArrayList<Integer> oldDuplicateIds = new ArrayList<Integer>();
		Map<String, Object> oldMetaboliteIdNameMap = new HashMap<String, Object>();
		Map<String, Object> oldMetaboliteUsedMap = new HashMap<String, Object>();
		ArrayList<Integer> oldSuspiciousMetabolites = new ArrayList<Integer>();		
		ArrayList<Integer> oldUnusedList = new ArrayList<Integer>();
		
		try {
			if (LocalConfig.getInstance().getBlankMetabIds() != null) {
				oldBlankMetabIds = (ArrayList<Integer>)(ObjectCloner.deepCopy(LocalConfig.getInstance().getBlankMetabIds()));
			}
			if (LocalConfig.getInstance().getDuplicateIds() != null) {
				oldDuplicateIds = (ArrayList<Integer>)(ObjectCloner.deepCopy(LocalConfig.getInstance().getDuplicateIds()));
			}
			if (LocalConfig.getInstance().getMetaboliteIdNameMap() != null) {
				oldMetaboliteIdNameMap = (Map<String, Object>) (ObjectCloner.deepCopy(LocalConfig.getInstance().getMetaboliteIdNameMap()));
			}
			if (LocalConfig.getInstance().getMetaboliteUsedMap() != null) {
				oldMetaboliteUsedMap = (Map<String, Object>) (ObjectCloner.deepCopy(LocalConfig.getInstance().getMetaboliteUsedMap()));
			}
			if (LocalConfig.getInstance().getSuspiciousMetabolites() != null) {
				oldSuspiciousMetabolites = (ArrayList<Integer>)(ObjectCloner.deepCopy(LocalConfig.getInstance().getSuspiciousMetabolites()));
			}
			if (LocalConfig.getInstance().getUnusedList() != null) {
				oldUnusedList = (ArrayList<Integer>)(ObjectCloner.deepCopy(LocalConfig.getInstance().getUnusedList()));
			}
			undoItem.setOldBlankMetabIds(oldBlankMetabIds);
			undoItem.setOldDuplicateIds(oldDuplicateIds);
			undoItem.setOldMetaboliteIdNameMap(oldMetaboliteIdNameMap);
			undoItem.setOldMetaboliteUsedMap(oldMetaboliteUsedMap);
			undoItem.setOldSuspiciousMetabolites(oldSuspiciousMetabolites);
			undoItem.setOldUnusedList(oldUnusedList);
		} catch (Exception e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
    }
    
    public void setUndoNewCollections(MetaboliteUndoItem undoItem) {
    	// make deep copies of lists and maps to be restored if redo is executed		
		ArrayList<Integer> newBlankMetabIds = new ArrayList<Integer>();
		ArrayList<Integer> newDuplicateIds = new ArrayList<Integer>();
		Map<String, Object> newMetaboliteIdNameMap = new HashMap<String, Object>();
		Map<String, Object> newMetaboliteUsedMap = new HashMap<String, Object>();
		ArrayList<Integer> newSuspiciousMetabolites = new ArrayList<Integer>();	
		ArrayList<Integer> newUnusedList = new ArrayList<Integer>();
		
		try {
			if (LocalConfig.getInstance().getBlankMetabIds() != null) {
				newBlankMetabIds = (ArrayList<Integer>)(ObjectCloner.deepCopy(LocalConfig.getInstance().getBlankMetabIds()));
			}
			if (LocalConfig.getInstance().getDuplicateIds() != null) {
				newDuplicateIds = (ArrayList<Integer>)(ObjectCloner.deepCopy(LocalConfig.getInstance().getDuplicateIds()));
			}
			if (LocalConfig.getInstance().getMetaboliteIdNameMap() != null) {
				newMetaboliteIdNameMap = (Map<String, Object>) (ObjectCloner.deepCopy(LocalConfig.getInstance().getMetaboliteIdNameMap()));
			}
			if (LocalConfig.getInstance().getMetaboliteUsedMap() != null) {
				newMetaboliteUsedMap = (Map<String, Object>) (ObjectCloner.deepCopy(LocalConfig.getInstance().getMetaboliteUsedMap()));
			}
			if (LocalConfig.getInstance().getSuspiciousMetabolites() != null) {
				newSuspiciousMetabolites = (ArrayList<Integer>)(ObjectCloner.deepCopy(LocalConfig.getInstance().getSuspiciousMetabolites()));
			}
			if (LocalConfig.getInstance().getUnusedList() != null) {
				newUnusedList = (ArrayList<Integer>)(ObjectCloner.deepCopy(LocalConfig.getInstance().getUnusedList()));
			}
			
			undoItem.setNewBlankMetabIds(newBlankMetabIds);
			undoItem.setNewDuplicateIds(newDuplicateIds);
			undoItem.setNewMetaboliteIdNameMap(newMetaboliteIdNameMap);
			undoItem.setNewMetaboliteUsedMap(newMetaboliteUsedMap);
			undoItem.setNewSuspiciousMetabolites(newSuspiciousMetabolites);
			undoItem.setNewUnusedList(newUnusedList);
		} catch (Exception e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
    }
    
    public void updateUndoButton() {
    	JScrollPopupMenu popupMenu = undoSplitButton.getPopupMenu();
    	addMenuItems(LocalConfig.getInstance().getUndoItemMap(), "undo");
		if (undoCount > 0) {
			enableOptionComponent(undoSplitButton, undoLabel, undoGrayedLabel);
			Class cls = LocalConfig.getInstance().getUndoItemMap().get(LocalConfig.getInstance().getUndoItemMap().size()).getClass();
			if ((cls.getName().equals("edu.rutgers.MOST.data.ReactionUndoItem"))) {
				undoSplitButton.setToolTipText("Undo " + ((ReactionUndoItem) LocalConfig.getInstance().getUndoItemMap().get(LocalConfig.getInstance().getUndoItemMap().size())).createUndoDescription());
			} else if ((cls.getName().equals("edu.rutgers.MOST.data.MetaboliteUndoItem"))) {
				undoSplitButton.setToolTipText("Undo " + ((MetaboliteUndoItem) LocalConfig.getInstance().getUndoItemMap().get(LocalConfig.getInstance().getUndoItemMap().size())).createUndoDescription());
			}			
		} else {
			disableOptionComponent(undoSplitButton, undoLabel, undoGrayedLabel);
			undoSplitButton.setToolTipText("Undo");
		}		
		undoCount += 1;	
	}
	
	public void updateRedoButton() {
		JScrollPopupMenu popupMenu = redoSplitButton.getPopupMenu();
		addMenuItems(LocalConfig.getInstance().getRedoItemMap(), "redo");
		if (LocalConfig.getInstance().getRedoItemMap().size() > 0) {
			enableOptionComponent(redoSplitButton, redoLabel, redoGrayedLabel);
			Class cls = LocalConfig.getInstance().getRedoItemMap().get(LocalConfig.getInstance().getRedoItemMap().size()).getClass();
			if ((cls.getName().equals("edu.rutgers.MOST.data.ReactionUndoItem"))) {
				redoSplitButton.setToolTipText("Redo " + ((ReactionUndoItem) LocalConfig.getInstance().getRedoItemMap().get(LocalConfig.getInstance().getRedoItemMap().size())).createUndoDescription());
			} else if ((cls.getName().equals("edu.rutgers.MOST.data.MetaboliteUndoItem"))) {
				redoSplitButton.setToolTipText("Redo " + ((MetaboliteUndoItem) LocalConfig.getInstance().getRedoItemMap().get(LocalConfig.getInstance().getRedoItemMap().size())).createUndoDescription());
			}
		} else {
			disableOptionComponent(redoSplitButton, redoLabel, redoGrayedLabel);
			redoSplitButton.setToolTipText("Redo");
		}			
	}
	
	public void addMenuItems(final Map<Object, Object> undoMap, final String type) {
		final JScrollPopupMenu popupMenu = new JScrollPopupMenu();
		popupMenu.addMouseListener(new MouseListener() {
	        public void mouseClicked(MouseEvent e) {
	        	popupMenu.setVisible(false);
	        }
	        public void mouseEntered(MouseEvent e) {
	        	LocalConfig.getInstance().setUndoMenuIndex(undoMap.size() + 1);
	        	popupMenu.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(0, 0, GraphicalInterfaceConstants.UNDO_BORDER_HEIGHT, 0), "Cancel", TitledBorder.LEFT, TitledBorder.BOTTOM));
	        }
	        public void mouseExited(MouseEvent e) {
	        }
			public void mousePressed(MouseEvent arg0) {
			}
			public void mouseReleased(MouseEvent arg0) {				
			}
	      });
		if (type.equals("undo")) {
			undoSplitButton.setPopupMenu(popupMenu);
		} else if (type.equals("redo")) {
			redoSplitButton.setPopupMenu(popupMenu);
		}
		for (int i = undoMap.size() - 1; i > -1; i--) {
			String item = "";
			Class cls = undoMap.get(i + 1).getClass();
			if ((cls.getName().equals("edu.rutgers.MOST.data.ReactionUndoItem"))) {
				item = ((ReactionUndoItem) undoMap.get(i + 1)).createUndoDescription();
			} else if ((cls.getName().equals("edu.rutgers.MOST.data.MetaboliteUndoItem"))) {
				item = ((MetaboliteUndoItem) undoMap.get(i + 1)).createUndoDescription();
			}
			final JMenuItem menuItem = new JMenuItem(item);
			menuItem.setName(Integer.toString(i + 1));
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (e.getActionCommand().equals(ENABLE)) {
						int scrollRow = 0;
						int scrollCol = 1;
						for (int i = undoMap.size(); i >= Integer.valueOf(menuItem.getName()); i--) {
							Class cls = undoMap.get(i).getClass();
							if ((cls.getName().equals("edu.rutgers.MOST.data.ReactionUndoItem"))) {
								int row = ((ReactionUndoItem) undoMap.get(i)).getRow();
								if (row > -1) {
									scrollRow = row;
								}
								int col = ((ReactionUndoItem) undoMap.get(i)).getColumn();
								if (col > -1) {
									scrollCol = col;
								}
								if (type.equals("undo")) {								
									reactionUndoAction(i);
									undoCount -= 1;	       					
								} else if (type.equals("redo")) { 
									if (((ReactionUndoItem) undoMap.get(i)).getUndoType().equals(UndoConstants.ADD_ROW)) {
										scrollRow = redoAddRowScrollRow(reactionsTable);
									}
									if (((ReactionUndoItem) undoMap.get(i)).getUndoType().equals(UndoConstants.ADD_COLUMN)) {
										scrollCol = redoAddColumnScrollColumn("reactions");
									}
									reactionRedoAction(i);
								}  
								updateUndoButton();
								updateRedoButton();
								if (i == Integer.valueOf(menuItem.getName())) {
									reloadTables(LocalConfig.getInstance().getLoadedDatabase());
									tabbedPane.setSelectedIndex(0);
									scrollToLocation(reactionsTable, scrollRow, scrollCol);	
								}											
							} else if ((cls.getName().equals("edu.rutgers.MOST.data.MetaboliteUndoItem"))) {
								int row = ((MetaboliteUndoItem) undoMap.get(i)).getRow();
								if (row > -1) {
									scrollRow = row;
								}
								int col = ((MetaboliteUndoItem) undoMap.get(i)).getColumn();
								if (col > -1) {
									scrollCol = col;
								}
								if (type.equals("undo")) {								
									metaboliteUndoAction(i);
									undoCount -= 1;	       					
								} else if (type.equals("redo")) { 
									if (((MetaboliteUndoItem) undoMap.get(i)).getUndoType().equals(UndoConstants.ADD_ROW)) {
										scrollRow = redoAddRowScrollRow(metabolitesTable);
									}
									if (((MetaboliteUndoItem) undoMap.get(i)).getUndoType().equals(UndoConstants.ADD_COLUMN)) {
										scrollCol = redoAddColumnScrollColumn("metabolites");
									}
									metaboliteRedoAction(i);				        					
								} 
								updateUndoButton();
								updateRedoButton();
								if (i == Integer.valueOf(menuItem.getName())) {
									reloadTables(LocalConfig.getInstance().getLoadedDatabase());
									tabbedPane.setSelectedIndex(1);
									scrollToLocation(metabolitesTable, scrollRow, scrollCol);					
								}							
							}
						}
						LocalConfig.getInstance().setUndoMenuIndex(undoMap.size() + 1);
					} else if (e.getActionCommand().equals(DISABLE)) {
						popupMenu.setVisible(false);
					}					
				}
			});
        	menuItem.addMouseListener(new MouseListener() {
        		boolean increase;
    	        public void mouseClicked(MouseEvent e) {
    	        }
    	        public void mouseEntered(MouseEvent e) {
    	        	
    	        }
    	        public void mouseExited(MouseEvent e) {
    	        	
    	        }
    			public void mousePressed(MouseEvent arg0) {
    			}
    			public void mouseReleased(MouseEvent arg0) {				
    			}
    	      });
        	menuItem.addChangeListener(new ChangeListener() {
        		public void stateChanged(ChangeEvent e) {
        			String lastmenuItem = "";
        			int numberOfActions = undoMap.size() - Integer.valueOf(menuItem.getName()) + 1; 
        			if (type.equals("undo")) {
        				lastmenuItem = "Undo " + Integer.toString(numberOfActions) + " Item(s)";
        			} else if (type.equals("redo")) {
        				lastmenuItem = "Redo " + Integer.toString(numberOfActions) + " Item(s)";
        			}
        			if (isMenuItemVisible(undoMap, popupMenu, menuItem)) {
        				menuItem.setActionCommand(ENABLE);
        				popupMenu.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(0, 0, GraphicalInterfaceConstants.UNDO_BORDER_HEIGHT, 0), lastmenuItem, TitledBorder.LEFT, TitledBorder.BOTTOM));
        			} else {
        				menuItem.setActionCommand(DISABLE);
        				popupMenu.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(0, 0, GraphicalInterfaceConstants.UNDO_BORDER_HEIGHT, 0), "Cancel", TitledBorder.LEFT, TitledBorder.BOTTOM));
        			}
        		}
        	});
        	popupMenu.getScrollBar().addAdjustmentListener(new AdjustmentListener() {
    			@Override
    			public void adjustmentValueChanged(AdjustmentEvent e) {
    				if(e.getValueIsAdjusting()) {
    					enableVisibleMenuItems(undoMap, popupMenu, menuItem);
    				}
    				if(popupMenu.getScrollBar().getValueIsAdjusting()) {
    					enableVisibleMenuItems(undoMap, popupMenu, menuItem);
    				}
    				if(e.getAdjustmentType() == AdjustmentEvent.TRACK) { 
    					enableVisibleMenuItems(undoMap, popupMenu, menuItem);
    				}
    			}
    		});	
        	menuItem.addMouseMotionListener(mml);
        	popupMenu.add(menuItem);
        }
    }
	
	public boolean isMenuItemVisible(Map<Object, Object> undoMap, JScrollPopupMenu popupMenu, JMenuItem menuItem) {
		int visibilityCorrection = 0;		
		double top = popupMenu.getScrollBar().getValue()/menuItem.getPreferredSize().getHeight();
		double fraction = top - Math.floor(top);
		if (fraction > GraphicalInterfaceConstants.UNDO_VISIBILITY_FRACTION) {
			visibilityCorrection = 1;
		} else {
			visibilityCorrection = 0;
		}
		int topIndex = (int) (popupMenu.getScrollBar().getValue()/menuItem.getPreferredSize().getHeight());
		int topItem = undoMap.size() - topIndex;
		int bottomItem = topItem - (GraphicalInterfaceConstants.UNDO_MAX_VISIBLE_ROWS - 1) - visibilityCorrection;
		if (Integer.valueOf(menuItem.getName()) > topItem || Integer.valueOf(menuItem.getName()) < bottomItem) { 
			return false;
		}
		return true;
			
	}
	
	public void enableVisibleMenuItems(Map<Object, Object> undoMap, JScrollPopupMenu popupMenu, JMenuItem menuItem) {
		if (isMenuItemVisible(undoMap, popupMenu, menuItem)) {
			menuItem.setActionCommand(ENABLE);
		} else {
			menuItem.setActionCommand(DISABLE);
			popupMenu.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(0, 0, GraphicalInterfaceConstants.UNDO_BORDER_HEIGHT, 0), "Cancel", TitledBorder.LEFT, TitledBorder.BOTTOM));
		}
	}
	
	private MouseMotionAdapter mml = new MouseMotionAdapter() {
		public void mouseMoved( MouseEvent e) {
			//JMenuItem menuItem = (JMenuItem) e.getSource();
			//LocalConfig.getInstance().setUndoMenuIndex(Integer.valueOf(menuItem.getName()));
		}
    };
    
    private MouseListener undoButtonMouseListener = new MouseAdapter() { 
    	public void mousePressed(MouseEvent e) {
    		if (undoSplitButton.buttonClicked) {
    			Class cls = LocalConfig.getInstance().getUndoItemMap().get(LocalConfig.getInstance().getUndoItemMap().size()).getClass();
				if ((cls.getName().equals("edu.rutgers.MOST.data.ReactionUndoItem"))) {
					int row = ((ReactionUndoItem) LocalConfig.getInstance().getUndoItemMap().get(LocalConfig.getInstance().getUndoItemMap().size())).getRow();
    				int col = ((ReactionUndoItem) LocalConfig.getInstance().getUndoItemMap().get(LocalConfig.getInstance().getUndoItemMap().size())).getColumn();	    				
    				reactionUndoAction(LocalConfig.getInstance().getUndoItemMap().size());
    				undoCount -= 1;				
    				updateUndoButton();
    				updateRedoButton(); 
    				reloadTables(LocalConfig.getInstance().getLoadedDatabase());
    				tabbedPane.setSelectedIndex(0);
    				if (row > -1 && col > -1) {
    					scrollToLocation(reactionsTable, row, col);
    				}			
    			} else if ((cls.getName().equals("edu.rutgers.MOST.data.MetaboliteUndoItem"))) {
    				int row = ((MetaboliteUndoItem) LocalConfig.getInstance().getUndoItemMap().get(LocalConfig.getInstance().getUndoItemMap().size())).getRow();
    				int col = ((MetaboliteUndoItem) LocalConfig.getInstance().getUndoItemMap().get(LocalConfig.getInstance().getUndoItemMap().size())).getColumn();	    				
    				metaboliteUndoAction(LocalConfig.getInstance().getUndoItemMap().size());
    				undoCount -= 1;
    				updateUndoButton();
    				updateRedoButton();
    				reloadTables(LocalConfig.getInstance().getLoadedDatabase());
    				tabbedPane.setSelectedIndex(1);
    				if (row > -1 && col > -1) {
    					scrollToLocation(metabolitesTable, row, col);
    				}
    			}				
    			// used for highlighting menu items, necessary here?
    			LocalConfig.getInstance().setUndoMenuIndex(0);   
    		}            
    	}
    };
    
    public void metaboliteUndoAction(int index) {
    	((MetaboliteUndoItem) LocalConfig.getInstance().getUndoItemMap().get(index)).undo(); 
    	LocalConfig.getInstance().getRedoItemMap().put(redoCount, LocalConfig.getInstance().getUndoItemMap().get(index));
    	redoCount += 1;		
    	if (((MetaboliteUndoItem) LocalConfig.getInstance().getUndoItemMap().get(index)).getUndoType().equals(UndoConstants.SORT)) {
    		setMetabolitesSortColumnIndex(((MetaboliteUndoItem) LocalConfig.getInstance().getUndoItemMap().get(index)).getOldSortColumnIndex());
    		setMetabolitesSortOrder(((MetaboliteUndoItem) LocalConfig.getInstance().getUndoItemMap().get(index)).getOldSortOrder());
    		LocalConfig.getInstance().getMetabolitesRedoSortColumns().add(getMetabolitesSortColumnIndex());
    		LocalConfig.getInstance().getMetabolitesRedoSortOrderList().add(getMetabolitesSortOrder());
    		LocalConfig.getInstance().getMetabolitesSortColumns().remove(LocalConfig.getInstance().getMetabolitesSortColumns().size() - 1);
    		LocalConfig.getInstance().getMetabolitesSortOrderList().remove(LocalConfig.getInstance().getMetabolitesSortOrderList().size() - 1);
    		// TODO; test moving this out of loop, may be slow for many undos
    		reloadTables(LocalConfig.getInstance().getLoadedDatabase());
    	}
    	LocalConfig.getInstance().getUndoItemMap().remove(index);
    	undoCount -= 1;
    	highlightUnusedMetabolites = false;
		highlightUnusedMetabolitesItem.setState(false);
		closeConnection();
    }

    public void reactionUndoAction(int index) {
    	((ReactionUndoItem) LocalConfig.getInstance().getUndoItemMap().get(index)).undo(); 
    	LocalConfig.getInstance().getRedoItemMap().put(redoCount, LocalConfig.getInstance().getUndoItemMap().get(index));    	
    	redoCount += 1;		
    	if (((ReactionUndoItem) LocalConfig.getInstance().getUndoItemMap().get(index)).getUndoType().equals(UndoConstants.SORT)) {
    		setReactionsSortColumnIndex(((ReactionUndoItem) LocalConfig.getInstance().getUndoItemMap().get(index)).getOldSortColumnIndex());
    		setReactionsSortOrder(((ReactionUndoItem) LocalConfig.getInstance().getUndoItemMap().get(index)).getOldSortOrder());
    		LocalConfig.getInstance().getReactionsRedoSortColumns().add(getReactionsSortColumnIndex());
    		LocalConfig.getInstance().getReactionsRedoSortOrderList().add(getReactionsSortOrder());
    		LocalConfig.getInstance().getReactionsSortColumns().remove(LocalConfig.getInstance().getReactionsSortColumns().size() - 1);
    		LocalConfig.getInstance().getReactionsSortOrderList().remove(LocalConfig.getInstance().getReactionsSortOrderList().size() - 1);
    		// TODO; test moving this out of loop, may be slow for many undos
    		reloadTables(LocalConfig.getInstance().getLoadedDatabase());
    	}
    	LocalConfig.getInstance().getUndoItemMap().remove(index);
    	undoCount -= 1;		
    	closeConnection();
    }
    
    private MouseListener redoButtonMouseListener = new MouseAdapter() { 
    	public void mousePressed(MouseEvent e) {
    		if (redoSplitButton.buttonClicked) {
    			Class cls = LocalConfig.getInstance().getRedoItemMap().get(LocalConfig.getInstance().getRedoItemMap().size()).getClass();
				if ((cls.getName().equals("edu.rutgers.MOST.data.ReactionUndoItem"))) {
					int row = ((ReactionUndoItem) LocalConfig.getInstance().getRedoItemMap().get(LocalConfig.getInstance().getRedoItemMap().size())).getRow();
    				int col = ((ReactionUndoItem) LocalConfig.getInstance().getRedoItemMap().get(LocalConfig.getInstance().getRedoItemMap().size())).getColumn();	    				
    				if (((ReactionUndoItem) LocalConfig.getInstance().getRedoItemMap().get(LocalConfig.getInstance().getRedoItemMap().size())).getUndoType().equals(UndoConstants.ADD_ROW)) {
						row = redoAddRowScrollRow(reactionsTable);
					}
    				if (((ReactionUndoItem) LocalConfig.getInstance().getRedoItemMap().get(LocalConfig.getInstance().getRedoItemMap().size())).getUndoType().equals(UndoConstants.ADD_COLUMN)) {
    					col = redoAddColumnScrollColumn("reactions");
					}
    				reactionRedoAction(LocalConfig.getInstance().getRedoItemMap().size());				
    				updateUndoButton();
    				updateRedoButton();  
    				reloadTables(LocalConfig.getInstance().getLoadedDatabase());
    				tabbedPane.setSelectedIndex(0);
    				if (row > -1 && col > -1) {
    					scrollToLocation(reactionsTable, row, col);  					
    				}	
    			} else if ((cls.getName().equals("edu.rutgers.MOST.data.MetaboliteUndoItem"))) {
    				int row = ((MetaboliteUndoItem) LocalConfig.getInstance().getRedoItemMap().get(LocalConfig.getInstance().getRedoItemMap().size())).getRow();
    				int col = ((MetaboliteUndoItem) LocalConfig.getInstance().getRedoItemMap().get(LocalConfig.getInstance().getRedoItemMap().size())).getColumn();	    	
    				if (((MetaboliteUndoItem) LocalConfig.getInstance().getRedoItemMap().get(LocalConfig.getInstance().getRedoItemMap().size())).getUndoType().equals(UndoConstants.ADD_ROW)) {
						row = redoAddRowScrollRow(metabolitesTable);
					}
    				if (((MetaboliteUndoItem) LocalConfig.getInstance().getRedoItemMap().get(LocalConfig.getInstance().getRedoItemMap().size())).getUndoType().equals(UndoConstants.ADD_COLUMN)) {
    					col = redoAddColumnScrollColumn("metabolites");
					}
    				metaboliteRedoAction(LocalConfig.getInstance().getRedoItemMap().size());
    				updateUndoButton();
    				updateRedoButton();
    				reloadTables(LocalConfig.getInstance().getLoadedDatabase()); 
    				tabbedPane.setSelectedIndex(1);
    				if (row > -1 && col > -1) {
    					scrollToLocation(metabolitesTable, row, col);				
    				}
    			}
    			// used for highlighting menu items, necessary here?
    			LocalConfig.getInstance().setUndoMenuIndex(0);  
    		}            
    	}
    };
    
    public void metaboliteRedoAction(int index) {    	
    	((MetaboliteUndoItem) LocalConfig.getInstance().getRedoItemMap().get(index)).redo();     	
    	LocalConfig.getInstance().getUndoItemMap().put(undoCount, LocalConfig.getInstance().getRedoItemMap().get(index));
    	//undoCount += 1;
    	if (((MetaboliteUndoItem) LocalConfig.getInstance().getRedoItemMap().get(index)).getUndoType().equals(UndoConstants.SORT)) {
    		setMetabolitesSortColumnIndex(((MetaboliteUndoItem) LocalConfig.getInstance().getRedoItemMap().get(index)).getNewSortColumnIndex());
    		setMetabolitesSortOrder(((MetaboliteUndoItem) LocalConfig.getInstance().getRedoItemMap().get(index)).getNewSortOrder());
    		LocalConfig.getInstance().getMetabolitesSortColumns().add(getMetabolitesSortColumnIndex());
    		LocalConfig.getInstance().getMetabolitesSortOrderList().add(getMetabolitesSortOrder());
    		LocalConfig.getInstance().getMetabolitesRedoSortColumns().remove(LocalConfig.getInstance().getMetabolitesRedoSortColumns().size() - 1);
    		LocalConfig.getInstance().getMetabolitesRedoSortOrderList().remove(LocalConfig.getInstance().getMetabolitesRedoSortOrderList().size() - 1);
    		reloadTables(LocalConfig.getInstance().getLoadedDatabase());
    	}
    	LocalConfig.getInstance().getRedoItemMap().remove(index);
    	redoCount -= 1; 
    	highlightUnusedMetabolites = false;
		highlightUnusedMetabolitesItem.setState(false);
		closeConnection();
    }
    
    public void reactionRedoAction(int index) {    	
    	((ReactionUndoItem) LocalConfig.getInstance().getRedoItemMap().get(index)).redo();  
    	LocalConfig.getInstance().getUndoItemMap().put(undoCount, LocalConfig.getInstance().getRedoItemMap().get(index));    	
    	//undoCount += 1;
    	if (((ReactionUndoItem) LocalConfig.getInstance().getRedoItemMap().get(index)).getUndoType().equals(UndoConstants.SORT)) {
    		setReactionsSortColumnIndex(((ReactionUndoItem) LocalConfig.getInstance().getRedoItemMap().get(index)).getNewSortColumnIndex());
    		setReactionsSortOrder(((ReactionUndoItem) LocalConfig.getInstance().getRedoItemMap().get(index)).getNewSortOrder());
    		LocalConfig.getInstance().getReactionsSortColumns().add(getReactionsSortColumnIndex());
    		LocalConfig.getInstance().getReactionsSortOrderList().add(getReactionsSortOrder());
    		LocalConfig.getInstance().getReactionsRedoSortColumns().remove(LocalConfig.getInstance().getReactionsRedoSortColumns().size() - 1);
    		LocalConfig.getInstance().getReactionsRedoSortOrderList().remove(LocalConfig.getInstance().getReactionsRedoSortOrderList().size() - 1);
    		reloadTables(LocalConfig.getInstance().getLoadedDatabase());
    	}
    	LocalConfig.getInstance().getRedoItemMap().remove(index);
    	redoCount -= 1;
    	closeConnection();
    }
    
    public void setUpMetabolitesUndo(MetaboliteUndoItem undoItem) {
    	setUndoNewCollections(undoItem);
		LocalConfig.getInstance().getUndoItemMap().put(undoCount, undoItem);				
		updateUndoButton();
		clearRedoButton();
    }
    
    public void setUpReactionsUndo(ReactionUndoItem undoItem) {
        ArrayList<Integer> addedMetabList = new ArrayList<Integer>();		
		try {
			if (LocalConfig.getInstance().getBlankMetabIds() != null) {
				addedMetabList = (ArrayList<Integer>)(ObjectCloner.deepCopy(LocalConfig.getInstance().getAddedMetabolites()));
			}			
			undoItem.setAddedMetabolites(addedMetabList);
	    	LocalConfig.getInstance().getUndoItemMap().put(undoCount, undoItem);				
			updateUndoButton();
			clearRedoButton();
		} catch (Exception e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
    }
    
    public static String tableCopySuffix(int count) {
    	return new DecimalFormat("000").format(count);
    }
    
    public void copyReactionsDatabaseTables() {
    	int numCopied = LocalConfig.getInstance().getNumReactionTablesCopied();
		LocalConfig.getInstance().setNumReactionTablesCopied(numCopied + 1);
		DatabaseCreator databaseCreator = new DatabaseCreator();
		databaseCreator.copyReactionTables(LocalConfig.getInstance().getDatabaseName(), tableCopySuffix(LocalConfig.getInstance().getNumReactionTablesCopied()));
    }
    
    public void copyMetaboliteDatabaseTable() {
    	int numCopied = LocalConfig.getInstance().getNumMetabolitesTableCopied();
		LocalConfig.getInstance().setNumMetabolitesTableCopied(numCopied + 1);
		DatabaseCreator databaseCreator = new DatabaseCreator();
		databaseCreator.copyMetabolitesTable(LocalConfig.getInstance().getDatabaseName(), tableCopySuffix(LocalConfig.getInstance().getNumMetabolitesTableCopied()));
    }    
    
    // any changes in table clears redo button
    public void clearRedoButton() {
    	disableOptionComponent(redoSplitButton, redoLabel, redoGrayedLabel);
    	redoSplitButton.setToolTipText("Redo");
    	redoCount = 1;
    	LocalConfig.getInstance().getRedoItemMap().clear();
    	LocalConfig.getInstance().getReactionsRedoSortColumns().clear();
		LocalConfig.getInstance().getReactionsRedoSortOrderList().clear();
		LocalConfig.getInstance().getMetabolitesRedoSortColumns().clear();
		LocalConfig.getInstance().getMetabolitesRedoSortOrderList().clear();
    }
    
    public void scrollToLocation(JXTable table, int row, int column) {
    	try {
    		table.scrollRectToVisible(table.getCellRect(row, column, false));
    		setTableCellFocused(row, column, table);
    		int viewRow = table.convertRowIndexToModel(row);
    		formulaBar.setText((String) table.getModel().getValueAt(viewRow, column));
    	} catch (Throwable t) {
    		
    	}    	
    }
    
    public int redoAddRowScrollRow(JXTable table) {
    	int maxRow = table.getModel().getRowCount();
		int row = table.convertRowIndexToView(maxRow - 1);
		row += 1;
		
		return row;
    }
    
    public int redoAddColumnScrollColumn(String tableName) {
    	int col = 0;
    	if (tableName.equals("reactions")) {
    		ReactionsMetaColumnManager reactionsMetaColumnManager = new ReactionsMetaColumnManager();
    		int metaColumnCount = reactionsMetaColumnManager.getMetaColumnCount(LocalConfig.getInstance().getLoadedDatabase());
    		col = metaColumnCount + GraphicalInterfaceConstants.REACTIONS_DB_COLUMN_NAMES.length;
    	} else if (tableName.equals("metabolites")) {
    		MetabolitesMetaColumnManager metabolitesMetaColumnManager = new MetabolitesMetaColumnManager();
    		int metaColumnCount = metabolitesMetaColumnManager.getMetaColumnCount(LocalConfig.getInstance().getLoadedDatabase());
    		col = metaColumnCount + GraphicalInterfaceConstants.METABOLITES_DB_COLUMN_NAMES.length;
    	}
    			
		return col;
    }
    
    /******************************************************************************/
    // end undo/redo
    /******************************************************************************/
    
    /********************************************************************************/
	// toolbar methods
	/********************************************************************************/
	
	public void setUpToolbarButton(final JButton button) {
		// based on http://www.java2s.com/Tutorial/Java/0240__Swing/Buttonsusedintoolbars.htm
		if (!System.getProperty("java.version").startsWith("1.3")) {
			button.setOpaque(false);
			button.setBackground(new java.awt.Color(0, 0, 0, 0));
        }
		button.setBorderPainted(false);
		button.setMargin(new Insets(2, 2, 2, 2));
		button.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
			}
			public void mouseEntered(MouseEvent e) {
				if (button.isEnabled()) {
					button.setBorderPainted(true);
				}				
			}
			public void mouseExited(MouseEvent e) {
				button.setBorderPainted(false);
			}
			public void mousePressed(MouseEvent arg0) {
			}
			public void mouseReleased(MouseEvent arg0) {				
			}
		});
	}
	
	ActionListener copyButtonActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent ae) {	
			if (tabbedPane.getSelectedIndex() == 0) {
				reactionsCopy();
			} else if (tabbedPane.getSelectedIndex() == 1) {
				metabolitesCopy();
			}
		}
	};
	
	ActionListener pasteButtonActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent ae) {	
			if (tabbedPane.getSelectedIndex() == 0) {
				pasting = true;
				reactionsPaste();
				pasting = false;
				LocalConfig.getInstance().pastedReaction = false;
			} else if (tabbedPane.getSelectedIndex() == 1) {
				pasting = true;
				metabolitesPaste();
				pasting = false;
			}
		}
	};
	
	ActionListener findButtonActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent ae) {	
			if (!findMode) {
				showFindReplace();
			}
			findReplaceItem.setEnabled(false);
			findbutton.setEnabled(false);
		}
	};
	
	/********************************************************************************/
	// end toolbar methods
	/********************************************************************************/
    
    public void deleteItemFromDynamicTree() {
    	Utilities u = new Utilities();
		closeConnection();
		Object[] options = {"    Yes    ", "    No    ",};
		int choice = JOptionPane.showOptionDialog(null, 
				GraphicalInterfaceConstants.DELETE_ASSOCIATED_FILES, 
				GraphicalInterfaceConstants.DELETE_ASSOCIATED_FILES_TITLE, 
				JOptionPane.YES_NO_OPTION, 
				JOptionPane.QUESTION_MESSAGE, 
				null, options, options[0]);
		if (choice == JOptionPane.YES_OPTION) {//here
			/*
			System.out.println("database name: "
					+ LocalConfig.getInstance()
							.getOptimizationFilesList()
							.get(DynamicTree.getRow() - 1) + ".db");
							*/
			u.delete(LocalConfig.getInstance().getOptimizationFilesList().get(DynamicTree.getRow() - 1) + ".db");
			File f = new File(LocalConfig.getInstance().getOptimizationFilesList().get(DynamicTree.getRow() - 1) + ".log");
			if (f.exists()) {
				u.delete(LocalConfig.getInstance().getOptimizationFilesList().get(DynamicTree.getRow() - 1) + ".log");
			}
			// TODO: Determine why MIP Files do not usually delete. (???)
			File f1 = new File(LocalConfig.getInstance().getOptimizationFilesList().get(DynamicTree.getRow() - 1).substring(4) + "_MIP.log");						
			if (f1.exists()) {
				u.delete(LocalConfig.getInstance().getOptimizationFilesList().get(DynamicTree.getRow() - 1).substring(4) + "_MIP.log");						
			}
		}
		if (choice == JOptionPane.NO_OPTION) {

		}
		String fileString = "jdbc:sqlite:" + getDatabaseName() + ".db";
		LocalConfig.getInstance().setLoadedDatabase(getDatabaseName());
		try {
			Class.forName("org.sqlite.JDBC");
			Connection con = DriverManager.getConnection(fileString);
			LocalConfig.getInstance().setCurrentConnection(con);
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
	
	public void deleteAllItemsFromDynamicTree() {
		closeConnection();
		Object[] options = {"    Yes    ", "    No    ",};
		int choice = JOptionPane.showOptionDialog(null, 
				GraphicalInterfaceConstants.DELETE_ASSOCIATED_FILES, 
				GraphicalInterfaceConstants.DELETE_ASSOCIATED_FILES_TITLE, 
				JOptionPane.YES_NO_OPTION, 
				JOptionPane.QUESTION_MESSAGE, 
				null, options, options[0]);
		if (choice == JOptionPane.YES_OPTION) {				
			deleteAllOptimizationFiles();
		}
		if (choice == JOptionPane.NO_OPTION) {

		}
		setUpTables();	
	}
    
    // enables menu items when main file is selected in analysis pane
	public void enableMenuItems() {
		saveSBMLItem.setEnabled(true);
		saveCSVMetabolitesItem.setEnabled(true);
		saveCSVReactionsItem.setEnabled(true);
		saveSQLiteItem.setEnabled(true);
		clearItem.setEnabled(true);
		fbaItem.setEnabled(true);
		gdbbItem.setEnabled(true);
		addReacRowItem.setEnabled(true);
		addMetabRowItem.setEnabled(true);
		addReacColumnItem.setEnabled(true);
		addMetabColumnItem.setEnabled(true);
		reactionsTableEditable = true;
		saveOptFile = false;
		formulaBar.setEditable(true);
		editorMenu.setEnabled(true);
	}
	
	// disables menu items when optimization is selected in analysis pane
	public void disableMenuItems() {
		saveSBMLItem.setEnabled(false);
		saveCSVMetabolitesItem.setEnabled(false);
		saveCSVReactionsItem.setEnabled(false);
		clearItem.setEnabled(false);
		saveSQLiteItem.setEnabled(false);
		fbaItem.setEnabled(false);
		gdbbItem.setEnabled(false);
		addReacRowItem.setEnabled(false);
		addMetabRowItem.setEnabled(false);
		addReacColumnItem.setEnabled(false);
		addMetabColumnItem.setEnabled(false);
		reactionsTableEditable = false;
		tabbedPane.setSelectedIndex(0);
		formulaBar.setEditable(false);
		formulaBar.setBackground(Color.WHITE);
		editorMenu.setEnabled(false);
		getFindReplaceDialog().replaceButton.setEnabled(false);
		getFindReplaceDialog().replaceAllButton.setEnabled(false);
		getFindReplaceDialog().replaceFindButton.setEnabled(false);
	}
    
	public void loadOptimization() {
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

			closeConnection();
			LocalConfig.getInstance().setLoadedDatabase(getOptimizePath());
			reloadTables(getOptimizePath());
		} 
	}
	
	public void deleteItemFromFileList() {
		Utilities u = new Utilities();
		closeConnection();
		Object[] options = {"    Yes    ", "    No    ",};
		int choice = JOptionPane.showOptionDialog(null, 
				GraphicalInterfaceConstants.DELETE_ASSOCIATED_FILES, 
				GraphicalInterfaceConstants.DELETE_ASSOCIATED_FILES_TITLE, 
				JOptionPane.YES_NO_OPTION, 
				JOptionPane.QUESTION_MESSAGE, 
				null, options, options[0]);
		if (choice == JOptionPane.YES_OPTION) {
			u.delete(LocalConfig.getInstance().getOptimizationFilesList().get(fileList.getSelectedIndex() - 1) + ".db");
			File f = new File(LocalConfig.getInstance().getOptimizationFilesList().get(fileList.getSelectedIndex() - 1) + ".log");
			if (f.exists()) {
				u.delete(LocalConfig.getInstance().getOptimizationFilesList().get(fileList.getSelectedIndex() - 1) + ".log");
			}
			// TODO: Determine why MIP Files do not usually delete. (???)
			File f1 = new File(LocalConfig.getInstance().getOptimizationFilesList().get(fileList.getSelectedIndex() - 1).substring(4) + "_MIP.log");						
			if (f1.exists()) {
				u.delete(LocalConfig.getInstance().getOptimizationFilesList().get(fileList.getSelectedIndex() - 1).substring(4) + "_MIP.log");						
			}
		}
		if (choice == JOptionPane.NO_OPTION) {

		}
		String fileString = "jdbc:sqlite:" + getDatabaseName() + ".db";
		LocalConfig.getInstance().setLoadedDatabase(getDatabaseName());
		try {
			Class.forName("org.sqlite.JDBC");
			Connection con = DriverManager.getConnection(fileString);
			LocalConfig.getInstance().setCurrentConnection(con);
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
	
	public void deleteAllItemsFromFileList() {
		closeConnection();
		Object[] options = {"    Yes    ", "    No    ",};
		int choice = JOptionPane.showOptionDialog(null, 
				GraphicalInterfaceConstants.DELETE_ASSOCIATED_FILES, 
				GraphicalInterfaceConstants.DELETE_ASSOCIATED_FILES_TITLE, 
				JOptionPane.YES_NO_OPTION, 
				JOptionPane.QUESTION_MESSAGE, 
				null, options, options[0]);
		if (choice == JOptionPane.YES_OPTION) {				
			deleteAllOptimizationFiles();
		}
		if (choice == JOptionPane.NO_OPTION) {

		}
		setUpTables();	
	}
	
	public void addReactionColumnCloseAction() {
		LocalConfig.getInstance().addColumnInterfaceVisible = false;
    	addReacColumnItem.setEnabled(true);
    	addMetabColumnItem.setEnabled(true);
    	getReactionColAddRenameInterface().setVisible(false);
    	getReactionColAddRenameInterface().dispose();
	}
	
	public void addMetaboliteColumnCloseAction() {
		LocalConfig.getInstance().addColumnInterfaceVisible = false;
    	addReacColumnItem.setEnabled(true);
    	addMetabColumnItem.setEnabled(true);
    	getMetaboliteColAddRenameInterface().setVisible(false);
    	getMetaboliteColAddRenameInterface().dispose();
	}
	
	public void tabToNextVisibleCell(JXTable table, ArrayList<Integer> visibleColumns) {
		// This overrides tab key and performs an action	
		// from http://www.coderanch.com/t/344392/GUI/java/Tabbing-cells-JTable
		int row = table.getSelectedRow();  
        int col = table.getSelectedColumn();  
        // Make sure we start with legal values.  
        while(col < 0) col++;  
        while(row < 0) row++;  
        // Find the next editable cell.  
        col++;
        while(!(visibleColumns.contains(col)))  
        {  
            col++;  
            if(col > table.getColumnCount()-1)  
            {  
                col = 1;  
                row = (row == table.getRowCount()-1) ? 0 : row+1;
            }  
        }
        scrollToLocation(table, row, col);
	}
	
	public static void main(String[] args) throws Exception {
		curSettings = new SettingsFactory();
		
		Class.forName("org.sqlite.JDBC");       
		DatabaseCreator databaseCreator = new DatabaseCreator();
		setDatabaseName(ConfigConstants.DEFAULT_DATABASE_NAME);
		LocalConfig.getInstance().setLoadedDatabase(ConfigConstants.DEFAULT_DATABASE_NAME);
		Connection con = DriverManager.getConnection("jdbc:sqlite:" + LocalConfig.getInstance().getDatabaseName() + ".db");
		LocalConfig.getInstance().setCurrentConnection(con);
		databaseCreator.createDatabase(LocalConfig.getInstance().getDatabaseName());
		databaseCreator.addRows(LocalConfig.getInstance().getDatabaseName(), GraphicalInterfaceConstants.BLANK_DB_METABOLITE_ROW_COUNT, GraphicalInterfaceConstants.BLANK_DB_REACTION_ROW_COUNT);
		databaseCreator.copyReactionTables(LocalConfig.getInstance().getDatabaseName(), tableCopySuffix(0));
		databaseCreator.copyMetabolitesTable(LocalConfig.getInstance().getDatabaseName(), tableCopySuffix(0));

		//based on code from http://stackoverflow.com/questions/6403821/how-to-add-an-image-to-a-jframe-title-bar
		final ArrayList<Image> icons = new ArrayList<Image>(); 
		icons.add(new ImageIcon("etc/most16.jpg").getImage()); 
		icons.add(new ImageIcon("etc/most32.jpg").getImage());


		final GraphicalInterface frame = new GraphicalInterface(con);	   

		frame.setIconImages(icons);
		frame.setSize(1000, 600);
		frame.setMinimumSize(new Dimension(800, 600));
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		// based on http://iitdu.forumsmotion.com/t593-java-swing-adding-confirmation-dialogue-for-closing-window-in-jframe
		// prevents window from closing when cancel button is pressed in Save Changes Prompt
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		showPrompt = true;
		
		// selected row default at first
		statusBar.setText("Row 1");

	}
}