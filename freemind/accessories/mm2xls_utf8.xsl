<?xml version="1.0" encoding="UTF-8"?>
<!--
    (c) by Naoki Nose, 2006
    This code is licensed under the GPL.
    (http://www.gnu.org/copyleft/gpl.html)
--> 
<xsl:stylesheet version="1.0"
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 xmlns="urn:schemas-microsoft-com:office:spreadsheet"
 xmlns:o="urn:schemas-microsoft-com:office:office"
 xmlns:x="urn:schemas-microsoft-com:office:excel"
 xmlns:ss="urn:schemas-microsoft-com:office:spreadsheet">
  <xsl:output method="xml" indent="yes" encoding="UTF-8" standalone="yes"/>

  <xsl:template match="/">
    <xsl:processing-instruction name="mso-application"> progid="Excel.Sheet"</xsl:processing-instruction>
    <Workbook>
      <Worksheet ss:Name="Sheet1">
        <Table>
          <xsl:apply-templates select="//map/node/node[position() = 1]" mode="row"/>
          <xsl:apply-templates select="//map/node//node[position() > 1]" mode="row"/>
        </Table>
      </Worksheet>
    </Workbook>
  </xsl:template>

  <xsl:template match="node" mode="row">
    <Row>
      <xsl:apply-templates select="." mode="blank"/>
      <xsl:apply-templates select="." mode="cell"/>
    </Row>
  </xsl:template>

  <xsl:template match="node" mode="blank">
    <xsl:if test="count(ancestor::node) &gt; 1">
      <Cell>
        <Data ss:Type="String"></Data>
      </Cell>
      <xsl:apply-templates select="ancestor::node[1]" mode="blank"/>
    </xsl:if>
  </xsl:template>

  <xsl:template match="node" mode="cell">
    <Cell>
      <Data ss:Type="String"><xsl:value-of select="@TEXT"/></Data>
    </Cell>
    <xsl:if test="count(./*) &gt; 0">
      <xsl:apply-templates select="./node[1]" mode="cell"/>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>
