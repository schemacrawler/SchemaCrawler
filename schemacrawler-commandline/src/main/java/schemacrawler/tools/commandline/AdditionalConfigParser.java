/*
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2014, Sualeh Fatehi.
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


import java.io.IOException;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.text.schema.SchemaTextOptions;
import sf.util.clparser.BooleanOption;
import sf.util.clparser.StringOption;

/**
 * Parses the command-line.
 *
 * @author Sualeh Fatehi
 */
public final class AdditionalConfigParser
  extends BaseOptionsParser<Config>
{

  private final Config config;

  public AdditionalConfigParser(final Config config)
  {
    super(new StringOption('p',
                           "additionalconfigfile",
                           "schemacrawler.additional.config.properties"),
          new BooleanOption("noinfo"),
          new BooleanOption("sorttables"),
          new BooleanOption("sortcolumns"),
          new BooleanOption("sortinout"),
          new BooleanOption("portablenames"));
    this.config = config;
  }

  @Override
  public Config getOptions()
    throws SchemaCrawlerException
  {
    // Start with main config
    final Config additionalConfig = new Config(config);

    // Override with additional config
    final String cfgFile = getStringValue("p");
    try
    {
      additionalConfig.putAll(Config.load(cfgFile));
    }
    catch (final IOException e)
    {
      throw new SchemaCrawlerException("Could not load " + cfgFile, e);
    }

    final SchemaTextOptions textOptions = new SchemaTextOptions(additionalConfig);
    if (getBooleanValue("noinfo"))
    {
      textOptions.setNoInfo(true);
    }
    if (getBooleanValue("sorttables"))
    {
      textOptions.setAlphabeticalSortForTables(false);
    }
    if (getBooleanValue("sortcolumns"))
    {
      textOptions.setAlphabeticalSortForTableColumns(true);
    }
    if (getBooleanValue("sortinout"))
    {
      textOptions.setAlphabeticalSortForRoutineColumns(true);
    }
    if (getBooleanValue("portablenames"))
    {
      textOptions.setHideConstraintNames(true);
      textOptions.setHideForeignKeyNames(true);
      textOptions.setHideIndexNames(true);
      textOptions.setHidePrimaryKeyNames(true);
      textOptions.setHideTriggerNames(true);
      textOptions.setHideRoutineSpecificNames(true);
      textOptions.setShowUnqualifiedNames(true);
    }

    additionalConfig.putAll(textOptions.toConfig());

    return additionalConfig;
  }

}
