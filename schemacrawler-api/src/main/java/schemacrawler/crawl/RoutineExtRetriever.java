/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.crawl;

import static schemacrawler.schemacrawler.InformationSchemaKey.ROUTINES;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.schema.RoutineBodyType;
import schemacrawler.schemacrawler.InformationSchemaViews;
import schemacrawler.schemacrawler.Query;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import us.fatehi.utility.string.StringFormat;

/**
 * A retriever that uses database metadata to get the extended details about the database routines.
 */
final class RoutineExtRetriever extends AbstractRetriever {

  private static final Logger LOGGER = Logger.getLogger(RoutineExtRetriever.class.getName());

  RoutineExtRetriever(
      final RetrieverConnection retrieverConnection,
      final MutableCatalog catalog,
      final SchemaCrawlerOptions options)
      throws SQLException {
    super(retrieverConnection, catalog, options);
  }

  /**
   * Retrieves a routine definitions from the database.
   *
   * @throws SQLException On a SQL exception
   */
  void retrieveRoutineInformation() throws SQLException {
    final InformationSchemaViews informationSchemaViews =
        getRetrieverConnection().getInformationSchemaViews();
    if (!informationSchemaViews.hasQuery(ROUTINES)) {
      LOGGER.log(Level.INFO, "Not retrieving routine definitions, since this was not requested");
      LOGGER.log(Level.FINE, "Routine definition SQL statement was not provided");
      return;
    }

    LOGGER.log(Level.INFO, "Retrieving routine definitions");

    final String name = "routine definitions";
    final RetrievalCounts retrievalCounts = new RetrievalCounts(name);
    final Query routineDefinitionsSql = informationSchemaViews.getQuery(ROUTINES);
    try (final Connection connection = getRetrieverConnection().getConnection(name);
        final Statement statement = connection.createStatement();
        final MetadataResultSet results =
            new MetadataResultSet(routineDefinitionsSql, statement, getLimitMap()); ) {
      while (results.next()) {
        retrievalCounts.count();
        final String catalogName = normalizeCatalogName(results.getString("ROUTINE_CATALOG"));
        final String schemaName = normalizeSchemaName(results.getString("ROUTINE_SCHEMA"));
        final String routineName = results.getString("ROUTINE_NAME");
        final String specificName = results.getString("SPECIFIC_NAME");

        final Optional<MutableRoutine> routineOptional =
            lookupRoutine(catalogName, schemaName, routineName, specificName);
        if (routineOptional.isPresent()) {
          final MutableRoutine routine = routineOptional.get();
          LOGGER.log(
              Level.FINER,
              new StringFormat("Retrieving routine information for <%s>", routineName));
          final RoutineBodyType routineBodyType =
              results.getEnum("ROUTINE_BODY", RoutineBodyType.unknown);
          final String definition = results.getString("ROUTINE_DEFINITION");

          routine.setRoutineBodyType(routineBodyType);
          routine.appendDefinition(definition);

          routine.addAttributes(results.getAttributes());
          retrievalCounts.countIncluded();
        }
      }
    } catch (final Exception e) {
      LOGGER.log(Level.WARNING, "Could not retrieve routine definitions", e);
    }
    retrievalCounts.log();
  }
}
