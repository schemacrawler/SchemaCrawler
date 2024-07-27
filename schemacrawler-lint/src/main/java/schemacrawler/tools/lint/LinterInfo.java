/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2024, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.tools.lint;

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.trimToEmpty;

public final class LinterInfo {

  private final String linterId;
  private final String summary;
  private final String description;
  private final String className;

  public LinterInfo(
      final String linterId,
      final String summary,
      final String description,
      final String className) {
    this.linterId = requireNonNull(linterId, "No linter id provided");
    this.summary = trimToEmpty(summary);
    this.description = trimToEmpty(description);
    this.className = requireNonNull(className, "No class name provided");
  }

  public String getLinterId() {
    return linterId;
  }

  public String getSummary() {
    return summary;
  }

  public String getDescription() {
    return description;
  }

  public String getClassName() {
    return className;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    LinterInfo that = (LinterInfo) o;

    if (!linterId.equals(that.linterId)
        || !summary.equals(that.summary)
        || !description.equals(that.description)) {
      return false;
    }
    return className.equals(that.className);
  }

  @Override
  public int hashCode() {
    int result = linterId.hashCode();
    result = 31 * result + summary.hashCode();
    result = 31 * result + description.hashCode();
    result = 31 * result + className.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return "LinterInfo{"
        + "linterId='"
        + linterId
        + '\''
        + ", summary='"
        + summary
        + '\''
        + ", description='"
        + description
        + '\''
        + ", className='"
        + className
        + '\''
        + '}';
  }
}
