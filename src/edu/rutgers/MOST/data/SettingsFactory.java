package edu.rutgers.MOST.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndDocument;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class SettingsFactory {
	public Map<String, String> mappings;
	public String filename;
	
	public SettingsFactory(String filename) throws Exception {
		mappings = new HashMap<String, String>();
		this.filename = filename;
		this.read();
	}
	
	public SettingsFactory() throws Exception {
		mappings = new HashMap<String, String>();
		String fileName = "";
		if (System.getProperty("os.name").equals("Windows 7") || System.getProperty("os.name").equals("Windows 8") || System.getProperty("os.name").equals("Windows Vista")) {
			File destDir = new File(SettingsConstants.SETTINGS_PATH_PREFIX_WINDOWS_7 + System.getProperty("user.name") + SettingsConstants.SETTINGS_PATH_SUFFIX_WINDOWS_7 + SettingsConstants.FOLDER_NAME);
			if (!destDir.exists()) {
				destDir.mkdir();				
			}
			fileName = SettingsConstants.SETTINGS_PATH_PREFIX_WINDOWS_7 + System.getProperty("user.name") + SettingsConstants.SETTINGS_PATH_SUFFIX_WINDOWS_7 + SettingsConstants.FOLDER_NAME + "settings.xml";
		} else if (System.getProperty("os.name").equals("Windows XP")) {
			File destDir = new File(SettingsConstants.SETTINGS_PATH_PREFIX_WINDOWS_XP + System.getProperty("user.name") + SettingsConstants.SETTINGS_PATH_SUFFIX_WINDOWS_XP + SettingsConstants.FOLDER_NAME);
			if (!destDir.exists()) {
				destDir.mkdir();				
			}
			fileName = SettingsConstants.SETTINGS_PATH_PREFIX_WINDOWS_XP + System.getProperty("user.name") + SettingsConstants.SETTINGS_PATH_SUFFIX_WINDOWS_XP + SettingsConstants.FOLDER_NAME + "settings.xml";
		} else {
			fileName = "settings.xml";
		}
		this.filename = fileName;
		//this.filename = "settings.xml";
		//System.out.println(this.filename);
		this.read();
	}
	
	public void add(String key, String value) {
		mappings.put(key,value);
		try {
			this.writeMethod1();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String get(String key) {
		String curValue = null;
		if (mappings.containsKey(key)) {
			curValue = mappings.get(key);
		}
		return curValue;
	}
	public void add(Setting aSetting) {
		String key = aSetting.getKey();
		String value = aSetting.getValue();
		this.add(key,value);	
		
	}
	
	public boolean exists(String dir) {
		File file=new File(dir);
		return file.exists();
	}
	
	public void writeMethod1() throws Exception {
		XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();

		XMLEventWriter writer = outputFactory.createXMLEventWriter(new FileOutputStream(this.filename));
	    
	    
	    XMLEventFactory xmlEventFactory = XMLEventFactory.newInstance();
	    
	    XMLEvent end = xmlEventFactory.createDTD("\n");

	    StartDocument startDocument = xmlEventFactory.createStartDocument("UTF-8", "1.0");
	    writer.add(startDocument);
	    writer.add(end);

	    StartElement startElement = xmlEventFactory.createStartElement("", "", "Settings");
	    writer.add(startElement);

	    Attribute attribute = xmlEventFactory.createAttribute("version", "1");
	    List attributeList = Arrays.asList(attribute);
	    List nsList = Arrays.asList();
	    
	    StartElement startElement2 = xmlEventFactory.createStartElement("", "", "Attributes",
	        attributeList.iterator(), nsList.iterator());
	    
	 
	    writer.add(startElement2);
	    
	    Set<String> keys = mappings.keySet();
	    String value;
	    for (String key : keys) {
	    	value = mappings.get(key);
	    	this.addAttribute(writer, xmlEventFactory, key, value);
	    }
	    
	    
	    EndDocument ed = xmlEventFactory.createEndDocument();
	    writer.add(ed);

	    writer.flush();
	    writer.close();

	    /*StartElement codeSE = xmlEventFactory.createStartElement("", "", "LastLoadedSBML");
	    writer.add(codeSE);
	    
	    
	    Characters codeChars = xmlEventFactory.createCharacters(lastL_SBML);
	    writer.add(codeChars);
	    EndElement codeEE = xmlEventFactory.createEndElement("", "", "LastLoadedSBML");
	    writer.add(codeEE);
		*/
	    


	    
	}
	
	public void addAttribute(XMLEventWriter writer, XMLEventFactory xmlEventFactory, 
			String key, String value) {
		try {
			StartElement codeSE = xmlEventFactory.createStartElement("", "", key);
		    writer.add(codeSE);
		    
		    
		    Characters codeChars = xmlEventFactory.createCharacters(value);
		    writer.add(codeChars);
		    EndElement codeEE = xmlEventFactory.createEndElement("", "", key);
	    
			writer.add(codeEE);
			
			mappings.put(key, value);
			
			
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void read() throws Exception {
		XMLInputFactory factory = XMLInputFactory.newInstance();
		try {
			FileReader fileReader = new FileReader(this.filename);
			XMLEventReader reader = factory.createXMLEventReader(fileReader);
			String currentElementValue = "";

			while (reader.hasNext()) {
				XMLEvent event = reader.nextEvent();
				if (event.isStartElement()) {
					StartElement element = (StartElement) event;
					currentElementValue = element.getName().toString();


					//System.out.println("Start Element: " + element.getName());

					Iterator iterator = element.getAttributes();
					while (iterator.hasNext()) {
						Attribute attribute = (Attribute) iterator.next();
						QName name = attribute.getName();
						String value = attribute.getValue();
						//System.out.println("Attribute name/value: " + name + "/" + value);
					}
				}

				if (event.isEndElement()) {
					EndElement element = (EndElement) event;
					//System.out.println("End element:" + element.getName());
				}

				if (event.isCharacters()) {
					Characters characters = (Characters) event;
					String curAddr = characters.getData();
					if (this.exists(curAddr)) {
						mappings.put(currentElementValue, curAddr);
					}
					currentElementValue = "";

					//System.out.println("Text: " + characters.getData());
				}
			}
		} catch (FileNotFoundException e ) {
			return;
		}	
	}
	
	public String toString() {
		return mappings.toString();
	}
	
}
