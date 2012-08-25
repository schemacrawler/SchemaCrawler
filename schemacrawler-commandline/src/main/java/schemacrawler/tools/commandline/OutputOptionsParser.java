/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2012, Sualeh Fatehi.
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


import java.io.File;

import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.options.OutputOptions;
import sf.util.Utility;
import sf.util.clparser.StringOption;

/**
 * Parses the command line.
 * 
 * @author Sualeh Fatehi
 */
final class OutputOptionsParser
  extends BaseOptionsParser<OutputOptions>
{

  OutputOptionsParser()
  {
    super(new StringOption("outputformat", OutputFormat.text.toString()),
          new StringOption('o', "outputfile", ""));
  }

  @Override
  protected OutputOptions getOptions()
  {
    final String outputFormatValue = getStringValue("outputformat");
    final String outputFileName = getStringValue("outputfile");

    final File outputFile;
    if (Utility.isBlank(outputFileName))
    {
      outputFile = null;
    }
    else
    {
      outputFile = new File(outputFileName);
    }
    final OutputOptions outputOptions = new OutputOptions(outputFormatValue,
                                                          outputFile);

    return outputOptions;
  }

}
