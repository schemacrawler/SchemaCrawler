/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2011, Sualeh Fatehi.
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
import schemacrawler.schemacrawler.ConnectionOptions;
import schemacrawler.schemacrawler.DatabaseConfigConnectionOptions;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import sf.util.clparser.NumberOption;
import sf.util.clparser.StringOption;

/**
 * Options for the command line.
 * 
 * @author sfatehi
 */
final class BundledDriverConnectionOptionsParser
  extends BaseDatabaseConnectionOptionsParser
{

  BundledDriverConnectionOptionsParser(final Config config)
  {
    super(config);
    addOption(new StringOption("host", null));
    addOption(new NumberOption("port", 0));
    addOption(new StringOption("database", ""));
    addOption(new StringOption("urlx", ""));
  }

  @Override
  public ConnectionOptions getOptions()
    throws SchemaCrawlerException
  {
    final DatabaseConfigConnectionOptions connectionOptions = new DatabaseConfigConnectionOptions(config);
    setCredentials(connectionOptions);

    if (hasOptionValue("host"))
    {
      connectionOptions.setHost(getStringValue("host"));
    }
    if (hasOptionValue("port"))
    {
      connectionOptions.setPort(getIntegerValue("port"));
    }
    if (hasOptionValue("database"))
    {
      connectionOptions.setDatabase(getStringValue("database"));
    }
    if (hasOptionValue("urlx"))
    {
      connectionOptions.setConnectionProperties(getStringValue("urlx"));
    }

    return connectionOptions;
  }

}
