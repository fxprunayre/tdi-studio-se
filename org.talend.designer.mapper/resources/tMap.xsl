<?xml version="1.0" encoding="utf-8"?>
<!-- edited with XMLSpy v2007 sp1 (http://www.altova.com) by  () -->
<xsl:stylesheet version="1.1" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
		<html>
			<head>
			</head>
			<body>
				<xsl:variable name="rootTable" select="externalNode/mapperTable"/>
				<xsl:variable name="subRootTable" select="externalNode/mapperTable/rootTable/table"/>
				 <xsl:variable name="componentPreviewPic" select="$rootTable/@preview"></xsl:variable>
				     <xsl:if test="string-length($componentPreviewPic)!=0">
				        <div class="FONTSTYLE" align="left">
							<img src="{$componentPreviewPic}" usemap="#jobmap" alt="No image available" class="bordercolor"/>
					</div> 				     
				</xsl:if>
				<xsl:for-each select="$rootTable">
				    <br/>
					<b class="FONTSTYLE">Mapper table for <xsl:value-of select="/externalNode/@name"/>(<xsl:value-of select="@type"/>):</b>
					<br/>
					<br/>				
					<xsl:for-each select="table">
						<table  border="0" width="96%" cellpadding="0" cellspacing="0">
							<tr>
								<td width="15">&#160;&#160;</td>
								<td>
									<b class="FONTSTYLE">Mapper table Properties(<xsl:value-of select="@name"/>):</b>	
									<table class="cols" border="1" width="90%" cellpadding="0" cellspacing="0" style="border-collapse: collapse" bordercolor="#111111" frame="box">
										<th align="center" width="30%" class="TABLECOLUMNSTYLE">Properties</th>
										<th align="left" class="TABLECOLUMNSTYLE">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Values</th>
										<tr>
											<td class="FONTSTYLE" align="center">Name</td>
											<td class="FONTSTYLE" align="left">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;<xsl:value-of select="@name"/>
											</td>
										</tr>
										<tr>
											<td class="FONTSTYLE" align="center">Matching-mode</td>
											<td class="FONTSTYLE" align="left">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;<xsl:value-of select="@matching-mode"/>
											</td>
										</tr>
										<tr>
											<td class="FONTSTYLE" align="center">isMinimized</td>
											<td class="FONTSTYLE" align="left">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;<xsl:value-of select="@isMinimized"/>
											</td>
										</tr>
										<tr>
											<td class="FONTSTYLE" align="center">isReject</td>
											<td class="FONTSTYLE" align="left">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;<xsl:value-of select="@isReject"/>
											</td>
										</tr>
										<tr>
											<td class="FONTSTYLE" align="center">isRejectInnerJoin</td>
											<td class="FONTSTYLE" align="left">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;<xsl:value-of select="@isRejectInnerJoin"/>
											</td>
										</tr>
										<tr>
											<td class="FONTSTYLE" align="center">isInnerJoin</td>
											<td class="FONTSTYLE" align="left">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;<xsl:value-of select="@isInnerJoin"/>
											</td>
										</tr>
									</table>
									<br/>
								<b class="FONTSTYLE">Metadata Table Entries(<xsl:value-of select="@name"/>):</b>
								<br/>
									<table class="cols" width="90%" cellpadding="0" cellspacing="0" style="border-collapse: collapse" bordercolor="#111111" frame="box">
										<tr class="profont">
											<th class="TABLECOLUMNSTYLE" width="25%">Name</th>
											<th class="TABLECOLUMNSTYLE" width="25%">Type</th>
											<th class="TABLECOLUMNSTYLE" width="25%">Expression</th>
											<th class="TABLECOLUMNSTYLE" width="25%">isNullable</th>
										</tr>
										<xsl:for-each select="metadataTableEntries/entry">
											<tr class="FONTSTYLE">
												<td align="center">
													<xsl:value-of select="@name"/>
												</td>
												<td align="center">
													<xsl:value-of select="@type"/>
												</td>
												<td align="center">
													<xsl:value-of select="@expression"/>
												</td>
												<td align="center">
													<xsl:value-of select="@isNullable"/>
												</td>
											</tr>
										</xsl:for-each>
									</table>
									    <br/>
										<b class="FONTSTYLE">Constraint Table Entries(<xsl:value-of select="@name"/>):</b>
									<table class="cols" width="90%" cellpadding="0" cellspacing="0" style="border-collapse: collapse" bordercolor="#111111" frame="box">
										<tr class="profont">
											<th class="TABLECOLUMNSTYLE" width="25%">Name</th>
											<th class="TABLECOLUMNSTYLE" width="25%">Type</th>
											<th class="TABLECOLUMNSTYLE" width="25%">Expression</th>
											<th class="TABLECOLUMNSTYLE" width="25%">isNullable</th>
										</tr>
										<xsl:for-each select="constraintTableEntries/entry">
											<tr class="FONTSTYLE">
												<td align="center">
													<xsl:value-of select="@name"/>
												</td>
												<td align="center">
													<xsl:value-of select="@type"/>
												</td>
												<td align="center">
													<xsl:value-of select="@expression"/>
												</td>
												<td align="center">
													<xsl:value-of select="@isNullable"/>
												</td>
											</tr>
										</xsl:for-each>
									</table>
								</td>
							</tr>
						</table>
						<br/>
					</xsl:for-each>
				</xsl:for-each>
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>
