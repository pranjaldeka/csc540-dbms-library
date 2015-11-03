/*********************************************************************
ROOM_BOOKING_ID_FUNC : AUTO_INCREMENT function retruns unique booking
					 id for room reservation
**********************************************************************/

DROP SEQUENCE SEQ_ID;
CREATE SEQUENCE SEQ_ID MINVALUE 1 START WITH 1 INCREMENT BY 1 CACHE 10;

CREATE OR REPLACE FUNCTION "ROOM_BOOKING_ID_FUNC"
  RETURN VARCHAR2
IS
BEGIN
  RETURN 'RB'||SEQ_ID.NEXTVAL;
END;
/
SHOW ERRORS