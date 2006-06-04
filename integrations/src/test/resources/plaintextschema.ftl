<#list schema.tables as table>- ${table}
<#list table.columns as column> - ${column}
</#list></#list>