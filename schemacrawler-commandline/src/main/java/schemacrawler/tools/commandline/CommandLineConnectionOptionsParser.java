/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2012, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */

package schemacrawler.tools.commandline;


import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.DatabaseConnectionOptions;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import sf.util.Utility;
import sf.util.clparser.StringOption;

/**
 * Options for the command line.
 * 
 * @author sfatehi
 */
final class CommandLineConnectionOptionsParser
  extends BaseDatabaseConnectionOptionsParser
{

  CommandLineConnectionOptionsParser(final Config config)
  {
    super(config);
    addOption(new StringOption("driver", null));
    addOption(new StringOption("url", null));
  }

  @Override
  public DatabaseConnectionOptions getOptions()
    throws SchemaCrawlerException
  {
    final DatabaseConnectionOptions connectionOptions;
    if (hasOptionValue("url"))
    {
      final String jdbcDriverClassName = getStringValue("driver");
      final String connectionUrl = getStringValue("url");
      if (Utility.isBlank(jdbcDriverClassName)
          || Utility.isBlank(connectionUrl))
      {
        connectionOptions = null;
      }
      else
      {
        connectionOptions = new DatabaseConnectionOptions(jdbcDriverClassName,
                                                          connectionUrl);
        setUser(connectionOptions);
        setPassword(connectionOptions);
      }
    }
    else
    {
      connectionOptions = null;
    }
    return connectionOptions;
  }

}
