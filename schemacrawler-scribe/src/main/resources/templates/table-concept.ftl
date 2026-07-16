<#import "mermaid.ftl" as mermaid>
<#import "common.ftl" as common>
---
${support.frontMatter(table)}
---

<#-- ==================== PAGE SETUP ==================== -->
<#assign typeStr = support.isView(table)?then(msg.labelView(), msg.labelTable())>
<#assign childFks = support.childForeignKeys(table)>
<#assign parentFks = support.parentForeignKeys(table)>
<#assign rowCount = support.rowCount(table)>

<#-- ==================== TITLE ==================== -->
# ${typeStr} ${table.fullName}

<#-- ==================== DESCRIPTION SECTION ==================== -->
<#if table.remarks?has_content>
## ${msg.sectionDescription()}

${table.remarks}

</#if>

<#-- ==================== METADATA SECTION ==================== -->
## ${msg.sectionMetadata()}

- ${msg.labelType()}: ${support.sentenceCase(support.typeName(table))}
<#assign entityModelType = support.localizedEntityModelType(table)>
<#if entityModelType?has_content>
- ${msg.labelEntityModelType()}: ${entityModelType}
</#if>
- ${msg.labelColumnCount()}: ${table.columns?size?c}
- ${msg.labelChildFkCount()}: ${childFks?size?c}
- ${msg.labelTriggerCount()}: ${table.triggers?size?c}
<#if rowCount gte 0>
- ${msg.labelRowCount()}: ${rowCount?c}
</#if>

<#-- ==================== COLUMNS SECTION ==================== -->
## ${msg.sectionColumns()}

### ${table.fullName}

<@common.markdownTableHeader headers=[
  msg.headerName(),
  msg.headerType(),
  msg.headerNullable(),
  msg.headerDefault(),
  msg.headerRemarks(),
  msg.headerAutoIncremented(),
  msg.headerGenerated()
] />
<#list table.columns as col>
<#assign defaultValue = (col.defaultValue!"")>
<@common.markdownTableRow values=[
  support.escapeMarkdown(col.name),
  support.escapeMarkdown(support.columnType(col) + col.width),
  col.nullable?string("NULL", "NOT NULL"),
  support.escapeMarkdown(defaultValue),
  support.escapeMarkdown(support.singleLineRemarks(col)),
  col.autoIncremented?string(msg.valueYes(), ""),
  col.generated?string(msg.valueYes(), "")
] />
</#list>

<#-- ==================== PRIMARY KEY SECTION ==================== -->
<#if table.hasPrimaryKey()>
## ${msg.sectionPrimaryKey()}

<#assign pkName = (table.primaryKey.name!"")>
<#if pkName?has_content>### ${pkName}</#if>
<@common.markdownTableHeader headers=[msg.headerName()] />
<#list table.primaryKey.constrainedColumns as col>
<@common.markdownTableRow values=[support.escapeMarkdown(col.name)] />
</#list>
</#if>

<#-- ==================== INDEXES SECTION ==================== -->
<#assign nonPkIndexes = support.nonPrimaryIndexes(table)>
<#if nonPkIndexes?has_content>
## ${msg.sectionIndexes()}

<#list nonPkIndexes as idx>
### ${idx.name}
<#if idx.type?string != "unknown" && idx.type?string != "other">${idx.type.name()} <#rt></#if>
<#if idx.unique>${msg.valueUniqueIndex()}</#if>

<@common.markdownTableHeader headers=[msg.headerName(), msg.headerType()] />
<#list idx.columns as col>
<@common.markdownTableRow values=[
  support.escapeMarkdown(col.name),
  support.escapeMarkdown(col.sortSequence.keyword)
] />
</#list>
</#list>
</#if>

<#-- ==================== FOREIGN KEYS SECTION ==================== -->
<#if childFks?has_content>
## ${msg.sectionForeignKeys()}

<#list childFks as fk>
<#assign fkName = support.cleanName(fk)>
### <#if fkName?has_content>${fkName}</#if>

${table.fullName} ${support.cardinalitySymbol(fk)} <@common.tableLink table=fk.primaryKeyTable/>

<#list fk.columnReferences as colRef>
- ${colRef.foreignKeyColumn.name} --> ${colRef.primaryKeyColumn.fullName}
</#list>
</#list>
</#if>

<#-- ==================== CHECK CONSTRAINTS SECTION ==================== -->
<#assign checkConstraints = []>
<#list table.tableConstraints as cc>
<#if !support.hasUserDefinedName(cc) && (cc.type?string) == "check">
<#assign checkConstraints = checkConstraints + [cc]>
</#if>
</#list>
<#if checkConstraints?has_content>
## ${msg.sectionCheckConstraints()}

<#list checkConstraints as cc>
<#assign ccName = support.cleanName(cc)>
<#if ccName?has_content>
### **${ccName}**
</#if>
<@common.markdownTableHeader headers=[msg.headerName()] />
<#list cc.constrainedColumns as col>
<@common.markdownTableRow values=[support.escapeMarkdown(col.name)] />
</#list>
</#list>
</#if>

<#-- ==================== TRIGGERS SECTION ==================== -->
<#if table.hasTriggers()>
## ${msg.sectionTriggers()}

<#list table.triggers as trigger>
### ${trigger.name}

<#assign triggerTiming = msg.valueUnknown()>
<#if trigger.conditionTiming?? && trigger.conditionTiming?string != "unknown">
<#assign triggerTiming = trigger.conditionTiming.value>
</#if>

<#assign triggerEvents = msg.valueUnknown()>
<#if trigger.eventManipulationTypes?? && trigger.eventManipulationTypes?has_content>
<#assign eventNames = []>
<#list trigger.eventManipulationTypes as evt>
<#assign eventNames = eventNames + [evt?string?upper_case]>
</#list>
<#assign triggerEvents = eventNames?join(", ")>
</#if>

<#assign triggerOrientation = msg.valueUnknown()>
<#if trigger.actionOrientation?? && trigger.actionOrientation?string != "unknown">
<#assign triggerOrientation = trigger.actionOrientation?string?upper_case>
</#if>

<#assign triggerOrder = trigger.actionOrder?c>
<#assign triggerCondition = (trigger.actionCondition!"")>
<#assign triggerActionStatement = (trigger.actionStatement!"")>

- ${msg.triggerAttributeTiming()}: ${support.escapeMarkdown(triggerTiming)}
- ${msg.triggerAttributeEvents()}: ${support.escapeMarkdown(triggerEvents)}
- ${msg.triggerAttributeOrientation()}: ${support.escapeMarkdown(triggerOrientation)}
- ${msg.triggerAttributeActionOrder()}: ${support.escapeMarkdown(triggerOrder)}

<#if triggerCondition?has_content>
${msg.triggerAttributeCondition()}: 
```sql
${triggerCondition}
```
</#if>

<#if !triggerActionStatement?has_content>
${msg.triggerAttributeActionStatement()}: 
```sql
${triggerActionStatement}
```
</#if>

</#list>
</#if>

<#-- ==================== DEFINITION SECTION ==================== -->
<#assign definitionText = support.tableDefinition(table)>
<#if definitionText?has_content>
## ${msg.sectionDefinition()}

```sql
${definitionText}
```
</#if>

<#-- ==================== ATTRIBUTES SECTION ==================== -->
<#assign tableAttributes = support.tableAttributes(table)>
<#if tableAttributes?has_content>
## ${msg.sectionAttributes()}

<@common.markdownTableHeader headers=[
  msg.headerAttribute(),
  msg.headerValue()
] />
<#list tableAttributes?keys?sort as attrName>
<#assign attrValue = (tableAttributes[attrName]!msg.valueNa())?string>
<#if attrValue?has_content>
<@common.markdownTableRow values=[
  support.escapeMarkdown(attrName),
  support.escapeMarkdown(attrValue)
] />
</#if>
</#list>
</#if>

<#-- ==================== DIAGRAM SECTION ==================== -->
## ${msg.sectionDiagram()}

<#-- De-duplicate: self-referencing (or mutually-referencing) tables would otherwise appear as
     both a "referencing" and a "referenced" neighbour, and their connecting FK in both
     parentFks and childFks, producing duplicate entity blocks / relationship lines. -->
<#assign diagramTables = [table]>
<#list support.referencingTables(table) as rt>
<#if !diagramTables?seq_contains(rt)>
<#assign diagramTables = diagramTables + [rt]>
</#if>
</#list>
<#list support.referencedTables(table) as rt>
<#if !diagramTables?seq_contains(rt)>
<#assign diagramTables = diagramTables + [rt]>
</#if>
</#list>
<#assign diagramFks = []>
<#list parentFks as fk>
<#if !diagramFks?seq_contains(fk)>
<#assign diagramFks = diagramFks + [fk]>
</#if>
</#list>
<#list childFks as fk>
<#if !diagramFks?seq_contains(fk)>
<#assign diagramFks = diagramFks + [fk]>
</#if>
</#list>
```mermaid
<@mermaid.mermaidDiagram diagramTables=diagramTables diagramFks=diagramFks/>
```

<#-- ==================== CROSS REFERENCES SECTION ==================== -->
<#assign usedByObjects = support.usedByObjects(table)>
<#assign referencedTables = support.referencedTables(table)>
<#if usedByObjects?has_content || referencedTables?has_content>
## ${msg.sectionCrossReferences()}

<#if usedByObjects?has_content>
### ${msg.labelReferencedBy()}
<#list usedByObjects as usedByObject>
<#assign type = support.simpleTypeName(usedByObject)>
<#if type == "table" || type == "view">
- <@common.tableLink table=usedByObject/>
<#elseif type == "table" || type == "view">
- [${usedByObject.fullName}](../routines/${usedByObject.key().slug()}.md)
<#else>
- ${usedByObject.fullName}
</#if>
</#list>
</#if>

<#if referencedTables?has_content>
### ${msg.labelReferences()}
<#list referencedTables as rt>
- <@common.tableLink table=rt/>
</#list>
</#if>
</#if>

<#-- ==================== LINTS SECTION ==================== -->
<#assign lints = support.lintIssues(table)>
<#if lints?has_content>
## ${msg.sectionLintIssues()}

<@common.markdownTableHeader headers=[msg.headerName(), msg.sectionDescription(), msg.headerValue()] />
<#list lints as lint>
<@common.markdownTableRow values=[
  support.severityMessage(lint.severity),
  lint.message,
  lint.valueAsString
] />
</#list>
</#if>
