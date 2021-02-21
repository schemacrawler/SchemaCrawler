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

package schemacrawler.crawl;

import java.util.Objects;

import schemacrawler.schema.NamedObject;
import schemacrawler.schema.NamedObjectKey;
import schemacrawler.utility.NamedObjectSort;

/**
 * Represents a named object.
 *
 * @author Sualeh Fatehi
 */
abstract class AbstractNamedObject implements NamedObject {

  private static final long serialVersionUID = -1486322887991472729L;

  private final String name;
  private transient NamedObjectKey key;

  /**
   * Effective Java - Item 17 - Minimize Mutability - Package-private constructors make a class
   * effectively final
   *
   * @param name Name of the named object
   */
  AbstractNamedObject(final String name) {
    this.name = name;
  }

  /** {@inheritDoc} */
  @Override
  public int compareTo(final NamedObject obj) {
    if (obj == null) {
      return -1;
    }

    return NamedObjectSort.alphabetical.compare(this, obj);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof AbstractNamedObject)) {
      return false;
    }
    return Objects.equals(name, ((NamedObject) obj).getName());
  }

  /** {@inheritDoc} */
  @Override
  public String getFullName() {
    return getName();
  }

  /** {@inheritDoc} */
  @Override
  public final String getName() {
    return name;
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }

  /** {@inheritDoc} */
  @Override
  public final String toString() {
    return getFullName();
  }

  @Override
  public NamedObjectKey toUniqueLookupKey() {
    buildKey();
    return key;
  }

  private void buildKey() {
    if (key != null) {
      return;
    }
    this.key = new NamedObjectKey(name);
  }
}
