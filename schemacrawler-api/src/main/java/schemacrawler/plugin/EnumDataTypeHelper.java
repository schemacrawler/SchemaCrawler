/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
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
