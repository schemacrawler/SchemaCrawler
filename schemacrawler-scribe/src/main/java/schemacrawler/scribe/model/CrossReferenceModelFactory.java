/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.scribe.model;

import static java.util.Objects.requireNonNull;
import static schemacrawler.utility.MetaDataUtility.getSimpleTypeName;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.ReferencingObject;
import schemacrawler.schema.Routine;
import schemacrawler.schema.Table;
import schemacrawler.scribe.renderer.ScribeSupport;

/** Builds "used by" cross-reference models for template rendering. */
public final class CrossReferenceModelFactory {

  public static List<CrossReferenceEntry> createCrossReferenceModel(final ScribeSupport support) {
    return new CrossReferenceModelFactory().createModel(support);
  }

  private CrossReferenceModelFactory() {
    // Prevent intantiation
  }

  private List<DatabaseObject> allDatabaseObjects(final Catalog catalog) {
    if (catalog == null) {
      return List.of();
    }
    final List<DatabaseObject> allDatabaseObjects = new ArrayList<>();
    allDatabaseObjects.addAll(catalog.getTables());
    allDatabaseObjects.addAll(catalog.getRoutines());
    return List.copyOf(allDatabaseObjects);
  }

  private CrossReferenceEntry asEntry(
      final DatabaseObject sourceObject, final DatabaseObject usedByObject) {
    return new CrossReferenceEntry(
        sourceObject,
        getSimpleTypeName(sourceObject),
        usedByObject,
        getSimpleTypeName(usedByObject));
  }

  /**
   * Creates object-to-object cross-reference rows from tables and routines.
   *
   * @return Cross-reference rows with source and used-by objects
   */
  private List<CrossReferenceEntry> createModel(final ScribeSupport support) {
    requireNonNull(support, "No support provided");
    final Catalog catalog = support.getCatalog();
    final List<DatabaseObject> sourceObjects = allDatabaseObjects(catalog);

    final List<CrossReferenceEntry> entries = new ArrayList<>();
    for (final DatabaseObject sourceObject : sourceObjects) {
      final List<DatabaseObject> usedByObjects = usedBy(sourceObject, sourceObjects);
      if (usedByObjects.isEmpty()) {
        continue;
      }

      for (final DatabaseObject usedByObject : usedByObjects) {
        entries.add(asEntry(sourceObject, usedByObject));
      }
    }
    return List.copyOf(entries);
  }

  private List<DatabaseObject> routineUsedBy(
      final Routine routine, final List<DatabaseObject> candidates) {
    final List<DatabaseObject> usedBy = new ArrayList<>();
    for (final DatabaseObject candidate : candidates) {
      if (!(candidate instanceof final ReferencingObject referencingObject)) {
        continue;
      }
      if (referencingObject.getReferencedObjects().contains(routine)) {
        usedBy.add(candidate);
      }
    }
    return List.copyOf(usedBy);
  }

  private List<DatabaseObject> usedBy(
      final DatabaseObject source, final List<DatabaseObject> candidates) {
    final List<DatabaseObject> usedBy = new ArrayList<>();
    if (source instanceof final Table table) {
      usedBy.addAll(table.getUsedByObjects());
    } else if (source instanceof Routine) {
      usedBy.addAll(routineUsedBy((Routine) source, candidates));
    }
    usedBy.removeIf(databaseObject -> databaseObject == null || source.equals(databaseObject));
    usedBy.sort(Comparator.comparing(DatabaseObject::getFullName));
    final List<DatabaseObject> deduplicated = new ArrayList<>();
    DatabaseObject previous = null;
    for (final DatabaseObject databaseObject : usedBy) {
      if (!databaseObject.equals(previous)) {
        deduplicated.add(databaseObject);
        previous = databaseObject;
      }
    }
    return List.copyOf(deduplicated);
  }
}
