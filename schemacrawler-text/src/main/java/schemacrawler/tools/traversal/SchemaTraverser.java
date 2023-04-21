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
    tablesComparator = NamedObjectSort.natural;
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
    handler.handle(catalog.getCrawlInfo());
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
    handler.handle(catalog.getDatabaseInfo());
    handler.handle(catalog.getJdbcDriverInfo());
    handler.handleInfoEnd();

    handler.end();
  }
}
