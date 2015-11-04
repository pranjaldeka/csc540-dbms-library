/***********************************************************************************
renew_pkg.sql : Renew a resource if no one in queue for that resource for that library
*************************************************************************************/
set serveroutput on;

CREATE OR REPLACE PACKAGE renew_pkg AS 
user_error	EXCEPTION;

PROCEDURE renew_proc(
	issue_type	    	IN 	VARCHAR2,
	issue_type_id		IN 	VARCHAR2,
	USER_TYPE           IN  VARCHAR2,
	USER_ID 			IN  VARCHAR2,
	duedate				IN	VARCHAR2,
	OUTPUT    OUT           VARCHAR2
);
END renew_pkg;
/
CREATE OR REPLACE PACKAGE BODY renew_pkg 
IS

PROCEDURE renew_proc(
    
	ISSUE_TYPE	    	IN 	VARCHAR2,
	ISSUE_TYPE_ID		IN 	VARCHAR2,
	USER_TYPE           IN  VARCHAR2,
	USER_ID 			IN  VARCHAR2,
	duedate				IN	VARCHAR2,
  OUTPUT    OUT  VARCHAR2
)
IS
out_message varchar2(20000);
out_message2 varchar2(20000);
is_queue_flag number(2);
sql_statement		varchar2(32000);
sql_step		varchar2(32000):='';
v_library_id    VARCHAR2(50):='';
v_primary_id   VARCHAR2(50):='';
user_id_column 		varchar2(100);
table_name 			varchar2(100);
user_table 			varchar2(100);
search_parameter 	VARCHAR2(50);

BEGIN

		sql_step := 'Fetching library id for renew.';
			 IF USER_TYPE = 'S' 
			  THEN
				 user_table := 'STUDENTS';
				 user_id_column := 'STUDENT_ID';
			  ELSIF USER_TYPE = 'F'
				  THEN
					 user_table := 'FACULTIES';
					 user_id_column := 'FACULTY_ID';
			  END IF;

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
	            END IF;

	             sql_statement := 
		          '
		          SELECT '||user_id_column||' 
		          FROM '||user_table||'
		          WHERE user_id = '''||user_id||'''';
		           EXECUTE IMMEDIATE sql_statement INTO v_primary_id;

			BEGIN
				  sql_statement := 'SELECT library_id FROM ssingh25.'||user_table||'_CO_'||table_name||' 
		            where '||search_parameter|| '='''||ISSUE_TYPE_ID||'''
		            and '||user_id_column|| '='''||v_primary_id||'''
		            and return_date is null
		            ';
            DBMS_OUTPUT.PUT_LINE(sql_statement) ;
            EXECUTE IMMEDIATE sql_statement INTO v_library_id ;
             DBMS_OUTPUT.PUT_LINE(v_library_id) ;
			EXCEPTION
	        WHEN NO_DATA_FOUND THEN
	        OUTPUT:= 'Library id not found!';

			END;
	/*check if not camera*/
	if ISSUE_TYPE <> 'C'
	then
		/*chk if no one in q*/
		/*if yes call checkin and checkout procs*/
		/*else cant renew bcz someone waiting in q*/
		sql_statement := '
			select count(1) 
			from RESOURCES_QUEUE
			where RESOURCE_ID = '''||ISSUE_TYPE_ID||'''
			and RESOURCE_TYPE = '''||ISSUE_TYPE||'''
			and library_id = '''||v_library_id||'''
		';
		BEGIN
			/*DBMS_OUTPUT.PUT_LINE(sql_statement || ' ' || is_queue_flag);*/
			EXECUTE IMMEDIATE sql_statement INTO is_queue_flag;
			/*DBMS_OUTPUT.PUT_LINE(sql_statement || ' ' || is_queue_flag);*/
			EXCEPTION
			WHEN NO_DATA_FOUND THEN
						is_queue_flag := 0;
						/*DBMS_OUTPUT.PUT_LINE('yessss'||is_queue_flag);*/
			WHEN OTHERS THEN
						/*DBMS_OUTPUT.PUT_LINE('nooooo'||is_queue_flag);*/
						output :=SQLERRM;	
		END;
		
		if is_queue_flag > 0
		then
			OUTPUT := 'Sorry. Cannot Renew. There is already a Queue for this resource!';
		else
			/*call checkin proc*/
			CHECK_IN_PKG.CHECK_IN_PROC(issue_type,issue_type_id,user_type,user_id,out_message2);
			/*OUTPUT := 'checkin ' || out_message2;*/
			
			/*call checkout proc*/

          
			
			DBMS_OUTPUT.PUT_LINE(issue_type||issue_type_id||user_type||user_id||v_library_id||duedate||out_message);
			check_out_pkg.check_out_proc(issue_type,issue_type_id,user_type,user_id, v_library_id, duedate,out_message);
			DBMS_OUTPUT.PUT_LINE(out_message);

			OUTPUT := out_message;
			
		end if;
	else
		OUTPUT := 'Sorry! Cameras cannot be renewed!';
	end if;

END renew_proc;
END renew_pkg;
/
SHOW ERRORS