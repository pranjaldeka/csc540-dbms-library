package com.ncsu.dbms.lib.resources;

import java.sql.CallableStatement;
import java.sql.SQLException;

import com.ncsu.dbms.lib.connection.DBConnection;
import com.ncsu.dbms.lib.utilities.Utility;

public class Resource {
	private String userName;
	private String userType;
	public Resource(String userName, String userType){
		this.userName = userName;
		this.userType = userType;
	}
	public  void searchResources(){
		System.out.println("Please enter your choice:");
		System.out.println("1: Publications\t2: Conference/Study rooms\t3: Cameras\t0: Go back to previous menu.");

		boolean flag = true;
		try{
			while(flag){
					int choice = Integer.parseInt(Utility.enteredConsoleString());
					switch(choice){
					case 0:
						Utility.callUserDialogueBox(userName, userType);
						flag = false;
						break;
					case 1:
						System.out.println("Publications");
						// Call check out method
						showPublicationMenuItems();
						flag = false;
							break;
					case 2:
						Room room = new Room(userName, userType);
						room.showDialogueBox();
						flag = false;
						break;
					case 3:					
						// Call check out method
						Camera camera = new Camera(this.userName, this.userType);
						camera.searchCameras();							
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
		Utility.setMessage("Please enter a choice:");
		System.out.println("1: Books\t2: Journals\t3: Conferences Papers\t0: Go back to previous menu.");
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
						Book book = new Book(this.userName, this.userType);
						book.showBooks();
						flag = false;
						break;
					case 2:
						System.out.println("Journals");
						Journal journal=new Journal(this.userName, this.userType);
						journal.showJournals();
						flag = false;
						break;
					case 3:
						System.out.println("Conference Papers");
						// Call check out method
						ConferencePaper confPaper = new ConferencePaper(this.userName, this.userType);
						confPaper.searchConferencePapers();
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
	public  void checkOutResource(String resourceType, String resourceName, String libraryType, String returnDate)throws SQLException {
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
	
	
		
	public void checkInResource(String resourceType, String resourceName) throws SQLException{
		try{
	    	CallableStatement cstmt = DBConnection.returnCallableStatememt("{call check_in_pkg.check_in_proc(?, ?,?,?,?)}");
	    	cstmt.setString(1, resourceType);
	    	cstmt.setString(2, resourceName.toUpperCase());
	    	cstmt.setString(3, userType);
	    	cstmt.setString(4, userName);

	    	cstmt.registerOutParameter(5, java.sql.Types.VARCHAR);
	    	String outputMessage = DBConnection.returnMessage(cstmt, 5);
	       	System.out.println(outputMessage);
		}
	       	catch(SQLException e){
	       		throw e;
		} 
		
	}
	
	public void checkedOutResources() {
		System.out.println("Please enter your choice:");
		System.out.println("1: Publications\t2: Conference/Study rooms\t3: Cameras\t0: Go back to previous menu.");

		boolean flag = true;
		try{
			while(flag){
					int choice = Integer.parseInt(Utility.enteredConsoleString());
					switch(choice){
					case 0:
						Utility.callUserDialogueBox(userName, userType);
						flag = false;
						break;
					case 1:
						System.out.println("Publications");
						// Call check in method
						showPublicationMenuItemsCheckedOut();
						flag = false;
						break;
					case 2:
						Room room = new Room(userName, userType);
						room.reservedOrCheckoutRooms();
						flag = false;
						break;
					case 3:
						System.out.println("Cameras");
					//	Journal.showDialogueBox();
						flag = false;
						break;
					
					 default:
						System.out.println("Invalid choice: Please enter again.");
						checkedOutResources();
						flag = false;
						break;
					}
				}
		}
		catch(Exception e){
			Utility.badErrorMessage();
			checkedOutResources();
		}
	}

	public void showPublicationMenuItemsCheckedOut() {
		Utility.setMessage("Please enter a choice:");//books, ebooks,journals and conference
		System.out.println("1: Books\t2: Journals\t3: Conference Papers\t0: Go back to previous menu.");
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
						// Call check in method
						Book book = new Book(this.userName, this.userType);
						book.checkedOutBooks();
						flag = false;
						break;
					case 2:
						System.out.println("Journals");
						Journal journal = new Journal(userName, userType);
						journal.checkedOutJournals();
						flag = false;
						break;
					case 3:
						System.out.println("Journals");
						ConferencePaper conferencePaper = new ConferencePaper(userName, userType);
						conferencePaper.checkedOutConferencePapers();
						flag = false;
						break;
					
					 default:
						System.out.println("Invalid choice: Please enter again.");
						showPublicationMenuItemsCheckedOut();
						flag = false;
						break;
					}
				}
		}
		catch(Exception e){
			Utility.badErrorMessage();
		}
	}
		
}
