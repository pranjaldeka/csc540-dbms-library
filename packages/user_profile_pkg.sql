/*********************************************************************
user_profile_pkg.sql : checks user profile whether it exists or not
**********************************************************************/
CREATE OR REPLACE PACKAGE user_profile_pkg AS 

PROCEDURE validate_user_proc(
	user_name 		IN 	NUMBER,
	user_password 	IN 	VARCHAR2,
	user_type		OUT	VARCHAR2,
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
		user_password = user_password
	GROUP BY
	first_name,last_name,user_type;
EXCEPTION
	WHEN NO_DATA_FOUND THEN
		user_type:='';
END validate_user_proc;

END user_profile_pkg;
/