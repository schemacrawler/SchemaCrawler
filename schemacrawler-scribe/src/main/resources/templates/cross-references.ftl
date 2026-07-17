<#import "common.ftl" as common>
---
${support.reportFrontMatter("Cross-references", "Cross-references between database objects")}<#rt>
---

# ${msg.sectionCrossReferences()}

<#if crossReferenceEntries?has_content>
<@common.markdownTableHeader headers=[msg.headerName(), msg.headerType(), msg.labelReferencedBy(), msg.headerType()] />
<#list crossReferenceEntries as entry>
<#assign databaseObjectRef>
<#if entry.databaseObjectType?string == "table" || entry.databaseObjectType?string == "view">
<@common.tableLink table=entry.databaseObject/>
<#elseif entry.databaseObjectType?string == "procedure" || entry.databaseObjectType?string == "function">
<@common.routineLink routine=entry.databaseObject/>
<#else>
${entry.databaseObjectName}
</#if>
</#assign>
<#assign usedByRef>
<#if entry.usedByDatabaseObjectType?string == "table" || entry.usedByDatabaseObjectType?string == "view">
<@common.tableLink table=entry.usedByDatabaseObject/>
<#elseif entry.usedByDatabaseObjectType?string == "procedure" || entry.usedByDatabaseObjectType?string == "function">
<@common.routineLink routine=entry.usedByDatabaseObject/>
<#else>
${entry.usedByDatabaseObjectName}
</#if>
</#assign>
<@common.markdownTableRow values=[databaseObjectRef, entry.databaseObjectType, usedByRef, entry.usedByDatabaseObjectType] />
</#list>
<#else>
_${msg.valueNa()}_
</#if>
