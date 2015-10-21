package com.ncsu.dbms.lib.console;

import java.util.Scanner;

import com.ncsu.dbms.lib.connection.DBConnection;
import com.ncsu.dbms.lib.exception.InvalidCredentialException;
import com.ncsu.dbms.lib.login.Login;

public class LibConsole {

	private DBConnection dbConn;
	public LibConsole(DBConnection dbConn) {
		this.dbConn = dbConn;
		// TODO Auto-generated constructor stub
	}


	public void start() {
		// TODO Auto-generated method stub
		
		System.out.println("*************************************************************************************\n");
		System.out.println("\t\t\tWelcome to NC State Library.\n");
		System.out.println("*************************************************************************************");
		loginScreen();
		
	}
	public void loginScreen(){
		boolean flag = true;
		while(flag){
			System.out.println("Please enter 1 to login into database and 0 to exit.");			
				@SuppressWarnings("resource")
				Scanner scanner = new Scanner(System.in);
				String value = scanner.nextLine();
				int choice = Integer.parseInt(value);
				switch(choice){
				case 1:
					System.out.println("------Please Enter your login credentials------");
					try{
						@SuppressWarnings("unused")
						Login login = new Login(dbConn);
						flag= false;
						break;
					}
					catch(InvalidCredentialException e){
						System.out.println(e.toString()+" Please try again.");
						flag = true;
						continue;
					}
				case 0:
					System.out.println("Goodbye !!!");
					flag = false;
					break;
				default:
					System.out.println("Invalid choice: Please enter again.");
						
				}
			}
		
	}
}
