# csc540-dbms-library

Views:
------------------
user_view(user_id,user_password,user_type,first_name, last_name) : record all the users who can login to system.

Packages:
----------------
user_profile_pkg.sql : checks user profile, whether it exists or not.

Jars:
-----------------
ojdbc14.jar : Add this jar to project build settings.

One_time:
-----------------
one_time_courses
one_time_degree_type
one_time_department
one_time_faculties
one_time_faculty_takes_courses
one_time_students
one_time_students_enroll_courses
