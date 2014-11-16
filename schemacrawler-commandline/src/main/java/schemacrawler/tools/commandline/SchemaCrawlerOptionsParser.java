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


import static sf.util.Utility.isBlank;

import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.InclusionRule;
import schemacrawler.schemacrawler.RegularExpressionExclusionRule;
import schemacrawler.schemacrawler.RegularExpressionInclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerCommandLineException;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevel;
import schemacrawler.tools.options.InfoLevel;
import sf.util.clparser.BooleanOption;
import sf.util.clparser.NumberOption;
import sf.util.clparser.StringOption;

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
    super(new StringOption("infolevel", "standard"),
          new StringOption("schemas", null),
          new StringOption("tabletypes", DEFAULT_TABLE_TYPES),
          new StringOption("tables", null),
          new StringOption("excludecolumns", null),
          new StringOption("synonyms", null),
          new StringOption("sequences", null),
          new StringOption("routinetypes", DEFAULT_ROUTINE_TYPES),
          new StringOption("routines", null),
          new StringOption("excludeinout", null),
          new StringOption("grepcolumns", null),
          new StringOption("grepinout", null),
          new StringOption("grepdef", null),
          new BooleanOption("invert-match"),
          new BooleanOption("only-matching"),
          new NumberOption("parents", 0),
          new NumberOption("children", 0));
    options = new SchemaCrawlerOptions(config);
  }

  @Override
  public SchemaCrawlerOptions getOptions()
    throws SchemaCrawlerException
  {
    if (hasOptionValue("infolevel"))
    {
      final String infoLevel = getStringValue("infolevel");
      try
      {
        final SchemaInfoLevel schemaInfoLevel = InfoLevel.valueOf(infoLevel)
          .getSchemaInfoLevel();
        options.setSchemaInfoLevel(schemaInfoLevel);
      }
      catch (final IllegalArgumentException e)
      {
        LOGGER.log(Level.INFO, "Unknown infolevel, " + infoLevel);
        options.setSchemaInfoLevel(SchemaInfoLevel.standard());
      }
    }
    else
    {
      throw new SchemaCrawlerCommandLineException("No infolevel specified");
    }

    if (hasOptionValue("schemas"))
    {
      final InclusionRule schemaInclusionRule = new RegularExpressionInclusionRule(getStringValue("schemas"));
      logOverride("schemas",
                  options.getSchemaInclusionRule(),
                  schemaInclusionRule);
      options.setSchemaInclusionRule(schemaInclusionRule);
    }

    if (hasOptionValue("tabletypes"))
    {
      final String tabletypes = getStringValue("tabletypes");
      if (!isBlank(tabletypes))
      {
        options.setTableTypesFromString(tabletypes);
      }
      else
      {
        options.setTableTypesFromString(null);
      }
    }

    if (hasOptionValue("tables"))
    {
      final InclusionRule tableInclusionRule = new RegularExpressionInclusionRule(getStringValue("tables"));
      logOverride("tables", options.getTableInclusionRule(), tableInclusionRule);
      options.setTableInclusionRule(tableInclusionRule);
    }
    if (hasOptionValue("excludecolumns"))
    {
      final InclusionRule columnInclusionRule = new RegularExpressionExclusionRule(getStringValue("excludecolumns"));
      logOverride("excludecolumns",
                  options.getColumnInclusionRule(),
                  columnInclusionRule);
      options.setColumnInclusionRule(columnInclusionRule);
    }

    if (hasOptionValue("routinetypes"))
    {
      options.setRoutineTypes(getStringValue("routinetypes"));
    }

    if (hasOptionValue("routines"))
    {
      final InclusionRule routineInclusionRule = new RegularExpressionInclusionRule(getStringValue("routines"));
      logOverride("routines",
                  options.getRoutineInclusionRule(),
                  routineInclusionRule);
      options.setRoutineInclusionRule(routineInclusionRule);
    }
    if (hasOptionValue("excludeinout"))
    {
      final InclusionRule routineColumnInclusionRule = new RegularExpressionExclusionRule(getStringValue("excludeinout"));
      logOverride("excludeinout",
                  options.getRoutineColumnInclusionRule(),
                  routineColumnInclusionRule);
      options.setRoutineColumnInclusionRule(routineColumnInclusionRule);
    }

    if (hasOptionValue("synonyms"))
    {
      final InclusionRule synonymInclusionRule = new RegularExpressionInclusionRule(getStringValue("synonyms"));
      logOverride("synonyms",
                  options.getSynonymInclusionRule(),
                  synonymInclusionRule);
      options.setSynonymInclusionRule(synonymInclusionRule);
    }

    if (hasOptionValue("sequences"))
    {
      final InclusionRule sequenceInclusionRule = new RegularExpressionInclusionRule(getStringValue("sequences"));
      logOverride("sequences",
                  options.getSequenceInclusionRule(),
                  sequenceInclusionRule);
      options.setSequenceInclusionRule(sequenceInclusionRule);
    }

    if (hasOptionValue("invert-match"))
    {
      options.setGrepInvertMatch(getBooleanValue("invert-match"));
    }

    if (hasOptionValue("only-matching"))
    {
      options.setGrepOnlyMatching(getBooleanValue("only-matching"));
    }

    if (hasOptionValue("grepcolumns"))
    {
      final InclusionRule grepColumnInclusionRule = new RegularExpressionInclusionRule(getStringValue("grepcolumns"));
      options.setGrepColumnInclusionRule(grepColumnInclusionRule);
    }
    else
    {
      options.setGrepColumnInclusionRule(null);
    }

    if (hasOptionValue("grepinout"))
    {
      final InclusionRule grepRoutineColumnInclusionRule = new RegularExpressionInclusionRule(getStringValue("grepinout"));
      options.setGrepRoutineColumnInclusionRule(grepRoutineColumnInclusionRule);
    }
    else
    {
      options.setGrepRoutineColumnInclusionRule(null);
    }

    if (hasOptionValue("grepdef"))
    {
      final InclusionRule grepDefinitionInclusionRule = new RegularExpressionInclusionRule(getStringValue("grepdef"));
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
