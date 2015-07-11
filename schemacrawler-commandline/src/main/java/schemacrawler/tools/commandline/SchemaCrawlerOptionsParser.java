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
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
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

  private final SchemaCrawlerOptionsBuilder optionsBuilder;

  public SchemaCrawlerOptionsParser(final Config config)
  {
    super(config);
    normalizeOptionName("title");
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

    optionsBuilder = new SchemaCrawlerOptionsBuilder().fromConfig(config);
  }

  @Override
  public SchemaCrawlerOptions getOptions()
    throws SchemaCrawlerException
  {
    if (config.hasValue("title"))
    {
      optionsBuilder.title(config.getStringValue("title", ""));
      consumeOption("title");
    }

    if (config.hasValue("infolevel"))
    {
      final String infoLevel = config.getStringValue("infolevel", "standard");
      final SchemaInfoLevel schemaInfoLevel = InfoLevel
        .valueOfFromString(infoLevel).getSchemaInfoLevel();
      optionsBuilder.schemaInfoLevel(schemaInfoLevel);
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
                  optionsBuilder.getSchemaInclusionRule(),
                  schemaInclusionRule);
      optionsBuilder.includeSchemas(schemaInclusionRule);
      consumeOption("schemas");
    }

    if (config.hasValue("tabletypes"))
    {
      final String tabletypes = config.getStringValue("tabletypes",
                                                      DEFAULT_TABLE_TYPES);
      if (!isBlank(tabletypes))
      {
        optionsBuilder.tableTypes(tabletypes);
      }
      else
      {
        optionsBuilder.tableTypes((String) null);
      }
      consumeOption("tabletypes");
    }

    if (config.hasValue("tables"))
    {
      final InclusionRule tableInclusionRule = config
        .getInclusionRule("tables");
      logOverride("tables",
                  optionsBuilder.getTableInclusionRule(),
                  tableInclusionRule);
      optionsBuilder.includeTables(tableInclusionRule);
      consumeOption("tables");
    }
    if (config.hasValue("excludecolumns"))
    {
      final InclusionRule columnInclusionRule = config
        .getExclusionRule("excludecolumns");
      logOverride("excludecolumns",
                  optionsBuilder.getColumnInclusionRule(),
                  columnInclusionRule);
      optionsBuilder.includeColumns(columnInclusionRule);
      consumeOption("excludecolumns");
    }

    if (config.hasValue("routinetypes"))
    {
      optionsBuilder.routineTypes(config.getStringValue("routinetypes",
                                                        DEFAULT_ROUTINE_TYPES));
      consumeOption("routinetypes");
    }

    if (config.hasValue("routines"))
    {
      final InclusionRule routineInclusionRule = config
        .getInclusionRule("routines");
      logOverride("routines",
                  optionsBuilder.getRoutineInclusionRule(),
                  routineInclusionRule);
      optionsBuilder.includeRoutines(routineInclusionRule);
      consumeOption("routines");
    }
    if (config.hasValue("excludeinout"))
    {
      final InclusionRule routineColumnInclusionRule = config
        .getExclusionRule("excludeinout");
      logOverride("excludeinout",
                  optionsBuilder.getRoutineColumnInclusionRule(),
                  routineColumnInclusionRule);
      optionsBuilder.includeRoutineColumns(routineColumnInclusionRule);
      consumeOption("excludeinout");
    }

    if (config.hasValue("synonyms"))
    {
      final InclusionRule synonymInclusionRule = config
        .getInclusionRule("synonyms");
      logOverride("synonyms",
                  optionsBuilder.getSynonymInclusionRule(),
                  synonymInclusionRule);
      optionsBuilder.includeSynonyms(synonymInclusionRule);
      consumeOption("synonyms");
    }

    if (config.hasValue("sequences"))
    {
      final InclusionRule sequenceInclusionRule = config
        .getInclusionRule("sequences");
      logOverride("sequences",
                  optionsBuilder.getSequenceInclusionRule(),
                  sequenceInclusionRule);
      optionsBuilder.includeSequences(sequenceInclusionRule);
      consumeOption("sequences");
    }

    if (config.hasValue("invert-match"))
    {
      optionsBuilder.setGrepInvertMatch(config.getBooleanValue("invert-match",
                                                               true));
      consumeOption("invert-match");
    }

    if (config.hasValue("only-matching"))
    {
      optionsBuilder.setGrepOnlyMatching(config
        .getBooleanValue("only-matching", true));
      consumeOption("only-matching");
    }

    if (config.hasValue("grepcolumns"))
    {
      final InclusionRule grepColumnInclusionRule = config
        .getInclusionRule("grepcolumns");
      optionsBuilder.includeGreppedColumns(grepColumnInclusionRule);
      consumeOption("grepcolumns");
    }
    else
    {
      optionsBuilder.includeGreppedColumns(null);
    }

    if (config.hasValue("grepinout"))
    {
      final InclusionRule grepRoutineColumnInclusionRule = config
        .getInclusionRule("grepinout");
      optionsBuilder
        .setGrepRoutineColumnInclusionRule(grepRoutineColumnInclusionRule);
      consumeOption("grepinout");
    }
    else
    {
      optionsBuilder.setGrepRoutineColumnInclusionRule(null);
    }

    if (config.hasValue("grepdef"))
    {
      final InclusionRule grepDefinitionInclusionRule = config
        .getInclusionRule("grepdef");
      optionsBuilder.includeGreppedDefinitions(grepDefinitionInclusionRule);
      consumeOption("grepdef");
    }
    else
    {
      optionsBuilder.includeGreppedDefinitions(null);
    }

    if (config.hasValue("hideemptytables"))
    {
      final boolean hideEmptyTables = config.getBooleanValue("hideemptytables",
                                                             true);
      if (hideEmptyTables)
      {
        optionsBuilder.hideEmptyTables();
      }
      consumeOption("hideemptytables");
    }

    if (config.hasValue("parents"))
    {
      final int parentTableFilterDepth = config.getIntegerValue("parents", 0);
      optionsBuilder.parentTableFilterDepth(parentTableFilterDepth);
      consumeOption("parents");
    }
    else
    {
      optionsBuilder.parentTableFilterDepth(0);
    }

    if (config.hasValue("children"))
    {
      final int childTableFilterDepth = config.getIntegerValue("children", 0);
      optionsBuilder.childTableFilterDepth(childTableFilterDepth);
      consumeOption("children");
    }
    else
    {
      optionsBuilder.childTableFilterDepth(0);
    }

    return optionsBuilder.toOptions();
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
