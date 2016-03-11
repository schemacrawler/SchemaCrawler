/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2016, Sualeh Fatehi.
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
package schemacrawler.tools.lint;


public class LinterHelp
{

  public static String getLinterHelpText()
    throws Exception
  {
    final StringBuilder buffer = new StringBuilder(1024);

    buffer.append("--- Available Linters ---").append(System.lineSeparator())
      .append(System.lineSeparator());

    final LinterRegistry registry = new LinterRegistry();
    for (final String linterId: registry)
    {
      final Linter linter = registry.newLinter(linterId);

      buffer.append("Linter: ").append(linter.getLinterId())
        .append(System.lineSeparator());
      buffer.append(linter.getDescription()).append(System.lineSeparator());
    }

    return buffer.toString();
  }

  public static void main(final String[] args)
    throws Exception
  {
    System.out.println(getLinterHelpText());
  }

  private LinterHelp()
  {
    // Prevent instantiation
  }

}
