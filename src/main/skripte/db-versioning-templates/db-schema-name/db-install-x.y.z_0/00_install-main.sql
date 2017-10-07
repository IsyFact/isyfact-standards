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
Dieses Skript fuehrt die Installation des Systems schrittweise durch.
*/

WHENEVER OSERROR EXIT 9
WHENEVER SQLERROR EXIT FAILURE SQL.SQLCODE ROLLBACK
SET PAGESIZE 5000;
SET NEWPAGE NONE;
SET serveroutput ON
SET LINE 350;


DEFINE P_ENVIRONMENT_SCRIPT = &1;
DEFINE P_LOG_DATEI = &2;

DEFINE SCHEMA_VERSION = 'x.y.z';
DEFINE SCHEMA_UPDATE = '0';


-- Schritt 1: Umgebungsvariablen laden
SPOOL &P_LOG_DATEI
@&P_ENVIRONMENT_SCRIPT


-- Schritt 2: Tablespace erstellen
CONNECT &SYSADMIN_CONNECTION 
@02_tablespaces.sql
DISCONNECT


-- Schritt 3: Benutzer und Tabellen für Schema-Versionierung anlegen
CONNECT &SYSADMIN_CONNECTION 
@03_user.sql
DISCONNECT

-- Schritt 4: Erzeuge anwendungsspezifische Objekte
CONNECT &USER_CONNECTION
@04-1_version_erzeugen.sql
INSERT INTO M_SCHEMA_LOG (SCHEMAVERSION, SCHEMAUPDATE, SCHRITT, BESCHREIBUNG, SKRIPT, SKRIPT_START, SKRIPT_ENDE, STATUS) 
       VALUES ('&SCHEMA_VERSION', '&SCHEMA_UPDATE', '01', 'Umgebungsvariablen laden', '&&P_ENVIRONMENT_SCRIPT', systimestamp, systimestamp, 'erfolgreich');
INSERT INTO M_SCHEMA_LOG (SCHEMAVERSION, SCHEMAUPDATE, SCHRITT, BESCHREIBUNG, SKRIPT, SKRIPT_START, SKRIPT_ENDE, STATUS) 
       VALUES ('&SCHEMA_VERSION', '&SCHEMA_UPDATE', '02', 'Tablespaces erstellen', '02_tablespaces.sql', systimestamp, systimestamp, 'erfolgreich');
INSERT INTO M_SCHEMA_LOG (SCHEMAVERSION, SCHEMAUPDATE, SCHRITT, BESCHREIBUNG, SKRIPT, SKRIPT_START, SKRIPT_ENDE, STATUS) 
       VALUES ('&SCHEMA_VERSION', '&SCHEMA_UPDATE', '03', 'Benutzer anlegen', '03_user.sql', systimestamp, systimestamp, 'erfolgreich');
INSERT INTO M_SCHEMA_LOG (SCHEMAVERSION, SCHEMAUPDATE, SCHRITT, BESCHREIBUNG, SKRIPT, SKRIPT_START, SKRIPT_ENDE, STATUS) 
       VALUES ('&SCHEMA_VERSION', '&SCHEMA_UPDATE', '04-1', 'Schemaversionierung erzeugen', '04-1_version_erzeugen.sql', systimestamp, systimestamp, 'erfolgreich');
INSERT INTO M_SCHEMA_VERSION (VERSION_NUMMER, UPDATE_NUMMER, STATUS)
       VALUES ('&SCHEMA_VERSION', '&SCHEMA_UPDATE', 'ungueltig');
COMMIT;

@99_starte-skript-mit-logging.sql 04-2_tabellen_erzeugen.sql '04-2' 'Tabellen erzeugen'
@99_starte-skript-mit-logging.sql 04-3_prozeduren_erzeugen.sql '04-3' 'Prozeduren erzeugen'
@99_starte-skript-mit-logging.sql 04-4_sonstiges_erzeugen.sql '04-4' 'Sonstiges erzeugen'
DISCONNECT

-- Schritt 5: Abschlussbearbeitung
CONNECT &SYSADMIN_CONNECTION 
@99_starte-skript-mit-logging.sql 05_abschlussbearbeitung.sql '05' 'Abschlussbearbeitung durchfuehren'
COMMIT;
DISCONNECT

-- Schritt 6: Benutzerrechte entziehen
CONNECT &SYSADMIN_CONNECTION 
@99_starte-skript-mit-logging.sql 06_user_rechte_entziehen.sql '06' 'Benutzerrechte entziehen'

-- Hinweis: Werden nach Ausführung der Install-Skripte schemaübergreifende Operationen ausgeführt 
-- darf das Schema nicht auf 'gueltig' gesetzt werden, sondern muss auf 'bereit' gesetzt werden.
update &USERNAME..M_SCHEMA_VERSION set STATUS = 'gueltig' where VERSION_NUMMER = '&SCHEMA_VERSION' and UPDATE_NUMMER = '&SCHEMA_UPDATE';
COMMIT;
DISCONNECT

SPOOL OFF
EXIT
