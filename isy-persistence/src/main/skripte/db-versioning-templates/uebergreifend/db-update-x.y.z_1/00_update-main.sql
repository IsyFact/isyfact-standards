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

-- Umgebungsvariablen laden
SPOOL &P_LOG_DATEI
@&P_ENVIRONMENT_SCRIPT

-- Schemametadaten aktualisieren
CONNECT &SYSADMIN_CONNECTION
declare
  version_aktuell VARCHAR2(25 CHAR);
begin
  select version_nummer into version_aktuell
  from &SCHEMA_NAME_HAUPTSCHEMA..m_schema_version
  where version_nummer = '&SCHEMA_VERSION' and update_nummer = '&SCHEMA_UPDATE' and status = 'bereit';
exception
  when no_data_found
  then raise_application_error(-20000, 'Die übergreifenden Operationen konnten nicht ausgeführt werden, weil das Hauptschema in der Version ' || '&SCHEMA_VERSION' || ' UPDATE ' || '&SCHEMA_UPDATE' || ' nicht auf bereit steht.');
end;
/
DISCONNECT

-- Schritt 2: Benutzerrechte setzen
CONNECT &SYSADMIN_CONNECTION
@99_starte-skript-mit-logging.sql 92_user-rechte_setzen.sql '92' 'Benutzerrechte setzen'
COMMIT;
DISCONNECT

-- Schritt 3.x: Upgrade-Skripte aufrufen
CONNECT &SYSADMIN_CONNECTION
@99_starte-skript-mit-logging.sql 93-1_uebergreifende_operation_yyy.sql '93_01' 'Schemaübergreifende Operation yyy'
-- Hier können weitere Skripte folgen
COMMIT;
DISCONNECT

-- Schritt 4: Abschlussbearbeitung
CONNECT &SYSADMIN_CONNECTION 
@99_starte-skript-mit-logging.sql 94_abschlussbearbeitung.sql '94' 'Abschlussbearbeitung durchfuehren'
COMMIT;
DISCONNECT

-- Schritt 5: Benutzerrechte entziehen
CONNECT &SYSADMIN_CONNECTION 
@99_starte-skript-mit-logging.sql 95_user_rechte_entziehen.sql '95' 'Benutzerrechte entziehen'
update &SCHEMA_NAME_HAUPTSCHEMA..M_SCHEMA_VERSION set STATUS = 'gueltig' where VERSION_NUMMER = '&SCHEMA_VERSION' and UPDATE_NUMMER = '&SCHEMA_UPDATE';
COMMIT;
DISCONNECT

SPOOL OFF
EXIT
