/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2009, Sualeh Fatehi.
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


import schemacrawler.crawl.JavaSqlTypesUtility.JavaSqlTypeGroup;
import schemacrawler.schema.BaseColumn;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.NamedObject;

/**
 * Represents a column in a database for tables and procedures. Created
 * from metadata returned by a JDBC call.
 * 
 * @author Sualeh Fatehi
 */
abstract class AbstractColumn
  extends AbstractDependantObject
  implements BaseColumn
{

  private static final long serialVersionUID = -8492662324895309485L;

  private ColumnDataType type;
  private int ordinalPosition;
  private int size;
  private int decimalDigits;
  private boolean nullable;

  AbstractColumn(final DatabaseObject parent, final String name)
  {
    super(parent, name);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final NamedObject obj)
  {
    if (obj == null)
    {
      return -1;
    }

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

    final JavaSqlTypeGroup typeGroup = JavaSqlTypesUtility
      .lookupSqlDataTypeGroup(columnDataType.getType());
    final boolean needWidth = (typeGroup == JavaSqlTypeGroup.character || typeGroup == JavaSqlTypeGroup.real);

    final StringBuilder columnWidthBuffer = new StringBuilder();
    if (needWidth)
    {
      columnWidthBuffer.append("(");
      columnWidthBuffer.append(size);
      if (typeGroup == JavaSqlTypeGroup.real)
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
