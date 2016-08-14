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
      final boolean value = config.getBooleanValue("noinfo", true);
      textOptionsBuilder.noInfo(value);
      consumeOption("noinfo");
    }
    if (config.hasValue("noremarks"))
    {
      final boolean value = config.getBooleanValue("noremarks", true);
      textOptionsBuilder.noRemarks(value);
      consumeOption("noremarks");
    }
    if (config.hasValue("sorttables"))
    {
      // Special treatment, since -sorttables is true by default in the
      // options
      final boolean value = config.getBooleanValue("sorttables", true);
      textOptionsBuilder.sortTables(value);
      consumeOption("sorttables");
    }
    if (config.hasValue("sortcolumns"))
    {
      final boolean value = config.getBooleanValue("sortcolumns", true);
      textOptionsBuilder.sortTableColumns(value);
      consumeOption("sortcolumns");
    }
    if (config.hasValue("sortinout"))
    {
      final boolean value = config.getBooleanValue("sortinout", true);
      textOptionsBuilder.sortInOut(value);
      consumeOption("sortinout");
    }
    if (config.hasValue("portablenames"))
    {
      final boolean value = config.getBooleanValue("portablenames", true);
      textOptionsBuilder.portableNames(value);
      consumeOption("portablenames");
    }

    config.putAll(textOptionsBuilder.toConfig());

    config.putAll(config);
  }

}
