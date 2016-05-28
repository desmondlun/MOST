package edu.rutgers.MOST.presentation;

import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;

import edu.rutgers.MOST.config.LocalConfig;
import edu.rutgers.MOST.data.SBMLConstants;
import edu.rutgers.MOST.data.SBMLReaction;

/**
 * Class contains commonly used functions and eliminate redundancy of code.
 *
 */
public class Utilities {

	public String createDateTimeStamp() {
		Date date = new Date();
		Format formatter;
		formatter = new SimpleDateFormat("_yyMMdd_HHmmss");
		String dateTimeStamp = formatter.format(date);
				
		return dateTimeStamp;
	}
	
	// based on http://www.java2s.com/Code/Java/File-Input-Output/DeletefileusingJavaIOAPI.htm
	public static void delete(String fileName) {
		// A File object to represent the filename
		File f = new File(fileName);

		// Make sure the file or directory exists and isn't write protected
		if (!f.exists())
			//System.out.println("Delete: no such file or directory: " + fileName);

		if (!f.canRead())
			//System.out.println("Delete: can't read: "+ fileName);
		
		if (!f.canWrite())
			//System.out.println("Delete: write protected: "+ fileName);

		if (!f.canExecute())
			//System.out.println("Delete: can't execute: "+ fileName);
		
		// If it is a directory, make sure it is empty
		if (f.isDirectory()) {
			String[] files = f.list();
			if (files.length > 0) {
				//System.out.println("Delete: directory not empty: " + fileName);
			}				
		}
		
        // Attempt to delete it
        boolean success = f.delete();
        if (success)
        	//System.out.println(fileName + " deletion succeeded");
	    if (!success) {
	    	//System.out.println(fileName + " deletion failed");
	    }	 
	}
	
	public void deleteFileIfExists(String filename) {
		File f = new File(filename);
		if (f.exists()) {
			//System.out.println(filename);
			delete(filename);						
		}
	}
	
	// from http://www.javadb.com/how-to-rename-a-file-change-file-name
	public void renameFile(String file, String toFile) {

        File toBeRenamed = new File(file);

        if (!toBeRenamed.exists() || toBeRenamed.isDirectory()) {

            //System.out.println("File does not exist: " + file);
            return;
        }

        File newFile = new File(toFile);

        //Rename
        if (toBeRenamed.renameTo(newFile)) {
            //System.out.println("File has been renamed.");
        } else {
            //System.out.println("Error renmaing file");
        }
    }
	
	public String lastPath(String path, JFileChooser fileChooser) {
		// based on http://stackoverflow.com/questions/1503555/how-to-find-my-documents-folder
		// works for Windows XP and Windows 7
		FileSystemView fsv = fileChooser.getFileSystemView();
		String defaultPath = fsv.getDefaultDirectory().getPath();
		// if username is preferable this works
		//String defaultPath = System.getenv("USERPROFILE");
		if (path == null) {
			return defaultPath;
		} else {
			File f = new File(path);
			if (f.exists()) {
				return path;
			} else {
				return defaultPath;
			}
		}
	}
	
	public static String getMOSTSettingsPath()
	{
		File destDir = null;
		if( System.getProperty( "os.name" ).contains( "Windows" ) )
		{
			if( System.getProperty( "os.name" ).equals( "Windows XP" ) )
			{
				destDir = new File(
						System.getProperty( "user.home" )
								+ GraphicalInterfaceConstants.SETTINGS_PATH_SUFFIX_WINDOWS_XP
								+ GraphicalInterfaceConstants.FOLDER_NAME );
			}
			else
			{
				destDir = new File( System.getenv( "LOCALAPPDATA" )
						+ GraphicalInterfaceConstants.FOLDER_NAME );
			}
		}
		else if( System.getProperty( "os.name" ).toLowerCase()
				.contains( "mac os x" ) )
		{
			destDir = new File( System.getenv( "HOME" ) + "/Library/"
					+ GraphicalInterfaceConstants.FOLDER_NAME );
		}
		else if( System.getProperty( "os.name" ).equals( "Linux" ) )
		{
			destDir = new File( GraphicalInterfaceConstants.FOLDER_NAME );
		}
		if( !destDir.exists() )
			destDir.mkdir();
		
		return destDir.getAbsolutePath() + File.separatorChar;
	}
	
	public String createLogFileName(String name) {
		return getMOSTSettingsPath() + name;		
	}
	
	public void showResizableDialog(String errorTitle, String errorDescription, String errorMessage) {
		final ArrayList<Image> icons = new ArrayList<Image>(); 
		icons.add(new ImageIcon("etc/most16.jpg").getImage()); 
		icons.add(new ImageIcon("etc/most32.jpg").getImage());
		
		ResizableDialog r = new ResizableDialog(errorTitle, errorDescription, errorMessage);
		
		r.setIconImages(icons);
    	r.setLocationRelativeTo(null);
    	r.setVisible(true);
	}
	
	/**
	 * Appends numeric suffix to duplicate abbreviations such as [1]. [2].
	 * These fields are keys in SBML and must be unique.
	 * @param value
	 * @param abbreviationIdMap
	 * @return
	 */
	public String duplicateSuffix(String value, Map<String, Object> abbreviationIdMap) {
		String duplicateSuffix = GraphicalInterfaceConstants.DUPLICATE_SUFFIX;
		if (abbreviationIdMap.containsKey(value + duplicateSuffix)) {
			int duplicateCount = Integer.valueOf(duplicateSuffix.substring(1, duplicateSuffix.length() - 1));
			while (abbreviationIdMap.containsKey(value + duplicateSuffix.replace("1", Integer.toString(duplicateCount + 1)))) {
				duplicateCount += 1;
			}
			duplicateSuffix = duplicateSuffix.replace("1", Integer.toString(duplicateCount + 1));
		}
		return duplicateSuffix;
	}
	
	/**
	 * Appends suffix to duplicate abbreviations 
	 * These fields are keys in SBML and must be unique.
	 * @param value
	 * @param abbreviationIdMap
	 * @return
	 */
	public String uniqueSaveName(String value, ArrayList<String> list) {
		String duplicateSuffix = GraphicalInterfaceConstants.DUPLICATE_SBML_SUFFIX;
		while (list.contains(value)) {
			value += duplicateSuffix;
		}
		return value;
	}
	
	/**
	 * Returns next available letter (to upper case) to be used as mnemonic
	 * from parameter name and list of used mnemonics.
	 * @param usedMnemonics
	 * @param parameterName
	 * @return mnemonic
	 */
	public String findMnemonic(ArrayList<String> usedMnemonics, String parameterName) {
		String mnemonic = "";
		for (int i = 0; i < parameterName.length(); i++) {
			mnemonic = Character.toString(parameterName.charAt(i)).toUpperCase();
			if (!usedMnemonics.contains(mnemonic)) {
				break;
			} 
		}
		
		return mnemonic;
		
	}
	
	public static void copyFile(File sourceFile, File destFile) throws IOException {
		if(!destFile.exists()) {
			destFile.createNewFile();
		}

		FileChannel source = null;
		FileChannel destination = null;

		try {
			source = new FileInputStream(sourceFile).getChannel();
			destination = new FileOutputStream(destFile).getChannel();
			destination.transferFrom(source, 0, source.size());
		}
		finally {
			if(source != null) {
				source.close();
			}
			if(destination != null) {
				destination.close();
			}
		}
	}
	
	public String csvLoadErrorMessage() {
		String message = GraphicalInterfaceConstants.STATUS_BAR_PREFIX;
		boolean itemAdded = false;
		if (LocalConfig.getInstance().getInvalidReactions().size() > 0) {
			if (itemAdded) {
				message += ", " + GraphicalInterfaceConstants.INVALID_REACTION_EQUATION_STATUS_BAR_MESSAGE;
			} else {
				message += GraphicalInterfaceConstants.INVALID_REACTION_EQUATION_STATUS_BAR_MESSAGE;
				itemAdded = true;
			}
		}
		if (LocalConfig.getInstance().getInvalidLowerBoundReversibleCombinations().size() > 0) {
			if (itemAdded) {
				message += ", " + GraphicalInterfaceConstants.INVALIID_LOWER_BOUND_REVERSIBLE_COMBINATION_STATUS_BAR_MESSAGE;
			} else {
				message += GraphicalInterfaceConstants.INVALIID_LOWER_BOUND_REVERSIBLE_COMBINATION_STATUS_BAR_MESSAGE;
				itemAdded = true;
			}
		}
		if (LocalConfig.getInstance().getInvalidEquationReversibleCombinations().size() > 0) {
			if (itemAdded) {
				message += ", " + GraphicalInterfaceConstants.INVALIID_EQUATION_REVERSIBLE_COMBINATION_STATUS_BAR_MESSAGE;
			} else {
				message += GraphicalInterfaceConstants.INVALIID_EQUATION_REVERSIBLE_COMBINATION_STATUS_BAR_MESSAGE;
				itemAdded = true;
			}
		}
		return message += ".";
	}
	
	public String sbmlLoadMessage() {
		String message = GraphicalInterfaceConstants.STATUS_BAR_PREFIX;
		if (LocalConfig.getInstance().getInvalidLowerBoundReversibleCombinations().size() > 0) {
			message += GraphicalInterfaceConstants.INVALIID_LOWER_BOUND_REVERSIBLE_COMBINATION_STATUS_BAR_MESSAGE;
		}
		return message += ".";
	}
	
	public String statusBarMessage() {
		String message = GraphicalInterfaceConstants.STATUS_BAR_PREFIX;
		boolean itemAdded = false;
		if (LocalConfig.getInstance().getSuspiciousMetabolites().size() > 0) {
			message += GraphicalInterfaceConstants.SUSPICIOUS_METABOLITES_STATUS_BAR_MESSAGE;
			itemAdded = true;
		}
		if (LocalConfig.getInstance().getInvalidReactions().size() > 0) {
			if (itemAdded) {
				message += ", " + GraphicalInterfaceConstants.INVALID_REACTION_EQUATION_STATUS_BAR_MESSAGE;
			} else {
				message += GraphicalInterfaceConstants.INVALID_REACTION_EQUATION_STATUS_BAR_MESSAGE;
				itemAdded = true;
			}
		}
		if (LocalConfig.getInstance().getInvalidLowerBoundReversibleCombinations().size() > 0) {
			if (itemAdded) {
				message += ", " + GraphicalInterfaceConstants.INVALIID_LOWER_BOUND_REVERSIBLE_COMBINATION_STATUS_BAR_MESSAGE;
			} else {
				message += GraphicalInterfaceConstants.INVALIID_LOWER_BOUND_REVERSIBLE_COMBINATION_STATUS_BAR_MESSAGE;
				itemAdded = true;
			}
		}
		if (LocalConfig.getInstance().getInvalidEquationReversibleCombinations().size() > 0) {
			if (itemAdded) {
				message += ", " + GraphicalInterfaceConstants.INVALIID_EQUATION_REVERSIBLE_COMBINATION_STATUS_BAR_MESSAGE;
			} else {
				message += GraphicalInterfaceConstants.INVALIID_EQUATION_REVERSIBLE_COMBINATION_STATUS_BAR_MESSAGE;
				itemAdded = true;
			}
		}
		return message += ".";
	}
	
	/*********************************************************************************/
	// metabolite abbreviation processing methods
	/*********************************************************************************/
	
	public String maybeRemovePrefixAndSuffix(String metabAbbr) {
		String abbr = "";
		abbr = maybeRemovePrefix(metabAbbr);
		abbr = maybeRemoveCompartmentSuffix(abbr);
		return abbr;
	}
	
	public String maybeRemovePrefix(String metabAbbr) {
		String abbr = metabAbbr;
		if (hasMetabolitePrefix(metabAbbr)) {
			abbr = metabAbbr.substring(2, metabAbbr.length());
		}
		return abbr;
		
	}
	
	public String maybeRemoveCompartmentSuffix(String metabAbbr) {
		String abbr = metabAbbr;
		// check if metabolite ends with "_x"
		if (hasCompartmentSuffix(metabAbbr)) {
			abbr = metabAbbr.substring(0, metabAbbr.length() - 2);
		}
		return abbr;
	}
	
	public boolean hasMetabolitePrefix(String metabAbbr) {
		for (int i = 0; i < SBMLConstants.METABOLITE_ABBREVIATION_PREFIXES.length; i++) {
			if (metabAbbr.startsWith(SBMLConstants.METABOLITE_ABBREVIATION_PREFIXES[i])) {
				return true;
			}
		}
		
		return false;
		
	}
	
	public boolean hasCompartmentSuffix(String metabAbbr) {
		if (metabAbbr.length() > 2) {
			String ch = metabAbbr.substring(metabAbbr.length() - 2, metabAbbr.length() - 1);
			if (ch.equals("_")) {
				return true;
			}
		}
		
		return false;
		
	}
	
	/**
	 * Make comma separated from ArrayList
	 * @param list
	 * @return
	 */
	public String makeCommaSeparatedList(ArrayList<String> list) {
		String name = "";
		if (list.size() > 0) {
			name = list.get(0);
			if (list.size() > 1) {
				for (int k = 1; k < list.size(); k++) {
					name += ", " + list.get(k);
				}
			}
		}
		
		return name; 
	}
	
	public String formattedString(SBMLReaction reaction) {
		String formattedOutput = "";
		String abbr = reaction.getReactionAbbreviation();
		if (abbr.length() > 11) {
			abbr = abbr.substring(0, 11) + "...";
		}
		String name = reaction.getReactionName();
		if (name.length() > 36) {
			name = name.substring(0, 36) + "...";
		}
		String eqAbbr = reaction.getReactionEqunAbbr();
		if (eqAbbr.length() > 46) {
			eqAbbr = eqAbbr.substring(0, 46) + "...";
		}
		String eqNames = reaction.getReactionEqunNames();
		formattedOutput = String.format("%1$-15s %2$-40s %3$-50s %4$-100s", abbr, name, eqAbbr, eqNames);
		
		return formattedOutput;
		
	}
	
	// based on http://examples.javacodegeeks.com/core-java/text/format-number-with-custom-numberformat
	// based on http://helpdesk.objects.com.au/java/how-to-control-decimal-places-displayed-in-jtable-column
	public String formattedNumber(String value, DecimalFormat formatter, DecimalFormat sciFormatter, double minDecimalFormat, double maxDecimalFormat) {
		try {
			if (Math.abs(Double.valueOf((String) value)) != 0.0 && (Math.abs(Double.valueOf((String) value)) < minDecimalFormat || Math.abs(Double.valueOf((String) value)) > maxDecimalFormat)) {
				try {
					value = sciFormatter.format((Number)Double.valueOf((String) value));
				} catch (NumberFormatException nfe) {
					
				}
			} else {
				try {
					value = formatter.format((Number)Double.valueOf((String) value));
				} catch (NumberFormatException nfe) {
					
				}
			}
		} catch (NumberFormatException nfe) {
			
		}
		return value;
	}

	// from http://stackoverflow.com/questions/19487506/built-in-methods-for-displaying-significant-figures
	public String toSignificantFiguresString(BigDecimal bd, int significantFigures ){
		String test = String.format("%."+significantFigures+"G", bd);
		if (test.contains("E+")){
			test = String.format(Locale.US, "%.0f", Double.valueOf(String.format("%."+significantFigures+"G", bd)));
		}
		return test;
	}

	/**
	 * Returns number rounded to significant figures and in scientific notation if
	 * above or below input values
	 * @param value
	 * @param significantFigures
	 * @param sciFormatter
	 * @param minDecimalFormat
	 * @param maxDecimalFormat
	 * @return
	 */
	public String roundToSignificantFigures(double value, int significantFigures, DecimalFormat sciFormatter, 
		double minDecimalFormat, double maxDecimalFormat) {
		String result = Double.toString(value);
		if (value == Double.valueOf(GraphicalInterfaceConstants.VALID_INFINITY_ENTRY) ||
			value == -Double.valueOf(GraphicalInterfaceConstants.VALID_INFINITY_ENTRY)) {
			result = GraphicalInterfaceConstants.VALID_INFINITY_ENTRY;
		} else {
			result = toSignificantFiguresString(BigDecimal.valueOf(value), significantFigures);
			if (Math.abs(Double.valueOf(result)) != 0.0 && (Math.abs(Double.valueOf(result)) < minDecimalFormat || Math.abs(Double.valueOf(result)) >= maxDecimalFormat)) {
				try {
					result = sciFormatter.format((Number)Double.valueOf(result));
				} catch (NumberFormatException nfe) {

				}
			}
		}
	
		return result;
	}

	/**
	 * Returns plural heading plus list to String if length of input list > 1. 
	 * Else returns singular heading plus input String.
	 * @param items
	 * @param heading
	 * @return
	 */
	public String maybeMakeList(ArrayList<String> items, String heading) {
		String item = "<p>" + heading + ": ";
		if (items.size() > 0) {
			item = "<p>" + heading + ": " + items.get(0);
		}
		if (items.size() > 1) {
			item = "<p>" + heading + "(s): " + items.toString();
		}
		return item;
	}
	
	/**
	 * ModelSEED metabolite abbreviations usually end with "_" + compartment suffix.
	 * Models may have "M_" prefix in front of ModelSEED id
	 * Returns metabolite abbreviation with these two items removed
	 * @param abbr
	 * @return
	 */
	public String cleanedUpModelSeedMetaboliteAbbreviation(String abbr) {
		if (abbr.startsWith(SBMLConstants.METABOLITE_ABBREVIATION_PREFIX) && abbr.length() > 2) {
			abbr = abbr.substring(2);
			//System.out.println("1 " + abbr);
		}
		if (abbr.contains("_")) {
			abbr = abbr.substring(0, abbr.indexOf("_"));
			//System.out.println("2 " + abbr);
		}
		
		return abbr;
		
	}

}
