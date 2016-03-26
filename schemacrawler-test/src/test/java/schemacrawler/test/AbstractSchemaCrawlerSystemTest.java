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
package schemacrawler.test;


import static org.junit.Assert.fail;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.ConnectionOptions;
import schemacrawler.schemacrawler.RegularExpressionInclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.options.InfoLevel;
import schemacrawler.utility.SchemaCrawlerUtility;
import sf.util.ObjectToString;

public abstract class AbstractSchemaCrawlerSystemTest
{

  protected final ApplicationContext appContext = new ClassPathXmlApplicationContext("datasources.xml");
  protected final String[] dataSources = {
                                           "MicrosoftSQLServer", "Oracle",
                                           "IBM_DB2", "MySQL", "PostgreSQL",
      // "Derby",
  };

  @Test
  public void connections()
    throws Exception
  {
    final List<String> connectionErrors = new ArrayList<>();
    for (final String dataSource: dataSources)
    {
      try
      {
        connect(dataSource);
      }
      catch (final Exception e)
      {
        final String message = dataSource + ": " + e.getMessage();
        System.out.println(message);
        connectionErrors.add(message);
      }
    }
    if (!connectionErrors.isEmpty())
    {
      final String error = ObjectToString.toString(connectionErrors);
      System.out.println(error);
      fail(error);
    }
  }

  protected Connection connect(final String dataSourceName)
    throws Exception
  {
    final ConnectionOptions connectionOptions = (ConnectionOptions) appContext
      .getBean(dataSourceName);
    final Connection connection = connectionOptions.getConnection();
    return connection;
  }

  protected SchemaCrawlerOptions createOptions(final String dataSourceName,
                                               final String schemaInclusion)
  {
    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions
      .setSchemaInfoLevel(InfoLevel.maximum.buildSchemaInfoLevel());
    if (schemaInclusion != null)
    {
      schemaCrawlerOptions
        .setSchemaInclusionRule(new RegularExpressionInclusionRule(schemaInclusion));
    }
    return schemaCrawlerOptions;
  }

  protected Catalog
    retrieveDatabase(final String dataSourceName,
                     final SchemaCrawlerOptions schemaCrawlerOptions)
                       throws Exception
  {
    final Connection connection = connect(dataSourceName);
    try
    {
      final Catalog catalog = SchemaCrawlerUtility
        .getCatalog(connection, schemaCrawlerOptions);
      return catalog;
    }
    catch (final Exception e)
    {
      throw new SchemaCrawlerException(dataSourceName, e);
    }
  }

}
