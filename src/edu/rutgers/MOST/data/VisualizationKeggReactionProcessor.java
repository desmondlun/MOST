package edu.rutgers.MOST.data;

import java.util.ArrayList;
import java.util.Collections;

import edu.rutgers.MOST.config.LocalConfig;

public class VisualizationKeggReactionProcessor {

	public boolean keggReactionIdFound(ArrayList<String> dataKeggReactantIds, 
			ArrayList<String> dataKeggProductIds, ArrayList<String> modelReactantKeggIds, 
			ArrayList<String> modelProductKeggIds, String direction) {
		boolean reactantMatch = false;
		boolean productMatch = false;
		Collections.sort(dataKeggReactantIds);
		Collections.sort(dataKeggProductIds);
		Collections.sort(modelReactantKeggIds);
		Collections.sort(modelProductKeggIds);
		if (dataKeggReactantIds.equals(modelReactantKeggIds)) {
			reactantMatch = true;
		}
		if (dataKeggProductIds.equals(modelProductKeggIds)) {
			productMatch = true;
		}
		if (reactantMatch && productMatch) {
//			System.out.println("model reac " + modelReactantKeggIds);
//			System.out.println("model prod " + modelProductKeggIds);
//			System.out.println(direction);
			return true;
		} else if (modelReactantKeggIds.contains("C00080") && modelProductKeggIds.contains("C00080")) {
//				System.out.println("b " + modelReactantKeggIds);
//				System.out.println("b " + modelProductKeggIds);
				modelReactantKeggIds.remove(modelReactantKeggIds.indexOf("C00080"));
				modelProductKeggIds.remove(modelProductKeggIds.indexOf("C00080"));
//				System.out.println("a " + modelReactantKeggIds);
//				System.out.println("a " + modelProductKeggIds);
				return keggReactionIdFound(dataKeggReactantIds, dataKeggProductIds, modelReactantKeggIds, 
						modelProductKeggIds, direction);
		} else {
			return false;
		}
	}
	
//	public String reactionDirection(String reactantComp, String productComp) {
//		String direction = "1";
//		if (reactantComp.equals(LocalConfig.getInstance().getExtraOrganismName()) &&
//				(productComp.equals(LocalConfig.getInstance().getPeriplasmName()) ||
//						productComp.equals(LocalConfig.getInstance().getCytosolName()))) {
//			direction = "-1";
//		} else if (reactantComp.equals(LocalConfig.getInstance().getPeriplasmName()) && 
//				productComp.equals(LocalConfig.getInstance().getCytosolName())) {
//			direction = "-1";
//		}
//		return direction;
//	}
	
}
