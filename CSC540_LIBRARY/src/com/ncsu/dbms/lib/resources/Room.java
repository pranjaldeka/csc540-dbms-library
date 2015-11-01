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

public class Room {
	String userName;
	public Room(String userName){
		this.userName = userName;
	}
	
	public void showDialogueBox(){
		String library = null;
		boolean flag = true;
		do{
			Utility.setMessage("Please select the Library:");
			Utility.setMessage("1. D.H. Hill \t\t 2. J.B. Hunt");
			 library = Utility.enteredConsoleString();
			if(library.equals("1") || library.equals("2"))
				flag=false;
			else
				System.out.println("Oops..Invalid entry !! Please try again");
		}
		while(flag);

		String inputCapacity = null;
		flag = true;
		do{
			Utility.setMessage("Please enter capacity ");
			inputCapacity = Utility.enteredConsoleString();
			if(inputCapacity.matches("-?\\d+(\\.\\d+)?"))
				flag=false;
			else
				System.out.println("Oops..Invalid entry !! Please try again");
		}
		while(flag);
		
		try {
        	ResultSet rs;
        	CallableStatement cstmt = DBConnection.returnCallableStatememt("{call USER_ROOM_PKG.user_fetches_rooms_proc(?, ?, ?, ?, ?)}");
        	cstmt.setString(1, Constant.kStudent);
        	cstmt.setString(2, inputCapacity);
        	cstmt.setString(3, Utility.getLibraryId(library));
        	cstmt.registerOutParameter(4, OracleTypes.CURSOR);
	       	cstmt.registerOutParameter(5, OracleTypes.VARCHAR);
	       	ArrayList<Object> arrayList = DBConnection.returnResultSetAndError(cstmt, 4, 5);
	       	System.out.println(arrayList.get(0));
	       	System.out.println(arrayList.get(1));
	       	if(!arrayList.get(1).equals(Constant.kBlankString))
	       	{
	       		System.out.println(arrayList.get(1));
	            Resource sr = new Resource(this.userName);
                sr.searchResources();
	       		return;
	       	}  
	       	rs = (ResultSet)arrayList.get(0);
            if (!rs.next() ) {
                System.out.println("No Rooms found with the input criteria. Please try again!!\n");
                Resource sr = new Resource(this.userName);
                sr.searchResources();
                return;
            } else {
                System.out.println("Library"+"\t\t" +"Room No" +"\t\t" +"Floor No"+"\t" +"Capacity" +"\t" + "Room Type");
                System.out.println("----------------------------------------------------------------------------------------------------------");

                do {
                	String libraryName = rs.getString("name");
		            String roomNo = rs.getString("room_no");
		            String floorNo = rs.getString("floor_no");
		            String capacity = rs.getString("capacity");
		            String type = rs.getString("room_type");
		            System.out.println(libraryName +"\t" + roomNo+"\t\t" + floorNo  +"\t\t" + capacity + "\t\t" + type);
                } while (rs.next());
            }
			 displayDialogueAfterSearch();
		}catch(SQLException e){
       		PrintSQLException.printSQLException(e);
			Utility.badErrorMessage();
			showDialogueBox();
		}
	}

	public void displayDialogueAfterSearch(){
		boolean flag = true;
		try{
			System.out.println("\nPlease enter your choice:");
			System.out.println("1: Reserve the room.\t0:Go back to previous menu.");
			while(flag){
					@SuppressWarnings("resource")
					Scanner scanner = new Scanner(System.in);
					String value = scanner.nextLine();
					int choice = Integer.parseInt(value);
					switch(choice){
					case 1:
						System.out.println("Reserving Room");
						// Call reserve room method
						reserveRoomConsole();
						flag = false;
						break;
					case 0:
						System.out.println("Going back to previous menu\n");
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

	private void reserveRoomConsole() {
		String library = null;
		boolean flag = true;
		do{
			Utility.setMessage("Please select the Library:");
			Utility.setMessage("1. D.H. Hill \t\t 2. J.B. Hunt");
			 library = Utility.enteredConsoleString();
			if(library.equals("1") || library.equals("2"))
				flag=false;
			else
				System.out.println("Oops..Invalid entry !! Please try again");
		}
		while(flag);
	
		String roomNo = null;

		Utility.setMessage("Please enter room no ");
		roomNo = Utility.enteredConsoleString();

		
		Utility.setMessage("Please enter start time ");
		String startTime = Utility.getTimeInput();

		Utility.setMessage("Please enter end time ");
		String endTime = Utility.getTimeInput();
		
		Resource resource = new Resource(userName);
		try {
			resource.reserveRoom(Constant.kStudent, library, roomNo, startTime, endTime);
		} catch (SQLException e) {
			PrintSQLException.printSQLException(e);
			Utility.badErrorMessage();
			Utility.setMessage("Failed to reserve a room. Please try again");
			displayDialogueAfterSearch();
		}
		Student s = new Student(this.userName);
		s.showMenuItems();
	}
	
	
}

       