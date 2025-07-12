/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.crawl;

import static java.sql.DatabaseMetaData.columnNullable;
import static java.sql.DatabaseMetaData.columnNullableUnknown;
import static schemacrawler.schema.DataTypeType.user_defined;
import static schemacrawler.schemacrawler.InformationSchemaKey.EXT_HIDDEN_TABLE_COLUMNS;
import static schemacrawler.schemacrawler.InformationSchemaKey.TABLE_COLUMNS;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.tableColumnsRetrievalStrategy;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.isBlank;
import schemacrawler.filter.InclusionRuleFilter;
import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.schema.Column;
import schemacrawler.schema.NamedObjectKey;
import schemacrawler.schemacrawler.Identifiers;
import schemacrawler.schemacrawler.InformationSchemaViews;
import schemacrawler.schemacrawler.Query;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import schemacrawler.schemacrawler.exceptions.SchemaCrawlerException;
import schemacrawler.schemacrawler.exceptions.WrappedSQLException;
import us.fatehi.utility.scheduler.TaskDefinition;
import us.fatehi.utility.scheduler.TaskRunner;
import us.fatehi.utility.scheduler.TaskRunners;
import us.fatehi.utility.string.StringFormat;

/** A retriever uses database metadata to get the details about the database table columns. */
final class TableColumnRetriever extends AbstractRetriever {

  private static final Logger LOGGER = Logger.getLogger(TableColumnRetriever.class.getName());

  TableColumnRetriever(
      final RetrieverConnection retrieverConnection,
      final MutableCatalog catalog,
      final SchemaCrawlerOptions options)
      throws SQLException {
    super(retrieverConnection, catalog, options);
  }

  void retrieveTableColumns(
      final NamedObjectList<MutableTable> allTables, final InclusionRule columnInclusionRule)
      throws SQLException {
    requireNonNull(allTables, "No tables provided");

    final InclusionRuleFilter<Column> columnFilter =
        new InclusionRuleFilter<>(columnInclusionRule, true);
    if (columnFilter.isExcludeAll()) {
      LOGGER.log(Level.INFO, "Not retrieving table columns, since this was not requested");
      return;
    }

    final Set<NamedObjectKey> hiddenTableColumnsLookupKeys = retrieveHiddenTableColumnsLookupKeys();

    switch (getRetrieverConnection().get(tableColumnsRetrievalStrategy)) {
      case data_dictionary_all:
        LOGGER.log(Level.INFO, "Retrieving table columns, using fast data dictionary retrieval");
        retrieveTableColumnsFromDataDictionary(
            allTables, columnFilter, hiddenTableColumnsLookupKeys);
        break;

      case metadata:
        LOGGER.log(Level.INFO, "Retrieving table columns");
        retrieveTableColumnsFromMetadata(allTables, columnFilter, hiddenTableColumnsLookupKeys);
        break;

      default:
        LOGGER.log(Level.INFO, "Not retrieving table columns");
        break;
    }
  }

  private boolean createTableColumn(
      final MetadataResultSet results,
      final NamedObjectList<MutableTable> allTables,
      final InclusionRuleFilter<Column> columnFilter,
      final Set<NamedObjectKey> hiddenTableColumnsLookupKeys) {

    // Get the "COLUMN_DEF" value first as it the Oracle driver
    // don't handle it properly otherwise.
    // https://github.com/schemacrawler/SchemaCrawler/issues/835
    final String defaultValue = results.getString("COLUMN_DEF");

    final String catalogName = normalizeCatalogName(results.getString("TABLE_CAT"));
    final String schemaName = normalizeSchemaName(results.getString("TABLE_SCHEM"));
    final String tableName = results.getString("TABLE_NAME");
    final String columnName = results.getString("COLUMN_NAME");
    LOGGER.log(
        Level.FINE,
        new StringFormat(
            "Retrieving table column <%s.%s.%s.%s>",
            catalogName, schemaName, tableName, columnName));
    if (isBlank(columnName)) {
      return false;
    }

    final Optional<MutableTable> optionalTable =
        allTables.lookup(new NamedObjectKey(catalogName, schemaName, tableName));
    if (!optionalTable.isPresent()) {
      return false;
    }

    final MutableTable table = optionalTable.get();
    final MutableColumn column = lookupOrCreateTableColumn(table, columnName);
    column.withQuoting(getRetrieverConnection().getIdentifiers());

    if (columnFilter.test(column) && belongsToSchema(table, catalogName, schemaName)) {
      final int ordinalPosition = results.getInt("ORDINAL_POSITION", 0);
      final int dataType = results.getInt("DATA_TYPE", 0);
      final String typeName = results.getString("TYPE_NAME");
      final int size = results.getInt("COLUMN_SIZE", 0);
      final int decimalDigits = results.getInt("DECIMAL_DIGITS", 0);
      final boolean isNullable =
          results.getInt("NULLABLE", columnNullableUnknown) == columnNullable;
      final boolean isAutoIncremented = results.getBoolean("IS_AUTOINCREMENT");
      final boolean isGenerated = results.getBoolean("IS_GENERATEDCOLUMN");
      final String remarks = results.getString("REMARKS");

      final boolean isHidden = hiddenTableColumnsLookupKeys.contains(column.key());

      column.setOrdinalPosition(ordinalPosition);
      column.setColumnDataType(
          lookupOrCreateColumnDataType(
              user_defined, table.getSchema(), dataType, getColumnTypeName(typeName)));
      column.setSize(size);
      column.setDecimalDigits(decimalDigits);
      column.setNullable(isNullable);
      column.setAutoIncremented(isAutoIncremented);
      column.setGenerated(isGenerated);
      column.setRemarks(remarks);
      if (defaultValue != null) {
        column.setDefaultValue(defaultValue);
      }

      column.addAttributes(results.getAttributes());

      LOGGER.log(
          Level.FINER,
          new StringFormat("Adding %scolumn to table <%s>", isHidden ? "hidden " : "", column));
      if (isHidden) {
        column.setHidden(true);
        table.addHiddenColumn(column);
      } else {
        table.addColumn(column);
      }
      return true;
    }
    return false;
  }

  private String getColumnTypeName(final String typeName) {
    String columnDataTypeName = null;
    if (!isBlank(typeName)) {
      final String[] split = typeName.split("\\.");
      if (split.length > 0) {
        columnDataTypeName = split[split.length - 1];
      }
    }
    // PostgreSQL and IBM DB2 may quote column data type names, so "unquote" them
    final Identifiers identifiers = getRetrieverConnection().getIdentifiers();
    columnDataTypeName = identifiers.unquoteName(columnDataTypeName);
    if (isBlank(columnDataTypeName)) {
      columnDataTypeName = typeName;
    }
    return columnDataTypeName;
  }

  private MutableColumn lookupOrCreateTableColumn(
      final MutableTable table, final String columnName) {
    final Optional<MutableColumn> columnOptional = table.lookupColumn(columnName);
    return columnOptional.orElseGet(() -> new MutableColumn(table, columnName));
  }

  private Set<NamedObjectKey> retrieveHiddenTableColumnsLookupKeys() throws SQLException {

    final Set<NamedObjectKey> hiddenTableColumnsLookupKeys = ConcurrentHashMap.newKeySet();

    final InformationSchemaViews informationSchemaViews =
        getRetrieverConnection().getInformationSchemaViews();
    if (!informationSchemaViews.hasQuery(EXT_HIDDEN_TABLE_COLUMNS)) {
      LOGGER.log(Level.INFO, "No hidden table columns SQL provided");
      return hiddenTableColumnsLookupKeys;
    }

    final RetrievalCounts retrievalCounts = new RetrievalCounts("hidden table columns");
    final Query hiddenColumnsSql = informationSchemaViews.getQuery(EXT_HIDDEN_TABLE_COLUMNS);
    try (final Connection connection = getRetrieverConnection().getConnection();
        final Statement statement = connection.createStatement();
        final MetadataResultSet results =
            new MetadataResultSet(hiddenColumnsSql, statement, getLimitMap()); ) {
      while (results.next()) {
        retrievalCounts.count();
        // NOTE: The column names in the extension table are different
        // than the database metadata column names
        final String catalogName = normalizeCatalogName(results.getString("TABLE_CATALOG"));
        final String schemaName = normalizeSchemaName(results.getString("TABLE_SCHEMA"));
        final String tableName = results.getString("TABLE_NAME");
        final String columnName = results.getString("COLUMN_NAME");

        LOGGER.log(
            Level.FINE,
            new StringFormat(
                "Retrieving hidden column <%s.%s.%s.%s>",
                catalogName, schemaName, tableName, columnName));

        final NamedObjectKey lookupKey =
            new NamedObjectKey(catalogName, schemaName, tableName, columnName);
        hiddenTableColumnsLookupKeys.add(lookupKey);
        retrievalCounts.countIncluded();
      }
    }
    retrievalCounts.log();

    return hiddenTableColumnsLookupKeys;
  }

  private void retrieveTableColumnsFromDataDictionary(
      final NamedObjectList<MutableTable> allTables,
      final InclusionRuleFilter<Column> columnFilter,
      final Set<NamedObjectKey> hiddenTableColumnsLookupKeys)
      throws SQLException {
    final InformationSchemaViews informationSchemaViews =
        getRetrieverConnection().getInformationSchemaViews();
    if (!informationSchemaViews.hasQuery(TABLE_COLUMNS)) {
      throw new ExecutionRuntimeException("No table columns SQL provided");
    }

    final RetrievalCounts retrievalCounts = new RetrievalCounts("table columns");
    final Query tableColumnsSql = informationSchemaViews.getQuery(TABLE_COLUMNS);
    try (final Connection connection = getRetrieverConnection().getConnection();
        final Statement statement = connection.createStatement();
        final MetadataResultSet results =
            new MetadataResultSet(tableColumnsSql, statement, getLimitMap()); ) {
      retrievalCounts.count();
      while (results.next()) {
        final boolean added =
            createTableColumn(results, allTables, columnFilter, hiddenTableColumnsLookupKeys);
        retrievalCounts.countIfIncluded(added);
      }
    }
    retrievalCounts.log();
  }

  private void retrieveTableColumnsFromMetadata(
      final NamedObjectList<MutableTable> allTables,
      final InclusionRuleFilter<Column> columnFilter,
      final Set<NamedObjectKey> hiddenTableColumnsLookupKeys)
      throws SQLException {
    try (final TaskRunner taskRunner =
        TaskRunners.getTaskRunner("retrieve-table-columns-from-metadata", 5); ) {
      for (final MutableTable table : allTables) {
        taskRunner.add(
            new TaskDefinition(
                table.getFullName(),
                () -> {
                  LOGGER.log(
                      Level.FINE, new StringFormat("Retrieving table columns for <%s>", table));
                  try (final Connection connection = getRetrieverConnection().getConnection();
                      final MetadataResultSet results =
                          new MetadataResultSet(
                              connection
                                  .getMetaData()
                                  .getColumns(
                                      table.getSchema().getCatalogName(),
                                      table.getSchema().getName(),
                                      table.getName(),
                                      null),
                              "DatabaseMetaData::getColumns"); ) {
                    while (results.next()) {
                      createTableColumn(
                          results, allTables, columnFilter, hiddenTableColumnsLookupKeys);
                    }
                  } catch (final SQLException e) {
                    throw new WrappedSQLException(
                        String.format(
                            "Could not retrieve table columns for %s <%s>",
                            table.getTableType(), table),
                        e);
                  }
                }));
      }
      taskRunner.submit();
    } catch (final SQLException | SchemaCrawlerException e) {
      throw e;
    } catch (final Exception e) {
      throw new ExecutionRuntimeException(e.getMessage(), e);
    }
  }
}
