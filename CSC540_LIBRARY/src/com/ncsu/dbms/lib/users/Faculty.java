package com.ncsu.dbms.lib.users;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import oracle.jdbc.OracleTypes;

import com.ncsu.dbms.lib.connection.DBConnection;
import com.ncsu.dbms.lib.exception.PrintSQLException;
import com.ncsu.dbms.lib.resources.Resource;
import com.ncsu.dbms.lib.resources.UserNotification;
import com.ncsu.dbms.lib.utilities.Constant;
import com.ncsu.dbms.lib.utilities.Utility;

public class Faculty extends User {

	public Faculty(String userName, String firstName, String lastName) {
		super(userName, firstName, lastName);
		userType = Constant.kFaculty;
		showMenuItems();

	}
	
	public Faculty(String userName){
		super(userName);
		userType = Constant.kFaculty;
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
                System.out.println("User ID" +"\t\t" + "First Name"+"\t  " +"Last Name" +"\t" + "Category" +"\t\t" + "Nationality" +"\t\t" + "Department Name");
                System.out.println("-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

                do {
		            String userid = rs.getString("user_id");
		            String firstName = rs.getString("first_name");
		            String lastName = rs.getString("last_name");
		            String category = rs.getString("category");
		            String nationality = rs.getString("nationality");
		            String deptName = rs.getString("name");

		            System.out.println(userid +"\t\t" +
		            firstName +"\t\t" + lastName +"\t\t" + category +"\t\t" + nationality + "\t\t" + 
		            deptName);


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
		System.out.println("Please select the field, you want to modify: \n");
        System.out.println("1. User ID" +"\t\t" + "2. First Name"+"\t\t" 
		+"3. Last Name\n" + "5. Password" + "\t\t" + "5. Category" + "\t\t" + "6. Nationality"+"\t\t" +
		 "\t\t\t"+"0. Go to the Previous Menu.");
        modifyProfileData();
	}
	private void modifyProfileData() {
		
		int enteredValue = Integer.parseInt(Utility.enteredConsoleString());
		if(enteredValue==1){
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
			//nationality
			updateProfileData("password", enteredProfileData());
		}else if(enteredValue==5){
			//Category
			updateProfileData("category", enteredProfileData());
		}else if(enteredValue==6){
			//nationality
			updateProfileData("nationality", enteredProfileData());
		}
		else if(enteredValue==0){
			showMenuItems();
		}
	}

	private String enteredProfileData() {
		System.out.println("Please enter value:");
		return Utility.enteredConsoleString();		
	}

}
