package edu.rutgers.MOST.data;

import java.util.ArrayList;

public class ProductFactory {
	private String sourceType;
	
	public ProductFactory(String sourceType) {
		this.sourceType = sourceType;
	}
	
	public ModelProduct getProductByReactionId(Integer reactionId) {
		if("SBML".equals(sourceType)){
			SBMLProduct product = new SBMLProduct();
			product.loadByReactionId(reactionId);
			return product;
		}
		return new SBMLProduct(); //Default behavior.
	}
	
	public ArrayList<SBMLProduct> getProductsByReactionId(Integer reactionId) {
		SBMLProductCollection aProductCollection = new SBMLProductCollection();
		if("SBML".equals(sourceType)){			
			aProductCollection.loadByReactionId(reactionId);					
		}
		
		return aProductCollection.getProductList();
	}
	
	public ArrayList<SBMLProduct> getAllProducts() {
		SBMLProductCollection aProductCollection = new SBMLProductCollection();
		if("SBML".equals(sourceType)){			
			aProductCollection.loadAll();					
		}
		
		return aProductCollection.getProductList();	
	}
}








