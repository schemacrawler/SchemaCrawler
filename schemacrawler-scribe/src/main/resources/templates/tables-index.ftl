<#import "common.ftl" as common>

# ${msg.navTables()}

<#list catalog.schemas as schema>
<#assign schemaName = schema.fullName!msg.valueNa()>
<#assign hasTablesInSchema = false>
<#list tables as table>
<#if (table.schema.fullName!msg.valueNa()) == schemaName>
<#if !hasTablesInSchema>
## ${schemaName}
<#assign hasTablesInSchema = true>
</#if>
- <#if support.isView(table)>${msg.labelView()}<#else>${msg.labelTable()}</#if>: <@common.tableLink table=table pathPrefix="./"/>
</#if>
</#list>
</#list>
