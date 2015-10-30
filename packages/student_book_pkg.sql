/*********************************************************************
student_book_pkg.sql : checks user profile whether it exists or not
**********************************************************************/
CREATE OR REPLACE PACKAGE student_book_pkg AS 
user_error       EXCEPTION;

PROCEDURE fetch_books_data_proc(
	p_ref			OUT SYS_REFCURSOR,
	out_err_msg		OUT VARCHAR2
);
END student_book_pkg;
/
CREATE OR REPLACE PACKAGE BODY student_book_pkg 
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
		      END
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

END student_book_pkg;
/