package com.ncsu.dbms.lib.resources;

import java.sql.CallableStatement;
import java.sql.SQLException;

import com.ncsu.dbms.lib.connection.DBConnection;
import com.ncsu.dbms.lib.exception.PrintSQLException;
import com.ncsu.dbms.lib.users.Student;
import com.ncsu.dbms.lib.utilities.Utility;

public class Resource {
	String userName;
	public Resource(String userName){
		this.userName = userName;
	}
	public  void searchResources(){
		System.out.println("Please enter your choice:");
		System.out.println("1: Publications\t2: Conference/Study rooms\t3: Cameras\t0: Go back to previous menu.");

		boolean flag = true;
		try{
			while(flag){
					int choice = Integer.parseInt(Utility.enteredConsoleString());
					switch(choice){
					case 0:
						Student student = new Student(this.userName);
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
						//Journal.showDialogueBox();
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
						Book book = new Book(this.userName);
						book.showDialogueBox();
						flag = false;
							break;
					case 2:
						System.out.println("Conference/Study rooms");
						Camera.showDialogueBox();
						flag = false;
						break;
					case 3:
						System.out.println("Journals");
						Journal journal=new Journal(this.userName);
						journal.showDialogueBox();
						flag = false;
						break;
					case 4:
						System.out.println("Conference Papers");
						// Call check out method
						ConferencePaper confPaper = new ConferencePaper(this.userName);
						confPaper.showDialogueBox();
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
	//Generic method for checking out a resource
	//Call this method to check out Books/ebooks/journals/conference papers
	public  void checkOutResource(String resourceType, String resourceName, String userType, String userName, String libraryType, String returnDate)throws SQLException {
		try{
	    	CallableStatement cstmt = DBConnection.returnCallableStatememt("{call check_out_pkg.check_out_proc(?, ?,?,?,?,?,?)}");
	    	cstmt.setString(1, resourceType);
	    	cstmt.setString(2, resourceName.toUpperCase());
	    	cstmt.setString(3, userType);
	    	cstmt.setString(4, userName);
	    	cstmt.setString(5, libraryType);
	    	cstmt.setString(6, returnDate);
	    	
	    	cstmt.registerOutParameter(7, java.sql.Types.VARCHAR);
			String outputMessage = DBConnection.returnMessage(cstmt, 7);
	       	System.out.println(outputMessage);
		}
	       	catch(SQLException e){
	       		throw e;
				} 	

	}
		
}
