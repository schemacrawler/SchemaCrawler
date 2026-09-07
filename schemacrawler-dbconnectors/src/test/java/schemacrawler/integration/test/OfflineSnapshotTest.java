/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.integration.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static us.fatehi.test.utility.TestUtility.flattenCommandlineArgs;
import static us.fatehi.test.utility.extensions.FileHasContent.classpathResource;
import static us.fatehi.test.utility.extensions.FileHasContent.hasSameContentAs;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import schemacrawler.Main;
import schemacrawler.test.utility.TestSerializeCatalogUtility;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.command.text.schema.options.TextOutputFormat;
import us.fatehi.test.utility.TestWriter;
import us.fatehi.test.utility.extensions.WithSystemProperty;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@WithTestDatabase
public class OfflineSnapshotTest {

  private static final String OFFLINE_EXECUTABLE_OUTPUT = "offline_executable_output/";

  private Path serializedCatalogFile;

  @Test
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "hsqldb")
  public void offlineSnapshotCommandLine() throws Exception {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      final Map<String, String> argsMap = new HashMap<>();
      argsMap.put("--server", "offline");
      argsMap.put("--database", serializedCatalogFile.toString());

      argsMap.put("--no-info", Boolean.FALSE.toString());
      argsMap.put("--info-level", "maximum");
      argsMap.put("--routines", ".*");
      argsMap.put("--command", "details");
      argsMap.put("--output-format", TextOutputFormat.text.getFormat());
      argsMap.put("--output-file", out.toString());

      Main.main(flattenCommandlineArgs(argsMap));
    }

    final String expectedResource = "details.txt";
    assertThat(
        outputOf(testout),
        hasSameContentAs(classpathResource(OFFLINE_EXECUTABLE_OUTPUT + expectedResource)));
  }

  @Test
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "hsqldb")
  public void offlineSnapshotCommandLineWithFilters() throws Exception {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      final Map<String, String> argsMap = new HashMap<>();
      argsMap.put("--server", "offline");
      argsMap.put("--database", serializedCatalogFile.toString());

      argsMap.put("--no-info", "true");
      argsMap.put("--info-level", "maximum");
      argsMap.put("--command", "details");
      argsMap.put("--output-format", TextOutputFormat.text.getFormat());
      argsMap.put("--routines", "");
      argsMap.put("--tables", ".*SALES");
      argsMap.put("--output-file", out.toString());

      Main.main(flattenCommandlineArgs(argsMap));
    }
    assertThat(
        outputOf(testout),
        hasSameContentAs(classpathResource(OFFLINE_EXECUTABLE_OUTPUT + "offlineWithFilters.txt")));
  }

  @Test
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "hsqldb")
  public void offlineSnapshotCommandLineWithSchemaFilters() throws Exception {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      final Map<String, String> argsMap = new HashMap<>();
      argsMap.put("--server", "offline");
      argsMap.put("--database", serializedCatalogFile.toString());

      argsMap.put("--no-info", "true");
      argsMap.put("--info-level", "maximum");
      argsMap.put("--routines", ".*");
      argsMap.put("--command", "list");
      argsMap.put("--output-format", TextOutputFormat.text.getFormat());
      argsMap.put("--schemas", "PUBLIC.BOOKS");
      argsMap.put("--output-file", out.toString());

      final List<String> argsList = new ArrayList<>();
      for (final Map.Entry<String, String> arg : argsMap.entrySet()) {
        argsList.add("-%s=%s".formatted(arg.getKey(), arg.getValue()));
      }

      Main.main(flattenCommandlineArgs(argsMap));
    }
    assertThat(
        outputOf(testout),
        hasSameContentAs(
            classpathResource(OFFLINE_EXECUTABLE_OUTPUT + "offlineWithSchemaFilters.txt")));
  }

  @BeforeEach
  public void serializeCatalog(final DatabaseConnectionSource connectionSource) {
    serializedCatalogFile = TestSerializeCatalogUtility.serializeCatalog(connectionSource);
  }
}
