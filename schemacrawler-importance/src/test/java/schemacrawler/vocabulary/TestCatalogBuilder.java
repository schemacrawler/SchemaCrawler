/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.vocabulary;

import static schemacrawler.test.utility.crawl.LightCatalogUtility.lightCatalog;

import java.util.ArrayList;
import java.util.List;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Routine;
import schemacrawler.schema.Sequence;
import schemacrawler.schema.Synonym;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaReference;
import schemacrawler.test.utility.crawl.LightProcedure;
import schemacrawler.test.utility.crawl.LightProcedureParameter;
import schemacrawler.test.utility.crawl.LightSequence;
import schemacrawler.test.utility.crawl.LightSynonym;
import schemacrawler.test.utility.crawl.LightTable;
import schemacrawler.test.utility.crawl.LightTrigger;

/** Builds small hand-made catalogs from names, for testing the vocabulary. */
final class TestCatalogBuilder {

  private final SchemaReference schema = new SchemaReference();
  private final List<Table> tables = new ArrayList<>();
  private final List<Routine> routines = new ArrayList<>();
  private final List<Sequence> sequences = new ArrayList<>();
  private final List<Synonym> synonyms = new ArrayList<>();

  TestCatalogBuilder table(final String name, final String... columnNames) {
    final LightTable table = new LightTable(schema, name);
    for (final String columnName : columnNames) {
      table.addColumn(columnName);
    }
    tables.add(table);
    return this;
  }

  /** Adds triggers to the table that was added last. */
  TestCatalogBuilder triggers(final String... triggerNames) {
    final LightTable table = (LightTable) tables.get(tables.size() - 1);
    for (final String triggerName : triggerNames) {
      table.addTrigger(new LightTrigger(table, triggerName));
    }
    return this;
  }

  TestCatalogBuilder routine(final String name, final String... parameterNames) {
    // The specific name keeps overloaded routines with the same name apart
    final LightProcedure routine = new LightProcedure(schema, name, "specific" + routines.size());
    for (final String parameterName : parameterNames) {
      routine.addParameter(new LightProcedureParameter(routine, parameterName));
    }
    routines.add(routine);
    return this;
  }

  TestCatalogBuilder sequence(final String name) {
    sequences.add(new LightSequence(schema, name));
    return this;
  }

  TestCatalogBuilder synonym(final String name) {
    synonyms.add(new LightSynonym(schema, name));
    return this;
  }

  Catalog build() {
    return lightCatalog(tables, routines, sequences, synonyms);
  }
}
