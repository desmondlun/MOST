package edu.rutgers.MOST.presentation;

import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;

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
	
	public void copyFileWithExtension(String oldFile, String newFile, String extension) {

		File sourceFile = new File(oldFile + ".dll");
		File destFile = new File(newFile + ".dll");
		try{
			copyFile(sourceFile, destFile);
		}
		catch(IOException exc){

			exc.printStackTrace();

		}
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
	
	public String lastPath(String path, JFileChooser fileChooser) {
		// based on http://stackoverflow.com/questions/1503555/how-to-find-my-documents-folder
		// works for Windows XP and Windows 7
		FileSystemView fsv = fileChooser.getFileSystemView();
		String defaultPath = fsv.getDefaultDirectory().getPath();
		// if username is preferable this works
		//String defaultPath = System.getenv("USERPROFILE") ;
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
	
	public String createLogFileName(String name) {
		String fileName = "";
		if (System.getProperty("os.name").equals("Windows 7") || System.getProperty("os.name").equals("Windows 8") || System.getProperty("os.name").equals("Windows Vista")) {
			File destDir = new File(GraphicalInterfaceConstants.SETTINGS_PATH_PREFIX_WINDOWS_7 + System.getProperty("user.name") + GraphicalInterfaceConstants.SETTINGS_PATH_SUFFIX_WINDOWS_7 + GraphicalInterfaceConstants.FOLDER_NAME);
			if (!destDir.exists()) {
				destDir.mkdir();				
			}
			fileName = GraphicalInterfaceConstants.SETTINGS_PATH_PREFIX_WINDOWS_7 + System.getProperty("user.name") + GraphicalInterfaceConstants.SETTINGS_PATH_SUFFIX_WINDOWS_7 + GraphicalInterfaceConstants.FOLDER_NAME + name;
		} else if (System.getProperty("os.name").equals("Windows XP")) {
			File destDir = new File(GraphicalInterfaceConstants.SETTINGS_PATH_PREFIX_WINDOWS_XP + System.getProperty("user.name") + GraphicalInterfaceConstants.SETTINGS_PATH_SUFFIX_WINDOWS_XP + GraphicalInterfaceConstants.FOLDER_NAME);
			if (!destDir.exists()) {
				destDir.mkdir();				
			}
			fileName = GraphicalInterfaceConstants.SETTINGS_PATH_PREFIX_WINDOWS_XP + System.getProperty("user.name") + GraphicalInterfaceConstants.SETTINGS_PATH_SUFFIX_WINDOWS_XP + GraphicalInterfaceConstants.FOLDER_NAME + name;
		} else {
			fileName = name;
		}
		
		return fileName;
		
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
	
}
