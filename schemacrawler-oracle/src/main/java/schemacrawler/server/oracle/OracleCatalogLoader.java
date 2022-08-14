/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2022, Sualeh Fatehi <sualeh@hotmail.com>.
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

import java.sql.Connection;

import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import schemacrawler.tools.catalogloader.BaseCatalogLoader;
import schemacrawler.tools.executable.CommandDescription;
import us.fatehi.utility.database.SqlScript;

public final class OracleCatalogLoader extends BaseCatalogLoader {

  public OracleCatalogLoader() {
    super(new CommandDescription("oracleloader", "Loader for Oracle databases"), -1);
  }

  @Override
  public void loadCatalog() {

    if (!isDatabaseSystemIdentifier(
        OracleDatabaseConnector.DB_SERVER_TYPE.getDatabaseSystemIdentifier())) {
      return;
    }

    try (final Connection connection = getDataSource().get(); ) {
      SqlScript.executeScriptFromResource("/schemacrawler-oracle.before.sql", connection);
    } catch (final Exception e) {
      throw new ExecutionRuntimeException("Could not execute setup script for Oracle", e);
    }
  }
}
