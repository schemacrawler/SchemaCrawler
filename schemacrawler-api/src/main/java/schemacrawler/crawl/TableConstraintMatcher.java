/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.crawl;


import static java.util.Objects.requireNonNull;

import schemacrawler.model.implementation.MutableCatalog;
import schemacrawler.model.implementation.MutablePrimaryKey;
import schemacrawler.model.implementation.MutableTable;
import schemacrawler.model.implementation.NamedObjectList;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.Optional;
import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.TableConstraint;
import schemacrawler.schema.TableConstraintType;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;

/** A retriever uses database metadata to get the constraints on the database tables. */
final class TableConstraintMatcher extends AbstractRetriever {

  TableConstraintMatcher(
      final RetrieverConnection retrieverConnection,
      final MutableCatalog catalog,
      final SchemaCrawlerOptions options)
      throws SQLException {
    super(retrieverConnection, catalog, options);
  }

  void matchTableConstraints(final NamedObjectList<MutableTable> allTables) {
    requireNonNull(allTables, "No tables provided");
    for (final MutableTable mutableTable : allTables) {
      if (mutableTable == null) {
        continue;
      }
      matchPrimaryKey(mutableTable);
      addImportedForeignKeys(mutableTable);
    }
  }

  /**
   * Add foreign keys as table constraints. Foreign keys are not loaded by the CONSTRAINTS view in
   * the information schema views, so they can be added in without fear of duplication.
   *
   * @param table Table to add constraints to
   */
  private void addImportedForeignKeys(final MutableTable table) {
    final Collection<ForeignKey> importedForeignKeys = table.getImportedForeignKeys();
    for (final ForeignKey foreignKey : importedForeignKeys) {
      final Optional<TableConstraint> lookupTableConstraint =
          table.lookupTableConstraint(foreignKey.getName());
      if (lookupTableConstraint.isPresent()) {
        final TableConstraint tableConstraint = lookupTableConstraint.get();
        copyRemarksAndAttributes(tableConstraint, foreignKey);
        table.removeTableConstraint(tableConstraint);
      }
      // Add or replace the table constraint with the foreign key, which has more information like
      // column mappings
      table.addTableConstraint(foreignKey);
    }
  }

  private void copyRemarksAndAttributes(
      final TableConstraint tableConstraint, final DatabaseObject databaseObject) {
    // Copy remarks over
    if (!databaseObject.hasRemarks() && tableConstraint.hasRemarks()) {
      databaseObject.setRemarks(tableConstraint.getRemarks());
    }
    // Copy attributes over
    for (final Entry<String, Object> attribute : tableConstraint.getAttributes().entrySet()) {
      databaseObject.setAttribute(attribute.getKey(), attribute.getValue());
    }
  }

  private void matchPrimaryKey(final MutableTable table) {
    if (!table.hasPrimaryKey()) {
      return;
    }
    final MutablePrimaryKey primaryKey = table.getPrimaryKey();
    // Remove table constraints that are primary keys, if the columns match
    for (final TableConstraint tableConstraint : table.getTableConstraints()) {
      if (tableConstraint.getType() == TableConstraintType.primary_key
          && (primaryKey.getName().equals(tableConstraint.getName())
              || primaryKey
                  .getConstrainedColumns()
                  .equals(tableConstraint.getConstrainedColumns()))) {
        copyRemarksAndAttributes(tableConstraint, primaryKey);
        table.removeTableConstraint(tableConstraint);
      }
    }
    // Add back primary key as table constraints
    table.addTableConstraint(primaryKey);
  }
}
