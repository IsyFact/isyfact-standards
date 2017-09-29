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
 Dieses Skript fuehrt die übergreifenden Operationen schrittweise durch. 
 Zum Loggen der Schritte wird das Hauptschema verwendet.
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

-- Pruefen, dass der Status des Hauptschemas auf "bereit" steht
declare
  statushauptschema varchar2(6);
begin
  SELECT STATUS into statushauptschema 
  from &SCHEMA_NAME_HAUPTSCHEMA..m_schema_version 
  where version_nummer = '&SCHEMA_VERSION' and update_nummer = '&SCHEMA_UPDATE' and status = 'bereit';
exception
  when no_data_found
  then raise_application_error(-20000, 'Die übergreifenden Operationen konnten nicht ausgeführt werden, weil das Hauptschema nicht auf bereit steht.');
end;
/

-- Schritt 2: Schemaübergreifende Rechte setzen
CONNECT &SYSADMIN_CONNECTION
@92_user.sql
DISCONNECT

-- Schritt 3: Schemaübergreifende Operationen aufrufen
CONNECT &SYSADMIN_CONNECTION
@99_starte-skript-mit-logging.sql 93-1_uebergreifende_operation_xxx.sql '93-1' 'Schemaübergreifende Operation xxx'
-- Hier können weitere Skripte folgen
DISCONNECT

-- Schritt 4: Abschlussbearbeitung
CONNECT &SYSADMIN_CONNECTION 
@99_starte-skript-mit-logging.sql 94_abschlussbearbeitung.sql '94' 'Abschlussbearbeitung durchfuehren'
COMMIT;
DISCONNECT

-- Schritt 5: Benutzerrechte entziehen
CONNECT &SYSADMIN_CONNECTION 
@99_starte-skript-mit-logging.sql 95_user_rechte_entziehen.sql '95' 'Benutzerrechte entziehen'

-- Das Hauptschema auf 'gueltig' setzen.
update &SCHEMA_NAME_HAUPTSCHEMA..M_SCHEMA_VERSION set STATUS = 'gueltig' where VERSION_NUMMER = '&SCHEMA_VERSION' and UPDATE_NUMMER = '&SCHEMA_UPDATE';
COMMIT;
DISCONNECT


SPOOL OFF
EXIT
