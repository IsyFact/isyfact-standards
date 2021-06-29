-- Dieses Skript erzeugt die benötigten schemaübergreifenden Zugriffsrechte.
-- ----------------------------------------------------------------------------------------------------------------
-- This script creates the required cross-schema access rights.

-- Das Hauptschema erhält volle Zugriffsrechte auf die Tabelle TABLE1 des Nebenschemas.
-- ----------------------------------------------------------------------------------------------------------------
-- The main schema receives full access rights on the table TABLE1 of the sub schema.
GRANT ALTER, SELECT, INSERT, UPDATE, DELETE ON  &SCHEMA_NAME_NEBENSCHEMA..TABLE1 TO &SCHEMA_NAME_HAUPTSCHEMA;
