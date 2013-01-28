#!/bin/bash --login
# Begin sortmm.sh

gcc `xml2-config --libs --cflags` -O2 -s -o sortmm sortmm.c

exit
# End sortmm.sh
