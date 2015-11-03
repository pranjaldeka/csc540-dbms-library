package com.ncsu.dbms.lib.console;

import java.util.Scanner;

import com.ncsu.dbms.lib.connection.DBConnection;
import com.ncsu.dbms.lib.exception.InvalidCredentialException;
import com.ncsu.dbms.lib.login.Login;
import com.ncsu.dbms.lib.utilities.Utility;
@SuppressWarnings("unused")

public class LibConsole {

	private DBConnection dbConn;
	public LibConsole(DBConnection dbConn) {
		this.dbConn = dbConn;
		// TODO Auto-generated constructor stub
	}
	public LibConsole(){
		
	}

	public void start() {
		// TODO Auto-generated method stub
		
		System.out.println("*************************************************************************************\n");
		System.out.println("\t\t\tWelcome to NC State Library.\n");
		System.out.println("*************************************************************************************");
		loginAgain();
		
	}
	
	public void loginAgain (){
		try{
		Utility.setMessage("Please enter 1 to login into database and 0 to exit.");
		boolean flag = true;
		while(flag){
				int choice = Integer.parseInt(Utility.enteredConsoleString());
				switch(choice){
				case 1:
					loginScreen(1);
					flag = false;
					break;
				case 0:
					logout();
					flag = false;
					break;
				default:
					System.out.println("Invalid choice: Please enter again.");
						
				}
			}
		}
		catch(Exception e){
			Utility.badErrorMessage();
			loginAgain();
		}
	}
	public void loginScreen(int choice ){
		try{
			boolean flag = true;
			while(flag){
					//int choice = Integer.parseInt(Utility.enteredConsoleString());
					switch(choice){
					case 1:
						System.out.println("------Please Enter your login credentials------");
						try{
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
						logout();
						flag = false;
						break;
					default:
						System.out.println("Invalid choice: Please enter again.");
							
					}
				}
		}
		catch(Exception e){
			System.out.println("Something bad happened!!! Please try again...");
			loginScreen(0);

		}
	}


	public void logout() {
			Utility.setMessage("Goodbye !!!");
			Utility.setMessage("Enter enter 1 to login again: ");
			boolean flag = true;
			while(flag){
					int choice = Integer.parseInt(Utility.enteredConsoleString());
					switch(choice){
					case 1:
						loginScreen(1);
						flag = false;
						break;
					default:
						System.out.println("Invalid choice: Please enter again.");
							
					}
				}

	}
}
