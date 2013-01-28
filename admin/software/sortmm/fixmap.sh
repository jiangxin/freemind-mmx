#!/bin/bash --login
# Begin fixmap.sh

if [ "${1}" == "" ] ; then
	echo "filename missing"
	exit
fi

base=`basename ${1} .mm`

sed -e 's/ <p>/<p>/g'				\
    -e 's/ <\/p>/<\/p>/g'			\
    -e 's/ <\/head>/<\/head>/g'			\
    -e 's/"   COLOR="/" COLOR="/g'		\
    -e 's/"  COLOR="/" COLOR="/g'		\
    -e 's/"  POSITION="/" POSITION="/g'		\
    -e 's/STYLE="bubble" >/STYLE="bubble">/g'	\
    -e 's/<p>-<\/p>/<p>\&#x2022;<\/p>/g'	\
    -e 's/<p>- /<p>\&#x2022; /g'		\
						\
    ${base}.mm > ${base}.tmp

sortmm ${base}.tmp > ${base}.bak

rm ${base}.tmp

exit
# End fixmap.sh
