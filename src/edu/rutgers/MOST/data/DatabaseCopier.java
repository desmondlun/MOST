package edu.rutgers.MOST.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.*;

public class DatabaseCopier {

	public void copyDatabase(String oldDatabasename, String newDatabasename) {

		File sourceFile = new File(oldDatabasename + ".db");
		File destFile = new File(newDatabasename + ".db");
		try{
			copyFile(sourceFile, destFile);
		}
		catch(IOException exc){

			exc.printStackTrace();

		}
	}

	public void copyLogFile(String oldLog, String newLog) {

		File sourceFile = new File(oldLog + ".log");
		File destFile = new File(newLog + ".log");
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
}

