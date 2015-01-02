/*
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2015, Sualeh Fatehi.
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
import schemacrawler.tools.text.schema.SchemaTextOptions;

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
    final SchemaTextOptions textOptions = new SchemaTextOptions(config);
    if (config.hasValue("noinfo"))
    {
      final boolean booleanValue = config.getBooleanValue("noinfo", true);
      textOptions.setNoInfo(booleanValue);
    }
    if (config.hasValue("noremarks"))
    {
      final boolean booleanValue = config.getBooleanValue("noremarks", true);
      textOptions.setHideRemarks(booleanValue);
    }
    if (config.hasValue("sorttables"))
    {
      final boolean booleanValue = config.getBooleanValue("sorttables", true);
      textOptions.setAlphabeticalSortForTables(booleanValue);
    }
    if (config.hasValue("sortcolumns"))
    {
      final boolean booleanValue = config.getBooleanValue("sortcolumns", true);
      textOptions.setAlphabeticalSortForTableColumns(booleanValue);
    }
    if (config.hasValue("sortinout"))
    {
      final boolean booleanValue = config.getBooleanValue("sortinout", true);
      textOptions.setAlphabeticalSortForRoutineColumns(booleanValue);
    }
    if (config.hasValue("portablenames"))
    {
      final boolean booleanValue = config
        .getBooleanValue("portablenames", true);
      textOptions.setHideConstraintNames(booleanValue);
      textOptions.setHideForeignKeyNames(booleanValue);
      textOptions.setHideIndexNames(booleanValue);
      textOptions.setHidePrimaryKeyNames(booleanValue);
      textOptions.setHideTriggerNames(booleanValue);
      textOptions.setHideRoutineSpecificNames(booleanValue);
      textOptions.setShowUnqualifiedNames(booleanValue);
    }

    config.putAll(textOptions.toConfig());

    config.putAll(config);
  }

}
