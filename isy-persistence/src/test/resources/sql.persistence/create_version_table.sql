/*
 * This script creates the tables needed for version monitoring.
 */

-- Verwaltungstabellen anlegen
create table m_schema_version
(
    version_nummer varchar2(25 char),
    update_nummer  varchar2(5 char),
    status         varchar2(25 char),
    constraint pk_m_schema_version primary key (version_nummer)
);
