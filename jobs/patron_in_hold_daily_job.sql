BEGIN
  DBMS_SCHEDULER.create_job (
    job_name        => 'put_patron_in_hold_job',
    job_type        => 'PLSQL_BLOCK',
    job_action      => 'begin patron_in_hold_pkg.patron_in_hold_proc end;',
    start_date      =>  timestamp'2015-11-02 00:00:00',
    repeat_interval => 'freq=daily; byminute=0; bysecond=0;',
    enabled         => TRUE);
END;

