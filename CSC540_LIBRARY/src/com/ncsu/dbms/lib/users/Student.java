package com.ncsu.dbms.lib.users;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import com.ncsu.dbms.lib.connection.DBConnection;
import com.ncsu.dbms.lib.console.LibConsole;
import com.ncsu.dbms.lib.exception.PrintSQLException;
import com.ncsu.dbms.lib.login.Login;
import com.ncsu.dbms.lib.resources.Resource;
import com.ncsu.dbms.lib.utilities.Constant;
import com.ncsu.dbms.lib.utilities.Utility;

import oracle.jdbc.OracleTypes;

public class Student extends User {
	public  String userName;
	public Student(String userName, String firstName, String lastName) {
		this.userName = userName;
		System.out.println("*******************Welcome*****************\n");
		System.out.println("\t\t" + firstName + " " + lastName + "!!!");
		System.out.println("\n*******************************************");
		showMenuItems();
	}
	public Student(String userName){
		this.userName = userName;
	}
	public Student(){
		
	}
	public  void showMenuItems() {
		// TODO Auto-generated method stub
		System.out.println("Please select from the below options: ");
		System.out.println("\n1. Profile \t\t 2. Resources");
		System.out.println("3. Checked-Out Resources   4. Resource Request");
		System.out.println("5. Notification\t\t  6. Due-Balance");
		System.out.println("7. Logout");
		selectAnAction();
	}

	public  void selectAnAction() {
		try{
			boolean flag = true;
			while(flag){
					@SuppressWarnings("resource")
					Scanner scanner = new Scanner(System.in);
					String value = scanner.nextLine();
					int choice = Integer.parseInt(value);
					switch(choice){
					case 1:
						//Profile
						showProfile(this.userName);
						flag=false;
							break;
					case 2:
						//Resources
						Resource sr = new Resource(this.userName);
						sr.searchResources();
						flag = false;
						flag = false;
						break;
					case 3:
						//Checked-Out Resources
						System.out.println("Adding a new Student");
						flag = false;
						break;
					case 4:
						//Resource Request
						System.out.println("Deleting a new Student");
						flag = false;
						break;
					case 5:
						//Notifications
						System.out.println("Adding a new Faculty");
						flag = false;
						break;
					case 6:
						//Due-Balances
						System.out.println("Deleting a new Faculty");
						flag = false;
						break;
					case 7:
						//Log Out
						System.out.println("Goodbye!!!");
						// ask for login again
						
						flag = false;
						break;
					default:
						System.out.println("Invalid choice: Please enter again.");
							
					}
				}
		}
			catch(Exception e){
				System.out.println("Something bad happened!!! Please try again...");
				showMenuItems();

			}
		
	
		
	}
	
	public void showProfile(String userId){
		// Searching a book;
        ResultSet rs;
        try {
        	CallableStatement cstmt = DBConnection.con.prepareCall("{call user_profile_pkg.fetch_profile_data_proc(?, ?, ?)}");
      	  
        //	cstmt.registerOutParameter(3, java.sql.Types.VARCHAR);
        	cstmt.registerOutParameter(2, OracleTypes.CURSOR);
        	cstmt.registerOutParameter(3, OracleTypes.VARCHAR);
        	cstmt.setString(1, userId);
        	cstmt.executeQuery();
        	rs = (ResultSet) cstmt.getObject(2);
        	String error = cstmt.getString(3);
        	if(error != null)
        	{
        		System.out.println(error);
        		showMenuItems();
        	}
            if (!rs.next() ) {
                System.out.println("Not a valid user id.");
                return;
            } else {
                System.out.println("Student ID"+"\t" +"User ID" +"\t" + "First Name"+"\t  " +"Last Name" +"\t" + "Phone No." +"\t\t\t" + "Alt. Phone No." +"\t\t" + "Date Of Birth"+"\t\t" + "Address"+"\t\t" + "Nationality"+"\t\t" + "Degree"+"\t\t\t" + "Department");
                System.out.println("-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

                do {
                	String studentid = rs.getString("student_id");
		            String userid = rs.getString("user_id");
		            String firstName = rs.getString("first_name");
		            String lastName = rs.getString("last_name");
		            String sex = rs.getString("sex");
		            String phone = rs.getString("phone_number");
		            String altPhone = rs.getString("alt_phone_number");
		            String dob = rs.getString("dob");
		            String address = rs.getString("address");
		            String nationality = rs.getString("nationality");
		            String degree = rs.getString("degree");
		            String department = rs.getString("name");

		            System.out.println(studentid +"\t" + userid +"\t\t" +
		            firstName +"\t\t" + lastName +"\t" + sex +"\t\t" + phone +
		            altPhone +"\t\t" + dob +"\t" + address +"\t\t" + nationality +
		            degree +"\t\t" + department);


                } while (rs.next());
                showMenuForModifyProfile();
            }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			PrintSQLException.printSQLException(e);
			showMenuForModifyProfile();
			}
       
        //conn.close();

	}
	private void showMenuForModifyProfile(){
		boolean flag = true;
		try{
			System.out.println("\nPlease enter your choice:");
			System.out.println("1: Modify Profile.\t0:Go back to previous menu.");
			while(flag){
					@SuppressWarnings("resource")
					Scanner scanner = new Scanner(System.in);
					String value = scanner.nextLine();
					int choice = Integer.parseInt(value);
					switch(choice){
					case 1:
						System.out.println("Modifying Profile");
						// Call Modify profile method method
						modifyProfileDataMenu();
						
						flag = false;
							break;
					case 0:
						System.out.println("Going back to previous menu");
						showMenuItems();
						flag = false;
						break;
					default:
						System.out.println("Invalid choice: Please enter again.");
							
					}
				}
		}
		catch(Exception e){
			System.out.println("Something bad happened!!! Please try again...");
			showMenuItems();
		}
	}
	private void modifyProfileDataMenu() {
		// TODO Auto-generated method stub
		System.out.println("Please select the field, you want to modify: ");
        System.out.println("1. User ID" +"\t\t" + "2. First Name"+"\t\t" 
		+"3. Last Name\n" + "4. Phone No." +"\t\t"  + "5. Alt. Phone No." +"\t" +
        "6. Date Of Birth\n"+ "7. Address"+"\t\t" + "8. Nationality"+"\t\t" +
		"9. Password\n"+"10. Sex"+"\t\t\t"+"0. Go to the Previous Menu.");
        modifyProfileData();
	}
	private void modifyProfileData() {
		
		int enteredValue = Integer.parseInt(Utility.enteredConsoleString());
		if(enteredValue==6){
			System.out.println("Please enter Date Of Birth in (MM/DD/YYYY) format.");
			dateOfBirthValidation();
		}
		else if(enteredValue==1){
			//user_id
			updateStudentProfileData("user_id", enteredProfileData());
		}
		else if(enteredValue==2){
			//first_name
			updateStudentProfileData("first_name", enteredProfileData());
		}else if(enteredValue==3){
			//last_name
			updateStudentProfileData("last_name", enteredProfileData());
		}else if(enteredValue==4){
			//Phone_no
			phoneNumberValidation("phone_number");
		}else if(enteredValue==5){
			//alt_phone
			phoneNumberValidation("alt_phone_number");
		}else if(enteredValue==7){
			//Address
			updateStudentProfileData("address", enteredProfileData());
		}else if(enteredValue==8){
			//Nationality
			updateStudentProfileData("nationality", enteredProfileData());
		}else if(enteredValue==9){
			//Password
			updateStudentProfileData("password", enteredProfileData());
		}
		else if(enteredValue==10){
			//sex
			updateStudentProfileData("sex", enteredProfileData());
		}
		else if(enteredValue==0){
			showMenuItems();
		}
	}
	private String enteredProfileData() {
		System.out.println("Please enter value:");
		return Utility.enteredConsoleString();		
	}
	private void dateOfBirthValidation(){
		String enteredValue = enteredProfileData();
		if(Utility.validateDateFormat(enteredValue))
		{
			System.out.println("Date is valid");
			//call save functionality
			updateStudentProfileData("dob", enteredValue);
		}else{
			System.out.println("Date format is invalid.");
			dateOfBirthValidation();
			
		}
	}
	private void phoneNumberValidation(String type){
		String valueEntered = enteredProfileData();
		if(Utility.isNumeric(valueEntered))
		{
			System.out.println("Number is valid");
			if(type.equals("phone_number")){
				updateStudentProfileData("phone_number", valueEntered);
			}
			else if(type.equals("alt_phone_number")){
				updateStudentProfileData("alt_phone_number", valueEntered);
			}
		}else{
			System.out.println("Phone number is invalid!!");
			phoneNumberValidation(type);
			
		}
	}
	
	private void updateStudentProfileData(String columnName, String newColumnValue){
        try {
        	CallableStatement cstmt = DBConnection.con.prepareCall("{call user_profile_pkg.update_user_profile_proc(?, ?, ?, ?, ?)}");
        	cstmt.setString(1, Constant.kStudent);
        	cstmt.setString(2, userName);
        	cstmt.setString(3, columnName);
        	cstmt.setString(4, newColumnValue);
        	cstmt.registerOutParameter(5, OracleTypes.VARCHAR);
        	cstmt.executeQuery();
        	String error = cstmt.getString(5);
        	if(error != null)
        	{
        		System.out.println(error);
        		showMenuForModifyProfile();
        	}else
        		System.out.println("Update Successful!!!");
        	showMenuForModifyProfile();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			PrintSQLException.printSQLException(e);
			showMenuForModifyProfile();
			}

	}
//	private void loginUserWindow(){
//		LibConsole login = new LibConsole(DBConnection.con);
//		
//	}

}
