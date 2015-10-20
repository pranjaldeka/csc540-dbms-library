create view user_view (user_id, user_password, user_type) as
select STUDENT_NUMBER, password,'S' from students
union all
select FACULTY_NUMBER, password,'F' from faculties
union all
select ADMIN_ID, password,'A' from ADMIN;
