<#list database.schemas as schema>
<#list database.getTables(schema) as table>- ${table}
<#list table.columns as column> - ${column}
</#list></#list></#list>