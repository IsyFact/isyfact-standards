
DROP USER Beispiel CASCADE;
CREATE USER Beispiel IDENTIFIED BY Beispiel;
GRANT CONNECT, RESOURCE, UNLIMITED TABLESPACE to Beispiel;
GRANT CREATE SESSION TO Beispiel;
GRANT CREATE TABLE TO Beispiel;
GRANT CREATE VIEW TO Beispiel;
GRANT CREATE SEQUENCE TO Beispiel;
