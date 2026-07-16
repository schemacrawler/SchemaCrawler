<#import "common.ftl" as common>

# ${msg.sectionLintIssues()}

<#list lintsBySeverity as severity, lints>
## ${support.severityMessage(severity)}

<@common.lintsTable lints=lints/>

</#list>
