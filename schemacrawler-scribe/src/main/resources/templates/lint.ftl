<#import "common.ftl" as common>
---
${frontMatter.reportFrontMatter("Database Lints", "Details of database schema design issues (lints)")}<#rt>
---

# ${msg.sectionLintIssues()}

<#list lintsBySeverity as severity, lints>
## ${support.severityMessage(severity)}

<@common.markdownTableHeader headers=[msg.headerName(), msg.headerDescription(), msg.headerValue()] />
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
