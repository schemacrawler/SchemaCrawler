/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.crawl;

import static java.util.Objects.hash;
import static java.util.Objects.requireNonNull;

import java.io.Serial;
import java.util.Objects;
import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.NamedObject;
import schemacrawler.schema.NamedObjectKey;
import schemacrawler.schema.Schema;
import schemacrawler.schema.TypedObject;
import schemacrawler.schema.Identifiers;

/** Represents a database object. */
abstract class AbstractDatabaseObject extends AbstractNamedObjectWithAttributes
    implements DatabaseObject {

  @Serial private static final long serialVersionUID = 3099561832386790624L;

  private final Schema schema;
  private transient NamedObjectKey key;
  private transient String fullName;

  /**
   * Effective Java - Item 17 - Minimize Mutability - Package-private constructors make a class
   * effectively final
   *
   * @param schema Schema of this object
   * @param name Name of the named object
   */
  AbstractDatabaseObject(final Schema schema, final String name) {
    super(name);
    this.schema = requireNonNull(schema, "No schema provided");
  }

  /**
   * {@inheritDoc}
   *
   * <p>NOTE: compareTo is not compatible with equals. equals compares the full name of a database
   * object, but compareTo uses more fields to define a "natural" sorting order. compareTo may
   * return incorrect results until the object is fully built by SchemaCrawler.
   */
  @Override
  public int compareTo(final NamedObject obj) {
    if (obj == null) {
      return -1;
    }

    if (obj instanceof DatabaseObject object2) {
      final int schemaCompareTo = getSchema().compareTo(object2.getSchema());
      if (schemaCompareTo != 0) {
        return schemaCompareTo;
      }
      if (this instanceof TypedObject object && obj instanceof TypedObject object1) {
        try {
          final int typeCompareTo = object.getType().compareTo(object1.getType());
          if (typeCompareTo != 0) {
            return typeCompareTo;
          }
        } catch (final Exception e) {
          // Ignore, since getType() may not be implemented by partial database objects
        }
      }
    }

    return super.compareTo(obj);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj) || !(obj instanceof DatabaseObject)) {
      return false;
    }
    return Objects.equals(schema, ((DatabaseObject) obj).getSchema());
  }

  /** {@inheritDoc} */
  @Override
  public String getFullName() {
    buildFullName();
    return fullName;
  }

  @Override
  public final Schema getSchema() {
    return schema;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + hash(schema);
    return result;
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
    fullName = identifiers.quoteFullName(this);
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
    key = schema.key().with(getName());
  }
}
