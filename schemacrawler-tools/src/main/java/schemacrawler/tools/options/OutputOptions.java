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

package schemacrawler.tools.options;


import java.io.File;
import java.io.Writer;

import schemacrawler.schemacrawler.Options;
import sf.util.ObjectToString;

/**
 * Contains output options.
 * 
 * @author Sualeh Fatehi
 */
public final class OutputOptions
  implements Options
{

  private static final long serialVersionUID = 7018337388923813055L;

  private String outputFormatValue;
  private File outputFile;
  private Writer writer;

  /**
   * Creates default OutputOptions.
   */
  public OutputOptions()
  {
    this(OutputFormat.text.name());
  }

  /**
   * Output options, given the type and the output to the console.
   * 
   * @param outputFormatValue
   *        Type of output, which is dependent on the executor
   */
  public OutputOptions(final String outputFormatValue)
  {
    this.outputFormatValue = outputFormatValue;
    outputFile = null;
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
  public OutputOptions(final String outputFormatValue, final File outputFile)
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
  public OutputOptions(final String outputFormatValue, final Writer writer)
  {
    this.outputFormatValue = outputFormatValue;
    outputFile = null;
    this.writer = writer;
  }

  /**
   * Output file, which has previously been created.
   * 
   * @return Output file
   */
  public File getOutputFile()
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
    OutputFormat outputFormat;
    try
    {
      outputFormat = OutputFormat.valueOf(outputFormatValue);
    }
    catch (final IllegalArgumentException e)
    {
      outputFormat = OutputFormat.text;
    }
    return outputFormat;
  }

  /**
   * Gets the output format value.
   * 
   * @return Output format value.s
   */
  public String getOutputFormatValue()
  {
    return outputFormatValue;
  }

  public Writer getWriter()
  {
    return writer;
  }

  public boolean isConsoleOutput()
  {
    return outputFile == null && writer == null;
  }

  public boolean isFileOutput()
  {
    return outputFile != null && writer == null;
  }

  /**
   * Sets the name of the output file.
   * 
   * @param outputFileName
   *        Output file name.
   */
  public void setOutputFile(final File outputFile)
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
    if (outputFormatValue == null)
    {
      throw new IllegalArgumentException("Cannot use null value in a setter");
    }
    this.outputFormatValue = outputFormatValue;
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

}
