/*********************************************************************
ROOM_BOOKING_ID_FUNC : AUTO_INCREMENT function returns unique booking
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

/*********************************************************************
EBOOK_CHECKOUT_ID_FUNC : AUTO_INCREMENT function returns unique checkout
					 id for ebook checkout
**********************************************************************/

DROP SEQUENCE EB_SEQ_ID;
CREATE SEQUENCE EB_SEQ_ID MINVALUE 1 START WITH 1 INCREMENT BY 1 CACHE 10;

CREATE OR REPLACE FUNCTION "EBOOK_CHECKOUT_ID_FUNC"
  RETURN VARCHAR2
IS
BEGIN
  RETURN 'EB'||EB_SEQ_ID.NEXTVAL;
END;

/*********************************************************************
EJOURNAL_CHECKOUT_ID_FUNC : AUTO_INCREMENT function retruns unique checkout
					 id for ejournal checkout
**********************************************************************/

DROP SEQUENCE EJ_SEQ_ID;
CREATE SEQUENCE EJ_SEQ_ID MINVALUE 1 START WITH 1 INCREMENT BY 1 CACHE 10;

CREATE OR REPLACE FUNCTION "EJOURNAL_CHECKOUT_ID_FUNC"
  RETURN VARCHAR2
IS
BEGIN
  RETURN 'EB'||EJ_SEQ_ID.NEXTVAL;
END;

/*********************************************************************
ECONFPAPER_CHECKOUT_ID_FUNC : AUTO_INCREMENT function retruns unique checkout
					 id for econference paper checkout
**********************************************************************/

DROP SEQUENCE ECF_SEQ_ID;
CREATE SEQUENCE ECF_SEQ_ID MINVALUE 1 START WITH 1 INCREMENT BY 1 CACHE 10;

CREATE OR REPLACE FUNCTION "ECONFPAPER_CHECKOUT_ID_FUNC"
  RETURN VARCHAR2
IS
BEGIN
  RETURN 'EB'||ECF_SEQ_ID.NEXTVAL;
END;

/
SHOW ERRORS