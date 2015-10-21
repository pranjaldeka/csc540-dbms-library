package com.ncsu.dbms.lib.login;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import com.ncsu.dbms.lib.connection.DBConnection;

public class Login {

	private DBConnection dbConn;
	public Login(DBConnection dbConn) {
		// TODO Auto-generated constructor stub
		this.dbConn = dbConn;
		validateLogin();
	}
	
	public void validateLogin(){
		
		System.out.println("Enter user id:");
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
		String username = scanner.nextLine();
		System.out.println("Enter password:");
		String password = scanner.nextLine();
		
		System.out.println("Username is " + username + " and password is " + password);
		
		String query = "Select count(1) from ssingh25.admin where admin_id = "+ username + " and password = " + password;
		
		ResultSet rs;
		try {
			rs = dbConn.executeQuery(query);
			while(rs.next()){
				if(rs.getInt(1)>0){
					System.out.println("User is valid");
				}
				else{
					System.out.println("User is invalid");
				}
			}
//			while(rs.next()) {
//				System.out.print(rs.getInt(1) + "\t");
//				System.out.println(rs.getString(2));
//			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		 
	
	}

}
