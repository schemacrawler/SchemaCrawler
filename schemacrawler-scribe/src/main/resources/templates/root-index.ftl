<#-- ==================== MAIN INDEX ==================== -->
# ${title!support.databaseTitle()}

## ${msg.sectionMetadata()}

<#assign dbVersion = (catalog.crawlInfo.databaseVersion)!>
- ${msg.labelDatabaseProduct()}: <#if dbVersion??>${(dbVersion.productName)!msg.valueUnknown()}<#else>${msg.valueUnknown()}</#if>
- ${msg.labelDatabaseVersion()}: <#if dbVersion??>${(dbVersion.productVersion)!msg.valueUnknown()}<#else>${msg.valueUnknown()}</#if>
<#if support.catalogStats()??>
<#assign catalogCounts = support.catalogStats().counts()>
- ${msg.labelTables()}: ${catalogCounts.tables()?c}
- ${msg.labelViews()}: ${catalogCounts.views()?c}
- ${msg.labelRoutines()}: ${catalogCounts.routines()?c}
- ${msg.labelForeignKeyCount()}: ${catalogCounts.foreignKeys()?c}
</#if>

<#if support.erModelStats()??>
<#assign erModelStats = support.erModelStats()>
## ${msg.sectionErModel()}

<#assign ec = erModelStats.entityCounts>
<#assign rc = erModelStats.relationshipCounts>
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
- ${msg.labelImplicitRelationshipCount()}: ${erModelStats.implicitRelationshipCount?c}
- ${msg.labelUnmodeledTableCount()}: ${erModelStats.unmodeledTableCount?c}
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
