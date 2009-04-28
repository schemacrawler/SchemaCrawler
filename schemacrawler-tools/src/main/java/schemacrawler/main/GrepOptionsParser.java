/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2009, Sualeh Fatehi.
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

package schemacrawler.main;


import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.GrepOptions;
import schemacrawler.schemacrawler.InclusionRule;
import sf.util.CommandLineParser.BooleanOption;
import sf.util.CommandLineParser.Option;
import sf.util.CommandLineParser.StringOption;

/**
 * Parses the command line.
 * 
 * @author Sualeh Fatehi
 */
final class GrepOptionsParser
  extends BaseOptionsParser<GrepOptions>
{

  private final StringOption optionGrepColumns = new StringOption(Option.NO_SHORT_FORM,
                                                                  "grep-columns",
                                                                  InclusionRule.NONE);
  private final StringOption optionGrepProcedureColumns = new StringOption(Option.NO_SHORT_FORM,
                                                                           "grep-inout",
                                                                           InclusionRule.NONE);
  private final BooleanOption optionGrepInvertMatch = new BooleanOption('v',
                                                                        "invert-match");

  private final GrepOptions options;

  GrepOptionsParser(final String[] args, final Config config)
  {
    super(args);
    options = new GrepOptions(config);
  }

  @Override
  protected GrepOptions getOptions()
  {
    parse(new Option[] {
        optionGrepColumns, optionGrepProcedureColumns, optionGrepInvertMatch
    });

    if (optionGrepInvertMatch.isFound())
    {
      options.setGrepInvertMatch(optionGrepInvertMatch.getValue());
    }

    if (optionGrepColumns.isFound())
    {
      final InclusionRule grepColumnInclusionRule = new InclusionRule(optionGrepColumns
                                                                        .getValue(),
                                                                      InclusionRule.NONE);
      options.setGrepColumnInclusionRule(grepColumnInclusionRule);
    }

    if (optionGrepProcedureColumns.isFound())
    {
      final InclusionRule grepProcedureColumnInclusionRule = new InclusionRule(optionGrepProcedureColumns
                                                                                 .getValue(),
                                                                               InclusionRule.NONE);
      options
        .setGrepProcedureColumnInclusionRule(grepProcedureColumnInclusionRule);
    }

    return options;
  }

  @Override
  protected String getHelpResource()
  {
    return "/help/GrepOptions.readme.txt";
  }

}
