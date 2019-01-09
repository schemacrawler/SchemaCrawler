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
package schemacrawler.test.utility;


import static org.hamcrest.MatcherAssert.assertThat;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.fileResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.BeforeAll;

import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.testdb.TestSchemaCreator;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.OutputOptionsBuilder;
import schemacrawler.tools.options.TextOutputFormat;

public abstract class BaseAdditionalDatabaseTest
  extends BaseSchemaCrawlerTest
{

  protected final static Logger LOGGER = Logger
    .getLogger(BaseExecutableTest.class.getName());

  @BeforeAll
  public static final void startLogging()
  {
    final ConsoleHandler ch = new ConsoleHandler();
    ch.setLevel(Level.INFO);
    LOGGER.addHandler(ch);
    LOGGER.setLevel(Level.INFO);
  }

  private DataSource dataSource;

  protected void createDatabase(final String scriptsResource)
    throws SchemaCrawlerException, SQLException
  {
    try (Connection connection = getConnection();)
    {
      final TestSchemaCreator schemaCreator = new TestSchemaCreator(connection,
                                                                    scriptsResource);
      schemaCreator.run();
    }
  }

  protected void createDataSource(final String connectionUrl,
                                  final String user,
                                  final String password)
    throws SchemaCrawlerException, SQLException
  {
    LOGGER.log(Level.CONFIG, "Database connection URL: " + connectionUrl);

    final BasicDataSource ds = new BasicDataSource();
    ds.setUrl(connectionUrl);
    ds.setUsername(user);
    ds.setPassword(password);
    ds.setDefaultAutoCommit(false);

    dataSource = ds;
  }

  protected void executeExecutable(final SchemaCrawlerExecutable executable,
                                   final String referenceFileName)
    throws Exception
  {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout;)
    {
      final OutputOptions outputOptions = OutputOptionsBuilder
        .newOutputOptions(TextOutputFormat.text, out);

      executable.setOutputOptions(outputOptions);
      if (!executable.hasConnection())
      {
        executable.setConnection(getConnection());
      }
      executable.execute();
    }
    assertThat(fileResource(testout),
               hasSameContentAs(classpathResource(referenceFileName)));
  }

  protected final Connection getConnection()
    throws SchemaCrawlerException
  {
    try
    {
      return dataSource.getConnection();
    }
    catch (final SQLException e)
    {
      throw new SchemaCrawlerException(e.getMessage(), e);
    }
  }

}
