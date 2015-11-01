package com.ncsu.dbms.lib.resources;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

import com.ncsu.dbms.lib.connection.DBConnection;
import com.ncsu.dbms.lib.exception.PrintSQLException;
import com.ncsu.dbms.lib.users.Student;
import com.ncsu.dbms.lib.utilities.Constant;
import com.ncsu.dbms.lib.utilities.Utility;

import oracle.jdbc.OracleTypes;


public class Book {
	String userName;
	public Book(String userName) {
		//showDialogueBox();
		this.userName = userName;
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
        	CallableStatement cstmt = DBConnection.returnCallableStatememt("{call STUDENT_PUBLICATION_PKG.fetch_books_data_proc(?, ?)}");
	       	cstmt.registerOutParameter(1, OracleTypes.CURSOR);
	       	cstmt.registerOutParameter(2, OracleTypes.VARCHAR);
	       	ArrayList<Object> arrayList = DBConnection.returnResultSetAndError(cstmt, 1, 2);
	       	if(!arrayList.get(1).equals(Constant.kBlankString))
	       	{
	       		System.out.println(arrayList.get(1));
	       	    Resource sr = new Resource(this.userName);
                sr.showPublicationMenuItems();
	       		return;
	       	}  
	       	rs = (ResultSet)arrayList.get(0);
            if (!rs.next() ) {
                System.out.println("No Books found with the entered keyword. Please try a different keyword:");
                Resource sr = new Resource(this.userName);
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
		                Resource sr = new Resource(this.userName);
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

		flag = true;
		boolean flagDate = true;
		boolean flagTime = true;
		do{
				String return_date = null;
				String enteredDate = null;
				String enteredTime = null;
				//String validFormat = "yyyy-mm-dd hh:mm:ss";
				String validDateFormat = "yyyy-MM-dd";
				String validTimeFormat = "HH:mm:ss";
				do{
					Utility.setMessage("Please enter date of return in yyyy-MM-dd format:");
					enteredDate = Utility.enteredConsoleString();
					if(Utility.validateDateFormat(enteredDate, validDateFormat)){
							flagDate = false;
							do{
								Utility.setMessage("Please enter time of return in HH:mm:ss format:");
								enteredTime = Utility.enteredConsoleString();
								if(Utility.validateDateFormat(enteredTime, validTimeFormat)){
									flagTime = false;
									flag = false;
									return_date = enteredDate + " " + enteredTime;
									System.out.println(isbn + " "+ library + " " + return_date);
									checkOutBook(isbn,library,return_date);	
								}else{
									System.out.println("Time is invalid. Please try again");
								}
							}
							while(flagTime);
					}else{
						System.out.println("Date is invalid. Please try again!");
					}
				}
				while(flagDate);
		}
		while (flag);				
	}
	
	private  void checkOutBook(String isbn, String lib, String return_date) {
		Resource sr = new Resource(this.userName);
		try{
			sr.checkOutResource(Constant.kBook, isbn, Constant.kStudent, 
					this.userName, Utility.getLibraryId(lib), return_date);
		}
       	catch(SQLException e){
       		PrintSQLException.printSQLException(e);
			Utility.badErrorMessage();
		} 	
		callStudentDialogueBox();

	}
	private void callStudentDialogueBox(){
		Student s = new Student(this.userName);
		s.showMenuItems();
	}
}
