/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.plugin;

import static schemacrawler.plugin.EnumDataTypeInfo.EMPTY_ENUM_DATA_TYPE_INFO;

import java.sql.Connection;

import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnDataType;

@FunctionalInterface
public interface EnumDataTypeHelper {

  EnumDataTypeHelper NO_OP_ENUM_DATA_TYPE_HELPER =
      (column, columnDataType, connection) -> EMPTY_ENUM_DATA_TYPE_INFO;

  EnumDataTypeInfo getEnumDataTypeInfo(
      Column column, ColumnDataType columnDataType, Connection connection);
}
