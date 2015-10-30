package com.ncsu.dbms.lib.utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public final class Utility {
	public static boolean validateDateFormat(String dateToValdate) {

	    SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
	    //To make strict date format validation
	    formatter.setLenient(false);
	    Date parsedDate = null;
	    try {
	        parsedDate = formatter.parse(dateToValdate);
	    } catch (ParseException e) {
	        //Handle exception
	    	System.out.println("Holy Crap!!");
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
}
