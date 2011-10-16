<?xml version="1.0" encoding="iso-8859-1"?>
<!--
    (c) by Gorka Puente García, Nov, 11th 2010
    This file is licensed under the GPL.
-->

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="text" indent="no" />
	<!-- global variables -->
	<!-- The priority icons mark the permissions -->
	<xsl:variable name="priority_icons">full-0 full-1 full-2 full-3 full-4 full-5 full-6 full-7 full-8 full-9</xsl:variable>

	<xsl:strip-space elements="*" />

	<xsl:template match="map">
		<!--Notify if EmailPage extension is to be installed -->
		<xsl:if test="node/icon[@BUILTIN='kmail']">
			<xsl:text>__@kmail@__</xsl:text>
			<xsl:text>&#xA;$wgEmailPageAllowAllUsers = true;&#xA;</xsl:text>
		</xsl:if>
		<!-- Ajax-->
		<xsl:text>&#xA;$wgUseAjax = true;&#xA;</xsl:text>
		<!--Edit pages on double click (JavaScript)-->
		<xsl:text>$wgDefaultUserOptions['editondblclick'] = 1;&#xA;</xsl:text>
		<!--Disable reading by anonymous users-->
		<xsl:text># Disable reading by anonymous users&#xA;</xsl:text>
		<xsl:text>$wgGroupPermissions['*']['read'] = false;&#xA;</xsl:text>
		<xsl:text>&#xA;$wgWhitelistRead = array ("Special:Userlogin", "MediaWiki:Common.css", &#xA;</xsl:text>
		<xsl:text>"MediaWiki:Common.js", "MediaWiki:Monobook.css", "MediaWiki:Monobook.js", "-");&#xA;</xsl:text>
		<!--Disable anonymous editing-->
		<xsl:text>&#xA;# Disable anonymous editing&#xA;</xsl:text>
		<xsl:text>$wgGroupPermissions['*']['edit'] = false;&#xA;</xsl:text>
		<!--Prevent new user registrations except by sysops-->
		<xsl:text>&#xA;# Prevent new user registrations except by sysops&#xA;</xsl:text>
		<xsl:text>$wgGroupPermissions['*']['createaccount'] = false;&#xA;</xsl:text>
		<!-- Group permissions-->
		<xsl:text>&#xA;$groupPower = array(&#xA;</xsl:text>
		<xsl:text> 0 => "*",&#xA; 1 => "user",&#xA; 2 => "autoconfirmed",&#xA; 3 => "emailconfirmed",&#xA; 4 => "bot",&#xA; 5 => "sysop",&#xA; 6 => "bureaucrat");&#xA;&#xA;</xsl:text>
		<xsl:apply-templates select="node" />
	</xsl:template>

	<!-- If underscore "_" appears in a node, then it has to be changed to an space " " -->

	<!-- match "node" -->
	<xsl:template match="node">
		<xsl:choose>
			<!-- Groups (Organigram children), clouds -->
			<xsl:when test="../@TEXT='Organigram'">
				<!--Create groups and grant general permissions-->
				<xsl:text>$groupPower[] = "</xsl:text>
				<xsl:call-template name="normalize-title">
					<xsl:with-param name="name" select="@TEXT" />
				</xsl:call-template>
				<xsl:text>";&#xA;</xsl:text>
				<xsl:text>$wgGroupPermissions['</xsl:text>
				<xsl:call-template name="normalize-title">
					<xsl:with-param name="name" select="@TEXT" />
				</xsl:call-template>
				<xsl:text>']['read']  = true; &#xA;</xsl:text>
				<xsl:text>$wgGroupPermissions['</xsl:text>
				<xsl:call-template name="normalize-title">
					<xsl:with-param name="name" select="@TEXT" />
				</xsl:call-template>
				<xsl:text>']['edit']  = true;</xsl:text>
				<xsl:text>&#xA;&#xA;</xsl:text>
				<!-- Check if any permission is restricted-->
				<xsl:if test="icon">
					<xsl:if test="contains($priority_icons, icon/@BUILTIN)">
						<!-- Extract group permissions-->
						<xsl:variable name="group_permissions">
							<xsl:for-each select="icon/@BUILTIN">
								<xsl:value-of select="." />
								<xsl:text>
								</xsl:text>
							</xsl:for-each>
						</xsl:variable>
						<!-- Current group-->
						<xsl:variable name="group">
							<xsl:call-template name="normalize-title">
								<xsl:with-param name="name" select="@TEXT" />
							</xsl:call-template>
						</xsl:variable>
						<!-- Check if any read restriction-->
						<xsl:for-each select="/map/node/node[@TEXT='Restriction']/node[@TEXT='read']">
							<xsl:if test="contains($group_permissions, @BUILTIN)">
								<xsl:text>$wgWhitelist['sysop']['read']  = $wgBlacklist['</xsl:text>
								<xsl:copy-of select="$group" />
								<xsl:text>']['read'] = array(</xsl:text>
								<!-- Extract all pages restricted to that group-->
								<xsl:variable name="pages">
									<xsl:for-each select="icon">
										<!-- Extract current restriction id-->
										<xsl:variable name="current_permission">
											<xsl:value-of select="@BUILTIN" />
										</xsl:variable>
										<xsl:for-each select="//node/icon">
											<xsl:if test="$current_permission = @BUILTIN and not(ancestor::node/@TEXT = 'Restriction') and not(ancestor::node/@TEXT = 'Organigram')">
												<xsl:text>"</xsl:text>
												<xsl:call-template name="normalize-title">
													<xsl:with-param name="name" select="../@TEXT" />
												</xsl:call-template>
												<xsl:text>"~</xsl:text>
											</xsl:if>
										</xsl:for-each>
									</xsl:for-each>
								</xsl:variable>
								<xsl:value-of select="translate(substring($pages,1, string-length($pages)-1),'&quot;~&quot;','&quot;,&quot;')" />
								<xsl:text>); &#xA;</xsl:text>
							</xsl:if>
						</xsl:for-each>
						<!-- Check if any edit restriction-->
						<xsl:for-each select="/map/node/node[@TEXT='Restriction']/node[@TEXT='edit']">
							<xsl:if test="contains($group_permissions, @BUILTIN)">
								<xsl:text>$wgWhitelist['sysop']['edit']  = $wgBlacklist['</xsl:text>
								<xsl:copy-of select="$group" />
								<xsl:text>']['edit'] = array(</xsl:text>
								<!-- Extract all pages restricted to that group-->
								<xsl:variable name="pages">
									<xsl:for-each select="icon">
										<!-- Extract current restriction id-->
										<xsl:variable name="current_permission">
											<xsl:value-of select="@BUILTIN" />
										</xsl:variable>
										<xsl:for-each select="//node/icon">
											<xsl:if test="$current_permission = @BUILTIN and not(ancestor::node/@TEXT = 'Restriction') and not(ancestor::node/@TEXT = 'Organigram')">
												<xsl:text>"</xsl:text>
												<xsl:call-template name="normalize-title">
													<xsl:with-param name="name" select="../@TEXT" />
												</xsl:call-template>
												<xsl:text>"~</xsl:text>
											</xsl:if>
										</xsl:for-each>
									</xsl:for-each>
								</xsl:variable>
								<xsl:value-of select="translate(substring($pages,1, string-length($pages)-1),'&quot;~&quot;','&quot;,&quot;')" />
								<xsl:text>); &#xA;&#xA;</xsl:text>
							</xsl:if>
						</xsl:for-each>
					</xsl:if>
				</xsl:if>
			</xsl:when>
			<!-- Presentation (logo)-->
			<xsl:when test="parent::node[@TEXT='Presentation'] and @TEXT='logo'">
				<xsl:text>$wgLogo = "__@logo@__/</xsl:text>
				<xsl:value-of select="@LINK" />
				<xsl:text>";</xsl:text>
				<xsl:text>&#xA;</xsl:text>
			</xsl:when>
			<!-- Presentation (skin)-->
			<xsl:when test="parent::node[@TEXT='Presentation'] and @TEXT='wikiSize'">
				<xsl:text>$wgDefaultSkin = '</xsl:text>
				<!-- wikiSize-->
				<xsl:variable name="wikiSize">
					<xsl:value-of select="icon/@BUILTIN" />
				</xsl:variable>
				<!-- wikiFamiliarity-->
				<xsl:variable name="wikiEditFreq">
					<xsl:value-of select="../node[@TEXT='wikiEditFreq']/icon/@BUILTIN" />
				</xsl:variable>
				<!-- go (green) = small, prepare (yellow) = medium, stop (red) = large-->
				<xsl:choose>
					<xsl:when test="$wikiSize = 'go'">
						<xsl:choose>
							<xsl:when test="$wikiEditFreq = 'go'">
								<xsl:text>gumaxvn</xsl:text>
							</xsl:when>
							<xsl:when test="$wikiEditFreq = 'prepare'">
								<xsl:text>gumaxdd</xsl:text>
							</xsl:when>
							<xsl:when test="$wikiEditFreq = 'stop'">
								<xsl:text>gumax</xsl:text>
							</xsl:when>
							<xsl:otherwise>
								<xsl:text>monobook</xsl:text>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:when>
					<xsl:when test="$wikiSize = 'prepare'">
						<xsl:choose>
							<xsl:when test="$wikiEditFreq = 'go'">
								<xsl:text>gumaxvn</xsl:text>
							</xsl:when>
							<xsl:when test="$wikiEditFreq = 'prepare'">
								<xsl:text>gumaxdd</xsl:text>
							</xsl:when>
							<xsl:when test="$wikiEditFreq = 'stop'">
								<xsl:text>monobook</xsl:text>
							</xsl:when>
							<xsl:otherwise>
								<xsl:text>monobook</xsl:text>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:when>
					<xsl:when test="$wikiSize = 'stop'">
						<xsl:choose>
							<xsl:when test="$wikiEditFreq = 'go'">
								<xsl:text>rilpoint</xsl:text>
							</xsl:when>
							<xsl:when test="$wikiEditFreq = 'prepare'">
								<xsl:text>cavendish</xsl:text>
							</xsl:when>
							<xsl:when test="$wikiEditFreq = 'stop'">
								<xsl:text>vector</xsl:text>
							</xsl:when>
							<xsl:otherwise>
								<xsl:text>monobook</xsl:text>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>monobook</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:text>';</xsl:text>
			</xsl:when>
			<xsl:otherwise>
			</xsl:otherwise>
		</xsl:choose>
		<!-- Apply to its subnodes-->
		<xsl:apply-templates select="node" />
	</xsl:template>
	<!-- End match "node" -->

	<!-- Replacement template -->
	<xsl:template name="string-replace-all">
		<xsl:param name="text" />
		<xsl:param name="replace" />
		<xsl:param name="by" />
		<xsl:choose>
			<xsl:when test="contains($text, $replace)">
				<xsl:value-of select="substring-before($text,$replace)" />
				<xsl:value-of select="$by" />
				<xsl:call-template name="string-replace-all">
					<xsl:with-param name="text" select="substring-after($text,$replace)" />
					<xsl:with-param name="replace" select="$replace" />
					<xsl:with-param name="by" select="$by" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$text" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Normalize name template -->
	<xsl:template name="normalize-title">
		<xsl:param name="name" />

		<!-- Escape "\" in node content (node text)-->
		<xsl:variable name="node_name_tmp1">
			<xsl:call-template name="string-replace-all">
				<xsl:with-param name="text" select="normalize-space($name)" />
				<xsl:with-param name="replace" select='"_"' />
				<xsl:with-param name="by" select='" "' />
			</xsl:call-template>
		</xsl:variable>

		<xsl:value-of select="$node_name_tmp1" />
	</xsl:template>

</xsl:stylesheet>
