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
package schemacrawler.test.sitegen;


import static java.nio.file.Files.createDirectories;
import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;

import schemacrawler.test.utility.BaseSchemaCrawlerTest;
import schemacrawler.test.utility.TestDatabaseConnectionParameterResolver;

@ExtendWith(TestDatabaseConnectionParameterResolver.class)
public class BaseSiteVariationsTest
  extends
  BaseSchemaCrawlerTest
{

  protected Path resolveTargetFromRootPath(final TestInfo testInfo,
                                           final String relativePath)
    throws URISyntaxException,
    IOException
  {
    requireNonNull(testInfo, "No test info provided");
    final Path projectRootPath = getProjectRootPath(testInfo);
    final Path directory = projectRootPath
      .resolve(Paths.get("target", "_website")).resolve(relativePath)
      .normalize().toAbsolutePath();
    createDirectories(directory);
    return directory;
  }

  private Path getProjectRootPath(final TestInfo testInfo)
    throws URISyntaxException,
    IOException
  {
    requireNonNull(testInfo, "No test info provided");
    final Class<?> testClass = testInfo.getTestClass()
      .orElseThrow(() -> new RuntimeException("Could not find test class"));
    final Path codePath = Paths.get(testClass.getProtectionDomain()
      .getCodeSource().getLocation().toURI()).normalize().toAbsolutePath();
    final Path projectRoot = codePath.resolve("../..").normalize()
      .toAbsolutePath();
    Files.createDirectories(projectRoot);
    return projectRoot;
  }

}
