/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.crawl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schema.TableType;
import schemacrawler.schemacrawler.SchemaReference;

public class NamedObjectTest {

  public static final TableType TABLE = new TableType("TABLE");

  @Test
  public void tableNames() {
    final String[] schemaNames = new String[] {"DBO", "PUBLIC"};
    final String[] tableNames = {
      "CUSTOMER", "CUSTOMERLIST", "INVOICE", "ITEM", "PRODUCT", "SUPPLIER"
    };

    MutableTable table;
    final NamedObjectList<Table> tables = new NamedObjectList<>();

    for (final String schemaName : schemaNames) {
      final Schema schema = new SchemaReference("CATALOG", schemaName);
      for (final String tableName : tableNames) {
        table = new MutableTable(schema, tableName);
        table.setTableType(TABLE);
        tables.add(table);
      }
    }
    assertThat(tables.size(), is(schemaNames.length * tableNames.length));
  }
}
