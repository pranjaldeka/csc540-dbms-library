SET SERVEROUTPUT ON;
CREATE OR REPLACE PACKAGE  CHECK_OUT_PKG 
IS

PROCEDURE CHECK_OUT_PROC(
    
	issue_type	    	IN 	VARCHAR2,
	issue_type_id		IN 	VARCHAR2,
	USER_TYPE           IN  VARCHAR2,
	USER_ID 			IN  VARCHAR2,
	LIBRARY_ID 			IN  VARCHAR2,
	duedate				IN	VARCHAR2,
	OUTPUT    OUT           VARCHAR2
);
END CHECK_OUT_PKG;
/
CREATE OR REPLACE PACKAGE BODY CHECK_OUT_PKG 
IS
PROCEDURE CHECK_OUT_PROC(
    
	ISSUE_TYPE	    	IN 	VARCHAR2,
	ISSUE_TYPE_ID		IN 	VARCHAR2,
	USER_TYPE           IN  VARCHAR2,
	USER_ID 			IN  VARCHAR2,
	LIBRARY_ID 			IN  VARCHAR2,
	duedate				IN	VARCHAR2,
  OUTPUT    OUT  VARCHAR2
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
primary_id varchar2(100);
valid_duration varchar2(200);
duedate_timestamp timestamp;
validdate_timestamp timestamp;
currentdate_timestamp timestamp;
BEGIN
  invalid := 0;
  IF USER_TYPE = 'S' 
	  THEN
		 user_table := 'STUDENTS';
		 user_id_column := 'student_id';
  ELSIF USER_TYPE = 'F'
	  THEN
		 user_table := 'FACULTIES';
		 user_id_column := 'faculty_id';
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
          	/*ELSIF ISSUE_TYPE = 'C' 
            THEN
                table_name := 'CAMERAS';
                search_parameter := 'CAMERA_ID';
						*/
            ELSE
                invalid :=1;
            END IF;
						
					
				
        IF  invalid = 0
			THEN
			/*current date time*/
			SELECT TO_CHAR
									(SYSDATE, 'YYYY-MM-DD HH24:MI:SS') into current_datetime
									 FROM DUAL;
		  /*check if due date is valid */
			sql_statement :='
				SELECT DISTINCT VALID_DURATION 
				FROM CHECKOUT_VALID_DURATION
				WHERE USER_TYPE = '''||user_type||''' 
				AND RESOURCE_TYPE = '''||issue_type||'''
						';
            
        DBMS_OUTPUT.PUT_LINE(sql_statement);
			EXECUTE IMMEDIATE sql_statement INTO valid_duration;
			
			sql_statement :='
				SELECT to_timestamp('''||duedate||''',''yyyy-mm-dd hh-mi-ss'') 
				FROM DUAL';
			  DBMS_OUTPUT.PUT_LINE(sql_statement);
			EXECUTE IMMEDIATE sql_statement INTO duedate_timestamp;
			
			sql_statement :='
				SELECT CURRENT_TIMESTAMP + '||valid_duration||'
				FROM DUAL
				';
        DBMS_OUTPUT.PUT_LINE(sql_statement);
			EXECUTE IMMEDIATE sql_statement INTO validdate_timestamp;
			
      SELECT CURRENT_TIMESTAMP INTO currentdate_timestamp 
      FROM dual;
      
		IF duedate_timestamp > validdate_timestamp OR duedate_timestamp < currentdate_timestamp
        THEN
          invalid := 1;
        END IF;
			IF invalid = 0
			
			THEN
						
					/*find student id or facultyid */
					  sql_statement := 
					  '
					  SELECT '||user_id_column||' 
					  FROM '||user_table||'
					  WHERE user_id = '''||user_id||'''';
					  /*DBMS_OUTPUT.PUT_LINE(sql_statement);*/
					  EXECUTE IMMEDIATE sql_statement INTO primary_id;
						  avail_flag :=0;
						  sql_statement := 
								'SELECT 1 
								FROM SSINGH25.' || table_name || ' T1
								inner join 
								SSINGH25.'||table_name||'_in_libraries T2
								ON T1.'||search_parameter|| '=T2.'||search_parameter|| '
								WHERE T2.LIBRARY_ID= '''||LIBRARY_ID||''' 
								and T1.'||search_parameter|| '='''||ISSUE_TYPE_ID||'''
								and not exists
								(
								select 1 
								from ssingh25.'||user_table||'_CO_'||table_name||' 
								where '||search_parameter|| '='''||ISSUE_TYPE_ID||'''
								and '||user_id_column|| '='''||primary_id||'''
								and return_date is null
								)
								and no_of_hardcopies <> 0';
							
							/*DBMS_OUTPUT.PUT_LINE(sql_statement);
					*/
					/*DBMS_OUTPUT.PUT_LINE(avail_flag);*/
							EXECUTE IMMEDIATE sql_statement INTO avail_flag;
							
					IF avail_flag = 1
							THEN
							/*
							#######################CHECKOUT TRANSACTION#######################;
							*/
					  
								
						 
									 
									sql_statement := '
									INSERT INTO '||user_table||'_CO_'||table_name||'
									VALUES (
									'''||primary_id||''',
									'''||ISSUE_TYPE_ID||''',
									'''||LIBRARY_ID||''',
									TIMESTAMP'''||current_datetime||''',
									NULL,
                  TIMESTAMP'''||duedate||'''
									)';
									
									DBMS_OUTPUT.PUT_LINE(sql_statement);
									
									EXECUTE IMMEDIATE sql_statement;
									
									/*update No. of avaialable hard copies*/
									
									sql_statement := 'UPDATE 
									SSINGH25.'||table_name||'_in_libraries T
									SET NO_OF_HARDCOPIES =NO_OF_HARDCOPIES-1
									WHERE T.LIBRARY_ID= '''||LIBRARY_ID||''' 
									and T.'||search_parameter|| '='''||ISSUE_TYPE_ID||'''';
									/*DBMS_OUTPUT.PUT_LINE(sql_statement);
									*/
						EXECUTE IMMEDIATE sql_statement;
								
								COMMIT;
							OUTPUT := 'CHECK OUT SUCCESSFUL';
					
					ELSE
					OUTPUT := 'BOOK UNAVAILABLE/ ALREADY ISSUED TO THE USER';
					END IF;
			ELSE
					OUTPUT := 'INVALID RETURN DATE';
			END IF;
	ELSE
        OUTPUT := 'INVALID PARAMETERS';
        /*DBMS_OUTPUT.PUT_LINE('BOOK UNAVAILABLE/ ALREADY ISSUED TO THE USER');*/
	END IF;	
			
	ELSE
        OUTPUT := 'INVALID PARAMETERS';
        /*DBMS_OUTPUT.PUT_LINE('BOOK UNAVAILABLE/ ALREADY ISSUED TO THE USER');*/
  END IF;
  DBMS_OUTPUT.PUT_LINE(avail_flag);
  EXCEPTION
	WHEN NO_DATA_FOUND THEN
    /*DBMS_OUTPUT.PUT_LINE('Invalid Inputs');*/
		 OUTPUT := 'BOOK UNAVAILABLE/ ALREADY ISSUED TO THE USER';
  WHEN OTHERS THEN
		output :=SQLERRM;   
END check_out_proc;

END check_out_pkg;