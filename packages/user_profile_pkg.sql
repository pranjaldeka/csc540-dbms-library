/*********************************************************************
user_profile_pkg.sql : checks user profile whether it exists or not
**********************************************************************/
CREATE OR REPLACE PACKAGE user_profile_pkg AS 
--TYPE std_ref_cursor IS REF CURSOR RETURN students%ROWTYPE;
user_error       EXCEPTION;
PROCEDURE validate_user_proc(
	user_name 		IN 	VARCHAR2,
	user_pass 	IN 	VARCHAR2,
	user_type		OUT	VARCHAR2,
	first_name		OUT VARCHAR2,
	last_name		OUT VARCHAR2	
);

PROCEDURE fetch_profile_data_proc(
	user_name 		IN 	VARCHAR2,
	p_ref			OUT SYS_REFCURSOR,
	out_err_msg		OUT VARCHAR2
  );
PROCEDURE update_user_profile_proc(
	user_type	IN 	VARCHAR2,
	user_id 	IN 	VARCHAR2,
	update_column		IN	VARCHAR2,
	new_column_value		IN VARCHAR2,
	out_err_msg		OUT 	VARCHAR2
);
END user_profile_pkg;
/
CREATE OR REPLACE PACKAGE BODY user_profile_pkg 
IS
PROCEDURE validate_user_proc(
	user_name 		IN 	VARCHAR2,
	user_pass 	IN 	VARCHAR2,
	user_type		OUT	VARCHAR2,
	first_name		OUT VARCHAR2,
	last_name		OUT VARCHAR2		
)
IS

BEGIN

	SELECT 
		user_type, first_name,last_name INTO user_type, first_name, last_name
	FROM 
		user_view 
	WHERE
		user_id = user_name AND
		user_password = user_pass
	GROUP BY
	first_name,last_name,user_type;
EXCEPTION
	WHEN NO_DATA_FOUND THEN
		user_type:='';
END validate_user_proc;


PROCEDURE fetch_profile_data_proc(
	user_name 		IN 	VARCHAR2,
	p_ref			OUT SYS_REFCURSOR,
	out_err_msg		OUT VARCHAR2
)
IS

BEGIN
	OPEN p_ref FOR
		SELECT 
			s.student_id  ,       
			s.user_id      ,      
			s.first_name    ,     
			s.last_name      ,    
			s.sex             ,             
			s.phone_number     ,           
			s.alt_phone_number  ,          
			s.dob                ,
			s.address           ,       
			s.nationality      ,       
			dt.degree ,               
			d.name
		FROM 
			students s, departments d, degree_types dt 
		WHERE
			s.dept_id = d.dept_id AND
			s.degree_type_id = dt.degree_type_id AND
			s.user_id = user_name ;
	
  EXCEPTION
 WHEN NO_DATA_FOUND THEN
    out_err_msg:='User Data not found!!';
END fetch_profile_data_proc;
PROCEDURE update_user_profile_proc(
	user_type	IN 	VARCHAR2,
	user_id 	IN 	VARCHAR2,
	update_column		IN	VARCHAR2,
	new_column_value		IN VARCHAR2,
  out_err_msg		OUT 	VARCHAR2

)
IS
sql_statement varchar2(32000);
user_id_column varchar(50);
user_table varchar(50);
invalid number(2);
BEGIN
	  invalid := 0;
	  IF user_type = 'S' 
	  THEN
	     user_table := 'STUDENTS';
	     /*user_id_column := 'STUDENT_ID';*/
	  ELSIF user_type = 'F'
	  THEN
	     user_table := 'FACULTIES';
	     /*user_id_column := 'FACULTY_ID';*/
	  ELSE
	     invalid := 1;
	  END IF;
	  
		/*update the Table*/
	  IF invalid = 0
	  
	  THEN
	     IF update_column = 'USER_ID'
	        THEN
	            sql_statement := '
	            UPDATE SSINGH25.'||user_table||
	            ' SET USER_ID = '''||new_column_value||'''
	            WHERE user_id = '''||user_id||'''
	            AND NOT EXISTS 
	                          (
	                          SELECT 1
	                          FROM USER_VIEW 
	                          WHERE USER_ID = '''||new_column_value||'''
	                          )';
	     ELSE    
	            sql_statement := '
	            UPDATE SSINGH25.'||user_table||
	            ' SET ' || update_column ||' = '''||new_column_value||'''
	            WHERE user_id = '''||user_id||'''';
	            
	        END IF;     
	        
	        DBMS_OUTPUT.PUT_LINE(sql_statement);
	        EXECUTE IMMEDIATE sql_statement;
	        COMMIT;

	          
	  ELSE
	        RAISE user_error;
	  END IF;
EXCEPTION
	WHEN user_error THEN
		out_err_msg:='Invalid Inputs.';
	WHEN OTHERS THEN
		out_err_msg:=SQLERRM;	

END update_user_profile_proc;


END user_profile_pkg;
/