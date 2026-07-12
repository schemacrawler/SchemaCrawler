<#import "common.ftl" as common>
# ${msg.sectionLintIssues()}

${lintCount?c} issue(s) found.

<#list lintGroups as group>
## ${group.linterId}

<@common.markdownTableHeader headers=[msg.headerName(), "Severity", msg.sectionDescription()] />
<#list group.entries as entry>
<#assign objectRef><#if entry.table??><@common.tableLink table=entry.table pathPrefix="../tables/"/><#else>${entry.objectName}</#if></#assign>
<@common.markdownTableRow values=[
  objectRef,
  entry.severity,
  entry.message
] />
</#list>

</#list>
