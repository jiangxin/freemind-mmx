#!/bin/sh
echo $@
#
# Thanks to Peter Torngaard
#
full=$(dirname $0)
echo ${full}/lib/freemind.jar
java -jar ${full}/lib/freemind.jar $@

