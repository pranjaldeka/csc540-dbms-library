package com.ncsu.dbms.lib.utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import com.ncsu.dbms.lib.users.Faculty;
import com.ncsu.dbms.lib.users.Student;

public final class Utility {
	public static boolean validateDateFormat(String dateToValdate, String validFormat) {

	    SimpleDateFormat formatter = new SimpleDateFormat(validFormat);
	    //To make strict date format validation
	    formatter.setLenient(false);
	    Date parsedDate = null;
	    try {
	        parsedDate = formatter.parse(dateToValdate);
	    } catch (ParseException e) {
	        //Handle exception
	    	System.out.println("Date Exception!!");
	    }
	    if(parsedDate==null)return false;
	    else return true;
	}
	
	public static String enteredConsoleString() {
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
		String value = scanner.nextLine();
		return value;
		
	}
	public static boolean isNumericIncDash(String str)
	{
	  return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
	}
	public static boolean isNumeric(String str)  
	{  
	  try  
	  {  
	    @SuppressWarnings("unused")
		double d = Double.parseDouble(str);  
	  }  
	  catch(NumberFormatException nfe)  
	  {  
	    return false;  
	  }  
	  return true;  
	}
	public static void setMessage(String message){
		System.out.println(message);
	}
	public static void badErrorMessage(){
		System.out.println("Something bad happened!!! Please try again...");
	}
	public static String getLibraryId(String id){
		if(id.equals("1"))
			return Constant.kHillLibrary;
		else if(id.equals("2"))
			return Constant.kHuntLibrary;
		else
			return null;
	}
	
	public static String getTimeInput() {
		boolean flag = true;
		boolean flagDate = true;
		boolean flagTime = true;
		String return_date = null;
		do{
				String enteredDate = null;
				String enteredTime = null;
				//String validFormat = "yyyy-mm-dd hh:mm:ss";
				String validDateFormat = "yyyy-MM-dd";
				String validTimeFormat = "HH:mm:ss";
				do{
					Utility.setMessage("Please enter date in yyyy-MM-dd format:");
					enteredDate = Utility.enteredConsoleString();
					if(Utility.validateDateFormat(enteredDate, validDateFormat)){
							flagDate = false;
							do{
								Utility.setMessage("Please enter time in HH:mm:ss format:");
								enteredTime = Utility.enteredConsoleString();
								if(Utility.validateDateFormat(enteredTime, validTimeFormat)){
									flagTime = false;
									flag = false;
									return_date = enteredDate + " " + enteredTime;
									return return_date;

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
		return return_date;
	}
	
	public static String getLibraryInput() {
		String library = null;
		boolean flag = true;
		do{
			Utility.setMessage("Please select the Library:");
			Utility.setMessage("1. D.H. Hill \t\t 2. J.B. Hunt");
			 library = Utility.enteredConsoleString();
			if(library.equals("1") || library.equals("2"))
				flag=false;
			else
				setMessage("Invalid input !! Please try again ");
		}
		while(flag);
		return library;
	}
	public static void callUserDialogueBox(String userName, String userType){
		if (userType.equals(Constant.kStudent)) {
			Student student = new Student(userName);
			student.showMenuItems();
		}
		else if (userType.equals(Constant.kFaculty)) {
			Faculty faculty = new Faculty(userName);
			faculty.showMenuItems();
		}
	}

	/**
	 * Method to identify if user wants hard copy or electronic copy
	 * @return
	 */
	public static boolean getDeliveryType() {
		
		boolean isHardCopy = true;
		boolean flag = true;
		String deliveryType = null;
		setMessage("Do you want hard copy or electronic copy?");
		do{
			Utility.setMessage("1. Hard Copy \t\t 2. Electronic copy");
			deliveryType = Utility.enteredConsoleString();
			if(deliveryType.equals("1") || deliveryType.equals("2"))
				flag=false;
			else
				setMessage("Invalid input !! Please try again ");
		}
		while(flag);
		if (deliveryType.equals("2")) {
			isHardCopy = false;
		}
		return isHardCopy;
	}
}
