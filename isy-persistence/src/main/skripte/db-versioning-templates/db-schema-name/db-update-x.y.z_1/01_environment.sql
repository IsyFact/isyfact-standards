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
 This script contains all of the system's environment variables for the environment.
*/

-- -------------------------------------
-- Configuration: Database connections
-- -------------------------------------
-- Oracle-Database connection (Connection-String)
-- in form <user-id>/<pwd>@<IP-Address>:<port>/<db-instanz>
-- for SYS-Admin.
DEFINE SYSADMIN_CONNECTION      = 'template';

-- Oracle-Database connection (Connection-String)
-- in form <user-id>/<pwd>@<IP-Address>:<port>/<db-instanz>
-- for the user of the new schema to be created.
DEFINE USER_CONNECTION          = 'template';


-- -------------------------------------
-- Configuration: User
-- -------------------------------------
-- Name of user for the schema (is the same as the schema name).
-- Must match <user-id> in the USER_CONNECTION parameter.
DEFINE USERNAME                 = 'template';

-- Password of user for schema.
-- Must match <pwd> User in the USER_CONNECTION parameter.
DEFINE PASSWORD                 = 'template';

-- -------------------------------------
-- Configuration: User_Protrech
-- -------------------------------------
-- Name of user for Protokollrecherche
DEFINE USERNAME_PROTRECH        = 'template';

-- Password of user  for Protokollrecherche
DEFINE PASSWORD_PROTRECH        = 'template';

-- -------------------------------------
-- Configuration: NLS Settings
-- -------------------------------------
DEFINE NLS_LANG                 = '.UTF8';

