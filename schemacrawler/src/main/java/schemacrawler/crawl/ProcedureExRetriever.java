/*
 * SchemaCrawler
 * Copyright (c) 2000-2008, Sualeh Fatehi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

    final String catalog = getRetrieverConnection().getCatalogName();
    try
    {
      while (results.next())
      {
        // final String catalog = results.getString("ROUTINE_CATALOG");
        final String schema = results.getString("ROUTINE_SCHEMA");
        final String procedureName = results.getString("ROUTINE_NAME");

        final MutableProcedure procedure = procedures.lookup(catalog,
                                                             schema,
                                                             procedureName);
        if (!belongsToSchema(procedure, catalog, schema))
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
