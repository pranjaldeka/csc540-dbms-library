package com.ncsu.dbms.lib.resources;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import com.ncsu.dbms.lib.connection.DBConnection;
import com.ncsu.dbms.lib.users.Student;


public class Book {
	public Book() {
		showDialogueBox();
	}
	public static void showDialogueBox(){
			
		System.out.println("Please enter a keyword(either ISBN/Title/Pubisher):");
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
		String keyword = scanner.nextLine();
		String queryString = new StringBuilder().append("\'").append("%").append(keyword).append("%").append("\'").toString().toUpperCase();
		searchBook(queryString);
	}
	private static void searchBook(String queryString) {
		// Searching a book;
        ResultSet rs;
    	String query;
    	query = "SELECT isbn, " +
    	"title, "+
    	"authors, "+
    	"year_of_publication, "+
    	"edition, "+
    	"publisher "+
    	"FROM books "+
    	"WHERE upper(ISBN) LIKE "+ queryString +
    	" OR upper(authors) LIKE "+ queryString +
    	" OR upper(title) LIKE "+ queryString;
        try {
			rs = DBConnection.executeQuery(query);
            
            if (!rs.next() ) {
                System.out.println("No Books found with the entered keyword. Please try a different keyword:");
                showDialogueBox();
                return;
            } else {
                System.out.println("ISBN"+"\t" +"Publisher" +"\t" + "Edition"+"\t  " +"Year_Of_Pub" +"\t" + "Authors" +"\t\t\t" + "Title");
                System.out.println("-----------------------------------------------------------------------------------------");

                do {
                	String isbn = rs.getString("isbn");
		            String title = rs.getString("title");
		            String authors = rs.getString("authors");
		            String yearOfPub = rs.getString("year_of_publication");
		            String edition = rs.getString("edition");
		            String publisher = rs.getString("publisher");
		            System.out.println(isbn +"\t" + publisher +"\t\t" + edition +"\t\t" + yearOfPub +"\t" + authors +"\t\t" + title);
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
						Student s = new Student();
						s.showMenuItems();
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
