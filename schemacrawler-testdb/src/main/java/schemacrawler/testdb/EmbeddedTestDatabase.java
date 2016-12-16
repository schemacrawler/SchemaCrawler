/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
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


import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class EmbeddedTestDatabase
{

  /**
   * Starts up a embedded test database.
   *
   * @param args
   *        Command-line arguments
   * @throws Exception
   *         Exception
   */
  public static void main(final String[] args)
    throws Exception
  {
    final String server;
    if (args == null || args.length == 0)
    {
      server = "hsqldb";
    }
    else
    {
      server = args[0];
    }
    System.out.println(new EmbeddedTestDatabase(server));
  }

  private final String databaseServer;
  private final DataSource dataSource;

  public EmbeddedTestDatabase(final String server)
    throws SQLException
  {
    try (
        final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] {
                                                                                                         getContext(server) });)
    {
      dataSource = context.getBean("dataSource", DataSource.class);
    }

    try (final Connection connection = dataSource.getConnection();)
    {
      databaseServer = String
        .format("%s %s",
                connection.getMetaData().getDatabaseProductName(),
                connection.getMetaData().getDatabaseProductVersion());
    }
  }

  public final DataSource getDataSource()
  {
    return dataSource;
  }

  @Override
  public String toString()
  {
    return databaseServer;
  }

  private String getContext(final String server)
  {
    if (server == null || server.trim().isEmpty())
    {
      throw new IllegalArgumentException("No server provided");
    }
    return String.format("%s-context.xml", server);
  }

}
