/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.crawl;

import static java.sql.DatabaseMetaData.columnNullable;
import static java.sql.DatabaseMetaData.columnNullableUnknown;
import static java.util.Objects.requireNonNull;
import static schemacrawler.schemacrawler.InformationSchemaKey.EXT_HIDDEN_TABLE_COLUMNS;
import static schemacrawler.schemacrawler.InformationSchemaKey.TABLE_COLUMNS;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.tableColumnsRetrievalStrategy;
import static us.fatehi.utility.Utility.isBlank;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.filter.InclusionRuleFilter;
import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.schema.Column;
import schemacrawler.schema.NamedObjectKey;
import schemacrawler.schema.Schema;
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

      case metadata_over_schemas:
        LOGGER.log(Level.INFO, "Retrieving table columns over schemas");
        retrieveTableColumnsFromMetadataOverSchemas(
            allTables, columnFilter, hiddenTableColumnsLookupKeys);
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
    if (optionalTable.isEmpty()) {
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
      column.setColumnDataType(lookupColumnDataType(table.getSchema(), typeName, dataType));
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

    final String name = "hidden table columns";
    final RetrievalCounts retrievalCounts = new RetrievalCounts(name);
    final Query hiddenColumnsSql = informationSchemaViews.getQuery(EXT_HIDDEN_TABLE_COLUMNS);
    try (final Connection connection = getRetrieverConnection().getConnection(name);
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

    final String name = "table columns";
    final RetrievalCounts retrievalCounts = new RetrievalCounts(name);
    final Query tableColumnsSql = informationSchemaViews.getQuery(TABLE_COLUMNS);
    try (final Connection connection = getRetrieverConnection().getConnection(name);
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
      final String name = "table columns from metadata";
      final RetrievalCounts retrievalCounts = new RetrievalCounts(name);
      for (final MutableTable table : allTables) {
        taskRunner.add(
            new TaskDefinition(
                table.getFullName(),
                () -> {
                  LOGGER.log(
                      Level.INFO, new StringFormat("Retrieving %s for %s", name, table.key()));
                  try (final Connection connection = getRetrieverConnection().getConnection(name);
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
                      retrievalCounts.count();
                      final boolean added =
                          createTableColumn(
                              results, allTables, columnFilter, hiddenTableColumnsLookupKeys);
                      retrievalCounts.countIfIncluded(added);
                    }
                  } catch (final SQLException e) {
                    throw new WrappedSQLException(
                        "Could not retrieve table columns for %s <%s>"
                            .formatted(table.getTableType(), table),
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

  private void retrieveTableColumnsFromMetadataOverSchemas(
      final NamedObjectList<MutableTable> allTables,
      final InclusionRuleFilter<Column> columnFilter,
      final Set<NamedObjectKey> hiddenTableColumnsLookupKeys)
      throws SQLException {

    final Collection<Schema> schemas = catalog.getSchemas();
    final String name = "table columns from metadata over schemas";
    final RetrievalCounts retrievalCounts = new RetrievalCounts(name);
    for (final Schema schema : schemas) {
      if (catalog.getTables(schema).isEmpty()) {
        continue;
      }
      try (final Connection connection = getRetrieverConnection().getConnection(name);
          final SchemaSetter schemaSetter = new SchemaSetter(connection, schema);
          final MetadataResultSet results =
              new MetadataResultSet(
                  connection
                      .getMetaData()
                      .getColumns(schema.getCatalogName(), schema.getName(), null, null),
                  "DatabaseMetaData::getColumns"); ) {
        while (results.next()) {
          retrievalCounts.count(schema.key());
          final boolean added =
              createTableColumn(results, allTables, columnFilter, hiddenTableColumnsLookupKeys);
          retrievalCounts.countIfIncluded(schema.key(), added);
        }
      } catch (final SQLException e) {
        LOGGER.log(
            Level.WARNING,
            e,
            new StringFormat("Could not retrieve table columns for schema <%s>", schema));
      }
      retrievalCounts.log(schema.key());
    }
    retrievalCounts.log();
  }
}
