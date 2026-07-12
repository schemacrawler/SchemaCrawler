<#-- One Mermaid erDiagram entity block for a table, listing its columns with PK/FK qualifiers. -->
<#macro entityBlock table>
${support.cleanFullName(table)} {
<#list table.columns as col>
    ${support.columnType(col)?replace(" ", "_")} ${support.cleanName(col)}<#if support.isPrimaryKeyColumn(col)> PK<#elseif support.isForeignKeyColumn(col)> FK</#if>
</#list>
}
</#macro>

<#-- One Mermaid erDiagram relationship line for a foreign key, foreign-key side to primary-key side. -->
<#macro relationshipLine fk>
${support.cleanFullName(fk.foreignKeyTable)} ${support.mermaidCardinality(fk)} ${support.cleanFullName(fk.primaryKeyTable)} : <#rt>
<#if support.hasUserDefinedName(fk)>"${support.cleanName(fk)}"<#else>""</#if>
</#macro>


<#macro mermaidDiagram diagramTables diagramFks>
---
config:
  theme: 'neutral'
---
erDiagram
  <#list diagramTables as dt>
    <@.namespace.entityBlock table=dt/>
  </#list>
  <#list diagramFks as fk>
    <@.namespace.relationshipLine fk=fk/>
  </#list>
</#macro>
