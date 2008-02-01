/*
 * SchemaCrawler
 * Copyright (c) 2000-2008, Sualeh Fatehi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package schemacrawler.crawl;


import schemacrawler.schema.BaseColumn;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.NamedObject;

/**
 * Represents a column in a database table. Created from metadata
 * returned by a JDBC call.
 * 
 * @author Sualeh Fatehi
 */
abstract class AbstractColumn
  extends AbstractDependantNamedObject
  implements BaseColumn
{

  private ColumnDataType type;
  private int ordinalPosition;
  private int size;
  private int decimalDigits;
  private boolean nullable;

  AbstractColumn(final String name, final NamedObject parent)
  {
    super(name, parent);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final NamedObject obj)
  {
    final BaseColumn other = (BaseColumn) obj;
    int comparison = 0;

    if (comparison == 0)
    {
      comparison = ordinalPosition - other.getOrdinalPosition();
    }
    if (comparison == 0)
    {
      comparison = super.compareTo(other);
    }

    return comparison;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.BaseColumn#getDecimalDigits()
   */
  public final int getDecimalDigits()
  {
    return decimalDigits;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.BaseColumn#getOrdinalPosition()
   */
  public final int getOrdinalPosition()
  {
    return ordinalPosition;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.BaseColumn#getSize()
   */
  public final int getSize()
  {
    return size;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.BaseColumn#getType()
   */
  public final ColumnDataType getType()
  {
    return type;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.BaseColumn#getWidth()
   */
  public final String getWidth()
  {

    final ColumnDataType columnDataType = getType();
    if (columnDataType == null)
    {
      return "";
    }

    if (size == 0 || size == Integer.MIN_VALUE || size == Integer.MAX_VALUE)
    {
      return "";
    }

    boolean needWidth = true;
    if (columnDataType.isDateType() || columnDataType.isBinaryType()
        || columnDataType.isIntegralType())
    {
      needWidth = false;
    }

    final StringBuffer columnWidthBuffer = new StringBuffer();
    if (needWidth)
    {
      columnWidthBuffer.append("(");
      columnWidthBuffer.append(size);
      if (columnDataType.isRealType())
      {
        columnWidthBuffer.append(", ").append(getDecimalDigits());
      }
      columnWidthBuffer.append(")");
    }

    return columnWidthBuffer.toString();

  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.BaseColumn#isNullable()
   */
  public final boolean isNullable()
  {
    return nullable;
  }

  final void setDecimalDigits(final int decimalDigits)
  {
    this.decimalDigits = decimalDigits;
  }

  final void setNullable(final boolean nullable)
  {
    this.nullable = nullable;
  }

  final void setOrdinalPosition(final int ordinalPosition)
  {
    this.ordinalPosition = ordinalPosition;
  }

  /**
   * Sets the column size.
   * 
   * @param size
   *        Size of the column
   */
  final void setSize(final int size)
  {
    this.size = size;
  }

  void setType(final ColumnDataType type)
  {
    this.type = type;
  }

}
