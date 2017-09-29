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
 Dieses Skript fuehrt das Update des Systems schrittweise durch.
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
DEFINE SCHEMA_UPDATE = '1';
DEFINE EXPECTED_SCHEMA_VERSION = 'x.y.z';
DEFINE EXPECTED_SCHEMA_UPDATE = '0';

-- Schritt 1a: Umgebungsvariablen laden
SPOOL &P_LOG_DATEI
@&P_ENVIRONMENT_SCRIPT

-- Schritt 1b: Schemametadaten aktualisieren
CONNECT &USER_CONNECTION
declare
  version_aktuell varchar2(25);
begin
  select version_nummer into version_aktuell
  from m_schema_version
  where version_nummer = '&EXPECTED_SCHEMA_VERSION' and update_nummer = '&EXPECTED_SCHEMA_UPDATE' and status = 'gueltig';
exception
  when no_data_found
  then raise_application_error(-20000, 'Falsche Version der Datenbank. Erwartete gültige Version ist ' || '&EXPECTED_SCHEMA_VERSION' || ' UPDATE ' || '&EXPECTED_SCHEMA_UPDATE' || '.');
end;
/

DELETE FROM M_SCHEMA_LOG WHERE SCHEMAVERSION = '&SCHEMA_VERSION' and SCHEMAUPDATE = '&SCHEMA_UPDATE';
DELETE FROM M_SCHEMA_VERSION;
INSERT INTO M_SCHEMA_VERSION (VERSION_NUMMER, UPDATE_NUMMER, STATUS)
       VALUES ('&SCHEMA_VERSION', '&SCHEMA_UPDATE', 'ungueltig');
COMMIT;
DISCONNECT

-- Schritt 2: Rechte setzen
CONNECT &SYSADMIN_CONNECTION 
@99_starte-skript-mit-logging.sql 02_user-rechte_setzen.sql '02' 'Rechte setzen'
COMMIT;
DISCONNECT

-- Schritt 3: Upgrade-Skripte aufrufen
CONNECT &USER_CONNECTION
@99_starte-skript-mit-logging.sql 03-1_upgrade-xxx.sql '03-01' 'Upgrade xxx'
-- Hier können weitere Skripte folgen
COMMIT;
DISCONNECT

-- Schritt 4: Abschlussbearbeitung
CONNECT &SYSADMIN_CONNECTION 
@99_starte-skript-mit-logging.sql 04_abschlussbearbeitung.sql '04' 'Abschlussbearbeitung durchfuehren'
COMMIT;
DISCONNECT

-- Schritt 5: Benutzerrechte entziehen
CONNECT &SYSADMIN_CONNECTION 
@99_starte-skript-mit-logging.sql 05_user_rechte_entziehen.sql '05' 'Benutzerrechte entziehen'

-- Hinweis: Werden nach Ausführung der Update-Skripte schemaübergreifende Operationen ausgeführt 
-- darf das Schema nicht auf 'gueltig' gesetzt werden, sondern muss auf 'bereit' gesetzt werden.
update &USERNAME..M_SCHEMA_VERSION set STATUS = 'gueltig' where VERSION_NUMMER = '&SCHEMA_VERSION' and UPDATE_NUMMER = '&SCHEMA_UPDATE';
COMMIT;
DISCONNECT

SPOOL OFF
EXIT
