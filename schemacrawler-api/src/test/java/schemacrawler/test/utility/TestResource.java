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

package schemacrawler.test.utility;

import java.nio.file.Path;
import static us.fatehi.utility.Utility.isBlank;
import static us.fatehi.utility.Utility.trimToEmpty;
import us.fatehi.utility.IOUtility;

public final class TestResource {

  static TestResource fromClasspath(final String resource) {
    final String resourceString;
    if (!isBlank(resource)) {
      resourceString = resource;
    } else {
      resourceString = null;
    }

    final boolean isAvailable = IOUtility.locateResource(resource) != null;

    return new TestResource(resourceString, isAvailable);
  }

  /**
   * Records the expected file path. The output file may or may not exist at the time this is
   * created.
   *
   * @param filePath Expected path
   * @return Test resource
   */
  static TestResource fromFilePath(final Path filePath) {
    final String resourceString;
    if (filePath != null) {
      resourceString = filePath.toString();
    } else {
      resourceString = null;
    }

    final boolean isAvailable = IOUtility.isFileReadable(filePath);

    return new TestResource(resourceString, isAvailable);
  }

  private final String resourceString;
  private final boolean isAvailable;

  private TestResource(String resourceString, boolean isAvailable) {
    this.resourceString = resourceString;
    this.isAvailable = isAvailable;
  }

  public String getResourceString() {
    return resourceString;
  }

  public boolean isAvailable() {
    return isAvailable;
  }

  @Override
  public String toString() {
    return trimToEmpty(resourceString);
  }
}
