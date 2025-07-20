/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.crawl;

import schemacrawler.schema.BaseColumn;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.JavaSqlTypeGroup;
import schemacrawler.schema.NamedObject;

/**
 * Represents a column in a database for tables and routines. Created from metadata returned by a
 * JDBC call.
 */
abstract class AbstractColumn<P extends DatabaseObject> extends AbstractDependantObject<P>
    implements BaseColumn<P> {

  private static final long serialVersionUID = -8492662324895309485L;

  private ColumnDataType columnDataType;
  private int decimalDigits;
  private boolean nullable;
  private int ordinalPosition;
  private int size;

  /**
   * Effective Java - Item 17 - Minimize Mutability - Package-private constructors make a class
   * effectively final
   *
   * @param parent Parent of this object
   * @param name Name of the named object
   */
  AbstractColumn(final DatabaseObjectReference<P> parent, final String name) {
    super(parent, name);
  }

  /**
   * {@inheritDoc}
   *
   * <p>NOTE: compareTo is not compatible with equals. equals compares the full name of a database
   * object, but compareTo uses more fields to define a "natural" sorting order. compareTo may
   * return incorrect results until the object is fully built by SchemaCrawler.
   */
  @Override
  public final int compareTo(final NamedObject obj) {
    if (obj == null) {
      return -1;
    }

    final BaseColumn<P> other = (BaseColumn<P>) obj;
    int comparison = 0;

    if (comparison == 0) {
      comparison = ordinalPosition - other.getOrdinalPosition();
    }
    if (comparison == 0) {
      comparison = super.compareTo(other);
    }

    return comparison;
  }

  /** {@inheritDoc} */
  @Override
  public final ColumnDataType getColumnDataType() {
    return columnDataType;
  }

  /** {@inheritDoc} */
  @Override
  public final int getDecimalDigits() {
    return decimalDigits;
  }

  /** {@inheritDoc} */
  @Override
  public final int getOrdinalPosition() {
    return ordinalPosition;
  }

  /** {@inheritDoc} */
  @Override
  public final int getSize() {
    return size;
  }

  /** {@inheritDoc} */
  @Override
  public final ColumnDataType getType() {
    return getColumnDataType();
  }

  /** {@inheritDoc} */
  @Override
  public final String getWidth() {

    if (!isColumnDataTypeKnown()) {
      return "";
    }

    final ColumnDataType columnDataType = getColumnDataType();

    if (size <= 0 || size >= 2_000_000_000) {
      return "";
    }

    final JavaSqlTypeGroup sqlDataTypeGroup = columnDataType.getJavaSqlType().getJavaSqlTypeGroup();
    final boolean needWidth =
        sqlDataTypeGroup == JavaSqlTypeGroup.character || sqlDataTypeGroup == JavaSqlTypeGroup.real;

    final StringBuilder columnWidthBuffer = new StringBuilder(64);
    if (needWidth) {
      columnWidthBuffer.append('(');
      columnWidthBuffer.append(size);
      if (sqlDataTypeGroup == JavaSqlTypeGroup.real) {
        columnWidthBuffer.append(", ").append(getDecimalDigits());
      }
      columnWidthBuffer.append(')');
    }

    return columnWidthBuffer.toString();
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isColumnDataTypeKnown() {
    return columnDataType != null;
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isNullable() {
    return nullable;
  }

  final void setColumnDataType(final ColumnDataType columnDataType) {
    this.columnDataType = columnDataType;
  }

  final void setDecimalDigits(final int decimalDigits) {
    this.decimalDigits = decimalDigits;
  }

  final void setNullable(final boolean nullable) {
    this.nullable = nullable;
  }

  final void setOrdinalPosition(final int ordinalPosition) {
    this.ordinalPosition = ordinalPosition;
  }

  /**
   * Sets the column size.
   *
   * @param size Size of the column
   */
  final void setSize(final int size) {
    this.size = size;
  }
}
