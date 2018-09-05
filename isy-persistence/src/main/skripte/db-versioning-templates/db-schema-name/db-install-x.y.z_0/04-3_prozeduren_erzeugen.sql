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
 Beispielskript zum Anlegen von Stored Procedures.
*/
create procedure insert_sys(p_ids in varchar2) IS 
    l_rest      varchar2(32000);
    l_id        varchar2(6);
    l_pos       integer;    
begin 
    l_rest := P_IDS;
    while (l_rest is not null) loop
        l_pos := instr(l_rest, ';');
        if (l_pos > 0) then 
            l_id := substr(l_rest, 1, l_pos - 1);
            l_rest := substr(l_rest, l_pos + 1);
        else 
            l_id := l_rest;
            l_rest := null;
        end if;
        if (l_id is not null) then 
            insert into filter(sys) values (l_id);
        end if;
    end loop;
exception 
    when others then 
        raise; 
end;
/
