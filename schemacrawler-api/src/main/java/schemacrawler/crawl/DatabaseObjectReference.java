/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.crawl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import static java.util.Objects.requireNonNull;
import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.PartialDatabaseObject;

class DatabaseObjectReference<D extends DatabaseObject> implements Serializable {

  private static final long serialVersionUID = 1748828818899660921L;

  private Reference<D> databaseObjectRef;
  private D partial;

  DatabaseObjectReference(final D databaseObject, final D partial) {
    databaseObjectRef =
        new SoftReference<>(requireNonNull(databaseObject, "Database object not provided"));

    this.partial = requireNonNull(partial, "Partial database object not provided");
    if (!(partial instanceof PartialDatabaseObject)) {
      throw new IllegalArgumentException("Partial database object not provided");
    }

    if (!partial.equals(databaseObject)) {
      throw new IllegalArgumentException("Inconsistent database object reference");
    }
  }

  @Override
  public final boolean equals(final Object obj) {
    return partial.equals(obj);
  }

  /**
   * {@inheritDoc} Modification over the Reference, always returns a non-null value.
   *
   * @see java.lang.ref.SoftReference#get()
   */
  public final D get() {
    final D dereferencedDatabaseObject;
    if (databaseObjectRef != null) {
      dereferencedDatabaseObject = databaseObjectRef.get();
    } else {
      dereferencedDatabaseObject = null;
    }

    if (dereferencedDatabaseObject == null) {
      return partial;
    }
    return dereferencedDatabaseObject;
  }

  @Override
  public final int hashCode() {
    return partial.hashCode();
  }

  public boolean isPartialDatabaseObjectReference() {
    return this.get() instanceof PartialDatabaseObject;
  }

  @Override
  public String toString() {
    return partial.toString();
  }

  /**
   * Read saved content of the reference, construct new reference, and the partial.
   *
   * @throws IOException
   * @throws ClassNotFoundException
   */
  private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
    if (in != null) {
      partial = (D) in.readObject();
      databaseObjectRef = new WeakReference<>((D) in.readObject());
    }
  }

  /**
   * Write only content of the reference. A Reference itself is not serializable.
   *
   * @throws java.io.IOException
   */
  private void writeObject(final ObjectOutputStream out) throws IOException {
    if (out != null) {
      out.writeObject(partial);
      out.writeObject(databaseObjectRef.get());
    }
  }
}
