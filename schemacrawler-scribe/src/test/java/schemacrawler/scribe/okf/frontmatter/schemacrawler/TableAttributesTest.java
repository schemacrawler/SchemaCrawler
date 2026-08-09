/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */
package schemacrawler.scribe.okf.frontmatter.schemacrawler;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static schemacrawler.loader.utility.TableRowCountsUtility.TABLE_ROW_COUNT_KEY;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import org.junit.jupiter.api.Test;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaReference;
import schemacrawler.scribe.okf.FrontMatterUtility;
import schemacrawler.test.utility.crawl.LightTable;
import schemacrawler.test.utility.crawl.LightTrigger;

public class TableAttributesTest {

  @Test
  public void derivesAttributesFromTable() {
    final LightTable delegate = new LightTable(new SchemaReference("PUBLIC", "BOOKS"), "BOOKS");
    delegate.addTrigger(new LightTrigger(delegate, "TRG_BOOKS"));
    delegate.setAttribute(TABLE_ROW_COUNT_KEY, 0L);

    final Table table =
        (Table)
            Proxy.newProxyInstance(
                Table.class.getClassLoader(),
                new Class<?>[] {Table.class},
                (proxy, method, args) -> {
                  if ("isSelfReferencing".equals(method.getName())) {
                    return true;
                  }
                  try {
                    return method.invoke(delegate, args);
                  } catch (final InvocationTargetException e) {
                    throw e.getCause();
                  }
                });

    final TableAttributes attributes = FrontMatterUtility.tableAttributes(table, true);

    assertThat(attributes.noPrimaryKey(), is(Boolean.TRUE));
    assertThat(attributes.noForeignKeys(), is(Boolean.TRUE));
    assertThat(attributes.noIndexes(), is(Boolean.TRUE));
    assertThat(attributes.selfReferencing(), is(Boolean.TRUE));
    assertThat(attributes.hasTriggers(), is(Boolean.TRUE));
    assertThat(attributes.emptyTable(), is(Boolean.TRUE));
    assertThat(attributes.bridgeTable(), is(Boolean.TRUE));
  }

  @Test
  public void handlesNullTable() {
    final TableAttributes attributes = FrontMatterUtility.tableAttributes(null, false);

    assertThat(attributes.noPrimaryKey(), is(nullValue()));
    assertThat(attributes.noForeignKeys(), is(nullValue()));
    assertThat(attributes.noIndexes(), is(nullValue()));
    assertThat(attributes.selfReferencing(), is(nullValue()));
    assertThat(attributes.hasTriggers(), is(nullValue()));
    assertThat(attributes.emptyTable(), is(nullValue()));
    assertThat(attributes.bridgeTable(), is(nullValue()));
  }
}
