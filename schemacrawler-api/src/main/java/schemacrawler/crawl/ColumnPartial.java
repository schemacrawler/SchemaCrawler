/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2024, Sualeh Fatehi <sualeh@hotmail.com>.
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

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.Optional;

import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.PartialDatabaseObject;
import schemacrawler.schema.Privilege;
import schemacrawler.schema.Table;

final class ColumnPartial extends AbstractDependantObject<Table>
    implements Column, PartialDatabaseObject {

  private static final long serialVersionUID = 502720342852782630L;

  private Column referencedColumn;

  ColumnPartial(final Column column) {
    this(requireNonNull(column, "No column provided").getParent(), column.getName());
    addAttributes(column.getAttributes());
  }

  ColumnPartial(final Table parent, final String name) {
    super(new TablePointer(parent), name);
  }

  @Override
  public ColumnDataType getColumnDataType() {
    throw new NotLoadedException(this);
  }

  @Override
  public int getDecimalDigits() {
    throw new NotLoadedException(this);
  }

  @Override
  public String getDefaultValue() {
    throw new NotLoadedException(this);
  }

  @Override
  public int getOrdinalPosition() {
    throw new NotLoadedException(this);
  }

  @Override
  public Collection<Privilege<Column>> getPrivileges() {
    throw new NotLoadedException(this);
  }

  @Override
  public Column getReferencedColumn() {
    return referencedColumn;
  }

  @Override
  public int getSize() {
    throw new NotLoadedException(this);
  }

  @Override
  public ColumnDataType getType() {
    throw new NotLoadedException(this);
  }

  @Override
  public String getWidth() {
    throw new NotLoadedException(this);
  }

  @Override
  public boolean isColumnDataTypeKnown() {
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isAutoIncremented() {
    throw new NotLoadedException(this);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isGenerated() {
    throw new NotLoadedException(this);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isHidden() {
    throw new NotLoadedException(this);
  }

  @Override
  public boolean isNullable() {
    throw new NotLoadedException(this);
  }

  @Override
  public boolean isPartOfForeignKey() {
    throw new NotLoadedException(this);
  }

  @Override
  public boolean isPartOfIndex() {
    throw new NotLoadedException(this);
  }

  @Override
  public boolean isPartOfPrimaryKey() {
    throw new NotLoadedException(this);
  }

  @Override
  public boolean isPartOfUniqueIndex() {
    throw new NotLoadedException(this);
  }

  @Override
  public Optional<Privilege<Column>> lookupPrivilege(final String name) {
    throw new NotLoadedException(this);
  }

  void setReferencedColumn(final Column referencedColumn) {
    this.referencedColumn = referencedColumn;
  }
}
