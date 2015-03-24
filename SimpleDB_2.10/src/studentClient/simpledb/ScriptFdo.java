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
			String[] studvals = {"(1, 'joe')",
								 "(2, 'amy', 20, 2004)",
								 "(3, 'max', 10, 2005)",
								 "(4, 'sue', 20, 2005)",
								 "(5, 'bob', 30, 2003)",
								 "(6, 'kim', 20, 2001)",
								 "(7, 'art', 30, 2004)",
								 "(8, 'pat', 20, 2001)",
								 "(9, 'lee', 10, 2004)"};
			
			
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
