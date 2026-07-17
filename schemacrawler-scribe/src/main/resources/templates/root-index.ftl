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

## ${msg.navTables()}

- [${msg.navTables()}](tables/index.md)

## ${msg.navRoutines()}

- [${msg.navRoutines()}](routines/index.md)

## reports/

- [reports/](reports/index.md)

<#if support.isLintEnabled()>
## ${msg.navLint()}

- [${msg.navLint()}](reports/lint.md)
</#if>

## ${msg.sectionDiagram()}

- [${msg.sectionDiagram()}](reports/schema.md)

## ${msg.sectionCrossReferences()}

- [${msg.sectionCrossReferences()}](reports/cross-references.md)
