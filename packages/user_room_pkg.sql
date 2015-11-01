/*********************************************************************
user_room_pkg.sql : Display all available rooms to the user
**********************************************************************/
set serveroutput on;

CREATE OR REPLACE PACKAGE user_room_pkg AS 
user_error	EXCEPTION;
PROCEDURE user_fetches_rooms_proc(
	user_type 		VARCHAR2,
	room_size		VARCHAR2,
	library 		VARCHAR2,
	pref			OUT SYS_REFCURSOR,
	out_msg			OUT VARCHAR2
);

PROCEDURE user_reserves_rooms_proc(
		user_type VARCHAR2,
		userid    VARCHAR2,
		in_room_no		VARCHAR2,
		in_library_id	VARCHAR2,
		start_time	VARCHAR2,
		end_time	VARCHAR2,
		out_msg		OUT VARCHAR2
);
PROCEDURE user_checksout_rooms_proc(
	user_type VARCHAR2,
	userid    VARCHAR2,
	room_no		VARCHAR2,
	library_id	VARCHAR2,
	start_time	VARCHAR2,
	out_msg		OUT VARCHAR2
);
END user_room_pkg;
/
CREATE OR REPLACE PACKAGE BODY user_room_pkg 
IS
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
	  	insert into sud_dummy values(2,sql_stmt);commit;			

		
		OPEN pref FOR sql_stmt;

	  EXCEPTION
	 WHEN NO_DATA_FOUND THEN
	    out_msg:='No Rooms available at this moment!!';
	 WHEN OTHERS THEN
	 	   out_msg:=SQLERRM;
	END user_fetches_rooms_proc;

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
				DBMS_OUTPUT.PUT_LINE('1111');
				out_msg:='The room is not available for you!!';
				RAISE USER_ERROR; 
			END IF;	
		
		sql_step:= 'Validating date and time for existing reservation.';

		FOR item IN (
	    	SELECT sr.reserv_start_time AS s_time,sr.reserv_end_time as e_time
	  		FROM students_reserves_rooms sr
	  		WHERE sr.room_no = in_room_no 
	  			AND sr.library_id = in_library_id
	 		UNION
			SELECT fr.reserv_start_time AS s_time,fr.reserv_end_time as e_time
	  		FROM faculties_reserves_rooms fr
	  		WHERE fr.room_no = in_room_no 
	  			AND fr.library_id = in_library_id
	  ) LOOP
			IF((item.s_time<= v_start_time AND v_start_time <=item.e_time) OR (item.s_time<=v_end_time AND v_end_time<=item.e_time)) THEN
				out_msg:='Can not reserve. The entered time interval is already reserved!!';
				raise user_error;
			END IF;	
		END LOOP;

	    sql_step:= 'Inserting data into tables.';

		sql_stmt:= 'INSERT INTO';
		IF user_type='S' THEN
			BEGIN

				SELECT student_id INTO v_id FROM students WHERE user_id=userid;

			EXCEPTION
				WHEN NO_DATA_FOUND THEN
				out_msg:='The user does not exists!!';
			END;
		
			sql_stmt:=sql_stmt || ' students_reserves_rooms';
		ELSIF user_type='F' THEN
			BEGIN
				SELECT faculty_id INTO v_id FROM faculties WHERE user_id=userid;
			EXCEPTION
				WHEN NO_DATA_FOUND THEN
				out_msg:='The user does not exists!!';
			END;
			sql_stmt:=sql_stmt || ' faculties_reserves_rooms';
		END IF;
			sql_stmt:=sql_stmt ||'
			VALUES (
			'''||v_id||''',
			'''||in_room_no||''',
			'''||in_library_id||''',
	          TIMESTAMP'''||start_time||''',
	          TIMESTAMP'''||end_time||''',
    		  ''0''
			)';

		EXECUTE IMMEDIATE	sql_stmt;
		COMMIT;
		out_msg:= 'Room reservation successful, Room No- ' ||in_room_no|| ' Library: '|| in_library_id || ' Start time : '||v_start_time|| ' End Time '|| v_end_time;
	EXCEPTION
		WHEN USER_ERROR THEN
			out_msg:= 'Error: ' || out_msg;
		WHEN OTHERS THEN
			out_msg:= sql_step || SQLERRM;
	END user_reserves_rooms_proc;
	PROCEDURE user_checksout_rooms_proc(
		user_type VARCHAR2,
		userid    VARCHAR2,
		room_no		VARCHAR2,
		library_id	VARCHAR2,
		start_time	VARCHAR2,
		out_msg		OUT VARCHAR2
	)
	IS
	sql_stmt 		VARCHAR2(32000):='';
	v_start_time 	TIMESTAMP;
	v_end_time 		TIMESTAMP;
	v_id			VARCHAR2(50);
	v_id_value		VARCHAR2(50);
	sql_step		VARCHAR2(32000):='';

	BEGIN
		sql_step:='converting start_time to TIMESTAMP.';	
		BEGIN
			SELECT to_timestamp(start_time,'yyyy-mm-dd HH24 mi-ss') INTO v_start_time FROM DUAL;
		EXCEPTION
					WHEN NO_DATA_FOUND THEN
					out_msg:='No room is currently booked for you!!';
		END;			
		sql_step:='extracting reserve end time from table.';	
		sql_stmt:= sql_stmt ||'
		SELECT
			 reserv_end_time  '||
		'FROM ';
		IF user_type='S' THEN
				BEGIN

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
      ' AND room_no = ' || ''''||room_no ||''''||
      ' AND library_id = ' || ''''||library_id ||''''||
      ' AND reserv_start_time = ' || ''''||v_start_time ||'''';

		insert into sud_dummy values(1,sql_stmt);
		COMMIT;	
		EXECUTE IMMEDIATE sql_stmt INTO v_end_time ;
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
      ' AND room_no = ' || ''''||room_no ||''''||
      ' AND library_id = ' || ''''||library_id ||''''||
      ' AND reserv_start_time = ' || ''''||v_start_time ||'''';

		insert into sud_dummy values(1,sql_stmt);
		COMMIT;	
		EXECUTE IMMEDIATE sql_stmt;
		COMMIT;	
	out_msg:='Check out successful!!';

	EXCEPTION
		WHEN USER_ERROR THEN
			out_msg:= 'Error: ' || out_msg;
		WHEN OTHERS THEN
			out_msg:=sql_step || SQLERRM;
	END user_checksout_rooms_proc;
END user_room_pkg;
/