<?xml version="1.0" encoding="iso-8859-1"?>
<!--
    (c) by Christian Foltin, 2005
    adapted from mm2oowriter.xsl by Ondrej Popp  
    This file is licensed under the GPL.
-->
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:manifest="http://openoffice.org/2001/manifest">

	<xsl:output method="xml" version="1.0" indent="yes" encoding="UTF-8"
		doctype-public="-//OpenOffice.org//DTD Manifest 1.0//EN"
		doctype-system="Manifest.dtd" omit-xml-declaration="no"/>
	<xsl:strip-space elements="*"/>
	
	<xsl:template match="map">
		<manifest:manifest xmlns:manifest="http://openoffice.org/2001/manifest">
                  <manifest:file-entry manifest:media-type="application/vnd.sun.xml.writer" manifest:full-path="/"/>
                  <manifest:file-entry manifest:media-type="" manifest:full-path="Pictures/"/>
                  <manifest:file-entry manifest:media-type="text/xml" manifest:full-path="content.xml"/>
				  <manifest:file-entry manifest:media-type="text/xml" manifest:full-path="styles.xml"/>
                </manifest:manifest>
	</xsl:template>
	
</xsl:stylesheet>
