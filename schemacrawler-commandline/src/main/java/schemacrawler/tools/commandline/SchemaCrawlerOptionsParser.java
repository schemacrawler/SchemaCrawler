/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2013, Sualeh Fatehi.
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
import schemacrawler.schemacrawler.InclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevel;
import schemacrawler.tools.options.InfoLevel;
import sf.util.clparser.BooleanOption;
import sf.util.clparser.NumberOption;
import sf.util.clparser.StringOption;

/**
 * Parses the command line.
 * 
 * @author Sualeh Fatehi
 */
final class SchemaCrawlerOptionsParser
  extends BaseOptionsParser<SchemaCrawlerOptions>
{

  private static final String DEFAULT_TABLE_TYPES = "TABLE,VIEW";
  private static final String DEFAULT_ROUTINE_TYPES = "PROCEDURE,FUNCTION";

  private final SchemaCrawlerOptions options;

  SchemaCrawlerOptionsParser(final Config config)
  {
    super(new StringOption("infolevel", "standard"),
          new StringOption("schemas", InclusionRule.NONE),
          new StringOption("table_types", DEFAULT_TABLE_TYPES),
          new StringOption("tables", InclusionRule.ALL),
          new StringOption("excludecolumns", InclusionRule.NONE),
          new StringOption("synonyms", InclusionRule.ALL),
          new StringOption("routine_types", DEFAULT_ROUTINE_TYPES),
          new StringOption("routines", InclusionRule.ALL),
          new StringOption("excludeinout", InclusionRule.NONE),
          new StringOption("grepcolumns", InclusionRule.NONE),
          new StringOption("grepinout", InclusionRule.NONE),
          new StringOption("grepdef", InclusionRule.NONE),
          new BooleanOption('v', "invert-match"),
          new NumberOption("parents", 0),
          new NumberOption("children", 0));
    options = new SchemaCrawlerOptions(config);
  }

  @Override
  protected SchemaCrawlerOptions getOptions()
    throws SchemaCrawlerException
  {
    if (hasOptionValue("infolevel"))
    {
      try
      {
        final String infoLevel = getStringValue("infolevel");
        final SchemaInfoLevel schemaInfoLevel = InfoLevel.valueOf(infoLevel)
          .getSchemaInfoLevel();
        options.setSchemaInfoLevel(schemaInfoLevel);
      }
      catch (final IllegalArgumentException e)
      {
        options.setSchemaInfoLevel(SchemaInfoLevel.standard());
      }
    }
    else
    {
      throw new SchemaCrawlerException("No infolevel specified");
    }

    if (hasOptionValue("schemas"))
    {
      final InclusionRule schemaInclusionRule = new InclusionRule(getStringValue("schemas"),
                                                                  InclusionRule.NONE);
      options.setSchemaInclusionRule(schemaInclusionRule);
    }

    if (hasOptionValue("table_types"))
    {
      options.setTableTypes(getStringValue("table_types"));
    }

    if (hasOptionValue("tables"))
    {
      final InclusionRule tableInclusionRule = new InclusionRule(getStringValue("tables"),
                                                                 InclusionRule.NONE);
      options.setTableInclusionRule(tableInclusionRule);
    }
    if (hasOptionValue("excludecolumns"))
    {
      final InclusionRule columnInclusionRule = new InclusionRule(InclusionRule.ALL,
                                                                  getStringValue("excludecolumns"));
      options.setColumnInclusionRule(columnInclusionRule);
    }

    if (hasOptionValue("routine_types"))
    {
      options.setRoutineTypes(getStringValue("routine_types"));
    }

    if (hasOptionValue("routines"))
    {
      final InclusionRule routineInclusionRule = new InclusionRule(getStringValue("routines"),
                                                                   InclusionRule.NONE);
      options.setRoutineInclusionRule(routineInclusionRule);
    }
    if (hasOptionValue("excludeinout"))
    {
      final InclusionRule routineColumnInclusionRule = new InclusionRule(InclusionRule.ALL,
                                                                         getStringValue("excludeinout"));
      options.setRoutineColumnInclusionRule(routineColumnInclusionRule);
    }

    if (hasOptionValue("synonyms"))
    {
      final InclusionRule synonymInclusionRule = new InclusionRule(getStringValue("synonyms"),
                                                                   InclusionRule.NONE);
      options.setSynonymInclusionRule(synonymInclusionRule);
    }

    if (hasOptionValue("v"))
    {
      options.setGrepInvertMatch(getBooleanValue("v"));
    }

    if (hasOptionValue("grepcolumns"))
    {
      final InclusionRule grepColumnInclusionRule = new InclusionRule(getStringValue("grepcolumns"),
                                                                      InclusionRule.NONE);
      options.setGrepColumnInclusionRule(grepColumnInclusionRule);
    }
    else
    {
      options.setGrepColumnInclusionRule(null);
    }

    if (hasOptionValue("grepinout"))
    {
      final InclusionRule grepRoutineColumnInclusionRule = new InclusionRule(getStringValue("grepinout"),
                                                                             InclusionRule.NONE);
      options.setGrepRoutineColumnInclusionRule(grepRoutineColumnInclusionRule);
    }
    else
    {
      options.setGrepRoutineColumnInclusionRule(null);
    }

    if (hasOptionValue("grepdef"))
    {
      final InclusionRule grepDefinitionInclusionRule = new InclusionRule(getStringValue("grepdef"),
                                                                          InclusionRule.NONE);
      options.setGrepDefinitionInclusionRule(grepDefinitionInclusionRule);
    }
    else
    {
      options.setGrepDefinitionInclusionRule(null);
    }

    if (hasOptionValue("parents"))
    {
      final int parentTableFilterDepth = getIntegerValue("parents");
      options.setParentTableFilterDepth(parentTableFilterDepth);
    }
    else
    {
      options.setParentTableFilterDepth(0);
    }

    if (hasOptionValue("children"))
    {
      final int childTableFilterDepth = getIntegerValue("children");
      options.setChildTableFilterDepth(childTableFilterDepth);
    }
    else
    {
      options.setParentTableFilterDepth(0);
    }

    return options;
  }
}
