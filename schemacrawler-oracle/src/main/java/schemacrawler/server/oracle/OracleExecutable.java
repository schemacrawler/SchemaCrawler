/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.server.oracle;


import static sf.util.DatabaseUtility.executeScriptFromResource;

import java.sql.Connection;

import schemacrawler.schemacrawler.DatabaseSpecificOverrideOptions;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.executable.BaseExecutable;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.text.schema.SchemaTextOptions;
import schemacrawler.tools.text.schema.SchemaTextOptionsBuilder;

public class OracleExecutable
  extends BaseExecutable
{

  protected OracleExecutable(final String command)
  {
    super(command);
  }

  @Override
  public void execute(final Connection connection,
                      final DatabaseSpecificOverrideOptions databaseSpecificOverrideOptions)
    throws Exception
  {
    executeOracleScripts(connection);

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(command);
    executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
    executable.setAdditionalConfiguration(additionalConfiguration);
    executable.setOutputOptions(outputOptions);
    executable.execute(connection, databaseSpecificOverrideOptions);
  }

  private void executeOracleScripts(final Connection connection)
    throws SchemaCrawlerException
  {
    executeScriptFromResource(connection, "/schemacrawler-oracle.before.sql");

    final SchemaTextOptions schemaTextOptions = new SchemaTextOptionsBuilder()
      .fromConfig(additionalConfiguration).toOptions();
    if (schemaTextOptions.isShowUnqualifiedNames())
    {
      executeScriptFromResource(connection,
                                "/schemacrawler-oracle.show_unqualified_names.sql");
    }
  }

}
