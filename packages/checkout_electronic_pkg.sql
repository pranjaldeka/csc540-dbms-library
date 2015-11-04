SET SERVEROUTPUT ON;
CREATE OR REPLACE PACKAGE CHECK_OUT_ELECTRONIC_PKG
IS
  PROCEDURE CHECK_OUT_PROC(
      issue_type    IN VARCHAR2,
      issue_type_id IN VARCHAR2,
      USER_TYPE     IN VARCHAR2,
      USER_ID       IN VARCHAR2,
      LIBRARY_ID    IN VARCHAR2,
      OUTPUT OUT VARCHAR2 );
END CHECK_OUT_ELECTRONIC_PKG;
/
CREATE OR REPLACE PACKAGE BODY CHECK_OUT_ELECTRONIC_PKG
IS
  PROCEDURE CHECK_OUT_PROC(
      ISSUE_TYPE    IN VARCHAR2,
      ISSUE_TYPE_ID IN VARCHAR2,
      USER_TYPE     IN VARCHAR2,
      USER_ID       IN VARCHAR2,
      LIBRARY_ID    IN VARCHAR2,
      OUTPUT OUT VARCHAR2 )
  IS
    sql_statement         VARCHAR2(32000);
    user_id_column        VARCHAR2(100);
    table_name            VARCHAR2(100);
    table_name_electronic VARCHAR2(100);
    user_table            VARCHAR2(100);
    current_datetime      VARCHAR2(100);
    invalid               NUMBER(2);
    search_parameter      VARCHAR2(50);
    order_id              VARCHAR2(3200);
    primary_id            VARCHAR2(100);
    resource_exists_in_lib_flag VARCHAR2(10);
  BEGIN
    invalid          := 0;
    IF USER_TYPE      = 'S' THEN
      user_table     := 'STUDENTS';
      user_id_column := 'student_id';
    ELSIF USER_TYPE   = 'F' THEN
      user_table     := 'FACULTIES';
      user_id_column := 'faculty_id';
    ELSE
      invalid := 1;
    END IF;
    IF invalid = 0
      THEN
      IF ISSUE_TYPE            = 'B' THEN
        table_name            := 'BOOKS';
        table_name_electronic := 'EBOOKS';
        search_parameter      := 'ISBN';
        SELECT EBOOK_CHECKOUT_ID_FUNC INTO order_id from dual;
      ELSIF ISSUE_TYPE         = 'J' THEN
        table_name            := 'JOURNALS';
        table_name_electronic := 'EJOURNALS';
        search_parameter      := 'ISSN';
        SELECT EJOURNAL_CHECKOUT_ID_FUNC INTO order_id from dual;
      ELSIF ISSUE_TYPE         = 'P' THEN
        table_name            := 'CONFERENCE_PAPERS';
        table_name_electronic := 'ECONF_PAPERS';
        search_parameter      := 'CONF_PAPER_ID';
        SELECT ECONFPAPER_CHECKOUT_ID_FUNC INTO order_id from dual;
      ELSE
        invalid :=1;
      END IF;
     END IF;
      IF invalid = 0 THEN
        SELECT TO_CHAR (SYSDATE, 'YYYY-MM-DD HH24:MI:SS')
        INTO current_datetime
        FROM DUAL;
        IF invalid = 0 THEN
          sql_statement := '        
              SELECT '||user_id_column||'         
              FROM '||user_table||'        
              WHERE user_id = '''||user_id||'''';

          EXECUTE IMMEDIATE sql_statement INTO primary_id;

          resource_exists_in_lib_flag := 0;

          sql_statement               := 'SELECT 1          
              FROM SSINGH25.' || table_name || ' T1         
              inner join          
              SSINGH25.'||table_name||'_in_libraries T2         
              ON T1.'||search_parameter|| '=T2.'||search_parameter|| '         
              WHERE T2.LIBRARY_ID= '''||LIBRARY_ID||'''          
              and T1.'||search_parameter|| '='''||ISSUE_TYPE_ID||'''';

          EXECUTE IMMEDIATE sql_statement INTO resource_exists_in_lib_flag;
      
         IF resource_exists_in_lib_flag = 1 THEN
            
            BEGIN
              sql_statement := '            
                  INSERT INTO '||user_table||'_CO_'||table_name_electronic||'            
                  VALUES ('''||order_id||''', '''||primary_id||''',            
                  '''||ISSUE_TYPE_ID||''',  '''||LIBRARY_ID||''',            
                  TIMESTAMP'''||current_datetime||''')';
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
            OUTPUT := 'RESOURCE IS NOT IN ANY LIBRARY';
          END IF;
        ELSE
          OUTPUT := 'INVALID PARAMETERS';
        END IF;
      ELSE
        OUTPUT := 'INVALID PARAMETERS';
      END IF;
    EXCEPTION
    WHEN NO_DATA_FOUND THEN
      /*DBMS_OUTPUT.PUT_LINE('Invalid Inputs');*/
      OUTPUT := 'RESOURCE UNAVAILABLE';
    WHEN OTHERS THEN
      output :=SQLERRM;
    END check_out_proc;
  END check_out_electronic_pkg;
  /
  show errors