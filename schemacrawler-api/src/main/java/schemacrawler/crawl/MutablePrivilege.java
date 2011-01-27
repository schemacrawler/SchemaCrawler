/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2011, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */

package schemacrawler.crawl;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.Privilege;
import sf.util.Utility;

/**
 * Represents a privilege of a table or column.
 * 
 * @author Sualeh Fatehi
 */
final class MutablePrivilege
  extends AbstractDependantObject
  implements Privilege
{

  private final class PrivilegeGrant
    implements Grant, Comparable<Grant>
  {

    private static final long serialVersionUID = 356151825191631484L;

    private final String grantor;
    private final String grantee;
    private final boolean isGrantable;

    PrivilegeGrant(final String grantor,
                   final String grantee,
                   final boolean isGrantable)
    {
      this.grantor = grantor;
      this.grantee = grantee;
      this.isGrantable = isGrantable;
    }

    public int compareTo(final Grant otherGrant)
    {
      int compare = 0;
      if (compare == 0)
      {
        compare = grantor.compareTo(otherGrant.getGrantor());
      }
      if (compare == 0)
      {
        compare = grantee.compareTo(otherGrant.getGrantee());
      }
      return compare;
    }

    @Override
    public boolean equals(final Object obj)
    {
      if (this == obj)
      {
        return true;
      }
      if (obj == null)
      {
        return false;
      }
      if (getClass() != obj.getClass())
      {
        return false;
      }
      final PrivilegeGrant other = (PrivilegeGrant) obj;
      if (!getOuterType().equals(other.getOuterType()))
      {
        return false;
      }
      if (grantee == null)
      {
        if (other.grantee != null)
        {
          return false;
        }
      }
      else if (!grantee.equals(other.grantee))
      {
        return false;
      }
      if (grantor == null)
      {
        if (other.grantor != null)
        {
          return false;
        }
      }
      else if (!grantor.equals(other.grantor))
      {
        return false;
      }
      if (isGrantable != other.isGrantable)
      {
        return false;
      }
      return true;
    }

    /**
     * {@inheritDoc}
     */
    public String getGrantee()
    {
      return grantee;
    }

    /**
     * {@inheritDoc}
     */
    public String getGrantor()
    {
      return grantor;
    }

    @Override
    public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = prime * result + getOuterType().hashCode();
      result = prime * result + (grantee == null? 0: grantee.hashCode());
      result = prime * result + (grantor == null? 0: grantor.hashCode());
      result = prime * result + (isGrantable? 1231: 1237);
      return result;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isGrantable()
    {
      return isGrantable;
    }

    private MutablePrivilege getOuterType()
    {
      return MutablePrivilege.this;
    }

  }

  private final Set<Grant> grants = new HashSet<Grant>();

  private static final long serialVersionUID = -1117664231494271886L;

  MutablePrivilege(final DatabaseObject parent, final String name)
  {
    super(parent, name);
  }

  public Grant[] getGrants()
  {
    final Grant[] grantsArray = grants.toArray(new Grant[grants.size()]);
    Arrays.sort(grantsArray);
    return grantsArray;
  }

  void addGrant(final String grantor,
                final String grantee,
                final boolean isGrantable)
  {
    if (!Utility.isBlank(grantor) && !Utility.isBlank(grantee))
    {
      grants.add(new PrivilegeGrant(grantor, grantee, isGrantable));
    }
  }

}
