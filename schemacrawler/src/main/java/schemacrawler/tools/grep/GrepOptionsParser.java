/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2007, Sualeh Fatehi.
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

package schemacrawler.tools.grep;


import schemacrawler.crawl.InclusionRule;
import schemacrawler.main.BaseCommandLineParser;
import sf.util.CommandLineParser;
import sf.util.CommandLineParser.BooleanOption;
import sf.util.CommandLineParser.Option;
import sf.util.CommandLineParser.StringOption;

final class GrepOptionsParser
  extends BaseCommandLineParser<GrepOptions>
{

  private final StringOption optionTables = new StringOption(CommandLineParser.Option.NO_SHORT_FORM,
                                                             "tables",
                                                             InclusionRule.INCLUDE_ALL);
  private final StringOption optionColumns = new StringOption(CommandLineParser.Option.NO_SHORT_FORM,
                                                              "columns",
                                                              InclusionRule.INCLUDE_ALL);
  private final BooleanOption optionInvertMatch = new BooleanOption('v',
                                                                    "invert-match");

  GrepOptionsParser(final String[] args)
  {
    super(args);
  }

  @Override
  protected GrepOptions getValue()
  {
    parse(new Option[] {
        optionTables, optionColumns, optionInvertMatch
    });

    final InclusionRule tableInclusionRule = new InclusionRule(optionTables
      .getValue(), InclusionRule.EXCLUDE_NONE);
    final InclusionRule columnInclusionRule = new InclusionRule(optionColumns
      .getValue(), InclusionRule.EXCLUDE_NONE);

    final GrepOptions options = new GrepOptions();
    options.setTableInclusionRule(tableInclusionRule);
    options.setColumnInclusionRule(columnInclusionRule);
    options.setInvertMatch(optionInvertMatch.getValue());

    return options;
  }

}
