<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template 
		match="/ | node() | @* | comment() | processing-instruction()">
		<xsl:copy>
			<xsl:apply-templates select="@* | node()"/>
		</xsl:copy>
	</xsl:template>

	<xsl:template match="map">
		<!-- versions -->
		<xsl:variable name="version"><!--
			--><xsl:choose><!--
			--><xsl:when test="@version='0.7.0'"><!--
			--></xsl:when><!--
			--><xsl:when test="@version='0.7.1'"><!--
			-->0710000<!--Numbering scheme: version.subversion.releasecandidateversion.betaversion.alphaversion
			--></xsl:when><!--
			--><xsl:when test="(starts-with(@version, '0.8.0_alpha'))"><!--
			-->0800001<!--
			--></xsl:when><!--
			--><xsl:when test="(starts-with(@version, '0.8.0_beta'))"><!--
			-->0800010<!--
			--></xsl:when><!--
			--><xsl:when test="@version='0.8.0 RC1'"><!--
			-->0800100<!--
			--></xsl:when><!--
			--><xsl:when test="@version='0.8.0 RC2'"><!--
			-->0800200<!--
			--></xsl:when><!--
			--><xsl:when test="@version='0.8.0 RC3'"><!--
			-->0800300<!--
			--></xsl:when><!--
			--><xsl:when test="@version='0.8.0 RC4'"><!--
			-->0800400<!--
			--></xsl:when><!--
			--><xsl:when test="@version='0.8.0 RC5'"><!--
			-->0800500<!--				
			--></xsl:when><!--
			--><xsl:when test="@version='0.8.0'"><!--
			-->0801000<!-- Means the 0.8 release. This number is bigger than that of 0.8RC5.
			--></xsl:when><!--
			--><xsl:when test="@version='0.8.FA Alpha 3' or @version='0.8.FA Alpha 4'"><!--
			-->0801004<!--
			--></xsl:when><!--
			--><xsl:otherwise><!--
			-->-1<!--
			--></xsl:otherwise><!--
			--></xsl:choose><!--
			--></xsl:variable><!--
		<xsl:message>!<xsl:value-of select="$version"></xsl:value-of>!</xsl:message>
		--><xsl:copy>
		<xsl:apply-templates select="@* | node()">
			<xsl:with-param name="version" select="$version"/>
		</xsl:apply-templates>
		</xsl:copy>
	</xsl:template>
	<!-- from
	 <hook NAME="accessories/plugins/CreationModificationPlugin.properties">
<Parameters CREATED="1107380732932" MODIFIED="1107901568379"/>
</hook>
 
 to 
 
	<node COLOR="#00b439" CREATED="1113680014182" FOLDED="true" 
		ID="Freemind_Link_241899915" MODIFIED="1113680014182" 
		TEXT="Transactions">
 -->
	<!-- remove the following attributes/tags: -->
	<xsl:template match="node/hook[@NAME='accessories/plugins/CreationModificationPlugin.properties']"/>
	<xsl:template match="node/@SHIFT_Y"/>
	<xsl:template match="node/@AA_NODE_CLASS"/>
	<xsl:template match="node/@ADDITIONAL_INFO"/>
	<xsl:template match="node/attrlayout"/>
	
	<xsl:template match="node">
		<xsl:param name="version">-1</xsl:param>
  		<xsl:copy>
			<xsl:choose>
				<!-- move the attributes CREATED and MODIFIED into the node tag as of version 0.8.0RC3-->
				<xsl:when test="$version &lt; 0800300 and hook[@NAME='accessories/plugins/CreationModificationPlugin.properties']">
					<xsl:attribute name="CREATED">
						<xsl:value-of 
							select="hook[@NAME='accessories/plugins/CreationModificationPlugin.properties']/Parameters/@CREATED"/>
					</xsl:attribute>
					<xsl:attribute name="MODIFIED">
						<xsl:value-of 
							select="hook[@NAME='accessories/plugins/CreationModificationPlugin.properties']/Parameters/@MODIFIED"/>
					</xsl:attribute>
				</xsl:when>				
				<xsl:when test="$version &lt; 0800400 and @SHIFT_Y">
					<xsl:attribute name="VSHIFT">
						<xsl:value-of 
							select="@SHIFT_Y"/>
					</xsl:attribute>
				</xsl:when>
				<xsl:when test="$version &lt; 0800400 and @ADDITIONAL_INFO">
					<xsl:attribute name="ENCRYPTED_CONTENT">
						<xsl:value-of 
							select="@ADDITIONAL_INFO"/>
					</xsl:attribute>
				</xsl:when>
			</xsl:choose>
			<xsl:if test="attrlayout">
				<xsl:element name="attribute_layout">
                    	<xsl:apply-templates select = "attrlayout/@*" /> 
				</xsl:element>
			</xsl:if>
			<xsl:apply-templates select="@*|node()">
				<xsl:with-param name="version" select="$version"/>
			</xsl:apply-templates>
		</xsl:copy>
	</xsl:template>
</xsl:stylesheet>
