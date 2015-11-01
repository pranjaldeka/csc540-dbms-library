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
pref SYS_REFCURSOR;
a varchar(500);
b varchar(500);
c varchar(500);
d varchar(500);
e varchar(500);

begin
user_room_pkg.user_reserves_rooms_proc('S','sud','R1','LIB1',' 2015-11-01 05:00:00',' 2015-11-01 06:00:00',out_message);
LOOP
    FETCH pref INTO a;
    EXIT WHEN pref%NOTFOUND;
    dbms_output.put_line(a );
  END LOOP;
 -- CLOSE pref;
 END;
 /
 desc students_reserves_rooms;
 desc rooms;

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
AND r.capacity    >=TO_NUMBER('2', '99')
AND r.library_id   = LIB1
AND r.room_type    = 'S';
desc students_co_books;
select * from sud_dummy;
truncate table sud_dummy;
select * from rooms;
select * from students;
INSERT INTO students_reserves_rooms VALUES('S','R1','LIB2',
TO_TIMESTAMP('2015-11-01 05:00:00', 'yyyy-mm-dd HH24 mi-ss'),
TO_TIMESTAMP('2015-11-01 05:00:00', 'yyyy-mm-dd HH24 mi-ss'),
'0');


INSERT INTO students_reserves_rooms VALUES('S','R1','LIB1',timestamp'01-NOV-15 06.00.00.000000 AM',TIMESTAMP'01-NOV-15 05.00.00.000000 AM','0')