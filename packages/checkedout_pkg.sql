/*********************************************************************
checked_out_resource_pkg.sql : displays checked out resources
**********************************************************************/
set serveroutput on;
CREATE OR REPLACE PACKAGE checked_out_resource_pkg AS 
user_error       EXCEPTION;

PROCEDURE checked_out_resources_proc(
	resource_type	    	  in 	varchar2,
	user_type					in	varchar2,
	user_id					in	varchar2,
	p_ref			          OUT SYS_REFCURSOR,
	out_err_msg		          OUT VARCHAR2
);
END checked_out_resource_pkg;
/
CREATE OR REPLACE PACKAGE BODY checked_out_resource_pkg 
IS
PROCEDURE checked_out_resources_proc(
	resource_type	    	  in 	varchar2,
	user_type					in	varchar2,
	user_id					in	varchar2,
	p_ref			          OUT SYS_REFCURSOR,
	out_err_msg		          OUT VARCHAR2
)
IS
	avail_flag 			number(2);
	sql_statement		varchar2(32000);
	user_id_column 		varchar2(100);
	table_name 			varchar2(100);
	user_table 			varchar2(100);
    invalid 			number(2);
	search_parameter 	VARCHAR2(50);
	primary_id varchar(100);	
	
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
	   IF invalid=0
	   THEN
	   	  
			IF resource_type = 'B' 
            THEN
                table_name := 'BOOKS';
                search_parameter := 'ISBN';
			ELSIF resource_type = 'J' 
            THEN
                table_name := 'JOURNALS';
                search_parameter := 'ISSN';
            ELSIF resource_type = 'P' 
            THEN
                table_name := 'CONFERENCE_PAPERS';
                search_parameter := 'CONF_PAPER_ID';
          	ELSIF resource_type = 'C' 
            THEN
                table_name := 'CAMERAS';
                search_parameter := 'CAMERA_ID';
						
            ELSE
                invalid :=1;
            END IF;
		IF invalid = 0
		THEN
			
			  sql_statement := 
			  '
			  SELECT '||user_id_column||' 
			  FROM '||user_table||'
			  WHERE user_id = '''||user_id||'''';
			  /*DBMS_OUTPUT.PUT_LINE(sql_statement);*/
			  EXECUTE IMMEDIATE sql_statement INTO primary_id;
			sql_statement := '
						SELECT 
							T1.*,
							l.name
						FROM
							'||table_name||' T1,
							'||user_table||'_CO_'||table_name||' T2,
							libraries l
						WHERE
							T1.'||search_parameter||'=T2.'||search_parameter||'
							AND '||user_id_column||' = '''||primary_id||'''
							AND T2.library_id=l.library_id
							and return_date is null';
							
						DBMS_OUTPUT.PUT_LINE(sql_statement);
		ELSE
		out_err_msg:='Invalid parameters!!';
		END IF;
		
			
	OPEN p_ref FOR sql_statement;
	ELSE
	out_err_msg:='Invalid parameters!!';
	END IF;
 EXCEPTION
 WHEN NO_DATA_FOUND THEN
    out_err_msg:='No Books found!!';
END checked_out_resources_proc;

END checked_out_resource_pkg;
/