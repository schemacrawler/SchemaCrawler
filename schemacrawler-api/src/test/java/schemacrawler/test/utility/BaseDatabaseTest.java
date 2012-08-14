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

package schemacrawler.test.utility;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;

import org.custommonkey.xmlunit.XMLUnit;
import org.junit.BeforeClass;

import schemacrawler.schema.Database;
import schemacrawler.schemacrawler.DatabaseConnectionOptions;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.utility.SchemaCrawlerUtility;
import sf.util.Utility;

public abstract class BaseDatabaseTest
{

  private final static DatabaseConnectionOptions connectionOptions;

  static
  {
    connectionOptions = createConnectionOptions();
    TestDatabaseUtility.initialize();
  }

  @BeforeClass
  public static void setApplicationLogLevel()
    throws Exception
  {
    Utility.setApplicationLogLevel(Level.OFF);
  }

  @BeforeClass
  public static void setEntityResolver()
    throws Exception
  {
    XMLUnit.setControlEntityResolver(new LocalEntityResolver());
  }

  private static DatabaseConnectionOptions createConnectionOptions()
  {
    try
    {
      final DatabaseConnectionOptions connectionOptions = new DatabaseConnectionOptions("org.hsqldb.jdbc.JDBCDriver",
                                                                                        TestDatabaseUtility.url);
      connectionOptions.setUser("sa");
      connectionOptions.setPassword("");

      return connectionOptions;
    }
    catch (final SchemaCrawlerException e)
    {
      e.printStackTrace();
      System.exit(1);
      return null;
    }
  }

  /**
   * Gets the connection.
   * 
   * @return Connection
   * @throws SchemaCrawlerException
   *         On an exception
   */
  protected Connection getConnection()
    throws SchemaCrawlerException
  {
    try
    {
      return connectionOptions.getConnection();
    }
    catch (final SQLException e)
    {
      throw new SchemaCrawlerException(e.getMessage(), e);
    }
  }

  protected Database getDatabase(final SchemaCrawlerOptions schemaCrawlerOptions)
    throws SchemaCrawlerException
  {
    final Database database = SchemaCrawlerUtility
      .getDatabase(getConnection(), schemaCrawlerOptions);
    return database;
  }

  protected DatabaseConnectionOptions getDatabaseConnectionOptions()
  {
    return connectionOptions;
  }

}
