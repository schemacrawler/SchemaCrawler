/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi <sualeh@hotmail.com>.
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


/**
 * Enumeration for text format type.
 */
public enum TextOutputFormat
  implements OutputFormat
{

 text("Plain text format"),
 html("HyperText Markup Language (HTML) format"),
 csv("Comma-separated values (CSV) format"),
 tsv("Tab-separated values (TSV) format"),
 json("JavaScript Object Notation (JSON) format"),;

  public static boolean isTextOutputFormat(final String format)
  {
    try
    {
      TextOutputFormat.valueOf(format);
      return true;
    }
    catch (final IllegalArgumentException | NullPointerException e)
    {
      return false;
    }
  }

  public static TextOutputFormat valueOfFromString(final String format)
  {
    TextOutputFormat outputFormat;
    try
    {
      outputFormat = TextOutputFormat.valueOf(format);
    }
    catch (final IllegalArgumentException | NullPointerException e)
    {
      outputFormat = text;
    }
    return outputFormat;
  }

  private final String description;

  private TextOutputFormat(final String description)
  {
    this.description = description;
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
