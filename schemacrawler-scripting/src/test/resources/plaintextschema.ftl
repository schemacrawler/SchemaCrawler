UTF-8: ¥ · £ · € · $ · ¢ · ₡ · ₢ · ₣ · ₤ · ₥ · ₦ · ₧ · ₨ · ₩ · ₪ · ₫ · ₭ · ₮ · ₯

${catalog.crawlInfo}

<#list catalog.schemas as schema>
<#list catalog.getTables(schema) as table>- ${table}
<#list table.columns as column> - ${column}
</#list></#list></#list>