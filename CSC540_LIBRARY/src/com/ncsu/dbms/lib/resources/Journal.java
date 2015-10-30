package com.ncsu.dbms.lib.resources;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import com.ncsu.dbms.lib.connection.DBConnection;
import com.ncsu.dbms.lib.users.Student;

public class Journal {
	public Journal(){
		showDialogueBox();
		
	}
	public static void showDialogueBox(){
		
		System.out.println("Please enter a keyword(either ISSN/Authors/Title):");
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
		String keyword = scanner.nextLine();
		String queryString = new StringBuilder().append("\'").append("%").append(keyword).append("%").append("\'").toString().toUpperCase();
		searchJournal(queryString);
	}
	private static void searchJournal(String queryString) {
		// Searching a book;
        ResultSet rs;
    	String query;
    	query = "SELECT ISSN, " +
    	"authors, "+
    	"year_of_publication, "+
    	"title "+
    	"FROM journals "+
    	"WHERE upper(ISSN) LIKE "+ queryString +
    	" OR upper(authors) LIKE "+ queryString +
    	" OR upper(title) LIKE "+ queryString;
        try {
			rs = DBConnection.executeQuery(query);
            
            if (!rs.next() ) {
                System.out.println("No Journal found with the entered keyword. Please try a different keyword:");
                showDialogueBox();
                return;
            } else {
                System.out.println("ISSN"+"\t" +"Authors" +"\t\t\t" + "Publication Year" + "\t "+ "Title" );
                System.out.println("-----------------------------------------------------------------------------------------");

                do {
                	String ISSN = rs.getString("ISSN");
		            String authors = rs.getString("authors");
		            String year_of_publication = rs.getString("year_of_publication");
		            String title = rs.getString("title");
		            System.out.println(ISSN +"\t" + authors +"\t\t" + year_of_publication +"\t\t" + title);
                } while (rs.next());
            }
			 displayDialogueAfterSearch();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void displayDialogueAfterSearch(){
		boolean flag = true;
		try{
			System.out.println("\nPlease enter your choice:");
			System.out.println("1: Check out Journal.\t0:Go back to previous menu.");
			while(flag){
					@SuppressWarnings("resource")
					Scanner scanner = new Scanner(System.in);
					String value = scanner.nextLine();
					int choice = Integer.parseInt(value);
					switch(choice){
					case 1:
						System.out.println("Checking out Journal");
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
