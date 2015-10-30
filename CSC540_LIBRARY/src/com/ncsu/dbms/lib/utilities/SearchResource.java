package com.ncsu.dbms.lib.utilities;

import java.util.Scanner;

import com.ncsu.dbms.lib.resources.Book;
import com.ncsu.dbms.lib.resources.Camera;
import com.ncsu.dbms.lib.resources.ConferencePaper;
import com.ncsu.dbms.lib.resources.Journal;
import com.ncsu.dbms.lib.resources.Room;
import com.ncsu.dbms.lib.users.Student;

public class SearchResource {

	public static void searchResources(){
		System.out.println("Hey!!!!");
		System.out.println("Please enter your choice of search (Enter 1/2/3/0):");
		System.out.println("1: Books\t2: Cameras\t3: Journals\t4.Study Rooms\t5.Conference Paper\t0: Go back to previous menu.");

		boolean flag = true;
		try{
			while(flag){
					@SuppressWarnings("resource")
					Scanner scanner = new Scanner(System.in);
					String value = scanner.nextLine();
					int choice = Integer.parseInt(value);
					switch(choice){
					case 0:
						Student student = new Student();
						student.showMenuItems();
						flag = false;
						break;
					case 1:
						System.out.println("Search a Book");
						// Call check out method
						Book.showDialogueBox();
						flag = false;
							break;
					case 2:
						System.out.println("Search a Camera");
						Camera.showDialogueBox();
						flag = false;
						break;
					case 3:
						System.out.println("Search a Journal");
						Journal.showDialogueBox();
						flag = false;
						break;
					case 4:
						System.out.println("Search a Room");
						Room.showDialogueBox();
						flag = false;
						break;
					case 5:
						System.out.println("Search a Conference Paper");
						ConferencePaper.showDialogueBox();
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
