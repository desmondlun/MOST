package edu.rutgers.MOST.presentation;

import org.apache.commons.dbutils.*;

import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.Vector;

//Based on code from:
//http://www.javaspecialists.eu/archive/Issue118.html
public class ReactionsDatabaseTableModel extends DefaultTableModel {	
	@Override 
	public boolean isCellEditable(int row, int col) {
		if (col == GraphicalInterfaceConstants.DB_REACTIONS_ID_COLUMN || col == GraphicalInterfaceConstants.REVERSIBLE_COLUMN) {
			return false; 
		} else if (GraphicalInterface.fileList.getSelectedIndex() > 0) {
			return false;	
		} else {
			return true; 
		} 
	} 	
	private final QueryRunner queryRunner = new QueryRunner();
	public ReactionsDatabaseTableModel(Connection con, String query)
	throws SQLException {
		// might need to delimit table names
		String sql = query;
		queryRunner.query(con, sql, new ResultSetHandler() {
			public Object handle(ResultSet rs) throws SQLException {
				// extract the column names
				int numColumns = rs.getMetaData().getColumnCount();
				Vector column = new Vector();
				for (int i = 1; i <= numColumns; i++) {
					column.add(rs.getMetaData().getColumnName(i));
				}
				// extract the data
				Vector data = new Vector();
				while (rs.next()) {
					Vector row = new Vector();
					for (int i = 1; i <= numColumns; i++) {
						row.add(rs.getString(i));
					}
					data.add(row);
				}
				setDataVector(data, column);
				return null;
			}
		});

	}
}

