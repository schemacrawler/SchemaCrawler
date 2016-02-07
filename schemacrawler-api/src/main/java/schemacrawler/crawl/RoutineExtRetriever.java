/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2016, Sualeh Fatehi.
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
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.RoutineBodyType;
import schemacrawler.schemacrawler.InformationSchemaViews;
import sf.util.FormattedStringSupplier;

/**
 * A retriever that uses database metadata to get the extended details
 * about the database routines.
 *
 * @author Sualeh Fatehi
 */
final class RoutineExtRetriever
  extends AbstractRetriever
{

  private static final Logger LOGGER = Logger
    .getLogger(RoutineExtRetriever.class.getName());

  RoutineExtRetriever(final RetrieverConnection retrieverConnection,
                      final MutableCatalog catalog)
                        throws SQLException
  {
    super(retrieverConnection, catalog);
  }

  /**
   * Retrieves a routine definitions from the database.
   *
   * @throws SQLException
   *         On a SQL exception
   */
  void retrieveRoutineInformation()
    throws SQLException
  {
    final InformationSchemaViews informationSchemaViews = getRetrieverConnection()
      .getInformationSchemaViews();
    if (!informationSchemaViews.hasRoutinesSql())
    {
      LOGGER
        .log(Level.INFO,
             "Not retrieving routine definitions, since this was not requested");
      LOGGER.log(Level.FINE,
                 "Routine definition SQL statement was not provided");
      return;
    }

    LOGGER.log(Level.INFO, "Retrieving procedure definitions");

    final String routineDefinitionsSql = informationSchemaViews
      .getRoutinesSql();

    final Connection connection = getDatabaseConnection();
    try (final Statement statement = connection.createStatement();
        final MetadataResultSet results = new MetadataResultSet(executeSql(statement,
                                                                           routineDefinitionsSql)))
    {
      while (results.next())
      {
        final String catalogName = quotedName(results
          .getString("ROUTINE_CATALOG"));
        final String schemaName = quotedName(results
          .getString("ROUTINE_SCHEMA"));
        final String routineName = quotedName(results
          .getString("ROUTINE_NAME"));
        final String specificName = quotedName(results
          .getString("SPECIFIC_NAME"));

        final Optional<MutableRoutine> routineOptional = lookupRoutine(catalogName,
                                                                       schemaName,
                                                                       routineName,
                                                                       specificName);
        if (routineOptional.isPresent())
        {
          final MutableRoutine routine = routineOptional.get();
          LOGGER.log(Level.FINER,
                     new FormattedStringSupplier("Retrieving routine information, %s",
                                                 routineName));
          final RoutineBodyType routineBodyType = results
            .getEnum("ROUTINE_BODY", RoutineBodyType.unknown);
          final String definition = results.getString("ROUTINE_DEFINITION");

          routine.setRoutineBodyType(routineBodyType);
          routine.appendDefinition(definition);

          routine.addAttributes(results.getAttributes());
        }
      }
    }
    catch (final Exception e)
    {
      LOGGER.log(Level.WARNING, "Could not retrieve routines", e);
    }
  }

}
