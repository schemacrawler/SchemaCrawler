/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
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
