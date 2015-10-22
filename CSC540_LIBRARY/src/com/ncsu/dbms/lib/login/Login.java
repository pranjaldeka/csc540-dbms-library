package com.ncsu.dbms.lib.login;

import java.sql.CallableStatement;
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
	
	private void validateLogin() throws InvalidCredentialException{
		
		System.out.println("Enter user id:");
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
		String username = scanner.nextLine();
		System.out.println("Enter password:");
		String password = scanner.nextLine();
		
		//System.out.println("Username is " + username + " and password is " + password);
		try {
		validateUserLogin(username, password);
		
		
		/*String query = "Select * from ssingh25.admin where admin_id = "+ username + " and password = " + password;
		
		ResultSet rs;
		
			rs = dbConn.executeQuery(query);
			if(rs.next()){
					System.out.println("Hello " + rs.getString(2) + " " + rs.getString(3) + " !!!");
			}
			else
				//System.out.println("Invalid credential!!!");
				throw new InvalidCredentialException ();
				*/
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

	private void validateUserLogin(String username, String password) throws InvalidCredentialException, SQLException{
		CallableStatement callStmt = null;
		String validateCall = "{call user_profile_pkg.validate_user_proc(?,?,?,?,?)}";
		try{
			callStmt = dbConn.con.prepareCall(validateCall);
			callStmt.setString(1, username);
			callStmt.setString(2, password);
			callStmt.registerOutParameter(3, java.sql.Types.VARCHAR);
			callStmt.registerOutParameter(4, java.sql.Types.VARCHAR);
			callStmt.registerOutParameter(5, java.sql.Types.VARCHAR);
			callStmt.executeQuery();
			int flag = callStmt.getInt(3);
			if(flag==0)
				throw new InvalidCredentialException();
			else
				System.out.println("Hello " + callStmt.getString(4) + " " + callStmt.getString(5) + " !!!");
			
		}
		catch (InvalidCredentialException e) {

			throw e;
		}
	 catch (SQLException e) {

		System.out.println(e.getMessage());

	}
		finally {

		if (callStmt != null) {
			callStmt.close();
		}
		}	
	}
}
