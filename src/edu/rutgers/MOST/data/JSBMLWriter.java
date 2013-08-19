
package edu.rutgers.MOST.data;

import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.filechooser.*;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeNode;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sbml.jsbml.ASTNode;
import org.sbml.jsbml.Compartment;
import org.sbml.jsbml.Creator;
import org.sbml.jsbml.History;
import org.sbml.jsbml.KineticLaw;

import org.sbml.jsbml.LocalParameter;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLException;
import org.sbml.jsbml.SBMLWriter;
import org.sbml.jsbml.SBase;
import org.sbml.jsbml.Species;
import org.sbml.jsbml.SpeciesReference;
import org.sbml.jsbml.Unit;
import org.sbml.jsbml.Unit.Kind;
import org.sbml.jsbml.UnitDefinition;
import org.sbml.jsbml.xml.XMLAttributes;
import org.sbml.jsbml.xml.XMLNamespaces;
import org.sbml.jsbml.xml.XMLNode;

import edu.rutgers.MOST.config.ConfigConstants;
import edu.rutgers.MOST.config.LocalConfig;

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
		//config.setLoadedDatabase(ConfigConstants.DEFAULT_DATABASE_NAME);
		//System.out.println(config.getDatabaseName());
		curSettings = new SettingsFactory();
		if (setOutFile()) {
		
			curConfig = config;
			
			databaseName = config.getDatabaseName();
			
					
			sourceType = "SBML";
			
			if (sourceType == "SBML") {
				this.create();
			}
		}
		
		/*
		if (LocalConfig.getInstance().getCurrentConnection() != null) {
        	try {
				LocalConfig.getInstance().getCurrentConnection().close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        }
        */
		//Connection con = DriverManager.getConnection("jdbc:sqlite:" + config.getDatabaseName() + ".db");
		//LocalConfig.getInstance().setCurrentConnection(con);
		//System.out.print(con.getSchema());
	}
	
	public JSBMLWriter() {
		metabolitesMap = new HashMap();
		speciesMap = new HashMap();
		speciesRefMap = new HashMap();
	}
	
	public void metaMap() {
		
		
	}
	
	
	public boolean setOutFile(){
		JTextArea output = null;
		String lastSaveSBML_path = curSettings.get("LastSaveSBML");
		
		if (lastSaveSBML_path == null) {
			lastSaveSBML_path = ".";
		}
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File(lastSaveSBML_path));
		chooser.setApproveButtonText("Save");
		chooser.setDialogTitle("Save to");
		chooser.setFileFilter(new XMLFileFilter());
		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		
		int option = chooser.showOpenDialog(output); 
		
		if(option == JFileChooser.APPROVE_OPTION){  
			if(chooser.getSelectedFile()!=null)	{  
				String path = chooser.getSelectedFile().getPath();
				if (!path.endsWith(".xml")) {
					path = path + ".xml";
				}
				File theFileToSave = new File(path);				
				this.setOutFile(theFileToSave);
				String rawPathName = chooser.getSelectedFile().getAbsolutePath();
				curSettings.add("LastSaveSBML", rawPathName);
				return true;
			}
			
		}
		return false;
	}
	
	
	public void setOutFile(File toFile) {
		outFile = toFile;
	}
	public void create() throws Exception {
		level = 3;
		version = 1;
		
		
		
		SBMLDocument doc = new SBMLDocument(level, version);
		
	    //doc.addNamespace("html", "xmlns","http://www.w3.org/1999/xhtml");
	    
	    
		allMeta = new SMetabolites();
		allReacts = new SReactions();
		
		
		// Create a new SBML model, and add a compartment to it.
		//System.out.println(databaseName);
		String dbN = databaseName.replace("Model", "");
		

		dbN = dbN.replace(" ", "");
		
		//Replace the following with something proper (see issue # ):
		dbN = "someModel";
		
		Model model = doc.createModel(dbN + "1");
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
		
		// Write the SBML document to a file.
		SBMLWriter sbmlwrite = new SBMLWriter();
		
		
		if (null == outFile) {
			sbmlwrite.write(doc, "test.xml", "MOST", "1.0");
		}
		else {
			sbmlwrite.write(doc, outFile, "MOST", "1.0");
		}
		
		//System.out.println("Successfully outputted to " + outFile);
	}
	
	public class SMetabolites {
		public Model model;
		public ArrayList<SBMLMetabolite> allMetabolites;
		public ArrayList<Species> allSpecies;
	
		public SMetabolites() {
			allMetabolites = new ArrayList();
			allSpecies = new ArrayList();
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
			MetaboliteFactory mFactory = new MetaboliteFactory(sourceType, databaseName);
			int length = mFactory.maximumId();
			//mFactory.getMetaboliteById(metaboliteId, sourceType, databaseName);
			/*
			System.out.print("Currently of size: ");
			System.out.print(length);
			System.out.print("\n");
			*/
			compartments = new HashMap();
			for (int i=1; i <= length; i++) {
				SBMLMetabolite curMeta = (SBMLMetabolite) mFactory.getMetaboliteById(i);
				//System.out.println(curMeta);
				
				String comp = curMeta.getCompartment();
				
				if (!compartments.containsKey(comp)) {
					Compartment temp = model.createCompartment(comp);
					compartments.put(comp,temp);
				}
				
				this.allMetabolites.add(curMeta);	
				metabolitesMap.put(i, curMeta);
				
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
		
		public boolean isNoNumberAtBeginning(String s){
		    return s.matches("^[^\\d].*");
		  }
		
		public boolean isValidID(String mAbrv) {
			if (!isNoNumberAtBeginning(mAbrv)) {
			    return false; //prints /{item}/
			} 
			
			if (mAbrv.startsWith("[")) {
				return false;
			}
			
			return true;
		}
		
		public String makeValidID(String mAbrv) {
			if (!isValidID(mAbrv)) {
				mAbrv = mAbrv.replace("[","");
				mAbrv = mAbrv.replace("]","");
				mAbrv = "m_" + mAbrv;
			}
			return mAbrv;
			
		}
		
		public void devModel() {
			Vector<Species> curSpecies;
			
			int count = 0;
			
			
			
			String comp;
			Compartment curComp;
			for (SBMLMetabolite cur : allMetabolites) {
				comp = cur.getCompartment();
				
				
				
				Compartment compartment = compartments.get(comp);
				String bound = cur.getBoundary();
				String mAbrv = cur.getMetaboliteAbbreviation();
				
				mAbrv = this.makeValidID(mAbrv);
				
				String mName = cur.getMetaboliteName();
				if (null != cur) { 
					//int charge = Integer.getInteger(cur.getCharge());
				}
				
				try {
				Species curSpec = model.createSpecies(mAbrv, compartment);
				curSpec.setId(mAbrv);
				if (null != mName) {
					curSpec.setName(mName);
				}
				//curSpec.setCharge(charge);
				if (null != bound) {
					curSpec.setBoundaryCondition(Boolean.getBoolean(bound));
				}
				//curSpec.setCharge(charge);
				
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
			allReactions = new ArrayList();
		}
		public void setModel(Model model) {
			this.model = model;
			this.parseAllReactions();
		}
		
		public void parseAllReactions() {
			ReactionFactory rFactory = new ReactionFactory(sourceType, databaseName);
			
			int length = rFactory.getAllReactions().size();
			
			
			for (int i = 1 ; i<= length; i++) {
				SBMLReaction curReact = (SBMLReaction) rFactory.getReactionById(i);
				//System.out.println(curReact);
				allReactions.add(curReact);				
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
			Vector<Species> curSpecies;
			
			int count = 0;
			//System.out.println();
			//model.addNamespace("html");
			//model.addNamespace("html:p");
			MetaboliteFactory mFactory = new MetaboliteFactory(sourceType, databaseName);
			ReactantFactory reFactory = new ReactantFactory(sourceType, databaseName);
			ProductFactory prFactory = new ProductFactory(sourceType, databaseName);
			
			
			
			
			/*LocalParameter lParaml = new LocalParameter("LOWER_BOUND");
			LocalParameter uParaml = new LocalParameter("UPPER_BOUND");
			LocalParameter oParaml = new LocalParameter("OBJECTIVE_COEFFICIENT");
			LocalParameter fParaml = new LocalParameter("FLUX_VALUE");
			LocalParameter rParaml = new LocalParameter("REDUCED_COST");
			*/
			
			//The following handles the 1 to 1 relation of instance variables to values 
			Map<Integer, Map<String, LocalParameter>> allparams = new HashMap();
			
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
				
				Map<String, LocalParameter> curParams = new HashMap();
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
			//math.setName("FLUX_VALUE");
			int curReacCount = 0;
			
			for (SBMLReaction cur : allReactions) {
				
				String id = cur.getReactionAbbreviation();
				String name = cur.getReactionName();
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
				
								
				
				Reaction curReact = model.createReaction(id);
				curReact.setName(name);
				curReact.setReversible(reversible);
				
				String geneAssoc = cur.getMeta1();
				String proteinAssoc = cur.getMeta2();
				String subSystem = cur.getMeta3();
				String proteinClass = cur.getMeta4();
				
				XMLNode gAssoc = new XMLNode();
				XMLNode pAssoc = new XMLNode();
				XMLAttributes gAssocA = new XMLAttributes();
				gAssocA.add("GENE_ASSOCIATION:", geneAssoc);
				//XMLNamespaces nSpace = new XMLNameSpaces();
				//nSpace.add(uri, prefix)
				//gAssoc.setNamespaces("html:p");
				
				gAssoc.setAttributes(gAssocA);
				
				//curReact.setNotes(gAssoc);
				
				
				
				
				
				//node.clearAttributes();
				//gAssoc.addAttr("GENE_ASSOCIATION", geneAssoc);
				//curReact.setNotes(gAssoc);
				
				//pAssoc.addAttr("PROTEIN_ASSOCIATION", proteinAssoc);
				//curReact.appendNotes(pAssoc);
				//node.addAttr("SUBSYSTEM",subSystem);
				//node.addAttr("PROTEIN_CLASS",proteinClass);
				
				
				Notes attr = new Notes(cur);
				
								
				ArrayList<ModelReactant> curReactants = reFactory.getReactantsByReactionId(cur.getId());
				
				ArrayList<ModelProduct> curProducts = prFactory.getProductsByReactionId(cur.getId());
				
				for (ModelReactant curReactant : curReactants) {
					try {
						SpeciesReference curSpec = new SpeciesReference(); //TODO: Figure spec
						SBMLReactant curR = (SBMLReactant) curReactant;
						
						int inId = curR.getMetaboliteId();
						SBMLMetabolite sMReactant = (SBMLMetabolite) mFactory.getMetaboliteById(inId);
						String reactAbbrv = sMReactant.getMetaboliteAbbreviation();
						//System.out.println(reactAbbrv);
						//SpeciesReference curSpec = speciesRefMap.get(reactAbbrv);
						reactAbbrv = reactAbbrv.replace("[","");
						reactAbbrv = reactAbbrv.replace("]","");
						reactAbbrv = "m" + reactAbbrv;
						
						//reactAbbrv = makeValidID(reactAbbrv);
						curSpec.setSpecies(reactAbbrv); 
						//curSpec.setName(reactAbbrv);
											
						//curSpec.setId(reactAbbrv);
						curSpec.setStoichiometry(curR.getStoic());
						
						curSpec.setLevel(level);
						curSpec.setVersion(version);
						//curReact.setLevel(level);
						//curReact.setVersion(version);
						
						curReact.addReactant(curSpec);
					}
					catch (Exception e) {
						//System.out.println("Error: " + e.getMessage());
						
					}
				}
				
				for (ModelProduct curProduct : curProducts) {
					try {
						SpeciesReference curSpec = new SpeciesReference();
						SBMLProduct curP = (SBMLProduct) curProduct;
						String mAbbrv = curP.getMetaboliteAbbreviation();
						//SpeciesReference curSpec = speciesRefMap.get(mAbbrv);
						mAbbrv = mAbbrv.replace("[","");
						mAbbrv = mAbbrv.replace("]","");
						mAbbrv = "m" + mAbbrv;
						
						curSpec.setSpecies(mAbbrv);
						
						//curSpec.setName(mAbbrv);
						curSpec.setStoichiometry(curP.getStoic());
						curSpec.setLevel(level);
						curSpec.setVersion(version);
						
						curReact.addProduct(curSpec);
					}
					catch (Exception e) {
						//System.out.println(e.getMessage());
					
					}
				}
				
				//curReact.addNamespace("html:p");
				//curReact.appendNotes(attr);
				
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
	
	
	public class Notes {
		public String geneAssoc;
		public String proteinAssoc;
		public String subSystem;
		public String proteinClass;
		
		public Notes(SBMLReaction react) {
			geneAssoc = react.getMeta1();
			proteinAssoc = react.getMeta2();
			subSystem = react.getMeta3();
			proteinClass = react.getMeta4();
		}
		
		public String[] getNotes() {
			String[] lines = new String[4];
			
			String[] keys = this.getKeys();
			String[] values = this.getValues();
			
			for (int i=0 ; i<4; i++) {
				lines[i] = this.toNode(keys[i],values[i]);
			} 
			return lines;
		}
		
		@Override
		public String toString() {
			String curStr = "";
			String[] keys = this.getKeys();
			String[] values = this.getValues();
			for (int i=0 ; i<4; i++) {
				curStr += this.toNode(keys[i],values[i]);
			}
			return curStr;
		}
		
		public String[] getKeys() {
			String[] keys = new String[4];
			keys[0] = "GENE_ASSOCIATION";
			keys[1] = "PROTEIN_ASSOCIATION";
			keys[2] = "SUBSYSTEM";
			keys[3] = "PROTEIN_CLASS";
			return keys;
		}
		
		public String[] getValues() {
			String[] values = new String[4];
			values[0] = geneAssoc;
			values[1] = proteinAssoc;
			values[2] = subSystem;
			values[3] = proteinClass;
			return values;
		}
		
		public String toNode(String key, String value) {
			String curStr = "<html:p>";
			curStr += key + ": " + value + "</html:p>\n";
			return curStr;
			
		}
		
		public void setGeneAssoc(String assoc) {
			
		}
	}
	
	class XMLFileFilter extends javax.swing.filechooser.FileFilter {
	    public boolean accept(File f) {
	        return f.isDirectory() || f.getName().toLowerCase().endsWith(".xml");
	    }
	    
	    public String getDescription() {
	        return ".xml files";
	    }
	}
	
}

