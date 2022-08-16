/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2022, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.server.postgresql;

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.database.DatabaseUtility.checkConnection;
import static us.fatehi.utility.database.DatabaseUtility.executeSql;
import static us.fatehi.utility.database.DatabaseUtility.readResultsVector;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.plugin.EnumDataTypeHelper;
import schemacrawler.plugin.EnumDataTypeInfo;
import schemacrawler.plugin.EnumDataTypeInfo.EnumDataTypeTypes;
import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnDataType;
import us.fatehi.utility.string.StringFormat;

public class PostgreSQLEnumDataTypeHelper implements EnumDataTypeHelper {
  private static final Logger LOGGER =
      Logger.getLogger(PostgreSQLEnumDataTypeHelper.class.getName());

  private static String constructEnumSql(final ColumnDataType columnDataType) {
    final String columnDataTypeName = columnDataType.getName();
    final String sql =
        String.format(
            "SELECT  \n"
                // + "  NULL AS TYPE_CATALOG,  \n"
                // + "  n.nspname AS TYPE_SCHEMA,  \n"
                // + "  t.typname AS TYPE_NAME,  \n"
                + "  e.enumlabel AS ENUM_LABEL  \n"
                + "FROM  \n"
                + "  pg_enum e  \n"
                + "  INNER JOIN pg_type t  \n"
                + "    ON e.enumtypid = t.oid  \n"
                + "  INNER JOIN pg_catalog.pg_namespace n  \n"
                + "    ON n.oid = t.typnamespace  \n"
                + "WHERE  \n"
                + "  t.typname = '%s'  \n"
                + "",
            columnDataTypeName);
    // NOTE: No check is made on the column data type schema
    return sql;
  }

  private static List<String> getEnumValues(
      final ColumnDataType columnDataType, final Connection connection) {
    requireNonNull(columnDataType, "No column provided");
    final String sql = constructEnumSql(columnDataType);
    try (final Statement statement = connection.createStatement(); ) {
      final ResultSet resultSet = executeSql(statement, sql);
      final List<String> enumValues = readResultsVector(resultSet);
      return enumValues;
    } catch (final SQLException e) {
      LOGGER.log(Level.WARNING, e, new StringFormat("Error executing SQL <%s>", sql));
    }
    return new ArrayList<>();
  }

  private final Set<ColumnDataType> visitedDataTypes;

  public PostgreSQLEnumDataTypeHelper() {
    this.visitedDataTypes = new HashSet<>();
  }

  @Override
  public EnumDataTypeInfo getEnumDataTypeInfo(
      final Column column, final ColumnDataType columnDataType, final Connection connection) {

    requireNonNull(columnDataType, "No column data type provided");

    if (visitedDataTypes.contains(columnDataType)) {
      final EnumDataTypeTypes enumType;
      if (columnDataType.isEnumerated()) {
        enumType = EnumDataTypeTypes.enumerated_data_type;
      } else {
        enumType = EnumDataTypeTypes.not_enumerated;
      }
      return new EnumDataTypeInfo(enumType, columnDataType.getEnumValues());
    }

    try {
      checkConnection(connection);
    } catch (final SQLException e) {
      LOGGER.log(Level.WARNING, "Could not obtain enumerated column values", e);
    }
    final List<String> enumValues = getEnumValues(columnDataType, connection);
    visitedDataTypes.add(columnDataType);

    final EnumDataTypeTypes enumType;
    if (enumValues.isEmpty()) {
      enumType = EnumDataTypeTypes.not_enumerated;
    } else {
      enumType = EnumDataTypeTypes.enumerated_data_type;
    }
    return new EnumDataTypeInfo(enumType, enumValues);
  }
}
