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
			PrintSQLException.printSQLException(e);
		}
       
        //conn.close();

	}
	
	public void displayDialogueAfterSearch(){
		boolean flag = true;
		try{
			System.out.println("\nPlease enter your choice:");
			System.out.println("1: Reserve a Camera.\t0:Go back to previous menu.");
			while(flag){
					@SuppressWarnings("resource")
					Scanner scanner = new Scanner(System.in);
					String value = scanner.nextLine();
					int choice = Integer.parseInt(value);
					switch(choice){
					case 1:
						// Call check out method
						reserveCameraConsole();
						flag = false;
							break;
					case 0:
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
	
	private void reserveCameraConsole() {
		Utility.setMessage("Please enter the Camera ID of camera you want to reserve");
		String cameraId = Utility.enteredConsoleString();
		String library = Utility.getLibraryInput();
		
		Utility.setMessage("Please enter date and time when you want to reserve");
		String reserveDate = Utility.getDateInput();
		
		reserveCamera(Utility.getLibraryId(library), cameraId, reserveDate,"0");
	}
	/**
	 * Reserve a camera for user 
	 * 
	 * @param library
	 * @param cameraId
	 * @param reserveTime
	 */
	private void reserveCamera(String library, String cameraId, String reserveDate, String flag) {
		try {
        	CallableStatement cstmt = DBConnection.returnCallableStatememt("{call camera_reserve_pkg.camera_reserve_proc(?,?,?,?,?,?,?,?)}");
        	cstmt.setString(1, cameraId.toUpperCase());
        	cstmt.setString(2, this.userName);
        	cstmt.setString(3, this.userType);
        	cstmt.setString(4, reserveDate);
        	cstmt.setString(5,library);
        	cstmt.setString(6,flag);
	       	cstmt.registerOutParameter(7, OracleTypes.VARCHAR);
	       	cstmt.registerOutParameter(8, OracleTypes.VARCHAR);
	       	System.out.println(cameraId + reserveDate + library );
	       	
	    	cstmt.executeQuery();
	     	String outputMessage = cstmt.getString(7);
	     	String waitFlag = cstmt.getString(8);
	     	if(Integer.parseInt(waitFlag) ==1){// 1. waiting 2. available
	     		// the camera is in waiting list
	     		reserveCameraInWaiting(library,cameraId,reserveDate,outputMessage,"W");
	     	}
	     	else if(Integer.parseInt(waitFlag) ==2){ // available
	     		reserveCameraInWaiting(library,cameraId,reserveDate,outputMessage,"A");
	     	}
	     	else{
	     		Utility.setMessage(outputMessage);

	     	}
//	     	if(outputMessage!=null)
//	       		Utility.setMessage(outputMessage);
//			//Utility.callUserDialogueBox(userName, userType);
	       		displayDialogueAfterSearch();

            }
		catch(SQLException e){
       		PrintSQLException.printSQLException(e);
			Utility.badErrorMessage();
			//Utility.callUserDialogueBox(userName, userType);
			displayDialogueAfterSearch();
		}
	}
	
	private void reserveCameraInWaiting(String library, String cameraId, String reserveDate, String outMessage, String availableFlag) {
		// TODO Auto-generated method stub
		if(availableFlag.equals("A")){
     		Utility.setMessage("The camera is available on the date: " + reserveDate);
     		Utility.setMessage("Please enter your choice : \n1. Reserve the camera.  \t\t0. Go back to previous menu.");
		}
		else{
			Utility.setMessage("The camera is in " + outMessage);
			Utility.setMessage("You can still reserve it or check back later.");
	 		Utility.setMessage("Please enter your choice : \n1. Reserve a camera in waiting list.  \t\t0. Go back to previous menu.");
		}
 		try{
 			boolean flag = true;
 			while(flag){
 					int choice = Integer.parseInt(Utility.enteredConsoleString());
 					switch(choice){
 					case 1:
 						reserveCamera(library, cameraId, reserveDate,"1");
 						flag = false;
 						break;
 					case 0:
 						flag = false;
 						break;
 					default:
 						System.out.println("Invalid choice: Please enter again.");
 					}
 				}
				displayDialogueAfterSearch();

 			}
 			catch(Exception e){
 				Utility.badErrorMessage();
					displayDialogueAfterSearch();
 			}
		
	}

	public void reservedOrCheckoutCameras() {
	}
	
/*	private  void displayDialogueAfterReservedOrCheckout(){
		boolean flag = true;
		try{
			System.out.println("\nPlease enter your choice:");
			System.out.println("1: Check out a camera.\t0:Go back to previous menu.");
			while(flag){
					@SuppressWarnings("resource")
					Scanner scanner = new Scanner(System.in);
					String value = scanner.nextLine();
					int choice = Integer.parseInt(value);
					switch(choice){
					case 1:
						// Call check out method
						checkOutCameraConsole();
						flag = false;
						break;
					case 0:
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
			displayDialogueAfterReservedOrCheckout();
		}
	} 

	private void checkOutCameraConsole(){
		Utility.setMessage("Please enter the Camera ID of camera you want to check out");
		String cameraid = Utility.enteredConsoleString();
		String library = Utility.getLibraryInput();
		
		Utility.setMessage("Please enter return date and time");
		String return_date = Utility.getTimeInput();
		
		checkOutCamera(cameraid,library,return_date);
	}
	*/

	/*private  void checkOutCamera(String cameraid, String lib, String return_date) {
		try{
	
			Resource sr = new Resource(this.userName, this.userType);
			sr.checkOutResource(Constant.kCamera, cameraid, Utility.getLibraryId(lib), return_date);
		}
       	catch(SQLException e){
       		PrintSQLException.printSQLException(e);
			Utility.badErrorMessage();
		} 
		Utility.callUserDialogueBox(userName, userType);
	
	}*/

}

