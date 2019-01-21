/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2019, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.integration.test;


import static com.wix.mysql.EmbeddedMysql.anEmbeddedMysql;
import static com.wix.mysql.config.Charset.UTF8;
import static com.wix.mysql.config.MysqldConfig.aMysqldConfig;
import static com.wix.mysql.distribution.Version.v5_6_latest;
import static java.util.concurrent.TimeUnit.MINUTES;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.notNullValue;
import static schemacrawler.utility.SchemaCrawlerUtility.getCatalog;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.wix.mysql.EmbeddedMysql;
import com.wix.mysql.config.MysqldConfig;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.Column;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.server.mysql.MySQLUtility;
import schemacrawler.test.utility.BaseAdditionalDatabaseTest;

public class MySQLEnumColumnTest
  extends BaseAdditionalDatabaseTest
{

  private boolean isDatabaseRunning;
  private EmbeddedMysql mysqld;

  @Test
  public void columnWithEnum()
    throws Exception
  {
    final SchemaCrawlerOptionsBuilder schemaCrawlerOptionsBuilder = SchemaCrawlerOptionsBuilder
      .builder().withSchemaInfoLevel(SchemaInfoLevelBuilder.maximum());
    final SchemaCrawlerOptions schemaCrawlerOptions = schemaCrawlerOptionsBuilder
      .toOptions();

    final Catalog catalog = getCatalog(getConnection(), schemaCrawlerOptions);
    final Schema schema = catalog.lookupSchema("schema_with_enum").orElse(null);
    assertThat(schema, notNullValue());
    final Table table = catalog.lookupTable(schema, "shirts").orElse(null);
    assertThat(table, notNullValue());
    final Column column = table.lookupColumn("size").orElse(null);
    assertThat(column, notNullValue());
    final List<String> enumValues = MySQLUtility.getEnumValues(column);
    assertThat(enumValues, containsInAnyOrder("small", "medium", "large"));
  }

  @BeforeEach
  public void createDatabase()
    throws SchemaCrawlerException, SQLException, IOException
  {
    try
    {
      final String schema = "schema_with_enum";

      final MysqldConfig config = aMysqldConfig(v5_6_latest)
        .withServerVariable("bind-address", "localhost")
        .withServerVariable("lower_case_table_names", 1).withCharset(UTF8)
        .withTimeout(1, MINUTES).withFreePort()
        .withUser("schema_with_enum", "schema_with_enum").build();
      mysqld = anEmbeddedMysql(config)
        .addSchema(schema,
                   () -> "CREATE TABLE shirts (name VARCHAR(40), size ENUM('small', 'medium', 'large'))")
        .start();

      final int port = mysqld.getConfig().getPort();
      final String user = mysqld.getConfig().getUsername();
      final String password = mysqld.getConfig().getPassword();
      final String connectionUrl = String
        .format("jdbc:mysql://localhost:%d/%s?useSSL=false", port, schema);

      createDataSource(connectionUrl, user, password);

      isDatabaseRunning = true;
    }
    catch (final Throwable e)
    {
      LOGGER.log(Level.FINE, e.getMessage(), e);
      // Do not run if database server cannot be loaded
      isDatabaseRunning = false;
    }
  }

  @AfterEach
  public void stopDatabaseServer()
  {
    if (isDatabaseRunning)
    {
      mysqld.stop();
    }
  }

}
