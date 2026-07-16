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
<#macro lintsTable lints>
<@common.markdownTableHeader headers=[msg.headerName(), msg.sectionDescription(), msg.headerValue()] />
<#list lints as lint>
<#assign objectRef>
<#if lint.objectType?string == 'table'>
<@common.tableFullNameLink tableFullName=lint.objectName tableKey=lint.objectKey/>
<#else>
${lint.objectName}<#rt>
</#if>
</#assign>
<@common.markdownTableRow values=[
  objectRef,
  lint.message,
  lint.valueAsString
] />
</#list>
</#macro>
