/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.crawl;

import static schemacrawler.schemacrawler.InformationSchemaKey.SEQUENCES;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.filter.InclusionRuleFilter;
import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.schema.NamedObjectKey;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Sequence;
import schemacrawler.schemacrawler.InformationSchemaViews;
import schemacrawler.schemacrawler.Query;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaReference;

/**
 * A retriever that uses database metadata to get the extended details about the database sequences.
 */
final class SequenceRetriever extends AbstractRetriever {

  private static final Logger LOGGER = Logger.getLogger(SequenceRetriever.class.getName());

  SequenceRetriever(
      final RetrieverConnection retrieverConnection,
      final MutableCatalog catalog,
      final SchemaCrawlerOptions options)
      throws SQLException {
    super(retrieverConnection, catalog, options);
  }

  /**
   * Retrieves the sequence definitions from the database.
   *
   * @param sequenceInclusionRule Rule for including sequences
   * @throws SQLException On a SQL exception
   */
  void retrieveSequenceInformation(final InclusionRule sequenceInclusionRule) throws SQLException {
    final InclusionRuleFilter<Sequence> sequenceFilter =
        new InclusionRuleFilter<>(sequenceInclusionRule, false);
    if (sequenceFilter.isExcludeAll()) {
      LOGGER.log(Level.INFO, "Not retrieving sequences, since this was not requested");
      return;
    }

    LOGGER.log(Level.INFO, "Retrieving sequences");

    final InformationSchemaViews informationSchemaViews =
        getRetrieverConnection().getInformationSchemaViews();
    if (!informationSchemaViews.hasQuery(SEQUENCES)) {
      LOGGER.log(Level.FINE, "Sequence definition SQL statement was not provided");
      return;
    }

    final NamedObjectList<SchemaReference> schemas = getAllSchemas();

    final RetrievalCounts retrievalCounts = new RetrievalCounts("sequences");
    final Query sequencesDefinitionSql = informationSchemaViews.getQuery(SEQUENCES);
    try (final Connection connection = getRetrieverConnection().getConnection();
        final Statement statement = connection.createStatement();
        final MetadataResultSet results =
            new MetadataResultSet(sequencesDefinitionSql, statement, getLimitMap()); ) {
      while (results.next()) {
        retrievalCounts.count();
        final String catalogName = normalizeCatalogName(results.getString("SEQUENCE_CATALOG"));
        final String schemaName = normalizeSchemaName(results.getString("SEQUENCE_SCHEMA"));
        final String sequenceName = results.getString("SEQUENCE_NAME");
        final BigInteger startValue = results.getBigInteger("START_VALUE");
        final BigInteger minimumValue = results.getBigInteger("MINIMUM_VALUE");
        final BigInteger maximumValue = results.getBigInteger("MAXIMUM_VALUE");
        final BigInteger increment = results.getBigInteger("INCREMENT");
        final long longIncrement = increment == null ? 1L : increment.longValue();
        final boolean cycle = results.getBoolean("CYCLE_OPTION");

        final Optional<SchemaReference> optionalSchema =
            schemas.lookup(new NamedObjectKey(catalogName, schemaName));
        if (!optionalSchema.isPresent()) {
          continue;
        }
        final Schema schema = optionalSchema.get();

        final MutableSequence sequence = new MutableSequence(schema, sequenceName);
        sequence.withQuoting(getRetrieverConnection().getIdentifiers());

        if (sequenceFilter.test(sequence)) {
          sequence.setStartValue(startValue);
          sequence.setMaximumValue(maximumValue);
          sequence.setMinimumValue(minimumValue);
          sequence.setIncrement(longIncrement);
          sequence.setCycle(cycle);

          sequence.addAttributes(results.getAttributes());

          catalog.addSequence(sequence);
          retrievalCounts.countIncluded();
        }
      }
    } catch (final Exception e) {
      LOGGER.log(Level.WARNING, "Could not retrieve sequences", e);
    }
    retrievalCounts.log();
  }
}
