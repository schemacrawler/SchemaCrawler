/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2015, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */
package schemacrawler.tools.traversal;


import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.NamedObject;
import schemacrawler.schema.Routine;
import schemacrawler.schema.Sequence;
import schemacrawler.schema.Synonym;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.utility.NamedObjectSort;

public class SchemaTraverser
{

  private Catalog catalog;
  private SchemaTraversalHandler handler;
  private Comparator<NamedObject> tablesComparator;
  private Comparator<NamedObject> routinesComparator;

  public Catalog getCatalog()
  {
    return catalog;
  }

  public SchemaTraversalHandler getHandler()
  {
    return handler;
  }

  public Comparator<NamedObject> getRoutinesComparator()
  {
    return routinesComparator;
  }

  public Comparator<NamedObject> getTablesComparator()
  {
    return tablesComparator;
  }

  public void setCatalog(final Catalog catalog)
  {
    this.catalog = requireNonNull(catalog, "No catalog provided");
  }

  public void setHandler(final SchemaTraversalHandler handler)
  {
    this.handler = requireNonNull(handler, "No handler provided");
  }

  public void
    setRoutinesComparator(final Comparator<NamedObject> routinesComparator)
  {
    this.routinesComparator = requireNonNull(routinesComparator);
  }

  public void
    setTablesComparator(final Comparator<NamedObject> tablesComparator)
  {
    this.tablesComparator = requireNonNull(tablesComparator);
  }

  public final void traverse()
    throws SchemaCrawlerException
  {

    final Collection<ColumnDataType> columnDataTypes = catalog
      .getColumnDataTypes();
    final Collection<Table> tables = catalog.getTables();
    final Collection<Routine> routines = catalog.getRoutines();
    final Collection<Synonym> synonyms = catalog.getSynonyms();
    final Collection<Sequence> sequences = catalog.getSequences();

    handler.begin();

    handler.handleHeaderStart();
    handler.handle(catalog.getCrawlInfo());
    handler.handleHeaderEnd();

    if (!tables.isEmpty())
    {

      handler.handleTablesStart();

      final List<? extends Table> tablesList = new ArrayList<>(tables);
      Collections.sort(tablesList, tablesComparator);
      for (Table table: tablesList)
      {
        handler.handle(table);
      }

      handler.handleTablesEnd();
    }

    if (!routines.isEmpty())
    {
      handler.handleRoutinesStart();

      final List<? extends Routine> routinesList = new ArrayList<>(routines);
      Collections.sort(routinesList, routinesComparator);
      for (Routine routine: routinesList)
      {
        handler.handle(routine);
      }

      handler.handleRoutinesEnd();
    }

    if (!sequences.isEmpty())
    {
      handler.handleSequencesStart();
      for (final Sequence sequence: sequences)
      {
        handler.handle(sequence);
      }
      handler.handleSequencesEnd();
    }

    if (!synonyms.isEmpty())
    {
      handler.handleSynonymsStart();
      for (final Synonym synonym: synonyms)
      {
        handler.handle(synonym);
      }
      handler.handleSynonymsEnd();
    }

    if (!columnDataTypes.isEmpty())
    {
      handler.handleColumnDataTypesStart();
      for (final ColumnDataType columnDataType: columnDataTypes)
      {
        handler.handle(columnDataType);
      }
      handler.handleColumnDataTypesEnd();
    }

    handler.handleInfoStart();
    handler.handle(catalog.getSchemaCrawlerInfo());
    handler.handle(catalog.getDatabaseInfo());
    handler.handle(catalog.getJdbcDriverInfo());
    handler.handleInfoEnd();

    handler.end();
  }

  public SchemaTraverser()
  {
    tablesComparator = NamedObjectSort.natural;
    routinesComparator = NamedObjectSort.natural;
  }

}
