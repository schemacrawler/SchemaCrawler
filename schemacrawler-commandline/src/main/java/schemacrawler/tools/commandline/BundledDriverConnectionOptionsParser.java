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

package schemacrawler.tools.commandline;


import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerException;

/**
 * Options for the command-line.
 *
 * @author sfatehi
 */
final class BundledDriverConnectionOptionsParser
  extends BaseDatabaseConnectionOptionsParser
{

  private static final String URLX = "urlx";
  private static final String DATABASE = "database";
  private static final String PORT = "port";
  private static final String HOST = "host";

  BundledDriverConnectionOptionsParser(final Config config)
  {
    super(config);
  }

  @Override
  public void loadConfig()
    throws SchemaCrawlerException
  {
    super.loadConfig();

    if (config.hasValue(HOST))
    {
      config.put(HOST, config.getStringValue(HOST, ""));
    }
    if (config.hasValue(PORT))
    {
      config.put(PORT, String.valueOf(config.getIntegerValue(PORT, 0)));
    }
    if (config.hasValue(DATABASE))
    {
      config.put(DATABASE, config.getStringValue(DATABASE, ""));
    }
    if (config.hasValue(URLX))
    {
      config.put(URLX, config.getStringValue(URLX, ""));
    }
  }

}
