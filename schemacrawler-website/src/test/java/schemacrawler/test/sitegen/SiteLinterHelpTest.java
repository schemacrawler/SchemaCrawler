/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test.sitegen;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestContext;
import schemacrawler.tools.lint.LinterHelp;

@ResolveTestContext
@EnabledIfSystemProperty(named = "distrib", matches = "^((?!(false|no)).)*$")
public class SiteLinterHelpTest {

  private Path directory;

  @BeforeEach
  public void _setupDirectory(final TestContext testContext)
      throws IOException, URISyntaxException {
    if (directory != null) {
      return;
    }
    directory = testContext.resolveTargetFromRootPath("site-markdown");
  }

  @Test
  public void linterHelp() throws Exception {
    final Path path = Path.of(directory.toString(), "lint.md");

    final String linterHelpMarkdown = new LinterHelp(true).get()[0];
    Files.write(path, linterHelpMarkdown.getBytes(UTF_8));
  }
}
