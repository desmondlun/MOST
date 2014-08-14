package edu.rutgers.MOST.presentation;

public class GDBBConstants {

	public static final String GDBB_DIALOG_TITLE = GraphicalInterfaceConstants.TITLE + " - " + "GDBB";
	
	public static final String NUM_KNOCKOUTS_LABEL = "   Number of Knockouts";
	public static final String NUM_THREADS_LABEL = "   Number of Threads";
	public static final String SYN_OBJ_COLUMN_LABEL = "   Synthetic Objective Vector";
	public static final String INDEFINITE_TIME_LABEL = "Indefinite Optimizer Time";
	public static final String FINITE_TIME_LABEL = "Finite Optimizer Time Limit (sec)";
	public static final String COUNTER_LABEL_PREFIX = "Time Elapsed: ";
	public static final String COUNTER_LABEL_SUFFIX = " s";
	
	public static final String NUM_KNOCKOUTS_DEFAULT = "1";
	public static final int MAX_NUM_THREADS = Runtime.getRuntime().availableProcessors();
	public static final String FINITE_TIME_DEFAULT = "300";
	
	public static final int GDBB_DIALOG_WIDTH = 400;
	public static final int GDBB_DIALOG_HEIGHT = 320;
	
	// layout constants
	public static final int COMPONENT_WIDTH = 150;
	public static final int COMPONENT_HEIGHT = 25;
	public static final int LABEL_WIDTH = 200;
	public static final int LABEL_HEIGHT = 25;
	public static final int LABELED_BUTTON_WIDTH = 220;
	public static final int LABELED_BUTTON_HEIGHT = 25;
	
	public static final int LABELED_BUTTON_TOP_GAP = 20;
	public static final int LABELED_BUTTON_BOTTOM_GAP = 30;
	
	public static final String PROCESSING = "Processing";
	public static final String PROCESSING_OOT = " .";
	public static final int MAX_NUM_DOTS = 6;
	
	public static final String FRAME_CLOSE_TITLE = "GDBB";
	public static final String FRAME_CLOSE_MESSAGE = "GDBB is Running. Are You Sure You Wish to Stop Optimizing?";
	
}
