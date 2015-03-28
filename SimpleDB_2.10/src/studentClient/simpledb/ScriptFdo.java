package studentClient.simpledb;

import java.sql.*;

import simpledb.remote.SimpleDriver;

public class ScriptFdo {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Connection conn = null;
		try {
			Driver d = new SimpleDriver();
			conn = d.connect("jdbc:simpledb://localhost", null);
			Statement stmt = conn.createStatement();

			String s = "create table FERNANDOS(id int, phrase varchar(24))";
			stmt.executeUpdate(s);
			System.out.println("Table FERNANDOS created.");

			s = "insert into FERNANDOS(id, phrase) values ";
			
			
			
			for (int i=0; i<500; i++){
				String soyfe="("+i+", 'soy fernando numero "+i+"')";
				stmt.executeUpdate(s + soyfe);
			}
			System.out.println("STUDENT records inserted.");

			
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (conn != null)
					conn.close();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	}

}
