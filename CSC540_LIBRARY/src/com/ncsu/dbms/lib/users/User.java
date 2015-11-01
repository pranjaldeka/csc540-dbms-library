package com.ncsu.dbms.lib.users;

import java.util.Scanner;


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
		System.out.println("Please select from the below options: ");
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
						System.out.println("Modifying Profile");
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


}
