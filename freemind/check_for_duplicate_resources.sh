#!/bin/bash
for s in Res*.properties ; do 
#    echo ">>>>>>>>>>>>>>> Start $s" ; 
    for g in $(cat $s | cut -d '=' -f 1 | grep -v "^\\s*#" | grep -v "^\\s*$" | sort | uniq -d) ; do 
	echo "=======    File: $s, key $g"
	grep -n "^$g\\s*=" $s
    done ; 
#    echo "<<<<<<<<<<<<<<< End $s" ; 
done 
