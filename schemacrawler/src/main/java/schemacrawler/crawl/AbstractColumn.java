/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2007, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */

package schemacrawler.crawl;


import schemacrawler.schema.BaseColumn;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.NamedObject;

/**
 * Represents a column in a database table. Created from metadata
 * returned by a JDBC call.
 * 
 * @author sfatehi
 */
abstract class AbstractColumn
  extends AbstractDependantNamedObject
  implements BaseColumn
{

  private ColumnDataType dataType;
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
   * 
   * @see schemacrawler.schema.BaseColumn#getSize()
   */
  public final int getSize()
  {
    return size;
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

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.BaseColumn#getType()
   */
  public final ColumnDataType getType()
  {
    return dataType;
  }

  /**
   * Sets the column's data type.
   * 
   * @param columnDataType
   *        Column data type
   */
  final void setType(final ColumnDataType columnDataType)
  {
    dataType = columnDataType;
  }

  /**
   * Creates a data type from the JDBC data type id, and the database
   * specific type name.
   * 
   * @param jdbcDataType
   *        JDBC data type
   * @param databaseSpecificTypeName
   *        Database specific type name
   */
  final void lookupAndSetDataType(final int jdbcDataType,
                                  final String databaseSpecificTypeName,
                                  final NamedObjectList columnDataTypes)
  {
    MutableColumnDataType columnDataType = (MutableColumnDataType) columnDataTypes
      .lookup(databaseSpecificTypeName);
    if (columnDataType == null)
    {
      columnDataType = new MutableColumnDataType(databaseSpecificTypeName);
      columnDataType.setType(jdbcDataType);
    }
    dataType = columnDataType;
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

  final void setDecimalDigits(final int decimalDigits)
  {
    this.decimalDigits = decimalDigits;
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

  final void setNullable(final boolean nullable)
  {
    this.nullable = nullable;
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

  final void setOrdinalPosition(final int ordinalPosition)
  {
    this.ordinalPosition = ordinalPosition;
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
   */
  public int compareTo(final Object obj)
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

}
