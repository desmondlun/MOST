package edu.rutgers.MOST.data;

import java.util.ArrayList;

public class ProductFactory {
	private String sourceType;
	private String databaseName;
	
	public ProductFactory(String sourceType, String databaseName) {
		this.sourceType = sourceType;
		this.databaseName = databaseName;
	}
	
	public ModelProduct getProductByReactionId(Integer reactionId) {
		if("SBML".equals(sourceType)){
			SBMLProduct product = new SBMLProduct();
			product.setDatabaseName(databaseName);
			product.loadByReactionId(reactionId);
			return product;
		}
		return new SBMLProduct(); //Default behavior.
	}
	
	public ArrayList<ModelProduct> getProductsByReactionId(Integer reactionId) {
		SBMLProductCollection aProductCollection = new SBMLProductCollection();
		if("SBML".equals(sourceType)){			
			aProductCollection.setDatabaseName(databaseName);
			aProductCollection.loadByReactionId(reactionId);					
		}
		
		return aProductCollection.getProductList();
	}
	
	public ArrayList<ModelProduct> getAllProducts() {
		SBMLProductCollection aProductCollection = new SBMLProductCollection();
		if("SBML".equals(sourceType)){			
			aProductCollection.setDatabaseName(databaseName);
			aProductCollection.loadAll();					
		}
		
		return aProductCollection.getProductList();	
	}
}








