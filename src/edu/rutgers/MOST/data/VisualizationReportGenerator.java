package edu.rutgers.MOST.data;

import java.util.ArrayList;
import java.util.Map;

import edu.rutgers.MOST.presentation.GraphicalInterfaceConstants;
import edu.rutgers.MOST.presentation.Utilities;

public class VisualizationReportGenerator {
	
	Utilities util = new Utilities();
	
	public String reportSection(String title, Map<Integer, SBMLReaction> idReactionMapAllReactions, ArrayList<Integer> idList) {
		String text = "";
		String header = String.format("%1$-15s %2$-40s %3$-50s %4$-100s", 
				"Reaction Abbr", 
				GraphicalInterfaceConstants.REACTION_NAME_COLUMN_NAME, 
				GraphicalInterfaceConstants.REACTION_EQUATION_ABBR_COLUMN_NAME, 
				GraphicalInterfaceConstants.REACTION_EQUATION_NAMES_COLUMN_NAME);
		
		text += title + Integer.toString(idList.size()) + " reactions" + "\n";
		text += header + "\n";
		for (int u = 0; u < idList.size(); u++) {
			String line = util.formattedString(idReactionMapAllReactions.get(idList.get(u)));
			text += line + "\n";
		}
		
		return text;
		
	}

}
