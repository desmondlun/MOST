package edu.rutgers.MOST.data;

import java.awt.Color;
import java.util.ArrayList;

public class SVGEdge {

	private ArrayList<String[]> endpoints;
	private Color stroke;
	private String strokeWidth;
	private ArrayList<String[]> triangle;
	
	public ArrayList<String[]> getEndpoints() {
		return endpoints;
	}
	public void setEndpoints(ArrayList<String[]> endpoints) {
		this.endpoints = endpoints;
	}
	public Color getStroke() {
		return stroke;
	}
	public void setStroke(Color stroke) {
		this.stroke = stroke;
	}
	public String getStrokeWidth() {
		return strokeWidth;
	}
	public void setStrokeWidth(String strokeWidth) {
		this.strokeWidth = strokeWidth;
	}
	public ArrayList<String[]> getTriangle() {
		return triangle;
	}
	public void setTriangle(ArrayList<String[]> triangle) {
		this.triangle = triangle;
	}
	
}
