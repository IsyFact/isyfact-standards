-- Dieses Skript erzeugt die benötigten Rechte für die Systeme bzw. entzieht nicht mehr benötigte Rechte wieder.
-- ----------------------------------------------------------------------------------------------------------------
-- This script creates the required rights for the systems or revokes rights that are no longer required.


-- Das Hauptschema darf keine DDL Operationen mehr auf der Tabelle TABLE1 des Nebenschemas ausführen
-- ----------------------------------------------------------------------------------------------------------------
-- The main schema is no longer permitted to execute ddl operations on the table TABLE1 of the sub schema
REVOKE ALTER ON &SCHEMA_NAME_NEBENSCHEMA..TABLE1 FROM &SCHEMA_NAME_HAUPTSCHEMA;

-- Das Nebenschema darf die Sequence SEQ_TABLE2 im Hauptschema nutzen
-- ----------------------------------------------------------------------------------------------------------------
-- The sub schema is allowed to use the sequence SEQ_TABLE2 of the main schema
GRANT SELECT, ALTER ON &SCHEMA_NAME_HAUPTSCHEMA..SEQ_TABLE2 TO &SCHEMA_NAME_NEBENSCHEMA;