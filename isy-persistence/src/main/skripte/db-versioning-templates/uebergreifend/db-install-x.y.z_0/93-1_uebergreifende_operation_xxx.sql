-- Dieses Skript führt Datenbankoperationen aus, in die mehrere Schemata involviert sind.
-- ----------------------------------------------------------------------------------------------------------------
-- This script executes database operations involving multiple schemas.


--------------------------------------------------------
-- Synonym für die Tabelle TABLE1 des Nebenschemas erstellen.
--------------------------------------------------------
-- Create synonym for the table TABLE1 of the sub schema.
--------------------------------------------------------
CREATE SYNONYM &SCHEMA_NAME_HAUPTSCHEMA..TABLE9 FOR &SCHEMA_NAME_NEBENSCHEMA..TABLE1;