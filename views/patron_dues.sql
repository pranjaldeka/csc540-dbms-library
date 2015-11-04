/* patron dues */
create or replace view patron_dues
 (  patron_id,
    patron_type,
    resource_type,
    resource_id,
	due_start_date,
	due_end_date,
	due_in_dollars
  )
as
 select
	 student_id,
	 'S', 
	 'B', 
	 isbn,
	 due_date,
	 return_date,
	 (trunc(coalesce(return_date,sysdate)- trunc(due_date)) * 2
	 from
 	students_co_books
	where coalesce(return_date,sysdate)>due_date
union all
 select
	 faculty_id,
	 'F', 
	 'B', 
	 isbn,
	 due_date,
	 return_date,
	 (trunc(coalesce(return_date,sysdate))- trunc(due_date)) * 2
	 from 
 	faculties_co_books
	where coalesce(return_date,sysdate)>due_date
union all
select
	 student_id,
	 'S', 
	 'J', 
	 issn,
	 due_date,
	 return_date,
	 (trunc(coalesce(return_date,sysdate))- trunc(due_date)) * 2
	 from 
 	students_co_journals
	where coalesce(return_date,sysdate)>due_date
union all
 select
	 faculty_id,
	 'F', 
	 'J', 
	 issn,
	 due_date,
	 return_date,
	 (trunc(coalesce(return_date,sysdate))- trunc(due_date)) * 2
	 from 
 	faculties_co_journals
	where coalesce(return_date,sysdate)>due_date
union all	
	select
	 student_id,
	 'S', 
	 'P', 
	 CONF_PAPER_ID,
	 due_date,
	 return_date,
	 (trunc(coalesce(return_date,sysdate))- trunc(due_date)) * 2
	 from 
 	students_co_conference_papers
	where coalesce(return_date,sysdate)>due_date
union all
 select
	 faculty_id,
	 'F', 
	 'P', 
	 CONF_PAPER_ID,
	 due_date,
	 return_date,
	 (trunc(coalesce(return_date,sysdate))- trunc(due_date)) * 2
	 from 
 	faculties_co_conference_papers
	where coalesce(return_date,sysdate)>due_date
union all
	select
	 student_id,
	 'S', 
	 'C', 
	 camera_id,
	 due_date,
	 return_date,
	 extract (day from (coalesce(return_date,sysdate)-due_date)) * 24 + (extract (hour from (coalesce(return_date,sysdate)-due_date))+1)
	 from 
 	students_co_cameras
	where coalesce(return_date,sysdate)>due_date
union all
 select
	 faculty_id,
	 'F', 
	 'C', 
	 camera_id,
	 due_date,
	 return_date,
	  extract (day from (coalesce(return_date,sysdate)-due_date)) * 24 + (extract (hour from (coalesce(return_date,sysdate)-due_date))+1)
	 from 
 	faculties_co_cameras
	where coalesce(return_date,sysdate)>due_date;

commit;