/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2015, Sualeh Fatehi.
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

  final OutputOptions outputOptions;

  public OutputOptionsParser(final Config config)
  {
    super(config);
    normalizeOptionName("outputformat");
    normalizeOptionName("outputfile", "o");

    outputOptions = new OutputOptions(config);
  }

  @Override
  public OutputOptions getOptions()
  {
    final String outputFormatValue = config
      .getStringValue("outputformat", TextOutputFormat.text.getFormat());
    outputOptions.setOutputFormatValue(outputFormatValue);

    final String outputFileName = config.getStringValue("outputfile", null);
    final Path outputFile;
    if (isBlank(outputFileName))
    {
      outputFile = null;
    }
    else
    {
      outputFile = Paths.get(outputFileName);
      outputOptions.setOutputFile(outputFile);
    }

    return outputOptions;
  }

}
