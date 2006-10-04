/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2006, Sualeh Fatehi.
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import sf.util.Utilities;

/**
 * ProcedureExRetriever uses database metadata to get the details about
 * the schema.
 * 
 * @author sfatehi
 */
final class ProcedureExRetriever
  extends AbstractRetriever
{

  private static final Logger LOGGER = Logger
    .getLogger(ProcedureExRetriever.class.getName());

  /**
   * Constructs a SchemaCrawler object, from a connection.
   * 
   * @param connection
   *          An open database connection.
   * @param driverClassName
   *          Class name of the JDBC driver
   * @param schemaPatternString
   *          JDBC schema pattern, or null
   * @throws SQLException
   *           On a SQL exception
   */
  ProcedureExRetriever(final RetrieverConnection retrieverConnection)
    throws SQLException
  {
    super(retrieverConnection);
  }

  /**
   * Retrieves a procedure definitions from the database.
   * 
   * @param procedures
   *          List of procedures.
   * @throws SQLException
   *           On a SQL exception
   */
  void retrieveProcedureDefinitions(final NamedObjectList procedures)
    throws SQLException
  {
    LOGGER.entering(getClass().getName(), "retrieveProcedureDefinitions",
                    new Object[] {});

    String procedureDefinitionsSql = getRetrieverConnection()
      .getProcedureDefinitionsSql();
    if (Utilities.isBlank(procedureDefinitionsSql))
    {
      LOGGER.log(Level.FINE,
                 "Procedure definition SQL statement was not provided");
      return;
    }

    Connection connection = getRetrieverConnection().getMetaData()
      .getConnection();
    Statement statement = connection.createStatement();
    final ResultSet results = statement.executeQuery(procedureDefinitionsSql);

    try
    {

      while (results.next())
      {
//        final String catalog = results.getString("PROCEDURE_CAT");
//        final String schema = results.getString("PROCEDURE_SCHEM");
        final String procedureName = results.getString("PROCEDURE_NAME");
        LOGGER.log(Level.FINEST, "Retrieving procedure definition: "
                                 + procedureName);
        final String definition = results.getString("PROCEDURE_DEFINITION");

        final MutableTable procedure = (MutableTable) procedures
          .lookup(procedureName);
        if (procedure == null)
        {
          LOGGER.log(Level.FINEST, "Procedure not found: " + procedureName);
          continue;
        }

        procedure.setDefinition(definition);
      }
    }
    finally
    {
      statement.close();
      results.close();
    }

  }

}
