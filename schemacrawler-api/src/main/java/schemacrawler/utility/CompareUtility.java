/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2012, Sualeh Fatehi.
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
package schemacrawler.utility;


import java.util.Iterator;
import java.util.List;

public class CompareUtility
{

  public static <T extends Comparable<? super T>> int compareLists(final List<? extends T> list1,
                                                                   final List<? extends T> list2)
  {

    if (list1 == null)
    {
      return -1;
    }
    if (list2 == null)
    {
      return 1;
    }

    int comparison = list1.size() - list2.size();

    if (comparison == 0)
    {
      final Iterator<? extends T> iter1 = list1.iterator();
      final Iterator<? extends T> iter2 = list2.iterator();

      while (comparison == 0 && iter1.hasNext() && iter2.hasNext())
      {
        final T object1 = iter1.next();
        final T object2 = iter2.next();

        comparison = object1.compareTo(object2);
      }
    }

    return comparison;
  }

  private CompareUtility()
  {
  }

}
