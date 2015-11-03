SET SERVEROUTPUT ON;
CREATE OR REPLACE PACKAGE  camera_reserve_pkg 
IS
PROCEDURE camera_reserve_proc(
    
	camera_id	    				IN 	VARCHAR2,
	user_id		                    IN 	VARCHAR2,
	user_type                       IN  VARCHAR2,
	reservation_timestamp 			IN  VARCHAR2,
	library_id						IN	VARCHAR2,
	insert_flag						IN	NUMBER,
    output   						 OUT VARCHAR2
);
END camera_reserve_pkg;
/
CREATE OR REPLACE PACKAGE BODY camera_reserve_pkg 
IS
PROCEDURE camera_reserve_proc(
    
	camera_id	    				IN 	VARCHAR2,
	user_id		                    IN 	VARCHAR2,
	user_type                       IN  VARCHAR2,
	reservation_timestamp 			IN  VARCHAR2,
	library_id						IN	VARCHAR2,
	insert_FLAG							IN	NUMBER,
    output   						 OUT VARCHAR2
)
IS

sql_statement		varchar2(32000);
user_id_column 		varchar2(100);
user_table 			varchar2(100);
waiting_list 		number(2);
already_co_flag 	number(2);
primary_id VARCHAR2(100);
day_week varchar2(50);
BEGIN

  IF USER_TYPE = 'S' 
	  THEN
		 user_table := 'STUDENTS';
		 user_id_column := 'STUDENT_ID';
  ELSIF USER_TYPE = 'F'
	  THEN
		 user_table := 'FACULTIES';
		 user_id_column := 'FACULTY_ID';

  END IF;
  
  /* check if reservation day is friday */
		SELECT TO_CHAR(SYSDATE,'D') 
		INTO day_week
		from dual;
		
		IF day_week = 6
		THEN

		/*  Check if camera is not reserved for that friday*/
		
		
		/*find student id or facultyid */
		
          sql_statement := 
          '
          SELECT '||user_id_column||' 
          FROM '||user_table||'
          WHERE user_id = '''||user_id||'''';
          
          EXECUTE IMMEDIATE sql_statement INTO primary_id;
                   
         
			IF insert_flag = 0
			THEN
					 -- check if user has already reserved
          BEGIN
					sql_statement := ' 		SELECT 0
											FROM CAMERAS_RESERVATION
											WHERE camera_id = '''||camera_id||'''
											AND TRUNC(RESERVATION_TIMESTAMP) ='''||TRUNC(reservation_timestamp)||'''
											AND patron_id = '''||primary_id||'''
											AND patron_type = '||user_type;
					
					EXECUTE IMMEDIATE sql_statement INTO already_co_flag ;	
					
					EXCEPTION
					WHEN NO_DATA_FOUND THEN
						already_co_flag := 1;
						OUTPUT := 'already reserved by the user !';					
          END;
					/*find waiting list*/
					IF already_co_flag = 0
					THEN
							sql_statement := '      SELECT COUNT(1) + 1
													FROM CAMERAS_RESERVATION
													WHERE camera_id = '''||camera_id||'''
													AND TRUNC(RESERVATION_TIMESTAMP) ='''||TRUNC(reservation_timestamp)||'''
													AND library_id ='''||library_id||'''
													';
													
							 EXECUTE IMMEDIATE sql_statement INTO waiting_list ;	
					  
					
							
						OUTPUT := 'waiting list - '||waiting_list;
				
			
					END IF;
			ELSE
					sql_statement := '
											INSERT INTO CAMERAS_RESERVATION
											VALUES (
											'''||camera_id||''',
											'''||primary_id||''',
											'''||user_type||''',
											'''||library_id||''',
											TIMESTAMP'''||reservation_timestamp||''',
											1,
											
											)';
								
											
											
					EXECUTE IMMEDIATE sql_statement;
					
					OUTPUT := 'reservation successful';
						
											
			
			END IF;
			
		ELSE
		
		 OUTPUT := 'reservation unsuccessful!';
		 
		END IF;
			
  
  EXCEPTION
	WHEN NO_DATA_FOUND THEN
    /*DBMS_OUTPUT.PUT_LINE('Invalid Inputs');*/
		 OUTPUT := 'RESERVATION UNCSUCCESSFUL';
  WHEN OTHERS THEN
		output :=SQLERRM;
END camera_reserve_proc;

END camera_reserve_pkg;
/
show errors