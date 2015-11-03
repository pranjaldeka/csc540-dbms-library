/*********************************************************************
user_notification_pkg.sql : Display notifications to the user
**********************************************************************/
set serveroutput on;

CREATE OR REPLACE PACKAGE user_notification_pkg AS 
user_error	EXCEPTION;
/*********************************************************************
show_notification_proc : This procedure collects all plausible
						 notifications and displays it to user
**********************************************************************/

PROCEDURE show_notification_proc(
	in_user_type 		VARCHAR2,
	in_user_id			VARCHAR2,
	pref				OUT SYS_REFCURSOR,
	out_msg				OUT VARCHAR2
);


END user_notification_pkg;
/
CREATE OR REPLACE PACKAGE BODY user_notification_pkg 
IS
/*********************************************************************
show_notification_proc : This procedure collects all plausible
						 notifications and displays it to user
**********************************************************************/
	PROCEDURE show_notification_proc(
		in_user_type 		VARCHAR2,
		in_user_id			VARCHAR2,
		pref				OUT SYS_REFCURSOR,
		out_msg				OUT VARCHAR2
	)
	IS
	v_id VARCHAR2(50);
	sql_step VARCHAR2(32000);
	sql_stmt VARCHAR2(32000);
	BEGIN
	sql_step:= ' Selecting user id from username.';
		IF in_user_type='S' THEN
			BEGIN

				SELECT student_id INTO v_id FROM students WHERE user_id=in_user_id;

			EXCEPTION
				WHEN NO_DATA_FOUND THEN
				out_msg:='The user does not exists!!';
			END;
			sql_step:= 'Opening the cursor';
			BEGIN 
				OPEN pref FOR 
						SELECT 'Room' AS R,
						  room_no
						  || ' needs to be vacated by '                     AS resource_name,
						  TO_CHAR(reserv_end_time, 'DD-MON-YYYY HH24:MI')   AS due_date,
						  ' Which was reserved on '	|| TO_CHAR(reserv_start_time, 'DD-MON-YYYY HH24:MI') AS checkout_time
						FROM students_reserves_rooms
						WHERE 
						SYSTIMESTAMP BETWEEN reserv_start_time AND reserv_end_time
						AND student_id = v_id

						UNION
						SELECT 'Book' AS R,
						  isbn
						  || ' must be returned before '             AS resource_name,
						  TO_CHAR(DUE_DATE, 'DD-MON-YYYY HH24:MI')   AS due_date,
						  ' Which was checked out on '	|| TO_CHAR(ISSUE_DATE, 'DD-MON-YYYY HH24:MI') AS checkout_time
						FROM STUDENTS_CO_BOOKS
						WHERE DUE_DATE >= SYSTIMESTAMP
						AND  RETURN_DATE IS NULL
						AND student_id = v_id

						UNION
						SELECT 'Conference Paper' AS R,
						  CONF_PAPER_ID
						  || ' must be returned before '             AS resource_name,
						  TO_CHAR(DUE_DATE, 'DD-MON-YYYY HH24:MI')   AS due_date,
						  ' Which was checked out on '	|| TO_CHAR(ISSUE_DATE, 'DD-MON-YYYY HH24:MI') AS checkout_time
						FROM STUDENTS_CO_CONFERENCE_PAPERS
						WHERE DUE_DATE >= SYSTIMESTAMP
						AND  RETURN_DATE IS NULL
						AND student_id = v_id

						UNION
						SELECT 'Journal' AS R,
						  ISSN
						  || ' is due on '                           AS resource_name,
						  TO_CHAR(DUE_DATE, 'DD-MON-YYYY HH24:MI')   AS due_date,
						  ' Which was checked out on '	|| TO_CHAR(ISSUE_DATE, 'DD-MON-YYYY HH24:MI') AS checkout_time
						FROM STUDENTS_CO_JOURNALS
						WHERE DUE_DATE >= SYSTIMESTAMP
						AND  RETURN_DATE IS NULL
						AND student_id = v_id

						UNION
						SELECT 'Camera' AS R,
						  CAMERA_ID
						  || ' must be returned before '             AS resource_name,
						  TO_CHAR(DUE_DATE, 'DD-MON-YYYY HH24:MI')   AS due_date,
						  ' Which was checked out on '	|| TO_CHAR(ISSUE_DATE, 'DD-MON-YYYY HH24:MI') AS checkout_time
						FROM STUDENTS_CO_CAMERAS
						WHERE DUE_DATE >= SYSTIMESTAMP
						AND  RETURN_DATE IS NULL
						AND student_id = v_id;
			EXCEPTION
				WHEN NO_DATA_FOUND THEN
					out_msg:='There is no reservation for you!!';

			END;


		ELSIF in_user_type='F' THEN
			BEGIN
				SELECT faculty_id INTO v_id FROM faculties WHERE user_id=in_user_id;
			EXCEPTION
				WHEN NO_DATA_FOUND THEN
				out_msg:='The user does not exists!!';
			END;
			sql_step:= 'Opening the cursor';
			BEGIN 
				OPEN pref FOR 
					SELECT 'Room' AS R,
					  room_no
					  || ' needs to be vacated by '                     AS resource_name,
					  TO_CHAR(reserv_end_time, 'DD-MON-YYYY HH24:MI')   AS due_date,
					  TO_CHAR(reserv_start_time, 'DD-MON-YYYY HH24:MI') AS checkout_time
					FROM FACULTIES_RESERVES_ROOMS
					WHERE 
					SYSTIMESTAMP BETWEEN reserv_start_time AND reserv_end_time
					AND faculty_id = v_id

					UNION
					SELECT 'Book' AS R,
					  isbn
					  || ' is due on '                           AS resource_name,
					  TO_CHAR(DUE_DATE, 'DD-MON-YYYY HH24:MI')   AS due_date,
					' Which was checked out on '	|| TO_CHAR(ISSUE_DATE, 'DD-MON-YYYY HH24:MI') AS checkout_time
					FROM FACULTIES_CO_BOOKS
					WHERE DUE_DATE >= SYSTIMESTAMP
					AND  RETURN_DATE IS NULL
					AND faculty_id = v_id

					UNION
					SELECT 'Conference Paper' AS R,
					  CONF_PAPER_ID
					  || ' is due on '                           AS resource_name,
					  TO_CHAR(DUE_DATE, 'DD-MON-YYYY HH24:MI')   AS due_date,
					' Which was checked out on '	|| TO_CHAR(ISSUE_DATE, 'DD-MON-YYYY HH24:MI') AS checkout_time
					FROM FACULTIES_CO_CONFERENCE_PAPERS
					WHERE DUE_DATE >= SYSTIMESTAMP
					AND  RETURN_DATE IS NULL
					AND faculty_id = v_id

					UNION
					SELECT 'Journal' AS R,
					  ISSN
					  || ' is due on '                           AS resource_name,
					  TO_CHAR(DUE_DATE, 'DD-MON-YYYY HH24:MI')   AS due_date,
					' Which was checked out on '	|| TO_CHAR(ISSUE_DATE, 'DD-MON-YYYY HH24:MI') AS checkout_time
					FROM FACULTIES_CO_JOURNALS
					WHERE DUE_DATE >= SYSTIMESTAMP
					AND  RETURN_DATE IS NULL
					AND faculty_id = v_id

					UNION
					SELECT 'Camera' AS R,
					  CAMERA_ID
					  || ' must be returned before '             AS resource_name,
					  TO_CHAR(DUE_DATE, 'DD-MON-YYYY HH24:MI')   AS due_date,
					' Which was checked out on '	|| TO_CHAR(ISSUE_DATE, 'DD-MON-YYYY HH24:MI') AS checkout_time
					FROM FACULTIES_CO_CAMERAS
					WHERE DUE_DATE >= SYSTIMESTAMP
					AND  RETURN_DATE IS NULL
					AND faculty_id = v_id;
			EXCEPTION
				WHEN NO_DATA_FOUND THEN
					out_msg:='There is no reservation for you!!';	
			END;		

		END IF;

	  EXCEPTION
	 WHEN OTHERS THEN
	 	   out_msg:=sql_step||SQLERRM;
	END show_notification_proc;


END user_notification_pkg;
/
SHOW ERRORS