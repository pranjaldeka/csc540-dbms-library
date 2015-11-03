SET SERVEROUTPUT ON;
CREATE OR REPLACE PACKAGE  CHECK_OUT_PKG 
IS

PROCEDURE CHECK_OUT_PROC(    
	issue_type	    	IN 	VARCHAR2,
	issue_type_id		IN 	VARCHAR2,
	USER_TYPE           IN  VARCHAR2,
	USER_ID 			IN  VARCHAR2,
	LIBRARY_ID 			IN  VARCHAR2,
	duedate				IN	VARCHAR2,
	OUTPUT    OUT           VARCHAR2
  
);

END CHECK_OUT_PKG;
/
CREATE OR REPLACE PACKAGE BODY CHECK_OUT_PKG 
IS

PROCEDURE CHECK_OUT_PROC(
    
	ISSUE_TYPE	    	IN 	VARCHAR2,
	ISSUE_TYPE_ID		IN 	VARCHAR2,
	USER_TYPE           IN  VARCHAR2,
	USER_ID 			IN  VARCHAR2,
	LIBRARY_ID 			IN  VARCHAR2,
	duedate				IN	VARCHAR2,
  OUTPUT    OUT  VARCHAR2
)
IS

no_of_hard_copies	number(2);
already_in_resource_q_flag number(2);
user_priority		number(2);
max_priority		number(10);
max_faculty_priority number(10);
already_co_flag		number(2);
resource_exists_in_lib_flag number(2);
chk_user_in_q_flag	number(2);
sql_statement		varchar2(32000);
user_id_column 		varchar2(100);
table_name 			varchar2(100);
user_table 			varchar2(100);
current_datetime 	varchar2(100);
invalid 			number(2);
search_parameter 	VARCHAR2(50);
primary_id varchar2(100);
valid_duration varchar2(200);
duedate_timestamp timestamp;
validdate_timestamp timestamp;
currentdate_timestamp timestamp;
day_week varchar2(50);
time_day varchar2(10);
BEGIN
  invalid := 0;
  IF USER_TYPE = 'S' 
	  THEN
		 user_table := 'STUDENTS';
		 user_id_column := 'student_id';
  ELSIF USER_TYPE = 'F'
	  THEN
		 user_table := 'FACULTIES';
		 user_id_column := 'faculty_id';
  ELSE
		 invalid := 1;
  END IF;
  


  IF invalid = 0
  /*Assign table name and its primary to apppropriate variables*/
   THEN
			IF ISSUE_TYPE = 'B' 
            THEN
                table_name := 'BOOKS';
                search_parameter := 'ISBN';
						ELSIF ISSUE_TYPE = 'J' 
            THEN
                table_name := 'JOURNALS';
                search_parameter := 'ISSN';
            ELSIF ISSUE_TYPE = 'P' 
            THEN
                table_name := 'CONFERENCE_PAPERS';
                search_parameter := 'CONF_PAPER_ID';
          	ELSIF ISSUE_TYPE = 'C' 
            THEN
                table_name := 'CAMERAS';
                search_parameter := 'CAMERA_ID';
						
            ELSE
                invalid :=1;
            END IF;
						
					
				
        IF  invalid = 0
			THEN
			/*current date time*/
			SELECT TO_CHAR
									(SYSDATE, 'YYYY-MM-DD HH24:MI:SS') into current_datetime
									 FROM DUAL;
		  /*check if due date is valid */
			
      
      
			sql_statement :='
				SELECT to_timestamp('''||duedate||''',''yyyy-mm-dd HH24 mi-ss'') 
				FROM DUAL';
			  /*DBMS_OUTPUT.PUT_LINE(sql_statement);*/
			EXECUTE IMMEDIATE sql_statement INTO duedate_timestamp;
			
	
			
      SELECT CURRENT_TIMESTAMP INTO currentdate_timestamp 
      FROM dual;
      
		IF ISSUE_TYPE <> 'C'  
        THEN
					sql_statement :='
					SELECT DISTINCT VALID_DURATION 
					FROM CHECKOUT_VALID_DURATION
					WHERE USER_TYPE = '''||user_type||''' 
					AND RESOURCE_TYPE = '''||issue_type||'''
							';
				
					/*DBMS_OUTPUT.PUT_LINE(sql_statement);*/
					EXECUTE IMMEDIATE sql_statement INTO valid_duration;
		  
					sql_statement :='
					SELECT CURRENT_TIMESTAMP + '||valid_duration||'
					FROM DUAL
					';
					/*DBMS_OUTPUT.PUT_LINE(sql_statement);*/
					EXECUTE IMMEDIATE sql_statement INTO validdate_timestamp;
					/*DBMS_OUTPUT.PUT_LINE(validdate_timestamp);
            	DBMS_OUTPUT.PUT_LINE(currentdate_timestamp);
              	DBMS_OUTPUT.PUT_LINE(duedate_timestamp);
					*/
          IF duedate_timestamp <= validdate_timestamp AND duedate_timestamp >=currentdate_timestamp
						THEN
							invalid := 0;
						ELSE
							invalid := 1;						
						END IF;
						

  
		ELSIF issue_type = 'C' 
		THEN
				SELECT TO_CHAR(SYSDATE,'D') 
				INTO day_week
				from dual;
				
				IF day_week = 2
				THEN
					select to_char(sysdate,'HH24MMSS') 
					INTO time_day from dual;
				
					if time_day >= '00000' AND time_day <= '235959'
					then
						invalid := 0;
						
						SELECT to_timestamp(NEXT_DAY(sysdate,'thursday')) + interval '18' hour 
						INTO validdate_timestamp
						FROM DUAL;
            
						
						IF duedate_timestamp <= validdate_timestamp AND duedate_timestamp >= currentdate_timestamp
						THEN
							invalid := 0;
						ELSE
							invalid := 1;						
						END IF;
						
					ELSE
						invalid := 1;
					END IF;
					
				ELSE 
				invalid := 1;
				END IF;
				
        END IF;
		

		IF invalid = 0
			
			THEN
						
					/*find student id or facultyid */
					  sql_statement := 
					  '
					  SELECT '||user_id_column||' 
					  FROM '||user_table||'
					  WHERE user_id = '''||user_id||'''';
					  /*DBMS_OUTPUT.PUT_LINE(sql_statement);*/
					  EXECUTE IMMEDIATE sql_statement INTO primary_id;
						  already_co_flag := 0;
						  resource_exists_in_lib_flag := 0;
						  sql_statement := 
								'SELECT 1 
								FROM SSINGH25.' || table_name || ' T1
								inner join 
								SSINGH25.'||table_name||'_in_libraries T2
								ON T1.'||search_parameter|| '=T2.'||search_parameter|| '
								WHERE T2.LIBRARY_ID= '''||LIBRARY_ID||''' 
								and T1.'||search_parameter|| '='''||ISSUE_TYPE_ID||'''
								';
						  EXECUTE IMMEDIATE sql_statement INTO resource_exists_in_lib_flag;
						  IF resource_exists_in_lib_flag = 1
						  THEN
						  BEGIN
								sql_statement := '
								select 1 
								from ssingh25.'||user_table||'_CO_'||table_name||' 
								where '||search_parameter|| '='''||ISSUE_TYPE_ID||'''
								and '||user_id_column|| '='''||primary_id||'''
								and return_date is null
								';
								/*DBMS_OUTPUT.PUT_LINE(sql_statement);*/
							EXECUTE IMMEDIATE sql_statement INTO already_co_flag;
							/*DBMS_OUTPUT.PUT_LINE(sql_statement);*/
							/*DBMS_OUTPUT.PUT_LINE(already_co_flag);*/
							EXCEPTION
								WHEN NO_DATA_FOUND THEN
									already_co_flag := 0;
									DBMS_OUTPUT.PUT_LINE(already_co_flag);
								WHEN OTHERS THEN
									DBMS_OUTPUT.PUT_LINE('Hii');
									output :=SQLERRM;
							END;
							IF already_co_flag = 1
							THEN
								OUTPUT := 'RESOURCE ALREADY ISSUED TO THE USER';
								
							ELSE
							
								no_of_hard_copies := 0;
								sql_statement := 
								'SELECT T2.no_of_hardcopies 
								FROM SSINGH25.' || table_name || ' T1
								inner join 
								SSINGH25.'||table_name||'_in_libraries T2
								ON T1.'||search_parameter|| '=T2.'||search_parameter|| '
								WHERE T2.LIBRARY_ID= '''||LIBRARY_ID||''' 
								and T1.'||search_parameter|| '='''||ISSUE_TYPE_ID||'''
									and T2.no_of_hardcopies > 0';
								BEGIN	
								EXECUTE IMMEDIATE sql_statement INTO no_of_hard_copies;
								EXCEPTION
								WHEN NO_DATA_FOUND THEN
									no_of_hard_copies := 0;
									DBMS_OUTPUT.PUT_LINE(no_of_hard_copies || 'Hiiiiii');
								WHEN OTHERS THEN
									DBMS_OUTPUT.PUT_LINE(no_of_hard_copies || 'Hellllllo');
									output :=SQLERRM;
								END;
								/*DBMS_OUTPUT.PUT_LINE(avail_flag);*/
								IF no_of_hard_copies > 0
								THEN
									/* chk if user already exists in RESOURCE_QUEUE and has the priority to checkout*/
									/* if yes, delete from queue - also reduce priority of others*/
									/* Eitherways call checkout transaction */
									user_priority := 0;
									sql_statement := '
											select MIN(PRIORITY) 
											from SSINGH25.RESOURCES_QUEUE
											where 
											RESOURCE_ID = '''||ISSUE_TYPE_ID||'''
											and PATRON_ID = '''||primary_id||'''
											and LIBRARY_ID = '''||LIBRARY_ID||'''
									';
									BEGIN
									EXECUTE IMMEDIATE sql_statement into user_priority;
									EXCEPTION
										WHEN NO_DATA_FOUND THEN
											user_priority := 0;
										WHEN OTHERS THEN
											output :=SQLERRM;
									END;
									IF user_priority > 0 and user_priority <= no_of_hard_copies
									THEN
										/* delete from queue */
										sql_statement := '
											delete from SSINGH25.RESOURCES_QUEUE
											where 
											RESOURCE_ID = '''||ISSUE_TYPE_ID||'''
											and PATRON_ID = '''||primary_id||'''
											and PRIORITY = '''||user_priority||'''
											and LIBRARY_ID = '''||LIBRARY_ID||'''
										';
										EXECUTE IMMEDIATE sql_statement;
										COMMIT;

										/* update other records whose priority < user_priority */
										sql_statement := '
											update SSINGH25.RESOURCES_QUEUE
											set PRIORITY = PRIORITY - 1
											where RESOURCE_ID = '''||ISSUE_TYPE_ID||''' 
											and LIBRARY_ID = '''||LIBRARY_ID||'''
											and '||user_priority||' < PRIORITY
										';
										EXECUTE IMMEDIATE sql_statement;
										COMMIT;
									end if;							
									/*
									#######################CALL CHECKOUT TRANSACTION#######################;
									*/	
									--checkout_pkg.CHECK_OUT_TRANSACTION_PROC (user_table, table_name, primary_id, ISSUE_TYPE_ID, LIBRARY_ID, current_datetime, duedate);	

									BEGIN
								sql_statement := '
											INSERT INTO '||user_table||'_CO_'||table_name||'
											VALUES (
											'''||primary_id||''',
											'''||ISSUE_TYPE_ID||''',
											'''||LIBRARY_ID||''',
											TIMESTAMP'''||current_datetime||''',
											NULL,
											TIMESTAMP'''||duedate||'''
											)';
								
											
											
											EXECUTE IMMEDIATE sql_statement;
											
											/*update No. of avaialable hard copies*/
											
											sql_statement := 'UPDATE 
											SSINGH25.'||table_name||'_in_libraries T
											SET NO_OF_HARDCOPIES =NO_OF_HARDCOPIES-1
											WHERE T.LIBRARY_ID= '''||LIBRARY_ID||''' 
											and T.'||search_parameter|| '='''||ISSUE_TYPE_ID||'''';
											/*DBMS_OUTPUT.PUT_LINE(sql_statement);
											*/
											EXECUTE IMMEDIATE sql_statement;
										
										COMMIT;
									OUTPUT := 'CHECK OUT SUCCESSFUL';

									EXCEPTION
										 WHEN NO_DATA_FOUND THEN
											OUTPUT:='Error during checkout';
										 WHEN OTHERS THEN
											   OUTPUT:=SQLERRM;
									END;
									
								
								ELSE
									/* put him in queue bcz he is eligible to checkout but no hard copies left*/
																		
									/*but first check if he is already in resources_queue for that particular resource
									irrespective of library*/
									begin
										already_in_resource_q_flag := 0;
										sql_statement := '
											select 1 
											from RESOURCES_QUEUE
											where RESOURCE_ID = '''||ISSUE_TYPE_ID||'''
											and PATRON_ID = '''||primary_id||'''
										';
										EXECUTE IMMEDIATE sql_statement INTO already_in_resource_q_flag;
										exception
											WHEN NO_DATA_FOUND THEN
												already_in_resource_q_flag := 0; 
											WHEN OTHERS THEN
												output :=SQLERRM;
									end;
									if already_in_resource_q_flag = 1
									then
										OUTPUT := 'You are already in Waiting Queue';
									else
										/* CHK if faculty or student
									    give him suitable priority */
										/* first check if the queue is empty - insert - set priority = max_priority + 1 */
										max_priority := 0;
										sql_statement := '
												select NVL(MAX(PRIORITY), 0) 
												from SSINGH25.RESOURCES_QUEUE
												where RESOURCE_ID = '''||ISSUE_TYPE_ID||''' 
												and LIBRARY_ID = '''||LIBRARY_ID||''' 
										';
										BEGIN

											DBMS_OUTPUT.PUT_LINE(sql_statement || ' ' || max_priority);
											EXECUTE IMMEDIATE sql_statement into max_priority;
											DBMS_OUTPUT.PUT_LINE(sql_statement || ' ' || max_priority);
											EXCEPTION
												WHEN NO_DATA_FOUND THEN
													max_priority := 0;
													DBMS_OUTPUT.PUT_LINE(max_priority || 'sud');
												WHEN OTHERS THEN
													DBMS_OUTPUT.PUT_LINE(max_priority || 'dev');
													output :=SQLERRM;
										END;														
										
										if (max_priority = 0 OR USER_TYPE = 'S')
										then
										begin
										/* insert */
											max_priority:= max_priority+1;
											DBMS_OUTPUT.PUT_LINE('at here ' ||max_priority);

											sql_statement := '
												INSERT INTO RESOURCES_QUEUE
												VALUES (
												'''||ISSUE_TYPE_ID||''',
												'''||primary_id||''',										
												'''||USER_TYPE||''',											
												'||max_priority||',
												'''||LIBRARY_ID||'''
												)';	
											DBMS_OUTPUT.PUT_LINE(sql_statement);
											EXECUTE IMMEDIATE sql_statement;
											commit;
											exception
											WHEN NO_DATA_FOUND THEN
												max_priority := 0;
												DBMS_OUTPUT.PUT_LINE(max_priority || 'sud2');
											WHEN OTHERS THEN
												DBMS_OUTPUT.PUT_LINE(max_priority || 'dev2');
												output :=SQLERRM;
											
										end;
										OUTPUT := 'Successfully added to Waiting Queue!';
										
									elsif USER_TYPE = 'F'
									THEN 
											/* Check max(priority) of faculty and increment priorities of others*/
											/*If he is the first faculty but already there are students*/
											max_faculty_priority := 0;
											
											sql_statement := '
												select NVL(MAX(PRIORITY), 0) 
												from SSINGH25.RESOURCES_QUEUE
												where PATRON_TYPE = ''F''
												and RESOURCE_ID = '''||ISSUE_TYPE_ID||''' 
												and LIBRARY_ID = '''||LIBRARY_ID||''' 
											';
											DBMS_OUTPUT.PUT_LINE('atttttt here ' ||max_faculty_priority);
											BEGIN
												EXECUTE IMMEDIATE sql_statement into max_faculty_priority;
												DBMS_OUTPUT.PUT_LINE('atttttt here ' ||max_faculty_priority);
												EXCEPTION
													WHEN NO_DATA_FOUND THEN
														max_faculty_priority := 0;
													WHEN OTHERS THEN
														output :=SQLERRM;
											END;
											
											/*shift priorities of others by one*/
											sql_statement := '
												update SSINGH25.RESOURCES_QUEUE
												set PRIORITY = PRIORITY + 1
												where PRIORITY > '||max_faculty_priority||'
												and RESOURCE_ID = '''||ISSUE_TYPE_ID||''' 
												and LIBRARY_ID = '''||LIBRARY_ID||''' 
											';
											DBMS_OUTPUT.PUT_LINE(sql_statement || ' ' || max_faculty_priority);
											EXECUTE IMMEDIATE sql_statement;
											COMMIT;										
											
											/* insert */
											max_faculty_priority:= max_faculty_priority+1;
											/*DBMS_OUTPUT.PUT_LINE('at here ' ||max_priority);*/

											sql_statement := '
												INSERT INTO RESOURCES_QUEUE
												VALUES (
												'''||ISSUE_TYPE_ID||''',
												'''||primary_id||''',										
												'''||USER_TYPE||''',											
												'||max_faculty_priority||',
												'''||LIBRARY_ID||'''										
												)';	
											DBMS_OUTPUT.PUT_LINE(sql_statement);
											EXECUTE IMMEDIATE sql_statement;
											commit;									
										
											OUTPUT := 'Successfully added to Waiting Queue!';
										
									end if;		
																		
								END IF;
							END IF;
							END IF;
						ELSE
							OUTPUT := 'RESOURCE IS NOT IN ANY LIBRARY';
						END IF;
							
			ELSE
					OUTPUT := 'INVALID RETURN DATE OR TIME';
			END IF;
						  ELSE
							OUTPUT := 'INVALID PARAMETERS';
							/*DBMS_OUTPUT.PUT_LINE('RESOURCE UNAVAILABLE/ ALREADY ISSUED TO THE USER');*/
						  END IF;	
			
	ELSE
        OUTPUT := 'INVALID PARAMETERS';
        /*DBMS_OUTPUT.PUT_LINE('RESOURCE UNAVAILABLE/ ALREADY ISSUED TO THE USER');*/
  END IF;
  /*DBMS_OUTPUT.PUT_LINE(avail_flag);*/
  EXCEPTION
	WHEN NO_DATA_FOUND THEN
    /*DBMS_OUTPUT.PUT_LINE('Invalid Inputs');*/
		 OUTPUT := 'RESOURCE UNAVAILABLE/ ALREADY ISSUED TO THE USER';
  WHEN OTHERS THEN
		output :=SQLERRM;   
END check_out_proc;

END check_out_pkg;