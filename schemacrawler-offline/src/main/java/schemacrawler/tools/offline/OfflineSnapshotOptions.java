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

package schemacrawler.tools.offline;


import java.io.File;
import java.io.Reader;
import java.nio.charset.Charset;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.Options;
import sf.util.ObjectToString;
import sf.util.Utility;

/**
 * Contains input options.
 *
 * @author Sualeh Fatehi
 */
public final class OfflineSnapshotOptions
implements Options
{

  private static final long serialVersionUID = 5202680507264097856L;

  private static final String SC_INPUT_ENCODING = "schemacrawler.encoding.input";

  private String inputSource;
  private File inputFile;
  private Reader reader;
  private Charset inputCharset;

  public OfflineSnapshotOptions(final Config config)
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

    setInputEncoding(configProperties
                     .getStringValue(SC_INPUT_ENCODING, "UTF-8"));
  }

  /**
   * Input options, given the type and the input filename.
   *
   * @param inputFile
   *        Input file
   */
  public OfflineSnapshotOptions(final File inputFile)
  {
    inputSource = null;
    this.inputFile = inputFile;
    reader = null;
  }

  /**
   * Input options, given the type and the input filename.
   *
   * @param inputFile
   *        Input file
   */
  public OfflineSnapshotOptions(final Reader reader)
  {
    inputSource = null;
    inputFile = null;
    this.reader = reader;
  }

  /**
   * Input options, given the type and the input filename.
   *
   * @param inputSource
   *        Type of input, which is dependent on the executor
   */
  public OfflineSnapshotOptions(final String inputSource)
  {
    this.inputSource = inputSource;
    inputFile = null;
    reader = null;
  }

  /**
   * Character encoding for input files for offline snapshots.
   */
  public Charset getInputCharset()
  {
    if (inputCharset == null)
    {
      return Charset.forName("UTF-8");
    }
    else
    {
      return inputCharset;
    }
  }

  /**
   * Input file, which has previously been created.
   *
   * @return Input file
   */
  public File getInputFile()
  {
    return inputFile;
  }

  /**
   * Gets the input format value.
   *
   * @return Input format value.s
   */
  public String getInputSource()
  {
    return inputSource;
  }

  public Reader getReader()
  {
    return reader;
  }

  public boolean hasInputFile()
  {
    return inputFile != null;
  }

  public boolean hasReader()
  {
    return reader != null;
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
    if (Utility.isBlank(inputEncoding))
    {
      inputCharset = Charset.defaultCharset();
    }
    else
    {
      inputCharset = Charset.forName(inputEncoding);
    }
  }

  /**
   * Sets the name of the input file.
   *
   * @param inputFileName
   *        Input file name.
   */
  public void setInputFile(final File inputFile)
  {
    this.inputFile = inputFile;
  }

  /**
   * Sets input source.
   *
   * @param inputSource
   *        Input source
   */
  public void setInputSource(final String inputSource)
  {
    this.inputSource = inputSource;
  }

  public void setReader(final Reader reader)
  {
    this.reader = reader;
  }

  @Override
  public String toString()
  {
    return ObjectToString.toString(this);
  }

}
