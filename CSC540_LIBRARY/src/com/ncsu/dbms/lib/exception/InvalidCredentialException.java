package com.ncsu.dbms.lib.exception;

public class InvalidCredentialException extends Exception {
  
	private static final long serialVersionUID = 1L;
	private String strError;
  
   /* Default constructor */
    public InvalidCredentialException () {
            this.strError = "Invalid Credential!!!";
    }
  
  /* Overloaded constructor */
    public InvalidCredentialException (String strError) {
            this.strError = strError;

    }
   /* Overrides toString() function */
    public String toString() {
            return this.strError;
    }
}