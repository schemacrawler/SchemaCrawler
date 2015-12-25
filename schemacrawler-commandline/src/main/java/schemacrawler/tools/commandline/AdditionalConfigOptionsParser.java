/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2016, Sualeh Fatehi.
 * This library is free software; you can redistribute it and/or modify it under
 * the terms
 * of the GNU Lesser General Public License as published by the Free Software
 * Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330,
 * Boston, MA 02111-1307, USA.
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
    }
    if (config.hasValue("noremarks"))
    {
      final boolean booleanValue = config.getBooleanValue("noremarks", true);
      if (booleanValue)
      {
        textOptionsBuilder.hideRemarks();
      }
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
    }
    if (config.hasValue("sortcolumns"))
    {
      final boolean booleanValue = config.getBooleanValue("sortcolumns", true);
      if (booleanValue)
      {
        textOptionsBuilder.sortTableColumns();
      }
    }
    if (config.hasValue("sortinout"))
    {
      final boolean booleanValue = config.getBooleanValue("sortinout", true);
      if (booleanValue)
      {
        textOptionsBuilder.sortInOut();
      }
    }
    if (config.hasValue("portablenames"))
    {
      final boolean booleanValue = config.getBooleanValue("portablenames",
                                                          true);
      if (booleanValue)
      {
        textOptionsBuilder.portableNames();
      }

    }

    config.putAll(textOptionsBuilder.toConfig());

    config.putAll(config);
  }

}
