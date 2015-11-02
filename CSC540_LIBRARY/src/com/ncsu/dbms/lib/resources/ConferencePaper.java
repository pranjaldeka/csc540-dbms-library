package com.ncsu.dbms.lib.resources;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

import oracle.jdbc.OracleTypes;

import com.ncsu.dbms.lib.resources.Resource;
import com.ncsu.dbms.lib.connection.DBConnection;
import com.ncsu.dbms.lib.exception.PrintSQLException;
import com.ncsu.dbms.lib.utilities.Constant;
import com.ncsu.dbms.lib.utilities.Utility;

public class ConferencePaper {
	private String userName;
	private String userType;
	public ConferencePaper(String userName, String userType){
		this.userName=userName;
		this.userType = userType;
	}
		
	
	public void searchConferencePapers() {
        try {

        	ResultSet rs;
        	CallableStatement cstmt = DBConnection.returnCallableStatememt("{call STUDENT_PUBLICATION_PKG.fetch_conf_papers_data_proc(?, ?)}");
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
                System.out.println("No conference papers found !!");
                Resource sr = new Resource(this.userName, this.userType);
                sr.showPublicationMenuItems();
                return;
            } else {
                System.out.println("Conf paper id"+"\t" +"Conf Name" +"\t" +"Authors"+"\t" +"\t" + "Title"+"\t  " +"Publication Year" + "\t" + "Number of hardcopies");
                System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------------");

                do {
                	String conf_paper_id = rs.getString("conf_paper_id");
		            String conf_name = rs.getString("conf_name");
		            String authors = rs.getString("authors");
		            String year_of_publication = rs.getString("year_of_publication");
		            String title = rs.getString("title");
		            String library = rs.getString("name");
		            String no_of_hardcopies = rs.getString("no_of_hardcopies");
		            String type = rs.getString("has_electronic");
		            System.out.println(conf_paper_id +"\t" + conf_name+"\t\t" + authors  +"\t\t" + title +"\t" + authors +"\t\t" + title +"\t\t" + year_of_publication + "\t\t" + no_of_hardcopies);
                } while (rs.next());
            }
			 displayDialogueAfterSearch();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       
        //conn.close();

	}
	
	public  void displayDialogueAfterSearch(){
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
						checkOutConfPaperConsole();
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
	private void checkOutConfPaperConsole(){
		boolean isHardCopy = Utility.getDeliveryType();
		Utility.setMessage("Please enter the conf id number of conference paper you want to check out");
		String confId = Utility.enteredConsoleString();
		String library = null;
		String return_date = null;
		if (isHardCopy) {
			library = Utility.getLibraryInput();

			Utility.setMessage("Please enter return date and time");
			return_date = Utility.getTimeInput();
		}

		checkOutConfPaper(confId,library,return_date, isHardCopy);	
		
	}
	private  void checkOutConfPaper(String confId, String lib, String return_date, boolean isHardCopy) {
		if (!isHardCopy) {
			Utility.setMessage("Not implemented yet..");
		}
		else {
			try{

				Resource sr = new Resource(this.userName, this.userType);
				sr.checkOutResource(Constant.kConferencePaper, confId, Utility.getLibraryId(lib), return_date);
		       	
			}
	       	catch(SQLException e){
	       		PrintSQLException.printSQLException(e);
				Utility.badErrorMessage();
			}
		}
		Utility.callUserDialogueBox(userName, userType);

	}


	public void checkedOutConferencePapers() {
		 try {
	        	ResultSet rs;
	        	CallableStatement cstmt = DBConnection.returnCallableStatememt("{call checked_out_resource_pkg.checked_out_resources_proc (?, ?, ?, ?, ?)}");
		       	cstmt.setString(1, Constant.kConferencePaper);
		       	cstmt.setString(2, userType);
		       	cstmt.setString(3, userName);
	        	cstmt.registerOutParameter(4, OracleTypes.CURSOR);
		       	cstmt.registerOutParameter(5, OracleTypes.VARCHAR);

		       	ArrayList<Object> arrayList = DBConnection.returnResultSetAndError(cstmt, 4, 5);  
		       	rs = (ResultSet)arrayList.get(0);
	            if (!rs.next() ) {
	                Resource sr = new Resource(this.userName, this.userType);
	                System.out.println("\nYour have not checked out any conference papers recently\n");
	                sr.showPublicationMenuItemsCheckedOut();
	                return;
	            } else {
	            	System.out.println("Conf paper id"+"\t" +"Conf Name" +"\t" +"Authors"+"\t"  +"\t" + "Title"+"\t  " +"Publication Year");
	                System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------------");

	                do {
	                	String conf_paper_id = rs.getString("conf_paper_id");
			            String conf_name = rs.getString("conf_name");
			            String authors = rs.getString("authors");
			            String year_of_publication = rs.getString("year_of_publication");
			            String title = rs.getString("title");
			            System.out.println(conf_paper_id +"\t" + conf_name+"\t\t" + authors  +"\t\t" + title +"\t" + authors +"\t\t" + title +"\t\t" + year_of_publication);
	                } while (rs.next());
	            }
	            displayDialogueAfterCheckedOutResource();
			}catch(SQLException e){
	       		PrintSQLException.printSQLException(e);
				Utility.badErrorMessage();
			}
		 }
	
	private  void displayDialogueAfterCheckedOutResource(){
		boolean flag = true;
		try{
			System.out.println("\nPlease enter your choice:");
			System.out.println("1: Return a conference paper.\t0:Go back to previous menu.");
			while(flag){
					@SuppressWarnings("resource")
					Scanner scanner = new Scanner(System.in);
					String value = scanner.nextLine();
					int choice = Integer.parseInt(value);
					switch(choice){
					case 1:
						System.out.println("Return a conference paper");
						// Call check out method
						checkIncheckInConferencePaperConsole();
						flag = false;
						break;
					case 0:
						System.out.println("Going back to main menu");
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
			displayDialogueAfterCheckedOutResource();
		}
	}
	
	private  void checkIncheckInConferencePaperConsole() {
		Utility.setMessage("Please enter the conference paper id of the conference paper you want to return");
		String confId = Utility.enteredConsoleString();
		checkInConferencePaper(confId);
	}
	
	private  void checkInConferencePaper(String confId) {
		try{
			Resource sr = new Resource(this.userName, this.userType);
			sr.checkInResource(Constant.kConferencePaper, confId);
		}
		catch(SQLException e){
	       		PrintSQLException.printSQLException(e);
				Utility.badErrorMessage();
	    } 	
		Utility.callUserDialogueBox(userName, userType);
	}
}
