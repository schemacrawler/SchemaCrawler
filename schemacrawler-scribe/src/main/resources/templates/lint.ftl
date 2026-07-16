<#import "common.ftl" as common>

# ${msg.sectionLintIssues()}

<#list lintsBySeverity as severity, lints>
## ${support.severityMessage(severity)}

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

</#list>
