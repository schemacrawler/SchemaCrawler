/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2013, Sualeh Fatehi.
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


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import schemacrawler.schema.ColumnReference;
import schemacrawler.schema.Database;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.BaseDatabaseDecorator;

public final class DatabaseWithAssociations
  extends BaseDatabaseDecorator
{

  private static final long serialVersionUID = -3953296149824921463L;

  private static final String WEAK_ASSOCIATIONS_KEY = "schemacrawler.weak_associations";

  public static final Collection<ColumnReference> getWeakAssociations(final Table table)
  {
    if (table == null)
    {
      return null;
    }

    final SortedSet<ColumnReference> weakAssociations = table
      .getAttribute(WEAK_ASSOCIATIONS_KEY, new TreeSet<ColumnReference>());
    final List<ColumnReference> weakAssociationsList = new ArrayList<ColumnReference>(weakAssociations);
    Collections.shuffle(weakAssociationsList);
    return weakAssociationsList;
  }

  static void addWeakAssociationToTable(final Table table,
                                        final ColumnReference weakAssociation)
  {
    if (table != null && weakAssociation != null)
    {
      final SortedSet<ColumnReference> tableWeakAssociations = table
        .getAttribute(WEAK_ASSOCIATIONS_KEY, new TreeSet<ColumnReference>());
      tableWeakAssociations.add(weakAssociation);
      table.setAttribute(WEAK_ASSOCIATIONS_KEY, tableWeakAssociations);
    }
  }

  private final Collection<ColumnReference> weakAssociations;

  public DatabaseWithAssociations(final Database database)
  {
    super(database);

    final List<Table> allTables = new ArrayList<Table>(database.getTables());
    final WeakAssociationsAnalyzer weakAssociationsAnalyzer = new WeakAssociationsAnalyzer(allTables);
    weakAssociations = weakAssociationsAnalyzer.analyzeTables();
  }

  public Collection<ColumnReference> getWeakAssociations()
  {
    return weakAssociations;
  }

}
