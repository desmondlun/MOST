package edu.rutgers.MOST.data;

import java.util.Arrays;

public class PathwaysCSVFileConstants {

	public static final String PATHWAYS_FILE_NAME = "etc/visualization/pathways.csv";
	public static final String PATHWAY_NAMES_FILE_NAME = "etc/visualization/pathway_names.csv";
	public static final String PATHWAY_GRAPH_FILE_NAME = "etc/visualization/pathway_graphing_data.csv";
	public static final String METABOLITES_FILE_NAME = "etc/visualization/metabolites.csv";
	public static final String METABOLITE_POSITIONS_FILE_NAME = "etc/visualization/pathway_metabolite_positions.csv";
	public static final String REACTIONS_FILE_NAME = "etc/visualization/reactions.csv";
	public static final String REACTION_POSITIONS_FILE_NAME = "etc/visualization/pathways_reaction_positions.csv";
	public static final String PATHWAY_SIDE_SPECIES_FILE_NAME = "etc/visualization/pathway_side_species.csv";
	public static final String CHEBI_IDS_KEGG_IDS_FILE_NAME = "etc/visualization/chebiIds_keggIds.csv";
	// metabolite substitutions handle situations such where multiple compounds can be mapped to the same node 
	// such as where NAD or NADP, ATP or GTP can be in the same reaction, or classes of compounds such as
	// fatty acids, of where models do not distinguish between alpha-Glucose, beta-Glucose and D-Glucose
	public static final String METABOLITE_SUBSTITUTIONS_FILE_NAME = "etc/visualization/metabolite_substitutions.csv";
	// metabolite alternatives are used for species such as ATP, NAD where other species such as GTP or NADP can
	// occur in place of the former species, but otherwise the reaction is the same. this data is only used when
	// the species is a "side" species so it is not interchangeable with the above data.
	public static final String METABOLITE_ALTERNATIVES_FILE_NAME = "etc/visualization/metabolite_alternates.csv";
	
	public static final String[] PATHWAYS_COLUMN_NAMES = 
		{
		"Pathway ID", "Pathway Name", "KEGG ID"
		};
	
	public static final String[] PATHWAY_NAMES_COLUMN_NAMES = 
		{
		"Pathway ID", "Pathway KEGG Ids", "Pathway Level", "Pathway Level Position", "Pathway Name", "Metabolites"
		};
	
	public static final String[] PATHWAY_GRAPH_COLUMN_NAMES = 
		{
		"Pathway ID", "Pathway Name", "Component"
		};
	
	public static final String[] METABOLITES_COLUMN_NAMES = 
		{
		"KEGG ID", "Names", "Occurence"
		};
	
	public static final String[] METABOLITE_SUBSTITUTIONS_COLUMN_NAMES = 
		{
		"KEGG ID", "Additional KEGG IDs", "Name"
		};
	
	public static final String[] METABOLITE_ALTERNATIVES_COLUMN_NAMES = 
		{
		"KEGG ID", "Additional KEGG IDs", "Name"
		};
	
	public static final String[] METABOLITE_POSITIONS_COLUMN_NAMES = 
		{
		"Pathway ID", "Metabolite ID", "Metabolite Level", "Metabolite Level Position", 
		"Metabolite Names", "Metabolite Abbreviation", "KEGG ID", "Border", "Type"
		};
	
	public static final String[] REACTIONS_COLUMN_NAMES = 
		{
		"Reaction KEGG ID",	"Reactants", "Products", "Reversability", "EC list", "Occurences", "Names"
		};
	
	public static final String[] REACTION_POSITIONS_COLUMN_NAMES = 
		{
		"Pathway ID", "Reaction ID", "Reactants", "Products", "Reversible", "EC # List", 
		"Level", "Level Position", "KEGG IDs"
		};
	
	public static final String[] PATHWAY_SIDE_SPECIES_COLUMN_NAMES = 
		{
		"Species Name"
		};

	public static final String[] CHEBL_ID_KEGG_ID_COLUMN_NAMES = 
		{
		"CHEBI ID", "KEGG ID"
		};
	
	private static java.util.List<String> pathwayColumnsList = Arrays.asList(PATHWAYS_COLUMN_NAMES);
	private static java.util.List<String> pathwayNamesColumnsList = Arrays.asList(PATHWAY_NAMES_COLUMN_NAMES);
	private static java.util.List<String> pathwayGraphColumnsList = Arrays.asList(PATHWAY_GRAPH_COLUMN_NAMES);
	private static java.util.List<String> metabolitesList = Arrays.asList(METABOLITES_COLUMN_NAMES);
	private static java.util.List<String> metabolitePositionsList = Arrays.asList(METABOLITE_POSITIONS_COLUMN_NAMES);
	private static java.util.List<String> reactionsColumnsList = Arrays.asList(REACTIONS_COLUMN_NAMES);
	private static java.util.List<String> reactionPositionsColumnsList = Arrays.asList(REACTION_POSITIONS_COLUMN_NAMES);
	private static java.util.List<String> pathwaySideSpeciesColumnsList = Arrays.asList(PATHWAY_SIDE_SPECIES_COLUMN_NAMES);
	private static java.util.List<String> metaboliteSubstitutionsColumnsList = Arrays.asList(METABOLITE_SUBSTITUTIONS_COLUMN_NAMES);
	private static java.util.List<String> metaboliteAlternativesColumnsList = Arrays.asList(METABOLITE_ALTERNATIVES_COLUMN_NAMES);
	private static java.util.List<String>chebiIdsKeggIdsColumnsList = Arrays.asList(CHEBL_ID_KEGG_ID_COLUMN_NAMES);

	public static final int PATHWAYS_ID_COLUMN = pathwayColumnsList.indexOf("Pathway ID");
	public static final int PATHWAYS_NAME_COLUMN = pathwayColumnsList.indexOf("Pathway Name");
	public static final int PATHWAYS_KEGG_ID_COLUMN = pathwayColumnsList.indexOf("KEGG ID");
	
	public static final int PATHWAY_NAMES_ID_COLUMN = pathwayNamesColumnsList.indexOf("Pathway ID");
	public static final int PATHWAY_NAMES_KEGG_IDS_COLUMN = pathwayNamesColumnsList.indexOf("Pathway KEGG Ids");
	public static final int PATHWAY_NAMES_LEVEL_COLUMN = pathwayNamesColumnsList.indexOf("Pathway Level");
	public static final int PATHWAY_NAMES_LEVEL_POSITION_COLUMN = pathwayNamesColumnsList.indexOf("Pathway Level Position");
	public static final int PATHWAY_NAMES_NAME_COLUMN = pathwayNamesColumnsList.indexOf("Pathway Name");
	public static final int PATHWAY_NAMES_METABOLITES_COLUMN = pathwayNamesColumnsList.indexOf("Metabolites");
	
	public static final int PATHWAY_GRAPH_ID_COLUMN = pathwayGraphColumnsList.indexOf("Pathway ID");
	// pathway name column is redundant and not used except for making it
	// easier to see which pathway is which. name data comes from pathways.csv file
	public static final int PATHWAY_GRAPH_NAME_COLUMN = pathwayGraphColumnsList.indexOf("Pathway Name");
	public static final int PATHWAY_GRAPH_COMPONENT_COLUMN = pathwayGraphColumnsList.indexOf("Component");

	public static final int METABOLITES_KEGG_ID_COLUMN = metabolitesList.indexOf("KEGG ID");
	public static final int METABOLITES_NAMES_COLUMN = metabolitesList.indexOf("Names");
	public static final int METABOLITES_OCCURENCE_COLUMN = metabolitesList.indexOf("Occurence");
	
	public static final int METABOLITE_SUBSTITUTIONS_KEGG_ID_COLUMN = metaboliteSubstitutionsColumnsList.indexOf("KEGG ID");
	public static final int METABOLITE_SUBSTITUTIONS_ALTERNATE_KEGG_IDS_COLUMN = metaboliteSubstitutionsColumnsList.indexOf("Additional KEGG IDs");
	public static final int METABOLITE_SUBSTITUTIONS_NAME_COLUMN = metaboliteSubstitutionsColumnsList.indexOf("Name");
	
	public static final int METABOLITE_ALTERNATIVES_KEGG_ID_COLUMN = metaboliteAlternativesColumnsList.indexOf("KEGG ID");
	public static final int METABOLITE_ALTERNATIVES_ALTERNATE_KEGG_IDS_COLUMN = metaboliteAlternativesColumnsList.indexOf("Additional KEGG IDs");
	public static final int METABOLITE_ALTERNATIVES_NAME_COLUMN = metaboliteAlternativesColumnsList.indexOf("Name");
	
	public static final int METABOLITE_POSITIONS_ID_COLUMN = metabolitePositionsList.indexOf("Pathway ID");
	public static final int METABOLITE_POSITIONS_METABOLITE_ID_COLUMN = metabolitePositionsList.indexOf("Metabolite ID");
	public static final int METABOLITE_POSITIONS_LEVEL_COLUMN = metabolitePositionsList.indexOf("Metabolite Level");
	public static final int METABOLITE_POSITIONS_POSITION_COLUMN = metabolitePositionsList.indexOf("Metabolite Level Position");
	// name column is redundant and not used except for making it
	// easier to see which pathway is which. name data comes from metabolites.csv file
	public static final int METABOLITE_POSITIONS_NAME_COLUMN = metabolitePositionsList.indexOf("Metabolite Names");
	// abbreviation still used if metabolite not found in model
	public static final int METABOLITE_POSITIONS_ABBR_COLUMN = metabolitePositionsList.indexOf("Metabolite Abbreviation");
	public static final int METABOLITE_POSITIONS_KEGG_ID_COLUMN = metabolitePositionsList.indexOf("KEGG ID");
	public static final int METABOLITE_POSITIONS_BORDER_COLUMN = metabolitePositionsList.indexOf("Border");
	public static final int METABOLITE_POSITIONS_TYPE_COLUMN = metabolitePositionsList.indexOf("Type");
	
	public static final int REACTIONS_KEGG_ID_COLUMN = reactionsColumnsList.indexOf("Reaction KEGG ID");
	public static final int REACTIONS_KEGG_REACTANTS_COLUMN = reactionsColumnsList.indexOf("Reactants");
	public static final int REACTIONS_KEGG_PRODUCTS_COLUMN = reactionsColumnsList.indexOf("Products");
	public static final int REACTIONS_REVERSABILITY_COLUMN = reactionsColumnsList.indexOf("Reversability");
	public static final int REACTIONS_EC_LIST_COLUMN = reactionsColumnsList.indexOf("EC list");
	public static final int REACTIONS_OCCURENCES_COLUMN = reactionsColumnsList.indexOf("Occurences");
	public static final int REACTIONS_NAMES_COLUMN = reactionsColumnsList.indexOf("Names");
	
	public static final int REACTIONS_PATHWAY_ID_COLUMN = reactionPositionsColumnsList.indexOf("Pathway ID");
	public static final int REACTIONS_REACTION_ID_COLUMN = reactionPositionsColumnsList.indexOf("Reaction ID");
	public static final int REACTIONS_REACTANTS_COLUMN = reactionPositionsColumnsList.indexOf("Reactants");
	public static final int REACTIONS_PRODUCTS_COLUMN = reactionPositionsColumnsList.indexOf("Products");
	public static final int REACTIONS_REVERSIBLE_COLUMN = reactionPositionsColumnsList.indexOf("Reversible");
	public static final int REACTIONS_EC_NUM_LIST_COLUMN = reactionPositionsColumnsList.indexOf("EC # List");
	public static final int REACTIONS_LEVEL_COLUMN = reactionPositionsColumnsList.indexOf("Level");
	public static final int REACTIONS_POSITION_COLUMN = reactionPositionsColumnsList.indexOf("Level Position");
	public static final int REACTIONS_POSITION_KEGG_IDS_COLUMN = reactionPositionsColumnsList.indexOf("KEGG IDs");

	public static final int PATHWAY_SIDE_SPECIES_NAME_COLUMN = pathwaySideSpeciesColumnsList.indexOf("Species Name");
	
	public static final int CHEBI_IDS_KEGG_IDS_CHEBI_ID_COLUMN = chebiIdsKeggIdsColumnsList.indexOf("CHEBI ID");
	public static final int CHEBI_IDS_KEGG_IDS_KEGG_ID_COLUMN = chebiIdsKeggIdsColumnsList.indexOf("KEGG ID");
	
	public static final String MAIN_METABOLITE_TYPE = "m";
	public static final String SMALL_MAIN_METABOLITE_TYPE = "sm";
	public static final String SIDE_METABOLITE_TYPE = "s";
	
	public static final String FORWARD_DIRECTION = "f";
	public static final String REVERSE_DIRECTION = "r";

}
