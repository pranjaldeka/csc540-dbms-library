/*********************************************************************
show_dues_pkg.sql : displays checked out resources
**********************************************************************/
set serveroutput on;
CREATE OR REPLACE PACKAGE show_dues_pkg AS 
user_error       EXCEPTION;

PROCEDURE show_dues_proc(
	user_type		     in	varchar2,
	user_id				 in	varchar2,
	p_ref			     OUT SYS_REFCURSOR,
	out_err_msg		     OUT VARCHAR2
);
END show_dues_pkg;
/
CREATE OR REPLACE PACKAGE BODY show_dues_pkg 
IS
PROCEDURE show_dues_proc(
	user_type				in	varchar2,
	user_id					in	varchar2,
	p_ref			        OUT SYS_REFCURSOR,
	out_err_msg		        OUT VARCHAR2
)
IS
	
	sql_statement		varchar2(32000);
	user_id_column 		varchar2(100);	
	user_table 			varchar2(100);
	primary_id 			varchar(100);	
	
BEGIN
			  IF USER_TYPE = 'S' 
			  THEN
					 user_table := 'STUDENTS';
					 user_id_column := 'student_id';
			  ELSIF USER_TYPE = 'F'
				  THEN
					 user_table := 'FACULTIES';
					 user_id_column := 'faculty_id';
			  
			  END IF;
			  sql_statement := 
					  '
					  SELECT '||user_id_column||' 
					  FROM '||user_table||'
					  WHERE user_id = '''||user_id||'''';
					  /*DBMS_OUTPUT.PUT_LINE(sql_statement);*/
					  EXECUTE IMMEDIATE sql_statement INTO primary_id;
			  sql_statement := 
			  '
			    select b.title as name,''book'' as type ,due_start_date,due_in_dollars from patron_dues  pd ,books b
				where b.isbn=resource_id
				and resource_type=''B''
				and patron_id = '''||primary_id||'''
				union all
				select b.title as name,''journal'' as type ,due_start_date,due_in_dollars from patron_dues  pd ,journals b
				where b.issn=resource_id
				and resource_type=''J''
				and patron_id = '''||primary_id||'''
				union all
				select b.title as name,''conference paper'' as type ,due_start_date,due_in_dollars from patron_dues  pd ,conference_papers b
				where b.conf_paper_id=resource_id
				and resource_type=''P''
				and patron_id = '''||primary_id||'''
				union all
				select b.model as name,''camera'' as type ,due_start_date,due_in_dollars from patron_dues  pd ,cameras b
				where b.camera_id=resource_id
				and resource_type=''C''
				and patron_id = '''||primary_id||'''
			  
			  ';
			   		
	OPEN p_ref FOR sql_statement;
	
 EXCEPTION
 WHEN NO_DATA_FOUND THEN
    out_err_msg:='No Dues found!!';
END show_dues_proc;

END show_dues_pkg;
/