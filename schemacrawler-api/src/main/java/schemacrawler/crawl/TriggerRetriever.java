/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
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

import static schemacrawler.schemacrawler.InformationSchemaKey.TRIGGERS;
import static schemacrawler.utility.EnumUtility.enumValues;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import static us.fatehi.utility.Utility.isBlank;
import schemacrawler.schema.ActionOrientationType;
import schemacrawler.schema.ConditionTimingType;
import schemacrawler.schema.EventManipulationType;
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
            new MetadataResultSet(triggerInformationSql, statement, getLimitMap()); ) {
      int count = 0;
      int addedCount = 0;
      while (results.next()) {
        count = count + 1;
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
        addedCount = addedCount + 1;
      }
      LOGGER.log(Level.INFO, new StringFormat("Processed %d/%d triggers", addedCount, count));
    } catch (final Exception e) {
      LOGGER.log(Level.WARNING, "Could not retrieve triggers", e);
    }
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
}
