/*
 * SchemaCrawler
 * Copyright (c) 2000-2013, Sualeh Fatehi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package schemacrawler.crawl;


import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.Test;

import schemacrawler.schema.NamedObject;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.test.utility.BaseDatabaseTest;

public class CoverageTest
  extends BaseDatabaseTest
{

  @Test
  public void namedObjectList()
  {
    final NamedObjectList<NamedObject> list = new NamedObjectList<NamedObject>();
    list.add(new AbstractNamedObject("name1")
    {

      private static final long serialVersionUID = -514565049545540452L;

    });
    list.add(new AbstractNamedObject("name2")
    {

      private static final long serialVersionUID = 6176088733525976950L;

    });
    assertEquals("NamedObjectList size", 2, list.size());
    assertEquals("NamedObjectList toString", "name1, name2", list.toString());
  }

  @Test(expected = IllegalArgumentException.class)
  public void namedObjectListNull()
  {
    final NamedObjectList<NamedObject> list = new NamedObjectList<NamedObject>();
    list.add(null);
  }

  @Test(expected = SQLException.class)
  public void retrieverConnection()
    throws SQLException
  {
    new RetrieverConnection(null, null);
  }

  @Test(expected = SQLException.class)
  public void retrieverConnectionClosed()
    throws SQLException, SchemaCrawlerException
  {
    final Connection connection = getConnection();
    connection.close();
    new RetrieverConnection(connection, null);
  }
}
