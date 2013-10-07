#!/bin/bash
# this script is used to extract the freemind source from the SourceForge CVS
# and remove the jar files that can be replaced through externally packaged
# jar files.
if [ $# -lt 2 ]
then
	echo "Usage: $0 <rpm|deb> <package version> [<RCS tag>]" >&2
	exit 1
fi

pkgtype="$1"
pkgver="$2"
rcstag="$3"

mkdir -p freemind-${pkgver}
cd freemind-${pkgver}
if [ -n "${rcstag}" ]
then
	cvs -z3 -d:pserver:anonymous@freemind.cvs.sourceforge.net:/cvsroot/freemind export -r ${rcstag} freemind
	#cvs -z3 -d:ext:ewl@cvs.sourceforge.net:/cvsroot/freemind export -r ${rcstag} freemind
	#echo "CVS command not available, please extract from source file." && exit 2
fi

if [ ${pkgtype} = "deb" ]
then
	rm -v -fr freemind/windows-launcher
	#rm -v freemind/lib/ant/lib/jaxb-api.jar
	rm -v freemind/lib/ant/lib/dom.jar
	#rm -v freemind/lib/ant/lib/jax-qname.jar
	#rm -v freemind/lib/ant/lib/jaxb-impl.jar
	#rm -v freemind/lib/ant/lib/jaxb-libs.jar
	#rm -v freemind/lib/ant/lib/jaxb-xjc.jar
	#rm -v freemind/lib/ant/lib/namespace.jar
	rm -v freemind/lib/ant/lib/relaxngDatatype.jar
	rm -v freemind/lib/ant/lib/sax.jar
	#rm -v freemind/lib/ant/lib/xsdlib.jar
	rm -v freemind/lib/commons-lang-2.0.jar
	#rm -v freemind/lib/forms-1.0.5.jar
	rm -v freemind/lib/jarbundler-1.4.jar
	rm -v freemind/plugins/collaboration/jabber/commons-logging.jar
	rm -v freemind/plugins/collaboration/jabber/crimson-1.1.3.jar
	rm -v freemind/plugins/collaboration/jabber/jakarta-oro.jar
	rm -v freemind/plugins/collaboration/jabber/jaxp-1.1.jar
	rm -v freemind/plugins/collaboration/jabber/jdom.jar
	rm -v freemind/plugins/collaboration/jabber/log4j.jar
	#rm -v freemind/plugins/collaboration/jabber/muse.jar
	rm -v freemind/plugins/help/jhall.jar
	rm -v freemind/plugins/time/jcalendar.jar
	rm -v freemind/plugins/svg/js.jar
	rm -v freemind/plugins/svg/batik-*
	#rm -v freemind/plugins/svg/pdf-transcoder.jar
	rm -v freemind/plugins/svg/xerces_2_5_0.jar
	rm -v freemind/plugins/svg/xml-apis.jar
elif [ ${pkgtype} = "rpm" ]
then
	rm -v -fr freemind/windows-launcher
	#rm -v freemind/lib/ant/lib/jaxb-api.jar
	#rm -v freemind/lib/ant/lib/dom.jar
	#rm -v freemind/lib/ant/lib/jax-qname.jar
	#rm -v freemind/lib/ant/lib/jaxb-impl.jar
	#rm -v freemind/lib/ant/lib/jaxb-libs.jar
	#rm -v freemind/lib/ant/lib/jaxb-xjc.jar
	#rm -v freemind/lib/ant/lib/namespace.jar
	rm -v freemind/lib/ant/lib/relaxngDatatype.jar
	#rm -v freemind/lib/ant/lib/sax.jar
	#rm -v freemind/lib/ant/lib/xsdlib.jar
	rm -v freemind/lib/commons-lang-2.0.jar
	#rm -v freemind/lib/forms-1.0.5.jar
	rm -v freemind/lib/jarbundler-1.4.jar
	#rm -v freemind/plugins/collaboration/jabber/commons-logging.jar
	#rm -v freemind/plugins/collaboration/jabber/crimson-1.1.3.jar
	#rm -v freemind/plugins/collaboration/jabber/jakarta-oro.jar
	#rm -v freemind/plugins/collaboration/jabber/jaxp-1.1.jar
	#rm -v freemind/plugins/collaboration/jabber/jdom.jar
	#rm -v freemind/plugins/collaboration/jabber/log4j.jar
	#rm -v freemind/plugins/collaboration/jabber/muse.jar
	rm -v freemind/plugins/help/jhall.jar
	rm -v freemind/plugins/time/jcalendar.jar
	rm -v freemind/plugins/svg/js.jar
	rm -v freemind/plugins/svg/batik-*
	#rm -v freemind/plugins/svg/pdf-transcoder.jar
	#rm -v freemind/plugins/svg/xerces_2_5_0.jar
	#rm -v freemind/plugins/svg/xml-apis.jar
else
	echo "Package type parameter must be 'rpm' or 'deb'." >&2
	exit 1
fi

cd ..
tar cvzf freemind-${pkgver}.tar.gz freemind-${pkgver}
