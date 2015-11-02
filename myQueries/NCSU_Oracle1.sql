SELECT r.room_no,
  r.capacity,
  r.floor_no,
  CASE r.room_type
    WHEN 'C'
    THEN 'Conference'
    ELSE 'Study'
  END AS room_type ,
  r.library_id ,
  l.name
FROM rooms r,
  libraries l
WHERE r.library_id                = l.library_id
AND (r.ROOM_NO,r.library_id) NOT IN
  ( SELECT room_no,library_id FROM students_reserves_rooms
  UNION
  SELECT room_no, library_id FROM faculties_reserves_rooms
  );
SELECT r.room_no,
  r.capacity,
  r.floor_no,
  CASE r.room_type
    WHEN 'C'
    THEN 'Conference'
    ELSE 'Study'
  END AS room_type,
  l.name
FROM rooms r,
  libraries l
WHERE r.library_id = l.library_id
AND NOT EXISTS
  (SELECT 1
  FROM students_reserves_rooms sr
  WHERE r.room_no  = sr.room_no
  AND r.library_id = sr.library_id
  )
AND NOT EXISTS
  (SELECT 1
  FROM faculties_reserves_rooms fr
  WHERE r.room_no  = fr.room_no
  AND r.library_id = fr.library_id
  );
DESC students_reserves_rooms; 

create table sud_dummy(id number, val clob);

declare
out_message varchar2(20000);
begin
user_room_pkg.user_reserves_rooms_proc('S','sud','R2','LIB2',' 2015-12-01 05:00:00',' 2015-12-01 06:00:00',out_message);
    dbms_output.put_line(OUT_MESSAGE );
 END;
 /
 
 declare
out_message varchar2(20000);
begin
user_room_pkg.user_checksout_rooms_proc('S','sud','RB2',out_message);
    dbms_output.put_line(OUT_MESSAGE );
 END;
 /
 
 SELECT * FROM ROOMS;
SELECT * FROM students_reserves_rooms;
TRUNCATE TABLE students_reserves_rooms;
 desc rooms;
-- All jobs
select * from user_scheduler_jobs;

delete from user_scheduler_jobs where job_name = 'INSERT_SUD_DATA';

-- Get information to job
select * from user_scheduler_job_log order by log_date desc;

-- Show details on job run
select * from user_scheduler_job_run_details;

select * from sud_dummy;
truncate table sud_dummy;
desc temp;
create table temp (val clob);
begin
dbms_scheduler.disable('INSERT_SUD_DATA', TRUE);
dbms_scheduler.stop_job('INSERT_SUD_DATA', TRUE);
end;
/
grant MANAGE SCHEDULER to SYS;

begin
dbms_scheduler.run_job('JOB_COLLECT_SESS_DATA',TRUE);
end;
 