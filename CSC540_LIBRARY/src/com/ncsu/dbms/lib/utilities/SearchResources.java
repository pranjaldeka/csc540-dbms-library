package com.ncsu.dbms.lib.utilities;

import java.util.Scanner;

import com.ncsu.dbms.lib.resources.Books;
import com.ncsu.dbms.lib.resources.Cameras;
import com.ncsu.dbms.lib.users.Student;

public class SearchResources {

	public static void searchResources(){
		System.out.println("Hey!!!!");
		System.out.println("Please enter your choice of search (Enter 1/2/3/0):");
		System.out.println(""1: Books\t2: Cameras\t3: Journals\t4.Study Rooms\t5.Conference Paper\t0: Go back to previous menu."");

		boolean flag = true;
		try{
			while(flag){
					@SuppressWarnings("resource")
					Scanner scanner = new Scanner(System.in);
					String value = scanner.nextLine();
					int choice = Integer.parseInt(value);
					switch(choice){
					case 0:
						Student.showMenuItems();
						flag = false;
						break;
					case 1:
						System.out.println("Search a Book");
						// Call check out method
						Books.showDialogueBox();
						flag = false;
							break;
					case 2:
						System.out.println("Search a Camera");
						Cameras.showDialogueBox();
						flag = false;
						break;
					case 3:
						System.out.println("Search a Journal");
						Journals.showDialogueBox();
						flag = false;
						break;
					case 4:
						System.out.println("Search a Room");
						Rooms.showDialogueBox();
						flag = false;
						break;
					case 5:
						System.out.println("Search a Conference Paper");
						Conf_Papers.showDialogueBox();
						flag = false;
						break;
					}
				}
		}
		catch(Exception e){
			System.out.println("Something bad happened!!! Please try again...");
			searchResources();
		}
	}
		
}
