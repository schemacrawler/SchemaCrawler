/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
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

import static java.util.Objects.requireNonNull;

import java.nio.file.Path;
import java.util.Optional;

final class TestResource {
  private final Path path;
  private final String resource;

  public TestResource() {
    path = null;
    resource = null;
  }

  TestResource(final Path path) {
    requireNonNull(path, "No path provided");
    this.path = path;
    resource = null;
  }

  TestResource(final String resource) {
    requireNonNull(resource, "No resource provided");
    path = null;
    this.resource = resource;
  }

  public Optional<String> getClasspathResource() {
    return Optional.ofNullable(resource);
  }

  public Optional<Path> getFileResource() {
    return Optional.ofNullable(path);
  }

  @Override
  public String toString() {
    if (path != null) {
      return String.format("file: <%s>", path);
    } else if (resource != null) {
      return String.format("classpath: <%s>", resource);
    } else {
      return "<empty>";
    }
  }
}
