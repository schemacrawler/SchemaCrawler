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

import schemacrawler.schemacrawler.RegularExpressionExclusionRule;
import schemacrawler.schemacrawler.RegularExpressionInclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.test.utility.BaseAdditionalDatabaseTest;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.text.schema.SchemaTextOptions;
import schemacrawler.tools.text.schema.SchemaTextOptionsBuilder;

public class H2Test
  extends BaseAdditionalDatabaseTest
{

  @Before
  public void createDatabase()
    throws SchemaCrawlerException, SQLException
  {
    createDatabase("jdbc:h2:mem:schemacrawler", "/h2.scripts.txt");
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
    textOptions.setShowDatabaseInfo(true);
    textOptions.setShowJdbcDriverInfo(true);

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable("details");
    executable.setSchemaCrawlerOptions(options);
    executable
      .setAdditionalConfiguration(new SchemaTextOptionsBuilder(textOptions)
        .toConfig());

    executeExecutable(executable, "text", "testH2WithConnection.txt");
  }

}
