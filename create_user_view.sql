create view user_view
 (  user_id,
    user_password,
    user_type,
    first_name,
    last_name
  )
as
select
	 user_id,
	 password,
	 'S', 
	 first_name, 
	 last_name
 from 
 	students
union all
select 
	user_id,
	password,
	'F', 
	first_name, 
	last_name 
	from 
faculties
union all
select 
	admin_id, 
	password,
	'A',
	first_name, 
	last_name 
from 
	ADMIN;
