<?xml version="1.0" encoding="iso-8859-1"?>
<!--
    (c) by Christian Foltin, 2005
    This file is licensed under the GPL.
-->
<xsl:stylesheet version="1.0" xmlns="http://www.w3.org/1999/xhtml"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:office="http://openoffice.org/2000/office"
	xmlns:style="http://openoffice.org/2000/style"
	xmlns:text="http://openoffice.org/2000/text"
	xmlns:table="http://openoffice.org/2000/table"
	xmlns:draw="http://openoffice.org/2000/drawing"
	xmlns:fo="http://www.w3.org/1999/XSL/Format"
	xmlns:xlink="http://www.w3.org/1999/xlink"
	xmlns:number="http://openoffice.org/2000/datastyle"
	xmlns:svg="http://www.w3.org/2000/svg"
	xmlns:chart="http://openoffice.org/2000/chart"
	xmlns:dr3d="http://openoffice.org/2000/dr3d"
	xmlns:math="http://www.w3.org/1998/Math/MathML"
	xmlns:form="http://openoffice.org/2000/form"
	xmlns:script="http://openoffice.org/2000/script">
	<xsl:output method="xml" version="1.0" indent="yes" encoding="iso-8859-1"
		doctype-public="-//OpenOffice.org//DTD OfficeDocument 1.0//EN"
		doctype-system="office.dtd" omit-xml-declaration="no"/>
	<xsl:strip-space elements="*"/>
	
	<xsl:template match="map">
		<office:document-content xmlns:office="http://openoffice.org/2000/office"
			xmlns:style="http://openoffice.org/2000/style"
			xmlns:text="http://openoffice.org/2000/text"
			xmlns:table="http://openoffice.org/2000/table"
			xmlns:draw="http://openoffice.org/2000/drawing"
			xmlns:fo="http://www.w3.org/1999/XSL/Format"
			xmlns:xlink="http://www.w3.org/1999/xlink"
			xmlns:number="http://openoffice.org/2000/datastyle"
			xmlns:svg="http://www.w3.org/2000/svg"
			xmlns:chart="http://openoffice.org/2000/chart"
			xmlns:dr3d="http://openoffice.org/2000/dr3d"
			xmlns:math="http://www.w3.org/1998/Math/MathML"
			xmlns:form="http://openoffice.org/2000/form"
			xmlns:script="http://openoffice.org/2000/script" office:class="text"
			office:version="1.0">
			<office:script/>
			<office:font-decls>
				<style:font-decl style:name="Tahoma1"
					fo:font-family="Tahoma, Lucidasans, &apos;Lucida Sans&apos;, &apos;Arial Unicode MS&apos;"/>
				<style:font-decl style:name="HG Mincho Light J"
					fo:font-family="&apos;HG Mincho Light J&apos;, &apos;MS Mincho&apos;, &apos;HG Mincho J&apos;, &apos;HG Mincho L&apos;, &apos;HG Mincho&apos;, Mincho, &apos;MS PMincho&apos;, &apos;HG Mincho Light J&apos;, &apos;MS Gothic&apos;, &apos;HG Gothic J&apos;, &apos;HG Gothic B&apos;, &apos;HG Gothic&apos;, Gothic, &apos;MS PGothic&apos;, &apos;Andale Sans UI&apos;, &apos;Arial Unicode MS&apos;, &apos;Lucida Sans Unicode&apos;, Tahoma"
					style:font-pitch="variable"/>
				<style:font-decl style:name="Nimbus Sans L1"
					fo:font-family="&apos;Nimbus Sans L&apos;"
					style:font-pitch="variable"/>
				<style:font-decl style:name="Tahoma"
					fo:font-family="Tahoma, Lucidasans, &apos;Lucida Sans&apos;, &apos;Arial Unicode MS&apos;"
					style:font-pitch="variable"/>
				<style:font-decl style:name="Nimbus Roman No9 L"
					fo:font-family="&apos;Nimbus Roman No9 L&apos;"
					style:font-family-generic="roman"
					style:font-pitch="variable"/>
				<style:font-decl style:name="Nimbus Sans L"
					fo:font-family="&apos;Nimbus Sans L&apos;"
					style:font-family-generic="swiss"
					style:font-pitch="variable"/>
			</office:font-decls>
			<office:automatic-styles/>
			<office:body>
				<text:sequence-decls>
					<text:sequence-decl text:display-outline-level="0"
						text:name="Illustration"/>
					<text:sequence-decl text:display-outline-level="0"
						text:name="Table"/>
					<text:sequence-decl text:display-outline-level="0"
						text:name="Text"/>
					<text:sequence-decl text:display-outline-level="0"
						text:name="Drawing"/>
				</text:sequence-decls>
				
				<xsl:apply-templates select="node"/>
			</office:body>
		</office:document-content>
	</xsl:template>
	
	<xsl:template match="node">
		<xsl:variable name="depth">
			<xsl:apply-templates select=".." mode="depthMesurement"/>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="$depth=0">
				<text:p text:style-name="P1">
					<xsl:value-of select="@TEXT"/>
				</text:p>
				
				<xsl:apply-templates select="hook|@LINK"/>
				<xsl:apply-templates select="node"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="ancestor::node[@FOLDED='true']">
						<xsl:apply-templates select=".." mode="childoutputUnordered">
							<xsl:with-param name="nodeText">
								<text:p text:style-name="Standard">
									<xsl:value-of select="@TEXT"/>
								</text:p>	
							</xsl:with-param>
						</xsl:apply-templates>						
					</xsl:when>
					<xsl:otherwise>
<!--						<xsl:apply-templates select=".."
							mode="childoutputOrdered">
							<xsl:with-param name="nodeText">
							</xsl:with-param>
						</xsl:apply-templates> -->
								<xsl:element name="text:h" namespace="text">
									<xsl:attribute name="text:style-name"
										namespace="text">
										<xsl:text>Heading </xsl:text><xsl:value-of
											select="$depth"/>
									</xsl:attribute>
									<xsl:attribute name="text:level"
										namespace="text"><xsl:value-of
											select="$depth"/></xsl:attribute>
									<xsl:value-of select="@TEXT"/>
								</xsl:element>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:apply-templates select="hook|@LINK"/>
				<xsl:apply-templates select="node"/>
			</xsl:otherwise>
		</xsl:choose>
		
	</xsl:template>
	
	<xsl:template match="hook"/>
	
	<xsl:template match="hook[@NAME='accessories/plugins/NodeNote.properties']">
		<xsl:choose>
			<xsl:when test="./text">
				<text:p text:style-name="Standard">
					<xsl:value-of select="./text"/>
				</text:p>
			</xsl:when>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="node" mode="childoutputOrdered">
		<xsl:param name="nodeText"></xsl:param>
			<text:ordered-list text:style-name="L1"
				text:continue-numbering="true">
				<text:list-item>
					<xsl:apply-templates select=".." mode="childoutputOrdered">
						<xsl:with-param name="nodeText"><xsl:copy-of
								select="$nodeText"/></xsl:with-param>
					</xsl:apply-templates>
				</text:list-item>
			</text:ordered-list>
	</xsl:template>
	
	<xsl:template match="node" mode="childoutputUnordered">
		<xsl:param name="nodeText"></xsl:param>
				<text:unordered-list>
					<text:list-item>
						<xsl:apply-templates select=".." mode="childoutputUnordered">
							<xsl:with-param name="nodeText"><xsl:copy-of
									select="$nodeText"/></xsl:with-param>
						</xsl:apply-templates>
					</text:list-item>
				</text:unordered-list>
	</xsl:template>
	
	<xsl:template match="map" mode="childoutputOrdered">
		<xsl:param name="nodeText"></xsl:param>
		<xsl:copy-of select="$nodeText"/>
	</xsl:template>
	<xsl:template match="map" mode="childoutputUnordered">
		<xsl:param name="nodeText"></xsl:param>
		<xsl:copy-of select="$nodeText"/>
	</xsl:template>

	<xsl:template match="node" mode="depthMesurement">
        <xsl:param name="depth" select=" '0' "/>
        <xsl:apply-templates select=".." mode="depthMesurement">
                <xsl:with-param name="depth" select="$depth + 1"/>
        </xsl:apply-templates>
	</xsl:template>
	<xsl:template match="map" mode="depthMesurement">
        <xsl:param name="depth" select=" '0' "/>
		<xsl:value-of select="$depth"/>
	</xsl:template>

		
	<!-- Give links out. -->
	<xsl:template match="@LINK">
		<text:p text:style-name="Standard">
			<xsl:element name="text:a" namespace="text">
				<xsl:attribute namespace="xlink" name="xlink:type">simple</xsl:attribute>
				<xsl:attribute namespace="xlink" name="xlink:href"><xsl:value-of select="."/>
				</xsl:attribute>
				<xsl:value-of select="."/>
			</xsl:element>
		</text:p>
	</xsl:template>
	
</xsl:stylesheet>
