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


import static sf.util.Utility.isBlank;

import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.InclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerCommandLineException;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevel;
import schemacrawler.tools.options.InfoLevel;

/**
 * Parses the command-line.
 *
 * @author Sualeh Fatehi
 */
public final class SchemaCrawlerOptionsParser
  extends BaseOptionsParser<SchemaCrawlerOptions>
{

  private static final Logger LOGGER = Logger
    .getLogger(SchemaCrawlerOptionsParser.class.getName());

  private static final String DEFAULT_TABLE_TYPES = "TABLE,VIEW";
  private static final String DEFAULT_ROUTINE_TYPES = "PROCEDURE,FUNCTION";

  private final SchemaCrawlerOptions options;

  public SchemaCrawlerOptionsParser(final Config config)
  {
    super(config);
    normalizeOptionName("infolevel");
    normalizeOptionName("schemas");
    normalizeOptionName("tabletypes");
    normalizeOptionName("tables");
    normalizeOptionName("excludecolumns");
    normalizeOptionName("synonyms");
    normalizeOptionName("sequences");
    normalizeOptionName("routinetypes");
    normalizeOptionName("routines");
    normalizeOptionName("excludeinout");
    normalizeOptionName("grepcolumns");
    normalizeOptionName("grepinout");
    normalizeOptionName("grepdef");
    normalizeOptionName("invert-match");
    normalizeOptionName("only-matching");
    normalizeOptionName("hideemptytables");
    normalizeOptionName("parents");
    normalizeOptionName("children");

    options = new SchemaCrawlerOptions(config);
  }

  @Override
  public SchemaCrawlerOptions getOptions()
    throws SchemaCrawlerException
  {
    if (config.hasValue("infolevel"))
    {
      final String infoLevel = config.getStringValue("infolevel", "standard");
      final SchemaInfoLevel schemaInfoLevel = InfoLevel
        .valueOfFromString(infoLevel).getSchemaInfoLevel();
      options.setSchemaInfoLevel(schemaInfoLevel);
      consumeOption("infolevel");
    }
    else
    {
      throw new SchemaCrawlerCommandLineException("No infolevel specified");
    }

    if (config.hasValue("schemas"))
    {
      final InclusionRule schemaInclusionRule = config
        .getInclusionRule("schemas");
      logOverride("schemas",
                  options.getSchemaInclusionRule(),
                  schemaInclusionRule);
      options.setSchemaInclusionRule(schemaInclusionRule);
      consumeOption("schemas");
    }

    if (config.hasValue("tabletypes"))
    {
      final String tabletypes = config.getStringValue("tabletypes",
                                                      DEFAULT_TABLE_TYPES);
      if (!isBlank(tabletypes))
      {
        options.setTableTypesFromString(tabletypes);
      }
      else
      {
        options.setTableTypesFromString(null);
      }
      consumeOption("tabletypes");
    }

    if (config.hasValue("tables"))
    {
      final InclusionRule tableInclusionRule = config
        .getInclusionRule("tables");
      logOverride("tables", options.getTableInclusionRule(), tableInclusionRule);
      options.setTableInclusionRule(tableInclusionRule);
      consumeOption("tables");
    }
    if (config.hasValue("excludecolumns"))
    {
      final InclusionRule columnInclusionRule = config
        .getExclusionRule("excludecolumns");
      logOverride("excludecolumns",
                  options.getColumnInclusionRule(),
                  columnInclusionRule);
      options.setColumnInclusionRule(columnInclusionRule);
      consumeOption("excludecolumns");
    }

    if (config.hasValue("routinetypes"))
    {
      options.setRoutineTypes(config.getStringValue("routinetypes",
                                                    DEFAULT_ROUTINE_TYPES));
      consumeOption("routinetypes");
    }

    if (config.hasValue("routines"))
    {
      final InclusionRule routineInclusionRule = config
        .getInclusionRule("routines");
      logOverride("routines",
                  options.getRoutineInclusionRule(),
                  routineInclusionRule);
      options.setRoutineInclusionRule(routineInclusionRule);
      consumeOption("routines");
    }
    if (config.hasValue("excludeinout"))
    {
      final InclusionRule routineColumnInclusionRule = config
        .getExclusionRule("excludeinout");
      logOverride("excludeinout",
                  options.getRoutineColumnInclusionRule(),
                  routineColumnInclusionRule);
      options.setRoutineColumnInclusionRule(routineColumnInclusionRule);
      consumeOption("excludeinout");
    }

    if (config.hasValue("synonyms"))
    {
      final InclusionRule synonymInclusionRule = config
        .getInclusionRule("synonyms");
      logOverride("synonyms",
                  options.getSynonymInclusionRule(),
                  synonymInclusionRule);
      options.setSynonymInclusionRule(synonymInclusionRule);
      consumeOption("synonyms");
    }

    if (config.hasValue("sequences"))
    {
      final InclusionRule sequenceInclusionRule = config
        .getInclusionRule("sequences");
      logOverride("sequences",
                  options.getSequenceInclusionRule(),
                  sequenceInclusionRule);
      options.setSequenceInclusionRule(sequenceInclusionRule);
      consumeOption("sequences");
    }

    if (config.hasValue("invert-match"))
    {
      options.setGrepInvertMatch(config.getBooleanValue("invert-match", true));
      consumeOption("invert-match");
    }

    if (config.hasValue("only-matching"))
    {
      options
        .setGrepOnlyMatching(config.getBooleanValue("only-matching", true));
      consumeOption("only-matching");
    }

    if (config.hasValue("grepcolumns"))
    {
      final InclusionRule grepColumnInclusionRule = config
        .getInclusionRule("grepcolumns");
      options.setGrepColumnInclusionRule(grepColumnInclusionRule);
      consumeOption("grepcolumns");
    }
    else
    {
      options.setGrepColumnInclusionRule(null);
    }

    if (config.hasValue("grepinout"))
    {
      final InclusionRule grepRoutineColumnInclusionRule = config
        .getInclusionRule("grepinout");
      options.setGrepRoutineColumnInclusionRule(grepRoutineColumnInclusionRule);
      consumeOption("grepinout");
    }
    else
    {
      options.setGrepRoutineColumnInclusionRule(null);
    }

    if (config.hasValue("grepdef"))
    {
      final InclusionRule grepDefinitionInclusionRule = config
        .getInclusionRule("grepdef");
      options.setGrepDefinitionInclusionRule(grepDefinitionInclusionRule);
      consumeOption("grepdef");
    }
    else
    {
      options.setGrepDefinitionInclusionRule(null);
    }

    if (config.hasValue("hideemptytables"))
    {
      options.setHideEmptyTables(config
        .getBooleanValue("hideemptytables", true));
      consumeOption("hideemptytables");
    }

    if (config.hasValue("parents"))
    {
      final int parentTableFilterDepth = config.getIntegerValue("parents", 0);
      options.setParentTableFilterDepth(parentTableFilterDepth);
      consumeOption("parents");
    }
    else
    {
      options.setParentTableFilterDepth(0);
    }

    if (config.hasValue("children"))
    {
      final int childTableFilterDepth = config.getIntegerValue("children", 0);
      options.setChildTableFilterDepth(childTableFilterDepth);
      consumeOption("children");
    }
    else
    {
      options.setParentTableFilterDepth(0);
    }

    return options;
  }

  private void logOverride(final String inclusionRuleName,
                           final InclusionRule oldSchemaInclusionRule,
                           final InclusionRule schemaInclusionRule)
  {
    LOGGER.log(Level.INFO, String
      .format("Overriding %s inclusion rule from command-line, to %s, from %s",
              inclusionRuleName,
              schemaInclusionRule,
              oldSchemaInclusionRule));
  }

}
