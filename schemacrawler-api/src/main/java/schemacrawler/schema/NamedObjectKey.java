/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2021, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.schema;

import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class NamedObjectKey implements Serializable {

  private static final long serialVersionUID = -5008609072012459037L;

  private final List<String> key;

  public NamedObjectKey(final String... key) {
    this(Arrays.asList(key));
  }

  private NamedObjectKey(final List<String> key) {
    requireNonNull(key, "No key provided");
    if (key.isEmpty()) {
      throw new IllegalArgumentException("No key values provided");
    }
    this.key = new ArrayList<>(key);
  }

  public NamedObjectKey add(final String name) {
    final List<String> newKey = new ArrayList<>(key);
    newKey.add(name);
    return new NamedObjectKey(newKey);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof NamedObjectKey)) {
      return false;
    }
    final NamedObjectKey other = (NamedObjectKey) obj;
    return Objects.equals(key, other.key);
  }

  @Override
  public int hashCode() {
    return Objects.hash(key);
  }

  @Override
  public String toString() {
    return "[key=" + String.join("/", key) + "]";
  }
}
