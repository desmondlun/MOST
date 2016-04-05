package edu.rutgers.MOST.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import edu.rutgers.MOST.presentation.ResizableDialog;

/**
 * based on http://www.java2s.com/Code/Java/XML/HowtowriteanXMLfileItsavesafiledescribingamoderndrawinginSVGformat.htm
 */
public class SVGWriter
{
	private ResizableDialog dialog = new ResizableDialog( "Error",
			"Error", "Error" );
	// test data - will be replaced by data from visualization
	//private SVGBuilder builder = new SVGBuilder();
	private SVGBuilder builder;
	
	public SVGBuilder getBuilder() {
		return builder;
	}

	public void setBuilder(SVGBuilder builder) {
		this.builder = builder;
	}

	/**
	 * Saves the drawing in SVG format, using DOM/XSLT
	 */
	public void saveDocument(File f) throws TransformerException, IOException
	{
		Document doc = builder.buildDocument();
		FileOutputStream fos = new FileOutputStream(f);
		Transformer t = TransformerFactory.newInstance().newTransformer();
		t.setOutputProperty(OutputKeys.INDENT, "yes");
		t.setOutputProperty(OutputKeys.METHOD, "xml");
		t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		t.transform(new DOMSource(doc), new StreamResult(fos));
//		t.transform(new DOMSource(doc), new StreamResult(new FileOutputStream(f)));
		fos.flush();
		fos.close();
	}
	
	public void saveFile() {
		JTextArea output = null;
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(new SVGFileFilter());
		boolean done = false;
		while (!done) {
			int retval = chooser.showSaveDialog(output);
			if (retval == JFileChooser.CANCEL_OPTION) {
				done = true;
			}
			if (retval == JFileChooser.APPROVE_OPTION) {
				File file = chooser.getSelectedFile();
				String path = file.getPath();
				if (!file.getPath().endsWith(".svg")) {
					path = path + ".svg";
					file = new File(path);
				}
				if (file.exists()) {
					int confirmDialog = JOptionPane.showConfirmDialog(chooser, "Replace existing file?");
					if (confirmDialog == JOptionPane.YES_OPTION) {
						done = true;

						try {
							saveDocument(file);
						} catch (TransformerException e) {
							processStackTrace(e);
							// TODO Auto-generated catch block
							//e.printStackTrace();
						} catch (IOException e) {
							processStackTrace(e);
							// TODO Auto-generated catch block
							//e.printStackTrace();
						}

					} else if (confirmDialog == JOptionPane.NO_OPTION) {        		    	  
						done = false;
					} else {
						done = true;
					}       		    	  
				} else {
					done = true;
					
					try {
						saveDocument(file);
					} catch (TransformerException e) {
						processStackTrace(e);
						// TODO Auto-generated catch block
						//e.printStackTrace();
					} catch (IOException e) {
						processStackTrace(e);
						// TODO Auto-generated catch block
						//e.printStackTrace();
					}
				}	
			}
		}
	}
	
	private void processStackTrace( Exception e ) {
		//e.printStackTrace();
		StringWriter errors = new StringWriter();
		e.printStackTrace( new PrintWriter( errors ) );
		dialog.setErrorMessage( errors.toString() );
		// centers dialog
		dialog.setLocationRelativeTo(null);
		dialog.setModal(true);
		dialog.setVisible( true );
	}
	
	public static void main(String[] args)
	{
		SVGWriter writer = new SVGWriter();
		writer.saveFile();
	}
}

class SVGFileFilter extends javax.swing.filechooser.FileFilter {
	public boolean accept(File f) {
		return f.isDirectory() || f.getName().toLowerCase().endsWith(".svg");
	}

	public String getDescription() {
		return ".svg files";
	}
}



