/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
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

import static java.util.Comparator.comparing;
import static java.util.Comparator.nullsLast;
import static java.util.Objects.compare;
import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.sql.SQLType;
import java.util.Comparator;

/** A wrapper around java.sql.Types. */
public final class JavaSqlType implements SQLType, Serializable, Comparable<JavaSqlType> {

  private static final long serialVersionUID = 2614819974745473431L;

  /** Unknown SQL data type. */
  public static final JavaSqlType UNKNOWN =
      new JavaSqlType(unknownSQLType(), Object.class, JavaSqlTypeGroup.unknown);

  private static Comparator<JavaSqlType> comparator =
      nullsLast(comparing(JavaSqlType::getName, String.CASE_INSENSITIVE_ORDER));

  private static SQLType unknownSQLType() {
    final class UnknownSQLType implements SQLType, Serializable {

      private static final long serialVersionUID = -2579002704227573365L;

      @Override
      public String getName() {
        return "UNKNOWN";
      }

      @Override
      public String getVendor() {
        return "us.fatehi.schemacrawler";
      }

      @Override
      public Integer getVendorTypeNumber() {
        return Integer.MIN_VALUE;
      }
    }

    return new UnknownSQLType();
  }

  private final Class<?> defaultMappedClass;
  private final JavaSqlTypeGroup javaSqlTypeGroup;
  private final SQLType sqlType;

  public JavaSqlType(
      final SQLType sqlType,
      final Class<?> defaultMappedClass,
      final JavaSqlTypeGroup javaSqlTypeGroup) {
    this.sqlType = requireNonNull(sqlType, "No SQLType provided");
    this.defaultMappedClass =
        requireNonNull(defaultMappedClass, "Np default mapped class provided");
    this.javaSqlTypeGroup = requireNonNull(javaSqlTypeGroup, "No SQLType group provided");
  }

  @Override
  public int compareTo(final JavaSqlType otherSqlDataType) {
    return compare(this, otherSqlDataType, comparator);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final JavaSqlType other = (JavaSqlType) obj;
    return sqlType.getVendorTypeNumber().equals(other.sqlType.getVendorTypeNumber());
  }

  public Class<?> getDefaultMappedClass() {
    return defaultMappedClass;
  }

  public JavaSqlTypeGroup getJavaSqlTypeGroup() {
    return javaSqlTypeGroup;
  }

  @Override
  public String getName() {
    return sqlType.getName();
  }

  @Override
  public String getVendor() {
    return sqlType.getVendor();
  }

  @Override
  public Integer getVendorTypeNumber() {
    final Integer vendorTypeNumber = sqlType.getVendorTypeNumber();
    if (vendorTypeNumber != null) {
      return vendorTypeNumber;
    } else {
      return Integer.MIN_VALUE;
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + sqlType.getVendorTypeNumber();
    return result;
  }

  @Override
  public String toString() {
    return String.format(
        "%s\t%d\t%s", sqlType.getName(), sqlType.getVendorTypeNumber(), javaSqlTypeGroup);
  }
}
