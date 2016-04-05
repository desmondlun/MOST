package edu.rutgers.MOST.data;

import java.awt.Color;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.rutgers.MOST.presentation.PathwaysFrameConstants;

public class SVGBuilder {
	
	private ArrayList<SVGEdge> edges;
	
	public ArrayList<SVGEdge> getEdges() {
		return edges;
	}

	public void setEdges(ArrayList<SVGEdge> edges) {
		this.edges = edges;
	}

	private ArrayList<BorderRectangle> rects;
	
	public ArrayList<BorderRectangle> getRects() {
		return rects;
	}

	public void setRects(ArrayList<BorderRectangle> rects) {
		this.rects = rects;
	}
	
	private ArrayList<SVGText> textList;

	public ArrayList<SVGText> getTextList() {
		return textList;
	}

	public void setTextList(ArrayList<SVGText> textList) {
		this.textList = textList;
	}

	private DocumentBuilder builder;
	
	public SVGBuilder()
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try
		{
			builder = factory.newDocumentBuilder();
		}
		catch (ParserConfigurationException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Creates an SVG document of the current drawing.
	 * @return the DOM tree of the SVG document
	 */
	public Document buildDocument()
	{
		Document doc = builder.newDocument();
		Element svgElement = doc.createElement("svg");
		doc.appendChild(svgElement);
		svgElement.setAttribute("version", "1.1");
		svgElement.setAttribute("width", "" + PathwaysFrameConstants.GRAPH_WIDTH);
		svgElement.setAttribute("height", "" + PathwaysFrameConstants.GRAPH_HEIGHT);
		svgElement.setAttribute("xmlns", "http://www.w3.org/2000/svg");
		
		for (int e = 0; e < edges.size(); e++) {
			Element lineElement = doc.createElement("line");
			lineElement.setAttribute("x1", "" + edges.get(e).getEndpoints().get(0)[0]);
			lineElement.setAttribute("y1", "" + edges.get(e).getEndpoints().get(0)[1]);
			lineElement.setAttribute("x2", "" + edges.get(e).getEndpoints().get(1)[0]);
			lineElement.setAttribute("y2", "" + edges.get(e).getEndpoints().get(1)[1]);
			lineElement.setAttribute("stroke", colorToString(edges.get(e).getStroke()));
			lineElement.setAttribute("stroke-width", edges.get(e).getStrokeWidth());
			svgElement.appendChild(lineElement);
		}
		for (int e = 0; e < edges.size(); e++) {
			if (edges.get(e).getTriangle() != null && edges.get(e).getTriangle().size() == 3) {
				Element polygonElement = doc.createElement("polygon");
				String points = edges.get(e).getTriangle().get(0)[0] + "," +
						edges.get(e).getTriangle().get(0)[1] + " " +
						edges.get(e).getTriangle().get(1)[0] + "," +
						edges.get(e).getTriangle().get(1)[1] + " " +
						edges.get(e).getTriangle().get(2)[0] + "," +
						edges.get(e).getTriangle().get(2)[1] + " ";
				polygonElement.setAttribute("points", points);
				String style = "fill:" + colorToString(edges.get(e).getStroke()) +
						";stroke:" + colorToString(edges.get(e).getStroke()) + ";stroke-width:1";
				polygonElement.setAttribute("style", style);
				svgElement.appendChild(polygonElement);
			}
		}
		for (int i = 0; i < rects.size(); i++)
		{
			BorderRectangle r = rects.get(i);
			Element rectElement = doc.createElement("rect");
			rectElement.setAttribute("x", "" + r.getX());
			rectElement.setAttribute("y", "" + r.getY());
			rectElement.setAttribute("width", "" + r.getWidth());
			rectElement.setAttribute("height", "" + r.getHeight());
			rectElement.setAttribute("stroke", colorToString(r.getStroke()));
			rectElement.setAttribute("stroke-width", r.getStrokeWidth());
			rectElement.setAttribute("fill", colorToString(r.getFill()));
			svgElement.appendChild(rectElement);
		}
		for (int t = 0; t < textList.size(); t++) {
			SVGText s = textList.get(t);
			Element textElement = doc.createElement("text");
			textElement.setAttribute("x", "" + s.getX());
			textElement.setAttribute("y", "" + s.getY());
			textElement.setAttribute("font-family", "" + s.getFont());
			textElement.setAttribute("font-size", "" + s.getFontSize() + "px");
			textElement.setAttribute("font-weight", "" + s.getFontWeight());
			textElement.setAttribute("fill", colorToString(s.getFill()));
			textElement.setTextContent(s.getText());
			svgElement.appendChild(textElement);
		}
		return doc;
	}

	/**
	 * Converts a color to a hex value.
	 * @param c a color
	 * @return a string of the form #rrggbb
	 */
	private static String colorToString(Color c)
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append(Integer.toHexString(c.getRGB() & 0xFFFFFF));
		while (buffer.length() < 6)
			buffer.insert(0, '0');
		buffer.insert(0, '#');
		return buffer.toString();
	}
	
}
