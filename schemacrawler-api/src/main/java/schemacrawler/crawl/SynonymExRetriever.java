/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2012, Sualeh Fatehi.
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


import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.DatabaseObject;
import schemacrawler.schemacrawler.InclusionRule;
import schemacrawler.schemacrawler.InformationSchemaViews;
import sf.util.Utility;

/**
 * A retriever that uses database metadata to get the extended details
 * about the database synonyms.
 * 
 * @author Matt Albrecht, Sualeh Fatehi
 */
final class SynonymExRetriever
  extends AbstractRetriever
{

  private static final Logger LOGGER = Logger
    .getLogger(SynonymExRetriever.class.getName());

  SynonymExRetriever(final RetrieverConnection retrieverConnection,
                     final MutableDatabase database)
    throws SQLException
  {
    super(retrieverConnection, database);
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
    if (synonymInclusionRule == null
        || synonymInclusionRule.equals(InclusionRule.EXCLUDE_ALL))
    {
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

    final Connection connection = getDatabaseConnection();
    final Statement statement = connection.createStatement();
    MetadataResultSet results = null;
    try
    {
      results = new MetadataResultSet(statement.executeQuery(synonymsDefinitionSql));
    }
    catch (final SQLException e)
    {
      LOGGER.log(Level.WARNING, "Could not retrieve synonym information", e);
      return;
    }

    try
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

        final MutableSchema schema = lookupSchema(catalogName, schemaName);
        if (schema == null)
        {
          LOGGER.log(Level.FINE, String.format("Cannot find schema, %s.%s",
                                               catalogName,
                                               schemaName));
          continue;
        }

        final MutableTable referencedTable = lookupTable(referencedObjectCatalogName,
                                                         referencedObjectSchemaName,
                                                         referencedObjectName);
        final MutableProcedure referencedProcedure = lookupProcedure(referencedObjectCatalogName,
                                                                     referencedObjectSchemaName,
                                                                     referencedObjectName);
        final DatabaseObject referencedObject;
        if (referencedTable != null)
        {
          referencedObject = referencedTable;
        }
        else if (referencedProcedure != null)
        {
          referencedObject = referencedProcedure;
        }
        else
        {
          referencedObject = new AbstractDatabaseObject(new MutableSchema(new SchemaReference(referencedObjectCatalogName,
                                                                                              referencedObjectSchemaName)),
            referencedObjectName)
          {

            private static final long serialVersionUID = -2212843304418302122L;
          };
        }

        final MutableSynonym synonym = new MutableSynonym(schema, synonymName);
        synonym.setReferencedObject(referencedObject);

        if (synonymInclusionRule.include(synonym.getFullName()))
        {
          schema.addSynonym(synonym);
        }

      }
    }
    finally
    {
      if (results != null)
      {
        results.close();
      }
      statement.close();
    }

  }

}
