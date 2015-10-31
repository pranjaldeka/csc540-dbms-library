package com.ncsu.dbms.lib.connection;


import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.ncsu.dbms.lib.utilities.Constant;
import com.ncsu.dbms.lib.utilities.Utility;

import oracle.jdbc.OracleTypes;

public class DBConnection {

	public static Connection con = null;
	public DBConnection() {
		// TODO Auto-generated constructor stub
		initialize();
	}
	public void initialize(){

		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con =  DriverManager.getConnection(
					"jdbc:oracle:thin:ssingh25/200103842@ora.csc.ncsu.edu:1521:orcl");

			
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
	public static ResultSet executeQuery( String query) throws SQLException{
		Statement stmt = null;
		ResultSet rs = null;
		stmt = ((java.sql.Connection) con).createStatement();
		rs = stmt.executeQuery(query);
		
		return rs;
		
	}
	public static CallableStatement returnCallableStatememt(String query) throws SQLException{
    	CallableStatement cstmt = DBConnection.con.prepareCall(query);
    	return cstmt;

	}
	public static ArrayList<Object> returnResultSetAndError(CallableStatement cstmt, int resultSetIndex, int errorIndex) throws SQLException{
      	 ResultSet rs; 
      	 ArrayList<Object> arrayList = new ArrayList<>();
      	cstmt.executeQuery();
      	rs = (ResultSet) cstmt.getObject(resultSetIndex);
      	String error = cstmt.getString(errorIndex);
      	if(rs!=null)
      		arrayList.add(rs);
      	if(error!= null)
      		arrayList.add(error);
      	else
      		arrayList.add(Constant.kBlankString);
      	return arrayList;
	}
	public static String returnMessage(CallableStatement cstmt, int errorIndex) throws SQLException{
     	cstmt.executeQuery();
     	String error = cstmt.getString(errorIndex);
     	if(error!= null)
     		return error;
     	else
     		return Constant.kBlankString;
	}

}
