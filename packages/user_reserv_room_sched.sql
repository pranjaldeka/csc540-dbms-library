/* Room reservation scheduler */

BEGIN
DBMS_SCHEDULER.CREATE_JOB (
   job_name             => 'DEL_ROOM_RESERV_DATA',
   job_type             => 'PLSQL_BLOCK',
   job_action           => 'BEGIN user_room_pkg.del_unchkd_room_proc; END;',
   start_date           => systimestamp + interval '10' second,
   repeat_interval      => 'freq=MINUTELY;interval=30', 
   enabled              =>  TRUE,
   comments             => 'Delete room data that have not been checked out within 1 hour');
END;
/