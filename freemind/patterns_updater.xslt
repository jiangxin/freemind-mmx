<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template 
		match="/ | node() | @* | comment() | processing-instruction()">
		<xsl:copy>
			<xsl:apply-templates select="@* | node()"/>
		</xsl:copy>
	</xsl:template>

	<xsl:template match="node">
		<xsl:choose> 
			<xsl:when test="@color">
				<xsl:element name="patterns_node_color">
					<xsl:attribute name="value"><xsl:value-of select="@color"/></xsl:attribute>
				</xsl:element>
		    </xsl:when>
			<xsl:otherwise>
				<patterns_node_color/>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:choose> 
			<xsl:when test="@style">
				<xsl:element name="patterns_node_style">
					<xsl:attribute name="value"><xsl:value-of select="@style"/></xsl:attribute>
				</xsl:element>
		    </xsl:when>
			<xsl:otherwise>
				<patterns_node_style/>
			</xsl:otherwise>
		</xsl:choose>
<!--		<xsl:choose> 
			<xsl:when test="@text">
				<xsl:element name="patterns_node_text">
					<xsl:attribute name="value"><xsl:value-of select="@text"/></xsl:attribute>
				</xsl:element>
		    </xsl:when>
			<xsl:otherwise>
				<patterns_node_text/>
			</xsl:otherwise>
		</xsl:choose>-->
		<xsl:choose> 
			<xsl:when test="@background_color">
				<xsl:element name="patterns_node_backgroundcolor">
					<xsl:attribute name="value"><xsl:value-of select="@background_color"/></xsl:attribute>
				</xsl:element>
		    </xsl:when>
			<xsl:otherwise>
				<patterns_node_backgroundcolor/>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:choose> 
			<xsl:when test="@icon and @icon != 'none'">
				<xsl:element name="patterns_node_icon">
					<xsl:attribute name="value"><xsl:value-of select="@icon"/></xsl:attribute>
				</xsl:element>
		    </xsl:when>
			<xsl:when test="@icon = 'none'">
				<patterns_node_icon/>
		    </xsl:when>
			<xsl:otherwise>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:apply-templates select="node()"/>
	</xsl:template>

	
	<xsl:template match="edge">
		<xsl:choose> 
			<xsl:when test="@color">
				<xsl:element name="patterns_edge_color">
					<xsl:attribute name="value"><xsl:value-of select="@color"/></xsl:attribute>
				</xsl:element>
		    </xsl:when>
			<xsl:otherwise>
				<patterns_edge_color/>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:choose> 
			<xsl:when test="@style">
				<xsl:element name="patterns_edge_style">
					<xsl:attribute name="value"><xsl:value-of select="@style"/></xsl:attribute>
				</xsl:element>
		    </xsl:when>
			<xsl:otherwise>
				<patterns_edge_style/>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:choose> 
			<xsl:when test="@width">
				<xsl:element name="patterns_edge_width">
					<xsl:attribute name="value"><xsl:value-of select="@width"/></xsl:attribute>
				</xsl:element>
		    </xsl:when>
			<xsl:otherwise>
				<patterns_edge_width/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="font">
		<xsl:element name="pattern_node_font">
			<xsl:apply-templates select="@*"/>
		</xsl:element>
	</xsl:template>

	<xsl:template match="child">
		<xsl:element name="patterns_child">
			<xsl:attribute name="pattern"><xsl:value-of select="@pattern"/></xsl:attribute>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
