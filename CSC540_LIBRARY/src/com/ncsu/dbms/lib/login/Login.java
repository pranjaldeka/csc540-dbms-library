package com.ncsu.dbms.lib.login;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.Scanner;

import com.ncsu.dbms.lib.connection.DBConnection;
import com.ncsu.dbms.lib.exception.InvalidCredentialException;
import com.ncsu.dbms.lib.users.Admin;
import com.ncsu.dbms.lib.users.Faculty;
import com.ncsu.dbms.lib.users.Student;

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

	private void validateUserLogin(String userName, String password) throws InvalidCredentialException, SQLException{
		CallableStatement callStmt = null;
		String validateCall = "{call user_profile_pkg.validate_user_proc(?,?,?,?,?)}";
		try{
			callStmt = dbConn.con.prepareCall(validateCall);
			callStmt.setString(1, userName);
			callStmt.setString(2, password);
			callStmt.registerOutParameter(3, java.sql.Types.VARCHAR);
			callStmt.registerOutParameter(4, java.sql.Types.VARCHAR);
			callStmt.registerOutParameter(5, java.sql.Types.VARCHAR);
			callStmt.executeQuery();
			String user_type = callStmt.getString(3);
			String firstName = callStmt.getString(4);
			String lastName = callStmt.getString(5);
			if(user_type.equals(""))
				throw new InvalidCredentialException();
			else if(user_type.equals("A")){ // call admin welcome window
				adminWelcomeMenu(userName, firstName, lastName);
			}
			else if(user_type.equals("S")){ // call Student welcome window
				studentWelcomeMenu(user_type, firstName, lastName);
			}
			else if(user_type.equals("F")) {// call faculty welcome window
				System.out.println("Hello " + callStmt.getString(4) + " " + callStmt.getString(5) + " !!!");
				facultyWelcomeMenu(userName,firstName,lastName);
			}

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
	
	// Admin welcome  Screen Menu Items
	private void adminWelcomeMenu(String userName, String firstName, String lastName){
		System.out.println("Hello Admin...");
		@SuppressWarnings("unused")
		Admin admin = new Admin(userName,firstName, lastName);
	}
	// Student welcome  Screen Menu Items

	private void studentWelcomeMenu(String userName, String firstName, String lastName){
		@SuppressWarnings("unused")
		Student student = new Student(userName,firstName, lastName);

	}
	// Faculty welcome  Screen Menu Items

	private void facultyWelcomeMenu(String userName, String firstName, String lastName){
		@SuppressWarnings("unused")
		Faculty faculty = new Faculty(userName,firstName, lastName);

	}
}
