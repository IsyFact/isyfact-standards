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
 Weitere system-spezifische Objekte von diesem Skript erzeugt werden.
*/

-- ----------------------------------------------------------------------------------------------------------------
-- Beispiel: Erstellen einer Queue-Tabelle und einer Queue.
-- ----------------------------------------------------------------------------------------------------------------

begin
    dbms_aqadm.create_queue_table(queue_table => 'XXX_queue_table', queue_payload_type => 'sys.aq$_jms_message');
    dbms_aqadm.create_queue(queue_name => 'XXX_queue', queue_table => 'XXX_queue_table', max_retries => 2147483647, retry_delay => 10);
end;
/