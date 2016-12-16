/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.utility;


import static sf.util.Utility.convertForComparison;

import java.util.Comparator;

import schemacrawler.schema.NamedObject;

public enum NamedObjectSort
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
     return convertForComparison(namedObject1.getFullName())
       .compareTo(convertForComparison(namedObject2.getFullName()));
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

  public static NamedObjectSort getNamedObjectSort(final boolean alphabeticalSort)
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
   * @see java.util.Comparator#compare(java.lang.Object,
   *      java.lang.Object)
   */
  @Override
  public abstract int compare(final NamedObject namedObject1,
                              final NamedObject namedObject2);

}
