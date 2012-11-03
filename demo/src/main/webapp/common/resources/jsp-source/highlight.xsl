<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output encoding="ISO-8859-1" indent="no" omit-xml-declaration="yes"/>

<xsl:template match="code">
<xsl:value-of select="text()"   disable-output-escaping="yes" />
</xsl:template>

<xsl:template match="cpp-linecomment">
<span class="cpp-comment">//<xsl:value-of select="text()"   disable-output-escaping="yes" /></span>
</xsl:template>

<xsl:template match="vb-comment">
<span class="cpp-comment"><xsl:value-of select="text()"   disable-output-escaping="yes" /></span>
</xsl:template>

<xsl:template match="cpp-blockcomment">
<span class="cpp-comment">/*<xsl:value-of select="text()"   disable-output-escaping="yes" />*/</span>
</xsl:template>

<xsl:template match="literal">
<span class="cpp-literal"><xsl:value-of select="text()"   disable-output-escaping="yes" /></span>
</xsl:template>

<xsl:template match="keyword">
<span class="cpp-keyword"><xsl:value-of select="text()"   disable-output-escaping="yes" /></span>
</xsl:template>

<xsl:template match="pgl">
<span class="pgl"><xsl:value-of select="text()"   disable-output-escaping="yes" /></span>
</xsl:template>

<xsl:template match="preprocessor">
<span class="cpp-preprocessor"><xsl:value-of select="text()"   disable-output-escaping="yes" /></span>
</xsl:template>

<xsl:template match="xml-value"><xsl:value-of select="text()"   disable-output-escaping="yes" /></xsl:template>
<xsl:template match="xml-tag"><span class="xml-tag"><xsl:value-of select="text()"   disable-output-escaping="yes" /></span></xsl:template>
<xsl:template match="xml-bracket"><span class="xml-bracket"><xsl:value-of select="text()"/></span></xsl:template>
<xsl:template match="xml-cdata">
	<span class="xml-bracket"><xsl:text>&lt;![CDATA[</xsl:text></span>
	<span class="xml-cdata"><xsl:value-of select="text()"   disable-output-escaping="yes" /></span>
	<span class="xml-bracket"><xsl:text>]]&gt;</xsl:text></span>
</xsl:template>
<xsl:template match="xml-attribute-name"><span class="xml-attribute-name"><xsl:value-of select="text()"   disable-output-escaping="yes" /></span></xsl:template>
<xsl:template match="xml-attribute-value"><span class="xml-attribute-value"><xsl:value-of select="text()"   disable-output-escaping="yes" /></span></xsl:template>

<xsl:template match="parsedcode">
	<xsl:choose>
		<xsl:when test="@in-box[.=0]">
			<xsl:element name="span">
				<xsl:attribute name="class">cpp-inline</xsl:attribute>
				<xsl:attribute name="lang"><xsl:value-of select="@lang"/></xsl:attribute>
				<xsl:apply-templates/>
			</xsl:element>
		</xsl:when>
		<xsl:otherwise>
			<xsl:element name="pre">
				<xsl:attribute name="class">cpp-pre</xsl:attribute>
				<xsl:attribute name="lang"><xsl:value-of select="@lang"/></xsl:attribute>
				<xsl:apply-templates/>
			</xsl:element>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<xsl:template match="/">
	<xsl:apply-templates/>
</xsl:template>

</xsl:stylesheet>
