/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
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
import schemacrawler.schema.TableConstraint;
import schemacrawler.schema.TableConstraintColumn;

final class MutableTableConstraintColumn
  extends AbstractDependantObject<Table>
  implements TableConstraintColumn
{

  private static final long serialVersionUID = -6923211341742623556L;

  private final Column column;
  private final TableConstraint tableConstraint;
  private int tableConstraintOrdinalPosition;

  MutableTableConstraintColumn(final TableConstraint tableConstraint,
                               final MutableColumn column)
  {
    super(new TableReference(column.getParent()), column.getName());
    this.tableConstraint = tableConstraint;
    this.column = column;
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

    int comparison = 0;

    if (obj instanceof MutableTableConstraintColumn)
    {
      final MutableTableConstraintColumn other = (MutableTableConstraintColumn) obj;
      comparison = tableConstraintOrdinalPosition
                   - other.tableConstraintOrdinalPosition;
    }

    if (comparison == 0)
    {
      comparison = super.compareTo(obj);
    }

    return comparison;
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.BaseColumn#getColumnDataType()
   */
  @Override
  public ColumnDataType getColumnDataType()
  {
    return column.getColumnDataType();
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.BaseColumn#getDecimalDigits()
   */
  @Override
  public int getDecimalDigits()
  {
    return column.getDecimalDigits();
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.Column#getDefaultValue()
   */
  @Override
  public String getDefaultValue()
  {
    return column.getDefaultValue();
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.BaseColumn#getOrdinalPosition()
   */
  @Override
  public int getOrdinalPosition()
  {
    return column.getOrdinalPosition();
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.Column#getPrivileges()
   */
  @Override
  public Collection<Privilege<Column>> getPrivileges()
  {
    return column.getPrivileges();
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.Column#getReferencedColumn()
   */
  @Override
  public Column getReferencedColumn()
  {
    return column.getReferencedColumn();
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.BaseColumn#getSize()
   */
  @Override
  public int getSize()
  {
    return column.getSize();
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.TableConstraintColumn#getTableConstraint()
   */
  @Override
  public TableConstraint getTableConstraint()
  {
    return tableConstraint;
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.TableConstraintColumn#getTableConstraintOrdinalPosition()
   */
  @Override
  public int getTableConstraintOrdinalPosition()
  {
    return tableConstraintOrdinalPosition;
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.TypedObject#getType()
   */
  @Override
  public ColumnDataType getType()
  {
    return column.getType();
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.BaseColumn#getWidth()
   */
  @Override
  public String getWidth()
  {
    return column.getWidth();
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.Column#isAutoIncremented()
   */
  @Override
  public boolean isAutoIncremented()
  {
    return column.isAutoIncremented();
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.Column#isGenerated()
   */
  @Override
  public boolean isGenerated()
  {
    return column.isGenerated();
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.Column#isHidden()
   */
  @Override
  public boolean isHidden()
  {
    return column.isHidden();
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.BaseColumn#isNullable()
   */
  @Override
  public boolean isNullable()
  {
    return column.isNullable();
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.Column#isPartOfForeignKey()
   */
  @Override
  public boolean isPartOfForeignKey()
  {
    return column.isPartOfForeignKey();
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.Column#isPartOfUniqueIndex()()
   */
  @Override
  public boolean isPartOfIndex()
  {
    return column.isPartOfIndex();
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.Column#isPartOfPrimaryKey()
   */
  @Override
  public boolean isPartOfPrimaryKey()
  {
    return column.isPartOfPrimaryKey();
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.Column#isPartOfUniqueIndex()
   */
  @Override
  public boolean isPartOfUniqueIndex()
  {
    return column.isPartOfUniqueIndex();
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.Column#lookupPrivilege(java.lang.String)
   */
  @Override
  public Optional<? extends Privilege<Column>> lookupPrivilege(final String name)
  {
    return column.lookupPrivilege(name);
  }

  void setTableConstraintOrdinalPosition(final int indexOrdinalPosition)
  {
    tableConstraintOrdinalPosition = indexOrdinalPosition;
  }

}
