package com.ncsu.dbms.lib.resources;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import com.ncsu.dbms.lib.connection.DBConnection;
import com.ncsu.dbms.lib.users.Student;
import com.ncsu.dbms.lib.utilities.SearchResource;

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
        	CallableStatement cstmt = DBConnection.con.prepareCall("{call student_book_pkg.fetch_books_data_proc(?, ?)}");
	       	 ResultSet rs; 
	       	cstmt.registerOutParameter(1, OracleTypes.CURSOR);
	       	cstmt.registerOutParameter(2, OracleTypes.VARCHAR);
	       	cstmt.executeQuery();
	       	rs = (ResultSet) cstmt.getObject(1);
	       	String error = cstmt.getString(2);
	       	if(error != null)
	       	{
	       		System.out.println(error);
	       		showDialogueBox();
	       	}            
            if (!rs.next() ) {
                System.out.println("No Books found with the entered keyword. Please try a different keyword:");
                SearchResource sr = new SearchResource();
                sr.showPublicationMenuItems();
                return;
            } else {
                System.out.println("ISBN"+"\t" +"Publisher" +"\t" +"Type"+"\t" +"Library" +"\t" + "Edition"+"\t  " +"Year_Of_Pub" +"\t" + "Authors" +"\t\t\t" + "Title"+"No. Of Hard Copies");
                System.out.println("-----------------------------------------------------------------------------------------");

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
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       
        //conn.close();

	}
	
	public static void displayDialogueAfterSearch(){
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
}
