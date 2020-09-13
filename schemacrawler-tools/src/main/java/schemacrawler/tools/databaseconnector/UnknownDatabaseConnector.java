/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.tools.databaseconnector;


import static schemacrawler.tools.options.Config.getSystemConfigurationProperty;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import schemacrawler.schemacrawler.DatabaseServerType;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import us.fatehi.utility.ioresource.EmptyInputResource;

final class UnknownDatabaseConnector
  extends DatabaseConnector
{

  private static final Pattern[] patterns = new Pattern[] {
    Pattern.compile("jdbc:db2:.*"),
    Pattern.compile("jdbc:(mysql|mariadb):.*"),
    Pattern.compile("jdbc:oracle:.*"),
    Pattern.compile("jdbc:postgresql:.*"),
    Pattern.compile("jdbc:sqlite:.*"),
    Pattern.compile("jdbc:sqlserver:.*"),
    };

  /**
   * Constructor for unknown databases. Bypass the null-checks of the main
   * constructor
   */
  UnknownDatabaseConnector()
  {
    super(DatabaseServerType.UNKNOWN,
          new EmptyInputResource(),
          (informationSchemaViewsBuilder, connection) -> {});
  }

  @Override
  public DatabaseConnectionSource newDatabaseConnectionSource(final String connectionUrl)
    throws SchemaCrawlerException
  {
    final DatabaseConnectionSource databaseConnectionSource =
      super.newDatabaseConnectionSource(connectionUrl);

    final String withoutDatabasePlugin = getSystemConfigurationProperty(
      "SC_IGNORE_MISSING_DATABASE_PLUGIN",
      Boolean.FALSE.toString());
    if (!Boolean.valueOf(withoutDatabasePlugin))
    {
      // Check if SchemaCrawler database plugin is in use
      final String url = databaseConnectionSource.getConnectionUrl();
      for (final Pattern pattern : patterns)
      {
        if (pattern
          .matcher(url)
          .matches())
        {
          throw new SchemaCrawlerException(String.format(
            "SchemaCrawler database plugin should be on the CLASSPATH for <%s>",
            url));
        }
      }
    }

    return databaseConnectionSource;
  }

  @Override
  protected Predicate<String> supportsUrlPredicate()
  {
    return url -> false;
  }

  @Override
  protected String constructConnectionUrl(String host, Integer port,
      String database, Map<String, String> urlx)
    throws SchemaCrawlerException
  {
    throw new SchemaCrawlerException("Cannot constract database connection operator for server without plugin");
  }

}
