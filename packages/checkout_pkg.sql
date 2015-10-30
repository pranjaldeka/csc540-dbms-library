create or replace PACKAGE BODY check_out_pkg 
IS
PROCEDURE check_out_proc(
  table_name 		IN 	VARCHAR2,
	search_parameter 	IN 	VARCHAR2,
	search_variable		IN	VARCHAR2,
  user_table IN VARCHAR2,
  user_id IN VARCHAR2,
  library_id IN varchar2
)
IS
avail_flag number(2);
sql_statement varchar2(32000);
user_id_column varchar2(100);
current_datetime varchar2(100);
BEGIN
DBMS_OUTPUT.PUT_LINE('Check OUT procedure');
  /*check if same book is issued to the user*/
  
  /*Check whether item is avaiable*/
  IF user_table='STUDENTS' 
  THEN
     user_id_column :='STUDENT_ID';
  ELSIF user_table = 'FACULTIES'
  THEN
      user_id_column :='FACULTY_ID';
  END IF;
  
  avail_flag :=0;
  sql_statement := 'SELECT 1 FROM SSINGH25.' || table_name || ' T1
    inner join 
    SSINGH25.'||table_name||'_in_libraries T2
    ON T1.'||search_parameter|| '=T2.'||search_parameter|| '
    WHERE T2.library_id= '''||library_id||''' 
    and T1.'||search_parameter|| '='''||search_variable||'''
    and not exists
    (
    select 1 from ssingh25.'||user_table||'_CO_'||table_name||' 
    where '||search_parameter|| '='''||search_variable||'''
    and '||user_id_column|| '='''||user_id||'''
    and return_date is null
    )
    and no_of_hardcopies <> 0';
    /*(
    select count(1) from ssingh25.'||user_table||'_CO_'||table_name||' 
    where '||search_parameter|| '='''||search_variable||'''
    and return_date is null) 
    and no_of_hardcopies <>0';
    */
    DBMS_OUTPUT.PUT_LINE(sql_statement);
    /*insert into jolly_dummy values(1,sql_statement);
    commit;
    */

    EXECUTE IMMEDIATE sql_statement INTO avail_flag;
    if avail_flag = 1
    then
    /*
    #######################CHECKOUT TRANSACTION#######################;
    */
    SET TRANSACTION NAME 'CHECKOUT';
  
     SELECT TO_CHAR
    (SYSDATE, 'YYYY-MM-DD HH24:MI:SS') into current_datetime
     FROM DUAL;
     
    sql_statement := '
    INSERT INTO '||user_table||'_CO_'||table_name||'
    VALUES (
    '''||user_id||''',
    '''||search_variable||''',
    '''||library_id||''',
    TIMESTAMP'''||current_datetime||''',
    NULL
    )';
    
    DBMS_OUTPUT.PUT_LINE(sql_statement);
    
    EXECUTE IMMEDIATE sql_statement;
    
    /*update No. of avaialable hard copies*/
    
    sql_statement := 'UPDATE 
    SSINGH25.'||table_name||'_in_libraries T
    SET NO_OF_HARDCOPIES =NO_OF_HARDCOPIES-1
    WHERE T.library_id= '''||library_id||''' 
    and T.'||search_parameter|| '='''||search_variable||'''';
    DBMS_OUTPUT.PUT_LINE(sql_statement);
    EXECUTE IMMEDIATE sql_statement;
        
    COMMIT;
    
    END IF;

EXCEPTION
  	WHEN NO_DATA_FOUND THEN
		avail_flag:=0;
DBMS_OUTPUT.PUT_LINE(avail_flag);

END check_out_proc;

END check_out_pkg;