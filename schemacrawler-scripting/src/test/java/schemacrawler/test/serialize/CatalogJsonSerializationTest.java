/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
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
import static schemacrawler.tools.utility.SchemaCrawlerUtility.getCatalog;
import static us.fatehi.test.utility.extensions.FileHasContent.classpathResource;
import static us.fatehi.test.utility.extensions.FileHasContent.hasSameContentAs;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;
import static us.fatehi.utility.IOUtility.isFileReadable;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.test.utility.DatabaseTestUtility;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.formatter.serialize.JsonSerializedCatalog;
import schemacrawler.tools.options.ConfigUtility;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.MissingNode;
import us.fatehi.test.utility.TestWriter;
import us.fatehi.test.utility.extensions.ResolveTestContext;
import us.fatehi.test.utility.extensions.TestContext;
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
        getCatalog(
            dataSource,
            schemaRetrievalOptionsDefault,
            schemaCrawlerOptions,
            ConfigUtility.newConfig());
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
          .values()
          .forEach(
              columnNode -> {
                final JsonNode columnFullnameNode = columnNode.get("full-name");
                if (columnFullnameNode != null) {
                  out.println("- column @uuid: " + columnNode.get("@uuid").asString());
                  out.println("  " + columnFullnameNode.asString());
                } else {
                  fail("Table column object not found - " + columnNode.asString());
                }
              });
    }

    assertThat(
        outputOf(testout), hasSameContentAs(classpathResource(testContext.testMethodFullName())));
  }
}
