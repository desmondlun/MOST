package edu.rutgers.MOST.presentation;

import java.io.File;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.rutgers.MOST.data.MetabolitesMetaColumnManager;
import edu.rutgers.MOST.data.ReactionsMetaColumnManager;

public class Utilities {

	public String createDateTimeStamp() {
		Date date = new Date();
		Format formatter;
		formatter = new SimpleDateFormat("_yyMMdd_HHmmss");
		String dateTimeStamp = formatter.format(date);
				
		return dateTimeStamp;
	}
	
	// based on http://www.java2s.com/Code/Java/File-Input-Output/DeletefileusingJavaIOAPI.htm
	public void delete(String fileName) {
		// A File object to represent the filename
		File f = new File(fileName);

		// Make sure the file or directory exists and isn't write protected
		if (!f.exists())
			System.out.println("Delete: no such file or directory: " + fileName);

		if (!f.canRead())
			System.out.println("Delete: can't read: "+ fileName);
		
		if (!f.canWrite())
			System.out.println("Delete: write protected: "+ fileName);

		if (!f.canExecute())
			System.out.println("Delete: can't execute: "+ fileName);
		
		// If it is a directory, make sure it is empty
		if (f.isDirectory()) {
			String[] files = f.list();
			if (files.length > 0)
				System.out.println("Delete: directory not empty: " + fileName);
		}
		
        // Attempt to delete it
        boolean success = f.delete();
        if (success)
        	System.out.println(fileName + " deletion succeeded");
	    if (!success)
			System.out.println(fileName + " deletion failed");
	 
	}
	
	public void deleteFileIfExists(String filename) {
		File f = new File(filename);
		if (f.exists()) {
			System.out.println(filename);
			delete(filename);						
		}
	}
	
	// from http://www.javadb.com/how-to-rename-a-file-change-file-name
	public void renameFile(String file, String toFile) {

        File toBeRenamed = new File(file);

        if (!toBeRenamed.exists() || toBeRenamed.isDirectory()) {

            System.out.println("File does not exist: " + file);
            return;
        }

        File newFile = new File(toFile);

        //Rename
        if (toBeRenamed.renameTo(newFile)) {
            System.out.println("File has been renamed.");
        } else {
            System.out.println("Error renmaing file");
        }
    }
	
	public String displayReactionsColumnNameFromIndex(String databaseName, int columnIndex) {
		String columnName = "";
		if (columnIndex > GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES.length - 1) {
			ReactionsMetaColumnManager reactionsMetaColumnManager = new ReactionsMetaColumnManager();
			columnName = reactionsMetaColumnManager.getColumnName(databaseName, columnIndex - GraphicalInterfaceConstants.REACTIONS_DB_COLUMN_NAMES.length + 1);
		} else {
			columnName = GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES[columnIndex];
		}
		return columnName;
	}
	
	public String dbReactionsColumnNameFromIndex(String databaseName, int columnIndex) {
		String dbColumnName = "";
		if (columnIndex > GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES.length - 1) {
			dbColumnName = "meta_" + (columnIndex - GraphicalInterfaceConstants.REACTIONS_DB_COLUMN_NAMES.length + 1);
		} else {
			dbColumnName = GraphicalInterfaceConstants.REACTIONS_DB_COLUMN_NAMES[columnIndex];
		}
		return dbColumnName;
	}
	
	public String displayMetabolitesColumnNameFromIndex(String databaseName, int columnIndex) {
		String columnName = "";
		if (columnIndex > GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES.length - 1) {
			MetabolitesMetaColumnManager metabolitesMetaColumnManager = new MetabolitesMetaColumnManager();
			columnName = metabolitesMetaColumnManager.getColumnName(databaseName, columnIndex - GraphicalInterfaceConstants.METABOLITES_DB_COLUMN_NAMES.length + 1);
		} else {
			columnName = GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES[columnIndex];
		}
		return columnName;
	}
	
	public String dbMetabolitesColumnNameFromIndex(String databaseName, int columnIndex) {
		String dbColumnName = "";
		if (columnIndex > GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES.length - 1) {
			dbColumnName = "meta_" + (columnIndex - GraphicalInterfaceConstants.METABOLITES_DB_COLUMN_NAMES.length + 1);
		} else {
			dbColumnName = GraphicalInterfaceConstants.METABOLITES_DB_COLUMN_NAMES[columnIndex];
		}
		return dbColumnName;
	}
	
}
