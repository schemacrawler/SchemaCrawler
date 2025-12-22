/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.crawl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.Connection;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import schemacrawler.schema.NamedObject;
import schemacrawler.test.utility.WithTestDatabase;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@WithTestDatabase
public class CoverageTest {

  @Test
  public void namedObjectList() {
    final NamedObjectList<NamedObject> list = new NamedObjectList<>();
    list.add(
        new AbstractNamedObject("name1") {

          private static final long serialVersionUID = -514565049545540452L;
        });
    list.add(
        new AbstractNamedObject("name2") {

          private static final long serialVersionUID = 6176088733525976950L;
        });
    assertThat(list.size(), equalTo(2));
    assertThat(list.toString(), equalTo("[\"name1\", \"name2\"]"));
  }

  @Test
  public void namedObjectListNull() {
    assertThrows(
        NullPointerException.class,
        () -> {
          final NamedObjectList<NamedObject> list = new NamedObjectList<>();
          list.add(null);
        });
  }

  @Test
  public void retrieverConnection() throws SQLException {
    assertThrows(NullPointerException.class, () -> new RetrieverConnection(null, null));
  }

  @Test
  public void retrieverConnectionClosed(final DatabaseConnectionSource dataSource) {
    assertThrows(
        NullPointerException.class,
        () -> {
          final Connection connection = dataSource.get();
          connection.close();
          new RetrieverConnection(dataSource, null);
        });
  }
}
