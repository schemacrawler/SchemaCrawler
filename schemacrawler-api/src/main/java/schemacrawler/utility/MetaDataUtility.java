/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2021, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.utility;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import schemacrawler.crawl.WeakAssociation;
import schemacrawler.schema.BaseForeignKey;
import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnReference;
import schemacrawler.schema.Index;
import schemacrawler.schema.PartialDatabaseObject;
import schemacrawler.schema.Table;
import us.fatehi.utility.UtilityMarker;

/**
 * SchemaCrawler utility methods.
 *
 * @author sfatehi
 */
@UtilityMarker
public final class MetaDataUtility {

  public enum ForeignKeyCardinality {
    unknown(""),
    zero_one("(0..1)"),
    zero_many("(0..many)"),
    one_one("(1..1)");

    private final String description;

    ForeignKeyCardinality(final String description) {
      this.description = requireNonNull(description, "No description provided");
    }

    @Override
    public String toString() {
      return description;
    }
  }

  public static Collection<List<String>> allIndexCoumnNames(final Table table) {
    return indexCoumnNames(table, false);
  }

  public static List<String> columnNames(final Index index) {
    if (index == null) {
      return Collections.emptyList();
    }

    final List<String> columnNames = new ArrayList<>();
    for (final Column indexColumn : index) {
      columnNames.add(indexColumn.getFullName());
    }
    return columnNames;
  }

  public static String constructForeignKeyName(final Column pkColumn, final Column fkColumn) {
    requireNonNull(pkColumn, "No primary key column provided");
    requireNonNull(fkColumn, "No foreign key column provided");

    final Table pkTable = pkColumn.getParent();
    final Table fkParent = fkColumn.getParent();
    final String pkHex = Integer.toHexString(pkTable.getFullName().hashCode());
    final String fkHex = Integer.toHexString(fkParent.getFullName().hashCode());
    final String foreignKeyName = String.format("SC_%s_%s", pkHex, fkHex).toUpperCase();
    return foreignKeyName;
  }

  public static boolean containsGeneratedColumns(final Index index) {
    if (index == null) {
      return false;
    }

    for (final Column indexColumn : index) {
      if (indexColumn.isGenerated()) {
        return true;
      }
    }
    return false;
  }

  public static void createWeakAssociation(final Column pkColumn, final Column fkColumn) {

    final boolean isPkColumnPartial = pkColumn instanceof PartialDatabaseObject;
    final boolean isFkColumnPartial = fkColumn instanceof PartialDatabaseObject;

    if (pkColumn == null || fkColumn == null || isFkColumnPartial && isPkColumnPartial) {
      return;
    }

    final String foreignKeyName = MetaDataUtility.constructForeignKeyName(pkColumn, fkColumn);

    final WeakAssociation weakAssociation = new WeakAssociation(foreignKeyName);
    weakAssociation.addColumnReference(pkColumn, fkColumn);

    fkColumn.getParent().addWeakAssociation(weakAssociation);
    pkColumn.getParent().addWeakAssociation(weakAssociation);
  }

  public static ForeignKeyCardinality findForeignKeyCardinality(
      final BaseForeignKey<?> foreignKey) {
    if (foreignKey == null) {
      return ForeignKeyCardinality.unknown;
    }
    final boolean isForeignKeyUnique = isForeignKeyUnique(foreignKey);

    final ColumnReference columnRef0 = foreignKey.getColumnReferences().get(0);
    final Column fkColumn = columnRef0.getForeignKeyColumn();
    final boolean isColumnReference = fkColumn instanceof PartialDatabaseObject;

    final ForeignKeyCardinality connectivity;
    if (isColumnReference) {
      connectivity = ForeignKeyCardinality.unknown;
    } else if (isForeignKeyUnique) {
      connectivity = ForeignKeyCardinality.zero_one;
    } else {
      connectivity = ForeignKeyCardinality.zero_many;
    }
    return connectivity;
  }

  public static List<String> foreignKeyColumnNames(
      final BaseForeignKey<? extends ColumnReference> foreignKey) {
    if (foreignKey == null) {
      return Collections.emptyList();
    }
    final List<String> columnNames = new ArrayList<>();
    for (final ColumnReference columnReference : foreignKey) {
      columnNames.add(columnReference.getForeignKeyColumn().getFullName());
    }
    return columnNames;
  }

  public static boolean isForeignKeyUnique(final BaseForeignKey<?> foreignKey) {
    if (foreignKey == null) {
      return false;
    }
    final ColumnReference columnRef0 = foreignKey.getColumnReferences().get(0);
    final Table fkTable = columnRef0.getForeignKeyColumn().getParent();
    final Collection<List<String>> uniqueIndexCoumnNames = uniqueIndexCoumnNames(fkTable);
    final List<String> foreignKeyColumnNames = foreignKeyColumnNames(foreignKey);
    return uniqueIndexCoumnNames.contains(foreignKeyColumnNames);
  }

  public static Collection<List<String>> uniqueIndexCoumnNames(final Table table) {
    return indexCoumnNames(table, true);
  }

  private static Collection<List<String>> indexCoumnNames(
      final Table table, final boolean includeUniqueOnly) {
    final List<List<String>> allIndexCoumns = new ArrayList<>();
    if (table instanceof PartialDatabaseObject) {
      return allIndexCoumns;
    }

    for (final Index index : table.getIndexes()) {
      if (includeUniqueOnly && !index.isUnique()) {
        continue;
      }

      final List<String> indexColumns = columnNames(index);
      allIndexCoumns.add(indexColumns);
    }
    return allIndexCoumns;
  }

  private MetaDataUtility() {
    // Prevent instantiation
  }
}
