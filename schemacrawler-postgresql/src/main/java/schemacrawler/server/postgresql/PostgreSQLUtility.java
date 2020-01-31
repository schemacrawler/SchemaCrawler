/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.server.postgresql;


import static java.util.Objects.requireNonNull;
import static sf.util.DatabaseUtility.executeSql;
import static sf.util.DatabaseUtility.readResultsVector;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import schemacrawler.schema.ColumnDataType;
import sf.util.SchemaCrawlerLogger;
import sf.util.StringFormat;

public class PostgreSQLUtility
{

  private static final SchemaCrawlerLogger LOGGER =
    SchemaCrawlerLogger.getLogger(PostgreSQLUtility.class.getName());

  public static List<String> getEnumValues(final ColumnDataType columnDataType,
                                           final Connection connection)
  {
    requireNonNull(columnDataType, "No column provided");
    final String sql = String.format(
      "SELECT e.enumlabel FROM pg_enum e JOIN pg_type t ON e.enumtypid = t.oid WHERE t.typname = '%s'",
      columnDataType.getName());
    try (final Statement statement = connection.createStatement();)
    {
      final ResultSet resultSet = executeSql(statement, sql);
      final List<String> enumValues = readResultsVector(resultSet);
      return enumValues;
    }
    catch (final SQLException e)
    {
      LOGGER.log(Level.WARNING,
                 new StringFormat("Error executing SQL <%s>", sql),
                 e);
    }
    return new ArrayList<>();
  }

  private PostgreSQLUtility()
  {
    // Prevent instantiation
  }

}
