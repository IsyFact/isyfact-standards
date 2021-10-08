-- Dieses Skript führt Datenbankoperationen aus, in die mehrere Schemata involviert sind.
-- ----------------------------------------------------------------------------------------------------------------
-- This script executes database operations involving multiple schemas.

--------------------------------------------------------
-- Synonym für die Sequence SEQ_TABLE2 des Hauptschemas erstellen.
--------------------------------------------------------
-- Create synonym for the table SEQ_TABLE2 of the main schema.
--------------------------------------------------------
CREATE SYNONYM &SCHEMA_NAME_NEBENSCHEMA..SEQ_TABLE9 FOR &SCHEMA_NAME_HAUPTSCHEMA..SEQ_TABLE2;