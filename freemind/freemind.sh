#!/bin/sh
echo $@
#
# Thanks to Peter Torngaard
#
full=$(dirname $0)
echo ${full}/lib/freemind.jar
java -cp ${full}/lib/jython.jar:\
${full}/lib/freemind.jar:\
${full}/lib/ant/lib/jaxb-api.jar:\
${full}/lib/ant/lib/jaxb-impl.jar:\
${full}/lib/ant/lib/jaxb-libs.jar:\
${full}/lib/ant/lib/namespace.jar:\
${full}/lib/ant/lib/relaxngDatatype.jar:\
${full}/lib/ant/lib/xsdlib.jar:\
${full}/lib/ant/lib/jax-qname.jar:\
${full}/lib/ant/lib/sax.jar:\
${full}/lib/ant/lib/dom.jar\
 freemind.main.FreeMind $@
