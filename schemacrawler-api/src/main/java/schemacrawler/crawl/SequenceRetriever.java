/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2015, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */

package schemacrawler.crawl;


import static sf.util.DatabaseUtility.executeSql;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.filter.InclusionRuleFilter;
import schemacrawler.schema.Schema;
import schemacrawler.schema.SchemaReference;
import schemacrawler.schema.Sequence;
import schemacrawler.schemacrawler.InclusionRule;
import schemacrawler.schemacrawler.InformationSchemaViews;

/**
 * A retriever that uses database metadata to get the extended details
 * about the database sequences.
 *
 * @author Sualeh Fatehi
 */
final class SequenceRetriever
  extends AbstractRetriever
{

  private static final Logger LOGGER = Logger
    .getLogger(SequenceRetriever.class.getName());

  SequenceRetriever(final RetrieverConnection retrieverConnection,
                    final MutableCatalog catalog)
                      throws SQLException
  {
    super(retrieverConnection, catalog);
  }

  /**
   * Retrieves the sequence definitions from the database.
   *
   * @param sequenceInclusionRule
   *        Rule for including sequences
   * @throws SQLException
   *         On a SQL exception
   */
  void retrieveSequenceInformation(final InclusionRule sequenceInclusionRule)
    throws SQLException
  {
    final InclusionRuleFilter<Sequence> sequenceFilter = new InclusionRuleFilter<>(sequenceInclusionRule,
                                                                                   false);
    if (sequenceFilter.isExcludeAll())
    {
      LOGGER.log(Level.INFO,
                 "Not retrieving sequences, since this was not requested");
      return;
    }

    final InformationSchemaViews informationSchemaViews = getRetrieverConnection()
      .getInformationSchemaViews();
    if (!informationSchemaViews.hasSequencesSql())
    {
      LOGGER.log(Level.FINE,
                 "Sequence definition SQL statement was not provided");
      return;
    }
    final String sequencesDefinitionSql = informationSchemaViews
      .getSequencesSql();

    final Collection<Schema> schemas = catalog.getSchemaNames();

    final Connection connection = getDatabaseConnection();

    try (final Statement statement = connection.createStatement();
        MetadataResultSet results = new MetadataResultSet(executeSql(statement,
                                                                     sequencesDefinitionSql));)
    {
      while (results.next())
      {
        final String catalogName = quotedName(results
          .getString("SEQUENCE_CATALOG"));
        final String schemaName = quotedName(results
          .getString("SEQUENCE_SCHEMA"));
        final String sequenceName = quotedName(results
          .getString("SEQUENCE_NAME"));
        final BigInteger minimumValue = results.getBigInteger("MINIMUM_VALUE");
        final BigInteger maximumValue = results.getBigInteger("MAXIMUM_VALUE");
        final BigInteger increment = results.getBigInteger("INCREMENT");
        final long longIncrement = increment == null? 1L: increment.longValue();
        final boolean cycle = results.getBoolean("CYCLE_OPTION");

        final Schema schema = new SchemaReference(catalogName, schemaName);
        if (!schemas.contains(schema))
        {
          continue;
        }

        final MutableSequence sequence = new MutableSequence(schema,
                                                             sequenceName);
        sequence.setMaximumValue(maximumValue);
        sequence.setMinimumValue(minimumValue);
        sequence.setIncrement(longIncrement);
        sequence.setCycle(cycle);

        sequence.addAttributes(results.getAttributes());

        if (sequenceFilter.test(sequence))
        {
          catalog.addSequence(sequence);
        }

      }
    }
    catch (final Exception e)
    {
      LOGGER.log(Level.WARNING, "Could not retrieve sequences", e);
    }

  }

}
