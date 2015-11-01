package com.ncsu.dbms.lib.utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import oracle.jdbc.Const;

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
	public static void welcomeMessage(String message){
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
}
