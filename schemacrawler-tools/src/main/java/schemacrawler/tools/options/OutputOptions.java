/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2014, Sualeh Fatehi.
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

package schemacrawler.tools.options;


import static sf.util.Utility.UTF8;
import static sf.util.Utility.isBlank;

import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.Options;
import sf.util.ObjectToString;

/**
 * Contains output options.
 *
 * @author Sualeh Fatehi
 */
public class OutputOptions
  implements Options
{

  private static final long serialVersionUID = 7018337388923813055L;

  private static final String SC_INPUT_ENCODING = "schemacrawler.encoding.input";
  private static final String SC_OUTPUT_ENCODING = "schemacrawler.encoding.output";

  private String outputFormatValue;

  private Path outputFile;
  private Writer writer;

  private Charset inputEncodingCharset;
  private Charset outputEncodingCharset;

  /**
   * Creates default OutputOptions.
   */
  public OutputOptions()
  {
    this(TextOutputFormat.text.getFormat());
  }

  public OutputOptions(final Config config)
  {
    this();

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
                                                     UTF8.name()));
    setOutputEncoding(configProperties.getStringValue(SC_OUTPUT_ENCODING,
                                                      UTF8.name()));
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
    this(outputFormat.getFormat(), outputFile);
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
    this(outputFormat.getFormat(), writer);
  }

  /**
   * Output options, given the type and the output to the console.
   *
   * @param outputFormatValue
   *        Type of output, which is dependent on the executor
   */
  public OutputOptions(final String outputFormatValue)
  {
    this(outputFormatValue, (Path) null);
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
    this.outputFormatValue = outputFormatValue;
    this.outputFile = outputFile;
    writer = null;
  }

  /**
   * Output options, given the type and the output filename.
   *
   * @param outputFormatValue
   *        Type of output, which is dependent on the executor
   * @param outputFile
   *        Output file
   */
  public OutputOptions(final String outputFormatValue, final String outputFile)
  {
    this.outputFormatValue = outputFormatValue;
    this.outputFile = Paths.get(outputFile);
    writer = null;
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
    this.outputFormatValue = outputFormatValue;
    outputFile = null;
    this.writer = writer;
  }

  /**
   * Character encoding for input files, such as scripts and templates.
   */
  public Charset getInputCharset()
  {
    if (inputEncodingCharset == null)
    {
      return UTF8;
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
      return UTF8;
    }
    else
    {
      return outputEncodingCharset;
    }
  }

  /**
   * Output file, which has previously been created.
   *
   * @return Output file
   */
  public Path getOutputFile()
  {
    return outputFile;
  }

  /**
   * Output format.
   *
   * @return Output format
   */
  public OutputFormat getOutputFormat()
  {
    final OutputFormat outputFormat = getTextOutputFormat();
    if (outputFormat == null)
    {
      return TextOutputFormat.text;
    }
    else
    {
      return outputFormat;
    }
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

  public Writer getWriter()
  {
    return writer;
  }

  /**
   * Whether a known output format has been specified.
   *
   * @return Has output format
   */
  public boolean hasOutputFormat()
  {
    return getTextOutputFormat() != null;
  }

  public boolean isConsoleOutput()
  {
    return outputFile == null && writer == null;
  }

  public boolean isFileOutput()
  {
    return outputFile != null && writer == null;
  }

  public void setInputEncoding(final Charset inputCharset)
  {
    if (inputCharset == null)
    {
      inputEncodingCharset = UTF8;
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
      inputEncodingCharset = UTF8;
    }
    else
    {
      inputEncodingCharset = Charset.forName(inputEncoding);
    }
  }

  public void setOutputEncoding(final Charset outputCharset)
  {
    if (outputCharset == null)
    {
      outputEncodingCharset = UTF8;
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
      outputEncodingCharset = UTF8;
    }
    else
    {
      outputEncodingCharset = Charset.forName(outputEncoding);
    }
  }

  /**
   * Sets the name of the output file.
   *
   * @param outputFileName
   *        Output file name.
   */
  public void setOutputFile(final Path outputFile)
  {
    this.outputFile = outputFile;
  }

  /**
   * Sets output format value.
   *
   * @param outputFormatValue
   *        Output format value
   */
  public void setOutputFormatValue(final String outputFormatValue)
  {
    this.outputFormatValue = Objects
      .requireNonNull(outputFormatValue, "Cannot use null value in a setter");
  }

  public void setWriter(final Writer writer)
  {
    this.writer = writer;
  }

  @Override
  public String toString()
  {
    return ObjectToString.toString(this);
  }

  private OutputFormat getTextOutputFormat()
  {
    TextOutputFormat outputFormat;
    try
    {
      outputFormat = TextOutputFormat.valueOf(outputFormatValue);
    }
    catch (final IllegalArgumentException e)
    {
      outputFormat = null;
    }
    return outputFormat;
  }

}
