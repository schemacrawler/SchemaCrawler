/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi.
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


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import schemacrawler.schemacrawler.RegularExpressionExclusionRule;
import schemacrawler.schemacrawler.RegularExpressionInclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.test.utility.BaseExecutableTest;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.text.schema.SchemaTextOptions;
import schemacrawler.tools.text.schema.SchemaTextOptionsBuilder;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:h2-context.xml")
public class H2Test
  extends BaseExecutableTest
{

  @Autowired
  private DataSource dataSource;

  @Test
  public void testDataSource()
    throws Exception
  {
    assertNotNull(dataSource);
    final Connection connection = getConnection();
    assertNotNull(connection);
    assertEquals("org.h2.jdbc.JdbcConnection", connection.getClass().getName());
  }

  @Test
  public void testH2WithConnection()
    throws Exception
  {
    final SchemaCrawlerOptions options = new SchemaCrawlerOptions();
    options.setSchemaInfoLevel(SchemaInfoLevelBuilder.maximum());
    options
      .setSchemaInclusionRule(new RegularExpressionInclusionRule(".*\\.BOOKS"));
    options
      .setSequenceInclusionRule(new RegularExpressionExclusionRule(".*\\.BOOKS\\.SYSTEM_SEQUENCE.*"));

    final SchemaTextOptions textOptions = new SchemaTextOptions();
    textOptions.setHideIndexNames(true);

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable("details");
    executable.setSchemaCrawlerOptions(options);
    executable
      .setAdditionalConfiguration(new SchemaTextOptionsBuilder(textOptions)
        .toConfig());

    executeExecutable(executable, "text", "testH2WithConnection.txt");
  }

  @Override
  protected Connection getConnection()
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
