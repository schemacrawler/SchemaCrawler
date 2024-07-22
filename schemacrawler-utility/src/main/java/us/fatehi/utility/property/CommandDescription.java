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

package us.fatehi.utility.property;

import static java.util.Comparator.comparing;
import static java.util.Comparator.nullsLast;
import static java.util.Objects.compare;
import static us.fatehi.utility.Utility.isBlank;
import static us.fatehi.utility.Utility.requireNotBlank;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;

public final class CommandDescription implements Serializable, Comparable<CommandDescription> {

  private static final long serialVersionUID = 2444083929278551904L;

  private static Comparator<CommandDescription> comparator =
      nullsLast(comparing(CommandDescription::getName, String.CASE_INSENSITIVE_ORDER));

  private final String name;
  private final String description;

  public CommandDescription(final String name, final String description) {
    this.name = requireNotBlank(name, "Command name not provided");

    if (isBlank(description)) {
      this.description = null;
    } else {
      this.description = description;
    }
  }

  @Override
  public int compareTo(final CommandDescription otherProperty) {
    return compare(this, otherProperty, comparator);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof CommandDescription)) {
      return false;
    }
    final CommandDescription other = (CommandDescription) obj;
    if (!Objects.equals(name, other.name)) {
      return false;
    }
    return true;
  }

  public String getDescription() {
    return description == null ? "" : description;
  }

  public String getName() {
    return name;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (name == null ? 0 : name.hashCode());
    return result;
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append(name);
    if (description != null) {
      builder.append(" - ").append(description);
    }
    return builder.toString();
  }
}
