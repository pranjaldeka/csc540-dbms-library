/*refreshes dues*/
set serveroutput on;

CREATE OR REPLACE PACKAGE calculate_dues_pkg AS 
user_error	EXCEPTION;

PROCEDURE calculate_dues_proc;

END calculate_dues_pkg;
/
CREATE OR REPLACE PACKAGE BODY calculate_dues_pkg
IS
	PROCEDURE calculate_dues_proc
	IS
	
	BEGIN

	/*
	--Update the old dues :-
	UPDATE patron_dues 
    SET due_in_dollars = (SELECT  (trunc(coalesce(return_date,sysdate))- trunc(due_date)) * 2
    FROM 

		students_co_books S
		
	 WHERE patron_id = S.student_id
					AND patron_type = 'S'
					AND resource_type = 'B'
					AND resource_id = S.isbn
					AND due_start_date = due_date
					AND due_end_date is null)
	 
					
	UPDATE patron_dues 
    SET due_in_dollars = (SELECT  (trunc(coalesce(return_date,sysdate))- trunc(due_date)) * 2
    FROM 

		students_co_journals S
		
	 WHERE patron_id = S.student_id
					AND patron_type = 'S'
					AND resource_type = 'J'
					AND resource_id = S.issn
					AND due_start_date = due_date
					AND due_end_date is null);
	
	UPDATE patron_dues 
    SET due_in_dollars = (SELECT  (trunc(coalesce(return_date,sysdate))- trunc(due_date)) * 2
    FROM 

		students_co_conference_papers S
		
	 WHERE patron_id = S.student_id
					AND patron_type = 'S'
					AND resource_type = 'P'
					AND resource_id = S.conf_paper_id
					AND due_start_date = due_date
					AND due_end_date is null);
					
	UPDATE patron_dues 
    SET due_in_dollars = (SELECT  extract (day from (coalesce(return_date,sysdate)-due_date)) * 24 + (extract (hour from (coalesce(return_date,sysdate)-due_date)))
    FROM 

		students_co_cameras S
		
	 WHERE patron_id = S.student_id
					AND patron_type = 'S'
					AND resource_type = 'C'
					AND resource_id = S.camera_id
					AND due_start_date = due_date
					AND due_end_date is null);		
	
	
	
	-- For facultieS
	UPDATE patron_dues 
    SET due_in_dollars = (SELECT  (trunc(coalesce(return_date,sysdate))- trunc(due_date)) * 2
    FROM 

		faculties_co_books S
		
	 WHERE patron_id = S.faculty_id
					AND patron_type = 'F'
					AND resource_type = 'B'
					AND resource_id = S.isbn
					AND due_start_date = due_date
					AND due_end_date is null);
					
	UPDATE patron_dues 
    SET due_in_dollars = (SELECT  (trunc(coalesce(return_date,sysdate))- trunc(due_date)) * 2
    FROM 

		faculties_co_journals S
		
	 WHERE patron_id = S.faculty_id
					AND patron_type = 'F'
					AND resource_type = 'J'
					AND resource_id = S.issn
					AND due_start_date = due_date
					AND due_end_date is null);
	
	UPDATE patron_dues 
    SET due_in_dollars = (SELECT  (trunc(coalesce(return_date,sysdate))- trunc(due_date)) * 2
    FROM 

		faculties_co_conference_papers S
		
	 WHERE patron_id = S.faculty_id
					AND patron_type = 'F'
					AND resource_type = 'P'
					AND resource_id = S.conf_paper_id
					AND due_start_date = due_date
					AND due_end_date is null);
					
	UPDATE patron_dues 
    SET due_in_dollars = (SELECT  extract (day from (coalesce(return_date,sysdate)-due_date)) * 24 + (extract (hour from (coalesce(return_date,sysdate)-due_date)))
    FROM 

		faculties_co_cameras S
		
	 WHERE patron_id = S.faculty_id
					AND patron_type = 'F'
					AND resource_type = 'C'
					AND resource_id = S.camera_id
					AND due_start_date = due_date
					AND due_end_date is null);		
	*/
	
	
	-- delete the active dues;
	
	DELETE FROM PATRON_DUES
	WHERE due_end_date is null;
	
	-- Insert the new ones:-
	
	
	INSERT INTO patron_dues 
	SELECT
		 student_id,
		 'S', 
		 'B', 
		 isbn,
		 due_date ,
		 NULL,
		 (trunc(coalesce(return_date,sysdate))- trunc(due_date)) * 2
	 FROM
		students_co_books S
	 WHERE COALESCE(return_date,SYSDATE) > due_date
			AND NOT EXISTS 
				(
				SELECT 1
				FROM 
					patron_dues
				where
					patron_id = S.student_id
					AND patron_type = 'S'
					AND resource_type = 'B'
					and resource_id = S.isbn
					and due_start_date = due_date

				)
		UNION ALL
		SELECT
		 student_id,
		 'S', 
		 'J', 
		 issn,
		 due_date ,
		 NULL,
		 (trunc(coalesce(return_date,sysdate))- trunc(due_date)) * 2
	 FROM
		students_co_journals S
	 WHERE COALESCE(return_date,SYSDATE) > due_date
			AND NOT EXISTS 
				(
				SELECT 1
				FROM 
					patron_dues
				where
					patron_id = S.student_id
					AND patron_type = 'S'
					AND resource_type = 'J'
					and resource_id = S.issn
					and due_start_date = due_date

				)
				
		UNION ALL
		
		SELECT
		 student_id,
		 'S', 
		 'P', 
		 CONF_PAPER_ID,
		 due_date ,
		 NULL,
		 (trunc(coalesce(return_date,sysdate))- trunc(due_date)) * 2
	 FROM
		students_co_conference_papers S
	 WHERE COALESCE(return_date,SYSDATE) > due_date
			AND NOT EXISTS 
				(
				SELECT 1
				FROM 
					patron_dues
				where
					patron_id = S.student_id
					AND patron_type = 'S'
					AND resource_type = 'P'
					and resource_id = S.CONF_PAPER_ID
					and due_start_date = due_date

				)
				
		UNION ALL
		
		SELECT
		 student_id,
		 'S', 
		 'C', 
		 camera_id,
		 due_date ,
		 NULL,
		 extract (day from (coalesce(return_date,sysdate)-due_date)) * 24 + (extract (hour from (coalesce(return_date,sysdate)-due_date)))
	 FROM
		 students_co_cameras S
	 WHERE COALESCE(return_date,SYSDATE) > due_date
			AND NOT EXISTS 
				(
				SELECT 1
				FROM 
					patron_dues
				where
					patron_id = S.student_id
					AND patron_type = 'S'
					AND resource_type = 'C'
					and resource_id = S.camera_id
					and due_start_date = due_date

				)
		
			UNION ALL
		
		SELECT
		 faculty_id,
		 'S', 
		 'B', 
		 isbn,
		 due_date ,
		 NULL,
		 (trunc(coalesce(return_date,sysdate))- trunc(due_date)) * 2
	 FROM
		faculties_co_books S
	 WHERE COALESCE(return_date,SYSDATE) > due_date
			AND NOT EXISTS 
				(
				SELECT 1
				FROM 
					patron_dues
				where
					patron_id = S.faculty_id
					AND patron_type = 'S'
					AND resource_type = 'B'
					and resource_id = S.isbn
					and due_start_date = due_date

				)
		UNION ALL
		SELECT
		 faculty_id,
		 'S', 
		 'J', 
		 issn,
		 due_date ,
		 NULL,
		 (trunc(coalesce(return_date,sysdate))- trunc(due_date)) * 2
	 FROM
		faculties_co_journals S
	 WHERE COALESCE(return_date,SYSDATE) > due_date
			AND NOT EXISTS 
				(
				SELECT 1
				FROM 
					patron_dues
				where
					patron_id = S.faculty_id
					AND patron_type = 'S'
					AND resource_type = 'J'
					and resource_id = S.issn
					and due_start_date = due_date

				)
				
		UNION ALL
		
		SELECT
		 faculty_id,
		 'S', 
		 'P', 
		 CONF_PAPER_ID,
		 due_date ,
		 NULL,
		 (trunc(coalesce(return_date,sysdate))- trunc(due_date)) * 2
	 FROM
		faculties_co_conference_papers S
	 WHERE COALESCE(return_date,SYSDATE) > due_date
			AND NOT EXISTS 
				(
				SELECT 1
				FROM 
					patron_dues
				where
					patron_id = S.faculty_id
					AND patron_type = 'S'
					AND resource_type = 'P'
					and resource_id = S.CONF_PAPER_ID
					and due_start_date = due_date

				)
				
		UNION ALL
		
		SELECT
		 faculty_id,
		 'S', 
		 'C', 
		 camera_id,
		 due_date ,
		 NULL,
		 extract (day from (coalesce(return_date,sysdate)-due_date)) * 24 + (extract (hour from (coalesce(return_date,sysdate)-due_date)))
	 FROM
		 faculties_co_cameras S
	 WHERE COALESCE(return_date,SYSDATE) > due_date
			AND NOT EXISTS 
				(
				SELECT 1
				FROM 
					patron_dues
				where
					patron_id = S.faculty_id
					AND patron_type = 'S'
					AND resource_type = 'C'
					and resource_id = S.camera_id
					and due_start_date = due_date

				);
		
		/* reflects the fine when a day for that fine is over */
		DELETE FROM patron_dues
		WHERE due_in_dollars = 0;		
		commit;
END calculate_dues_proc;

END calculate_dues_pkg;
	