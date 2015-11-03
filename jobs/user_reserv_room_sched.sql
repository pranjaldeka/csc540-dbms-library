/********************************************************************************************************
 Room reservation scheduler : This scheduler is called every half an hour and
						updates the is_checked_out flag of 
						students_reserves_rooms/faculties_reserves_rooms
						to 2, if a user does not check out a room 1 hour
						after reservation start time.
 ********************************************************************************************************/

BEGIN
DBMS_SCHEDULER.CREATE_JOB (
   job_name             => 'DEL_ROOM_RESERV_DATA',
   job_type             => 'PLSQL_BLOCK',
   job_action           => 'BEGIN user_room_pkg.del_unchkd_room_proc; END;',
   start_date           => systimestamp + interval '10' second,
   repeat_interval      => 'freq=MINUTELY;interval=5', 
   enabled              =>  TRUE,
   comments             => 'Delete room data that have not been checked out within 1 hour');
END;
/

/*
Disable a job

exec dbms_scheduler.disable( 'DEL_ROOM_RESERV_DATA' );


exec dbms_scheduler.set_attribute_null('DEL_ROOM_RESERV_DATA','schedule_name');


exec DBMS_SCHEDULER.SET_ATTRIBUTE ( name => 'DEL_ROOM_RESERV_DATA', attribute => 'repeat_interval', value => 'freq=MINUTELY;interval=5');

exec dbms_scheduler.enable( 'DEL_ROOM_RESERV_DATA' );

-- All jobs
select * from user_scheduler_jobs;

delete from user_scheduler_jobs where job_name = 'DEL_ROOM_RESERV_DATA';

-- Get information to job
select * from user_scheduler_job_log order by log_date desc;

-- Show details on job run
select * from user_scheduler_job_run_details;

*/