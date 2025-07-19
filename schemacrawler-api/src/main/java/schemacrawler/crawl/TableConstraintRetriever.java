/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.crawl;

import static schemacrawler.schemacrawler.InformationSchemaKey.CHECK_CONSTRAINTS;
import static schemacrawler.schemacrawler.InformationSchemaKey.CONSTRAINT_COLUMN_USAGE;
import static schemacrawler.schemacrawler.InformationSchemaKey.EXT_TABLE_CONSTRAINTS;
import static schemacrawler.schemacrawler.InformationSchemaKey.TABLE_CONSTRAINTS;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.Objects.requireNonNull;
import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.Schema;
import schemacrawler.schema.TableConstraint;
import schemacrawler.schema.TableConstraintType;
import schemacrawler.schemacrawler.InformationSchemaViews;
import schemacrawler.schemacrawler.Query;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import us.fatehi.utility.string.StringFormat;

/** A retriever uses database metadata to get the constraints on the database tables. */
final class TableConstraintRetriever extends AbstractRetriever {

  private static final Logger LOGGER = Logger.getLogger(TableConstraintRetriever.class.getName());

  private final Map<List<String>, MutableTableConstraint> tableConstraintsMap;

  TableConstraintRetriever(
      final RetrieverConnection retrieverConnection,
      final MutableCatalog catalog,
      final SchemaCrawlerOptions options)
      throws SQLException {
    super(retrieverConnection, catalog, options);
    tableConstraintsMap = new HashMap<>();
  }

  public void matchTableConstraints(final NamedObjectList<MutableTable> allTables) {
    requireNonNull(allTables, "No tables provided");
    for (final MutableTable mutableTable : allTables) {
      if (mutableTable == null) {
        continue;
      }
      matchPrimaryKey(mutableTable);
      addImportedForeignKeys(mutableTable);
    }
  }

  void retrieveTableConstraintDefinitions() {
    if (tableConstraintsMap.isEmpty()) {
      LOGGER.log(Level.FINE, "No table constraints found");
      return;
    }

    final InformationSchemaViews informationSchemaViews =
        getRetrieverConnection().getInformationSchemaViews();

    if (!informationSchemaViews.hasQuery(CHECK_CONSTRAINTS)) {
      LOGGER.log(Level.FINE, "Extended table constraints SQL statement was not provided");
      return;
    }
    final Query extTableConstraintInformationSql =
        informationSchemaViews.getQuery(CHECK_CONSTRAINTS);

    // Get check constraint definitions
    final String name = "table constraint definitions";
    final RetrievalCounts retrievalCounts = new RetrievalCounts(name);
    try (final Connection connection = getRetrieverConnection().getConnection(name);
        final Statement statement = connection.createStatement();
        final MetadataResultSet results =
            new MetadataResultSet(extTableConstraintInformationSql, statement, getLimitMap()); ) {
      while (results.next()) {
        retrievalCounts.count();
        final String catalogName = normalizeCatalogName(results.getString("CONSTRAINT_CATALOG"));
        final String schemaName = normalizeSchemaName(results.getString("CONSTRAINT_SCHEMA"));
        final String constraintName = results.getString("CONSTRAINT_NAME");
        LOGGER.log(
            Level.FINER,
            new StringFormat("Retrieving definition for constraint <%s>", constraintName));
        final String definition = results.getString("CHECK_CLAUSE");

        final MutableTableConstraint tableConstraint =
            tableConstraintsMap.get(Arrays.asList(catalogName, schemaName, constraintName));
        if (tableConstraint == null) {
          LOGGER.log(
              Level.FINEST,
              new StringFormat("Could not add table constraint <%s>", constraintName));
          continue;
        }
        tableConstraint.appendDefinition(definition);

        tableConstraint.addAttributes(results.getAttributes());

        retrievalCounts.countIncluded();
      }
    } catch (final Exception e) {
      LOGGER.log(Level.WARNING, "Could not retrieve check constraints", e);
    }
    retrievalCounts.log();
  }

  /**
   * Retrieves table constraint information from the database, in the INFORMATION_SCHEMA format.
   *
   * @throws SQLException On a SQL exception
   */
  void retrieveTableConstraintInformation() throws SQLException {

    final InformationSchemaViews informationSchemaViews =
        getRetrieverConnection().getInformationSchemaViews();

    if (!informationSchemaViews.hasQuery(EXT_TABLE_CONSTRAINTS)) {
      LOGGER.log(
          Level.INFO,
          "Not retrieving additional table constraint information, since this was not requested");
      LOGGER.log(
          Level.FINE, "Additional table constraints information SQL statement was not provided");
      return;
    }

    LOGGER.log(Level.INFO, "Retrieving additional table constraint information");

    final Query extTableConstraintsInformationSql =
        informationSchemaViews.getQuery(EXT_TABLE_CONSTRAINTS);

    final String name = "table constraint information";
    final RetrievalCounts retrievalCounts = new RetrievalCounts(name);
    try (final Connection connection = getRetrieverConnection().getConnection(name);
        final Statement statement = connection.createStatement();
        final MetadataResultSet results =
            new MetadataResultSet(extTableConstraintsInformationSql, statement, getLimitMap()); ) {

      while (results.next()) {
        retrievalCounts.count();
        final String catalogName = normalizeCatalogName(results.getString("CONSTRAINT_CATALOG"));
        final String schemaName = normalizeSchemaName(results.getString("CONSTRAINT_SCHEMA"));
        final String tableName = results.getString("TABLE_NAME");
        final String tableConstraintName = results.getString("CONSTRAINT_NAME");

        final Optional<MutableTable> tableOptional =
            lookupTable(catalogName, schemaName, tableName);
        if (!tableOptional.isPresent()) {
          LOGGER.log(
              Level.FINE,
              new StringFormat(
                  "Cannot find table <%s.%s.%s>", catalogName, schemaName, tableConstraintName));
          continue;
        }

        LOGGER.log(
            Level.FINER,
            new StringFormat(
                "Retrieving additional table constraint information <%s>", tableConstraintName));
        final MutableTable table = tableOptional.get();
        final Optional<TableConstraint> tableConstraintOptional =
            table.lookupTableConstraint(tableConstraintName);
        if (!tableConstraintOptional.isPresent()) {
          LOGGER.log(
              Level.FINE,
              new StringFormat(
                  "Cannot find table constraint <%s.%s.%s.%s>",
                  catalogName, schemaName, tableName, tableConstraintName));
          continue;
        }

        final TableConstraint tableConstraint = tableConstraintOptional.get();

        final String remarks = results.getString("REMARKS");

        tableConstraint.setRemarks(remarks);

        final Map<String, Object> attributes = results.getAttributes();
        final Set<Entry<String, Object>> entrySet = attributes.entrySet();
        for (final Entry<String, Object> entry : entrySet) {
          tableConstraint.setAttribute(entry.getKey(), entry.getValue());
        }

        retrievalCounts.countIncluded();
      }
    } catch (final Exception e) {
      LOGGER.log(Level.WARNING, "Could not retrieve table constraint information", e);
    }
    retrievalCounts.log();
  }

  /**
   * Retrieves table constraint information from the database, in the INFORMATION_SCHEMA format.
   *
   * @throws SQLException On a SQL exception
   */
  void retrieveTableConstraints() {

    final InformationSchemaViews informationSchemaViews =
        getRetrieverConnection().getInformationSchemaViews();

    createTableConstraints(tableConstraintsMap, informationSchemaViews);

    if (!tableConstraintsMap.isEmpty()) {
      retrieveTableConstraintsColumns(tableConstraintsMap, informationSchemaViews);
    }
  }

  /**
   * Add foreign keys as table constraints. Foreign keys are not loaded by the CONSTRAINTS view in
   * the information schema views, so they can be added in without fear of duplication.
   *
   * @param table Table to add constraints to
   */
  private void addImportedForeignKeys(final MutableTable table) {
    final Collection<ForeignKey> importedForeignKeys = table.getImportedForeignKeys();
    for (final ForeignKey foreignKey : importedForeignKeys) {
      final Optional<TableConstraint> lookupTableConstraint =
          table.lookupTableConstraint(foreignKey.getName());
      if (lookupTableConstraint.isPresent()) {
        final TableConstraint tableConstraint = lookupTableConstraint.get();
        copyRemarksAndAttributes(tableConstraint, foreignKey);
        table.removeTableConstraint(tableConstraint);
      }
      // Add or replace the table constraint with the foreign key, which has more information like
      // column mappings
      table.addTableConstraint(foreignKey);
    }
  }

  private void copyRemarksAndAttributes(
      final TableConstraint tableConstraint, final DatabaseObject databaseObject) {
    // Copy remarks over
    if (!databaseObject.hasRemarks() && tableConstraint.hasRemarks()) {
      databaseObject.setRemarks(tableConstraint.getRemarks());
    }
    // Copy attributes over
    for (final Entry<String, Object> attribute : tableConstraint.getAttributes().entrySet()) {
      databaseObject.setAttribute(attribute.getKey(), attribute.getValue());
    }
  }

  private void createTableConstraints(
      final Map<List<String>, MutableTableConstraint> tableConstraintsMap,
      final InformationSchemaViews informationSchemaViews) {
    if (!informationSchemaViews.hasQuery(TABLE_CONSTRAINTS)) {
      LOGGER.log(Level.FINE, "Table constraints SQL statement was not provided");
      return;
    }

    final String name = "table constraints";
    final RetrievalCounts retrievalCounts = new RetrievalCounts(name);
    final Query tableConstraintsInformationSql = informationSchemaViews.getQuery(TABLE_CONSTRAINTS);
    try (final Connection connection = getRetrieverConnection().getConnection(name);
        final Statement statement = connection.createStatement();
        final MetadataResultSet results =
            new MetadataResultSet(tableConstraintsInformationSql, statement, getLimitMap()); ) {
      while (results.next()) {
        retrievalCounts.count();
        final String catalogName = normalizeCatalogName(results.getString("CONSTRAINT_CATALOG"));
        final String schemaName = normalizeSchemaName(results.getString("CONSTRAINT_SCHEMA"));
        final String constraintName = results.getString("CONSTRAINT_NAME");
        LOGGER.log(Level.FINER, new StringFormat("Retrieving constraint <%s>", constraintName));
        // "TABLE_CATALOG", "TABLE_SCHEMA"
        final String tableName = results.getString("TABLE_NAME");

        final Optional<MutableTable> tableOptional =
            lookupTable(catalogName, schemaName, tableName);
        if (!tableOptional.isPresent()) {
          LOGGER.log(
              Level.FINE,
              new StringFormat("Cannot find table <%s.%s.%s>", catalogName, schemaName, tableName));
          continue;
        }

        final MutableTable table = tableOptional.get();
        final String constraintType = results.getString("CONSTRAINT_TYPE");
        final boolean deferrable = results.getBoolean("IS_DEFERRABLE");
        final boolean initiallyDeferred = results.getBoolean("INITIALLY_DEFERRED");

        final MutableTableConstraint tableConstraint =
            new MutableTableConstraint(table, constraintName);
        tableConstraint.setTableConstraintType(
            TableConstraintType.valueOfFromValue(constraintType));
        tableConstraint.setDeferrable(deferrable);
        tableConstraint.setInitiallyDeferred(initiallyDeferred);

        tableConstraint.addAttributes(results.getAttributes());

        // Add constraint to table
        table.addTableConstraint(tableConstraint);
        retrievalCounts.countIncluded();

        // Add to map, since we will need this later
        final Schema schema = table.getSchema();
        tableConstraintsMap.put(
            Arrays.asList(schema.getCatalogName(), schema.getName(), constraintName),
            tableConstraint);
      }
    } catch (final Exception e) {
      LOGGER.log(Level.WARNING, "Could not retrieve table constraint information", e);
      return;
    }
    retrievalCounts.log();
  }

  private void matchPrimaryKey(final MutableTable table) {
    if (!table.hasPrimaryKey()) {
      return;
    }
    final MutablePrimaryKey primaryKey = table.getPrimaryKey();
    // Remove table constraints that are primary keys, if the columns match
    for (final TableConstraint tableConstraint : table.getTableConstraints()) {
      if (tableConstraint.getType() == TableConstraintType.primary_key
          && (primaryKey.getName().equals(tableConstraint.getName())
              || primaryKey
                  .getConstrainedColumns()
                  .equals(tableConstraint.getConstrainedColumns()))) {
        copyRemarksAndAttributes(tableConstraint, primaryKey);
        table.removeTableConstraint(tableConstraint);
      }
    }
    // Add back primary key as table constraints
    table.addTableConstraint(primaryKey);
  }

  private void retrieveTableConstraintsColumns(
      final Map<List<String>, MutableTableConstraint> tableConstraintsMap,
      final InformationSchemaViews informationSchemaViews) {
    if (!informationSchemaViews.hasQuery(CONSTRAINT_COLUMN_USAGE)) {
      LOGGER.log(Level.FINE, "Table constraints columns usage SQL statement was not provided");
      return;
    }

    final String name = "table constraints columns";
    final RetrievalCounts retrievalCounts = new RetrievalCounts(name);
    final Query tableConstraintsColumnsInformationSql =
        informationSchemaViews.getQuery(CONSTRAINT_COLUMN_USAGE);

    try (final Connection connection = getRetrieverConnection().getConnection(name);
        final Statement statement = connection.createStatement();
        final MetadataResultSet results =
            new MetadataResultSet(
                tableConstraintsColumnsInformationSql, statement, getLimitMap()); ) {
      while (results.next()) {
        retrievalCounts.count();
        final String catalogName = normalizeCatalogName(results.getString("CONSTRAINT_CATALOG"));
        final String schemaName = normalizeSchemaName(results.getString("CONSTRAINT_SCHEMA"));
        final String constraintName = results.getString("CONSTRAINT_NAME");
        LOGGER.log(
            Level.FINER,
            new StringFormat("Retrieving definition for constraint <%s>", constraintName));

        final MutableTableConstraint tableConstraint =
            tableConstraintsMap.get(Arrays.asList(catalogName, schemaName, constraintName));
        if (tableConstraint == null) {
          LOGGER.log(
              Level.FINEST,
              new StringFormat("Could not add column constraint <%s>", constraintName));
          continue;
        }

        // "TABLE_CATALOG", "TABLE_SCHEMA"
        final String tableName = results.getString("TABLE_NAME");

        final Optional<MutableTable> tableOptional =
            lookupTable(catalogName, schemaName, tableName);
        if (!tableOptional.isPresent()) {
          LOGGER.log(
              Level.FINE,
              new StringFormat("Cannot find table <%s.%s.%s>", catalogName, schemaName, tableName));
          continue;
        }

        final MutableTable table = tableOptional.get();
        final String columnName = results.getString("COLUMN_NAME");
        final Optional<MutableColumn> columnOptional = table.lookupColumn(columnName);
        if (!columnOptional.isPresent()) {
          LOGGER.log(
              Level.FINE,
              new StringFormat(
                  "Cannot find column <%s.%s.%s.%s>",
                  catalogName, schemaName, tableName, columnName));
          continue;
        }
        final MutableColumn column = columnOptional.get();
        final int ordinalPosition = results.getInt("ORDINAL_POSITION", 0);
        final MutableTableConstraintColumn constraintColumn =
            new MutableTableConstraintColumn(tableConstraint, column);
        constraintColumn.setKeyOrdinalPosition(ordinalPosition);

        tableConstraint.addColumn(constraintColumn);
        retrievalCounts.countIncluded();
      }
    } catch (final Exception e) {
      LOGGER.log(Level.WARNING, "Could not retrieve check constraints", e);
    }
    retrievalCounts.log();
  }
}
