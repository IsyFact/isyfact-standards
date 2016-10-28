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
 Dieses Skript startet das angegebene Skript und loggt die Ausfuehrung
 Parameter: P_SKRIPT: Das auszuführende Skript
            P_SCHRITT: Die Nummer des Schrittes
            P_BESCHREIBUNG: Die Beschreibung des Schrittes
*/

DEFINE P_SKRIPT = '&1';
DEFINE P_SCHRITT = '&2';
DEFINE P_BESCHREIBUNG = '&3';

INSERT INTO &USERNAME..M_SCHEMA_LOG (SCHEMAVERSION, SCHEMAUPDATE, SCHRITT, BESCHREIBUNG, SKRIPT, SKRIPT_START, STATUS) 
       VALUES ('&SCHEMA_VERSION', '&SCHEMA_UPDATE', '&P_SCHRITT', '&P_BESCHREIBUNG', '&P_SKRIPT', systimestamp, 'wird ausgefuehrt');
COMMIT;
prompt &P_SKRIPT
@&P_SKRIPT
UPDATE &USERNAME..M_SCHEMA_LOG SET SKRIPT_ENDE = systimestamp, STATUS = 'erfolgreich' WHERE SCHEMAVERSION = '&SCHEMA_VERSION' AND SCHEMAUPDATE = '&SCHEMA_UPDATE' AND SCHRITT = '&P_SCHRITT'; 
COMMIT;

