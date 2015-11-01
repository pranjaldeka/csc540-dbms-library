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

public class Journal {
	String userName;
	public Journal(String userName){
		this.userName=userName;
		
	}
	public void showDialogueBox(){
		
	
		searchJournal("");
	}
	private void searchJournal(String queryString) {
		try{
	    	ResultSet rs;
        	CallableStatement cstmt = DBConnection.returnCallableStatememt("{call STUDENT_PUBLICATION_PKG.fetch_journals_data_proc(?, ?)}");
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
	       		System.out.println("No conference papers found with the entered keyword. Please try a different keyword:");
	       		Resource sr = new Resource(this.userName);
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
						System.out.println("Checking out a Journal");
						// Call check out method
						checkOutJournalConsole();
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
	private void checkOutJournalConsole(){
		Utility.setMessage("Please enter the ISSN number of journal you want to check out");
		String issn = Utility.enteredConsoleString();
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
				String enteredHour = null;
				String enteredTime = null;
				//String validFormat = "yyyy-mm-dd hh:mm:ss";
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
									System.out.println(issn + " "+ library + " " + return_date);
									checkOutConfPaper(issn,library,return_date);	
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
	private  void checkOutConfPaper(String issn, String lib, String return_date) {
		try{

			Resource sr = new Resource(this.userName);
			sr.checkOutResource(Constant.kJournal, issn, Constant.kStudent, this.userName, Utility.getLibraryId(lib), return_date);
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
