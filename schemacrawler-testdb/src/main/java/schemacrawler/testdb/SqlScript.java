/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2018, Sualeh Fatehi <sualeh@hotmail.com>.
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


import static java.util.Objects.requireNonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class SqlScript
  implements Runnable
{

  private static final Logger LOGGER = Logger
    .getLogger(SqlScript.class.getName());

  private static final boolean debug = Boolean.valueOf(System
    .getProperty("schemacrawler.testdb.SqlScript.debug", "false"));

  private final ScriptResource scriptResource;
  private final Connection connection;
  private final Reader reader;

  public SqlScript(final String scriptName,
                   final Connection connection,
                   final Reader reader,
                   final String delimiter)
  {
    this.connection = requireNonNull(connection,
                                     "No database connection provided");
    this.reader = requireNonNull(reader, "No reader provided");
    scriptResource = new ScriptResource(scriptName, delimiter, debug);
  }

  public SqlScript(final ScriptResource scriptResource,
                   final Connection connection,
                   final Reader reader)
  {
    this.connection = requireNonNull(connection,
                                     "No database connection provided");
    this.reader = requireNonNull(reader, "No reader provided");
    this.scriptResource = requireNonNull(scriptResource,
                                         "No script resource provided");
  }

  public String getScriptName()
  {
    return scriptResource.getScriptName();
  }

  @Override
  public void run()
  {
    if (scriptResource.skip())
    {
      return;
    }

    try
    {
      // NOTE: Do not close reader or connection, since we did not open
      // them
      final BufferedReader lineReader = new BufferedReader(reader);
      final List<String> sqlList = readSql(lineReader);
      for (final String sql: sqlList)
      {
        try (final Statement statement = connection.createStatement();)
        {
          if (Pattern.matches("\\s+", sql))
          {
            continue;
          }
          if (debug)
          {
            LOGGER.log(Level.INFO, "\n" + sql);
          }
          statement.execute(sql);
          connection.commit();
        }
      }
    }
    catch (final Exception e)
    {
      throw new RuntimeException(e);
    }
  }

  private List<String> readSql(final BufferedReader lineReader)
    throws IOException
  {
    final List<String> list = new ArrayList<>();
    String line;
    StringBuilder sql = new StringBuilder();
    while ((line = lineReader.readLine()) != null)
    {
      final String trimmedLine = line.trim();
      final boolean isComment = trimmedLine.startsWith("--")
                                || trimmedLine.startsWith("//");
      final String delimiter = scriptResource.getDelimiter();
      if (!isComment && trimmedLine.endsWith(delimiter))
      {
        sql.append(line.substring(0, line.lastIndexOf(delimiter)));
        list.add(sql.toString());
        sql = new StringBuilder();
      }
      else
      {
        sql.append(line);
        sql.append("\n");
      }
    }
    // Check if the last line is not delimited
    if (sql.length() > 0)
    {
      list.add(sql.toString());
    }

    return list;
  }

}
