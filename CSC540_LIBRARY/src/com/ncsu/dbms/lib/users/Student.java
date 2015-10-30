package com.ncsu.dbms.lib.users;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import com.ncsu.dbms.lib.connection.DBConnection;
import com.ncsu.dbms.lib.utilities.SearchResource;

import oracle.jdbc.OracleTypes;

public class Student extends User {
	private String userName;
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
						SearchResource.searchResources();
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
						System.out.println("Adding a new Admin");
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
        	System.out.println("userid is " + userId);
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
            }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       
        //conn.close();

	}
	

}
