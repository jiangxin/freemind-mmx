<?xml version="1.0" encoding="iso-8859-1"?>
<!--

 The contents of this file are subject to the Mozilla Public License Version
 1.1 (the "License"); you may not use this file except in compliance with
 the License. You may obtain a copy of the License at
 http://www.mozilla.org/MPL/
 
 Software distributed under the License is distributed on an "AS IS" basis,
 WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 for the specific language governing rights and limitations under the
 License.
 
 Miika Nurminen (minurmin@cc.jyu.fi) 12.12.2003.

Transforms Freemind (0.6.7) mm file to XHTML 1.1. 
Output is valid (possibly apart HTML entered by user in Freemind).

-->
<xsl:stylesheet version="1.0"
                xmlns="http://www.w3.org/1999/xhtml" 
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
>
<!-- mozilla doesn't parse method xhtml (in xslt 2.0) -->
<xsl:output method="xml"
            version="1.0"
            encoding="iso-8859-1"
            doctype-public="-//W3C//DTD XHTML 1.1//EN"  
            doctype-system="http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd"
            omit-xml-declaration="no"
/>
<!-- fc, 17.10.2004: The following parameter is set by freemind. -->
<xsl:param name="destination_dir">.</xsl:param>
<xsl:strip-space elements="*"/>
<!-- note! nonempty links are required for opera! (tested with opera 7).
     #160 is non-breaking space.  / mn, 11.12.2003 
--><xsl:template match="/">
<xsl:processing-instruction name="xml-stylesheet">href="treestyles.css" type="text/css"</xsl:processing-instruction>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="fi" >
<head>
<title><xsl:value-of select="/node/@TEXT"/>
	</title>
	<xsl:element name="link"><xsl:attribute name="rel">stylesheet</xsl:attribute>
		<xsl:attribute name="href"><xsl:value-of select="$destination_dir"/>treestyles.css</xsl:attribute>
		<xsl:attribute name="type">text/css</xsl:attribute></xsl:element>
<xsl:element name="script"><xsl:attribute name="type">text/javascript</xsl:attribute>
	<xsl:attribute name="src"><xsl:value-of select="$destination_dir"/>marktree.js</xsl:attribute>&#160; </xsl:element>
</head>
<body>

<div class="basetop">
<a href="#" onclick="expandAll(document.getElementById('base'))">Expand</a> - <a href="#" onclick="collapseAll(document.getElementById('base'))">Collapse</a>
</div>
<!--
<div class="basetext">
<button onclick="expandAll(document.getElementById('base'))"> Expand all </button><button onclick="collapseAll(document.getElementById('base'))"> Collapse all </button>
</div>
-->
<div id="base" class="basetext">
<ul>

<xsl:apply-templates />

</ul>
</div>

<!--
<div class="basetext">
<button onclick="expandAll(document.getElementById('base'))"> Expand all </button><button onclick="collapseAll(document.getElementById('base'))"> Collapse all </button>
</div>
-->

</body>
</html>
</xsl:template>

<xsl:template match="font"><xsl:if test="string-length(@SIZE) &gt; 0">font-size:<xsl:value-of select="round((number(@SIZE) div 12)*100)" />%;</xsl:if><xsl:if test="@BOLD='true'">font-weight:bold;</xsl:if><xsl:if test="@ITALIC='true'">font-style:italic;</xsl:if></xsl:template>

<xsl:template name="link">
  <xsl:if test="string-length(@LINK) &gt; 0">
    - [ <a> <xsl:attribute name="href"><xsl:value-of select="@LINK" />  
    </xsl:attribute><xsl:value-of select="@LINK"/></a> ]   
  </xsl:if>
</xsl:template>
<!--
<xsl:template name="html">
  <xsl:value-of select="substring(@TEXT,7,string-length(@TEXT))"  disable-output-escaping="yes"/>
</xsl:template>
-->
<xsl:template name="html">
    <xsl:choose>
        <xsl:when test="(substring(@TEXT,string-length(@TEXT)-13,14)='&lt;/body&gt;&lt;/html&gt;') and 
                                   (substring(@TEXT,1,12)='&lt;html&gt;&lt;body&gt;')">
         <xsl:value-of select="substring(@TEXT,13,string-length(@TEXT)-26)"  disable-output-escaping="yes"/>
        </xsl:when>              
        <xsl:when test="substring(@TEXT,string-length(@TEXT)-6,7)='&lt;/html&gt;'">
            <xsl:value-of select="substring(@TEXT,7,string-length(@TEXT)-14)"  disable-output-escaping="yes"/>
        </xsl:when>              
        <xsl:otherwise> 
            <xsl:value-of select="substring(@TEXT,7,string-length(@TEXT))"  disable-output-escaping="yes"/>
        </xsl:otherwise>
    </xsl:choose>
</xsl:template>

<xsl:template name="spantext">
  <xsl:element name="span">
    <xsl:attribute name="style">
       <xsl:if test="string-length(@COLOR) &gt; 0">color:<xsl:value-of select="@COLOR" />;
       </xsl:if>
       <xsl:apply-templates select="font" />
       </xsl:attribute>                    
    <xsl:value-of select="@TEXT" />
  </xsl:element>
    <xsl:call-template name="link" />
    <xsl:if test="string-length(normalize-space(@TEXT)) = 0">
      <br /> <!-- anonymous node -->
    </xsl:if>
</xsl:template>

<xsl:template name="spanbold">
  <xsl:element name="span">
    <xsl:attribute name="style">
       font-weight:bold;
       <xsl:if test="string-length(@COLOR) &gt; 0">color:<xsl:value-of select="@COLOR" />;
       </xsl:if>
       <xsl:apply-templates select="font" />
       </xsl:attribute>                    
    <xsl:value-of select="@TEXT" />
  </xsl:element>
    <xsl:call-template name="link" />
    <xsl:if test="string-length(normalize-space(@TEXT)) = 0">
      <br /> <!-- anonymous node -->
    </xsl:if>
</xsl:template>


<xsl:template match="node">
<!--<xsl:if test="string-length(normalize-space(@TEXT)) = 0">
      ANONY <br /> 
    </xsl:if>-->
  <xsl:choose>
  <xsl:when test="(string-length(normalize-space(@TEXT)) = 0) and (string-length(normalize-space(@LINK))= 0)">      

     <xsl:if test="count(child::node)>0" > <!-- anonoymous nodes are not processed, but their children are -->
<xsl:apply-templates select="node" />  
</xsl:if>
     <xsl:if test="count(child::node)=0" > <!-- anonoymous nodes are not processed, but their children are -->
     <li class="basic" /> <!-- must be here to render valid html -->  
</xsl:if>

  </xsl:when>
  <xsl:when test="(string-length((@TEXT)) > 0) or (string-length(@LINK) > 0)">      
    <xsl:if test="count(child::node)=0"> 
      <li class="basic">
        <xsl:choose>
          <xsl:when test="substring(@TEXT,1,6)='&lt;html&gt;'">
            <xsl:call-template name="html" />
          </xsl:when>              
          <xsl:otherwise> 
            <xsl:call-template name="spantext" />
          </xsl:otherwise>
        </xsl:choose>
      </li>
    </xsl:if>
    <xsl:if test="count(child::node)>0" > 
      <xsl:choose>
        <xsl:when test="@FOLDED='true'">
          <li class="exp">
            <xsl:choose>        
            <xsl:when test="substring(@TEXT,1,6)='&lt;html&gt;'">
            <xsl:call-template name="html" />
          </xsl:when>
            <xsl:otherwise>
            <xsl:call-template name="spanbold" />
          </xsl:otherwise>
            </xsl:choose>
            <ul class="sub"><xsl:apply-templates select="node"/></ul>
          </li>
        </xsl:when>
        <xsl:otherwise>
          <li class="col">
            <xsl:choose>        
            <xsl:when test="substring(@TEXT,1,6)='&lt;html&gt;'">
              <xsl:call-template name="html" />
            </xsl:when>
            <xsl:otherwise>
              <xsl:call-template name="spanbold" />
            </xsl:otherwise>
          </xsl:choose>
          <ul class="subexp"><xsl:apply-templates select="node" /></ul>
          </li>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:if>
</xsl:when>
<xsl:otherwise>
  <xsl:apply-templates select="node" />
</xsl:otherwise>
</xsl:choose>


</xsl:template>


</xsl:stylesheet>
