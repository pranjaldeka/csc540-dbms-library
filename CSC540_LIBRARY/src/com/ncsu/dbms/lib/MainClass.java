package com.ncsu.dbms.lib;

import com.ncsu.dbms.lib.connection.DBConnection;
import com.ncsu.dbms.lib.console.LibConsole;

public class MainClass {
    public static void main(String[] args) {
    	DBConnection conn = new DBConnection();
    	LibConsole console = new LibConsole(conn);
    	console.start();
    }
}