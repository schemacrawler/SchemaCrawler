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

package schemacrawler.test.utility.crawl;

import static java.lang.reflect.Proxy.newProxyInstance;
import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.requireNotBlank;

import java.sql.SQLFeatureNotSupportedException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.NamedObject;
import schemacrawler.schema.NamedObjectKey;
import schemacrawler.schema.Privilege;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.utility.JavaSqlTypes;

public class LightColumn implements Column {

  private static final long serialVersionUID = -1931193814458050468L;

  public static ColumnDataType integerColumnDataType() {
    return (ColumnDataType)
        newProxyInstance(
            ColumnDataType.class.getClassLoader(),
            new Class[] {ColumnDataType.class},
            (proxy, method, args) -> {
              final String methodName = method.getName();
              switch (methodName) {
                case "getName":
                  return "INTEGER";
                case "getJavaSqlType":
                  return new JavaSqlTypes().getFromJavaSqlTypeName("INTEGER");
                default:
                  throw new SQLFeatureNotSupportedException(methodName);
              }
            });
  }

  private final Table parent;
  private final String name;

  public LightColumn(final Table parent, final String name) {
    this.parent = requireNonNull(parent);
    this.name = requireNotBlank(name, "No name provided");
  }

  @Override
  public int compareTo(final NamedObject o) {
    return 0;
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
    final LightColumn other = (LightColumn) obj;
    return Objects.equals(name, other.name) && Objects.equals(parent, other.parent);
  }

  @Override
  public <T> T getAttribute(final String name) {
    return null;
  }

  @Override
  public <T> T getAttribute(final String name, final T defaultValue) throws ClassCastException {
    return null;
  }

  @Override
  public Map<String, Object> getAttributes() {
    return new HashMap<>();
  }

  @Override
  public ColumnDataType getColumnDataType() {
    return integerColumnDataType();
  }

  @Override
  public int getDecimalDigits() {
    return 0;
  }

  @Override
  public String getDefaultValue() {
    return "";
  }

  @Override
  public String getFullName() {
    final StringBuffer buffer = new StringBuffer();
    buffer.append(parent.getFullName()).append(".").append(name);
    return buffer.toString();
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public int getOrdinalPosition() {
    return 0;
  }

  @Override
  public Table getParent() {
    return parent;
  }

  @Override
  public Collection<Privilege<Column>> getPrivileges() {
    return new HashSet<>();
  }

  @Override
  public Column getReferencedColumn() {
    return null;
  }

  @Override
  public String getRemarks() {
    return "";
  }

  @Override
  public Schema getSchema() {
    return parent.getSchema();
  }

  @Override
  public String getShortName() {
    return name;
  }

  @Override
  public int getSize() {
    return 0;
  }

  @Override
  public ColumnDataType getType() {
    return getColumnDataType();
  }

  @Override
  public String getWidth() {
    return "";
  }

  @Override
  public boolean hasAttribute(final String name) {
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, parent);
  }

  @Override
  public boolean hasRemarks() {
    return false;
  }

  @Override
  public boolean isAutoIncremented() {
    return false;
  }

  @Override
  public boolean isColumnDataTypeKnown() {
    return false;
  }

  @Override
  public boolean isGenerated() {
    return false;
  }

  @Override
  public boolean isHidden() {
    return false;
  }

  @Override
  public boolean isNullable() {
    return false;
  }

  @Override
  public boolean isParentPartial() {
    return false;
  }

  @Override
  public boolean isPartOfForeignKey() {
    return false;
  }

  @Override
  public boolean isPartOfIndex() {
    return false;
  }

  @Override
  public boolean isPartOfPrimaryKey() {
    return true;
  }

  @Override
  public boolean isPartOfUniqueIndex() {
    return true;
  }

  @Override
  public NamedObjectKey key() {
    return parent.key().with(name);
  }

  @Override
  public <T> Optional<T> lookupAttribute(final String name) {
    return Optional.empty();
  }

  @Override
  public <P extends Privilege<Column>> Optional<P> lookupPrivilege(final String name) {
    return Optional.empty();
  }

  @Override
  public void removeAttribute(final String name) {}

  @Override
  public <T> void setAttribute(final String name, final T value) {}

  @Override
  public void setRemarks(final String remarks) {}

  @Override
  public String toString() {
    return getFullName();
  }
}
