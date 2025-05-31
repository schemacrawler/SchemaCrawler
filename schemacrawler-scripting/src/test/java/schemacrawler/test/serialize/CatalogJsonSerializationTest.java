/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.test.serialize;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.fail;
import static schemacrawler.test.utility.DatabaseTestUtility.schemaRetrievalOptionsDefault;
import static schemacrawler.test.utility.DatabaseTestUtility.validateSchema;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.tools.utility.SchemaCrawlerUtility.getCatalog;
import static us.fatehi.utility.IOUtility.isFileReadable;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.test.utility.DatabaseTestUtility;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestWriter;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.formatter.serialize.JsonSerializedCatalog;
import schemacrawler.tools.options.Config;
import us.fatehi.utility.IOUtility;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@WithTestDatabase
@ResolveTestContext
public class CatalogJsonSerializationTest {

  private static final boolean DEBUG = true;
  private Path directory;

  @BeforeEach
  public void _setupDirectory(final TestContext testContext)
      throws IOException, URISyntaxException {
    if (directory != null) {
      return;
    }
    directory = testContext.resolveTargetFromRootPath(".");
  }

  @Test
  public void catalogSerializationWithJson(
      final TestContext testContext, final DatabaseConnectionSource dataSource) throws Exception {
    final SchemaCrawlerOptions schemaCrawlerOptions =
        DatabaseTestUtility.schemaCrawlerOptionsWithMaximumSchemaInfoLevel;

    final Catalog catalog =
        getCatalog(dataSource, schemaRetrievalOptionsDefault, schemaCrawlerOptions, new Config());
    validateSchema(catalog);

    final Path testOutputFile = IOUtility.createTempFilePath("sc_serialized_catalog", "json");
    try (final OutputStream out =
        Files.newOutputStream(testOutputFile, WRITE, CREATE, TRUNCATE_EXISTING)) {
      new JsonSerializedCatalog(catalog).save(out);
    }
    assertThat("Catalog was not serialized", isFileReadable(testOutputFile), is(true));

    if (DEBUG) {
      final Path copied = directory.resolve(testContext.testMethodFullName() + ".json");
      Files.copy(testOutputFile, copied, StandardCopyOption.REPLACE_EXISTING);
    }

    // Read generated JSON file, and assert values
    final ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    final JsonNode catalogNode = mapper.readTree(testOutputFile.toFile());
    assertThat(
        "Catalog schemas were not serialized",
        catalogNode.findPath("schemas"),
        not(instanceOf(MissingNode.class)));

    final JsonNode allTableColumnsNode = catalogNode.findPath("all-table-columns");
    assertThat(
        "Table columns were not serialized",
        allTableColumnsNode,
        not(instanceOf(MissingNode.class)));

    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      allTableColumnsNode
          .get(1)
          .elements()
          .forEachRemaining(
              columnNode -> {
                final JsonNode columnFullnameNode = columnNode.get("full-name");
                if (columnFullnameNode != null) {
                  out.println("- column @uuid: " + columnNode.get("@uuid").asText());
                  out.println("  " + columnFullnameNode.asText());
                } else {
                  fail("Table column object not found - " + columnNode.asText());
                }
              });
    }

    assertThat(
        outputOf(testout), hasSameContentAs(classpathResource(testContext.testMethodFullName())));
  }
}
