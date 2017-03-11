/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
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


import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class ProjectRoot
  implements TestRule
{

  private Path projectRoot;

  @Override
  public Statement apply(final Statement base, final Description description)
  {
    return new Statement()
    {
      @Override
      public void evaluate()
        throws Throwable
      {
        projectRoot = getDirectory(description);
        base.evaluate();
      }

      private Path getDirectory(final Description description)
        throws URISyntaxException, IOException
      {
        final Class<?> testClass = description.getTestClass();
        final Path codePath = Paths.get(testClass.getProtectionDomain()
          .getCodeSource().getLocation().toURI()).normalize().toAbsolutePath();
        final Path projectRoot = codePath.resolve("../..").normalize()
          .toAbsolutePath();
        Files.createDirectories(projectRoot);
        return projectRoot;
      }
    };
  }

  public Path getProjectRootPath()
  {
    return projectRoot;
  }

}
