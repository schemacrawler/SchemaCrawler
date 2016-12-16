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
