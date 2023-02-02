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
CREATE TABLE BATCH_STATUS
(
    BATCH_ID                   VARCHAR2(255) NOT NULL,
    BATCH_NAME                 VARCHAR2(255),
    BATCH_STATUS               VARCHAR2(255),
    SATZ_NUMMER_LETZTES_COMMIT NUMBER(19, 0),
    SCHLUESSEL_LETZTES_COMMIT  VARCHAR2(255),
    DATUM_LETZTER_START        TIMESTAMP,
    DATUM_LETZTER_ABBRUCH      TIMESTAMP,
    DATUM_LETZTER_ERFOLG       TIMESTAMP,
    CONSTRAINT BATCH_STATUS_PK PRIMARY KEY (BATCH_ID)
);
