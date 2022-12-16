/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
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

import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.Map;
import java.util.Properties;

import schemacrawler.crawl.SchemaCrawler;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.LoadOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.schemacrawler.SchemaRetrievalOptionsBuilder;
import us.fatehi.utility.PropertiesUtility;
import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.datasource.DatabaseConnectionSourceUtility;
import us.fatehi.utility.ioresource.ClasspathInputResource;

public final class DatabaseTestUtility {

  public static final SchemaRetrievalOptions schemaRetrievalOptionsDefault =
      SchemaRetrievalOptionsBuilder.newSchemaRetrievalOptions();
  public static final SchemaCrawlerOptions schemaCrawlerOptionsWithMaximumSchemaInfoLevel =
      getMaximumSchemaCrawlerOptions();

  public static Catalog getCatalog(
      final Connection connection, final SchemaCrawlerOptions schemaCrawlerOptions) {
    return getCatalog(connection, schemaRetrievalOptionsDefault, schemaCrawlerOptions);
  }

  public static Catalog getCatalog(
      final Connection connection,
      final SchemaRetrievalOptions schemaRetrievalOptions,
      final SchemaCrawlerOptions schemaCrawlerOptions) {

    final DatabaseConnectionSource dataSource =
        DatabaseConnectionSourceUtility.newTestDatabaseConnectionSource(connection);

    final SchemaCrawler schemaCrawler =
        new SchemaCrawler(dataSource, schemaRetrievalOptions, schemaCrawlerOptions);
    final Catalog catalog = schemaCrawler.crawl();
    return catalog;
  }

  public static Map<String, String> loadHsqldbConfig() throws IOException {
    final Properties properties =
        TestUtility.loadProperties(
            new ClasspathInputResource("/hsqldb.INFORMATION_SCHEMA.config.properties"));
    return PropertiesUtility.propertiesMap(properties);
  }

  public static Path tempHsqldbConfig() throws IOException {
    final Properties properties =
        TestUtility.loadProperties(
            new ClasspathInputResource("/hsqldb.INFORMATION_SCHEMA.config.properties"));
    return TestUtility.savePropertiesToTempFile(properties);
  }

  private static SchemaCrawlerOptions getMaximumSchemaCrawlerOptions() {
    final LoadOptionsBuilder loadOptionsBuilder =
        LoadOptionsBuilder.builder().withSchemaInfoLevel(SchemaInfoLevelBuilder.maximum());
    return SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
        .withLoadOptions(loadOptionsBuilder.toOptions());
  }

  private DatabaseTestUtility() {
    // Prevent instantiation
  }
}
