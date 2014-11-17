<xsl:stylesheet version="1.0"
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<!-- Usage:
   xsltproc -stringparam mmx_file mindmap.mmx <this_xslt> mindmap.mm
-->
   <xsl:output method="xml" version="1.0" encoding="utf-8"
       indent="no" />

   <xsl:param name="mmx_file" />
   <xsl:variable name="indexfile" select="document($mmx_file)" />

   <xsl:key name="node-by-id" match="node" use="@ID"/>

   <xsl:template match="map">
       <map>
           <xsl:copy-of select="@*" />
           <xsl:apply-templates />
       </map>
   </xsl:template>

   <xsl:template match="node">
       <xsl:variable name="id" select="@ID" />
       <xsl:copy>
           <xsl:copy-of select="@*" />
           <xsl:for-each select="$indexfile">
               <xsl:copy-of select="key('node-by-id', $id)/@*" />
           </xsl:for-each>
           <xsl:apply-templates />
       </xsl:copy>
   </xsl:template>

   <xsl:template match="*">
     <xsl:copy-of select="."/>
   </xsl:template>

</xsl:stylesheet>
