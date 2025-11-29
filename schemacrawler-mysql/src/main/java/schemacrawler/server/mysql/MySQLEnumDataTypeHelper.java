/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.server.mysql;

import static java.util.Objects.requireNonNull;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static us.fatehi.utility.CollectionsUtility.splitList;
import static us.fatehi.utility.Utility.isBlank;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import schemacrawler.plugin.EnumDataTypeHelper;
import schemacrawler.plugin.EnumDataTypeInfo;
import schemacrawler.plugin.EnumDataTypeInfo.EnumDataTypeTypes;
import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnDataType;

public class MySQLEnumDataTypeHelper implements EnumDataTypeHelper {

  private static Pattern enumPattern = Pattern.compile("enum.*\\((.*)\\)", CASE_INSENSITIVE);

  private static List<String> getEnumValues(final Column column) {
    requireNonNull(column, "No column provided");
    final ArrayList<String> enumValues = new ArrayList<>();

    final String columnTypeString = column.getAttribute("COLUMN_TYPE");
    if (isBlank(columnTypeString)) {
      return enumValues;
    }
    final Matcher matcher = enumPattern.matcher(columnTypeString);
    if (!matcher.matches()) {
      return enumValues;
    }
    final String group = matcher.group(1);
    if (!isBlank(group)) {
      final String[] enumValuesQuoted = splitList(group);
      for (final String enumValueQuoted : enumValuesQuoted) {
        if (!isBlank(enumValueQuoted)
            && enumValueQuoted.length() >= 2
            && enumValueQuoted.startsWith("'")
            && enumValueQuoted.endsWith("'")) {
          final String enumValue = enumValueQuoted.substring(1, enumValueQuoted.length() - 1);
          enumValues.add(enumValue);
        }
      }
    }
    return enumValues;
  }

  @Override
  public EnumDataTypeInfo getEnumDataTypeInfo(
      final Column column, final ColumnDataType columnDataType, final Connection connection) {
    requireNonNull(column, "No column provided");
    final List<String> enumValues = getEnumValues(column);
    final EnumDataTypeTypes enumType;
    if (enumValues.isEmpty()) {
      enumType = EnumDataTypeTypes.not_enumerated;
    } else {
      enumType = EnumDataTypeTypes.enumerated_column;
    }
    return new EnumDataTypeInfo(enumType, enumValues);
  }
}
