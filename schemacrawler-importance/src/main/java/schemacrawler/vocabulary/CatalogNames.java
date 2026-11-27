/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.vocabulary;

import static java.util.Objects.requireNonNull;

import java.util.List;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Column;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.Index;
import schemacrawler.schema.PrimaryKey;
import schemacrawler.schema.Routine;
import schemacrawler.schema.RoutineParameter;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Sequence;
import schemacrawler.schema.Synonym;
import schemacrawler.schema.Table;
import schemacrawler.schema.TableConstraint;
import schemacrawler.schema.Trigger;
import us.fatehi.utility.UtilityMarker;

/** Reads the names of the objects in a catalog. This is the only code that reads the catalog. */
@UtilityMarker
final class CatalogNames {

  static Names read(final Catalog catalog) {
    requireNonNull(catalog, "No catalog provided");

    final Names.Builder names = Names.builder();
    for (final Schema schema : catalog.getSchemas()) {
      names.add(ObjectKind.SCHEMA, schema);
    }
    for (final Table table : catalog.getTables()) {
      readTable(names, table);
    }
    for (final Routine routine : catalog.getRoutines()) {
      readRoutine(names, routine);
    }
    for (final Sequence sequence : catalog.getSequences()) {
      names.add(ObjectKind.SEQUENCE, sequence);
    }
    for (final Synonym synonym : catalog.getSynonyms()) {
      names.add(ObjectKind.SYNONYM, synonym);
    }
    return names.build();
  }

  private static void readRoutine(final Names.Builder names, final Routine routine) {
    names.add(ObjectKind.ROUTINE, routine);
    final List<RoutineParameter<? extends Routine>> parameters = routine.getParameters();
    for (final RoutineParameter<? extends Routine> parameter : parameters) {
      names.add(ObjectKind.PARAMETER, parameter);
    }
  }

  private static void readTable(final Names.Builder names, final Table table) {
    names.add(ObjectKind.TABLE, table);
    for (final Column column : table.getColumns()) {
      names.add(ObjectKind.COLUMN, column);
    }
    final PrimaryKey primaryKey = table.getPrimaryKey();
    if (primaryKey != null) {
      names.add(ObjectKind.CONSTRAINT, primaryKey);
    }
    for (final ForeignKey foreignKey : table.getForeignKeys()) {
      names.add(ObjectKind.CONSTRAINT, foreignKey);
    }
    for (final TableConstraint constraint : table.getTableConstraints()) {
      names.add(ObjectKind.CONSTRAINT, constraint);
    }
    for (final Index index : table.getIndexes()) {
      names.add(ObjectKind.INDEX, index);
    }
    for (final Trigger trigger : table.getTriggers()) {
      names.add(ObjectKind.TRIGGER, trigger);
    }
  }

  private CatalogNames() {
    // Utility class
  }
}
