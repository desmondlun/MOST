package edu.rutgers.MOST.data;

import java.util.ArrayList;

import edu.rutgers.MOST.config.LocalConfig;

public class SBMLProductCollection implements ModelProductCollection {
    
	private Integer reactionId;
	private ArrayList<SBMLProduct> productList;
	

	public ArrayList<SBMLProduct> getProductList() {
		return productList;
	}

	public void setProductList(ArrayList<SBMLProduct> productList) {
		this.productList = productList;
	}
	
	public void setReactionId(Integer reactionId) {
		this.reactionId = reactionId;
	}

	public Integer getReactionId() {
		return reactionId;
	}

	public void loadByReactionId(Integer reactionId) {

	}

	public void loadAll() {
		productList = new ArrayList<SBMLProduct>();
		ReactionFactory rFactory = new ReactionFactory("SBML");
		ArrayList<Integer> reactionIdList = rFactory.reactionIdList();
		for (int i = 0; i < reactionIdList.size(); i++) {
			int index = reactionIdList.get(i);
			try {
				for (int j = 0; j < ((SBMLReactionEquation)LocalConfig.getInstance().getReactionEquationMap().get(index)).products.size(); j++) {
					SBMLProduct product = ((SBMLReactionEquation)LocalConfig.getInstance().getReactionEquationMap().get(index)).products.get(j);
					this.productList.add(product);
					//System.out.println(i);
					//System.out.println(product.getMetaboliteAbbreviation());
				}
			} catch (Throwable t) {
				
			} 
		}
		//System.out.println("product list " + this.productList);
	}	

	public static void main(String[] args) {
		/*
		ProductFactory aProductFactory = new ProductFactory("SBML", "test_03182012");
		ArrayList<ModelProduct> products = aProductFactory.getProductsByReactionId(1);
		Iterator<ModelProduct> iterator = products.iterator();
		 
		while(iterator.hasNext()){
			SBMLProduct aProduct = (SBMLProduct)iterator.next();
			//System.out.print("\nabbr" + aProduct.toString());
		}
		*/
	}

}










 



















