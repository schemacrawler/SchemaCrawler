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
