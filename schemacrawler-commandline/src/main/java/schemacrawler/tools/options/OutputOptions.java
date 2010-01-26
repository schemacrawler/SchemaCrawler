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

package schemacrawler.tools.options;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schemacrawler.Options;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import sf.util.Utility;

/**
 * Contains output options.
 * 
 * @author Sualeh Fatehi
 */
public final class OutputOptions
  implements Options
{

  private static final long serialVersionUID = 7018337388923813055L;

  private static final Logger LOGGER = Logger.getLogger(OutputOptions.class
    .getName());

  private String outputFormatValue;
  private File outputFile;

  private boolean appendOutput;

  private boolean noHeader;
  private boolean noFooter;
  private boolean noInfo;

  /**
   * Creates default OutputOptions.
   */
  public OutputOptions()
  {
    this(OutputFormat.text.name(), null);
  }

  /**
   * Output options, given the type and the output filename.
   * 
   * @param outputFormat
   *        Type of output, which is dependent on the executor
   * @param outputFilename
   *        Output filename
   */
  public OutputOptions(final OutputFormat outputFormat,
                       final String outputFilename)
  {
    if (outputFormat == null)
    {
      throw new IllegalArgumentException("No output format provided");
    }
    outputFormatValue = outputFormat.name();

    if (!sf.util.Utility.isBlank(outputFilename))
    {
      outputFile = new File(outputFilename);
    }

    noHeader = true;
    noFooter = true;
    noInfo = true;
  }

  /**
   * Output options, given the type and the output filename.
   * 
   * @param outputFormatValue
   *        Type of output, which is dependent on the executor
   * @param outputFilename
   *        Output filename
   */
  public OutputOptions(final String outputFormatValue,
                       final String outputFilename)
  {
    this.outputFormatValue = outputFormatValue;

    if (!sf.util.Utility.isBlank(outputFilename))
    {
      outputFile = new File(outputFilename);
    }

    noHeader = true;
    noFooter = true;
    noInfo = true;
  }

  /**
   * Close the output writer.
   * 
   * @param writer
   *        Output writer
   */
  public void closeOutputWriter(final Writer writer)
  {
    if (writer != null)
    {
      try
      {
        writer.flush();
      }
      catch (final IOException e)
      {
        LOGGER.log(Level.WARNING, "Exception flushing output writer", e);
      }
    }

    if (outputFile != null)
    {
      if (writer != null)
      {
        try
        {
          writer.close();
          LOGGER.log(Level.INFO, "Closed output writer to file, "
                                 + outputFile.getAbsolutePath());
        }
        catch (final IOException e)
        {
          LOGGER.log(Level.WARNING, "Exception closing output writer", e);
        }
      }
    }
    else
    {
      LOGGER.log(Level.INFO,
                 "Not closing output writer, since output is to console");
    }
  }

  /**
   * Clone this object.
   * 
   * @return Clone
   */
  public OutputOptions duplicate()
  {
    final OutputOptions outputOptions = new OutputOptions();

    outputOptions.outputFormatValue = outputFormatValue;

    outputOptions.outputFile = outputFile;

    outputOptions.appendOutput = appendOutput;

    outputOptions.noHeader = noHeader;
    outputOptions.noFooter = noFooter;
    outputOptions.noInfo = noInfo;

    return outputOptions;
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

  /**
   * Whether the output gets appended.
   * 
   * @return Whether the output gets appended
   */
  public boolean isAppendOutput()
  {
    return appendOutput;
  }

  /**
   * Whether to print footers.
   * 
   * @return Whether to print footers
   */
  public boolean isNoFooter()
  {
    return noFooter;
  }

  /**
   * Whether to print headers.
   * 
   * @return Whether to print headers
   */
  public boolean isNoHeader()
  {
    return noHeader;
  }

  /**
   * Whether to print information.
   * 
   * @return Whether to print information
   */
  public boolean isNoInfo()
  {
    return noInfo;
  }

  /**
   * Opens the output writer.
   * 
   * @return Writer
   * @throws IOException
   *         On an exception
   */
  public PrintWriter openOutputWriter()
    throws SchemaCrawlerException
  {
    final PrintWriter writer;
    try
    {
      if (outputFile == null)
      {
        writer = new PrintWriter(System.out, /* autoFlush = */true);
        LOGGER.log(Level.INFO, "Opened output writer to console");
      }
      else
      {
        final FileWriter fileWriter = new FileWriter(outputFile, appendOutput);
        writer = new PrintWriter(new BufferedWriter(fileWriter), /*
                                                                  * autoFlush
                                                                  * =
                                                                  */true);
        LOGGER.log(Level.INFO, "Opened output writer to file, "
                               + outputFile.getAbsolutePath());
      }
    }
    catch (final Exception e)
    {
      throw new SchemaCrawlerException("Could not obtain output writer", e);
    }
    return writer;
  }

  /**
   * Whether the output gets appended.
   * 
   * @param appendOutput
   *        Whether the output gets appended
   */
  public void setAppendOutput(final boolean appendOutput)
  {
    this.appendOutput = appendOutput;
  }

  /**
   * Whether to print footers.
   * 
   * @param noFooter
   *        Whether to print footers
   */
  public void setNoFooter(final boolean noFooter)
  {
    this.noFooter = noFooter;
  }

  /**
   * Whether to print headers.
   * 
   * @param noHeader
   *        Whether to print headers
   */
  public void setNoHeader(final boolean noHeader)
  {
    this.noHeader = noHeader;
  }

  /**
   * Whether to print information.
   * 
   * @param noInfo
   *        Whether to print information
   */
  public void setNoInfo(final boolean noInfo)
  {
    this.noInfo = noInfo;
  }

  /**
   * Sets the name of the output file.
   * 
   * @param outputFileName
   *        Output file name.
   */
  public void setOutputFileName(final String outputFileName)
  {
    if (Utility.isBlank(outputFileName))
    {
      throw new IllegalArgumentException("Cannot set null output file name");
    }
    outputFile = new File(outputFileName);
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

}
