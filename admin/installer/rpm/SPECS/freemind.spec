# pretty messy stuff required to have one spec file for all vendors.
%define FM_release %{!?suse_version:%{?mdkversion:mdk}%{!?mdkversion:notsuse}}
%define FM_comp_ext %{?mdkversion:bz2}%{!?mdkversion:gz}
%define KDEDATA %{?suse_version:/opt/kde3/share}%{!?suse_version:%{_datadir}}

Name: freemind
Summary: Java Program for creating and viewing Mindmaps
URL: http://%{name}.sourceforge.net/
Version: 0.8.1
Release: 1%{FM_release}
License: GPL
Packager: Eric Lavarde <rpm at zorglub.s.bawue.de>
Vendor: (none)
Distribution: JPackage (contrib/unofficial)
Requires: java >= 0:1.4, jakarta-commons-lang, relaxngDatatype, msv-xsdlib, jgoodies-forms, xml-commons-apis, ws-jaxme, jakarta-commons-codec
Group: Applications/Productivity
#BuildRequires: classpathx-jaxp, fop
BuildRequires: java-devel >= 0:1.4, ant, batik, jakarta-commons-lang, relaxngDatatype, rhino, xerces-j2, msv-xsdlib, xml-commons-apis, jcalendar >= 1.2.2, javahelp2, jpackage-utils, jgoodies-forms, ws-jaxme, jakarta-commons-codec
BuildArchitectures: noarch
BuildRoot: %{_tmppath}/%{name}-%{version}-%{release}-buildroot
Source0: %{name}-%{version}.tar.gz
Source1: %{name}.desktop
Source2: x-%{name}.desktop
Source3: %{name}.1
Source4: urlsee
Source5: urlsee.1
Source6: %{name}-browser.README
Source7: %{name}-export.sh
Source8: x-%{name}.xml
Patch0: %{name}_build_xml.patch
Patch1: %{name}_properties.patch
Patch2: %{name}_user_properties.patch
Patch3: %{name}_sh.patch
Patch4: %{name}_manifest.patch
Patch5: %{name}_plugin_help.patch
Patch6: %{name}_plugin_svg.patch
Patch7: %{name}_plugin_time.patch
Patch8: %{name}_build_xml_version.patch
Patch9: %{name}_build_xml_jaxme.patch
Patch10: %{name}_fontsizeaction.patch
Patch11: %{name}_Resources_fr.patch
Patch12: %{name}_Resources_pl.patch
Patch13: %{name}_base64_tools.patch
Patch14: %{name}_base64_build.patch
Patch15: %{name}_base64_sh.patch
%description
Taking the Concept-Mapping approach to Human-Computer Interface design.
A mind mapper, and at the same time an easy-to-operate hierarchical editor
with strong emphasis on folding. These two are not really two different
things, just two different descriptions of a single application. Often used
for knowledge and content management.

(see also http://freemind.sourceforge.net/wiki/index.php/FreeMind_on_Linux)

#%package browser
#Summary: Java Applet for publishing Mindmaps produced with FreeMind
#Group: Applications/Productivity
#%description browser
#This package contains a java applet as well as an example of an HTML file to
#publish FreeMind maps using a standard web server.

%package plugins-svg
Summary: Java Plugin for FreeMind to export Mindmaps to SVG and PDF
Group: Applications/Productivity
#Requires: fop
Requires: java >= 0:1.4, %{name} >= %{version}, batik, rhino, xerces-j2, xml-commons-apis
%description plugins-svg
This plugin will add two menu points 'File -> Export -> SVG-Export' and
'File -> Export -> PDF-Export'.

%package plugins-help
Summary: Java Plugin for FreeMind to show an extended online help
Group: Applications/Productivity
Requires: java >= 0:1.4, %{name} >= %{version}, javahelp2
%description plugins-help
This plugin will add a menu point 'Help -> Online Help'.

%package plugins-time
Summary: Java Plugin for FreeMind to paste dates and set reminders
Group: Applications/Productivity
Requires: java >= 0:1.4, %{name} >= %{version}, jcalendar >= 1.2.2
%description plugins-time
This plugin will add a menu point 'Tools -> Show Calendar...'.

#%package plugins-collab-jabber
#Summary: Java Plugin for FreeMind to collaborate over Jabber.
#Group: Applications/Productivity
##Requires: jakarta-commons-logging, crimson, oro, classpathx-jaxp, jdom, log4j
#Requires: java >= 0:1.4, %{name} >= %{version}
#%description plugins-collab-jabber
#This plugin will add a menu point 'Tools -> Start Collaboration Mode'.
#
#Attention, this feature is still alpha-code and not guaranteed to work,
#it might munge your data, eat your dog and kick your baby (or the other way
#around).

%package javadoc
Summary: Javadoc for FreeMind, a Java Program for creating and viewing Mindmaps
Group: Development/Documentation
%description javadoc
This package contains the Javadoc documentation related to FreeMind.

%prep
%setup -q
%patch0 -p1
%patch1 -p1
%patch2 -p1
%patch3 -p0
%patch4 -p1
%patch5 -p1
%patch6 -p1
%patch7 -p1
%patch8 -p0
##%patch9 -p1 # jaxme patching (will only work with version 0.6)
%patch10 -p0
%patch11 -p0
%patch12 -p0
%patch13 -p0
%patch14 -p0
%patch15 -p0

%build
unset CLASSPATH
cd %{name}
%ant
%ant doc browser
cd ..
# chmod +x bin/dist/%{name}.sh

%install
rm -rf $RPM_BUILD_ROOT
install -d $RPM_BUILD_ROOT%{_datadir}/%{name} \
	$RPM_BUILD_ROOT%{_datadir}/%{name}/lib/ant/lib \
	$RPM_BUILD_ROOT%{_datadir}/%{name}/accessories \
	$RPM_BUILD_ROOT%{_javadocdir}/%{name} \
	$RPM_BUILD_ROOT%{_mandir}/man1 \
	$RPM_BUILD_ROOT%{_sysconfdir}/%{name} \
	$RPM_BUILD_ROOT%{_bindir}
install -m 755 bin/dist/%{name}.sh $RPM_BUILD_ROOT%{_datadir}/%{name}
install -m 644 bin/dist/doc/%{name}.mm $RPM_BUILD_ROOT%{_datadir}/%{name}
install bin/dist/lib/*.jar $RPM_BUILD_ROOT%{_datadir}/%{name}/lib
install bin/dist/lib/ant/lib/*.jar \
	$RPM_BUILD_ROOT%{_datadir}/%{name}/lib/ant/lib
install bin/dist/accessories/* $RPM_BUILD_ROOT%{_datadir}/%{name}/accessories
cp -a bin/dist/plugins $RPM_BUILD_ROOT%{_datadir}/%{name}
cp -a bin/dist/doc/javadoc/* $RPM_BUILD_ROOT%{_javadocdir}/%{name}
install bin/dist/patterns.xml $RPM_BUILD_ROOT%{_sysconfdir}/%{name}
install -m 644 %SOURCE3 $RPM_BUILD_ROOT%{_mandir}/man1	# freemind.1
install -m 755 %SOURCE4 $RPM_BUILD_ROOT%{_bindir}	# urlsee
install -m 644 %SOURCE5 $RPM_BUILD_ROOT%{_mandir}/man1	# urlsee.1
install -m 644 %SOURCE6 bin/dist/browser/README.txt

#kde
install -d -m 755 $RPM_BUILD_ROOT%{KDEDATA}/icons/hicolor/32x32/apps
install -d -m 755 $RPM_BUILD_ROOT%{KDEDATA}/icons/locolor/32x32/apps
install -m 644 freemind/images/FreeMindWindowIcon.png \
	$RPM_BUILD_ROOT%{KDEDATA}/icons/hicolor/32x32/apps/freemind.png
install -m 644 freemind/images/FreeMindWindowIcon.png \
	$RPM_BUILD_ROOT%{KDEDATA}/icons/locolor/32x32/apps/freemind.png
# install x-freemind.desktop
install -d -m 755 $RPM_BUILD_ROOT%{KDEDATA}/mimelnk/application
install -m 644 %SOURCE2 $RPM_BUILD_ROOT%{KDEDATA}/mimelnk/application

# FREEDESKTOP.ORG integration
install -d -m 755 $RPM_BUILD_ROOT%{_datadir}/applications
install -d -m 755 $RPM_BUILD_ROOT%{_datadir}/mime/packages
install -m 644 %SOURCE1 $RPM_BUILD_ROOT%{_datadir}/applications
install -m 644 %SOURCE8 $RPM_BUILD_ROOT%{_datadir}/mime/packages
install -d -m 755 $RPM_BUILD_ROOT%{_iconsbasedir}/32x32/apps
install -m 644 freemind/images/FreeMindWindowIcon.png \
	$RPM_BUILD_ROOT%{_iconsbasedir}/32x32/apps/freemind.png
# TODO: the size 48x48 would be mandatory!
#       %{_icons48dir} is equal to %{_iconsbasedir}/48x48/apps

cd $RPM_BUILD_ROOT
ln -s %{_datadir}/%{name}/%{name}.sh $RPM_BUILD_ROOT%{_bindir}/%{name}

%clean
rm -rf $RPM_BUILD_ROOT

%pre
%post
if [ -x $(which update-mime-database) ]
then # FREEDESKTOP.ORG integration
	update-mime-database %{_datadir}/mime
fi

%preun
%postun
if [ -x $(which update-mime-database) ]
then # FREEDESKTOP.ORG integration
	update-mime-database %{_datadir}/mime
fi

%files
%defattr(-,root,root)
%{_bindir}/%{name}
%{_bindir}/urlsee
%dir %{_datadir}/%{name}
%{_datadir}/%{name}/%{name}.sh
%{_datadir}/%{name}/%{name}.mm
%{_datadir}/%{name}/accessories
%{_datadir}/%{name}/lib
%dir %{_datadir}/%{name}/plugins
%{_mandir}/man1/%{name}.1.%{FM_comp_ext}
%{_mandir}/man1/urlsee.1.%{FM_comp_ext}
%{KDEDATA}/icons/hicolor/32x32/apps/freemind.png
%{KDEDATA}/icons/locolor/32x32/apps/freemind.png
%{KDEDATA}/mimelnk/application/x-%{name}.desktop
# FREEDESKTOP.ORG integration
%{_datadir}/applications/%{name}.desktop
%{_datadir}/mime/packages/x-%{name}.xml
%{_iconsbasedir}/32x32/apps/freemind.png
%doc %{name}/license %{name}/history.txt
%config %{_sysconfdir}/%{name}

#%files browser
#%defattr(-,root,root)
#%doc bin/dist/browser/*
%files plugins-svg
%defattr(-,root,root)
%{_datadir}/%{name}/plugins/ExportSvg.xml
%{_datadir}/%{name}/plugins/svg
%files plugins-help
%defattr(-,root,root)
%{_datadir}/%{name}/plugins/FreemindHelp.xml
%{_datadir}/%{name}/plugins/help
%files plugins-time
%defattr(-,root,root)
%{_datadir}/%{name}/plugins/TimeManagement.xml
%{_datadir}/%{name}/plugins/time
#%files plugins-collab-jabber
#%defattr(-,root,root)
#%{_datadir}/%{name}/plugins/CollaborationJabber.xml
#%{_datadir}/%{name}/plugins/collaboration/jabber
%files javadoc
%defattr(-,root,root)
%{_javadocdir}/%{name}

# date +"%a %b %d %Y"
%changelog
* Thu Feb 08 2008 Eric Lavarde <rpm at zorglub.s.bawue.de>
- Upstream version 0.8.1 to add Java 6 support.
- Better freemind.sh and urlsee scripts.
- Added support for batik package 1.7 (under SuSE).
* Fri Nov 11 2005 Eric Lavarde <rpm at zorglub.s.bawue.de>
- Corrected polish translation (thanks to hekto5 on sf.net).
- Add debug dpkg/rpm information and check for Sun/Blackdown VM in freemind.sh.
- Introduce usage of commons-codec to replace sun.misc.Base64Encoder/Decoder.
- Set java source and target version to 1.3 in order to avoid warnings from
  jikes.
- Suppress useless import of sun.java2d.loops.FontInfo from FontSizeAction.java.
- Improved French traduction.
- corrected urlsee to send output to /tmp/urlsee.log when DEBUG is
  defined because FreeMind discards all output.
- corrected urlsee to transform %20 into <space> when using 'see' or
  other local command.
* Mon Nov 07 2005 Eric Lavarde <rpm at zorglub.s.bawue.de>
- packaging version 0.8.0-5.
- integration using freedesktop.org standards.
* Wed Oct 19 2005 Eric Lavarde <rpm at zorglub.s.bawue.de>
- packaging version 0.8.0-3.
- rollbacking ws-jaxme integration, due to some incompatibilities.
- modification of freemind.sh to integrate forms and xml-commons-apis instead
  of dom & sax.
* Tue Oct 18 2005 Eric Lavarde <rpm at zorglub.s.bawue.de>
- packaging version 0.8.0-2.
- added dependency to ws-jaxme and jgoodies-forms.
- removed sax, dom and forms jar files from sources.
* Sat Sep 10 2005 Eric Lavarde <rpm at zorglub.s.bawue.de>
- packaging version 0.8.0-1.
* Sat Jul 24 2005 Eric Lavarde <rpm at zorglub.s.bawue.de>
- replacing jar files coming with freemind through external dependencies:
  batik, jakarta-commons-lang, relaxngDatatype, rhino, jcalendar, javahelp2
- packaging version 0.8.0.rc5 (-browser and -plugins-collab-jabber removed)
* Wed Jun 15 2005 Eric Lavarde <rpm at zorglub.s.bawue.de>
- continuing direction JPackage.org compatibility with 0.8.0.rc3.
- first working package but with all jars included.
- added urlsee and manpage to package.
* Tue Mar 22 2005 Eric Lavarde <rpm at zorglub.s.bawue.de>
- going for JPackage.org compatibility.
- upstream version 0_8_0_rc2.
* Sat Jul 03 2004 Eric Lavarde <rpm at zorglub.s.bawue.de>
- Slowly I start to come behind how to have a cross-platform spec...
* Mon Jun 28 2004 Eric Lavarde <rpm at zorglub.s.bawue.de>
- Did some more non-SuSE vendor adaptation (can't create anymore cross-platform)
* Mon Jun 21 2004 Eric Lavarde <rpm at zorglub.s.bawue.de>
- Trying to make at least spec file platform independent (with macros)
- Upgrade freemind.sh to better find the java binary (Thanks to Jan Schulz
  again) and copy patterns.xml to user directory.
- Updated man page to document all environment variables and files
  used by freemind start script.
- Remove user.properties because freemind does create it itself now
  (removes need for /etc/skel files).
* Sat May 01 2004 Eric Lavarde <rpm at zorglub.s.bawue.de>
- Changed to patches as sent to upstream team.
- Small fixes of the manpage.
- Taken KDE Integration from Packman package (menu and mime type)
  http://packman.links2linux.org/?query=freemind&action=search
- Taken description from same source.
- Added new property 'browser_other' for mozilla vs. konqueror.
* Tue Mar 13 2004 Eric Lavarde <rpm at zorglub.s.bawue.de>
- First packaged release
