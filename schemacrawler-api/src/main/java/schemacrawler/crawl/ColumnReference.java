/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2012, Sualeh Fatehi.
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

import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.Privilege;
import schemacrawler.schema.Table;

final class ColumnReference
  extends AbstractDependantObject<Table>
  implements Column
{

  private static final long serialVersionUID = 502720342852782630L;
  private Column referencedColumn;

  ColumnReference(final Table parent, final String name)
  {
    super(parent, name);
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
  public Privilege<Column> getPrivilege(final String name)
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
  public ColumnDataType getColumnDataType()
  {
    throw new NotLoadedException();
  }

  @Override
  public String getWidth()
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
  public boolean isPartOfPrimaryKey()
  {
    throw new NotLoadedException();
  }

  @Override
  public boolean isPartOfUniqueIndex()
  {
    throw new NotLoadedException();
  }

  void setReferencedColumn(final Column referencedColumn)
  {
    this.referencedColumn = referencedColumn;
  }

}
