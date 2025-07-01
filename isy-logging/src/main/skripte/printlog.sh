#! /bin/bash
# cat VORLAGE_testLogback.log | while read a; do echo $a | json_pp ; done

while read -r line; do
   echo "$line" | json_pp  | sed 's/\\r\\n\\tat/\n/g' | sed 's/\\r\\n/\n/g'
done < $1 
