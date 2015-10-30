package com.ncsu.dbms.lib.resources;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import com.ncsu.dbms.lib.connection.DBConnection;
import com.ncsu.dbms.lib.users.Student;

public class Camera {
	public Camera() {
		showDialogueBox();
	}
	public static void showDialogueBox(){
		
		System.out.println("Please enter a keyword(model or make or id number):");
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
		String keyword = scanner.nextLine();
		String queryString = new StringBuilder().append("\'").append("%").append(keyword).append("%").append("\'").toString().toUpperCase();
		searchCamera(queryString);
	}

	private static void searchCamera(String queryString) {
		// Searching a book;
        ResultSet rs;
    	String query;
    	query = "SELECT camera_id, " +
    	"library_id, "+
    	"make, "+
    	"model, "+
    	"lens_config, "+
    	"memory_available "+
    	"FROM cameras "+
    	"WHERE upper(camera_id) LIKE "+ queryString +
    	" OR upper(model) LIKE "+ queryString +
    	" OR upper(make) LIKE "+ queryString;
        try {
			rs = DBConnection.executeQuery(query);
            
            if (!rs.next() ) {
                System.out.println("No Camera found with the entered keyword. Please try a different keyword:");
                showDialogueBox();
                return;
            } else {
                System.out.println("Camera id"+"\t" +"Library" +"\t" + "Make"+"\t  " +"Model" +"\t" + "Lens" +"\t " + "Memory");
                System.out.println("-----------------------------------------------------------------------------------------");

                do {
                	String camera_id = rs.getString("camera_id");
		            String library_id = rs.getString("library_id");
		            String make = rs.getString("make");
		            String model = rs.getString("model");
		            String lens_config = rs.getString("lens_config");
		            String memory_available = rs.getString("memory_available");
		            System.out.println(camera_id +"\t" + library_id +"\t\t" + make +"\t\t" + model +"\t" + lens_config +"\t\t" + memory_available);
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
			System.out.println("1: Check-out a Camera.\t0:Go back to previous menu.");
			while(flag){
					@SuppressWarnings("resource")
					Scanner scanner = new Scanner(System.in);
					String value = scanner.nextLine();
					int choice = Integer.parseInt(value);
					switch(choice){
					case 1:
						System.out.println("Checking out a Camera");
						// Call check out method
						flag = false;
							break;
					case 0:
						System.out.println("Going back to previous menu");
						Student.showMenuItems();
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
