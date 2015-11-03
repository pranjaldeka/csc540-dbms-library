package com.ncsu.dbms.lib.resources;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.ncsu.dbms.lib.connection.DBConnection;
import com.ncsu.dbms.lib.exception.PrintSQLException;
import com.ncsu.dbms.lib.utilities.Constant;
import com.ncsu.dbms.lib.utilities.Utility;

import oracle.jdbc.OracleTypes;

public class UserNotification {

	private String userName;
	private String userType;
	public UserNotification(String userName, String userType){
		this.userName = userName;
		this.userType = userType;
	}
	public void showNotification(){
        try {
        	ResultSet rs;
        	CallableStatement cstmt = DBConnection.returnCallableStatememt("{call user_notification_pkg.show_notification_proc(?, ?,?,?)}");
        	cstmt.setString(1, this.userType);
        	cstmt.setString(2, this.userName);

        	cstmt.registerOutParameter(3, OracleTypes.CURSOR);
	       	cstmt.registerOutParameter(4, OracleTypes.VARCHAR);
	       	ArrayList<Object> arrayList = DBConnection.returnResultSetAndError(cstmt, 3, 4);
	       	if(!arrayList.get(1).equals(Constant.kBlankString))
	       	{
	       		System.out.println(arrayList.get(1));
				displayDialogue()	;		
	       		return;
	       	}  
	       	rs = (ResultSet)arrayList.get(0);
            if (!rs.next() ) {
            	System.out.println("\n"+ "You do not have any notification!!\n");
    			Utility.callUserDialogueBox(this.userName, this.userType);
                return;
            } else {
            	System.out.println("\n"+ "You have the following notifications!!\n");
                do {
                	String resourceType = rs.getString("R");
		            String resourceName = rs.getString("resource_name");
		            String dueDate = rs.getString("due_date");
		            String reserveDate = rs.getString("checkout_time");
		            System.out.println(resourceType +" " + resourceName+" " + dueDate + " " +  reserveDate);
                } while (rs.next());
    			displayDialogue()	;		

            }
		}catch(SQLException e){
       		PrintSQLException.printSQLException(e);
			Utility.badErrorMessage();
			displayDialogue()	;		
		}

	}
	
	public  void displayDialogue(){
		boolean flag = true;
		try{
			System.out.println("\nPlease enter 0 to go back to previous menu:");
			while(flag){
					int choice = Integer.parseInt(Utility.enteredConsoleString());
					switch(choice){
					case 0:
						Utility.callUserDialogueBox(this.userName, this.userType);
						flag = false;
						break;
					default:
						System.out.println("Invalid choice: Please enter again.");
							
					}
				}
		}
		catch(Exception e){
			System.out.println("Something bad happened!!! Please try again...");
			displayDialogue();
		}
	}
	
}
