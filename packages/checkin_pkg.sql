SET SERVEROUTPUT ON;
CREATE OR REPLACE PACKAGE  CHECK_IN_PKG 
IS
PROCEDURE CHECK_IN_PROC(
    
	issue_type	    	in 	varchar2,
	issue_type_id		in 	varchar2,
	user_type           in  varchar2,
	user_id 			in  varchar2,	
  OUTPUT    OUT VARCHAR2
);
END CHECK_IN_PKG;
/
CREATE OR REPLACE PACKAGE BODY CHECK_IN_PKG 
IS
PROCEDURE CHECK_IN_PROC(
    
	issue_Type	    	In 	Varchar2,
	issue_Type_Id		In 	Varchar2,
	user_Type           In  Varchar2,
	user_Id 			In  Varchar2,
  output    Out  Varchar2
)
IS
avail_flag 			number(2);
sql_statement		varchar2(32000);
user_id_column 		varchar2(100);
table_name 			varchar2(100);
user_table 			varchar2(100);
current_datetime 	varchar2(100);
invalid 			number(2);
search_parameter 	VARCHAR2(50);
primary_id VARCHAR2(100);
library_id 	VARCHAR2(50);
BEGIN
  invalid := 0;
  IF USER_TYPE = 'S' 
	  THEN
		 user_table := 'STUDENTS';
		 user_id_column := 'STUDENT_ID';
  ELSIF USER_TYPE = 'F'
	  THEN
		 user_table := 'FACULTIES';
		 user_id_column := 'FACULTY_ID';
  ELSE
		 invalid := 1;
  END IF;
  


  IF invalid = 0
  /*Assign table name and its primary to apppropriate variables*/
   THEN
			      IF ISSUE_TYPE = 'B' 
            THEN
                table_name := 'BOOKS';
                search_parameter := 'ISBN';
						ELSIF ISSUE_TYPE = 'J' 
            THEN
                table_name := 'JOURNALS';
                search_parameter := 'ISSN';
            ELSIF ISSUE_TYPE = 'P' 
            THEN
                table_name := 'CONFERENCE_PAPERS';
                search_parameter := 'CONF_PAPER_ID';
          	ELSIF ISSUE_TYPE = 'C' 
            THEN
                table_name := 'CAMERAS';
                search_parameter := 'CAMERA_ID';
						ELSE
                invalid :=1;
            END IF;
						
            				
				
     IF  invalid = 0
			THEN
    	/*
				#######################CHECKIN TRANSACTION#######################;
				*/
					/*find student id or facultyid */
          sql_statement := 
          '
          SELECT '||user_id_column||' 
          FROM '||user_table||'
          WHERE user_id = '''||user_id||'''';
          
          EXECUTE IMMEDIATE sql_statement INTO primary_id;
          
          
          SELECT TO_CHAR
						(SYSDATE, 'YYYY-MM-DD HH24:MI:SS') into current_datetime
						 FROM DUAL;
             
						  /*find library id*/
            sql_statement := 'SELECT library_id FROM ssingh25.'||user_table||'_CO_'||table_name||' 
            where '||search_parameter|| '='''||ISSUE_TYPE_ID||'''
            and '||user_id_column|| '='''||primary_id||'''
            and return_date is null
            ';
            DBMS_OUTPUT.PUT_LINE(sql_statement) ;
            EXECUTE IMMEDIATE sql_statement INTO library_id ;
             DBMS_OUTPUT.PUT_LINE(library_id) ;

						sql_statement := 
            'update ssingh25.'||user_table||'_CO_'||table_name||' 
            set return_date=TIMESTAMP'''||current_datetime||'''
            where '||search_parameter|| '='''||ISSUE_TYPE_ID||'''
            and '||user_id_column|| '='''||primary_id||'''
            and return_date is null
            ';
						
						/*DBMS_OUTPUT.PUT_LINE(sql_statement) ;
						*/
						EXECUTE IMMEDIATE sql_statement ;
						
           
						
            
						/*update No. of avaialable hard copies*/
						
						sql_statement := 'UPDATE 
						SSINGH25.'||table_name||'_in_libraries T
						SET NO_OF_HARDCOPIES =NO_OF_HARDCOPIES+1
						WHERE T.LIBRARY_ID= '''||LIBRARY_ID||''' 
						and T.'||search_parameter|| '='''||ISSUE_TYPE_ID||'''';
						/*DBMS_OUTPUT.PUT_LINE(sql_statement);
						*/
            EXECUTE IMMEDIATE sql_statement;
					
					COMMIT;
				OUTPUT := 'CHECK IN SUCCESSFUL';
        
    
	ELSE
        OUTPUT := 'INVALID PARAMETERS';
        /*DBMS_OUTPUT.PUT_LINE('BOOK UNAVAILABLE/ ALREADY ISSUED TO THE USER');*/
	END IF;	
			
	ELSE
        OUTPUT := 'INVALID PARAMETERS';
        /*DBMS_OUTPUT.PUT_LINE('BOOK UNAVAILABLE/ ALREADY ISSUED TO THE USER');*/
  END IF;
  /*DBMS_OUTPUT.PUT_LINE(avail_flag);
  */
  EXCEPTION
	WHEN NO_DATA_FOUND THEN
    /*DBMS_OUTPUT.PUT_LINE('Invalid Inputs');*/
		 OUTPUT := 'CHECKIN UNCSUCCESSFUL';
  WHEN OTHERS THEN
		output :=SQLERRM;
END CHECK_IN_proc;

END CHECK_IN_pkg;