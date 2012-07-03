package edu.rutgers.MOST.data;

import java.util.ArrayList;
public class ProductFactory {

	
	
	public ModelProduct getProductByReactionId(Integer reactionId, String sourceType, String databaseName){
		
		
		if("SBML".equals(sourceType)){
			SBMLProduct product = new SBMLProduct();
			product.setDatabaseName(databaseName);
			product.loadByReactionId(reactionId);
			return product;
		}
		return new SBMLProduct(); //Default behavior.
	}
	
	public ArrayList<ModelProduct> getProductsByReactionId(Integer reactionId, String sourceType, String databaseName)
	{
		
		SBMLProductCollection aProductCollection = new SBMLProductCollection();
		if("SBML".equals(sourceType)){
			
			aProductCollection.setDatabaseName(databaseName);
			boolean hasProducts = aProductCollection.loadByReactionId(reactionId);
			
			
		}
		
		
		return aProductCollection.getProductList();
		
		
		
		
	}
}








