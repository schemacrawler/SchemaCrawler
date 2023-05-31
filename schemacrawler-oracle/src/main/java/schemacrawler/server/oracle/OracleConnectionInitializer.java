/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
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

import static us.fatehi.utility.database.SqlScript.executeScriptFromResource;
import java.sql.Connection;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import us.fatehi.utility.string.StringFormat;

public final class OracleConnectionInitializer implements Consumer<Connection> {

  private static final Logger LOGGER =
      Logger.getLogger(OracleConnectionInitializer.class.getName());

  @Override
  public void accept(final Connection connection) {
    LOGGER.log(Level.FINE, new StringFormat("Initializing Oracle connection <%s>", connection));
    executeScriptFromResource("/schemacrawler-oracle.before.sql", connection);
  }
}
