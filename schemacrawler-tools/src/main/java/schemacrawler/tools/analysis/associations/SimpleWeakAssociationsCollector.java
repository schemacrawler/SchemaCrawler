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
package schemacrawler.tools.analysis.associations;


import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import schemacrawler.schema.ColumnMap;
import schemacrawler.schema.Table;

public class SimpleWeakAssociationsCollector
  implements WeakAssociationsCollector
{

  public static final ColumnMap[] getWeakAssociations(final Table table)
  {
    if (table == null)
    {
      return null;
    }

    final Set<ColumnMap> weakAssociations = table
      .getAttribute(WEAK_ASSOCIATIONS_KEY, new HashSet<ColumnMap>());
    final ColumnMap[] tableWeakAssociations = weakAssociations
      .toArray(new ColumnMap[weakAssociations.size()]);
    Arrays.sort(tableWeakAssociations);
    return tableWeakAssociations;
  }

  private final Set<ColumnMap> weakAssociations;

  public SimpleWeakAssociationsCollector()
  {
    weakAssociations = new HashSet<ColumnMap>();
  }

  @Override
  public void addWeakAssociation(final ColumnMap weakAssociation)
  {
    if (weakAssociation != null)
    {
      weakAssociations.add(weakAssociation);

      addWeakAssociation(weakAssociation.getPrimaryKeyColumn().getParent(),
                         weakAssociation);
      addWeakAssociation(weakAssociation.getForeignKeyColumn().getParent(),
                         weakAssociation);
    }
  }

  @Override
  public void clear()
  {
    weakAssociations.clear();
  }

  @Override
  public boolean isEmpty()
  {
    return weakAssociations.isEmpty();
  }

  @Override
  public Iterator<ColumnMap> iterator()
  {
    return weakAssociations.iterator();
  }

  @Override
  public int size()
  {
    return weakAssociations.size();
  }

  @Override
  public ColumnMap[] toArray()
  {
    return weakAssociations.toArray(new ColumnMap[weakAssociations.size()]);
  }

  private void addWeakAssociation(final Table table,
                                  final ColumnMap weakAssociation)
  {
    if (table != null && weakAssociation != null)
    {
      weakAssociations.add(weakAssociation);

      final Collection<ColumnMap> tableWeakAssociations = table
        .getAttribute(WEAK_ASSOCIATIONS_KEY, new HashSet<ColumnMap>());
      tableWeakAssociations.add(weakAssociation);
      table.setAttribute(WEAK_ASSOCIATIONS_KEY, tableWeakAssociations);
    }
  }

}
