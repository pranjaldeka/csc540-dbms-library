
/*********************************************************************
user_profile_pkg.sql : checks user profile whether it exists or not
**********************************************************************/
CREATE OR REPLACE PACKAGE check_out_pkg AS 

PROCEDURE check_out_proc(
  table_name 		IN 	VARCHAR2,
	search_parameter 	IN 	VARCHAR2,
	search_variable		IN	VARCHAR2,
  user_type IN VARCHAR2	
);
END check_out_pkg;
/
CREATE OR REPLACE PACKAGE BODY check_out_pkg 
IS
PROCEDURE check_out_proc(
	table_name 		IN 	VARCHAR2,
	search_parameter 	IN 	VARCHAR2,
	search_variable		IN	VARCHAR2,
  user_type IN VARCHAR2	
)
IS
avail_flag number(2);
sql_statement varchar2(32000);
search_id vachar2(100);
BEGIN
DBMS_OUTPUT.PUT_LINE('Hello from oracle');
  sql_statement := 
	'SELECT 1,'||search_parameter|| 
	' FROM SSINGH25.' || table_name || '	WHERE
		'||search_parameter|| ' like ''%'||search_variable||'%''    
		';
    insert into jolly_dummy values(1,sql_statement);
    commit;
    

    EXECUTE IMMEDIATE sql_statement INTO avail_flag,search_id;
    if avail_flag = 1
    then
    
    else
    end
        DBMS_OUTPUT.PUT_LINE(avail_flag);

   
/*EXCEPTION
	WHEN NO_DATA_FOUND THEN;
		user_type:='';
*/
END check_out_proc;

END check_out_pkg;
/
describe SSINGH25.Stu