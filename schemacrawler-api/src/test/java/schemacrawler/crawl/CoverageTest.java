/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2022, Sualeh Fatehi <sualeh@hotmail.com>.
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
