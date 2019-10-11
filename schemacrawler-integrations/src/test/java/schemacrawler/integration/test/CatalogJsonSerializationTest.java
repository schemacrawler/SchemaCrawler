/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2019, Sualeh Fatehi <sualeh@hotmail.com>.
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
import static schemacrawler.test.utility.FileHasContent.*;
import static schemacrawler.utility.SchemaCrawlerUtility.getCatalog;

import java.sql.Connection;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.test.utility.*;
import schemacrawler.tools.integration.serialization.JsonSerializedCatalog;

@ExtendWith(TestDatabaseConnectionParameterResolver.class)
@ExtendWith(TestContextParameterResolver.class)
public class CatalogJsonSerializationTest
{

  @Test
  public void catalogSerializationWithJson(final TestContext testContext,
                                           final Connection connection)
    throws Exception
  {
    final SchemaCrawlerOptions schemaCrawlerOptions = DatabaseTestUtility.schemaCrawlerOptionsWithMaximumSchemaInfoLevel;

    final Catalog catalog = getCatalog(connection, schemaCrawlerOptions);

    final TestOutputStream testout = new TestOutputStream();
    try (final TestOutputStream out = testout)
    {
      new JsonSerializedCatalog(catalog).save(out);
    }
    assertThat(outputOf(testout),
               hasSameContentAs(classpathResource(testContext
                                                    .testMethodFullName())));
  }

}
