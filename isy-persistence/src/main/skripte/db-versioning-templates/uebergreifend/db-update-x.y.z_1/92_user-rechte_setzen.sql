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
 Dieses Skript erzeugt die benötigten Rechte für die Systeme bzw. entzieht nicht mehr benötigte Rechte wieder.
*/


-- Das Hauptschema darf keine DDL Operationen mehr auf der Tabelle TABLEX des Nebenschemas ausführen
REVOKE ALTER ON &SCHEMA_NAME_NEBENSCHEMA..TABLE1 FROM &SCHEMA_NAME_HAUPTSCHEMA;

-- Das Nebenschema darf die Sequence SEQ_TABLE2 im Hauptschema nutzen
GRANT SELECT, ALTER ON &SCHEMA_NAME_HAUPTSCHEMA..SEQ_TABLE2 TO &SCHEMA_NAME_NEBENSCHEMA;