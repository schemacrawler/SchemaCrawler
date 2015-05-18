package schemacrawler.crawl;


import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;

import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.PartialDatabaseObject;

class DatabaseObjectReference<D extends DatabaseObject>
implements Serializable
{

  private static final long serialVersionUID = 1748828818899660921L;

  private final transient Reference<D> databaseObjectRef;
  private final D partial;

  DatabaseObjectReference(final D databaseObject, final D partial)
  {
    databaseObjectRef = new SoftReference<>(requireNonNull(databaseObject,
                                                           "Database object not provided"));

    this.partial = requireNonNull(partial,
        "Partial database object not provided");
    if (!(partial instanceof PartialDatabaseObject))
    {
      throw new IllegalArgumentException("Partial database object not provided");
    }
  }

  @Override
  public boolean equals(final Object obj)
  {
    return partial.equals(obj);
  }

  /**
   * {@inheritDoc} Modification over the Reference, always returns a
   * non-null value.
   *
   * @see java.lang.ref.SoftReference#get()
   */
  public D get()
  {
    final D dereferencedDatabaseObject;
    if (databaseObjectRef != null)
    {
      dereferencedDatabaseObject = databaseObjectRef.get();
    }
    else
    {
      dereferencedDatabaseObject = null;
    }

    if (dereferencedDatabaseObject == null)
    {
      return partial;
    }
    else
    {
      return dereferencedDatabaseObject;
    }
  }

  @Override
  public int hashCode()
  {
    return partial.hashCode();
  }

  public boolean isPartialDatabaseObjectReference()
  {
    return this.get() instanceof PartialDatabaseObject;
  }

  @Override
  public String toString()
  {
    return partial.toString();
  }

}
