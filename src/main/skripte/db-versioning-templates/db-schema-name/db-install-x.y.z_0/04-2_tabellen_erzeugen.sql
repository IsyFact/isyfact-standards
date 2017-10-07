/*
 See the NOTICE file distributed with this work for additional
 information regarding copyright ownership.
 The Federal Office of Administration (Bundesverwaltungsamt, BVA)
 licenses this file to you under the Apache License, Version 2.0 (the
 License). You may not use this file except in compliance with the
 License. You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 implied. See the License for the specific language governing
 permissions and limitations under the License.
*/
/*
 Beispielskript für die Tabellenerzeugung.
*/

PURGE recyclebin;

--------------------------------------------------------
--  DDL for Table TABLE1
--------------------------------------------------------
CREATE TABLE TABLE1 (
    COL_A VARCHAR2(255 CHAR),
    COL_B VARCHAR2(255 CHAR),
    COL_C VARCHAR2(255 CHAR),
    COL_D NUMBER(19,0),
    COL_E VARCHAR2(255 CHAR),
    COL_F TIMESTAMP (6),
    COL_G TIMESTAMP (6),
    COL_H TIMESTAMP (6)
);

--------------------------------------------------------
--  DDL for Table TABLE2
--------------------------------------------------------
CREATE TABLE TABLE2 (
    COL_A NUMBER(10,0),
    COL_B TIMESTAMP (6),
    COL_C VARCHAR2(255 CHAR) NOT NULL,
    COL_D VARCHAR2(255 CHAR),
    COL_E VARCHAR2(2 CHAR),
    COL_F VARCHAR2(6 CHAR),
    COL_G VARCHAR2(6 CHAR),
    COL_H NUMBER(10,0),
    COL_I NUMBER(1,0),
    CONSTRAINT PK_TABLE2 PRIMARY KEY (COL_A) 
);

CREATE SEQUENCE SEQ_TABLE2 MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 1 CACHE 20 NOORDER  NOCYCLE ;


--------------------------------------------------------
--  DDL for Table TABLE3
--------------------------------------------------------
CREATE TABLE TABLE3 (
    COL_A NUMBER(10,0),
    COL_B VARCHAR2(255 CHAR) NOT NULL,
    COL_C VARCHAR2(255 CHAR),
    COL_D VARCHAR2(255 CHAR),
    COL_E VARCHAR2(32 CHAR),
    COL_F NUMBER(10,0),
    COL_G VARCHAR2(4000 CHAR),
    COL_H VARCHAR2(6 CHAR),
    CONSTRAINT PK_TABLE3 PRIMARY KEY (COL_A) 
);

CREATE SEQUENCE SEQ_TABLE3 MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 1 CACHE 20 NOORDER  NOCYCLE ;

--------------------------------------------------------
--  DDL for Index COL_D
--------------------------------------------------------
CREATE INDEX IDX_TABLE1_COL_D ON TABLE1 (COL_D);

