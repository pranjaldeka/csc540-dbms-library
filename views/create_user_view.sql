create view user_view
 (  user_id,
    user_password,
    user_type,
    first_name,
    last_name
  )
as
select
	 STUDENT_NUMBER,
	 password,
	 'S', 
	 first_name, 
	 last_name
 from 
 	students
union all
select 
	FACULTY_NUMBER,
	password,
	'F', 
	first_name, 
	last_name 
	from 
faculties
union all
select 
	ADMIN_ID, 
	password,
	'A',
	first_name, 
	last_name 
from 
	ADMIN;
