/*********************************************************************
student_publication_pkg.sql : checks user profile whether it exists or not
**********************************************************************/
set serveroutput on;

CREATE OR REPLACE PACKAGE student_publication_pkg AS 
user_error       EXCEPTION;

PROCEDURE fetch_books_data_proc(
	p_ref			OUT SYS_REFCURSOR,
	out_err_msg		OUT VARCHAR2
);

PROCEDURE fetch_journals_data_proc(
	p_ref			OUT SYS_REFCURSOR,
	out_err_msg		OUT VARCHAR2
);

PROCEDURE fetch_conf_papers_data_proc(
	p_ref			OUT SYS_REFCURSOR,
	out_err_msg		OUT VARCHAR2
);

PROCEDURE fetch_cameras_data_proc(
	p_ref			OUT SYS_REFCURSOR,
	out_err_msg		OUT VARCHAR2
);

PROCEDURE reserved_cameras_data_proc(
	p_ref			OUT SYS_REFCURSOR,
	userid			in VARCHAR2,
	user_type		in	varchar2,
	out_err_msg		OUT VARCHAR2
);
END student_publication_pkg;
/
CREATE OR REPLACE PACKAGE BODY student_publication_pkg 
IS
PROCEDURE fetch_books_data_proc(
	p_ref			OUT SYS_REFCURSOR,
	out_err_msg		OUT VARCHAR2
)
IS

BEGIN
	OPEN p_ref FOR
		SELECT 
		      b.isbn,
		      b.title,
		      b.authors,
		      b.publisher,
		      b.edition,
		      b.year_of_publication,
		      bil.no_of_hardcopies,
		      l.name,
		      CASE bil.has_electronic 
		        WHEN '1' THEN 'Hard Copy/Electronic'
		        ELSE 'Hard Copy'
		      END AS has_electronic
      FROM
		        books b,
		        books_in_libraries bil,
		        libraries l
       WHERE
		       b.isbn = bil.isbn AND
		       bil.library_id = l.library_id;
	
  EXCEPTION
 WHEN NO_DATA_FOUND THEN
    out_err_msg:='No Books found!!';
END fetch_books_data_proc;

PROCEDURE fetch_journals_data_proc(
	p_ref			OUT SYS_REFCURSOR,
	out_err_msg		OUT VARCHAR2
)
IS

BEGIN
	OPEN p_ref FOR
		SELECT 
		      j.issn,
		      j.authors,
		      j.year_of_publication,
		      j.title,
		      jil.no_of_hardcopies,
		      l.name,
		      CASE jil.has_electronic 
		        WHEN '1' THEN 'Hard Copy/Electronic'
		        ELSE 'Hard Copy'
		      END AS has_electronic
      FROM
		        journals j,
		        journals_in_libraries jil,
		        libraries l
       WHERE
		       j.issn = jil.issn AND
		       jil.library_id = l.library_id;
	
  EXCEPTION
 WHEN NO_DATA_FOUND THEN
    out_err_msg:='No Journals found!!';
END fetch_journals_data_proc;

PROCEDURE fetch_conf_papers_data_proc(
	p_ref			OUT SYS_REFCURSOR,
	out_err_msg		OUT VARCHAR2
)
IS

BEGIN
	OPEN p_ref FOR
		SELECT 
		      c.conf_paper_id,
		      c.conf_name,
		      c.authors,
		      c.year_of_publication,
			  c.title,
		      cil.no_of_hardcopies,
		      l.name,
		      CASE cil.has_electronic 
		        WHEN '1' THEN 'Hard Copy/Electronic'
		        ELSE 'Hard Copy'
		      END AS has_electronic
      FROM
		        conference_papers c,
		        conference_papers_in_libraries cil,
		        libraries l
       WHERE
		       c.conf_paper_id = cil.conf_paper_id AND
		       cil.library_id = l.library_id;
	
  EXCEPTION
 WHEN NO_DATA_FOUND THEN
    out_err_msg:='No Conference Paper found!!';
END fetch_conf_papers_data_proc;

PROCEDURE fetch_cameras_data_proc(
	p_ref			OUT SYS_REFCURSOR,
	out_err_msg		OUT VARCHAR2
)
IS

BEGIN
	OPEN p_ref FOR
		SELECT 
		      c.*,
		      l.name
		      
		FROM
		        cameras c,
		        cameras_in_libraries cil,
		        libraries l
       WHERE
		       c.camera_id = cil.camera_id AND
		       cil.library_id = l.library_id;
	
  EXCEPTION
 WHEN NO_DATA_FOUND THEN
    out_err_msg:='No Camera found!!';
END fetch_cameras_data_proc;


PROCEDURE reserved_cameras_data_proc(
	p_ref			OUT SYS_REFCURSOR,
	userid			in VARCHAR2,
	user_type		in	varchar2,
	out_err_msg		OUT VARCHAR2
)
IS
user_id_column 		varchar2(100);
table_name 			varchar2(100);
user_table 			varchar2(100);
primary_id VARCHAR2(100);
sql_statement varchar2(32000);
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
		sql_statement := 
          '
          SELECT '||user_id_column||' 
          FROM '||user_table||'
          WHERE user_id = '''||userid||'''';
          
          EXECUTE IMMEDIATE sql_statement INTO primary_id;
          
	
		
		sql_statement := 'SELECT 
		      c.camera_id,
			  cil.model,
		      l.name,
		      c.reservation_timestamp
		FROM
		        cameras_reservation c,
				cameras cil,
		        libraries l
       WHERE
		       c.camera_id = cil.camera_id
			   AND c.library_id = l.library_id
			   and c.patron_id = '''||primary_id||''' 
			   and c.patron_type= '''||user_type||'''';
	
	OPEN p_ref FOR  sql_statement;
	
  EXCEPTION
 WHEN NO_DATA_FOUND THEN
    out_err_msg:='No Camera reserved!!';
END reserved_cameras_data_proc;

END student_publication_pkg;


/