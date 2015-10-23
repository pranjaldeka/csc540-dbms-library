package com.ncsu.dbms.lib.users;

import java.util.Scanner;

public class Faculty extends User {
	@SuppressWarnings("unused")
	private String userName;
	public Faculty(String userName, String firstName, String lastName) {
		// TODO Auto-generated constructor stub
		this.userName = userName;
		System.out.println("*******************Welcome*****************\n");
		System.out.println("\t\t" + firstName + " " + lastName + "!!!");
		System.out.println("\n*******************************************");
		showMenuItems();

	}
	private void showMenuItems() {
		// TODO Auto-generated method stub
		System.out.println("Please select from the below options: ");
		System.out.println("\n1. Search a Book \t\t 2. Reserve a Book");
		System.out.println("3. Search Camera \t\t 4. Reserve a Camera");
		System.out.println("5. Search a Conference Room \t\t 6. Reserve a Conference Room");
		System.out.println("7. Check all reserved resources   \t\t 8. Cancel a reservation");
		selectAnAction();
	}

	private void selectAnAction() {
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

}
