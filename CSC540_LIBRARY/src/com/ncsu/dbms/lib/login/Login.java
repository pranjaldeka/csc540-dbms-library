package com.ncsu.dbms.lib.login;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import com.ncsu.dbms.lib.connection.DBConnection;
import com.ncsu.dbms.lib.exception.InvalidCredentialException;

public class Login {

	private DBConnection dbConn;
	public Login(DBConnection dbConn) throws InvalidCredentialException{
		// TODO Auto-generated constructor stub
		this.dbConn = dbConn;
		try{
			validateLogin();
		}
		catch(Exception e)
		{
			throw e;
		}
	}
	
	public void validateLogin() throws InvalidCredentialException{
		
		System.out.println("Enter user id:");
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
		String username = scanner.nextLine();
		System.out.println("Enter password:");
		String password = scanner.nextLine();
		
		//System.out.println("Username is " + username + " and password is " + password);
		
		String query = "Select * from ssingh25.admin where admin_id = "+ username + " and password = " + password;
		
		ResultSet rs;
		try {
			rs = dbConn.executeQuery(query);
			if(rs.next()){
					System.out.println("Hello " + rs.getString(2) + " " + rs.getString(3) + " !!!");
			}
			else
				//System.out.println("Invalid credential!!!");
				throw new InvalidCredentialException ();
		} 
		catch(InvalidCredentialException e){
			throw e;
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		 
	
	}

}
