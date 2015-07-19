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

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.filter.InclusionRuleFilter;
import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.Schema;
import schemacrawler.schema.SchemaReference;
import schemacrawler.schema.Synonym;
import schemacrawler.schemacrawler.InclusionRule;
import schemacrawler.schemacrawler.InformationSchemaViews;
import sf.util.Utility;

/**
 * A retriever that uses database metadata to get the extended details
 * about the database synonyms.
 *
 * @author Matt Albrecht, Sualeh Fatehi
 */
final class SynonymRetriever
  extends AbstractRetriever
{

  private static final Logger LOGGER = Logger.getLogger(SynonymRetriever.class
    .getName());

  SynonymRetriever(final RetrieverConnection retrieverConnection,
                   final MutableCatalog catalog)
    throws SQLException
  {
    super(retrieverConnection, catalog);
  }

  /**
   * Retrieves the synonym definitions from the database.
   *
   * @param synonymInclusionRule
   *        Rule for including synonyms
   * @throws SQLException
   *         On a SQL exception
   */
  void retrieveSynonymInformation(final InclusionRule synonymInclusionRule)
    throws SQLException
  {
    final InclusionRuleFilter<Synonym> synonymFilter = new InclusionRuleFilter<>(synonymInclusionRule,
                                                                                 false);
    if (synonymFilter.isExcludeAll())
    {
      LOGGER.log(Level.INFO,
                 "Not retrieving synonyms, since this was not requested");
      return;
    }

    final InformationSchemaViews informationSchemaViews = getRetrieverConnection()
      .getInformationSchemaViews();
    if (!informationSchemaViews.hasSynonymsSql())
    {
      LOGGER.log(Level.FINE,
                 "Synonym definition SQL statement was not provided");
      return;
    }
    final String synonymsDefinitionSql = informationSchemaViews
      .getSynonymsSql();

    final Collection<Schema> schemas = catalog.getSchemaNames();

    final Connection connection = getDatabaseConnection();

    try (final Statement statement = connection.createStatement();
        MetadataResultSet results = new MetadataResultSet(executeSql(statement,
                                                                     synonymsDefinitionSql));)
    {
      while (results.next())
      {
        final String catalogName = quotedName(results
          .getString("SYNONYM_CATALOG"));
        final String schemaName = quotedName(results
          .getString("SYNONYM_SCHEMA"));
        final String synonymName = quotedName(results.getString("SYNONYM_NAME"));
        final String referencedObjectCatalogName = quotedName(results
          .getString("REFERENCED_OBJECT_CATALOG"));
        final String referencedObjectSchemaName = quotedName(results
          .getString("REFERENCED_OBJECT_SCHEMA"));
        final String referencedObjectName = quotedName(results
          .getString("REFERENCED_OBJECT_NAME"));

        if (Utility.isBlank(referencedObjectName))
        {
          LOGGER.log(Level.FINE, String
            .format("No reference for synonym, %s.%s.%s",
                    catalogName,
                    schemaName,
                    synonymName));
          continue;
        }

        final Schema schema = new SchemaReference(catalogName, schemaName);
        final Schema referencedSchema = new SchemaReference(referencedObjectCatalogName,
                                                            referencedObjectSchemaName);
        if (!schemas.contains(schema) && !schemas.contains(referencedSchema))
        {
          continue;
        }

        final Optional<MutableTable> referencedTable = lookupTable(referencedObjectCatalogName,
                                                                   referencedObjectSchemaName,
                                                                   referencedObjectName);
        final Optional<MutableRoutine> referencedRoutine = lookupRoutine(referencedObjectCatalogName,
                                                                         referencedObjectSchemaName,
                                                                         referencedObjectName,
                                                                         referencedObjectName);
        final DatabaseObject referencedObject;
        if (referencedTable.isPresent())
        {
          referencedObject = referencedTable.get();
        }
        else if (referencedRoutine.isPresent())
        {
          referencedObject = referencedRoutine.get();
        }
        else
        {
          referencedObject = new AbstractDatabaseObject(referencedSchema,
            referencedObjectName)
          {

            private static final long serialVersionUID = -2212843304418302122L;
          };
        }

        final MutableSynonym synonym = new MutableSynonym(schema, synonymName);
        synonym.setReferencedObject(referencedObject);

        synonym.addAttributes(results.getAttributes());

        if (synonymFilter.test(synonym))
        {
          catalog.addSynonym(synonym);
        }

      }
    }
    catch (final Exception e)
    {
      LOGGER.log(Level.WARNING, "Could not retrieve synonyms", e);
    }

  }

}
