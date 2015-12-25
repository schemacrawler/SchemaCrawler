/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2016, Sualeh Fatehi.
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
package schemacrawler.tools.analysis.associations;


import schemacrawler.crawl.BaseColumnReference;
import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.Table;

/**
 * Represents a single column mapping from a primary key column to a
 * foreign key column.
 *
 * @author Sualeh Fatehi
 */
public final class WeakAssociation
  extends BaseColumnReference
{

  private static final long serialVersionUID = -4411771492159843382L;

  WeakAssociation(final Column primaryKeyColumn, final Column foreignKeyColumn)
  {
    super(primaryKeyColumn, foreignKeyColumn);
  }

  public boolean isValid()
  {
    final Column primaryKeyColumn = getPrimaryKeyColumn();
    final Column foreignKeyColumn = getForeignKeyColumn();

    final Table pkTable = primaryKeyColumn.getParent();
    final Table fkTable = foreignKeyColumn.getParent();
    if ((foreignKeyColumn.isPartOfPrimaryKey()
         || foreignKeyColumn.isPartOfUniqueIndex())
        && pkTable.compareTo(fkTable) > 0)
    {
      return false;
    }

    final ColumnDataType fkColumnType = foreignKeyColumn.getColumnDataType();
    final ColumnDataType pkColumnType = primaryKeyColumn.getColumnDataType();
    final boolean isValid = fkColumnType.getJavaSqlType().getJavaSqlTypeName()
      .equals(pkColumnType.getJavaSqlType().getJavaSqlTypeName());
    return isValid;
  }

  @Override
  public String toString()
  {
    return getPrimaryKeyColumn() + " <~~ " + getForeignKeyColumn();
  }

}
