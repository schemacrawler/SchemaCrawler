<#list catalog.getSchema("PUBLIC").tables as table>- ${table}
<#list table.columns as column> - ${column}
</#list></#list>