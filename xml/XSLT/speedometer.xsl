<?xml version="1.0" encoding="iso-8859-1"?>
<!-- Stylesheet to convert a JMRI Speedometer XML file into displayable     -->
<!-- HTML                                                                   -->

<!-- Used by default when the speedometer file is displayed in a web        -->
<!-- browser                                                                -->

<!-- This is just a basic implementation for debugging purposes, without    -->
<!-- any real attempt at formatting                                         -->

<!-- This file is part of JMRI.  Copyright 2011 - 2023                      -->
<!--                                                                        -->
<!-- JMRI is free software; you can redistribute it and/or modify it under  -->
<!-- the terms of version 2 of the GNU General Public License as published  -->
<!-- by the Free Software Foundation. See the "COPYING" file for a copy     -->
<!-- of this license.                                                       -->
<!--                                                                        -->
<!-- JMRI is distributed in the hope that it will be useful, but WITHOUT    -->
<!-- ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or  -->
<!-- FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License  -->
<!-- for more details.                                                      -->

<xsl:stylesheet	version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<!-- Need to instruct the XSLT processor to use HTML output rules.
     See http://www.w3.org/TR/xslt#output for more details
-->
<xsl:output method="html" encoding="UTF-8"/>

<!-- Define the copyright year for the output page
     In batch work via running Ant, this is defined
     via the build.xml file. We build it by concatenation
     because XPath will evaluate '1997 - 2017' to '20'.
-->
<xsl:param name="JmriCopyrightYear" select="concat('1997','-','2025')" />

<!-- This first template matches our root element in the input file.
     This will trigger the generation of the HTML skeleton document.
     In between we let the processor recursively process any contained
     elements, which is what the apply-templates instruction does.
     We can also pick some stuff out explicitly in the head section using
     value-of instructions.
-->
<xsl:template match='speedometer-config'>
<html>
<head>
<title>JMRI Speedometer File</title>
</head>

<body>
<h2>JMRI Speedometer File</h2>
<xsl:apply-templates/>
<hr/>
This page was produced by <a href="https://www.jmri.org">JMRI</a>.
<p/>Copyright &#169; <xsl:value-of select="$JmriCopyrightYear" /> JMRI Community.
<p/>JMRI, DecoderPro, PanelPro, DispatcherPro and associated logos are our trademarks.
<p/><a href="https://www.jmri.org/Copyright.html">Additional information on copyright, trademarks and licenses is linked here.</a>
</body>
</html>
</xsl:template>

<!-- Display Configuration -->
<xsl:template match="configuration">
<h4>Configuration</h4>
Use Metric Units= "<xsl:value-of select="useMetric"/>"
</xsl:template>

<!-- Display Sensors -->
<xsl:template match="sensors">
<h4>Sensors</h4>
<xsl:apply-templates/>
</xsl:template>

<xsl:template match="sensor">
Sensor
sensorName="<xsl:value-of select="sensorName"/>"
type="<xsl:value-of select="type"/>"
trigger="<xsl:value-of select="trigger"/>"
<xsl:if test="distance">
distance="<xsl:value-of select="distance"/>"
</xsl:if>
<br/>
</xsl:template>

</xsl:stylesheet>
