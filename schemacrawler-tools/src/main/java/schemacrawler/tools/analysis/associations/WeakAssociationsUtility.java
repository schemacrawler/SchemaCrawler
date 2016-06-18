/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi <sualeh@hotmail.com>.
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


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import schemacrawler.schema.Table;

public final class WeakAssociationsUtility
{

  private static final String WEAK_ASSOCIATIONS_KEY = "schemacrawler.weak_associations";

  public static final Collection<WeakAssociationForeignKey> getWeakAssociations(final Table table)
  {
    if (table == null)
    {
      return null;
    }

    final SortedSet<WeakAssociationForeignKey> weakAssociations = table
      .getAttribute(WEAK_ASSOCIATIONS_KEY,
                    new TreeSet<WeakAssociationForeignKey>());
    final List<WeakAssociationForeignKey> weakAssociationsList = new ArrayList<>(weakAssociations);
    Collections.sort(weakAssociationsList);
    return weakAssociationsList;
  }

  static void addWeakAssociationToTable(final Table table,
                                        final WeakAssociationForeignKey weakAssociation)
  {
    if (table != null && weakAssociation != null)
    {
      final SortedSet<WeakAssociationForeignKey> tableWeakAssociations = table
        .getAttribute(WEAK_ASSOCIATIONS_KEY,
                      new TreeSet<WeakAssociationForeignKey>());

      tableWeakAssociations.add(weakAssociation);

      table.setAttribute(WEAK_ASSOCIATIONS_KEY, tableWeakAssociations);
    }
  }

  private WeakAssociationsUtility()
  {
  }

}
