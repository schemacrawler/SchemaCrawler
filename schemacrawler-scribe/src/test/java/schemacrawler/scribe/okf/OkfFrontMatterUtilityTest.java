/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */
package schemacrawler.scribe.okf;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static schemacrawler.loader.utility.TableRowCountsUtility.TABLE_ROW_COUNT_KEY;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import org.junit.jupiter.api.Test;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaReference;
import schemacrawler.test.utility.crawl.LightPrimaryKey;
import schemacrawler.test.utility.crawl.LightTable;
import schemacrawler.test.utility.crawl.LightTrigger;
import schemacrawler.tools.utility.EntityModelType;
import schemacrawler.tools.utility.TableImportanceUtility;
import schemacrawler.tools.utility.TableTraits;

public class OkfFrontMatterUtilityTest {

  @Test
  public void tableAttributesDeriveExpectedFlags() {
    final LightTable delegate = new LightTable(new SchemaReference("PUBLIC", "BOOKS"), "BOOKS");
    delegate.setPrimaryKey(new LightPrimaryKey(delegate.addColumn("PK_COL")));
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

    final TableTraits attributes = TableImportanceUtility.tableTraitsfrom(table);
    assertThat(attributes.noPrimaryKey(), is(nullValue()));
    assertThat(attributes.selfReferencing(), is(Boolean.TRUE));
    assertThat(attributes.hasTriggers(), is(Boolean.TRUE));
    assertThat(attributes.emptyTable(), is(Boolean.TRUE));
    assertThat(attributes.entityModelType(), is(EntityModelType.strong_entity));
  }

  @Test
  public void tableAttributesDoNotMarkEmptyWhenRowCountUnavailable() {
    final LightTable table = new LightTable(new SchemaReference("PUBLIC", "BOOKS"), "BOOKS");
    final TableTraits attributes = TableImportanceUtility.tableTraitsfrom(table);
    assertThat(attributes.emptyTable(), is(nullValue()));
  }
}
