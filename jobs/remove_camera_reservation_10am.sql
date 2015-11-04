BEGIN
  DBMS_SCHEDULER.create_job (
    job_name        => 'remove_camera_reservation_10am',
    job_type        => 'PLSQL_BLOCK',
    job_action      => 'begin camera_reserve_pkg.delete_camera_reserve_proc end;',
    start_date      =>  timestamp'2015-11-02 00:00:00',
    repeat_interval => 'freq=daily; byminute=0; bysecond=0;',
    enabled         => TRUE);
END;
