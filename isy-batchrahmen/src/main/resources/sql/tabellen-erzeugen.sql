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
CREATE TABLE BATCHSTATUS
(
    BATCHID                 VARCHAR2(255) NOT NULL,
    BATCHNAME               VARCHAR2(255),
    BATCHSTATUS             VARCHAR2(255),
    SATZNUMMERLETZTESCOMMIT NUMBER(19, 0),
    SCHLUESSELLETZTESCOMMIT VARCHAR2(255),
    DATUMLETZTERSTART       TIMESTAMP,
    DATUMLETZTERABBRUCH     TIMESTAMP,
    DATUMLETZTERERFOLG      TIMESTAMP,
    CONSTRAINT BATCHSTATUS_PK PRIMARY KEY (BATCHID)
);
