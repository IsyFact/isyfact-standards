-- Dieses Skript führt die übergreifenden Operationen schrittweise durch.
-- Zum Loggen der Schritte wird das Hauptschema verwendet.
-- ----------------------------------------------------------------------------------------------------------------
-- This script executes the crosscutting operations step by step.
-- The main schema is used to log the steps.


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
-- ----------------------------------------------------------------------------------------------------------------
-- Step 1: Load environment variables
SPOOL &P_LOG_DATEI
@&P_ENVIRONMENT_SCRIPT

-- Pruefen, dass der Status des Hauptschemas auf "bereit" steht
-- ----------------------------------------------------------------------------------------------------------------
-- Check if the status of the main schema is 'bereit' (ready)
CONNECT &SYSADMIN_CONNECTION
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
DISCONNECT

-- Schritt 2: Schemaübergreifende Rechte setzen
-- ----------------------------------------------------------------------------------------------------------------
-- Step 2: Set cross-schema rights
CONNECT &SYSADMIN_CONNECTION
@92_user.sql
DISCONNECT

-- Schritt 3: Schemaübergreifende Operationen aufrufen
-- ----------------------------------------------------------------------------------------------------------------
-- Step 3: Execute cross-schema operations
CONNECT &SYSADMIN_CONNECTION
@99_starte-skript-mit-logging.sql 93-1_uebergreifende_operation_xxx.sql '93-1' 'Schemaübergreifende Operation xxx'
-- Hier können weitere Skripte folgen
-- ----------------------------------------------------------------------------------------------------------------
-- Additional scripts may follow here
DISCONNECT

-- Schritt 4: Abschlussbearbeitung
-- ----------------------------------------------------------------------------------------------------------------
-- Step 4: Perform closing operations
CONNECT &SYSADMIN_CONNECTION
@99_starte-skript-mit-logging.sql 94_abschlussbearbeitung.sql '94' 'Abschlussbearbeitung durchfuehren'
COMMIT;
DISCONNECT

-- Schritt 5: Benutzerrechte entziehen
-- ----------------------------------------------------------------------------------------------------------------
-- Step 5: Remove user privileges
CONNECT &SYSADMIN_CONNECTION
@99_starte-skript-mit-logging.sql 95_user_rechte_entziehen.sql '95' 'Benutzerrechte entziehen'

-- Das Hauptschema auf 'gueltig' setzen.
-- ----------------------------------------------------------------------------------------------------------------
-- Set the main schema to 'gueltig' (valid).
update &SCHEMA_NAME_HAUPTSCHEMA..M_SCHEMA_VERSION set STATUS = 'gueltig' where VERSION_NUMMER = '&SCHEMA_VERSION' and UPDATE_NUMMER = '&SCHEMA_UPDATE';
COMMIT;
DISCONNECT


SPOOL OFF
EXIT
