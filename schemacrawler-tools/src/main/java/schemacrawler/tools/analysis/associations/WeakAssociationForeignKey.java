/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2016, Sualeh Fatehi.
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

package schemacrawler.tools.analysis.associations;


import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import schemacrawler.schema.BaseForeignKey;
import schemacrawler.schema.ColumnReference;
import schemacrawler.schema.NamedObject;
import schemacrawler.utility.CompareUtility;

/**
 * Represents a foreign-key mapping to a primary key in another table.
 *
 * @author Sualeh Fatehi
 */
public class WeakAssociationForeignKey
  implements BaseForeignKey<WeakAssociation>
{

  private static final long serialVersionUID = -5164664131926303038L;

  private final String name;
  private final SortedSet<WeakAssociation> columnReferences = new TreeSet<>();

  public WeakAssociationForeignKey(final String name)
  {
    this.name = requireNonNull(name, "No name provided");
  }

  /**
   * {@inheritDoc}
   * <p>
   * Note: Since foreign keys are not always explicitly named in
   * databases, the sorting routine orders the foreign keys by the names
   * of the columns in the foreign keys.
   * </p>
   */
  @Override
  public int compareTo(final NamedObject obj)
  {
    if (obj == null)
    {
      return -1;
    }

    final BaseForeignKey<?> other = (BaseForeignKey<?>) obj;
    final List<? extends ColumnReference> thisColumnReferences = getColumnReferences();
    final List<? extends ColumnReference> otherColumnReferences = other
      .getColumnReferences();

    return CompareUtility.compareLists(thisColumnReferences,
                                       otherColumnReferences);
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
    final WeakAssociationForeignKey other = (WeakAssociationForeignKey) obj;
    if (columnReferences == null)
    {
      if (other.columnReferences != null)
      {
        return false;
      }
    }
    else if (!columnReferences.equals(other.columnReferences))
    {
      return false;
    }
    return true;
  }

  @Override
  public List<WeakAssociation> getColumnReferences()
  {
    return new ArrayList<WeakAssociation>(columnReferences);
  }

  @Override
  public String getFullName()
  {
    return getName();
  }

  @Override
  public String getLookupKey()
  {
    return getName();
  }

  @Override
  public String getName()
  {
    return name;
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result
             + (columnReferences == null? 0: columnReferences.hashCode());
    return result;
  }

  public boolean isValid()
  {
    for (final WeakAssociation weakAssociation: columnReferences)
    {
      if (weakAssociation == null)
      {
        return false;
      }
      if (!weakAssociation.isValid())
      {
        return false;
      }
    }
    return true;
  }

  @Override
  public Iterator<WeakAssociation> iterator()
  {
    return columnReferences.iterator();
  }

  @Override
  public String toString()
  {
    return columnReferences.toString();
  }

  void add(final WeakAssociation weakAssociation)
  {
    if (weakAssociation != null)
    {
      columnReferences.add(weakAssociation);
    }
  }

}
