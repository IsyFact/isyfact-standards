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
 Dieses Skript enthält alle Umgebungsvariablen des Systems für die Umgebung.
*/ 

-- -------------------------------------
-- Konfiguration: Datenbankverbindungen
-- -------------------------------------
-- Oracle-Datenbankverbindung (Connection-String) 
-- in der Form <user-id>/<pwd>@<IP-Adresse>:<port>/<db-instanz>
-- für den SYS-Admin.
DEFINE SYSADMIN_CONNECTION      = 'template';

-- Oracle-Datenbankverbindung (Connection-String) 
-- in der Form <user-id>/<pwd>@<IP-Adresse>:<port>/<db-instanz>
-- für den Benutzer des neu anzulegenden Schemas.
DEFINE USER_CONNECTION          = 'template';


-- -------------------------------------
-- Konfiguration: Tablespace
-- -------------------------------------
-- Name des Tablespace, in dem das Schema angelegt wird.
DEFINE TABLESPACE_NAME          = 'template';

-- Quota des User für das Schema.
DEFINE TABLESPACE_QUOTA         = 'template';

-- Name des Data-Files inklusiv vollständigen Pfad, 
-- das dem Tablespace zugeordnet wird.
DEFINE DATAFILE_NAME            = 'template';

-- Größe des Data-Files incl. Einheit (z.B. 20G für 20 GB).
DEFINE DATAFILE_SIZE            = 'template';

-- Größe des nächsten zu allokierenden Extends 
-- incl. Einheit (z.B. 5M für 5 MB).
DEFINE AUTOEXTEND_NEXT_SIZE     = 'template'; 

-- Maximale Größe des Data-Files incl. Einheit (z.B. 100G für 100GB).
DEFINE AUTOEXTEND_MAX_SIZE      = 'template';


-- -------------------------------------
-- Konfiguration: User
-- -------------------------------------
-- Name des Users für das Schema (ist gleich dem Schemanamen). 
-- Muss mit <user-id> im Parameter USER_CONNECTION übereinstimmen.
DEFINE USERNAME                 = 'template';

-- Kennwort des Users für das Schema. 
-- Muss mit <pwd> User im Parameter USER_CONNECTION übereinstimmen.
DEFINE PASSWORD                 = 'template';

-- -------------------------------------
-- Konfiguration: User_Protrech
-- -------------------------------------
-- Name des Users für die Protokollrecherche
DEFINE USERNAME_PROTRECH        = 'template';

-- Kennwort des Users  für die Protokollrecherche
DEFINE PASSWORD_PROTRECH        = 'template';

-- -------------------------------------
-- Konfiguration: NLS Einstellungen
-- -------------------------------------
DEFINE NLS_LANG                 = '.UTF8';

