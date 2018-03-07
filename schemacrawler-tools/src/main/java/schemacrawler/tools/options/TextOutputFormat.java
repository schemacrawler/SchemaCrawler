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


import static sf.util.Utility.isBlank;

import java.util.ArrayList;
import java.util.List;

/**
 * Enumeration for text format type.
 */
public enum TextOutputFormat
  implements
  OutputFormat
{

 text("Plain text format", "txt"),
 html("HyperText Markup Language (HTML) format"),
 csv("Comma-separated values (CSV) format"),
 tsv("Tab-separated values (TSV) format"),
 json("JavaScript Object Notation (JSON) format"),;

  public static boolean isTextOutputFormat(final String format)
  {
    return fromFormatOrNull(format) != null;
  }

  public static TextOutputFormat valueOfFromString(final String format)
  {
    final TextOutputFormat outputFormat = fromFormatOrNull(format);
    if (outputFormat == null)
    {
      return TextOutputFormat.text;
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
    final String comparableFormat = format.toLowerCase();
    for (final TextOutputFormat textFormat: TextOutputFormat.values())
    {
      if (textFormat.formatSpecifiers.contains(comparableFormat))
      {
        return textFormat;
      }
    }
    return null;
  }

  private final String description;
  final List<String> formatSpecifiers;

  private TextOutputFormat(final String description,
                           final String... additionalFormatSpecifiers)
  {
    this.description = description;

    formatSpecifiers = new ArrayList<>();
    formatSpecifiers.add(name());
    if (additionalFormatSpecifiers != null)
    {
      for (final String additionalFormatSpecifier: additionalFormatSpecifiers)
      {
        formatSpecifiers.add(additionalFormatSpecifier);
      }
    }
  }

  @Override
  public String getDescription()
  {
    return description;
  }

  @Override
  public String getFormat()
  {
    return name();
  }

  @Override
  public String toString()
  {
    return String.format("%s - %s", getFormat(), description);
  }

}
