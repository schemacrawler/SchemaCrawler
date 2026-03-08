/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
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
import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import schemacrawler.tools.state.AbstractExecutionState;
import schemacrawler.utility.NamedObjectSort;

public class SchemaTraverser extends AbstractExecutionState {

  private SchemaTraversalHandler handler;
  private Comparator<NamedObject> tablesComparator;
  private Comparator<NamedObject> routinesComparator;

  public SchemaTraverser() {
    tablesComparator = NamedObjectSort.natural;
    routinesComparator = NamedObjectSort.natural;
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

    if (!hasCatalog()) {
      throw new ExecutionRuntimeException("No catalog provided");
    }

    final Catalog catalog = getCatalog();

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
      for (final Sequence sequence : sequences) {
        handler.handle(sequence);
      }
      handler.handleSequencesEnd();
    }

    if (!synonyms.isEmpty()) {
      handler.handleSynonymsStart();
      for (final Synonym synonym : synonyms) {
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
