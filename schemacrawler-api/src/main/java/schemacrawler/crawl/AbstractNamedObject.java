/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.crawl;

import static schemacrawler.utility.NamedObjectSort.alphabetical;
import java.util.Objects;
import static java.util.Objects.compare;
import static java.util.Objects.hash;
import schemacrawler.schema.NamedObject;
import schemacrawler.schema.NamedObjectKey;

/** Represents a named object. */
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
    return compare(this, obj, alphabetical);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if ((obj == null) || !(obj instanceof NamedObject)) {
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
    return hash(name);
  }

  @Override
  public NamedObjectKey key() {
    buildKey();
    return key;
  }

  /** {@inheritDoc} */
  @Override
  public final String toString() {
    return getFullName();
  }

  private void buildKey() {
    if (key != null) {
      return;
    }
    key = new NamedObjectKey(name);
  }
}
