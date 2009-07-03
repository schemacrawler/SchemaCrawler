<#list database.catalogs as catalog><#list catalog.schemas as schema>
<#list schema.tables as table>- ${table}
<#list table.columns as column> - ${column}
</#list></#list></#list></#list>