/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.traversal;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.NamedObject;
import schemacrawler.schema.Routine;
import schemacrawler.schema.Sequence;
import schemacrawler.schema.Synonym;
import schemacrawler.schema.Table;
import schemacrawler.utility.NamedObjectSort;

public class SchemaTraverser {

  private Catalog catalog;
  private SchemaTraversalHandler handler;
  private Comparator<NamedObject> tablesComparator;
  private Comparator<NamedObject> routinesComparator;

  public SchemaTraverser() {
    tablesComparator = Comparator.comparing((NamedObject n) -> ((Table) n).getSchema().getFullName()).thenComparing(NamedObject::getName);
    routinesComparator = NamedObjectSort.natural;
  }

  public Catalog getCatalog() {
    return catalog;
  }

  public SchemaTraversalHandler getHandler() {
    return handler;
  }

  public Comparator<NamedObject> getRoutinesComparator() {
    return routinesComparator;
  }

  public Comparator<NamedObject> getTablesComparator() {
    return tablesComparator;
  }

  public void setCatalog(final Catalog catalog) {
    this.catalog = requireNonNull(catalog, "No catalog provided");
  }

  public void setHandler(final SchemaTraversalHandler handler) {
    this.handler = requireNonNull(handler, "No handler provided");
  }

  public void setRoutinesComparator(final Comparator<NamedObject> routinesComparator) {
    this.routinesComparator = requireNonNull(routinesComparator, "No routines comparator provided");
  }

  public void setTablesComparator(final Comparator<NamedObject> tablesComparator) {
    this.tablesComparator = requireNonNull(tablesComparator, "No tables comparator provided");
  }

  public final void traverse() {

    final Collection<ColumnDataType> columnDataTypes = catalog.getColumnDataTypes();
    final Collection<Table> tables = catalog.getTables();
    final Collection<Routine> routines = catalog.getRoutines();
    final Collection<Synonym> synonyms = catalog.getSynonyms();
    final Collection<Sequence> sequences = catalog.getSequences();

    handler.begin();

    handler.handleHeaderStart();
    handler.handleHeader(catalog.getCrawlInfo());
    handler.handleHeaderEnd();

    if (!tables.isEmpty()) {

      handler.handleTablesStart();

      final List<? extends Table> tablesList = new ArrayList<>(tables);
      tablesList.sort(tablesComparator);
      for (final Table table : tablesList) {
        handler.handle(table);
      }

      handler.handleTablesEnd();
    }

    if (!routines.isEmpty()) {
      handler.handleRoutinesStart();

      final List<? extends Routine> routinesList = new ArrayList<>(routines);
      routinesList.sort(routinesComparator);
      for (final Routine routine : routinesList) {
        handler.handle(routine);
      }

      handler.handleRoutinesEnd();
    }

    if (!sequences.isEmpty()) {
      handler.handleSequencesStart();
      final List<? extends Sequence> sequencesList = new ArrayList<>(sequences);
      sequencesList.sort(NamedObjectSort.alphabetical);
      for (final Sequence sequence : sequencesList) {
        handler.handle(sequence);
      }
      handler.handleSequencesEnd();
    }

    if (!synonyms.isEmpty()) {
      handler.handleSynonymsStart();
      final List<? extends Synonym> synonymsList = new ArrayList<>(synonyms);
      synonymsList.sort(NamedObjectSort.alphabetical);
      for (final Synonym synonym : synonymsList) {
        handler.handle(synonym);
      }
      handler.handleSynonymsEnd();
    }

    if (!columnDataTypes.isEmpty()) {
      handler.handleColumnDataTypesStart();
      for (final ColumnDataType columnDataType : columnDataTypes) {
        handler.handle(columnDataType);
      }
      handler.handleColumnDataTypesEnd();
    }

    handler.handleInfoStart();
    handler.handleInfo(catalog.getDatabaseInfo());
    handler.handleInfo(catalog.getJdbcDriverInfo());
    handler.handleInfoEnd();

    handler.end();
  }
}
