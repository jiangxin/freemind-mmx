<?xml version="1.0" encoding="iso-8859-1"?>
<!--
    (c) by Gorka Puente García, Nov, 11th 2010
    This file is licensed under the GPL.
-->

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:func="http://exslt.org/functions" xmlns:exsl="http://exslt.org/common" xmlns:str="http://exslt.org/strings" extension-element-prefixes="str func exsl">
	<xsl:output method="text" indent="no" />
	<xsl:strip-space elements="*" />

	<xsl:template match="map">
		<!-- The priority icons mark the permissions -->
		<xsl:variable name="priority_icons">full-0 full-1 full-2 full-3 full-4 full-5 full-6 full-7 full-8 full-9</xsl:variable>
		<xsl:variable name="valid_extensions">xml doc png jpg gif jpeg docx pdf</xsl:variable>

		<!-- Organigram -->
		<xsl:if test="not(node/node[@TEXT='Organigram'])">[WARNING] "Organigram" node is missing/</xsl:if>
		<xsl:if test="node/node[@TEXT='Organigram']">
			<xsl:if test="not(node/node[@TEXT='Organigram']/child::*)">[WARNING] "Roles" are missing/</xsl:if>
			<xsl:if test="not(node/node[@TEXT='Organigram']/node/node)">[WARNING] "Employees" are missing/</xsl:if>
		</xsl:if>
		<!-- Restriction  -->
		<xsl:if test="not(node/node[@TEXT='Restriction'])">[WARNING] "Restriction" node is missing/</xsl:if>
		<xsl:if test="node/node[@TEXT='Restriction']">
			<xsl:if test="not(node/node[@TEXT='Restriction']/node)">[WARNING] "Denial" attributes (read or edit) are missing/</xsl:if>
		</xsl:if>
		<!-- Incomplete restriction-->
		<xsl:for-each select="str:tokenize($priority_icons, ' ')">
			<xsl:variable name="current_icon">
				<xsl:value-of select="." />
			</xsl:variable>
			<xsl:choose>
				<xsl:when test="/map/node/node[@TEXT='Restriction']/descendant::node/icon[@BUILTIN=$current_icon]">
					<xsl:choose>
						<xsl:when test="/map/node/node[@TEXT='Organigram']/descendant::node/icon[@BUILTIN=$current_icon]">
							<xsl:choose>
								<xsl:when test="//node/icon[@BUILTIN=$current_icon]">
								</xsl:when>
								<xsl:otherwise>
									<xsl:text>[ERROR] Restriction "icon </xsl:text>
									<xsl:value-of select="substring($current_icon,6,1)" />
									<xsl:text>" is incomplete: Check your nodes and add "icon </xsl:text>
									<xsl:value-of select="substring($current_icon,6,1)" />
									<xsl:text>"/</xsl:text>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>[ERROR] Restriction "icon </xsl:text>
							<xsl:value-of select="substring($current_icon,6,1)" />
							<xsl:text>" is incomplete: Check restrictions in groups and add "icon </xsl:text>
							<xsl:value-of select="substring($current_icon,6,1)" />
							<xsl:text>"/</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:when test="/map/node/node[@TEXT='Organigram']/descendant::node/icon[@BUILTIN=$current_icon]">
					<xsl:choose>
						<xsl:when test="/map/node/node[@TEXT='Restriction']/descendant::node/icon[@BUILTIN=$current_icon]">
							<xsl:choose>
								<xsl:when test="//node/icon[@BUILTIN=$current_icon]">
								</xsl:when>
								<xsl:otherwise>
									<xsl:text>[ERROR] Restriction "icon </xsl:text>
									<xsl:value-of select="substring($current_icon,6,1)" />
									<xsl:text>" is incomplete: Check your nodes and add "icon </xsl:text>
									<xsl:value-of select="substring($current_icon,6,1)" />
									<xsl:text>"/</xsl:text>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>[ERROR] Restriction "icon </xsl:text>
							<xsl:value-of select="substring($current_icon,6,1)" />
							<xsl:text>" is incomplete: Check denial attributes and add "icon </xsl:text>
							<xsl:value-of select="substring($current_icon,6,1)" />
							<xsl:text>"/</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:when test="//node/icon[@BUILTIN=$current_icon]">
					<xsl:choose>
						<xsl:when test="/map/node/node[@TEXT='Restriction']/descendant::node/icon[@BUILTIN=$current_icon]">
							<xsl:choose>
								<xsl:when test="/map/node/node[@TEXT='Organigram']/descendant::node/icon[@BUILTIN=$current_icon]">
								</xsl:when>
								<xsl:otherwise>
									<xsl:text>[ERROR] Restriction "icon </xsl:text>
									<xsl:value-of select="substring($current_icon,6,1)" />
									<xsl:text>" is incomplete: Check restrictions in groups and add "icon </xsl:text>
									<xsl:value-of select="substring($current_icon,6,1)" />
									<xsl:text>"/</xsl:text>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>[ERROR] Restriction "icon </xsl:text>
							<xsl:value-of select="substring($current_icon,6,1)" />
							<xsl:text>" is incomplete: Check denial attributes and add "icon </xsl:text>
							<xsl:value-of select="substring($current_icon,6,1)" />
							<xsl:text>"/</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
			</xsl:choose>
		</xsl:for-each>
		<!-- Presentation -->
		<xsl:if test="not(node/node[@TEXT='Presentation'])">
			<xsl:text>[WARNING] "Presentation" node is missing/</xsl:text>
		</xsl:if>
		<xsl:if test="node/node[@TEXT='Presentation']">
			<xsl:if test="not(node/node[@TEXT='Presentation']/node[@TEXT='logo'])">
				<xsl:text>[WARNING] "logo" node is missing/</xsl:text>
			</xsl:if>
			<xsl:if test="not(node/node[@TEXT='Presentation']/node[@TEXT='wikiSize'])">
				<xsl:text>[WARNING] "wikiSize" node is missing/</xsl:text>
			</xsl:if>
			<xsl:if test="not(node/node[@TEXT='Presentation']/node[@TEXT='wikiEditFreq'])">
				<xsl:text>[WARNING] "wikiEditFreq" node is missing/</xsl:text>
			</xsl:if>
		</xsl:if>
		<!-- Events -->
		<xsl:if test="not(node/node[@TEXT='Event'])">
			<xsl:text>[WARNING] "Event" node is missing/</xsl:text>
		</xsl:if>
		<!-- Malformed date -->
		<xsl:for-each select="/map/node/node[@TEXT='Event']/node">
			<xsl:variable name="current_event">
				<xsl:value-of select="@TEXT" />
			</xsl:variable>
			<xsl:for-each select="str:tokenize($current_event, '/')">
				<xsl:variable name="current_event_date">
					<xsl:value-of select="." />
				</xsl:variable>
				<xsl:choose>
					<!-- Day -->
					<xsl:when test="(position()=1) and ($current_event_date &lt; 0 or $current_event_date &gt; 31)">
						<xsl:text>[ERROR] Event "</xsl:text>
						<xsl:value-of select="translate($current_event, '/','-')" />
						<xsl:text>" is invalid: "</xsl:text>
						<xsl:value-of select="$current_event_date" />
						<xsl:text>" is an invalid day/</xsl:text>
					</xsl:when>
					<!-- Month -->
					<xsl:when test="(position()=2) and  ($current_event_date &lt; 0 or $current_event_date &gt; 12)">
						<xsl:text>[ERROR] Event "</xsl:text>
						<xsl:value-of select="translate($current_event, '/','-')" />
						<xsl:text>" is invalid: "</xsl:text>
						<xsl:value-of select="$current_event_date" />
						<xsl:text>" is an invalid month/</xsl:text>
					</xsl:when>
					<!-- Year -->
					<xsl:when test="position()=3 and  ($current_event_date &lt; 2000 or $current_event_date &gt; 2100)">
						<xsl:text>[ERROR] Event "</xsl:text>
						<xsl:value-of select="translate($current_event, '/','-')" />
						<xsl:text>" is invalid: "</xsl:text>
						<xsl:value-of select="$current_event_date" />
						<xsl:text>" is an invalid year/</xsl:text>
					</xsl:when>
					<xsl:otherwise>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:for-each>
		</xsl:for-each>
		<!-- Invalid event -->
		<xsl:for-each select="/map/node/node[@TEXT='Event']/node/node">
			<!-- $current_event/@TEXT and $current_event/@ID [ERROR] !-->
			<xsl:variable name="current_event_text">
				<xsl:value-of select="@TEXT" />
			</xsl:variable>
			<xsl:variable name="current_event_id">
				<xsl:value-of select="@ID" />
			</xsl:variable>
			<!-- There's not a node with its name except itself(id) -->
			<xsl:if test="not(//node[@TEXT = $current_event_text and @ID != $current_event_id])">
				<xsl:text>[ERROR] Page "</xsl:text>
				<xsl:value-of select="$current_event_text" />
				<xsl:text>" doesn't exist: Check events/</xsl:text>
			</xsl:if>
		</xsl:for-each>
		<!-- Template -->
		<xsl:if test="not(node/node[@TEXT='Template'])">
			<xsl:text>[WARNING] "Template" node is missing/</xsl:text>
		</xsl:if>
		<!-- Relationships-->
		<!-- Arrowlink from article to category-->
		<xsl:for-each select="//arrowlink">
			<xsl:if test=" ../@STYLE='bubble'">
				<xsl:variable name="points_to">
					<xsl:value-of select="@DESTINATION" />
				</xsl:variable>
				<xsl:if test="//node[@ID = $points_to and ((not(@STYLE) and not(ancestor-or-self::*[@STYLE ='bubble'])) or @STYLE='fork')]">
					<xsl:text>[WARNING] The relationship originated in "</xsl:text>
					<xsl:value-of select="../@TEXT" />
					<xsl:text>" is "relatedWith", shouldn't be "belongsTo"?</xsl:text>
					<xsl:text>/</xsl:text>
				</xsl:if>
			</xsl:if>
		</xsl:for-each>
		<!-- Incorrect source document -->
		<xsl:for-each select="//node[@LINK]">
			<xsl:variable name="file_extension">
				<xsl:value-of select="substring(@LINK, string-length(@LINK)-2, 4)" />
			</xsl:variable>
			<xsl:if test="not(contains($valid_extensions, $file_extension))">
				<xsl:text>[ERROR] "</xsl:text>
				<xsl:choose>
					<xsl:when test="@TEXT =''">
						<xsl:value-of select="../@TEXT" />
						<xsl:text>" child node invalid content: </xsl:text>
					</xsl:when>
					<xsl:when test="@TEXT !=''">
						<xsl:value-of select="@TEXT" />
						<xsl:text>" node invalid content: </xsl:text>
					</xsl:when>
					<xsl:otherwise>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:text>Extension "</xsl:text>
				<xsl:value-of select="$file_extension" />
				<xsl:text>" of linked file is not supported/</xsl:text>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>

	<func:function name="str:tokenize">
		<xsl:param name="string" select="''" />
		<xsl:param name="delimiters" select="' &#x9;&#xA;'" />
		<xsl:choose>
			<xsl:when test="not($string)">
				<func:result select="/.." />
			</xsl:when>
			<xsl:when test="not(function-available('exsl:node-set'))">
				<xsl:message terminate="yes">
        ERROR: EXSLT - Functions implementation of str:tokenize relies on exsl:node-set().
      </xsl:message>
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="tokens">
					<xsl:choose>
						<xsl:when test="not($delimiters)">
							<xsl:call-template name="str:_tokenize-characters">
								<xsl:with-param name="string" select="$string" />
							</xsl:call-template>
						</xsl:when>
						<xsl:otherwise>
							<xsl:call-template name="str:_tokenize-delimiters">
								<xsl:with-param name="string" select="$string" />
								<xsl:with-param name="delimiters" select="$delimiters" />
							</xsl:call-template>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<func:result select="exsl:node-set($tokens)/token" />
			</xsl:otherwise>
		</xsl:choose>
	</func:function>

	<xsl:template name="str:_tokenize-characters">
		<xsl:param name="string" />
		<xsl:if test="$string">
			<token>
				<xsl:value-of select="substring($string, 1, 1)" />
			</token>
			<xsl:call-template name="str:_tokenize-characters">
				<xsl:with-param name="string" select="substring($string, 2)" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="str:_tokenize-delimiters">
		<xsl:param name="string" />
		<xsl:param name="delimiters" />
		<xsl:variable name="delimiter" select="substring($delimiters, 1, 1)" />
		<xsl:choose>
			<xsl:when test="not($delimiter)">
				<token>
					<xsl:value-of select="$string" />
				</token>
			</xsl:when>
			<xsl:when test="contains($string, $delimiter)">
				<xsl:if test="not(starts-with($string, $delimiter))">
					<xsl:call-template name="str:_tokenize-delimiters">
						<xsl:with-param name="string" select="substring-before($string, $delimiter)" />
						<xsl:with-param name="delimiters" select="substring($delimiters, 2)" />
					</xsl:call-template>
				</xsl:if>
				<xsl:call-template name="str:_tokenize-delimiters">
					<xsl:with-param name="string" select="substring-after($string, $delimiter)" />
					<xsl:with-param name="delimiters" select="$delimiters" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="str:_tokenize-delimiters">
					<xsl:with-param name="string" select="$string" />
					<xsl:with-param name="delimiters" select="substring($delimiters, 2)" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
