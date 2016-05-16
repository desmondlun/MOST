package edu.rutgers.MOST.presentation;

import java.awt.Color;
import java.awt.Font;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

public class PathwaysFrameConstants {

	public static final String NODE_INFORMATION_TITLE = "Node Information";
	
	public static final float SCALING_FACTOR = (float) 1.2;
	public static final float START_SCALING_FACTOR = (float) 0.1;
	public static final int GRAPH_WIDTH = 24000;
	public static final int GRAPH_HEIGHT = 18000;
//	public static final int GRAPH_WIDTH = 8000;
//	public static final int GRAPH_HEIGHT = 12000;
	public static final int GRAPH_WINDOW_WIDTH = 1000;
	public static final int GRAPH_WINDOW_HEIGHT = 600;
	public static final int TRANSPORT_WIDTH_INCREMENT = 200;
	public static final int TRANSPORT_HEIGHT_INCREMENT = 100;
//	public static final double BORDER_LEFT = -2;
//	public static final double BORDER_RIGHT = 43;
//	public static final double BORDER_TOP = -1;
//	public static final double BORDER_BOTTOM = 31;
	public static final double BORDER_LEFT = 0.1;
	public static final double BORDER_RIGHT = 45.5;
	public static final double BORDER_TOP = 0.1;
	public static final double BORDER_BOTTOM = 32.5;
	
	public static final int OFFSET_WIDTH = 140;
	public static final int OFFSET_HEIGHT = 70;
	
	public static final double BORDER_THICKNESS = 4;
	
	public static final int TOP_SPACE = 300;
	//public static final int START_Y = 5600;
	public static final int START_Y = 6000;
   	
	public static final int HORIZONTAL_INCREMENT = 400; 
	public static final int VERTICAL_INCREMENT = 380; // was 360, change made TCA cycle more circular
	public static final int PATHWAY_NAME_NODE_WIDTH = 200;
	public static final int PATHWAY_NAME_NODE_HEIGHT = 40;
	public static final int FLUX_RANGE_NODE_WIDTH = 600;
	public static final int FLUX_RANGE_NODE_HEIGHT = 48;
	public static final int COMPARTMENT_LABEL_NODE_WIDTH = 4000;
	public static final int COMPARTMENT_LABEL_NODE_HEIGHT = 230;
	public static final int COMPARTMENT_LABEL_X_OFFSET = 2000;
	public static final int COMPARTMENT_LABEL_Y_OFFSET = 115;
	public static final int COMPARTMENT_LABEL_LINE_OFFSET = 100;
	public static final int ADDITIONAL_COMPARTMENT_OFFSET = 440;
	public static final int COMPARTMENT_LABEL_LEFT_PADDING = 25;
	public static final int COMPARTMENT_LABEL_TOP_PADDING = 25;
	public static final int LEGEND_LABEL_NODE_WIDTH = 700;
	public static final int LEGEND_LABEL_NODE_HEIGHT = 150;
	public static final int LEGEND_LABEL_X_OFFSET = 100;
	public static final int LEGEND_LABEL_Y_OFFSET = 150;
	
	public static final int LEGEND_LABEL_X_POSITION = 930;
	public static final int LEGEND_LABEL_Y_POSITION = 500;
	
	public static final int FLUX_RANGE_START_X_POSITION = 550;
	public static final int FLUX_RANGE_START_Y_POSITION = 600;
	
	public static final int FLUX_RANGE_START_Y_INCREMENT = 50;
	public static final int FLUX_RANGE_EDGE_X_OFFSET = 300;
	public static final int FLUX_RANGE_EDGE_LENGTH = 500;
	
	public static final int LEGEND_BORDER_LEFT_X = 200;
	public static final int LEGEND_BORDER_RIGHT_X = 1440;
	public static final int LEGEND_BORDER_TOP_Y = 400;
	public static final int LEGEND_BORDER_BOTTOM_Y = 1100;
	
	public static final int METABOLITE_NO_BORDER_NODE_WIDTH = 90;
	public static final int METABOLITE_NO_BORDER_NODE_HEIGHT = 28;
	public static final int METABOLITE_BORDER_NODE_WIDTH = 100;
	public static final int METABOLITE_BORDER_NODE_HEIGHT = 36;
	public static final int SMALL_MAIN_METABOLITE_NODE_WIDTH = 70;
	public static final int SMALL_MAIN_METABOLITE_NODE_HEIGHT = 36;
	public static final int SIDE_METABOLITE_NODE_WIDTH = 60;
	public static final int SIDE_METABOLITE_NODE_HEIGHT = 20;
	public static final int REACTION_NODE_WIDTH = 120;
	public static final int REACTION_NODE_HEIGHT = 36;
	public static final int PATHWAY_NAME_NODE_FONT_SIZE = 36;
	public static final int FLUX_RANGE_NODE_FONT_SIZE = 27;
	public static final int COMPARTMENT_LABEL_NODE_FONT_SIZE = 100;
	public static final int LEGEND_LABEL_NODE_FONT_SIZE = 90;
	public static final int METABOLITE_NODE_FONT_SIZE = 27;
	public static final int SMALL_MAIN_METABOLITE_NODE_FONT_SIZE = 28;
	public static final int SIDE_METABOLITE_NODE_FONT_SIZE = 12;
	public static final int REACTION_NODE_FONT_SIZE = 27;
	public static final int SIDE_NODE_FONT_SIZE = 17;
	public static final int METABOLITE_NODE_MAX_CHARS = 5;
	public static final int SMALL_MAIN_METABOLITE_NODE_MAX_CHARS = 3;
	public static final int SIDE_METABOLITE_NODE_MAX_CHARS = 5;
	public static final int REACTION_NODE_MAX_CHARS = 7;
	public static final int PATHWAY_NAME_NODE_MAX_CHARS = 10;
	public static final int COMPARTMENT_LABEL_NODE_MAX_CHARS = 80;
	
	public static final String REACTION_CORRECTION_TYPE = "r";
	public static final String METABOLITE_CORRECTION_TYPE = "m";
	
	public static final String FONT_NAME = "Arial";
	public static final String FONT_WEIGHT = "bold";
	public static final int FONT_STYLE = Font.BOLD;
//	public static final String FONT_NAME = "Lucida Console";
//	public static final String FONT_WEIGHT = "plain";
//	public static final int FONT_STYLE = Font.PLAIN;
	
	public static final int METABOLITE_NODE_ELLIPSIS_CORRECTION = 2;
	public static final int SMALL_MAIN_METABOLITE_NODE_ELLIPSIS_CORRECTION = 2;
	public static final int SIDE_METABOLITE_NODE_ELLIPSIS_CORRECTION = 2;
	public static final int REACTION_NODE_ELLIPSIS_CORRECTION = 2;
	public static final int PATHWAY_NAME_NODE_ELLIPSIS_CORRECTION = 2;
	public static final int COMPARTMENT_LABEL_NODE_ELLIPSIS_CORRECTION = 2;
	
	// positions to start text in node
	public static final int PATHWAY_NAME_NODE_XPOS = 0;
	public static final int PATHWAY_NAME_NODE_YPOS = 34;
	public static final int FLUX_RANGE_NODE_XPOS = 20;
	public static final int FLUX_RANGE_NODE_YPOS = 34;
	public static final int COMPARTMENT_LABEL_NODE_XPOS = 30;
	public static final int COMPARTMENT_LABEL_NODE_YPOS = 100;
	public static final int LEGEND_LABEL_NODE_XPOS = 30;
	public static final int LEGEND_LABEL_NODE_YPOS = 100;
	public static final int METABOLITE_NODE_XPOS = 0;
	public static final int METABOLITE_NODE_YPOS = 23;
	public static final int SMALL_MAIN_METABOLITE_NODE_XPOS = 0;
	public static final int SMALL_MAIN_METABOLITE_NODE_YPOS = 23;
	public static final int SIDE_METABOLITE_NODE_XPOS = 0;
	public static final int SIDE_METABOLITE_NODE_YPOS = 13;
	public static final int REACTION_NODE_XPOS = 0;
	public static final int REACTION_NODE_YPOS = 23;
	public static final int SIDE_NODE_XPOS = 0;
	public static final int SIDE_NODE_YPOS = 23;
	
	public static final int PATHWAY_NAME_BORDER_WIDTH = 4;
	public static final int METABOLITE_BORDER_WIDTH = 4;
	public static final int SIDE_METABOLITE_BORDER_WIDTH = 2;
	
	public static final double DEFAULT_EDGE_WIDTH = 1.0; 
//	public static final int ARROW_LENGTH = 25; 
//	public static final int ARROW_WIDTH = 30;
//	public static final int ARROW_NOTCH = 12;
//	public static final int ARROW_LENGTH = 16;
	public static final int ARROW_LENGTH = 24;
	public static final int ARROW_WIDTH = 16;
	public static final double DEFAULT_ARROW_SIZE = 0.5; 
	
	public static final int ARROW_NOTCH = 0;
//	public static final int ARROW_NOTCH = 10;
	public static final double INFINITE_FLUX_RATIO = 0.95;
	public static final double INFINITE_FLUX_WIDTH = 12.0;
	// if secondary max flux is user set, there is a gap between secondary max flux
	// and 95% of max flux. this width fills that gap
	public static final double ABOVE_SECONDARY_MAX_FLUX_WIDTH = 10.0;
	public static final double SECONDARY_MAX_FLUX_WIDTH = 8.0;
	public static final double TOP_FLUX_RATIO = 0.5;
	public static final double TOP_FLUX_WIDTH = 6.0;
	public static final double MID_FLUX_RATIO = 0.25;
	public static final double MID_FLUX_WIDTH = 4.0;
	public static final double LOW_MID_FLUX_RATIO = 0.125;
	public static final double LOW_MID_FLUX_WIDTH = 3.0;
	public static final double LOWER_MID_FLUX_RATIO = 0.05;
	public static final double LOWER_MID_FLUX_WIDTH = 2.0;
	public static final double MINIMUM_FLUX_RATIO = 0.02;
	public static final double MINIMUM_FLUX_WIDTH = 1.0;
	
	public static final double[] FLUX_WIDTH_RATIOS = {
		MINIMUM_FLUX_RATIO,
		LOWER_MID_FLUX_RATIO,
		LOW_MID_FLUX_RATIO,
		MID_FLUX_RATIO,
		TOP_FLUX_RATIO};
	
	public static final double[] FLUX_WIDTHS = {
		MINIMUM_FLUX_WIDTH,
		LOWER_MID_FLUX_WIDTH,
		LOW_MID_FLUX_WIDTH,
		MID_FLUX_WIDTH,
		TOP_FLUX_WIDTH};
	
	public static final String ZERO_FLUX_WIDTH_NAME = "width_0.0";
	public static final String WIDTH_PREFIX = "width_";
	public static final String WIDTH_RIGHT_NODE_SUFFIX = "_2";
	
	public static final int PATHWAYS_COMPONENT = 0;
	public static final int PROCESSES_COMPONENT = 1;
	
	public static final String PERIPLASM_SUFFIX = "_p";
	public static final String EXTRAORGANISM_SUFFIX = "_e";
	
	// prevents nodes with calculated positions from touching
	public static final double NODE_SPACING_CORRECTION = 1.1;
	
//	public static final Color PATHWAY_NAME_COLOR = Color.orange;
	public static final Color PATHWAY_NAME_COLOR = new Color(255,153,51);
	
	public static final Color NODE_BACKGROUND_DETAULT_COLOR = Color.white;
//	public static final Color METABOLITE_NODE_DETAULT_COLOR = Color.white;
//	public static final Color REACTION_NODE_DETAULT_COLOR = Color.white;
	public static final Color REACTION_NODE_DETAULT_FONT_COLOR = Color.blue;
	
	public static final Color REACTION_KO_FONT_COLOR = Color.red;
	
	// not found colors from http://www.rapidtables.com/web/color/RGB_Color.htm
	// light blue
	public static final Color REACTION_NOT_FOUND_FONT_COLOR = new Color(224,224,255);
	// one step darker 
	//public static final Color REACTION_NOT_FOUND_FONT_COLOR = new Color(153,204,255);
	// light gray
	public static final Color METABOLITE_NOT_FOUND_COLOR = new Color(224,224,224);
	// one step darker 
	//public static final Color METABOLITE_NOT_FOUND_COLOR = new Color(192,192,192);
	
	public static final Color PATHWAY_NAME_NOT_FOUND_COLOR = new Color(255,224,192);

	public static final Color COFACTOR_COLOR = Color.red;
	public static final Color COFACTOR_NOT_FOUND_COLOR = new Color(255,224,224);
	
	// formerly used yellow and orange, but this is confusing since some metabolites
	// have no borders using the Roche map convention
	//public static final Color METABOLITE_NOT_FOUND_COLOR = Color.yellow;
	//public static final Color REACTION_NOT_FOUND_FONT_COLOR = Color.orange;
	
	public static final double BLACK_COLOR_VALUE = 0.0;
	public static final double GRAY_COLOR_VALUE = 1.0;
	public static final double RED_COLOR_VALUE = 2.0;
	public static final double GREEN_COLOR_VALUE = 3.0;
	public static final double BLUE_COLOR_VALUE = 4.0;
	public static final double BLUE_NOT_FOUND_COLOR_VALUE = 5.0;
	
	public static final String BLACK_ARROW_NAME = "arrow-black";
	public static final String GRAY_ARROW_NAME = "arrow-gray";
	public static final String RED_ARROW_NAME = "arrow-red";
	public static final String NOT_FOUND_ARROW_NAME = "arrow-nf";
	
	public static final Color REACTION_EDGE_NOT_FOUND_COLOR = new Color(153,204,255);
	
	public static final double DEFAULT_COLOR_VALUE = BLACK_COLOR_VALUE;
	
	public static final String[] COFACTOR_KEGG_IDS = 
		{ 
		"C00002", "C00003", "C00004", "C00005", "C00006", "C00008", 
		"C00010", "C00015", "C00016", "C00020",  
		"C00028", "C00030", "C00035",
		"C00044", "C00055", "C00061", "C00063", "C00068",
		"C00075", "C00101", "C00105", "C00112", "C00131", 
		"C00138", "C00139", "C00143", "C00144", "C00206",
		"C00234","C00239", "C00268", "C00272", 
		"C00282", "C00342", "C00343",
		"C00360","C00361", "C00362", "C00363", "C00364", "C00365", 
		"C00445", "C00664", "C00725", "C01352", "C01847", 
		"C02051", "C02147", 
		"C02972", "C04253", "C04570"
		};

	public static java.util.List<String> cofactorList = Arrays.asList(COFACTOR_KEGG_IDS);
	
	public static final DecimalFormat FLUX_FORMATTER = new DecimalFormat("#.####");
	public static final DecimalFormat SCIENTIFIC_FLUX_FORMATTER = new DecimalFormat("#.##E0");
	public static final double MIN_DECIMAL_FORMAT = 0.001;
	public static final double MAX_DECIMAL_FORMAT = 1000;
	
}
