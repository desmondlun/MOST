package edu.rutgers.MOST.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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
			
			PreparedStatement metabUpdatePrep = conn.prepareStatement("update metabolites set metabolite_abbreviation=?, metabolite_name=?, charge=?, " 
			+ " compartment=?, boundary=?, meta_1=?, meta_2=?, meta_3=?, meta_4=?, meta_5=?, meta_6=?, meta_7=?, meta_8=?, "
			+ " meta_9=?, meta_10=?, meta_11=?, meta_12=?, meta_13=?, meta_14=?, meta_15=? where id=?"); 
			try {
				stat.executeUpdate("BEGIN TRANSACTION");

				for (int i = 0; i < rowList.size(); i++) {
					
					//if strings contain ' (single quote), it will not execute insert statement
					//this code escapes ' as '' - sqlite syntax for escaping '
					String metaboliteAbbreviation = (String) GraphicalInterface.metabolitesTable.getModel().getValueAt(rowList.get(i), GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN);
					if (metaboliteAbbreviation == null) {
						metaboliteAbbreviation = " ";
					}
					String metaboliteName = (String) GraphicalInterface.metabolitesTable.getModel().getValueAt(rowList.get(i), GraphicalInterfaceConstants.METABOLITE_NAME_COLUMN);
					if (metaboliteName == null) {
						metaboliteName = " ";
					}
					String charge = (String) GraphicalInterface.metabolitesTable.getModel().getValueAt(rowList.get(i), GraphicalInterfaceConstants.CHARGE_COLUMN);
					if (charge == null) {
						charge = " ";
					}
					String compartment = (String) GraphicalInterface.metabolitesTable.getModel().getValueAt(rowList.get(i), GraphicalInterfaceConstants.COMPARTMENT_COLUMN);
					if (compartment == null) {
						compartment = " ";
					}
					String boundary = (String) GraphicalInterface.metabolitesTable.getModel().getValueAt(rowList.get(i), GraphicalInterfaceConstants.BOUNDARY_COLUMN);
					String meta1 = (String) GraphicalInterface.metabolitesTable.getModel().getValueAt(rowList.get(i), GraphicalInterfaceConstants.METABOLITE_META1_COLUMN);
					if (meta1 == null) {
						meta1 = " ";
					}
					String meta2 = (String) GraphicalInterface.metabolitesTable.getModel().getValueAt(rowList.get(i), GraphicalInterfaceConstants.METABOLITE_META2_COLUMN);
					if (meta2 == null) {
						meta2 = " ";
					}
					String meta3 = (String) GraphicalInterface.metabolitesTable.getModel().getValueAt(rowList.get(i), GraphicalInterfaceConstants.METABOLITE_META3_COLUMN);
					if (meta3 == null) {
						meta3 = " ";
					}
					String meta4 = (String) GraphicalInterface.metabolitesTable.getModel().getValueAt(rowList.get(i), GraphicalInterfaceConstants.METABOLITE_META4_COLUMN);
					if (meta4 == null) {
						meta4 = " ";
					}
					String meta5 = (String) GraphicalInterface.metabolitesTable.getModel().getValueAt(rowList.get(i), GraphicalInterfaceConstants.METABOLITE_META5_COLUMN);
					if (meta5 == null) {
						meta5 = " ";
					}
					String meta6 = (String) GraphicalInterface.metabolitesTable.getModel().getValueAt(rowList.get(i), GraphicalInterfaceConstants.METABOLITE_META6_COLUMN);
					if (meta6 == null) {
						meta6 = " ";
					}
					String meta7 = (String) GraphicalInterface.metabolitesTable.getModel().getValueAt(rowList.get(i), GraphicalInterfaceConstants.METABOLITE_META7_COLUMN);
					if (meta7 == null) {
						meta7 = " ";
					}
					String meta8 = (String) GraphicalInterface.metabolitesTable.getModel().getValueAt(rowList.get(i), GraphicalInterfaceConstants.METABOLITE_META8_COLUMN);
					if (meta8 == null) {
						meta8 = " ";
					}
					String meta9 = (String) GraphicalInterface.metabolitesTable.getModel().getValueAt(rowList.get(i), GraphicalInterfaceConstants.METABOLITE_META9_COLUMN);
					if (meta9 == null) {
						meta9 = " ";
					}
					String meta10 = (String) GraphicalInterface.metabolitesTable.getModel().getValueAt(rowList.get(i), GraphicalInterfaceConstants.METABOLITE_META10_COLUMN);
					if (meta10 == null) {
						meta10 = " ";
					}
					String meta11 = (String) GraphicalInterface.metabolitesTable.getModel().getValueAt(rowList.get(i), GraphicalInterfaceConstants.METABOLITE_META11_COLUMN);
					if (meta11 == null) {
						meta11 = " ";
					}
					String meta12 = (String) GraphicalInterface.metabolitesTable.getModel().getValueAt(rowList.get(i), GraphicalInterfaceConstants.METABOLITE_META12_COLUMN);
					if (meta12 == null) {
						meta12 = " ";
					}
					String meta13 = (String) GraphicalInterface.metabolitesTable.getModel().getValueAt(rowList.get(i), GraphicalInterfaceConstants.METABOLITE_META13_COLUMN);
					if (meta13 == null) {
						meta13 = " ";
					}
					String meta14 = (String) GraphicalInterface.metabolitesTable.getModel().getValueAt(rowList.get(i), GraphicalInterfaceConstants.METABOLITE_META14_COLUMN);
					if (meta14 == null) {
						meta14 = " ";
					}
					String meta15 = (String) GraphicalInterface.metabolitesTable.getModel().getValueAt(rowList.get(i), GraphicalInterfaceConstants.METABOLITE_META15_COLUMN);
					if (meta15 == null) {
						meta15 = " ";
					}
					
					metabUpdatePrep.setString(1, metaboliteAbbreviation);
					metabUpdatePrep.setString(2, metaboliteName);
					metabUpdatePrep.setString(3, charge);
					metabUpdatePrep.setString(4, compartment);
					metabUpdatePrep.setString(5, boundary);
					metabUpdatePrep.setString(6, meta1);
					metabUpdatePrep.setString(7, meta2);
					metabUpdatePrep.setString(8, meta3);
					metabUpdatePrep.setString(9, meta4);
					metabUpdatePrep.setString(10, meta5);
					metabUpdatePrep.setString(11, meta6);
					metabUpdatePrep.setString(12, meta7);
					metabUpdatePrep.setString(13, meta8);
					metabUpdatePrep.setString(14, meta9);
					metabUpdatePrep.setString(15, meta10);
					metabUpdatePrep.setString(16, meta11);
					metabUpdatePrep.setString(17, meta12);
					metabUpdatePrep.setString(18, meta13);
					metabUpdatePrep.setString(19, meta14);
					metabUpdatePrep.setString(20, meta15);
					metabUpdatePrep.setInt(21, metabIdList.get(i));
					
					metabUpdatePrep.executeUpdate();
					
				}
				
				stat.executeUpdate("COMMIT");
			} catch (Exception e) {
				e.printStackTrace();
				stat.executeUpdate("ROLLBACK"); // throw away all updates since BEGIN TRANSACTION
			}

			conn.close();
			
		}catch(SQLException e){

			e.printStackTrace();

		}

	}
	
	public void deleteRows(ArrayList<Integer> idList, String databaseName) {
		
		String queryString = "jdbc:sqlite:" + databaseName + ".db";
		
		try{
			Connection conn =
				DriverManager.getConnection(queryString);
			Statement stat = conn.createStatement();

			try {
				stat.executeUpdate("BEGIN TRANSACTION");

				for (int i = 0; i < idList.size(); i++) {
					if (LocalConfig.getInstance().getBlankMetabIds().contains(idList.get(i))) {
						LocalConfig.getInstance().getBlankMetabIds().remove(idList.get(i));
					}
					if (LocalConfig.getInstance().getDuplicateIds().contains(idList.get(i))) {
						LocalConfig.getInstance().getDuplicateIds().remove(idList.get(i));
					}
					if (LocalConfig.getInstance().getSuspiciousMetabolites().contains(idList.get(i))) {
						LocalConfig.getInstance().getSuspiciousMetabolites().remove(idList.get(i));
					}
					String delete = "delete from metabolites where id = " + idList.get(i) + ";";
					stat.executeUpdate(delete);
				}
				
				stat.executeUpdate("COMMIT");
			} catch (Exception e) {
				e.printStackTrace();
				stat.executeUpdate("ROLLBACK"); // throw away all updates since BEGIN TRANSACTION
			}
			
			conn.close();

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

			conn.close();
			
		}catch(SQLException e){

			e.printStackTrace();

		}
		
	}
	
}
