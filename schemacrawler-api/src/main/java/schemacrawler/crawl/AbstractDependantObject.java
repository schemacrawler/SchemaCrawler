/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.crawl;

import static java.util.Objects.hash;
import static java.util.Objects.requireNonNull;

import java.io.Serial;
import java.util.Objects;
import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.DependantObject;
import schemacrawler.schema.NamedObjectKey;
import schemacrawler.schemacrawler.Identifiers;

/**
 * Represents the dependant of a database object, such as a column or an index, which are dependants
 * of a table.
 */
abstract class AbstractDependantObject<D extends DatabaseObject> extends AbstractDatabaseObject
    implements DependantObject<D> {

  @Serial private static final long serialVersionUID = -4327208866052082457L;

  private final DatabaseObjectReference<D> parent;
  private transient NamedObjectKey key;
  private transient String fullName;
  private transient String shortName;

  /**
   * Effective Java - Item 17 - Minimize Mutability - Package-private constructors make a class
   * effectively final
   *
   * @param parent Parent of this object
   * @param name Name of the named object
   */
  AbstractDependantObject(final DatabaseObjectReference<D> parent, final String name) {
    super(
        requireNonNull(parent, "Parent of dependant object not provided").get().getSchema(), name);
    this.parent = parent;
  }

  @Override
  public final boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj) || !(obj instanceof DependantObject)) {
      return false;
    }
    return Objects.equals(parent, ((DependantObject<?>) obj).getParent());
  }

  /** {@inheritDoc} */
  @Override
  public final String getFullName() {
    buildFullName();
    return fullName;
  }

  /** {@inheritDoc} */
  @Override
  public final D getParent() {
    // Check if parent is null - this can happen if the object is in an
    // incomplete state during deserialization
    if (parent == null) {
      return null;
    }
    return parent.get();
  }

  @Override
  public final String getShortName() {
    buildShortName();
    return shortName;
  }

  @Override
  public final int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + hash(parent);
    return result;
  }

  @Override
  public final boolean isParentPartial() {
    return parent.isPartialDatabaseObjectReference();
  }

  @Override
  public NamedObjectKey key() {
    buildKey();
    return key;
  }

  @Override
  public void withQuoting(final Identifiers identifiers) {
    if (identifiers == null) {
      return;
    }
    super.withQuoting(identifiers);
    shortName = identifiers.quoteShortName(this);
  }

  private void buildFullName() {
    if (fullName != null) {
      return;
    }
    fullName = Identifiers.STANDARD.quoteFullName(this);
  }

  private void buildKey() {
    if (key != null) {
      return;
    }
    key = parent.get().key().with(getName());
  }

  private void buildShortName() {
    if (shortName != null) {
      return;
    }
    shortName = Identifiers.STANDARD.quoteShortName(this);
  }
}
