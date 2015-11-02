package com.ncsu.dbms.lib.resources;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

import oracle.jdbc.OracleTypes;

import com.ncsu.dbms.lib.connection.DBConnection;
import com.ncsu.dbms.lib.exception.PrintSQLException;
import com.ncsu.dbms.lib.utilities.Constant;
import com.ncsu.dbms.lib.utilities.Utility;

public class Camera {
	private String userName;
	private String userType;
	public Camera(String username, String userType) {
		this.userName = username;
		this.userType = userType;
	}

	public void searchCameras() {
		// Searching a camera;
    	
        try {
        	ResultSet rs;
        	CallableStatement cstmt = DBConnection.returnCallableStatememt("{call STUDENT_PUBLICATION_PKG.fetch_cameras_data_proc(?, ?)}");
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
                System.out.println("No cameras found !!");
                Resource sr = new Resource(this.userName, this.userType);
                sr.showPublicationMenuItems();
                return;
            } else {		
                System.out.println("Camera id"+"\t" +"Library" +"\t" + "Make"+"\t  " +"Model" +"\t" + "Lens" +"\t " + "Memory");
                System.out.println("-----------------------------------------------------------------------------------------");

                do {
                	String camera_id = rs.getString("camera_id");
		            String library_id = rs.getString("name");
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
	
	public void displayDialogueAfterSearch(){
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
						checkOutCameraConsole();
						flag = false;
							break;
					case 0:
						System.out.println("Going back to previous menu");
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
	
	private void checkOutCameraConsole(){
		Utility.setMessage("Please enter the Camera ID of camera you want to check out");
		String cameraid = Utility.enteredConsoleString();
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
		
		Utility.setMessage("Please enter return date and time");
		String return_date = Utility.getTimeInput();
		
		checkOutCamera(cameraid,library,return_date);
	}
	

	private  void checkOutCamera(String cameraid, String lib, String return_date) {
		try{
	
			Resource sr = new Resource(this.userName, this.userType);
			sr.checkOutResource(Constant.kCamera, cameraid, Utility.getLibraryId(lib), return_date);
		}
       	catch(SQLException e){
       		PrintSQLException.printSQLException(e);
			Utility.badErrorMessage();
		} 
		Utility.callUserDialogueBox(userName, userType);
	
	}

}

