/*********************************************************************
user_profile_pkg.sql : checks user profile whether it exists or not
**********************************************************************/
CREATE OR REPLACE PACKAGE user_profile_pkg AS 

PROCEDURE validate_user_proc(
	user_name 		IN 	NUMBER,
	user_password 	IN 	VARCHAR2,
	count_rows		OUT	NUMBER,
	first_name		OUT VARCHAR2,
	last_name		OUT VARCHAR2	
);
END user_profile_pkg;
/
CREATE OR REPLACE PACKAGE BODY user_profile_pkg 
IS
PROCEDURE validate_user_proc(
	user_name 		IN 	NUMBER,
	user_password 	IN 	VARCHAR2,
	count_rows		OUT	NUMBER,
	first_name		OUT VARCHAR2,
	last_name		OUT VARCHAR2		
)
IS

BEGIN

	SELECT 
		COUNT(1), first_name,last_name INTO count_rows, first_name, last_name
	FROM 
		user_view 
	WHERE
		user_id = user_name AND
		user_password = user_password
	GROUP By
	first_name,last_name;
EXCEPTION
	WHEN NO_DATA_FOUND THEN
		--SELECT 0, null,null INTO count_rows, first_name, last_name FROM dual;
		count_rows:=0;
END validate_user_proc;

END user_profile_pkg;
/