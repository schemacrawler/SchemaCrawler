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


import schemacrawler.schemacrawler.InclusionRule;
import schemacrawler.tools.grep.GrepOptions;
import sf.util.CommandLineParser.BooleanOption;
import sf.util.CommandLineParser.Option;
import sf.util.CommandLineParser.StringOption;

final class GrepOptionsParser
  extends BaseCommandLineParser<GrepOptions>
{

  private final StringOption optionTables = new StringOption(Option.NO_SHORT_FORM,
                                                             "tables",
                                                             InclusionRule.INCLUDE_ALL);
  private final StringOption optionTableColumns = new StringOption(Option.NO_SHORT_FORM,
                                                                   "columns",
                                                                   InclusionRule.INCLUDE_ALL);
  private final StringOption optionProcedures = new StringOption(Option.NO_SHORT_FORM,
                                                                 "procedures",
                                                                 InclusionRule.EXCLUDE_ALL);
  private final StringOption optionProcedureColumns = new StringOption(Option.NO_SHORT_FORM,
                                                                       "inout",
                                                                       InclusionRule.EXCLUDE_ALL);
  private final StringOption optionDefinitionText = new StringOption(Option.NO_SHORT_FORM,
                                                                     "definition",
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
        optionTables,
        optionTableColumns,
        optionProcedures,
        optionProcedureColumns,
        optionDefinitionText,
        optionInvertMatch
    });

    final InclusionRule tableInclusionRule = new InclusionRule(optionTables
      .getValue(), InclusionRule.EXCLUDE_ALL);
    final InclusionRule tableColumnInclusionRule = new InclusionRule(optionTableColumns
                                                                       .getValue(),
                                                                     InclusionRule.EXCLUDE_ALL);
    final InclusionRule procedureInclusionRule = new InclusionRule(optionProcedures
                                                                     .getValue(),
                                                                   InclusionRule.EXCLUDE_ALL);
    final InclusionRule procedureColumnInclusionRule = new InclusionRule(optionProcedureColumns
                                                                           .getValue(),
                                                                         InclusionRule.EXCLUDE_ALL);
    final InclusionRule definitionTextInclusionRule = new InclusionRule(optionDefinitionText
                                                                          .getValue(),
                                                                        InclusionRule.EXCLUDE_ALL);

    final GrepOptions options = new GrepOptions();
    options.setTableInclusionRule(tableInclusionRule);
    options.setTableColumnInclusionRule(tableColumnInclusionRule);
    options.setProcedureInclusionRule(procedureInclusionRule);
    options.setProcedureColumnInclusionRule(procedureColumnInclusionRule);
    options.setDefinitionTextInclusionRule(definitionTextInclusionRule);
    options.setInvertMatch(optionInvertMatch.getValue());

    return options;
  }

}
