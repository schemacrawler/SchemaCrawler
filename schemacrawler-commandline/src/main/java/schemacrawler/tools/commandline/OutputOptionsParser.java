/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2010, Sualeh Fatehi.
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


import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.options.OutputOptions;
import sf.util.CommandLineParser.BooleanOption;
import sf.util.CommandLineParser.Option;
import sf.util.CommandLineParser.StringOption;

/**
 * Parses the command line.
 * 
 * @author Sualeh Fatehi
 */
final class OutputOptionsParser
  extends BaseOptionsParser<OutputOptions>
{

  private final StringOption optionOutputFormat = new StringOption(Option.NO_SHORT_FORM,
                                                                   "outputformat",
                                                                   OutputFormat.text
                                                                     .toString());
  private final StringOption optionOutputFile = new StringOption(Option.NO_SHORT_FORM,
                                                                 "outputfile",
                                                                 "");
  private final BooleanOption optionNoInfo = new BooleanOption(Option.NO_SHORT_FORM,
                                                               "noinfo");

  OutputOptionsParser(final String[] args)
  {
    super(args);
  }

  @Override
  protected OutputOptions getOptions()
  {
    parse(new Option[] {
        optionOutputFormat, optionOutputFile, optionNoInfo, optionNoInfo
    });

    final String outputFormatValue = optionOutputFormat.getValue();
    final String outputFile = optionOutputFile.getValue();

    final OutputOptions outputOptions = new OutputOptions(outputFormatValue,
                                                          outputFile);
    outputOptions.setNoInfo(optionNoInfo.getValue());

    return outputOptions;
  }

}
