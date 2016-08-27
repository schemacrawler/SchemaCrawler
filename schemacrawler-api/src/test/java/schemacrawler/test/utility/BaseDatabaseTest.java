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

package schemacrawler.test.utility;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;

import org.custommonkey.xmlunit.XMLUnit;
import org.junit.BeforeClass;

import schemacrawler.crawl.SchemaCrawler;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.DatabaseConnectionOptions;
import schemacrawler.schemacrawler.DatabaseSpecificOverrideOptions;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.testdb.TestDatabase;
import sf.util.Utility;

public abstract class BaseDatabaseTest
{

  private final static DatabaseConnectionOptions connectionOptions;

  static
  {
    connectionOptions = createConnectionOptions();
    TestDatabase.initialize();
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
      final DatabaseConnectionOptions connectionOptions = new DatabaseConnectionOptions(TestDatabase.CONNECTION_STRING);
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

  protected Catalog getCatalog(final DatabaseSpecificOverrideOptions databaseSpecificOverrideOptions,
                               final SchemaCrawlerOptions schemaCrawlerOptions)
    throws SchemaCrawlerException
  {
    final SchemaCrawler schemaCrawler = new SchemaCrawler(getConnection(),
                                                          databaseSpecificOverrideOptions);
    final Catalog catalog = schemaCrawler.crawl(schemaCrawlerOptions);

    return catalog;
  }

  protected Catalog getCatalog(final SchemaCrawlerOptions schemaCrawlerOptions)
    throws SchemaCrawlerException
  {
    return getCatalog(new DatabaseSpecificOverrideOptions(),
                      schemaCrawlerOptions);
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

  protected DatabaseConnectionOptions getDatabaseConnectionOptions()
  {
    return connectionOptions;
  }

}
