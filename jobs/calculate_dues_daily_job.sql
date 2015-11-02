BEGIN
  DBMS_SCHEDULER.create_job (
    job_name        => 'refreshes_dues_on_resources',
    job_type        => 'PLSQL_BLOCK',
    job_action      => 'begin calculate_dues_pkg.calculate_dues_proc; end;',
    start_date      =>  timestamp'2015-11-02 00:00:00',
    repeat_interval => 'freq=daily; byminute=0; bysecond=0;',
    enabled         => TRUE);
END;

