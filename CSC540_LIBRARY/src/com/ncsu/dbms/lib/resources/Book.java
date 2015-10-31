package com.ncsu.dbms.lib.resources;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

import com.ncsu.dbms.lib.connection.DBConnection;
import com.ncsu.dbms.lib.console.LibConsole;
import com.ncsu.dbms.lib.exception.PrintSQLException;
import com.ncsu.dbms.lib.users.Student;
import com.ncsu.dbms.lib.utilities.Constant;
import com.ncsu.dbms.lib.utilities.SearchResource;
import com.ncsu.dbms.lib.utilities.Utility;

import oracle.jdbc.OracleTypes;


public class Book {
	public Book() {
		//showDialogueBox();
	}
	public  void showDialogueBox(){
		searchBook("");
	}
	private  void searchBook(String queryString) {
		// Searching a book;
    	
        try {
        	/*CallableStatement cstmt = DBConnection.con.prepareCall("{call student_book_pkg.fetch_books_data_proc(?, ?)}");
	       	 ResultSet rs; 
	       	cstmt.registerOutParameter(1, OracleTypes.CURSOR);
	       	cstmt.registerOutParameter(2, OracleTypes.VARCHAR);
	       	cstmt.executeQuery();
	       	rs = (ResultSet) cstmt.getObject(1);
	       	String error = cstmt.getString(2); */
        	ResultSet rs;
        	CallableStatement cstmt = DBConnection.returnCallableStatememt("{call student_book_pkg.fetch_books_data_proc(?, ?)}");
	       	cstmt.registerOutParameter(1, OracleTypes.CURSOR);
	       	cstmt.registerOutParameter(2, OracleTypes.VARCHAR);
	       	ArrayList<Object> arrayList = DBConnection.returnResultSetAndError(cstmt, 1, 2);
	       	if(!arrayList.get(1).equals(Constant.kBlankString))
	       	{
	       		System.out.println(arrayList.get(1));
	       		showDialogueBox();
	       		return;
	       	}  
	       	rs = (ResultSet)arrayList.get(0);
            if (!rs.next() ) {
                System.out.println("No Books found with the entered keyword. Please try a different keyword:");
                SearchResource sr = new SearchResource();
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
//						Student s = new Student();
//						s.showMenuItems();
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
		Utility.welcomeMessage("Please enter the ISBN number of book you want to check out");
		String isbn = Utility.enteredConsoleString();
		String library = null;
		boolean flag = true;
		do{
			Utility.welcomeMessage("Please select the Library:");
			Utility.welcomeMessage("1. D.H. Hill \t\t 2. J.B. Hunt");
			 library = Utility.enteredConsoleString();
			if(library.equals("1") || library.equals("2"))
				flag=false;
		}
		while(flag);
		System.out.println(isbn + " "+ library);
		checkOutBook(isbn,library);
	}
	private  void checkOutBook(String isbn, String lib) {
		try{
	    	CallableStatement cstmt = DBConnection.returnCallableStatememt("{call check_out_pkg.check_out_proc(?, ?,?,?,?,?)}");
	    	cstmt.setString(1, Constant.kBook);
	    	cstmt.setString(2, isbn.toUpperCase());
	    	cstmt.setString(3, Constant.kStudent);
	    	cstmt.setString(4, Student.userName);
	    	cstmt.setString(5, Utility.getLibraryId(lib));

	    	cstmt.registerOutParameter(6, java.sql.Types.VARCHAR);
	    	String outputMessage = DBConnection.returnMessage(cstmt, 6);
	       	System.out.println(outputMessage);
	       	callStudentDialogueBox();
	       	
		}
	       	catch(SQLException e){
	       		PrintSQLException.printSQLException(e);
				Utility.badErrorMessage();
	       		callStudentDialogueBox();
			} 	

	}
	private void callStudentDialogueBox(){
		Student s = new Student();
		s.showMenuItems();
	}
}
