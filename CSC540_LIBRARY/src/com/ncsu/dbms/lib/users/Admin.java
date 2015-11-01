package com.ncsu.dbms.lib.users;

import java.util.Scanner;

import com.ncsu.dbms.lib.utilities.Constant;

public class Admin extends User {

	@SuppressWarnings("unused")
	private String userName;
	public Admin(String userName, String firstName, String lastName) {
		super(userName, firstName, lastName);
	}
	
	public Admin(String userName){
		super(userName);
	}
	
	public  void showMenuItems() {
		System.out.println("Please select from the below options: ");
		System.out.println("\n1. Profile \t\t 2. Resources");
		System.out.println("3. Checked-Out Resources \t\t 4. Notification");
		System.out.println("5. Due-Balance\t\t  6. Logout");
		selectAnAction();
	}

	protected void selectAnAction() {
		boolean flag = true;
		while(flag){
				@SuppressWarnings("resource")
				Scanner scanner = new Scanner(System.in);
				String value = scanner.nextLine();
				int choice = Integer.parseInt(value);
				switch(choice){
				case 1:
					System.out.println("Adding a new Book"); // call add book functionality
					flag = false;
						break;
				case 2:
					System.out.println("Deleting a new Book");
					flag = false;
					break;
				case 3:
					System.out.println("Adding a new Student");
					flag = false;
					break;
				case 4:
					System.out.println("Deleting a new Student");
					flag = false;
					break;
				case 5:
					System.out.println("Adding a new Faculty");
					flag = false;
					break;
				case 6:
					System.out.println("Deleting a new Faculty");
					flag = false;
					break;
				case 7:
					System.out.println("Adding a new Admin");
					flag = false;
					break;
				case 8:
					System.out.println("Deleting a new Admin");
					flag = false;
					break;
				default:
					System.out.println("Invalid choice: Please enter again.");
						
				}
			}
		}
	
	protected void showProfile(){}
	
	protected void modifyProfileDataMenu() {}

}
