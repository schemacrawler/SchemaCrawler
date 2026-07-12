<#import "common.ftl" as common>
---
${support.frontMatter(routine)}
---

<#-- ==================== PAGE SETUP ==================== -->
<#assign typeStr = (routine.routineType?string == "function")?then(msg.labelFunction(), msg.labelStoredProcedure())>

<#-- ==================== TITLE ==================== -->
# ${typeStr} ${routine.fullName}

<#-- ==================== DESCRIPTION SECTION ==================== -->
<#if routine.remarks?has_content>
## ${msg.sectionDescription()}

${routine.remarks}

</#if>

<#-- ==================== METADATA SECTION ==================== -->
## ${msg.sectionMetadata()}

- ${msg.labelType()}: ${support.sentenceCase(routine.routineType?string)}
- ${msg.labelReturnType()}: ${routine.returnType}
- ${msg.labelBodyType()}: ${routine.routineBodyType?string}

<#-- ==================== PARAMETERS SECTION ==================== -->
## ${msg.sectionParameters()}

<@common.markdownTableHeader headers=[
  msg.headerName(),
  msg.headerMode(),
  msg.headerType(),
  msg.headerRemarks()
] />
<#list routine.parameters as param>
<@common.markdownTableRow values=[
  support.cleanName(param),
  (param.parameterMode?string)!msg.valueNa(),
  (param.columnDataType.name + param.width)!msg.valueNa(),
  support.singleLineRemarks(param)
] />
</#list>

<#-- ==================== DEFINITION SECTION ==================== -->
<#assign definitionText = support.routineDefinition(routine)>
<#if definitionText?has_content>
## ${msg.sectionDefinition()}

```sql
${definitionText}
```

</#if>
