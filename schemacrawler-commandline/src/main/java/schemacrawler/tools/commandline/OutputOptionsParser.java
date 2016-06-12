/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package schemacrawler.tools.commandline;


import static sf.util.Utility.isBlank;

import java.nio.file.Path;
import java.nio.file.Paths;

import schemacrawler.schemacrawler.Config;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.TextOutputFormat;

/**
 * Parses the command-line.
 *
 * @author Sualeh Fatehi
 */
public final class OutputOptionsParser
  extends BaseOptionsParser<OutputOptions>
{

  private static final String OUTPUT_FILE = "outputfile";
  private static final String OUTPUT_FORMAT = "outputformat";

  final OutputOptions outputOptions;

  public OutputOptionsParser(final Config config)
  {
    super(config);
    normalizeOptionName(OUTPUT_FORMAT);
    normalizeOptionName(OUTPUT_FILE, "o");

    outputOptions = new OutputOptions(config);
  }

  @Override
  public OutputOptions getOptions()
  {
    final String outputFormatValue = config
      .getStringValue(OUTPUT_FORMAT, TextOutputFormat.text.getFormat());
    consumeOption(OUTPUT_FORMAT);
    outputOptions.setOutputFormatValue(outputFormatValue);

    final String outputFileName = config.getStringValue(OUTPUT_FILE, null);
    consumeOption(OUTPUT_FILE);
    if (!isBlank(outputFileName))
    {
      final Path outputFile = Paths.get(outputFileName).toAbsolutePath();
      outputOptions.setOutputFile(outputFile);
    }

    return outputOptions;
  }

}
