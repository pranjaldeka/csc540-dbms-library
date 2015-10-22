package com.ncsu.dbms.lib.connection;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {

	public Connection con = null;
	public DBConnection() {
		// TODO Auto-generated constructor stub
		initialize();
	}
	public void initialize(){

		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con =  DriverManager.getConnection(
					"jdbc:oracle:thin:ssingh25/@ora.csc.ncsu.edu:1521:orcl");

			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				//rs.close();
				//stmt.close();
				//con.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	public ResultSet executeQuery( String query) throws SQLException{
		Statement stmt = null;
		ResultSet rs = null;
		stmt = ((java.sql.Connection) con).createStatement();
		rs = stmt.executeQuery(query);
		
		return rs;
		
	}

}
