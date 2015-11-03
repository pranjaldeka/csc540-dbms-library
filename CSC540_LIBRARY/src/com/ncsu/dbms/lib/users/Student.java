package com.ncsu.dbms.lib.users;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import com.ncsu.dbms.lib.connection.DBConnection;
import com.ncsu.dbms.lib.exception.PrintSQLException;
import com.ncsu.dbms.lib.resources.Resource;
import com.ncsu.dbms.lib.resources.UserNotification;
import com.ncsu.dbms.lib.utilities.Constant;
import com.ncsu.dbms.lib.utilities.Utility;

import oracle.jdbc.OracleTypes;

public class Student extends User {
	public Student(String userName, String firstName, String lastName) {
		super(userName, firstName, lastName);
		userType = Constant.kStudent;
		showMenuItems();
	}

	public Student(String userName){
		super(userName);
		userType = Constant.kStudent;
	}

	public  void showMenuItems() {
		System.out.println("Please select from the below options: ");
		System.out.println("\n1. Profile \t\t\t\t 2. Resources");
		System.out.println("3. Checked-Out Resources \t\t 4. Notification");
		System.out.println("5. Due-Balance\t\t\t\t 6. Logout");
		selectAnAction();
	}

	public  void selectAnAction() {
		Resource resource = new Resource(this.userName, this.userType);
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
						showProfile();
						flag=false;
						break;
					case 2:
						//Resources
						resource.searchResources();
						flag = false;
						break;
					case 3:
						//Checked-Out Resources
						resource.checkedOutResources();
						flag = false;
						break;
					case 4:
						//Notifications
						UserNotification un = new UserNotification(this.userName, this.userType);
						un.showNotification();
						flag = false;
						break;
					case 5:
						//Due-Balances
						resource.showDues();
						flag = false;
						break;
					case 6:
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
	
	
	protected void showProfile(){
		// Searching a book;
        ResultSet rs;
        try {
        	CallableStatement cstmt = DBConnection.con.prepareCall("{call user_profile_pkg.fetch_profile_data_proc(?, ?, ?, ?)}");
      	  
        	cstmt.setString(1, userName);
        	cstmt.setString(2, userType);
        	cstmt.registerOutParameter(3, OracleTypes.CURSOR);
        	cstmt.registerOutParameter(4, OracleTypes.VARCHAR);
        	cstmt.executeQuery();
        	rs = (ResultSet) cstmt.getObject(3);
        	String error = cstmt.getString(4);
        	if(error != null)
        	{
        		System.out.println(error);
        		showMenuItems();
        	}
            if (!rs.next() ) {
                System.out.println("Not a valid user id.");
                return;
            } else {
                System.out.println("User ID" +"\t\t" + "First Name"+"\t\t" +"Last Name" +"\t" + "Sex" + "\t" 
                              + "Phone No." +"\t" + "Alt. Phone No." +"\t\t" + "Date Of Birth"+"\t\t" 
                		      + "Address"+"\t\t\t\t" + "Nationality"+"\t\t" + "Degree"+"\t\t\t" + "Department");
                System.out.println("-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

                do {
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

		            System.out.println(userid +"\t\t" +
		            firstName +"\t\t" + lastName +"\t\t" + sex +"\t" + phone + "\t\t" + 
		            altPhone +"\t\t\t" + dob +"\t" + address +"\t\t" + nationality + "\t\t" + 
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
	
	protected void modifyProfileDataMenu() {
		// TODO Auto-generated method stub
		System.out.println("Please select the field, you want to modify: ");
        System.out.println("1. User ID" +"\t\t" + "2. First Name"+"\t\t" 
		+"3. Last Name\n" + "4. Phone No." +"\t\t"  + "5. Alt. Phone No." +"\t" +
        "6. Date Of Birth\n"+ "7. Address"+"\t\t" + "8. Nationality"+"\t\t" +
		"9. Password\n"+"10. Sex"+"\t\t\t"+ "11. Password\n" + "0. Go to the Previous Menu.");
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
			updateProfileData("user_id", enteredProfileData());
		}
		else if(enteredValue==2){
			//first_name
			updateProfileData("first_name", enteredProfileData());
		}else if(enteredValue==3){
			//last_name
			updateProfileData("last_name", enteredProfileData());
		}else if(enteredValue==4){
			//Phone_no
			phoneNumberValidation("phone_number");
		}else if(enteredValue==5){
			//alt_phone
			phoneNumberValidation("alt_phone_number");
		}else if(enteredValue==7){
			//Address
			updateProfileData("address", enteredProfileData());
		}else if(enteredValue==8){
			//Nationality
			updateProfileData("nationality", enteredProfileData());
		}else if(enteredValue==9){
			//Password
			updateProfileData("password", enteredProfileData());
		}
		else if(enteredValue==10){
			//sex
			updateProfileData("sex", enteredProfileData());
		}
		else if(enteredValue==11){
			//password
			updateProfileData("password", enteredProfileData());
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
		String validFormat = "MM/dd/yyyy";
		if(Utility.validateDateFormat(enteredValue, validFormat))
		{
			//call save functionality
			updateProfileData("dob", enteredValue);
		}else{
			System.out.println("Date format is invalid.");
			dateOfBirthValidation();
			
		}
	}
	private void phoneNumberValidation(String type){
		String valueEntered = enteredProfileData();
		if(Utility.isNumeric(valueEntered))
		{
			if(type.equals("phone_number")){
				updateProfileData("phone_number", valueEntered);
			}
			else if(type.equals("alt_phone_number")){
				updateProfileData("alt_phone_number", valueEntered);
			}
		}else{
			System.out.println("Phone number is invalid!!");
			phoneNumberValidation(type);
			
		}
	}
	

}
