/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
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


import java.math.BigInteger;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import schemacrawler.filter.InclusionRuleFilter;
import schemacrawler.schema.Schema;
import schemacrawler.schema.SchemaReference;
import schemacrawler.schema.Sequence;
import schemacrawler.schemacrawler.InclusionRule;
import schemacrawler.schemacrawler.InformationSchemaViews;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.utility.Query;
import sf.util.SchemaCrawlerLogger;

/**
 * A retriever that uses database metadata to get the extended details
 * about the database sequences.
 *
 * @author Sualeh Fatehi
 */
final class SequenceRetriever
  extends AbstractRetriever
{

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger
    .getLogger(SequenceRetriever.class.getName());

  SequenceRetriever(final RetrieverConnection retrieverConnection,
                    final MutableCatalog catalog,
                    final SchemaCrawlerOptions options)
    throws SQLException
  {
    super(retrieverConnection, catalog, options);
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

    LOGGER.log(Level.INFO, "Retrieving sequences");

    final InformationSchemaViews informationSchemaViews = getRetrieverConnection()
      .getInformationSchemaViews();
    if (!informationSchemaViews.hasSequencesSql())
    {
      LOGGER.log(Level.FINE,
                 "Sequence definition SQL statement was not provided");
      return;
    }

    final NamedObjectList<SchemaReference> schemas = getAllSchemas();

    final Query sequencesDefinitionSql = informationSchemaViews
      .getSequencesSql();
    final Connection connection = getDatabaseConnection();

    try (final Statement statement = connection.createStatement();
        final MetadataResultSet results = new MetadataResultSet(sequencesDefinitionSql,
                                                                statement,
                                                                getSchemaInclusionRule());)
    {
      while (results.next())
      {
        final String catalogName = nameQuotedName(results
          .getString("SEQUENCE_CATALOG"));
        final String schemaName = nameQuotedName(results
          .getString("SEQUENCE_SCHEMA"));
        final String sequenceName = nameQuotedName(results
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
