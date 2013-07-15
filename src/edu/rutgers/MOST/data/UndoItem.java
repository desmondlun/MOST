package edu.rutgers.MOST.data;

public interface UndoItem {
	
	public String baseclass="UndoItem";
	public String createUndoDescription();
	public void undo();
	public void redo();
	public String toString();
	
}
