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

public class Journal {
	private String userName;
	private String userType;
	public Journal(String userName, String userType){
		this.userName=userName;
		this.userType = userType;
		
	}

	public void showJournals() {
		try{
	    	ResultSet rs;
        	CallableStatement cstmt = DBConnection.returnCallableStatememt("{call STUDENT_PUBLICATION_PKG.fetch_journals_data_proc(?, ?)}");
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
	       		System.out.println("No journals found !!");
	       		Resource sr = new Resource(this.userName, this.userType);
	       		sr.showPublicationMenuItems();
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
	public  void displayDialogueAfterSearch(){
		boolean flag = true;
		try{
			System.out.println("\nPlease enter your choice:");
			System.out.println("1: Check-out a Journal.\t0:Go back to previous menu.");
			while(flag){
					@SuppressWarnings("resource")
					Scanner scanner = new Scanner(System.in);
					String value = scanner.nextLine();
					int choice = Integer.parseInt(value);
					switch(choice){
					case 1:
						// Call check out method
						checkOutJournalConsole("C");
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
	private void checkOutJournalConsole(String flag){
	/*	boolean isHardCopy = Utility.getDeliveryType();
		Utility.setMessage("Please enter the ISSN number of journal you want to check out");
		String issn = Utility.enteredConsoleString();
		String library = null;
		library = Utility.getLibraryInput();
		String return_date = null;
		if (isHardCopy) {
			Utility.setMessage("Please enter return date and time");
			return_date = Utility.getTimeInput();
		}
*/
		String library = null;
		boolean isHardCopy=false;
		String isbn = null;
		String return_date = null;

			if(flag.equals("R")){
				// renew an old Journal
				Utility.setMessage("Please enter the  ISSN number of journal you want to renew:");
				 isbn = Utility.enteredConsoleString();
				 System.out.println(isbn);
				library = "";
				isHardCopy = true;
				return_date = Utility.getTimeInput();

				renewResource(isbn, return_date, true);
			}
			else if(flag.equals("C")){
				//check out a Journal
				Utility.setMessage("Please enter the ISSN number of journal you want to check out");
				 isbn = Utility.enteredConsoleString();
				 isHardCopy = Utility.getDeliveryType();
				library = Utility.getLibraryInput();
				if (isHardCopy) {
					Utility.setMessage("Please enter return date and time");
					return_date = Utility.getTimeInput();
				}

				checkOutJournal(isbn,library,return_date, isHardCopy);	

			}
	}
	private  void checkOutJournal(String issn, String lib, String return_date, boolean isHardCopy) {
		Resource sr = new Resource(this.userName, this.userType);
		if (!isHardCopy) {
			try{
				sr.checkOutElectronicResource(Constant.kJournal, issn, Utility.getLibraryId(lib));
			}
	       	catch(SQLException e){
	       		PrintSQLException.printSQLException(e);
				Utility.badErrorMessage();
			}
		}
		else {
			try{
				sr.checkOutResource(Constant.kJournal, issn, Utility.getLibraryId(lib), return_date);
			}
	       	catch(SQLException e){
	       		PrintSQLException.printSQLException(e);
				Utility.badErrorMessage();
			}
		} 
		Utility.callUserDialogueBox(userName, userType);

	}
	
	public void checkedOutJournals() {
        try {
        	ResultSet rs;
        	CallableStatement cstmt = DBConnection.returnCallableStatememt("{call checked_out_resource_pkg.checked_out_resources_proc (?, ?, ?, ?, ?)}");
	       	cstmt.setString(1, Constant.kJournal);
	       	cstmt.setString(2, userType);
	       	cstmt.setString(3, userName);
        	cstmt.registerOutParameter(4, OracleTypes.CURSOR);
	       	cstmt.registerOutParameter(5, OracleTypes.VARCHAR);

	       	ArrayList<Object> arrayList = DBConnection.returnResultSetAndError(cstmt, 4, 5);  
	       	rs = (ResultSet)arrayList.get(0);
            if (!rs.next() ) {
                Resource sr = new Resource(this.userName, this.userType);
                System.out.println("\nYour have not checked out any journals recently\n");
                sr.showPublicationMenuItemsCheckedOut();
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
			System.out.println("1: Return a journal. \t2. Renew a Journal. \t0:Go back to main menu.");
			while(flag){
					@SuppressWarnings("resource")
					Scanner scanner = new Scanner(System.in);
					String value = scanner.nextLine();
					int choice = Integer.parseInt(value);
					switch(choice){
					case 1:
						// Call check out method
						checkInJournalConsole();
						flag = false;
						break;
					case 2:
						// Call check out method
						checkOutJournalConsole("R");
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
	
	private  void checkInJournalConsole() {
		Utility.setMessage("Please enter the ISSN number of journal you want to return");
		String issn = Utility.enteredConsoleString();
		checkInJournal(issn);
	}
	
	private  void checkInJournal(String issn) {
		try{
			Resource sr = new Resource(this.userName, this.userType);
			sr.checkInResource(Constant.kJournal, issn);
		}
		catch(SQLException e){
	       		PrintSQLException.printSQLException(e);
				Utility.badErrorMessage();
	    } 	
		Utility.callUserDialogueBox(userName, userType);
	}
	// renew 
		private  void renewResource(String issn, String return_date, boolean isHardCopy) {
			Resource sr = new Resource(this.userName, this.userType);

				try{
					sr.renewResource(Constant.kJournal, issn, return_date);
				}
		       	catch(SQLException e){
		       		PrintSQLException.printSQLException(e);
					Utility.badErrorMessage();
				} 
			Utility.callUserDialogueBox(userName, userType);

		}
}
