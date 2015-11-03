/*********************************************************************
user_room_pkg.sql : Display all available rooms to the user
**********************************************************************/
set serveroutput on;

CREATE OR REPLACE PACKAGE user_room_pkg AS 
user_error	EXCEPTION;
/*********************************************************************
user_fetches_rooms_proc : This procedure fetches all room data 
						based on the room size and library. 
**********************************************************************/
PROCEDURE user_fetches_rooms_proc(
	user_type 		VARCHAR2,
	room_size		VARCHAR2,
	library 		VARCHAR2,
	pref			OUT SYS_REFCURSOR,
	out_msg			OUT VARCHAR2
);

/*********************************************************************
user_reserves_rooms_proc : This procedure reserves a room for a user.
**********************************************************************/
PROCEDURE user_reserves_rooms_proc(
		user_type VARCHAR2,
		userid    VARCHAR2,
		in_room_no		VARCHAR2,
		in_library_id	VARCHAR2,
		start_time	VARCHAR2,
		end_time	VARCHAR2,
		out_msg		OUT VARCHAR2
);

/*********************************************************************
user_checksout_rooms_proc : This procedure checks out a reserved room 
							for a user.
**********************************************************************/
PROCEDURE user_checksout_rooms_proc(
		user_type VARCHAR2,
		userid    VARCHAR2,
		in_booking_id	VARCHAR2,
		out_msg		OUT VARCHAR2
);

/*********************************************************************
user_reserved_rooms_proc : This procedure displays all reserved rooms
							 to a user.
**********************************************************************/
PROCEDURE user_reserved_rooms_proc(
	in_user_type 		VARCHAR2,
	in_userid    		VARCHAR2,
	pref				OUT SYS_REFCURSOR,
	out_msg				OUT VARCHAR2
);

/*********************************************************************
del_unchkd_room_proc : This procedure is called from DBMS scheduler and
						updates the is_checked_out flag of 
						students_reserves_rooms/faculties_reserves_rooms
						to 2, if a user does not check out a room 1 hour
						after reservation start time.
**********************************************************************/
PROCEDURE del_unchkd_room_proc;
END user_room_pkg;
/
CREATE OR REPLACE PACKAGE BODY user_room_pkg 
IS

/*********************************************************************
user_fetches_rooms_proc : This procedure fetches all room data 
						based on the room size and library. 
**********************************************************************/
	PROCEDURE user_fetches_rooms_proc(
		user_type 		VARCHAR2,
		room_size		VARCHAR2,
		library 		VARCHAR2,
		pref		OUT SYS_REFCURSOR,
		out_msg		OUT VARCHAR2
	)
	IS
	sql_stmt VARCHAR2(32000);
	BEGIN
		sql_stmt:= 'SELECT '||
					' r.room_no, '||
					' r.capacity, ' ||
					' r.floor_no, '||
					' CASE r.room_type '||
	   				' WHEN ''C''' ||
	    			' THEN ''Conference''' ||
	    			' ELSE ''Study''' ||
	  				' END AS room_type,' ||
	  				' l.name ' ||
	  				' FROM rooms r, libraries l ' ||
	  				' WHERE r.library_id = l.library_id '||
	  				' AND r.capacity>=' || 'TO_NUMBER(' ||room_size||', ''99'')'||
	  				' AND r.library_id = ' || ''''||library ||'''';
	  	IF user_type ='S' THEN
	  		sql_stmt:=sql_stmt || ' AND r.room_type = ''S''';
	  	END IF;  
		OPEN pref FOR sql_stmt;

	  EXCEPTION
	 WHEN NO_DATA_FOUND THEN
	    out_msg:='No Rooms available at this moment!!';
	 WHEN OTHERS THEN
	 	   out_msg:=SQLERRM;
	END user_fetches_rooms_proc;

/*********************************************************************
user_reserves_rooms_proc : This procedure reserves a room for a user.
**********************************************************************/
	PROCEDURE user_reserves_rooms_proc(
		user_type VARCHAR2,
		userid    VARCHAR2,
		in_room_no		VARCHAR2,
		in_library_id	VARCHAR2,
		start_time	VARCHAR2,
		end_time	VARCHAR2,
		out_msg		OUT VARCHAR2
	)
	IS
	sql_stmt 		VARCHAR2(32000);
	v_start_time 	TIMESTAMP;
	v_end_time 		TIMESTAMP;
	v_id			VARCHAR2(50);
	sql_step		VARCHAR2(32000):='';
	v_count			NUMBER(2);
	booking_id		VARCHAR2(500);
	BEGIN
		sql_step:= 'Converting the start date and end date to TIMESTAMP';
			SELECT to_timestamp(start_time,'yyyy-mm-dd HH24 mi-ss') INTO v_start_time FROM DUAL;
			SELECT to_timestamp(end_time,'yyyy-mm-dd HH24 mi-ss') INTO v_end_time FROM DUAL;

		sql_step:= 'Validating date and time.';

	    IF (v_start_time< SYSTIMESTAMP OR v_end_time< SYSTIMESTAMP OR v_end_time< v_start_time)THEN
	      			out_msg:='Entered date and time is invalid!!';
	      			RAISE user_error;
	    END IF;
	    sql_step:= 'Validating maximum hours for reservation.';

		IF ( TO_NUMBER(EXTRACT( hour FROM (v_end_time -  v_start_time)))>3) THEN
			out_msg:='Please enter a duration less than or equal to 3 hours.';
			raise user_error;
		END IF;
		sql_step:= 'Validating the room type for students and users.';
			IF user_type='S' THEN
				SELECT COUNT(1) INTO v_count
				 FROM rooms r
				 WHERE r.room_no  = in_room_no
				 AND r.library_id = in_library_id
				 AND r.room_type  = 'S';
			END IF;
			DBMS_OUTPUT.PUT_LINE(v_count);
			IF v_count = 0 THEN
				out_msg:='The room is not available for you!!';
				RAISE USER_ERROR; 
			END IF;	
		
		sql_step:= 'Validating date and time for existing reservation.';

		FOR item IN (
	    	SELECT sr.reserv_start_time AS s_time,sr.reserv_end_time as e_time
	  		FROM students_reserves_rooms sr
	  		WHERE sr.room_no = in_room_no 
	  			AND sr.library_id = in_library_id
	  			AND sr.is_checked_out  IN ('0','1')
	 		UNION
			SELECT fr.reserv_start_time AS s_time,fr.reserv_end_time as e_time
	  		FROM faculties_reserves_rooms fr
	  		WHERE fr.room_no = in_room_no 
	  			AND fr.library_id = in_library_id
	  			AND fr.is_checked_out  IN ('0','1')
	  ) LOOP
			IF((item.s_time<= v_start_time AND v_start_time <=item.e_time) OR (item.s_time<=v_end_time AND v_end_time<=item.e_time)) THEN
				out_msg:='Can not reserve. The entered time interval is already reserved!!';
				raise user_error;
			END IF;	
		END LOOP;
		sql_step:='creating booking_id';
			SELECT ROOM_BOOKING_ID_FUNC INTO booking_id FROM DUAL;
	    sql_step:= 'Inserting data into tables.';

		sql_stmt:= 'INSERT INTO';
		IF user_type='S' THEN
			BEGIN

				SELECT student_id INTO v_id FROM students WHERE user_id=userid;

			EXCEPTION
				WHEN NO_DATA_FOUND THEN
				out_msg:='The user does not exists!!';
			END;
		
			sql_stmt:=sql_stmt || ' students_reserves_rooms ';
		ELSIF user_type='F' THEN
			BEGIN
				SELECT faculty_id INTO v_id FROM faculties WHERE user_id=userid;
			EXCEPTION
				WHEN NO_DATA_FOUND THEN
				out_msg:='The user does not exists!!';
			END;
			sql_stmt:=sql_stmt || ' faculties_reserves_rooms ';
		END IF;
			sql_stmt:=sql_stmt ||'
			VALUES (
			'''||booking_id||''',
			'''||v_id||''',
			'''||in_room_no||''',
			'''||in_library_id||''',
	          TIMESTAMP'''||start_time||''',
	          TIMESTAMP'''||end_time||''',
    		  ''0''
			)';
		delete from sud_dummy;
		insert into sud_dummy values(3,sql_stmt);
			COMMIT;
		EXECUTE IMMEDIATE	sql_stmt;
		COMMIT; 
		out_msg:= 'Room reservation successful, Room No- ' ||in_room_no|| ' Library: '|| in_library_id || ' Start time : '||v_start_time|| ' End Time '|| v_end_time || ' and booking id :' || booking_id;
	EXCEPTION
		WHEN USER_ERROR THEN
			out_msg:= 'Error: ' || out_msg;
		WHEN OTHERS THEN
			out_msg:= sql_step || SQLERRM;
	END user_reserves_rooms_proc;

/*********************************************************************
user_checksout_rooms_proc : This procedure checks out a reserved room 
							for a user.
**********************************************************************/
	PROCEDURE user_checksout_rooms_proc(
		user_type VARCHAR2,
		userid    VARCHAR2,
		in_booking_id	VARCHAR2,
		out_msg		OUT VARCHAR2
	)
	IS
	sql_stmt 		VARCHAR2(32000):='';
	v_start_time 	TIMESTAMP;
	v_end_time 		TIMESTAMP;
	v_id			VARCHAR2(50);
	sql_step		VARCHAR2(32000):='';
	v_count			NUMBER(10);
	BEGIN
		sql_step:='Checking reservation exists or not.';	
				
		sql_stmt:= sql_stmt ||'
		SELECT
			 reserv_start_time,reserv_end_time  '||
		'FROM ';
		IF user_type='S' THEN
				BEGIN
					SELECT COUNT(1) INTO v_count FROM students_reserves_rooms WHERE ROOM_BOOKING_ID = in_booking_id;
					IF v_count=0 THEN
						out_msg:='No room is currently booked for you!!';			
						RAISE user_error;
					END IF;	
					SELECT student_id INTO v_id FROM students WHERE user_id=userid;

				EXCEPTION
					WHEN NO_DATA_FOUND THEN
					out_msg:='The user does not exists!!';
				END;
			
				sql_stmt:=sql_stmt || ' students_reserves_rooms ' ||
					' WHERE student_id = ' ||
					''''||v_id||'''';

			ELSIF user_type='F' THEN
				BEGIN
					SELECT COUNT(1) INTO v_count FROM faculties_reserves_rooms WHERE ROOM_BOOKING_ID = in_booking_id;
					IF v_count=0 THEN
						out_msg:='No room is currently booked for you!!';			
						RAISE user_error;
					END IF;	
					SELECT faculty_id INTO v_id FROM faculties WHERE user_id=userid;
				EXCEPTION
					WHEN NO_DATA_FOUND THEN
					out_msg:='The user does not exists!!';
				END;
				sql_stmt:=sql_stmt || ' faculties_reserves_rooms' ||
					' WHERE faculty_id = ' ||
					''''||v_id||'''';
		END IF;
		sql_stmt:= sql_stmt ||
      ' AND ROOM_BOOKING_ID = ' || ''''||in_booking_id ||'''';

		EXECUTE IMMEDIATE sql_stmt INTO v_start_time,v_end_time ;
		COMMIT;	
			
		sql_step:='validating whether checking out within 1 hour or not.';

		IF SYSTIMESTAMP<v_start_time OR SYSTIMESTAMP>(v_start_time+ INTERVAL '1' HOUR) OR v_end_time<SYSTIMESTAMP THEN
			out_msg:='You did not check out within our hour!!';
			RAISE USER_ERROR;
		END IF;
		sql_step:='Updating the table';
		sql_stmt:='UPDATE  ';
		IF user_type='S' THEN
				sql_stmt:=sql_stmt || ' students_reserves_rooms ' ||
					' SET is_checked_out = '||'''1''' ||
					' WHERE student_id = ' ||
					''''||v_id||'''';

			ELSIF user_type='F' THEN
				sql_stmt:=sql_stmt || ' faculties_reserves_rooms' ||
					' WHERE student_id = ' ||
					''''||v_id||'''';
		END IF;
		sql_stmt:= sql_stmt ||
      ' AND ROOM_BOOKING_ID = ' || ''''||in_booking_id ||'''';

		EXECUTE IMMEDIATE sql_stmt;
		COMMIT;	
	out_msg:='Check out successful!!';

	EXCEPTION
		WHEN USER_ERROR THEN
			out_msg:= 'Error: ' || out_msg;
		WHEN OTHERS THEN
			out_msg:=sql_step || SQLERRM;
	END user_checksout_rooms_proc;

/*********************************************************************
user_reserved_rooms_proc : This procedure displays all reserved rooms
							 to a user.
**********************************************************************/
	PROCEDURE user_reserved_rooms_proc(
	in_user_type 		VARCHAR2,
	in_userid    		VARCHAR2,
	pref				OUT SYS_REFCURSOR,
	out_msg				OUT VARCHAR2
	)
	IS
	sql_stmt 		VARCHAR2(32000):='';
	v_id			VARCHAR2(50);
	sql_step		VARCHAR2(32000):='';
	v_count			NUMBER(2);
	BEGIN
	sql_step:='Checking whether user has booked any room or not';
			sql_stmt:= 'SELECT '||
					' r.room_booking_id, '||
					' r.room_no, '||
					' r.reserv_start_time, '||
					' r.reserv_end_time, '||
					' CASE r.is_checked_out '||
	   				' WHEN ''0''' ||
	    			' THEN ''NO''' ||
	    			' ELSE ''YES''' ||
	  				' END AS is_checked_out,' ||
	  				' l.name FROM' ;
			IF in_user_type='S' THEN
				BEGIN

					SELECT student_id INTO v_id FROM students WHERE user_id=in_userid;

				EXCEPTION
					WHEN NO_DATA_FOUND THEN
					out_msg:='The user does not exists!!';
				END;
			
				sql_stmt:=sql_stmt || ' students_reserves_rooms r ,libraries l ' ||
					' WHERE student_id = ' ||
					''''||v_id||'''';

			ELSIF in_user_type='F' THEN
				BEGIN
					SELECT faculty_id INTO v_id FROM faculties WHERE user_id=in_userid;
				EXCEPTION
					WHEN NO_DATA_FOUND THEN
					out_msg:='The user does not exists!!';
				END;
				sql_stmt:=sql_stmt || ' faculties_reserves_rooms r, libraries l' ||
					' WHERE faculty_id = ' ||
					''''||v_id||'''';
		END IF;
	  sql_stmt:= sql_stmt ||
		' AND r.library_id = l.library_id ';
		insert into sud_dummy values(1,sql_stmt);
		COMMIT;	
	OPEN pref FOR sql_stmt;
EXCEPTION
	WHEN NO_DATA_FOUND THEN
		out_msg:='You have not booked any room!!';
	WHEN OTHERS THEN
			out_msg:=sql_step || SQLERRM;
END user_reserved_rooms_proc;

/*********************************************************************
del_unchkd_room_proc : This procedure is called from DBMS scheduler and
						updates the is_checked_out flag of 
						students_reserves_rooms/faculties_reserves_rooms
						to 2, if a user does not check out a room 1 hour
						after reservation start time.
**********************************************************************/
PROCEDURE del_unchkd_room_proc
IS
sql_stmt VARCHAR2(20000):='';
BEGIN
	sql_stmt:=' UPDATE '||
	' students_reserves_rooms '||
	' SET is_checked_out = ''2'''||
	' WHERE TO_NUMBER(EXTRACT( HOUR FROM (SYSTIMESTAMP - RESERV_START_TIME)))>=1 '||
	'AND is_checked_out = ''0''';
	insert into sud_dummy values(21,sql_stmt);
		commit;		
	EXECUTE IMMEDIATE sql_stmt;
	COMMIT;
	sql_stmt:= ' UPDATE '||
	' faculties_reserves_rooms  '||
	' SET is_checked_out = ''2'''||
	'WHERE TO_NUMBER(EXTRACT( HOUR FROM (SYSTIMESTAMP - RESERV_START_TIME)))>=1 '||
	'AND is_checked_out = ''0''';
	EXECUTE IMMEDIATE sql_stmt;
	COMMIT;

END del_unchkd_room_proc;

END user_room_pkg;
/
SHOW ERRORS