/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static schemacrawler.test.utility.CommandlineTestUtility.commandlineExecution;
import static schemacrawler.test.utility.ExecutableTestUtility.hasSameContentAndTypeAs;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.outputOf;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import schemacrawler.schemacrawler.InfoLevel;
import schemacrawler.test.utility.AssertNoSystemOutOutput;
import schemacrawler.test.utility.DatabaseConnectionInfo;
import schemacrawler.test.utility.TestUtility;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.options.OutputFormat;

// @AssertNoSystemErrOutput
@AssertNoSystemOutOutput
@WithTestDatabase
public abstract class AbstractTitleTest {

  private static final String TITLE_OUTPUT = "title_output/";

  @BeforeAll
  public static void clean() throws Exception {
    TestUtility.clean(TITLE_OUTPUT);
  }

  @Test
  public void commandLineWithTitle(final DatabaseConnectionInfo connectionInfo) throws Exception {
    assertAll(
        outputFormats()
            .flatMap(
                outputFormat ->
                    commands()
                        .map(
                            command ->
                                () -> {
                                  final String referenceFile = referenceFile(command, outputFormat);

                                  final Map<String, String> argsMap = new HashMap<>();
                                  argsMap.put("--schemas", ".*\\.(?!FOR_LINT).*");
                                  argsMap.put("--info-level", InfoLevel.standard.name());
                                  argsMap.put(
                                      "--title", "Database Design for Books and Publishers");

                                  assertThat(
                                      outputOf(
                                          commandlineExecution(
                                              connectionInfo, command, argsMap, outputFormat)),
                                      hasSameContentAndTypeAs(
                                          classpathResource(TITLE_OUTPUT + referenceFile),
                                          outputFormat));
                                })));
  }

  protected Stream<String> commands() {
    return Arrays.asList("schema", "list").stream();
  }

  protected abstract Stream<? extends OutputFormat> outputFormats();

  private String referenceFile(final String command, final OutputFormat outputFormat) {
    final String referenceFile =
        "commandLineWithTitle_%s.%s".formatted(command, outputFormat.getFormat());
    return referenceFile;
  }
}
