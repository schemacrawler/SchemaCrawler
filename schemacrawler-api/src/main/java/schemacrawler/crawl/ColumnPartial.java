/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.crawl;

import java.util.Collection;
import java.util.Optional;
import static java.util.Objects.requireNonNull;
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

  /** {@inheritDoc} */
  @Override
  public boolean isAutoIncremented() {
    throw new NotLoadedException(this);
  }

  @Override
  public boolean isColumnDataTypeKnown() {
    return false;
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
