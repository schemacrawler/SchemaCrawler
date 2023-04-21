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

package schemacrawler.schema;

import static us.fatehi.utility.Utility.convertForComparison;

import java.io.Serializable;
import java.util.Arrays;

public final class NamedObjectKey implements Serializable {

  private static final long serialVersionUID = -5008609072012459037L;

  private final String[] key;

  public NamedObjectKey(final String... key) {
    if (key == null || key.length == 0) {
      this.key = new String[0];
    } else {
      this.key = Arrays.copyOf(key, key.length);
    }
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
    return Arrays.equals(key, other.key);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(key);
  }

  public String slug() {
    if (key.length == 0) {
      return "";
    }
    final String name = key[key.length - 1];
    return convertForComparison(name) + "_" + Integer.toHexString(hashCode());
  }

  @Override
  public String toString() {
    return "{\"key\": \"" + String.join("/", key) + "\"}";
  }

  public NamedObjectKey with(final String name) {
    final int currentLength = key.length;
    final String[] newKey = Arrays.copyOf(key, currentLength + 1);
    newKey[currentLength] = name;
    return new NamedObjectKey(newKey);
  }
}
