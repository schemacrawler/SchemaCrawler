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


import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.BeforeClass;

import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.testdb.TestSchemaCreator;

public abstract class BaseAdditionalDatabaseTest
  extends BaseExecutableTest
{

  protected final static Logger LOGGER = Logger
    .getLogger(BaseExecutableTest.class.getName());

  @BeforeClass
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
    final BasicDataSource dataSource = new BasicDataSource();
    dataSource.setUsername(user);
    dataSource.setPassword(password);
    dataSource.setUrl(connectionUrl);
    dataSource.setDefaultAutoCommit(false);
    dataSource.setInitialSize(1);
    dataSource.setMaxTotal(1);

    LOGGER.log(Level.CONFIG, "Database connection URL: " + connectionUrl);
    this.dataSource = dataSource;
  }

  @Override
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
