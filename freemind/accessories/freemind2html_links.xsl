<?xml version="1.0" encoding="UTF-8"?>

<!--
	File:        freemind2html_links.xsl
	Version:     0.1
	Description: A XSLT stylesheet to transform mindmap files created with
	FreeMind (http://freemind.sf.net) into HTML files with an image and hyperlinks to the URLs. 
-->
<xsl:stylesheet version="1.0"
                xmlns="http://www.w3.org/1999/xhtml" 
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		>
<!-- mozilla doesn't parse method xhtml (in xslt 2.0) -->
<xsl:output method="xml"
            version="1.0"
            encoding="UTF-8"
            doctype-public="-//W3C//DTD XHTML 1.1//EN"  
            doctype-system="http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd"
	    omit-xml-declaration="no"
	    />

<!-- fc, 20.10.2004: The following parameter is set by freemind. -->
<xsl:param name="destination_dir">./</xsl:param>
<xsl:param name="area_code"></xsl:param>

<!-- ### THE ROOT TEMPLATE ### -->

<xsl:template match="/">
<html>
<!-- Thanks to gulpman: -->
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>

<xsl:comment>This file has been created with freemind2html_links.xsl</xsl:comment>
<head>
	<title>Image map</title>
</head>

<body>
	<h1><xsl:call-template name="output-title" /></h1>
	<!-- place image -->
	<div style="width:96%; 	padding:2%; 	margin-bottom:10px; 	border: 0px; 	text-align:center; 	vertical-align:center;">
		<xsl:element name="img">
			<xsl:attribute name="src">
				<xsl:value-of select="$destination_dir"/>image.png</xsl:attribute>
			<xsl:attribute name="style">margin-bottom:10px; 	border: 0px; 	text-align:center; 	vertical-align:center;</xsl:attribute>
			<xsl:attribute name="alt">Imagemap</xsl:attribute>
			<xsl:attribute name="usemap">#fm_imagemap</xsl:attribute>
		</xsl:element>
	</div>
	<map name="fm_imagemap" id="fm_imagemap">
		<xsl:value-of select="$area_code" disable-output-escaping="yes"/>
	</map>
</body>

</html>
</xsl:template> <!-- xsl:template match="/" -->


</xsl:stylesheet>
