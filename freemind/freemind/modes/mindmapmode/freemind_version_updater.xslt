<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template 
		match="/ | node() | @* | comment() | processing-instruction()">
		<xsl:copy>
			<xsl:apply-templates select="@* | node()"/>
		</xsl:copy>
	</xsl:template>

	<xsl:template 
		match="/ | node() | @* | comment() | processing-instruction()" mode="BELOW_0_8_0RC3">
		<xsl:copy>
			<xsl:apply-templates select="@* | node()"/>
		</xsl:copy>
	</xsl:template>

	<xsl:template match="map">
		<xsl:choose>
			<!-- versions that may use the CreationModificationPlugin. -->
			<xsl:when 
				test="(starts-with(@version, '0.7.')) or (starts-with(@version, '0.8.0_alpha')) or (starts-with(@version, '0.8.0_beta')) or (@version='0.8.0 RC1') or (@version='0.8.0 RC2')">
				<xsl:copy>
					<xsl:apply-templates select="@*" mode="BELOW_0_8_0RC3"/>
					<!--<xsl:attribute name="version">0.8.0 RC3</xsl:attribute>-->
					<xsl:apply-templates select="node()" mode="BELOW_0_8_0RC3"/>
				</xsl:copy>
			</xsl:when>
			<xsl:otherwise>
				<xsl:copy>
					<xsl:apply-templates select="@* | node()"/>
				</xsl:copy>
			</xsl:otherwise>
		</xsl:choose>
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
	<xsl:template 
		match="node/hook[@NAME='accessories/plugins/CreationModificationPlugin.properties']" mode="BELOW_0_8_0RC3"><!--
	--></xsl:template>
	
	<xsl:template match="node[./hook[@NAME='accessories/plugins/CreationModificationPlugin.properties']]" mode="BELOW_0_8_0RC3">
		<xsl:copy>
			<xsl:attribute name="CREATED">
				<xsl:value-of 
					select="hook[@NAME='accessories/plugins/CreationModificationPlugin.properties']/Parameters/@CREATED"/>
			</xsl:attribute>
			<xsl:attribute name="MODIFIED">
				<xsl:value-of 
					select="hook[@NAME='accessories/plugins/CreationModificationPlugin.properties']/Parameters/@MODIFIED"/>
			</xsl:attribute>
			<xsl:apply-templates select="@*|node()" mode="BELOW_0_8_0RC3"/>
		</xsl:copy>
	</xsl:template>
</xsl:stylesheet>