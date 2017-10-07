/*
 Dieses Skript startet das angegebene Skript und loggt die Ausfuehrung
 Parameter: P_SKRIPT: Das auszuführende Skript
            P_SCHRITT: Die Nummer des Schrittes
            P_BESCHREIBUNG: Die Beschreibung des Schrittes
*/

DEFINE P_SKRIPT = '&1';
DEFINE P_SCHRITT = '&2';
DEFINE P_BESCHREIBUNG = '&3';

INSERT INTO &SCHEMA_NAME_HAUPTSCHEMA..M_SCHEMA_LOG (SCHEMAVERSION, SCHEMAUPDATE, SCHRITT, BESCHREIBUNG, SKRIPT, SKRIPT_START, STATUS) 
       VALUES ('&SCHEMA_VERSION', '&SCHEMA_UPDATE', '&P_SCHRITT', '&P_BESCHREIBUNG', '&P_SKRIPT', systimestamp, 'wird ausgefuehrt');
COMMIT;
prompt &P_SKRIPT
@&P_SKRIPT
UPDATE &SCHEMA_NAME_HAUPTSCHEMA..M_SCHEMA_LOG SET SKRIPT_ENDE = systimestamp, STATUS = 'erfolgreich' WHERE SCHEMAVERSION = '&SCHEMA_VERSION' AND SCHEMAUPDATE = '&SCHEMA_UPDATE' AND SCHRITT = '&P_SCHRITT'; 
COMMIT;