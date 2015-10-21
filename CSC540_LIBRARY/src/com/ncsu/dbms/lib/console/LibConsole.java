package com.ncsu.dbms.lib.console;

import java.util.Scanner;

import com.ncsu.dbms.lib.connection.DBConnection;
import com.ncsu.dbms.lib.login.Login;

public class LibConsole {

	private DBConnection dbConn;
	public LibConsole(DBConnection dbConn) {
		this.dbConn = dbConn;
		// TODO Auto-generated constructor stub
	}


	public void start() {
		// TODO Auto-generated method stub
		
		System.out.println("*************************************************************************************\n\n\n");
		System.out.println("\t\t\tWelcome to NC State Library.\n\n\n");
		System.out.println("*************************************************************************************");
		loginScreen();
		
	}
	public void loginScreen(){
		boolean flag = true;
		while(flag){
//			System.out.println("Please select from the following categories:(Enter 1/2/3)");
//			System.out.println("1. Admin\t\t2. Faculty\t\t3. Student\n");
			System.out.println("------Please Enter your login credentials------");
			Login login = new Login(dbConn);

		/*	@SuppressWarnings("resource")
			Scanner scanner = new Scanner(System.in);
			String value = scanner.nextLine();
			int choice = Integer.parseInt(value);
			switch(choice){
			case 1:
				System.out.println("------Please Enter your login credentials------");
				break;
			case 2:
				System.out.println("------Please Enter your login credentials------");
				break;
			case 3:
				System.out.println("------Please Enter your login credentials------");
				break;
			default:
				System.out.println("Invalid choice: Please enter again.");
					
			}*/
		}
	}
	
}
