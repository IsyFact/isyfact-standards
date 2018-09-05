/*
 See the NOTICE file distributed with this work for additional
 information regarding copyright ownership.
 The Federal Office of Administration (Bundesverwaltungsamt, BVA)
 licenses this file to you under the Apache License, Version 2.0 (the
 License). You may not use this file except in compliance with the
 License. You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 implied. See the License for the specific language governing
 permissions and limitations under the License.
*/
/*
 Dieses Skript erzeugt die für die Versionsüberwachung benötigten Tabellen.
*/

-- Verwaltungstabellen anlegen
create table m_schema_log (
  schemaversion varchar2(25 char),
  schemaupdate varchar2(5 char),
  schritt varchar2(10),
  beschreibung varchar2(100 char),
  skript varchar2(100 char),
  skript_start timestamp,
  skript_ende timestamp,
  status varchar2(25 char),
  constraint pk_m_schema_log primary key (schemaversion, schemaupdate, schritt)
);

create table m_schema_version (
  version_nummer varchar2(25 char),
  update_nummer varchar2(5 char),
  status varchar2(25 char),
  constraint pk_m_schema_version primary key (version_nummer)
);

