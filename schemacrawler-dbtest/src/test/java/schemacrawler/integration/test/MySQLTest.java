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
package schemacrawler.integration.test;


import static com.wix.mysql.EmbeddedMysql.anEmbeddedMysql;
import static com.wix.mysql.config.Charset.UTF8;
import static com.wix.mysql.config.MysqldConfig.aMysqldConfig;
import static com.wix.mysql.distribution.Version.v5_6_latest;
import static java.util.concurrent.TimeUnit.MINUTES;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.wix.mysql.EmbeddedMysql;
import com.wix.mysql.config.MysqldConfig;

import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.test.utility.BaseAdditionalDatabaseTest;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.text.schema.SchemaTextOptions;
import schemacrawler.tools.text.schema.SchemaTextOptionsBuilder;

public class MySQLTest
  extends BaseAdditionalDatabaseTest
{

  private boolean isDatabaseRunning;
  private EmbeddedMysql mysqld;

  @Before
  public void createDatabase()
    throws SchemaCrawlerException, SQLException, IOException
  {
    try
    {
      final String schema = "schemacrawler";

      final MysqldConfig config = aMysqldConfig(v5_6_latest)
        .withServerVariable("bind-address", "localhost").withFreePort()
        .withCharset(UTF8).withTimeout(1, MINUTES).build();
      mysqld = anEmbeddedMysql(config).addSchema(schema).start();

      final int port = mysqld.getConfig().getPort();
      final String user = mysqld.getConfig().getUsername();
      final String password = mysqld.getConfig().getPassword();
      final String connectionUrl = String
        .format("jdbc:mysql://localhost:%d/%s?useSSL=false", port, schema);

      createDataSource(connectionUrl, user, password);
      createDatabase("/mysql.scripts.txt");

      isDatabaseRunning = true;
    }
    catch (final Throwable e)
    {
      e.printStackTrace();
      // Do not run if database server cannot be loaded
      isDatabaseRunning = false;
    }
  }

  @After
  public void stopDatabaseServer()
  {
    if (isDatabaseRunning)
    {
      mysqld.stop();
    }
  }

  @Test
  public void testMySQLWithConnection()
    throws Exception
  {
    if (!isDatabaseRunning)
    {
      System.out.println("Did not run MySQL test");
      return;
    }

    final SchemaCrawlerOptions options = SchemaCrawlerOptionsBuilder
      .withMaximumSchemaInfoLevel();

    final SchemaTextOptionsBuilder textOptionsBuilder = SchemaTextOptionsBuilder
      .builder();
    textOptionsBuilder.noIndexNames().showDatabaseInfo().showJdbcDriverInfo();
    final SchemaTextOptions textOptions = textOptionsBuilder.toOptions();

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable("details");
    executable.setSchemaCrawlerOptions(options);
    executable.setAdditionalConfiguration(SchemaTextOptionsBuilder
      .builder(textOptions).toConfig());

    executeExecutable(executable, "text", "testMySQLWithConnection.txt");
  }

}
