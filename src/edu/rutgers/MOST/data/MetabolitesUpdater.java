package edu.rutgers.MOST.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import edu.rutgers.MOST.config.LocalConfig;
import edu.rutgers.MOST.presentation.GraphicalInterface;
import edu.rutgers.MOST.presentation.GraphicalInterfaceConstants;

public class MetabolitesUpdater {

	public void updateMetaboliteRows(ArrayList<Integer> rowList, ArrayList<Integer> metabIdList, String databaseName) {
		String queryString = "jdbc:sqlite:" + databaseName + ".db";
		
		try{
			Connection conn =
				DriverManager.getConnection(queryString);
			Statement stat = conn.createStatement();

			try {
				stat.executeUpdate("BEGIN TRANSACTION");

				for (int i = 0; i < rowList.size(); i++) {
					
					//if strings contain ' (single quote), it will not execute insert statement
					//this code escapes ' as '' - sqlite syntax for escaping '
					String metaboliteAbbreviation = (String) GraphicalInterface.metabolitesTable.getModel().getValueAt(rowList.get(i), GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN);
					if (metaboliteAbbreviation != null) {
						if (metaboliteAbbreviation.contains("'")) {
							metaboliteAbbreviation = metaboliteAbbreviation.replaceAll("'", "''");
						}
					} else {
						metaboliteAbbreviation = " ";
					}
					String metaboliteName = (String) GraphicalInterface.metabolitesTable.getModel().getValueAt(rowList.get(i), GraphicalInterfaceConstants.METABOLITE_NAME_COLUMN);
					if (metaboliteName != null) {
						if (metaboliteName.contains("'")) {
							metaboliteName = metaboliteName.replaceAll("'", "''");
						}
					} else {
						metaboliteName = " ";
					}
					String charge = (String) GraphicalInterface.metabolitesTable.getModel().getValueAt(rowList.get(i), GraphicalInterfaceConstants.CHARGE_COLUMN);
					if (charge == null) {
						charge = " ";
					}
					String compartment = (String) GraphicalInterface.metabolitesTable.getModel().getValueAt(rowList.get(i), GraphicalInterfaceConstants.COMPARTMENT_COLUMN);
					if (compartment != null) {
						if (compartment.contains("'")) {
							compartment = compartment.replaceAll("'", "''");
						}
					} else {
						compartment = " ";
					}
					String boundary = (String) GraphicalInterface.metabolitesTable.getModel().getValueAt(rowList.get(i), GraphicalInterfaceConstants.BOUNDARY_COLUMN);
					String meta1 = (String) GraphicalInterface.metabolitesTable.getModel().getValueAt(rowList.get(i), GraphicalInterfaceConstants.METABOLITE_META1_COLUMN);
					if (meta1 != null) {
						if (meta1.contains("'")) {
							meta1 = meta1.replaceAll("'", "''");
						}
					} else {
						meta1 = " ";
					}
					String meta2 = (String) GraphicalInterface.metabolitesTable.getModel().getValueAt(rowList.get(i), GraphicalInterfaceConstants.METABOLITE_META2_COLUMN);
					if (meta2 != null) {
						if (meta2.contains("'")) {
							meta2 = meta2.replaceAll("'", "''");
						}
					} else {
						meta2 = " ";
					}
					String meta3 = (String) GraphicalInterface.metabolitesTable.getModel().getValueAt(rowList.get(i), GraphicalInterfaceConstants.METABOLITE_META3_COLUMN);
					if (meta3 != null) {
						if (meta3.contains("'")) {
							meta3 = meta3.replaceAll("'", "''");
						}
					} else {
						meta3 = " ";
					}
					String meta4 = (String) GraphicalInterface.metabolitesTable.getModel().getValueAt(rowList.get(i), GraphicalInterfaceConstants.METABOLITE_META4_COLUMN);
					if (meta4 != null) {
						if (meta4.contains("'")) {
							meta4 = meta4.replaceAll("'", "''");
						}
					} else {
						meta4 = " ";
					}
					String meta5 = (String) GraphicalInterface.metabolitesTable.getModel().getValueAt(rowList.get(i), GraphicalInterfaceConstants.METABOLITE_META5_COLUMN);
					if (meta5 != null) {
						if (meta5.contains("'")) {
							meta5 = meta5.replaceAll("'", "''");
						}
					} else {
						meta5 = " ";
					}
					String meta6 = (String) GraphicalInterface.metabolitesTable.getModel().getValueAt(rowList.get(i), GraphicalInterfaceConstants.METABOLITE_META6_COLUMN);
					if (meta6 != null) {
						if (meta6.contains("'")) {
							meta6 = meta6.replaceAll("'", "''");
						}
					} else {
						meta6 = " ";
					}
					String meta7 = (String) GraphicalInterface.metabolitesTable.getModel().getValueAt(rowList.get(i), GraphicalInterfaceConstants.METABOLITE_META7_COLUMN);
					if (meta7 != null) {
						if (meta7.contains("'")) {
							meta7 = meta7.replaceAll("'", "''");
						}
					} else {
						meta7 = " ";
					}
					String meta8 = (String) GraphicalInterface.metabolitesTable.getModel().getValueAt(rowList.get(i), GraphicalInterfaceConstants.METABOLITE_META8_COLUMN);
					if (meta8 != null) {
						if (meta8.contains("'")) {
							meta8 = meta8.replaceAll("'", "''");
						}
					} else {
						meta8 = " ";
					}
					String meta9 = (String) GraphicalInterface.metabolitesTable.getModel().getValueAt(rowList.get(i), GraphicalInterfaceConstants.METABOLITE_META9_COLUMN);
					if (meta9 != null) {
						if (meta9.contains("'")) {
							meta9 = meta9.replaceAll("'", "''");
						}
					} else {
						meta9 = " ";
					}
					String meta10 = (String) GraphicalInterface.metabolitesTable.getModel().getValueAt(rowList.get(i), GraphicalInterfaceConstants.METABOLITE_META10_COLUMN);
					if (meta10 != null) {
						if (meta10.contains("'")) {
							meta10 = meta10.replaceAll("'", "''");
						}
					} else {
						meta10 = " ";
					}
					String meta11 = (String) GraphicalInterface.metabolitesTable.getModel().getValueAt(rowList.get(i), GraphicalInterfaceConstants.METABOLITE_META11_COLUMN);
					if (meta11 != null) {
						if (meta11.contains("'")) {
							meta11 = meta11.replaceAll("'", "''");
						}
					} else {
						meta11 = " ";
					}
					String meta12 = (String) GraphicalInterface.metabolitesTable.getModel().getValueAt(rowList.get(i), GraphicalInterfaceConstants.METABOLITE_META12_COLUMN);
					if (meta12 != null) {
						if (meta12.contains("'")) {
							meta12 = meta12.replaceAll("'", "''");
						}
					} else {
						meta12 = " ";
					}
					String meta13 = (String) GraphicalInterface.metabolitesTable.getModel().getValueAt(rowList.get(i), GraphicalInterfaceConstants.METABOLITE_META13_COLUMN);
					if (meta13 != null) {
						if (meta13.contains("'")) {
							meta13 = meta13.replaceAll("'", "''");
						}
					} else {
						meta13 = " ";
					}
					String meta14 = (String) GraphicalInterface.metabolitesTable.getModel().getValueAt(rowList.get(i), GraphicalInterfaceConstants.METABOLITE_META14_COLUMN);
					if (meta14 != null) {
						if (meta14.contains("'")) {
							meta14 = meta14.replaceAll("'", "''");
						}
					} else {
						meta14 = " ";
					}
					String meta15 = (String) GraphicalInterface.metabolitesTable.getModel().getValueAt(rowList.get(i), GraphicalInterfaceConstants.METABOLITE_META15_COLUMN);
					if (meta15 != null) {
						if (meta15.contains("'")) {
							meta15 = meta15.replaceAll("'", "''");
						}
					} else {
						meta15 = " ";
					}
					
					String update = "update metabolites set metabolite_abbreviation='" + metaboliteAbbreviation + "', metabolite_name='" + metaboliteName + "', charge='" + charge + "', " 
						+ " compartment='" + compartment + "', boundary='" + boundary + "', meta_1='" + meta1 + "', meta_2='" + meta2 + "', meta_3='" + meta3 + "', meta_4='" + meta4 + "', meta_5='" + meta5 + "', "
						+ " meta_6='" + meta6 + "', meta_7='" + meta7 + "', meta_8='" + meta8 + "', meta_9='" + meta9 + "', meta_10='" + meta10 + "', "
						+ " meta_11='" + meta11 + "', meta_12='" + meta12 + "', meta_13='" + meta13 + "', meta_14='" + meta14 + "', meta_15='" + meta15 + "' where id=" + metabIdList.get(i) + ";";
					stat.executeUpdate(update);
				}
				
				stat.executeUpdate("COMMIT");
			} catch (Exception e) {
				e.printStackTrace();
				stat.executeUpdate("ROLLBACK"); // throw away all updates since BEGIN TRANSACTION
			}

		}catch(SQLException e){

			e.printStackTrace();

		}

	}
	
	public void deleteRows(ArrayList<Integer> idList, String databaseName) {
		//TODO: need to check if unused
		String queryString = "jdbc:sqlite:" + databaseName + ".db";
		
		try{
			Connection conn =
				DriverManager.getConnection(queryString);
			Statement stat = conn.createStatement();

			try {
				stat.executeUpdate("BEGIN TRANSACTION");

				for (int i = 0; i < idList.size(); i++) {
					String delete = "delete from metabolites where id = " + idList.get(i) + ";";
					stat.executeUpdate(delete);
				}
				
				stat.executeUpdate("COMMIT");
			} catch (Exception e) {
				e.printStackTrace();
				stat.executeUpdate("ROLLBACK"); // throw away all updates since BEGIN TRANSACTION
			}

		}catch(SQLException e){

			e.printStackTrace();

		}
		
	}
	
	//unusedList is list of objects, need to cast to integer, hence need for different method
	public void deleteUnused(ArrayList<Integer> unusedList, String databaseName) {
		//TODO: need to check if unused
		String queryString = "jdbc:sqlite:" + databaseName + ".db";
		
		try{
			Connection conn =
				DriverManager.getConnection(queryString);
			Statement stat = conn.createStatement();

			try {
				stat.executeUpdate("BEGIN TRANSACTION");

				for (int i = 0; i < unusedList.size(); i++) {
					String delete = "delete from metabolites where id = " + unusedList.get(i) + ";";
					stat.executeUpdate(delete);
				}
				for (int j = 0; j < LocalConfig.getInstance().getBlankMetabIds().size(); j++) {
					String delete = "delete from metabolites where id = " + LocalConfig.getInstance().getBlankMetabIds().get(j) + ";";
					stat.executeUpdate(delete);
				}
				for (int k = 0; k < LocalConfig.getInstance().getDuplicateIds().size(); k++) {
					String delete = "delete from metabolites where id = " + LocalConfig.getInstance().getDuplicateIds().get(k) + ";";
					stat.executeUpdate(delete);
				}
				
				stat.executeUpdate("COMMIT");
			} catch (Exception e) {
				e.printStackTrace();
				stat.executeUpdate("ROLLBACK"); // throw away all updates since BEGIN TRANSACTION
			}

		}catch(SQLException e){

			e.printStackTrace();

		}
		
	}
	
}
