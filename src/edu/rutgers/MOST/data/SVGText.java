package edu.rutgers.MOST.data;

import java.awt.Color;

public class SVGText {
	
	private double x;
	private double y;
	private String font;
	private String fontSize;
	private String fontWeight;
	private Color fill;
	private String text;
	
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
	public String getFont() {
		return font;
	}
	public void setFont(String font) {
		this.font = font;
	}
	public String getFontSize() {
		return fontSize;
	}
	public void setFontSize(String fontSize) {
		this.fontSize = fontSize;
	}
	
	public String getFontWeight() {
		return fontWeight;
	}
	public void setFontWeight(String fontWeight) {
		this.fontWeight = fontWeight;
	}
	public Color getFill() {
		return fill;
	}
	public void setFill(Color fill) {
		this.fill = fill;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	@Override
	public String toString() {
		return "SVGText [x=" + x
		+ ", y=" + y
		+ ", font=" + font
		+ ", fontSize=" + fontSize
		+ ", fontWeight=" + fontWeight
		+ ", fill=" + fill.toString()
		+ ", text=" + text + "]";
	}

}
