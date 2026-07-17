---
${support.reportFrontMatter("Database Schema Diagram", "Diagram of the entire database schema")}<#rt>
---

# ${msg.sectionDiagram()}


<#assign Q = '"'>
```mermaid
---
<#if title?? && title?has_content>
title: ${title?json_string}
</#if>
config:
  theme: base
---
erDiagram

  classDef strong_entity stroke:#283593;
  classDef subtype stroke:#1976D2;
  classDef weak_entity stroke:#1976D2;
  classDef unknown stroke:#AAAAAA;
  classDef non_entity stroke:#AAAAAA;

<#list support.entities() as entity>
  ${Q}${support.cleanFullName(entity)}${Q}:::${entity.getType()} {
    <#list entity.getEntityAttributes() as entity_attribute>
      <#assign cleaned = support.stripName(entity_attribute)>
      <#if !cleaned?has_content>
        <#assign cleaned = "UNKNOWN">
      </#if>
      <#assign remarks = "">
      <#if entity_attribute.hasRemarks()>
        <#assign remarks = ' "' + support.singleLineRemarks(entity_attribute) + '"' >
      </#if>
    ${entity_attribute.getType()} ${cleaned}${remarks}
    </#list>
  }

</#list>

<#list er_model.getRelationships() as relationship>
  <#assign cardinality = relationship.getType()>
  <#assign cardinality_name = cardinality.name()>
  <#assign cardinality_symbol = support.cardinalitySymbol(relationship)>
  <#assign label = "foreign key">
  <#if cardinality_name == "many_many">
    <#assign label = "via bridge table ${relationship.getName()}">
  <#elseif relationship.getLeftEntity() == relationship.getRightEntity()>
    <#assign label = "self-reference">
  <#elseif relationship.hasRemarks()>
    <#assign label = support.singleLineRemarks(relationship)>
  </#if>
  ${Q}${support.cleanFullName(relationship.getLeftEntity())}${Q}<#rt>
  ${cardinality_symbol}<#rt>
  ${Q}${support.cleanFullName(relationship.getRightEntity())}${Q} : "${label}"
</#list>
```
