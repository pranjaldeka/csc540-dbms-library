SET SERVEROUTPUT ON;
BEGIN
   FOR R IN (SELECT  table_name FROM user_tables) LOOP
      EXECUTE IMMEDIATE 'DROP TABLE '||R.table_name||' CASCADE CONSTRAINTS';
   END LOOP;
END;
/
CREATE TABLE DEPARTMENTS
(
	DEPT_ID VARCHAR2(20),
	NAME VARCHAR2(50) NOT NULL,
	PRIMARY KEY (DEPT_ID)
);
CREATE TABLE ADMIN(
  ADMIN_ID VARCHAR2(10),
  FIRST_NAME VARCHAR2(50) NOT NULL,
  LAST_NAME VARCHAR2(50) NOT NULL,
  PASSWORD VARCHAR2(50) NOT NULL,
  PRIMARY KEY (ADMIN_ID)
 ); 
CREATE TABLE FACULTIES
(
	FACULTY_ID VARCHAR2(10),
	USER_ID VARCHAR2(50) NOT NULL,
  	PASSWORD VARCHAR2(50) NOT NULL,
	FIRST_NAME VARCHAR2(50) NOT NULL,
	LAST_NAME VARCHAR2(50) NOT NULL,
	CATEGORY VARCHAR2(50),
	DEPT_ID VARCHAR2(10),
	NATIONALITY VARCHAR2(20),
	COURSE_ID VARCHAR2(50),
	PRIMARY KEY (FACULTY_ID),
	FOREIGN KEY (DEPT_ID) REFERENCES DEPARTMENTS(DEPT_ID),
	FOREIGN KEY (COURSE_ID) REFERENCES COURSES(COURSE_ID)	
);
CREATE TABLE COURSES
(
	COURSE_ID VARCHAR2(50),
	NAME VARCHAR2(50) NOT NULL,
	PRIMARY KEY (COURSE_ID)
);
CREATE TABLE FACULTY_TAKES_COURSES
(
COURSE_ID VARCHAR2(50) NOT NULL,
FACULTY_ID VARCHAR2(10),
FOREIGN KEY (COURSE_ID) REFERENCES COURSES(COURSE_ID),
FOREIGN KEY (FACULTY_ID) REFERENCES FACULTIES(FACULTY_ID)
);
CREATE TABLE DEGREE_TYPES
(
	DEGREE_TYPE_ID NUMBER(10),
	YEAR NUMBER(4),
	DEGREE VARCHAR2(50),
	CLASSIFICATION VARCHAR2(50),
	PRIMARY KEY (DEGREE_TYPE_ID)
);
CREATE TABLE STUDENTS
(
  STUDENT_ID VARCHAR2(10),
  USER_ID	VARCHAR2(50) NOT NULL,
  PASSWORD VARCHAR2(50) NOT NULL,
  FIRST_NAME VARCHAR2(50) NOT NULL,
  LAST_NAME VARCHAR2(50) NOT NULL,
  SEX VARCHAR2(1),
  PHONE_NUMBER VARCHAR2(20),
  ALT_PHONE_NUMBER VARCHAR2(20),
  DOB VARCHAR2(15) NOT NULL,
  ADDRESS VARCHAR2(200),
  NATIONALITY VARCHAR2(20),
  DEGREE_TYPE_ID NUMBER(10),
  DEPT_ID VARCHAR2(10),
  PRIMARY KEY (STUDENT_ID),
  FOREIGN KEY (DEPT_ID) REFERENCES DEPARTMENTS(DEPT_ID),
  FOREIGN KEY (DEGREE_TYPE_ID) REFERENCES DEGREE_TYPES(DEGREE_TYPE_ID)
);
CREATE TABLE STUDENTS_ENROLLS_COURSES
(
COURSE_ID VARCHAR2(50) NOT NULL,
STUDENT_ID  VARCHAR2(10),
PRIMARY KEY (COURSE_ID, STUDENT_ID),
FOREIGN KEY (COURSE_ID) REFERENCES COURSES(COURSE_ID),
FOREIGN KEY (STUDENT_ID) REFERENCES STUDENTS(STUDENT_ID)
);
CREATE TABLE LIBRARIES
(
	LIBRARY_ID VARCHAR2(10),
	NAME VARCHAR2(50) NOT NULL,
	PRIMARY KEY (LIBRARY_ID)
);
CREATE TABLE ROOMS
(
	ROOM_NO VARCHAR2(10),
	LIBRARY_ID VARCHAR2(10),
	CAPACITY NUMBER(10),
	FLOOR_NO NUMBER(10),
	ROOM_TYPE VARCHAR2(1) NOT NULL,
	PRIMARY KEY (ROOM_NO,LIBRARY_ID),
	FOREIGN KEY (LIBRARY_ID) REFERENCES LIBRARIES(LIBRARY_ID)
);
/*
CREATE TABLE CONFERENCE_ROOMS
(
	ROOM_NO NUMBER(10),
	LIBRARY NUMBER(10),
	CAPACITY NUMBER(50),
	FLOOR_NO NUMBER(10),
	PRIMARY KEY (ROOM_NO)
);
*/
CREATE TABLE CAMERAS
(
	CAMERA_ID VARCHAR2(10),
	--LIBRARY_ID VARCHAR2(10) NOT NULL,
	MAKE VARCHAR2(50),
	MODEL VARCHAR2(50),
	LENS_CONFIG VARCHAR2(50),
	MEMORY_AVAILABLE NUMBER(10),
	PRIMARY KEY (CAMERA_ID)
);

CREATE TABLE CAMERAS_IN_LIBRARIES
(
	CAMERA_ID VARCHAR2(10),
	LIBRARY_ID VARCHAR2(10) NOT NULL,
	NO_OF_HARDCOPIES NUMBER(10),
	PRIMARY KEY (CAMERA_ID,LIBRARY_ID),
	FOREIGN KEY (LIBRARY_ID) REFERENCES LIBRARIES(LIBRARY_ID)
);
CREATE TABLE CONFERENCE_PAPERS
(
	CONF_PAPER_ID VARCHAR2(10),
	CONF_NAME VARCHAR2(100) NOT NULL,
	AUTHORS VARCHAR2(100) NOT NULL,
	YEAR_OF_PUBLICATION NUMBER(4) NOT NULL,
	TITLE VARCHAR2(100) NOT NULL,
	PRIMARY KEY (CONF_PAPER_ID)
);
CREATE TABLE CONFERENCE_PAPERS_IN_LIBRARIES
(
	CONF_PAPER_ID VARCHAR2(10),
	LIBRARY_ID VARCHAR2(10) NOT NULL,
	NO_OF_HARDCOPIES NUMBER(10),
	HAS_ELECTRONIC CHAR CHECK (HAS_ELECTRONIC IN (0,1)),
	PRIMARY KEY (CONF_PAPER_ID,LIBRARY_ID),
	FOREIGN KEY (LIBRARY_ID) REFERENCES LIBRARIES(LIBRARY_ID)
);
/*CREATE TABLE CONFERENCE_PAPERS_ELECTRONIC
(
	CONF_PAPER_ID NUMBER(10),
	LIBRARY_ID NUMBER(10) NOT NULL,
	CONF_NAME VARCHAR2(100) NOT NULL,
	AUTHORS VARCHAR2(100) NOT NULL,
	YEAR_OF_PUBLICATION NUMBER(4) NOT NULL,
	TITLE VARCHAR2(100) NOT NULL,
	NO_OF_HARDCOPIES NUMBER(10),
	HAS_ELECTRONIC CHAR CHECK (HAS_ELECTRONIC IN (0,1)),,
	PRIMARY KEY (CONF_PAPER_ID,LIBRARY_ID),
	FOREIGN KEY (LIBRARY_ID) REFERENCES LIBRARIES(ID)
);*/
CREATE TABLE JOURNALS
(
	ISSN VARCHAR2(10),
	AUTHORS VARCHAR2(100) NOT NULL,
	YEAR_OF_PUBLICATION NUMBER(4) NOT NULL,
	TITLE VARCHAR2(100) NOT NULL,
	PRIMARY KEY (ISSN)
);
CREATE TABLE JOURNALS_IN_LIBRARIES
(
	ISSN VARCHAR2(10),
	LIBRARY_ID VARCHAR2(10) NOT NULL,
	NO_OF_HARDCOPIES NUMBER(10),
	HAS_ELECTRONIC CHAR CHECK (HAS_ELECTRONIC IN (0,1)),
	PRIMARY KEY (ISSN,LIBRARY_ID),
	FOREIGN KEY (LIBRARY_ID) REFERENCES LIBRARIES(LIBRARY_ID)
);
CREATE TABLE BOOKS
(
	ISBN VARCHAR2(10),
	EDITION VARCHAR2(10) NOT NULL,
	PUBLISHER VARCHAR2(100) NOT NULL,
	AUTHORS VARCHAR2(100) NOT NULL,
	YEAR_OF_PUBLICATION NUMBER(4) NOT NULL,
	TITLE VARCHAR2(100) NOT NULL,
	/*RESERVED    CHAR CHECK (RESERVED IN ('Y','N'))
	*/PRIMARY KEY (ISBN)
);
CREATE TABLE BOOKS_IN_LIBRARIES
(
	ISBN VARCHAR2(10),
	LIBRARY_ID VARCHAR2(10) NOT NULL,
	NO_OF_HARDCOPIES NUMBER(10),
	HAS_ELECTRONIC CHAR CHECK (HAS_ELECTRONIC IN (0,1)),
	PRIMARY KEY (ISBN,LIBRARY_ID),
	FOREIGN KEY (LIBRARY_ID) REFERENCES LIBRARIES(LIBRARY_ID)
);

CREATE TABLE STUDENTS_CO_BOOKS
(
STUDENT_ID VARCHAR2(10),
ISBN VARCHAR2(10),
LIBRARY_ID VARCHAR2(10) NOT NULL,
ISSUE_DATE TIMESTAMP,
RETURN_DATE TIMESTAMP,
DUE_DATE TIMESTAMP,
PRIMARY KEY (ISBN,LIBRARY_ID,STUDENT_ID,ISSUE_DATE),
FOREIGN KEY (ISBN) REFERENCES BOOKS(ISBN),
FOREIGN KEY (LIBRARY_ID) REFERENCES LIBRARIES(LIBRARY_ID),
FOREIGN KEY (STUDENT_ID) REFERENCES STUDENTS(STUDENT_ID)
);
CREATE TABLE STUDENTS_CO_JOURNALS
(
STUDENT_ID VARCHAR2(10),
ISSN VARCHAR2(10),
LIBRARY_ID VARCHAR2(10) NOT NULL,
ISSUE_DATE TIMESTAMP,
RETURN_DATE TIMESTAMP,
DUE_DATE TIMESTAMP,
PRIMARY KEY (ISSN,LIBRARY_ID,STUDENT_ID,ISSUE_DATE),
FOREIGN KEY (ISSN) REFERENCES JOURNALS(ISSN),
FOREIGN KEY (LIBRARY_ID) REFERENCES LIBRARIES (LIBRARY_ID),
FOREIGN KEY (STUDENT_ID) REFERENCES STUDENTS(STUDENT_ID)
);
CREATE TABLE STUDENTS_CO_CONFERENCE_PAPERS
(
STUDENT_ID VARCHAR2(10),
CONF_PAPER_ID VARCHAR2(10),
LIBRARY_ID VARCHAR2(10) NOT NULL,
ISSUE_DATE TIMESTAMP,
RETURN_DATE TIMESTAMP,
DUE_DATE TIMESTAMP,
PRIMARY KEY (CONF_PAPER_ID,LIBRARY_ID,STUDENT_ID, ISSUE_DATE),
FOREIGN KEY (CONF_PAPER_ID) REFERENCES CONFERENCE_PAPERS(CONF_PAPER_ID),
FOREIGN KEY (LIBRARY_ID) REFERENCES LIBRARIES (LIBRARY_ID),
FOREIGN KEY (STUDENT_ID) REFERENCES STUDENTS(STUDENT_ID)
);
CREATE TABLE FACULTIES_CO_CONFERENCE_PAPERS
(
FACULTY_ID VARCHAR2(10),
CONF_PAPER_ID VARCHAR2(10),
LIBRARY_ID VARCHAR2(10) NOT NULL,
ISSUE_DATE TIMESTAMP,
RETURN_DATE TIMESTAMP,
DUE_DATE TIMESTAMP,
PRIMARY KEY (CONF_PAPER_ID,LIBRARY_ID,FACULTY_ID, ISSUE_DATE),
FOREIGN KEY (CONF_PAPER_ID) REFERENCES CONFERENCE_PAPERS(CONF_PAPER_ID),
FOREIGN KEY (LIBRARY_ID) REFERENCES LIBRARIES (LIBRARY_ID),
FOREIGN KEY (FACULTY_ID) REFERENCES FACULTIES(FACULTY_ID)
);
CREATE TABLE FACULTIES_CO_JOURNALS
(
FACULTY_ID VARCHAR2(10),
ISSN VARCHAR2(10),
LIBRARY_ID VARCHAR2(10) NOT NULL,
ISSUE_DATE TIMESTAMP,
RETURN_DATE TIMESTAMP,
DUE_DATE TIMESTAMP,
PRIMARY KEY (ISSN,LIBRARY_ID,FACULTY_ID, ISSUE_DATE),
FOREIGN KEY (ISSN) REFERENCES JOURNALS(ISSN),
FOREIGN KEY (LIBRARY_ID) REFERENCES LIBRARIES (LIBRARY_ID),
FOREIGN KEY (FACULTY_ID) REFERENCES FACULTIES(FACULTY_ID)
);
CREATE TABLE FACULTIES_CO_BOOKS
(
FACULTY_ID VARCHAR2(10),
ISBN VARCHAR2(10),
LIBRARY_ID VARCHAR2(10) NOT NULL,
ISSUE_DATE TIMESTAMP,
RETURN_DATE TIMESTAMP,
DUE_DATE TIMESTAMP,
PRIMARY KEY (ISBN,LIBRARY_ID,FACULTY_ID),
FOREIGN KEY (ISBN) REFERENCES BOOKS(ISBN),
FOREIGN KEY (LIBRARY_ID) REFERENCES LIBRARIES(LIBRARY_ID),
FOREIGN KEY (FACULTY_ID) REFERENCES FACULTIES(FACULTY_ID)
);


CREATE TABLE FACULTIES_CO_CAMERAS
(
CAMERA_BOOKING_ID  VARCHAR2(3200),	
FACULTY_ID VARCHAR2(10),
CAMERA_ID VARCHAR2(10),
LIBRARY_ID VARCHAR2(10) NOT NULL,
ISSUE_DATE TIMESTAMP,
RETURN_DATE TIMESTAMP,
DUE_DATE TIMESTAMP,
PRIMARY KEY (CAMERA_BOOKING_ID),
FOREIGN KEY (CAMERA_ID) REFERENCES CAMERAS(CAMERA_ID),
FOREIGN KEY (LIBRARY_ID) REFERENCES LIBRARIES(LIBRARY_ID),
FOREIGN KEY (FACULTY_ID) REFERENCES FACULTIES(FACULTY_ID)
);
CREATE TABLE STUDENTS_CO_CAMERAS
(
CAMERA_BOOKING_ID  VARCHAR2(3200),	
STUDENT_ID VARCHAR2(10),
CAMERA_ID VARCHAR2(10),
LIBRARY_ID VARCHAR2(10) NOT NULL,
ISSUE_DATE TIMESTAMP,
RETURN_DATE TIMESTAMP,
DUE_DATE TIMESTAMP,
PRIMARY KEY (CAMERA_BOOKING_ID),
FOREIGN KEY (CAMERA_ID) REFERENCES CAMERAS(CAMERA_ID),
FOREIGN KEY (LIBRARY_ID) REFERENCES LIBRARIES(LIBRARY_ID),
FOREIGN KEY (STUDENT_ID) REFERENCES STUDENTS(STUDENT_ID)
);
CREATE TABLE STUDENTS_RESERVES_ROOMS
(
ROOM_BOOKING_ID  VARCHAR2(3200),	
STUDENT_ID VARCHAR2(10),
ROOM_NO VARCHAR2(10),
LIBRARY_ID VARCHAR2(10) NOT NULL,
RESERV_START_TIME TIMESTAMP,
RESERV_END_TIME TIMESTAMP,
IS_CHECKED_OUT CHAR CHECK (IS_CHECKED_OUT IN (0,1,2)),
--PRIMARY KEY (STUDENT_ID,ROOM_NO,LIBRARY_ID,RESERV_START_TIME),
PRIMARY KEY (ROOM_BOOKING_ID),
FOREIGN KEY (LIBRARY_ID,ROOM_NO) REFERENCES ROOMS(LIBRARY_ID,ROOM_NO),
FOREIGN KEY (STUDENT_ID) REFERENCES STUDENTS(STUDENT_ID)
);
CREATE TABLE FACULTIES_RESERVES_ROOMS
(
ROOM_BOOKING_ID  VARCHAR2(3200),	
FACULTY_ID VARCHAR2(10),
ROOM_NO VARCHAR2(10),
LIBRARY_ID VARCHAR2(10) NOT NULL,
RESERV_START_TIME TIMESTAMP,
RESERV_END_TIME TIMESTAMP,
IS_CHECKED_OUT CHAR CHECK (IS_CHECKED_OUT IN (0,1,2)),
PRIMARY KEY (ROOM_BOOKING_ID),
FOREIGN KEY (LIBRARY_ID,ROOM_NO) REFERENCES ROOMS(LIBRARY_ID,ROOM_NO),
FOREIGN KEY (FACULTY_ID) REFERENCES FACULTIES(FACULTY_ID)
);
CREATE TABLE FACULTIES_RESERVES_COURSE_BOOK
(
COURSE_ID VARCHAR2(50) NOT NULL,
FACULTY_ID VARCHAR2(10),
ISBN VARCHAR2(10),
RESERV_START_TIME TIMESTAMP,
RESERV_END_TIME TIMESTAMP,
PRIMARY KEY (COURSE_ID,FACULTY_ID,ISBN),
FOREIGN KEY(COURSE_ID) REFERENCES COURSES(COURSE_ID),
FOREIGN KEY(FACULTY_ID) REFERENCES FACULTIES(FACULTY_ID),
FOREIGN KEY (ISBN) REFERENCES BOOKS(ISBN)
);

CREATE TABLE CHECKOUT_VALID_DURATION
(
	USER_TYPE VARCHAR2(1) NOT NULL,
	RESOURCE_TYPE VARCHAR2(1) NOT NULL,
	VALID_DURATION VARCHAR2(50)
);

CREATE TABLE RESOURCES_QUEUE
(
	RESOURCE_ID VARCHAR2(10),
	PATRON_ID VARCHAR2(10),
	PATRON_TYPE VARCHAR2(1),
	PRIORITY NUMBER(10),
	LIBRARY_ID VARCHAR2(10)
);


CREATE TABLE PATRON_DUES
(
	patron_id VARCHAR2(10),
    patron_type VARCHAR2(1),
    resource_type VARCHAR2(1),
    resource_id VARCHAR2(10),
	due_start_date TIMESTAMP,
	due_end_date TIMESTAMP,
	due_in_dollars NUMBER(10),
PRIMARY KEY (patron_id,patron_type,resource_type,resource_id,due_start_date)
);

CREATE TABLE PATRON_IN_HOLD
(
	patron_id VARCHAR2(10),
    patron_type VARCHAR2(1),
	hold_start_date TIMESTAMP,
	hold_end_date TIMESTAMP,
	PRIMARY KEY (patron_id,patron_type,hold_start_date)
)
;
