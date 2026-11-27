/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.vocabulary;

import java.util.HashMap;
import java.util.Map;
import schemacrawler.schema.NamedObjectKey;
import schemacrawler.schemacrawler.SchemaReference;
import schemacrawler.test.utility.crawl.LightColumn;
import schemacrawler.test.utility.crawl.LightProcedure;
import schemacrawler.test.utility.crawl.LightProcedureParameter;
import schemacrawler.test.utility.crawl.LightTable;
import schemacrawler.test.utility.crawl.LightTrigger;

/**
 * Builds hand-made {@link Names} from light objects. The kind only labels the name, so a top-level
 * object is a table when its kind is a table, and otherwise a routine, which has a key that is
 * unique in the fixture.
 */
final class NamesFixture {

  private final SchemaReference schema = new SchemaReference();
  private final Names.Builder builder = Names.builder();
  private final Map<NamedObjectKey, LightTable> tables = new HashMap<>();
  private final Map<NamedObjectKey, LightProcedure> routines = new HashMap<>();
  private int count;

  /** Adds a top-level object, such as a table, routine, sequence or synonym. */
  NamedObjectKey add(final ObjectKind kind, final String name) {
    if (kind == ObjectKind.TABLE) {
      final LightTable table = new LightTable(schema, name);
      tables.put(table.key(), table);
      builder.add(kind, table);
      return table.key();
    }
    final LightProcedure routine = new LightProcedure(schema, name, "specific" + count++);
    routines.put(routine.key(), routine);
    builder.add(kind, routine);
    return routine.key();
  }

  /** Adds an object contained in a table or routine, such as a column or a parameter. */
  NamedObjectKey add(final ObjectKind kind, final NamedObjectKey parentKey, final String name) {
    if (parentKey == null) {
      return add(kind, name);
    }
    final LightTable table = tables.get(parentKey);
    if (table != null) {
      if (kind == ObjectKind.TRIGGER) {
        final LightTrigger trigger = new LightTrigger(table, name);
        table.addTrigger(trigger);
        builder.add(kind, trigger);
        return trigger.key();
      }
      final LightColumn column = table.addColumn(name);
      builder.add(kind, column);
      return column.key();
    }
    final LightProcedure routine = routines.get(parentKey);
    if (routine == null) {
      throw new IllegalArgumentException("Unknown parent " + parentKey);
    }
    final LightProcedureParameter parameter = new LightProcedureParameter(routine, name);
    routine.addParameter(parameter);
    builder.add(kind, parameter);
    return parameter.key();
  }

  Names build() {
    return builder.build();
  }
}
