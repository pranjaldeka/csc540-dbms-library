set serveroutput on;

CREATE OR REPLACE PACKAGE patron_in_hold_pkg AS 
user_error	EXCEPTION;

PROCEDURE patron_in_hold_proc;

END patron_in_hold_pkg;
/
CREATE OR REPLACE PACKAGE BODY patron_in_hold_pkg
IS
	PROCEDURE patron_in_hold_proc
	IS
	
	BEGIN
	
	INSERT INTO PATRON_IN_HOLD 
	
	SELECT  patron_id,
			patron_type,
			trunc(sysdate),
			NULL
	FROM
		    PATRON_DUES
	WHERE	
			due_end_date is null
			AND patron_type = 'S'
	GROUP BY patron_id,patron_type,trunc(sysdate)
	HAVING MAX(TRUNC(SYSDATE) - TRUNC (due_start_Date)) > 90;
			
	
				

END patron_in_hold_proc;

END patron_in_hold_pkg;
	