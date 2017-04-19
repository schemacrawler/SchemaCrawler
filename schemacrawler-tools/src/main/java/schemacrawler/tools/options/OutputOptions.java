/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
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


import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;
import static sf.util.Utility.isBlank;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.Options;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.iosource.ClasspathInputResource;
import schemacrawler.tools.iosource.CompressedFileInputResource;
import schemacrawler.tools.iosource.CompressedFileOutputResource;
import schemacrawler.tools.iosource.ConsoleOutputResource;
import schemacrawler.tools.iosource.FileInputResource;
import schemacrawler.tools.iosource.FileOutputResource;
import schemacrawler.tools.iosource.InputResource;
import schemacrawler.tools.iosource.OutputResource;
import schemacrawler.tools.iosource.WriterOutputResource;
import sf.util.ObjectToString;

/**
 * Contains output options.
 *
 * @author Sualeh Fatehi
 */
public class OutputOptions
  implements Options
{

  private static final String SCHEMACRAWLER_DATA = "schemacrawler.data";

  private static final long serialVersionUID = 7018337388923813055L;

  private static final String SC_INPUT_ENCODING = "schemacrawler.encoding.input";
  private static final String SC_OUTPUT_ENCODING = "schemacrawler.encoding.output";

  private static final SecureRandom random = new SecureRandom();
  private OutputResource outputResource;
  private InputResource inputResource;
  private String outputFormatValue;
  private Charset inputEncodingCharset;

  private Charset outputEncodingCharset;

  /**
   * Creates default OutputOptions.
   */
  public OutputOptions()
  {

  }

  public OutputOptions(final Config config)
  {
    final Config configProperties;
    if (config == null)
    {
      configProperties = new Config();
    }
    else
    {
      configProperties = config;
    }

    setInputEncoding(configProperties.getStringValue(SC_INPUT_ENCODING,
                                                     UTF_8.name()));
    setOutputEncoding(configProperties.getStringValue(SC_OUTPUT_ENCODING,
                                                      UTF_8.name()));
  }

  /**
   * Output options, given the type and the output filename.
   *
   * @param outputFormat
   *        Type of output, which is dependent on the executor
   * @param outputFile
   *        Output file
   */
  public OutputOptions(final OutputFormat outputFormat, final Path outputFile)
  {
    this(requireNonNull(outputFormat, "No output format provided").getFormat(),
         outputFile);
  }

  /**
   * Output options, given the type and the output filename.
   *
   * @param outputFormatValue
   *        Type of output, which is dependent on the executor
   * @param outputFile
   *        Output file
   */
  public OutputOptions(final OutputFormat outputFormat, final Writer writer)
  {
    this(requireNonNull(outputFormat, "No output format provided").getFormat(),
         writer);
  }

  /**
   * Output options, given the type and the output to the console.
   *
   * @param outputFormatValue
   *        Type of output, which is dependent on the executor
   */
  public OutputOptions(final String outputFormatValue)
  {
    this.outputFormatValue = requireNonNull(outputFormatValue,
                                            "No output format value provided");
    setConsoleOutput();
  }

  /**
   * Output options, given the type and the output filename.
   *
   * @param outputFormatValue
   *        Type of output, which is dependent on the executor
   * @param outputFile
   *        Output file
   */
  public OutputOptions(final String outputFormatValue, final Path outputFile)
  {
    this.outputFormatValue = requireNonNull(outputFormatValue,
                                            "No output format value provided");
    setOutputFile(outputFile);
  }

  /**
   * Output options, given the type and the output filename.
   *
   * @param outputFormatValue
   *        Type of output, which is dependent on the executor
   * @param outputFile
   *        Output file
   */
  public OutputOptions(final String outputFormatValue, final Writer writer)
  {
    this.outputFormatValue = requireNonNull(outputFormatValue,
                                            "No output format value provided");
    setWriter(writer);
  }

  public void forceCompressedOutputFile()
    throws IOException
  {
    if (!(outputResource instanceof CompressedFileOutputResource))
    {
      final Path outputFile = getOutputFile();
      outputResource = new CompressedFileOutputResource(outputFile,
                                                        SCHEMACRAWLER_DATA);
    }
  }

  public void forceOutputFile()
  {
    if (!(outputResource instanceof FileOutputResource))
    {
      final Path outputFile = getOutputFile();
      outputResource = new FileOutputResource(outputFile);
    }
  }

  /**
   * Character encoding for input files, such as scripts and templates.
   */
  public Charset getInputCharset()
  {
    if (inputEncodingCharset == null)
    {
      return UTF_8;
    }
    else
    {
      return inputEncodingCharset;
    }
  }

  /**
   * Character encoding for output files.
   */
  public Charset getOutputCharset()
  {
    if (outputEncodingCharset == null)
    {
      return UTF_8;
    }
    else
    {
      return outputEncodingCharset;
    }
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
      // Tacky hack for htmlx format
      final String extension;
      if ("htmlx".equals(outputFormatValue))
      {
        extension = "svg.html";
      }
      else
      {
        extension = outputFormatValue;
      }
      // Create output file path
      outputFile = Paths
        .get(".", String.format("sc.%s.%s", nextRandomString(), extension))
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
   * @throws SchemaCrawlerException
   */
  public Reader openNewInputReader()
    throws IOException
  {
    obtainInputResource();
    if (inputResource == null)
    {
      throw new IOException("Cannot read " + outputFormatValue);
    }
    return inputResource.openNewInputReader(getInputCharset());
  }

  /**
   * Gets the output reader. If the output resource is null, first set
   * it to console output.
   *
   * @throws SchemaCrawlerException
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
    obtainOutputResource();
    return outputResource.openNewOutputWriter(getOutputCharset(), appendOutput);
  }

  /**
   * Sets the name of the input file for compressed input. It is
   * important to note that the input encoding should be available at
   * this point.
   *
   * @param outputFileName
   *        Output file name.
   * @throws IOException
   *         When file cannot be read
   */
  public void setCompressedInputFile(final Path inputFile)
    throws IOException
  {
    requireNonNull(inputFile, "No input file provided");
    inputResource = new CompressedFileInputResource(inputFile,
                                                    SCHEMACRAWLER_DATA);
  }

  /**
   * Sets the name of the output file for compressed output. It is
   * important to note that the output encoding should be available at
   * this point.
   *
   * @param outputFileName
   *        Output file name.
   * @throws IOException
   */
  public void setCompressedOutputFile(final Path outputFile)
    throws IOException
  {
    requireNonNull(outputFile, "No output file provided");
    outputResource = new CompressedFileOutputResource(outputFile,
                                                      SCHEMACRAWLER_DATA);
  }

  public void setConsoleOutput()
  {
    outputResource = new ConsoleOutputResource();
  }

  public void setInputEncoding(final Charset inputCharset)
  {
    if (inputCharset == null)
    {
      inputEncodingCharset = UTF_8;
    }
    else
    {
      inputEncodingCharset = inputCharset;
    }
  }

  /**
   * Set character encoding for input files, such as scripts and
   * templates.
   *
   * @param inputEncoding
   *        Input encoding
   */
  public void setInputEncoding(final String inputEncoding)
  {
    if (isBlank(inputEncoding))
    {
      inputEncodingCharset = UTF_8;
    }
    else
    {
      inputEncodingCharset = Charset.forName(inputEncoding);
    }
  }

  /**
   * Sets the name of the input file. It is important to note that the
   * input encoding should be available at this point.
   *
   * @param inputFileName
   *        Input file name.
   * @throws IOException
   *         When file cannot be read
   */
  public void setInputFile(final Path inputFile)
    throws IOException
  {
    requireNonNull(inputFile, "No input file provided");
    inputResource = new FileInputResource(inputFile);
  }

  public void setInputResource(final InputResource inputResource)
  {
    this.inputResource = inputResource;
  }

  /**
   * Sets the name of the input resource, first from a file, failing
   * which from the classpath. It is important to note that the input
   * encoding should be available at this point.
   *
   * @param inputResourceName
   *        Input resource name, which could be a file path, or a
   *        classpath resource.
   * @throws IOException
   *         When the resource cannot be accessed
   */
  public void setInputResourceName(final String inputResourceName)
    throws IOException
  {
    requireNonNull(inputResourceName, "No input resource name provided");
    try
    {
      final Path filePath = Paths.get(inputResourceName);
      inputResource = new FileInputResource(filePath);
    }
    catch (final Exception e)
    {
      inputResource = new ClasspathInputResource(inputResourceName);
    }
  }

  public void setOutputEncoding(final Charset outputCharset)
  {
    if (outputCharset == null)
    {
      outputEncodingCharset = UTF_8;
    }
    else
    {
      outputEncodingCharset = outputCharset;
    }
  }

  /**
   * Set character encoding for output files.
   *
   * @param outputEncoding
   *        Output encoding
   */
  public void setOutputEncoding(final String outputEncoding)
  {
    if (isBlank(outputEncoding))
    {
      outputEncodingCharset = UTF_8;
    }
    else
    {
      outputEncodingCharset = Charset.forName(outputEncoding);
    }
  }

  /**
   * Sets the name of the output file. It is important to note that the
   * output encoding should be available at this point.
   *
   * @param outputFileName
   *        Output file name.
   */
  public void setOutputFile(final Path outputFile)
  {
    requireNonNull(outputFile, "No output file provided");
    outputResource = new FileOutputResource(outputFile);
  }

  /**
   * Sets output format value.
   *
   * @param outputFormatValue
   *        Output format value
   */
  public void setOutputFormatValue(final String outputFormatValue)
  {
    this.outputFormatValue = requireNonNull(outputFormatValue,
                                            "Cannot use null value in a setter");
  }

  public void setWriter(final Writer writer)
  {
    requireNonNull(writer, "No output writer provided");
    outputResource = new WriterOutputResource(writer);
  }

  @Override
  public String toString()
  {
    return ObjectToString.toString(this);
  }

  private String nextRandomString()
  {
    final int length = 8;
    return new BigInteger(length * 5, random).toString(32);
  }

  /**
   * Gets the input resource. If the input resource is null, first set
   * it to a value based off the output format value.
   */
  private void obtainInputResource()
  {
    if (inputResource == null)
    {
      try
      {
        setInputResourceName(outputFormatValue);
      }
      catch (final IOException e)
      {
        inputResource = null;
      }
    }
  }

  /**
   * Gets the output resource. If the output resource is null, first set
   * it to console output.
   */
  private void obtainOutputResource()
  {
    if (outputResource == null)
    {
      outputResource = new ConsoleOutputResource();
    }
  }

}
