package com.ncsu.dbms.lib.resources;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import com.ncsu.dbms.lib.connection.DBConnection;
import com.ncsu.dbms.lib.users.Student;

public class ConferenceRoom {
	public ConferenceRoom(){
		showDialogueBox();
	}

	public static void showDialogueBox(){
		
		System.out.println("Please enter a keyword(either Room number/Capacity):");
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
		String keyword = scanner.nextLine();
		String queryString = new StringBuilder().append("\'").append("%").append(keyword).append("%").append("\'").toString().toUpperCase();
		searchConfRoom(queryString);
	}
	private static void searchConfRoom(String queryString) {
		// Searching a book;
        ResultSet rs;
    	String query;
    	query = "SELECT room_no, " +
    	"library_id, "+
    	"capacity, "+
    	"floor_no, "+
      	"FROM conference_rooms "+
    	"WHERE room_no LIKE "+ queryString +
    	" OR capacity LIKE "+ queryString;
        try {
			rs = DBConnection.executeQuery(query);
            
            if (!rs.next() ) {
                System.out.println("No Conference Room found with the entered keyword. Please try a different keyword:");
                showDialogueBox();
                return;
            } else {
                System.out.println("Room No"+"\t" +"Library" +"\t" + "Capacity" + "\t "+ "Floor#" +"\t  ");
                System.out.println("-----------------------------------------------------------------------------------------");

                do {
                	String room_no = rs.getString("room_no");
		            String library_id = rs.getString("library_id");
		            String capacity = rs.getString("capacity");
		            String floor_no = rs.getString("floor_no");
		            System.out.println(room_no +"\t" + library_id +"\t\t" + capacity +"\t\t" + floor_no +"\t" );
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
			System.out.println("1: Reserve the conference room.\t0:Go back to previous menu.");
			while(flag){
					@SuppressWarnings("resource")
					Scanner scanner = new Scanner(System.in);
					String value = scanner.nextLine();
					int choice = Integer.parseInt(value);
					switch(choice){
					case 1:
						System.out.println("Reserving Conference Room");
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
