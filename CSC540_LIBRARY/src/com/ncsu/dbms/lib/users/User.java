package com.ncsu.dbms.lib.users;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.Scanner;

import oracle.jdbc.OracleTypes;

import com.ncsu.dbms.lib.connection.DBConnection;
import com.ncsu.dbms.lib.exception.PrintSQLException;


public abstract class User {
	
	public  String userName;
	
	public String userType;
	
	public User(String username) {
		this.userName = username;
	}
	public User(String userName, String firstName, String lastName) {
		this.userName = userName;
		System.out.println("*******************Welcome*****************\n");
		System.out.println("\t\t" + firstName + " " + lastName + "!!!");
		System.out.println("\n*******************************************");
	}
	
	public  void showMenuItems() {
		System.out.println("\nPlease select from the below options: ");
		System.out.println("\n1. Profile \t\t 2. Resources");
		System.out.println("3. Checked-Out Resources \t\t 4. Notification");
		System.out.println("5. Due-Balance\t\t  6. Logout");
		selectAnAction();
	}
	
	protected abstract void selectAnAction();
	
	protected abstract void showProfile();
	
	protected void showMenuForModifyProfile(){
		boolean flag = true;
		try{
			System.out.println("\nPlease enter your choice:");
			System.out.println("1: Modify Profile.\t0:Go back to previous menu.");
			while(flag){
					@SuppressWarnings("resource")
					Scanner scanner = new Scanner(System.in);
					String value = scanner.nextLine();
					int choice = Integer.parseInt(value);
					switch(choice){
					case 1:
						// Call Modify profile method method
						modifyProfileDataMenu();
						flag = false;
							break;
					case 0:
						System.out.println("Going back to previous menu");
						showMenuItems();
						flag = false;
						break;
					default:
						System.out.println("Invalid choice: Please enter again.");
							
					}
				}
		}
		catch(Exception e){
			System.out.println("Something bad happened!!! Please try again...");
			showMenuItems();
		}
	}

	
	protected abstract void modifyProfileDataMenu();

	protected void updateProfileData(String columnName, String newColumnValue){
		  try {
	        	CallableStatement cstmt = DBConnection.con.prepareCall("{call user_profile_pkg.update_user_profile_proc(?, ?, ?, ?, ?)}");
	        	cstmt.setString(1, userType);
	        	cstmt.setString(2, userName);
	        	cstmt.setString(3, columnName);
	        	cstmt.setString(4, newColumnValue);
	        	cstmt.registerOutParameter(5, OracleTypes.VARCHAR);
	        	cstmt.executeQuery();
	        	String error = cstmt.getString(5);
	        	if(error != null)
	        	{
	        		System.out.println(error);
	        	}else
	        		System.out.println("\nUpdate Successful!!!\n");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				PrintSQLException.printSQLException(e);
				}
	        showProfile();

	}
}
