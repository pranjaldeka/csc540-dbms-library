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
	LIBRARY_ID 			IN  VARCHAR2,
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
	LIBRARY_ID 			IN  VARCHAR2,
	duedate				IN	VARCHAR2,
  OUTPUT    OUT  VARCHAR2
)
IS
out_message varchar2(20000);
out_message2 varchar2(20000);
is_queue_flag number(2);
sql_statement		varchar2(32000);
BEGIN

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
			check_out_pkg.check_out_proc(issue_type,issue_type_id,user_type,user_id, library_id, duedate,out_message);
			
			OUTPUT := out_message;
			
		end if;
	else
		OUTPUT := 'Sorry! Cameras cannot be renewed!';
	end if;

END renew_proc;
END renew_pkg;
/
SHOW ERRORS