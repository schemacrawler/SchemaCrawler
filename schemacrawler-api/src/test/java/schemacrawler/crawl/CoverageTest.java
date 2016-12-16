/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
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
    final NamedObjectList<NamedObject> list = new NamedObjectList<>();
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

  @Test(expected = NullPointerException.class)
  public void namedObjectListNull()
  {
    final NamedObjectList<NamedObject> list = new NamedObjectList<>();
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
