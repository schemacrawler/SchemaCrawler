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
import static org.hamcrest.Matchers.*;
import static schemacrawler.utility.SchemaCrawlerUtility.getCatalog;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.test.utility.DatabaseTestUtility;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestContextParameterResolver;
import schemacrawler.test.utility.TestDatabaseConnectionParameterResolver;
import schemacrawler.tools.integration.serialization.JsonSerializedCatalog;
import sf.util.IOUtility;

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

    final Path testOutputFile = IOUtility
      .createTempFilePath("sc_serialized_catalog", "json");
    try (final OutputStream out = new FileOutputStream(testOutputFile.toFile()))
    {
      new JsonSerializedCatalog(catalog).save(out);
    }
    assertThat("Catalog was not serialized",
               Files.size(testOutputFile),
               greaterThan(0L));

    // Read generated JSON file, and assert values
    final ObjectMapper objectMapper = new ObjectMapper();
    final JsonNode rootNode = objectMapper.readTree(testOutputFile.toFile());
    final JsonNode catalogNode = rootNode.path("catalog");
    assertThat("Catalog was not serialized", catalogNode, not(nullValue()));
    assertThat("Catalog schemas were not serialized",
               catalogNode.findPath("schemas"),
               not(nullValue()));
    assertThat("Catalog tables were not serialized",
               catalogNode.findPath("tables"),
               not(nullValue()));
  }

}
