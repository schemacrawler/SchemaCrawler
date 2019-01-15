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

package schemacrawler.test.utility;


import static java.nio.file.Files.createDirectories;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.util.ToStringBuilder;

public final class TestContext
{

  private final Optional<Class<?>> optionalTestClass;
  private final Optional<Method> optionalTestMethod;

  TestContext(ExtensionContext extensionContext)
  {
    this.optionalTestClass = extensionContext.getTestClass();
    this.optionalTestMethod = extensionContext.getTestMethod();
  }

  public String currentMethodFullName()
  {
    return optionalTestMethod
      .map(method -> String.format("%s.%s",
                                   method.getDeclaringClass().getSimpleName(),
                                   method.getName()))
      .orElseThrow(() -> new RuntimeException("Could not find test method"));
  }

  public String currentMethodName()
  {
    return optionalTestMethod.map(method -> method.getName())
      .orElseThrow(() -> new RuntimeException("Could not find test method"));
  }

  @Override
  public String toString()
  {
    return new ToStringBuilder(this)
      .append("testClass", nullSafeGet(this.optionalTestClass))
      .append("testMethod", nullSafeGet(this.optionalTestMethod)).toString();
  }

  private static Object nullSafeGet(Optional<?> optional)
  {
    return optional != null? optional.orElse(null): null;
  }

  public Path resolveTargetFromRootPath(final String relativePath)
    throws URISyntaxException, IOException
  {
    final Path projectRootPath = getProjectRootPath();
    final Path directory = projectRootPath
      .resolve(Paths.get("target", "_website")).resolve(relativePath)
      .normalize().toAbsolutePath();
    createDirectories(directory);
    return directory;
  }

  private Path getProjectRootPath()
    throws URISyntaxException, IOException
  {
    final Class<?> testClass = this.optionalTestClass
      .orElseThrow(() -> new RuntimeException("Could not find test class"));
    final Path codePath = Paths.get(testClass.getProtectionDomain()
      .getCodeSource().getLocation().toURI()).normalize().toAbsolutePath();
    final Path projectRoot = codePath.resolve("../..").normalize()
      .toAbsolutePath();
    Files.createDirectories(projectRoot);
    return projectRoot;
  }

}
