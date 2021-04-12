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
 This script creates the required users and rights for the system.
*/

-- Explicitly CREATE SESSION instead of CONNECT-Rolle
GRANT CREATE SESSION TO &USERNAME;

-- Set temporary CREATE rights for initial creation.
GRANT CREATE TABLE TO &USERNAME;
GRANT CREATE SEQUENCE TO &USERNAME;
GRANT CREATE PROCEDURE TO &USERNAME;
GRANT CREATE TRIGGER TO &USERNAME;
GRANT CREATE VIEW TO &USERNAME;

-- Possibly further rights (queuing, materialized view, ...)


-- Give Protkollrechecher user the rights to CREATE SESSION.
GRANT CREATE SESSION TO &USERNAME_PROTRECH;
