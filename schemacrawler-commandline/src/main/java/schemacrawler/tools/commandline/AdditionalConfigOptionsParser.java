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
import schemacrawler.tools.text.schema.SchemaTextOptionsBuilder;

/**
 * Parses the command-line.
 *
 * @author Sualeh Fatehi
 */
public final class AdditionalConfigOptionsParser
  extends BaseConfigOptionsParser
{

  public AdditionalConfigOptionsParser(final Config config)
  {
    super(config);
  }

  @Override
  public void loadConfig()
    throws SchemaCrawlerException
  {
    final SchemaTextOptionsBuilder textOptionsBuilder = new SchemaTextOptionsBuilder()
      .fromConfig(config);
    if (config.hasValue("noinfo"))
    {
      final boolean booleanValue = config.getBooleanValue("noinfo", true);
      if (booleanValue)
      {
        textOptionsBuilder.noInfo();
      }
      consumeOption("noinfo");
    }
    if (config.hasValue("noremarks"))
    {
      final boolean booleanValue = config.getBooleanValue("noremarks", true);
      if (booleanValue)
      {
        textOptionsBuilder.hideRemarks();
      }
      consumeOption("noremarks");
    }
    if (config.hasValue("sorttables"))
    {
      // Special treatment, since -sorttables is true by default in the
      // options
      final boolean booleanValue = config.getBooleanValue("sorttables", true);
      if (booleanValue)
      {
        textOptionsBuilder.sortTables();
      }
      else
      {
        textOptionsBuilder.naturalSortTables();
      }
      consumeOption("sorttables");
    }
    if (config.hasValue("sortcolumns"))
    {
      final boolean booleanValue = config.getBooleanValue("sortcolumns", true);
      if (booleanValue)
      {
        textOptionsBuilder.sortTableColumns();
      }
      consumeOption("sortcolumns");
    }
    if (config.hasValue("sortinout"))
    {
      final boolean booleanValue = config.getBooleanValue("sortinout", true);
      if (booleanValue)
      {
        textOptionsBuilder.sortInOut();
      }
      consumeOption("sortinout");
    }
    if (config.hasValue("portablenames"))
    {
      final boolean booleanValue = config.getBooleanValue("portablenames",
                                                          true);
      if (booleanValue)
      {
        textOptionsBuilder.portableNames();
      }
      consumeOption("portablenames");
    }

    config.putAll(textOptionsBuilder.toConfig());

    config.putAll(config);
  }

}
