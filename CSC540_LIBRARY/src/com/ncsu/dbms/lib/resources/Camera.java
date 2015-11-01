package com.ncsu.dbms.lib.resources;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

import oracle.jdbc.OracleTypes;

import com.ncsu.dbms.lib.connection.DBConnection;
import com.ncsu.dbms.lib.exception.PrintSQLException;
import com.ncsu.dbms.lib.users.Student;
import com.ncsu.dbms.lib.utilities.Constant;
import com.ncsu.dbms.lib.utilities.Utility;

public class Camera {
	private String userName;
	private String userType;
	public Camera(String username, String userType) {
		//showDialogueBox();
		this.userName = username;
		this.userType = userType;
	}
	public void showDialogueBox(){
		
		/*System.out.println("Please enter a keyword(model or make or id number):");
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
		String keyword = scanner.nextLine();
		String queryString = new StringBuilder().append("\'").append("%").append(keyword).append("%").append("\'").toString().toUpperCase();
		searchCamera(queryString);*/
		searchCamera("");
	}

	private void searchCamera(String queryString) {
		// Searching a camera;
    	
        try {
        	/*CallableStatement cstmt = DBConnection.con.prepareCall("{call student_book_pkg.fetch_books_data_proc(?, ?)}");
	       	 ResultSet rs; 
	       	cstmt.registerOutParameter(1, OracleTypes.CURSOR);
	       	cstmt.registerOutParameter(2, OracleTypes.VARCHAR);
	       	cstmt.executeQuery();
	       	rs = (ResultSet) cstmt.getObject(1);
	       	String error = cstmt.getString(2); */
        	ResultSet rs;
        	CallableStatement cstmt = DBConnection.returnCallableStatememt("{call STUDENT_PUBLICATION_PKG.fetch_cameras_data_proc(?, ?)}");
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
                System.out.println("No cameras found with the entered keyword. Please try a different keyword:");
                Resource sr = new Resource(this.userName, this.userType);
                sr.showPublicationMenuItems();
                return;
            } else {		
		    	/*query = "SELECT camera_id, " +
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
		            } else {*/
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
		
		flag = true;
		boolean flagDate = true;
		boolean flagTime = true;
		do{
				String return_date = null;
				String enteredDate = null;
				String enteredTime = null;
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
									System.out.println(cameraid + " "+ library + " " + return_date);
									checkOutCamera(cameraid,library,return_date);	
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
	

	private  void checkOutCamera(String cameraid, String lib, String return_date) {
		try{
	
			Resource sr = new Resource(this.userName, this.userType);
			sr.checkOutResource(Constant.kCamera, cameraid, Utility.getLibraryId(lib), return_date);
	       	callStudentDialogueBox();
	       	
		}
	       	catch(SQLException e){
	       		PrintSQLException.printSQLException(e);
				Utility.badErrorMessage();
	       		callStudentDialogueBox();
			} 	
	
	}

	private void callStudentDialogueBox(){
		Student s = new Student(this.userName);
		s.showMenuItems();
	}
}

