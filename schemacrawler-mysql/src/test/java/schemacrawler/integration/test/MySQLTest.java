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
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.concurrent.TimeUnit.MINUTES;
import static schemacrawler.test.utility.ExecutableTestUtility.executeExecutable;
import static sf.util.Utility.isBlank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.wix.mysql.EmbeddedMysql;
import com.wix.mysql.SqlScriptSource;
import com.wix.mysql.config.MysqldConfig;

import schemacrawler.schemacrawler.RegularExpressionInclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.test.utility.BaseAdditionalDatabaseTest;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.TextOutputFormat;
import schemacrawler.tools.text.schema.SchemaTextOptions;
import schemacrawler.tools.text.schema.SchemaTextOptionsBuilder;
import sf.util.IOUtility;

public class MySQLTest
  extends BaseAdditionalDatabaseTest
{

  protected class SqlScriptClasspathSource
    implements SqlScriptSource
  {
    private final String classpathResource;

    protected SqlScriptClasspathSource(final String classpathResource)
    {
      this.classpathResource = classpathResource;
    }

    @Override
    public String read()
      throws IOException
    {
      LOGGER.log(Level.CONFIG,
                 "Reading SQL script resource, " + classpathResource);
      return IOUtility.readResourceFully(classpathResource);
    }

    @Override
    public String toString()
    {
      return classpathResource;
    }

  }

  private boolean isDatabaseRunning;
  private EmbeddedMysql mysqld;

  @BeforeEach
  public void createDatabase()
    throws SchemaCrawlerException, SQLException, IOException
  {
    try
    {
      final String schema = "books";
      final List<SqlScriptSource> sqlScriptSources = sqlScriptSources();

      final MysqldConfig config = aMysqldConfig(v5_6_latest)
        .withServerVariable("bind-address", "localhost")
        .withServerVariable("lower_case_table_names", 1).withCharset(UTF8)
        .withTimeout(1, MINUTES).withFreePort()
        .withUser("schemacrawler", "schemacrawler").build();
      mysqld = anEmbeddedMysql(config).addSchema(schema, sqlScriptSources)
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

  @Test
  public void testMySQLWithConnection()
    throws Exception
  {
    if (!isDatabaseRunning)
    {
      LOGGER.log(Level.INFO, "Did NOT run MySQL test");
      return;
    }

    final SchemaCrawlerOptionsBuilder schemaCrawlerOptionsBuilder = SchemaCrawlerOptionsBuilder
      .builder();
    schemaCrawlerOptionsBuilder
      .withSchemaInfoLevel(SchemaInfoLevelBuilder.maximum())
      .includeSchemas(new RegularExpressionInclusionRule("books"))
      .includeAllSequences().includeAllSynonyms().includeAllRoutines();
    final SchemaCrawlerOptions options = schemaCrawlerOptionsBuilder
      .toOptions();

    final SchemaTextOptionsBuilder textOptionsBuilder = SchemaTextOptionsBuilder
      .builder();
    textOptionsBuilder.showDatabaseInfo().showJdbcDriverInfo();
    final SchemaTextOptions textOptions = textOptionsBuilder.toOptions();

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable("details");
    executable.setSchemaCrawlerOptions(options);
    executable.setAdditionalConfiguration(SchemaTextOptionsBuilder
      .builder(textOptions).toConfig());

    executeExecutable(getConnection(),
                      executable,
                      TextOutputFormat.text,
                      "testMySQLWithConnection.txt");
    LOGGER.log(Level.INFO, "Completed MySQL test successfully");
  }

  private List<SqlScriptSource> sqlScriptSources()
    throws IOException
  {
    final String scriptsResource = "/mysql.scripts.5.6.txt";
    final List<SqlScriptSource> scriptsResourceList = new ArrayList<>();
    try (
        final BufferedReader reader = new BufferedReader(new InputStreamReader(MySQLTest.class
          .getResourceAsStream(scriptsResource), UTF_8));)
    {
      reader.lines().forEach(line -> {
        if (!isBlank(line) && line.startsWith(";"))
        {
          final String scriptResource = line.substring(2);
          scriptsResourceList.add(new SqlScriptClasspathSource(scriptResource));
        }
      });
    }
    catch (final IOException e)
    {
      throw e;
    }
    return scriptsResourceList;
  }

}
