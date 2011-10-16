<?xml version="1.0" encoding="utf-8" ?>
<!--http://sourceforge.net/projects/word2wiki/-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:w="http://schemas.microsoft.com/office/word/2003/wordml" xmlns:sl="http://schemas.microsoft.com/schemaLibrary/2003/core" xmlns:aml="http://schemas.microsoft.com/aml/2001/core" xmlns:wx="http://schemas.microsoft.com/office/word/2003/auxHint" xmlns:o="urn:schemas-microsoft-com:office:office" xmlns:v="urn:schemas-microsoft-com:vml" exclude-result-prefixes="w sl aml wx o v">

	<xsl:output method="text" encoding="utf-8" />

	<xsl:key name="outline" match="w:outlineLvl" use="ancestor::w:style[@w:type = 'paragraph']/@w:styleId" />
	<xsl:key name="font" match="wx:font" use="ancestor::w:style[@w:type = 'paragraph']/@w:styleId" />
	<xsl:key name="style" match="w:style" use="w:styleId" />
	<xsl:key name="list" match="w:list" use="@w:ilfo" />
	<xsl:key name="listType" match="w:listDef" use="@w:listDefId" />

	<!-- Previously <xsl:param name="extraStyles" select="document('src/wsl_resources/word2wiki/extraStyles.xml')" />-->
	<xsl:variable name="extraStyles">More Info Note Warn Stop Important</xsl:variable>


	<xsl:template match="w:body">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="wx:sect" priority="1">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="w:p[ancestor::w:body]">
		<!-- Set initial parameters -->
		<xsl:variable name="paraStyle" select="w:pPr/w:pStyle/@w:val" />
		<xsl:variable name="paraObject" select="key('style',$paraStyle)" />
		<xsl:variable name="outLvl" select="key('outline',w:pPr/w:pStyle/@w:val)/@w:val" />
		<xsl:variable name="listPtr">
			<xsl:choose>
				<xsl:when test="w:pPr/w:listPr/w:ilfo">
					<xsl:value-of select="w:pPr/w:listPr/w:ilfo/@w:val" />
				</xsl:when>
				<xsl:when test="$paraObject/w:pPr/w:listPr/w:ilfo">
					<xsl:value-of select="$paraObject/w:pPr/w:listPr/w:ilfo/@w:val" />
				</xsl:when>
				<xsl:when test="w:pPr/w:listPr">NONE</xsl:when>
			</xsl:choose>
		</xsl:variable>

		<!-- Select correct Wiki markup -->
		<xsl:choose>

			<!-- Headings -->
			<xsl:when test="$outLvl">
				<xsl:value-of select="substring('=====',1,$outLvl+1)" />
				<xsl:apply-templates />
				<xsl:value-of select="substring('=====',1,$outLvl+1)" />
				<xsl:text>&#xa;&#xa;</xsl:text>
			</xsl:when>

			<!-- Styles implemented as user-defined templates -->
			<xsl:when test="contains($extraStyles, $paraStyle) and $paraStyle != ''">
				<xsl:text>{{</xsl:text>
				<xsl:value-of select="$paraStyle" />
				<xsl:text>|1=</xsl:text>
				<xsl:apply-templates />
				<xsl:text>}}&#xa;&#xa;</xsl:text>
			</xsl:when>

			<!-- Fixed-font styles or paragraphs with fixed-font text -->
			<xsl:when test="contains(key('font',$paraStyle)/@wx:val,'Courier') or contains(w:pPr/w:rPr/w:rFonts/@w:ascii,'Courier')">
				<xsl:text>
				</xsl:text>
				<xsl:apply-templates />
				<xsl:text>&#xa;</xsl:text>
			</xsl:when>

			<!-- bullets and numbered lists -->
			<xsl:when test="$listPtr != ''">
				<xsl:variable name="listLevel">
					<xsl:choose>
						<xsl:when test="w:pPr/w:listPr/w:ilvl">
							<xsl:value-of select="w:pPr/w:listPr/w:ilvl/@w:val" />
						</xsl:when>
						<xsl:when test="$paraObject/w:pPr/w:listPr/w:ilvl">
							<xsl:value-of select="$paraObject/w:pPr/w:listPr/w:ilvl/@w:val" />
						</xsl:when>
						<xsl:otherwise>0</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<xsl:variable name="leadIn" select="substring(w:pPr/w:listPr/wx:t/@wx:val,1,1)" />
				<xsl:variable name="listType">
					<xsl:choose>
						<xsl:when test="$leadIn &gt;= '0' and $leadIn &lt;= '9'">#####</xsl:when>
						<xsl:otherwise>*****</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<xsl:value-of select="substring($listType,1,$listLevel+1)" />
				<xsl:text>
				</xsl:text>
				<xsl:apply-templates />
				<xsl:text>&#xa;</xsl:text>
			</xsl:when>

			<!-- Indented text -->
			<xsl:when test="w:pPr/w:ind/@w:left or $paraObject/w:pPr/w:ind/@w:left">
				<xsl:variable name="indLevel">
					<xsl:choose>
						<xsl:when test="w:pPr/w:ind/@w:left">
							<xsl:value-of select="w:pPr/w:ind/@w:left" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="$paraObject/w:pPr/w:ind/@w:left" />
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<xsl:variable name="indValue">
					<xsl:choose>
						<xsl:when test="$indLevel &lt; 700">1</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="$indLevel div 700" />
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<xsl:value-of select="substring(':::::',1,$indValue)" />
				<xsl:apply-templates />
				<xsl:text>&#xa;&#xa;</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates />
				<xsl:text>&#xa;&#xa;</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
		<!--
  <xsl:element name="{$elName}">
    <xsl:choose> 
      <xsl:when test="$elName != 'p'" />
      <xsl:when test="not($paraStyle)" />
      <xsl:when test="$paraStyle = 'normal' or $paraStyle = 'BodyText'" />
      <xsl:otherwise>
        <xsl:attribute name="class"><xsl:value-of select="$paraStyle" /></xsl:attribute>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates />
  </xsl:element>
-->
	</xsl:template>

	<xsl:template match="w:r">
		<xsl:call-template name="textOnlyRange" />
	</xsl:template>

	<xsl:template match="w:r" mode="textOnly" name="textOnlyRange" priority="1">
		<xsl:variable name="raw">
			<xsl:apply-templates mode="text" />
		</xsl:variable>
		<xsl:variable name="contents" select="normalize-space($raw)" />
		<xsl:choose>
			<xsl:when test="$contents = ''">
				<xsl:value-of select="$raw" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="rawStart" select="substring-before($raw,$contents)" />
				<xsl:variable name="rawEnd" select="substring-before($raw,$contents)" />
				<xsl:variable name="rawMiddle" select="substring(substring($raw,1,string-length($raw) - string-length($rawEnd)),string-length($rawStart)+1)" />
				<xsl:value-of select="$rawStart" />
				<xsl:choose>
					<xsl:when test="(w:rPr/w:rStyle and w:rPr/w:rStyle/@w:val != 'Hyperlink') or (w:rPr/w:color) or (w:rPr/w:rFonts and not(contains(w:rPr/w:rFonts/@w:ascii,'Courier')))">
						<xsl:text>&lt;span</xsl:text>
						<xsl:if test="w:rPr/w:rStyle">
							<xsl:text> class='</xsl:text>
							<xsl:value-of select="w:rPr/w:rStyle/@w:val" />
							<xsl:text>'</xsl:text>
						</xsl:if>
						<xsl:variable name="style">
							<xsl:if test="w:rPr/w:rFonts">font-family: <xsl:value-of select="w:rPr/w:rFonts/@w:ascii" />;</xsl:if>
							<xsl:if test="w:rPr/w:color">color: <xsl:value-of select="w:rPr/w:color/@w:val" />;</xsl:if>
						</xsl:variable>
						<xsl:if test="$style != ''">
							<xsl:text> style='</xsl:text>
							<xsl:value-of select="$style" />
							<xsl:text>'</xsl:text>
						</xsl:if>
						<xsl:text>&gt;</xsl:text>
						<xsl:call-template name="isRangeBold">
							<xsl:with-param name="contents" select="$rawMiddle" />
						</xsl:call-template>
						<xsl:text>&lt;/span&gt;</xsl:text>
					</xsl:when>
					<xsl:when test="contains(w:rPr/w:rFonts/@w:ascii,'Courier') and not(contains(ancestor::w:p/w:pPr/w:rPr/w:rFonts/@w:ascii,'Courier'))">
						<xsl:text>&lt;code&gt;</xsl:text>
						<xsl:call-template name="isRangeBold">
							<xsl:with-param name="contents" select="$rawMiddle" />
						</xsl:call-template>
						<xsl:text>&lt;/code&gt;</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="isRangeBold">
							<xsl:with-param name="contents" select="$rawMiddle" />
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:value-of select="$rawEnd" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="isRangeBold">
		<xsl:param name="contents" />
		<xsl:choose>
			<xsl:when test="w:rPr/w:b">
				<xsl:text>'''</xsl:text>
				<xsl:call-template name="isRangeItalic">
					<xsl:with-param name="contents" select="$contents" />
				</xsl:call-template>
				<xsl:text>'''</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="isRangeItalic">
					<xsl:with-param name="contents" select="$contents" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="isRangeItalic">
		<xsl:param name="contents" />
		<xsl:choose>
			<xsl:when test="w:rPr/w:i">
				<xsl:text>''</xsl:text>
				<xsl:value-of select="$contents" />
				<xsl:text>''</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$contents" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="w:t" mode="text">
		<xsl:value-of select="text()" />
	</xsl:template>
	<xsl:template match="w:br" mode="text">&lt;br /&gt;</xsl:template>

	<xsl:template match="w:pict[v:shape/v:imagedata/@o:href]" priority="2" mode="text">
		<xsl:call-template name="wPictWithHref" />
	</xsl:template>
	<xsl:template match="w:pict" mode="text">[[Image:]]</xsl:template>
	<xsl:template match="w:binData" mode="text" />

	<xsl:template match="text()" />

	<xsl:template match="w:tbl">
		<xsl:text>{|</xsl:text>
		<xsl:if test="w:tblPr/w:tblStyle/@w:val">
			<xsl:text> class='</xsl:text>
			<xsl:value-of select="w:tblPr/w:tblStyle/@w:val" />
			<xsl:text>'</xsl:text>
		</xsl:if>
		<xsl:text>&#xa;</xsl:text>
		<xsl:apply-templates />
		<xsl:text>|}&#xa;&#xa;</xsl:text>
	</xsl:template>

	<xsl:template match="w:tr">
		<xsl:text>|-&#xa;</xsl:text>
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="w:tc">
		<xsl:text>|</xsl:text>
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="w:hlink">
		<xsl:text>[</xsl:text>
		<xsl:value-of select="@w:dest" />
		<xsl:text>
		</xsl:text>
		<xsl:apply-templates />
		<xsl:text>]</xsl:text>
	</xsl:template>

	<xsl:template match="w:p/w:r[w:rPr/w:rStyle/@w:val = 'Hyperlink']" priority="2" />

	<xsl:template match="w:p/w:r[contains(w:instrText,'HYPERLINK')]">
		<xsl:variable name="quote">"</xsl:variable>
		<xsl:variable name="link" select="substring-before(substring-after(w:instrText,$quote),$quote)" />
		<xsl:variable name="beforeID" select="generate-id()" />
		<xsl:variable name="afterID" select="generate-id(following-sibling::w:r[w:fldChar/@w:fldCharType='end'][1])" />
		<xsl:text>[</xsl:text>
		<xsl:value-of select="$link" />
		<xsl:text>
		</xsl:text>
		<xsl:for-each select="following-sibling::w:r[following-sibling::w:r[generate-id() = $afterID]]">
			<xsl:apply-templates select="." mode='textOnly' />
		</xsl:for-each>
		<xsl:text>]</xsl:text>
	</xsl:template>

	<xsl:template match="w:pict[v:shape/v:imagedata/@o:href]" priority="2" name="wPictWithHref">
		<xsl:text>[[Image:</xsl:text>
		<xsl:call-template name="fileName">
			<xsl:with-param name="path" select="v:shape/v:imagedata/@o:href" />
		</xsl:call-template>
		<xsl:text>]]</xsl:text>
	</xsl:template>

	<xsl:template match="w:pict">[[Image:]]</xsl:template>

	<xsl:template match="w:sectPr" />

	<xsl:template match="w:binData" />

	<xsl:template name="fileName">
		<xsl:param name="path" />
		<xsl:choose>
			<xsl:when test="contains($path,'\')">
				<xsl:call-template name="fileName">
					<xsl:with-param name="path" select="substring-after($path,'\')" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="contains($path,'/')">
				<xsl:call-template name="fileName">
					<xsl:with-param name="path" select="substring-after($path,'/')" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$path" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>
