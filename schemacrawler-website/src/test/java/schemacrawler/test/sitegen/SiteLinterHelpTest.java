/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2022, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.test.sitegen;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
    final Path path = Paths.get(directory.toString(), "lint.md");

    final String linterHelpMarkdown = new LinterHelp(true).get()[0];
    Files.write(path, linterHelpMarkdown.getBytes(UTF_8));
  }
}
