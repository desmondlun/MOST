package edu.rutgers.MOST.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import edu.rutgers.MOST.config.LocalConfig;
import edu.rutgers.MOST.presentation.GraphicalInterfaceConstants;
import edu.rutgers.MOST.presentation.Utilities;
import au.com.bytecode.opencsv.CSVReader;

public class PathwayFilesReader {
	
	Map<String, MetabolicPathway> metabolicPathways = new HashMap<String, MetabolicPathway>();
	Map<String, PathwayNameData> pathwayNameMap = new HashMap<String, PathwayNameData>();
	Map<String, PathwayMetaboliteData> metaboliteDataKeggIdMap = new HashMap<String, PathwayMetaboliteData>();
	Map<String, String> metaboliteNameAbbrMap = new HashMap<String, String>();
	Map<String, PathwayMetaboliteData> metaboliteNameDataMap = new HashMap<String, PathwayMetaboliteData>();
	Map<String, ArrayList<String>> additionalMetabolitesMap = new HashMap<String, ArrayList<String>>();
	Map<String, ArrayList<String>> alternateMetabolitesMap = new HashMap<String, ArrayList<String>>();
	Map<String, ArrayList<String>> metaboliteSubstitutionsMap = new HashMap<String, ArrayList<String>>();
	Map<String, PathwayReactionData> reactionDataKeggIdMap = new HashMap<String, PathwayReactionData>();
	Map<String, ArrayList<String>> ecNumberKeggReactionIdMap = new HashMap<String, ArrayList<String>>();
	Map<String, ArrayList<String>> keggReactionIdECNumberMap = new HashMap<String, ArrayList<String>>();
	Map<String, String> chebiIdKeggIdMap = new HashMap<String, String>();
	Map<String, ArrayList<String>> chebiIdKeggIdListMap = new HashMap<String, ArrayList<String>>();
	ArrayList<String> keggIdsInGraph = new ArrayList<String>();
	
	Utilities util = new Utilities();
	PathwayMetaboliteNodeFactory pmnf = new PathwayMetaboliteNodeFactory();
	
	public PathwayFilesReader() {
		
	}
	
	public void readPathwaysFile(File pathways) {
		CSVReader reader;
		
		int count = 0;
		
		try {
			reader = new CSVReader(new FileReader(pathways), ',');
			String [] dataArray;
			try {
				while ((dataArray = reader.readNext()) != null) {
					if (count > 0) {
						MetabolicPathway pathway = new MetabolicPathway();
						for (int s = 0; s < dataArray.length; s++) {
							if (s == PathwaysCSVFileConstants.PATHWAYS_ID_COLUMN) {
								pathway.setId(dataArray[s]);
							}
							if (s == PathwaysCSVFileConstants.PATHWAYS_NAME_COLUMN) {
								pathway.setName(dataArray[s]);
							}
							if (s == PathwaysCSVFileConstants.PATHWAYS_KEGG_ID_COLUMN) {
								pathway.setKeggId(dataArray[s]);
							}
						}
						metabolicPathways.put(pathway.getId(), pathway);
					}
					count += 1;
				}
				reader.close();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null,                
						"File Not Found Error.",                
						"Error",                                
						JOptionPane.ERROR_MESSAGE);
				//e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null,                
					"File Not Found Error.",                
					"Error",                                
					JOptionPane.ERROR_MESSAGE);
			//e.printStackTrace();
		}
	}
	
	public void readPathwayGraphFile(File pathwayGraph) {
		CSVReader reader;
		
		int count = 0;
		
		try {
			reader = new CSVReader(new FileReader(pathwayGraph), ',');
			String [] dataArray;
			try {
				while ((dataArray = reader.readNext()) != null) {
					if (count > 0) {
						if (metabolicPathways.containsKey(dataArray[PathwaysCSVFileConstants.PATHWAY_GRAPH_ID_COLUMN])) {
							MetabolicPathway pathway = metabolicPathways.get(dataArray[PathwaysCSVFileConstants.PATHWAY_GRAPH_ID_COLUMN]);
							for (int s = 0; s < dataArray.length; s++) {
								if (s == PathwaysCSVFileConstants.PATHWAY_GRAPH_COMPONENT_COLUMN) {
									pathway.setComponent(Integer.parseInt(dataArray[s]));
								}
							}
							metabolicPathways.put(pathway.getId(), pathway);
						} else {
							//System.out.println("pathway " + dataArray[PathwaysCSVFileConstants.PATHWAY_GRAPH_ID_COLUMN]  + " not found.");
						}
					}
					count += 1;
				}
				reader.close();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null,                
						"File Not Found Error.",                
						"Error",                                
						JOptionPane.ERROR_MESSAGE);
				//e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null,                
					"File Not Found Error.",                
					"Error",                                
					JOptionPane.ERROR_MESSAGE);
			//e.printStackTrace();
		}
	}
	
	public void readPathwayNamesFile(File pathwayNames) {
		CSVReader reader;
		
		int count = 0;
		
		try {
			reader = new CSVReader(new FileReader(pathwayNames), ',');
			String [] dataArray;
			try {
				while ((dataArray = reader.readNext()) != null) {
					if (count > 0) {
						PathwayNameData pn = new PathwayNameData();
						for (int s = 0; s < dataArray.length; s++) {
							if (s == PathwaysCSVFileConstants.PATHWAY_NAMES_ID_COLUMN) {
								pn.setId(dataArray[s]);
							}
							if (s == PathwaysCSVFileConstants.PATHWAY_NAMES_KEGG_IDS_COLUMN) {
								
							}
							if (s == PathwaysCSVFileConstants.PATHWAY_NAMES_LEVEL_COLUMN) {
								pn.setLevel(Double.parseDouble(dataArray[s]));
							}
							if (s == PathwaysCSVFileConstants.PATHWAY_NAMES_LEVEL_POSITION_COLUMN) {
								pn.setLevelPosition(Double.parseDouble(dataArray[s]));
							}
							if (s == PathwaysCSVFileConstants.PATHWAY_NAMES_NAME_COLUMN) {
								pn.setName(dataArray[s]);
							}
							if (s == PathwaysCSVFileConstants.PATHWAY_NAMES_METABOLITES_COLUMN) {
								String[] metabolites = dataArray[s].split("\\|");
								ArrayList<String> metabolitesList = new ArrayList<String>();
								for (int i = 0; i < metabolites.length; i++) {
									metabolitesList.add(metabolites[i]);
								}
								pn.setMetabolites(metabolitesList);
							}
						}
						pathwayNameMap.put(pn.getId(), pn);
					}
					count += 1;
				}
				reader.close();
				LocalConfig.getInstance().setPathwayNameMap(pathwayNameMap);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null,                
						"File Not Found Error.",                
						"Error",                                
						JOptionPane.ERROR_MESSAGE);
				//e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null,                
					"File Not Found Error.",                
					"Error",                                
					JOptionPane.ERROR_MESSAGE);
			//e.printStackTrace();
		}
	}
	
	public void readMetabolitesFile(File metabolites) {
		CSVReader reader;
		
		int count = 0;
		
		try {
			reader = new CSVReader(new FileReader(metabolites), ',');
			String [] dataArray;
			try {
				while ((dataArray = reader.readNext()) != null) {
					if (count > 0) {
						PathwayMetaboliteData pm = new PathwayMetaboliteData();
						for (int s = 0; s < dataArray.length; s++) {
							//System.out.println(dataArray[s]);
							if (s == PathwaysCSVFileConstants.METABOLITES_KEGG_ID_COLUMN) {
								pm.setKeggId(dataArray[s]);
							}
							if (s == PathwaysCSVFileConstants.METABOLITES_NAMES_COLUMN) {
								// need to escape pipe: http://stackoverflow.com/questions/21524642/splitting-string-with-pipe-character
								String[] names = dataArray[s].split("\\|");
								ArrayList<String> namesList = new ArrayList<String>();
								for (int i = 0; i < names.length; i++) {
									namesList.add(names[i]);
								}
								pm.setNames(namesList);
							}
							if (s == PathwaysCSVFileConstants.METABOLITES_OCCURENCE_COLUMN) {
								pm.setOccurence(Integer.valueOf(dataArray[s]));
							}
						}
						metaboliteDataKeggIdMap.put(pm.getKeggId(), pm);
					}
					count += 1;
				}
				reader.close();
				LocalConfig.getInstance().setMetaboliteDataKeggIdMap(metaboliteDataKeggIdMap);
//				System.out.println(metaboliteDataKeggIdMap);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null,                
						"File Not Found Error.",                
						"Error",                                
						JOptionPane.ERROR_MESSAGE);
				//e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null,                
					"File Not Found Error.",                
					"Error",                                
					JOptionPane.ERROR_MESSAGE);
			//e.printStackTrace();
		}	
	}
	
	public void readMetaboliteSubstitutionsFile(File metaboliteSubstitutions) {
		CSVReader reader;
		
		int count = 0;
		
		try {
			reader = new CSVReader(new FileReader(metaboliteSubstitutions), ',');
			String [] dataArray;
			try {
				while ((dataArray = reader.readNext()) != null) {
					if (count > 0) {
						for (int s = 0; s < dataArray.length; s++) {
							String keggId = dataArray[PathwaysCSVFileConstants.METABOLITE_SUBSTITUTIONS_KEGG_ID_COLUMN];
							if (s == PathwaysCSVFileConstants.METABOLITE_SUBSTITUTIONS_ALTERNATE_KEGG_IDS_COLUMN) {
								// need to escape pipe: http://stackoverflow.com/questions/21524642/splitting-string-with-pipe-character
								String[] ids = dataArray[s].split("\\|");
								ArrayList<String> idsList = new ArrayList<String>();
								for (int i = 0; i < ids.length; i++) {
									idsList.add(ids[i]);
								}
								metaboliteSubstitutionsMap.put(keggId, idsList);
//								System.out.println("sub kegg id " + keggId);
//								System.out.println("sub list " + idsList);
							}
						}
					}
					count += 1;
				}
				reader.close();
				LocalConfig.getInstance().setMetaboliteSubstitutionsMap(metaboliteSubstitutionsMap);
				//System.out.println(metaboliteSubstitutionsMap);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null,                
						"File Not Found Error.",                
						"Error",                                
						JOptionPane.ERROR_MESSAGE);
				//e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null,                
					"File Not Found Error.",                
					"Error",                                
					JOptionPane.ERROR_MESSAGE);
			//e.printStackTrace();
		}	
	}
	
	public void readMetaboliteAlternativesFile(File metaboliteAlternatives) {
		CSVReader reader;
		
		int count = 0;
		
		try {
			reader = new CSVReader(new FileReader(metaboliteAlternatives), ',');
			String [] dataArray;
			try {
				while ((dataArray = reader.readNext()) != null) {
					if (count > 0) {
						for (int s = 0; s < dataArray.length; s++) {
							String keggId = dataArray[PathwaysCSVFileConstants.METABOLITE_ALTERNATIVES_KEGG_ID_COLUMN];
							if (s == PathwaysCSVFileConstants.METABOLITE_ALTERNATIVES_ALTERNATE_KEGG_IDS_COLUMN) {
								// need to escape pipe: http://stackoverflow.com/questions/21524642/splitting-string-with-pipe-character
								String[] ids = dataArray[s].split("\\|");
								ArrayList<String> idsList = new ArrayList<String>();
								for (int i = 0; i < ids.length; i++) {
									idsList.add(ids[i]);
								}
								alternateMetabolitesMap.put(keggId, idsList);
//								System.out.println("sub kegg id " + keggId);
//								System.out.println("sub list " + idsList);
							}
						}
					}
					count += 1;
				}
				reader.close();
				LocalConfig.getInstance().setAlternateMetabolitesMap(alternateMetabolitesMap);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null,                
						"File Not Found Error.",                
						"Error",                                
						JOptionPane.ERROR_MESSAGE);
				//e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null,                
					"File Not Found Error.",                
					"Error",                                
					JOptionPane.ERROR_MESSAGE);
			//e.printStackTrace();
		}	
	}
	
	public void readMetabolitePositionsFile(File metabolitePositions) {
		CSVReader reader;
		
		int count = 0;
		
		try {
			reader = new CSVReader(new FileReader(metabolitePositions), ',');
			String [] dataArray;
			try {
				while ((dataArray = reader.readNext()) != null) {
					if (count > 0) {
						PathwayMetaboliteData pm = new PathwayMetaboliteData();
						
						String id = dataArray[PathwaysCSVFileConstants.METABOLITE_POSITIONS_ID_COLUMN];
						for (int s = 0; s < dataArray.length; s++) {
							if (s == PathwaysCSVFileConstants.METABOLITE_POSITIONS_METABOLITE_ID_COLUMN) {
								pm.setId(dataArray[s]);
							}
							if (s == PathwaysCSVFileConstants.METABOLITE_POSITIONS_LEVEL_COLUMN) {
								pm.setLevel(Double.parseDouble(dataArray[s]));
							}
							if (s == PathwaysCSVFileConstants.METABOLITE_POSITIONS_POSITION_COLUMN) {
								pm.setLevelPosition(Double.parseDouble(dataArray[s]));
							}
							if (s == PathwaysCSVFileConstants.METABOLITE_POSITIONS_NAME_COLUMN) {
								// need to escape pipe: http://stackoverflow.com/questions/21524642/splitting-string-with-pipe-character
								String[] names = dataArray[s].split("\\|");
								ArrayList<String> namesList = new ArrayList<String>();
								for (int i = 0; i < names.length; i++) {
									namesList.add(names[i]);
								}
								pm.setNames(namesList);
							}
							if (s == PathwaysCSVFileConstants.METABOLITE_POSITIONS_ABBR_COLUMN) {
								pm.setAbbreviation(dataArray[s]);
							}
							if (s == PathwaysCSVFileConstants.METABOLITE_POSITIONS_KEGG_ID_COLUMN) {
								pm.setKeggId(dataArray[s]);
								if (!keggIdsInGraph.contains(dataArray[s])) {
									keggIdsInGraph.add(dataArray[s]);
								}
							}
							if (s == PathwaysCSVFileConstants.METABOLITE_POSITIONS_BORDER_COLUMN) {
								pm.setBorder(dataArray[s]);
							}
							if (s == PathwaysCSVFileConstants.METABOLITE_POSITIONS_TYPE_COLUMN) {
								pm.setType(dataArray[s]);
							}
						}
						metabolicPathways.get(id).getMetabolitesData().put(pm.getId(), pm);
						String name = pm.getNames().get(0);
						// add KEGG id to nodes but not if KEGG id does not exist and file contains
						// entry such as "-1"
						if (pm.getKeggId().startsWith("C")) {
							name = "<html>" + pm.getAbbreviation() + "<p>Name: " +
								pm.getNames().get(0) + "<p>KEGG Id: " + pm.getKeggId() +
								"<p>Metabolite Database Id: " + pm.getId();
						}
						String abbr = pm.getAbbreviation();
						if (LocalConfig.getInstance().getKeggIdMetaboliteMap().containsKey(pm.getKeggId())) {
							String metabAbbr = pm.getNames().get(0);
							ArrayList<String> abbrList = new ArrayList<String>();
							ArrayList<String> nameList = new ArrayList<String>();
							ArrayList<String> keggIdList = new ArrayList<String>();
							ArrayList<String> chebiIdList = new ArrayList<String>();
							ArrayList<String> chargeList = new ArrayList<String>();
							for (int j = 0; j < LocalConfig.getInstance().getKeggIdMetaboliteMap().get(pm.getKeggId()).size(); j++) {
								if (LocalConfig.getInstance().getKeggIdMetaboliteMap().get(pm.getKeggId()).get(j).getCompartment().
										equals(LocalConfig.getInstance().getSelectedCompartmentName())) {
									metabAbbr = LocalConfig.getInstance().getKeggIdMetaboliteMap().get(pm.getKeggId()).get(j).getMetaboliteAbbreviation();
									if (!abbrList.contains(LocalConfig.getInstance().getKeggIdMetaboliteMap().get(pm.getKeggId()).get(j).getMetaboliteAbbreviation())) {
										abbrList.add(LocalConfig.getInstance().getKeggIdMetaboliteMap().get(pm.getKeggId()).get(j).getMetaboliteAbbreviation());
									}
									if (!nameList.contains(LocalConfig.getInstance().getKeggIdMetaboliteMap().get(pm.getKeggId()).get(j).getMetaboliteName())) {
										nameList.add(LocalConfig.getInstance().getKeggIdMetaboliteMap().get(pm.getKeggId()).get(j).getMetaboliteName());
									}
									if (!keggIdList.contains(pm.getKeggId())) {
										keggIdList.add(pm.getKeggId());
									}
									String chebiId = LocalConfig.getInstance().getKeggIdMetaboliteMap().get(pm.getKeggId()).get(j).getChebiId();
									if (chebiId != null && chebiId.length() > 0 && !chebiIdList.contains(chebiId.trim())) {
										chebiIdList.add(chebiId.trim());
									}
									String charge = LocalConfig.getInstance().getKeggIdMetaboliteMap().get(pm.getKeggId()).get(j).getCharge();
									if (charge != null && charge.length() > 0 && !chargeList.contains(charge.trim())) {
										chargeList.add(charge.trim());
									}
								} 
							}
							if (abbrList.size() > 0) {
								name = util.makeCommaSeparatedList(abbrList);
								metabAbbr = name;
							} else {
								name = metabAbbr;
							}
							if (abbrList.size() > 1) {
		    					ArrayList<String> abbrNoPrefixOrSuffix = new ArrayList<String>();
		    					for (int p = 0; p < abbrList.size(); p++) {
		    						abbrNoPrefixOrSuffix.add(util.maybeRemovePrefixAndSuffix(abbrList.get(p)));
		    					}
		    					abbr = util.makeCommaSeparatedList(abbrNoPrefixOrSuffix);
		    				} else {
		    					abbr = util.maybeRemovePrefixAndSuffix(metabAbbr);
		    				}
							//abbr = util.maybeRemovePrefixAndSuffix(metabAbbr);
							name = pmnf.htmlDisplayName(abbr, nameList, abbrList, keggIdList, chebiIdList, chargeList, pm.getId());
						}
						if (metaboliteNameAbbrMap.containsKey(name)) {
							name = name + pmnf.duplicateSuffix(name, metaboliteNameAbbrMap);
						}
						pm.setName(name);
						pm.setAbbreviation(abbr);
						metaboliteNameAbbrMap.put(name, abbr);
						metaboliteNameDataMap.put(name, pm);
					}
					count += 1;
				}
				reader.close();
				LocalConfig.getInstance().setMetabolicPathways(metabolicPathways);
				LocalConfig.getInstance().setMetaboliteNameAbbrMap(metaboliteNameAbbrMap);
				LocalConfig.getInstance().setMetaboliteNameDataMap(metaboliteNameDataMap);
				LocalConfig.getInstance().setKeggIdsInGraph(keggIdsInGraph);
//				System.out.println("pfr kegg ids in graph " + keggIdsInGraph);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null,                
						"File Not Found Error.",                
						"Error",                                
						JOptionPane.ERROR_MESSAGE);
				//e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null,                
					"File Not Found Error.",                
					"Error",                                
					JOptionPane.ERROR_MESSAGE);
			//e.printStackTrace();
		}	
	}
	
	public void readReactionsFile(File reactions) {
		CSVReader reader;
		
		int count = 0;
		
		try {
			reader = new CSVReader(new FileReader(reactions), ',');
			String [] dataArray;
			try {
				while ((dataArray = reader.readNext()) != null) {
					if (count > 0) {
						PathwayReactionData pr = new PathwayReactionData();
						ArrayList<String> keggIdsList = new ArrayList<String>();
						String keggReactionId = dataArray[PathwaysCSVFileConstants.REACTIONS_KEGG_ID_COLUMN];
						pr.setKeggReactionId(keggReactionId);
						for (int s = 0; s < dataArray.length; s++) {
							//System.out.println(dataArray[s]);
							if (s == PathwaysCSVFileConstants.REACTIONS_KEGG_REACTANTS_COLUMN) {
								// need to escape pipe: http://stackoverflow.com/questions/21524642/splitting-string-with-pipe-character
								String[] reactants = dataArray[s].split("\\|");
								ArrayList<String> reactantsList = new ArrayList<String>();
								for (int i = 0; i < reactants.length; i++) {
									reactantsList.add(reactants[i]);
									keggIdsList.add(reactants[i]);
								}
								pr.setKeggReactantIds(reactantsList);
							}
							if (s == PathwaysCSVFileConstants.REACTIONS_KEGG_PRODUCTS_COLUMN) {
								// need to escape pipe: http://stackoverflow.com/questions/21524642/splitting-string-with-pipe-character
								String[] products = dataArray[s].split("\\|");
								ArrayList<String> productsList = new ArrayList<String>();
								for (int i = 0; i < products.length; i++) {
									productsList.add(products[i]);
									if (!keggIdsList.contains(products[i])) {
										keggIdsList.add(products[i]);
									}
								}
								pr.setKeggProductIds(productsList);
							}
							pr.setKeggIds(keggIdsList);
							// reversibility data currently not correct in this file
//							if (s == PathwaysCSVFileConstants.REACTIONS_REVERSABILITY_COLUMN) {
//								pr.setReversible(dataArray[s]);
//							}
							if (s == PathwaysCSVFileConstants.REACTIONS_EC_LIST_COLUMN) {
								// need to escape pipe: http://stackoverflow.com/questions/21524642/splitting-string-with-pipe-character
								String[] ecNumbers = dataArray[s].split("\\|");
								ArrayList<String> ecNumbersList = new ArrayList<String>();
								for (int i = 0; i < ecNumbers.length; i++) {
									ecNumbersList.add(ecNumbers[i]);
									if (ecNumbers[i] != null && ecNumbers[i].length() > 0) {
										if (ecNumberKeggReactionIdMap.containsKey(ecNumbers[i])) {
											ArrayList<String> r = ecNumberKeggReactionIdMap.get(ecNumbers[i]);
											r.add(keggReactionId);
											ecNumberKeggReactionIdMap.put(ecNumbers[i], r);
										} else {
											ArrayList<String> r = new ArrayList<String>();
											r.add(keggReactionId);
											ecNumberKeggReactionIdMap.put(ecNumbers[i], r);
										}
									}
								}
								keggReactionIdECNumberMap.put(keggReactionId, ecNumbersList);
								pr.setEcNumbers(ecNumbersList);
							}
							if (s == PathwaysCSVFileConstants.REACTIONS_OCCURENCES_COLUMN) {
								pr.setOccurences(Integer.valueOf(dataArray[s]));
							}
							if (s == PathwaysCSVFileConstants.REACTIONS_NAMES_COLUMN) {
								// need to escape pipe: http://stackoverflow.com/questions/21524642/splitting-string-with-pipe-character
								String[] names = dataArray[s].split("\\|");
								ArrayList<String> namesList = new ArrayList<String>();
								for (int i = 0; i < names.length; i++) {
									namesList.add(names[i]);
								}
								pr.setNames(namesList);
							}
						}
						//System.out.println("pfr " + pr);
						reactionDataKeggIdMap.put(pr.getKeggReactionId(), pr);
					}
					count += 1;
				}
				reader.close();
				LocalConfig.getInstance().setReactionDataKeggIdMap(reactionDataKeggIdMap);
				//System.out.println(reactionDataKeggIdMap);
				LocalConfig.getInstance().setEcNumberKeggReactionIdMap(ecNumberKeggReactionIdMap);
				LocalConfig.getInstance().setKeggReactionIdECNumberMap(keggReactionIdECNumberMap);
				//System.out.println("ec num kegg rxn " + ecNumberKeggReactionIdMap);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null,                
						"File Not Found Error.",                
						"Error",                                
						JOptionPane.ERROR_MESSAGE);
				//e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null,                
					"File Not Found Error.",                
					"Error",                                
					JOptionPane.ERROR_MESSAGE);
			//e.printStackTrace();
		}	
	}
	
	public void readReactionPositionsFile(File reactionPositions) {
		CSVReader reader;
		
		int count = 0;
		
		try {
			reader = new CSVReader(new FileReader(reactionPositions), ',');
			String [] dataArray;
			try {
				while ((dataArray = reader.readNext()) != null) {
					if (count > 0) {
						PathwayReactionData pr = new PathwayReactionData();
						String id = dataArray[PathwaysCSVFileConstants.REACTIONS_PATHWAY_ID_COLUMN];
						Map<String, PathwayMetaboliteData> metabolitesData = LocalConfig.getInstance().getMetabolicPathways().get(id).getMetabolitesData();
						ArrayList<String> keggReactantIds = new ArrayList<String>();
						ArrayList<String> keggProductIds = new ArrayList<String>();
						Map<String, PathwayMetaboliteData> keggReactantIdsDataMap = new HashMap<String, PathwayMetaboliteData>();
						Map<String, PathwayMetaboliteData> keggProductIdsDataMap = new HashMap<String, PathwayMetaboliteData>();
						for (int s = 0; s < dataArray.length; s++) {
							if (s == PathwaysCSVFileConstants.REACTIONS_PATHWAY_ID_COLUMN) {
								pr.setPathwayId(dataArray[s]);
							}
							if (s == PathwaysCSVFileConstants.REACTIONS_REACTION_ID_COLUMN) {
								pr.setReactionId(dataArray[s]);
							}	
							if (s == PathwaysCSVFileConstants.REACTIONS_REACTANTS_COLUMN) {
								String[] reac = dataArray[s].split("\\|");
								ArrayList<String> reactantIds = new ArrayList<String>();
								for (int i = 0; i < reac.length; i++) {
									reactantIds.add(reac[i]);
									if (metabolitesData.containsKey(reac[i])) {
										keggReactantIds.add(metabolitesData.get(reac[i]).getKeggId());
//										reactantData.add(metabolitesData.get(reac[i]));
										keggReactantIdsDataMap.put(metabolitesData.get(reac[i]).getKeggId(), metabolitesData.get(reac[i]));
									}
								}
								pr.setReactantIds(reactantIds);
								//System.out.println(keggReactantIds);
							}
							if (s == PathwaysCSVFileConstants.REACTIONS_PRODUCTS_COLUMN) {
								String[] prod = dataArray[s].split("\\|");
								ArrayList<String> productIds = new ArrayList<String>();
								for (int i = 0; i < prod.length; i++) {
									productIds.add(prod[i]);
									if (metabolitesData.containsKey(prod[i])) {
										keggProductIds.add(metabolitesData.get(prod[i]).getKeggId());
//										productData.add(metabolitesData.get(prod[i]));
										keggProductIdsDataMap.put(metabolitesData.get(prod[i]).getKeggId(), metabolitesData.get(prod[i]));
									}
								}
								pr.setProductIds(productIds);
								//System.out.println(keggProductIds);
							}
							if (s == PathwaysCSVFileConstants.REACTIONS_REVERSIBLE_COLUMN) {
								pr.setReversible(dataArray[s]);
							}
							if (s == PathwaysCSVFileConstants.REACTIONS_EC_NUM_LIST_COLUMN) {
								String[] ecNumbers = dataArray[s].split("\\|");
								ArrayList<String> ec = new ArrayList<String>();
								for (int i = 0; i < ecNumbers.length; i++) {
									ec.add(ecNumbers[i]);
								}
								pr.setEcNumbers(ec);
								metabolicPathways.get(id).getEcNumbers().add(ec);
							}
							if (s == PathwaysCSVFileConstants.REACTIONS_LEVEL_COLUMN) {
								pr.setLevel(Double.parseDouble(dataArray[s]));
							}
							if (s == PathwaysCSVFileConstants.REACTIONS_POSITION_COLUMN) {
								pr.setLevelPosition(Double.parseDouble(dataArray[s]));
							}
							if (s == PathwaysCSVFileConstants.REACTIONS_POSITION_KEGG_IDS_COLUMN) {
								String[] keggIds = dataArray[s].split("\\|");
								ArrayList<String> kegg = new ArrayList<String>();
								for (int i = 0; i < keggIds.length; i++) {
									kegg.add(keggIds[i]);
								}
								pr.setKeggReactionIds(kegg);
							}
						}
						pr.writeReactionEquation();
						pr.setName(pr.getEquation());
						pr.setDisplayName("<html>" + pr.getEquation() +"<p> EC Number(s): " + pr.getEcNumbers() + "<p> Reaction Database Id: " + pr.getReactionId());
						pr.setKeggReactantIds(keggReactantIds);
						pr.setKeggProductIds(keggProductIds);
						pr.setKeggReactantIdsDataMap(keggReactantIdsDataMap);
						pr.setKeggProductIdsDataMap(keggProductIdsDataMap);
						metabolicPathways.get(id).getReactionsData().put(pr.getReactionId(), pr);
						//System.out.println(pr.getKeggReactionIds());
					}
					count += 1;
				}
				reader.close();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null,                
						"File Not Found Error.",                
						"Error",                                
						JOptionPane.ERROR_MESSAGE);
				//e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null,                
					"File Not Found Error.",                
					"Error",                                
					JOptionPane.ERROR_MESSAGE);
			//e.printStackTrace();
		}	
	}
	
	public void readSideSpeciesFile(File sideSpecies) {
		CSVReader reader;

		int count = 0;
		ArrayList<String> sideSpeciesList = new ArrayList<String>();

		try {
			reader = new CSVReader(new FileReader(sideSpecies), ',');
			String [] dataArray;
			try {
				while ((dataArray = reader.readNext()) != null) {
					if (count > 0) {
						for (int s = 0; s < dataArray.length; s++) {	
							if (s == PathwaysCSVFileConstants.PATHWAY_SIDE_SPECIES_NAME_COLUMN) {
								sideSpeciesList.add(dataArray[s]);
							}
						}
					}
					count += 1;
				}
				reader.close();
				LocalConfig.getInstance().setSideSpeciesList(sideSpeciesList);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null,                
						"File Not Found Error.",                
						"Error",                                
						JOptionPane.ERROR_MESSAGE);
				//e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null,                
					"File Not Found Error.",                
					"Error",                                
					JOptionPane.ERROR_MESSAGE);
			//e.printStackTrace();
		}	
	}
	
	public void readChebiIdsKeggIdsFile(File chebiIdsKeggIds) {
		CSVReader reader;
		
		int count = 0;
		
		try {
			reader = new CSVReader(new FileReader(chebiIdsKeggIds), ',');
			String [] dataArray;
			try {
				while ((dataArray = reader.readNext()) != null) {
					if (count > 0) {
						chebiIdKeggIdMap.put(dataArray[PathwaysCSVFileConstants.CHEBI_IDS_KEGG_IDS_CHEBI_ID_COLUMN], 
								dataArray[PathwaysCSVFileConstants.CHEBI_IDS_KEGG_IDS_KEGG_ID_COLUMN]);
						if (chebiIdKeggIdListMap.containsKey(dataArray[PathwaysCSVFileConstants.CHEBI_IDS_KEGG_IDS_CHEBI_ID_COLUMN])) {
							ArrayList<String> keggIds = chebiIdKeggIdListMap.get(dataArray[PathwaysCSVFileConstants.CHEBI_IDS_KEGG_IDS_CHEBI_ID_COLUMN]);
							if (!keggIds.contains(dataArray[PathwaysCSVFileConstants.CHEBI_IDS_KEGG_IDS_KEGG_ID_COLUMN])) {
								keggIds.add(dataArray[PathwaysCSVFileConstants.CHEBI_IDS_KEGG_IDS_KEGG_ID_COLUMN]);
								chebiIdKeggIdListMap.put(dataArray[PathwaysCSVFileConstants.CHEBI_IDS_KEGG_IDS_CHEBI_ID_COLUMN], keggIds);
							}
						} else {
							ArrayList<String> keggIds = new ArrayList<String>();
							keggIds.add(dataArray[PathwaysCSVFileConstants.CHEBI_IDS_KEGG_IDS_KEGG_ID_COLUMN]);
							chebiIdKeggIdListMap.put(dataArray[PathwaysCSVFileConstants.CHEBI_IDS_KEGG_IDS_CHEBI_ID_COLUMN], keggIds);
						}
					}
					count += 1;
				}
				//System.out.println(chebiIdKeggIdMap);
				LocalConfig.getInstance().setChebiIdKeggIdMap(chebiIdKeggIdMap);
				reader.close();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null,                
						"File Not Found Error.",                
						"Error",                                
						JOptionPane.ERROR_MESSAGE);
				//e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null,                
					"File Not Found Error.",                
					"Error",                                
					JOptionPane.ERROR_MESSAGE);
			//e.printStackTrace();
		}
	}
	
	/**
	 * Files where data is read once and the data structure is not modified in any way.
	 * Most of these are large files and it is beneficial to read only once.
	 */
	public void readOnceFiles() {
		File pathwayNames = new File(PathwaysCSVFileConstants.PATHWAY_NAMES_FILE_NAME);
		File pathwayGraph = new File(PathwaysCSVFileConstants.PATHWAY_GRAPH_FILE_NAME);
		File metabolites = new File(PathwaysCSVFileConstants.METABOLITES_FILE_NAME);
		File metaboliteAlternatives = new File(PathwaysCSVFileConstants.METABOLITE_ALTERNATIVES_FILE_NAME);
		File reactions = new File(PathwaysCSVFileConstants.REACTIONS_FILE_NAME);
		File sideSpecies = new File(PathwaysCSVFileConstants.PATHWAY_SIDE_SPECIES_FILE_NAME);
		File metaboliteSubstitutions = new File(PathwaysCSVFileConstants.METABOLITE_SUBSTITUTIONS_FILE_NAME);
		File chebiIdsKeggIds = new File(PathwaysCSVFileConstants.CHEBI_IDS_KEGG_IDS_FILE_NAME);
		PathwayFilesReader reader = new PathwayFilesReader();
		reader.readPathwayNamesFile(pathwayNames);
		reader.readPathwayGraphFile(pathwayGraph);
		reader.readMetabolitesFile(metabolites);
		reader.readMetaboliteAlternativesFile(metaboliteAlternatives);
		reader.readReactionsFile(reactions);
		reader.readSideSpeciesFile(sideSpecies);
		reader.readMetaboliteSubstitutionsFile(metaboliteSubstitutions);
		reader.readChebiIdsKeggIdsFile(chebiIdsKeggIds);
	}
	
	/**
	 * Files where data is read but resultant data structure may be modified
	 */
	public void readFiles() {
		File pathways = new File(PathwaysCSVFileConstants.PATHWAYS_FILE_NAME);
		File metabolitePositions = new File(PathwaysCSVFileConstants.METABOLITE_POSITIONS_FILE_NAME);
		File reactionPositions = new File(PathwaysCSVFileConstants.REACTION_POSITIONS_FILE_NAME);
		File metaboliteSubstitutions = new File(PathwaysCSVFileConstants.METABOLITE_SUBSTITUTIONS_FILE_NAME);
		PathwayFilesReader reader = new PathwayFilesReader();
		reader.readPathwaysFile(pathways);
		reader.readMetabolitePositionsFile(metabolitePositions);
		reader.readReactionPositionsFile(reactionPositions);
		// TODO: remove after done updating database
		reader.readMetaboliteSubstitutionsFile(metaboliteSubstitutions);
	}
	
	public static void main( String args[] )
	{
		PathwayFilesReader reader = new PathwayFilesReader();
		reader.readOnceFiles();
		reader.readFiles();
	}

}
