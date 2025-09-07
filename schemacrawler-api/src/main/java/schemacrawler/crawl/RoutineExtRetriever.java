/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.crawl;

import static schemacrawler.schemacrawler.InformationSchemaKey.ROUTINES;
import static schemacrawler.schemacrawler.InformationSchemaKey.ROUTINE_REFERENCES;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.routineReferencesRetrievalStrategy;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.routinesRetrievalStrategy;
import static us.fatehi.utility.Utility.isBlank;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.RoutineBodyType;
import schemacrawler.schema.Schema;
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
    final Query routineDefinitionsSql = informationSchemaViews.getQuery(ROUTINES);

    switch (getRetrieverConnection().get(routinesRetrievalStrategy)) {
      case metadata_over_schemas:
        LOGGER.log(Level.INFO, "Retrieving additional view information, over schemas");
        retrieveRoutineInformationOverSchemas(routineDefinitionsSql);
        break;

      case data_dictionary_all:
      default:
        LOGGER.log(
            Level.INFO,
            "Retrieving additional view information, using fast data dictionary retrieval");
        retrieveRoutineInformationFromDataDictionary(routineDefinitionsSql);
        break;
    }
  }

  /**
   * Retrieves objects that a routine references from the database.
   *
   * @throws SQLException On a SQL exception
   */
  void retrieveRoutineReferences() throws SQLException {
    final InformationSchemaViews informationSchemaViews =
        getRetrieverConnection().getInformationSchemaViews();
    if (!informationSchemaViews.hasQuery(ROUTINE_REFERENCES)) {
      LOGGER.log(Level.INFO, "Not retrieving routine references, since this was not requested");
      LOGGER.log(Level.FINE, "Routine references SQL statement was not provided");
      return;
    }
    final Query routineReferencesSql = informationSchemaViews.getQuery(ROUTINE_REFERENCES);

    switch (getRetrieverConnection().get(routineReferencesRetrievalStrategy)) {
      case metadata_over_schemas:
        LOGGER.log(Level.INFO, "Retrieving additional view information, over schemas");
        retrieveRoutineReferencesOverSchemas(routineReferencesSql);
        break;

      case data_dictionary_all:
      default:
        LOGGER.log(
            Level.INFO,
            "Retrieving additional view information, using fast data dictionary retrieval");
        retrieveRoutineReferencesFromDataDictionary(routineReferencesSql);
        break;
    }
  }

  /**
   * Retrieves a routine definitions from the database.
   *
   * @throws SQLException On a SQL exception
   */
  private boolean addRoutineInformation(final MetadataResultSet results) throws SQLException {
    final String catalogName = normalizeCatalogName(results.getString("ROUTINE_CATALOG"));
    final String schemaName = normalizeSchemaName(results.getString("ROUTINE_SCHEMA"));
    final String routineName = results.getString("ROUTINE_NAME");
    final String specificName = results.getString("SPECIFIC_NAME");

    final Optional<MutableRoutine> routineOptional =
        lookupRoutine(catalogName, schemaName, routineName, specificName);
    if (routineOptional.isPresent()) {
      final MutableRoutine routine = routineOptional.get();
      LOGGER.log(
          Level.FINER, new StringFormat("Retrieving routine information for <%s>", routineName));
      final RoutineBodyType routineBodyType =
          results.getEnum("ROUTINE_BODY", RoutineBodyType.unknown);
      final String definition = results.getString("ROUTINE_DEFINITION");

      routine.setRoutineBodyType(routineBodyType);
      routine.appendDefinition(definition);

      routine.addAttributes(results.getAttributes());
    }
    return routineOptional.isPresent();
  }

  private boolean addRoutineReferences(final MetadataResultSet results) throws SQLException {
    final String catalogName = normalizeCatalogName(results.getString("ROUTINE_CATALOG"));
    final String schemaName = normalizeSchemaName(results.getString("ROUTINE_SCHEMA"));
    final String routineName = results.getString("ROUTINE_NAME");
    final String specificName = results.getString("SPECIFIC_NAME");
    final String referencedObjectCatalogName = results.getString("REFERENCED_OBJECT_CATALOG");
    final String referencedObjectSchemaName = results.getString("REFERENCED_OBJECT_SCHEMA");
    final String referencedObjectName = results.getString("REFERENCED_OBJECT_NAME");
    // final String referencedObjectType = results.getString("REFERENCED_OBJECT_TYPE");

    final Optional<MutableRoutine> routineOptional =
        lookupRoutine(catalogName, schemaName, routineName, specificName);
    if (routineOptional.isPresent()) {
      final MutableRoutine routine = routineOptional.get();
      LOGGER.log(
          Level.FINER, new StringFormat("Retrieving routine references for <%s>", routineName));

      final Optional<DatabaseObject> referencedObjectOptional =
          lookupReferencedObject(
              referencedObjectCatalogName,
              referencedObjectSchemaName,
              referencedObjectName,
              specificName);
      if (referencedObjectOptional.isPresent()) {
        routine.addReferencedObject(referencedObjectOptional.get());
      }
      return referencedObjectOptional.isPresent();
    }
    return false;
  }

  private Optional<DatabaseObject> lookupReferencedObject(
      final String catalogName,
      final String schemaName,
      final String objectName,
      final String specificName) {
    final Optional<MutableTable> tableOptional = lookupTable(catalogName, schemaName, objectName);
    if (tableOptional.isPresent()) {
      return Optional.of((DatabaseObject) tableOptional.get());
    }
    lookupRoutine(catalogName, schemaName, objectName, specificName);
    if (tableOptional.isPresent()) {
      return Optional.of((DatabaseObject) tableOptional.get());
    }
    return Optional.empty();
  }

  private void retrieveRoutineInformationFromDataDictionary(final Query routineDefinitionsSql)
      throws SQLException {
    final String name = "routine definitions";
    final RetrievalCounts retrievalCounts = new RetrievalCounts(name);
    try (final Connection connection = getRetrieverConnection().getConnection(name);
        final Statement statement = connection.createStatement();
        final MetadataResultSet results =
            new MetadataResultSet(routineDefinitionsSql, statement, getLimitMap()); ) {
      while (results.next()) {
        retrievalCounts.count();
        boolean addedRoutineInformation = addRoutineInformation(results);
        retrievalCounts.countIfIncluded(addedRoutineInformation);
      }
    } catch (final Exception e) {
      LOGGER.log(Level.WARNING, "Could not retrieve routine definitions", e);
    }
    retrievalCounts.log();
  }

  private void retrieveRoutineInformationOverSchemas(final Query routineDefinitionsSql)
      throws SQLException {
    final Collection<Schema> schemas = catalog.getSchemas();
    final String name = "routine definitions";
    final RetrievalCounts retrievalCounts = new RetrievalCounts(name);
    for (final Schema schema : schemas) {
      if (catalog.getTables(schema).isEmpty()) {
        continue;
      }
      try (final Connection connection = getRetrieverConnection().getConnection(name)) {
        final String currentCatalogName = connection.getCatalog();
        final String catalogName = schema.getCatalogName();
        if (!isBlank(catalogName)) {
          connection.setCatalog(catalogName);
        }
        try (final Statement statement = connection.createStatement();
            final MetadataResultSet results =
                new MetadataResultSet(routineDefinitionsSql, statement, getLimitMap()); ) {
          while (results.next()) {
            retrievalCounts.count(schema.key());
            boolean addedRoutineInformation = addRoutineInformation(results);
            retrievalCounts.countIfIncluded(schema.key(), addedRoutineInformation);
          }
        } catch (final Exception e) {
          LOGGER.log(
              Level.WARNING,
              String.format("Could not retrieve routine definitions for schema <%s>", schema),
              e);
        }
        retrievalCounts.log(schema.key());
        connection.setCatalog(currentCatalogName);
      }
    }
  }

  private void retrieveRoutineReferencesFromDataDictionary(final Query routineReferencesSql)
      throws SQLException {
    final String name = "routine references";
    final RetrievalCounts retrievalCounts = new RetrievalCounts(name);
    try (final Connection connection = getRetrieverConnection().getConnection(name);
        final Statement statement = connection.createStatement();
        final MetadataResultSet results =
            new MetadataResultSet(routineReferencesSql, statement, getLimitMap()); ) {
      while (results.next()) {
        retrievalCounts.count();
        boolean addedRoutineReferences = addRoutineReferences(results);
        retrievalCounts.countIfIncluded(addedRoutineReferences);
      }
    } catch (final Exception e) {
      LOGGER.log(Level.WARNING, "Could not retrieve routine definitions", e);
    }
    retrievalCounts.log();
  }

  private void retrieveRoutineReferencesOverSchemas(final Query routineReferencesSql)
      throws SQLException {
    final Collection<Schema> schemas = catalog.getSchemas();
    final String name = "routine references";
    final RetrievalCounts retrievalCounts = new RetrievalCounts(name);
    for (final Schema schema : schemas) {
      if (catalog.getTables(schema).isEmpty()) {
        continue;
      }
      try (final Connection connection = getRetrieverConnection().getConnection(name)) {
        final String currentCatalogName = connection.getCatalog();
        final String catalogName = schema.getCatalogName();
        if (!isBlank(catalogName)) {
          connection.setCatalog(catalogName);
        }
        try (final Statement statement = connection.createStatement();
            final MetadataResultSet results =
                new MetadataResultSet(routineReferencesSql, statement, getLimitMap()); ) {
          while (results.next()) {
            retrievalCounts.count(schema.key());
            boolean addedRoutineReferences = addRoutineReferences(results);
            retrievalCounts.countIfIncluded(schema.key(), addedRoutineReferences);
          }
        } catch (final Exception e) {
          LOGGER.log(Level.WARNING, "Could not retrieve routine definitions", e);
        }
        retrievalCounts.log(schema.key());
        connection.setCatalog(currentCatalogName);
      }
    }
  }
}
