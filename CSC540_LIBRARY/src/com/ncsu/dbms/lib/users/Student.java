package com.ncsu.dbms.lib.users;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import com.ncsu.dbms.lib.connection.DBConnection;
import com.ncsu.dbms.lib.utilities.SearchResource;

public class Student extends User {
	@SuppressWarnings("unused")
	private String userName;
	public Student(String userName, String firstName, String lastName) {
		this.userName = userName;
		System.out.println("*******************Welcome*****************\n");
		System.out.println("\t\t" + firstName + " " + lastName + "!!!");
		System.out.println("\n*******************************************");
		showMenuItems();
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
						Student s = new Student();
						s.showProfile(userName);
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
    	String query;
    	query = "SELECT "+
    			"STUDENT_ID         ,"+
    			"USER_ID            ,"+
    			"FIRST_NAME         ,"+
    			"LAST_NAME          ,"+
    			"SEX                          ,"+
    			"PHONE_NUMBER                ,"+
    			"ALT_PHONE_NUMBER            ,"+
    			"DOB                ,"+
    			"ADDRESS                    ,"+
    			"NATIONALITY                 ,"+
    			"DEGREE_TYPE_ID                ,"+
    			"DEPT_ID "+
    			"FROM studnets WHERE user_id = "+userId;
        try {
			rs = DBConnection.executeQuery(query);
            
            if (!rs.next() ) {
                System.out.println("Not a valid user id.");
                return;
            } else {
                System.out.println("Student ID"+"\t" +"User ID" +"\t" + "First Name"+"\t  " +"Last Name" +"\t" + "Phone No." +"\t\t\t" + "Alt. Phone No." +"\t\t" + "Date Of Birth"+"\t\t" + "Address"+"\t\t" + "Nationality"+"\t\t" + "Degree"+"\t\t\t" + "Department");
                System.out.println("-----------------------------------------------------------------------------------------");

                do {
                	String isbn = rs.getString("isbn");
		            String title = rs.getString("title");
		            String authors = rs.getString("authors");
		            String yearOfPub = rs.getString("year_of_publication");
		            String edition = rs.getString("edition");
		            String publisher = rs.getString("publisher");
		            System.out.println(isbn +"\t" + publisher +"\t\t" + edition +"\t\t" + yearOfPub +"\t" + authors +"\t\t" + title);
                } while (rs.next());
            }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       
        //conn.close();

	}
	

}
