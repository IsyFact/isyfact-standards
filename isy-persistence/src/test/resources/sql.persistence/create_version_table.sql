/*
 * This script creates the tables needed for version monitoring.
 */

-- Verwaltungstabellen anlegen
create table m_schema_version
(
    version_nummer varchar2(25),
    update_nummer  varchar2(5),
    status         varchar2(25),
    constraint pk_m_schema_version primary key (version_nummer)
);
