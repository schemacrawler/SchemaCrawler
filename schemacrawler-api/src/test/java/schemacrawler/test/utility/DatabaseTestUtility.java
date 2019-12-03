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

package schemacrawler.test.utility;


import static java.util.Objects.requireNonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.Properties;

import schemacrawler.crawl.SchemaCrawler;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.schemacrawler.SchemaRetrievalOptionsBuilder;

public final class DatabaseTestUtility
{

  public static Catalog getCatalog(final Connection connection,
                                   final SchemaCrawlerOptions schemaCrawlerOptions)
    throws SchemaCrawlerException
  {
    return getCatalog(connection,
                      SchemaRetrievalOptionsBuilder.newSchemaRetrievalOptions(),
                      schemaCrawlerOptions);
  }

  public static Catalog getCatalog(final Connection connection,
                                   final SchemaRetrievalOptions schemaRetrievalOptions,
                                   final SchemaCrawlerOptions schemaCrawlerOptions)
    throws SchemaCrawlerException
  {
    final SchemaCrawler schemaCrawler = new SchemaCrawler(connection,
                                                          schemaRetrievalOptions,
                                                          schemaCrawlerOptions);
    final Catalog catalog = schemaCrawler.crawl();

    return catalog;
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
  protected static Config loadConfigFromClasspathResource(final String resource)
    throws IOException
  {
    final InputStream stream = DatabaseTestUtility.class
      .getResourceAsStream(resource);
    requireNonNull(stream, "Could not load config from resource, " + resource);
    final Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
    final Properties properties = new Properties();
    try (final BufferedReader bufferedReader = new BufferedReader(reader);)
    {
      properties.load(bufferedReader);
    }
    return new Config(properties);
  }

  public static Config loadHsqldbConfig()
    throws IOException
  {
    return loadConfigFromClasspathResource("/hsqldb.INFORMATION_SCHEMA.config.properties");
  }

  public final static SchemaCrawlerOptions schemaCrawlerOptionsWithMaximumSchemaInfoLevel = SchemaCrawlerOptionsBuilder
    .builder().withSchemaInfoLevel(SchemaInfoLevelBuilder.maximum())
    .toOptions();

  private DatabaseTestUtility()
  {
    // Prevent instantiation
  }

}
