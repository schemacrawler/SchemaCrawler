/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.integration.test;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static schemacrawler.utility.SchemaCrawlerUtility.matchSchemaRetrievalOptions;

import java.sql.Connection;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import schemacrawler.schemacrawler.DatabaseServerType;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.test.utility.TestDatabaseConnectionParameterResolver;

@ExtendWith(TestDatabaseConnectionParameterResolver.class)
public class MatchSchemaRetrievalOptionsTest
{

  @DisplayName(
    "\"SC_WITHOUT_DATABASE_PLUGIN\" is set to \"hsqldb\" - use \"unknown\" plugin")
  @Test
  public void matchSchemaRetrievalOptions1(final Connection connection)
    throws Exception
  {
    try
    {
      System.setProperty("SC_WITHOUT_DATABASE_PLUGIN", "hsqldb");
      final SchemaRetrievalOptions schemaRetrievalOptions =
        matchSchemaRetrievalOptions(connection);
      final DatabaseServerType databaseServerType =
        schemaRetrievalOptions.getDatabaseServerType();
      assertThat(databaseServerType.isUnknownDatabaseSystem(), is(true));
    }
    finally
    {
      System.clearProperty("SC_WITHOUT_DATABASE_PLUGIN");
    }
  }

  @DisplayName(
    "\"SC_WITHOUT_DATABASE_PLUGIN\" is set to \"newdb\" - use \"hsqldb\" plugin")
  @Test
  public void matchSchemaRetrievalOptions2(final Connection connection)
    throws Exception
  {
    try
    {
      System.setProperty("SC_WITHOUT_DATABASE_PLUGIN", "newdb");
      final SchemaRetrievalOptions schemaRetrievalOptions =
        matchSchemaRetrievalOptions(connection);
      final DatabaseServerType databaseServerType =
        schemaRetrievalOptions.getDatabaseServerType();
      assertThat(databaseServerType.getDatabaseSystemIdentifier(),
                 is("hsqldb"));
    }
    finally
    {
      System.clearProperty("SC_WITHOUT_DATABASE_PLUGIN");
    }
  }

  @DisplayName("\"SC_WITHOUT_DATABASE_PLUGIN\" is not set - use default plugin")
  @Test
  public void matchSchemaRetrievalOptions0(final Connection connection)
    throws Exception
  {
    try
    {
      System.setProperty("SC_WITHOUT_DATABASE_PLUGIN", "newdb");
      final SchemaRetrievalOptions schemaRetrievalOptions =
        matchSchemaRetrievalOptions(connection);
      final DatabaseServerType databaseServerType =
        schemaRetrievalOptions.getDatabaseServerType();
      assertThat(databaseServerType.getDatabaseSystemIdentifier(),
                 is("hsqldb"));
    }
    finally
    {
      System.clearProperty("SC_WITHOUT_DATABASE_PLUGIN");
    }
  }

}
