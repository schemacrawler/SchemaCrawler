/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package schemacrawler.crawl;

import static schemacrawler.schemacrawler.InformationSchemaKey.ADDITIONAL_COLUMN_ATTRIBUTES;
import static schemacrawler.schemacrawler.InformationSchemaKey.ADDITIONAL_TABLE_ATTRIBUTES;
import static schemacrawler.schemacrawler.InformationSchemaKey.EXT_INDEXES;
import static schemacrawler.schemacrawler.InformationSchemaKey.EXT_TABLES;
import static schemacrawler.schemacrawler.InformationSchemaKey.TRIGGERS;
import static schemacrawler.schemacrawler.InformationSchemaKey.VIEWS;
import static schemacrawler.schemacrawler.InformationSchemaKey.VIEW_TABLE_USAGE;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.plugin.EnumDataTypeHelper;
import schemacrawler.plugin.EnumDataTypeInfo;
import schemacrawler.schema.ActionOrientationType;
import schemacrawler.schema.CheckOptionType;
import schemacrawler.schema.ConditionTimingType;
import schemacrawler.schema.EventManipulationType;
import schemacrawler.schemacrawler.InformationSchemaViews;
import schemacrawler.schemacrawler.Query;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import us.fatehi.utility.string.StringFormat;

/** A retriever uses database metadata to get the extended details about the database tables. */
final class TableExtRetriever extends AbstractRetriever {

  private static final Logger LOGGER = Logger.getLogger(TableExtRetriever.class.getName());

  TableExtRetriever(
      final RetrieverConnection retrieverConnection,
      final MutableCatalog catalog,
      final SchemaCrawlerOptions options)
      throws SQLException {
    super(retrieverConnection, catalog, options);
  }

  /**
   * Retrieves additional column attributes from the database.
   *
   * @throws SQLException On a SQL exception
   */
  void retrieveAdditionalColumnAttributes() throws SQLException {
    final InformationSchemaViews informationSchemaViews =
        getRetrieverConnection().getInformationSchemaViews();
    if (!informationSchemaViews.hasQuery(ADDITIONAL_COLUMN_ATTRIBUTES)) {
      LOGGER.log(
          Level.INFO, "Not retrieving additional column attributes, since this was not requested");
      LOGGER.log(Level.FINE, "Additional column attributes SQL statement was not provided");
      return;
    }
    final Query columnAttributesSql = informationSchemaViews.getQuery(ADDITIONAL_COLUMN_ATTRIBUTES);

    try (final Connection connection = getRetrieverConnection().getConnection();
        final Statement statement = connection.createStatement();
        final MetadataResultSet results =
            new MetadataResultSet(columnAttributesSql, statement, getSchemaInclusionRule()); ) {

      while (results.next()) {
        final String catalogName = normalizeCatalogName(results.getString("TABLE_CATALOG"));
        final String schemaName = normalizeSchemaName(results.getString("TABLE_SCHEMA"));
        final String tableName = results.getString("TABLE_NAME");
        final String columnName = results.getString("COLUMN_NAME");
        LOGGER.log(Level.FINER, "Retrieving additional column attributes: " + columnName);

        final Optional<MutableTable> tableOptional =
            lookupTable(catalogName, schemaName, tableName);
        if (!tableOptional.isPresent()) {
          LOGGER.log(
              Level.FINE,
              new StringFormat("Cannot find table <%s.%s.%s>", catalogName, schemaName, tableName));
          continue;
        }

        final MutableTable table = tableOptional.get();
        final Optional<MutableColumn> columnOptional = table.lookupColumn(columnName);
        if (!columnOptional.isPresent()) {
          LOGGER.log(
              Level.FINE,
              new StringFormat(
                  "Cannot find column <%s.%s.%s.%s>",
                  catalogName, schemaName, tableName, columnName));
        } else {
          final MutableColumn column = columnOptional.get();
          column.addAttributes(results.getAttributes());
        }
      }
    } catch (final Exception e) {
      LOGGER.log(Level.WARNING, "Could not retrieve additional column attributes", e);
    }
  }

  /** Retrieves additional column metadata. */
  void retrieveAdditionalColumnMetadata() {

    try (final Connection connection = getRetrieverConnection().getConnection(); ) {
      final EnumDataTypeHelper enumDataTypeHelper =
          getRetrieverConnection().getEnumDataTypeHelper();

      final NamedObjectList<MutableTable> tables = catalog.getAllTables();
      for (final MutableTable table : tables) {
        final NamedObjectList<MutableColumn> columns = table.getAllColumns();
        for (final MutableColumn column : columns) {
          MutableColumnDataType columnDataType = (MutableColumnDataType) column.getColumnDataType();

          // Check for enumerated column data-types
          final EnumDataTypeInfo enumDataTypeInfo =
              enumDataTypeHelper.getEnumDataTypeInfo(column, columnDataType, connection);
          switch (enumDataTypeInfo.getType()) {
            case enumerated_column:
              // Create new column data-type with enumeration
              final MutableColumnDataType copiedColumnDataType =
                  new MutableColumnDataType(columnDataType);
              columnDataType = copiedColumnDataType; // overwrite with new column data-type
              columnDataType.setEnumValues(enumDataTypeInfo.getEnumValues());
              break;
            case enumerated_data_type:
              // Update column data-type with enumeration
              columnDataType.setEnumValues(enumDataTypeInfo.getEnumValues());
              break;
            default:
              break;
          }

          column.setColumnDataType(columnDataType);
        }
      }
    } catch (final SQLException e) {
      LOGGER.log(Level.WARNING, "Could not retrieve additional column metadata", e);
    }
  }

  /**
   * Retrieves additional table attributes from the database.
   *
   * @throws SQLException On a SQL exception
   */
  void retrieveAdditionalTableAttributes() throws SQLException {
    final InformationSchemaViews informationSchemaViews =
        getRetrieverConnection().getInformationSchemaViews();
    if (!informationSchemaViews.hasQuery(ADDITIONAL_TABLE_ATTRIBUTES)) {
      LOGGER.log(
          Level.INFO, "Not retrieving additional table attributes, since this was not requested");
      LOGGER.log(Level.FINE, "Additional table attributes SQL statement was not provided");
      return;
    }
    final Query tableAttributesSql = informationSchemaViews.getQuery(ADDITIONAL_TABLE_ATTRIBUTES);

    try (final Connection connection = getRetrieverConnection().getConnection();
        final Statement statement = connection.createStatement();
        final MetadataResultSet results =
            new MetadataResultSet(tableAttributesSql, statement, getSchemaInclusionRule()); ) {

      while (results.next()) {
        final String catalogName = normalizeCatalogName(results.getString("TABLE_CATALOG"));
        final String schemaName = normalizeSchemaName(results.getString("TABLE_SCHEMA"));
        final String tableName = results.getString("TABLE_NAME");
        LOGGER.log(Level.FINER, "Retrieving additional table attributes: " + tableName);

        final Optional<MutableTable> tableOptional =
            lookupTable(catalogName, schemaName, tableName);
        if (!tableOptional.isPresent()) {
          LOGGER.log(
              Level.FINE,
              new StringFormat("Cannot find table <%s.%s.%s>", catalogName, schemaName, tableName));
          continue;
        }

        final MutableTable table = tableOptional.get();
        table.addAttributes(results.getAttributes());
      }
    } catch (final Exception e) {
      LOGGER.log(Level.WARNING, "Could not retrieve additional table attributes", e);
    }
  }

  /**
   * Retrieves index information from the database, in the INFORMATION_SCHEMA format.
   *
   * @throws SQLException On a SQL exception
   */
  void retrieveIndexInformation() throws SQLException {
    final InformationSchemaViews informationSchemaViews =
        getRetrieverConnection().getInformationSchemaViews();

    if (!informationSchemaViews.hasQuery(EXT_INDEXES)) {
      LOGGER.log(
          Level.INFO, "Not retrieving additional index information, since this was not requested");
      LOGGER.log(Level.FINE, "Indexes information SQL statement was not provided");
      return;
    }

    LOGGER.log(Level.INFO, "Retrieving additional index information");

    final Query extIndexesInformationSql = informationSchemaViews.getQuery(EXT_INDEXES);
    try (final Connection connection = getRetrieverConnection().getConnection();
        final Statement statement = connection.createStatement();
        final MetadataResultSet results =
            new MetadataResultSet(
                extIndexesInformationSql, statement, getSchemaInclusionRule()); ) {

      while (results.next()) {
        final String catalogName = normalizeCatalogName(results.getString("INDEX_CATALOG"));
        final String schemaName = normalizeSchemaName(results.getString("INDEX_SCHEMA"));
        final String tableName = results.getString("TABLE_NAME");
        final String indexName = results.getString("INDEX_NAME");

        final Optional<MutableTable> tableOptional =
            lookupTable(catalogName, schemaName, tableName);
        if (!tableOptional.isPresent()) {
          LOGGER.log(
              Level.FINE,
              new StringFormat("Cannot find table <%s.%s.%s>", catalogName, schemaName, indexName));
          continue;
        }

        LOGGER.log(Level.FINER, new StringFormat("Retrieving index information <%s>", indexName));
        final MutableTable table = tableOptional.get();
        final Optional<MutableIndex> indexOptional = table.lookupIndex(indexName);
        if (!indexOptional.isPresent()) {
          LOGGER.log(
              Level.FINE,
              new StringFormat(
                  "Cannot find index <%s.%s.%s.%s>",
                  catalogName, schemaName, tableName, indexName));
          continue;
        }

        final MutableIndex index = indexOptional.get();

        final String definition = results.getString("INDEX_DEFINITION");
        final String remarks = results.getString("REMARKS");

        index.appendDefinition(definition);
        index.setRemarks(remarks);

        index.addAttributes(results.getAttributes());
      }
    } catch (final Exception e) {
      LOGGER.log(Level.WARNING, "Could not retrieve index information", e);
    }
  }

  /**
   * Retrieves table definitions from the database, in the INFORMATION_SCHEMA format.
   *
   * @throws SQLException On a SQL exception
   */
  void retrieveTableDefinitions() throws SQLException {
    final InformationSchemaViews informationSchemaViews =
        getRetrieverConnection().getInformationSchemaViews();

    if (!informationSchemaViews.hasQuery(EXT_TABLES)) {
      LOGGER.log(Level.INFO, "Not retrieving table definitions, since this was not requested");
      LOGGER.log(Level.FINE, "Table definitions SQL statement was not provided");
      return;
    }

    LOGGER.log(Level.INFO, "Retrieving table definitions");

    final Query tableDefinitionsInformationSql = informationSchemaViews.getQuery(EXT_TABLES);
    try (final Connection connection = getRetrieverConnection().getConnection();
        final Statement statement = connection.createStatement();
        final MetadataResultSet results =
            new MetadataResultSet(
                tableDefinitionsInformationSql, statement, getSchemaInclusionRule()); ) {

      while (results.next()) {
        final String catalogName = normalizeCatalogName(results.getString("TABLE_CATALOG"));
        final String schemaName = normalizeSchemaName(results.getString("TABLE_SCHEMA"));
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

        LOGGER.log(Level.FINER, new StringFormat("Retrieving table information <%s>", tableName));
        final String definition = results.getString("TABLE_DEFINITION");

        table.appendDefinition(definition);

        table.addAttributes(results.getAttributes());
      }
    } catch (final Exception e) {
      LOGGER.log(Level.WARNING, "Could not retrieve table definitions", e);
    }
  }

  /**
   * Retrieves a trigger information from the database, in the INFORMATION_SCHEMA format.
   *
   * @throws SQLException On a SQL exception
   */
  void retrieveTriggerInformation() throws SQLException {
    final InformationSchemaViews informationSchemaViews =
        getRetrieverConnection().getInformationSchemaViews();
    if (!informationSchemaViews.hasQuery(TRIGGERS)) {
      LOGGER.log(Level.INFO, "Not retrieving trigger definitions, since this was not requested");
      LOGGER.log(Level.FINE, "Trigger definition SQL statement was not provided");
      return;
    }

    LOGGER.log(Level.INFO, "Retrieving trigger definitions");

    final Query triggerInformationSql = informationSchemaViews.getQuery(TRIGGERS);
    try (final Connection connection = getRetrieverConnection().getConnection();
        final Statement statement = connection.createStatement();
        final MetadataResultSet results =
            new MetadataResultSet(triggerInformationSql, statement, getSchemaInclusionRule()); ) {

      while (results.next()) {
        final String catalogName = normalizeCatalogName(results.getString("TRIGGER_CATALOG"));
        final String schemaName = normalizeSchemaName(results.getString("TRIGGER_SCHEMA"));
        final String triggerName = results.getString("TRIGGER_NAME");
        LOGGER.log(Level.FINER, new StringFormat("Retrieving trigger <%s>", triggerName));

        // "EVENT_OBJECT_CATALOG", "EVENT_OBJECT_SCHEMA"
        final String tableName = results.getString("EVENT_OBJECT_TABLE");

        final Optional<MutableTable> tableOptional =
            lookupTable(catalogName, schemaName, tableName);
        if (!tableOptional.isPresent()) {
          LOGGER.log(
              Level.FINE,
              new StringFormat("Cannot find table <%s.%s.%s>", catalogName, schemaName, tableName));
          continue;
        }

        final MutableTable table = tableOptional.get();

        final EventManipulationType eventManipulationType =
            results.getEnum("EVENT_MANIPULATION", EventManipulationType.unknown);
        final int actionOrder = results.getInt("ACTION_ORDER", 0);
        final String actionCondition = results.getString("ACTION_CONDITION");
        final String actionStatement = results.getString("ACTION_STATEMENT");
        final ActionOrientationType actionOrientation =
            results.getEnum("ACTION_ORIENTATION", ActionOrientationType.unknown);
        String conditionTimingString = results.getString("ACTION_TIMING");
        if (conditionTimingString == null) {
          conditionTimingString = results.getString("CONDITION_TIMING");
        }
        final ConditionTimingType conditionTiming =
            ConditionTimingType.valueOfFromValue(conditionTimingString);

        final MutableTrigger trigger =
            table.lookupTrigger(triggerName).orElse(new MutableTrigger(table, triggerName));
        trigger.setEventManipulationType(eventManipulationType);
        trigger.setActionOrder(actionOrder);
        trigger.appendActionCondition(actionCondition);
        trigger.appendActionStatement(actionStatement);
        trigger.setActionOrientation(actionOrientation);
        trigger.setConditionTiming(conditionTiming);

        trigger.addAttributes(results.getAttributes());
        trigger.withQuoting(getRetrieverConnection().getIdentifiers());
        // Add trigger to the table
        table.addTrigger(trigger);
      }
    } catch (final Exception e) {
      LOGGER.log(Level.WARNING, "Could not retrieve triggers", e);
    }
  }

  /**
   * Retrieves view information from the database, in the INFORMATION_SCHEMA format.
   *
   * @throws SQLException On a SQL exception
   */
  void retrieveViewInformation() throws SQLException {
    final InformationSchemaViews informationSchemaViews =
        getRetrieverConnection().getInformationSchemaViews();

    if (!informationSchemaViews.hasQuery(VIEWS)) {
      LOGGER.log(
          Level.INFO, "Not retrieving additional view information, since this was not requested");
      LOGGER.log(Level.FINE, "Views SQL statement was not provided");
      return;
    }

    LOGGER.log(Level.INFO, "Retrieving additional view information");

    final Query viewInformationSql = informationSchemaViews.getQuery(VIEWS);
    try (final Connection connection = getRetrieverConnection().getConnection();
        final Statement statement = connection.createStatement();
        final MetadataResultSet results =
            new MetadataResultSet(viewInformationSql, statement, getSchemaInclusionRule()); ) {

      while (results.next()) {

        // Get the "VIEW_DEFINITION" value first as it the Oracle driver
        // don't handle it properly otherwise.
        // https://github.com/schemacrawler/SchemaCrawler/issues/835
        final String definition = results.getString("VIEW_DEFINITION");

        final String catalogName = normalizeCatalogName(results.getString("TABLE_CATALOG"));
        final String schemaName = normalizeSchemaName(results.getString("TABLE_SCHEMA"));
        final String viewName = results.getString("TABLE_NAME");

        final Optional<MutableTable> viewOptional = lookupTable(catalogName, schemaName, viewName);
        if (!viewOptional.isPresent()) {
          LOGGER.log(
              Level.FINE,
              new StringFormat("Cannot find table <%s.%s.%s>", catalogName, schemaName, viewName));
          continue;
        }

        final MutableView view = (MutableView) viewOptional.get();
        LOGGER.log(Level.FINER, new StringFormat("Retrieving view information <%s>", viewName));

        final CheckOptionType checkOption =
            results.getEnum("CHECK_OPTION", CheckOptionType.unknown);
        final boolean updatable = results.getBoolean("IS_UPDATABLE");

        view.appendDefinition(definition);
        view.setCheckOption(checkOption);
        view.setUpdatable(updatable);

        view.addAttributes(results.getAttributes());
      }
    } catch (final Exception e) {
      LOGGER.log(Level.WARNING, "Could not retrieve views", e);
    }
  }

  /**
   * Retrieves view table usage from the database, in the INFORMATION_SCHEMA format.
   *
   * @throws SQLException On a SQL exception
   */
  void retrieveViewTableUsage() throws SQLException {
    final InformationSchemaViews informationSchemaViews =
        getRetrieverConnection().getInformationSchemaViews();

    if (!informationSchemaViews.hasQuery(VIEW_TABLE_USAGE)) {
      LOGGER.log(
          Level.INFO, "Not retrieving additional view table usage, since this was not requested");
      LOGGER.log(Level.FINE, "View table usage SQL statement was not provided");
      return;
    }

    LOGGER.log(Level.INFO, "Retrieving view table usage");

    final Query viewTableUsageSql = informationSchemaViews.getQuery(VIEW_TABLE_USAGE);
    try (final Connection connection = getRetrieverConnection().getConnection();
        final Statement statement = connection.createStatement();
        final MetadataResultSet results =
            new MetadataResultSet(viewTableUsageSql, statement, getSchemaInclusionRule()); ) {

      while (results.next()) {
        final String catalogName = normalizeCatalogName(results.getString("VIEW_CATALOG"));
        final String schemaName = normalizeSchemaName(results.getString("VIEW_SCHEMA"));
        final String viewName = results.getString("VIEW_NAME");

        final Optional<MutableTable> viewOptional = lookupTable(catalogName, schemaName, viewName);
        if (!viewOptional.isPresent()) {
          LOGGER.log(
              Level.FINE,
              new StringFormat("Cannot find view <%s.%s.%s>", catalogName, schemaName, viewName));
          continue;
        }

        final MutableView view = (MutableView) viewOptional.get();
        LOGGER.log(Level.FINER, new StringFormat("Retrieving view information <%s>", viewName));

        final String tableCatalogName = normalizeCatalogName(results.getString("TABLE_CATALOG"));
        final String tableSchemaName = normalizeSchemaName(results.getString("TABLE_SCHEMA"));
        final String tableName = results.getString("TABLE_NAME");

        final Optional<MutableTable> tableOptional =
            lookupTable(tableCatalogName, tableSchemaName, tableName);
        if (!tableOptional.isPresent()) {
          LOGGER.log(
              Level.FINE,
              new StringFormat(
                  "Cannot find table <%s.%s.%s>", tableCatalogName, tableSchemaName, tableName));
          continue;
        }

        final MutableTable table = tableOptional.get();
        LOGGER.log(Level.FINER, new StringFormat("Retrieving table information <%s>", tableName));

        view.addTableUsage(table);
      }
    } catch (final Exception e) {
      LOGGER.log(Level.WARNING, "Could not retrieve table usage for views", e);
    }
  }
}
