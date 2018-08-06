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

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;

import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.testdb.SqlScript;
import schemacrawler.testdb.TestSchemaCreator;

public abstract class BaseAdditionalDatabaseTest
  extends BaseExecutableTest
{

  private DataSource dataSource;

  protected void createDatabase(final String connectionUrl,
                                final String scriptsResource)
    throws SchemaCrawlerException, SQLException
  {
    final BasicDataSource dataSource = new BasicDataSource();
    dataSource.setUsername(null);
    dataSource.setPassword(null);
    dataSource.setUrl(connectionUrl);
    dataSource.setDefaultAutoCommit(false);
    dataSource.setInitialSize(1);
    dataSource.setMaxTotal(1);

    this.dataSource = dataSource;

    try (Connection connection = getConnection();)
    {
      final TestSchemaCreator schemaCreator = new TestSchemaCreator(connection,
                                                                    scriptsResource);
      schemaCreator.run();
    }
  }

  protected void dropDatabase(final String connectionUrl,
                              final String dropDbResource)
  {
    try (final BasicDataSource dataSource = new BasicDataSource();)
    {
      dataSource.setUsername(null);
      dataSource.setPassword(null);
      dataSource.setUrl(connectionUrl);
      dataSource.setDefaultAutoCommit(false);
      dataSource.setInitialSize(1);
      dataSource.setMaxTotal(1);

      try (Connection connection = dataSource.getConnection();)
      {
        final SqlScript dropDbScript = new SqlScript(dropDbResource,
                                                     connection);
        dropDbScript.run();
      }
    }
    catch (final Throwable e)
    {
      e.printStackTrace();
    }
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
