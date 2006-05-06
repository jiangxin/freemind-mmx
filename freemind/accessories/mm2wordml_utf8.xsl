<?xml version="1.0" encoding="UTF-8" ?>
<!--
    (c) by Naoki Nose, 2006
    This code is licensed under the GPL.
    (http://www.gnu.org/copyleft/gpl.html)
--> 
<xsl:stylesheet version="1.0"
 xmlns:w="http://schemas.microsoft.com/office/word/2003/wordml" 
 xmlns:v="urn:schemas-microsoft-com:vml" 
 xmlns:w10="urn:schemas-microsoft-com:office:word" 
 xmlns:sl="http://schemas.microsoft.com/schemaLibrary/2003/core" 
 xmlns:aml="http://schemas.microsoft.com/aml/2001/core" 
 xmlns:wx="http://schemas.microsoft.com/office/word/2003/auxHint" 
 xmlns:o="urn:schemas-microsoft-com:office:office" 
 xmlns:dt="uuid:C2F41010-65B3-11d1-A29F-00AA00C14882" 
 w:macrosPresent="no" 
 w:embeddedObjPresent="no" 
 w:ocxPresent="no" 
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="xml"  indent="yes" encoding="UTF-8" standalone="yes"/>

  <xsl:template match="/">
    <xsl:processing-instruction name="mso-application"> progid="Word.Document"</xsl:processing-instruction>
    <w:wordDocument>
      <w:styles>
        <w:versionOfBuiltInStylenames w:val="4"/>
        <w:latentStyles w:defLockedState="off" w:latentStyleCount="156"/>
        <w:style w:type="paragraph" w:default="on" w:styleId="a">
          <w:name w:val="Normal"/>
          <wx:uiName wx:val="標準"/>
          <w:pPr>
            <w:widowControl w:val="off"/>
            <w:jc w:val="both"/>
          </w:pPr>
          <w:rPr>
            <wx:font wx:val="Century"/>
            <w:kern w:val="2"/>
            <w:sz w:val="21"/>
            <w:sz-cs w:val="24"/>
            <w:lang w:val="EN-US" w:fareast="JA" w:bidi="AR-SA"/>
          </w:rPr>
        </w:style>
        <w:style w:type="paragraph" w:styleId="1">
          <w:name w:val="heading 1"/>
          <wx:uiName wx:val="見出し 1"/>
          <w:rsid w:val="00617B79"/>
          <w:pPr>
            <w:pStyle w:val="1"/>
            <w:keepNext/>
            <w:outlineLvl w:val="0"/>
          </w:pPr>
          <w:rPr>
            <w:rFonts w:ascii="Arial" w:fareast="ＭＳ ゴシック" w:h-ansi="Arial"/>
            <wx:font wx:val="Arial"/>
            <w:sz w:val="24"/>
            <w:lang w:val="EN-US" w:fareast="JA" w:bidi="AR-SA"/>
          </w:rPr>
        </w:style>
        <w:style w:type="paragraph" w:styleId="2">
          <w:name w:val="heading 2"/>
          <wx:uiName wx:val="見出し 2"/>
          <w:basedOn w:val="a"/>
          <w:next w:val="a"/>
          <w:rsid w:val="00AE60E3"/>
          <w:pPr>
            <w:pStyle w:val="2"/>
            <w:keepNext/>
            <w:outlineLvl w:val="1"/>
          </w:pPr>
          <w:rPr>
            <w:rFonts w:ascii="Arial" w:fareast="ＭＳ ゴシック" w:h-ansi="Arial"/>
            <wx:font wx:val="Arial"/>
          </w:rPr>
        </w:style>
        <w:style w:type="paragraph" w:styleId="3">
          <w:name w:val="heading 3"/>
          <wx:uiName wx:val="見出し 3"/>
          <w:basedOn w:val="a"/>
          <w:next w:val="a"/>
          <w:rsid w:val="00AE60E3"/>
          <w:pPr>
            <w:pStyle w:val="3"/>
            <w:keepNext/>
            <w:ind w:left-chars="400"/>
            <w:outlineLvl w:val="2"/>
          </w:pPr>
          <w:rPr>
            <w:rFonts w:ascii="Arial" w:fareast="ＭＳ ゴシック" w:h-ansi="Arial"/>
            <wx:font wx:val="Arial"/>
          </w:rPr>
        </w:style>
        <w:style w:type="paragraph" w:styleId="4">
          <w:name w:val="heading 4"/>
          <wx:uiName wx:val="見出し 4"/>
          <w:basedOn w:val="a"/>
          <w:next w:val="a"/>
          <w:rsid w:val="00AE60E3"/>
          <w:pPr>
            <w:pStyle w:val="4"/>
            <w:keepNext/>
            <w:ind w:left-chars="400"/>
            <w:outlineLvl w:val="3"/>
          </w:pPr>
          <w:rPr>
            <wx:font wx:val="Century"/>
            <w:b/>
            <w:b-cs/>
          </w:rPr>
        </w:style>
        <w:style w:type="character" w:default="on" w:styleId="a0">
          <w:name w:val="Default Paragraph Font"/>
          <wx:uiName wx:val="段落フォント"/>
          <w:semiHidden/>
        </w:style>
        <w:style w:type="table" w:default="on" w:styleId="a1">
          <w:name w:val="Normal Table"/>
          <wx:uiName wx:val="標準の表"/>
          <w:semiHidden/>
          <w:rPr>
            <w:rFonts w:ascii="Times New Roman" w:fareast="Times New Roman" w:h-ansi="Times New Roman"/>
            <wx:font wx:val="Times New Roman"/>
          </w:rPr>
          <w:tblPr>
            <w:tblInd w:w="0" w:type="dxa"/>
            <w:tblCellMar>
              <w:top w:w="0" w:type="dxa"/>
              <w:left w:w="108" w:type="dxa"/>
              <w:bottom w:w="0" w:type="dxa"/>
              <w:right w:w="108" w:type="dxa"/>
            </w:tblCellMar>
          </w:tblPr>
        </w:style>
        <w:style w:type="list" w:default="on" w:styleId="a2">
          <w:name w:val="No List"/>
          <wx:uiName wx:val="リストなし"/>
          <w:semiHidden/>
        </w:style>
        <w:style w:type="paragraph" w:styleId="a3">
          <w:name w:val="Title"/>
          <wx:uiName wx:val="表題"/>
          <w:basedOn w:val="a"/>
          <w:rsid w:val="00D24077"/>
          <w:pPr>
            <w:pStyle w:val="a3"/>
            <w:spacing w:before="240" w:after="120"/>
            <w:jc w:val="center"/>
            <w:outlineLvl w:val="0"/>
          </w:pPr>
          <w:rPr>
            <w:rFonts w:ascii="Arial" w:fareast="ＭＳ ゴシック" w:h-ansi="Arial" w:cs="Arial"/>
            <wx:font wx:val="Arial"/>
            <w:sz w:val="32"/>
            <w:sz-cs w:val="32"/>
          </w:rPr>
        </w:style>
      </w:styles>
      <w:body>
        <xsl:apply-templates/>
      </w:body>
    </w:wordDocument>
  </xsl:template>

  <xsl:template match="//map/node">
    <wx:sect>
      <wx:sub-section>
      <w:p>
        <w:pPr>
          <w:pStyle w:val="a3"/>
        </w:pPr>
        <w:r>
          <w:t><xsl:value-of select="@TEXT"/></w:t>
        </w:r>
      </w:p>
      </wx:sub-section>
      <xsl:apply-templates select="./*" mode="heading"/>
    </wx:sect>
  </xsl:template>

  <xsl:template match="node" mode="heading">
    <xsl:param name="level" select="1"/>
    <wx:sub-section>
      <w:p>
        <w:pPr>
          <w:pStyle w:val="{$level}"/>
        </w:pPr>
        <w:r>
          <w:t><xsl:value-of select="@TEXT"/></w:t>
        </w:r>
      </w:p>
      <xsl:if test="$level &lt; 4">
        <xsl:apply-templates mode="heading"><xsl:with-param name="level" select="$level + 1"/></xsl:apply-templates>
      </xsl:if>
    </wx:sub-section>
  </xsl:template>
</xsl:stylesheet> 
