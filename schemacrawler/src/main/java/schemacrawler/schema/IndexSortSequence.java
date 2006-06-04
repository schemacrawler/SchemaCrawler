/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2006, Sualeh Fatehi.
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

package schemacrawler.schema;


import java.io.ObjectStreamException;
import java.io.Serializable;

/**
 * An enumeration wrapper around index sort sequences.
 */
public final class IndexSortSequence
  implements Serializable
{

  private static final long serialVersionUID = 4048790182419837238L;

  private static final IndexSortSequence[] INDEX_SORT_SEQUENCE_ALL = {
    new IndexSortSequence("", ""),
    new IndexSortSequence("ascending", "A"),
    new IndexSortSequence("descending", "D")
  };

  private final transient String indexSortSequence;
  private final transient String indexSortSequenceCode;

  private IndexSortSequence(final String indexSortSequence,
                            final String indexSortSequenceCode)
  {
    ordinal = nextOrdinal++;
    this.indexSortSequence = indexSortSequence;
    this.indexSortSequenceCode = indexSortSequenceCode;
  }

  public String getIndexSortSequence()
  {
    return indexSortSequence;
  }

  public String getIndexSortSequenceCode()
  {
    return indexSortSequenceCode;
  }

  /**
   * {@inheritDoc}
   * 
   * @see Object#toString()
   */
  public String toString()
  {
    return indexSortSequence;
  }

  /**
   * Find the enumeration value corresponding to the string.
   * 
   * @param sortSequenceCode
   *          String value of sort sequence
   * @return Enumeration value
   */
  public static IndexSortSequence valueOfFromCode(final String sortSequenceCode)
  {
    IndexSortSequence indexSortSequence = INDEX_SORT_SEQUENCE_ALL[0];
    for (int i = 0; i < INDEX_SORT_SEQUENCE_ALL.length; i++)
    {
      if (INDEX_SORT_SEQUENCE_ALL[i].getIndexSortSequenceCode()
        .equalsIgnoreCase(sortSequenceCode))
      {
        indexSortSequence = INDEX_SORT_SEQUENCE_ALL[i];
        break;
      }
    }
    return indexSortSequence;
  }

  /**
   * Value of the enumeration from the code.
   * 
   * @param sortSequenceCode
   *          Code
   * @return Enumeration value
   */
  public static IndexSortSequence valueOf(final String sortSequenceCode)
  {
    IndexSortSequence indexSortSequence = INDEX_SORT_SEQUENCE_ALL[0];
    for (int i = 0; i < INDEX_SORT_SEQUENCE_ALL.length; i++)
    {
      if (INDEX_SORT_SEQUENCE_ALL[i].getIndexSortSequence()
        .equalsIgnoreCase(sortSequenceCode))
      {
        indexSortSequence = INDEX_SORT_SEQUENCE_ALL[i];
        break;
      }
    }
    return indexSortSequence;
  }

  // The 4 declarations below are necessary for serialization
  private static int nextOrdinal;
  private final int ordinal;

  private static final IndexSortSequence[] VALUES = INDEX_SORT_SEQUENCE_ALL;

  Object readResolve()
    throws ObjectStreamException
  {
    return VALUES[ordinal]; // Canonicalize
  }
}
