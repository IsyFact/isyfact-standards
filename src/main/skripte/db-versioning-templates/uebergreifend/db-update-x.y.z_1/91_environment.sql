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
 Dieses Skript enthält alle Umgebungsvariablen die notwendig sind um sich gegen die benötigten Schemas zu verbinden.
*/ 

-- -------------------------------------
-- Konfiguration: Datenbankverbindungen
-- -------------------------------------
-- Oracle-Datenbankverbindung (Connection-String) 
-- in der Form <user-id>/<pwd>@<IP-Adresse>:<port>/<db-instanz>
-- für den SYS-Admin.
DEFINE SYSADMIN_CONNECTION     = 'template';

-- Die verschiedenen Schema-Namen.
-- Name des Schemas/Benutzers des Hauptschemas.
DEFINE SCHEMA_NAME_HAUPTSCHEMA = 'template';
-- Name des Schemas/Benutzers eines Nebenschemas.
DEFINE SCHEMA_NAME_NEBENSCHEMA = 'template';

-- -------------------------------------
-- Konfiguration: NLS Einstellungen
-- -------------------------------------
DEFINE NLS_LANG                = '.UTF8';
