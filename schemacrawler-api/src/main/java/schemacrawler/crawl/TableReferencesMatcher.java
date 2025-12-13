/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.crawl;


import schemacrawler.model.implementation.MutableCatalog;
import schemacrawler.model.implementation.MutableTable;
import java.sql.SQLException;
import java.util.Collection;
import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.NamedObjectKey;
import schemacrawler.schema.Routine;
import schemacrawler.schema.Synonym;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import us.fatehi.utility.Multimap;

/** A retriever uses database metadata to get the references to database tables. */
final class TableReferencesMatcher extends AbstractRetriever {

  TableReferencesMatcher(
      final RetrieverConnection retrieverConnection,
      final MutableCatalog catalog,
      final SchemaCrawlerOptions options)
      throws SQLException {
    super(retrieverConnection, catalog, options);
  }

  void collectTableReferences() {

    final Multimap<NamedObjectKey, DatabaseObject> tableReferences = new Multimap<>();

    // Collect referencing routines
    for (final Routine routine : catalog.getRoutines()) {
      for (final DatabaseObject referencedObject : routine.getReferencedObjects()) {
        if (referencedObject instanceof Table) {
          tableReferences.add(referencedObject.key(), routine);
        }
      }
    }
    // Collect referencing synonyms
    for (final Synonym synonym : catalog.getSynonyms()) {
      for (final DatabaseObject referencedObject : synonym.getReferencedObjects()) {
        if (referencedObject instanceof Table) {
          tableReferences.add(referencedObject.key(), synonym);
        }
      }
    }
    // Collect referencing tables and views
    for (final Table table : catalog.getTables()) {
      for (final DatabaseObject referencedObject : table.getReferencedObjects()) {
        if (referencedObject instanceof Table) {
          tableReferences.add(referencedObject.key(), table);
        }
      }
    }

    // Finally, add the referencing objects to each table
    for (final Table table : catalog.getTables()) {
      if (tableReferences.containsKey(table.key())) {
        final Collection<DatabaseObject> references = tableReferences.get(table.key());
        final MutableTable mutableTable = (MutableTable) table;
        mutableTable.addReferencingObjects(references);
      }
    }
  }
}
