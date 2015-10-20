/* Grant user access script*/

SET SERVEROUTPUT ON;
BEGIN
   FOR R IN (SELECT  table_name FROM user_tables) LOOP
      EXECUTE IMMEDIATE 'GRANT ALL ON '||R.table_name||' to sjolly';
   END LOOP;
END;
/

SET SERVEROUTPUT ON;
BEGIN
   FOR R IN (SELECT  VIEW_NAME FROM user_views) LOOP
      EXECUTE IMMEDIATE 'GRANT ALL ON '||R.view_name||' to dsingh4';
   END LOOP;
END;
/
