package edu.rutgers.MOST.presentation;

import javax.swing.*;
import java.sql.*;
import java.util.*;

//Based on code from:
//http://www.javaspecialists.eu/archive/Issue118.html
public class TableNameListModel extends AbstractListModel {
  private final List listData = new ArrayList();
  public TableNameListModel(Connection con) throws SQLException {
    ResultSet rs = con.getMetaData().getTables(null,null,null,null);
    // you might need a filter here if your database mixes system
    // tables with user tables, e.g. Microsoft SQL Server
    while (rs.next()) {	
      listData.add(rs.getString("table_name"));
    }
    rs.close();
  }
  public int getSize() { return listData.size(); }
  public Object getElementAt(int i) { return listData.get(i); }
}