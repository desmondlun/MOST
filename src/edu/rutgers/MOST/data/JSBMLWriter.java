
package edu.rutgers.MOST.data;

import java.io.File;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;

import java.util.HashMap;
import java.util.Map;

import org.sbml.jsbml.ASTNode;
import org.sbml.jsbml.Compartment;
import org.sbml.jsbml.KineticLaw;
import org.sbml.jsbml.LocalParameter;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLWriter;
import org.sbml.jsbml.Species;
import org.sbml.jsbml.SpeciesReference;
import org.sbml.jsbml.Unit;
import org.sbml.jsbml.Unit.Kind;
import org.sbml.jsbml.UnitDefinition;

import edu.rutgers.MOST.config.LocalConfig;
import edu.rutgers.MOST.presentation.GraphicalInterface;
import edu.rutgers.MOST.presentation.GraphicalInterfaceConstants;
import edu.rutgers.MOST.presentation.Utilities;

public class JSBMLWriter implements TreeModelListener{
	public String sourceType;
	public String databaseName;
	public SMetabolites allMeta;
	public LocalConfig curConfig;
	public SReactions allReacts;
	public int level;
	public int version;
	public Map<Integer, SBMLMetabolite> metabolitesMap;
	public Map<String, Species> speciesMap;
	public Map<String,Compartment> compartments;
	public Map<String,SpeciesReference> speciesRefMap;
	public File outFile;
	public SettingsFactory curSettings;
	public String optFilePath;
	public boolean load;

	public String getOptFilePath() {
		return optFilePath;
	}

	public void setOptFilePath(String optFilePath) {
		this.optFilePath = optFilePath;
	}

	/**
	 * @param args
	 */
	/** Main routine. This does not take any arguments. */
		public static void main(String[] args) throws Exception {
			new JSBMLWriter();
		}

	@Override
	public void treeNodesChanged(TreeModelEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void treeNodesInserted(TreeModelEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void treeNodesRemoved(TreeModelEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void treeStructureChanged(TreeModelEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public void setConfig(LocalConfig conf) {
		this.curConfig = conf;
		
	}
	
	public void formConnect(LocalConfig config) throws Exception {
		curSettings = new SettingsFactory();
		load = false;
		if (setOutFile()) {
		    load = true;
		    
			curConfig = config;
					
			sourceType = "SBML";
			
			if (sourceType == "SBML") {
				this.create();
			}
		}
	}
	
	public File getOutFile() {
		return outFile;
	}

	public JSBMLWriter() {
		metabolitesMap = new HashMap< Integer, SBMLMetabolite >();
		speciesMap = new HashMap< String, Species >();
		speciesRefMap = new HashMap< String, SpeciesReference >();
	}
	
	public void metaMap() {
		
		
	}
	
	public boolean setOutFile(){
		if (GraphicalInterface.showJSBMLFileChooser) {
			JTextArea output = null;
			String lastSBML_path = curSettings.get("LastSBML");
			
			Utilities u = new Utilities();
			JFileChooser chooser = new JFileChooser();
			// if path is null or does not exist, default used, else last path used
			chooser.setCurrentDirectory(new File(u.lastPath(lastSBML_path, chooser)));					
			
			chooser.setApproveButtonText("Save");
			chooser.setDialogTitle("Save to");
			chooser.setFileFilter(new XMLFileFilter());
			chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			
			if (GraphicalInterface.saveOptFile) {	
				String path = getOptFilePath();
				File theFileToSave = new File(path);
				chooser.setSelectedFile(theFileToSave);
			} 		
			
			boolean done = false;
			boolean cancel = false;
			while (!done) {
				//... Open a file dialog.
				int option = chooser.showSaveDialog(output); 
				if(option == JFileChooser.CANCEL_OPTION) {
					cancel = true;
					done = true;
					// this should only be false if opt file ?
//					if (GraphicalInterface.saveOptFile) {
//						GraphicalInterface.exit = false;
//					}
				}
				if(option == JFileChooser.APPROVE_OPTION){  
					if(chooser.getSelectedFile()!=null)	{  
						String path = chooser.getSelectedFile().getPath();
						if (!path.endsWith(".xml")) {
							path = path + ".xml";
						}
						File theFileToSave = new File(path);
						if (theFileToSave.exists()) {
							int confirmDialog = JOptionPane.showConfirmDialog(chooser, "Replace existing file?");
							if (confirmDialog == JOptionPane.YES_OPTION) {
								done = true;
								this.setOutFile(theFileToSave);

								String rawPathName = chooser.getSelectedFile().getAbsolutePath();
								if (!rawPathName.endsWith(".xml")) {
									rawPathName = rawPathName + ".xml";
								}
								curSettings.add("LastSBML", rawPathName);
							} else if (confirmDialog == JOptionPane.NO_OPTION) {        		    	  
								done = false;
							} else if (confirmDialog == JOptionPane.CANCEL_OPTION) { 
								cancel = true;
								done = true;							
							}       		    	  
						} else {
							done = true;
							this.setOutFile(theFileToSave);

							String rawPathName = chooser.getSelectedFile().getAbsolutePath();
							if (!rawPathName.endsWith(".xml")) {
								rawPathName = rawPathName + ".xml";
							}
							curSettings.add("LastSBML", rawPathName);
						}
					}
				}			
			}
			
			if (done && !cancel) {
				return true;
			}
		} else {
			File theFileToSave = GraphicalInterface.getSBMLFile();
			this.setOutFile(theFileToSave);
			return true;
		}
		
		return false;
	}
	
	public void setOutFile(File toFile) {
		outFile = toFile;
	}
	@SuppressWarnings( "deprecation" )
	public void create() throws Exception {
		level = 2;
		version = 1;
		
		
		
		SBMLDocument doc = new SBMLDocument(level, version);
		
	    //doc.addNamespace("html", "xmlns","http://www.w3.org/1999/xhtml");
	    
	    
		allMeta = new SMetabolites();
		allReacts = new SReactions();
		
		
		// Create a new SBML model, and add a compartment to it.
		//System.out.println(databaseName);
		//String dbN = databaseName.replace("Model", "");
		

		//dbN = dbN.replace(" ", "");
		
		//Replace the following with something proper (see issue # ):
		String dbN = "someModel";
		
		if (outFile.getName().endsWith(".xml")) {
			dbN = outFile.getName().substring(0, outFile.getName().length() - 4);
		}
		
		JSBMLValidator validator = new JSBMLValidator();
		String validDbN = validator.makeValidID(dbN);
		Model model = doc.createModel(validDbN);
		//Model model = doc.createModel(dbN + "1");
		UnitDefinition mmolgh = new UnitDefinition();
		
		Unit mole = new Unit();
		mole.setKind(Kind.MOLE);
		mole.setLevel(level);
		mole.setScale(-3);
		mole.setVersion(version);
		
		Unit gram = new Unit();
		gram.setKind(Kind.GRAM);
		gram.setLevel(level);
		gram.setVersion(version);
		//gram.setLevel(3);
		gram.setExponent(-1);
		
		Unit second = new Unit();
		second.setKind(Kind.SECOND);
		second.setLevel(level);
		second.setMultiplier(.00027777);
		second.setVersion(version);
		
		//second.setVersion(version);
		//second.setMultiplier(.00027777);
		second.setExponent(-1);
		
		mmolgh.setName("mmol_per_gDW_per_hr");
		mmolgh.setId("mmol_per_gDW_per_hr");
		mmolgh.setLevel(level);
		mmolgh.setVersion(version);
		
		mmolgh.addUnit(mole);
		mmolgh.addUnit(gram);
		mmolgh.addUnit(second);
		
		
		model.addUnitDefinition(mmolgh);
		
		
		allMeta.setModel(model);
		allReacts.setModel(model);
		
		
		/*for (Species spec : allMeta.allSpecies) {
			System.out.println(spec.getId());
		}*/
		
		//Compartment compartment = model.createCompartment("default");
		//compartment.setSize(1d);
		
		// C
		// Create a model history object and add author information to it.
		//History hist = model.getHistory(); // Will create the History, if it does not exist
		//Creator creator = new Creator("Given Name", "Family Name", "Organisation", "My@EMail.com");
		//hist.addCreator(creator);
		
		// Create some sample content in the SBML model.
		//Species specOne = model.createSpecies("test_spec1", compartment);
		//Species specTwo = model.createSpecies("test_spec2", compartment);
		
		//Reaction sbReaction = model.createReaction("reaction_id");
		
		
		// Add a substrate (SBO:0000015) and product (SBO:0000011) to the reaction.
		/*SpeciesReference subs = sbReaction.createReactant(specOne);
		subs.setSBOTerm(15);
		SpeciesReference prod = sbReaction.createProduct(specTwo);
		prod.setSBOTerm(11);*/
		
		// For brevity, WE DO NOT PERFORM ERROR CHECKING, but you should,
		// using the method doc.checkConsistency() and then checking the error log.
		
		new SBMLWriter();
		
		
		if (null == outFile) {
			SBMLWriter.write(doc, "test.xml", "MOST", "1.0");
		}
		else {
			SBMLWriter.write(doc, outFile, "MOST", "1.0");
		}
		
		//System.out.println("Successfully outputted to " + outFile);
	}
	
	public class SMetabolites {
		public Model model;
		public ArrayList<SBMLMetabolite> allMetabolites;
		public ArrayList<Species> allSpecies;
	
		public SMetabolites() {
			allMetabolites = new ArrayList< SBMLMetabolite >();
			allSpecies = new ArrayList< Species >();
		}
		
		public void setDatabase(String name) {
			
		}
		
		/*public SMetabolites(MetaboliteFactory mFactory) {
			this.parseAllMetabolites(mFactory);
		}*/
		
		public void setModel(Model model) {
			this.model = model;
			this.parseAllMetabolites();
		}
		
		public void parseAllMetabolites() {
			MetaboliteFactory mFactory = new MetaboliteFactory(sourceType);
			// TODO: change to size of collection
			int length = GraphicalInterface.metabolitesTable.getRowCount();
			//mFactory.getMetaboliteById(metaboliteId, sourceType, databaseName);
			/*
			System.out.print("Currently of size: ");
			System.out.print(length);
			System.out.print("\n");
			*/
			int metabRow = 0;
			int blankMetabAbbrCount = 1;
			compartments = new HashMap< String, Compartment >();
			for (int c = 0; c < LocalConfig.getInstance().getListOfCompartments().size(); c++) {
				String compId = LocalConfig.getInstance().getListOfCompartments().get(c).getId().toString();
				String compName = compId;
				if (LocalConfig.getInstance().getListOfCompartments().get(c).getName() != null &&
						LocalConfig.getInstance().getListOfCompartments().get(c).getName().toString().length() > 0) {
					compName = LocalConfig.getInstance().getListOfCompartments().get(c).getName().toString();
				}  
				if (!compartments.containsKey(compId)) {
					Compartment temp = model.createCompartment(compId);
					temp.setName(compName);
					if (LocalConfig.getInstance().getListOfCompartments().get(c).getOutside() != null &&
							LocalConfig.getInstance().getListOfCompartments().get(c).getOutside().toString().length() > 0) {
						temp.setOutside(LocalConfig.getInstance().getListOfCompartments().get(c).getOutside().toString());
					}
					compartments.put(compId,temp);
				}
			}
			// compartment cannot be empty in SBML
			String blankCompName = GraphicalInterfaceConstants.DEFAULT_COMPARTMENT_ID + Integer.toString(1);
			if (compartments.containsKey(blankCompName)) {
				blankCompName += "_1";
			}
			
			for (int i=0; i < length; i++) {
				SBMLMetabolite curMeta = (SBMLMetabolite) mFactory.getMetaboliteByRow(i);
				//SBMLMetabolite curMeta = (SBMLMetabolite) mFactory.getMetaboliteById(i);
				// if metabolite name column or metabolite abbreviation contain a value, add to model
				if ((curMeta.getMetaboliteName() != null && curMeta.getMetaboliteName().trim().length() > 0) ||
						(curMeta.getMetaboliteAbbreviation() != null && curMeta.getMetaboliteAbbreviation().trim().length() > 0)) {
					// creates abbreviation for metabolite where name is not blank and abbreviation is blank
					// these blank metabolites are now rewritten before this code is run so this should never occur
					if (curMeta.getMetaboliteAbbreviation() == null || curMeta.getMetaboliteAbbreviation().trim().length() == 0) {
						//curMeta.setMetaboliteAbbreviation(SBMLConstants.METABOLITE_ABBREVIATION_PREFIX + "_" + blankMetabAbbrCount);
						curMeta.setMetaboliteAbbreviation(SBMLConstants.METABOLITE_ABBREVIATION_PREFIX + blankMetabAbbrCount);
						blankMetabAbbrCount += 1;
					}
					JSBMLValidator validator = new JSBMLValidator();
					String abbr = validator.makeValidID(curMeta.getMetaboliteAbbreviation());
					curMeta.setMetaboliteAbbreviation(abbr);
					String comp = validator.replaceInvalidSBMLCharacters(curMeta.getCompartment());
					String compTrim = comp.trim();
					
					if (compTrim == null || compTrim.length() == 0) {
						compTrim = blankCompName;
					}
					if (curMeta.getCompartment() == null || curMeta.getCompartment().trim().length() == 0) {
						curMeta.setCompartment(blankCompName);
					}
					// leave this here for csv models where there is no list of compartments
					if (!compartments.containsKey(compTrim)) {
						Compartment temp = model.createCompartment(compTrim);
						temp.setName(compTrim);
						compartments.put(compTrim,temp);
					}

					this.allMetabolites.add(curMeta);	
					metabolitesMap.put(metabRow, curMeta);
					metabRow += 1;	
				}					
			}
			
			if (this.model != null) {
				this.devModel();
			}
		}
		
		public Species getSpecies(String mName) {
			Species match = null;
			for (Species cur : allSpecies) {
				if (cur.getName() == mName) {
					match = cur;
				}
			}
			return match;	
		}
		
		public void devModel() {
			String comp;
			for (SBMLMetabolite cur : allMetabolites) {
				comp = cur.getCompartment();
								
				Compartment compartment = compartments.get(comp);
				String bound = cur.getBoundary();
				String mAbrv = cur.getMetaboliteAbbreviation();
				
				JSBMLValidator validator = new JSBMLValidator();
				// this should not be necessary since the model is being rewritten 
				// before the writer is called
				mAbrv = validator.makeValidID(mAbrv);
				
				String mName = validator.replaceInvalidSBMLCharacters(cur.getMetaboliteName());
				
				try {
				Species curSpec = model.createSpecies(mAbrv, compartment);
				curSpec.setId(mAbrv);
				if (null != mName) {
					curSpec.setName(mName);
				}
				
				if (null != bound) {
					boolean b = false;
					if (bound.equals("true")) {
						b = true;
					} else if (bound.equals("false")) {
						b = false;
					}
					//curSpec.setBoundaryCondition(Boolean.getBoolean(bound));
					curSpec.setBoundaryCondition(b);
				}
				
				// write notes

				curSpec.addNamespace("html:p");

				String charge = "CHARGE:" + " " + cur.getCharge();
				curSpec.appendNotes(charge);
				for (int n = 0; n < LocalConfig.getInstance().getMetabolitesMetaColumnNames().size(); n++) {
					String value = "";
					if (cur.getMetaValues().get(n) != null) {
						value = validator.replaceInvalidSBMLCharacters(cur.getMetaValues().get(n));
					}			
					String note = LocalConfig.getInstance().getMetabolitesMetaColumnNames().get(n) + ": " + value;
					curSpec.appendNotes(note);
				}
				
				allSpecies.add(curSpec);
				speciesMap.put(mName, curSpec);
				//System.out.println(mName);
				
				}
				catch (Exception e) {
					/*
					System.out.println("Error: " + e.getMessage());
					System.out.println(mAbrv + " couldn't be added to model");
					System.out.println();
					*/
				}
				
				//SpeciesReference curSpecRef = new SpeciesReference(); //TODO: figure spec ref
				
				//curSpecRef.setSpecies(curSpec);
				//curSpecRef.setId(mName);
				//speciesRefMap.put(mName, curSpecRef);
			}
			
		}
	}
	
	public class SReactions {
		public Model model;
		public ArrayList<SBMLReaction> allReactions;
	
		public SReactions() {
			allReactions = new ArrayList< SBMLReaction >();
		}
		public void setModel(Model model) {
			this.model = model;
			this.parseAllReactions();
		}
		
		public void parseAllReactions() {
			ReactionFactory rFactory = new ReactionFactory(sourceType);
			
			//int length = rFactory.getAllReactions().size();
			
			int blankReacAbbrCount = 1;
			for (int i = 0; i < GraphicalInterface.reactionsTable.getRowCount(); i++) {
			//for (int i = 0 ; i< length; i++) {
				SBMLReaction curReact = (SBMLReaction) rFactory.getReactionByRow(i);
				// if row contains a reaction equation, it is considered valid, else will not be in model
				// if reaction abbreviation is empty, it will be named "R_" + number
				if (curReact.getReactionEqunAbbr() != null && curReact.getReactionEqunAbbr().trim().length() > 0) {
					//SBMLReaction curReact = (SBMLReaction) rFactory.getReactionById(i);
					// these blank reactions are now rewritten before this code is run so this should never occur
					if (curReact.getReactionAbbreviation() == null || curReact.getReactionAbbreviation().trim().length() == 0) {
						//curReact.setReactionAbbreviation(SBMLConstants.REACTION_ABBREVIATION_PREFIX + "_" + blankReacAbbrCount);
						curReact.setReactionAbbreviation(SBMLConstants.REACTION_ABBREVIATION_PREFIX + blankReacAbbrCount);
						blankReacAbbrCount += 1;
					}
									
					allReactions.add(curReact);	
				}											
			}
			
			
			if (this.model != null) {
				this.devModel();
			}
		}
		
		/*public Species getReaction(String mName) {
			Species match = null;
			for (Species cur : allReactions) {
				if (cur.getName() == mName) {
					match = cur;
				}
			}
			return match;	
		}*/
		
		
		public void devModel() {
			//model.addNamespace("html");
			//model.addNamespace("html:p");
			MetaboliteFactory mFactory = new MetaboliteFactory(sourceType);	
			
			/*LocalParameter lParaml = new LocalParameter("LOWER_BOUND");
			LocalParameter uParaml = new LocalParameter("UPPER_BOUND");
			LocalParameter oParaml = new LocalParameter("OBJECTIVE_COEFFICIENT");
			LocalParameter fParaml = new LocalParameter("FLUX_VALUE");
			LocalParameter rParaml = new LocalParameter("REDUCED_COST");
			*/
			
			//The following handles the 1 to 1 relation of instance variables to values 
			Map<Integer, Map<String, LocalParameter>> allparams = new HashMap< Integer, Map< String, LocalParameter >>();
			
			String lowerStr = "LOWER_BOUND";
			String upperStr = "UPPER_BOUND";
			String objStr = "OBJECTIVE_COEFFICIENT";
			String fluxStr = "FLUX_VALUE";
			String redStr = "REDUCED_COST";
			
			String unitStr = "mmol_per_gDW_per_hr";
			UnitDefinition uD = model.getUnitDefinition(unitStr);
			
			for (int i =0 ; i < allReactions.size(); i++) {
				LocalParameter lParaml = new LocalParameter(lowerStr);
				LocalParameter uParaml = new LocalParameter(upperStr);
				LocalParameter oParaml = new LocalParameter(objStr);
				LocalParameter fParaml = new LocalParameter(fluxStr);
				LocalParameter rParaml = new LocalParameter(redStr);
				
				Map<String, LocalParameter> curParams = new HashMap< String, LocalParameter >();
				lParaml.setUnits(uD);
				uParaml.setUnits(uD);
				fParaml.setUnits(uD);
				
				curParams.put(lowerStr, lParaml);
				curParams.put(upperStr, uParaml);
				curParams.put(objStr, oParaml);
				curParams.put(fluxStr, fParaml);
				curParams.put(redStr, rParaml);
				
				
				allparams.put(i, curParams);
				
			}
			
			
			
			ASTNode math = new ASTNode();
			math.setName("FLUX_VALUE");
			int curReacCount = 0;
			ArrayList<String> abbrList = new ArrayList<String>();
			JSBMLValidator validator = new JSBMLValidator();
			for (SBMLReaction cur : allReactions) {
				
				String id = cur.getReactionAbbreviation();
				String name = validator.replaceInvalidSBMLCharacters(cur.getReactionName());
				//ArrayList<SBMLReactant> curReactants = cur.getReactantsList();
								
				//System.out.println("Reactants [Size]: " + String.valueOf(cur.getReactantsList().size()));
				
				//curReactants.addAll(cur.getReactantsList());
			
				//ArrayList<SBMLProduct> curProducts = cur.getProductsList();
				//System.out.println("Products [Size]: " + String.valueOf(curProducts.size()));
				
				KineticLaw law = new KineticLaw();
				
				//Determine values
				Boolean reversible = Boolean.valueOf(cur.getReversible());
				Double lowerBound = cur.getLowerBound(); 
				Double upperBound = cur.getUpperBound(); 
				Double objectCoeff = cur.getBiologicalObjective();
				Double fluxValue = cur.getFluxValue(); 
				Double reducCost = 0.000000; //TODO Find proper value
				
				//Get Local Parameters
				Map<String, LocalParameter> curParam = allparams.get(curReacCount);
				curParam.get(lowerStr).setValue(lowerBound);
				curParam.get(upperStr).setValue(upperBound);
				curParam.get(objStr).setValue(objectCoeff);
				curParam.get(fluxStr).setValue(fluxValue);
				curParam.get(redStr).setValue(reducCost);
				
				//Set to current Kinetic law
				law.addLocalParameter(curParam.get(lowerStr));
				law.addLocalParameter(curParam.get(upperStr));
				law.addLocalParameter(curParam.get(objStr));
				law.addLocalParameter(curParam.get(fluxStr));
				law.addLocalParameter(curParam.get(redStr));
						
				//law.addDeclaredNamespace("FLUX_VALUE", "http://www.w3.org/1998/Math/MathML");
				
				// this should not be necessary since the model is being rewritten 
				// before the writer is called
				String validId = validator.makeValidReactionID(id);
				if (!abbrList.contains(validId)) {
					abbrList.add(validId);
				} else {
					validId = validId + validator.duplicateSuffix(validId, abbrList);
					abbrList.add(validId);
				}
				Reaction curReact = model.createReaction(validId);
				curReact.setName(name);
				curReact.setReversible(reversible);
				
				// write notes
				
				curReact.addNamespace("html:p");
				String ga = validator.replaceInvalidSBMLCharacters(cur.getGeneAssociation());
				String gAssoc = "GENE_ASSOCIATION:" + " " + ga;
				curReact.appendNotes(gAssoc);
				
				String pa = validator.replaceInvalidSBMLCharacters(cur.getProteinAssociation());
				String pAssoc = "PROTEIN_ASSOCIATION:" + " " + pa;
				curReact.appendNotes(pAssoc);
				
				//System.out.println(cur.getSubsystem());
				String sb = validator.replaceInvalidSBMLCharacters(cur.getSubsystem());
				String subsys = "SUBSYSTEM:" + " " + sb;
				curReact.appendNotes(subsys);
				
				String pc = validator.replaceInvalidSBMLCharacters(cur.getProteinClass());
				String pClass = "PROTEIN_CLASS:" + " " + pc;
				curReact.appendNotes(pClass);
				
				String syn = Double.toString(cur.getSyntheticObjective());
				String synClass = "SYNTHETIC_OBJECTIVE:" + " " + syn;
				curReact.appendNotes(synClass);
				
				if (LocalConfig.getInstance().fvaColumnsVisible) {
					String minFlux = Double.toString(cur.getMinFlux());
					String minFluxClass = SBMLConstants.MIN_FLUX_NOTES_NAME + ":" + " " + minFlux;
					curReact.appendNotes(minFluxClass);
					
					String maxFlux = Double.toString(cur.getMaxFlux());
					String maxFluxClass = SBMLConstants.MAX_FLUX_NOTES_NAME + ":" + " " + maxFlux;
					curReact.appendNotes(maxFluxClass);
				}
				
				for (int n = 0; n < LocalConfig.getInstance().getReactionsMetaColumnNames().size(); n++) {
					String value = "";
					if (cur.getMetaValues().get(n) != null) {
						value = validator.replaceInvalidSBMLCharacters(cur.getMetaValues().get(n));
					}
					String note = LocalConfig.getInstance().getReactionsMetaColumnNames().get(n) + ": " + value;
					curReact.appendNotes(note);
				}
						
				if (LocalConfig.getInstance().getReactionEquationMap().get(cur.getId()) != null) {
					for (int r = 0; r < ((SBMLReactionEquation)LocalConfig.getInstance().getReactionEquationMap().get(cur.getId())).getReactants().size(); r++) {
						SpeciesReference curSpec = new SpeciesReference(); //TODO: Figure spec
						SBMLReactant curR = ((SBMLReactionEquation)LocalConfig.getInstance().getReactionEquationMap().get(cur.getId())).getReactants().get(r);
						int inId = curR.getMetaboliteId();
						mFactory.getMetaboliteById(inId);
						String reactAbbrv = curR.getMetaboliteAbbreviation();
				
						//Utilities u = new Utilities();
						String abbr = validator.makeValidID(reactAbbrv);
						reactAbbrv = abbr;
						curSpec.setSpecies(reactAbbrv); 
						//curSpec.setName(reactAbbrv);
											
						//curSpec.setId(reactAbbrv);
						curSpec.setStoichiometry(curR.getStoic());
						
						curSpec.setLevel(level);
						curSpec.setVersion(version);
						
						curReact.addReactant(curSpec);
					}
					
					for (int p = 0; p < ((SBMLReactionEquation)LocalConfig.getInstance().getReactionEquationMap().get(cur.getId())).getProducts().size(); p++) {
						SpeciesReference curSpec = new SpeciesReference(); //TODO: Figure spec
						SBMLProduct curP = ((SBMLReactionEquation)LocalConfig.getInstance().getReactionEquationMap().get(cur.getId())).getProducts().get(p);
						int inId = curP.getMetaboliteId();
						mFactory.getMetaboliteById(inId);
						String reactAbbrv = curP.getMetaboliteAbbreviation();
						
						//Utilities u = new Utilities();
						String abbr = validator.makeValidID(reactAbbrv);
						reactAbbrv = abbr;
						
						curSpec.setSpecies(reactAbbrv); 
						//curSpec.setName(reactAbbrv);
											
						//curSpec.setId(reactAbbrv);
						curSpec.setStoichiometry(curP.getStoic());
						
						curSpec.setLevel(level);
						curSpec.setVersion(version);
						
						curReact.addProduct(curSpec);
					}
				}
			
//				curReact.addNamespace("html:p");
//				curReact.appendNotes(attr);
				
				law.setMath(math);
				curReact.setKineticLaw(law);
				
				curReacCount++;

				//curReact.setNotes(attr.toString());
				
				
				//curReact.setNotes(node);
				//curReact.writeXMLAttributes();
				
				
				
				//"http://www.w3.org/1998/Math/MathML"
				
				
				
				
				
				/*
				
				for (Parameter param : parameters) {
					String curId = param.getId();
					String value = param.getValue();
					String units = param.getUnits();
					
					
					
					lParam.setId(curId);
					lParam.setName(curId);
					lParam.setMetaId(curId);
					
					lParam.setValue(Double.valueOf(value));
					
					if (units != null) {
						lParam.setUnits(units);
					}
					
					//law.addLocalParameter(lParam);
				}
				*/
				
				//ASTNode mathml = new ASTNode();
				
				//law.setMath(math)addNamespace("http://www.w3.org/1998/Math/MathML");
								
				//curReact.setKineticLaw(law);
			}
			
		}
	}
	
	
//	public class Notes {
//		public String geneAssoc;
//		public String proteinAssoc;
//		public String subSystem;
//		public String proteinClass;
//		
//		public Notes(SBMLReaction react) {
//			geneAssoc = react.getGeneAssociation();
//			proteinAssoc = react.getProteinAssociation();
//			subSystem = react.getSubsystem();
//			proteinClass = react.getProteinClass();
//		}
//		
//		public String[] getNotes() {
//			String[] lines = new String[4];
//			
//			String[] keys = this.getKeys();
//			String[] values = this.getValues();
//			
//			for (int i=0 ; i<4; i++) {
//				lines[i] = this.toNode(keys[i],values[i]);
//			} 
//			return lines;
//		}
//		
//		@Override
//		public String toString() {
//			String curStr = "";
//			String[] keys = this.getKeys();
//			String[] values = this.getValues();
//			for (int i=0 ; i<4; i++) {
//				curStr += this.toNode(keys[i],values[i]);
//			}
//			return curStr;
//		}
//		
//		public String[] getKeys() {
//			String[] keys = new String[4];
//			keys[0] = "GENE_ASSOCIATION";
//			keys[1] = "PROTEIN_ASSOCIATION";
//			keys[2] = "SUBSYSTEM";
//			keys[3] = "PROTEIN_CLASS";
//			return keys;
//		}
//		
//		public String[] getValues() {
//			String[] values = new String[4];
//			values[0] = geneAssoc;
//			values[1] = proteinAssoc;
//			values[2] = subSystem;
//			values[3] = proteinClass;
//			return values;
//		}
//		
//		public String toNode(String key, String value) {
//			String curStr = "<html:p>";
//			curStr += key + ": " + value + "</html:p>\n";
//			return curStr;
//			
//		}
//		
//		public void setGeneAssoc(String assoc) {
//			
//		}
//	}
	
	
	class XMLFileFilter extends javax.swing.filechooser.FileFilter {
	    public boolean accept(File f) {
	        return f.isDirectory() || f.getName().toLowerCase().endsWith(".xml");
	    }
	    
	    public String getDescription() {
	        return ".xml files";
	    }
	}
	
}

