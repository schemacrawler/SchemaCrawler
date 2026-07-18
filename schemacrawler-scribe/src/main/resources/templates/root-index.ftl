<#-- ==================== MAIN INDEX ==================== -->
# ${title!support.databaseTitle()}

## ${msg.sectionMetadata()}

<#assign dbVersion = (catalog.crawlInfo.databaseVersion)!>
- ${msg.labelDatabaseProduct()}: <#if dbVersion??>${(dbVersion.productName)!msg.valueUnknown()}<#else>${msg.valueUnknown()}</#if>
- ${msg.labelDatabaseVersion()}: <#if dbVersion??>${(dbVersion.productVersion)!msg.valueUnknown()}<#else>${msg.valueUnknown()}</#if>
- ${msg.labelTables()}: ${support.tableCount()?c}
- ${msg.labelViews()}: ${support.viewCount()?c}
- ${msg.labelRoutines()}: ${support.routineCount()?c}
- ${msg.labelForeignKeyCount()}: ${support.foreignKeyCount()?c}

<#if support.erModelStats()??>
<#assign erStats = support.erModelStats()>
## ${msg.sectionErModel()}

<#assign ec = erStats.entityCounts>
<#assign rc = erStats.relationshipCounts>
- ${msg.labelEntityCount()}: ${ec.count?c}
- ${msg.labelStrongEntityCount()}: ${ec.strongEntities?c}
- ${msg.labelWeakEntityCount()}: ${ec.weakEntities?c}
- ${msg.labelSubtypeEntityCount()}: ${ec.subtypes?c}
- ${msg.labelNonEntityCount()}: ${ec.nonEntities?c}
- ${msg.labelUnknownEntityCount()}: ${ec.unknown?c}
- ${msg.labelRelationshipCount()}: ${rc.count?c}
- ${msg.labelOneToOneRelationshipCount()}: ${rc.oneOne?c}
- ${msg.labelOneToManyRelationshipCount()}: ${rc.oneMany?c}
- ${msg.labelZeroToOneRelationshipCount()}: ${rc.zeroOne?c}
- ${msg.labelZeroToManyRelationshipCount()}: ${rc.zeroMany?c}
- ${msg.labelManyToManyRelationshipCount()}: ${rc.manyMany?c}
- ${msg.labelUnknownRelationshipCount()}: ${rc.unknown?c}
- ${msg.labelImplicitRelationshipCount()}: ${erStats.implicitRelationshipCount?c}
- ${msg.labelUnmodeledTableCount()}: ${erStats.unmodeledTableCount?c}
</#if>

## ${msg.navTables()}

- [${msg.navTables()}](tables/index.md)

## ${msg.navRoutines()}

- [${msg.navRoutines()}](routines/index.md)

<#if support.isLintEnabled()>
## ${msg.navLint()}

- [${msg.navLint()}](reports/lint.md)
</#if>

## ${msg.sectionDiagram()}

- [${msg.sectionDiagram()}](reports/schema.md)

## ${msg.sectionCrossReferences()}

- [${msg.sectionCrossReferences()}](reports/cross-references.md)
