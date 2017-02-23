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
package schemacrawler.server.mysql;


import static java.util.Objects.requireNonNull;
import static sf.util.Utility.isBlank;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import schemacrawler.schema.Column;

public class MySQLUtility
{

  private static Pattern enumPattern = Pattern.compile("enum\\((.*)\\)");

  public static List<String> getEnumValues(final Column column)
  {
    requireNonNull(column, "No column provided");
    final ArrayList<String> enumValues = new ArrayList<>();

    final String columnTypeString = column.getAttribute("COLUMN_TYPE");
    if (isBlank(columnTypeString))
    {
      return enumValues;
    }
    final Matcher matcher = enumPattern.matcher(columnTypeString);
    if (!matcher.matches())
    {
      return enumValues;
    }
    final String group = matcher.group(1);
    if (!isBlank(group))
    {
      final String[] enumValuesQuoted = group.split(",");
      for (final String enumValueQuoted: enumValuesQuoted)
      {
        if (!isBlank(enumValueQuoted) && enumValueQuoted.length() >= 2
            && enumValueQuoted.startsWith("'") && enumValueQuoted.endsWith("'"))
        {
          final String enumValue = enumValueQuoted
            .substring(1, enumValueQuoted.length() - 1);
          enumValues.add(enumValue);
        }
      }
    }
    return enumValues;
  }

  private MySQLUtility()
  {
    // Prevent instantiation
  }

}
