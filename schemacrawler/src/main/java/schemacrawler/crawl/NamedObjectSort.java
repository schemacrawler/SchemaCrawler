/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2010, Sualeh Fatehi.
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


import java.util.Comparator;

import schemacrawler.schema.NamedObject;

enum NamedObjectSort
  implements Comparator<NamedObject>
{

  /**
   * Alphabetical sort.
   */
  alphabetical
    {
      @Override
      public int compare(final NamedObject namedObject1,
                         final NamedObject namedObject2)
      {
        return namedObject1.getFullName()
          .compareToIgnoreCase(namedObject2
            .getFullName());
      }
    },

  /**
   * Natural sort.
   */
  natural
    {
      @Override
      public int compare(final NamedObject namedObject1,
                         final NamedObject namedObject2)
      {
        return namedObject1.compareTo(namedObject2);
      }
    };

  static NamedObjectSort getNamedObjectSort(final boolean alphabeticalSort)
  {
    if (alphabeticalSort)
    {
      return NamedObjectSort.alphabetical;
    }
    else
    {
      return NamedObjectSort.natural;
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
   */
  public abstract int compare(final NamedObject namedObject1,
                              final NamedObject namedObject2);

}
