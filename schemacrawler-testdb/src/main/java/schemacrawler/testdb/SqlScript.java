/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.testdb;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class SqlScript
{

  private static final String delimiter = ";";

  private final Connection connection;

  public SqlScript(final Connection connection)
  {
    this.connection = connection;
  }

  /**
   * Executes an SQL script.
   *
   * @param reader
   *        Source of the SQL script
   */
  public void run(final Reader reader)
    throws IOException, SQLException
  {
    try (final BufferedReader lineReader = new BufferedReader(reader);)
    {
      String line;
      StringBuilder sql = new StringBuilder();
      while ((line = lineReader.readLine()) != null)
      {
        final String trimmedLine = line.trim();
        if (!(trimmedLine.startsWith("--") || trimmedLine.startsWith("//"))
            && trimmedLine.endsWith(delimiter))
        {
          sql.append(line.substring(0, line.lastIndexOf(delimiter)));

          try (final Statement statement = connection.createStatement();)
          {
            statement.execute(sql.toString());
            connection.commit();
          }

          sql = new StringBuilder();
        }
        else
        {
          sql.append(line);
          sql.append("\n");
        }
      }
    }
  }

}
