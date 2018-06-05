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

package schemacrawler.test.utility;


import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;
import static sf.util.Utility.applyApplicationLogLevel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

import org.custommonkey.xmlunit.XMLUnit;
import org.junit.BeforeClass;

import schemacrawler.crawl.SchemaCrawler;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.ConnectionOptions;
import schemacrawler.schemacrawler.DatabaseConnectionOptions;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.schemacrawler.SchemaRetrievalOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SingleUseUserCredentials;
import schemacrawler.schemacrawler.UserCredentials;
import schemacrawler.testdb.TestDatabase;

public abstract class BaseDatabaseTest
{

  static
  {
    TestDatabase.initialize();
  }

  @BeforeClass
  public static void setApplicationLogLevel()
    throws Exception
  {
    applyApplicationLogLevel(Level.OFF);
  }

  @BeforeClass
  public static void setEntityResolver()
    throws Exception
  {
    XMLUnit.setControlEntityResolver(new LocalEntityResolver());
  }

  protected Catalog getCatalog(final SchemaRetrievalOptions schemaRetrievalOptions,
                               final SchemaCrawlerOptions schemaCrawlerOptions)
    throws SchemaCrawlerException
  {
    createDataSource();

    final SchemaCrawler schemaCrawler = new SchemaCrawler(getConnection(),
                                                          schemaRetrievalOptions);
    final Catalog catalog = schemaCrawler.crawl(schemaCrawlerOptions);

    return catalog;
  }

  protected Catalog getCatalog(final SchemaCrawlerOptions schemaCrawlerOptions)
    throws SchemaCrawlerException
  {
    return getCatalog(new SchemaRetrievalOptionsBuilder().toOptions(),
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
      return createDataSource().getConnection();
    }
    catch (final SQLException e)
    {
      throw new SchemaCrawlerException(e.getMessage(), e);
    }
  }

  /**
   * Loads a properties file from a CLASSPATH resource.
   *
   * @param resource
   *        A CLASSPATH resource.
   * @return Config loaded from classpath resource
   * @throws IOException
   *         On an exception
   */
  protected Config loadConfigFromClasspathResource(final String resource)
    throws IOException
  {
    final InputStream stream = BaseDatabaseTest.class
      .getResourceAsStream(resource);
    requireNonNull(stream, "Could not load config from resource, " + resource);
    final Reader reader = new InputStreamReader(stream, UTF_8);
    final Properties properties = new Properties();
    try (final BufferedReader bufferedReader = new BufferedReader(reader);)
    {
      properties.load(bufferedReader);
    }
    return new Config(properties);
  }

  protected Config loadHsqldbConfig()
    throws IOException
  {
    return loadConfigFromClasspathResource("/hsqldb.INFORMATION_SCHEMA.config.properties");
  }

  private ConnectionOptions createDataSource()
    throws SchemaCrawlerException
  {
    final UserCredentials userCredentials = new SingleUseUserCredentials("sa",
                                                                         "");
    final Map<String, String> map = new HashMap<>();
    map.put("url", TestDatabase.CONNECTION_STRING);
    final ConnectionOptions connectionOptions = new DatabaseConnectionOptions(userCredentials,
                                                                              map);
    return connectionOptions;
  }

}
