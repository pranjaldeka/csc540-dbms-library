package com.ncsu.dbms.lib.resources;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import com.ncsu.dbms.lib.connection.DBConnection;
import com.ncsu.dbms.lib.users.Student;

public class ConferencePaper {
	public ConferencePaper(){
		showDialogueBox();
	}
		
	
	public static void showDialogueBox(){
		
		System.out.println("Please enter a keyword(either paper id/Title/authors):");
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
    	query = "SELECT conf_paper_id, " +
    	"conf_name, "+
    	"authors, "+
    	"year_of_publication, "+
    	"title, "+
   		"FROM conference_papers "+
    	"WHERE upper(conf_paper_id) LIKE "+ queryString +
    	" OR upper(authors) LIKE "+ queryString +
    	" OR upper(title) LIKE "+ queryString;
        try {
			rs = DBConnection.executeQuery(query);
            
            if (!rs.next() ) {
                System.out.println("No Conference Paper found with the entered keyword. Please try a different keyword:");
                showDialogueBox();
                return;
            } else {
                System.out.println("Conf Paper Id"+"\t" +"Conf Name" +"\t" + "Authors"+"\t\t\t  " +"Year_Of_Pub" +"\t" + "Title" +"\t\t\t");
                System.out.println("-----------------------------------------------------------------------------------------");

                do {
                	String conf_paper_id = rs.getString("conf_paper_id");
		            String conf_name = rs.getString("conf_name");
		            String authors = rs.getString("authors");
		            String yearOfPub = rs.getString("year_of_publication");
		            String title = rs.getString("title");
		            String publisher = rs.getString("publisher");
		            System.out.println(conf_paper_id +"\t" + conf_name +"\t\t" + authors +"\t\t" + yearOfPub +"\t" + title +"\t\t" );
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
			System.out.println("1: Check-out a Conference Paper.\t0:Go back to previous menu.");
			while(flag){
					@SuppressWarnings("resource")
					Scanner scanner = new Scanner(System.in);
					String value = scanner.nextLine();
					int choice = Integer.parseInt(value);
					switch(choice){
					case 1:
						System.out.println("Checking out a Conference Paper");
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
