#!/bin/sh
echo $@
#
# Thanks to Peter Torngaard
#
full=$(dirname $0)
echo ${full}/lib/freemind.jar
java -cp ${full}/:${full}/lib/jython.jar:${full}/lib/freemind.jar freemind.main.FreeMind $@
