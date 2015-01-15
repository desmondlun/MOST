package edu.rutgers.MOST.presentation;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.text.JTextComponent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.stream.XMLStreamException;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLReader;

import edu.rutgers.MOST.Analysis.Eflux2;
import edu.rutgers.MOST.Analysis.FBA;
import edu.rutgers.MOST.Analysis.GDBB;
import edu.rutgers.MOST.Analysis.SPOT;
import edu.rutgers.MOST.config.LocalConfig;
import edu.rutgers.MOST.data.ConfigProperties;
import edu.rutgers.MOST.data.GDBBModel;
import edu.rutgers.MOST.data.JSBMLWriter;
import edu.rutgers.MOST.data.MetaboliteFactory;
import edu.rutgers.MOST.data.MetaboliteUndoItem;
import edu.rutgers.MOST.data.Model;
import edu.rutgers.MOST.data.ModelReactionEquation;
import edu.rutgers.MOST.data.ObjectCloner;
import edu.rutgers.MOST.data.ReactionEquationUpdater;
import edu.rutgers.MOST.data.ReactionFactory;
import edu.rutgers.MOST.data.ReactionUndoItem;
import edu.rutgers.MOST.data.SBMLModelReader;
import edu.rutgers.MOST.data.SBMLProduct;
import edu.rutgers.MOST.data.SBMLReactant;
import edu.rutgers.MOST.data.SBMLReaction;
import edu.rutgers.MOST.data.SBMLReactionEquation;
import edu.rutgers.MOST.data.SettingsConstants;
import edu.rutgers.MOST.data.SettingsFactory;
import edu.rutgers.MOST.data.Solution;
import edu.rutgers.MOST.data.TextMetabolitesModelReader;
import edu.rutgers.MOST.data.TextMetabolitesWriter;
import edu.rutgers.MOST.data.TextReactionsModelReader;
import edu.rutgers.MOST.data.TextReactionsWriter;
import edu.rutgers.MOST.data.UndoConstants;
import edu.rutgers.MOST.logic.ReactionParser;
import edu.rutgers.MOST.optimization.solvers.GurobiSolver;

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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import layout.TableLayout;

public class GraphicalInterface extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static JToolBar toolbar = new JToolBar("Toolbar", JToolBar.HORIZONTAL);
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
	public static JTextArea formulaBar = new JTextArea();
	public static JScrollPane formulaPane = new JScrollPane(formulaBar);

	public static JXTable reactionsTable = new JXTable(){  
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public boolean isCellEditable(int row, int column){	    	   
			if (!isRoot || analysisRunning) {	
				return false;					
			}
			if (column == GraphicalInterfaceConstants.REACTION_EQUN_NAMES_COLUMN) {
				return false;
			}
			return true;  
		}

		public Component prepareEditor(
				TableCellEditor editor, int row, int column)
		{
			Component c = super.prepareEditor(editor, row, column);

			if (c instanceof JTextComponent)
			{
				((JTextField)c).selectAll();
			}

			return c;
		}		
	}; 

	public static JXTable metabolitesTable = new JXTable(){  
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public boolean isCellEditable(int row, int column){	    	   
			if (!isRoot || analysisRunning) {	
				return false;					
			}
			return true;  
		}

		public Component prepareEditor(
				TableCellEditor editor, int row, int column)
		{
			Component c = super.prepareEditor(editor, row, column);

			if (c instanceof JTextComponent)
			{
				((JTextField)c).selectAll();
			}

			return c;
		}		
	}; 

	protected GraphicalInterface gi;

	public static DefaultListModel<String> listModel = new DefaultListModel<String>();
	public static synchronized int getListModelSize()
	{
		return listModel.size();
	}

	public static JTextArea outputTextArea = new JTextArea();
	public static JScrollPane outputPane = new JScrollPane(outputTextArea);

	public static JLabel statusBar = new JLabel();

	//set tabs south (bottom) = 3
	public static JTabbedPane tabbedPane = new JTabbedPane(3); 

	//Methods of saving current directory
	public static SettingsFactory curSettings;

	public javax.swing.Timer gdbbTimer = null;

	static protected Runnable solutionListener = null;
	
	private Task task;	
	public final static ProgressBar progressBar = new ProgressBar();	
	javax.swing.Timer timer = new javax.swing.Timer(100, new TimeListener());

	/*****************************************************************************/
	// boolean values
	/*****************************************************************************/

	public boolean showIdColumn;

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
	public static boolean reactionsReplace;
	public static boolean metabolitesReplace;
	// paste
	public static boolean validPaste;                 // used for error message when pasting non-valid values
	public static boolean showDuplicatePrompt;
	public static boolean duplicateMetabOK;
	public static boolean participatingMessageShown;
	public static boolean pasteOutOfRangeErrorShown;
	public static boolean continuePasting;
	public static boolean showDuplicateReacPrompt;
	public static boolean duplicateReacOK;
	// other
	public static boolean showErrorMessage;
	public static boolean saveOptFile;
	public static boolean addReacColumn;              // used to scroll added column to visible
	public static boolean addMetabColumn;             // used to scroll added column to visible
	public static boolean duplicatePromptShown;		  // ensures "Duplicate Metabolite" prompt displayed once per event
	public static boolean renameMetabolite;           // if Rename menu action determines used, is set to true to for OK button action          
	public static boolean reactionsTableEditable;	  
	public static boolean reactionCancelLoad;
	public static boolean isRoot;
	public static boolean openFileChooser;
	public static boolean showMetaboliteRenameInterface;
	public static boolean addMetabolite;
	public boolean enterPressed;
	public boolean reactionsUndo;
	public boolean saveChangesOKClicked;
	public boolean mpsPopoutVisible;
	// save
	public boolean saveFile;
	public boolean saveSBML;
	public boolean saveEnabled;
	public static boolean showJSBMLFileChooser;
	public boolean modelCollectionLoad;
	// close
	public static boolean exit;
	// GDBB
	public boolean gdbbSelected;
	public boolean gdbbStopped;
	public boolean gdbbRunning;
	public boolean gdbbProcessed;
	
	public boolean fluxesSet;
	public static boolean analysisRunning;

	/*****************************************************************************/
	// end boolean values
	/*****************************************************************************/ 

	/*****************************************************************************/
	// components
	/*****************************************************************************/		

	private static AboutDialog aboutDialog;
	
	private static AboutDialog getAboutDialog() {
		return aboutDialog;
	}

	private static void setAboutDialog(AboutDialog aboutDialog) {
		GraphicalInterface.aboutDialog = aboutDialog;
	}

	private static AddMetaboliteRowsDialog addMetaboliteRowsDialog;
	
	public static AddMetaboliteRowsDialog getAddMetaboliteRowsDialog() {
		return addMetaboliteRowsDialog;
	}

	public static void setAddMetaboliteRowsDialog(
			AddMetaboliteRowsDialog addMetaboliteRowsDialog) {
		GraphicalInterface.addMetaboliteRowsDialog = addMetaboliteRowsDialog;
	}

	private static AddReactionRowsDialog addReactionRowsDialog;

	public static AddReactionRowsDialog getAddReactionRowsDialog() {
		return addReactionRowsDialog;
	}

	public static void setAddReactionRowsDialog(
			AddReactionRowsDialog addReactionRowsDialog) {
		GraphicalInterface.addReactionRowsDialog = addReactionRowsDialog;
	}

	private static CSVLoadInterface csvLoadInterface;
	
	public static CSVLoadInterface getCSVLoadInterface() {
		return csvLoadInterface;
	}

	public static void setCSVLoadInterface(CSVLoadInterface csvLoadInterface) {
		GraphicalInterface.csvLoadInterface = csvLoadInterface;
	}
	
	private static CSVSaveInterface csvSaveInterface;

	private static CSVSaveInterface getCSVSaveInterface() {
		return csvSaveInterface;
	}

	private static void setCSVSaveInterface(CSVSaveInterface csvSaveInterface) {
		GraphicalInterface.csvSaveInterface = csvSaveInterface;
	}

	private static FindReplaceDialog findReplaceDialog;

	public void setFindReplaceDialog(FindReplaceDialog findReplaceDialog) {
		GraphicalInterface.findReplaceDialog = findReplaceDialog;
	}

	public static FindReplaceDialog getFindReplaceDialog() {
		return findReplaceDialog;
	}
	
	private static GDBBDialog gdbbDialog;

	public static GDBBDialog getGdbbDialog() {
		return gdbbDialog;
	}

	public static void setGdbbDialog(GDBBDialog gdbbDialog) {
		GraphicalInterface.gdbbDialog = gdbbDialog;
	}

	private static GurobiParametersDialog gurobiParametersDialog;
	
	public static GurobiParametersDialog getGurobiParametersDialog() {
		return gurobiParametersDialog;
	}

	private static MetaboliteColAddRenameInterface metaboliteColAddRenameInterface;   

	public void setMetaboliteColAddRenameInterface(MetaboliteColAddRenameInterface metaboliteColAddRenameInterface) {
		GraphicalInterface.metaboliteColAddRenameInterface = metaboliteColAddRenameInterface;
	}

	public static MetaboliteColAddRenameInterface getMetaboliteColAddRenameInterface() {
		return metaboliteColAddRenameInterface;
	}

	private static MetaboliteColumnNameInterface metaboliteColumnNameInterface;

	public static MetaboliteColumnNameInterface getMetaboliteColumnNameInterface() {
		return metaboliteColumnNameInterface;
	}

	public static void setMetaboliteColumnNameInterface(
			MetaboliteColumnNameInterface metaboliteColumnNameInterface) {
		GraphicalInterface.metaboliteColumnNameInterface = metaboliteColumnNameInterface;
	}

	private static MetaboliteRenameInterface metaboliteRenameInterface;

	public void setMetaboliteRenameInterface(MetaboliteRenameInterface metaboliteRenameInterface) {
		GraphicalInterface.metaboliteRenameInterface = metaboliteRenameInterface;
	}

	public static MetaboliteRenameInterface getMetaboliteRenameInterface() {
		return metaboliteRenameInterface;
	}

	private static ModelCollectionTable modelCollectionTable;

	public static ModelCollectionTable getModelCollectionTable() {
		return modelCollectionTable;
	}

	public static void setModelCollectionTable(ModelCollectionTable modelCollectionTable) {
		GraphicalInterface.modelCollectionTable = modelCollectionTable;
	}
	
	private static MPSPopout mpsPopout;

	public static MPSPopout getMpsPopout() {
		return mpsPopout;
	}

	public static void setMpsPopout(MPSPopout mpsPopout) {
		GraphicalInterface.mpsPopout = mpsPopout;
	}

	private static OutputPopout popout;

	public void setPopout(OutputPopout popout) {
		GraphicalInterface.popout = popout;
	}

	public static OutputPopout getPopout() {
		return popout;
	}

	private static ReactionColAddRenameInterface reactionColAddRenameInterface;

	public void setReactionColAddRenameInterface(ReactionColAddRenameInterface reactionColAddRenameInterface) {
		GraphicalInterface.reactionColAddRenameInterface = reactionColAddRenameInterface;
	}

	public static ReactionColAddRenameInterface getReactionColAddRenameInterface() {
		return reactionColAddRenameInterface;
	}

	private static ReactionColumnNameInterface reactionColumnNameInterface;

	public static ReactionColumnNameInterface getReactionColumnNameInterface() {
		return reactionColumnNameInterface;
	}

	public static void setReactionColumnNameInterface(
			ReactionColumnNameInterface reactionColumnNameInterface) {
		GraphicalInterface.reactionColumnNameInterface = reactionColumnNameInterface;
	}

	private static ReactionEditor reactionEditor;

	public void setReactionEditor(ReactionEditor reactionEditor) {
		GraphicalInterface.reactionEditor = reactionEditor;
	}

	public static ReactionEditor getReactionEditor() {
		return reactionEditor;
	}

	private static SuspiciousMetabolitesDialog suspiciousMetabolitesDialog = new SuspiciousMetabolitesDialog();

	public static SuspiciousMetabolitesDialog getSuspiciousMetabolitesDialog() {
		return suspiciousMetabolitesDialog;
	}

	public static void setSuspiciousMetabolitesDialog(
			SuspiciousMetabolitesDialog suspiciousMetabolitesDialog) {
		GraphicalInterface.suspiciousMetabolitesDialog = suspiciousMetabolitesDialog;
	}
	
	private SolverSetUpDialog solvSetUpDlg;

	public SolverSetUpDialog getSolverSetUpDialog() {
		return solvSetUpDlg;
	}

	public void setSolverSetUpDialog(SolverSetUpDialog solverSetUpDialog) {
		this.solvSetUpDlg = solverSetUpDialog;
	}

	private DynamicTreePanel treePanel;

	private String urlString;
	
	public String getUrlString() {
		return urlString;
	}

	public void setUrlString(String urlString) {
		this.urlString = urlString;
	}

	/*****************************************************************************/
	// end components
	/*****************************************************************************/

	/*****************************************************************************/
	// find replace
	/*****************************************************************************/

	private static ArrayList<ArrayList<Integer>> metabolitesFindLocationsList;

	public static ArrayList<ArrayList<Integer>> getMetabolitesFindLocationsList() {
		return metabolitesFindLocationsList;
	}

	public static void setMetabolitesFindLocationsList(
			ArrayList<ArrayList<Integer>> metabolitesFindLocationsList) {
		GraphicalInterface.metabolitesFindLocationsList = metabolitesFindLocationsList;
	}

	private static ArrayList<Integer> metabolitesReplaceLocation;

	public static ArrayList<Integer> getMetabolitesReplaceLocation() {
		return metabolitesReplaceLocation;
	}

	public static void setMetabolitesReplaceLocation(ArrayList<Integer> metabolitesReplaceLocation) {
		GraphicalInterface.metabolitesReplaceLocation = metabolitesReplaceLocation;
	}

	private static ArrayList<ArrayList<Integer>> reactionsFindLocationsList;

	public static ArrayList<ArrayList<Integer>> getReactionsFindLocationsList() {
		return reactionsFindLocationsList;
	}

	public static void setReactionsFindLocationsList(
			ArrayList<ArrayList<Integer>> reactionsFindLocationsList) {
		GraphicalInterface.reactionsFindLocationsList = reactionsFindLocationsList;
	}

	private static ArrayList<Integer> reactionsReplaceLocation;

	public static ArrayList<Integer> getReactionsReplaceLocation() {
		return reactionsReplaceLocation;
	}

	public static void setReactionsReplaceLocation(ArrayList<Integer> reactionsReplaceLocation) {
		GraphicalInterface.reactionsReplaceLocation = reactionsReplaceLocation;
	}

	private static String replaceAllError;

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
	// gdbb items
	/*****************************************************************************/
	
	private int timeCount;
	private int dotCount;
	
	private Map<String, ArrayList<Double>> gdbbFluxesMap;
	
	public Map<String, ArrayList<Double>> getGdbbFluxesMap() {
		return gdbbFluxesMap;
	}

	public void setGdbbFluxesMap(Map<String, ArrayList<Double>> gdbbFluxesMap) {
		this.gdbbFluxesMap = gdbbFluxesMap;
	}
	
	private String dateTimeStamp;
	
	public String getDateTimeStamp() {
		return dateTimeStamp;
	}

	public void setDateTimeStamp(String dateTimeStamp) {
		this.dateTimeStamp = dateTimeStamp;
	}
	
	/*****************************************************************************/
	// end gdbb items
	/*****************************************************************************/	

	/*****************************************************************************/
	// menu items
	/*****************************************************************************/

	public final JMenuItem loadSBMLItem = new JMenuItem("Load SBML");
	public final JMenuItem loadCSVItem = new JMenuItem("Load CSV");
	public final JMenuItem loadExistingItem = new JMenuItem(GraphicalInterfaceConstants.LOAD_FROM_MODEL_COLLECTION_TABLE_TITLE);
	public final JMenuItem saveItem = new JMenuItem("Save");
	public final JMenuItem saveSBMLItem = new JMenuItem("Save As SBML");
	public final JMenuItem saveCSVItem = new JMenuItem("Save As CSV");
	public final JMenuItem clearItem = new JMenuItem("Clear Tables");
	public final JMenuItem exitItem = new JMenuItem("Exit");
	public final JMenuItem fbaItem = new JMenuItem("FBA");
	public final JMenuItem gdbbItem = new JMenuItem("GDBB");
	public final JMenuItem eflux2Item = new JMenuItem( "E-Flux2" );
	public final JMenuItem spotItem = new JMenuItem( "SPOT" );	
	public final JMenuItem aboutBox = new JMenuItem("About MOST");
	public final JCheckBoxMenuItem highlightUnusedMetabolitesItem = new JCheckBoxMenuItem("Highlight Unused Metabolites");
	public final JMenuItem deleteUnusedItem = new JMenuItem("Delete All Unused Metabolites");
	public final JMenuItem findSuspiciousItem = new JMenuItem("Find Suspicious Metabolites");
	public final JMenuItem undoItem = new JMenuItem("Undo");
	public final JMenuItem redoItem = new JMenuItem("Redo");
	public final JMenuItem findReplaceItem = new JMenuItem("Find/Replace");	
	public final JMenuItem selectAllItem = new JMenuItem("SelectAll");	
	public final JMenuItem addReacRowItem = new JMenuItem("Add Row to Reactions Table");
	public final JMenuItem addReacRowsItem = new JMenuItem("Add Rows to Reactions Table");
	public final JMenuItem addMetabRowItem = new JMenuItem("Add Row to Metabolites Table");
	public final JMenuItem addMetabRowsItem = new JMenuItem("Add Rows to Metabolites Table");
	public final JMenuItem addReacColumnItem = new JMenuItem("Add Column to Reactions Table");
	public final JMenuItem addMetabColumnItem = new JMenuItem("Add Column to Metabolites Table"); 
	public final JMenuItem deleteReactionRowMenuItem = new JMenuItem("Delete Row(s)");
	public final JMenuItem deleteMetaboliteRowMenuItem = new JMenuItem("Delete Row(s)");
	public final JMenuItem editorMenu = new JMenuItem("Launch Reaction Editor");
	public final JMenuItem unsortReacMenuItem = new JMenuItem("Unsort Reactions Table");
	public final JMenuItem unsortMetabMenuItem = new JMenuItem("Unsort Metabolites Table");
	public final JMenuItem setUpSolver = new JMenuItem("Select Solvers");
	public final JMenuItem gurobiParametersItem = new JMenuItem(GurobiParameters.GUROBI_PARAMETERS_MENU_ITEM);
	public final JMenuItem glpkParametersItem = new JMenuItem( GLPKParameters.GLPK_PARAMETERS_MENU_ITEM );
	public final JMenuItem ipOptParametersItem = new JMenuItem( IPoptParameters.IPOPT_PARAMETERS_MENU_ITEM );
	
	public final JMenuItem formulaBarCutItem = new JMenuItem("Cut");
	public final JMenuItem formulaBarCopyItem = new JMenuItem("Copy");
	public final JMenuItem formulaBarPasteItem = new JMenuItem("Paste");
	public final JMenuItem formulaBarDeleteItem = new JMenuItem("Delete");
	public final JMenuItem formulaBarSelectAllItem = new JMenuItem("Select All");

	public final JMenuItem outputCopyItem = new JMenuItem("Copy");
	public final JMenuItem outputSelectAllItem = new JMenuItem("Select All");
	public final JMenuItem mpsItem = new JMenuItem("View MPS File");

	public final JMenuItem unhighlightParticipatingReactionsMenu = new JMenuItem("Un-Highlight Participating Reactions");
	public final JMenuItem unhighlightMenu = new JMenuItem("Un-Highlight Participating Reactions");

	/*****************************************************************************/
	// end menu items
	/*****************************************************************************/	

	/*****************************************************************************/
	// misc
	/*****************************************************************************/
	
	private static class GISolution
	{
		protected StringBuffer stringBuffer = new StringBuffer();
		protected ArrayList< Double > soln = new ArrayList< Double >();
		protected ArrayList< Double > vaMin = null;
		protected ArrayList< Double > vaMax = null;
		protected boolean isFoldered = false;
		protected String folderName = "";
		protected Integer knockoutOffset = null;
		protected ArrayList< Double > soln_ko = null;
		public Model model = null;
		public boolean updateGUIWithModel = false;
	}
	private static Vector< GISolution > vecGISolution = new Vector< GISolution >();

	private static int currentMetabolitesRow;

	public void setCurrentMetabolitesRow(int currentMetabolitesRow){
		GraphicalInterface.currentMetabolitesRow = currentMetabolitesRow;
	}

	public static int getCurrentMetabolitesRow() {
		return currentMetabolitesRow;
	}

	private static int currentMetabolitesColumn;

	public void setCurrentMetabolitesColumn(int currentMetabolitesColumn){
		GraphicalInterface.currentMetabolitesColumn = currentMetabolitesColumn;
	}

	public static int getCurrentMetabolitesColumn() {
		return currentMetabolitesColumn;
	}

	private static int currentReactionsRow;

	public void setCurrentReactionsRow(int currentReactionsRow){
		GraphicalInterface.currentReactionsRow = currentReactionsRow;
	}

	public static int getCurrentReactionsRow() {
		return currentReactionsRow;
	}

	private static int currentReactionsColumn;

	public void setCurrentReactionsColumn(int currentReactionsColumn){
		GraphicalInterface.currentReactionsColumn = currentReactionsColumn;
	}

	public static int getCurrentReactionsColumn() {
		return currentReactionsColumn;
	}

	private static String fileType;
	
	public static String getFileType() {
		return fileType;
	}

	public static void setFileType(String fileType) {
		GraphicalInterface.fileType = fileType;
	}
	
	private static String gurobiPath;

	public static String getGurobiPath() {
		return gurobiPath;
	}

	public static void setGurobiPath(String gurobiPath) {
		GraphicalInterface.gurobiPath = gurobiPath;
	}

	private static ArrayList<Image> icons;

	public void setIconsList(ArrayList<Image> icons) {
		GraphicalInterface.icons = icons;
	}

	public static ArrayList<Image> getIconsList() {
		return icons;
	}    

	private static String loadErrorMessage;

	public void setLoadErrorMessage(String loadErrorMessage) {
		GraphicalInterface.loadErrorMessage = loadErrorMessage;
	}

	public static String getLoadErrorMessage() {
		return loadErrorMessage;
	}

	private static String oldReaction;

	public void setOldReaction(String oldReaction) {
		GraphicalInterface.oldReaction = oldReaction;
	}

	public static String getOldReaction() {
		return oldReaction;
	}

	private static String optimizeName;
	
	public static String getOptimizeName() {
		return optimizeName;
	}

	public static void setOptimizeName(String optimizeName) {
		GraphicalInterface.optimizeName = optimizeName;
	}

	private static String participatingMetabolite;

	public void setParticipatingMetabolite(String participatingMetabolite) {
		GraphicalInterface.participatingMetabolite = participatingMetabolite;
	}

	public static String getParticipatingMetabolite() {
		return participatingMetabolite;
	}

	private static String pasteError;

	public void setPasteError(String pasteError) {
		GraphicalInterface.pasteError = pasteError;
	}

	public static String getPasteError() {
		return pasteError;
	}

	private static int selectionMode;

	public void setSelectionMode(int selectionMode){
		GraphicalInterface.selectionMode = selectionMode;
	}

	public static int getSelectionMode() {
		return selectionMode;
	}

	private static File SBMLFile;

	public void setSBMLFile(File SBMLFile) {
		GraphicalInterface.SBMLFile = SBMLFile;
	}

	public static File getSBMLFile() {
		return SBMLFile;
	}

	private static String tableCellNewValue;

	public void setTableCellNewValue(String tableCellNewValue) {
		GraphicalInterface.tableCellNewValue = tableCellNewValue;
	}

	public static String getTableCellNewValue() {
		return tableCellNewValue;
	} 

	private static String tableCellOldValue;

	public void setTableCellOldValue(String tableCellOldValue) {
		GraphicalInterface.tableCellOldValue = tableCellOldValue;
	}

	public static String getTableCellOldValue() {
		return tableCellOldValue;
	}  
	
	private static ArrayList<Integer> visibleReactionsColumns;

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
	// solvers
	/*****************************************************************************/
	
	private static String mixedIntegerLinearSolverName;
	
	public static String getMixedIntegerLinearSolverName() {
		return mixedIntegerLinearSolverName;
	}

	public static void setMixedIntegerLinearSolverName(
			String mixedIntegerLinearSolverName) {
		GraphicalInterface.mixedIntegerLinearSolverName = mixedIntegerLinearSolverName;
	}
	
	private static String nonlinearSolverName;
	
	public static String getNonlinearSolverName() {
		return nonlinearSolverName;
	}

	public static void setNonlinearSolverName(String nonlinearSolverName) {
		GraphicalInterface.nonlinearSolverName = nonlinearSolverName;
	}

	private static String quadraticSolverName;

	public static String getQuadraticSolverName() {
		return quadraticSolverName;
	}

	public static void setQuadraticSolverName(String quadraticSolverName) {
		GraphicalInterface.quadraticSolverName = quadraticSolverName;
	}
	
	/*****************************************************************************/
	// end solvers
	/*****************************************************************************/
	
	/*****************************************************************************/
	// solver properties
	/*****************************************************************************/
	
	private static AbstractParametersDialog gurobiParameters = null;
	public static AbstractParametersDialog getGurobiParameters()
	{
		return gurobiParameters;
	}
	private static AbstractParametersDialog glpkParameters = null;
	public static AbstractParametersDialog getGLPKParameters()
	{
		return glpkParameters;
	}
	private static AbstractParametersDialog ipOptParameters = null;
	public static AbstractParametersDialog getIPOptParameters()
	{
		return ipOptParameters;
	}
	
	/*****************************************************************************/
	// end solver properties
	/*****************************************************************************/

	/*****************************************************************************/
	// sorting
	/*****************************************************************************/

	private static int metabolitesSortColumnIndex;

	public void setMetabolitesSortColumnIndex(int metabolitesSortColumnIndex){
		GraphicalInterface.metabolitesSortColumnIndex = metabolitesSortColumnIndex;
	}

	public static int getMetabolitesSortColumnIndex() {
		return metabolitesSortColumnIndex;
	}

	private static SortOrder metabolitesSortOrder;

	public void setMetabolitesSortOrder(SortOrder metabolitesSortOrder){
		GraphicalInterface.metabolitesSortOrder = metabolitesSortOrder;
	}

	public static SortOrder getMetabolitesSortOrder() {
		return metabolitesSortOrder;
	}

	private static int metabolitesOldSortColumnIndex;
	
	public static int getMetabolitesOldSortColumnIndex() {
		return metabolitesOldSortColumnIndex;
	}

	public static void setMetabolitesOldSortColumnIndex(
			int metabolitesOldSortColumnIndex) {
		GraphicalInterface.metabolitesOldSortColumnIndex = metabolitesOldSortColumnIndex;
	}

	private static SortOrder metabolitesOldSortOrder;
	
	public void setMetabolitesOldSortOrder(SortOrder metabolitesOldSortOrder){
		GraphicalInterface.metabolitesOldSortOrder = metabolitesOldSortOrder;
	}

	public static SortOrder getMetabolitesOldSortOrder() {
		return metabolitesOldSortOrder;
	}
	
	private static int reactionsSortColumnIndex;

	public void setReactionsSortColumnIndex(int reactionsSortColumnIndex){
		GraphicalInterface.reactionsSortColumnIndex = reactionsSortColumnIndex;
	}

	public static int getReactionsSortColumnIndex() {
		return reactionsSortColumnIndex;
	}

	private static int reactionsOldSortColumnIndex;
	
	public static int getReactionsOldSortColumnIndex() {
		return reactionsOldSortColumnIndex;
	}

	public static void setReactionsOldSortColumnIndex(
			int reactionsOldSortColumnIndex) {
		GraphicalInterface.reactionsOldSortColumnIndex = reactionsOldSortColumnIndex;
	}

	private static SortOrder reactionsSortOrder;

	public void setReactionsSortOrder(SortOrder reactionsSortOrder){
		GraphicalInterface.reactionsSortOrder = reactionsSortOrder;
	}

	public static SortOrder getReactionsSortOrder() {
		return reactionsSortOrder;
	}
	
	private static SortOrder reactionsOldSortOrder;

	public static SortOrder getReactionsOldSortOrder() {
		return reactionsOldSortOrder;
	}

	public static void setReactionsOldSortOrder(SortOrder reactionsOldSortOrder) {
		GraphicalInterface.reactionsOldSortOrder = reactionsOldSortOrder;
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

	public GraphicalInterface() {		
		// make this true only when troubleshooting, false for actual use
//		showIdColumn = true;
		showIdColumn = false;

		gi = this;

		isRoot = true;
		
		LocalConfig.getInstance().fvaColumnsVisible = false;
		LocalConfig.getInstance().fvaDone = true;
		analysisRunning = false;
		
		enableSaveItems(false);
		// setEnableAnalysisMenuItems( false );

		// Tree Panel
		treePanel = new DynamicTreePanel(new DynamicTree() {
			private static final long serialVersionUID = 1L;

			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)
						tree.getLastSelectedPathComponent();

				if (node == null) return;
				
				// close mps popout if selected node changed
				if (getMpsPopout() != null) {
					getMpsPopout().setVisible(false);
					getMpsPopout().dispose();
					mpsPopoutVisible = false;
				}

				Solution nodeInfo = (Solution)node.getUserObject();
				String solutionName = nodeInfo.getSolutionName();
				String databaseName = nodeInfo.getDatabaseName();
				Utilities u = new Utilities();
				maybeShowFVAColumns(solutionName);
				if (node.isLeaf()) {
					if (solutionName != null) {
						if (solutionName.equals(LocalConfig.getInstance().getModelName())) {
							isRoot = true;
							saveOptFile = false;
							setUpReactionsTable(LocalConfig.getInstance().getReactionsTableModelMap().get(solutionName));
							setUpMetabolitesTable(LocalConfig.getInstance().getMetabolitesTableModelMap().get(solutionName));
							setTitle(GraphicalInterfaceConstants.TITLE + " - " + solutionName);
							clearOutputPane();
							if (getPopout() != null) {
								getPopout().clear();
								getPopout().setTitle(gi.getTitle());
							}	
							enableMenuItems();
						} else {
							saveOptFile = true;
							if (node.getUserObject().toString() != null) {
								setUpReactionsTable(LocalConfig.getInstance().getReactionsTableModelMap().get(databaseName));
								setUpMetabolitesTable(LocalConfig.getInstance().getMetabolitesTableModelMap().get(databaseName));
								// only set table models if gdbb not running to prevent
								// out of range error, fluxes and knockouts set when item
								// selected, node info index > -1 is so FBA items will not 
								// execute this code
								if (!gdbbRunning && nodeInfo.getIndex() > -1) {
									ReactionFactory rFactory = new ReactionFactory("SBML");
								    rFactory.setFluxes(getGdbbFluxesMap().get(nodeInfo.getSolutionName()), GraphicalInterfaceConstants.FLUX_VALUE_COLUMN,
								    		LocalConfig.getInstance().getReactionsTableModelMap().get(solutionName));
									//System.out.println(LocalConfig.getInstance().getGdbbKnockoutsMap().get(nodeInfo.getSolutionName()));
									rFactory.updateKnockouts(LocalConfig.getInstance().getGdbbKnockoutsMap().get(nodeInfo.getSolutionName()));
								}								
								loadOutputPane(u.createLogFileName(databaseName + ".log"));
								isRoot = false;	
								disableMenuItems();
								setTitle(GraphicalInterfaceConstants.TITLE + " - " + databaseName);
								if (getPopout() != null) {
									getPopout().load(u.createLogFileName(databaseName + ".log"), gi.getTitle());
								}
//								if (nodeInfo.getIndex() > -1) {
//									setTitle(GraphicalInterfaceConstants.TITLE + " - " + nodeInfo.getDatabaseName() + "_[" + nodeInfo.getIndex() + "]");
//									if (getPopout() != null) {
//										getPopout().load(u.createLogFileName(nodeInfo.getSolutionName() + ".log"), gi.getTitle());
//									}
//								} else {
//									setTitle(GraphicalInterfaceConstants.TITLE + " - " + solutionName);
//									if (getPopout() != null) {
//										getPopout().load(u.createLogFileName(solutionName + ".log"), gi.getTitle());
//									}
//								}																				
							}
						}		
					}								
				} else {
					// do nothing when folder is clicked
//					saveOptFile = true;
//					if (node.getUserObject().toString() != null) {
//						setUpReactionsTable(LocalConfig.getInstance().getReactionsTableModelMap().get(solutionName));
//						setUpMetabolitesTable(LocalConfig.getInstance().getMetabolitesTableModelMap().get(solutionName));
//						setTitle(GraphicalInterfaceConstants.TITLE + " - " + solutionName);
//						loadOutputPane(u.createLogFileName(databaseName + ".log"));
//						if (getPopout() != null) {
//							getPopout().load(u.createLogFileName(databaseName + ".log"), gi.getTitle());
//						}										
//						disableMenuItems();						
//						isRoot = false;	
//					}					
				}
			}
		});

		// this code must be before any components if the components
		// are going to have an image icon
		final ArrayList<Image> icons = new ArrayList<Image>(); 
		icons.add(new ImageIcon("etc/most16.jpg").getImage()); 
		icons.add(new ImageIcon("etc/most32.jpg").getImage());
		setIconsList(icons);

		LocalConfig.getInstance().setProgress(0);
		progressBar.pack();
		progressBar.setIconImages(icons);
		progressBar.setSize(GraphicalInterfaceConstants.PROGRESS_BAR_WIDTH, GraphicalInterfaceConstants.PROGRESS_BAR_HEIGHT);		
		progressBar.setResizable(false);
		progressBar.setTitle("Loading...");
		//progressBar.progress.setIndeterminate(true);
		progressBar.setLocationRelativeTo(null);
		progressBar.setVisible(false);
		progressBar.setAlwaysOnTop(true);

		CSVLoadInterface csvLoadInterface = new CSVLoadInterface();
		csvLoadInterface.setIconImages(icons);					
		csvLoadInterface.setSize(600, 200);
		csvLoadInterface.setResizable(false);
		csvLoadInterface.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		csvLoadInterface.setLocationRelativeTo(null);		
		csvLoadInterface.setVisible(false);	
		csvLoadInterface.setModal(true);
		setCSVLoadInterface(csvLoadInterface);
		csvLoadInterface.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				getCSVLoadInterface().setVisible(false);	        	
			}
		});			
		getCSVLoadInterface().okButton.addActionListener(okButtonCSVLoadActionListener);
		getCSVLoadInterface().cancelButton.addActionListener(cancelButtonCSVLoadActionListener);
		
		CSVSaveInterface csvSaveInterface = new CSVSaveInterface();
		csvSaveInterface.setIconImages(icons);					
		csvSaveInterface.setSize(600, 200);
		csvSaveInterface.setResizable(false);
		csvSaveInterface.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		csvSaveInterface.setLocationRelativeTo(null);		
		csvSaveInterface.setVisible(false);	
		csvSaveInterface.setModal(true);
		setCSVSaveInterface(csvSaveInterface);
		csvSaveInterface.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				getCSVSaveInterface().setVisible(false);	        	
			}
		});			
		getCSVSaveInterface().okButton.addActionListener(okButtonCSVSaveActionListener);
		getCSVSaveInterface().cancelButton.addActionListener(cancelButtonCSVSaveActionListener);
		
		AboutDialog aboutDialog = new AboutDialog();
		aboutDialog.setIconImages(icons);					
		aboutDialog.setSize(400, 180);
		aboutDialog.setResizable(false);
		aboutDialog.setLocationRelativeTo(null);		
		aboutDialog.setVisible(false);	
		aboutDialog.setModal(true);
		setAboutDialog(aboutDialog);
		getAboutDialog().licenseButton.addActionListener(new OpenUrlAction());
		
		File f = new File(ModelCollectionConstants.MODEL_COLLECTION_FILE_NAME);
		ModelCollectionTable mcTable = new ModelCollectionTable(f);
		mcTable.setIconImages(icons);
		mcTable.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		mcTable.setAlwaysOnTop(true);				
		mcTable.setLocationRelativeTo(null);
		mcTable.setVisible(false);
		setModelCollectionTable(mcTable);
		ModelCollectionTable.okButton.addActionListener(modelCollectionOKButtonActionListener);
		ModelCollectionTable.cancelButton.addActionListener(modelCollectionCancelButtonActionListener);
		mcTable.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				loadExistingItem.setEnabled(true);
				getModelCollectionTable().setVisible(false);
				getModelCollectionTable().dispose();
				enableLoadItems();
				disableMenuItemsForFVA(false);
			}
		});
		
		SuspiciousMetabolitesDialog suspiciousMetabolitesDialog = new SuspiciousMetabolitesDialog();
		suspiciousMetabolitesDialog.setIconImages(icons);
		suspiciousMetabolitesDialog.setSize(GraphicalInterfaceConstants.SUSPICIOUS_METABOLITES_DIALOG_WIDTH, 
				GraphicalInterfaceConstants.SUSPICIOUS_METABOLITES_DIALOG_HEIGHT);
		suspiciousMetabolitesDialog.setResizable(false);
		suspiciousMetabolitesDialog.setLocationRelativeTo(null);		
		suspiciousMetabolitesDialog.setVisible(false);	
		suspiciousMetabolitesDialog.setModal(true);
		SuspiciousMetabolitesDialog.messageButton.addActionListener(new OpenUrlAction());
		setSuspiciousMetabolitesDialog(suspiciousMetabolitesDialog);
		
		setSolverSetUpDialog( new SolverSetUpDialog() );
		getSolverSetUpDialog().setIconImages(icons);					
		getSolverSetUpDialog().setSize(GraphicalInterfaceConstants.SOLVER_DIALOG_WIDTH, GraphicalInterfaceConstants.SOLVER_DIALOG_HEIGHT);
		getSolverSetUpDialog().setResizable(false);
		getSolverSetUpDialog().setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		getSolverSetUpDialog().setLocationRelativeTo(null);		
		getSolverSetUpDialog().setModal(true);
		getSolverSetUpDialog().okButton.addActionListener(solvOKActionListener);
		getSolverSetUpDialog().addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				getSolverSetUpDialog().setVisible(false);
				getSolverSetUpDialog().dispose();
			}
		});	
		
		gurobiParameters = new GurobiParametersDialog( new File( Utilities.getMOSTSettingsPath() + "Gurobi.properties" ) );
		glpkParameters = new GLPKParametersDialog( new File( Utilities.getMOSTSettingsPath() + "GLPK.properties" ) );
		ipOptParameters = new IPoptParametersDialog( new File( Utilities.getMOSTSettingsPath() + "IPopt.properties" ) );
		
		
		setTitle(GraphicalInterfaceConstants.TITLE);
		LocalConfig.getInstance().setModelName(GraphicalInterfaceConstants.DEFAULT_MODEL_NAME);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				if (LocalConfig.getInstance().fvaDone) {
					SaveChangesPrompt();
					if (exit) {
						// Exit the application
						System.exit(0);	
					}	
				}          	        	
			}
		});

		setBooleanDefaults();
		setSortDefault();
		setUpCellSelectionMode();
		setFileType(GraphicalInterfaceConstants.DEFAULT_FILE_TYPE);
		// TODO: need to account for adding metabolites when creating model in blank gui
		LocalConfig.getInstance().setMaxMetabolite(0);
		LocalConfig.getInstance().setMaxMetaboliteId(GraphicalInterfaceConstants.BLANK_METABOLITE_ROW_COUNT);
		LocalConfig.getInstance().setMaxReactionId(GraphicalInterfaceConstants.BLANK_REACTION_ROW_COUNT);
		LocalConfig.getInstance().setReactionsLocationsListCount(0);
		LocalConfig.getInstance().setMetabolitesLocationsListCount(0);

		formulaBar.setLineWrap(true);
		outputTextArea.setEditable(false);

		listModel.addElement(GraphicalInterfaceConstants.DEFAULT_MODEL_NAME);
		DynamicTreePanel.getTreePanel().addObject(new Solution(GraphicalInterfaceConstants.DEFAULT_MODEL_NAME, GraphicalInterfaceConstants.DEFAULT_MODEL_NAME));

		// lists populated in file load
		Map<String, Object> metaboliteAbbreviationIdMap = new HashMap<String, Object>();
		LocalConfig.getInstance().setMetaboliteAbbreviationIdMap(metaboliteAbbreviationIdMap);
		Map<Object, String> metaboliteIdNameMap = new HashMap<Object, String>();
		LocalConfig.getInstance().setMetaboliteIdNameMap(metaboliteIdNameMap);
		Map<String, Object> reactionAbbreviationIdMap = new HashMap<String, Object>();
		LocalConfig.getInstance().setReactionAbbreviationIdMap(reactionAbbreviationIdMap);
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
		
		// gdbb
		Map<String, ArrayList<Double>> gdbbFluxesMap = new HashMap<String, ArrayList<Double>>();
		setGdbbFluxesMap(gdbbFluxesMap);
		ArrayList<Integer> gdbbKnockoutsList = new ArrayList<Integer>();
		LocalConfig.getInstance().setGdbbKnockoutsList(gdbbKnockoutsList);
		Map<String, ArrayList<Integer>> gdbbKnockoutsMap = new HashMap<String, ArrayList<Integer>>();
		LocalConfig.getInstance().setGdbbKnockoutsMap(gdbbKnockoutsMap);

		selectedReactionsRowStartIndex = 0;
		selectedReactionsRowEndIndex = 0;
		selectedReactionsColumnStartIndex = 1;
		selectedReactionsColumnEndIndex = 1;
		selectedMetabolitesRowStartIndex = 0;
		selectedMetabolitesRowEndIndex = 0;
		selectedMetabolitesColumnStartIndex = 1;
		selectedMetabolitesColumnEndIndex = 1;

		// miscellaneous lists and maps
		ArrayList<Integer> participatingReactions = new ArrayList<Integer>();
		LocalConfig.getInstance().setParticipatingReactions(participatingReactions);
		ArrayList<String> optimizationFilesList = new ArrayList<String>();
		LocalConfig.getInstance().setOptimizationFilesList(optimizationFilesList);
		Map<String, Object> metabDisplayCollectionMap = new HashMap<String, Object>();
		LocalConfig.getInstance().setMetabDisplayCollectionMap(metabDisplayCollectionMap);
		ArrayList<String> invalidReactions = new ArrayList<String>();
		LocalConfig.getInstance().setInvalidReactions(invalidReactions);
		ArrayList<String> showFVAColumnsList = new ArrayList<String>();
		LocalConfig.getInstance().setShowFVAColumnsList(showFVAColumnsList);

		// meta column lists
		ArrayList<String> reactionsMetaColumnNames = new ArrayList<String>();
		LocalConfig.getInstance().setReactionsMetaColumnNames(reactionsMetaColumnNames);
		ArrayList<String> metabolitesMetaColumnNames = new ArrayList<String>();
		LocalConfig.getInstance().setMetabolitesMetaColumnNames(metabolitesMetaColumnNames);

		Map<Object, ModelReactionEquation> reactionEquationMap = new HashMap<Object, ModelReactionEquation>();
		LocalConfig.getInstance().setReactionEquationMap(reactionEquationMap);
		Map<String, Object> reactionsIdRowMap = new HashMap<String, Object>();
		LocalConfig.getInstance().setReactionsIdRowMap(reactionsIdRowMap);

		// table model maps
		Map<String, DefaultTableModel> metabolitesTableModelMap = new HashMap<String, DefaultTableModel>();
		LocalConfig.getInstance().setMetabolitesTableModelMap(metabolitesTableModelMap);
		Map<String, DefaultTableModel> reactionsTableModelMap = new HashMap<String, DefaultTableModel>();
		LocalConfig.getInstance().setReactionsTableModelMap(reactionsTableModelMap);

		// undo/redo
		Map<Object, Object> undoItemMap = new HashMap<Object, Object>();
		LocalConfig.getInstance().setUndoItemMap(undoItemMap);
		Map<Object, Object> redoItemMap = new HashMap<Object, Object>();
		LocalConfig.getInstance().setRedoItemMap(redoItemMap);
		Map<String, DefaultTableModel> metabolitesUndoTableModelMap = new HashMap<String, DefaultTableModel>();
		LocalConfig.getInstance().setMetabolitesUndoTableModelMap(metabolitesUndoTableModelMap);
		Map<String, DefaultTableModel> reactionsUndoTableModelMap = new HashMap<String, DefaultTableModel>();
		LocalConfig.getInstance().setReactionsUndoTableModelMap(reactionsUndoTableModelMap);

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

		DynamicTreePanel.getTreePanel().deleteItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {
				deleteItemFromDynamicTree();
				
			}
		});

		DynamicTreePanel.getTreePanel().clearItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {
				deleteAllItemsFromDynamicTree();
			}
		});

		/**************************************************************************/
		// set up output popout
		/**************************************************************************/		

		final JPopupMenu outputPopupMenu = new JPopupMenu(); 
		outputPopupMenu.add(outputCopyItem);
		outputCopyItem.setEnabled(false);
		outputCopyItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
		outputCopyItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) { 	
				setClipboardContents(outputTextArea.getSelectedText());							
			}
		});
		outputPopupMenu.add(outputSelectAllItem);
		outputSelectAllItem.setEnabled(false);
		outputSelectAllItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
		outputSelectAllItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) { 
				outputTextArea.selectAll();							
			}
		});
		outputPopupMenu.addSeparator();
		final JMenuItem popOutItem = new JMenuItem("Pop Out");
		outputPopupMenu.add(popOutItem);
		popOutItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) { 	
				OutputPopout popout = new OutputPopout();
				popout.setIconImages(icons);
				setPopout(popout);
				popout.setSize(700, 400);
				popout.setLocationRelativeTo(null);
				popout.addWindowListener(new WindowAdapter() {
					public void windowClosing(WindowEvent evt) {
						getPopout().setVisible(false);
						getPopout().dispose();
						popOutItem.setEnabled(true);
					}
				});	
				popOutItem.setEnabled(false);
				popout.setTitle(gi.getTitle());
				popout.setOutputText(outputTextArea.getText());
			}
		});
		outputPopupMenu.addSeparator();
		outputPopupMenu.add(mpsItem);
		mpsItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) { 	
				MPSPopout mpsPopout = new MPSPopout();
				mpsPopout.setIconImages(icons);
				setMpsPopout(mpsPopout);
				mpsPopoutVisible = true;
				mpsPopout.addWindowListener(new WindowAdapter() {
					public void windowClosing(WindowEvent evt) {
						getMpsPopout().setVisible(false);
						getMpsPopout().dispose();
						mpsPopoutVisible = false;
						mpsItem.setEnabled(true);
					}
				});	
				mpsItem.setEnabled(false);
				mpsPopout.setTitle(gi.getTitle() + ".mps");
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)
						DynamicTreePanel.getTreePanel().tree.getLastSelectedPathComponent();
				Solution nodeInfo = (Solution)node.getUserObject();	
				File f = new File(Utilities.getMOSTSettingsPath() + nodeInfo.getDatabaseName() + ".mps");
				if (f.exists()) {
					mpsPopout.readFile(f);
				}
			}
		});

		outputTextArea.addMouseListener(new MouseAdapter() {

			public void mousePressed(MouseEvent e)  {check(e);}
			public void mouseReleased(MouseEvent e) {check(e);}

			public void check(MouseEvent e) {
				if (e.isPopupTrigger()) { //if the event shows the menu
					if (isRoot) {
						mpsItem.setEnabled(false);
					} else {
						DefaultMutableTreeNode node = (DefaultMutableTreeNode)
								DynamicTreePanel.getTreePanel().tree.getLastSelectedPathComponent();
						Solution nodeInfo = (Solution)node.getUserObject();	
						File f = new File(Utilities.getMOSTSettingsPath() + nodeInfo.getDatabaseName() + ".mps");
						if (f.exists()) {
							// prevent user from launching popout multiple times by
							// disabling menu item if popout is visible
							if (!mpsPopoutVisible) {
								mpsItem.setEnabled(true);
							} else {
								mpsItem.setEnabled(false);
							}
						} else {
							mpsItem.setEnabled(false);
						}
					}
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
		// create menu bar
		/**************************************************************************/

		JMenuBar menuBar = new JMenuBar();

		setJMenuBar(menuBar);

		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);

		fileMenu.add(loadSBMLItem);
		loadSBMLItem.setMnemonic(KeyEvent.VK_L);
		loadSBMLItem.addActionListener(new LoadSBMLAction());

		fileMenu.add(loadCSVItem);
		loadCSVItem.setMnemonic(KeyEvent.VK_V);
		loadCSVItem.addActionListener(new LoadCSVAction());

		fileMenu.add(loadExistingItem);
		loadExistingItem.setMnemonic(KeyEvent.VK_D);
		loadExistingItem.addActionListener(new LoadExistingItemAction());

		fileMenu.addSeparator();

		fileMenu.add(saveItem);
		saveItem.setMnemonic(KeyEvent.VK_S);
		saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		saveItem.addActionListener(new SaveItemAction());
		
		fileMenu.add(saveSBMLItem);
		saveSBMLItem.setMnemonic(KeyEvent.VK_B);
		saveSBMLItem.addActionListener(new SaveSBMLItemAction());

		fileMenu.add(saveCSVItem);
		saveCSVItem.setMnemonic(KeyEvent.VK_A);
		saveCSVItem.addActionListener(new SaveCSVItemAction());

		fileMenu.addSeparator();

		fileMenu.add(clearItem);
		clearItem.setMnemonic(KeyEvent.VK_C);
		clearItem.addActionListener(new ClearAction());

		fileMenu.addSeparator();

		fileMenu.add(exitItem);
		exitItem.setMnemonic(KeyEvent.VK_X);
		exitItem.addActionListener(new ExitAction());

		menuBar.add(fileMenu);

		//Analysis menu
		JMenu analysisMenu = new JMenu("Analysis");
		analysisMenu.setMnemonic(KeyEvent.VK_A);
		
		/**
		 * This is a generic action listener used for 
		 * the Analysis portion of the graphical interface
		 */
		abstract class AnalysisCommonActionListener implements ActionListener
		{
			private boolean bioObjWarn = true;
			protected GISolution giSolution = null;
			
			public AnalysisCommonActionListener()
			{
			}
			
			public AnalysisCommonActionListener( boolean bioObjWarn )
			{
				this.bioObjWarn = bioObjWarn;
			}
			
			@Override
			public void actionPerformed( ActionEvent a )
			{
				LocalConfig.getInstance().noBiolObjWarningShown = false;
				gdbbSelected = false;
				analysisRunning = true;
				this.giSolution = new GISolution();
				final Model model = new Model( bioObjWarn );
				Thread t = new Thread()
				{
					public void run()
					{
						disableMenuItems();
						disableLoadItems();
						disableMenuItemsForFVA(true);
						ArrayList< Double > soln = null;
						try
						{
							soln = analysisPart( model );
							fluxesSet = false;
						}
						catch( Exception e )
						{
							analysisRunning = false;
							return;
						}
						finally
						{
							enableMenuItems();
							enableLoadItems();
							disableMenuItemsForFVA(false);
						}
						//End optimization
						
						StringBuffer outputText = new StringBuffer();
						outputPart( model, outputText );
						giSolution.soln = soln;
						giSolution.stringBuffer = outputText;
						giSolution.model = getUpdateModel();
						giSolution.updateGUIWithModel = getWillTModelUpdate();
						vecGISolution.add( giSolution );
						java.awt.EventQueue.invokeLater( solutionListener );
					}
				};
				t.start();
				fluxesSet = false;
			}
			
			/**
			 * Run the analysis portion
			 * @param model The data model used for this problem
			 * @return The vector of optimized fluxes
			 */
			protected abstract ArrayList< Double > analysisPart( Model model ) throws Exception;
			
			/**
			 * This is output portion that runs after the optimization.
			 * Whatever is written to outputText will display on the console
			 * near the bottom of the GUI
			 * @param model The model used for this problem
			 * @param outputText The string buffer that will be displayed on the console
			 */
			protected abstract void outputPart( Model model, StringBuffer outputText );
			
			/**
			 * Get the Model object that will update the TableModel for the GUI
			 * @return The Model object
			 */
			protected abstract Model getUpdateModel();
			
			/**
			 * Get the boolean value that represents whether or not the TableModel will update
			 * @return A boolean value
			 */
			protected abstract boolean getWillTModelUpdate();
		}
		
		// based on http://stackoverflow.com/questions/9358710/java-action-listener-on-menu-and-not-on-menu-item
		analysisMenu.addMenuListener(new MenuListener() {
			@Override
			public void menuCanceled(MenuEvent arg0) {
			}
			@Override
			public void menuDeselected(MenuEvent arg0) {
			}
			@Override
			public void menuSelected(MenuEvent arg0) {
				if (LocalConfig.getInstance().fvaDone) {
					DynamicTreePanel.getTreePanel().setNodeSelected(0);
				}
			}
	    });

		//Analysis --> FBA
		analysisMenu.add(fbaItem);
		fbaItem.setMnemonic(KeyEvent.VK_F);

		fbaItem.addActionListener( new AnalysisCommonActionListener(){
			private	Double maxObj = 0.0;
			FBA fba = null;
			
			@Override
			protected ArrayList< Double > analysisPart( Model model ) throws Exception
			{
				fba = new FBA();
				fba.setModel(model);
				ArrayList< Double > soln = fba.run();
				maxObj = fba.getMaxObj();
				if( fba.FVASelected )
				{
					this.giSolution.vaMin = fba.minVariability;
					this.giSolution.vaMax = fba.maxVariability;
				}
				return soln;
			}

			@Override
			protected void outputPart( Model model, StringBuffer outputText )
			{
				outputText.append("FBA\n");
				outputText.append(LocalConfig.getInstance().getModelName() + "\n");
				outputText.append(model.getNumMetabolites() + " metabolites, " + model.getNumReactions() + " reactions\n");
				if (maxObj.equals("-0.0")) {
					maxObj = Double.valueOf( "0.0" );
				}
				outputText.append("Maximum objective: "	+ maxObj + "\n");
				outputText.append("MIL solver = " + GraphicalInterface.getMixedIntegerLinearSolverName() + "\n" );
				/*if (fba.FVASelected) {
					LocalConfig.getInstance().fvaColumnsVisible = true;
					ReactionFactory rFactory = new ReactionFactory("SBML");
					rFactory.setFluxes(fba.minVariability, GraphicalInterfaceConstants.MIN_FLUX_COLUMN,
							LocalConfig.getInstance().getReactionsTableModelMap().get(optimizeName));
					rFactory.setFluxes(fba.maxVariability, GraphicalInterfaceConstants.MAX_FLUX_COLUMN,
							LocalConfig.getInstance().getReactionsTableModelMap().get(optimizeName));
					LocalConfig.getInstance().getShowFVAColumnsList().add(getOptimizeName());
					fluxesSet = true;
				} else {
					LocalConfig.getInstance().fvaColumnsVisible = false;
				}*/
			}

			@Override
			protected Model getUpdateModel()
			{
				return null;
			}

			@Override
			protected boolean getWillTModelUpdate()
			{
				return false;
			}
		});

		menuBar.add(analysisMenu);

		//Analysis --> GDBB
		analysisMenu.add(gdbbItem);
        gdbbItem.setMnemonic(KeyEvent.VK_G);

        gdbbItem.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent a) {
        		gdbbSelected = true;
        		Utilities u = new Utilities();
        		// Date time stamp set at start of GDBB so all solutions
        		// for a given run have same date time stamp
        		String dateTimeStamp = u.createDateTimeStamp();
        		setDateTimeStamp(dateTimeStamp);
        		LocalConfig.getInstance().noBiolObjWarningShown = false;
        		LocalConfig.getInstance().noSynObjWarningShown = false;
        		Thread t = new Thread()
        		{
        			final GDBB gdbb = new GDBB();
        			@Override
        			public void run()
        			{
        				//GDBB dialog
        				final GDBBDialog gdbbDialog = new GDBBDialog();
        				gdbbTimer = new javax.swing.Timer(1000, new ActionListener()
						{
        					private int timeCount = 0;
        					private int dotCount = 0;
							@Override
							public void actionPerformed( ActionEvent ae )
							{
								timeCount += 1;
								dotCount += 1;
								StringBuffer dotBuffer = new StringBuffer();
								int numDots = dotCount % ( GDBBConstants.MAX_NUM_DOTS + 1 );
								for( int i = 0; i < numDots; i++)
								{
									dotBuffer.append( " ." );
								}
								if( gdbbDialog.finiteTimeSelected
									&& timeCount == Integer.valueOf( gdbbDialog.getFiniteTimeString() ) )
								{
									gdbb.getSolver().abort();
									gdbbDialog.setVisible( false );
									gdbbDialog.dispose();
								}
								if( !gdbbStopped )
								{
									gdbbDialog.getCounterLabel().setText(
										GDBBConstants.COUNTER_LABEL_PREFIX
										+ timeCount
										+ GDBBConstants.COUNTER_LABEL_SUFFIX );
								}
								else
								{
									gdbbDialog.getCounterLabel().setText(
										GDBBConstants.PROCESSING
										+ dotBuffer
										.toString() );
								}
							}
						});

        				gdbbDialog.setModal(true);
            			gdbbDialog.setIconImages(icons);
            			gdbbDialog.setTitle(GDBBConstants.GDBB_DIALOG_TITLE);
            			gdbbDialog.setSize(GDBBConstants.GDBB_DIALOG_WIDTH, GDBBConstants.GDBB_DIALOG_HEIGHT);
            			gdbbDialog.setResizable(false);
            			gdbbDialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            			gdbbDialog.setLocationRelativeTo(null);
            			setGdbbDialog(gdbbDialog);

            			gdbbDialog.addWindowListener(new WindowAdapter() 
            			{
            				public void windowClosing(WindowEvent evt) 
            				{
            					if (gdbbTimer.isRunning()) 
            					{
            						Object[] options = {"    Yes    ", "    No    ",};
            						int choice = JOptionPane.showOptionDialog(null, 
            								GDBBConstants.FRAME_CLOSE_MESSAGE, 
            								GDBBConstants.FRAME_CLOSE_TITLE, 
            								JOptionPane.YES_NO_OPTION, 
            								JOptionPane.QUESTION_MESSAGE, 
            								null, options, options[0]);
            						if (choice == JOptionPane.YES_OPTION) 
            						{
                    					gdbb.getSolver().abort();
                    					gdbbDialog.setVisible( false );
                    					gdbbDialog.dispose();
            						}
            						if (choice == JOptionPane.NO_OPTION) 
            						{
            						}
            					}
            					else 
            					{
            						gdbbDialog.setVisible(false);
            						gdbbDialog.dispose();
            					}
            				}
            			});	
            			gdbbDialog.startButton.addActionListener(new ActionListener() 
            			{
            				public void actionPerformed(ActionEvent prodActionEvent) 
            				{
            					Thread t = new Thread()
            					{
            						@Override
            						public void run()
            						{
            							gdbbDialog.startButton.setEnabled( false );
                    					gdbbDialog.stopButton.setEnabled( true );
                    					gdbbStopped = false;       			
                    					
                    					// check if all entries are valid
                    					boolean isValid = true;
                    					boolean koIsInteger = true;
                    					boolean finiteTimeIsInteger = true;
                    					try 
                    					{
                    						Integer.parseInt(gdbbDialog.getNumKnockouts());
                    					}
                    					catch(NumberFormatException nfe2) 
                    					{
                    						isValid = false;
                    						koIsInteger = false;
                    					}
                    					if (gdbbDialog.finiteTimeSelected) 
                    					{
                    						try 
                    						{
                    							Integer.parseInt(gdbbDialog.getFiniteTimeString());
                    						}
                    						catch(NumberFormatException nfe2) 
                    						{
                    							isValid = false;
                    							finiteTimeIsInteger = false;
                    						}
                    					}        				
                    					if (!isValid) 
                    					{
                    						JOptionPane.showMessageDialog(null,                
                    								GraphicalInterfaceConstants.INTEGER_VALUE_ERROR_TITLE,                
                    								GraphicalInterfaceConstants.INTEGER_VALUE_ERROR_MESSAGE,                               
                    								JOptionPane.ERROR_MESSAGE);
                    						if (!koIsInteger) 
                    							gdbbDialog.setKnockoutDefaultValue();
                    						if (!finiteTimeIsInteger)
                    							gdbbDialog.setFiniteTimeDefaultValue();
                    						gdbbDialog.startButton.setEnabled( true );
                        					gdbbDialog.stopButton.setEnabled( false );
                        					gdbbStopped = true;  
                    					}
                    					else 
                    					{
                    						// run gdbb
                    						gdbbDialog.disableComponents();
                    						gdbbDialog.stopButton.setEnabled(true);
                    						gdbbTimer.start();

                    						GDBBModel model = new GDBBModel(GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES[GraphicalInterfaceConstants.SYNTHETIC_OBJECTIVE_COLUMN]);
                    						model.setC((new Double(gdbbDialog.getNumKnockouts())).doubleValue());
                    						model.setTimeLimit((new Double(gdbbDialog.getFiniteTimeString())).doubleValue());

                    						if (!gdbbDialog.finiteTimeSelected)
                    							model.setTimeLimit(Double.POSITIVE_INFINITY);
                    						else
                    							model.setTimeLimit((new Double(gdbbDialog.getFiniteTimeString())).doubleValue());

                    						//gdbbTask.getModel().setThreadNum((Integer)gdbbDialog.cbNumThreads.getSelectedItem());
                    						//model.setThreadNum(gdbbDialog.selectedNumberOfThreads());
                    						gdbb.setGDBBModel( model );
                    						gdbb.setFinalizingCallback( new GDBB.Callback()
        									{
        										@Override
        										public void invoke()
        										{
        											gdbbDialog.setVisible( false );
        											gdbbDialog.dispose();
        										}
        									} );
                    						gdbb.start();
                    						gdbbRunning = true;
                    						gdbbProcessed = false;
                    					}  
            						}
            					};
            					t.start();
            				}
            			});
            			gdbbDialog.stopButton.addActionListener(new ActionListener() 
            			{
            				public void actionPerformed(ActionEvent prodActionEvent) 
            				{
            					gdbb.getSolver().abort();
            					gdbbDialog.setVisible( false );
            					gdbbDialog.dispose();
            				}
            			});
            			gdbbDialog.setVisible( true );
            			gdbbDialog.dispose();
            			
        			}
        			
        		};
        		t.start();
        	}
        });

        //Analysis --> Eflux2
        analysisMenu.add( eflux2Item );
        eflux2Item.setMnemonic( KeyEvent.VK_E );
        
        eflux2Item.addActionListener( new AnalysisCommonActionListener(){
			private	Double maxObj = 0.0;
			private Model model = null;
			@Override
			protected ArrayList< Double > analysisPart( Model model ) throws Exception
			{
				this.model = model;
				Eflux2 eflux2 = new Eflux2();
				eflux2.setModel(model);
	            // uncomment next three lines for proof of concept of adding a new tab at runtime
//        		JScrollPane scrollPaneGene = new JScrollPane();
//        		tabbedPane.addTab("Genes", scrollPaneGene);
//        		tabbedPane.repaint();
				ArrayList<Double> soln = eflux2.run();
				maxObj = eflux2.getMaxObj();
				return soln;
			}

        	@Override
        	protected void outputPart( Model model, StringBuffer outputText )
        	{
        		outputText.append("Eflux2\n");
				outputText.append(LocalConfig.getInstance().getModelName() + "\n");
				outputText.append(model.getNumMetabolites() + " metabolites, " + model.getNumReactions() + " reactions\n");
				if (maxObj.equals("-0.0")) {
					maxObj = Double.valueOf( "0.0" );
				}
				outputText.append("Maximum objective: "	+ maxObj + "\n");
				outputText.append("MIL solver = " + GraphicalInterface.getMixedIntegerLinearSolverName() + "\n" );
				outputText.append( "Quadratic Solver = " + GraphicalInterface.getQuadraticSolverName() + "\n" );
			}

			@Override
			protected Model getUpdateModel()
			{
				return model;
			}

			@Override
			protected boolean getWillTModelUpdate()
			{
				return true;
			}
        });

        //Analysis --> SPOT
        analysisMenu.add( spotItem );
        spotItem.setMnemonic( KeyEvent.VK_S );

        spotItem.addActionListener( new AnalysisCommonActionListener( false ){
        	private Double maxObj = 0.0;
        	private Model model = null;
        	
			@Override
			protected ArrayList< Double > analysisPart( Model model ) throws Exception
			{
				this.model = model;
				SPOT spot = new SPOT();
				spot.setModel( model );
				// uncomment next three lines for proof of concept of adding a
				// new tab at runtime
				// JScrollPane scrollPaneGene = new JScrollPane();
				// tabbedPane.addTab("Genes", scrollPaneGene);
				// tabbedPane.repaint();
				ArrayList< Double > soln = spot.run();
				maxObj = spot.getMaxObj();
				return soln;
			}

			@Override
			protected void outputPart( Model model, StringBuffer outputText )
			{
				outputText.append( "SPOT\n" );
				outputText.append( LocalConfig.getInstance()
						.getModelName() + "\n" );
				outputText.append( model.getNumMetabolites()
						+ " metabolites, " + model.getNumReactions()
						+ " reactions\n" );
				outputText.append( "SPOT value: " + maxObj + "\n" );
				outputText.append("MIL solver = " + GraphicalInterface.getMixedIntegerLinearSolverName() + "\n" );
				outputText.append("Nonlinear solver = " + GraphicalInterface.getNonlinearSolverName() + "\n" );
			}

			@Override
			protected Model getUpdateModel()
			{
				return model;
			}

			@Override
			protected boolean getWillTModelUpdate()
			{
				return true;
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
				metabolitesTable.repaint();
			}
		});

		editMenu.add(deleteUnusedItem);
		deleteUnusedItem.setMnemonic(KeyEvent.VK_D);		

		deleteUnusedItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {				
				tabbedPane.setSelectedIndex(1);
				// copy model so old model can be restored if paste not valid
				DefaultTableModel oldMetabolitesModel = copyMetabolitesTableModel((DefaultTableModel) metabolitesTable.getModel());	
				copyMetabolitesTableModels(oldMetabolitesModel); 
				MetaboliteUndoItem undoItem = createMetaboliteUndoItem("", "", metabolitesTable.getSelectedRow(), metabolitesTable.getSelectedColumn(), 0, UndoConstants.DELETE_UNUSED, UndoConstants.METABOLITE_UNDO_ITEM_TYPE);
				undoItem.setTableCopyIndex(LocalConfig.getInstance().getNumMetabolitesTableCopied());
				setUndoOldCollections(undoItem);
				
				createUnusedMetabolitesList();
				
				for (int i = 0; i < LocalConfig.getInstance().getUnusedList().size(); i++) {
					deleteMetabolitesRowById(LocalConfig.getInstance().getUnusedList().get(i));
					if (LocalConfig.getInstance().getSuspiciousMetabolites().contains(LocalConfig.getInstance().getUnusedList().get(i))) {
						LocalConfig.getInstance().getSuspiciousMetabolites().remove(LocalConfig.getInstance().getSuspiciousMetabolites().indexOf(LocalConfig.getInstance().getUnusedList().get(i)));
					}
				}	
				DefaultTableModel model = (DefaultTableModel) metabolitesTable.getModel();
				for (int j = metabolitesTable.getRowCount() - 1; j >=0; j--) {
					if (metabolitesTable.getModel().getValueAt(j, GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN) == null ||
							((String) metabolitesTable.getModel().getValueAt(j, GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN)).trim().length() == 0) {
						model.removeRow(j);
					}
				}
				highlightUnusedMetabolites = false;
				highlightUnusedMetabolitesItem.setState(false);
				formulaBar.setText("");				
				LocalConfig.getInstance().getUnusedList().clear();
				DefaultTableModel newMetabolitesModel = copyMetabolitesTableModel((DefaultTableModel) metabolitesTable.getModel());			
				setUpMetabolitesTable(newMetabolitesModel);
				copyMetabolitesTableModels(newMetabolitesModel); 
				setUndoNewCollections(undoItem);
				setUpMetabolitesUndo(undoItem);	
				maybeDisplaySuspiciousMetabMessage(statusBarRow());
				int numMetabRows = metabolitesTable.getRowCount();
				int maxId = Integer.valueOf((String) metabolitesTable.getModel().getValueAt(numMetabRows - 1, GraphicalInterfaceConstants.METABOLITE_ID_COLUMN));
				LocalConfig.getInstance().setMaxMetabolite(numMetabRows);
				LocalConfig.getInstance().setMaxMetaboliteId(maxId + 1);	
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
						Integer cellValue = Integer.valueOf((String) metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_ID_COLUMN));
						if (cellValue == firstId) {
							setTableCellFocused(r, GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN, metabolitesTable);
						}	
					}
				}
			}    	     
		});

		editMenu.addSeparator(); 

		editMenu.add(undoItem);
		undoItem.setMnemonic(KeyEvent.VK_U);
		undoItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
		undoItem.setEnabled(false);

		undoItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {				
				undoButtonAction();
			}    	     
		});

		editMenu.add(redoItem);
		redoItem.setMnemonic(KeyEvent.VK_E);
		redoItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_Y, ActionEvent.CTRL_MASK));
		redoItem.setEnabled(false);

		redoItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {
				redoButtonAction();			
			}    	     
		});

		editMenu.add(findReplaceItem);
		findReplaceItem.setMnemonic(KeyEvent.VK_F);
		findReplaceItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_F, ActionEvent.CTRL_MASK));
		findReplaceItem.setEnabled(true);

		findReplaceItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {
				showFindReplace();
				findReplaceItem.setEnabled(false);
				findbutton.setEnabled(false);
			}    	     
		});

		editMenu.add(selectAllItem);
		selectAllItem.setMnemonic(KeyEvent.VK_A);
		selectAllItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_A, ActionEvent.CTRL_MASK));
		selectAllItem.setEnabled(true);

		selectAllItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {
				if (tabbedPane.getSelectedIndex() == 0) {
					metabolitesTable.clearSelection();
					includeRxnColumnNames = false;
					reactionsTable.selectAll();			
					selectReactionsRows();
				} else if (tabbedPane.getSelectedIndex() == 1) {
					reactionsTable.clearSelection();
					includeMtbColumnNames = false;
					metabolitesTable.selectAll();			
					selectMetabolitesRows();
				}
			}    	     
		});
		
		editMenu.addSeparator();

		editMenu.add(addReacRowItem);
		addReacRowItem.setMnemonic(KeyEvent.VK_R);

		ActionListener addReacRowActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				tabbedPane.setSelectedIndex(0);
				int row = reactionsTable.getSelectedRow();
				int col = reactionsTable.getSelectedColumn();
				int id = LocalConfig.getInstance().getMaxReactionId();
				DefaultTableModel model = (DefaultTableModel) reactionsTable.getModel();	
				model.addRow(createReactionsRow(id));
				setUpReactionsTable(model);
				ReactionUndoItem undoItem = createReactionUndoItem("", "", row, col, id, UndoConstants.ADD_ROW, UndoConstants.REACTION_UNDO_ITEM_TYPE);		
				setOldUsedMap(undoItem);
				//set focus to id cell in new row in order to set row visible
				int maxRow = reactionsTable.getModel().getRowCount();
				int viewRow = reactionsTable.convertRowIndexToView(maxRow - 1);
				setTableCellFocused(viewRow, 1, reactionsTable);
				setNewUsedMap(undoItem);
				setUpReactionsUndo(undoItem);
				LocalConfig.getInstance().setMaxReactionId(id + 1);
			}
		};  

		addReacRowItem.addActionListener(addReacRowActionListener);
		
		editMenu.add(addReacRowsItem);
		addReacRowsItem.setMnemonic(KeyEvent.VK_W);
		
		addReacRowsItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				setCurrentReactionsRow(reactionsTable.getSelectedRow());
				setCurrentReactionsColumn(reactionsTable.getSelectedColumn());
				tabbedPane.setSelectedIndex(0);
				AddReactionRowsDialog addReactionRowsDialog = new AddReactionRowsDialog();
				setAddReactionRowsDialog(addReactionRowsDialog);
				getAddReactionRowsDialog().setTitle(GraphicalInterfaceConstants.ADD_ROWS_DIALOG_TITLE);
				getAddReactionRowsDialog().setIconImages(icons);
				getAddReactionRowsDialog().setSize(220, 150);
				getAddReactionRowsDialog().setResizable(false);
				getAddReactionRowsDialog().setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
				//getAddReactionRowsDialog().setAlwaysOnTop(true);
				getAddReactionRowsDialog().setModal(true);
				getAddReactionRowsDialog().setLocationRelativeTo(null);
				getAddReactionRowsDialog().addWindowListener(new WindowAdapter() {
					public void windowClosing(WindowEvent evt) {
						addReactionRowsDialogCloseAction();
					}
				});
				getAddReactionRowsDialog().setVisible(true);						
			}
		});

		ActionListener addReacRowsOKButtonActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent prodActionEvent) {	
				EntryValidator validator = new EntryValidator();
				// check if integer
				if (!validator.isInteger(AddReactionRowsDialog.textField.getText())) {
//					getAddReactionRowsDialog().setAlwaysOnTop(false);
//					getAddReactionRowsDialog().setModal(false);
					JOptionPane.showMessageDialog(null,                
							GraphicalInterfaceConstants.INTEGER_VALUE_ERROR_TITLE,                
							GraphicalInterfaceConstants.INTEGER_VALUE_ERROR_MESSAGE,                                
							JOptionPane.ERROR_MESSAGE);
					//					getAddReactionRowsDialog().setAlwaysOnTop(true);
//					getAddReactionRowsDialog().setModal(true);
					AddReactionRowsDialog.textField.setText(GraphicalInterfaceConstants.DEFAULT_NUM_ADD_ROWS);
					AddReactionRowsDialog.textField.selectAll();
				} else {
					int numRows = Integer.valueOf(AddReactionRowsDialog.textField.getText());
					// check that value in acceptable range
					if (numRows >= GraphicalInterfaceConstants.MAX_NUM_ADD_ROWS || numRows <= 0) {
//						getAddReactionRowsDialog().setAlwaysOnTop(false);
//						getAddReactionRowsDialog().setModal(false);
						JOptionPane.showMessageDialog(null,                
								GraphicalInterfaceConstants.ADD_ROWS_OUT_OF_RANGE_MESSAGE,                
								GraphicalInterfaceConstants.ADD_ROWS_OUT_OF_RANGE_TITLE,                                
								JOptionPane.ERROR_MESSAGE);

						//						getAddReactionRowsDialog().setAlwaysOnTop(true);
//						getAddReactionRowsDialog().setModal(true);
						AddReactionRowsDialog.textField.setText(GraphicalInterfaceConstants.DEFAULT_NUM_ADD_ROWS);
						
						AddReactionRowsDialog.textField.selectAll();
					} else {
						// copy old model for undo/redo
						DefaultTableModel oldReactionsModel = copyReactionsTableModel((DefaultTableModel) reactionsTable.getModel());	
						copyReactionsTableModels(oldReactionsModel); 
						
						int row = reactionsTable.getSelectedRow();
						int col = reactionsTable.getSelectedColumn();
						int id = LocalConfig.getInstance().getMaxReactionId();
						DefaultTableModel model = (DefaultTableModel) reactionsTable.getModel();
						for (int i = 0; i < numRows; i++) {
							model.addRow(createReactionsRow(id));
							id += 1;
						}
						setUpReactionsTable(model);
						ReactionUndoItem undoItem = createReactionUndoItem("", "", row, col, id, UndoConstants.ADD_ROWS, UndoConstants.REACTION_UNDO_ITEM_TYPE);				
						int maxRow = reactionsTable.getModel().getRowCount();
						int viewRow = reactionsTable.convertRowIndexToView(maxRow - 1);
						setTableCellFocused(viewRow, 1, reactionsTable);
						LocalConfig.getInstance().setMaxReactionId(id);
						addReactionRowsDialogCloseAction();
						
						undoItem.setTableCopyIndex(LocalConfig.getInstance().getNumReactionTablesCopied());
						copyReactionsTableModels(model); 
						setUpReactionsUndo(undoItem);
					}					
				}								
			}
		};

		ActionListener addReacRowsCancelButtonActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent prodActionEvent) {
				addReactionRowsDialogCloseAction();
			}
		};

		
		AddReactionRowsDialog.getOkButton().addActionListener(addReacRowsOKButtonActionListener);
		
		AddReactionRowsDialog.getCancelButton().addActionListener(addReacRowsCancelButtonActionListener);

		editMenu.add(addMetabRowItem); 
		addMetabRowItem.setMnemonic(KeyEvent.VK_M);

		addMetabRowItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				tabbedPane.setSelectedIndex(1);
				int row = metabolitesTable.getSelectedRow();
				int col = metabolitesTable.getSelectedColumn();
				int id = LocalConfig.getInstance().getMaxMetaboliteId();
				DefaultTableModel model = (DefaultTableModel) metabolitesTable.getModel();	
				model.addRow(createMetabolitesRow(id));
				setUpMetabolitesTable(model);
				MetaboliteUndoItem undoItem = createMetaboliteUndoItem("", "", row, col, id, UndoConstants.ADD_ROW, UndoConstants.METABOLITE_UNDO_ITEM_TYPE);
				setUndoOldCollections(undoItem);				
				int maxRow = metabolitesTable.getModel().getRowCount();
				int viewRow = metabolitesTable.convertRowIndexToView(maxRow - 1);
				setTableCellFocused(viewRow, 1, metabolitesTable);
				setUndoNewCollections(undoItem);
				setUpMetabolitesUndo(undoItem);
				LocalConfig.getInstance().setMaxMetaboliteId(id + 1);
			}
		});
		
		editMenu.add(addMetabRowsItem); 
		addMetabRowsItem.setMnemonic(KeyEvent.VK_I);
		
		addMetabRowsItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				setCurrentMetabolitesRow(metabolitesTable.getSelectedRow());
				setCurrentMetabolitesColumn(metabolitesTable.getSelectedColumn());
				tabbedPane.setSelectedIndex(1);
				AddMetaboliteRowsDialog addMetaboliteRowsDialog = new AddMetaboliteRowsDialog();
				setAddMetaboliteRowsDialog(addMetaboliteRowsDialog);
				getAddMetaboliteRowsDialog().setTitle(GraphicalInterfaceConstants.ADD_ROWS_DIALOG_TITLE);
				getAddMetaboliteRowsDialog().setIconImages(icons);
				getAddMetaboliteRowsDialog().setSize(220, 150);
				getAddMetaboliteRowsDialog().setResizable(false);
				getAddMetaboliteRowsDialog().setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
				//getAddMetaboliteRowsDialog().setAlwaysOnTop(true);
				getAddMetaboliteRowsDialog().setModal(true);
				getAddMetaboliteRowsDialog().setLocationRelativeTo(null);
				getAddMetaboliteRowsDialog().addWindowListener(new WindowAdapter() {
					public void windowClosing(WindowEvent evt) {
						addMetaboliteRowsDialogCloseAction();
					}
				});
				getAddMetaboliteRowsDialog().setVisible(true);						
			}
		});

		ActionListener addMetabRowsOKButtonActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent prodActionEvent) {	
				EntryValidator validator = new EntryValidator();
				// check if integer
				if (!validator.isInteger(AddMetaboliteRowsDialog.textField.getText())) {
//					getAddMetaboliteRowsDialog().setAlwaysOnTop(false);
//					getAddMetaboliteRowsDialog().setModal(false);
					JOptionPane.showMessageDialog(null,                
							GraphicalInterfaceConstants.INTEGER_VALUE_ERROR_TITLE,                
							GraphicalInterfaceConstants.INTEGER_VALUE_ERROR_MESSAGE,                                
							JOptionPane.ERROR_MESSAGE);
					//					getAddMetaboliteRowsDialog().setAlwaysOnTop(true);
//					getAddMetaboliteRowsDialog().setModal(true);
					AddMetaboliteRowsDialog.textField.setText(GraphicalInterfaceConstants.DEFAULT_NUM_ADD_ROWS);
					AddMetaboliteRowsDialog.textField.selectAll();
				} else {
					int numRows = Integer.valueOf(AddMetaboliteRowsDialog.textField.getText());
					// check that value in acceptable range
					if (numRows >= GraphicalInterfaceConstants.MAX_NUM_ADD_ROWS || numRows <= 0) {
//						getAddMetaboliteRowsDialog().setAlwaysOnTop(false);
//						getAddMetaboliteRowsDialog().setModal(false);
						JOptionPane.showMessageDialog(null,                
								GraphicalInterfaceConstants.ADD_ROWS_OUT_OF_RANGE_MESSAGE,                
								GraphicalInterfaceConstants.ADD_ROWS_OUT_OF_RANGE_TITLE,                                
								JOptionPane.ERROR_MESSAGE);
						//						getAddMetaboliteRowsDialog().setAlwaysOnTop(true);
//						getAddMetaboliteRowsDialog().setModal(true);
						AddMetaboliteRowsDialog.textField.setText(GraphicalInterfaceConstants.DEFAULT_NUM_ADD_ROWS);
						
						AddMetaboliteRowsDialog.textField.selectAll();
					} else {
						// copy old model for undo/redo
						DefaultTableModel oldMetabolitesModel = copyMetabolitesTableModel((DefaultTableModel) metabolitesTable.getModel());	
						copyMetabolitesTableModels(oldMetabolitesModel); 
						
						int row = metabolitesTable.getSelectedRow();
						int col = metabolitesTable.getSelectedColumn();
						int id = LocalConfig.getInstance().getMaxMetaboliteId();
						DefaultTableModel model = (DefaultTableModel) metabolitesTable.getModel();
						for (int i = 0; i < numRows; i++) {
							model.addRow(createMetabolitesRow(id));
							id += 1;
						}						
						setUpMetabolitesTable(model);
						MetaboliteUndoItem undoItem = createMetaboliteUndoItem("", "", row, col, id, UndoConstants.ADD_ROWS, UndoConstants.METABOLITE_UNDO_ITEM_TYPE);
						setUndoOldCollections(undoItem);				
						int maxRow = metabolitesTable.getModel().getRowCount();
						int viewRow = metabolitesTable.convertRowIndexToView(maxRow - 1);
						setTableCellFocused(viewRow, 1, metabolitesTable);
						LocalConfig.getInstance().setMaxMetaboliteId(id);
						addMetaboliteRowsDialogCloseAction();
						
						undoItem.setTableCopyIndex(LocalConfig.getInstance().getNumMetabolitesTableCopied());
						copyMetabolitesTableModels(model); 
						setUndoNewCollections(undoItem);
						setUpMetabolitesUndo(undoItem);
					}					
				}								
			}
		};

		ActionListener addMetabRowsCancelButtonActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent prodActionEvent) {
				addMetaboliteRowsDialogCloseAction();
			}
		};

		
		AddMetaboliteRowsDialog.getOkButton().addActionListener(addMetabRowsOKButtonActionListener);
		
		AddMetaboliteRowsDialog.getCancelButton().addActionListener(addMetabRowsCancelButtonActionListener);

		editMenu.addSeparator();

		editMenu.add(addReacColumnItem);
		addReacColumnItem.setMnemonic(KeyEvent.VK_C);

		addReacColumnItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				setCurrentReactionsRow(reactionsTable.getSelectedRow());
				setCurrentReactionsColumn(reactionsTable.getSelectedColumn());
				tabbedPane.setSelectedIndex(0);
				ReactionColAddRenameInterface reactionColAddRenameInterface = new ReactionColAddRenameInterface();
				setReactionColAddRenameInterface(reactionColAddRenameInterface);
				reactionColAddRenameInterface.setTitle(GraphicalInterfaceConstants.COLUMN_ADD_INTERFACE_TITLE);
				reactionColAddRenameInterface.setIconImages(icons);
				reactionColAddRenameInterface.setSize(350, 150);
				reactionColAddRenameInterface.setResizable(false);
				reactionColAddRenameInterface.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
				//reactionColAddRenameInterface.setAlwaysOnTop(true);
				reactionColAddRenameInterface.setModal(true);
				reactionColAddRenameInterface.setLocationRelativeTo(null);
				reactionColAddRenameInterface.addWindowListener(new WindowAdapter() {
					public void windowClosing(WindowEvent evt) {
						addReactionColumnCloseAction();
					}
				});					
				reactionColAddRenameInterface.setVisible(true);	
			}
		});

		ActionListener addColOKButtonActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent prodActionEvent) {
				// allows table to scroll to make added column visible
				addReacColumn = true;
				if (getReactionColAddRenameInterface().isColumnDuplicate()) {
//					reactionColAddRenameInterface.setAlwaysOnTop(false);
//					reactionColAddRenameInterface.setModal(false);
					JOptionPane.showMessageDialog(null,                
							"Column Name Already Exists.",                
							"Duplicate ColumnName",                                
							JOptionPane.ERROR_MESSAGE);
//					reactionColAddRenameInterface.setAlwaysOnTop(true);
//					reactionColAddRenameInterface.setModal(true);
				} else {
					// copy old model for undo/redo
					DefaultTableModel oldReactionsModel = copyReactionsTableModel((DefaultTableModel) reactionsTable.getModel());			
					copyReactionsTableModels(oldReactionsModel);
					
					LocalConfig.getInstance().getReactionsMetaColumnNames().add(ReactionColAddRenameInterface.textField.getText());
					ReactionUndoItem undoItem = createReactionUndoItem("", "", getCurrentReactionsRow(), getCurrentReactionsColumn(), 0, UndoConstants.ADD_COLUMN, UndoConstants.REACTION_UNDO_ITEM_TYPE);
					setOldUsedMap(undoItem);
					undoItem.setTableCopyIndex(LocalConfig.getInstance().getNumReactionTablesCopied());
					undoItem.setAddedColumnIndex(LocalConfig.getInstance().getReactionsMetaColumnNames().size() + GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES.length);
					ArrayList<String> oldMetaCol = new ArrayList<String>();
					ArrayList<String> newMetaCol = new ArrayList<String>();
					for (int i = 0; i < LocalConfig.getInstance().getReactionsMetaColumnNames().size(); i++) {
						oldMetaCol.add(LocalConfig.getInstance().getReactionsMetaColumnNames().get(i));
					}
					undoItem.setOldMetaColumnNames(oldMetaCol);
					getReactionColAddRenameInterface().addColumn();
					
					ReactionColAddRenameInterface.textField.setText("");
					getReactionColAddRenameInterface().setVisible(false);
					getReactionColAddRenameInterface().dispose();
					setUpReactionsTable(LocalConfig.getInstance().getReactionsTableModelMap().get(LocalConfig.getInstance().getModelName()));
					DefaultTableModel newReactionsModel = copyReactionsTableModel((DefaultTableModel) reactionsTable.getModel());			
					copyReactionsTableModels(newReactionsModel);
					for (int i = 0; i < LocalConfig.getInstance().getReactionsMetaColumnNames().size(); i++) {
						newMetaCol.add(LocalConfig.getInstance().getReactionsMetaColumnNames().get(i));
					}
					undoItem.setNewMetaColumnNames(newMetaCol);
					setNewUsedMap(undoItem);
					setUpReactionsUndo(undoItem);
					addReacColumn = false;
					addReactionColumnCloseAction();
				}							
			}
		};

		ActionListener addColCancelButtonActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent prodActionEvent) {
				addReactionColumnCloseAction();
			}
		};

		ReactionColAddRenameInterface.okButton.addActionListener(addColOKButtonActionListener);
		ReactionColAddRenameInterface.cancelButton.addActionListener(addColCancelButtonActionListener);

		editMenu.add(addMetabColumnItem);
		addMetabColumnItem.setMnemonic(KeyEvent.VK_O);

		addMetabColumnItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				setCurrentMetabolitesRow(metabolitesTable.getSelectedRow());
				setCurrentMetabolitesColumn(metabolitesTable.getSelectedColumn());
				tabbedPane.setSelectedIndex(1);
				MetaboliteColAddRenameInterface metaboliteColAddRenameInterface = new MetaboliteColAddRenameInterface();
				setMetaboliteColAddRenameInterface(metaboliteColAddRenameInterface);
				metaboliteColAddRenameInterface.setTitle(GraphicalInterfaceConstants.COLUMN_ADD_INTERFACE_TITLE);
				metaboliteColAddRenameInterface.setIconImages(icons);
				metaboliteColAddRenameInterface.setSize(350, 150);
				metaboliteColAddRenameInterface.setResizable(false);
				metaboliteColAddRenameInterface.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
				//metaboliteColAddRenameInterface.setAlwaysOnTop(true);
				metaboliteColAddRenameInterface.setModal(true);
				metaboliteColAddRenameInterface.setLocationRelativeTo(null);
				metaboliteColAddRenameInterface.addWindowListener(new WindowAdapter() {
					public void windowClosing(WindowEvent evt) {
						addMetaboliteColumnCloseAction();
					}
				});
				metaboliteColAddRenameInterface.setVisible(true);						
			}
		});

		ActionListener addMetabColOKButtonActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent prodActionEvent) {				
				// allows table to scroll to make added column visible
				addMetabColumn = true;
				if (getMetaboliteColAddRenameInterface().isColumnDuplicate()) {
//					metaboliteColAddRenameInterface.setAlwaysOnTop(false);
//					metaboliteColAddRenameInterface.setModal(false);
					JOptionPane.showMessageDialog(null,                
							"Column Name Already Exists.",                
							"Duplicate ColumnName",                                
							JOptionPane.ERROR_MESSAGE);
//					metaboliteColAddRenameInterface.setAlwaysOnTop(true);
//					metaboliteColAddRenameInterface.setModal(true);
				} else {
					// copy old model for undo/redo
					DefaultTableModel oldMetabolitesModel = copyMetabolitesTableModel((DefaultTableModel) metabolitesTable.getModel());			
					copyMetabolitesTableModels(oldMetabolitesModel);
					LocalConfig.getInstance().getMetabolitesMetaColumnNames().add(MetaboliteColAddRenameInterface.textField.getText());
					MetaboliteUndoItem undoItem = createMetaboliteUndoItem("", "", getCurrentMetabolitesRow(), getCurrentMetabolitesColumn(), 0, UndoConstants.ADD_COLUMN, UndoConstants.METABOLITE_UNDO_ITEM_TYPE);		
					undoItem.setTableCopyIndex(LocalConfig.getInstance().getNumMetabolitesTableCopied());
					undoItem.setAddedColumnIndex(LocalConfig.getInstance().getMetabolitesMetaColumnNames().size() + GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES.length);
					setUndoOldCollections(undoItem);
					ArrayList<String> oldMetaCol = new ArrayList<String>();
					ArrayList<String> newMetaCol = new ArrayList<String>();
					for (int i = 0; i < LocalConfig.getInstance().getMetabolitesMetaColumnNames().size(); i++) {
						oldMetaCol.add(LocalConfig.getInstance().getMetabolitesMetaColumnNames().get(i));
					}
					undoItem.setOldMetaColumnNames(oldMetaCol);
					getMetaboliteColAddRenameInterface().addColumn();
					
					MetaboliteColAddRenameInterface.textField.setText("");
					getMetaboliteColAddRenameInterface().setVisible(false);
					getMetaboliteColAddRenameInterface().dispose();
					setUpMetabolitesTable(LocalConfig.getInstance().getMetabolitesTableModelMap().get(LocalConfig.getInstance().getModelName()));					
					DefaultTableModel newMetabolitesModel = copyMetabolitesTableModel((DefaultTableModel) metabolitesTable.getModel());			
					copyMetabolitesTableModels(newMetabolitesModel);
					for (int i = 0; i < LocalConfig.getInstance().getMetabolitesMetaColumnNames().size(); i++) {
						newMetaCol.add(LocalConfig.getInstance().getMetabolitesMetaColumnNames().get(i));
					}
					undoItem.setNewMetaColumnNames(newMetaCol);
					setUndoNewCollections(undoItem);
					setUpMetabolitesUndo(undoItem);					
					addMetabColumn = false;
					addMetaboliteColumnCloseAction();
				}								
			}
		};

		ActionListener addMetabColCancelButtonActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent prodActionEvent) {
				addMetaboliteColumnCloseAction();
			}
		};

		MetaboliteColAddRenameInterface.okButton.addActionListener(addMetabColOKButtonActionListener);
		MetaboliteColAddRenameInterface.cancelButton.addActionListener(addMetabColCancelButtonActionListener);

		editMenu.addSeparator(); 
		
		editMenu.add(unsortReacMenuItem);
		unsortReacMenuItem.setMnemonic(KeyEvent.VK_T);
		unsortReacMenuItem.setEnabled(false);
		
		unsortReacMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				setReactionsSortDefault();
				
				unsortReacMenuItem.setEnabled(false);
				int id = 0;
				if (reactionsTable.getSelectedRow() > -1) {
					int viewRow = reactionsTable.convertRowIndexToModel(reactionsTable.getSelectedRow());
					id = Integer.valueOf((String) reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTIONS_ID_COLUMN));
				}
				ReactionUndoItem undoItem = createReactionUndoItem("", "", reactionsTable.getSelectedRow(), reactionsTable.getSelectedColumn(), id, UndoConstants.UNSORT, UndoConstants.REACTION_UNDO_ITEM_TYPE);
				setOldUsedMap(undoItem);
				LocalConfig.getInstance().getReactionsSortColumns().add(0);
				LocalConfig.getInstance().getReactionsSortOrderList().add(reactionsTable.getSortOrder(reactionsTable.getSortedColumnIndex()));
				undoItem.setOldSortColumnIndex(LocalConfig.getInstance().getReactionsSortColumns().get(LocalConfig.getInstance().getReactionsSortColumns().size() - 2));
				undoItem.setNewSortColumnIndex(LocalConfig.getInstance().getReactionsSortColumns().get(LocalConfig.getInstance().getReactionsSortColumns().size() - 1));
				undoItem.setOldSortOrder(LocalConfig.getInstance().getReactionsSortOrderList().get(LocalConfig.getInstance().getReactionsSortOrderList().size() - 2));
				undoItem.setNewSortOrder(LocalConfig.getInstance().getReactionsSortOrderList().get(LocalConfig.getInstance().getReactionsSortOrderList().size() - 1));
				setNewUsedMap(undoItem);
				setUpReactionsUndo(undoItem);
				
				DefaultTableModel model = (DefaultTableModel) reactionsTable.getModel();
				setUpReactionsTable(model);
				tabbedPane.setSelectedIndex(0);
			}
		});
		
		editMenu.add(unsortMetabMenuItem);
		unsortMetabMenuItem.setMnemonic(KeyEvent.VK_B);
		unsortMetabMenuItem.setEnabled(false);
		
		unsortMetabMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				setMetabolitesSortDefault();
				
				unsortMetabMenuItem.setEnabled(false);
				int id = 0;
				if (metabolitesTable.getSelectedRow() > -1) {
					int viewRow = metabolitesTable.convertRowIndexToModel(metabolitesTable.getSelectedRow());
					id = Integer.valueOf((String) metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_ID_COLUMN));					
				}
				MetaboliteUndoItem undoItem = createMetaboliteUndoItem("", "", metabolitesTable.getSelectedRow(), metabolitesTable.getSelectedColumn(), id, UndoConstants.UNSORT, UndoConstants.METABOLITE_UNDO_ITEM_TYPE);
				setUndoOldCollections(undoItem);
				LocalConfig.getInstance().getMetabolitesSortColumns().add(0);
				LocalConfig.getInstance().getMetabolitesSortOrderList().add(metabolitesTable.getSortOrder(metabolitesTable.getSortedColumnIndex()));
				undoItem.setOldSortColumnIndex(LocalConfig.getInstance().getMetabolitesSortColumns().get(LocalConfig.getInstance().getMetabolitesSortColumns().size() - 2));
				undoItem.setNewSortColumnIndex(LocalConfig.getInstance().getMetabolitesSortColumns().get(LocalConfig.getInstance().getMetabolitesSortColumns().size() - 1));
				undoItem.setOldSortOrder(LocalConfig.getInstance().getMetabolitesSortOrderList().get(LocalConfig.getInstance().getMetabolitesSortOrderList().size() - 2));
				undoItem.setNewSortOrder(LocalConfig.getInstance().getMetabolitesSortOrderList().get(LocalConfig.getInstance().getMetabolitesSortOrderList().size() - 1));
				setUpMetabolitesUndo(undoItem);
				
				DefaultTableModel model = (DefaultTableModel) metabolitesTable.getModel();
				setUpMetabolitesTable(model);
				tabbedPane.setSelectedIndex(1);
			}
		});
		
		menuBar.add(editMenu);

		JMenu optionsMenu = new JMenu("Options");
		optionsMenu.setMnemonic(KeyEvent.VK_O);

		optionsMenu.add(setUpSolver);
		setUpSolver.setMnemonic(KeyEvent.VK_S);
		setUpSolver.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {
				if(GurobiSolver.isGurobiLinked()){
					enableGurobiItems();
				}
				else {
					disableGurobiItems();
				}
				// sets selected items in combo boxes based on items in config file if not null
				ConfigProperties configProp = new ConfigProperties();
				if (configProp.fileExists()) {
					ConfigProperties.readFile();
					if (ConfigProperties.getMixedIntegerLinearSolverName() != null) {
						if (ConfigProperties.getMixedIntegerLinearSolverName().equals(GraphicalInterfaceConstants.GLPK_SOLVER_NAME)) {
							getSolverSetUpDialog().cbLinear.setSelectedItem(GraphicalInterfaceConstants.GLPK_SOLVER_NAME);
						} else if (ConfigProperties.getMixedIntegerLinearSolverName().equals(GraphicalInterfaceConstants.GUROBI_SOLVER_NAME)) {
							getSolverSetUpDialog().cbLinear.setSelectedItem(GraphicalInterfaceConstants.GUROBI_SOLVER_NAME);
						}
					}
					if (ConfigProperties.getQuadraticSolverName() != null) {
						if (ConfigProperties.getQuadraticSolverName().equals(GraphicalInterfaceConstants.IPOPT_SOLVER_NAME)) {
							getSolverSetUpDialog().cbQuadratic.setSelectedItem(GraphicalInterfaceConstants.IPOPT_SOLVER_NAME);
						} else if (ConfigProperties.getQuadraticSolverName().equals(GraphicalInterfaceConstants.GUROBI_SOLVER_NAME)) {
							getSolverSetUpDialog().cbQuadratic.setSelectedItem(GraphicalInterfaceConstants.GUROBI_SOLVER_NAME);
						}
					}
					if (ConfigProperties.getNonlinearSolverName() != null) {
						if (ConfigProperties.getNonlinearSolverName().equals(GraphicalInterfaceConstants.IPOPT_SOLVER_NAME)) {
							getSolverSetUpDialog().cbNonlinear.setSelectedItem(GraphicalInterfaceConstants.IPOPT_SOLVER_NAME);
						}
					}
				}
				getSolverSetUpDialog().setVisible(true);
			}    	     
		});
		
		optionsMenu.add(gurobiParametersItem);
		gurobiParametersItem.setMnemonic(KeyEvent.VK_G);
		if (GurobiSolver.isGurobiLinked()){
			gurobiParametersItem.setEnabled(true);
		} else {
			gurobiParametersItem.setEnabled(false);
		}
		gurobiParametersItem.addActionListener( new ActionListener()
		{
			public void actionPerformed(ActionEvent a)
			{
				gurobiParameters.setVisible( true );
			}    	     
		} );

		optionsMenu.add( glpkParametersItem );
		glpkParametersItem.setMnemonic( KeyEvent.VK_L );
		glpkParametersItem.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed( ActionEvent evt )
			{
				glpkParameters.setVisible( true );
			}
		} );
		
		optionsMenu.add( ipOptParametersItem );
		ipOptParametersItem.setMnemonic( KeyEvent.VK_I );
		ipOptParametersItem.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed( ActionEvent evt )
			{
				ipOptParameters.setVisible( true );
			}
		} );
		
		
		menuBar.add(optionsMenu);

		JMenu helpMenu = new JMenu("Help");
		helpMenu.setMnemonic(KeyEvent.VK_H);

		JMenuItem viewHelpTopics = new JMenuItem("View Help Topics");
		helpMenu.add(viewHelpTopics);
		viewHelpTopics.setMnemonic(KeyEvent.VK_H);

		viewHelpTopics.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {
				setUrlString(GraphicalInterfaceConstants.HELP_TOPICS_URL);
				openURL();				
			}    	     
		});

		helpMenu.add(aboutBox);
		aboutBox.setMnemonic(KeyEvent.VK_A);

		aboutBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {
                setUrlString(GraphicalInterfaceConstants.ABOUT_LICENSE_URL);
				getAboutDialog().setVisible(true);
			}    	     
		});

		menuBar.add(helpMenu);

		/**************************************************************************/
		// end menu bar
		/**************************************************************************/

		/**************************************************************************/
		//set up toolbar
		/**************************************************************************/			

		toolbar.add(savebutton);
		setUpToolbarButton(savebutton);
		savebutton.setToolTipText("Save (Ctrl+S)");
		savebutton.addActionListener(saveButtonActionListener);
		
		toolbar.addSeparator();
		
		toolbar.add(copybutton);
		setUpToolbarButton(copybutton);
		copybutton.setToolTipText("Copy (Ctrl+C)");
		copybutton.addActionListener(copyButtonActionListener);
		toolbar.add(pastebutton);
		setUpToolbarButton(pastebutton);
		pastebutton.setToolTipText("Paste (Ctrl+V)");
		pastebutton.addActionListener(pasteButtonActionListener);

		toolbar.addSeparator();

		addImage(undoSplitButton, undoLabel);
		addImage(undoSplitButton, undoGrayedLabel);
		undoSplitButton.setToolTipText("Can't Undo (Ctrl+Z)");	
		undoSplitButton.addMouseListener(undoButtonMouseListener);
		disableOptionComponent(undoSplitButton, undoLabel, undoGrayedLabel);
		toolbar.add(undoSplitButton);

		addImage(redoSplitButton, redoLabel);
		addImage(redoSplitButton, redoGrayedLabel);
		redoSplitButton.setToolTipText("Can't Redo (Ctrl+Y)");	
		redoSplitButton.addMouseListener(redoButtonMouseListener);
		disableOptionComponent(redoSplitButton, redoLabel, redoGrayedLabel);
		toolbar.add(redoSplitButton);		

		toolbar.add(findbutton);
		setUpToolbarButton(findbutton);
		findbutton.setToolTipText("Find/Replace (Ctrl+F)");
		findbutton.addActionListener(findButtonActionListener);

		/**************************************************************************/
		//end set up toolbar
		/**************************************************************************/	

		/************************************************************************/
		// set up tables
		/************************************************************************/

		// register actions
		ActionListener reactionsCopyActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				if (reactionsTable.getSelectedRow() > -1 && reactionsTable.getSelectedColumn() > 0) {
					reactionsCopy();
				}							
			}
		};

		ActionListener reactionsPasteActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				if (isRoot) {
					if (reactionsTable.getSelectedRow() > -1 && reactionsTable.getSelectedColumn() > 0) 
						reactionsPaste();							
				}									
			}
		};

		ActionListener reactionsClearActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				if (isRoot) {
					reactionsClear();
				}				
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

		ActionListener reactionsUndoActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				if (undoItem.isEnabled()) {
					undoButtonAction();
				}				
			}
		};

		ActionListener reactionsRedoActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				if (redoItem.isEnabled()) {
					redoButtonAction();
				}				
			}
		};
		
		ActionListener reactionsSaveActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				if (saveEnabled) {
					saveLoadedFile();	
				}			
			}
		};
		
		ActionListener reactionsSelectAllActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				metabolitesTable.clearSelection();
				includeRxnColumnNames = false;
				reactionsTable.selectAll();			
				selectReactionsRows();
			}
		};

		FindReplaceDialog.findButton.addActionListener(findReactionsButtonActionListener);
		FindReplaceDialog.findAllButton.addActionListener(findAllReactionsButtonActionListener);
		FindReplaceDialog.replaceButton.addActionListener(replaceReactionsButtonActionListener);
		FindReplaceDialog.replaceAllButton.addActionListener(replaceAllReactionsButtonActionListener);
		//findReplaceDialog.replaceFindButton.addActionListener(replaceFindReactionsButtonActionListener);
		FindReplaceDialog.doneButton.addActionListener(findDoneButtonActionListener);
		FindReplaceDialog.caseCheckBox.addActionListener(matchCaseActionListener);
		FindReplaceDialog.wrapCheckBox.addActionListener(wrapAroundActionListener);
		FindReplaceDialog.selectedAreaCheckBox.addActionListener(selectedAreaActionListener);
		FindReplaceDialog.backwardsCheckBox.addActionListener(searchBackwardsActionListener);

		KeyStroke reacCopy = KeyStroke.getKeyStroke(KeyEvent.VK_C,ActionEvent.CTRL_MASK,false);       
		KeyStroke reacPaste = KeyStroke.getKeyStroke(KeyEvent.VK_V,ActionEvent.CTRL_MASK,false); 		
		KeyStroke reacClear = KeyStroke.getKeyStroke(KeyEvent.VK_E,ActionEvent.CTRL_MASK,false); 
		KeyStroke reacFind = KeyStroke.getKeyStroke(KeyEvent.VK_F,ActionEvent.CTRL_MASK,false); 
		KeyStroke reacUndo = KeyStroke.getKeyStroke(KeyEvent.VK_Z,ActionEvent.CTRL_MASK,false); 
		KeyStroke reacRedo = KeyStroke.getKeyStroke(KeyEvent.VK_Y,ActionEvent.CTRL_MASK,false); 
		KeyStroke reacSave = KeyStroke.getKeyStroke(KeyEvent.VK_S,ActionEvent.CTRL_MASK,false);
		KeyStroke reacSelectAll = KeyStroke.getKeyStroke(KeyEvent.VK_A,ActionEvent.CTRL_MASK,false);

		DefaultTableModel blankReacModel = createBlankReactionsTableModel();
		setUpReactionsTable(blankReacModel);
		LocalConfig.getInstance().getReactionsTableModelMap().put(GraphicalInterfaceConstants.DEFAULT_MODEL_NAME, blankReacModel);
		new TableCellListener(reactionsTable, reacAction);
		ReactionsPopupListener reactionsPopupListener = new ReactionsPopupListener();
		reactionsTable.addMouseListener(reactionsPopupListener);
		reactionsTable.setRowHeight(20);
		reactionsTable.registerKeyboardAction(reactionsCopyActionListener,reacCopy,JComponent.WHEN_FOCUSED); 
		reactionsTable.registerKeyboardAction(reactionsPasteActionListener,reacPaste,JComponent.WHEN_FOCUSED); 		
		reactionsTable.registerKeyboardAction(reactionsClearActionListener,reacClear,JComponent.WHEN_FOCUSED); 
		reactionsTable.registerKeyboardAction(reactionsFindActionListener,reacFind,JComponent.WHEN_IN_FOCUSED_WINDOW); 
		reactionsTable.registerKeyboardAction(reactionsFindActionListener,reacFind,JComponent.WHEN_FOCUSED); 
		reactionsTable.registerKeyboardAction(reactionsUndoActionListener,reacUndo,JComponent.WHEN_IN_FOCUSED_WINDOW); 
		reactionsTable.registerKeyboardAction(reactionsUndoActionListener,reacUndo,JComponent.WHEN_FOCUSED); 
		reactionsTable.registerKeyboardAction(reactionsRedoActionListener,reacRedo,JComponent.WHEN_IN_FOCUSED_WINDOW); 
		reactionsTable.registerKeyboardAction(reactionsRedoActionListener,reacRedo,JComponent.WHEN_FOCUSED); 
		reactionsTable.registerKeyboardAction(reactionsSaveActionListener,reacSave,JComponent.WHEN_IN_FOCUSED_WINDOW); 
		reactionsTable.registerKeyboardAction(reactionsSaveActionListener,reacSave,JComponent.WHEN_FOCUSED); 
		reactionsTable.registerKeyboardAction(reactionsSelectAllActionListener,reacSelectAll,JComponent.WHEN_FOCUSED); 

		// from http://www.java.net/node/651087
		// need tab to skip hidden columns		
		reactionsTable.getInputMap().put(KeyStroke.getKeyStroke("TAB"), "actionString");
		reactionsTable.getActionMap().put("actionString", new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent ae) {
				// This overrides tab key and performs an action
				tabToNextVisibleCell(reactionsTable, getVisibleReactionsColumns());
			}
		});

		ActionListener metabolitesCopyActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				if (metabolitesTable.getSelectedRow() > -1 && metabolitesTable.getSelectedColumn() > 0) {
					metabolitesCopy();
				}			
			}
		};

		ActionListener metabolitesPasteActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				if (isRoot) {
					if (metabolitesTable.getSelectedRow() > -1 && metabolitesTable.getSelectedColumn() > 0) {
						metabolitesPaste();
					}					
				}
			}
		};

		ActionListener metabolitesClearActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				if (isRoot) {
					metabolitesClear();
				}				
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

		ActionListener metabolitesUndoActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				if (undoItem.isEnabled()) {
					undoButtonAction();
				}
			}
		};

		ActionListener metabolitesRedoActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				if (redoItem.isEnabled()) {
					redoButtonAction();
				}
			}
		};
		
		ActionListener metabolitesSaveActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				if (saveEnabled) {
					saveLoadedFile();	
				}
			}
		};

		ActionListener metabolitesSelectAllActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				reactionsTable.clearSelection();
				includeMtbColumnNames = false;
				metabolitesTable.selectAll();			
				selectMetabolitesRows();
			}
		};
		
		FindReplaceDialog.findButton.addActionListener(findMetabolitesButtonActionListener);
		FindReplaceDialog.findAllButton.addActionListener(findAllMetabolitesButtonActionListener);
		FindReplaceDialog.replaceButton.addActionListener(replaceMetabolitesButtonActionListener);
		FindReplaceDialog.replaceAllButton.addActionListener(replaceAllMetabolitesButtonActionListener);
		//findReplaceDialog.replaceFindButton.addActionListener(replaceFindMetabolitesButtonActionListener);
		FindReplaceDialog.doneButton.addActionListener(findDoneButtonActionListener);
		FindReplaceDialog.caseCheckBox.addActionListener(matchCaseActionListener);
		FindReplaceDialog.wrapCheckBox.addActionListener(wrapAroundActionListener);
		FindReplaceDialog.selectedAreaCheckBox.addActionListener(selectedAreaActionListener);
		FindReplaceDialog.backwardsCheckBox.addActionListener(searchBackwardsActionListener);

		KeyStroke metabCopy = KeyStroke.getKeyStroke(KeyEvent.VK_C,ActionEvent.CTRL_MASK,false);       
		KeyStroke metabPaste = KeyStroke.getKeyStroke(KeyEvent.VK_V,ActionEvent.CTRL_MASK,false);
		KeyStroke metabClear = KeyStroke.getKeyStroke(KeyEvent.VK_E,ActionEvent.CTRL_MASK,false);
		KeyStroke metabFind = KeyStroke.getKeyStroke(KeyEvent.VK_F,ActionEvent.CTRL_MASK,false);
		KeyStroke metabUndo = KeyStroke.getKeyStroke(KeyEvent.VK_Z,ActionEvent.CTRL_MASK,false);
		KeyStroke metabRedo = KeyStroke.getKeyStroke(KeyEvent.VK_Y,ActionEvent.CTRL_MASK,false);
		KeyStroke metabSave = KeyStroke.getKeyStroke(KeyEvent.VK_S,ActionEvent.CTRL_MASK,false);
		KeyStroke metabSelectAll = KeyStroke.getKeyStroke(KeyEvent.VK_A,ActionEvent.CTRL_MASK,false);

		DefaultTableModel blankMetabModel = createBlankMetabolitesTableModel();
		setUpMetabolitesTable(blankMetabModel);
		LocalConfig.getInstance().getMetabolitesTableModelMap().put(GraphicalInterfaceConstants.DEFAULT_MODEL_NAME, blankMetabModel);
		new TableCellListener(metabolitesTable, metabAction);
		MetabolitesPopupListener metabolitesPopupListener = new MetabolitesPopupListener();
		metabolitesTable.addMouseListener(metabolitesPopupListener);
		metabolitesTable.setRowHeight(20);
		metabolitesTable.registerKeyboardAction(metabolitesCopyActionListener,metabCopy,JComponent.WHEN_FOCUSED); 
		metabolitesTable.registerKeyboardAction(metabolitesPasteActionListener,metabPaste,JComponent.WHEN_FOCUSED); 
		metabolitesTable.registerKeyboardAction(metabolitesClearActionListener,metabClear,JComponent.WHEN_FOCUSED);
		metabolitesTable.registerKeyboardAction(metabolitesFindActionListener,metabFind,JComponent.WHEN_IN_FOCUSED_WINDOW);
		metabolitesTable.registerKeyboardAction(metabolitesFindActionListener,metabFind,JComponent.WHEN_FOCUSED);
		metabolitesTable.registerKeyboardAction(metabolitesUndoActionListener,metabUndo,JComponent.WHEN_IN_FOCUSED_WINDOW);
		metabolitesTable.registerKeyboardAction(metabolitesUndoActionListener,metabUndo,JComponent.WHEN_FOCUSED);
		metabolitesTable.registerKeyboardAction(metabolitesRedoActionListener,metabRedo,JComponent.WHEN_IN_FOCUSED_WINDOW);
		metabolitesTable.registerKeyboardAction(metabolitesRedoActionListener,metabRedo,JComponent.WHEN_FOCUSED);
		metabolitesTable.registerKeyboardAction(metabolitesSaveActionListener,metabSave,JComponent.WHEN_IN_FOCUSED_WINDOW);
		metabolitesTable.registerKeyboardAction(metabolitesSaveActionListener,metabSave,JComponent.WHEN_FOCUSED);
		metabolitesTable.registerKeyboardAction(metabolitesSelectAllActionListener,metabSelectAll,JComponent.WHEN_FOCUSED);  			

		DynamicTreePanel.getTreePanel().setNodeSelected(0);
		
		/************************************************************************/
		// end set up tables
		/************************************************************************/

		/************************************************************************/
		// set up other components of gui
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
						metabolitesTable.getModel().setValueAt(formulaBar.getText(), viewRow, metabolitesTable.getSelectedColumn());    							
//						try {
//							metabolitesTable.getModel().setValueAt(formulaBar.getText(), viewRow, metabolitesTable.getSelectedColumn());    							
//						} catch (Throwable t) {
//
//						}					
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
					enterPressed = true;
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
			public void focusGained(FocusEvent e) {	
				Component c = e.getComponent();
				if (c instanceof JTextField) {
					((JTextField)c).selectAll();
				}
				if (tabbedPane.getSelectedIndex() == 0 && reactionsTable.getSelectedRow() > - 1) {
					int viewRow = reactionsTable.convertRowIndexToModel(reactionsTable.getSelectedRow());
					if (reactionsTable.getModel().getValueAt(viewRow, reactionsTable.getSelectedColumn()) != null) {
						setTableCellOldValue((String) reactionsTable.getModel().getValueAt(viewRow, reactionsTable.getSelectedColumn()));    			
					} else {
						setTableCellOldValue("");
					}
					setCurrentReactionsRow(reactionsTable.getSelectedRow());
					setCurrentReactionsColumn(reactionsTable.getSelectedColumn());
				} else if (tabbedPane.getSelectedIndex() == 1 && metabolitesTable.getSelectedRow() > - 1) {
					int viewRow = metabolitesTable.convertRowIndexToModel(metabolitesTable.getSelectedRow());
					if (metabolitesTable.getModel().getValueAt(viewRow, metabolitesTable.getSelectedColumn()) != null) {
						setTableCellOldValue((String) metabolitesTable.getModel().getValueAt(viewRow, metabolitesTable.getSelectedColumn()));    			
					} else {
						setTableCellOldValue("");
					}
					setCurrentMetabolitesRow(metabolitesTable.getSelectedRow());
					setCurrentMetabolitesColumn(metabolitesTable.getSelectedColumn());
				}

				formulaBarFocusGained = true;				
			}

			@Override
			public void focusLost(FocusEvent arg0) {
				// if enter key not hit, but formula bar loses focus, old cell value is restored
				// this avoids invalid values being placed in cells
				formulaBarFocusGained = false;
				selectedCellChanged = true;	
				if (!enterPressed) {
					if (getCurrentReactionsRow() > -1 && getCurrentReactionsColumn() > 0) {
						if (tabbedPane.getSelectedIndex() == 0) {
							int viewRow = reactionsTable.convertRowIndexToModel(getCurrentReactionsRow());
							reactionsTable.getModel().setValueAt(getTableCellOldValue(), viewRow, getCurrentReactionsColumn());
						} 
					} else if (getCurrentMetabolitesRow() > -1 && getCurrentMetabolitesColumn() > 0) {
						if (tabbedPane.getSelectedIndex() == 1) {
							int viewRow = metabolitesTable.convertRowIndexToModel(getCurrentMetabolitesRow());
							metabolitesTable.getModel().setValueAt(getTableCellOldValue(), viewRow, getCurrentMetabolitesColumn());
						}
					} 
				}
				enterPressed = false;
			}
		});

		final JPopupMenu formulaBarPopupMenu = new JPopupMenu(); 
		formulaBarCutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
		formulaBarCopyItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
		formulaBarPasteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
		formulaBarSelectAllItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
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
					if (!analysisRunning) {
						formulaBarPopupMenu.show(formulaBar, e.getX(), e.getY()); 
					}
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
		tabbedPane.addTab(GraphicalInterfaceConstants.DEFAULT_REACTION_TABLE_TAB_NAME, scrollPaneReac);
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_R);

		JScrollPane scrollPaneMetab = new JScrollPane(metabolitesTable);
		LineNumberTableRowHeader tableMetabLineNumber = new LineNumberTableRowHeader(scrollPaneMetab, metabolitesTable);
		tableMetabLineNumber.setBackground(new Color(240, 240, 240));
		scrollPaneMetab.setRowHeaderView(tableMetabLineNumber);		
		JLabel metabRowLabel = new JLabel(GraphicalInterfaceConstants.ROW_HEADER_TITLE);
		metabRowLabel.setFont(rowLabel.getFont().deriveFont(Font.PLAIN));		
		scrollPaneMetab.setCorner(JScrollPane.UPPER_LEFT_CORNER, metabRowLabel);
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
					maybeDisplaySuspiciousMetabMessage(reactionRow);
					// prevents invisible id column from setting id in formulaBar for find events
					if (reactionsTable.getSelectedRow() > -1 && reactionsTable.getSelectedColumn() > 0) {
						// this code just sets value in formula bar to match value in selected cell
						// strange error has occurred here at times, which is why try/catch used here
						try {
							int viewRow = reactionsTable.convertRowIndexToModel(reactionsTable.getSelectedRow());
							formulaBar.setText((String) reactionsTable.getModel().getValueAt(viewRow, reactionsTable.getSelectedColumn()));
							setTableCellOldValue(formulaBar.getText());
						} catch (Throwable t) {
							formulaBar.setText("");
						}						
					} 
					enableOrDisableReactionsItems();
				} else if (tabIndex == 1 && metabolitesTable.getSelectedRow() > - 1) {
					selectedCellChanged = true;
					maybeDisplaySuspiciousMetabMessage(metaboliteRow);				
					if (metabolitesTable.getSelectedRow() > -1 && metabolitesTable.getSelectedColumn() > 0) {
						int viewRow = metabolitesTable.convertRowIndexToModel(metabolitesTable.getSelectedRow());
						formulaBar.setText((String) metabolitesTable.getModel().getValueAt(viewRow, metabolitesTable.getSelectedColumn())); 
						setTableCellOldValue(formulaBar.getText());
					}
					enableOrDisableMetabolitesItems();
				} else {
					maybeDisplaySuspiciousMetabMessage(statusBarRow());
					formulaBar.setText("");
				}
			}
		});

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
				{border, 0.06, 10, 0.08, 10, TableLayout.FILL, 10, 0.17, 5, 0.02, border}}; // Rows
				//{border, 0.06, 10, 0.04, 10, TableLayout.FILL, 10, 0.15, 5, 0.02, border}}; // old value

		setLayout (new TableLayout(size)); 

		add (toolbar, "0, 1, 4, 1");
		add (formulaPane, "1, 3, 1, 1");
		add (tabbedPane, "1, 5, 1, 1"); // Left
		add (treePanel, "3, 3, 1, 7"); // Right
		add (outputPane, "1, 7, 1, 1"); // Bottom
		add (statusBar, "1, 9, 3, 1");

		setBackground(Color.lightGray);
		
		tabbedPane.setSelectedIndex(0);
		scrollToLocation(reactionsTable, 0, 1);
		
		formulaBar.setText((String) reactionsTable.getModel().getValueAt(0, 1));   
		
		solutionListener = new Runnable()
		{
			@Override
			public void run()
			{
				GISolution current_giSolution = vecGISolution.get( 0 );
				vecGISolution.remove( 0 );
				
				final Utilities u = new Utilities();
				
				highlightUnusedMetabolites = false;
				highlightUnusedMetabolitesItem.setState(false);

				String dateTimeStamp = u.createDateTimeStamp();
				// GDBB date time stamp constant for given analysis
				if (gdbbSelected) {
					dateTimeStamp = getDateTimeStamp();
				}
				String prefix;
				if (gdbbSelected) {
					prefix = GraphicalInterfaceConstants.GDBB_PREFIX;
				} else {
					prefix = GraphicalInterfaceConstants.OPTIMIZATION_PREFIX;
				}
				
				final String optimizeName = 
						current_giSolution.isFoldered ? current_giSolution.folderName  :
							prefix
							+ LocalConfig.getInstance().getModelName() + dateTimeStamp;
				setOptimizeName(optimizeName);
				String solnName = optimizeName;
				if (!getOptimizeName().startsWith(prefix
						+ LocalConfig.getInstance().getModelName() + dateTimeStamp)) {
					String name = prefix
							+ LocalConfig.getInstance().getModelName() + dateTimeStamp
							+ "_" + getOptimizeName();
					setOptimizeName(name);
				}
				
				// copy mps file if exists to optimizationName + mps, then delete original file
				if (getMixedIntegerLinearSolverName().equals(GraphicalInterfaceConstants.GLPK_SOLVER_NAME)) {
					File f = new File(Utilities.getMOSTSettingsPath() + "LastProblem_GLPK.mps");
					copyMPSFile(f);
				} else if (getMixedIntegerLinearSolverName().equals(GraphicalInterfaceConstants.GUROBI_SOLVER_NAME)) {
					File f = new File(Utilities.getMOSTSettingsPath() + "LastProblem_Gurobi.mps");
					copyMPSFile(f);
				}

				// copy models, run optimization on these models
				DefaultTableModel metabolitesOptModel = copyMetabolitesTableModel((DefaultTableModel) metabolitesTable.getModel());
				DefaultTableModel reactionsOptModel = copyReactionsTableModel((DefaultTableModel) reactionsTable.getModel());				
				LocalConfig.getInstance().getReactionsTableModelMap().put(getOptimizeName(), reactionsOptModel);
				LocalConfig.getInstance().getMetabolitesTableModelMap().put(getOptimizeName(), metabolitesOptModel);
				//setUpReactionsTable(LocalConfig.getInstance().getReactionsTableModelMap().get(optimizeName));
				//setUpMetabolitesTable(LocalConfig.getInstance().getMetabolitesTableModelMap().get(optimizeName));
				LocalConfig.getInstance().getOptimizationFilesList().add(getOptimizeName());
				if (gdbbSelected) {
					// This file is unused for GDBB, but having it present in optimization files list
					// causes save optimization files prompt to appear even if all opt files have been saved.
					String timeStampLogName = GraphicalInterfaceConstants.GDBB_PREFIX +
							LocalConfig.getInstance().getModelName() + getDateTimeStamp();
					if (LocalConfig.getInstance().getOptimizationFilesList().contains(timeStampLogName)) {
						LocalConfig.getInstance().getOptimizationFilesList().remove(LocalConfig.getInstance().getOptimizationFilesList().indexOf(timeStampLogName));
					}
				}
				setTitle(GraphicalInterfaceConstants.TITLE + " - " + getOptimizeName());	
				listModel.addElement(getOptimizeName());	
				
				ReactionFactory rFactory = new ReactionFactory("SBML");
				rFactory.setFluxes( current_giSolution.soln, GraphicalInterfaceConstants.FLUX_VALUE_COLUMN,
						LocalConfig.getInstance().getReactionsTableModelMap().get(getOptimizeName()));
				if( current_giSolution.vaMin != null && current_giSolution.vaMax != null )
				{
					LocalConfig.getInstance().fvaColumnsVisible = true;
					rFactory.setFluxes(current_giSolution.vaMin, GraphicalInterfaceConstants.MIN_FLUX_COLUMN,
							LocalConfig.getInstance().getReactionsTableModelMap().get(optimizeName));
					rFactory.setFluxes(current_giSolution.vaMax, GraphicalInterfaceConstants.MAX_FLUX_COLUMN,
							LocalConfig.getInstance().getReactionsTableModelMap().get(optimizeName));
					LocalConfig.getInstance().getShowFVAColumnsList().add(getOptimizeName());
				}
				else
				{
					LocalConfig.getInstance().fvaColumnsVisible = false;
				}
				
				if( current_giSolution.isFoldered  )
					DynamicTreePanel.getTreePanel().addObject(
						(DefaultMutableTreeNode)DynamicTreePanel.getTreePanel()
						.getRootNode().getChildAt(DynamicTreePanel.getTreePanel()
						.getRootNode().getChildCount() - 1), new Solution( solnName, getOptimizeName() ), true);
				else
					DynamicTreePanel.getTreePanel().addObject(new Solution( solnName, getOptimizeName() ));
				//DynamicTreePanel.getTreePanel().setNodeSelected(GraphicalInterface.listModel.getSize() - 1);
				DynamicTreePanel.getTreePanel().selectLastNode();
				rFactory.resetKnockOuts();
				if( current_giSolution.knockoutOffset != null && current_giSolution.soln_ko != null )
					rFactory.setKnockouts( current_giSolution.soln_ko.subList( current_giSolution.knockoutOffset, current_giSolution.soln_ko.size()));		
				
				// update the tablemodel
				if( current_giSolution.updateGUIWithModel )
					current_giSolution.model.updateGUITableModel();
				
				if (LocalConfig.getInstance().hasValidGurobiKey) {
					Writer writer = null;
					try {
						StringBuffer outputText = current_giSolution.stringBuffer;
						
						File file = new File(u.createLogFileName(getOptimizeName() + ".log"));
						writer = new BufferedWriter(new FileWriter(file));
						writer.write(outputText.toString());

					} catch (FileNotFoundException e) {
						JOptionPane.showMessageDialog(null,                
								"File Not Found.",                
								"Error",                                
								JOptionPane.ERROR_MESSAGE);
						//e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						try {
							if (writer != null) {
								writer.close();
							}
						} catch (IOException e) {
							JOptionPane.showMessageDialog(null,                
									"File Not Found.",                
									"Error",                                
									JOptionPane.ERROR_MESSAGE);
							//e.printStackTrace();
						}
					}
					loadOutputPane(u.createLogFileName(getOptimizeName() + ".log"));
					if (getPopout() != null) {
						getPopout().load(u.createLogFileName(getOptimizeName() + ".log"), gi.getTitle());
					}
				} else {
					DynamicTreePanel.getTreePanel().setNodeSelected(0);
				}
				LocalConfig.getInstance().hasValidGurobiKey = true;
			}
		};
		
	}
	
	
	/********************************************************************************/
	//end constructor and layout
	/********************************************************************************/

	public void copyMPSFile(File f) {
		File copy = new File(Utilities.getMOSTSettingsPath() + getOptimizeName() + ".mps");
		if (f.exists()) {
			try {
				Utilities.copyFile(f, copy);
				Utilities.delete(f.getPath());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/*******************************************************************************/
	//begin formulaBar methods and actions
	/*******************************************************************************/ 

	public void updateReactionsCell() {
//		if (formulaBar.getText() != null) {
//			LocalConfig.getInstance().reactionsTableChanged = true;
//		}						
		int viewRow = reactionsTable.convertRowIndexToModel(reactionsTable.getSelectedRow());
		int id = Integer.valueOf((String) reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTIONS_ID_COLUMN));
		String newValue = formulaBar.getText();
		ReactionUndoItem undoItem = createReactionUndoItem(getTableCellOldValue(), newValue, reactionsTable.getSelectedRow(), reactionsTable.getSelectedColumn(), id, UndoConstants.TYPING, UndoConstants.REACTION_UNDO_ITEM_TYPE);
		setOldUsedMap(undoItem);
		undoItem.setMaxMetab(LocalConfig.getInstance().getMaxMetabolite());
		undoItem.setMaxMetabId(LocalConfig.getInstance().getMaxMetaboliteId());
		undoItem.setOldLowerBound((String) (reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.LOWER_BOUND_COLUMN)));
		undoItem.setOldUpperBound((String) (reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.UPPER_BOUND_COLUMN)));
		updateReactionsCellIfValid(getTableCellOldValue(), newValue, viewRow, reactionsTable.getSelectedColumn());
		if (reactionUpdateValid) {
			LocalConfig.getInstance().reactionsTableChanged = true;
			enableSaveItems(true);
			undoItem.setNewLowerBound((String) (reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.LOWER_BOUND_COLUMN)));
			undoItem.setNewUpperBound((String) (reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.UPPER_BOUND_COLUMN)));
			setNewUsedMap(undoItem);
			setUpReactionsUndo(undoItem);
			scrollToLocation(reactionsTable, reactionsTable.getSelectedRow(), reactionsTable.getSelectedColumn());
		} 		
	}

	public void updateMetabolitesCell() {
//		if (formulaBar.getText() != null) {
//			LocalConfig.getInstance().metabolitesTableChanged = true;
//		}						
		int viewRow = metabolitesTable.convertRowIndexToModel(metabolitesTable.getSelectedRow());		
		int id = Integer.valueOf((String) metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_ID_COLUMN));
		String newValue = formulaBar.getText();
		// these variables are needed since after the table is reloaded, there is
		// no selected cell
		int row = metabolitesTable.getSelectedRow();
		int col = metabolitesTable.getSelectedColumn();
		MetaboliteUndoItem undoItem = createMetaboliteUndoItem(getTableCellOldValue(), newValue, row, col, id, UndoConstants.TYPING, UndoConstants.METABOLITE_UNDO_ITEM_TYPE);
		setUndoOldCollections(undoItem);
		updateMetabolitesCellIfValid(getTableCellOldValue(), newValue, viewRow, metabolitesTable.getSelectedColumn());	
		if (metaboliteUpdateValid) {
			LocalConfig.getInstance().metabolitesTableChanged = true;
			enableSaveItems(true);
			// fixes bug where entering value in formula bar in a sorted column and then
			// hitting enter - old value (or value from somewhere else in row) set
			formulaBar.setText(newValue);
			setUndoNewCollections(undoItem);
			setUpMetabolitesUndo(undoItem);
			scrollToLocation(metabolitesTable, metabolitesTable.getSelectedRow(), metabolitesTable.getSelectedColumn());
		} 	
	}

	/*******************************************************************************/
	//end formulaBar methods and actions
	/*******************************************************************************/ 

	/*******************************************************************************/
	// begin Model menu methods and actions
	/*******************************************************************************/ 

	/*******************************************************************************/
	// load methods and actions
	/*******************************************************************************/ 

	class LoadSBMLAction implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			SaveChangesPrompt();
			saveFile = false;
			if (openFileChooser) {
				JTextArea output = null;
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setDialogTitle("Load SBML File"); 
				fileChooser.setFileFilter(new SBMLFileFilter());
				fileChooser.setFileFilter(new XMLFileFilter());
				fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);			
				//TODO: test the possibility of a global FileChooser

				String lastSBML_path = curSettings.get("LastSBML");
				Utilities u = new Utilities();
				// if path is null or does not exist, default used, else last path used
				fileChooser.setCurrentDirectory(new File(u.lastPath(lastSBML_path, fileChooser)));					
 
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
						listModel.clear();						
						DynamicTreePanel.getTreePanel().clear();
						String filename;
						if (rawFilename.endsWith(".xml")) {
							filename = rawFilename.substring(0, rawFilename.length() - 4);
						} else {
							filename = rawFilename.substring(0, rawFilename.length() - 5);
						}
						setSBMLFile(file);
						LocalConfig.getInstance().setModelName(filename);
						LocalConfig.getInstance().setProgress(0);
						progressBar.setVisible(true);
						progressBar.progress.setIndeterminate(true);
						disableLoadItems();

						timer.start();

						task = new Task();
						task.execute();
						saveEnabled = true;
						modelCollectionLoad = false;
					}
				}
			}			
		}
	}

	class LoadCSVAction implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			SaveChangesPrompt();
			saveFile = false;
			if (openFileChooser) {
				//setExtension(".csv");	
				getCSVLoadInterface().textMetabField.setText("");
				getCSVLoadInterface().textReacField.setText("");
				LocalConfig.getInstance().setMetabolitesCSVFile(null);
				LocalConfig.getInstance().hasMetabolitesFile = false;
				LocalConfig.getInstance().hasReactionsFile = false;
				getCSVLoadInterface().okButton.setEnabled(false);
				csvLoadInterface.setVisible(true);
			}				
		}
	}

	ActionListener okButtonCSVLoadActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent ae) {	
			getCSVLoadInterface().setVisible(false);
			getCSVLoadInterface().dispose();	
			//loadSetUp();
			//isCSVFile = true;
			loadCSV();
		}
	}; 

	ActionListener cancelButtonCSVLoadActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent ae) {	
			getCSVLoadInterface().setVisible(false);
		}
	};

	public void loadCSV() {	
		LocalConfig.getInstance().setMetabolitesNextRowCorrection(0);

		if (LocalConfig.getInstance().hasMetabolitesFile && LocalConfig.getInstance().getMetabolitesCSVFile() != null) {
			TextMetabolitesModelReader reader = new TextMetabolitesModelReader();
			ArrayList<String> columnNamesFromFile = reader.columnNamesFromFile(LocalConfig.getInstance().getMetabolitesCSVFile(), 0);
			MetaboliteColumnNameInterface columnNameInterface = new MetaboliteColumnNameInterface(columnNamesFromFile);
			setMetaboliteColumnNameInterface(columnNameInterface);
			getMetaboliteColumnNameInterface().setIconImages(icons);					
			getMetaboliteColumnNameInterface().setSize(600, 330);
			getMetaboliteColumnNameInterface().setResizable(false);
			getMetaboliteColumnNameInterface().setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			getMetaboliteColumnNameInterface().setLocationRelativeTo(null);											
			getMetaboliteColumnNameInterface().addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent evt) {
					metaboliteColumnNameCloseAction();	        	
				}
			});
			getMetaboliteColumnNameInterface().okButton.addActionListener(okButtonCSVMetabLoadActionListener);
			getMetaboliteColumnNameInterface().cancelButton.addActionListener(cancelButtonCSVMetabLoadActionListener);
			getMetaboliteColumnNameInterface().setModal(true);
			getMetaboliteColumnNameInterface().setVisible(true);
		} else {
			loadReactionColumnNameInterface();
		} 		
	}

	public static File chooseCSVFile( String title )
	{
		String lastCSV_path = curSettings.get(SettingsConstants.CSV_SETTINGS_NAME);
		Utilities u = new Utilities();
		// if path is null or does not exist, default used, else last path used
		final JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(u.lastPath(lastCSV_path, fileChooser)));	
		fileChooser.setFileSelectionMode( JFileChooser.FILES_ONLY );
		fileChooser.setDialogTitle( title );
		fileChooser.setFileFilter( new javax.swing.filechooser.FileFilter()
		{

			@Override
			public boolean accept( File file )
			{
				if( file.isDirectory() || file.exists() && file.getName().toLowerCase().endsWith( ".csv" ) )
				{
					curSettings.add( SettingsConstants.CSV_SETTINGS_NAME, file.getAbsolutePath() );
					//bug - this prints out every file in directory
					//System.out.println(file.getAbsolutePath());
					return true;
				}
				return false;
			}

			@Override
			public String getDescription()
			{
				return "csv files";
			}
			
		});
		if( JFileChooser.APPROVE_OPTION != fileChooser.showOpenDialog( null ) )
			return null;
		return fileChooser.getSelectedFile();
	}
	
	public void metaboliteColumnNameCloseAction() {
		getMetaboliteColumnNameInterface().setVisible(false);
		getMetaboliteColumnNameInterface().dispose();
		if (LocalConfig.getInstance().hasReactionsFile) {
			loadReactionColumnNameInterface();
		} 
	}

	ActionListener okButtonCSVMetabLoadActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent ae) {
			getMetaboliteColumnNameInterface().getColumnIndices();
			setFileType(GraphicalInterfaceConstants.CSV_FILE_TYPE);

			loadSetUp();
			TextMetabolitesModelReader reader = new TextMetabolitesModelReader();
			if (getMetaboliteColumnNameInterface().validColumns) {
				getMetaboliteColumnNameInterface().setVisible(false);
				getMetaboliteColumnNameInterface().dispose();
				
				reader.load(LocalConfig.getInstance().getMetabolitesCSVFile());	
				setUpMetabolitesTable(TextMetabolitesModelReader.getMetabolitesTableModel());
				LocalConfig.getInstance().getMetabolitesTableModelMap().put(LocalConfig.getInstance().getModelName(), TextMetabolitesModelReader.getMetabolitesTableModel());
				if (LocalConfig.getInstance().hasReactionsFile) {
					loadReactionColumnNameInterface();
				} else {
					curSettings.add("LastCSVReactions", "none");
					saveEnabled = true;
					modelCollectionLoad = false;
					DefaultTableModel blankReacModel = createBlankReactionsTableModel();
					setUpReactionsTable(blankReacModel);
					LocalConfig.getInstance().getReactionsTableModelMap().put(LocalConfig.getInstance().getModelName(), blankReacModel);
					setUpTables();
				}	
			}					
		}
	};

	ActionListener cancelButtonCSVMetabLoadActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent ae) {
			metaboliteColumnNameCloseAction();
		}
	};

	public void loadReactionColumnNameInterface() {
		LocalConfig.getInstance().setReactionsNextRowCorrection(0);

		TextReactionsModelReader reader = new TextReactionsModelReader();			    
		ArrayList<String> columnNamesFromFile = reader.columnNamesFromFile(LocalConfig.getInstance().getReactionsCSVFile(), 0);	
		ReactionColumnNameInterface columnNameInterface = new ReactionColumnNameInterface(columnNamesFromFile);
		setReactionColumnNameInterface(columnNameInterface);
		getReactionColumnNameInterface().setIconImages(icons);					
		getReactionColumnNameInterface().setSize(600, 650);
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
					//clearTables();
				}        	
			}
		});
		getReactionColumnNameInterface().okButton.addActionListener(okButtonCSVReacLoadActionListener);
		getReactionColumnNameInterface().cancelButton.addActionListener(cancelButtonCSVReacLoadActionListener);
		getReactionColumnNameInterface().setModal(true);
		getReactionColumnNameInterface().setVisible(true);	
	}

	ActionListener okButtonCSVReacLoadActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent ae) {
			getReactionColumnNameInterface().getColumnIndices();
			
			if (!LocalConfig.getInstance().hasMetabolitesFile) {
				curSettings.add("LastCSVMetabolites", "none");
				loadSetUp();
			}
			saveEnabled = true;
			modelCollectionLoad = false;
			TextReactionsModelReader reader = new TextReactionsModelReader();
			if (getReactionColumnNameInterface().validColumns) {
				getReactionColumnNameInterface().setVisible(false);
				getReactionColumnNameInterface().dispose();

				DynamicTreePanel.getTreePanel().clear();
				listModel.clear();
				setFileType(GraphicalInterfaceConstants.CSV_FILE_TYPE);
				
				reader.load(LocalConfig.getInstance().getReactionsCSVFile());	
				setUpReactionsTable(TextReactionsModelReader.getReactionsTableModel());
				LocalConfig.getInstance().getReactionsTableModelMap().put(LocalConfig.getInstance().getModelName(), TextReactionsModelReader.getReactionsTableModel());
				// sets updated model if any metabolites added in reactions load
				setUpMetabolitesTable(TextReactionsModelReader.getMetabolitesTableModel());
				LocalConfig.getInstance().getMetabolitesTableModelMap().put(LocalConfig.getInstance().getModelName(), TextReactionsModelReader.getMetabolitesTableModel());
				setUpTables();
				setEnableAnalysisMenuItems( true );
				if (LocalConfig.getInstance().getSuspiciousMetabolites().size() > 0) {
					setUrlString(GraphicalInterfaceConstants.SUSPICIOUS_METABOLITES_URL);
					getSuspiciousMetabolitesDialog().setVisible(true);
				}
			}			
		}
	};

	ActionListener cancelButtonCSVReacLoadActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent ae) {
			csvReactionCancelLoadAction();
		}
	};

	public void csvReactionCancelLoadAction() {
		reactionCancelLoad = true;
	}

	class LoadExistingItemAction implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			SaveChangesPrompt();
			saveFile = false;
			if (openFileChooser) {
				if (!saveChangesOKClicked) {
					getModelCollectionTable().setVisible(true);
					loadExistingItem.setEnabled(false);
					disableLoadItems();
					disableMenuItemsForFVA(true);
				} else {
					loadExistingItem.setEnabled(true);
				}
			}			
		}
	}

	// need path in settings to prevent overwriting
	ActionListener modelCollectionOKButtonActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent ae) {			
			if (openFileChooser) {
				loadSetUp();
				listModel.clear();
				DynamicTreePanel.getTreePanel().clear();
				saveEnabled = false;
				modelCollectionLoad = true;
				if (getModelCollectionTable().getFileType().equals(GraphicalInterfaceConstants.SBML_FILE_TYPE)) {
					setFileType(GraphicalInterfaceConstants.SBML_FILE_TYPE);
					String path = getModelCollectionTable().getPath();
					File file = new File(path);
					setSBMLFile(file);
					LocalConfig.getInstance().setModelName(getModelCollectionTable().getFileName());
					LocalConfig.getInstance().setProgress(0);
					disableLoadItems();
					disableMenuItemsForFVA(true);
					progressBar.setVisible(true);
					progressBar.progress.setIndeterminate(true);

					timer.start();

					task = new Task();
					task.execute();
				} else if (getModelCollectionTable().getFileType().equals(GraphicalInterfaceConstants.CSV_FILE_TYPE)) {
					setFileType(GraphicalInterfaceConstants.CSV_FILE_TYPE);
					// currently there are no csv files in the table, but if added in the future
					// add loading code here
				}

				//loadExistingItem.setEnabled(true);					
			}			
		}
	};

	ActionListener modelCollectionCancelButtonActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent ae) {						
			loadExistingItem.setEnabled(true);
			enableLoadItems();
			disableMenuItemsForFVA(false);
		}
	};

	/*******************************************************************************/
	//end load methods and actions
	/*******************************************************************************/

	/*******************************************************************************/
	//save methods and actions
	/*******************************************************************************/

	class SaveItemAction implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			saveLoadedFile();
		}
	} 
	
	public void saveLoadedFile() {
		saveFile = true;
		if (!LocalConfig.getInstance().getModelName().equals(GraphicalInterfaceConstants.DEFAULT_MODEL_NAME)) {			
			// if user has loaded an sbml file, lastsbml will not be null, but must check if file exists
			// since settings file may not be correct
			if (getFileType().equals(GraphicalInterfaceConstants.SBML_FILE_TYPE)) {
				LocalConfig.getInstance().reactionsTableChanged = false;
				LocalConfig.getInstance().metabolitesTableChanged = false;
				if (curSettings.get("LastSBML") != null && isRoot) {
					File f = new File(curSettings.get("LastSBML"));
					if (f.exists()) {
						showJSBMLFileChooser = false;
						setSBMLFile(f);
						String modelName = f.getName();
						if (modelName.endsWith(".xml")) {
							modelName = modelName.substring(0, modelName.length() - 4);
						}
						LocalConfig.getInstance().setModelName(modelName);
						saveAsSBML();
						showJSBMLFileChooser = true;
					} else {
						saveAsSBML();
					}										
				} else {
					saveAsSBML();
				}
			} else if (getFileType().equals(GraphicalInterfaceConstants.CSV_FILE_TYPE)) {
				if (curSettings.get("LastCSVMetabolites") != null && !curSettings.get("LastCSVMetabolites").equals("none") && 
						curSettings.get("LastCSVReactions") != null && !curSettings.get("LastCSVReactions").equals("none") && isRoot) {
					File f1 = new File(curSettings.get("LastCSVMetabolites"));
					File f2 = new File(curSettings.get("LastCSVReactions"));
					if (f1.exists() && f2.exists()) {
						saveMetabolitesTextFile(f1.getPath(), f1.getName());
						saveReactionsTextFile(f2.getPath(), f2.getName());
						LocalConfig.getInstance().metabolitesTableChanged = false;
						LocalConfig.getInstance().reactionsTableChanged = false;
					} else {
						saveCSVWithInterface();
					}
				} else {
					saveCSVWithInterface();
				}
			} 
		} else {
			// if "untitled" model name - that is file not saved, save default is csv
			// since it may be very confusing if the model the user just built is rewritten.
			// with save as csv the model remains unchanged
			saveCSVWithInterface();
		}
		// fixes bug where if cancel is pressed on file chooser, exit does not work
		exit = true;
	}
	
	public void saveAsSBML() {
		saveFile = true;
		saveSBML = true;
		try {
			JSBMLWriter jWrite = new JSBMLWriter();
			if (saveOptFile) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)
						DynamicTreePanel.getTreePanel().tree.getLastSelectedPathComponent();
				Solution nodeInfo = (Solution)node.getUserObject();	
//				String suffix = "";
//				if (nodeInfo.getIndex() > 0) {
//					suffix = "_[" + nodeInfo.getIndex() + "]";
//				}
//				jWrite.setOptFilePath(nodeInfo.getDatabaseName() + suffix);
				jWrite.setOptFilePath(nodeInfo.getDatabaseName());
				if (LocalConfig.getInstance().getOptimizationFilesList().contains(nodeInfo.getDatabaseName())) {
					LocalConfig.getInstance().getOptimizationFilesList().remove(LocalConfig.getInstance().getOptimizationFilesList().indexOf(nodeInfo.getDatabaseName()));
				}
			}

			jWrite.formConnect(LocalConfig.getInstance());
			
			if (jWrite.load) {
				setSBMLFile(jWrite.getOutFile());
				String modelName = jWrite.getOutFile().getName();
				if (showJSBMLFileChooser) {
					curSettings.add("LastSBML", jWrite.getOutFile().getAbsolutePath());
				}	
				if (!saveOptFile) {
					if (modelName.endsWith(".xml")) {
						modelName = modelName.substring(0, modelName.length() - 4);
					}
					LocalConfig.getInstance().setModelName(modelName);
					LocalConfig.getInstance().setProgress(0);
					progressBar.setVisible(true);
					progressBar.progress.setIndeterminate(true);

					timer.start();
					task = new Task();
					task.execute();
					saveEnabled = true;
					modelCollectionLoad = false;
				} 
			}		
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null,                
					"Unable to Write File.",                
					"Error",                                
					JOptionPane.ERROR_MESSAGE);
			//e.printStackTrace();
		}	
	}
	
	class SaveSBMLItemAction implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			saveAsSBML();
		}
	}
	
	class SaveCSVItemAction implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			saveCSVWithInterface();
		}
	}
	
	public void saveCSVWithInterface() {
		saveFile = true;
		updateCSVSaveInterface();
		getCSVSaveInterface().setVisible(true);
	}
	
	public void updateCSVSaveInterface() {
		// If loaded file is of csv type. last csv reactions and metabolites will be correct
		// for loaded model. Since default file type is csv, if model is untitled, don't want
		// user to overwrite last loaded csv from a previous session
		if (isRoot) {
			if (getFileType().equals(GraphicalInterfaceConstants.CSV_FILE_TYPE) && !LocalConfig.getInstance().getModelName().equals(GraphicalInterfaceConstants.DEFAULT_MODEL_NAME)) {
				if (curSettings.get("LastCSVMetabolites") != null && !curSettings.get("LastCSVMetabolites").equals("none")) {
					getCSVSaveInterface().updateMetabolitesPath(curSettings.get("LastCSVMetabolites"));
				} else {
					getCSVSaveInterface().updateMetabolitesPath("");
				}
				if (curSettings.get("LastCSVReactions") != null && !curSettings.get("LastCSVReactions").equals("none")) {
					getCSVSaveInterface().updateReactionsPath(curSettings.get("LastCSVReactions"));
				} else {
					getCSVSaveInterface().updateReactionsPath("");
				}
			} else {
				getCSVSaveInterface().updateMetabolitesPath("");
				getCSVSaveInterface().updateReactionsPath("");
			}
			getCSVSaveInterface().disableMetabolitesItems(!LocalConfig.getInstance().metabolitesTableChanged);
			getCSVSaveInterface().disableReactionsItems(!LocalConfig.getInstance().reactionsTableChanged);
		} else {
			getCSVSaveInterface().updateMetabolitesPath("");
			getCSVSaveInterface().updateReactionsPath("");
		}
	}
	
	ActionListener okButtonCSVSaveActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent ae) {
			String metabPath = getCSVSaveInterface().metabolitesPath();
			String reacPath = getCSVSaveInterface().reactionsPath();
			getCSVSaveInterface().setVisible(false);
			getCSVSaveInterface().dispose();
			if (metabPath != null && metabPath.length() > 0) {
				File f1 = new File(metabPath);
				saveMetabolitesTextFile(f1.getPath(), f1.getName());
			}
			if (reacPath != null && reacPath.length() > 0) {
				File f2 = new File(reacPath);
				saveReactionsTextFile(f2.getPath(), f2.getName());
			}
		}
	}; 

	ActionListener cancelButtonCSVSaveActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent ae) {	
			getCSVSaveInterface().setVisible(false);
		}
	};

	class SaveCSVMetabolitesItemAction implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			saveFile = true;
			saveMetabolitesTextFileChooser();
		}
	}

	public void saveMetabolitesTextFile(String path, String filename) {
		saveEnabled = true;
		modelCollectionLoad = false;
		TextMetabolitesWriter writer = new TextMetabolitesWriter();
		writer.write(path);	
		if (filename.endsWith(".csv")) {
			filename = filename.substring(0, filename.length() - 4);
		}
		setTitle(GraphicalInterfaceConstants.TITLE + " - " + filename);
		listModel.setElementAt(filename, 0);
		LocalConfig.getInstance().setModelName(filename);
		
		DefaultTableModel metabolitesModel = copyMetabolitesTableModel((DefaultTableModel) metabolitesTable.getModel());
		DefaultTableModel reactionsModel = copyReactionsTableModel((DefaultTableModel) reactionsTable.getModel());				
		LocalConfig.getInstance().getReactionsTableModelMap().put(filename, reactionsModel);
		LocalConfig.getInstance().getMetabolitesTableModelMap().put(filename, metabolitesModel);

		setUpTables();
		setFileType(GraphicalInterfaceConstants.CSV_FILE_TYPE);
		LocalConfig.getInstance().metabolitesTableChanged = false;
		curSettings.add(SettingsConstants.CSV_MODEL_SETTINGS_NAME, path);
		curSettings.add("LastCSVMetabolites", path);
	}

	public void saveMetabolitesTextFileChooser() {
		JTextArea output = null;
		JFileChooser fileChooser = new JFileChooser(new File(LocalConfig.getInstance().getModelName()));
		fileChooser.setDialogTitle("Save CSV Metabolites File");
		fileChooser.setFileFilter(new CSVFileFilter());
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

		String lastCSV_path = curSettings.get(SettingsConstants.CSV_MODEL_SETTINGS_NAME);
		Utilities u = new Utilities();
		// if path is null or does not exist, default used, else last path used		
		fileChooser.setCurrentDirectory(new File(u.lastPath(lastCSV_path, fileChooser)));					

		boolean done = false;
		while (!done) {
			//... Open a file dialog.
			int retval = fileChooser.showSaveDialog(output);
			if (retval == JFileChooser.CANCEL_OPTION) {
				done = true;
				//exit = false;
			}
			if (retval == JFileChooser.APPROVE_OPTION) {
				//... The user selected a file, get it, use it.
				String rawPathName = fileChooser.getSelectedFile().getAbsolutePath();
				if (!rawPathName.endsWith(".csv")) {
					rawPathName = rawPathName + ".csv";
				}
				curSettings.add(SettingsConstants.CSV_MODEL_SETTINGS_NAME, rawPathName);
				curSettings.add("LastCSVMetabolites", rawPathName);

				LocalConfig.getInstance().hasMetabolitesFile = true;

				//checks if filename endswith .csv else renames file to end with .csv
				String path = fileChooser.getSelectedFile().getPath();
				String filename = fileChooser.getSelectedFile().getName();
				if (!path.endsWith(".csv")) {
					path = path + ".csv";
				}

				File file = new File(path);
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

	class SaveCSVReactionsItemAction implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			saveFile = true;
			saveReactionsTextFileChooser(); 
		}
	}

	public void saveReactionsTextFile(String path, String filename) {
		saveEnabled = true;
		modelCollectionLoad = false;
		TextReactionsWriter writer = new TextReactionsWriter();
		writer.write(path);		
		if (filename.endsWith(".csv")) {
			filename = filename.substring(0, filename.length() - 4);
		}
		if (saveOptFile) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)
					DynamicTreePanel.getTreePanel().tree.getLastSelectedPathComponent();
			Solution nodeInfo = (Solution)node.getUserObject();	

			if (LocalConfig.getInstance().getOptimizationFilesList().contains(nodeInfo.getDatabaseName())) {
				LocalConfig.getInstance().getOptimizationFilesList().remove(LocalConfig.getInstance().getOptimizationFilesList().indexOf(nodeInfo.getDatabaseName()));
			}
		}
		if (!saveOptFile) {
			setTitle(GraphicalInterfaceConstants.TITLE + " - " + filename);	
			listModel.setElementAt(filename, 0);
			LocalConfig.getInstance().setModelName(filename);
			
			DefaultTableModel metabolitesModel = copyMetabolitesTableModel((DefaultTableModel) metabolitesTable.getModel());
			DefaultTableModel reactionsModel = copyReactionsTableModel((DefaultTableModel) reactionsTable.getModel());				
			LocalConfig.getInstance().getReactionsTableModelMap().put(filename, reactionsModel);
			LocalConfig.getInstance().getMetabolitesTableModelMap().put(filename, metabolitesModel);
			
			setUpTables();
			setFileType(GraphicalInterfaceConstants.CSV_FILE_TYPE);
			LocalConfig.getInstance().reactionsTableChanged = false;
			curSettings.add(SettingsConstants.CSV_MODEL_SETTINGS_NAME, path);
			curSettings.add("LastCSVReactions", path);
		}	
		saveOptFile = false;
	}

	public void saveReactionsTextFileChooser() {
		JTextArea output = null;
		JFileChooser fileChooser = new JFileChooser(new File(LocalConfig.getInstance().getModelName()));
		fileChooser.setDialogTitle("Save CSV Reactions File");
		fileChooser.setFileFilter(new CSVFileFilter());
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

		String lastCSV_path = curSettings.get(SettingsConstants.CSV_MODEL_SETTINGS_NAME);
		Utilities u = new Utilities();
		// if path is null or does not exist, default used, else last path used		
		fileChooser.setCurrentDirectory(new File(u.lastPath(lastCSV_path, fileChooser)));	

		boolean done = false;
		while (!done) {
			//... Open a file dialog.
			File file = null;
			if (saveOptFile) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)
						DynamicTreePanel.getTreePanel().tree.getLastSelectedPathComponent();
				Solution nodeInfo = (Solution)node.getUserObject();	
//				String suffix = "";
//				if (nodeInfo.getIndex() > 0) {
//					suffix = "_[" + nodeInfo.getIndex() + "]";
//				}
//				file = new File(nodeInfo.getDatabaseName() + suffix);
				file = new File(nodeInfo.getDatabaseName());
				fileChooser.setSelectedFile(file);
			}
			int retval = fileChooser.showSaveDialog(output);
			if (retval == JFileChooser.CANCEL_OPTION) {
				done = true;
				//exit = false;
			}
			if (retval == JFileChooser.APPROVE_OPTION) {            	  
				//... The user selected a file, get it, use it.
				String rawPathName = fileChooser.getSelectedFile().getAbsolutePath();
				if (!rawPathName.endsWith(".csv")) {
					rawPathName = rawPathName + ".csv";
				}
				curSettings.add(SettingsConstants.CSV_MODEL_SETTINGS_NAME, rawPathName);
				curSettings.add("LastCSVReactions", rawPathName);

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
					LocalConfig.getInstance().reactionsTableChanged = false;
				}			                  	  
			}
		}
	}

	class ClearAction implements ActionListener {
		public void actionPerformed(ActionEvent cae) {
			LocalConfig.getInstance().setModelName(GraphicalInterfaceConstants.DEFAULT_MODEL_NAME);
			clearTables();
		}
	}

	public void clearTables() {
		SaveChangesPrompt();
		loadSetUp();
		listModel.clear();
		DynamicTreePanel.getTreePanel().clear();
		DefaultTableModel blankReacModel = createBlankReactionsTableModel();
		setUpReactionsTable(blankReacModel);
		LocalConfig.getInstance().getReactionsTableModelMap().put(GraphicalInterfaceConstants.DEFAULT_MODEL_NAME, blankReacModel);
		DefaultTableModel blankMetabModel = createBlankMetabolitesTableModel();
		setUpMetabolitesTable(blankMetabModel);
		LocalConfig.getInstance().getMetabolitesTableModelMap().put(GraphicalInterfaceConstants.DEFAULT_MODEL_NAME, blankMetabModel);
		LocalConfig.getInstance().setModelName(GraphicalInterfaceConstants.DEFAULT_MODEL_NAME);
		setUpTables();
		setEnableAnalysisMenuItems( true );
	}

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

	class JarFileFilter extends javax.swing.filechooser.FileFilter {
		public boolean accept(File f) {
			return f.isDirectory() || f.getName().toLowerCase().endsWith(".jar");
		}

		public String getDescription() {
			return ".jar files";
		}
	}

	class ExitAction implements ActionListener {
		public void actionPerformed(ActionEvent cae) {
			SaveChangesPrompt();
			if (exit) {
				// Exit the application
				System.exit(0);	
			}			
		}
	}

	/**
	 * Prompts user on load or exit to save table changes if changes have been made and optimizations
	 * if any have been run. Table changes message shown first, then optimizations
	 */
	public void SaveChangesPrompt() {
		openFileChooser = true;
		if (LocalConfig.getInstance().metabolitesTableChanged || LocalConfig.getInstance().reactionsTableChanged) {
			saveTableChanges();
		} else if (LocalConfig.getInstance().getOptimizationFilesList().size() > 0) {
			saveOptimizationsPrompt();
		}
	}	

	/**
	 * Prompt shown on exit or load action if either table has been changed.
	 */
	public void saveTableChanges() {
		openFileChooser = true;
		Object[] options = {"  Yes  ", "   No   ", "Cancel"};
		String message = "";
		String suffix = " Save changes?";
		if (LocalConfig.getInstance().metabolitesTableChanged && LocalConfig.getInstance().reactionsTableChanged) {
			message += "Reactions table and Metabolites table changed." + suffix;
			
		} else if (LocalConfig.getInstance().reactionsTableChanged) {
			message += "Reactions table changed." + suffix;
		} else if (LocalConfig.getInstance().metabolitesTableChanged) {
			message += "Metabolites table changed." + suffix;
		} else {
			message += suffix;
		}
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
			if (getFileType().equals("csv")) {
				saveCSVWithInterface();
			} else if (getFileType().equals("sbml")) {
				if (LocalConfig.getInstance().metabolitesTableChanged ||
						LocalConfig.getInstance().reactionsTableChanged) {
					saveOptFile = false;
					saveAsSBML();
					//						showJSBMLFileChooser = true;
				}
			}	
			if (LocalConfig.getInstance().getOptimizationFilesList().size() > 0) {
				saveOptimizationsPrompt();
			} else {
				LocalConfig.getInstance().getOptimizationFilesList().clear();
				exit = true;
			}
		}
		if (choice == JOptionPane.NO_OPTION)
		{
			if (LocalConfig.getInstance().getOptimizationFilesList().size() > 0) {
				saveOptimizationsPrompt();
			} else {
				exit = true;
			}
		}
		if (choice == JOptionPane.CANCEL_OPTION) {
			exit = false;
			openFileChooser = false;
		}
	}

	/**
	 * Prompt shown on exit or load action to save optimizations if any have been run
	 */
	public void saveOptimizationsPrompt() {
		saveChangesOKClicked = false;
		Object[] options = {"  Yes  ", "   No   "};
		int choice = JOptionPane.showOptionDialog(null, 
				"Optimizations have not been saved. Save Optimizations?", 
				"Save Optimizations?",  
				JOptionPane.YES_NO_CANCEL_OPTION, 
				JOptionPane.QUESTION_MESSAGE, 
				null, options, options[0]);
		//options[0] sets "Yes" as default button
		// interpret the user's choice	
		if (choice == JOptionPane.YES_OPTION)
		{
			// close dialog to give user a chance to save optimizations
			// do not exit program
			exit = false;
			// if yes clicked and action was load Model Collection Table,
			// do not load Model Collection Table, give user chance to save
			// optimizations
			saveChangesOKClicked = true;
		}
		if (choice == JOptionPane.NO_OPTION)
		{
			exit = true;
		}
	}

	/*******************************************************************************/
	//end Model menu methods and actions
	/*******************************************************************************/

	/*******************************************************************************/
	// table actions
	/*******************************************************************************/

	//based on code from http://tips4java.wordpress.com/2009/06/07/table-cell-listener/
	Action reacAction = new AbstractAction()
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent ae)
		{		  	
			TableCellListener tcl = (TableCellListener)ae.getSource();

			if (tcl.getOldValue() != tcl.getNewValue()) {
				int id = Integer.parseInt((String) (reactionsTable.getModel().getValueAt(tcl.getRow(), GraphicalInterfaceConstants.REACTIONS_ID_COLUMN)));
				ReactionUndoItem undoItem = createReactionUndoItem(tcl.getOldValue(), tcl.getNewValue(), reactionsTable.getSelectedRow(), tcl.getColumn(), id, UndoConstants.TYPING, UndoConstants.REACTION_UNDO_ITEM_TYPE);
				setOldUsedMap(undoItem);
				undoItem.setMaxMetab(LocalConfig.getInstance().getMaxMetabolite());
				undoItem.setMaxMetabId(LocalConfig.getInstance().getMaxMetaboliteId());
				undoItem.setOldLowerBound((String) (reactionsTable.getModel().getValueAt(tcl.getRow(), GraphicalInterfaceConstants.LOWER_BOUND_COLUMN)));			
				undoItem.setOldUpperBound((String) (reactionsTable.getModel().getValueAt(tcl.getRow(), GraphicalInterfaceConstants.UPPER_BOUND_COLUMN)));			
				updateReactionsCellIfValid(tcl.getOldValue(), tcl.getNewValue(), tcl.getRow(), tcl.getColumn());
				if (reactionUpdateValid) {
					LocalConfig.getInstance().reactionsTableChanged = true;
					enableSaveItems(true);
					formulaBar.setText(tcl.getNewValue());
					undoItem.setNewLowerBound((String) (reactionsTable.getModel().getValueAt(tcl.getRow(), GraphicalInterfaceConstants.LOWER_BOUND_COLUMN)));			
					undoItem.setNewUpperBound((String) (reactionsTable.getModel().getValueAt(tcl.getRow(), GraphicalInterfaceConstants.UPPER_BOUND_COLUMN)));			
					setNewUsedMap(undoItem);
					setUpReactionsUndo(undoItem);
				} else {
					formulaBar.setText(tcl.getOldValue());
				}
			}
		}
	};

	Action metabAction = new AbstractAction()
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

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
					LocalConfig.getInstance().metabolitesTableChanged = true;
					enableSaveItems(true);
					if (renameMetabolite) {
						undoItem.setUndoType(UndoConstants.RENAME_METABOLITE);
					} else {
						undoItem.setUndoType(UndoConstants.TYPING);
					}
					formulaBar.setText(mtcl.getNewValue());
					setUndoNewCollections(undoItem);
					setUpMetabolitesUndo(undoItem);
				} else {
					formulaBar.setText(mtcl.getOldValue());
				}
			}			
		}
	};

	/**
	 * updates reactions table with new value is valid, else reverts to old value
	 * @param oldValue
	 * @param newValue
	 * @param rowIndex
	 * @param colIndex
	 */
	public void updateReactionsCellIfValid(String oldValue, String newValue, int rowIndex, int colIndex) {		
		reactionUpdateValid = true;
		EntryValidator validator = new EntryValidator();
		Utilities u = new Utilities();
		//LocalConfig.getInstance().editMode = true;
		int id = Integer.parseInt((String) (reactionsTable.getModel().getValueAt(rowIndex, 0)));		
		if (colIndex == GraphicalInterfaceConstants.REACTION_EQUN_ABBR_COLUMN) {
			//SBMLReactionEquation oldEquation = (SBMLReactionEquation) LocalConfig.getInstance().getReactionEquationMap().get(id);
			ReactionParser parser = new ReactionParser();
			if (!parser.isValid(newValue.trim())) {
				if (newValue.trim().length() > 0) {
					setFindReplaceAlwaysOnTop(false);
					JOptionPane.showMessageDialog(null,                
							GraphicalInterfaceConstants.INVALID_REACTIONS_ENTRY_ERROR_MESSAGE, 
							GraphicalInterfaceConstants.INVALID_REACTIONS_ENTRY_ERROR_TITLE,                               
							JOptionPane.ERROR_MESSAGE);
					setFindReplaceAlwaysOnTop(true);
					LocalConfig.getInstance().getInvalidReactions().add(newValue);
					reactionsTable.getModel().setValueAt(newValue, rowIndex, GraphicalInterfaceConstants.REACTION_EQUN_ABBR_COLUMN);
					reactionUpdateValid = false;
				} else {
					updateReactionEquation(rowIndex, id, oldValue, newValue);
					LocalConfig.getInstance().getReactionEquationMap().remove(id);
				}
			} else {
				//  if reaction is changed unhighlight unused metabolites since
				//  used status may change, same with participating reactions
				highlightUnusedMetabolites = false;
				highlightUnusedMetabolitesItem.setState(false);
				parser.reactionList(newValue.trim());
				// if reaction is reversible, no need to check lower bound
				if (newValue.contains("<") || (newValue.contains("=") && !newValue.contains(">"))) {										
					updateReactionEquation(rowIndex, id, oldValue, newValue);
					// check if lower bound is >= 0 if reversible = false
				} else if (newValue.contains("-->") || newValue.contains("->") || newValue.contains("=>")) {
					parser.reactionList(newValue.trim());
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
								reactionsTable.getModel().setValueAt(GraphicalInterfaceConstants.BOOLEAN_VALUES[0], rowIndex, GraphicalInterfaceConstants.REVERSIBLE_COLUMN);
								updateReactionEquation(rowIndex, id, oldValue, newValue);
								//updateReactionEquation(newValue, id, rowIndex, oldEquation, parser.getEquation());
							}
							// set old equation
							if (choice == JOptionPane.NO_OPTION) {
								reactionsTable.getModel().setValueAt(oldValue, rowIndex, GraphicalInterfaceConstants.REACTION_EQUN_ABBR_COLUMN);
								reactionUpdateValid = false;
							}
							setFindReplaceAlwaysOnTop(true);
							// if in replace all mode, just set lower bound to 0 and set new equation
						} else {
							reactionsTable.getModel().setValueAt("0.0", rowIndex, GraphicalInterfaceConstants.LOWER_BOUND_COLUMN);
							reactionsTable.getModel().setValueAt(GraphicalInterfaceConstants.BOOLEAN_VALUES[0], rowIndex, GraphicalInterfaceConstants.REVERSIBLE_COLUMN);
							updateReactionEquation(rowIndex, id, oldValue, newValue);
						}
					} else {
						// lower bound >= 0, set new equation
						updateReactionEquation(rowIndex, id, oldValue, newValue);
					}
				} 
				maybeDisplaySuspiciousMetabMessage(statusBarRow());					
			}
		// this column is not editable, but code below is still necessary for replace
		} else if (colIndex == GraphicalInterfaceConstants.REACTION_EQUN_NAMES_COLUMN) {
			if (!replaceAllMode) {
				setFindReplaceAlwaysOnTop(false);
				JOptionPane.showMessageDialog(null,                
						GraphicalInterfaceConstants.EQUATION_NAMES_ERROR_MESSAGE, 
						GraphicalInterfaceConstants.EQUATION_NAMES_ERROR_TITLE,                               
						JOptionPane.ERROR_MESSAGE);
				formulaBar.setText(getTableCellOldValue());
				setFindReplaceAlwaysOnTop(true);
			}				
			reactionUpdateValid = false;
			reactionsTable.getModel().setValueAt(oldValue, rowIndex, GraphicalInterfaceConstants.REACTION_EQUN_NAMES_COLUMN);
		} else if (colIndex == GraphicalInterfaceConstants.KO_COLUMN) {
			// if replace 't' or 'f' not allowed, else for entry autofill occurs
			maybeUpdateBooleanValue(oldValue, newValue, rowIndex, GraphicalInterfaceConstants.KO_COLUMN);		
		} else if (colIndex == GraphicalInterfaceConstants.REVERSIBLE_COLUMN) {
			// if replace 't' or 'f' not allowed, else for entry autofill occurs
			maybeUpdateBooleanValue(oldValue, newValue, rowIndex, GraphicalInterfaceConstants.REVERSIBLE_COLUMN);
			if (reactionUpdateValid) {
				if (newValue.toLowerCase().startsWith(GraphicalInterfaceConstants.VALID_FALSE_VALUES[0])) {
					reactionsTable.getModel().setValueAt("0.0", rowIndex, GraphicalInterfaceConstants.LOWER_BOUND_COLUMN);
				}
				if (LocalConfig.getInstance().getReactionEquationMap().get(id) != null) {
					// rewrite equations and update map
					SBMLReactionEquation equn = ((SBMLReactionEquation) LocalConfig.getInstance().getReactionEquationMap().get(id));
					if (newValue.toLowerCase().startsWith(GraphicalInterfaceConstants.VALID_TRUE_VALUES[0])) {
						((SBMLReactionEquation) LocalConfig.getInstance().getReactionEquationMap().get(id)).setReversible(GraphicalInterfaceConstants.BOOLEAN_VALUES[1]);
					} else if (newValue.toLowerCase().startsWith(GraphicalInterfaceConstants.VALID_FALSE_VALUES[0])) {
						((SBMLReactionEquation) LocalConfig.getInstance().getReactionEquationMap().get(id)).setReversible(GraphicalInterfaceConstants.BOOLEAN_VALUES[0]);
					}
					((SBMLReactionEquation) LocalConfig.getInstance().getReactionEquationMap().get(id)).writeReactionEquation();
					reactionsTable.getModel().setValueAt(equn.equationAbbreviations, rowIndex, GraphicalInterfaceConstants.REACTION_EQUN_ABBR_COLUMN);
					reactionsTable.getModel().setValueAt(equn.equationNames, rowIndex, GraphicalInterfaceConstants.REACTION_EQUN_NAMES_COLUMN);
				}				
			}
		} else if (colIndex == GraphicalInterfaceConstants.REACTION_ABBREVIATION_COLUMN) {
			if (LocalConfig.getInstance().getReactionAbbreviationIdMap().containsKey(newValue)) {
				setFindReplaceAlwaysOnTop(false);
				Object[] options = {"    Yes    ", "    No    ",};
				int choice = JOptionPane.showOptionDialog(null, 
						GraphicalInterfaceConstants.DUPLICATE_REACTION_MESSAGE + newValue + u.duplicateSuffix(newValue, LocalConfig.getInstance().getReactionAbbreviationIdMap()) + " ?", 
						GraphicalInterfaceConstants.DUPLICATE_REACTION_TITLE, 
						JOptionPane.YES_NO_OPTION, 
						JOptionPane.QUESTION_MESSAGE, 
						null, options, options[0]);
				if (choice == JOptionPane.YES_OPTION) {
					newValue = newValue + u.duplicateSuffix(newValue, LocalConfig.getInstance().getReactionAbbreviationIdMap());
					reactionsTable.getModel().setValueAt(newValue, rowIndex, GraphicalInterfaceConstants.REACTION_ABBREVIATION_COLUMN);
					LocalConfig.getInstance().getReactionAbbreviationIdMap().put(newValue, id);
					LocalConfig.getInstance().getReactionAbbreviationIdMap().remove(oldValue);
				}
				if (choice == JOptionPane.NO_OPTION) {
					reactionUpdateValid = false;
					reactionsTable.getModel().setValueAt(oldValue, rowIndex, GraphicalInterfaceConstants.REACTION_ABBREVIATION_COLUMN);
				}			
				setFindReplaceAlwaysOnTop(true);
			} else {
				//if a blank is entered remove key/value of old value
				if (newValue == null || newValue.length() == 0 || newValue.trim().equals("")) {
					LocalConfig.getInstance().getReactionAbbreviationIdMap().remove(oldValue);
					// non-duplicate entry
				} else {
					if (newValue.trim() != null && newValue.trim().length() > 0) {
						LocalConfig.getInstance().getReactionAbbreviationIdMap().remove(oldValue);
						LocalConfig.getInstance().getReactionAbbreviationIdMap().put(newValue, Integer.parseInt((String) (reactionsTable.getModel().getValueAt(rowIndex, 0))));
					}					
				}
				reactionsTable.getModel().setValueAt(newValue, rowIndex, GraphicalInterfaceConstants.REACTION_ABBREVIATION_COLUMN);
			}
			LocalConfig.getInstance().getReactionAbbreviationIdMap().remove(oldValue);
			// update id name map
			if (!LocalConfig.getInstance().getReactionAbbreviationIdMap().containsKey((String) reactionsTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.REACTION_ABBREVIATION_COLUMN))) {
				if (reactionsTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.REACTION_ABBREVIATION_COLUMN) != null 
						&& ((String) reactionsTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.REACTION_ABBREVIATION_COLUMN)).trim().length() > 0) {
					LocalConfig.getInstance().getReactionAbbreviationIdMap().put((String) reactionsTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.REACTION_ABBREVIATION_COLUMN), new Integer(id));
				}				
			}
		} else if (colIndex == GraphicalInterfaceConstants.FLUX_VALUE_COLUMN || 
				colIndex == GraphicalInterfaceConstants.MIN_FLUX_COLUMN ||
				colIndex == GraphicalInterfaceConstants.MAX_FLUX_COLUMN ||
				colIndex == GraphicalInterfaceConstants.LOWER_BOUND_COLUMN || 
				colIndex == GraphicalInterfaceConstants.UPPER_BOUND_COLUMN || 
				colIndex == GraphicalInterfaceConstants.BIOLOGICAL_OBJECTIVE_COLUMN || 
				colIndex == GraphicalInterfaceConstants.SYNTHETIC_OBJECTIVE_COLUMN) {
			if (validator.isInvalidInfinityEntry(newValue)) {
				newValue = GraphicalInterfaceConstants.VALID_INFINITY_ENTRY;
			} else if (validator.isInvalidNegativeInfinityEntry(newValue)) {
				newValue = "-" + GraphicalInterfaceConstants.VALID_INFINITY_ENTRY;
			}
			if (!validator.isNumber(newValue)) {
				if (!replaceAllMode) {
					setFindReplaceAlwaysOnTop(false);
					JOptionPane.showMessageDialog(null,                
							GraphicalInterfaceConstants.NUMERIC_VALUE_ERROR_TITLE,                
							GraphicalInterfaceConstants.NUMERIC_VALUE_ERROR_MESSAGE,                               
							JOptionPane.ERROR_MESSAGE);
					formulaBar.setText(getTableCellOldValue());
					setFindReplaceAlwaysOnTop(true);
				}	
				reactionUpdateValid = false;
				reactionsTable.getModel().setValueAt(oldValue, rowIndex, colIndex);				
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
						colIndex == GraphicalInterfaceConstants.MIN_FLUX_COLUMN ||
						colIndex == GraphicalInterfaceConstants.MAX_FLUX_COLUMN ||
						colIndex == GraphicalInterfaceConstants.BIOLOGICAL_OBJECTIVE_COLUMN ||
						colIndex == GraphicalInterfaceConstants.SYNTHETIC_OBJECTIVE_COLUMN) {
					reactionsTable.getModel().setValueAt(newValue, rowIndex, colIndex);
				}				
			}
		} else {
			// action for remaining columns
			reactionsTable.getModel().setValueAt(newValue, rowIndex, colIndex);
		}
	}
	
	// if valid boolean value, update cell
	public void maybeUpdateBooleanValue(String oldValue, String newValue, int rowIndex, int colIndex) {
		// if replace 't' or 'f' not allowed, else for entry autofill occurs
		EntryValidator validator = new EntryValidator();
		if (reactionsReplace) {
			if (!validator.validBooleanValue(newValue)) {
				showBooleanErrorMessage();
				reactionUpdateValid = false;
				reactionsTable.getModel().setValueAt(oldValue, rowIndex, colIndex);					
			} else {
				reactionsTable.getModel().setValueAt(newValue, rowIndex, colIndex);					
			}
		} else {
			if (validator.validTrueEntry(newValue)) {
				reactionsTable.getModel().setValueAt(GraphicalInterfaceConstants.BOOLEAN_VALUES[1], rowIndex, colIndex);
			} else if (validator.validFalseEntry(newValue)) {
				reactionsTable.getModel().setValueAt(GraphicalInterfaceConstants.BOOLEAN_VALUES[0], rowIndex, colIndex);
			} else if (newValue != null) {				
				showBooleanErrorMessage();			
				reactionUpdateValid = false;
				reactionsTable.getModel().setValueAt(oldValue, rowIndex, GraphicalInterfaceConstants.KO_COLUMN);
			}
		}			
	}
	
	public void showBooleanErrorMessage() {
		if (!replaceAllMode) {
			setFindReplaceAlwaysOnTop(false);
			JOptionPane.showMessageDialog(null,                
					GraphicalInterfaceConstants.BOOLEAN_VALUE_ERROR_MESSAGE, 
					GraphicalInterfaceConstants.BOOLEAN_VALUE_ERROR_TITLE,                               
					JOptionPane.ERROR_MESSAGE);
			formulaBar.setText(getTableCellOldValue());
			setFindReplaceAlwaysOnTop(true);
		}	
	}
	
	public void updateReactionEquation(int rowIndex, int reactionId, String oldEquation, String newEquation) {
		LocalConfig.getInstance().getAddedMetabolites().clear();
		SBMLReactionEquation equation = new SBMLReactionEquation();	
		ReactionEquationUpdater updater = new ReactionEquationUpdater();
		updater.createLists(oldEquation, newEquation);
		if (!reactionsUndo) {
			updater.removeOldItems(updater.getRemoveReactantsList(), updater.getRemoveProductsList());
		}		
		if (newEquation != null && newEquation.trim().length() > 0) {
			new ArrayList<SBMLReactant>();
			new ArrayList<SBMLProduct>();
			ReactionParser parser = new ReactionParser();
			parser.reactionList(newEquation.trim());
			SBMLReactionEquation unprocessedEqun = ReactionParser.getEquation();
			for (int i = 0; i < updater.getMaybeAddReactants().size(); i++) {
				maybeAddMetabolite(updater.getMaybeAddReactants().get(i));
				if (addMetabolite || LocalConfig.getInstance().getMetaboliteAbbreviationIdMap().containsKey(updater.getMaybeAddReactants().get(i))) {
					Integer metabId = (Integer) LocalConfig.getInstance().getMetaboliteAbbreviationIdMap().get(updater.getMaybeAddReactants().get(i));				
					for (int j = 0; j < unprocessedEqun.getReactants().size(); j++) {
						if (unprocessedEqun.getReactants().get(j).getMetaboliteAbbreviation().equals(updater.getMaybeAddReactants().get(i))) {
							unprocessedEqun.getReactants().get(j).setMetaboliteId(metabId);
							unprocessedEqun.getReactants().get(j).setReactionId(reactionId);
							if (LocalConfig.getInstance().getMetaboliteIdNameMap().containsKey(unprocessedEqun.getReactants().get(j).getMetaboliteId())) {
								unprocessedEqun.getReactants().get(j).setMetaboliteName(LocalConfig.getInstance().getMetaboliteIdNameMap().get(metabId));
								unprocessedEqun.getReactants().get(j).setCompartment(LocalConfig.getInstance().getMetaboliteIdCompartmentMap().get(metabId));
							}
						}
					}
					if (parser.isSuspicious(updater.getMaybeAddReactants().get(i))) {
						if (!LocalConfig.getInstance().getSuspiciousMetabolites().contains(metabId)) {
							LocalConfig.getInstance().getSuspiciousMetabolites().add(metabId);
						}							
					}
					updater.updateMetaboliteUsedMap(updater.getMaybeAddReactants().get(i), "new");
				} else {
					unprocessedEqun.removeReactantByAbbr(updater.getMaybeAddReactants().get(i));
				}
			}
			for (int i = 0; i < updater.getMaybeAddProducts().size(); i++) {
				maybeAddMetabolite(updater.getMaybeAddProducts().get(i));
				if (addMetabolite || LocalConfig.getInstance().getMetaboliteAbbreviationIdMap().containsKey(updater.getMaybeAddProducts().get(i))) {
					Integer metabId = (Integer) LocalConfig.getInstance().getMetaboliteAbbreviationIdMap().get(updater.getMaybeAddProducts().get(i));				
					for (int j = 0; j < unprocessedEqun.getProducts().size(); j++) {
						if (unprocessedEqun.getProducts().get(j).getMetaboliteAbbreviation().equals(updater.getMaybeAddProducts().get(i))) {
							unprocessedEqun.getProducts().get(j).setMetaboliteId(metabId);
							unprocessedEqun.getProducts().get(j).setReactionId(reactionId);
							if (LocalConfig.getInstance().getMetaboliteIdNameMap().containsKey(unprocessedEqun.getProducts().get(j).getMetaboliteId())) {
								unprocessedEqun.getProducts().get(j).setMetaboliteName(LocalConfig.getInstance().getMetaboliteIdNameMap().get(metabId));
								unprocessedEqun.getProducts().get(j).setCompartment(LocalConfig.getInstance().getMetaboliteIdCompartmentMap().get(metabId));
							}
						}
					}
					if (parser.isSuspicious(updater.getMaybeAddProducts().get(i))) {
						if (!LocalConfig.getInstance().getSuspiciousMetabolites().contains(metabId)) {
							LocalConfig.getInstance().getSuspiciousMetabolites().add(metabId);
						}							
					}
					updater.updateMetaboliteUsedMap(updater.getMaybeAddProducts().get(i), "new");
				} else {
					unprocessedEqun.removeProductByAbbr(updater.getMaybeAddProducts().get(i));
				}
			}
			// copy SBMLReactionEquation - if this is not done, all of the SBMLReactionEquation
			// objects will be the same due to the reactant and product lists all being copies
			// of the same lists
			ArrayList<SBMLReactant> reac = new ArrayList<SBMLReactant>();
			ArrayList<SBMLProduct> prod = new ArrayList<SBMLProduct>();
			for (int i = 0; i < unprocessedEqun.getReactants().size(); i++) {
				unprocessedEqun.getReactants().get(i).setReactionId(reactionId);
				// for some reason the ids are null for metabolites not added - reset here
				String abbrev = unprocessedEqun.getReactants().get(i).getMetaboliteAbbreviation();
				unprocessedEqun.getReactants().get(i).setMetaboliteId((Integer) LocalConfig.getInstance().getMetaboliteAbbreviationIdMap().get(abbrev));
				reac.add(unprocessedEqun.getReactants().get(i));
			}
			for (int i = 0; i < unprocessedEqun.getProducts().size(); i++) {
				unprocessedEqun.getProducts().get(i).setReactionId(reactionId);
				String abbrev = unprocessedEqun.getProducts().get(i).getMetaboliteAbbreviation();
				unprocessedEqun.getProducts().get(i).setMetaboliteId((Integer) LocalConfig.getInstance().getMetaboliteAbbreviationIdMap().get(abbrev));
				prod.add(unprocessedEqun.getProducts().get(i));
			}
			equation.setReactants(reac);
			equation.setProducts(prod);
			equation.setReversible(unprocessedEqun.getReversible());
			equation.setReversibleArrow(unprocessedEqun.getReversibleArrow());
			equation.setIrreversibleArrow(unprocessedEqun.getIrreversibleArrow());
			equation.writeReactionEquation();
			LocalConfig.getInstance().getReactionEquationMap().put(reactionId, equation);
			if (LocalConfig.getInstance().noButtonClicked) {			
				reactionsTable.getModel().setValueAt(equation.equationAbbreviations, rowIndex, GraphicalInterfaceConstants.REACTION_EQUN_ABBR_COLUMN);
			}
			reactionsTable.getModel().setValueAt(equation.equationNames, rowIndex, GraphicalInterfaceConstants.REACTION_EQUN_NAMES_COLUMN);
			reactionsTable.getModel().setValueAt(equation.getReversible(), rowIndex, GraphicalInterfaceConstants.REVERSIBLE_COLUMN);
			if (equation.getReversible().equals(GraphicalInterfaceConstants.BOOLEAN_VALUES[0])) {
				if (Double.valueOf((String) reactionsTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.LOWER_BOUND_COLUMN)) < 0) {
					reactionsTable.getModel().setValueAt("0.0", rowIndex, GraphicalInterfaceConstants.LOWER_BOUND_COLUMN);
				}				
			}
			GraphicalInterface.showPrompt = true;
			createUnusedMetabolitesList();
			if (getParticipatingMetabolite() != null && getParticipatingMetabolite().trim().length() > 0) {
				MetaboliteFactory aFactory = new MetaboliteFactory("SBML");
				LocalConfig.getInstance().setParticipatingReactions(aFactory.participatingReactions(getParticipatingMetabolite()));
			}
		} else {
			reactionsTable.getModel().setValueAt("", rowIndex, GraphicalInterfaceConstants.REACTION_EQUN_NAMES_COLUMN);
			LocalConfig.getInstance().getReactionEquationMap().remove(reactionId);
		}
//		System.out.println("upd equn " + LocalConfig.getInstance().getReactionEquationMap());
//		System.out.println("upd equn id " + LocalConfig.getInstance().getMetaboliteAbbreviationIdMap());
//		System.out.println("upd equn used " + LocalConfig.getInstance().getMetaboliteUsedMap());	
	}
	
	public void maybeAddMetabolite(String species) {
		if (species != null && species.length() > 0) {
			addMetabolite = true;		
			int maxMetab = LocalConfig.getInstance().getMaxMetabolite();
			int maxMetabId = LocalConfig.getInstance().getMaxMetaboliteId();
			if (!(LocalConfig.getInstance().getMetaboliteAbbreviationIdMap().containsKey(species.trim()))) {
				if (showPrompt && !(GraphicalInterface.replaceAllMode && LocalConfig.getInstance().yesToAllButtonClicked)) {
					Object[] options = {"Yes",
							"Yes to All",
					"No"};
					LocalConfig.getInstance().addReactantPromptShown = true;
					int choice = JOptionPane.showOptionDialog(null, 
							"The metabolite " + species + " does not exist. Do you wish to add it?", 
							"Add Metabolite?", 
							JOptionPane.YES_NO_CANCEL_OPTION, 
							JOptionPane.QUESTION_MESSAGE, 
							null, options, options[0]);
					//options[0] sets "Yes" as default button

					// interpret the user's choice	  
					if (choice == JOptionPane.YES_OPTION)
					{
						LocalConfig.getInstance().addMetaboliteOption = true;
						addNewMetabolite(maxMetab, maxMetabId, species);
						maxMetab += 1;
						LocalConfig.getInstance().setMaxMetabolite(maxMetab);
						maxMetabId += 1;
						LocalConfig.getInstance().setMaxMetaboliteId(maxMetabId);
					}
					//No option actually corresponds to "Yes to All" button
					if (choice == JOptionPane.NO_OPTION)
					{
						LocalConfig.getInstance().addMetaboliteOption = true;
						GraphicalInterface.showPrompt = false;
						addNewMetabolite(maxMetab, maxMetabId, species);
						maxMetab += 1;
						LocalConfig.getInstance().setMaxMetabolite(maxMetab);
						maxMetabId += 1;
						LocalConfig.getInstance().setMaxMetaboliteId(maxMetabId);
						LocalConfig.getInstance().yesToAllButtonClicked = true;
					}
					//Cancel option actually corresponds to "No" button
					if (choice == JOptionPane.CANCEL_OPTION) {
						addMetabolite = false;
						LocalConfig.getInstance().addMetaboliteOption = false;
						LocalConfig.getInstance().noButtonClicked = true; 
					}	
				} else {
					addNewMetabolite(maxMetab, maxMetabId, species);
					maxMetab += 1;
					LocalConfig.getInstance().setMaxMetabolite(maxMetab);
					maxMetabId += 1;
					LocalConfig.getInstance().setMaxMetaboliteId(maxMetabId);
				}
			}
		}		
	}
	
	public void addNewMetabolite(int maxMetab, int maxMetabId, String species) {
		DefaultTableModel model = (DefaultTableModel) metabolitesTable.getModel();		
		if (maxMetab < LocalConfig.getInstance().getMaxMetaboliteId()) {		
			if (reactionsTable.getRowCount() > LocalConfig.getInstance().getMetaboliteAbbreviationIdMap().size()) {
				LocalConfig.getInstance().getMetaboliteAbbreviationIdMap().put(species, maxMetab);
				try {
					model.setValueAt(species, maxMetab, GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN);
					LocalConfig.getInstance().getAddedMetabolites().add((maxMetab));
				} catch (Throwable t) {
					model.addRow(createMetabolitesRow(maxMetabId));
					LocalConfig.getInstance().getMetaboliteAbbreviationIdMap().put(species, maxMetabId);
					model.setValueAt(species, maxMetab, GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN);
					LocalConfig.getInstance().getAddedMetabolites().add((maxMetabId));
				}				
			} else {
				model.addRow(createMetabolitesRow(maxMetabId));
				LocalConfig.getInstance().getMetaboliteAbbreviationIdMap().put(species, maxMetabId);
				model.setValueAt(species, maxMetab, GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN);
				LocalConfig.getInstance().getAddedMetabolites().add((maxMetabId));
			}			
		} else {
			LocalConfig.getInstance().getMetaboliteAbbreviationIdMap().put(species, maxMetabId);
			model.addRow(createMetabolitesRow(maxMetabId));
			model.setValueAt(species, maxMetabId, GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN);
			LocalConfig.getInstance().getAddedMetabolites().add((maxMetabId));
		}		
		setUpMetabolitesTable(model);
	}

	/**
	 * updates metabolites table with new value if valid, else reverts to old value
	 * @param oldValue
	 * @param newValue
	 * @param rowIndex
	 * @param colIndex
	 */
	public void updateMetabolitesCellIfValid(String oldValue, String newValue, int rowIndex, int colIndex) {
		metaboliteUpdateValid = true;
		EntryValidator validator = new EntryValidator();
		Utilities u = new Utilities();
		int id = Integer.parseInt((String) (metabolitesTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.METABOLITE_ID_COLUMN)));
		String metabAbbrev = (String) (metabolitesTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN));
		String metabName = (String) (metabolitesTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.METABOLITE_NAME_COLUMN));
		if (colIndex == GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN) {
			createUnusedMetabolitesList();
			// enables or disables unused metabolites menu items depending on if there are unused items present						
			if (LocalConfig.getInstance().getUnusedList().size() > 0) {
				highlightUnusedMetabolitesItem.setEnabled(true);
				deleteUnusedItem.setEnabled(true);
			} else {
				highlightUnusedMetabolitesItem.setEnabled(false);
				deleteUnusedItem.setEnabled(false);
			}
			if (oldValue == null || oldValue.trim().length() == 0 && 
					newValue != null || newValue.trim().length() > 0) {
				if (id >= LocalConfig.getInstance().getMaxMetabolite()) {
					int max = id + 1;
					LocalConfig.getInstance().setMaxMetabolite(max);
				}				
			}
			if (LocalConfig.getInstance().getMetaboliteUsedMap().containsKey(oldValue) 
					&& !LocalConfig.getInstance().getMetaboliteAbbreviationIdMap().containsKey(newValue)) {
				if (newValue != null && newValue.toString().trim().length() > 0) {					
					if (!newValue.equals(oldValue)) {
						showMetaboliteRenameInterface = false;
						showRenameMessage(oldValue);
						if (renameMetabolite) {					
							rewriteReactions(id, oldValue, metabName, newValue, colIndex);
							updateMetaboliteMaps(id, oldValue, metabName, newValue, colIndex);
						} else {
							metaboliteUpdateValid = false;
							metabolitesTable.getModel().setValueAt(oldValue, rowIndex, GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN);
						}
					}					
				} else {
					setFindReplaceAlwaysOnTop(false);
					JOptionPane.showMessageDialog(null,                
							GraphicalInterfaceConstants.METABOLITE_RENAME_ERROR_MESSAGE,                
							GraphicalInterfaceConstants.METABOLITE_RENAME_ERROR_TITLE,                                
							JOptionPane.ERROR_MESSAGE);
					formulaBar.setText(getTableCellOldValue());
					setFindReplaceAlwaysOnTop(true);
					metaboliteUpdateValid = false;
					metabolitesTable.getModel().setValueAt(oldValue, rowIndex, GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN);
				}
				// entry is duplicate
			} else if (LocalConfig.getInstance().getMetaboliteAbbreviationIdMap().containsKey(newValue)) {
				setFindReplaceAlwaysOnTop(false);
				Object[] options = {"    Yes    ", "    No    ",};
				int choice = JOptionPane.showOptionDialog(null, 
						GraphicalInterfaceConstants.DUPLICATE_METABOLITE_MESSAGE + newValue + u.duplicateSuffix(newValue, LocalConfig.getInstance().getMetaboliteAbbreviationIdMap()) + " ?", 
						GraphicalInterfaceConstants.DUPLICATE_METABOLITE_TITLE, 
						JOptionPane.YES_NO_OPTION, 
						JOptionPane.QUESTION_MESSAGE, 
						null, options, options[0]);
				if (choice == JOptionPane.YES_OPTION) {
					newValue = newValue + u.duplicateSuffix(newValue, LocalConfig.getInstance().getMetaboliteAbbreviationIdMap());
					metabolitesTable.getModel().setValueAt(newValue, rowIndex, GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN);
					LocalConfig.getInstance().getMetaboliteAbbreviationIdMap().put(newValue, id);
					LocalConfig.getInstance().getMetaboliteAbbreviationIdMap().remove(oldValue);
				}
				if (choice == JOptionPane.NO_OPTION) {
					metaboliteUpdateValid = false;
					metabolitesTable.getModel().setValueAt(oldValue, rowIndex, GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN);
				}			
				setFindReplaceAlwaysOnTop(true);
			} else {
				//if a blank is entered remove key/value of old value
				if (newValue == null || newValue.length() == 0 || newValue.trim().equals("")) {
					LocalConfig.getInstance().getMetaboliteAbbreviationIdMap().remove(oldValue);
					// non-duplicate entry
				} else {
					if (newValue.trim() != null && newValue.trim().length() > 0) {
						LocalConfig.getInstance().getMetaboliteAbbreviationIdMap().remove(oldValue);
						LocalConfig.getInstance().getMetaboliteAbbreviationIdMap().put(newValue, Integer.parseInt((String) (metabolitesTable.getModel().getValueAt(rowIndex, 0))));
					}					
				}
				metabolitesTable.getModel().setValueAt(newValue, rowIndex, GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN);
			}
			LocalConfig.getInstance().getMetaboliteAbbreviationIdMap().remove(oldValue);
			// update id name map
			if (!LocalConfig.getInstance().getMetaboliteAbbreviationIdMap().containsKey((String) metabolitesTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN))) {
				if (metabolitesTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN) != null 
						&& ((String) metabolitesTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN)).trim().length() > 0) {
					LocalConfig.getInstance().getMetaboliteAbbreviationIdMap().put((String) metabolitesTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN), new Integer(id));
				}				
			}
			updateMetaboliteCollections(rowIndex, Integer.parseInt((String) (metabolitesTable.getModel().getValueAt(rowIndex, 0)))); 
		} else if (colIndex == GraphicalInterfaceConstants.METABOLITE_NAME_COLUMN) {
			if (LocalConfig.getInstance().getMetaboliteUsedMap().containsKey(metabAbbrev)) {
				if (!newValue.equals(oldValue)) {
					showMetaboliteRenameInterface = false;
					showRenameMessage(oldValue);
					if (renameMetabolite) {	
						rewriteReactions(id, metabAbbrev, oldValue, newValue, colIndex);
						updateMetaboliteMaps(id, oldValue, metabName, newValue, colIndex);
					} else {
						metaboliteUpdateValid = false;
						metabolitesTable.getModel().setValueAt(oldValue, rowIndex, GraphicalInterfaceConstants.METABOLITE_NAME_COLUMN);
					}
				}		
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
					formulaBar.setText(getTableCellOldValue());
					setFindReplaceAlwaysOnTop(true);
				}				
				metaboliteUpdateValid = false;
				metabolitesTable.getModel().setValueAt(oldValue, rowIndex, colIndex);
			} else {
				metabolitesTable.getModel().setValueAt(newValue, rowIndex, colIndex);
			}
		} else if (colIndex == GraphicalInterfaceConstants.BOUNDARY_COLUMN) {
			if (metabolitesReplace) {
				if (!validator.validBooleanValue(newValue)) {
					showBooleanErrorMessage();
					metaboliteUpdateValid = false;
					metabolitesTable.getModel().setValueAt(oldValue, rowIndex, colIndex);					
				} else {
					metabolitesTable.getModel().setValueAt(newValue, rowIndex, colIndex);					
				}
			} else {
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
						formulaBar.setText(getTableCellOldValue());
						setFindReplaceAlwaysOnTop(true);
					}	
					metaboliteUpdateValid = false;
					metabolitesTable.getModel().setValueAt(oldValue, rowIndex, GraphicalInterfaceConstants.BOUNDARY_COLUMN);
				}
			}
		} else if (colIndex == GraphicalInterfaceConstants.COMPARTMENT_COLUMN) {
			rewriteReactionEquationNames(id, metabAbbrev, newValue);
			LocalConfig.getInstance().getMetaboliteIdCompartmentMap().put(new Integer(id), newValue); 
			//System.out.println(LocalConfig.getInstance().getMetaboliteIdCompartmentMap());
		} else {
			// action for remaining columns
			metabolitesTable.getModel().setValueAt(newValue, rowIndex, colIndex);
		}
		showMetaboliteRenameInterface = true;
	}

	public void updateMetaboliteCollections(int rowIndex, int metaboliteId) {
		// prevents invisible id column from setting id in formulaBar for find events
		if (metabolitesTable.getSelectedRow() > 0) {
			int viewRow = metabolitesTable.convertRowIndexToView(metabolitesTable.getSelectedRow());
			formulaBar.setText((String) metabolitesTable.getModel().getValueAt(viewRow, metabolitesTable.getSelectedColumn()));
		}
		if (!LocalConfig.getInstance().getMetaboliteAbbreviationIdMap().containsKey((String) metabolitesTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN))) {
			if (metabolitesTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN) != null 
					&& ((String) metabolitesTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN)).trim().length() > 0) {
				LocalConfig.getInstance().getMetaboliteAbbreviationIdMap().put((String) metabolitesTable.getModel().getValueAt(rowIndex, GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN), new Integer(metaboliteId));
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
	}

	public static void updateReactionsCellById(String value, int id, int col) {
		Map<String, Object> reactionsIdRowMap = new HashMap<String, Object>();
		for (int i = 0; i < reactionsTable.getRowCount(); i++) {
			reactionsIdRowMap.put((String) reactionsTable.getModel().getValueAt(i, GraphicalInterfaceConstants.REACTIONS_ID_COLUMN), i);
		}
		String row = (reactionsIdRowMap.get(Integer.toString(id))).toString();
		int rowNum = Integer.valueOf(row);
		reactionsTable.getModel().setValueAt(value, rowNum, col);
	}
	
	public static void updateMetabolitesCellById(String value, int id, int col) {
		Map<String, Object> metabolitesIdRowMap = new HashMap<String, Object>();
		for (int i = 0; i < metabolitesTable.getRowCount(); i++) {
			metabolitesIdRowMap.put((String) metabolitesTable.getModel().getValueAt(i, GraphicalInterfaceConstants.REACTIONS_ID_COLUMN), i);
		}
		String row = (metabolitesIdRowMap.get(Integer.toString(id))).toString();
		int rowNum = Integer.valueOf(row);
		metabolitesTable.getModel().setValueAt(value, rowNum, col);
	}

	public void deleteMetabolitesRowById(int id) {
		Map<String, Object> metabolitesIdRowMap = new HashMap<String, Object>();
		for (int i = 0; i < metabolitesTable.getRowCount(); i++) {
			metabolitesIdRowMap.put((String) metabolitesTable.getModel().getValueAt(i, GraphicalInterfaceConstants.REACTIONS_ID_COLUMN), i);
		}
		try {
			String row = (metabolitesIdRowMap.get(Integer.toString(id))).toString();
			int rowNum = Integer.valueOf(row);
			DefaultTableModel model = (DefaultTableModel) metabolitesTable.getModel();
			model.removeRow(rowNum);
		} catch (Throwable t) {
			
		}		
	}

	/*****************************************************************************/
	//end table actions
	/*****************************************************************************/

	/******************************************************************************/
	//reload tables methods
	/******************************************************************************/

	/**
	 * This method sets parameters to initial values before load of model
	 */
	public void loadSetUp() {
		if (getFindReplaceDialog() != null) {
			getFindReplaceDialog().dispose();
		}
		// need to make sure the two lines below are not necessary before removing
//		findReplaceItem.setEnabled(true);
//		findbutton.setEnabled(true);
		clearOutputPane();
		if (getPopout() != null) {
			popout.dispose();
		}		
		setBooleanDefaults();
		clearConfigLists();	
		undoSplitButton.setToolTipText("Can't Undo (Ctrl+Z)");
		redoSplitButton.setToolTipText("Can't Redo (Ctrl+Y)");
		disableOptionComponent(undoSplitButton, undoLabel, undoGrayedLabel);
		disableOptionComponent(redoSplitButton, undoLabel, undoGrayedLabel);
		undoCount = 1;
		redoCount = 1;
		LocalConfig.getInstance().setNumReactionTablesCopied(0);
		LocalConfig.getInstance().setNumMetabolitesTableCopied(0);
		showPrompt = true;
		highlightUnusedMetabolites = false;
		highlightUnusedMetabolitesItem.setState(false);
		setReactionsSortColumnIndex(0);
		setMetabolitesSortColumnIndex(0);
		LocalConfig.getInstance().setReactionsLocationsListCount(0);
		LocalConfig.getInstance().setMetabolitesLocationsListCount(0);		
		// default selection mode cells only
		setUpCellSelectionMode();
		unsortReacMenuItem.setEnabled(false);
		unsortMetabMenuItem.setEnabled(false);
	}
	
	/**
	 * This method is called whenever the metabolites table is reloaded
	 * @param model
	 */
	public void setUpMetabolitesTable(DefaultTableModel model) {
		metabolitesTable.setModel(model);
		setMetabolitesTableLayout();

		// enables or disables menu items depending on if there are unused items present
		createUnusedMetabolitesList();
		if (LocalConfig.getInstance().getUnusedList().size() > 0) {
			highlightUnusedMetabolitesItem.setEnabled(true);
			deleteUnusedItem.setEnabled(true);
		} else {
			highlightUnusedMetabolitesItem.setEnabled(false);
			deleteUnusedItem.setEnabled(false);
		}
		maybeDisplaySuspiciousMetabMessage(statusBarRow());	

		if (getMetabolitesSortColumnIndex() >= 0) {
			metabolitesTable.setSortOrder(getMetabolitesSortColumnIndex(), getMetabolitesSortOrder());
		} else {
			setMetabolitesSortColumnIndex(0);
			setMetabolitesSortOrder(SortOrder.ASCENDING);
		}			
		statusBar.setText("Row 1");	   
	}

	/**
	 * This method is called whenever the reactions table is reloaded
	 * @param model
	 */
	public void setUpReactionsTable(DefaultTableModel model) {
		reactionsTable.setModel(model);
		setReactionsTableLayout();
		analysisRunning = false;

		if (getReactionsSortColumnIndex() >= 0) {
			reactionsTable.setSortOrder(getReactionsSortColumnIndex(), getReactionsSortOrder());
		} else {
			setReactionsSortColumnIndex(0);
			setReactionsSortOrder(SortOrder.ASCENDING);
		}	
		maybeDisplaySuspiciousMetabMessage(statusBarRow());	
		// create list of visible columns when loading table, so tabbing, copy, paste
		// and other functions account for visibility of fva columns
		ArrayList<Integer> visibleReactionsColumns = new ArrayList<Integer>();
		for (int i = 1; i < reactionsTable.getColumnCount(); i++) {
			if (LocalConfig.getInstance().fvaColumnsVisible) {
				visibleReactionsColumns.add(i);
			} else {
				if (i == GraphicalInterfaceConstants.MIN_FLUX_COLUMN ||
						i == GraphicalInterfaceConstants.MAX_FLUX_COLUMN) {
					//if column is min flux or max flux, don't add columns
				} else {
					visibleReactionsColumns.add(i);
				}
			}
		}
		setVisibleReactionsColumns(visibleReactionsColumns);
	}

	/**
	 * This method is called whenever a model is loaded to reset the GUI to default conditions.
	 */
	public void setUpTables() {
		setTitle(GraphicalInterfaceConstants.TITLE + " - " + LocalConfig.getInstance().getModelName());				
		if (saveFile) {
			DynamicTreePanel.getTreePanel().setNodeSelected(0);
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)
					DynamicTreePanel.getTreePanel().tree.getLastSelectedPathComponent();
			Solution nodeInfo = (Solution)node.getUserObject();		
			nodeInfo.setSolutionName(LocalConfig.getInstance().getModelName());
			node.setUserObject((new Solution(LocalConfig.getInstance().getModelName(), LocalConfig.getInstance().getModelName())));			
			treePanel.repaint();
		} else {
			DynamicTreePanel.getTreePanel().clear();
			listModel.addElement(LocalConfig.getInstance().getModelName());
			DynamicTreePanel.getTreePanel().addObject(new Solution(LocalConfig.getInstance().getModelName(), LocalConfig.getInstance().getModelName()));				
			setSortDefault();
			LocalConfig.getInstance().getOptimizationFilesList().clear();
			cleanupTemporaryDirectory();
		}
		DynamicTreePanel.getTreePanel().setNodeSelected(0);
		// enables or disables menu items depending on if there are unused items present
		createUnusedMetabolitesList();
		enableMenuItems();
		setUpCellSelectionMode();		
		maybeDisplaySuspiciousMetabMessage(statusBarRow());
		if (saveEnabled) {
			enableSaveItems(true);
		}
		if (!saveFile) {
			tabbedPane.setSelectedIndex(0);
			metabolitesTable.scrollRectToVisible(metabolitesTable.getCellRect(0, 1, false));
			scrollToLocation(reactionsTable, 0, 1);
		}
		
		try {
			formulaBar.setText((String) reactionsTable.getModel().getValueAt(0, 1));  
		} catch (Throwable t) {
			
		}
	}
	
	public void enableSaveItems(boolean enabled) {
		if (!modelCollectionLoad) {
			saveItem.setEnabled(enabled);
			savebutton.setEnabled(enabled);
			saveEnabled = enabled;
		}
	}

	/**
	 * This method sets boolean values to default values when model is loaded
	 */
	public void setBooleanDefaults() {
		// listener values
		selectedCellChanged = false;
		formulaBarFocusGained = false;
		tabChanged = false;
		// find-replace values
		findMode = false;
		findButtonReactionsClicked = false;
		findButtonMetabolitesClicked = false;
		searchBackwards = FindReplaceConstants.SEARCH_BACKWARDS_DEFAULT;
		matchCase = FindReplaceConstants.MATCH_CASE_DEFAULT;
		wrapAround = FindReplaceConstants.WRAP_AROUND_DEFAULT;
		searchSelectedArea = FindReplaceConstants.SELECTED_AREA_DEFAULT;
		reactionUpdateValid = true;
		metaboliteUpdateValid = true;
		replaceAllMode = false;
		reactionsFindAll = false;
		metabolitesFindAll = false;
		changeReactionFindSelection = true;
		changeMetaboliteFindSelection = true;
		reactionsReplace = false;
		metabolitesReplace = false;
		// paste
		validPaste = true;
		showDuplicatePrompt = true;
		duplicateMetabOK = true;
		participatingMessageShown = false;
		pasteOutOfRangeErrorShown = false;
		continuePasting = true;
		showDuplicateReacPrompt = true;
		duplicateReacOK = true;
		// other
		showErrorMessage = true;
		saveOptFile = false;
		addReacColumn = false;
		addMetabColumn = false;
		showPrompt = true;
		duplicatePromptShown = false;
		reactionsTableEditable = true;
		renameMetabolite = false;
		showMetaboliteRenameInterface = true;
		exit = true;
		reactionCancelLoad = false;
		isRoot = true;
		LocalConfig.getInstance().hasValidGurobiKey = true;
		openFileChooser = true;
		LocalConfig.getInstance().reactionsTableChanged = false;
		LocalConfig.getInstance().metabolitesTableChanged = false;
		LocalConfig.getInstance().includesReactions = true;		
		enterPressed = false;
		reactionsUndo = false;
		saveChangesOKClicked = false;
		mpsPopoutVisible = false;
		// save
		if (!saveSBML) {
			saveFile = false;
		}		
		showJSBMLFileChooser = true;
		gdbbSelected = false;
		gdbbRunning = false;
		gdbbProcessed = false;
	}

	public void clearConfigLists() {	
		LocalConfig.getInstance().getInvalidReactions().clear();
		LocalConfig.getInstance().getMetaboliteAbbreviationIdMap().clear();
		LocalConfig.getInstance().getMetaboliteIdNameMap().clear();
		LocalConfig.getInstance().getMetaboliteUsedMap().clear();
		LocalConfig.getInstance().getReactionAbbreviationIdMap().clear();
		LocalConfig.getInstance().getSuspiciousMetabolites().clear();
		LocalConfig.getInstance().getUnusedList().clear();
		if (!saveSBML) {
			LocalConfig.getInstance().getOptimizationFilesList().clear();
			LocalConfig.getInstance().getMetabolitesTableModelMap().clear();
			LocalConfig.getInstance().getReactionsTableModelMap().clear();
			getGdbbFluxesMap().clear();
			LocalConfig.getInstance().getGdbbKnockoutsList().clear();
			LocalConfig.getInstance().getGdbbKnockoutsMap().clear();
			LocalConfig.getInstance().getShowFVAColumnsList().clear();
		}		
		LocalConfig.getInstance().getAddedMetabolites().clear();
		LocalConfig.getInstance().getReactionEquationMap().clear();
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
		
		LocalConfig.getInstance().setMaxMetabolite(0);
		LocalConfig.getInstance().setMaxMetaboliteId(0);
	}

	public static DefaultTableModel copyMetabolitesTableModel(DefaultTableModel model) {
		DefaultTableModel metabolitesModel = new DefaultTableModel();
		for (int m = 0; m < GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES.length; m++) {
			metabolitesModel.addColumn(GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES[m]);
		}
		for (int n = 0; n < LocalConfig.getInstance().getMetabolitesMetaColumnNames().size(); n++) {
			metabolitesModel.addColumn(LocalConfig.getInstance().getMetabolitesMetaColumnNames().get(n));
		}
		for (int p = 0; p < metabolitesTable.getModel().getRowCount(); p++) {
			Vector <String> metabRow = new Vector<String>();
			for (int q = 0; q < metabolitesTable.getModel().getColumnCount(); q++) {
				metabRow.add((String) metabolitesTable.getModel().getValueAt(p, q));
			}
			metabolitesModel.addRow(metabRow);
		}
		return metabolitesModel;
	}

	public static DefaultTableModel copyReactionsTableModel(DefaultTableModel model) {
		DefaultTableModel reactionsModel = new DefaultTableModel();
		for (int r = 0; r < GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES.length; r++) {
			reactionsModel.addColumn(GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES[r]);
		}	
		for (int s = 0; s < LocalConfig.getInstance().getReactionsMetaColumnNames().size(); s++) {
			reactionsModel.addColumn(LocalConfig.getInstance().getReactionsMetaColumnNames().get(s));
		}
		for (int t = 0; t < reactionsTable.getModel().getRowCount(); t++) {
			Vector <String> reacRow = new Vector<String>();
			for (int v = 0; v < reactionsTable.getModel().getColumnCount(); v++) {
				reacRow.add((String) reactionsTable.getModel().getValueAt(t, v));
			}
			reactionsModel.addRow(reacRow);
		}
		return reactionsModel;
	}

	public static DefaultTableModel createBlankMetabolitesTableModel() {
		DefaultTableModel blankMetabModel = new DefaultTableModel();
		for (int m = 0; m < GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES.length; m++) {
			blankMetabModel.addColumn(GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES[m]);
		}
		for (int j = 0; j < GraphicalInterfaceConstants.BLANK_METABOLITE_ROW_COUNT; j++) {
			blankMetabModel.addRow(createMetabolitesRow(j));
		}
		return blankMetabModel;
	}

	public static DefaultTableModel createBlankReactionsTableModel() {
		DefaultTableModel blankReacModel = new DefaultTableModel();		
		for (int r = 0; r < GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES.length; r++) {
			blankReacModel.addColumn(GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES[r]);
		} 
		for (int i = 0; i < GraphicalInterfaceConstants.BLANK_REACTION_ROW_COUNT; i++) {
			blankReacModel.addRow(createReactionsRow(i));
		}
		return blankReacModel;
	}

	public static DefaultTableModel deletedColumnReactionsTableModel(int index) {
		DefaultTableModel reacModel = (DefaultTableModel) reactionsTable.getModel();
		DefaultTableModel model = new DefaultTableModel();	
		ArrayList<String> newMetaColumnNames = new ArrayList<String>();
		for (int r = 0; r < GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES.length; r++) {
			model.addColumn(GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES[r]);
		} 
		for (int m = 0; m < LocalConfig.getInstance().getReactionsMetaColumnNames().size(); m++) {
			if (m != index - GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES.length) {
				model.addColumn(LocalConfig.getInstance().getReactionsMetaColumnNames().get(m));
				newMetaColumnNames.add(LocalConfig.getInstance().getReactionsMetaColumnNames().get(m));
			}
		}
		LocalConfig.getInstance().setReactionsMetaColumnNames(newMetaColumnNames);
		for (int i = 0; i < reacModel.getRowCount(); i++) {
			Vector <String> reacRow = new Vector<String>();
			for (int j = 0; j < reacModel.getColumnCount(); j++) {
				if (j != index) {	
					if (reacModel.getValueAt(i, j) != null) {
						reacRow.add((String) reacModel.getValueAt(i, j));
					} else {
						reacRow.add("");
					}					
				}				
			}
			model.addRow(reacRow);
		}
		return model;
	}

	public static DefaultTableModel deletedColumnMetabolitesTableModel(int index) {
		DefaultTableModel metabModel = (DefaultTableModel) metabolitesTable.getModel();
		DefaultTableModel model = new DefaultTableModel();	
		ArrayList<String> newMetaColumnNames = new ArrayList<String>();
		for (int r = 0; r < GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES.length; r++) {
			model.addColumn(GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES[r]);
		} 
		for (int m = 0; m < LocalConfig.getInstance().getMetabolitesMetaColumnNames().size(); m++) {
			if (m != index - GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES.length) {
				model.addColumn(LocalConfig.getInstance().getMetabolitesMetaColumnNames().get(m));
				newMetaColumnNames.add(LocalConfig.getInstance().getMetabolitesMetaColumnNames().get(m));
			}
		}
		LocalConfig.getInstance().setMetabolitesMetaColumnNames(newMetaColumnNames);
		for (int i = 0; i < metabModel.getRowCount(); i++) {
			Vector <String> metabRow = new Vector<String>();
			for (int j = 0; j < metabModel.getColumnCount(); j++) {
				if (j != index) {	
					if (metabModel.getValueAt(i, j) != null) {
						metabRow.add((String) metabModel.getValueAt(i, j));
					} else {
						metabRow.add("");
					}					
				}				
			}
			model.addRow(metabRow);
		}
		return model;
	}
	
	/**
	 * 
	 * @param solutionName
	 * Copies table models for analysis, adds models to table model maps,
	 * and list of solutions for deletion from temporary directory 
	 */
	public static void copyTableModelsForAnalysis(String solutionName) {
		// copy models, run optimization on these model
		DefaultTableModel metabolitesOptModel = copyMetabolitesTableModel(LocalConfig.getInstance().getMetabolitesTableModelMap().get(LocalConfig.getInstance().getModelName()));
		DefaultTableModel reactionsOptModel = copyReactionsTableModel(LocalConfig.getInstance().getReactionsTableModelMap().get(LocalConfig.getInstance().getModelName()));				
		LocalConfig.getInstance().getReactionsTableModelMap().put(solutionName, reactionsOptModel);
		LocalConfig.getInstance().getMetabolitesTableModelMap().put(solutionName, metabolitesOptModel);
		
		LocalConfig.getInstance().getOptimizationFilesList().add(solutionName);
	}

	/******************************************************************************/
	//end reload tables methods
	/******************************************************************************/

	/*******************************************************************************/
	//table layouts
	/*******************************************************************************/

	// start reactions table layout
	public void positionColumn(JXTable table,int col_Index) {
		table.moveColumn(table.getColumnCount()-1, col_Index);
	}

	//from http://www.roseindia.net/java/example/java/swing/ChangeColumnName.shtml  
	public void ChangeName(JXTable table, int col_index, String col_name){
		table.getColumnModel().getColumn(col_index).setHeaderValue(col_name);
	}

	//used in numerical columns so they are sorted by value and not as strings
	Comparator< ? > numberComparator = new Comparator< Object >() {
		public int compare(Object o1, Object o2) {
			Double d1 = Double.valueOf(o1 == null ? "0" : (String)o1);
			Double d2 = d1;
			try {
				d2 = Double.valueOf(o2 == null ? "0" : (String)o2);
			} catch (Throwable t) {

			}			
			return d1.compareTo(d2);
		}
	};

	private class ReactionsRowListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent event) {
			if (LocalConfig.getInstance().findReplaceFocusLost) {
				findButtonReactionsClicked = false;
				throwNotFoundError = false;
				if (getFindReplaceDialog() != null && !LocalConfig.getInstance().addReactantPromptShown) {
					
					FindReplaceDialog.replaceButton.setEnabled(false);
					
					FindReplaceDialog.replaceAllButton.setEnabled(false);
					//getFindReplaceDialog().replaceFindButton.setEnabled(false);
				}
			}
			// close reaction editor if row changed. alternative would be to repopulate with
			// values from reaction in newly selected row
			if (getReactionEditor() != null) {
				getReactionEditor().setVisible(false);
				getReactionEditor().dispose();
				LocalConfig.getInstance().reactionEditorVisible = false;
				editorMenu.setEnabled(true);
			}
			String reactionRow = Integer.toString((reactionsTable.getSelectedRow() + 1));
			maybeDisplaySuspiciousMetabMessage(reactionRow);
			if (reactionsTable.getRowCount() > 0 && reactionsTable.getSelectedRow() > -1 && tabbedPane.getSelectedIndex() == 0) {
				enableOrDisableReactionsItems();
				// if any cell selected any existing find all highlighting is unhighlighted
				reactionsFindAll = false;
				metabolitesFindAll = false;	
				reactionsTable.repaint();
				metabolitesTable.repaint();  
				selectedCellChanged = true;
				changeReactionFindSelection = true;
				if (reactionsTable.getSelectedRow() < reactionsTable.getRowCount()) {
					int viewRow = reactionsTable.convertRowIndexToModel(reactionsTable.getSelectedRow());
					// prevents invisible id column from setting id in formulaBar for find events
					if (reactionsTable.getSelectedColumn() > 0) {
						try {
							formulaBar.setText((String) reactionsTable.getModel().getValueAt(viewRow, reactionsTable.getSelectedColumn()));
						} catch (Throwable t) {

						} 
					} 
				}				   			  						
			} else {
				// to catch strange error "Attempt to mutate in notification" 
				try {
					formulaBar.setText("");
				} catch (Throwable t) {

				}

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
					
					FindReplaceDialog.replaceButton.setEnabled(false);
					
					FindReplaceDialog.replaceAllButton.setEnabled(false);
					//getFindReplaceDialog().replaceFindButton.setEnabled(false);
				}				
			}
			if (reactionsTable.getSelectedRow() > -1 && reactionsTable.getSelectedColumn() > -1 && tabbedPane.getSelectedIndex() == 0) {
				String reactionRow = Integer.toString((reactionsTable.getSelectedRow() + 1));
				maybeDisplaySuspiciousMetabMessage(reactionRow);
				enableOrDisableReactionsItems();
				// if any cell selected any existing find all highlighting is unhighlighted
				reactionsFindAll = false;
				metabolitesFindAll = false;	
				reactionsTable.repaint();
				metabolitesTable.repaint();
				selectedCellChanged = true;	
				changeReactionFindSelection = true;
				if (reactionsTable.getSelectedRow() < reactionsTable.getRowCount()) {
					int viewRow = reactionsTable.convertRowIndexToModel(reactionsTable.getSelectedRow());
					// prevents invisible id column from setting id in formulaBar for find events
					if (reactionsTable.getSelectedColumn() > 0) {
						try {
							formulaBar.setText((String) reactionsTable.getModel().getValueAt(viewRow, reactionsTable.getSelectedColumn()));				
						} catch (Throwable t) {

						}					
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

	// disables items if cell or table is non-editable
	public void enableOrDisableReactionsItems() {
		if (reactionsTable.getSelectedColumn() == GraphicalInterfaceConstants.REACTION_EQUN_NAMES_COLUMN) {
		//if (reactionsTable.getSelectedColumn() == GraphicalInterfaceConstants.REVERSIBLE_COLUMN || reactionsTable.getSelectedColumn() == GraphicalInterfaceConstants.REACTION_EQUN_NAMES_COLUMN) {
			formulaBar.setEditable(false);
			formulaBar.setForeground(GraphicalInterfaceConstants.FORMULA_BAR_NONEDITABLE_COLOR);
			formulaBar.setBackground(Color.WHITE);
			formulaBarPasteItem.setEnabled(false);
			pastebutton.setEnabled(false);
		} else {
			if (isRoot && !analysisRunning) {
				formulaBar.setForeground(Color.BLACK);
				formulaBar.setEditable(true);
				formulaBarPasteItem.setEnabled(true);
				pastebutton.setEnabled(true);
			} else {
				formulaBar.setForeground(GraphicalInterfaceConstants.FORMULA_BAR_NONEDITABLE_COLOR);
				formulaBar.setEditable(false);
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
				int id = 0;
				if (reactionsTable.getSelectedRow() > -1) {
					int viewRow = reactionsTable.convertRowIndexToModel(reactionsTable.getSelectedRow());	
					id = Integer.valueOf((String) reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTIONS_ID_COLUMN));
				}
				ReactionUndoItem undoItem = createReactionUndoItem("", "", reactionsTable.getSelectedRow(), reactionsTable.getSelectedColumn(), id, UndoConstants.SORT, UndoConstants.REACTION_UNDO_ITEM_TYPE);
				setOldUsedMap(undoItem);
				LocalConfig.getInstance().getReactionsSortColumns().add(r);
				LocalConfig.getInstance().getReactionsSortOrderList().add(reactionsTable.getSortOrder(reactionsTable.getSortedColumnIndex()));
				undoItem.setOldSortColumnIndex(LocalConfig.getInstance().getReactionsSortColumns().get(LocalConfig.getInstance().getReactionsSortColumns().size() - 2));
				undoItem.setNewSortColumnIndex(LocalConfig.getInstance().getReactionsSortColumns().get(LocalConfig.getInstance().getReactionsSortColumns().size() - 1));
				undoItem.setOldSortOrder(LocalConfig.getInstance().getReactionsSortOrderList().get(LocalConfig.getInstance().getReactionsSortOrderList().size() - 2));
				undoItem.setNewSortOrder(LocalConfig.getInstance().getReactionsSortOrderList().get(LocalConfig.getInstance().getReactionsSortOrderList().size() - 1));
				setNewUsedMap(undoItem);
				setUpReactionsUndo(undoItem);
				unsortReacMenuItem.setEnabled(true);
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
			int id = Integer.valueOf(reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTIONS_ID_COLUMN).toString());					
			if (highlightParticipatingRxns == true && LocalConfig.getInstance().getParticipatingReactions().contains(id)) {									
				return true;
			}						
			return false;
		}
	};

	ColorHighlighter participating = new ColorHighlighter(participatingPredicate, Color.GREEN, null);
	
	HighlightPredicate invalidReactionPredicate = new HighlightPredicate() {
		public boolean isHighlighted(Component renderer ,ComponentAdapter adapter) {
			if (adapter.getValue() != null && LocalConfig.getInstance().getInvalidReactions().contains(adapter.getValue().toString()) &&
					adapter.column == GraphicalInterfaceConstants.REACTION_EQUN_ABBR_COLUMN) {			
				return true;
			}					
			return false;
		}
	};
	
	ColorHighlighter invalidReaction = new ColorHighlighter(invalidReactionPredicate, Color.RED, null);
	
	HighlightPredicate nonEditablePredicate = new HighlightPredicate() {
		public boolean isHighlighted(Component renderer ,ComponentAdapter adapter) {
			if (adapter.column == GraphicalInterfaceConstants.REACTION_EQUN_NAMES_COLUMN ||
					!isRoot || analysisRunning) {									
				return true;
			} 
			return false;
		}
	};

	ColorHighlighter nonEditable = new ColorHighlighter(nonEditablePredicate, null, GraphicalInterfaceConstants.NONEDITABLE_COLOR);

	public void setReactionsTableLayout() {
		reactionsTable.getSelectionModel().addListSelectionListener(new ReactionsRowListener());
		reactionsTable.getColumnModel().getSelectionModel().
		addListSelectionListener(new ReactionsColumnListener());

		reactionsTable.setAutoResizeMode(JXTable.AUTO_RESIZE_OFF);
		reactionsTable.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

		// Comparator allows numerical columns to be sorted by numeric value and
		// not like strings
		reactionsTable.getColumnExt(GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES[GraphicalInterfaceConstants.REACTIONS_ID_COLUMN]).setComparator(numberComparator);
		reactionsTable.getColumnExt(GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES[GraphicalInterfaceConstants.FLUX_VALUE_COLUMN]).setComparator(numberComparator);
		reactionsTable.getColumnExt(GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES[GraphicalInterfaceConstants.MIN_FLUX_COLUMN]).setComparator(numberComparator);
		reactionsTable.getColumnExt(GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES[GraphicalInterfaceConstants.MAX_FLUX_COLUMN]).setComparator(numberComparator);
		reactionsTable.getColumnExt(GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES[GraphicalInterfaceConstants.LOWER_BOUND_COLUMN]).setComparator(numberComparator);
		reactionsTable.getColumnExt(GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES[GraphicalInterfaceConstants.UPPER_BOUND_COLUMN]).setComparator(numberComparator);
		reactionsTable.getColumnExt(GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES[GraphicalInterfaceConstants.BIOLOGICAL_OBJECTIVE_COLUMN]).setComparator(numberComparator);
		reactionsTable.getColumnExt(GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES[GraphicalInterfaceConstants.SYNTHETIC_OBJECTIVE_COLUMN]).setComparator(numberComparator);

		reactionsTable.addHighlighter(participating);
		reactionsTable.addHighlighter(reactionsSelectedArea);
		reactionsTable.addHighlighter(reactionFindAll);
		reactionsTable.addHighlighter(invalidReaction);
		reactionsTable.addHighlighter(nonEditable);

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
			if (c == GraphicalInterfaceConstants.GENE_ASSOCIATION_COLUMN) {
				tips.setToolTip(col, GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES[GraphicalInterfaceConstants.GENE_ASSOCIATION_COLUMN]);
			}
			if (c == GraphicalInterfaceConstants.MIN_FLUX_COLUMN) {
				tips.setToolTip(col, GraphicalInterfaceConstants.MIN_FLUX_TOOLTIP);
			}
			if (c == GraphicalInterfaceConstants.MAX_FLUX_COLUMN) {
				tips.setToolTip(col, GraphicalInterfaceConstants.MAX_FLUX_TOOLTIP);
			}
		}
		reactionsTable.getTableHeader().addMouseMotionListener(tips);
		reactionsTable.getTableHeader().addMouseListener(new ReactionsColumnHeaderListener());
		reactionsTable.getTableHeader().addMouseListener(new ReactionsHeaderPopupListener());	

		//from http://www.java2s.com/Tutorial/Java/0240__Swing/thelastcolumnismovedtothefirstposition.htm
		// columns cannot be rearranged by dragging
		reactionsTable.getTableHeader().setReorderingAllowed(false);  

		int r = reactionsTable.getModel().getColumnCount();
		for (int i = 0; i < r; i++) {	    	
			//set background of id column to grey
			ColorTableCellRenderer reacGreyRenderer = new ColorTableCellRenderer();
			ReactionsTableCellRenderer reacRenderer = new ReactionsTableCellRenderer();
			NumberFormatCellRenderer numRenderer = new NumberFormatCellRenderer();

			TableColumn column = reactionsTable.getColumnModel().getColumn(i);
			column.setPreferredWidth(GraphicalInterfaceConstants.REACTION_META_DEFAULT_WIDTH); 
			if (i==GraphicalInterfaceConstants.REACTIONS_ID_COLUMN) {
				if (showIdColumn) {
					column.setPreferredWidth(GraphicalInterfaceConstants.REACTIONS_ID_WIDTH); 
				} else {
					//sets column not visible
					column.setMaxWidth(0);
					column.setMinWidth(0); 
					column.setWidth(0); 
					column.setPreferredWidth(0);
				}		
				ChangeName(reactionsTable, GraphicalInterfaceConstants.REACTIONS_ID_COLUMN, 
						GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES[GraphicalInterfaceConstants.REACTIONS_ID_COLUMN]); 
				column.setCellRenderer(reacGreyRenderer);
				//sets color of id column to grey
				reacGreyRenderer.setHorizontalAlignment(JLabel.CENTER);	
			} else if (i==GraphicalInterfaceConstants.FLUX_VALUE_COLUMN ||
					i==GraphicalInterfaceConstants.MIN_FLUX_COLUMN ||
					i==GraphicalInterfaceConstants.MAX_FLUX_COLUMN) {
				column.setCellRenderer(numRenderer);
				numRenderer.setHorizontalAlignment(JLabel.RIGHT);
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
			if (i==GraphicalInterfaceConstants.MIN_FLUX_COLUMN) {
				if (LocalConfig.getInstance().fvaColumnsVisible) {
					column.setPreferredWidth(GraphicalInterfaceConstants.DEFAULT_WIDTH); 
					column.setMinWidth(GraphicalInterfaceConstants.DEFAULT_WIDTH);
					column.setMaxWidth(GraphicalInterfaceConstants.DEFAULT_WIDTH);
				} else {
					//sets column not visible
					column.setMaxWidth(0);
					column.setMinWidth(0); 
					column.setWidth(0); 
					column.setPreferredWidth(0); 
				}	
				ChangeName(reactionsTable, GraphicalInterfaceConstants.MIN_FLUX_COLUMN, 
						GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES[GraphicalInterfaceConstants.MIN_FLUX_COLUMN]); 
			}
			if (i==GraphicalInterfaceConstants.MAX_FLUX_COLUMN) {
				if (LocalConfig.getInstance().fvaColumnsVisible) {
					column.setPreferredWidth(GraphicalInterfaceConstants.DEFAULT_WIDTH); 
					column.setMinWidth(GraphicalInterfaceConstants.DEFAULT_WIDTH);
					column.setMaxWidth(GraphicalInterfaceConstants.DEFAULT_WIDTH);
				} else {
					//sets column not visible
					column.setMaxWidth(0);
					column.setMinWidth(0); 
					column.setWidth(0); 
					column.setPreferredWidth(0);
				}	
				ChangeName(reactionsTable, GraphicalInterfaceConstants.MAX_FLUX_COLUMN, 
						GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES[GraphicalInterfaceConstants.MAX_FLUX_COLUMN]); 
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
			if (i==GraphicalInterfaceConstants.FLUX_VALUE_COLUMN) {
				column.setMinWidth(GraphicalInterfaceConstants.DEFAULT_WIDTH);
				column.setMaxWidth(GraphicalInterfaceConstants.DEFAULT_WIDTH);
			}

			if (i==GraphicalInterfaceConstants.GENE_ASSOCIATION_COLUMN) {
				column.setPreferredWidth(GraphicalInterfaceConstants.REACTION_META_DEFAULT_WIDTH);        
				ChangeName(reactionsTable, GraphicalInterfaceConstants.GENE_ASSOCIATION_COLUMN, 
						GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES[GraphicalInterfaceConstants.GENE_ASSOCIATION_COLUMN]); 
			}
			if (i==GraphicalInterfaceConstants.PROTEIN_ASSOCIATION_COLUMN) {
				column.setPreferredWidth(GraphicalInterfaceConstants.REACTION_META_DEFAULT_WIDTH);        
				ChangeName(reactionsTable, GraphicalInterfaceConstants.PROTEIN_ASSOCIATION_COLUMN, 
						GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES[GraphicalInterfaceConstants.PROTEIN_ASSOCIATION_COLUMN]); 
			}
			if (i==GraphicalInterfaceConstants.SUBSYSTEM_COLUMN) {
				column.setPreferredWidth(GraphicalInterfaceConstants.REACTION_META_DEFAULT_WIDTH);        
				ChangeName(reactionsTable, GraphicalInterfaceConstants.SUBSYSTEM_COLUMN, 
						GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES[GraphicalInterfaceConstants.SUBSYSTEM_COLUMN]); 
			}
			if (i==GraphicalInterfaceConstants.PROTEIN_CLASS_COLUMN) {
				column.setPreferredWidth(GraphicalInterfaceConstants.REACTION_META_DEFAULT_WIDTH);        
				ChangeName(reactionsTable, GraphicalInterfaceConstants.PROTEIN_CLASS_COLUMN, 
						GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES[GraphicalInterfaceConstants.PROTEIN_CLASS_COLUMN]); 
			}
			if (i > GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES.length - 1) {
				column.setPreferredWidth(GraphicalInterfaceConstants.REACTION_META_DEFAULT_WIDTH);        
			}
			// only scrolls all the way to the right when column added
			if (addReacColumn) {
				scrollToLocation(reactionsTable, 0, LocalConfig.getInstance().getReactionsMetaColumnNames().size() + GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES.length);
			}
		}
	}

	/********************************************************************************/
	// end reactions table layout
	/********************************************************************************/

	/********************************************************************************/
	// start metabolites table layout
	/********************************************************************************/

	private class MetabolitesRowListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent event) {
			highlightParticipatingRxns = false;
			if (LocalConfig.getInstance().findReplaceFocusLost) {
				findButtonMetabolitesClicked = false;
				throwNotFoundError = false;
				if (getFindReplaceDialog() != null && !LocalConfig.getInstance().addReactantPromptShown) {
					
					FindReplaceDialog.replaceButton.setEnabled(false);
					
					FindReplaceDialog.replaceAllButton.setEnabled(false);
					//getFindReplaceDialog().replaceFindButton.setEnabled(false);
				}
			}
			String metaboliteRow = Integer.toString((metabolitesTable.getSelectedRow() + 1));
			maybeDisplaySuspiciousMetabMessage(metaboliteRow);
			if (metabolitesTable.getRowCount() > 0 && metabolitesTable.getSelectedRow() > -1 && tabbedPane.getSelectedIndex() == 1) {
				enableOrDisableMetabolitesItems();
				// if any cell selected any existing find all highlighting is unhighlighted
				reactionsFindAll = false;
				metabolitesFindAll = false;	
				reactionsTable.repaint();
				metabolitesTable.repaint();  
				selectedCellChanged = true;
				changeMetaboliteFindSelection = true;
				if (metabolitesTable.getSelectedRow() < metabolitesTable.getRowCount()) {
					int viewRow = metabolitesTable.convertRowIndexToModel(metabolitesTable.getSelectedRow());
					// prevents invisible id column from setting id in formulaBar for find events
					if (metabolitesTable.getSelectedColumn() > 0) {
						try {
							formulaBar.setText((String) metabolitesTable.getModel().getValueAt(viewRow, metabolitesTable.getSelectedColumn()));
						} catch (Throwable t) {

						} 
					} 
				}				   			  						
			} else {
				// to catch strange error "Attempt to mutate in notification" 
				try {
					formulaBar.setText("");
				} catch (Throwable t) {

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
					
					FindReplaceDialog.replaceButton.setEnabled(false);
					
					FindReplaceDialog.replaceAllButton.setEnabled(false);
					//getFindReplaceDialog().replaceFindButton.setEnabled(false);
				}				
			}
			if (metabolitesTable.getSelectedRow() > -1 && metabolitesTable.getSelectedColumn() > -1 && tabbedPane.getSelectedIndex() == 1) {
				String metaboliteRow = Integer.toString((metabolitesTable.getSelectedRow() + 1));
				maybeDisplaySuspiciousMetabMessage(metaboliteRow);
				enableOrDisableMetabolitesItems();
				// if any cell selected any existing find all highlighting is unhighlighted
				reactionsFindAll = false;
				metabolitesFindAll = false;	
				reactionsTable.repaint();
				metabolitesTable.repaint();
				selectedCellChanged = true;	
				changeReactionFindSelection = true;
				if (metabolitesTable.getSelectedRow() < metabolitesTable.getRowCount()) {
					int viewRow = metabolitesTable.convertRowIndexToModel(metabolitesTable.getSelectedRow());
					// prevents invisible id column from setting id in formulaBar for find events
					if (metabolitesTable.getSelectedColumn() > 0) {
						try {
							formulaBar.setText((String) metabolitesTable.getModel().getValueAt(viewRow, metabolitesTable.getSelectedColumn()));				
						} catch (Throwable t) {

						}					
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

	// disables items if table is non-editable
	public void enableOrDisableMetabolitesItems() {
		if (metabolitesTable.getSelectedRow() > -1 && metabolitesTable.getSelectedColumn() > 0) {
			if (isRoot && !analysisRunning) {
				formulaBar.setForeground(Color.BLACK);
				formulaBar.setEditable(true);
				formulaBarPasteItem.setEnabled(true);
				pastebutton.setEnabled(true);
			} else {
				formulaBar.setEditable(false);
				formulaBar.setForeground(GraphicalInterfaceConstants.FORMULA_BAR_NONEDITABLE_COLOR);
				formulaBar.setBackground(Color.WHITE);
				formulaBarPasteItem.setEnabled(false);
				pastebutton.setEnabled(false);
			}
		}		
	}

	class MetabolitesColumnHeaderListener extends MouseAdapter {
		public void mouseClicked(MouseEvent evt) {
			int m = metabolitesTable.getSortedColumnIndex();
			// bug: mouse listener sets multiple undo items
			// this fix works
			if (m != getMetabolitesSortColumnIndex() || metabolitesTable.getSortOrder(metabolitesTable.getSortedColumnIndex()) != getMetabolitesSortOrder()) {
				int id = 0;
				if (metabolitesTable.getSelectedRow() > -1) {
					int viewRow = metabolitesTable.convertRowIndexToModel(metabolitesTable.getSelectedRow());
					id = Integer.valueOf((String) metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_ID_COLUMN));
				}
				MetaboliteUndoItem undoItem = createMetaboliteUndoItem("", "", metabolitesTable.getSelectedRow(), metabolitesTable.getSelectedColumn(), id, UndoConstants.SORT, UndoConstants.METABOLITE_UNDO_ITEM_TYPE);
				setUndoOldCollections(undoItem);
				LocalConfig.getInstance().getMetabolitesSortColumns().add(m);
				LocalConfig.getInstance().getMetabolitesSortOrderList().add(metabolitesTable.getSortOrder(metabolitesTable.getSortedColumnIndex()));
				undoItem.setOldSortColumnIndex(LocalConfig.getInstance().getMetabolitesSortColumns().get(LocalConfig.getInstance().getMetabolitesSortColumns().size() - 2));
				undoItem.setNewSortColumnIndex(LocalConfig.getInstance().getMetabolitesSortColumns().get(LocalConfig.getInstance().getMetabolitesSortColumns().size() - 1));
				undoItem.setOldSortOrder(LocalConfig.getInstance().getMetabolitesSortOrderList().get(LocalConfig.getInstance().getMetabolitesSortOrderList().size() - 2));
				undoItem.setNewSortOrder(LocalConfig.getInstance().getMetabolitesSortOrderList().get(LocalConfig.getInstance().getMetabolitesSortOrderList().size() - 1));
				setUpMetabolitesUndo(undoItem);
				unsortMetabMenuItem.setEnabled(true);
			}
			setMetabolitesSortColumnIndex(m);
			setMetabolitesSortOrder(metabolitesTable.getSortOrder(metabolitesTable.getSortedColumnIndex()));
		}
	}

	HighlightPredicate suspiciousPredicate = new HighlightPredicate() {
		public boolean isHighlighted(Component renderer ,ComponentAdapter adapter) {
			int viewRow = metabolitesTable.convertRowIndexToModel(adapter.row);			
			int id = Integer.valueOf(metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_ID_COLUMN).toString());					
			if (isRoot && metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN) != null) {
				if (LocalConfig.getInstance().getSuspiciousMetabolites().contains(id)) {					
					return true;
				}
			}						
			return false;
		}
	};

	ColorHighlighter suspicious = new ColorHighlighter(suspiciousPredicate, Color.ORANGE, null);

	HighlightPredicate unusedPredicate = new HighlightPredicate() {
		public boolean isHighlighted(Component renderer ,ComponentAdapter adapter) {
			int viewRow = metabolitesTable.convertRowIndexToModel(adapter.row);
			if (isRoot && metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN) != null) {
				if (highlightUnusedMetabolites == true && !(LocalConfig.getInstance().getMetaboliteUsedMap().containsKey(metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN).toString()))) {					
					return true;
				}
			}						
			return false;
		}
	};

	ColorHighlighter unused = new ColorHighlighter(unusedPredicate, Color.YELLOW, null);

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

	HighlightPredicate nonEditableMetabPredicate = new HighlightPredicate() {
		public boolean isHighlighted(Component renderer ,ComponentAdapter adapter) {
			if (!isRoot || analysisRunning) {									
				return true;
			}						
			return false;
		}
	};

	ColorHighlighter nonEditableMetab = new ColorHighlighter(nonEditableMetabPredicate, null, GraphicalInterfaceConstants.NONEDITABLE_COLOR);
	
	public void setMetabolitesTableLayout() {
		metabolitesTable.getSelectionModel().addListSelectionListener(new MetabolitesRowListener());
		metabolitesTable.getColumnModel().getSelectionModel().
		addListSelectionListener(new MetabolitesColumnListener());

		metabolitesTable.setAutoResizeMode(JXTable.AUTO_RESIZE_OFF);
		metabolitesTable.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

		metabolitesTable.getColumnExt(GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES[GraphicalInterfaceConstants.METABOLITE_ID_COLUMN]).setComparator(numberComparator);

		metabolitesTable.addHighlighter(unused);
		metabolitesTable.addHighlighter(suspicious);
		metabolitesTable.addHighlighter(metabolitesSelectedArea);
		metabolitesTable.addHighlighter(metaboliteFindAll);
		metabolitesTable.addHighlighter(nonEditableMetab);

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

		int m = metabolitesTable.getModel().getColumnCount();
		for (int w = 0; w < m; w++) {
			ColorTableCellRenderer metabGreyRenderer = new ColorTableCellRenderer();
			MetabolitesTableCellRenderer metabRenderer = new MetabolitesTableCellRenderer();

			TableColumn column = metabolitesTable.getColumnModel().getColumn(w);
			column.setPreferredWidth(GraphicalInterfaceConstants.METABOLITE_META_DEFAULT_WIDTH); 
			if (w==GraphicalInterfaceConstants.METABOLITE_ID_COLUMN) {
				if (showIdColumn) {
					column.setPreferredWidth(GraphicalInterfaceConstants.METABOLITE_ID_WIDTH);
				} else {
					//sets column not visible
					column.setMaxWidth(0);
					column.setMinWidth(0); 
					column.setWidth(0); 
					column.setPreferredWidth(0);
				}		
				ChangeName(metabolitesTable, GraphicalInterfaceConstants.METABOLITE_ID_COLUMN, 
						GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES[GraphicalInterfaceConstants.METABOLITE_ID_COLUMN]);     
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
			if (w > GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES.length - 1) {
				column.setPreferredWidth(GraphicalInterfaceConstants.METABOLITE_META_DEFAULT_WIDTH);
			}
			// only scrolls all the way to the right when column added
			if (addMetabColumn) {
				scrollToLocation(metabolitesTable, 0, LocalConfig.getInstance().getMetabolitesMetaColumnNames().size() + GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES.length);
			}
		}	  
	}

	public static Vector<String> createReactionsRow(int id)
	{
		Vector<String> row = new Vector<String>();
		row.addElement(Integer.toString(id));
		// start at 1 since id is already added
		for (int i = 1; i < GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES.length; i++) {		  
			if (i==GraphicalInterfaceConstants.KO_COLUMN) {
				row.addElement(GraphicalInterfaceConstants.KO_DEFAULT);
			} else if (i==GraphicalInterfaceConstants.FLUX_VALUE_COLUMN) {
				row.addElement(GraphicalInterfaceConstants.FLUX_VALUE_DEFAULT_STRING);
			} else if (i==GraphicalInterfaceConstants.MIN_FLUX_COLUMN) {	
				row.addElement(GraphicalInterfaceConstants.MIN_FLUX_DEFAULT_STRING);
			} else if (i==GraphicalInterfaceConstants.MAX_FLUX_COLUMN) {	
				row.addElement(GraphicalInterfaceConstants.MAX_FLUX_DEFAULT_STRING);	
			} else if (i==GraphicalInterfaceConstants.REVERSIBLE_COLUMN) {
				row.addElement(GraphicalInterfaceConstants.REVERSIBLE_DEFAULT);
			} else if (i==GraphicalInterfaceConstants.LOWER_BOUND_COLUMN) {
				row.addElement(GraphicalInterfaceConstants.LOWER_BOUND_DEFAULT_REVERSIBLE_STRING);
			} else if (i==GraphicalInterfaceConstants.UPPER_BOUND_COLUMN) {
				row.addElement(GraphicalInterfaceConstants.UPPER_BOUND_DEFAULT_STRING);
			} else if (i==GraphicalInterfaceConstants.BIOLOGICAL_OBJECTIVE_COLUMN) {
				row.addElement(GraphicalInterfaceConstants.BIOLOGICAL_OBJECTIVE_DEFAULT_STRING);
			} else if (i==GraphicalInterfaceConstants.SYNTHETIC_OBJECTIVE_COLUMN) {
				row.addElement(GraphicalInterfaceConstants.SYNTHETIC_OBJECTIVE_DEFAULT_STRING);
			} else {
				row.addElement("");
			}		  
		}
		return row;
	}

	public static Vector<String> createMetabolitesRow(int id)
	{
		Vector<String> row = new Vector<String>();
		row.addElement(Integer.toString(id));
		for (int i = 1; i < GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES.length; i++) {
			if (i==GraphicalInterfaceConstants.BOUNDARY_COLUMN) {
				row.addElement(GraphicalInterfaceConstants.BOUNDARY_DEFAULT);
			} else {
				row.addElement("");
			}	
		}
		return row;
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
		setReactionsSortDefault();
		setMetabolitesSortDefault();
	}
	
	public void setReactionsSortDefault() {
		setReactionsSortColumnIndex(0);
		setReactionsSortOrder(SortOrder.ASCENDING);
	}
	
	public void setMetabolitesSortDefault() {
		setMetabolitesSortColumnIndex(0);
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
		if (columnIndex > GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES.length - 1) {
			deleteColumnMenu.setEnabled(true);
		} else {
			deleteColumnMenu.setEnabled(false);
		}
		deleteColumnMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				// copy old model for undo/redo
				DefaultTableModel oldReactionsModel = copyReactionsTableModel((DefaultTableModel) reactionsTable.getModel());			
				copyReactionsTableModels(oldReactionsModel);
				ReactionUndoItem undoItem = createReactionUndoItem("", "", reactionsTable.getSelectedRow(), reactionsTable.getSelectedColumn(), 0, UndoConstants.DELETE_COLUMN, UndoConstants.REACTION_UNDO_ITEM_TYPE);
				setOldUsedMap(undoItem);
				undoItem.setTableCopyIndex(LocalConfig.getInstance().getNumReactionTablesCopied());								
				undoItem.setDeletedColumnIndex(columnIndex);
				ArrayList<String> oldMetaCol = new ArrayList<String>();
				ArrayList<String> newMetaCol = new ArrayList<String>();
				for (int i = 0; i < LocalConfig.getInstance().getReactionsMetaColumnNames().size(); i++) {
					oldMetaCol.add(LocalConfig.getInstance().getReactionsMetaColumnNames().get(i));
				}
				undoItem.setOldMetaColumnNames(oldMetaCol);
				DefaultTableModel model = deletedColumnReactionsTableModel(columnIndex);
				reactionsTable.setModel(model);
				setUpReactionsTable(model);
				copyReactionsTableModels(model);
				for (int i = 0; i < LocalConfig.getInstance().getReactionsMetaColumnNames().size(); i++) {
					newMetaCol.add(LocalConfig.getInstance().getReactionsMetaColumnNames().get(i));
				}
				undoItem.setNewMetaColumnNames(newMetaCol);
				setNewUsedMap(undoItem);
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
		if (columnIndex > GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES.length - 1) {
			deleteColumnMenu.setEnabled(true);
		} else {
			deleteColumnMenu.setEnabled(false);
		}
		deleteColumnMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				// copy old model for undo/redo
				DefaultTableModel oldMetabolitesModel = copyMetabolitesTableModel((DefaultTableModel) metabolitesTable.getModel());			
				copyMetabolitesTableModels(oldMetabolitesModel);
				MetaboliteUndoItem undoItem = createMetaboliteUndoItem("", "", metabolitesTable.getSelectedRow(), metabolitesTable.getSelectedColumn(), 0, UndoConstants.DELETE_COLUMN, UndoConstants.METABOLITE_UNDO_ITEM_TYPE);
				setUndoOldCollections(undoItem);
				undoItem.setTableCopyIndex(LocalConfig.getInstance().getNumMetabolitesTableCopied());								
				undoItem.setDeletedColumnIndex(columnIndex);
				ArrayList<String> oldMetaCol = new ArrayList<String>();
				ArrayList<String> newMetaCol = new ArrayList<String>();
				for (int i = 0; i < LocalConfig.getInstance().getMetabolitesMetaColumnNames().size(); i++) {
					oldMetaCol.add(LocalConfig.getInstance().getMetabolitesMetaColumnNames().get(i));
				}
				undoItem.setOldMetaColumnNames(oldMetaCol);
				DefaultTableModel model = deletedColumnMetabolitesTableModel(columnIndex);
				metabolitesTable.setModel(model);
				setUpMetabolitesTable(model);
				copyMetabolitesTableModels(model);
				for (int i = 0; i < LocalConfig.getInstance().getMetabolitesMetaColumnNames().size(); i++) {
					newMetaCol.add(LocalConfig.getInstance().getMetabolitesMetaColumnNames().get(i));
				}
				undoItem.setNewMetaColumnNames(newMetaCol);
				setUndoNewCollections(undoItem);				
				setUpMetabolitesUndo(undoItem);
			}
		});
		metabolitesHeaderContextMenu.add(deleteColumnMenu);	

		return metabolitesHeaderContextMenu;

	}

	/************************************************************************************/
	//end header context menus
	/************************************************************************************/	

	/************************************************************************************/
	//context menu methods
	/************************************************************************************/
	
	public void showPasteOutOfRangeError() {
		if (!pasteOutOfRangeErrorShown) {
			JOptionPane.showMessageDialog(null,                
					GraphicalInterfaceConstants.PASTE_OUT_OF_RANGE_ERROR,                
					"Paste Error",                                
					JOptionPane.ERROR_MESSAGE);
			pasteOutOfRangeErrorShown = true;
		}				
	}
	
	/**
	 * Changes cell selection if selected cell not in selected area.
	 * Used to change selection on right click if cell not selected.
	 * @param row
	 * @param col
	 * @param table
	 */
	public void maybeChangeSelection(int row, int col, JXTable table) {
		ArrayList<Integer> rowlist = new ArrayList<Integer>();
		ArrayList<Integer> collist = new ArrayList<Integer>();
		for (int i = 0; i < table.getSelectedRows().length; i++) {
			rowlist.add(table.getSelectedRows()[i]);
		}
		for (int i = 0; i < table.getSelectedColumns().length; i++) {
			collist.add(table.getSelectedColumns()[i]);
		}
		if (rowlist.contains(row) &&
				collist.contains(col)) {
			
		} else {
			setTableCellFocused(row, col, table);
		}
	}
	
	/************************************************************************************/
	//end context menu methods
	/************************************************************************************/
	
	/*******************************************************************************/
	//Reactions Table context menus
	/*******************************************************************************/	

	// from http://docs.oracle.com/javase/tutorial/uiswing/components/menu.html
	public class ReactionsPopupListener extends MouseAdapter {

		public void maybeShowPopup(MouseEvent e) {
			if (e.isPopupTrigger() && reactionsTable.isEnabled()) {
				if (!analysisRunning) {
					Point p = new Point(e.getX(), e.getY());
					int col = reactionsTable.columnAtPoint(p);
					int row = reactionsTable.rowAtPoint(p);
					setCurrentReactionsRow(row);

					// translate table index to model index
					//int mcol = reactionsTable.getColumn(reactionsTable.getColumnName(col)).getModelIndex();

					if (row >= 0 && row < reactionsTable.getRowCount()) {
						cancelCellEditing();
						maybeChangeSelection(row, col, reactionsTable);
						// create reaction equation column popup menu
						if (col == GraphicalInterfaceConstants.REACTION_EQUN_ABBR_COLUMN) {
							JPopupMenu reactionsContextMenu = createReactionEquationContextMenu(row, col);
							if (reactionsContextMenu != null
									&& reactionsContextMenu.getComponentCount() > 0) {
								reactionsContextMenu.show(reactionsTable, p.x, p.y);
							}
						} else {
							// create popup for remaining columns
							JPopupMenu contextMenu = createReactionsContextMenu(row, col);
							// ... and show it
							if (contextMenu != null
									&& contextMenu.getComponentCount() > 0) {
								contextMenu.show(reactionsTable, p.x, p.y);
							}
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

	private JPopupMenu createReactionEquationContextMenu(final int rowIndex,
			final int columnIndex) {
		JPopupMenu contextMenu = createReactionsContextMenu(rowIndex, columnIndex);
		contextMenu.addSeparator();
		if (!reactionsTableEditable || LocalConfig.getInstance().reactionEditorVisible) {
			editorMenu.setEnabled(false);
		}
		editorMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!LocalConfig.getInstance().reactionEditorVisible) {
					setCurrentReactionsRow(rowIndex);
					ReactionEditor reactionEditor = new ReactionEditor();
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
			int viewRow = reactionsTable.convertRowIndexToModel(getCurrentReactionsRow());
//			int viewRow = reactionsTable.convertRowIndexToModel(reactionsTable.getSelectedRow());
			int id = (Integer.valueOf((String) reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTIONS_ID_COLUMN)));
			String oldValue = (String) reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTION_EQUN_ABBR_COLUMN);
			reactionEditor.setReactionEquation(reactionEditor.reactionArea.getText());
			reactionsTable.getModel().setValueAt(reactionEditor.getReactionEquation(), viewRow, GraphicalInterfaceConstants.REACTION_EQUN_ABBR_COLUMN);
			scrollToLocation(reactionsTable, reactionsTable.getSelectedRow(), GraphicalInterfaceConstants.REACTION_EQUN_ABBR_COLUMN);
			updateReactionsCellIfValid(oldValue, reactionEditor.getReactionEquation(), viewRow, GraphicalInterfaceConstants.REACTION_EQUN_ABBR_COLUMN);
			ReactionUndoItem undoItem = createReactionUndoItem(reactionEditor.getOldReaction(), reactionEditor.getReactionEquation(), reactionsTable.getSelectedRow(), GraphicalInterfaceConstants.REACTION_EQUN_ABBR_COLUMN, id, UndoConstants.TYPING, UndoConstants.REACTION_UNDO_ITEM_TYPE);
			setOldUsedMap(undoItem);
			undoItem.setMaxMetab(LocalConfig.getInstance().getMaxMetabolite());
			undoItem.setMaxMetabId(LocalConfig.getInstance().getMaxMetaboliteId());
			undoItem.setOldLowerBound((String) (reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.LOWER_BOUND_COLUMN)));
			undoItem.setOldUpperBound((String) (reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.UPPER_BOUND_COLUMN)));
			if (reactionUpdateValid) {
				enableSaveItems(true);
				LocalConfig.getInstance().reactionsTableChanged = true;
				formulaBar.setText(reactionEditor.getReactionEquation());
				undoItem.setNewLowerBound((String) (reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.LOWER_BOUND_COLUMN)));
				undoItem.setNewUpperBound((String) (reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.UPPER_BOUND_COLUMN)));
				setNewUsedMap(undoItem);
				setUpReactionsUndo(undoItem);
			} else {
				formulaBar.setText(oldValue);
			}
			maybeDisplaySuspiciousMetabMessage(statusBarRow());	
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

	private JPopupMenu createReactionsContextMenu(final int rowIndex,
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
				"Select Table Cells Only");

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
				metabolitesTable.clearSelection();
				reactionsTable.selectAll();			
				selectReactionsRows();
			}
		});
		selectCellsOnly.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
		selectCellsOnly.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (selectCellsOnly.isSelected()) {
					includeRxnColumnNames = false;
				} else {
					includeRxnColumnNames = true;
				}				
				reactionsTable.selectAll();			
				selectReactionsRows();
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

		JMenuItem pasteMenu = new JMenuItem("Paste");
		pasteMenu.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_V, ActionEvent.CTRL_MASK));
		if (isClipboardContainingText(this)
				&& reactionsTable.getModel().isCellEditable(rowIndex, columnIndex)
				&& isRoot) {
			pasteMenu.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					reactionsPaste();										
				}
			});
		} else {
			pasteMenu.setEnabled(false);
		}
		// the above - isCellEditable should disable paste for this column but for
		// some reason it does not, but this works. this column is not editable
		// so it is not permissible to paste here either
		if (columnIndex == GraphicalInterfaceConstants.REACTION_EQUN_NAMES_COLUMN) {
			pasteMenu.setEnabled(false);
		}
		contextMenu.add(pasteMenu);

		JMenuItem clearMenu = new JMenuItem("Clear Contents");
		if (isRoot) {
			if (columnIndex == GraphicalInterfaceConstants.KO_COLUMN || columnIndex == GraphicalInterfaceConstants.FLUX_VALUE_COLUMN ||
					columnIndex == GraphicalInterfaceConstants.MIN_FLUX_COLUMN || columnIndex == GraphicalInterfaceConstants.MAX_FLUX_COLUMN ||
					columnIndex == GraphicalInterfaceConstants.LOWER_BOUND_COLUMN || columnIndex == GraphicalInterfaceConstants.UPPER_BOUND_COLUMN ||
					columnIndex == GraphicalInterfaceConstants.BIOLOGICAL_OBJECTIVE_COLUMN || columnIndex == GraphicalInterfaceConstants.SYNTHETIC_OBJECTIVE_COLUMN ||
					columnIndex == GraphicalInterfaceConstants.REACTION_EQUN_NAMES_COLUMN || columnIndex == GraphicalInterfaceConstants.REVERSIBLE_COLUMN) {
				clearMenu.setEnabled(false);
			} else {
				clearMenu.setEnabled(true);
			}			
		} else {
			clearMenu.setEnabled(false);
		}
		clearMenu.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_E, ActionEvent.CTRL_MASK));
		clearMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reactionsClear();
			}
		});
		contextMenu.add(clearMenu);

		contextMenu.addSeparator();

		if (isRoot) {
			deleteReactionRowMenuItem.setEnabled(true);
		} else {
			deleteReactionRowMenuItem.setEnabled(false);
		}
		deleteReactionRowMenuItem.addActionListener(new ActionListener() {
			@SuppressWarnings( "unchecked" )
			public void actionPerformed(ActionEvent ae) {
				if (reactionsTable.getModel().getRowCount() > 0 && reactionsTable.getSelectedRow() != -1 )
				{
					Map<String, Object> oldMetaboliteUsedMap = new HashMap<String, Object>();

					try {
						if (LocalConfig.getInstance().getMetaboliteUsedMap() != null) {
							oldMetaboliteUsedMap = (Map<String, Object>) (ObjectCloner.deepCopy(LocalConfig.getInstance().getMetaboliteUsedMap()));
						}
					} catch (Exception e3) {
						JOptionPane.showMessageDialog(null,                
								"Error Deleting Row.",                
								"Error",                                
								JOptionPane.ERROR_MESSAGE);
						//e3.printStackTrace();
					}
					// copy old model for undo/redo
					DefaultTableModel oldReactionsModel = copyReactionsTableModel((DefaultTableModel) reactionsTable.getModel());			
					copyReactionsTableModels(oldReactionsModel);
					DefaultTableModel model = (DefaultTableModel) reactionsTable.getModel();
					int startRow=reactionsTable.getSelectedRows()[0]; 
					int rowIndexEnd = reactionsTable.getSelectionModel().getMaxSelectionIndex();
					ArrayList<Integer> deleteIds = new ArrayList<Integer>();
					for (int r = rowIndexEnd; r >= startRow; r--) {
						int viewRow = reactionsTable.convertRowIndexToModel(r);
						int id = (Integer.valueOf((String) reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTIONS_ID_COLUMN)));
						deleteIds.add(id);
						String key = (String) reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTION_ABBREVIATION_COLUMN);
						if (LocalConfig.getInstance().getReactionAbbreviationIdMap().containsKey(key)) {
							LocalConfig.getInstance().getReactionAbbreviationIdMap().remove(key);
						}
						model.removeRow(viewRow);
						if (LocalConfig.getInstance().getReactionEquationMap().get(id) != null) {
							for (int i = 0; i < ((SBMLReactionEquation) LocalConfig.getInstance().getReactionEquationMap().get(id)).getReactants().size(); i++) {
								String reactant = ((SBMLReactionEquation) LocalConfig.getInstance().getReactionEquationMap().get(id)).getReactants().get(i).getMetaboliteAbbreviation();
								if (oldMetaboliteUsedMap.containsKey(reactant)) {
									updateMetaboliteUsedMap(reactant);
								}
							}
							for (int j = 0; j < ((SBMLReactionEquation) LocalConfig.getInstance().getReactionEquationMap().get(id)).getProducts().size(); j++) {
								String product = ((SBMLReactionEquation) LocalConfig.getInstance().getReactionEquationMap().get(id)).getProducts().get(j).getMetaboliteAbbreviation();
								if (oldMetaboliteUsedMap.containsKey(product)) {
									updateMetaboliteUsedMap(product);
								}
							}
							LocalConfig.getInstance().getReactionEquationMap().remove(id);
						}
						
//						System.out.println("del " + LocalConfig.getInstance().getReactionEquationMap());
//						System.out.println("del id " + LocalConfig.getInstance().getMetaboliteAbbreviationIdMap());
//						System.out.println("del used " + LocalConfig.getInstance().getMetaboliteUsedMap());
					}
					//System.out.println(deleteIds);
					ReactionUndoItem undoItem = createReactionUndoItem("", "", startRow, reactionsTable.getSelectedColumn(), deleteIds.get(0), UndoConstants.DELETE_ROW, UndoConstants.REACTION_UNDO_ITEM_TYPE);
					undoItem.setOldMetaboliteUsedMap(oldMetaboliteUsedMap);
					undoItem.setTableCopyIndex(LocalConfig.getInstance().getNumReactionTablesCopied());
					undoItem.setDeleteIds(deleteIds);
					copyReactionsTableModels(model); 
					setNewUsedMap(undoItem);
					setUpReactionsUndo(undoItem);
					LocalConfig.getInstance().getReactionsTableModelMap().put(LocalConfig.getInstance().getModelName(), model);
					setUpReactionsTable(model);
				}
				formulaBar.setText("");
			}
		});
		contextMenu.add(deleteReactionRowMenuItem);	

		contextMenu.addSeparator();

		if (highlightParticipatingRxns) {
			unhighlightMenu.setEnabled(true);
		} else {
			unhighlightMenu.setEnabled(false);
		}
		unhighlightMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				highlightParticipatingRxns = false;
				reactionsTable.repaint();
			}
		});

		contextMenu.add(unhighlightMenu);	

		return contextMenu;
	}
	
	public void updateMetaboliteUsedMap(String species) {
		int usedCount = (Integer) LocalConfig.getInstance().getMetaboliteUsedMap().get(species);
		if (usedCount > 1) {
			LocalConfig.getInstance().getMetaboliteUsedMap().put(species, new Integer(usedCount - 1));									
		} else {
			LocalConfig.getInstance().getMetaboliteUsedMap().remove(species);
		}
	}

	/*******************************************************************************/
	//end Reactions Table context menus
	/*******************************************************************************/

	/**************************************************************************/
	//reactionsTable context menu methods
	/**************************************************************************/

	public void selectReactionsRows() {
		setClipboardContents("");

		StringBuffer sbf=new StringBuffer();
		int numrows = reactionsTable.getSelectedRowCount(); 
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
			for (int r = 0; r < LocalConfig.getInstance().getReactionsMetaColumnNames().size(); r++) {
				sbf.append("\t");
				sbf.append(LocalConfig.getInstance().getReactionsMetaColumnNames().get(r));					
			}
			sbf.append("\n");
		}

		for (int i = 0; i < numrows; i++) {
			//starts at 1 to avoid reading hidden db id column
			for (int j = 1; j < reactionsTable.getColumnCount(); j++) 
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
		int numCols=reactionsTable.getSelectedColumnCount(); 
		int numRows=reactionsTable.getSelectedRowCount(); 
		int[] rowsSelected=reactionsTable.getSelectedRows(); 
		int[] colsSelected=reactionsTable.getSelectedColumns(); 
		try {
			if (numRows!=rowsSelected[rowsSelected.length-1]-rowsSelected[0]+1 || numRows!=rowsSelected.length || 
					numCols!=colsSelected[colsSelected.length-1]-colsSelected[0]+1 || numCols!=colsSelected.length) {

				JOptionPane.showMessageDialog(null, "Invalid Copy Selection", "Invalid Copy Selection", JOptionPane.ERROR_MESSAGE);
				return; 
			}
		} catch (Throwable t) {
			
		}
		 		
		// Clipboard already contains correct values for select all and include column names, do not change 
		if (rowsSelected.length == reactionsTable.getRowCount() && colsSelected.length == reactionsTable.getColumnCount() && includeRxnColumnNames) {

		} else {
			if (getSelectionMode() == 1) {
				StringBuffer excelStr=new StringBuffer(); 
				for (int i = 0; i < reactionsTable.getRowCount(); i++) { 
					for (int j=0; j<numCols; j++) {
						if (getVisibleReactionsColumns().contains(colsSelected[j])) {
							try {
								excelStr.append(escape(reactionsTable.getValueAt(i, colsSelected[j]))); 
							} catch (Throwable t) {

							}
							if (j<numCols-1) {
								//System.out.println("t");
								excelStr.append("\t"); 
							} 
						}
					}
					excelStr.append("\n");
				}
				StringSelection sel  = new StringSelection(excelStr.toString()); 
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(sel, sel);
			} else if (getSelectionMode() == 2) {
				LocalConfig.getInstance().includesReactions = true;
				includeRxnColumnNames = false;				
				selectReactionsRows();
			} else {
				StringBuffer excelStr=new StringBuffer(); 
				ArrayList<ModelReactionEquation> copiedReactionList = new ArrayList<ModelReactionEquation>();
				for (int i=0; i<numRows; i++) { 
					for (int j=0; j<numCols; j++) { 
						if (getVisibleReactionsColumns().contains(colsSelected[j])) {
							if (colsSelected[j] == GraphicalInterfaceConstants.REACTION_EQUN_ABBR_COLUMN) {
								int viewRow = reactionsTable.convertRowIndexToModel(rowsSelected[i]);
								int id = Integer.valueOf((String) reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTIONS_ID_COLUMN));
								copiedReactionList.add(LocalConfig.getInstance().getReactionEquationMap().get(id));					
							}
							try {
								excelStr.append(escape(reactionsTable.getValueAt(rowsSelected[i], colsSelected[j]))); 
							} catch (Throwable t) {

							}	
							if (j<numCols-1) {
								//System.out.println("t");
								excelStr.append("\t"); 
							} 
						}				
					} 
					//System.out.println("n");
					excelStr.append("\n"); 
				}
				StringSelection sel  = new StringSelection(excelStr.toString()); 
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(sel, sel);
			}
		}		
	}
	
	public boolean selectionContainsFVAColumns() {
		boolean contains = false;
		for (int i = 0; i < reactionsTable.getSelectedColumns().length; i++) {
			if (reactionsTable.getSelectedColumns()[i] == GraphicalInterfaceConstants.MIN_FLUX_COLUMN ||
					reactionsTable.getSelectedColumns()[i] == GraphicalInterfaceConstants.MAX_FLUX_COLUMN) {
				contains = true;
			}
		}
		return contains;
	}
	
	/**
	 * Returns list of columns to be pasted. Accounts for FVA columns possibly being hidden.
	 * @return
	 */
	public ArrayList<Integer> pasteColumnList() {
		ArrayList<Integer> pasteColumnList = new ArrayList<Integer>();
		if (reactionsTable.getSelectedColumns().length > 1) {
			for (int i = 0; i < reactionsTable.getSelectedColumns().length; i++) {
				if (LocalConfig.getInstance().fvaColumnsVisible) {
					pasteColumnList.add(reactionsTable.getSelectedColumns()[i]);
				} else {
					if (selectionContainsFVAColumns()) {
						if (reactionsTable.getSelectedColumns()[i] > GraphicalInterfaceConstants.FLUX_VALUE_COLUMN) {
							pasteColumnList.add(reactionsTable.getSelectedColumns()[i] + 2);
						} else {
							pasteColumnList.add(reactionsTable.getSelectedColumns()[i]);
						}
					} else {
						pasteColumnList.add(reactionsTable.getSelectedColumns()[i]);
					}
				}
			}
		} else {
			int startCol = reactionsTable.getSelectedColumns()[0];
			int numCols = numberOfClipboardColumns();
			boolean correctionNecessary = false;
			if (startCol <= GraphicalInterfaceConstants.FLUX_VALUE_COLUMN) {
				correctionNecessary = true;
			}
			for (int j = 0; j < numCols; j++) {
				if (LocalConfig.getInstance().fvaColumnsVisible) {
					pasteColumnList.add(startCol + j);
				} else {
					if (correctionNecessary) {
						if ((startCol + j) > GraphicalInterfaceConstants.FLUX_VALUE_COLUMN) {
							pasteColumnList.add(startCol + j + 2);
						} else {
							pasteColumnList.add(startCol + j);
						}
					} else {
						pasteColumnList.add(startCol + j);
					}
				}
			}
		}
		
		return pasteColumnList;
		
	}

	public void reactionsPaste() {
		int numCols = reactionsTable.getSelectedColumns().length;
		if (!LocalConfig.getInstance().fvaColumnsVisible) {
			if (selectionContainsFVAColumns()) {
				numCols -= 2;
			}
		}
		// Columns can not be repeatedly pasted (at least for now).
		// If number of columns = 1, entire length of clipboard is pasted,
		// else copy and paste selections column length compared
		if (numCols > 1 && numCols != numberOfClipboardColumns()) {
			JOptionPane.showMessageDialog(null,                
					GraphicalInterfaceConstants.PASTE_AREA_ERROR,                
					"Paste Error",                                
					JOptionPane.ERROR_MESSAGE);
		} else {
			showDuplicateReacPrompt = true;
			duplicateReacOK = true;
			pasteOutOfRangeErrorShown = false;
			// start at first item of pasteId's;
			int startIndex = 0;
		    // this prevents paste menu item and paste button from throwing null pointer exception if
			// pressed when no cell is selected. if this is done, nothing happens. may want to gray out
			if (reactionsTable.getSelectedRows().length > 0 && reactionsTable.getSelectedColumns().length > 0) {
				int startRow =reactionsTable.getSelectedRows()[0]; 
				int startCol =reactionsTable.getSelectedColumns()[0];
				int numSelectedRows = reactionsTable.getSelectedRowCount();
				//System.out.println(pasteColumnList());
				ArrayList<Integer> colList = pasteColumnList();
				String pasteString = ""; 
				try { 
					pasteString = (String)(Toolkit.getDefaultToolkit().getSystemClipboard().getContents(this).getTransferData(DataFlavor.stringFlavor)); 
				} catch (Exception e1) { 
					JOptionPane.showMessageDialog(null, "Invalid Paste Type", "Invalid Paste Type", JOptionPane.ERROR_MESSAGE);
					return; 
				}
				// copy model so old model can be restored if paste not valid
				DefaultTableModel oldReactionsModel = copyReactionsTableModel((DefaultTableModel) reactionsTable.getModel());			
				copyReactionsTableModels(oldReactionsModel);
				ReactionUndoItem undoItem = createReactionUndoItem("", "", startRow, startCol, 0, UndoConstants.PASTE, UndoConstants.REACTION_UNDO_ITEM_TYPE);
				setOldUsedMap(undoItem);
				undoItem.setTableCopyIndex(LocalConfig.getInstance().getNumReactionTablesCopied());
				ArrayList<String> pasteIds = new ArrayList<String>();
				// this allows selecting one row for paste even if many rows are copied
				// and pastes the whole clipboard contents
				if (numSelectedRows < numberOfClipboardRows()) {
					for (int y = 0; y < numberOfClipboardRows(); y++) {
						if (startRow + y < reactionsTable.getRowCount()) {
							int viewRow = reactionsTable.convertRowIndexToModel(startRow + y);
							pasteIds.add((String) reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTIONS_ID_COLUMN));
						}
					}
				} else {
					for (int y = 0; y < numSelectedRows; y++) {
						int viewRow = reactionsTable.convertRowIndexToModel(reactionsTable.getSelectedRows()[y]);
						pasteIds.add((String) reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTIONS_ID_COLUMN));						
					}
				}	
				undoItem.setPasteIds(pasteIds);
				// save sort column and order
				setReactionsOldSortColumnIndex(getReactionsSortColumnIndex());
				setReactionsOldSortOrder(getReactionsSortOrder());
				// unsort table to avoid sorting of pasted values that results in cells
				// updated after sorted column populated with incorrect values
				setReactionsSortDefault();
				DefaultTableModel model = (DefaultTableModel) reactionsTable.getModel();
				setUpReactionsTable(model);
				// after unsorting - get rows corresponding to ids. Using ids will not work if
				// any rows are deleted, and updating each cell by id will take too long -
				// O(n) for each cell updated vs O(1)
				ArrayList<Integer> pasteRows = new ArrayList<Integer>();
				Map<String, Object> reactionsIdRowMap = new HashMap<String, Object>();
				for (int i = 0; i < reactionsTable.getRowCount(); i++) {
					reactionsIdRowMap.put((String) reactionsTable.getModel().getValueAt(i, GraphicalInterfaceConstants.REACTIONS_ID_COLUMN), i);
				}
				for (int z = 0; z < pasteIds.size(); z++) {
					String row = (reactionsIdRowMap.get(pasteIds.get(z))).toString();
					int rowNum = Integer.valueOf(row);
					pasteRows.add(rowNum);
				}
				// if selected rows for paste > number of clipboard rows, need to paste
				// clipboard rows repeatedly
				if (numberOfClipboardRows() > 0 && numSelectedRows > numberOfClipboardRows()) {
					int quotient = numSelectedRows/numberOfClipboardRows();
					int remainder = numSelectedRows%numberOfClipboardRows();
					for (int q = 0; q < quotient; q++) {
						String[] lines = pasteString.split("\n");
						for (int i=0 ; i<numberOfClipboardRows(); i++) { 
							if (i < lines.length) {
								String[] cells = lines[i].split("\t");
								// fixes bug where if last cell in row is blank, will not
								// paste blank value over cell value if not blank
								if (q < quotient) {
									for (int j = 0; j < colList.size(); j++) {
//									for (int j=0 ; j < numberOfClipboardColumns(); j++) {
										if (startCol + cells.length > reactionsTable.getColumnCount()) {
											showPasteOutOfRangeError();				
										} else {
											if (j < cells.length) {
												updateReactionsCellIfPasteValid(cells[j], pasteRows.get(q*numberOfClipboardRows() + i), colList.get(j));
											} else {
												updateReactionsCellIfPasteValid("", pasteRows.get(q*numberOfClipboardRows() + i), colList.get(j));
											} 	
										}								
									}
								}									
							} else {
								if (q < quotient) {
									for (int j=0 ; j < numberOfClipboardColumns(); j++) {
										if (startCol + j > reactionsTable.getColumnCount()) {
											showPasteOutOfRangeError();			
										} else {
											updateReactionsCellIfPasteValid("", pasteRows.get(q*numberOfClipboardRows() + i), colList.get(j));
										}									
									}
								}									
							}							 
						}
						startRow += numberOfClipboardRows();
					}
					for (int m = 0; m < remainder; m++) {
						String[] lines = pasteString.split("\n");
						pasteReactionValues(m, lines, pasteRows, startIndex, startCol, colList);
					}
					// if selected rows for paste <= number of clipboard rows 	
				} else {
					String[] lines = pasteString.split("\n");
					if (startRow + lines.length > reactionsTable.getRowCount()) {
						showPasteOutOfRangeError();
					} else {
						for (int i=0 ; i<numberOfClipboardRows(); i++) { 
							pasteReactionValues(i, lines, pasteRows, startIndex, startCol, colList);
						} 
					}
				}
				// if paste not valid, set old model
				if (!validPaste) {
					restoreOldReactionsSort();
					JOptionPane.showMessageDialog(null,                
							getPasteError(),                
							"Paste Error",                                
							JOptionPane.ERROR_MESSAGE);
					setUpReactionsTable(oldReactionsModel);
					LocalConfig.getInstance().getReactionsTableModelMap().put(LocalConfig.getInstance().getModelName(), oldReactionsModel);
					deleteReactionsPasteUndoItem();
					validPaste = true;
				} else {
					enableSaveItems(true);
					LocalConfig.getInstance().reactionsTableChanged = true;
					DefaultTableModel newReactionsModel = copyReactionsTableModel((DefaultTableModel) reactionsTable.getModel());			
					copyReactionsTableModels(newReactionsModel);
					if (pasteIds.size() > 0) {
						undoItem.setId(Integer.valueOf(pasteIds.get(0)));
					}
					setNewUsedMap(undoItem);
					setUpReactionsUndo(undoItem);
					// reset sort column and order
					setReactionsSortColumnIndex(getReactionsOldSortColumnIndex());
					setReactionsSortOrder(getReactionsOldSortOrder());
					setUpReactionsTable(newReactionsModel);
					if (pasteIds.size() > 0) {
						scrollToLocation(reactionsTable, getRowFromReactionsId(Integer.valueOf(pasteIds.get(0))), startCol);
					}
//					System.out.println("paste " + LocalConfig.getInstance().getReactionEquationMap());
//					System.out.println("paste id " + LocalConfig.getInstance().getMetaboliteAbbreviationIdMap());
//					System.out.println("paste used " + LocalConfig.getInstance().getMetaboliteUsedMap());
				}
			} 	
		}
	}

	// i is the loop counter index
	public void pasteReactionValues(int i, String[] lines, ArrayList<Integer> pasteRows, int startIndex, int startCol, ArrayList<Integer> colList) {
		if (i < lines.length) {
			String[] cells = lines[i].split("\t"); 
			if (startCol + cells.length > reactionsTable.getColumnCount()) {
				showPasteOutOfRangeError();			
			} else {
				for (int j = 0; j < colList.size(); j++) {
//				for (int j=0 ; j < numberOfClipboardColumns(); j++) { 
					if (j < cells.length) {
						updateReactionsCellIfPasteValid(cells[j], pasteRows.get(startIndex + i), colList.get(j));
					} else {
						updateReactionsCellIfPasteValid("", pasteRows.get(startIndex + i), colList.get(j));
					} 
				}
			}
		} else {
			for (int j = 0; j < colList.size(); j++) {
			//for (int j=0 ; j < numberOfClipboardColumns(); j++) { 
				updateReactionsCellIfPasteValid("", pasteRows.get(startIndex + i), colList.get(j));
			}
		}
	}
	
	// used for invalid paste, invalid clear, and invalid replace all
	public void deleteReactionsPasteUndoItem() {
		int numCopied = LocalConfig.getInstance().getNumReactionTablesCopied();	
		if (LocalConfig.getInstance().getReactionsUndoTableModelMap().containsKey(Integer.toString(numCopied + 1))) {
			LocalConfig.getInstance().getReactionsUndoTableModelMap().remove(Integer.toString(numCopied + 1));
		}
		numCopied -= 1;
		if (LocalConfig.getInstance().getReactionsUndoTableModelMap().containsKey(Integer.toString(numCopied + 1))) {
			LocalConfig.getInstance().getReactionsUndoTableModelMap().remove(Integer.toString(numCopied + 1));
		}
		LocalConfig.getInstance().setNumReactionTablesCopied(numCopied);
	}

	public void restoreOldReactionsSort() {
		setReactionsSortColumnIndex(getReactionsOldSortColumnIndex());
		setReactionsSortOrder(getReactionsOldSortOrder());
		DefaultTableModel model = (DefaultTableModel) reactionsTable.getModel();
		setUpReactionsTable(model);
	}
	
	public void updateReactionsCellIfPasteValid(String value, int row, int col) {
		EntryValidator validator = new EntryValidator();
		Utilities u = new Utilities();
		int id = Integer.valueOf((String) reactionsTable.getModel().getValueAt(row, GraphicalInterfaceConstants.REACTIONS_ID_COLUMN));		
		String reacAbbrev = (String)reactionsTable.getModel().getValueAt(row, GraphicalInterfaceConstants.REACTION_ABBREVIATION_COLUMN);
		if (col == GraphicalInterfaceConstants.REACTION_ABBREVIATION_COLUMN) {
			if (value != null && value.trim().length() > 0 && LocalConfig.getInstance().getReactionAbbreviationIdMap().containsKey(value)) {
				if (showDuplicateReacPrompt) {
					restoreOldReactionsSort();
					Object[] options = {"    Yes    ", "    No    ",};
					int choice = JOptionPane.showOptionDialog(null, 
							GraphicalInterfaceConstants.DUPLICATE_REACTION_PASTE_MESSAGE, 
							GraphicalInterfaceConstants.DUPLICATE_REACTION_TITLE, 
							JOptionPane.YES_NO_OPTION, 
							JOptionPane.QUESTION_MESSAGE, 
							null, options, options[0]);
					if (choice == JOptionPane.YES_OPTION) {	
						value = value + u.duplicateSuffix(value, LocalConfig.getInstance().getReactionAbbreviationIdMap());
						updateReactionsCellById(value, id, col);
						LocalConfig.getInstance().getReactionAbbreviationIdMap().remove(reacAbbrev);
						LocalConfig.getInstance().getReactionAbbreviationIdMap().put(value, id);
						showDuplicateReacPrompt = false;
					}
					if (choice == JOptionPane.NO_OPTION) {
						showDuplicateReacPrompt = false;
						duplicateReacOK = false;
						reactionsTable.setValueAt(reacAbbrev, row, col);
					}
				} else {
					if (duplicateReacOK) {
						value = value + u.duplicateSuffix(value, LocalConfig.getInstance().getReactionAbbreviationIdMap());
						updateReactionsCellById(value, id, col);
						//reactionsTable.setValueAt(value, viewRow, col);
						if (LocalConfig.getInstance().getReactionAbbreviationIdMap().containsKey(reacAbbrev)) {
							LocalConfig.getInstance().getReactionAbbreviationIdMap().remove(reacAbbrev);
						}
						LocalConfig.getInstance().getReactionAbbreviationIdMap().put(value, id);
					}						
				}
			} else {
				reactionsTable.setValueAt(value, row, col);
				if (LocalConfig.getInstance().getReactionAbbreviationIdMap().containsKey(reacAbbrev)) {
					LocalConfig.getInstance().getReactionAbbreviationIdMap().remove(reacAbbrev);
				}
				if (value != null && value.trim().length() > 0) {
					LocalConfig.getInstance().getReactionAbbreviationIdMap().put(value, id);
				}
			}
		} else if (col == GraphicalInterfaceConstants.FLUX_VALUE_COLUMN ||
				col == GraphicalInterfaceConstants.MIN_FLUX_COLUMN ||
				col == GraphicalInterfaceConstants.MAX_FLUX_COLUMN ||
				col == GraphicalInterfaceConstants.LOWER_BOUND_COLUMN ||
				col == GraphicalInterfaceConstants.UPPER_BOUND_COLUMN ||
				col == GraphicalInterfaceConstants.BIOLOGICAL_OBJECTIVE_COLUMN ||
				col == GraphicalInterfaceConstants.SYNTHETIC_OBJECTIVE_COLUMN) {
			if (isReactionsEntryValid(row, col, value)) {
				if (validator.isInvalidInfinityEntry(value)) {
					value = GraphicalInterfaceConstants.VALID_INFINITY_ENTRY;
				} else if (validator.isInvalidNegativeInfinityEntry(value)) {
					value = "-" + GraphicalInterfaceConstants.VALID_INFINITY_ENTRY;
				} 
				reactionsTable.setValueAt(value, row, col);	
				formulaBar.setText("");
			} else {
				validPaste = false;
			}
		} else if (isReactionsEntryValid(row, col, value)) {
			if (col < reactionsTable.getColumnCount()) {
				reactionsTable.setValueAt(value, row, col);
			} 
			formulaBar.setText("");
		} else {
			validPaste = false;
		}	
		//System.out.println(LocalConfig.getInstance().getReactionAbbreviationIdMap());
	}

	public boolean isReactionsEntryValid(int row, int columnIndex, String value) {
		EntryValidator validator = new EntryValidator();
		int viewRow = reactionsTable.convertRowIndexToModel(row);
		int id = Integer.valueOf((String) reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTIONS_ID_COLUMN));
		if (columnIndex == GraphicalInterfaceConstants.FLUX_VALUE_COLUMN || 
				columnIndex == GraphicalInterfaceConstants.MIN_FLUX_COLUMN ||
				columnIndex == GraphicalInterfaceConstants.MAX_FLUX_COLUMN ||
				columnIndex == GraphicalInterfaceConstants.LOWER_BOUND_COLUMN ||
				columnIndex == GraphicalInterfaceConstants.UPPER_BOUND_COLUMN ||
				columnIndex == GraphicalInterfaceConstants.BIOLOGICAL_OBJECTIVE_COLUMN ||
				columnIndex == GraphicalInterfaceConstants.SYNTHETIC_OBJECTIVE_COLUMN) {
			if (validator.isInvalidInfinityEntry(value)) {
				value = GraphicalInterfaceConstants.VALID_INFINITY_ENTRY;
			} else if (validator.isInvalidNegativeInfinityEntry(value)) {
				value = "-" + GraphicalInterfaceConstants.VALID_INFINITY_ENTRY;
			}
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
				return false;
			}
		} else if (columnIndex == GraphicalInterfaceConstants.KO_COLUMN) {
			if (value.compareTo("true") == 0 || value.compareTo("false") == 0) {
				return true;
			} else {
				setPasteError(GraphicalInterfaceConstants.INVALID_PASTE_BOOLEAN_VALUE);
				setReplaceAllError(GraphicalInterfaceConstants.INVALID_REPLACE_ALL_BOOLEAN_VALUE);
				validPaste = false;
				return false;
			}
		} else if (columnIndex == GraphicalInterfaceConstants.REVERSIBLE_COLUMN) {
			// TODO: get equation object after setting value and update to this value
			if (value.compareTo("true") == 0 || value.compareTo("false") == 0) {
				if (LocalConfig.getInstance().getReactionEquationMap().get(id) != null) {
					((SBMLReactionEquation) LocalConfig.getInstance().getReactionEquationMap().get(id)).setReversible(value);
					((SBMLReactionEquation) LocalConfig.getInstance().getReactionEquationMap().get(id)).writeReactionEquation();
					reactionsTable.setValueAt(((SBMLReactionEquation) LocalConfig.getInstance().getReactionEquationMap().get(id)).equationAbbreviations, row, GraphicalInterfaceConstants.REACTION_EQUN_ABBR_COLUMN);
					reactionsTable.setValueAt(((SBMLReactionEquation) LocalConfig.getInstance().getReactionEquationMap().get(id)).equationNames, row, GraphicalInterfaceConstants.REACTION_EQUN_NAMES_COLUMN);
				}	
				if (value.compareTo("false") == 0) {
					// do we need to show a prompt?
					reactionsTable.setValueAt("0.0", row, GraphicalInterfaceConstants.LOWER_BOUND_COLUMN);
				}
				return true;
			} else {
				setPasteError(GraphicalInterfaceConstants.INVALID_PASTE_BOOLEAN_VALUE);
				setReplaceAllError(GraphicalInterfaceConstants.INVALID_REPLACE_ALL_BOOLEAN_VALUE);
				validPaste = false;
				return false;
			}
		} else if (columnIndex == GraphicalInterfaceConstants.REACTION_EQUN_ABBR_COLUMN) {
			String oldEqun = (String) reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTION_EQUN_ABBR_COLUMN);
			ReactionParser parser = new ReactionParser();
			if (!parser.isValid(value)) {
				if (value.trim().length() > 0) {
					setPasteError("Invalid Reaction Format");
					setReplaceAllError("Invalid Reaction Format");
					validPaste = false;
					return false;
				} else {
					updateReactionEquation(viewRow, id, oldEqun, value);
					LocalConfig.getInstance().getReactionEquationMap().remove(id);
					return true;
				}					
			} else {
				updateReactionEquation(viewRow, id, oldEqun, value);
				return true;
			}				
		} 
		return true;

	}

	public void reactionsClear() {
		// this prevents clear contents menu item from throwing null pointer exception if
		// pressed when no cell is selected. if this is done, nothing happens. may want to gray out
		if (reactionsTable.getSelectedRows().length > 0 && reactionsTable.getSelectedColumns().length > 0) {
			int startRow=(reactionsTable.getSelectedRows())[0]; 
			int startCol=(reactionsTable.getSelectedColumns())[0];
			int id = Integer.valueOf((String) reactionsTable.getModel().getValueAt(startRow, GraphicalInterfaceConstants.REACTIONS_ID_COLUMN));
			// copy model for undo 
			DefaultTableModel oldReactionsModel = copyReactionsTableModel((DefaultTableModel) reactionsTable.getModel());	
			copyReactionsTableModels(oldReactionsModel);
			ReactionUndoItem undoItem = createReactionUndoItem("", "", startRow, startCol, id, UndoConstants.CLEAR_CONTENTS, UndoConstants.REACTION_UNDO_ITEM_TYPE);
			setOldUsedMap(undoItem);
			undoItem.setTableCopyIndex(LocalConfig.getInstance().getNumReactionTablesCopied());		
			boolean valid = true;
			ArrayList<Integer> deleteIds = new ArrayList<Integer>();
			// check if columns that require values will be cleared
			for(int j=0; j < reactionsTable.getSelectedColumns().length ;j++) { 
				if (startCol + j == GraphicalInterfaceConstants.KO_COLUMN || startCol + j == GraphicalInterfaceConstants.FLUX_VALUE_COLUMN || 
						startCol + j == GraphicalInterfaceConstants.MIN_FLUX_COLUMN || startCol + j == GraphicalInterfaceConstants.MAX_FLUX_COLUMN ||
						startCol + j == GraphicalInterfaceConstants.LOWER_BOUND_COLUMN || startCol + j == GraphicalInterfaceConstants.UPPER_BOUND_COLUMN
						|| startCol + j == GraphicalInterfaceConstants.BIOLOGICAL_OBJECTIVE_COLUMN || startCol + j == GraphicalInterfaceConstants.REVERSIBLE_COLUMN ||
						startCol + j == GraphicalInterfaceConstants.SYNTHETIC_OBJECTIVE_COLUMN || startCol + j == GraphicalInterfaceConstants.REACTION_EQUN_NAMES_COLUMN) {
					valid = false;
				}
			}
			if (valid) {
				for(int i=0; i < reactionsTable.getSelectedRows().length ;i++) { 
					for(int j=0; j < reactionsTable.getSelectedColumns().length ;j++) {
						updateReactionsCellIfPasteValid("", startRow+i, startCol+j);
						int reacId = Integer.valueOf((String) reactionsTable.getModel().getValueAt(startRow+i, GraphicalInterfaceConstants.REACTIONS_ID_COLUMN));
						deleteIds.add(reacId);
					} 
				}
				DefaultTableModel newReactionsModel = copyReactionsTableModel((DefaultTableModel) reactionsTable.getModel());			
				copyReactionsTableModels(newReactionsModel);  
				setNewUsedMap(undoItem);
				undoItem.setDeleteIds(deleteIds);
				setUpReactionsUndo(undoItem);
			} else {
				JOptionPane.showMessageDialog(null,                
						GraphicalInterfaceConstants.CLEAR_ERROR_MESSAGE,                
						"Clear Error",                                
						JOptionPane.ERROR_MESSAGE);
				deleteReactionsPasteUndoItem();
			}
		}
//		System.out.println("clear " + LocalConfig.getInstance().getReactionEquationMap());
//		System.out.println("clear id " + LocalConfig.getInstance().getMetaboliteAbbreviationIdMap());
//		System.out.println("clear used " + LocalConfig.getInstance().getMetaboliteUsedMap());
	}
	
	/**************************************************************************/
	//end reactionsTable context menu methods
	/**************************************************************************/

	/****************************************************************************/
	// Metabolites Table context menus
	/****************************************************************************/

	// from http://docs.oracle.com/javase/tutorial/uiswing/components/menu.html
	public class MetabolitesPopupListener extends MouseAdapter {

		public void maybeShowPopup(MouseEvent e) {
			if (e.isPopupTrigger() && metabolitesTable.isEnabled()) {
				if (!analysisRunning) {
					Point p = new Point(e.getX(), e.getY());
					int col = metabolitesTable.columnAtPoint(p);
					int row = metabolitesTable.rowAtPoint(p);

					// translate table index to model index
					//int mcol = metabolitesTable.getColumn(metabolitesTable.getColumnName(col)).getModelIndex();

					if (row >= 0 && row < metabolitesTable.getRowCount()) {
						cancelCellEditing(); 
						maybeChangeSelection(row, col, metabolitesTable);
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

	public void showRenameMessage(String toBeRenamed) {
		Object[] options = {"    Yes    ", "    No    ",};
		int choice = JOptionPane.showOptionDialog(null, 
				GraphicalInterfaceConstants.PARTICIPATING_METAB_RENAME_MESSAGE_PREFIX + 
				toBeRenamed + GraphicalInterfaceConstants.PARTICIPATING_METAB_RENAME_MESSAGE_SUFFIX, 
				GraphicalInterfaceConstants.PARTICIPATING_METAB_RENAME_TITLE, 
				JOptionPane.YES_NO_OPTION, 
				JOptionPane.QUESTION_MESSAGE, 
				null, options, options[0]);
		if (choice == JOptionPane.YES_OPTION) {
			renameMetabolite = true;
			if (showMetaboliteRenameInterface) {
				showMetaboliteRenameInterface();
			}			
		}
		if (choice == JOptionPane.NO_OPTION) {

		}
	}

	public void showMetaboliteRenameInterface() {
		MetaboliteRenameInterface metaboliteRenameInterface = new MetaboliteRenameInterface();
		setMetaboliteRenameInterface(metaboliteRenameInterface);
		metaboliteRenameInterface.setTitle(GraphicalInterfaceConstants.RENAME_METABOLITE_INTERFACE_TITLE);
		metaboliteRenameInterface.setIconImages(icons);
		metaboliteRenameInterface.setSize(350, 160);
		metaboliteRenameInterface.setResizable(false);
		metaboliteRenameInterface.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		metaboliteRenameInterface.setLocationRelativeTo(null);
		metaboliteRenameInterface.setVisible(true);
	}

	public void updateMetaboliteMaps(int id, String metabAbbrev, String metabName, String newName, int columnIndex) {
		if (columnIndex == GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN ) {
//			System.out.println("bef" + LocalConfig.getInstance().getMetaboliteAbbreviationIdMap());
//			System.out.println("bef" + LocalConfig.getInstance().getMetaboliteIdNameMap());
//			System.out.println("bef" + LocalConfig.getInstance().getMetaboliteUsedMap());
			LocalConfig.getInstance().getMetaboliteAbbreviationIdMap().remove(metabAbbrev);	
			LocalConfig.getInstance().getMetaboliteAbbreviationIdMap().put(newName, id);
			if (LocalConfig.getInstance().getMetaboliteUsedMap().containsKey(metabAbbrev)) {
				Object value = LocalConfig.getInstance().getMetaboliteUsedMap().get(metabAbbrev);
				LocalConfig.getInstance().getMetaboliteUsedMap().remove(metabAbbrev);
				LocalConfig.getInstance().getMetaboliteUsedMap().put(newName, value);
			}			
//			System.out.println("aft" + LocalConfig.getInstance().getMetaboliteAbbreviationIdMap());
//			System.out.println("aft" + LocalConfig.getInstance().getMetaboliteIdNameMap());
//			System.out.println("aft" + LocalConfig.getInstance().getMetaboliteUsedMap());
		} else if (columnIndex == GraphicalInterfaceConstants.METABOLITE_NAME_COLUMN) {
			LocalConfig.getInstance().getMetaboliteIdNameMap().remove(id);	
			LocalConfig.getInstance().getMetaboliteIdNameMap().put(id, metabName);
		}
	}

	/**
	 * Updates reaction equations if metabolite abbreviation or metabolite name changed.
	 * @param id
	 * @param metabAbbrev
	 * @param metabName
	 * @param newName
	 * @param columnIndex
	 */
	public void rewriteReactions(int id, String metabAbbrev, String metabName, String newName, int columnIndex) {
		MetaboliteFactory aFactory = new MetaboliteFactory("SBML");
		ArrayList<Integer> participatingReactions = aFactory.participatingReactions(metabAbbrev);			
		// rewrite reactions 
		for (int i = 0; i < participatingReactions.size(); i++) {
			SBMLReactionEquation equn = (SBMLReactionEquation) LocalConfig.getInstance().getReactionEquationMap().get(participatingReactions.get(i));
			if (columnIndex == GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN) {
				for (int j = 0; j < equn.getReactants().size(); j++) {
					if (equn.getReactants().get(j).getMetaboliteAbbreviation().equals(metabAbbrev)) {
						equn.getReactants().get(j).setMetaboliteAbbreviation(newName);
					}
				}
				for (int j = 0; j < equn.getProducts().size(); j++) {
					if (equn.getProducts().get(j).getMetaboliteAbbreviation().equals(metabAbbrev)) {
						equn.getProducts().get(j).setMetaboliteAbbreviation(newName);
					}
				}
				equn.writeReactionEquation();
				updateReactionsCellById(equn.equationAbbreviations, participatingReactions.get(i), GraphicalInterfaceConstants.REACTION_EQUN_ABBR_COLUMN);
			} else if (columnIndex == GraphicalInterfaceConstants.METABOLITE_NAME_COLUMN) {
				//if (metabName != null && metabName.length() > 0) {
					for (int j = 0; j < equn.getReactants().size(); j++) {
						if (equn.getReactants().get(j).getMetaboliteAbbreviation().equals(metabAbbrev)) {
							equn.getReactants().get(j).setMetaboliteName(newName);
						}
					}
					for (int j = 0; j < equn.getProducts().size(); j++) {
						if (equn.getProducts().get(j).getMetaboliteAbbreviation().equals(metabAbbrev)) {
							equn.getProducts().get(j).setMetaboliteName(newName);
						}
					}
//				} else {
//					
//				}
				 
				equn.writeReactionEquation();
				updateReactionsCellById(equn.equationNames, participatingReactions.get(i), GraphicalInterfaceConstants.REACTION_EQUN_NAMES_COLUMN);
			}								
		}
	}
	
	/**
	 * Updates reaction equations with full names when compartment is modified
	 * @param id
	 * @param metabAbbrev
	 * @param compartment
	 */
	public void rewriteReactionEquationNames(int id, String metabAbbrev, String compartment) {
		MetaboliteFactory aFactory = new MetaboliteFactory("SBML");
		ArrayList<Integer> participatingReactions = aFactory.participatingReactions(metabAbbrev);			
		// rewrite reactions 
		for (int i = 0; i < participatingReactions.size(); i++) {
			SBMLReactionEquation equn = (SBMLReactionEquation) LocalConfig.getInstance().getReactionEquationMap().get(participatingReactions.get(i));
			for (int j = 0; j < equn.getReactants().size(); j++) {
				if (equn.getReactants().get(j).getMetaboliteAbbreviation().equals(metabAbbrev)) {
					equn.getReactants().get(j).setCompartment(compartment);
				}
			}
			for (int j = 0; j < equn.getProducts().size(); j++) {
				if (equn.getProducts().get(j).getMetaboliteAbbreviation().equals(metabAbbrev)) {
					equn.getProducts().get(j).setCompartment(compartment);
				}
			}
			equn.writeReactionEquation();
			updateReactionsCellById(equn.equationNames, participatingReactions.get(i), GraphicalInterfaceConstants.REACTION_EQUN_NAMES_COLUMN);								
		}
	}

	private JPopupMenu createMetaboliteAbbreviationContextMenu(final int rowIndex,
			final int columnIndex) {
		JPopupMenu contextMenu = createMetabolitesContextMenu(rowIndex, columnIndex);
		contextMenu.addSeparator();

		final int viewRow = metabolitesTable.convertRowIndexToModel(rowIndex);
		final int id = Integer.valueOf((String) metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_ID_COLUMN));		
		final String metabAbbrev = (String) metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN);	
		final String metabName = (String) metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_NAME_COLUMN);

		JMenuItem renameMenu = new JMenuItem("Rename");
		if (isRoot) {
			renameMenu.setEnabled(true);
		} else {
			renameMenu.setEnabled(false);
		}
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
				if (LocalConfig.getInstance().getMetaboliteUsedMap().containsKey(metabAbbrev)) {
					String toBeRenamed = "";
					if (columnIndex == GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN) {
						toBeRenamed = metabAbbrev;
					} else if (columnIndex == GraphicalInterfaceConstants.METABOLITE_NAME_COLUMN) {
						toBeRenamed = metabName;
					}
					showRenameMessage(toBeRenamed);
					// not necessary to use the interface to rename an unused or duplicate
					// metabolite but for consistency, when rename item clicked, interface
					// is displayed and functional. another option would be to disable the
					// menu item if these conditions are true but that may be confusing
				} else {
					showMetaboliteRenameInterface();
				}
			}
		});

		ActionListener metabRenameOKButtonActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent prodActionEvent) {
				String newName = "";
				metaboliteRenameInterface.setNewName(MetaboliteRenameInterface.textField.getText());
				if (metaboliteRenameInterface.getNewName() != null && metaboliteRenameInterface.getNewName().length() > 0) {
					newName = metaboliteRenameInterface.getNewName();
					// check if duplicate metabolite
					if (LocalConfig.getInstance().getMetaboliteAbbreviationIdMap().containsKey(newName) && 
							columnIndex == GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN) {							
						if (!duplicatePromptShown) {
							JOptionPane.showMessageDialog(null,                
									"Duplicate Metabolite.",                
									"Duplicate Metabolite",                                
									JOptionPane.ERROR_MESSAGE);
						}
						duplicatePromptShown = true;
					} else {
						Object idValue = LocalConfig.getInstance().getMetaboliteAbbreviationIdMap().get(metabAbbrev);
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
						if (renameMetabolite) {
							rewriteReactions(id, metabAbbrev, metabName, newName, columnIndex);
							updateMetaboliteMaps(id, metabAbbrev, metabName, newName, columnIndex);
							if (columnIndex == GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN) {
								metabolitesTable.getModel().setValueAt(newName, viewRow, GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN);
							} else if (columnIndex == GraphicalInterfaceConstants.METABOLITE_NAME_COLUMN) {
								metabolitesTable.getModel().setValueAt(newName, viewRow, GraphicalInterfaceConstants.METABOLITE_NAME_COLUMN);
							}
						}
						MetaboliteRenameInterface.textField.setText("");
						metaboliteRenameInterface.setVisible(false);
						metaboliteRenameInterface.dispose();
						if (undoItem != null) {
							setUndoNewCollections(undoItem);
							setUpMetabolitesUndo(undoItem);
						}	
					}						
				}
			}
		};
		
		MetaboliteRenameInterface.okButton.addActionListener(metabRenameOKButtonActionListener);

		contextMenu.addSeparator();

		final JMenuItem participatingReactionsMenu = new JMenuItem("Highlight Participating Reactions");
		if (columnIndex == GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN) {
			if (metabAbbrev == null || !LocalConfig.getInstance().getMetaboliteUsedMap().containsKey(metabAbbrev) || highlightParticipatingRxns) {
				participatingReactionsMenu.setEnabled(false);
			}
		} else if (columnIndex == GraphicalInterfaceConstants.METABOLITE_NAME_COLUMN) {
			if (metabName == null || !LocalConfig.getInstance().getMetaboliteUsedMap().containsKey(metabAbbrev) || highlightParticipatingRxns) {
				participatingReactionsMenu.setEnabled(false);
			}
		}		
		participatingReactionsMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				highlightParticipatingRxns = true;
				unhighlightParticipatingReactionsMenu.setEnabled(true);
				MetaboliteFactory aFactory = new MetaboliteFactory("SBML");	
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
						Integer cellValue = Integer.valueOf((String) reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTIONS_ID_COLUMN));
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

		if (metabAbbrev == null || !LocalConfig.getInstance().getMetaboliteUsedMap().containsKey(metabAbbrev) || !highlightParticipatingRxns) {
			unhighlightParticipatingReactionsMenu.setEnabled(false);
		}
		unhighlightParticipatingReactionsMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				highlightParticipatingRxns = false;
			}
		});
		contextMenu.add(unhighlightParticipatingReactionsMenu);
		
		contextMenu.addSeparator();

		final JMenuItem ignoreSuspiciousMenu = new JMenuItem("Ignore Suspicious Metabolite");
		
		if (LocalConfig.getInstance().getSuspiciousMetabolites().contains(id)) {
			ignoreSuspiciousMenu.setEnabled(true);
		} else {
			ignoreSuspiciousMenu.setEnabled(false);
		}
		
		ignoreSuspiciousMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				LocalConfig.getInstance().getSuspiciousMetabolites().remove(LocalConfig.getInstance().getSuspiciousMetabolites().indexOf(id));
				maybeDisplaySuspiciousMetabMessage(statusBarRow());
			}
		});
		
		contextMenu.add(ignoreSuspiciousMenu);

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
				"Select Table Cells Only");

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
				reactionsTable.clearSelection();
				metabolitesTable.selectAll();			
				selectMetabolitesRows();
			}
		});
		selectCellsOnly.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
		selectCellsOnly.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (selectCellsOnly.isSelected()) {
					includeMtbColumnNames = false;
				} else {
					includeMtbColumnNames = true;
				}				
				metabolitesTable.selectAll();			
				selectMetabolitesRows();
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
				metabolitesCopy();
			}
		});
		contextMenu.add(copyMenu);

		JMenuItem pasteMenu = new JMenuItem("Paste");
		pasteMenu.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_V, ActionEvent.CTRL_MASK));
		if (isClipboardContainingText(this)
				&& metabolitesTable.getModel().isCellEditable(rowIndex, columnIndex)
				&& isRoot) {
			pasteMenu.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					metabolitesPaste();
				}
			});
		} else {
			pasteMenu.setEnabled(false);
		}
		contextMenu.add(pasteMenu);

		JMenuItem clearMenu = new JMenuItem("Clear Contents");
		if (isRoot) {
			if (columnIndex == GraphicalInterfaceConstants.BOUNDARY_COLUMN) {
				clearMenu.setEnabled(false);
			} else {
				clearMenu.setEnabled(true);
			}			
		} else {
			clearMenu.setEnabled(false);
		}
		clearMenu.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_E, ActionEvent.CTRL_MASK));
		clearMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				metabolitesClear();
			}
		});
		contextMenu.add(clearMenu);

		contextMenu.addSeparator();

		if (isRoot) {
			deleteMetaboliteRowMenuItem.setEnabled(true);
		} else {
			deleteMetaboliteRowMenuItem.setEnabled(false);
		}
		if (metabolitesTable.getSelectedRow() > -1) {
			createUnusedMetabolitesList();
			int viewRow = metabolitesTable.convertRowIndexToModel(metabolitesTable.getSelectedRow());
			Integer.valueOf((String) metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_ID_COLUMN));			
			String metabAbbrev = (String) metabolitesTable.getModel().getValueAt(viewRow, 1);
			if (LocalConfig.getInstance().getMetaboliteUsedMap().containsKey(metabAbbrev)) {
				deleteMetaboliteRowMenuItem.setEnabled(false);
			} else {
				if (isRoot) {
					deleteMetaboliteRowMenuItem.setEnabled(true);
					deleteMetaboliteRowMenuItem.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent ae) {
							boolean participant = false;
							boolean errorShown = false;
							if (metabolitesTable.getModel().getRowCount() > 0 && metabolitesTable.getSelectedRow() != -1 )
							{
								// copy old model for undo/redo
								DefaultTableModel oldMetabolitesModel = copyMetabolitesTableModel((DefaultTableModel) metabolitesTable.getModel());	
								copyMetabolitesTableModels(oldMetabolitesModel); 
								DefaultTableModel model = (DefaultTableModel) metabolitesTable.getModel();
								int startRow=metabolitesTable.getSelectedRows()[0]; 
								int rowIndexEnd = metabolitesTable.getSelectionModel().getMaxSelectionIndex();
								int firstViewRow = metabolitesTable.convertRowIndexToModel(startRow);
								int firstId = (Integer.valueOf((String) metabolitesTable.getModel().getValueAt(firstViewRow, GraphicalInterfaceConstants.METABOLITE_ID_COLUMN)));
								MetaboliteUndoItem undoItem = createMetaboliteUndoItem("", "", startRow, 0, firstId, UndoConstants.DELETE_ROW, UndoConstants.METABOLITE_UNDO_ITEM_TYPE);
								setUndoOldCollections(undoItem);
								for (int r = rowIndexEnd; r >= startRow; r--) {
									int viewRow = metabolitesTable.convertRowIndexToModel(r);
									int id = (Integer.valueOf((String) metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_ID_COLUMN)));
									if (LocalConfig.getInstance().getSuspiciousMetabolites().contains(id)) {
										LocalConfig.getInstance().getSuspiciousMetabolites().remove(LocalConfig.getInstance().getSuspiciousMetabolites().indexOf(id));
									}
									String key = (String) metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN);
									// check if id is used	
									if (LocalConfig.getInstance().getMetaboliteUsedMap().containsKey(key)) {
										participant = true;
									} else {
										model.removeRow(viewRow);
										if (LocalConfig.getInstance().getMetaboliteAbbreviationIdMap().containsKey(key)) {
											LocalConfig.getInstance().getMetaboliteAbbreviationIdMap().remove(key);
										}
									}									
								}
								undoItem.setTableCopyIndex(LocalConfig.getInstance().getNumMetabolitesTableCopied());
								copyMetabolitesTableModels(model); 
								setUndoNewCollections(undoItem);
								setUpMetabolitesUndo(undoItem);
								LocalConfig.getInstance().getMetabolitesTableModelMap().put(LocalConfig.getInstance().getModelName(), model);
							}
							formulaBar.setText("");
							if (participant && !errorShown) {
								JOptionPane.showMessageDialog(null,                
										GraphicalInterfaceConstants.PARTICIPATING_METAB_ERROR_MESSAGE,
										GraphicalInterfaceConstants.PARTICIPATING_METAB_ERROR_TITLE,                                
										JOptionPane.ERROR_MESSAGE);
								errorShown = true;
							}
							int numMetabRows = metabolitesTable.getRowCount();
							int maxId = Integer.valueOf((String) metabolitesTable.getModel().getValueAt(numMetabRows - 1, GraphicalInterfaceConstants.METABOLITE_ID_COLUMN));
							LocalConfig.getInstance().setMaxMetabolite(numMetabRows);
							LocalConfig.getInstance().setMaxMetaboliteId(maxId + 1);
						}
					});
				} else {
					deleteMetaboliteRowMenuItem.setEnabled(false);
				}
			}
		}

		contextMenu.add(deleteMetaboliteRowMenuItem);	

		return contextMenu;
	}

	/****************************************************************************/
	// end Metabolites Table context menus
	/****************************************************************************/

	/**************************************************************************/
	//metabolitesTable context menu methods
	/**************************************************************************/

	public void selectMetabolitesRows() {
		setClipboardContents("");

		StringBuffer sbf=new StringBuffer();
		int numrows = metabolitesTable.getSelectedRowCount(); 
		int[] rowsselected=metabolitesTable.getSelectedRows();  

		metabolitesTable.changeSelection(rowsselected[0], 1, false, false);
		metabolitesTable.changeSelection(rowsselected[numrows - 1], metabolitesTable.getColumnCount(), false, true);
		metabolitesTable.scrollColumnToVisible(1);

		if (includeMtbColumnNames == true) {
			//add column names to clipboard
			for (int c = 1; c < GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES.length; c++) {
				sbf.append(GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES[c]);
				if (c < GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES.length - 1) {
					sbf.append("\t"); 
				}			
			}
			for (int r = 0; r < LocalConfig.getInstance().getMetabolitesMetaColumnNames().size(); r++) {
				sbf.append("\t");
				sbf.append(LocalConfig.getInstance().getMetabolitesMetaColumnNames().get(r));					
			}
			sbf.append("\n");
		}

		for (int i = 0; i < numrows; i++) {
			//starts at 1 to avoid reading hidden db id column 
			for (int j = 1; j < metabolitesTable.getColumnCount(); j++) 
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
		int numCols=metabolitesTable.getSelectedColumnCount(); 
		int numRows=metabolitesTable.getSelectedRowCount(); 
		int[] rowsSelected=metabolitesTable.getSelectedRows(); 
		int[] colsSelected=metabolitesTable.getSelectedColumns(); 
		try {
			if (numRows!=rowsSelected[rowsSelected.length-1]-rowsSelected[0]+1 || numRows!=rowsSelected.length || 
					numCols!=colsSelected[colsSelected.length-1]-colsSelected[0]+1 || numCols!=colsSelected.length) {

				JOptionPane.showMessageDialog(null, "Invalid Copy Selection", "Invalid Copy Selection", JOptionPane.ERROR_MESSAGE);
				return; 
			} 
		} catch (Throwable t) {
			
		}
		
		// Clipboard already contains correct values for select all and include column names, do not change 
		if (rowsSelected.length == metabolitesTable.getRowCount() && colsSelected.length == metabolitesTable.getColumnCount() && includeRxnColumnNames) {

		} else {
			if (getSelectionMode() == 1) {
				StringBuffer excelStr=new StringBuffer(); 
				for (int i = 0; i < metabolitesTable.getRowCount(); i++) { 
					for (int j=0; j<numCols; j++) { 
						try {
							excelStr.append(escape(metabolitesTable.getValueAt(i, colsSelected[j]))); 
						} catch (Throwable t) {

						}
						if (j<numCols-1) {
							//System.out.println("t");
							excelStr.append("\t"); 
						} 
					}
					excelStr.append("\n");
				}
				StringSelection sel  = new StringSelection(excelStr.toString()); 
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(sel, sel);
			} else if (getSelectionMode() == 2) {
				includeRxnColumnNames = false;				
				selectMetabolitesRows();
			} else {
				StringBuffer excelStr=new StringBuffer(); 
				for (int i=0; i<numRows; i++) { 
					for (int j=0; j<numCols; j++) { 
						try {
							excelStr.append(escape(metabolitesTable.getValueAt(rowsSelected[i], colsSelected[j]))); 
						} catch (Throwable t) {

						}						
						if (j<numCols-1) {
							//System.out.println("t");
							excelStr.append("\t"); 
						} 
					} 
					//System.out.println("n");
					excelStr.append("\n"); 
				} 

				StringSelection sel  = new StringSelection(excelStr.toString()); 
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(sel, sel);
			}
		}
	}

	public void metabolitesPaste() {
		// columns can not be repeatedly pasted (at least for now)
		if (metabolitesTable.getSelectedColumns().length > 1 && metabolitesTable.getSelectedColumns().length != numberOfClipboardColumns()) {
			JOptionPane.showMessageDialog(null,                
					GraphicalInterfaceConstants.PASTE_AREA_ERROR,                
					"Paste Error",                                
					JOptionPane.ERROR_MESSAGE);
		} else {
			showDuplicatePrompt = true;
			duplicateMetabOK = true;
			participatingMessageShown = false;
			pasteOutOfRangeErrorShown = false;
			continuePasting = true;
			// start at first item of pasteId's;
			int startIndex = 0;
			// this prevents paste menu item and paste button from throwing null pointer exception if
			// pressed when no cell is selected. if this is done, nothing happens. may want to gray out
			if (metabolitesTable.getSelectedRows().length > 0 && metabolitesTable.getSelectedColumns().length > 0) {
				int startRow=metabolitesTable.getSelectedRows()[0]; 
				int startCol=metabolitesTable.getSelectedColumns()[0];
				int numSelectedRows = metabolitesTable.getSelectedRowCount(); 
				String pasteString = ""; 
				try { 
					pasteString = (String)(Toolkit.getDefaultToolkit().getSystemClipboard().getContents(this).getTransferData(DataFlavor.stringFlavor)); 
				} catch (Exception e1) { 
					JOptionPane.showMessageDialog(null, "Invalid Paste Type", "Invalid Paste Type", JOptionPane.ERROR_MESSAGE);
					return; 
				} 
				// copy model so old model can be restored if paste not valid
				DefaultTableModel oldMetabolitesModel = copyMetabolitesTableModel((DefaultTableModel) metabolitesTable.getModel());	
				copyMetabolitesTableModels(oldMetabolitesModel); 
				MetaboliteUndoItem undoItem = createMetaboliteUndoItem("", "", startRow, startCol, 0, UndoConstants.PASTE, UndoConstants.METABOLITE_UNDO_ITEM_TYPE);
				undoItem.setTableCopyIndex(LocalConfig.getInstance().getNumMetabolitesTableCopied());
				setUndoOldCollections(undoItem);
				ArrayList<String> pasteIds = new ArrayList<String>();
				// this allows selecting one row for paste even if many rows are copied
				// and pastes the whole clipboard contents
				if (numSelectedRows < numberOfClipboardRows()) {
					for (int y = 0; y < numberOfClipboardRows(); y++) {
						if (startRow + y < metabolitesTable.getRowCount()) {
							int viewRow = metabolitesTable.convertRowIndexToModel(startRow + y);
							pasteIds.add((String) metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_ID_COLUMN));
						}
					}
				} else {
					for (int y = 0; y < numSelectedRows; y++) {
						int viewRow = metabolitesTable.convertRowIndexToModel(metabolitesTable.getSelectedRows()[y]);
						pasteIds.add((String) metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_ID_COLUMN));
					}
				}		
				// save sort column and order
				setMetabolitesOldSortColumnIndex(getMetabolitesSortColumnIndex());
				setMetabolitesOldSortOrder(getMetabolitesSortOrder());
				// unsort table to avoid sorting of pasted values that results in cells
				// updated after sorted column populated with incorrect values
				setMetabolitesSortDefault();
				DefaultTableModel model = (DefaultTableModel) metabolitesTable.getModel();
				setUpMetabolitesTable(model);
				// after unsorting - get rows corresponding to ids. Using ids will not work if
				// any rows are deleted, and updating each cell by id will take too long -
				// O(n) for each cell updated vs O(1)
				ArrayList<Integer> pasteRows = new ArrayList<Integer>();
				Map<String, Object> metabolitesIdRowMap = new HashMap<String, Object>();
				for (int i = 0; i < metabolitesTable.getRowCount(); i++) {
					metabolitesIdRowMap.put((String) metabolitesTable.getModel().getValueAt(i, GraphicalInterfaceConstants.REACTIONS_ID_COLUMN), i);
				}
				for (int z = 0; z < pasteIds.size(); z++) {
					String row = (metabolitesIdRowMap.get(pasteIds.get(z))).toString();
					int rowNum = Integer.valueOf(row);
					pasteRows.add(rowNum);
				}
				// if selected rows for paste > number of clipboard rows, need to paste
				// clipboard rows repeatedly
				if (numberOfClipboardRows() > 0 && numSelectedRows > numberOfClipboardRows()) {
					int quotient = numSelectedRows/numberOfClipboardRows();
					int remainder = numSelectedRows%numberOfClipboardRows();
					for (int q = 0; q < quotient; q++) {
						String[] lines = pasteString.split("\n");
						for (int i=0 ; i<numberOfClipboardRows(); i++) { 
							if (i < lines.length) {
								String[] cells = lines[i].split("\t");
								// fixes bug where if last cell in row is blank, will not
								// paste blank value over cell value if not blank
								if (q < quotient) {
									for (int j=0 ; j < numberOfClipboardColumns(); j++) { 
										if (startCol + cells.length > metabolitesTable.getColumnCount()) {
											showPasteOutOfRangeError();				
										} else {
											if (j < cells.length) {
												updateMetabolitesCellIfPasteValid(cells[j], pasteRows.get(q*numberOfClipboardRows() + i), startCol+j);
											} else {
												updateMetabolitesCellIfPasteValid("", pasteRows.get(q*numberOfClipboardRows() + i), startCol+j);
											} 
										}									
									}
								}									
							} else {
								if (q < quotient) {
									for (int j=0 ; j < numberOfClipboardColumns(); j++) {
										if (startCol + j > metabolitesTable.getColumnCount()) {
											showPasteOutOfRangeError();			
										} else {
											updateMetabolitesCellIfPasteValid("", pasteRows.get(q*numberOfClipboardRows() + i), startCol+j);
										}									
									}
								}									
							}							 
						}
						startIndex += numberOfClipboardRows();
					}
					for (int m = 0; m < remainder; m++) {
						String[] lines = pasteString.split("\n");
						pasteMetaboliteValues(m, lines, pasteRows, startIndex, startCol);
					}
					// if selected rows for paste <= number of clipboard rows 	
				} else {
					String[] lines = pasteString.split("\n");
					if (startRow + lines.length > metabolitesTable.getRowCount()) {
						showPasteOutOfRangeError();
					} else {
						for (int i=0 ; i<numberOfClipboardRows(); i++) { 
							pasteMetaboliteValues(i, lines, pasteRows, startIndex, startCol);
						} 
					}
				}
				// if paste not valid, set old model
				if (!validPaste) {
					restoreOldMetabolitesSort();
					JOptionPane.showMessageDialog(null,                
							getPasteError(),                
							"Paste Error",                                
							JOptionPane.ERROR_MESSAGE);
					setUpMetabolitesTable(oldMetabolitesModel);
					LocalConfig.getInstance().getMetabolitesTableModelMap().put(LocalConfig.getInstance().getModelName(), oldMetabolitesModel);
					deleteReactionsPasteUndoItem();
					validPaste = true;
				} else {
					enableSaveItems(true);
					LocalConfig.getInstance().metabolitesTableChanged = true;
					DefaultTableModel newMetabolitesModel = copyMetabolitesTableModel((DefaultTableModel) metabolitesTable.getModel());			
					copyMetabolitesTableModels(newMetabolitesModel); 
					setUndoNewCollections(undoItem);
					if (pasteIds.size() > 0) {
						undoItem.setId(Integer.valueOf(pasteIds.get(0)));
					}				
					setUpMetabolitesUndo(undoItem);	
					// reset sort column and order
					setMetabolitesSortColumnIndex(getMetabolitesOldSortColumnIndex());
					setMetabolitesSortOrder(getMetabolitesOldSortOrder());
					setUpMetabolitesTable(newMetabolitesModel);	
					if (pasteIds.size() > 0) {
						scrollToLocation(metabolitesTable, getRowFromMetabolitesId(Integer.valueOf(pasteIds.get(0))), startCol);
					}				
				}
			}					
		}		
	}
	
	// i is the loop counter index
	public void pasteMetaboliteValues(int i, String[] lines, ArrayList<Integer> pasteRows, int startIndex, int startCol) {
		if (i < lines.length) {
			String[] cells = lines[i].split("\t"); 
			if (startCol + cells.length > metabolitesTable.getColumnCount()) {
				showPasteOutOfRangeError();			
			} else {
				for (int j=0 ; j < numberOfClipboardColumns(); j++) { 
					if (j < cells.length) {				
						updateMetabolitesCellIfPasteValid(cells[j], pasteRows.get(startIndex + i), startCol+j);
					
					} else {
						updateMetabolitesCellIfPasteValid("", pasteRows.get(startIndex + i), startCol+j);
					} 
				}
			}
		} else {
			for (int j=0 ; j < numberOfClipboardColumns(); j++) { 
				updateMetabolitesCellIfPasteValid("", pasteRows.get(startIndex + i), startCol+j);
			}
		}
	}
	
	// used for invalid paste, invalid clear, and invalid replace all
	public void deleteMetabolitesPasteUndoItem() {
		int numCopied = LocalConfig.getInstance().getNumMetabolitesTableCopied();	
		if (LocalConfig.getInstance().getMetabolitesUndoTableModelMap().containsKey(Integer.toString(numCopied + 1))) {
			LocalConfig.getInstance().getMetabolitesUndoTableModelMap().remove(Integer.toString(numCopied + 1));
		}
		numCopied -= 1;
		if (LocalConfig.getInstance().getMetabolitesUndoTableModelMap().containsKey(Integer.toString(numCopied + 1))) {
			LocalConfig.getInstance().getMetabolitesUndoTableModelMap().remove(Integer.toString(numCopied + 1));
		}
		LocalConfig.getInstance().setNumMetabolitesTableCopied(numCopied);
	}

	public void restoreOldMetabolitesSort() {
		setMetabolitesSortColumnIndex(getMetabolitesOldSortColumnIndex());
		setMetabolitesSortOrder(getMetabolitesOldSortOrder());
		DefaultTableModel model = (DefaultTableModel) metabolitesTable.getModel();
		setUpMetabolitesTable(model);
	}
	
	public void updateMetabolitesCellIfPasteValid(String value, int row, int col) {
		Utilities u = new Utilities();
		int id = Integer.valueOf((String) metabolitesTable.getModel().getValueAt(row, GraphicalInterfaceConstants.METABOLITE_ID_COLUMN));		
		String metabAbbrev = (String) metabolitesTable.getModel().getValueAt(row, GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN);
		metabolitesTable.convertRowIndexToView(row);
		if (col == GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN) {
			if (LocalConfig.getInstance().getMetaboliteUsedMap().containsKey(metabAbbrev)) {
				restoreOldMetabolitesSort();
				if (!participatingMessageShown) {
					JOptionPane.showMessageDialog(null,                
							GraphicalInterfaceConstants.PARTICIPATING_METAB_PASTE_ERROR_MESSAGE,                
							GraphicalInterfaceConstants.PARTICIPATING_METAB_PASTE_ERROR_TITLE,                                
							JOptionPane.ERROR_MESSAGE);
					participatingMessageShown = true;
					continuePasting = false;
				}				
			} else {
				if (continuePasting) {
					if (value != null && value.trim().length() > 0 && LocalConfig.getInstance().getMetaboliteAbbreviationIdMap().containsKey(value)) {
						if (showDuplicatePrompt) {
							restoreOldMetabolitesSort();
							Object[] options = {"    Yes    ", "    No    ",};
							int choice = JOptionPane.showOptionDialog(null, 
									GraphicalInterfaceConstants.DUPLICATE_METABOLITE_PASTE_MESSAGE, 
									GraphicalInterfaceConstants.DUPLICATE_METABOLITE_TITLE, 
									JOptionPane.YES_NO_OPTION, 
									JOptionPane.QUESTION_MESSAGE, 
									null, options, options[0]);
							if (choice == JOptionPane.YES_OPTION) {	
								value = value + u.duplicateSuffix(value, LocalConfig.getInstance().getMetaboliteAbbreviationIdMap());
								updateMetabolitesCellById(value, id, col);
								LocalConfig.getInstance().getMetaboliteAbbreviationIdMap().remove(metabAbbrev);
								LocalConfig.getInstance().getMetaboliteAbbreviationIdMap().put(value, id);
								showDuplicatePrompt = false;
							}
							if (choice == JOptionPane.NO_OPTION) {
								showDuplicatePrompt = false;
								duplicateMetabOK = false;
								metabolitesTable.setValueAt(metabAbbrev, row, col);
							}
						} else {
							if (duplicateMetabOK) {
								value = value + u.duplicateSuffix(value, LocalConfig.getInstance().getMetaboliteAbbreviationIdMap());
								updateMetabolitesCellById(value, id, col);
								//metabolitesTable.setValueAt(value, viewRow, col);
								if (LocalConfig.getInstance().getMetaboliteAbbreviationIdMap().containsKey(metabAbbrev)) {
									LocalConfig.getInstance().getMetaboliteAbbreviationIdMap().remove(metabAbbrev);
								}
								LocalConfig.getInstance().getMetaboliteAbbreviationIdMap().put(value, id);
							}						
						}
					} else {
						metabolitesTable.setValueAt(value, row, col);
						if (LocalConfig.getInstance().getMetaboliteAbbreviationIdMap().containsKey(metabAbbrev)) {
							LocalConfig.getInstance().getMetaboliteAbbreviationIdMap().remove(metabAbbrev);
						}
						if (value != null && value.trim().length() > 0) {
							LocalConfig.getInstance().getMetaboliteAbbreviationIdMap().put(value, id);
						}
					}
				}				
			}
		} else if (col == GraphicalInterfaceConstants.METABOLITE_NAME_COLUMN) {
			if (LocalConfig.getInstance().getMetaboliteUsedMap().containsKey(metabAbbrev)) {
				if (!participatingMessageShown) {
					restoreOldMetabolitesSort();
					JOptionPane.showMessageDialog(null,                
							GraphicalInterfaceConstants.PARTICIPATING_METAB_PASTE_ERROR_MESSAGE,                
							GraphicalInterfaceConstants.PARTICIPATING_METAB_PASTE_ERROR_TITLE,                                
							JOptionPane.ERROR_MESSAGE);
					participatingMessageShown = true;
				}		
			} else {
				metabolitesTable.setValueAt(value, row, col);
				LocalConfig.getInstance().getMetaboliteIdNameMap().put(id, value);				
			}
		} else if (col == GraphicalInterfaceConstants.COMPARTMENT_COLUMN) {	
			metabolitesTable.setValueAt(value, row, col);
			rewriteReactionEquationNames(id, metabAbbrev, value);
			LocalConfig.getInstance().getMetaboliteIdCompartmentMap().put(new Integer(id), value); 
		} else if (isMetabolitesEntryValid(col, value)) {
			if (col < metabolitesTable.getColumnCount()) {
				metabolitesTable.setValueAt(value, row, col);
			} 
			formulaBar.setText("");
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
		// this prevents clear contents menu item from throwing null pointer exception if
		// pressed when no cell is selected. if this is done, nothing happens. may want to gray out
		if (metabolitesTable.getSelectedRows().length > 0 && metabolitesTable.getSelectedColumns().length > 0) {
			int startRow=(metabolitesTable.getSelectedRows())[0]; 
			int startCol=(metabolitesTable.getSelectedColumns())[0];
			int id = Integer.valueOf((String) metabolitesTable.getModel().getValueAt(startRow, GraphicalInterfaceConstants.METABOLITE_ID_COLUMN));
			// copy model for undo
			DefaultTableModel oldMetabolitesModel = copyMetabolitesTableModel((DefaultTableModel) metabolitesTable.getModel());	
			copyMetabolitesTableModels(oldMetabolitesModel); 
			MetaboliteUndoItem undoItem = createMetaboliteUndoItem("", "", startRow, startCol, id, UndoConstants.CLEAR_CONTENTS, UndoConstants.METABOLITE_UNDO_ITEM_TYPE);
			undoItem.setTableCopyIndex(LocalConfig.getInstance().getNumMetabolitesTableCopied());
			setUndoOldCollections(undoItem);		
			boolean valid = true;
			//ArrayList<Integer> rowList = new ArrayList<Integer>();
			//ArrayList<Integer> metabIdList = new ArrayList<Integer>();
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
					/*
					for (int r = 0; r < metabolitesTable.getSelectedRows().length; r++) {
						int row = metabolitesTable.convertRowIndexToModel(startRow + r);
						rowList.add(row);
						int metabId = Integer.valueOf((String) metabolitesTable.getModel().getValueAt(row, 0));
						metabIdList.add(metabId);
					}
					*/
					for(int i=0; i < metabolitesTable.getSelectedRows().length ;i++) { 
						for(int j=0; j < metabolitesTable.getSelectedColumns().length ;j++) { 					
							//int viewRow = metabolitesTable.convertRowIndexToView(rowList.get(i));
							metabolitesTable.setValueAt(" ", startRow + i, startCol + j);
						} 
					}
					DefaultTableModel newMetabolitesModel = copyMetabolitesTableModel((DefaultTableModel) metabolitesTable.getModel());			
					copyMetabolitesTableModels(newMetabolitesModel); 
					setUndoNewCollections(undoItem);
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
	}

	/**************************************************************************/
	//end metabolitesTable context menu methods
	/**************************************************************************/

	/*******************************************************************************/
	//output pane methods
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
	//end output pane methods
	/*******************************************************************************/

	/***********************************************************************************/
	//clipboard
	/***********************************************************************************/

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
					JOptionPane.showMessageDialog(null,                
							"Clipboard Error.",                
							"Error",                                
							JOptionPane.ERROR_MESSAGE);
					//ex.printStackTrace();
				} catch (UnsupportedFlavorException ex) {
					JOptionPane.showMessageDialog(null,                
							"Clipboard Error. Unsupported Flavor",                
							"Error",                                
							JOptionPane.ERROR_MESSAGE);
					//ex.printStackTrace();
				}
			}
		}
		return null;
	}

	@SuppressWarnings( "deprecation" )
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

	private String escape(Object cell) { 
		return cell.toString().replace("\n", " ").replace("\t", " "); 
	} 

	private int numberOfClipboardRows() {
		// fixes the bug where paste will not paste blank values
		// over cells with non-blank values, also gets 
		// number of rows copied from external application
		int numRows = 0;
		if (isClipboardContainingText(this)) {
			String pasteString = ""; 
			try { 
				pasteString = (String)(Toolkit.getDefaultToolkit().getSystemClipboard().getContents(this).getTransferData(DataFlavor.stringFlavor)); 
			} catch (Exception e1) { 

			} 
			for (int i=0 ; i<pasteString.length(); i++) { 
				if( pasteString.charAt(i) == '\n' ) {
					numRows += 1;
				}
			}
			/*
			String[] lines = pasteString.split("\n"); 
			for (int i=0 ; i<lines.length; i++) { 
				numRows += 1;
			} 
			 */					
		}
		return numRows;		
	}

	private int numberOfClipboardColumns() {
		int numColumns = 0;
		if (isClipboardContainingText(this)) {
			String pasteString = ""; 
			try { 
				pasteString = (String)(Toolkit.getDefaultToolkit().getSystemClipboard().getContents(this).getTransferData(DataFlavor.stringFlavor)); 
			} catch (Exception e1) { 

			} 
			String[] lines = pasteString.split("\n");
			// fixes bug where if last column copied is blank, would not
			// paste over cell with value
			try {
				for (int i=0 ; i<lines[0].length(); i++) { 
					if( lines[0].charAt(i) == '\t' ) {
						numColumns += 1;
					}
				}
			} catch (Throwable t) {

			}			
		}
		numColumns += 1;
		return numColumns;		
	}

	/*****************************************************************************/
	//end clipboard
	/******************************************************************************/

	/******************************************************************************/
	// undo/redo
	/******************************************************************************/

	/**
	 * adds image to OptionComponent
	 * @param comp
	 * @param label
	 */
	public void addImage(OptionComponent comp, JLabel label) {
		label.setBorder(BorderFactory.createEmptyBorder(0,0,0,20));
		label.setAlignmentX(LEFT_ALIGNMENT);
		comp.add(label); 
	}
	/**
	 * @param comp
	 * @param label
	 * @param grayedLabel
	 */
	public void enableOptionComponent(OptionComponent comp, JLabel label, JLabel grayedLabel) {
		comp.setEnabled(true);
		label.setVisible(true);
		grayedLabel.setVisible(false);
	}

	/**
	 * 
	 * @param comp
	 * @param label
	 * @param grayedLabel
	 */
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
    	undoItem.setOldValue(oldValue);
    	undoItem.setNewValue(newValue);
    	undoItem.setRow(row);
    	undoItem.setColumn(column);
    	if (column > -1) {
    		undoItem.setColumnName(undoItem.displayReactionsColumnNameFromIndex(column, LocalConfig.getInstance().getReactionsMetaColumnNames()));
    	}    	
    	undoItem.setId(id);
    	undoItem.setUndoType(undoType);
    	undoItem.setUndoItemType(undoItemType);
		return undoItem;
    	
    }
    
    @SuppressWarnings( "unchecked" )
	public MetaboliteUndoItem createMetaboliteUndoItem(String oldValue, String newValue, 
    		int row, int column, int id, String undoType, String undoItemType) {
    	if (oldValue == null) {
    		oldValue = "";
    	}
    	if (newValue == null) {
    		newValue = "";
    	}
    	MetaboliteUndoItem undoItem = new MetaboliteUndoItem();
    	undoItem.setOldValue(oldValue);
    	undoItem.setNewValue(newValue);
    	undoItem.setRow(row);
    	undoItem.setColumn(column);
    	if (column > -1) {
    		undoItem.setColumnName(undoItem.displayMetabolitesColumnNameFromIndex(column, LocalConfig.getInstance().getMetabolitesMetaColumnNames()));
    	} 
    	undoItem.setId(id);
    	undoItem.setUndoType(undoType);
    	undoItem.setUndoItemType(undoItemType);
    	Map<String, Object> oldMetaboliteAbbreviationIdMap = null;
		Map<String, Object> oldMetaboliteUsedMap = null;
		ArrayList<Integer> oldSuspiciousMetabolites = null;		
		ArrayList<Integer> oldUnusedList = null;
		
		try {
			oldMetaboliteAbbreviationIdMap = (Map<String, Object>) (ObjectCloner.deepCopy(LocalConfig.getInstance().getMetaboliteAbbreviationIdMap()));
			oldMetaboliteUsedMap = (Map<String, Object>) (ObjectCloner.deepCopy(LocalConfig.getInstance().getMetaboliteUsedMap()));
			oldSuspiciousMetabolites = (ArrayList<Integer>)(ObjectCloner.deepCopy(LocalConfig.getInstance().getSuspiciousMetabolites()));
			oldUnusedList = (ArrayList<Integer>)(ObjectCloner.deepCopy(LocalConfig.getInstance().getUnusedList()));
			undoItem.setOldMetaboliteAbbreviationIdMap(oldMetaboliteAbbreviationIdMap);
			undoItem.setOldMetaboliteUsedMap(oldMetaboliteUsedMap);
			undoItem.setOldSuspiciousMetabolites(oldSuspiciousMetabolites);
			undoItem.setOldUnusedList(oldUnusedList);
		} catch (Exception e3) {
			JOptionPane.showMessageDialog(null,                
					"Error Creating Undo Item.",                
					"Error",                                
					JOptionPane.ERROR_MESSAGE);
			//e3.printStackTrace();
		}
		return undoItem;
    	
    }

	@SuppressWarnings( "unchecked" )
	public void setUndoOldCollections(MetaboliteUndoItem undoItem) {
		new ArrayList<Integer>();
		new ArrayList<Integer>();
		Map<String, Object> oldMetaboliteAbbreviationIdMap = new HashMap<String, Object>();
		Map<String, Object> oldMetaboliteUsedMap = new HashMap<String, Object>();
		ArrayList<Integer> oldSuspiciousMetabolites = new ArrayList<Integer>();		
		ArrayList<Integer> oldUnusedList = new ArrayList<Integer>();

		try {
			if (LocalConfig.getInstance().getMetaboliteAbbreviationIdMap() != null) {
				oldMetaboliteAbbreviationIdMap = (Map<String, Object>) (ObjectCloner.deepCopy(LocalConfig.getInstance().getMetaboliteAbbreviationIdMap()));
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
			undoItem.setOldMetaboliteAbbreviationIdMap(oldMetaboliteAbbreviationIdMap);
			undoItem.setOldMetaboliteUsedMap(oldMetaboliteUsedMap);
			undoItem.setOldSuspiciousMetabolites(oldSuspiciousMetabolites);
			undoItem.setOldUnusedList(oldUnusedList);
		} catch (Exception e3) {
			JOptionPane.showMessageDialog(null,                
					"Error Creating Undo Item.",                
					"Error",                                
					JOptionPane.ERROR_MESSAGE);
			//e3.printStackTrace();
		}
	}

	@SuppressWarnings( "unchecked" )
	public void setUndoNewCollections(MetaboliteUndoItem undoItem) {
		new ArrayList<Integer>();
		new ArrayList<Integer>();
		Map<String, Object> newMetaboliteAbbreviationIdMap = new HashMap<String, Object>();
		Map<String, Object> newMetaboliteUsedMap = new HashMap<String, Object>();
		ArrayList<Integer> newSuspiciousMetabolites = new ArrayList<Integer>();	
		ArrayList<Integer> newUnusedList = new ArrayList<Integer>();

		try {
			if (LocalConfig.getInstance().getMetaboliteAbbreviationIdMap() != null) {
				newMetaboliteAbbreviationIdMap = (Map<String, Object>) (ObjectCloner.deepCopy(LocalConfig.getInstance().getMetaboliteAbbreviationIdMap()));
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

			undoItem.setNewMetaboliteAbbreviationIdMap(newMetaboliteAbbreviationIdMap);
			undoItem.setNewMetaboliteUsedMap(newMetaboliteUsedMap);
			undoItem.setNewSuspiciousMetabolites(newSuspiciousMetabolites);
			undoItem.setNewUnusedList(newUnusedList);
		} catch (Exception e3) {
			JOptionPane.showMessageDialog(null,                
					"Error Creating Undo Item.",                
					"Error",                                
					JOptionPane.ERROR_MESSAGE);
			//e3.printStackTrace();
		}
	}
	
	@SuppressWarnings( "unchecked" )
	public void setOldUsedMap(ReactionUndoItem undoItem) {
		Map<String, Object> oldMetaboliteUsedMap = new HashMap<String, Object>();

		try {
			if (LocalConfig.getInstance().getMetaboliteUsedMap() != null) {
				oldMetaboliteUsedMap = (Map<String, Object>) (ObjectCloner.deepCopy(LocalConfig.getInstance().getMetaboliteUsedMap()));
			}
			undoItem.setOldMetaboliteUsedMap(oldMetaboliteUsedMap);
		} catch (Exception e3) {
			JOptionPane.showMessageDialog(null,                
					"Error Creating Undo Item.",                
					"Error",                                
					JOptionPane.ERROR_MESSAGE);
			//e3.printStackTrace();
		}
	}

	@SuppressWarnings( "unchecked" )
	public void setNewUsedMap(ReactionUndoItem undoItem) {
		Map<String, Object> newMetaboliteUsedMap = new HashMap<String, Object>();

		try {
			if (LocalConfig.getInstance().getMetaboliteUsedMap() != null) {
				newMetaboliteUsedMap = (Map<String, Object>) (ObjectCloner.deepCopy(LocalConfig.getInstance().getMetaboliteUsedMap()));
			}
			undoItem.setNewMetaboliteUsedMap(newMetaboliteUsedMap);
		} catch (Exception e3) {
			JOptionPane.showMessageDialog(null,                
					"Error Creating Undo Item.",                
					"Error",                                
					JOptionPane.ERROR_MESSAGE);
			//e3.printStackTrace();
		}
	}

	public void updateUndoButton() {
		undoSplitButton.getPopupMenu();
		addMenuItems(LocalConfig.getInstance().getUndoItemMap(), "undo");
		if (undoCount > 0) {
			enableOptionComponent(undoSplitButton, undoLabel, undoGrayedLabel);
			undoItem.setEnabled(true);
			Class< ? extends Object > cls = LocalConfig.getInstance().getUndoItemMap().get(LocalConfig.getInstance().getUndoItemMap().size()).getClass();
			if ((cls.getName().equals("edu.rutgers.MOST.data.ReactionUndoItem"))) {
				undoSplitButton.setToolTipText("Undo " + ((ReactionUndoItem) LocalConfig.getInstance().getUndoItemMap().get(LocalConfig.getInstance().getUndoItemMap().size())).createUndoDescription() + " (Ctrl+Z)");
			} else if ((cls.getName().equals("edu.rutgers.MOST.data.MetaboliteUndoItem"))) {
				undoSplitButton.setToolTipText("Undo " + ((MetaboliteUndoItem) LocalConfig.getInstance().getUndoItemMap().get(LocalConfig.getInstance().getUndoItemMap().size())).createUndoDescription() + " (Ctrl+Z)");
			}			
		} else {
			disableOptionComponent(undoSplitButton, undoLabel, undoGrayedLabel);
			undoItem.setEnabled(false);
			undoSplitButton.setToolTipText("Can't Undo (Ctrl+Z)");
		}		
		undoCount += 1;	
	}

	public void updateRedoButton() {
		redoSplitButton.getPopupMenu();
		addMenuItems(LocalConfig.getInstance().getRedoItemMap(), "redo");
		if (LocalConfig.getInstance().getRedoItemMap().size() > 0) {
			enableOptionComponent(redoSplitButton, redoLabel, redoGrayedLabel);
			redoItem.setEnabled(true);
			Class< ? extends Object > cls = LocalConfig.getInstance().getRedoItemMap().get(LocalConfig.getInstance().getRedoItemMap().size()).getClass();
			if ((cls.getName().equals("edu.rutgers.MOST.data.ReactionUndoItem"))) {
				redoSplitButton.setToolTipText("Redo " + ((ReactionUndoItem) LocalConfig.getInstance().getRedoItemMap().get(LocalConfig.getInstance().getRedoItemMap().size())).createUndoDescription() + " (Ctrl+Y)");
			} else if ((cls.getName().equals("edu.rutgers.MOST.data.MetaboliteUndoItem"))) {
				redoSplitButton.setToolTipText("Redo " + ((MetaboliteUndoItem) LocalConfig.getInstance().getRedoItemMap().get(LocalConfig.getInstance().getRedoItemMap().size())).createUndoDescription() + " (Ctrl+Y)");
			}
		} else {
			disableOptionComponent(redoSplitButton, redoLabel, redoGrayedLabel);
			redoItem.setEnabled(false);
			redoSplitButton.setToolTipText("Can't Redo (Ctrl+Y)");
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
			Class< ? extends Object > cls = undoMap.get(i + 1).getClass();
			if ((cls.getName().equals("edu.rutgers.MOST.data.ReactionUndoItem"))) {
				item = ((ReactionUndoItem) undoMap.get(i + 1)).createUndoDescription();
			} else if ((cls.getName().equals("edu.rutgers.MOST.data.MetaboliteUndoItem"))) {
				item = ((MetaboliteUndoItem) undoMap.get(i + 1)).createUndoDescription();
			}
			final JMenuItem menuItem = new JMenuItem(item);
			menuItem.setName(Integer.toString(i + 1));
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					boolean scroll = true;
					boolean typing = false;
					if (e.getActionCommand().equals(ENABLE)) {
						int scrollRow = 0;
						int scrollCol = 1;
						for (int i = undoMap.size(); i >= Integer.valueOf(menuItem.getName()); i--) {
							Class< ? extends Object > cls = undoMap.get(i).getClass();
							if ((cls.getName().equals("edu.rutgers.MOST.data.ReactionUndoItem"))) {
								int row = ((ReactionUndoItem) undoMap.get(i)).getRow();
								int id = ((ReactionUndoItem) undoMap.get(i)).getId();
								if (row > -1) {
									scrollRow = row;
								}
								int col = ((ReactionUndoItem) undoMap.get(i)).getColumn();
								if (col > -1) {
									scrollCol = col;
								}
								if (type.equals("undo")) {
									ArrayList<Integer> deleteMetabRows = new ArrayList<Integer>();
									ArrayList<Integer> deleteIds = new ArrayList<Integer>();
									if (((ReactionUndoItem) undoMap.get(i)).getUndoType().equals(UndoConstants.DELETE_COLUMN)) {
										scrollCol = ((ReactionUndoItem) undoMap.get(i)).getDeletedColumnIndex();
									}
									if (((ReactionUndoItem) undoMap.get(i)).getUndoType().equals(UndoConstants.TYPING) || 
											((ReactionUndoItem) undoMap.get(i)).getUndoType().equals(UndoConstants.REPLACE)) {
										typing = true;
										if (((ReactionUndoItem) undoMap.get(i)).getColumn() == GraphicalInterfaceConstants.REACTION_EQUN_ABBR_COLUMN) {
											reactionsUndo = true;
											updateReactionEquation(((ReactionUndoItem) undoMap.get(i)).getRow(), ((ReactionUndoItem) undoMap.get(i)).getId(), ((ReactionUndoItem) undoMap.get(i)).getNewValue(), ((ReactionUndoItem) undoMap.get(i)).getOldValue());
											for (int j = 0; j < ((ReactionUndoItem) undoMap.get(i)).getAddedMetabolites().size(); j++) {
												deleteMetabRows.add(((ReactionUndoItem) undoMap.get(i)).getAddedMetabolites().get(j));
											}
											reactionsUndo = false;
										}							
									} else if (((ReactionUndoItem) undoMap.get(i)).getUndoType().equals(UndoConstants.DELETE_ROW) ||
											((ReactionUndoItem) undoMap.get(i)).getUndoType().equals(UndoConstants.CLEAR_CONTENTS)) {
										deleteIds =	((ReactionUndoItem) undoMap.get(i)).getDeleteIds();								
									} 
									reactionUndoAction(i);
									updateReactionEquationsForUndo(deleteIds);

									if (reactionsTable.getModel().getRowCount() > LocalConfig.getInstance().getMetaboliteIdNameMap().size()) {
										if (LocalConfig.getInstance().getMaxMetabolite() >= Integer.valueOf((String)reactionsTable.getModel().getValueAt(reactionsTable.getModel().getRowCount() - 1, GraphicalInterfaceConstants.REACTIONS_ID_COLUMN))) {
											for (int k = 0; k < deleteMetabRows.size(); k++) {
												deleteMetabolitesRowById(deleteMetabRows.get(k));
											}
										}					
									}									
									DefaultTableModel model = (DefaultTableModel) reactionsTable.getModel();
									setUpReactionsTable(model);
									DefaultTableModel reacModel = copyReactionsTableModel(model);
									LocalConfig.getInstance().getReactionsTableModelMap().put(LocalConfig.getInstance().getModelName(), reacModel);
									undoCount -= 1;	
									if (typing) {
										scrollRow = getRowFromReactionsId(id);
										typing = false;
									}
								} else if (type.equals("redo")) { 
									if (((ReactionUndoItem) undoMap.get(i)).getUndoType().equals(UndoConstants.ADD_ROW)) {
										//scrollRow = redoAddRowScrollRow(reactionsTable);
										redoAddReactionRow();
										scroll = false;
									}
									if (((ReactionUndoItem) undoMap.get(i)).getUndoType().equals(UndoConstants.ADD_COLUMN)) {
										scrollCol = redoAddColumnScrollColumn("reactions");
										addReacColumn = true;
									}
									if (((ReactionUndoItem) undoMap.get(i)).getUndoType().equals(UndoConstants.TYPING) || 
											((ReactionUndoItem) undoMap.get(i)).getUndoType().equals(UndoConstants.REPLACE)) {
										if (((ReactionUndoItem) undoMap.get(i)).getColumn() == GraphicalInterfaceConstants.REACTION_EQUN_ABBR_COLUMN) {											
											updateReactionEquation(((ReactionUndoItem) undoMap.get(i)).getRow(), ((ReactionUndoItem) undoMap.get(i)).getId(), ((ReactionUndoItem) undoMap.get(i)).getOldValue(), ((ReactionUndoItem) undoMap.get(i)).getNewValue());
										}	
									}
									reactionRedoAction(i);
									DefaultTableModel model = (DefaultTableModel) reactionsTable.getModel();
									setUpReactionsTable(model);
									DefaultTableModel reacModel = copyReactionsTableModel(model);
									LocalConfig.getInstance().getReactionsTableModelMap().put(LocalConfig.getInstance().getModelName(), reacModel);
									scrollRow = getRowFromReactionsId(id);
								}  
								updateUndoButton();
								updateRedoButton();
								addReacColumn = false;
								if (i == Integer.valueOf(menuItem.getName())) {
									DefaultTableModel model = (DefaultTableModel) reactionsTable.getModel();
									setUpReactionsTable(model);
									DefaultTableModel reacModel = copyReactionsTableModel(model);
									LocalConfig.getInstance().getReactionsTableModelMap().put(LocalConfig.getInstance().getModelName(), reacModel);
									tabbedPane.setSelectedIndex(0);
									if (scroll) {										
										scrollToLocation(reactionsTable, scrollRow, scrollCol);
									}									
								}											
							} else if ((cls.getName().equals("edu.rutgers.MOST.data.MetaboliteUndoItem"))) {
								int row = ((MetaboliteUndoItem) undoMap.get(i)).getRow();
								int id = ((MetaboliteUndoItem) undoMap.get(i)).getId();
								if (row > -1) {
									scrollRow = row;
								}
								int col = ((MetaboliteUndoItem) undoMap.get(i)).getColumn();
								if (col > -1) {
									scrollCol = col;
								}
								if (type.equals("undo")) {	
									if (((MetaboliteUndoItem) undoMap.get(i)).getUndoType().equals(UndoConstants.TYPING) ||
											((MetaboliteUndoItem) undoMap.get(i)).getUndoType().equals(UndoConstants.REPLACE)) {
										typing = true;
									}
									if (((MetaboliteUndoItem) undoMap.get(i)).getUndoType().equals(UndoConstants.DELETE_COLUMN)) {
										scrollCol = ((MetaboliteUndoItem) undoMap.get(i)).getDeletedColumnIndex();
									}
									metaboliteUndoAction(i);
									DefaultTableModel model = (DefaultTableModel) metabolitesTable.getModel();
									setUpMetabolitesTable(model);
									DefaultTableModel metabModel = copyMetabolitesTableModel(model);
									LocalConfig.getInstance().getMetabolitesTableModelMap().put(LocalConfig.getInstance().getModelName(), metabModel);
									undoCount -= 1;	
									if (typing) {
										scrollRow = getRowFromMetabolitesId(id);
										typing = false;
									}
								} else if (type.equals("redo")) { 
									if (((MetaboliteUndoItem) undoMap.get(i)).getUndoType().equals(UndoConstants.ADD_ROW)) {
										//scrollRow = redoAddRowScrollRow(metabolitesTable);
										redoAddMetaboliteRow();
										scroll = false;
									}
									if (((MetaboliteUndoItem) undoMap.get(i)).getUndoType().equals(UndoConstants.ADD_COLUMN)) {
										scrollCol = redoAddColumnScrollColumn("metabolites");
										addMetabColumn = true;
									}
									metaboliteRedoAction(i);
									DefaultTableModel model = (DefaultTableModel) metabolitesTable.getModel();
									setUpMetabolitesTable(model);
									DefaultTableModel metabModel = copyMetabolitesTableModel(model);
									LocalConfig.getInstance().getMetabolitesTableModelMap().put(LocalConfig.getInstance().getModelName(), metabModel);
									scrollRow = getRowFromMetabolitesId(id);
								} 
								updateUndoButton();
								updateRedoButton();
								addMetabColumn = false;
								if (i == Integer.valueOf(menuItem.getName())) {
									DefaultTableModel model = (DefaultTableModel) metabolitesTable.getModel();
									setUpMetabolitesTable(model);
									DefaultTableModel metabModel = copyMetabolitesTableModel(model);
									LocalConfig.getInstance().getMetabolitesTableModelMap().put(LocalConfig.getInstance().getModelName(), metabModel);
									tabbedPane.setSelectedIndex(1);
									if (scroll) {
										scrollToLocation(metabolitesTable, scrollRow, scrollCol);					
									}									
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
	
	public void updateReactionEquationsForUndo(ArrayList<Integer> deleteIds) {
		Map<String, Object> reactionsIdRowMap = new HashMap<String, Object>();
		for (int j = 0; j < GraphicalInterface.reactionsTable.getRowCount(); j++) {
			reactionsIdRowMap.put((String) GraphicalInterface.reactionsTable.getModel().getValueAt(j, GraphicalInterfaceConstants.REACTIONS_ID_COLUMN), j);
		}
		for (int j = 0; j < deleteIds.size(); j++) {
			String rowStr = (reactionsIdRowMap.get(Integer.toString(deleteIds.get(j)))).toString();
			int rowNum = Integer.valueOf(rowStr);
			if (reactionsTable.getModel().getValueAt(rowNum, GraphicalInterfaceConstants.REACTION_EQUN_ABBR_COLUMN) != null && ((String) reactionsTable.getModel().getValueAt(rowNum, GraphicalInterfaceConstants.REACTION_EQUN_ABBR_COLUMN)).trim().length() > 0) {
				String eq = (String) reactionsTable.getModel().getValueAt(rowNum, GraphicalInterfaceConstants.REACTION_EQUN_ABBR_COLUMN);
				updateReactionEquation(rowNum, deleteIds.get(j), "", eq);
			}	
		}
	}

	public void redoAddReactionRow() {
		int id = LocalConfig.getInstance().getMaxReactionId();
		DefaultTableModel model = (DefaultTableModel) reactionsTable.getModel();	
		model.addRow(createReactionsRow(id));
		setUpReactionsTable(model);
		//set focus to id cell in new row in order to set row visible
		int maxRow = reactionsTable.getModel().getRowCount();
		int viewRow = reactionsTable.convertRowIndexToView(maxRow - 1);
		setTableCellFocused(viewRow, 1, reactionsTable);
		LocalConfig.getInstance().setMaxReactionId(id + 1);
	}

	public void redoAddMetaboliteRow() {
		int id = LocalConfig.getInstance().getMaxMetaboliteId();
		DefaultTableModel model = (DefaultTableModel) metabolitesTable.getModel();	
		model.addRow(createMetabolitesRow(id));
		setUpMetabolitesTable(model);
		//set focus to id cell in new row in order to set row visible
		int maxRow = metabolitesTable.getModel().getRowCount();
		int viewRow = metabolitesTable.convertRowIndexToView(maxRow - 1);
		setTableCellFocused(viewRow, 1, metabolitesTable);
		LocalConfig.getInstance().setMaxMetaboliteId(id + 1);
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
				undoButtonAction();
			}            
		}
	};

	public void undoButtonAction() {
		Class< ? extends Object > cls = LocalConfig.getInstance().getUndoItemMap().get(LocalConfig.getInstance().getUndoItemMap().size()).getClass();
		if ((cls.getName().equals("edu.rutgers.MOST.data.ReactionUndoItem"))) {
			reactionUndoButtonAction();		
		} else if ((cls.getName().equals("edu.rutgers.MOST.data.MetaboliteUndoItem"))) {
			metaboliteUndoButtonAction();
		}				
		// used for highlighting menu items, necessary here?
		LocalConfig.getInstance().setUndoMenuIndex(0);  
	}

	public void reactionUndoButtonAction() {
		ArrayList<Integer> deleteIds = new ArrayList<Integer>();
		boolean typing = false;
		int scrollRow = 0;
		int scrollCol = 1;
		ArrayList<Integer> deleteMetabRows = new ArrayList<Integer>();
		int row = ((ReactionUndoItem) LocalConfig.getInstance().getUndoItemMap().get(LocalConfig.getInstance().getUndoItemMap().size())).getRow();		
		int id = ((ReactionUndoItem) LocalConfig.getInstance().getUndoItemMap().get(LocalConfig.getInstance().getUndoItemMap().size())).getId();	 
		if (row > -1) {
			scrollRow = row;
		}
		int col = ((ReactionUndoItem) LocalConfig.getInstance().getUndoItemMap().get(LocalConfig.getInstance().getUndoItemMap().size())).getColumn();	    				
		if (col > -1) {
			scrollCol = col;
		}
		if (((ReactionUndoItem) LocalConfig.getInstance().getUndoItemMap().get(LocalConfig.getInstance().getUndoItemMap().size())).getUndoType().equals(UndoConstants.DELETE_COLUMN)) {
			col = ((ReactionUndoItem) LocalConfig.getInstance().getUndoItemMap().get(LocalConfig.getInstance().getUndoItemMap().size())).getDeletedColumnIndex();
		}
		if (((ReactionUndoItem) LocalConfig.getInstance().getUndoItemMap().get(LocalConfig.getInstance().getUndoItemMap().size())).getUndoType().equals(UndoConstants.TYPING) || 
				((ReactionUndoItem) LocalConfig.getInstance().getUndoItemMap().get(LocalConfig.getInstance().getUndoItemMap().size())).getUndoType().equals(UndoConstants.REPLACE)) {
			typing = true;
			if (((ReactionUndoItem) LocalConfig.getInstance().getUndoItemMap().get(LocalConfig.getInstance().getUndoItemMap().size())).getColumn() == GraphicalInterfaceConstants.REACTION_EQUN_ABBR_COLUMN) {
				reactionsUndo = true;
				updateReactionEquation(((ReactionUndoItem) LocalConfig.getInstance().getUndoItemMap().get(LocalConfig.getInstance().getUndoItemMap().size())).getRow(), ((ReactionUndoItem) LocalConfig.getInstance().getUndoItemMap().get(LocalConfig.getInstance().getUndoItemMap().size())).getId(), ((ReactionUndoItem) LocalConfig.getInstance().getUndoItemMap().get(LocalConfig.getInstance().getUndoItemMap().size())).getNewValue(), ((ReactionUndoItem) LocalConfig.getInstance().getUndoItemMap().get(LocalConfig.getInstance().getUndoItemMap().size())).getOldValue());
				for (int j = 0; j < ((ReactionUndoItem) LocalConfig.getInstance().getUndoItemMap().get(LocalConfig.getInstance().getUndoItemMap().size())).getAddedMetabolites().size(); j++) {
					deleteMetabRows.add(((ReactionUndoItem) LocalConfig.getInstance().getUndoItemMap().get(LocalConfig.getInstance().getUndoItemMap().size())).getAddedMetabolites().get(j));
				}
				reactionsUndo = false;
			}							
		} else if (((ReactionUndoItem) LocalConfig.getInstance().getUndoItemMap().get(LocalConfig.getInstance().getUndoItemMap().size())).getUndoType().equals(UndoConstants.DELETE_ROW) ||
				((ReactionUndoItem) LocalConfig.getInstance().getUndoItemMap().get(LocalConfig.getInstance().getUndoItemMap().size())).getUndoType().equals(UndoConstants.CLEAR_CONTENTS)) {
			deleteIds = (((ReactionUndoItem) LocalConfig.getInstance().getUndoItemMap().get(LocalConfig.getInstance().getUndoItemMap().size())).getDeleteIds());
		}
		reactionUndoAction(LocalConfig.getInstance().getUndoItemMap().size());
		updateReactionEquationsForUndo(deleteIds);
		
		DefaultTableModel model = (DefaultTableModel) reactionsTable.getModel();
		setUpReactionsTable(model);
		DefaultTableModel reacModel = copyReactionsTableModel(model);
		LocalConfig.getInstance().getReactionsTableModelMap().put(LocalConfig.getInstance().getModelName(), reacModel);
		undoCount -= 1;	
		if (typing) {
			scrollRow = getRowFromReactionsId(id);
			typing = false;
		}
		updateUndoButton();
		updateRedoButton(); 
		tabbedPane.setSelectedIndex(0);
		scrollToLocation(reactionsTable, scrollRow, scrollCol);
	}

	public void metaboliteUndoButtonAction() {
		boolean typing = false;
		int scrollRow = 0;
		int scrollCol = 1;
		int row = ((MetaboliteUndoItem) LocalConfig.getInstance().getUndoItemMap().get(LocalConfig.getInstance().getUndoItemMap().size())).getRow();
		int id = ((MetaboliteUndoItem) LocalConfig.getInstance().getUndoItemMap().get(LocalConfig.getInstance().getUndoItemMap().size())).getId();
		if (row > -1) {
			scrollRow = row;
		}
		int col = ((MetaboliteUndoItem) LocalConfig.getInstance().getUndoItemMap().get(LocalConfig.getInstance().getUndoItemMap().size())).getColumn();	    				
		if (col > -1) {
			scrollCol = col;
		}
		if (((MetaboliteUndoItem) LocalConfig.getInstance().getUndoItemMap().get(LocalConfig.getInstance().getUndoItemMap().size())).getUndoType().equals(UndoConstants.DELETE_COLUMN)) {
			col = ((MetaboliteUndoItem) LocalConfig.getInstance().getUndoItemMap().get(LocalConfig.getInstance().getUndoItemMap().size())).getDeletedColumnIndex();
		}
		if (((MetaboliteUndoItem) LocalConfig.getInstance().getUndoItemMap().get(LocalConfig.getInstance().getUndoItemMap().size())).getUndoType().equals(UndoConstants.TYPING) || 
				((MetaboliteUndoItem) LocalConfig.getInstance().getUndoItemMap().get(LocalConfig.getInstance().getUndoItemMap().size())).getUndoType().equals(UndoConstants.REPLACE)) {
			typing = true;							
		}
		metaboliteUndoAction(LocalConfig.getInstance().getUndoItemMap().size());
		undoCount -= 1;
		if (typing) {
			scrollRow = getRowFromMetabolitesId(id);
			typing = false;
		}
		updateUndoButton();
		updateRedoButton();
		tabbedPane.setSelectedIndex(1);
		scrollToLocation(metabolitesTable, scrollRow, scrollCol);
	}

	public void metaboliteUndoAction(int index) {
    	((MetaboliteUndoItem) LocalConfig.getInstance().getUndoItemMap().get(index)).undo(); 
    	DefaultTableModel model = (DefaultTableModel) metabolitesTable.getModel();
    	setUpMetabolitesTable(model);
    	LocalConfig.getInstance().getRedoItemMap().put(redoCount, LocalConfig.getInstance().getUndoItemMap().get(index));
    	redoCount += 1;		
    	if (((MetaboliteUndoItem) LocalConfig.getInstance().getUndoItemMap().get(index)).getUndoType().equals(UndoConstants.SORT) ||
    			((MetaboliteUndoItem) LocalConfig.getInstance().getUndoItemMap().get(index)).getUndoType().equals(UndoConstants.UNSORT)) {
    		setMetabolitesSortColumnIndex(((MetaboliteUndoItem) LocalConfig.getInstance().getUndoItemMap().get(index)).getOldSortColumnIndex());
    		setMetabolitesSortOrder(((MetaboliteUndoItem) LocalConfig.getInstance().getUndoItemMap().get(index)).getOldSortOrder());
    		LocalConfig.getInstance().getMetabolitesRedoSortColumns().add(getMetabolitesSortColumnIndex());
    		LocalConfig.getInstance().getMetabolitesRedoSortOrderList().add(getMetabolitesSortOrder());
    		LocalConfig.getInstance().getMetabolitesSortColumns().remove(LocalConfig.getInstance().getMetabolitesSortColumns().size() - 1);
    		LocalConfig.getInstance().getMetabolitesSortOrderList().remove(LocalConfig.getInstance().getMetabolitesSortOrderList().size() - 1);
    		setUpMetabolitesTable(model);
    	}
    	LocalConfig.getInstance().getUndoItemMap().remove(index);
    	undoCount -= 1;
    	highlightUnusedMetabolites = false;
		highlightUnusedMetabolitesItem.setState(false);
    }

    public void reactionUndoAction(int index) {
    	((ReactionUndoItem) LocalConfig.getInstance().getUndoItemMap().get(index)).undo(); 
    	DefaultTableModel model = (DefaultTableModel) reactionsTable.getModel();
    	setUpReactionsTable(model);
    	LocalConfig.getInstance().getRedoItemMap().put(redoCount, LocalConfig.getInstance().getUndoItemMap().get(index));    	
    	redoCount += 1;		
    	if (((ReactionUndoItem) LocalConfig.getInstance().getUndoItemMap().get(index)).getUndoType().equals(UndoConstants.SORT) ||
    			((ReactionUndoItem) LocalConfig.getInstance().getUndoItemMap().get(index)).getUndoType().equals(UndoConstants.UNSORT)) {
    		setReactionsSortColumnIndex(((ReactionUndoItem) LocalConfig.getInstance().getUndoItemMap().get(index)).getOldSortColumnIndex());
    		setReactionsSortOrder(((ReactionUndoItem) LocalConfig.getInstance().getUndoItemMap().get(index)).getOldSortOrder());
    		LocalConfig.getInstance().getReactionsRedoSortColumns().add(getReactionsSortColumnIndex());
    		LocalConfig.getInstance().getReactionsRedoSortOrderList().add(getReactionsSortOrder());
    		LocalConfig.getInstance().getReactionsSortColumns().remove(LocalConfig.getInstance().getReactionsSortColumns().size() - 1);
    		LocalConfig.getInstance().getReactionsSortOrderList().remove(LocalConfig.getInstance().getReactionsSortOrderList().size() - 1);
    		setUpReactionsTable(model);
    	} 
    	ArrayList<Integer> deleteMetabRows = new ArrayList<Integer>();
    	if (((ReactionUndoItem) LocalConfig.getInstance().getUndoItemMap().get(index)).getUndoType().equals(UndoConstants.TYPING) ||
    		((ReactionUndoItem) LocalConfig.getInstance().getUndoItemMap().get(index)).getUndoType().equals(UndoConstants.REPLACE)) {
    		
    	}
    	LocalConfig.getInstance().getUndoItemMap().remove(index);
    	undoCount -= 1;	
    	if (reactionsTable.getModel().getRowCount() > LocalConfig.getInstance().getMetaboliteIdNameMap().size()) {
			if (LocalConfig.getInstance().getMaxMetabolite() >= Integer.valueOf((String)reactionsTable.getModel().getValueAt(reactionsTable.getModel().getRowCount() - 1, GraphicalInterfaceConstants.REACTIONS_ID_COLUMN))) {
				for (int k = 0; k < deleteMetabRows.size(); k++) {
					deleteMetabolitesRowById(deleteMetabRows.get(k));
				}
			}					
		}
    }

	private MouseListener redoButtonMouseListener = new MouseAdapter() { 
		public void mousePressed(MouseEvent e) {
			if (redoSplitButton.buttonClicked) {
				redoButtonAction();
			}            
		}
	};

	public void redoButtonAction() {
		Class< ? extends Object > cls = LocalConfig.getInstance().getRedoItemMap().get(LocalConfig.getInstance().getRedoItemMap().size()).getClass();
		if ((cls.getName().equals("edu.rutgers.MOST.data.ReactionUndoItem"))) {
			reactionRedoButtonAction();
		} else if ((cls.getName().equals("edu.rutgers.MOST.data.MetaboliteUndoItem"))) {
			metaboliteRedoButtonAction();
		}
		// used for highlighting menu items, necessary here?
		LocalConfig.getInstance().setUndoMenuIndex(0); 
	}

	public void reactionRedoButtonAction() {
		((ReactionUndoItem) LocalConfig.getInstance().getRedoItemMap().get(LocalConfig.getInstance().getRedoItemMap().size())).getRow();
		int col = ((ReactionUndoItem) LocalConfig.getInstance().getRedoItemMap().get(LocalConfig.getInstance().getRedoItemMap().size())).getColumn();	    				
		int id = ((ReactionUndoItem) LocalConfig.getInstance().getRedoItemMap().get(LocalConfig.getInstance().getRedoItemMap().size())).getId();	    				
		if (((ReactionUndoItem) LocalConfig.getInstance().getRedoItemMap().get(LocalConfig.getInstance().getRedoItemMap().size())).getUndoType().equals(UndoConstants.ADD_ROW)) {
			//row = redoAddRowScrollRow(reactionsTable);
			redoAddReactionRow();
		}
		if (((ReactionUndoItem) LocalConfig.getInstance().getRedoItemMap().get(LocalConfig.getInstance().getRedoItemMap().size())).getUndoType().equals(UndoConstants.ADD_COLUMN)) {
			col = redoAddColumnScrollColumn("reactions");
			addReacColumn = true;
		}
		if (((ReactionUndoItem) LocalConfig.getInstance().getRedoItemMap().get(LocalConfig.getInstance().getRedoItemMap().size())).getUndoType().equals(UndoConstants.TYPING) ||
				((ReactionUndoItem) LocalConfig.getInstance().getRedoItemMap().get(LocalConfig.getInstance().getRedoItemMap().size())).getUndoType().equals(UndoConstants.REPLACE)) {
			if (((ReactionUndoItem) LocalConfig.getInstance().getRedoItemMap().get(LocalConfig.getInstance().getRedoItemMap().size())).getColumn() == GraphicalInterfaceConstants.REACTION_EQUN_ABBR_COLUMN) {
				updateReactionEquation(((ReactionUndoItem) LocalConfig.getInstance().getRedoItemMap().get(LocalConfig.getInstance().getRedoItemMap().size())).getRow(), ((ReactionUndoItem) LocalConfig.getInstance().getRedoItemMap().get(LocalConfig.getInstance().getRedoItemMap().size())).getId(), ((ReactionUndoItem) LocalConfig.getInstance().getRedoItemMap().get(LocalConfig.getInstance().getRedoItemMap().size())).getOldValue(), ((ReactionUndoItem) LocalConfig.getInstance().getRedoItemMap().get(LocalConfig.getInstance().getRedoItemMap().size())).getNewValue());
			}
		} 
		reactionRedoAction(LocalConfig.getInstance().getRedoItemMap().size());
		updateUndoButton();
		updateRedoButton();  
		DefaultTableModel model = (DefaultTableModel) reactionsTable.getModel();
		setUpReactionsTable(model);
		DefaultTableModel reacModel = copyReactionsTableModel(model);
		LocalConfig.getInstance().getReactionsTableModelMap().put(LocalConfig.getInstance().getModelName(), reacModel);
		tabbedPane.setSelectedIndex(0);
//		if (row > -1 && col > -1) {
//			scrollToLocation(reactionsTable, row, col);  					
//		}
//		if (scroll) {
//			if (row < 0) {
//				row = 0;
//			}
//			scrollToLocation(reactionsTable, row, col);
//		}
		scrollToLocation(reactionsTable, getRowFromReactionsId(id), col);
		addReacColumn = false;
	}

	public void metaboliteRedoButtonAction() {
		((MetaboliteUndoItem) LocalConfig.getInstance().getRedoItemMap().get(LocalConfig.getInstance().getRedoItemMap().size())).getRow();
		int col = ((MetaboliteUndoItem) LocalConfig.getInstance().getRedoItemMap().get(LocalConfig.getInstance().getRedoItemMap().size())).getColumn();	    	
		int id = ((MetaboliteUndoItem) LocalConfig.getInstance().getRedoItemMap().get(LocalConfig.getInstance().getRedoItemMap().size())).getId();	    	
		if (((MetaboliteUndoItem) LocalConfig.getInstance().getRedoItemMap().get(LocalConfig.getInstance().getRedoItemMap().size())).getUndoType().equals(UndoConstants.ADD_ROW)) {
			redoAddMetaboliteRow();
		}
		if (((MetaboliteUndoItem) LocalConfig.getInstance().getRedoItemMap().get(LocalConfig.getInstance().getRedoItemMap().size())).getUndoType().equals(UndoConstants.ADD_COLUMN)) {
			col = redoAddColumnScrollColumn("metabolites");
			addMetabColumn = true;
		}
		metaboliteRedoAction(LocalConfig.getInstance().getRedoItemMap().size());
		updateUndoButton();
		updateRedoButton();
		DefaultTableModel model = (DefaultTableModel) metabolitesTable.getModel();
		setUpMetabolitesTable(model);
		DefaultTableModel metabModel = copyMetabolitesTableModel(model);
		LocalConfig.getInstance().getMetabolitesTableModelMap().put(LocalConfig.getInstance().getModelName(), metabModel);
		tabbedPane.setSelectedIndex(1);
//		if (row > -1 && col > -1) {
//			scrollToLocation(metabolitesTable, row, col);				
//		}
//		if (scroll) {
//			if (row < 0) {
//				row = 0;
//			}
//			scrollToLocation(metabolitesTable, row, col);
//		}
		scrollToLocation(metabolitesTable, getRowFromMetabolitesId(id), col);
		addMetabColumn = false;
	}

	public void metaboliteRedoAction(int index) {    	
    	((MetaboliteUndoItem) LocalConfig.getInstance().getRedoItemMap().get(index)).redo();     	
    	LocalConfig.getInstance().getUndoItemMap().put(undoCount, LocalConfig.getInstance().getRedoItemMap().get(index));
    	//undoCount += 1;
    	if (((MetaboliteUndoItem) LocalConfig.getInstance().getRedoItemMap().get(index)).getUndoType().equals(UndoConstants.SORT) ||
    			((MetaboliteUndoItem) LocalConfig.getInstance().getRedoItemMap().get(index)).getUndoType().equals(UndoConstants.UNSORT)) {
    		setMetabolitesSortColumnIndex(((MetaboliteUndoItem) LocalConfig.getInstance().getRedoItemMap().get(index)).getNewSortColumnIndex());
    		setMetabolitesSortOrder(((MetaboliteUndoItem) LocalConfig.getInstance().getRedoItemMap().get(index)).getNewSortOrder());
    		LocalConfig.getInstance().getMetabolitesSortColumns().add(getMetabolitesSortColumnIndex());
    		LocalConfig.getInstance().getMetabolitesSortOrderList().add(getMetabolitesSortOrder());
    		LocalConfig.getInstance().getMetabolitesRedoSortColumns().remove(LocalConfig.getInstance().getMetabolitesRedoSortColumns().size() - 1);
    		LocalConfig.getInstance().getMetabolitesRedoSortOrderList().remove(LocalConfig.getInstance().getMetabolitesRedoSortOrderList().size() - 1);
    		DefaultTableModel model = (DefaultTableModel) metabolitesTable.getModel();
    		setUpMetabolitesTable(model);
    	}
    	LocalConfig.getInstance().getRedoItemMap().remove(index);
    	redoCount -= 1; 
    	highlightUnusedMetabolites = false;
		highlightUnusedMetabolitesItem.setState(false);
    }
    
    public void reactionRedoAction(int index) {    	
    	((ReactionUndoItem) LocalConfig.getInstance().getRedoItemMap().get(index)).redo();  
    	LocalConfig.getInstance().getUndoItemMap().put(undoCount, LocalConfig.getInstance().getRedoItemMap().get(index));    	
    	//undoCount += 1;
    	if (((ReactionUndoItem) LocalConfig.getInstance().getRedoItemMap().get(index)).getUndoType().equals(UndoConstants.SORT) ||
    			((ReactionUndoItem) LocalConfig.getInstance().getRedoItemMap().get(index)).getUndoType().equals(UndoConstants.UNSORT)) {
    		setReactionsSortColumnIndex(((ReactionUndoItem) LocalConfig.getInstance().getRedoItemMap().get(index)).getNewSortColumnIndex());
    		setReactionsSortOrder(((ReactionUndoItem) LocalConfig.getInstance().getRedoItemMap().get(index)).getNewSortOrder());
    		LocalConfig.getInstance().getReactionsSortColumns().add(getReactionsSortColumnIndex());
    		LocalConfig.getInstance().getReactionsSortOrderList().add(getReactionsSortOrder());
    		LocalConfig.getInstance().getReactionsRedoSortColumns().remove(LocalConfig.getInstance().getReactionsRedoSortColumns().size() - 1);
    		LocalConfig.getInstance().getReactionsRedoSortOrderList().remove(LocalConfig.getInstance().getReactionsRedoSortOrderList().size() - 1);
    		DefaultTableModel model = (DefaultTableModel) reactionsTable.getModel();
    		setUpReactionsTable(model);
    	}
    	LocalConfig.getInstance().getRedoItemMap().remove(index);
    	redoCount -= 1;
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
			undoItem.setAddedMetabolites(addedMetabList);
	    	LocalConfig.getInstance().getUndoItemMap().put(undoCount, undoItem);				
			updateUndoButton();
			clearRedoButton();
		} catch (Exception e3) {
			JOptionPane.showMessageDialog(null,                
					"Error Creating Undo Item.",                
					"Error",                                
					JOptionPane.ERROR_MESSAGE);
			//e3.printStackTrace();
		}
    }
    
    public static String tableCopySuffix(int count) {
    	return new DecimalFormat("000").format(count);
    }
    
    public void copyReactionsTableModels(DefaultTableModel model) {
    	int numCopied = LocalConfig.getInstance().getNumReactionTablesCopied();
		LocalConfig.getInstance().setNumReactionTablesCopied(numCopied + 1);
		LocalConfig.getInstance().getReactionsUndoTableModelMap().put(Integer.toString(LocalConfig.getInstance().getNumReactionTablesCopied()), model);
    }
    
    public void copyMetabolitesTableModels(DefaultTableModel model) {
    	int numCopied = LocalConfig.getInstance().getNumMetabolitesTableCopied();
		LocalConfig.getInstance().setNumMetabolitesTableCopied(numCopied + 1);
		LocalConfig.getInstance().getMetabolitesUndoTableModelMap().put(Integer.toString(LocalConfig.getInstance().getNumMetabolitesTableCopied()), model);
    }    
    
    // any changes in table clears redo button
    public void clearRedoButton() {
    	disableOptionComponent(redoSplitButton, redoLabel, redoGrayedLabel);
    	redoItem.setEnabled(false);
    	redoSplitButton.setToolTipText("Can't Redo (Ctrl+Y)");
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
    		col = LocalConfig.getInstance().getReactionsMetaColumnNames().size() + GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES.length;
    	} else if (tableName.equals("metabolites")) {
    		col = LocalConfig.getInstance().getMetabolitesMetaColumnNames().size() + GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES.length;
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

	ActionListener saveButtonActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent ae) {
			saveLoadedFile();
		}
	};
	
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
				reactionsPaste();
			} else if (tabbedPane.getSelectedIndex() == 1) {
				metabolitesPaste();
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

	/*******************************************************************************/
	// find/replace methods
	/******************************************************************************/

	public void showFindReplace() {
		LocalConfig.getInstance().setReactionsLocationsListCount(0);
		LocalConfig.getInstance().setMetabolitesLocationsListCount(0);
		LocalConfig.getInstance().findFieldChanged = false;
		FindReplaceDialog findReplace = new FindReplaceDialog();
		setFindReplaceDialog(findReplace);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

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
		searchBackwards = FindReplaceConstants.SEARCH_BACKWARDS_DEFAULT;
		matchCase = FindReplaceConstants.MATCH_CASE_DEFAULT;
		wrapAround = FindReplaceConstants.WRAP_AROUND_DEFAULT;
		searchSelectedArea = FindReplaceConstants.SELECTED_AREA_DEFAULT;
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
		//if (rowGreater) {
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

	// this has been removed since the keyboard shortcut does not work due
	// to button action causes Find/Replace dialog to lose focus
	/*
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
	 */

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
			
			FindReplaceDialog.wrapCheckBox.setSelected(true);
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
				}			
			}			
		}
	};

	public ArrayList<ArrayList<Integer>> reactionsLocationsList() {		
		reactionsTable.getRowCount();
		reactionsTable.getColumnCount();
		if (searchSelectedArea) {
			if (!findButtonReactionsClicked) {
				int numcols=reactionsTable.getSelectedColumnCount(); 
				int numrows=reactionsTable.getSelectedRowCount(); 
				int[] rowsselected=reactionsTable.getSelectedRows(); 
				int[] colsselected=reactionsTable.getSelectedColumns();
				if (numrows > 0 && getSelectionMode() != 1) {
					selectedReactionsRowStartIndex = rowsselected[0];
					selectedReactionsRowEndIndex = rowsselected[0] + numrows;
				}			
				if (numcols > 0 && getSelectionMode() != 2) {
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
						if (findValue != null) {
							findValue = findValue.toLowerCase();
						}					
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
		reactionsReplace = true;
		int viewRow = reactionsTable.convertRowIndexToModel(getReactionsReplaceLocation().get(0));
		String oldValue = (String) reactionsTable.getModel().getValueAt(viewRow, getReactionsReplaceLocation().get(1));		
		String newValue = replaceValue(oldValue, replaceLocation(oldValue));
		int id = Integer.valueOf((String) reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTIONS_ID_COLUMN));
		ArrayList<ArrayList<Integer>> locationList = reactionsLocationsList();
		if (replaceLocation(oldValue) > -1) {
			String oldLowerBound = (String) (reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.LOWER_BOUND_COLUMN));
			String oldUpperBound = (String) (reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.UPPER_BOUND_COLUMN));
			reactionsTable.getModel().setValueAt(newValue, viewRow, getReactionsReplaceLocation().get(1));
			updateReactionsCellIfValid(oldValue, newValue, viewRow, getReactionsReplaceLocation().get(1));
			ReactionUndoItem undoItem = createReactionUndoItem(oldValue, newValue, reactionsTable.getSelectedRow(), getReactionsReplaceLocation().get(1), id, UndoConstants.REPLACE, UndoConstants.REACTION_UNDO_ITEM_TYPE);
			setOldUsedMap(undoItem);
			undoItem.setMaxMetab(LocalConfig.getInstance().getMaxMetabolite());
			undoItem.setMaxMetabId(LocalConfig.getInstance().getMaxMetaboliteId());
			undoItem.setOldLowerBound(oldLowerBound);
			undoItem.setOldUpperBound(oldUpperBound);
			if (reactionUpdateValid) {
				enableSaveItems(true);
				LocalConfig.getInstance().reactionsTableChanged = true;
				scrollToLocation(reactionsTable, getRowFromReactionsId(id), getReactionsReplaceLocation().get(1));
				formulaBar.setText(newValue);
				undoItem.setNewLowerBound((String) (reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.LOWER_BOUND_COLUMN)));
				undoItem.setNewUpperBound((String) (reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.UPPER_BOUND_COLUMN)));
				setNewUsedMap(undoItem);
				setUpReactionsUndo(undoItem);
			} else {
				formulaBar.setText(oldValue);
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
		reactionsReplace = false;
	}

	ActionListener replaceAllReactionsButtonActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent ae) {
			if (tabbedPane.getSelectedIndex() == 0) {
				// copy model so old model can be restored if replace all not valid
				DefaultTableModel oldReactionsModel = copyReactionsTableModel((DefaultTableModel) reactionsTable.getModel());	
				copyReactionsTableModels(oldReactionsModel);
				ReactionUndoItem undoItem = createReactionUndoItem("", "", getReactionsFindLocationsList().get(0).get(0), getReactionsFindLocationsList().get(0).get(1), 0, UndoConstants.REPLACE_ALL, UndoConstants.REACTION_UNDO_ITEM_TYPE);
				setOldUsedMap(undoItem);
				undoItem.setTableCopyIndex(LocalConfig.getInstance().getNumReactionTablesCopied());
				replaceAllMode = true;
				showErrorMessage = true;
				ArrayList<String> pasteIds = new ArrayList<String>();
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
					if (isReactionsEntryValid(viewRow, getReactionsFindLocationsList().get(i).get(1), replaceAllValue)) {	
						EntryValidator validator = new EntryValidator();
						if (getReactionsFindLocationsList().get(i).get(1) == GraphicalInterfaceConstants.FLUX_VALUE_COLUMN ||
								getReactionsFindLocationsList().get(i).get(1) == GraphicalInterfaceConstants.MIN_FLUX_COLUMN ||
								getReactionsFindLocationsList().get(i).get(1) == GraphicalInterfaceConstants.MAX_FLUX_COLUMN ||
								getReactionsFindLocationsList().get(i).get(1) == GraphicalInterfaceConstants.LOWER_BOUND_COLUMN ||
								getReactionsFindLocationsList().get(i).get(1) == GraphicalInterfaceConstants.UPPER_BOUND_COLUMN ||
								getReactionsFindLocationsList().get(i).get(1) == GraphicalInterfaceConstants.BIOLOGICAL_OBJECTIVE_COLUMN ||
								getReactionsFindLocationsList().get(i).get(1) == GraphicalInterfaceConstants.SYNTHETIC_OBJECTIVE_COLUMN) {
							if (validator.isInvalidInfinityEntry(replaceAllValue)) {
								replaceAllValue = GraphicalInterfaceConstants.VALID_INFINITY_ENTRY;
							} else if (validator.isInvalidNegativeInfinityEntry(replaceAllValue)) {
								replaceAllValue = "-" + GraphicalInterfaceConstants.VALID_INFINITY_ENTRY;
							}
						}
						reactionsTable.setValueAt(replaceAllValue, viewRow, getReactionsFindLocationsList().get(i).get(1));						
						if (getReactionsFindLocationsList().get(i).get(1) == GraphicalInterfaceConstants.REACTION_EQUN_ABBR_COLUMN) {
							int id = Integer.valueOf((String) reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTIONS_ID_COLUMN));								
							updateReactionEquation(getReactionsFindLocationsList().get(i).get(0), id, oldValue, replaceAllValue);
						} else if (getReactionsFindLocationsList().get(i).get(1) == GraphicalInterfaceConstants.REVERSIBLE_COLUMN) {
							pasteIds.add((String) reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTIONS_ID_COLUMN));
						}
					} else {
						validPaste = false;
					}		
				}
				undoItem.setPasteIds(pasteIds);
				if (validPaste) {
					enableSaveItems(true);
					LocalConfig.getInstance().reactionsTableChanged = true;
					DefaultTableModel newReactionsModel = copyReactionsTableModel((DefaultTableModel) reactionsTable.getModel());			
					copyReactionsTableModels(newReactionsModel); 
					setNewUsedMap(undoItem);
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
					// restore old model if invalid replace
					setUpReactionsTable(oldReactionsModel);
					LocalConfig.getInstance().getReactionsTableModelMap().put(LocalConfig.getInstance().getModelName(), oldReactionsModel);
					deleteReactionsPasteUndoItem();
					
					FindReplaceDialog.replaceAllButton.setEnabled(true);
					validPaste = true;
				}
			}

			// these two lines remove highlighting from find all by creating a new
			// list which should be empty, since the find all value has been replaced
			ArrayList<ArrayList<Integer>> locationList = reactionsLocationsList();
			setReactionsFindLocationsList(locationList);

			// reset boolean values to default
			replaceAllMode = false;
			getFindReplaceDialog().requestFocus();
		}
	};

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

	// temporarily removed see replaceFindReactionsButtonActionListener above
	/*
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
	 */

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
				}			
			}		
		}
	};

	public ArrayList<ArrayList<Integer>> metabolitesLocationsList() {		
		metabolitesTable.getRowCount();
		metabolitesTable.getColumnCount();
		if (searchSelectedArea) {
			if (!findButtonMetabolitesClicked) {
				int numcols=metabolitesTable.getSelectedColumnCount(); 
				int numrows=metabolitesTable.getSelectedRowCount(); 
				int[] rowsselected=metabolitesTable.getSelectedRows(); 
				int[] colsselected=metabolitesTable.getSelectedColumns();
				if (numrows > 0 && getSelectionMode() != 1) {
					selectedMetabolitesRowStartIndex = rowsselected[0];
					selectedMetabolitesRowEndIndex = rowsselected[0] + numrows;
				}			
				if (numcols > 0 && getSelectionMode() != 2) {
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
					if (cellValue.contains(findValue)) {
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
		metabolitesReplace = true;
		int viewRow = metabolitesTable.convertRowIndexToModel(getMetabolitesReplaceLocation().get(0));
		String oldValue = (String) metabolitesTable.getModel().getValueAt(viewRow, getMetabolitesReplaceLocation().get(1));
		String newValue = replaceValue(oldValue, replaceLocation(oldValue));
		int id = Integer.valueOf((String)  metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_ID_COLUMN));
		if (replaceLocation(oldValue) > -1) {
			//String newValue = replaceValue(oldValue, replaceLocation(oldValue));
			metabolitesTable.getModel().setValueAt(newValue, viewRow, getMetabolitesReplaceLocation().get(1));
			updateMetabolitesCellIfValid(oldValue, newValue, viewRow, getMetabolitesReplaceLocation().get(1));
			MetaboliteUndoItem undoItem = createMetaboliteUndoItem(oldValue, newValue, metabolitesTable.getSelectedRow(), getMetabolitesReplaceLocation().get(1), id, UndoConstants.REPLACE, UndoConstants.METABOLITE_UNDO_ITEM_TYPE);
			setUndoOldCollections(undoItem);
			if (metaboliteUpdateValid) {
				enableSaveItems(true);
				LocalConfig.getInstance().metabolitesTableChanged = true;
				scrollToLocation(metabolitesTable, getRowFromMetabolitesId(id), getMetabolitesReplaceLocation().get(1));
				formulaBar.setText(newValue);
				setUndoNewCollections(undoItem);
				setUpMetabolitesUndo(undoItem);
			} else {
				formulaBar.setText(oldValue);
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
		metabolitesReplace = false;
	}

	ActionListener replaceAllMetabolitesButtonActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent ae) {
			if (tabbedPane.getSelectedIndex() == 1) {
				// copy model so old model can be restored if replace not valid
				DefaultTableModel oldMetabolitesModel = copyMetabolitesTableModel((DefaultTableModel) metabolitesTable.getModel());
				copyMetabolitesTableModels(oldMetabolitesModel); 
				MetaboliteUndoItem undoItem = createMetaboliteUndoItem("", "", getMetabolitesFindLocationsList().get(0).get(0), getMetabolitesFindLocationsList().get(0).get(1), 1, UndoConstants.REPLACE_ALL, UndoConstants.METABOLITE_UNDO_ITEM_TYPE);
				undoItem.setTableCopyIndex(LocalConfig.getInstance().getNumMetabolitesTableCopied());
				setUndoOldCollections(undoItem);				
				replaceAllMode = true;
				showErrorMessage = true;
				ArrayList<Integer> rowList = new ArrayList<Integer>();
				ArrayList<Integer> metabIdList = new ArrayList<Integer>();
				for (int i = 0; i < getMetabolitesFindLocationsList().size(); i++) {
					int viewRow = metabolitesTable.convertRowIndexToModel(getMetabolitesFindLocationsList().get(i).get(0));
					Integer.valueOf((String) metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_ID_COLUMN));		
					rowList.add(viewRow);
					int metabId = Integer.valueOf((String) metabolitesTable.getModel().getValueAt(viewRow, 0));
					metabIdList.add(metabId);
				}
				for (int i = 0; i < getMetabolitesFindLocationsList().size(); i++) {
					int viewRow = metabolitesTable.convertRowIndexToModel(getMetabolitesFindLocationsList().get(i).get(0));
					int id = Integer.valueOf((String) metabolitesTable.getModel().getValueAt(viewRow, 0));
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
						if (LocalConfig.getInstance().getMetaboliteUsedMap().containsKey(oldValue)) {
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
					} else if (getMetabolitesFindLocationsList().get(i).get(1) == GraphicalInterfaceConstants.COMPARTMENT_COLUMN) {
						if (isMetabolitesEntryValid(getMetabolitesFindLocationsList().get(i).get(1), replaceAllValue)) {
							metabolitesTable.setValueAt(replaceAllValue, viewRow, getMetabolitesFindLocationsList().get(i).get(1));	
							rewriteReactionEquationNames(id, metabAbbrev, replaceAllValue);
							LocalConfig.getInstance().getMetaboliteIdCompartmentMap().put(new Integer(id), replaceAllValue); 
						} else {
							validPaste = false;
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
					enableSaveItems(true);
					LocalConfig.getInstance().metabolitesTableChanged = true;
					DefaultTableModel newMetabolitesModel = copyMetabolitesTableModel((DefaultTableModel) metabolitesTable.getModel());			
					copyMetabolitesTableModels(newMetabolitesModel); 
					setUndoNewCollections(undoItem);
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
					setUpMetabolitesTable(oldMetabolitesModel);
					LocalConfig.getInstance().getMetabolitesTableModelMap().put(LocalConfig.getInstance().getModelName(), oldMetabolitesModel);
					deleteMetabolitesPasteUndoItem();
					
					FindReplaceDialog.replaceAllButton.setEnabled(true);
					validPaste = true;
				}				
			}

			// these two lines remove highlighting from find all
			ArrayList<ArrayList<Integer>> locationList = metabolitesLocationsList();
			setMetabolitesFindLocationsList(locationList);

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
			
			FindReplaceDialog.replaceButton.setEnabled(false);
			//getFindReplaceDialog().replaceFindButton.setEnabled(false);
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
	// end reactions find replace

	/*******************************************************************************/
	// end find/replace methods
	/******************************************************************************/

	public void createUnusedMetabolitesList() {
		Map<String, Object> idMap = LocalConfig.getInstance().getMetaboliteAbbreviationIdMap();

		try {
			ArrayList<String> usedList = new ArrayList<String>(LocalConfig.getInstance().getMetaboliteUsedMap().keySet());
			ArrayList<String> idList = new ArrayList<String>(LocalConfig.getInstance().getMetaboliteAbbreviationIdMap().keySet());
			ArrayList<Integer> unusedList = new ArrayList<Integer>();
			// removes unused metabolites from idMap and populates list of
			// unused metabolite id's for deletion from table
			for (int i = 0; i < idList.size(); i++) {						
				if (!usedList.contains(idList.get(i))) {
					try {
						int id = (Integer) idMap.get(idList.get(i));
						unusedList.add(id); 
					} catch (Throwable t) {
						Utilities u = new Utilities();
						u.showResizableDialog("Error", "Error", "errorMessage");
					}								
				}
			}
			LocalConfig.getInstance().setUnusedList(unusedList);
		} catch (Throwable t) {
//			System.out.println("Error");
//			Utilities u = new Utilities();
//			u.showResizableDialog("Error", "Error", "errorMessage");
		}		
	}

	public void deleteItemFromDynamicTree() {
		Object[] options = {"    Yes    ", "    No    ",};
		int choice = JOptionPane.showOptionDialog(null, 
				GraphicalInterfaceConstants.DELETE_ASSOCIATED_FILES, 
				GraphicalInterfaceConstants.DELETE_ASSOCIATED_FILES_TITLE, 
				JOptionPane.YES_NO_OPTION, 
				JOptionPane.QUESTION_MESSAGE, 
				null, options, options[0]);
		listModel.remove(listModel.indexOf(LocalConfig.getInstance().getOptimizationFilesList().get(DynamicTree.getRow() - 1)));
		if (choice == JOptionPane.YES_OPTION) {
			File f = new File(LocalConfig.getInstance().getOptimizationFilesList().get(DynamicTree.getRow() - 1) + ".log");
			if (f.exists()) {
				Utilities.delete(LocalConfig.getInstance().getOptimizationFilesList().get(DynamicTree.getRow() - 1) + ".log");
			}
		}
		if (choice == JOptionPane.NO_OPTION) {

		}
	}

	public void deleteAllItemsFromDynamicTree() {
		Object[] options = {"    Yes    ", "    No    ",};
		int choice = JOptionPane.showOptionDialog(null, 
				GraphicalInterfaceConstants.DELETE_ASSOCIATED_FILES, 
				GraphicalInterfaceConstants.DELETE_ASSOCIATED_FILES_TITLE, 
				JOptionPane.YES_NO_OPTION, 
				JOptionPane.QUESTION_MESSAGE, 
				null, options, options[0]);
		if (choice == JOptionPane.YES_OPTION) {				
			cleanupTemporaryDirectory();
			LocalConfig.getInstance().getOptimizationFilesList().clear(); 
		}
		if (choice == JOptionPane.NO_OPTION) {

		}
	}
	
	public void enableLoadItems() {
		loadSBMLItem.setEnabled(true);
		loadCSVItem.setEnabled(true);
		loadExistingItem.setEnabled(true);
		clearItem.setEnabled(true);
		enableSaveItems(true);
		saveSBMLItem.setEnabled(true);
		saveCSVItem.setEnabled(true);
		findReplaceItem.setEnabled(true);
		selectAllItem.setEnabled(true);
	    setUpSolver.setEnabled(true);
	    gurobiParametersItem.setEnabled(true);
		glpkParametersItem.setEnabled(true);
		ipOptParametersItem.setEnabled(true);
		enableMenuItems();
	}
	
	// grays out load items so user cannot load a model while a model is loading.
	// only necessary for sbml, csv load too fast 
	public void disableLoadItems() {
		loadSBMLItem.setEnabled(false);
		loadCSVItem.setEnabled(false);
		loadExistingItem.setEnabled(false);
		clearItem.setEnabled(false);
		enableSaveItems(false);
		saveSBMLItem.setEnabled(false);
		saveCSVItem.setEnabled(false);
		findReplaceItem.setEnabled(false);	
		selectAllItem.setEnabled(false);
	    setUpSolver.setEnabled(false);
	    gurobiParametersItem.setEnabled(false);
		glpkParametersItem.setEnabled(false);
		ipOptParametersItem.setEnabled(false);
		disableMenuItems();
	}

	// enables menu items when main file is selected in analysis pane
	public void enableMenuItems() {
		if (LocalConfig.getInstance().fvaDone) {
			clearItem.setEnabled(true);
			if (LocalConfig.getInstance().getUnusedList().size() > 0) {
				highlightUnusedMetabolitesItem.setEnabled(true);
				deleteUnusedItem.setEnabled(true);
			}
			maybeDisplaySuspiciousMetabMessage(statusBarRow());	
			// since we are still testing the new behavior where clicking the Analysis menu
			// selects the original model in the tree, these items should remain until
			// it is determined that the new behavior is acceptable.
//			fbaItem.setEnabled(true);
//			gdbbItem.setEnabled(true);
//			eflux2Item.setEnabled( true );
//			spotItem.setEnabled( true );
			addReacRowItem.setEnabled(true);
			addReacRowsItem.setEnabled(true);
			addMetabRowItem.setEnabled(true);
			addMetabRowsItem.setEnabled(true);
			addReacColumnItem.setEnabled(true);
			addMetabColumnItem.setEnabled(true);
			reactionsTableEditable = true;
			saveOptFile = false;
			formulaBar.setEditable(true);		
			editorMenu.setEnabled(true);
			pastebutton.setEnabled(true);
			if (undoCount > 1) {
				enableOptionComponent(undoSplitButton, undoLabel, undoGrayedLabel);
				undoItem.setEnabled(true);
			}
			if (LocalConfig.getInstance().getRedoItemMap().size() > 0) {
				enableOptionComponent(redoSplitButton, redoLabel, redoGrayedLabel);
				redoItem.setEnabled(true);
			}
		} 
	}
	
	// disables menu items when optimization is selected in analysis pane (tree)
	public void disableMenuItems() {
		clearItem.setEnabled(false);
		highlightUnusedMetabolitesItem.setEnabled(false);
		deleteUnusedItem.setEnabled(false);
		findSuspiciousItem.setEnabled(false);
		addReacRowItem.setEnabled(false);
		addReacRowsItem.setEnabled(false);
		addMetabRowItem.setEnabled(false);
		addMetabRowsItem.setEnabled(false);
		addReacColumnItem.setEnabled(false);
		addMetabColumnItem.setEnabled(false);
		reactionsTableEditable = false;
		tabbedPane.setSelectedIndex(0);
		formulaBar.setEditable(false);
		formulaBar.setBackground(Color.WHITE);
		editorMenu.setEnabled(false);
		
		FindReplaceDialog.replaceButton.setEnabled(false);
		
		FindReplaceDialog.replaceAllButton.setEnabled(false);
		//getFindReplaceDialog().replaceFindButton.setEnabled(false);
		pastebutton.setEnabled(false);
		disableOptionComponent(undoSplitButton, undoLabel, undoGrayedLabel);
		disableOptionComponent(redoSplitButton, redoLabel, redoGrayedLabel);
		undoItem.setEnabled(false);
		redoItem.setEnabled(false);
	}
	
	/**
	 * 
	 * @param enabled
	 * Users should not be able to run an analysis while FVA is running
	 * Also any item that causes a dialog to be launched must be disabled
	 * while FVA progress bar is visible
	 */
	public void disableMenuItemsForFVA(boolean enabled) {
		fbaItem.setEnabled(!enabled);
		gdbbItem.setEnabled(!enabled);
		eflux2Item.setEnabled(!enabled);
		spotItem.setEnabled(!enabled);
		exitItem.setEnabled(!enabled);
		aboutBox.setEnabled(!enabled);
	}

	/**
	 * Enable or disable the Analysis menu items
	 * @param expr True if enabled, False otherwise
	 */
	public synchronized void setEnableAnalysisMenuItems( boolean expr )
	{
		fbaItem.setEnabled( expr );
		gdbbItem.setEnabled( expr );
		eflux2Item.setEnabled( expr );
		spotItem.setEnabled( expr );
	}
	
	public void maybeDisplaySuspiciousMetabMessage(String row) {
		try {
			if (isRoot && LocalConfig.getInstance().getSuspiciousMetabolites().size() > 0) {
				setLoadErrorMessage("Model contains suspicious metabolites.");
				// selected row default at row 1 (index 0)
				statusBar.setText("Row " + row + "                   " + getLoadErrorMessage());
				findSuspiciousItem.setEnabled(true);
			} else {
				statusBar.setText("Row " + row);
				findSuspiciousItem.setEnabled(false);
			}
		} catch (Throwable t) {
			statusBar.setText("Row " + row);
		}		
	}
	
	public String statusBarRow() {
		String row = "1";
		if (tabbedPane.getSelectedIndex() == 0) {
			if (reactionsTable.getSelectedRow() > -1) {
				row = Integer.toString((reactionsTable.getSelectedRow() + 1));
			} 
		} else if (tabbedPane.getSelectedIndex() == 1) {
			if (metabolitesTable.getSelectedRow() > -1) {
				row = Integer.toString((metabolitesTable.getSelectedRow() + 1));
			}
		}
		return row;
	}

	public void addReactionColumnCloseAction() {
    	
		ReactionColAddRenameInterface.textField.setText("");
    	getReactionColAddRenameInterface().setVisible(false);
    	getReactionColAddRenameInterface().dispose();
	}

	public void addMetaboliteColumnCloseAction() {
    	
		MetaboliteColAddRenameInterface.textField.setText("");
    	getMetaboliteColAddRenameInterface().setVisible(false);
    	getMetaboliteColAddRenameInterface().dispose();
	}
	
	public void addReactionRowsDialogCloseAction() {
		
		
		AddReactionRowsDialog.textField.setText(GraphicalInterfaceConstants.DEFAULT_NUM_ADD_ROWS);
		getAddReactionRowsDialog().setVisible(false);
		getAddReactionRowsDialog().dispose();		
	}	
	
	public void addMetaboliteRowsDialogCloseAction() {
		
		AddMetaboliteRowsDialog.textField.setText(GraphicalInterfaceConstants.DEFAULT_NUM_ADD_ROWS);
		getAddMetaboliteRowsDialog().setVisible(false);
		getAddMetaboliteRowsDialog().dispose();	
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
        TableCellEditor editor = table.getCellEditor();
        if (editor != null) {
          editor.stopCellEditing();
        }
        scrollToLocation(table, row, col);        
	}

	/*******************************************************************************/
	//progressBar methods
	/*******************************************************************************/

	public static class GDBBParam
	{
		public String string;
		public Solution solution;
		public double maxObj;
		public GDBBModel model;
		public boolean addFolder;
	}
	
	public static void addGDBBSolution( final GDBBParam param )
	{
		java.awt.EventQueue.invokeLater( new Runnable()
		{
			@Override
			public void run()
			{				
				GDBBModel model = param.model;
				Solution solution = param.solution;
				ReactionFactory rFactory = new ReactionFactory( "SBML" );
				double[] x = solution.getKnockoutVector();
				String synObjString = "";
				Vector< String > uniqueGeneAssociations = rFactory.getUniqueGeneAssociations();
				int knockoutOffset = 4*model.getNumReactions() + model.getNumMetabolites();
				Vector< Double > synthObjVector = rFactory.getSyntheticObjectiveVector();
				Vector< String > reactionAbbreviations = rFactory.getReactionAbbreviations();
				for (int i = 0; i < synthObjVector.size(); i ++) {
					if (synthObjVector.get(i) > 0) {
						synObjString += "Reaction '" + reactionAbbreviations.get(i) + "' Synthetic Objective = " + synthObjVector.get(i) + "\n";

					}
				}
				
				StringBuffer text = new StringBuffer();
				text.append("GDBB" + "\n");
				text.append(synObjString);
				text.append("Maximum Number of Knockouts = " + (int) model.getC() + "\n");
				text.append(model.getNumMetabolites() + " metabolites, " + model.getNumReactions() + " reactions, " + model.getNumGeneAssociations() + " unique gene associations\n");
				text.append("Synthetic objective: " + Double.toString(solution.getObjectiveValue()) + "\n");				
				text.append("Knockouts:");
				String kString = "";
				ArrayList< Double > soln = new ArrayList< Double >();
				ArrayList< String > listKnockOuts = new ArrayList< String >();
				for (int j = 0; j < x.length; j++)
				{
					soln.add(x[j]);
					if ((j >= knockoutOffset) && (x[j] >= 0.5)) 
					{        // compiler optimizes: boolean short circuiting
						kString += "\n\t" + uniqueGeneAssociations.elementAt(j - knockoutOffset);
						listKnockOuts.add( uniqueGeneAssociations.elementAt(j - knockoutOffset) );
					}
				}
				if (kString != null) {
					text.append(kString);
				}
				
				Model fbaModel = new Model();
				
				Double gdbbBioObj = Double.NaN;
				for( SBMLReaction reaction : fbaModel.getReactions() )
				{
					if( listKnockOuts.contains( reaction.getGeneAssociation() ) )
						reaction.setKnockout( GraphicalInterfaceConstants.BOOLEAN_VALUES[1] );
					if( reaction.getBiologicalObjective() != 0.0 )
						gdbbBioObj = soln.get( reaction.getId() );
				}
				
				FBA fba = new FBA();
				fba.setModel( fbaModel );
				try
				{
					fba.disableSolverErrors();
					fba.solve();
				}
				catch( Exception e )
				{
				}
				
				Double fbaObj = fba.getMaxObj();
				
				Double diff = fbaObj - gdbbBioObj;
				
				
				Double fbaCheckTol = 1E-1;
				
				if( diff * diff <= fbaCheckTol * fbaCheckTol )
				{
					if( param.addFolder )
					{
						Utilities u = new Utilities();
		        		final String dateTimeStamp = u.createDateTimeStamp();
		        		final String optimizeName = GraphicalInterfaceConstants.GDBB_PREFIX + LocalConfig.getInstance().getModelName() + dateTimeStamp;
		        		
						DefaultTableModel metabolitesOptModel = copyMetabolitesTableModel((DefaultTableModel) metabolitesTable.getModel());
		        		DefaultTableModel reactionsOptModel = copyReactionsTableModel((DefaultTableModel) reactionsTable.getModel());				
		        		LocalConfig.getInstance().getReactionsTableModelMap().put(optimizeName, reactionsOptModel);
		        		LocalConfig.getInstance().getMetabolitesTableModelMap().put(optimizeName, metabolitesOptModel);
		        		listModel.addElement(optimizeName);
		        		setOptimizeName(optimizeName);
		        		
						GISolution parentNode = new GISolution();
						parentNode.folderName = optimizeName;
						parentNode.isFoldered = false;
						parentNode.soln = new ArrayList< Double >();
						parentNode.stringBuffer = new StringBuffer();
						vecGISolution.add( parentNode );
						java.awt.EventQueue.invokeLater( solutionListener );
					}
					
					text.append("\n");
					text.append( "MIL solver = " + GraphicalInterface.getMixedIntegerLinearSolverName() );
					
					GISolution current = new GISolution();
					current.soln_ko  = soln;
					current.soln = new ArrayList< Double >( soln.subList( 0, model.getNumReactions() ) );
					current.stringBuffer = text;
					current.isFoldered = true;
					current.folderName = "" + solution.getObjectiveValue();
					current.knockoutOffset = new Integer( knockoutOffset );
					vecGISolution.add( current );		
					java.awt.EventQueue.invokeLater( solutionListener );
				}
			}
		} );
	}
	
	class Task extends SwingWorker<Void, Void> {

		@Override
		public void done() {
		}

		@Override
		protected Void doInBackground() throws Exception {
			loadSetUp();
			SBMLDocument doc = new SBMLDocument();
			SBMLReader reader = new SBMLReader();
			SBMLModelReader modelReader = null;
			try {
				doc = reader.readSBML(getSBMLFile());
				modelReader = new SBMLModelReader(doc);
				modelReader.load();
				setFileType(GraphicalInterfaceConstants.SBML_FILE_TYPE);
				// clear last csv for reactions and metabolites to get correct save behavior
				curSettings.add("LastCSVReactions", "none");
				curSettings.add("LastCSVMetabolites", "none");
			} catch (FileNotFoundException e) {	
				JOptionPane.showMessageDialog(null,                
						"File does not exist.",                
						"File does not exist.",                                
						JOptionPane.ERROR_MESSAGE);					
				//e.printStackTrace();		
				progressBar.setVisible(false);
				progressBar.progress.setIndeterminate(false);
				LocalConfig.getInstance().setProgress( 100 );
				enableLoadItems();
			} catch (XMLStreamException e) {
				JOptionPane.showMessageDialog(null,                
						"This File is not a Valid SBML File.",                
						"Invalid SBML File.",                                
						JOptionPane.ERROR_MESSAGE);
				// TODO Auto-generated catch block
				//e.printStackTrace();
				progressBar.setVisible(false);
				progressBar.progress.setIndeterminate(false);
				LocalConfig.getInstance().setProgress( 100 );
				enableLoadItems();
			}	
			setEnableAnalysisMenuItems( true );
			while (LocalConfig.getInstance().getProgress() < 100) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException ignore) {
				}			
			}
			Thread.sleep( 1000 );
			timer.stop();
			return null;
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
				maybeShowFVAColumns(LocalConfig.getInstance().getModelName());
				setUpReactionsTable(SBMLModelReader.getReactionsTableModel());
				LocalConfig.getInstance().getReactionsTableModelMap().put(LocalConfig.getInstance().getModelName(), SBMLModelReader.getReactionsTableModel());
				setUpMetabolitesTable(SBMLModelReader.getMetabolitesTableModel());
				LocalConfig.getInstance().getMetabolitesTableModelMap().put(LocalConfig.getInstance().getModelName(), SBMLModelReader.getMetabolitesTableModel());	
				setUpTables();
				progressBar.setVisible(false);		
				timer.stop();
				// This appears redundant, but is the only way to not have an extra progress bar on screen
				progressBar.setVisible(false);
				//progressBar.progress.setIndeterminate(true);
				saveSBML = false;
				progressBar.progress.setIndeterminate(false);
				enableLoadItems();
			}
		}
	}

	class GDBBTimeListener implements ActionListener {
		public void actionPerformed(ActionEvent ae) {			
			timeCount += 1;
			dotCount += 1;
			StringBuffer dotBuffer = new StringBuffer();
			int numDots = dotCount % (GDBBConstants.MAX_NUM_DOTS + 1);
			for (int i = 0; i < numDots; i++) {
				dotBuffer.append(" .");
			}		
			if (getGdbbDialog().finiteTimeSelected && timeCount == Integer.valueOf(getGdbbDialog().getFiniteTimeString())) {
				stopGDBBAction();
			}
			if (!gdbbStopped) {
				getGdbbDialog().getCounterLabel().setText(GDBBConstants.COUNTER_LABEL_PREFIX + timeCount + GDBBConstants.COUNTER_LABEL_SUFFIX);
			} else {
				getGdbbDialog().getCounterLabel().setText(GDBBConstants.PROCESSING + dotBuffer.toString());
			}
		}
	}
	
	// used when stop button or finite time stops GDBB
	public synchronized void stopGDBBAction() {
		getGdbbDialog().stopButton.setEnabled(false);
		gdbbStopped = true;
		dotCount = 0;
		getGdbbDialog().getCounterLabel().setText(GDBBConstants.PROCESSING);
	}
	
	/*******************************************************************************/
	//end progressBar methods
	/*******************************************************************************/

	/******************************************************************************/
	// Solver Set Up methods
	/******************************************************************************/

	public static String findGurobiPath() {
		String gurobiPath = "";

		String variable = System.getenv("GUROBI_HOME");  
		if (variable != null && variable.contains("gurobi")) {
			if (System.getProperty("os.name").equals("Linux")) {
				// path behavior not consistent, 32 bit Gurobi environmental variable
				// returns full path, 64 bit only relative path
				if (!variable.startsWith(System.getProperty("user.home"))) {
					gurobiPath = System.getProperty("user.home") + variable;
				} else {
					gurobiPath = variable;
				}
			} else {
				gurobiPath = variable;
			}
		}

		return gurobiPath;
	}
	
	/**
	 * Writes solver selections to config file.
	 */
	ActionListener solvOKActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent ae) {
			String linear = "";
			String quadratic = "";
			String nonlinear = "";
			
			if ( getSolverSetUpDialog().cbLinear.getSelectedItem().equals(GraphicalInterfaceConstants.GLPK_SOLVER_NAME)) {
				linear = GraphicalInterfaceConstants.GLPK_SOLVER_NAME;
			} else if ( getSolverSetUpDialog().cbLinear.getSelectedItem().equals(GraphicalInterfaceConstants.GUROBI_SOLVER_NAME)) {
				linear = GraphicalInterfaceConstants.GUROBI_SOLVER_NAME;
			}
			if ( getSolverSetUpDialog().cbQuadratic.getSelectedItem().equals(GraphicalInterfaceConstants.IPOPT_SOLVER_NAME)) {
				quadratic = GraphicalInterfaceConstants.IPOPT_SOLVER_NAME;
			} else if ( getSolverSetUpDialog().cbQuadratic.getSelectedItem().equals(GraphicalInterfaceConstants.GUROBI_SOLVER_NAME)) {
				quadratic = GraphicalInterfaceConstants.GUROBI_SOLVER_NAME;
			}
			if ( getSolverSetUpDialog().cbNonlinear.getSelectedItem().equals(GraphicalInterfaceConstants.IPOPT_SOLVER_NAME)) {
				nonlinear = GraphicalInterfaceConstants.IPOPT_SOLVER_NAME;
			}
			
			setMixedIntegerLinearSolverName(linear);
			setQuadraticSolverName(quadratic);
			setNonlinearSolverName(nonlinear);
			ConfigProperties.writeToFile(linear, quadratic, nonlinear);

			getSolverSetUpDialog().setVisible(false);
		}
	};
	
	public void enableGurobiItems() {
		if (getSolverSetUpDialog().cbLinear.getItemCount() == GraphicalInterfaceConstants.MIXED_INTEGER_LINEAR_OPTIONS.length) {
			getSolverSetUpDialog().cbLinear.addItem(GraphicalInterfaceConstants.GUROBI_SOLVER_NAME);
		}
		if (getSolverSetUpDialog().cbQuadratic.getItemCount() == GraphicalInterfaceConstants.QUADRATIC_OPTIONS.length) {
			getSolverSetUpDialog().cbQuadratic.addItem(GraphicalInterfaceConstants.GUROBI_SOLVER_NAME);
		}
		getSolverSetUpDialog().cbLinear.setSelectedItem(GraphicalInterfaceConstants.GUROBI_SOLVER_NAME);
		getSolverSetUpDialog().cbQuadratic.setSelectedItem(GraphicalInterfaceConstants.GUROBI_SOLVER_NAME);
		getSolverSetUpDialog().gurobiLabel.setText("<HTML>" + GraphicalInterfaceConstants.GUROBI_INSTALLED_MESSAGE + "</HTML>");
		gurobiParametersItem.setEnabled(true);
	}
	
	public void disableGurobiItems() {
		getSolverSetUpDialog().cbLinear.setSelectedItem(0);
		getSolverSetUpDialog().cbQuadratic.setSelectedItem(0);
		getSolverSetUpDialog().gurobiLabel.setText("<HTML>" + GraphicalInterfaceConstants.GUROBI_NOT_INSTALLED_PREFIX + GraphicalInterfaceConstants.GUROBI_MINIMUM_VERSION + System.getProperty("sun.arch.data.model") + GraphicalInterfaceConstants.GUROBI_NOT_INSTALLED_SUFFIX + "</HTML>");
		gurobiParametersItem.setEnabled(false);
	}
	
	/******************************************************************************/
	// end Solver Set Up methods
	/******************************************************************************/
	
	public Integer getRowFromReactionsId(int id) {
		int viewRow = 0;
		for (int j = 0; j < reactionsTable.getRowCount(); j++) {
			if (Integer.valueOf(reactionsTable.getModel().getValueAt(j, GraphicalInterfaceConstants.REACTIONS_ID_COLUMN).toString()) == id) {
				viewRow = reactionsTable.convertRowIndexToView(j);
			}
		}
		return viewRow;
	}
	
	public Integer getRowFromMetabolitesId(int id) {
		int viewRow = 0;
		for (int j = 0; j < metabolitesTable.getRowCount(); j++) {
			if (Integer.valueOf(metabolitesTable.getModel().getValueAt(j, GraphicalInterfaceConstants.METABOLITE_ID_COLUMN).toString()) == id) {
				viewRow = metabolitesTable.convertRowIndexToView(j);
			}
		}
		return viewRow;
	}
	
	class OpenUrlAction implements ActionListener {
		@Override public void actionPerformed(ActionEvent e) {
			openURL();	
		}
	}
	
	public void openURL() {
		java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
		if( !desktop.isSupported( java.awt.Desktop.Action.BROWSE ) ) {
			//System.err.println( "Desktop doesn't support the browse action (fatal)" );
			//System.exit( 1 );
			JOptionPane.showMessageDialog(null,                
					"Default Browser Error. Default Browser May Not Be Set On This System.",                
					"Default Browser Error",                                
					JOptionPane.ERROR_MESSAGE); 
		} else {
			try{ 
				java.awt.Desktop.getDesktop().browse(java.net.URI.create(getUrlString()));  
			}  
			catch (java.io.IOException e1) {  
				JOptionPane.showMessageDialog(null,                
						GraphicalInterfaceConstants.URL_NOT_FOUND_MESSAGE,                
						GraphicalInterfaceConstants.URL_NOT_FOUND_TITLE,                                
						JOptionPane.ERROR_MESSAGE);   
			}
		}
	}
	
	/**
	 * Deletes log files in temporary directory.
	 */
	public static void cleanupTemporaryDirectory() {
		String tempFilesDir = "";
		if (System.getProperty("os.name").equals("Windows 7") || System.getProperty("os.name").equals("Windows 8") || System.getProperty("os.name").equals("Windows Vista")) {
			tempFilesDir = SettingsConstants.SETTINGS_PATH_PREFIX_WINDOWS_7 + System.getProperty("user.name") + SettingsConstants.SETTINGS_PATH_SUFFIX_WINDOWS_7 + SettingsConstants.FOLDER_NAME;
		} else if (System.getProperty("os.name").equals("Windows XP")) {
			tempFilesDir = SettingsConstants.SETTINGS_PATH_PREFIX_WINDOWS_XP + System.getProperty("user.name") + SettingsConstants.SETTINGS_PATH_SUFFIX_WINDOWS_XP + SettingsConstants.FOLDER_NAME;
		}
		// based on http://stackoverflow.com/questions/4917326/how-to-iterate-over-the-files-of-a-certain-directory-in-java
		File dir = new File(tempFilesDir);
		File[] directoryListing = dir.listFiles();
		if (directoryListing != null) {
			for (File child : directoryListing) {
				Utilities u = new Utilities();
				// if there is ever a reason to not delete any log files not of these type in future
				// this code will need to be used.
//				if (child.getName().startsWith(GraphicalInterfaceConstants.OPTIMIZATION_PREFIX) ||
//						child.getName().startsWith(GraphicalInterfaceConstants.GDBB_PREFIX)) {
//					u.deleteFileIfExists(child.getAbsolutePath());
//				}
				if (child.getName().endsWith("log")) {
					u.deleteFileIfExists(child.getAbsolutePath());
				} else if (child.getName().endsWith("mps")) {
					u.deleteFileIfExists(child.getAbsolutePath());
				}
			}
		} 
	}
	
	/**
	 * Get solver names from config file if exists
	 */
	public static void getSolverFromConfigProperties() {
		// set default solvers in order to avoid having null solver names
		setMixedIntegerLinearSolverName(GraphicalInterfaceConstants.DEFAULT_MIXED_INTEGER_SOLVER_NAME);
		setQuadraticSolverName(GraphicalInterfaceConstants.DEFAULT_QUADRATIC_SOLVER_NAME);
		setNonlinearSolverName(GraphicalInterfaceConstants.DEFAULT_NONLINEAR_SOLVER_NAME);
		// get solver names from config file if exists
		ConfigProperties configProp = new ConfigProperties();
		if (configProp.fileExists()) {
			ConfigProperties.readFile();
			if (ConfigProperties.getMixedIntegerLinearSolverName() != null) {
				if (ConfigProperties.getMixedIntegerLinearSolverName().equals(GraphicalInterfaceConstants.GLPK_SOLVER_NAME)) {
					setMixedIntegerLinearSolverName(GraphicalInterfaceConstants.GLPK_SOLVER_NAME);
				} else if (ConfigProperties.getMixedIntegerLinearSolverName().equals(GraphicalInterfaceConstants.GUROBI_SOLVER_NAME)) {
					setMixedIntegerLinearSolverName(GraphicalInterfaceConstants.GUROBI_SOLVER_NAME);
				}
			}
			if (ConfigProperties.getQuadraticSolverName() != null) {
				if (ConfigProperties.getQuadraticSolverName().equals(GraphicalInterfaceConstants.IPOPT_SOLVER_NAME)) {
					setQuadraticSolverName(GraphicalInterfaceConstants.IPOPT_SOLVER_NAME);
				} else if (ConfigProperties.getQuadraticSolverName().equals(GraphicalInterfaceConstants.GUROBI_SOLVER_NAME)) {
					setQuadraticSolverName(GraphicalInterfaceConstants.GUROBI_SOLVER_NAME);
				}
			}
			if (ConfigProperties.getNonlinearSolverName() != null) {
				if (ConfigProperties.getNonlinearSolverName().equals(GraphicalInterfaceConstants.IPOPT_SOLVER_NAME)) {
					setNonlinearSolverName(GraphicalInterfaceConstants.IPOPT_SOLVER_NAME);
				} 
			}
		}
	}
	
	public void maybeShowFVAColumns(String solutionName) {
		if (LocalConfig.getInstance().getShowFVAColumnsList().contains(solutionName)) {
			LocalConfig.getInstance().fvaColumnsVisible = true;
		} else {
			LocalConfig.getInstance().fvaColumnsVisible = false;
		}
	}
	
	public static void main(String[] args) {
	//public static void main(String[] args) throws Exception {
		ResizableDialog dialog = new ResizableDialog("Error", "Error", "Error");
		dialog.setLocationRelativeTo(null);
		try {
			curSettings = new SettingsFactory();
			
			cleanupTemporaryDirectory();
			getSolverFromConfigProperties();

			//based on code from http://stackoverflow.com/questions/6403821/how-to-add-an-image-to-a-jframe-title-bar
			final ArrayList<Image> icons = new ArrayList<Image>(); 
			icons.add(new ImageIcon("etc/most16.jpg").getImage()); 
			icons.add(new ImageIcon("etc/most32.jpg").getImage());

			GraphicalInterface frame = new GraphicalInterface();
			frame.setIconImages(icons);
			frame.setSize(1000, 610);
			frame.setMinimumSize(new Dimension(800, 610));
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
			
			// based on http://iitdu.forumsmotion.com/t593-java-swing-adding-confirmation-dialogue-for-closing-window-in-jframe
			// prevents window from closing when cancel button is pressed in Save Changes Prompt
			frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			
			showPrompt = true;
			
			// selected row default at first row
			statusBar.setText("Row 1");
			
//			Map<String, String> env = System.getenv();
//	        for (String envName : env.keySet()) {
//	            System.out.format("%s=%s%n", envName, env.get(envName));
//	        }
		} catch (Exception e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			dialog.setErrorMessage(errors.toString());
			dialog.setVisible(true);
		}		
	}
}


