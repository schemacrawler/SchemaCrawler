/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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


import static sf.util.Utility.isBlank;

import java.util.List;
import java.util.logging.Level;

import schemacrawler.SchemaCrawlerLogger;
import sf.util.string.StringFormat;

/**
 * Enumeration for text format type.
 */
public enum TextOutputFormat
  implements OutputFormat
{

  text("Plain text format", "txt"),
  html("HyperText Markup Language (HTML) format"),
  tsv("Tab-separated values (TSV) format");

  private static final SchemaCrawlerLogger LOGGER =
    SchemaCrawlerLogger.getLogger(TextOutputFormat.class.getName());

  /**
   * Gets the value from the format.
   *
   * @param format
   *   Text output format.
   * @return TextOutputFormat
   */
  public static TextOutputFormat fromFormat(final String format)
  {
    final TextOutputFormat outputFormat = fromFormatOrNull(format);
    if (outputFormat == null)
    {
      LOGGER.log(Level.CONFIG,
                 new StringFormat("Unknown format <%s>, using default",
                                  format));
      return text;
    }
    else
    {
      return outputFormat;
    }
  }

  private static TextOutputFormat fromFormatOrNull(final String format)
  {
    if (isBlank(format))
    {
      return null;
    }
    for (final TextOutputFormat outputFormat : TextOutputFormat.values())
    {
      if (outputFormat.outputFormatState.isSupportedFormat(format))
      {
        return outputFormat;
      }
    }
    return null;
  }

  /**
   * Checks if the value of the format is supported.
   *
   * @return True if the format is a text output format
   */
  public static boolean isSupportedFormat(final String format)
  {
    return fromFormatOrNull(format) != null;
  }

  private final OutputFormatState outputFormatState;

  private TextOutputFormat(final String description)
  {
    outputFormatState = new OutputFormatState(name(), description);
  }

  private TextOutputFormat(final String description,
                           final String... additionalFormatSpecifiers)
  {
    outputFormatState =
      new OutputFormatState(name(), description, additionalFormatSpecifiers);
  }

  @Override
  public String getDescription()
  {
    return outputFormatState.getDescription();
  }

  @Override
  public String getFormat()
  {
    return outputFormatState.getFormat();
  }

  @Override
  public List<String> getFormats()
  {
    return outputFormatState.getFormats();
  }

  @Override
  public String toString()
  {
    return outputFormatState.toString();
  }

}
