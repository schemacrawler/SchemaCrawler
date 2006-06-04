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
import java.sql.DatabaseMetaData;

/**
 * The deferrability value for foreign keys.
 */
public final class ForeignKeyDeferrability
  implements Serializable
{

  private static final ForeignKeyDeferrability[] FOREIGN_KEY_DEFERRABILITY_ALL = {
    new ForeignKeyDeferrability(DatabaseMetaData.importedKeyInitiallyDeferred,
        "initially deferred"),
    new ForeignKeyDeferrability(DatabaseMetaData.importedKeyInitiallyImmediate,
        "initially immediate"),
    new ForeignKeyDeferrability(DatabaseMetaData.importedKeyNotDeferrable,
        "not deferrable"),
  };

  // The declarations below are necessary for serialization
  private static int nextOrdinal;
  private static final long serialVersionUID = 3617290108341334582L;

  private static final ForeignKeyDeferrability[] VALUES = FOREIGN_KEY_DEFERRABILITY_ALL;

  /**
   * Find the enumeration value corresponding to the string.
   * 
   * @param fkDeferrabilityId
   *          Id
   * @return Enumeration value
   */
  public static ForeignKeyDeferrability valueOfFromId(
                                                      final int fkDeferrabilityId)
  {
    ForeignKeyDeferrability fkDeferrability = null;
    for (int i = 0; i < FOREIGN_KEY_DEFERRABILITY_ALL.length; i++)
    {
      if (FOREIGN_KEY_DEFERRABILITY_ALL[i].getForeignKeyDeferrabilityId() == fkDeferrabilityId)
      {
        fkDeferrability = FOREIGN_KEY_DEFERRABILITY_ALL[i];
        break;
      }
    }
    return fkDeferrability;
  }

  /**
   * Value of the enumeration from the code.
   * 
   * @param fkDeferrabilityName
   *          Code
   * @return Enumeration value
   */
  public static ForeignKeyDeferrability valueOf(final String fkDeferrabilityName)
  {
    ForeignKeyDeferrability fkDeferrability = null;
    for (int i = 0; i < FOREIGN_KEY_DEFERRABILITY_ALL.length; i++)
    {
      if (FOREIGN_KEY_DEFERRABILITY_ALL[i].getForeignKeyDeferrabilityName()
        .equalsIgnoreCase(fkDeferrabilityName))
      {
        fkDeferrability = FOREIGN_KEY_DEFERRABILITY_ALL[i];
        break;
      }
    }
    return fkDeferrability;
  }

  private final transient String fkDeferrabilityName;
  private final transient int fkDeferrabilityId;
  private final int ordinal;

  private ForeignKeyDeferrability(final int fkDeferrabilityId,
                                  final String fkDeferrabilityName)
  {
    ordinal = nextOrdinal++;
    this.fkDeferrabilityId = fkDeferrabilityId;
    this.fkDeferrabilityName = fkDeferrabilityName;
  }

  private int getForeignKeyDeferrabilityId()
  {
    return fkDeferrabilityId;
  }

  public String getForeignKeyDeferrabilityName()
  {
    return fkDeferrabilityName;
  }

  /**
   * {@inheritDoc}
   * 
   * @see Object#toString()
   */
  public String toString()
  {
    return getForeignKeyDeferrabilityName();
  }

  Object readResolve()
    throws ObjectStreamException
  {
    return VALUES[ordinal]; // Canonicalize
  }

}
