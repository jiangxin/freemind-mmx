<?xml version="1.0" encoding="UTF-8"?>
<!--
    (c) by Naoki Nose, 2006
    This code is licensed under the GPL.
    (http://www.gnu.org/copyleft/gpl.html)
--> 
<xsl:stylesheet version="1.0"
  xmlns="http://schemas.microsoft.com/project"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="xml" indent="yes" encoding="UTF-8" standalone="yes"/>

  <xsl:template match="/">
    <Project>
      <xsl:apply-templates/>
    </Project>
  </xsl:template>

  <xsl:template match="//map/node">
    <Name><xsl:value-of select="@TEXT"/></Name>
    <Tasks>
      <xsl:apply-templates select="./*" mode="tasks"/>
    </Tasks>
  </xsl:template>

  <xsl:template match="node" mode="tasks">
    <xsl:param name="level" select="1"/>
    <Task>
      <UID><xsl:number level="any" count="//map/node//node" format="1"/></UID>
      <Name><xsl:value-of select="@TEXT"/></Name>
      <OutlineLevel><xsl:value-of select="$level"/></OutlineLevel>
      <FixedCostAccrual>1</FixedCostAccrual>
    </Task>
    <xsl:apply-templates mode="tasks"><xsl:with-param name="level" select="$level + 1"/></xsl:apply-templates>
  </xsl:template>

</xsl:stylesheet>
