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
package schemacrawler.schema;

public enum JavaSqlTypeGroup {
  unknown,
  binary,
  bit,
  character,
  id,
  integer,
  real,
  reference,
  temporal,
  url,
  xml,
  large_object,
  object;

  public static JavaSqlTypeGroup valueOf(final int type) {

    final JavaSqlTypeGroup typeGroup;
    switch (type) {
      case java.sql.Types.ARRAY:
      case java.sql.Types.DISTINCT:
      case java.sql.Types.JAVA_OBJECT:
      case java.sql.Types.OTHER:
      case java.sql.Types.STRUCT:
        typeGroup = JavaSqlTypeGroup.object;
        break;
      case java.sql.Types.BINARY:
      case java.sql.Types.LONGVARBINARY:
      case java.sql.Types.VARBINARY:
        typeGroup = JavaSqlTypeGroup.binary;
        break;
      case java.sql.Types.BIT:
      case java.sql.Types.BOOLEAN:
        typeGroup = JavaSqlTypeGroup.bit;
        break;
      case java.sql.Types.CHAR:
      case java.sql.Types.LONGNVARCHAR:
      case java.sql.Types.LONGVARCHAR:
      case java.sql.Types.NCHAR:
      case java.sql.Types.NVARCHAR:
      case java.sql.Types.VARCHAR:
        typeGroup = JavaSqlTypeGroup.character;
        break;
      case java.sql.Types.ROWID:
        typeGroup = JavaSqlTypeGroup.id;
        break;
      case java.sql.Types.BIGINT:
      case java.sql.Types.INTEGER:
      case java.sql.Types.SMALLINT:
      case java.sql.Types.TINYINT:
        typeGroup = JavaSqlTypeGroup.integer;
        break;
      case java.sql.Types.BLOB:
      case java.sql.Types.CLOB:
      case java.sql.Types.NCLOB:
        typeGroup = JavaSqlTypeGroup.large_object;
        break;
      case java.sql.Types.DECIMAL:
      case java.sql.Types.DOUBLE:
      case java.sql.Types.FLOAT:
      case java.sql.Types.NUMERIC:
      case java.sql.Types.REAL:
        typeGroup = JavaSqlTypeGroup.real;
        break;
      case java.sql.Types.REF:
      case java.sql.Types.REF_CURSOR:
        typeGroup = JavaSqlTypeGroup.reference;
        break;
      case java.sql.Types.DATE:
      case java.sql.Types.TIME:
      case java.sql.Types.TIMESTAMP:
      case java.sql.Types.TIMESTAMP_WITH_TIMEZONE:
      case java.sql.Types.TIME_WITH_TIMEZONE:
        typeGroup = JavaSqlTypeGroup.temporal;
        break;
      case java.sql.Types.DATALINK:
        typeGroup = JavaSqlTypeGroup.url;
        break;
      case java.sql.Types.SQLXML:
        typeGroup = JavaSqlTypeGroup.xml;
        break;
      default:
        typeGroup = JavaSqlTypeGroup.unknown;
        break;
    }
    return typeGroup;
  }
}
