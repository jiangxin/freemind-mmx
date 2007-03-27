<?xml version="1.0" encoding="UTF-8"?>
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
	<xsl:output method="xml" version="1.0" indent="no" encoding="UTF-8"
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
    <style:font-decl style:name="StarSymbol" fo:font-family="StarSymbol" style:font-charset="x-symbol"/>
    <style:font-decl style:name="starbats" fo:font-family="starbats" style:font-charset="x-symbol"/>
    <style:font-decl style:name="Lucidasans1" fo:font-family="Lucidasans"/>
    <style:font-decl style:name="Arial1" fo:font-family="Arial" style:font-pitch="variable"/>
    <style:font-decl style:name="Lucidasans" fo:font-family="Lucidasans" style:font-pitch="variable"/>
    <style:font-decl style:name="Times New Roman" fo:font-family="'Times New Roman'" style:font-family-generic="roman" style:font-pitch="variable"/>
    <style:font-decl style:name="Arial" fo:font-family="Arial" style:font-family-generic="swiss" style:font-pitch="variable"/>
			</office:font-decls>
			  <office:automatic-styles>
    <style:style style:name="P1" style:family="paragraph" style:parent-style-name="Standard" style:list-style-name="L1">
      <style:properties fo:font-style="normal" style:font-style-asian="normal" style:font-style-complex="normal"/>
    </style:style>
    <style:style style:name="P2" style:family="paragraph" style:parent-style-name="Standard" style:list-style-name="L2">
      <style:properties fo:font-style="normal" style:font-style-asian="normal" style:font-style-complex="normal"/>
    </style:style>
    <style:style style:name="P3" style:family="paragraph" style:parent-style-name="Standard">
      <style:properties fo:font-style="normal" style:font-style-asian="normal" style:font-style-complex="normal"/>
    </style:style>
<!--				<style:style style:name="Table1" style:family="table">
				  <style:properties style:width="16.999cm" table:align="margins"/>
				</style:style>
				<style:style style:name="Table1.A" style:family="table-column">
				  <style:properties style:column-width="5.666cm" style:rel-column-width="21845*"/>
				</style:style>
				<style:style style:name="Table1.A1" style:family="table-cell">
				  <style:properties fo:padding="0.097cm" fo:border-left="0.002cm solid #000000" fo:border-right="none" fo:border-top="0.002cm solid #000000" fo:border-bottom="0.002cm solid #000000"/>
				</style:style>
				<style:style style:name="Table1.C1" style:family="table-cell">
				  <style:properties fo:padding="0.097cm" fo:border="0.002cm solid #000000"/>
				</style:style>
				<style:style style:name="Table1.A2" style:family="table-cell">
				  <style:properties fo:padding="0.097cm" fo:border-left="0.002cm solid #000000" fo:border-right="none" fo:border-top="none" fo:border-bottom="0.002cm solid #000000"/>
				</style:style>
				<style:style style:name="Table1.C2" style:family="table-cell">
				  <style:properties fo:padding="0.097cm" fo:border-left="0.002cm solid #000000" fo:border-right="0.002cm solid #000000" fo:border-top="none" fo:border-bottom="0.002cm solid #000000"/>
				</style:style>-->
				<style:style style:name="T1" style:family="text">
				  <style:properties fo:font-weight="bold" style:font-weight-asian="bold" style:font-weight-complex="bold"/>
				</style:style>
				<style:style style:name="T2" style:family="text">
				  <style:properties fo:font-style="italic" style:font-style-asian="italic" style:font-style-complex="italic"/>
				</style:style>
				<style:style style:name="T3" style:family="text">
				  <style:properties style:text-underline-color="font-color" style:text-underline="single"/>
				</style:style>
    <text:list-style style:name="L1">
      <text:list-level-style-bullet text:level="1" text:style-name="Bullet Symbols" style:num-suffix="." text:bullet-char="●">
        <style:properties text:space-before="0.635cm" text:min-label-width="0.635cm" style:font-name="StarSymbol"/>
      </text:list-level-style-bullet>
      <text:list-level-style-bullet text:level="2" text:style-name="Bullet Symbols" style:num-suffix="." text:bullet-char="○">
        <style:properties text:space-before="1.27cm" text:min-label-width="0.635cm" style:font-name="StarSymbol"/>
      </text:list-level-style-bullet>
      <text:list-level-style-bullet text:level="3" text:style-name="Bullet Symbols" style:num-suffix="." text:bullet-char="■">
        <style:properties text:space-before="1.905cm" text:min-label-width="0.635cm" style:font-name="StarSymbol"/>
      </text:list-level-style-bullet>
      <text:list-level-style-bullet text:level="4" text:style-name="Bullet Symbols" style:num-suffix="." text:bullet-char="●">
        <style:properties text:space-before="2.54cm" text:min-label-width="0.635cm" style:font-name="StarSymbol"/>
      </text:list-level-style-bullet>
      <text:list-level-style-bullet text:level="5" text:style-name="Bullet Symbols" style:num-suffix="." text:bullet-char="○">
        <style:properties text:space-before="3.175cm" text:min-label-width="0.635cm" style:font-name="StarSymbol"/>
      </text:list-level-style-bullet>
      <text:list-level-style-bullet text:level="6" text:style-name="Bullet Symbols" style:num-suffix="." text:bullet-char="■">
        <style:properties text:space-before="3.81cm" text:min-label-width="0.635cm" style:font-name="StarSymbol"/>
      </text:list-level-style-bullet>
      <text:list-level-style-bullet text:level="7" text:style-name="Bullet Symbols" style:num-suffix="." text:bullet-char="●">
        <style:properties text:space-before="4.445cm" text:min-label-width="0.635cm" style:font-name="StarSymbol"/>
      </text:list-level-style-bullet>
      <text:list-level-style-bullet text:level="8" text:style-name="Bullet Symbols" style:num-suffix="." text:bullet-char="○">
        <style:properties text:space-before="5.08cm" text:min-label-width="0.635cm" style:font-name="StarSymbol"/>
      </text:list-level-style-bullet>
      <text:list-level-style-bullet text:level="9" text:style-name="Bullet Symbols" style:num-suffix="." text:bullet-char="■">
        <style:properties text:space-before="5.715cm" text:min-label-width="0.635cm" style:font-name="StarSymbol"/>
      </text:list-level-style-bullet>
      <text:list-level-style-bullet text:level="10" text:style-name="Bullet Symbols" style:num-suffix="." text:bullet-char="●">
        <style:properties text:space-before="6.35cm" text:min-label-width="0.635cm" style:font-name="StarSymbol"/>
      </text:list-level-style-bullet>
    </text:list-style>
    <text:list-style style:name="L2">
      <text:list-level-style-number text:level="1" text:style-name="Numbering Symbols" style:num-suffix="." style:num-format="1">
        <style:properties text:space-before="0.635cm" text:min-label-width="0.635cm"/>
      </text:list-level-style-number>
      <text:list-level-style-number text:level="2" text:style-name="Numbering Symbols" style:num-suffix="." style:num-format="1">
        <style:properties text:space-before="1.27cm" text:min-label-width="0.635cm"/>
      </text:list-level-style-number>
      <text:list-level-style-number text:level="3" text:style-name="Numbering Symbols" style:num-suffix="." style:num-format="1">
        <style:properties text:space-before="1.905cm" text:min-label-width="0.635cm"/>
      </text:list-level-style-number>
      <text:list-level-style-number text:level="4" text:style-name="Numbering Symbols" style:num-suffix="." style:num-format="1">
        <style:properties text:space-before="2.54cm" text:min-label-width="0.635cm"/>
      </text:list-level-style-number>
      <text:list-level-style-number text:level="5" text:style-name="Numbering Symbols" style:num-suffix="." style:num-format="1">
        <style:properties text:space-before="3.175cm" text:min-label-width="0.635cm"/>
      </text:list-level-style-number>
      <text:list-level-style-number text:level="6" text:style-name="Numbering Symbols" style:num-suffix="." style:num-format="1">
        <style:properties text:space-before="3.81cm" text:min-label-width="0.635cm"/>
      </text:list-level-style-number>
      <text:list-level-style-number text:level="7" text:style-name="Numbering Symbols" style:num-suffix="." style:num-format="1">
        <style:properties text:space-before="4.445cm" text:min-label-width="0.635cm"/>
      </text:list-level-style-number>
      <text:list-level-style-number text:level="8" text:style-name="Numbering Symbols" style:num-suffix="." style:num-format="1">
        <style:properties text:space-before="5.08cm" text:min-label-width="0.635cm"/>
      </text:list-level-style-number>
      <text:list-level-style-number text:level="9" text:style-name="Numbering Symbols" style:num-suffix="." style:num-format="1">
        <style:properties text:space-before="5.715cm" text:min-label-width="0.635cm"/>
      </text:list-level-style-number>
      <text:list-level-style-number text:level="10" text:style-name="Numbering Symbols" style:num-suffix="." style:num-format="1">
        <style:properties text:space-before="6.35cm" text:min-label-width="0.635cm"/>
      </text:list-level-style-number>
    </text:list-style>
			  </office:automatic-styles>
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
			<xsl:when test="$depth=0"><!-- Title -->
				<xsl:call-template name="output-nodecontent">
					<xsl:with-param name="style">Heading</xsl:with-param>
				</xsl:call-template>
				<xsl:apply-templates select="hook|@LINK"/>
				<xsl:call-template name="output-notecontent" />
				<xsl:apply-templates select="node"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="ancestor::node[@FOLDED='true']">
						<xsl:apply-templates select=".." mode="childoutputUnordered">
							<xsl:with-param name="nodeText">
								<xsl:call-template name="output-nodecontent">
									<xsl:with-param name="style">Standard</xsl:with-param>
								</xsl:call-template>
							</xsl:with-param>
						</xsl:apply-templates>						
					</xsl:when>
					<xsl:otherwise>
<!--						<xsl:apply-templates select=".."
							mode="childoutputOrdered">
							<xsl:with-param name="nodeText">
							</xsl:with-param>
						</xsl:apply-templates> -->
						<xsl:variable name="heading_level"><xsl:text>Heading </xsl:text><xsl:value-of
									select="$depth"/></xsl:variable>
						<xsl:element name="text:h">
							<xsl:attribute name="text:style-name" ><!--
								--><xsl:value-of select="$heading_level"/><!--
							--></xsl:attribute>
							<xsl:attribute name="text:level"><xsl:value-of
									select="$depth"/></xsl:attribute>
							<xsl:call-template name="output-nodecontent">
								<!--No Style for Headings.-->
								<xsl:with-param name="style"></xsl:with-param>
							</xsl:call-template>
						</xsl:element>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:apply-templates select="hook|@LINK"/>
				<xsl:call-template name="output-notecontent" />
				<xsl:apply-templates select="node"/>
			</xsl:otherwise>
		</xsl:choose>
		
	</xsl:template>
	
	<xsl:template match="hook"/>
	
<!--	<xsl:template match="hook[@NAME='accessories/plugins/NodeNote.properties']">
		<xsl:choose>
			<xsl:when test="./text">
				<text:p text:style-name="Standard">
					<xsl:value-of select="./text"/>
				</text:p>
			</xsl:when>
		</xsl:choose>
	</xsl:template> -->
	
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

	<xsl:template name="output-nodecontent">
		<xsl:param name="style">Standard</xsl:param>
			<xsl:choose>
			<xsl:when test="richcontent[@TYPE='NODE']">
				<xsl:apply-templates select="richcontent[@TYPE='NODE']/html/body" mode="richcontent">
					<xsl:with-param name="style" select="$style"/>
				</xsl:apply-templates>
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="$style = ''">
						<!--no style for headings. -->
						<xsl:call-template name="textnode" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:element name="text:p">
							<xsl:attribute name="text:style-name"><xsl:value-of select="$style"/></xsl:attribute>
							<xsl:call-template name="textnode" />
						</xsl:element>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
			</xsl:choose>
	</xsl:template> <!-- xsl:template name="output-nodecontent" -->
	
	<xsl:template name="output-notecontent">
		<xsl:if test="richcontent[@TYPE='NOTE']">
			<xsl:apply-templates select="richcontent[@TYPE='NOTE']/html/body" mode="richcontent" >
				<xsl:with-param name="style">Standard</xsl:with-param>					
			</xsl:apply-templates>
		</xsl:if>
	</xsl:template> <!-- xsl:template name="output-note" -->


	<xsl:template name="textnode">
		<xsl:call-template name="format_text">
			<xsl:with-param name="nodetext">
				<xsl:choose>
					<xsl:when test="@TEXT = ''"><xsl:text> </xsl:text></xsl:when>
					<xsl:otherwise><xsl:value-of select="@TEXT" /></xsl:otherwise>
				</xsl:choose>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template> <!-- xsl:template name="textnode" -->
	

	<!-- replace ASCII line breaks through ODF line breaks (br) -->
	<xsl:template name="format_text">
		<xsl:param name="nodetext"></xsl:param>
		<xsl:if test="string-length(substring-after($nodetext,'&#xa;')) = 0">
			<xsl:value-of select="$nodetext" />
		</xsl:if>
		<xsl:if test="string-length(substring-after($nodetext,'&#xa;')) > 0">
			<xsl:value-of select="substring-before($nodetext,'&#xa;')" />
			<text:line-break/>
			<xsl:call-template name="format_text">
				<xsl:with-param name="nodetext">
					<xsl:value-of select="substring-after($nodetext,'&#xa;')" />
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
	</xsl:template> <!-- xsl:template name="format_text" -->

	<xsl:template match="body" mode="richcontent">
		<xsl:param name="style">Standard</xsl:param>
<!--       <xsl:copy-of select="string(.)"/> -->
		<xsl:apply-templates select="text()|*" mode="richcontent"><xsl:with-param name="style" select="$style"></xsl:with-param></xsl:apply-templates>
	</xsl:template> 
	<xsl:template match="text()" mode="richcontent">	<xsl:copy-of select="string(.)"/></xsl:template> 
	<xsl:template match="br" mode="richcontent">
		<text:line-break/>
	</xsl:template> 
	<xsl:template match="b" mode="richcontent">
		<xsl:param name="style">Standard</xsl:param>
		<text:span text:style-name="T1">
			<xsl:apply-templates select="text()|*" mode="richcontent"><xsl:with-param name="style" select="$style"></xsl:with-param></xsl:apply-templates>
		</text:span>
	</xsl:template> 
	<xsl:template match="p" mode="richcontent">
		<xsl:param name="style">Standard</xsl:param>
		<xsl:choose>
			<xsl:when test="$style = ''">
				<xsl:apply-templates select="text()|*" mode="richcontent"><xsl:with-param name="style" select="$style"></xsl:with-param></xsl:apply-templates>			
			</xsl:when>
			<xsl:otherwise>
				<xsl:element name="text:p">
					<xsl:attribute name="text:style-name"><xsl:value-of select="$style"/></xsl:attribute>
					<xsl:apply-templates select="text()|*" mode="richcontent"><xsl:with-param name="style" select="$style"></xsl:with-param></xsl:apply-templates>
				</xsl:element>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template> 

	<xsl:template match="i" mode="richcontent">
		<xsl:param name="style">Standard</xsl:param>
		<text:span text:style-name="T2">
			<xsl:apply-templates select="text()|*" mode="richcontent"><xsl:with-param name="style" select="$style"></xsl:with-param></xsl:apply-templates>
		</text:span>
	</xsl:template> 
	<xsl:template match="u" mode="richcontent">
		<xsl:param name="style">Standard</xsl:param>
		<text:span text:style-name="T3">
			<xsl:apply-templates select="text()|*" mode="richcontent"><xsl:with-param name="style" select="$style"></xsl:with-param></xsl:apply-templates>
		</text:span>
	</xsl:template> 
	<xsl:template match="ul" mode="richcontent">
		<xsl:param name="style">Standard</xsl:param>
		<text:ordered-list text:style-name="L1">
			<xsl:apply-templates select="text()|*" mode="richcontentul"><xsl:with-param name="style" select="$style"></xsl:with-param></xsl:apply-templates>
		</text:ordered-list>
	    <text:p text:style-name="P3"/>
	</xsl:template> 
	<xsl:template match="ol" mode="richcontent">
		<xsl:param name="style">Standard</xsl:param>
		<text:ordered-list text:style-name="L2">
			<xsl:apply-templates select="text()|*" mode="richcontentol"><xsl:with-param name="style" select="$style"></xsl:with-param></xsl:apply-templates>
		</text:ordered-list>
		<text:p text:style-name="P3"/>
	</xsl:template> 
	<xsl:template match="li" mode="richcontentul">
		<xsl:param name="style">Standard</xsl:param>
      <text:list-item>
        <text:p text:style-name="P1"><!--
			--><xsl:apply-templates select="text()|*" mode="richcontent"><xsl:with-param name="style" select="$style"></xsl:with-param></xsl:apply-templates><!--			
		--></text:p>
      </text:list-item>
	</xsl:template> 
	<xsl:template match="li" mode="richcontentol">
		<xsl:param name="style">Standard</xsl:param>
	    <text:list-item>
        <text:p text:style-name="P2"><!--
			--><xsl:apply-templates select="text()|*" mode="richcontent"><xsl:with-param name="style" select="$style"></xsl:with-param></xsl:apply-templates><!--			
		--></text:p>
      </text:list-item>
	</xsl:template> 
	
<!--
      <text:list-item>
        <text:p text:style-name="P1">b
      </text:list-item>
      <text:list-item>
        <text:p text:style-name="P1">c</text:p>
      </text:list-item>
    <text:p text:style-name="P2"/>
	-->
			<!-- 
    <text:ordered-list text:style-name="L2">
      <text:list-item>
        <text:p text:style-name="P3">1</text:p>
      </text:list-item>
      <text:list-item>
        <text:p text:style-name="P3">2</text:p>
      </text:list-item>
      <text:list-item>
        <text:p text:style-name="P3">3</text:p>
      </text:list-item>
    </text:ordered-list>
    <text:p text:style-name="P2"/>
-->
	<!-- Table: 
		    <table:table table:name="Table1" table:style-name="Table1">
      <table:table-column table:style-name="Table1.A" table:number-columns-repeated="3"/>
      <table:table-row>
        <table:table-cell table:style-name="Table1.A1" table:value-type="string">
          <text:p text:style-name="Table Contents">T11</text:p>
        </table:table-cell>
        <table:table-cell table:style-name="Table1.A1" table:value-type="string">
          <text:p text:style-name="Table Contents">T21</text:p>
        </table:table-cell>
        <table:table-cell table:style-name="Table1.C1" table:value-type="string">
          <text:p text:style-name="Table Contents">T31</text:p>
        </table:table-cell>
      </table:table-row>
      <table:table-row>
        <table:table-cell table:style-name="Table1.A2" table:value-type="string">
          <text:p text:style-name="Table Contents">T12</text:p>
        </table:table-cell>
        <table:table-cell table:style-name="Table1.A2" table:value-type="string">
          <text:p text:style-name="Table Contents">T22</text:p>
        </table:table-cell>
        <table:table-cell table:style-name="Table1.C2" table:value-type="string">
          <text:p text:style-name="Table Contents">T32</text:p>
        </table:table-cell>
      </table:table-row>
      <table:table-row>
        <table:table-cell table:style-name="Table1.A2" table:value-type="string">
          <text:p text:style-name="Table Contents">T13</text:p>
        </table:table-cell>
        <table:table-cell table:style-name="Table1.A2" table:value-type="string">
          <text:p text:style-name="Table Contents">T23</text:p>
        </table:table-cell>
        <table:table-cell table:style-name="Table1.C2" table:value-type="string">
          <text:p text:style-name="Table Contents">T32</text:p>
        </table:table-cell>
      </table:table-row>
    </table:table>
-->
	
	
</xsl:stylesheet>
