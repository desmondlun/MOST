package edu.rutgers.MOST.data;

import java.awt.Color;

public class BorderRectangle {
	
	private double x;
	private double y;
	private double width;
	private double height;
	private Color stroke;
	private String strokeWidth;
	private Color fill;
	
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
	public double getWidth() {
		return width;
	}
	public void setWidth(double width) {
		this.width = width;
	}
	public double getHeight() {
		return height;
	}
	public void setHeight(double height) {
		this.height = height;
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
	public Color getFill() {
		return fill;
	}
	public void setFill(Color fill) {
		this.fill = fill;
	}

}
