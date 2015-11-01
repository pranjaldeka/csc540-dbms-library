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
	public  void showDialogueBox(){
		searchBook("");
	}
	private  void searchBook(String queryString) {
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
			System.out.println("1: Check-out a book.\t0:Go back to previous menu.");
			while(flag){
					@SuppressWarnings("resource")
					Scanner scanner = new Scanner(System.in);
					String value = scanner.nextLine();
					int choice = Integer.parseInt(value);
					switch(choice){
					case 1:
						System.out.println("Checking out a book");
						// Call check out method
						checkOutBookConsole();
						flag = false;
							break;
					case 0:
						System.out.println("Going back to previous menu");
		                Resource sr = new Resource(this.userName, this.userType);
		                sr.searchResources();
						flag = false;
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
	private  void checkOutBookConsole() {
		Utility.setMessage("Please enter the ISBN number of book you want to check out");
		String isbn = Utility.enteredConsoleString();
		String library = null;
		boolean flag = true;
		do{
			Utility.setMessage("Please select the Library:");
			Utility.setMessage("1. D.H. Hill \t\t 2. J.B. Hunt");
			 library = Utility.enteredConsoleString();
			if(library.equals("1") || library.equals("2"))
				flag=false;
		}
		while(flag);

		String return_date = Utility.getTimeInput();
		
		checkOutBook(isbn,library,return_date);	
	}
	
	private  void checkOutBook(String isbn, String lib, String return_date) {
		Resource sr = new Resource(this.userName, this.userType);
		try{
			sr.checkOutResource(Constant.kBook, isbn, Utility.getLibraryId(lib), return_date);
		}
       	catch(SQLException e){
       		PrintSQLException.printSQLException(e);
			Utility.badErrorMessage();
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
                System.out.println("\nYour have not checkout any books recently\n");
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
	
	public  void displayDialogueAfterCheckedOutResource(){
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
			displayDialogueAfterSearch();
		}
	}
	
	private  void checkInBookConsole() {
		Utility.setMessage("Please enter the ISBN number of book you want to return");
		String isbn = Utility.enteredConsoleString();
		System.out.println(isbn);
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
