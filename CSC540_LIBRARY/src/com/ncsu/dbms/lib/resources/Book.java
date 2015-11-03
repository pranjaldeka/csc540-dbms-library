package com.ncsu.dbms.lib.resources;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

import com.ncsu.dbms.lib.connection.DBConnection;
import com.ncsu.dbms.lib.exception.PrintSQLException;
import com.ncsu.dbms.lib.utilities.Constant;
import com.ncsu.dbms.lib.utilities.Utility;

import oracle.jdbc.OracleTypes;


public class Book {

	private String userName;
	private String userType;

	public Book(String userName, String userType) {
		this.userName = userName;
		this.userType = userType;
	}

	public  void showBooks() {
		// Searching a book;
    	
        try {
        	ResultSet rs;
        	CallableStatement cstmt = DBConnection.returnCallableStatememt("{call STUDENT_PUBLICATION_PKG.fetch_books_data_proc(?, ?)}");
	       	cstmt.registerOutParameter(1, OracleTypes.CURSOR);
	       	cstmt.registerOutParameter(2, OracleTypes.VARCHAR);
	       	ArrayList<Object> arrayList = DBConnection.returnResultSetAndError(cstmt, 1, 2);
	       	if(!arrayList.get(1).equals(Constant.kBlankString))
	       	{
	       		System.out.println(arrayList.get(1));
	       	    Resource sr = new Resource(this.userName, this.userType);
                sr.showPublicationMenuItems();
	       		return;
	       	}  
	       	rs = (ResultSet)arrayList.get(0);
            if (!rs.next() ) {
                System.out.println("No Books found !!");
                Resource sr = new Resource(this.userName, this.userType);
                sr.showPublicationMenuItems();
                return;
            } else {
                System.out.println("ISBN"+"\t" +"Publisher" +"\t" +"Type"+"\t" +"Library" +"\t" + "Edition"+"\t  " +"Year_Of_Pub" +"\t" + "Authors" +"\t\t\t" + "Title"+"No. Of Hard Copies");
                System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------------");

                do {
                	String isbn = rs.getString("isbn");
		            String title = rs.getString("title");
		            String authors = rs.getString("authors");
		            String publisher = rs.getString("publisher");
		            String edition = rs.getString("edition");
		            String yearOfPub = rs.getString("year_of_publication");
		            String noHardCopies = rs.getString("no_of_hardcopies");
		            String library = rs.getString("name");
		            String type = rs.getString("has_electronic");
		            System.out.println(isbn +"\t" + publisher+"\t\t" + type  +"\t" + library + edition +"\t\t" + yearOfPub +"\t" + authors +"\t\t" + title +" "+noHardCopies);
                } while (rs.next());
            }
			 displayDialogueAfterSearch();
		}catch(SQLException e){
       		PrintSQLException.printSQLException(e);
			Utility.badErrorMessage();
		}
       
        //conn.close();

	}
	
	public  void displayDialogueAfterSearch(){
		boolean flag = true;
		try{
			System.out.println("\nPlease enter your choice:");
			System.out.println("1: Check-out a book.\t 0:Go back to previous menu.");
			if (userType.equals(Constant.kFaculty)) {
				Utility.setMessage("2: Reserve a book\t");
			}
			while(flag){
					@SuppressWarnings("resource")
					Scanner scanner = new Scanner(System.in);
					String value = scanner.nextLine();
					int choice = Integer.parseInt(value);
					switch(choice){
					case 1:
						// Call check out method
						checkOutBookConsole();
						flag = false;
							break;
					case 0:
		                Resource sr = new Resource(this.userName, this.userType);
		                sr.searchResources();
						flag = false;
						break;
					case 2:
						if (userType.equals(Constant.kFaculty)) {
							reserveBookConsole();
						}
						else {
							Utility.setMessage("Invalid choice: Please enter again.");
						}
						break;
					default:
						System.out.println("Invalid choice: Please enter again.");
							
					}
				}
		}
		catch(Exception e){
			System.out.println("Something bad happened!!! Please try again...");
			displayDialogueAfterSearch();
		}
	}
	private void reserveBookConsole() {
		Utility.setMessage("Please enter the ISBN number of book you want to check out");
		String isbn = Utility.enteredConsoleString();
		String library = Utility.getLibraryInput();
		Utility.setMessage("Please enter reservation end date and time");
		String reserve_end_date = Utility.getTimeInput();
		reserveBook(isbn,library,reserve_end_date);
	}

	private void reserveBook(String isbn, String library,
			String reserve_end_date) {
	}

	private  void checkOutBookConsole() {
		boolean isHardCopy = Utility.getDeliveryType();
		Utility.setMessage("Please enter the ISBN number of book you want to check out");
		String isbn = Utility.enteredConsoleString();
		String library = null;
		library = Utility.getLibraryInput();
		String return_date = null;
		if (isHardCopy) {
			Utility.setMessage("Please enter return date and time");
			return_date = Utility.getTimeInput();
		}
		
		
		checkOutBook(isbn,library,return_date, isHardCopy);	
	}
	
	private  void checkOutBook(String isbn, String lib, String return_date, boolean isHardCopy) {
		Resource sr = new Resource(this.userName, this.userType);
		if (!isHardCopy) {
			try{
				sr.checkOutElectronicResource(Constant.kBook, isbn, Utility.getLibraryId(lib));
			}
	       	catch(SQLException e){
	       		PrintSQLException.printSQLException(e);
				Utility.badErrorMessage();
			}
		}
		else {
			try{
				sr.checkOutResource(Constant.kBook, isbn, Utility.getLibraryId(lib), return_date);
			}
	       	catch(SQLException e){
	       		PrintSQLException.printSQLException(e);
				Utility.badErrorMessage();
			} 
		}	
		Utility.callUserDialogueBox(userName, userType);

	}

	
	public void checkedOutBooks() {
        try {
        	ResultSet rs;
        	CallableStatement cstmt = DBConnection.returnCallableStatememt("{call checked_out_resource_pkg.checked_out_resources_proc (?, ?, ?, ?, ?)}");
	       	cstmt.setString(1, Constant.kBook);
	       	cstmt.setString(2, userType);
	       	cstmt.setString(3, userName);
        	cstmt.registerOutParameter(4, OracleTypes.CURSOR);
	       	cstmt.registerOutParameter(5, OracleTypes.VARCHAR);

	       	ArrayList<Object> arrayList = DBConnection.returnResultSetAndError(cstmt, 4, 5);  
	       	rs = (ResultSet)arrayList.get(0);
            if (!rs.next() ) {
                Resource sr = new Resource(this.userName, this.userType);
                System.out.println("\nYour have not checked out any books recently\n");
                sr.showPublicationMenuItemsCheckedOut();
                return;
            } else {
                System.out.println("ISBN"+"\t" +"Publisher" + "\t" +"Library" +"\t" + "Edition"+"\t  " +"Year_Of_Pub" +"\t" + "Authors" +"\t\t\t" + "Title");
                System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------------");

                do {
                	String isbn = rs.getString("isbn");
		            String title = rs.getString("title");
		            String authors = rs.getString("authors");
		            String publisher = rs.getString("publisher");
		            String edition = rs.getString("edition");
		            String yearOfPub = rs.getString("year_of_publication");
		            String library = rs.getString("name");
		            System.out.println(isbn +"\t" + publisher+"\t\t"  + library + edition +"\t\t" + yearOfPub +"\t" + authors +"\t\t" + title);
                } while (rs.next());
            }
            displayDialogueAfterCheckedOutResource();
		}catch(SQLException e){
       		PrintSQLException.printSQLException(e);
			Utility.badErrorMessage();
		}
	}
	
	private  void displayDialogueAfterCheckedOutResource(){
		boolean flag = true;
		try{
			System.out.println("\nPlease enter your choice:");
			System.out.println("1: Return a book.\t0:Go back to previous menu.");
			while(flag){
					@SuppressWarnings("resource")
					Scanner scanner = new Scanner(System.in);
					String value = scanner.nextLine();
					int choice = Integer.parseInt(value);
					switch(choice){
					case 1:
						System.out.println("Return a book");
						// Call check out method
						checkInBookConsole();
						flag = false;
						break;
					case 0:
						System.out.println("Going back to main menu");
						Utility.callUserDialogueBox(userName, userType);
						flag = false;
						break;
					default:
						System.out.println("Invalid choice: Please enter again.");
							
					}
				}
		}
		catch(Exception e){
			System.out.println("Something bad happened!!! Please try again...");
			displayDialogueAfterCheckedOutResource();
		}
	}
	
	private  void checkInBookConsole() {
		Utility.setMessage("Please enter the ISBN number of book you want to return");
		String isbn = Utility.enteredConsoleString();
		checkInBook(isbn);
	}
	
	private  void checkInBook(String isbn) {
		try{
			Resource sr = new Resource(this.userName, this.userType);
			sr.checkInResource(Constant.kBook, isbn);
		}
		catch(SQLException e){
	       		PrintSQLException.printSQLException(e);
				Utility.badErrorMessage();
	    } 	
		Utility.callUserDialogueBox(userName, userType);
	}
}
