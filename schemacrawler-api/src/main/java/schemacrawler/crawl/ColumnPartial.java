/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2015, Sualeh Fatehi.
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


import java.util.Collection;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.PartialDatabaseObject;
import schemacrawler.schema.Privilege;
import schemacrawler.schema.Table;

final class ColumnPartial
  extends AbstractDependantObject<Table>
  implements Column, PartialDatabaseObject
{

  private static final long serialVersionUID = 502720342852782630L;

  private Column referencedColumn;

  ColumnPartial(final Column column)
  {
    this(requireNonNull(column, "No column provided").getParent(),
         column.getName());
    addAttributes(column.getAttributes());
  }

  ColumnPartial(final Table parent, final String name)
  {
    super(new TableReference(parent), name);
  }

  @Override
  public ColumnDataType getColumnDataType()
  {
    throw new NotLoadedException();
  }

  @Override
  public int getDecimalDigits()
  {
    throw new NotLoadedException();
  }

  @Override
  public String getDefaultValue()
  {
    throw new NotLoadedException();
  }

  @Override
  public int getOrdinalPosition()
  {
    throw new NotLoadedException();
  }

  @Override
  public Collection<Privilege<Column>> getPrivileges()
  {
    throw new NotLoadedException();
  }

  @Override
  public Column getReferencedColumn()
  {
    return referencedColumn;
  }

  @Override
  public int getSize()
  {
    throw new NotLoadedException();
  }

  @Override
  public ColumnDataType getType()
  {
    throw new NotLoadedException();
  }

  @Override
  public String getWidth()
  {
    throw new NotLoadedException();
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.Column#isAutoIncremented()
   */
  @Override
  public boolean isAutoIncremented()
  {
    throw new NotLoadedException();
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.Column#isGenerated()
   */
  @Override
  public boolean isGenerated()
  {
    throw new NotLoadedException();
  }

  @Override
  public boolean isNullable()
  {
    throw new NotLoadedException();
  }

  @Override
  public boolean isPartOfForeignKey()
  {
    throw new NotLoadedException();
  }

  @Override
  public boolean isPartOfIndex()
  {
    throw new NotLoadedException();
  }

  @Override
  public boolean isPartOfPrimaryKey()
  {
    throw new NotLoadedException();
  }

  @Override
  public boolean isPartOfUniqueIndex()
  {
    throw new NotLoadedException();
  }

  @Override
  public Optional<Privilege<Column>> lookupPrivilege(final String name)
  {
    throw new NotLoadedException();
  }

  void setReferencedColumn(final Column referencedColumn)
  {
    this.referencedColumn = referencedColumn;
  }

}
