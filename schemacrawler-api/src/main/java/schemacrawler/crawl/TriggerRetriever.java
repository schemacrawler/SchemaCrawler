/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.crawl;

import static schemacrawler.schemacrawler.InformationSchemaKey.TRIGGERS;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.triggersRetrievalStrategy;
import static schemacrawler.utility.EnumUtility.enumValues;
import static us.fatehi.utility.Utility.isBlank;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.schema.ActionOrientationType;
import schemacrawler.schema.ConditionTimingType;
import schemacrawler.schema.EventManipulationType;
import schemacrawler.schema.Schema;
import schemacrawler.schemacrawler.InformationSchemaViews;
import schemacrawler.schemacrawler.Query;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import us.fatehi.utility.string.StringFormat;

/** A retriever uses database metadata to get the extended details about the database tables. */
final class TriggerRetriever extends AbstractRetriever {

  private static final Logger LOGGER = Logger.getLogger(TriggerRetriever.class.getName());

  TriggerRetriever(
      final RetrieverConnection retrieverConnection,
      final MutableCatalog catalog,
      final SchemaCrawlerOptions options)
      throws SQLException {
    super(retrieverConnection, catalog, options);
  }

  /**
   * Retrieves a trigger information from the database, in the INFORMATION_SCHEMA format.
   *
   * @throws SQLException On a SQL exception
   */
  void retrieveTriggerInformation() throws SQLException {
    if (catalog.getTables().isEmpty()) {
      LOGGER.log(Level.FINE, "No tables found");
      return;
    }

    final InformationSchemaViews informationSchemaViews =
        getRetrieverConnection().getInformationSchemaViews();
    if (!informationSchemaViews.hasQuery(TRIGGERS)) {
      LOGGER.log(Level.INFO, "Not retrieving trigger definitions, since this was not requested");
      LOGGER.log(Level.FINE, "Trigger definition SQL statement was not provided");
      return;
    }
    final Query triggerInformationSql = informationSchemaViews.getQuery(TRIGGERS);

    switch (getRetrieverConnection().get(triggersRetrievalStrategy)) {
      case data_dictionary_over_schemas:
        LOGGER.log(
            Level.INFO, "Retrieving triggers, using fast data dictionary retrieval over schemas");
        retrieveTriggerOverSchemas(triggerInformationSql);
        break;

      case data_dictionary_all:
      default:
        LOGGER.log(Level.INFO, "Retrieving triggers, using fast data dictionary retrieval");
        retrieveTriggerFromDataDictionary(triggerInformationSql);
        break;
    }
  }

  /**
   * Creates a trigger and adds it to the table.
   *
   * @throws SQLException On a SQL exception
   */
  private boolean createTrigger(final MetadataResultSet results) throws SQLException {
    final String catalogName = normalizeCatalogName(results.getString("TRIGGER_CATALOG"));
    final String schemaName = normalizeSchemaName(results.getString("TRIGGER_SCHEMA"));
    final String triggerName = results.getString("TRIGGER_NAME");
    LOGGER.log(Level.FINER, new StringFormat("Retrieving trigger <%s>", triggerName));

    // "EVENT_OBJECT_CATALOG", "EVENT_OBJECT_SCHEMA"
    final String tableName = results.getString("EVENT_OBJECT_TABLE");

    final Optional<MutableTable> tableOptional = lookupTable(catalogName, schemaName, tableName);
    if (!tableOptional.isPresent()) {
      LOGGER.log(
          Level.FINE,
          new StringFormat("Cannot find table <%s.%s.%s>", catalogName, schemaName, tableName));
      return false;
    }

    final MutableTable table = tableOptional.get();

    final Set<EventManipulationType> eventManipulationTypes = getEventManipulationType(results);
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

    final MutableTrigger trigger;
    final Optional<MutableTrigger> optionalTrigger = table.lookupTrigger(triggerName);
    if (optionalTrigger.isPresent()) {
      trigger = optionalTrigger.get();
    } else {
      trigger = new MutableTrigger(table, triggerName);
      // Set fields only for the first time the trigger is seen
      trigger.setActionOrder(actionOrder);
      trigger.appendActionCondition(actionCondition);
      trigger.appendActionStatement(actionStatement);
      trigger.setActionOrientation(actionOrientation);
      trigger.setConditionTiming(conditionTiming);
    }
    trigger.withQuoting(getRetrieverConnection().getIdentifiers());

    trigger.setEventManipulationTypes(eventManipulationTypes);
    trigger.addAttributes(results.getAttributes());

    // Add trigger to the table
    table.addTrigger(trigger);

    return true;
  }

  private Set<EventManipulationType> getEventManipulationType(final MetadataResultSet results) {
    if (results == null) {
      return null;
    }
    String eventManipulationString = results.getString("EVENT_MANIPULATION");
    if (isBlank(eventManipulationString)) {
      return null;
    }
    eventManipulationString = eventManipulationString.toLowerCase(Locale.ENGLISH);

    // Find what to split multiple values by
    final String splitBy;
    final String oracleSeparator = " or ";
    final String plainSeparator = ",";
    if (eventManipulationString.contains(oracleSeparator)) {
      // Oracle returns values separated by "OR"
      splitBy = oracleSeparator;
    } else if (eventManipulationString.contains(plainSeparator)) {
      splitBy = plainSeparator;
    } else {
      splitBy = "";
    }

    return enumValues(eventManipulationString, splitBy, EventManipulationType.unknown);
  }

  private void retrieveTriggerFromDataDictionary(final Query triggerInformationSql)
      throws SQLException {
    final String name = "trigger definitions";
    final RetrievalCounts retrievalCounts = new RetrievalCounts(name);
    try (final Connection connection = getRetrieverConnection().getConnection(name);
        final Statement statement = connection.createStatement();
        final MetadataResultSet results =
            new MetadataResultSet(triggerInformationSql, statement, getLimitMap()); ) {
      while (results.next()) {
        retrievalCounts.count();
        final boolean added = createTrigger(results);
        retrievalCounts.countIfIncluded(added);
      }
    } catch (final Exception e) {
      LOGGER.log(Level.WARNING, "Could not retrieve triggers", e);
    }
    retrievalCounts.log();
  }

  private void retrieveTriggerOverSchemas(final Query triggerInformationSql) throws SQLException {
    final String name = "trigger definitions";
    final RetrievalCounts retrievalCounts = new RetrievalCounts(name);
    for (final Schema schema : getAllSchemas()) {
      if (catalog.getTables(schema).isEmpty()) {
        continue;
      }
      try (final Connection connection = getRetrieverConnection().getConnection(name);
          final SchemaSetter schemaSetter = new SchemaSetter(connection, schema);
          final Statement statement = connection.createStatement();
          final MetadataResultSet results =
              new MetadataResultSet(triggerInformationSql, statement, getLimitMap()); ) {
        while (results.next()) {
          retrievalCounts.count(schema.key());
          final boolean added = createTrigger(results);
          retrievalCounts.countIfIncluded(schema.key(), added);
        }
      } catch (final Exception e) {
        LOGGER.log(
            Level.WARNING,
            e,
            new StringFormat("Could not retrieve triggers for schema <%s>", schema));
      }
      retrievalCounts.log(schema.key());
    }
  }
}
