package com.ncsu.dbms.lib.utilities;

import com.ncsu.dbms.lib.resources.Book;
import com.ncsu.dbms.lib.resources.Camera;
import com.ncsu.dbms.lib.resources.ConferencePaper;
import com.ncsu.dbms.lib.resources.Journal;
import com.ncsu.dbms.lib.resources.Room;
import com.ncsu.dbms.lib.users.Student;

public class SearchResource {
	public  void searchResources(){
		System.out.println("Please enter your choice:");
		System.out.println("1: Publications\t2: Conference/Study rooms\t3: Cameras\t0: Go back to previous menu.");

		boolean flag = true;
		try{
			while(flag){
					int choice = Integer.parseInt(Utility.enteredConsoleString());
					switch(choice){
					case 0:
						Student student = new Student(Student.userName);
						student.showMenuItems();
						flag = false;
						break;
					case 1:
						System.out.println("Publications");
						// Call check out method
						showPublicationMenuItems();
						flag = false;
							break;
					case 2:
						System.out.println("Conference/Study rooms");
						Camera.showDialogueBox();
						flag = false;
						break;
					case 3:
						System.out.println("Cameras");
						Journal.showDialogueBox();
						flag = false;
						break;
					
					 default:
						System.out.println("Invalid choice: Please enter again.");
						searchResources();
						flag = false;
						break;
					}
				}
		}
		catch(Exception e){
			Utility.badErrorMessage();
			searchResources();
		}
	}
	public void showPublicationMenuItems(){
		Utility.welcomeMessage("Please enter a choice:");// books,  ebooks, journals and conference
		System.out.println("1: Books\t2: eBooks\t3: Journals\t4: Conferences\t0: Go back to previous menu.");
		boolean flag = true;
		try{
			while(flag){
					int choice = Integer.parseInt(Utility.enteredConsoleString());
					switch(choice){
					case 0:
						searchResources();
						flag = false;
						break;
					case 1:
						System.out.println("Books");
						// Call check out method
						Book book = new Book();
						book.showDialogueBox();
						flag = false;
							break;
					case 2:
						System.out.println("Conference/Study rooms");
						Camera.showDialogueBox();
						flag = false;
						break;
					case 3:
						System.out.println("Cameras");
						Journal.showDialogueBox();
						flag = false;
						break;
					
					 default:
						System.out.println("Invalid choice: Please enter again.");
						showPublicationMenuItems();
						flag = false;
						break;
					}
				}
		}
		catch(Exception e){
			Utility.badErrorMessage();
			searchResources();
		}
	}
		
}
