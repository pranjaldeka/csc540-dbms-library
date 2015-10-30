SET SERVEROUTPUT ON;
CREATE OR REPLACE PACKAGE  check_in_pkg 
IS
PROCEDURE check_in_proc(
    
	issue_type	    	in 	varchar2,
	issue_type_id		in 	varchar2,
	user_type           in  varchar2,
	user_id 			in  varchar2,
	library_id 			in  varchar2,
	output    			out varchar2
);
END check_in_pkg;
/
CREATE OR REPLACE PACKAGE BODY check_in_pkg 
IS
PROCEDURE check_in_proc(
    
	issue_type	    	in 	varchar2,
	issue_type_id		in 	varchar2,
	user_type           in  varchar2,
	user_id 			in  varchar2,
	library_id 			in  varchar2,
	output    			out  varchar2
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
BEGIN
  invalid := 0;
  IF user_type = 'S' 
	  THEN
		 user_table := 'STUDENTS';
		 user_id_column := 'STUDENT_ID';
  ELSIF user_type = 'F'
	  THEN
		 user_table := 'FACULTIES';
		 user_id_column := 'FACULTY_ID';
  ELSE
		 invalid := 1;
  END IF;
  


  IF invalid = 0
  /*Assign table name and its primary to apppropriate variables*/
   THEN
			IF issue_type = 'B' 
            THEN
                table_name := 'BOOKS';
                search_parameter := 'ISBN';
						
			ELSIF issue_type = 'J' 
            THEN
                table_name := 'JOURNALS';
                search_parameter := 'ISSN';
            ELSIF issue_type = 'P' 
            THEN
                table_name := 'CONFERENCE_PAPERS';
                search_parameter := 'CONF_PAPER_ID';
          	ELSIF issue_type = 'C' 
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
					
						 SELECT TO_CHAR
									(SYSDATE, 'YYYY-MM-DD HH24:MI:SS') INTO current_datetime
						 FROM DUAL;
									 
						sql_statement := 
						'UPDATE ssingh25.'||user_table||'_CO_'||table_name||' 
						set return_date=TIMESTAMP'''||current_datetime||'''
						WHERE '||search_parameter|| '='''||issue_type_id||'''
						AND '||user_id_column|| '='''||user_id||'''
						AND  library_id='''||library_id||'''
						AND return_date IS NULL';
						
						/*DBMS_OUTPUT.PUT_LINE(sql_statement);
						*/
						EXECUTE IMMEDIATE sql_statement;
						
						/*update No. of avaialable hard copies*/
						
						sql_statement := 'UPDATE 
						SSINGH25.'||table_name||'_in_libraries T
						SET NO_OF_HARDCOPIES =NO_OF_HARDCOPIES+1
						WHERE T.library_id= '''||library_id||''' 
						AND T.'||search_parameter|| '='''||issue_type_id||'''';
						/*DBMS_OUTPUT.PUT_LINE(sql_statement);
						*/
						EXECUTE IMMEDIATE sql_statement;
					
						COMMIT;
						output :=  'CHECK IN SUCCESSFUL';
        
			ELSE
						output := 'INVALID PARAMETERS';
        
			END IF;	
			
	ELSE
			output := 'INVALID PARAMETERS';
       
	END IF;
  
  EXCEPTION
	WHEN NO_DATA_FOUND THEN
    /*DBMS_OUTPUT.PUT_LINE('Invalid Inputs');*/
		 output := 'CHECKIN UNCSUCCESSFUL';
	WHEN OTHERS THEN
		 output := SQLERRM;	
END check_in_proc;

END check_in_pkg;