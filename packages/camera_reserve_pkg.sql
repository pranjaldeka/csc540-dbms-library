SET SERVEROUTPUT ON;
CREATE OR REPLACE PACKAGE  camera_reserve_pkg 
IS
PROCEDURE camera_reserve_proc(
    
	camera_id	    				IN 	VARCHAR2,
	user_id		                    IN 	VARCHAR2,
	user_type                       IN  VARCHAR2,
	reservation_date 			    IN  VARCHAR2,
	library_id						IN	VARCHAR2,
	insert_flag						IN	VARCHAR2,
  output   						 OUT VARCHAR2
);
END camera_reserve_pkg;
/
CREATE OR REPLACE PACKAGE BODY camera_reserve_pkg 
IS
PROCEDURE camera_reserve_proc(
    
	camera_id	    				IN 	VARCHAR2,
	user_id		             IN 	VARCHAR2,
	user_type              IN  VARCHAR2,
	reservation_date 			IN  VARCHAR2,
	library_id						IN	VARCHAR2,	
  insert_flag						IN	VARCHAR2,
  output   						 OUT VARCHAR2
)
IS

sql_statement		varchar2(32000);
user_id_column 		varchar2(100);
user_table 			varchar2(100);
reservation_timestamp timestamp;
already_co_flag 	VARCHAR2(2);
primary_id VARCHAR2(100);
day_week varchar2(50);
no_reservation VARCHAR2(100);
no_available VARCHAR2(100);
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
		BEGIN
			sql_statement := 
			'
			SELECT TO_CHAR(timestamp '''||reservation_date||''',''D'') 
			from dual
			';
			EXECUTE IMMEDIATE sql_statement INTO day_week;
			EXCEPTION
					WHEN NO_DATA_FOUND THEN
						day_week := 0;
						 OUTPUT := 'Reservation day should be friday !';
		END;
		
    
			sql_statement := 
			'
			SELECT timestamp '''||reservation_date||'''
			from dual
			';
			EXECUTE IMMEDIATE sql_statement INTO reservation_timestamp;
		
    
    
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
                   
         DBMS_OUTPUT.put_line('userid assigned');
		
		 -- check if user has already reserved
          BEGIN
					sql_statement := 
                      'SELECT 0 FROM DUAL
                      WHERE NOT EXISTS
                      (
                        SELECT 1
                        FROM CAMERAS_RESERVATION
                        WHERE camera_id = '''||camera_id||'''
                        AND RESERVATION_TIMESTAMP ='''||reservation_timestamp||'''
                        AND patron_id = '''||primary_id||'''
                        AND patron_type = '''||user_type||'''
                      )';
				   DBMS_OUTPUT.put_line(sql_statement);	
					EXECUTE IMMEDIATE sql_statement INTO already_co_flag ;	
                             
         DBMS_OUTPUT.put_line(sql_statement);
					
					EXCEPTION
					WHEN NO_DATA_FOUND THEN
						already_co_flag := 1;
						OUTPUT := 'already reserved by the user !';					
          END;
					
			IF already_co_flag = 0 
			THEN
			
			IF insert_flag = 1
			THEN
			
				sql_statement := '
									INSERT INTO CAMERAS_RESERVATION
									VALUES (
									'''||camera_id||''',
									'''||primary_id||''',
									'''||user_type||''',
									'''||library_id||''',
									TIMESTAMP'''||reservation_date||''',
									1
									
									)';
				DBMS_OUTPUT.put_line(sql_statement);						
				EXECUTE IMMEDIATE sql_statement;
				
				OUTPUT := 'reservation successful';
			ELSE
			
			
					/*find waiting list*/
					sql_statement := 
                      '
                        SELECT COUNT(1)
                        FROM CAMERAS_RESERVATION
                        WHERE camera_id = '''||camera_id||'''
                        AND RESERVATION_TIMESTAMP ='''||reservation_timestamp||'''
                        AND library_id = '''||library_id||'''
                        AND ACTIVE_flag= 1
                      ';
					  
					DBMS_OUTPUT.put_line(sql_statement);	
					EXECUTE IMMEDIATE sql_statement INTO no_reservation ;	
					
          BEGIN
              sql_statement := 
                          '
                            SELECT no_of_hardcopies
                            FROM CAMERAS_IN_LIBRARIES 
                            WHERE camera_id = '''||camera_id||'''
                            AND library_id = '''||library_id||'''
                          ';
                
              DBMS_OUTPUT.put_line(sql_statement);	
              EXECUTE IMMEDIATE sql_statement INTO no_available ;	
              
              DBMS_OUTPUT.put_line('here');	
              EXCEPTION
              WHEN NO_DATA_FOUND THEN
              OUTPUT := 'Camera not avaialable in library';
              no_available:=-1;
              WHEN OTHERS THEN
              output := 'Camera not avaialable in library';
              
          END;
                                
			IF no_available <> -1
              THEN
              IF no_reservation >= no_available 
              THEN
             
				OUTPUT := 'waiting list - '||to_char(to_number(no_reservation) - to_number(no_available) + 1);
				DBMS_OUTPUT.put_line(output);
              ELSE 
				OUTPUT := 'available - '||to_char(to_number(no_available) - to_number(no_reservation) + 1);
              END IF;
            ELSE
			OUTPUT := 'Camera not avaialable in library';
			
			 END IF;
		END IF;			
			
				
			ELSE

			OUTPUT := 'User already reserved the camera on this date!';						

			END IF;
			
		ELSE
		
		 OUTPUT := 'Reservation day should be friday !';
		 
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


