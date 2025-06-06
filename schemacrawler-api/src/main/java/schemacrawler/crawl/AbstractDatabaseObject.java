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

package schemacrawler.crawl;

import java.util.Objects;
import static java.util.Objects.hash;
import static java.util.Objects.requireNonNull;
import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.NamedObject;
import schemacrawler.schema.NamedObjectKey;
import schemacrawler.schema.Schema;
import schemacrawler.schema.TypedObject;
import schemacrawler.schemacrawler.Identifiers;

/** Represents a database object. */
abstract class AbstractDatabaseObject extends AbstractNamedObjectWithAttributes
    implements DatabaseObject {

  private static final long serialVersionUID = 3099561832386790624L;

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

    if (obj instanceof DatabaseObject) {
      final int schemaCompareTo = getSchema().compareTo(((DatabaseObject) obj).getSchema());
      if (schemaCompareTo != 0) {
        return schemaCompareTo;
      }
      if (this instanceof TypedObject && obj instanceof TypedObject) {
        try {
          final int typeCompareTo =
              ((TypedObject) this).getType().compareTo(((TypedObject) obj).getType());
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
