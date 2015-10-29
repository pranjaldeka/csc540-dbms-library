set serveroutput on;
/*********************************************************************
user_profile_pkg.sql : checks in a publicaation and camera
**********************************************************************/
CREATE OR REPLACE PACKAGE check_in_pkg AS 

PROCEDURE check_in_proc(
  table_name 		IN 	VARCHAR2,
	search_parameter 	IN 	VARCHAR2,
	search_variable		IN	VARCHAR2,
  user_table IN VARCHAR2,
  user_id IN VARCHAR2,
  library_id IN varchar2
);
END check_in_pkg;
/
CREATE OR REPLACE PACKAGE BODY check_in_pkg 
IS
PROCEDURE check_in_proc(
  table_name 		IN 	VARCHAR2,
	search_parameter 	IN 	VARCHAR2,
	search_variable		IN	VARCHAR2,
  user_table IN VARCHAR2,
  user_id IN VARCHAR2,
  library_id IN varchar2
)
IS
sql_statement varchar2(32000);
user_id_column varchar(50);
current_datetime varchar2(100);
BEGIN
/*DBMS_OUTPUT.PUT_LINE('Check in procedure');
  /*check if same book is issued to the user*/
  
  /*Check whether item is avaiable*/
  IF user_table='STUDENTS' 
  THEN
     user_id_column :='STUDENT_ID';
  ELSIF user_table = 'FACULTIES'
  THEN
      user_id_column :='FACULTY_ID';
  END IF;
  /*
  #######################CHECKIN TRANSACTION#######################;
  */
  
  SET TRANSACTION NAME 'CHECKIN';
  
    /*Calculte current date time*/
  SELECT TO_CHAR
    (SYSDATE, 'YYYY-MM-DD HH24:MI:SS') into current_datetime
     FROM DUAL;
     
     /*Update the checkout record*/
     
  sql_statement := 'update ssingh25.'||user_table||'_CO_'||table_name||' 
    set return_date=TIMESTAMP'''||current_datetime||'''
    where '||search_parameter|| '='''||search_variable||'''
    and '||user_id_column|| '='''||user_id||'''
    and  library_id='''||library_id||'''
    and return_date is null';
    DBMS_OUTPUT.PUT_LINE(sql_statement); 
    EXECUTE IMMEDIATE sql_statement ;
    /*Update the no. of hardcopies*/
    
   sql_statement := 'UPDATE 
    SSINGH25.'||table_name||'_in_libraries T
    SET NO_OF_HARDCOPIES =NO_OF_HARDCOPIES+1
    WHERE T.library_id= '''||library_id||''' 
    and T.'||search_parameter|| '='''||search_variable||'''';
    DBMS_OUTPUT.PUT_LINE(sql_statement);
    EXECUTE IMMEDIATE sql_statement;
    
    COMMIT;
      
    
/*IF SQL%NOTFOUND 
THEN
    DBMS_OUTPUT.PUT_LINE('No such record');
END IF;
  */ 

   
END check_in_proc;

END check_in_pkg;
/
