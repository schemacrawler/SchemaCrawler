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
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.tableCheckConstraintsRetrievalStrategy;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.tableConstraintColumnsRetrievalStrategy;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.tableConstraintsRetrievalStrategy;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.schema.NamedObjectKey;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schema.TableConstraint;
import schemacrawler.schema.TableConstraintType;
import schemacrawler.schemacrawler.InformationSchemaViews;
import schemacrawler.schemacrawler.Query;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import us.fatehi.utility.string.StringFormat;

/** A retriever uses database metadata to get the constraints on the database tables. */
final class TableConstraintRetriever extends AbstractRetriever {

  private static final Logger LOGGER = Logger.getLogger(TableConstraintRetriever.class.getName());

  private final Map<NamedObjectKey, MutableTableConstraint> tableConstraintsMap;

  TableConstraintRetriever(
      final RetrieverConnection retrieverConnection,
      final MutableCatalog catalog,
      final SchemaCrawlerOptions options)
      throws SQLException {
    super(retrieverConnection, catalog, options);
    // NOTE: This map has a pseudo-lookup key to look up
    // table constraints by name directly, without looking up the table
    tableConstraintsMap = new HashMap<>();
  }

  void retrieveTableConstraintColumns() throws SQLException {
    if (tableConstraintsMap.isEmpty()) {
      LOGGER.log(Level.FINE, "No table constraints found");
      return;
    }

    final InformationSchemaViews informationSchemaViews =
        getRetrieverConnection().getInformationSchemaViews();

    if (!informationSchemaViews.hasQuery(CONSTRAINT_COLUMN_USAGE)) {
      LOGGER.log(Level.FINE, "Table constraints columns usage SQL statement was not provided");
      return;
    }
    final Query tableConstraintsColumnsSql =
        informationSchemaViews.getQuery(CONSTRAINT_COLUMN_USAGE);

    switch (getRetrieverConnection().get(tableConstraintColumnsRetrievalStrategy)) {
      case data_dictionary_over_schemas:
        LOGGER.log(
            Level.INFO,
            "Retrieving table constraint columns, using fast data dictionary retrieval"
                + " over schemas");
        retrieveTableConstraintColumnsOverSchemas(tableConstraintsColumnsSql);
        break;

      case data_dictionary_all:
      default:
        LOGGER.log(
            Level.INFO,
            "Retrieving table constraint columns, using fast data dictionary retrieval");
        retrieveTableConstraintColumnsFromDataDictionary(tableConstraintsColumnsSql);
        break;
    }
  }

  void retrieveTableConstraintDefinitions() throws SQLException {
    if (tableConstraintsMap.isEmpty()) {
      LOGGER.log(Level.FINE, "No table constraints found");
      return;
    }

    final InformationSchemaViews informationSchemaViews =
        getRetrieverConnection().getInformationSchemaViews();

    if (!informationSchemaViews.hasQuery(CHECK_CONSTRAINTS)) {
      LOGGER.log(Level.FINE, "Extended table check constraints SQL statement was not provided");
      return;
    }
    final Query checkConstraintSql = informationSchemaViews.getQuery(CHECK_CONSTRAINTS);

    switch (getRetrieverConnection().get(tableCheckConstraintsRetrievalStrategy)) {
      case data_dictionary_over_schemas:
        LOGGER.log(
            Level.INFO,
            "Retrieving check constraint definitions, using fast data dictionary retrieval"
                + " over schemas");
        retrieveTableConstraintDefinitionsOverSchemas(checkConstraintSql);
        break;

      case data_dictionary_all:
      default:
        LOGGER.log(
            Level.INFO,
            "Retrieving check constraint definitions, using fast data dictionary retrieval");
        retrieveTableConstraintDefinitionsFromDataDictionary(checkConstraintSql);
        break;
    }
  }

  /**
   * Retrieves table constraint information from the database, in the INFORMATION_SCHEMA format.
   *
   * <p>IMPORTANT: This retrieval does not use the table constraint map, since it looks up remarks
   * for all constraints, including primary keys. In some databases, primary keys do not have unique
   * names.
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

    final Query extTableConstraintsSql = informationSchemaViews.getQuery(EXT_TABLE_CONSTRAINTS);

    final String name = "table constraint information";
    final RetrievalCounts retrievalCounts = new RetrievalCounts(name);
    try (final Connection connection = getRetrieverConnection().getConnection(name);
        final Statement statement = connection.createStatement();
        final MetadataResultSet results =
            new MetadataResultSet(extTableConstraintsSql, statement, getLimitMap()); ) {

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
  void retrieveTableConstraints() throws SQLException {
    if (catalog.getTables().isEmpty()) {
      LOGGER.log(Level.FINE, "No tables found");
      return;
    }

    final InformationSchemaViews informationSchemaViews =
        getRetrieverConnection().getInformationSchemaViews();

    if (!informationSchemaViews.hasQuery(TABLE_CONSTRAINTS)) {
      LOGGER.log(Level.FINE, "Table constraints SQL statement was not provided");
      return;
    }
    final Query tableConstraintsSql = informationSchemaViews.getQuery(TABLE_CONSTRAINTS);

    switch (getRetrieverConnection().get(tableConstraintsRetrievalStrategy)) {
      case data_dictionary_over_schemas:
        LOGGER.log(
            Level.INFO,
            "Retrieving table constraints, using fast data dictionary retrieval" + " over schemas");
        retrieveTableConstraintsOverSchemas(tableConstraintsSql);
        break;

      case data_dictionary_all:
      default:
        LOGGER.log(
            Level.INFO, "Retrieving table constraints, using fast data dictionary retrieval");
        retrieveTableConstraintsFromDataDictionary(tableConstraintsSql);
        break;
    }
  }

  private boolean addTableConstraintDefinition(final MetadataResultSet results) {
    final String catalogName = normalizeCatalogName(results.getString("CONSTRAINT_CATALOG"));
    final String schemaName = normalizeSchemaName(results.getString("CONSTRAINT_SCHEMA"));
    final String tableName = results.getString("TABLE_NAME");
    final String constraintName = results.getString("CONSTRAINT_NAME");
    LOGGER.log(
        Level.FINER, new StringFormat("Retrieving definition for constraint <%s>", constraintName));
    final String definition = results.getString("CHECK_CLAUSE");

    final MutableTableConstraint tableConstraint = tableConstraintsMap.get(
        new NamedObjectKey(catalogName, schemaName, tableName, constraintName));
    if (tableConstraint == null) {
      LOGGER.log(
          Level.FINEST, new StringFormat("Could not add table constraint <%s>", constraintName));
      return false;
    }
    tableConstraint.setDefinition(definition);

    tableConstraint.addAttributes(results.getAttributes());

    return true;
  }

  /**
   * Retrieves table constraint information from the database, in the INFORMATION_SCHEMA format.
   *
   * @throws SQLException On a SQL exception
   */
  private boolean createTableConstraint(final MetadataResultSet results) {
    final String catalogName = normalizeCatalogName(results.getString("CONSTRAINT_CATALOG"));
    final String schemaName = normalizeSchemaName(results.getString("CONSTRAINT_SCHEMA"));
    final String constraintName = results.getString("CONSTRAINT_NAME");
    LOGGER.log(Level.FINER, new StringFormat("Retrieving constraint <%s>", constraintName));
    // "TABLE_CATALOG", "TABLE_SCHEMA"
    final String tableName = results.getString("TABLE_NAME");

    final Optional<MutableTable> tableOptional = lookupTable(catalogName, schemaName, tableName);
    if (!tableOptional.isPresent()) {
      LOGGER.log(
          Level.FINE,
          new StringFormat("Cannot find table <%s.%s.%s>", catalogName, schemaName, tableName));
      return false;
    }

    final MutableTable table = tableOptional.get();
    final String constraintType = results.getString("CONSTRAINT_TYPE");
    final boolean deferrable = results.getBoolean("IS_DEFERRABLE");
    final boolean initiallyDeferred = results.getBoolean("INITIALLY_DEFERRED");

    final MutableTableConstraint tableConstraint =
        new MutableTableConstraint(table, constraintName);
    tableConstraint.setTableConstraintType(TableConstraintType.valueOfFromValue(constraintType));
    tableConstraint.setDeferrable(deferrable);
    tableConstraint.setInitiallyDeferred(initiallyDeferred);

    tableConstraint.addAttributes(results.getAttributes());

    // Add constraint to table
    table.addTableConstraint(tableConstraint);

    // Save look up for constraint with a simplified key
    tableConstraintsMap.put(table.key().with(constraintName), tableConstraint);

    return true;
  }

  private boolean createTableConstraintColumn(final MetadataResultSet results) {
    final String catalogName = normalizeCatalogName(results.getString("CONSTRAINT_CATALOG"));
    final String schemaName = normalizeSchemaName(results.getString("CONSTRAINT_SCHEMA"));
    // "TABLE_CATALOG", "TABLE_SCHEMA"
    final String tableName = results.getString("TABLE_NAME");
    final String constraintName = results.getString("CONSTRAINT_NAME");

    LOGGER.log(
        Level.FINER,
        new StringFormat(
            "Retrieving table constraint column for <%s.%s.%s>",
            catalogName, schemaName, constraintName));

    final MutableTableConstraint tableConstraint = tableConstraintsMap.get(
        new NamedObjectKey(catalogName, schemaName, tableName, constraintName));
    if (tableConstraint == null) {
      LOGGER.log(
          Level.FINEST, new StringFormat("Could not add column constraint <%s>", constraintName));
      return false;
    }

    final Table table = tableConstraint.getParent();
    if (!table.getName().equals(tableName)) {
      LOGGER.log(
          Level.FINE,
          new StringFormat("Cannot find table <%s.%s.%s>", catalogName, schemaName, tableName));
      return false;
    }

    final String columnName = results.getString("COLUMN_NAME");
    final Optional<MutableColumn> columnOptional = table.lookupColumn(columnName);
    if (!columnOptional.isPresent()) {
      LOGGER.log(
          Level.FINE,
          new StringFormat(
              "Cannot find column <%s.%s.%s.%s>", catalogName, schemaName, tableName, columnName));
      return false;
    }
    final MutableColumn column = columnOptional.get();
    final int ordinalPosition = results.getInt("ORDINAL_POSITION", 0);
    final MutableTableConstraintColumn constraintColumn =
        new MutableTableConstraintColumn(tableConstraint, column);
    constraintColumn.setKeyOrdinalPosition(ordinalPosition);

    tableConstraint.addColumn(constraintColumn);

    return true;
  }

  private void retrieveTableConstraintColumnsFromDataDictionary(
      final Query tableConstraintsColumnsSql) {
    final String name = "table constraints columns";
    final RetrievalCounts retrievalCounts = new RetrievalCounts(name);
    try (final Connection connection = getRetrieverConnection().getConnection(name);
        final Statement statement = connection.createStatement();
        final MetadataResultSet results =
            new MetadataResultSet(tableConstraintsColumnsSql, statement, getLimitMap()); ) {
      while (results.next()) {
        retrievalCounts.count();
        final boolean added = createTableConstraintColumn(results);
        retrievalCounts.countIfIncluded(added);
      }
    } catch (final Exception e) {
      LOGGER.log(Level.WARNING, "Could not retrieve table constraint columns", e);
    }
    retrievalCounts.log();
  }

  private void retrieveTableConstraintColumnsOverSchemas(final Query tableConstraintsColumnsSql)
      throws SQLException {
    final String name = "table constraints columns";
    final RetrievalCounts retrievalCounts = new RetrievalCounts(name);
    for (final Schema schema : getAllSchemas()) {
      if (catalog.getTables(schema).isEmpty()) {
        continue;
      }
      try (final Connection connection = getRetrieverConnection().getConnection(name);
          final SchemaSetter schemaSetter = new SchemaSetter(connection, schema);
          final Statement statement = connection.createStatement();
          final MetadataResultSet results =
              new MetadataResultSet(tableConstraintsColumnsSql, statement, getLimitMap(schema)); ) {
        while (results.next()) {
          retrievalCounts.count(schema.key());
          final boolean added = createTableConstraintColumn(results);
          retrievalCounts.countIfIncluded(schema.key(), added);
        }
      } catch (final Exception e) {
        LOGGER.log(
            Level.WARNING,
            e,
            new StringFormat(
                "Could not retrieve table constraint columns for schema <%s>", schema));
      }
      retrievalCounts.log(schema.key());
    }
    retrievalCounts.log();
  }

  private void retrieveTableConstraintDefinitionsFromDataDictionary(
      final Query checkConstraintSql) {
    final String name = "check constraint definitions";
    final RetrievalCounts retrievalCounts = new RetrievalCounts(name);
    try (final Connection connection = getRetrieverConnection().getConnection(name);
        final Statement statement = connection.createStatement();
        final MetadataResultSet results =
            new MetadataResultSet(checkConstraintSql, statement, getLimitMap()); ) {
      while (results.next()) {
        retrievalCounts.count();
        final boolean added = addTableConstraintDefinition(results);
        retrievalCounts.countIfIncluded(added);
      }
    } catch (final Exception e) {
      LOGGER.log(Level.WARNING, "Could not retrieve check constraints", e);
    }
    retrievalCounts.log();
  }

  private void retrieveTableConstraintDefinitionsOverSchemas(final Query checkConstraintSql)
      throws SQLException {
    final String name = "check constraint definitions";
    final RetrievalCounts retrievalCounts = new RetrievalCounts(name);
    for (final Schema schema : getAllSchemas()) {
      if (catalog.getTables(schema).isEmpty()) {
        continue;
      }
      try (final Connection connection = getRetrieverConnection().getConnection(name);
          final SchemaSetter schemaSetter = new SchemaSetter(connection, schema);
          final Statement statement = connection.createStatement();
          final MetadataResultSet results =
              new MetadataResultSet(checkConstraintSql, statement, getLimitMap(schema)); ) {
        while (results.next()) {
          retrievalCounts.count();
          final boolean added = addTableConstraintDefinition(results);
          retrievalCounts.countIfIncluded(added);
        }
      } catch (final Exception e) {
        LOGGER.log(
            Level.WARNING,
            e,
            new StringFormat(
                "Could not retrieve check constraint definitions for schema <%s>", schema));
      }
      retrievalCounts.log(schema.key());
    }
    retrievalCounts.log();
  }

  /**
   * Retrieves table constraint information from the database, in the INFORMATION_SCHEMA format.
   *
   * @throws SQLException On a SQL exception
   */
  private void retrieveTableConstraintsFromDataDictionary(final Query tableConstraintsSql) {
    final String name = "table constraints";
    final RetrievalCounts retrievalCounts = new RetrievalCounts(name);
    try (final Connection connection = getRetrieverConnection().getConnection(name);
        final Statement statement = connection.createStatement();
        final MetadataResultSet results =
            new MetadataResultSet(tableConstraintsSql, statement, getLimitMap()); ) {
      while (results.next()) {
        retrievalCounts.count();
        final boolean added = createTableConstraint(results);
        retrievalCounts.countIfIncluded(added);
      }
    } catch (final Exception e) {
      LOGGER.log(Level.WARNING, "Could not retrieve table constraint information", e);
      return;
    }
    retrievalCounts.log();
  }

  /**
   * Retrieves table constraint information from the database, in the INFORMATION_SCHEMA format.
   *
   * @throws SQLException On a SQL exception
   */
  private void retrieveTableConstraintsOverSchemas(final Query tableConstraintsSql)
      throws SQLException {
    final String name = "table constraints";
    final RetrievalCounts retrievalCounts = new RetrievalCounts(name);
    for (final Schema schema : getAllSchemas()) {
      if (catalog.getTables(schema).isEmpty()) {
        continue;
      }
      try (final Connection connection = getRetrieverConnection().getConnection(name);
          final SchemaSetter schemaSetter = new SchemaSetter(connection, schema);
          final Statement statement = connection.createStatement();
          final MetadataResultSet results =
              new MetadataResultSet(tableConstraintsSql, statement, getLimitMap(schema)); ) {
        while (results.next()) {
          retrievalCounts.count(schema.key());
          final boolean added = createTableConstraint(results);
          retrievalCounts.countIfIncluded(schema.key(), added);
        }
      } catch (final Exception e) {
        LOGGER.log(
            Level.WARNING,
            e,
            new StringFormat("Could not retrieve table constraints for schema <%s>", schema));
      }
      retrievalCounts.log(schema.key());
    }
    retrievalCounts.log();
  }
}
