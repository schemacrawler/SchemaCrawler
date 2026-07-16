<#macro markdownTableHeader headers>
| <#list headers as h>${h}<#sep> | </#list> |
| <#list headers as h>---<#sep> | </#list> |
</#macro>
<#macro markdownTableRow values>
| <#list values as v>${v}<#sep> | </#list> |
</#macro>
<#macro tableLink table pathPrefix="../tables/">
[${table.fullName}](${pathPrefix}${table.key().slug()}.md)<#rt>
</#macro>
<#macro tableFullNameLink tableFullName tableKey pathPrefix="../tables/">
[${tableFullName}](${pathPrefix}${tableKey.slug()}.md)<#rt>
</#macro>
<#macro routineLink routine pathPrefix="../routines/">
[${routine.fullName}](${pathPrefix}${routine.key().slug()}.md)<#rt>
</#macro>