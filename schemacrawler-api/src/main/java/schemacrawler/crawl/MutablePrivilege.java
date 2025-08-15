/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.crawl;

import static java.util.Comparator.naturalOrder;
import static us.fatehi.utility.Utility.isBlank;
import static us.fatehi.utility.Utility.trimToEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.Grant;
import schemacrawler.schema.Privilege;

/** Represents a privilege of a table or column. */
final class MutablePrivilege<D extends DatabaseObject> extends AbstractDependantObject<D>
    implements Privilege<D> {

  final class PrivilegeGrant implements Grant<D> {

    private static final long serialVersionUID = 356151825191631484L;
    private final String grantee;
    private final String grantor;
    private final boolean isGrantable;

    PrivilegeGrant(final String grantor, final String grantee, final boolean isGrantable) {
      this.grantor = grantor;
      this.grantee = grantee;
      this.isGrantable = isGrantable;
    }

    /**
     * {@inheritDoc}
     *
     * <p>NOTE: compareTo is not compatible with equals. equals compares the full name of a database
     * object, but compareTo uses more fields to define a "natural" sorting order. compareTo may
     * return incorrect results until the object is fully built by SchemaCrawler.
     */
    @Override
    public int compareTo(final Grant<D> otherGrant) {
      int compare = 0;
      if (compare == 0) {
        if (grantor == null) {
          return -1;
        }
        compare = grantor.compareTo(otherGrant.getGrantor());
      }
      if (compare == 0) {
        if (grantee == null) {
          return -1;
        }
        compare = grantee.compareTo(otherGrant.getGrantee());
      }
      return compare;
    }

    @Override
    public boolean equals(final Object obj) {
      if (this == obj) {
        return true;
      }
      if ((obj == null) || (getClass() != obj.getClass())) {
        return false;
      }
      final PrivilegeGrant other = (PrivilegeGrant) obj;
      return Objects.equals(getParent(), other.getParent())
          && Objects.equals(grantee, other.grantee)
          && Objects.equals(grantor, other.grantor)
          && isGrantable == other.isGrantable;
    }

    /** {@inheritDoc} */
    @Override
    public String getGrantee() {
      return grantee;
    }

    /** {@inheritDoc} */
    @Override
    public String getGrantor() {
      return grantor;
    }

    @Override
    public Privilege<D> getParent() {
      return MutablePrivilege.this;
    }

    @Override
    public int hashCode() {
      return Objects.hash(getParent(), grantee, grantor, isGrantable);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isGrantable() {
      return isGrantable;
    }

    @Override
    public String toString() {
      return String.format(
          "%s --> %s%s",
          trimToEmpty(grantor), trimToEmpty(grantee), isGrantable ? " (grantable)" : "");
    }
  }

  private static final long serialVersionUID = -1117664231494271886L;

  private final Set<Grant<D>> grants;

  MutablePrivilege(final DatabaseObjectReference<D> parent, final String name) {
    super(parent, name);
    grants = ConcurrentHashMap.newKeySet();
  }

  @Override
  public Collection<Grant<D>> getGrants() {
    final List<Grant<D>> values = new ArrayList<>(grants);
    values.sort(naturalOrder());
    return values;
  }

  void addGrant(final String grantor, final String grantee, final boolean isGrantable) {
    if (!(isBlank(grantor) && isBlank(grantee))) {
      grants.add(new PrivilegeGrant(grantor, grantee, isGrantable));
    }
  }
}
