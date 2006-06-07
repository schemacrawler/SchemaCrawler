/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2006, Sualeh Fatehi.
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

package schemacrawler.tools;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.BaseOptions;

/**
 * Contains output options.
 * 
 * @author sfatehi
 */
public final class OutputOptions
  extends BaseOptions
{

  private static final Logger LOGGER = Logger.getLogger(OutputOptions.class
    .getName());

  private static final long serialVersionUID = 7018337388923813055L;

  private String outputFormatValue;

  private File outputFile;
  private PrintWriter writer;

  private boolean appendOutput;

  private boolean noHeader;
  private boolean noFooter;
  private boolean noInfo;

  private OutputOptions()
  {
    this("", "");
  }

  /**
   * Output options, given the type and the output filename.
   * 
   * @param outputFormatValue
   *          Type of output, which is dependent on the executor
   * @param outputFilename
   *          Output filename
   */
  public OutputOptions(final String outputFormatValue,
                       final String outputFilename)
  {
    this.outputFormatValue = outputFormatValue;

    if (outputFilename == null || outputFilename.length() == 0)
    {
      this.outputFile = null;
    }
    else
    {
      this.outputFile = new File(outputFilename);
    }

    this.noHeader = true;
    this.noFooter = true;
    this.noInfo = true;
  }

  /**
   * Output format value.
   * 
   * @return Output format value
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
   * Whether the output gets appended.
   * 
   * @param appendOutput Whether the output gets appended
   */  
  public void setAppendOutput(final boolean appendOutput)
  {
    this.appendOutput = appendOutput;
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
   * Gets the output writer. The first time this method is called, it creates a
   * new output writer. Every subsequent time, it returns the same writer.
   * 
   * @return Writer
   */
  public synchronized PrintWriter getOutputWriter()
  {
    if (writer == null)
    {
      if (outputFile == null)
      {
        writer = new PrintWriter(System.out, true);
      }
      else
      {
        try
        {
          writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(
              this.outputFile, this.appendOutput), "UTF-8"), /* autoFlush = */
          true);
        }
        catch (final IOException e)
        {
          LOGGER.log(Level.WARNING, e.getMessage());
        }
      }
    }

    return writer;
  }

  /**
   * Output format.
   * 
   * @return Output format
   */
  public OutputFormat getOutputFormat()
  {
    return OutputFormat.valueOf(outputFormatValue);
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
   * Whether to print footers.
   * 
   * @param noFooter Whether to print footers
   */  
  public void setNoFooter(final boolean noFooter)
  {
    this.noFooter = noFooter;
  }

  /**
   * Whether to print headers.
   * 
   * @param noHeader Whether to print headers
   */   
  public void setNoHeader(final boolean noHeader)
  {
    this.noHeader = noHeader;
  }

  /**
   * Whether to print information.
   * 
   * @param noInfo Whether to print information
   */   
  public void setNoInfo(final boolean noInfo)
  {
    this.noInfo = noInfo;
  }

  /**
   * Clone this object.
   * 
   * @return Clone
   */
  public OutputOptions duplicate()
  {
    OutputOptions outputOptions = new OutputOptions();

    outputOptions.outputFormatValue = this.outputFormatValue;

    outputOptions.outputFile = this.outputFile;
    outputOptions.writer = this.writer;

    outputOptions.appendOutput = this.appendOutput;

    outputOptions.noHeader = this.noHeader;
    outputOptions.noFooter = this.noFooter;
    outputOptions.noInfo = this.noInfo;

    return outputOptions;
  }

  /**
   * {@inheritDoc}
   * 
   * @see Object#toString()
   */
  public String toString()
  {
    final StringBuffer buffer = new StringBuffer();
    buffer.append("OutputOptions[");
    buffer.append("outputFormat=").append(getOutputFormat());
    buffer.append("; outputFormatValue=").append(outputFormatValue);
    if (outputFile != null)
    {
      buffer.append("; outputFile=").append(outputFile.getAbsolutePath());
    }
    buffer.append("; appendOutput=").append(appendOutput);
    buffer.append("; noHeader=").append(noHeader);
    buffer.append("; noFooter=").append(noFooter);
    buffer.append("; noInfo=").append(noInfo);
    buffer.append("]");
    return buffer.toString();
  }

}
