package com.ncsu.dbms.lib.users;

import java.util.Scanner;

import com.ncsu.dbms.lib.utilities.SearchResource;

public class Student extends User {
	@SuppressWarnings("unused")
	private String userName;
	public Student(String userName, String firstName, String lastName) {
		this.userName = userName;
		System.out.println("*******************Welcome*****************\n");
		System.out.println("\t\t" + firstName + " " + lastName + "!!!");
		System.out.println("\n*******************************************");
		showMenuItems();
	}
	public static void showMenuItems() {
		// TODO Auto-generated method stub
		System.out.println("Please select from the below options: ");
		System.out.println("\n1. Search a Resource \t\t 2. Reserve a Resource");
		System.out.println("3. Check all reserved resources  4. Cancel a reservation");
		selectAnAction();
	}

	private static void selectAnAction() {
		try{
			boolean flag = true;
			while(flag){
					@SuppressWarnings("resource")
					Scanner scanner = new Scanner(System.in);
					String value = scanner.nextLine();
					int choice = Integer.parseInt(value);
					switch(choice){
					case 1:
						SearchResource.searchResources();
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
			catch(Exception e){
				System.out.println("Something bad happened!!! Please try again...");
				showMenuItems();

			}
		
	
		
	}
}
