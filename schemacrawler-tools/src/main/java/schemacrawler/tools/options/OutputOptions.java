/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2018, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.tools.options;


import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import schemacrawler.schemacrawler.Options;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.iosource.CompressedFileInputResource;
import schemacrawler.tools.iosource.CompressedFileOutputResource;
import schemacrawler.tools.iosource.FileInputResource;
import schemacrawler.tools.iosource.FileOutputResource;
import schemacrawler.tools.iosource.InputResource;
import schemacrawler.tools.iosource.OutputResource;
import sf.util.ObjectToString;

/**
 * Contains output options.
 *
 * @author Sualeh Fatehi
 */
public final class OutputOptions
  implements Options
{

  private final OutputResource outputResource;
  private final InputResource inputResource;
  private final String outputFormatValue;
  private final Charset inputEncodingCharset;
  private final Charset outputEncodingCharset;

  OutputOptions(final InputResource inputResource,
                final Charset inputEncodingCharset,
                final OutputResource outputResource,
                final Charset outputEncodingCharset,
                final String outputFormatValue)
  {
    this.inputResource = requireNonNull(inputResource,
                                        "No input resource provided");
    this.inputEncodingCharset = requireNonNull(inputEncodingCharset,
                                               "No input encoding provided");
    this.outputResource = requireNonNull(outputResource,
                                         "No output resource provided");
    this.outputEncodingCharset = requireNonNull(outputEncodingCharset,
                                                "No output encoding provided");
    this.outputFormatValue = requireNonNull(outputFormatValue,
                                            "No output format value provided");
  }

  /**
   * Character encoding for input files, such as scripts and templates.
   */
  public Charset getInputCharset()
  {
    return inputEncodingCharset;
  }

  /**
   * Character encoding for output files.
   */
  public Charset getOutputCharset()
  {
    return outputEncodingCharset;
  }

  public Path getOutputFile()
  {
    final Path outputFile;
    if (outputResource instanceof FileOutputResource)
    {
      outputFile = ((FileOutputResource) outputResource).getOutputFile();
    }
    else if (outputResource instanceof CompressedFileOutputResource)
    {
      outputFile = ((CompressedFileOutputResource) outputResource)
        .getOutputFile();
    }
    else
    {
      // Create output file path
      outputFile = Paths.get(".",
                             String.format("schemacrawler-%s.%s",
                                           UUID.randomUUID(),
                                           outputFormatValue))
        .normalize().toAbsolutePath();
    }
    return outputFile;
  }

  /**
   * Gets the output format value.
   *
   * @return Output format value.
   */
  public String getOutputFormatValue()
  {
    return outputFormatValue;
  }

  /**
   * Gets the input reader. If the input resource is null, first set it
   * to a value based off the output format value.
   * 
   * @return Input reader
   * @throws IOException
   *         On an exception
   */
  public Reader openNewInputReader()
    throws IOException
  {
    return inputResource.openNewInputReader(inputEncodingCharset);
  }

  public Path getInputFile()
  {
    final Path inputFile;
    if (inputResource instanceof FileInputResource)
    {
      inputFile = ((FileInputResource) inputResource).getInputFile();
    }
    else if (inputResource instanceof CompressedFileInputResource)
    {
      inputFile = ((CompressedFileInputResource) inputResource).getInputFile();
    }
    else
    {
      // Create input file path
      inputFile = Paths.get(".",
                            String.format("schemacrawler-%s.%s",
                                          UUID.randomUUID(),
                                          outputFormatValue))
        .normalize().toAbsolutePath();
    }
    return inputFile;
  }

  /**
   * Gets the output reader. If the output resource is null, first set
   * it to console output.
   *
   * @return Output writer
   * @throws IOException
   *         On an exception
   */
  public Writer openNewOutputWriter()
    throws IOException
  {
    return openNewOutputWriter(false);
  }

  /**
   * Gets the output reader. If the output resource is null, first set
   * it to console output.
   *
   * @throws SchemaCrawlerException
   */
  public Writer openNewOutputWriter(final boolean appendOutput)
    throws IOException
  {
    return outputResource.openNewOutputWriter(getOutputCharset(), appendOutput);
  }

  @Override
  public String toString()
  {
    return ObjectToString.toString(this);
  }

  InputResource getInputResource()
  {
    return inputResource;
  }

  OutputResource getOutputResource()
  {
    return outputResource;
  }

}
