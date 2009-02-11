/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2009, Sualeh Fatehi.
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
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.RoutineBodyType;
import schemacrawler.schemacrawler.InformationSchemaViews;

/**
 * A retriever that uses database metadata to get the extended details
 * about the database procedures.
 * 
 * @author Sualeh Fatehi
 */
final class ProcedureExRetriever
  extends AbstractRetriever
{

  private static final Logger LOGGER = Logger
    .getLogger(ProcedureExRetriever.class.getName());

  ProcedureExRetriever(final RetrieverConnection retrieverConnection)
    throws SQLException
  {
    super(retrieverConnection);
  }

  /**
   * Retrieves a procedure definitions from the database.
   * 
   * @param procedures
   *        List of procedures.
   * @throws SQLException
   *         On a SQL exception
   */
  void retrieveProcedureInformation(final NamedObjectList<MutableProcedure> procedures)
    throws SQLException
  {
    final InformationSchemaViews informationSchemaViews = getRetrieverConnection()
      .getInformationSchemaViews();
    if (!informationSchemaViews.hasRoutinesSql())
    {
      LOGGER.log(Level.FINE,
                 "Procedure definition SQL statement was not provided");
      return;
    }
    final String procedureDefinitionsSql = informationSchemaViews.getRoutines()
      .getQuery();

    final Connection connection = getDatabaseConnection();
    final Statement statement = connection.createStatement();
    MetadataResultSet results = null;
    try
    {
      results = new MetadataResultSet(statement
        .executeQuery(procedureDefinitionsSql));
    }
    catch (final SQLException e)
    {
      LOGGER.log(Level.WARNING, "Could not retrieve procedure information", e);
      return;
    }

    final String catalogName = getRetrieverConnection().getCatalogName();
    try
    {
      while (results.next())
      {
        // final String catalogName =
        // results.getString("ROUTINE_CATALOG");
        final String schemaName = results.getString("ROUTINE_SCHEMA");
        final String procedureName = results.getString("ROUTINE_NAME");

        final MutableProcedure procedure = procedures.lookup(catalogName,
                                                             schemaName,
                                                             procedureName);
        if (!belongsToSchema(procedure, catalogName, schemaName))
        {
          LOGGER.log(Level.FINEST, "Procedure not found: " + procedureName);
          continue;
        }

        LOGGER.log(Level.FINEST, "Retrieving procedure information for "
                                 + procedureName);
        final RoutineBodyType routineBodyType = RoutineBodyType.valueOf(results
          .getString("ROUTINE_BODY").toLowerCase(Locale.ENGLISH));
        String definition = results.getString("ROUTINE_DEFINITION");
        final String text = procedure.getDefinition();

        if (!(text == null || text.trim().length() == 0))
        {
          definition = procedure.getDefinition() + definition;
        }

        procedure.setRoutineBodyType(routineBodyType);
        procedure.setDefinition(definition);

        procedure.addAttributes(results.getAttributes());
      }
    }
    finally
    {
      statement.close();
      results.close();
    }

  }

}
