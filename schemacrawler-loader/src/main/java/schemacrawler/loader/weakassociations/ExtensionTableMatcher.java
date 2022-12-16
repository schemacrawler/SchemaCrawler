/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.loader.weakassociations;

import java.util.function.Predicate;

import schemacrawler.schema.Column;
import schemacrawler.schema.Table;

public final class ExtensionTableMatcher implements Predicate<ProposedWeakAssociation> {

  private final boolean inferExtensionTables;

  public ExtensionTableMatcher(final boolean inferExtensionTables) {
    this.inferExtensionTables = inferExtensionTables;
  }

  @Override
  public boolean test(final ProposedWeakAssociation proposedWeakAssociation) {

    if (!inferExtensionTables) {
      return false;
    }

    if (proposedWeakAssociation == null) {
      return false;
    }

    final Column foreignKeyColumn = proposedWeakAssociation.getForeignKeyColumn();
    final Column primaryKeyColumn = proposedWeakAssociation.getPrimaryKeyColumn();

    final String pkColumnName =
        primaryKeyColumn.getName().replaceAll("[^\\p{L}\\{d}]", "").toLowerCase();
    final String fkColumnName =
        foreignKeyColumn.getName().replaceAll("[^\\p{L}\\{d}]", "").toLowerCase();
    if (pkColumnName.equals(fkColumnName)) {
      final Table pkTable = primaryKeyColumn.getParent();
      final Table fkTable = foreignKeyColumn.getParent();
      final boolean fkIsUnique =
          foreignKeyColumn.isPartOfPrimaryKey() || foreignKeyColumn.isPartOfUniqueIndex();
      final boolean pkTableSortsFirst = pkTable.compareTo(fkTable) < 0;
      return fkIsUnique && pkTableSortsFirst;
    } else {
      return false;
    }
  }
}
