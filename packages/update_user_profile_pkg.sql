set serveroutput on;
/*==============================Update_user_profile.pkg ==============================
================================Updates profile of faculty or users==============*/

CREATE OR REPLACE PACKAGE update_user_profile_pkg AS 

PROCEDURE update_user_profile_proc(
	user_type	IN 	VARCHAR2,
	user_id 	IN 	VARCHAR2,
	update_column		IN	VARCHAR2,
	new_column_value		IN VARCHAR2
);
END update_user_profile_pkg;
/

CREATE OR REPLACE PACKAGE BODY update_user_profile_pkg 
IS
PROCEDURE update_user_profile_proc(
	user_type	IN 	VARCHAR2,
	user_id 	IN 	VARCHAR2,
	update_column		IN	VARCHAR2,
	new_column_value		IN VARCHAR2
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
     user_id_column := 'STUDENT_ID';
  ELSIF user_type = 'F'
  THEN
     user_table := 'FACULTIES';
     user_id_column := 'FACULTY_ID';
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
            WHERE '||user_id_column||' = '''||user_id||'''
            AND NOT EXISTS 
                          (
                          SELECT 1
                          FROM USER_VIEW 
                          WHERE USER_ID = '''||new_column_value||'''
                          )';
        ELSE    
            sql_statement := '
            UPDATE SSINGH25.'||user_table||
            ' SET USER_ID = '''||new_column_value||'''
            WHERE '||user_id_column||' = '''||user_id||'''';
            
        END IF;     
        
        DBMS_OUTPUT.PUT_LINE(sql_statement);
        EXECUTE IMMEDIATE sql_statement;
        COMMIT;
        /*  
        IF SQL%NOTFOUND 
          THEN
              DBMS_OUTPUT.PUT_LINE('No such record');
          END IF
          ;
          */
          
  ELSE
        DBMS_OUTPUT.PUT_LINE('Invalid Inputs');
  END IF;

END update_user_profile_proc;

END update_user_profile_pkg;
/