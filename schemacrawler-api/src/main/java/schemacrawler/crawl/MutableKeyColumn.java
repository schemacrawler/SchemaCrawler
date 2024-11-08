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

package schemacrawler.crawl;

import java.util.Collection;
import java.util.Optional;
import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.NamedObject;
import schemacrawler.schema.Privilege;
import schemacrawler.schema.Table;

class MutableKeyColumn extends AbstractDependantObject<Table> implements Column {

  private static final long serialVersionUID = 6988029161945610279L;

  private final Column column;
  private int keyOrdinalPosition;

  MutableKeyColumn(final Column column) {
    super(new TablePointer(column.getParent()), column.getName());
    this.column = column;
  }

  /**
   * {@inheritDoc}
   *
   * <p>NOTE: compareTo is not compatible with equals. equals compares the full name of a database
   * object, but compareTo uses more fields to define a "natural" sorting order. compareTo may
   * return incorrect results until the object is fully built by SchemaCrawler.
   */
  @Override
  public int compareTo(final NamedObject obj) {
    if (obj == null) {
      return -1;
    }

    int comparison = 0;

    if (obj instanceof MutableKeyColumn) {
      final MutableKeyColumn other = (MutableKeyColumn) obj;
      comparison = keyOrdinalPosition - other.keyOrdinalPosition;
    }

    if (comparison == 0) {
      comparison = super.compareTo(obj);
    }

    return comparison;
  }

  /** {@inheritDoc} */
  @Override
  public ColumnDataType getColumnDataType() {
    return column.getColumnDataType();
  }

  /** {@inheritDoc} */
  @Override
  public int getDecimalDigits() {
    return column.getDecimalDigits();
  }

  /** {@inheritDoc} */
  @Override
  public String getDefaultValue() {
    return column.getDefaultValue();
  }

  /** {@inheritDoc} */
  @Override
  public int getOrdinalPosition() {
    return column.getOrdinalPosition();
  }

  /** {@inheritDoc} */
  @Override
  public Collection<Privilege<Column>> getPrivileges() {
    return column.getPrivileges();
  }

  /** {@inheritDoc} */
  @Override
  public Column getReferencedColumn() {
    return column.getReferencedColumn();
  }

  /** {@inheritDoc} */
  @Override
  public int getSize() {
    return column.getSize();
  }

  /** {@inheritDoc} */
  @Override
  public ColumnDataType getType() {
    return column.getType();
  }

  /** {@inheritDoc} */
  @Override
  public String getWidth() {
    return column.getWidth();
  }

  /** {@inheritDoc} */
  @Override
  public boolean isAutoIncremented() {
    return column.isAutoIncremented();
  }

  @Override
  public boolean isColumnDataTypeKnown() {
    return column.isColumnDataTypeKnown();
  }

  /** {@inheritDoc} */
  @Override
  public boolean isGenerated() {
    return column.isGenerated();
  }

  /** {@inheritDoc} */
  @Override
  public boolean isHidden() {
    return column.isHidden();
  }

  /** {@inheritDoc} */
  @Override
  public boolean isNullable() {
    return column.isNullable();
  }

  /** {@inheritDoc} */
  @Override
  public boolean isPartOfForeignKey() {
    return column.isPartOfForeignKey();
  }

  /** {@inheritDoc} */
  @Override
  public boolean isPartOfIndex() {
    return column.isPartOfIndex();
  }

  /** {@inheritDoc} */
  @Override
  public boolean isPartOfPrimaryKey() {
    return column.isPartOfPrimaryKey();
  }

  /** {@inheritDoc} */
  @Override
  public boolean isPartOfUniqueIndex() {
    return column.isPartOfUniqueIndex();
  }

  /** {@inheritDoc} */
  @Override
  public Optional<? extends Privilege<Column>> lookupPrivilege(final String name) {
    return column.lookupPrivilege(name);
  }

  int getKeyOrdinalPosition() {
    return keyOrdinalPosition;
  }

  void setGenerated(final boolean isGenerated) {
    if (column instanceof MutableColumn) {
      ((MutableColumn) column).setGenerated(isGenerated);
    }
  }

  void setKeyOrdinalPosition(final int keyOrdinalPosition) {
    this.keyOrdinalPosition = keyOrdinalPosition;
  }
}
