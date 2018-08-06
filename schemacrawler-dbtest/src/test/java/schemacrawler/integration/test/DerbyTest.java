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


import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;

import schemacrawler.schemacrawler.RegularExpressionInclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.test.utility.BaseAdditionalDatabaseTest;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.text.schema.SchemaTextOptions;
import schemacrawler.tools.text.schema.SchemaTextOptionsBuilder;

public class DerbyTest
  extends BaseAdditionalDatabaseTest
{

  @Before
  public void createDatabase()
    throws SchemaCrawlerException, ClassNotFoundException, SQLException
  {
    Class.forName("org.apache.derby.impl.jdbc.EmbedConnection");
    createDataSource("jdbc:derby:memory:schemacrawler;create=true", null, null);
    createDatabase("/derby.scripts.txt");
  }

  @Test
  public void testDerbyWithConnection()
    throws Exception
  {
    final SchemaCrawlerOptionsBuilder schemaCrawlerOptionsBuilder = SchemaCrawlerOptionsBuilder
      .builder().withSchemaInfoLevel(SchemaInfoLevelBuilder.maximum())
      .includeSchemas(new RegularExpressionInclusionRule("BOOKS"));
    final SchemaCrawlerOptions schemaCrawlerOptions = schemaCrawlerOptionsBuilder
      .toOptions();

    final SchemaTextOptionsBuilder textOptionsBuilder = SchemaTextOptionsBuilder
      .builder();
    textOptionsBuilder.noIndexNames().showDatabaseInfo().showJdbcDriverInfo();
    final SchemaTextOptions textOptions = textOptionsBuilder.toOptions();

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable("details");
    executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
    executable.setAdditionalConfiguration(SchemaTextOptionsBuilder
      .builder(textOptions).toConfig());

    executeExecutable(executable, "text", "testDerbyWithConnection.txt");
  }

}
