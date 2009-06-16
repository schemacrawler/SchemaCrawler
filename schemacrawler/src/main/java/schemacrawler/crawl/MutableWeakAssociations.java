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


import schemacrawler.schema.Column;
import schemacrawler.schema.ForeignKeyColumnMap;
import schemacrawler.schema.WeakAssociations;

/**
 * Represents a foreign-key mapping to a primary key in another table.
 * 
 * @author Sualeh Fatehi
 */
class MutableWeakAssociations
  implements WeakAssociations
{

  private static final long serialVersionUID = 4121411795974895671L;

  private final NamedObjectList<MutableForeignKeyColumnMap> columnPairs = new NamedObjectList<MutableForeignKeyColumnMap>(NamedObjectSort.natural);

  /**
   * {@inheritDoc}
   * 
   * @see WeakAssociations#getColumnPairs()
   * @see ForeignKey#getColumnPairs()
   */
  public ForeignKeyColumnMap[] getColumnPairs()
  {
    return columnPairs.values().toArray(new ForeignKeyColumnMap[columnPairs
      .size()]);
  }

  void addColumnPair(final Column pkColumn, final Column fkColumn)
  {
    final String fkColumnMapName = pkColumn.getName() + "."
                                   + fkColumn.getName();
    final MutableForeignKeyColumnMap fkColumnPair = new MutableForeignKeyColumnMap(fkColumn
                                                                                     .getParent(),
                                                                                   fkColumnMapName);
    fkColumnPair.setKeySequence(1);
    fkColumnPair.setPrimaryKeyColumn(pkColumn);
    fkColumnPair.setForeignKeyColumn(fkColumn);
    columnPairs.add(fkColumnPair);
  }

}
