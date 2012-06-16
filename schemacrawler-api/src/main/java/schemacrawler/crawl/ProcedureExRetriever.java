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

  ProcedureExRetriever(final RetrieverConnection retrieverConnection,
                       final MutableDatabase database)
    throws SQLException
  {
    super(retrieverConnection, database);
  }

  /**
   * Retrieves a procedure definitions from the database.
   * 
   * @throws SQLException
   *         On a SQL exception
   */
  void retrieveProcedureInformation()
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
    final String procedureDefinitionsSql = informationSchemaViews
      .getRoutinesSql();

    final Connection connection = getDatabaseConnection();
    final Statement statement = connection.createStatement();
    MetadataResultSet results = null;
    try
    {
      results = new MetadataResultSet(statement.executeQuery(procedureDefinitionsSql));
    }
    catch (final SQLException e)
    {
      LOGGER.log(Level.WARNING, "Could not retrieve procedure information", e);
      return;
    }

    try
    {
      while (results.next())
      {
        final String catalogName = quotedName(results
          .getString("ROUTINE_CATALOG"));
        final String schemaName = quotedName(results
          .getString("ROUTINE_SCHEMA"));
        final String procedureName = quotedName(results
          .getString("ROUTINE_NAME"));

        final MutableProcedure procedure = lookupProcedure(catalogName,
                                                           schemaName,
                                                           procedureName);
        if (procedure != null)
        {
          LOGGER.log(Level.FINER, "Retrieving procedure information: "
                                  + procedureName);
          final RoutineBodyType routineBodyType = results
            .getEnum("ROUTINE_BODY", RoutineBodyType.unknown);
          final String definition = results.getString("ROUTINE_DEFINITION");

          procedure.setRoutineBodyType(routineBodyType);
          procedure.appendDefinition(definition);

          procedure.addAttributes(results.getAttributes());
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
