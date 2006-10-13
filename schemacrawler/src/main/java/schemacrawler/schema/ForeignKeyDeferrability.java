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
import java.sql.DatabaseMetaData;

/**
 * The deferrability value for foreign keys.
 */
public final class ForeignKeyDeferrability
  implements EnumType
{

  private static final ForeignKeyDeferrability[] FOREIGN_KEY_DEFERRABILITY_ALL =
  {
   new ForeignKeyDeferrability(DatabaseMetaData.importedKeyInitiallyDeferred,
       "initially deferred"),
   new ForeignKeyDeferrability(DatabaseMetaData.importedKeyInitiallyImmediate,
       "initially immediate"),
   new ForeignKeyDeferrability(DatabaseMetaData.importedKeyNotDeferrable,
       "not deferrable"), };

  // The declarations below are necessary for serialization
  private static int nextOrdinal;
  private static final long serialVersionUID = 3617290108341334582L;

  private static final ForeignKeyDeferrability[] VALUES = FOREIGN_KEY_DEFERRABILITY_ALL;

  /**
   * Find the enumeration value corresponding to the string.
   * 
   * @param id
   *        Id
   * @return Enumeration value
   */
  public static ForeignKeyDeferrability valueOfFromId(final int id)
  {
    ForeignKeyDeferrability fkDeferrability = null;
    for (int i = 0; i < FOREIGN_KEY_DEFERRABILITY_ALL.length; i++)
    {
      if (FOREIGN_KEY_DEFERRABILITY_ALL[i].getForeignKeyDeferrabilityId() == id)
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
   *        Code
   * @return Enumeration value
   */
  public static ForeignKeyDeferrability valueOf(final String fkDeferrabilityName)
  {
    ForeignKeyDeferrability fkDeferrability = null;
    for (int i = 0; i < FOREIGN_KEY_DEFERRABILITY_ALL.length; i++)
    {
      if (FOREIGN_KEY_DEFERRABILITY_ALL[i].getName().equalsIgnoreCase(
          fkDeferrabilityName))
      {
        fkDeferrability = FOREIGN_KEY_DEFERRABILITY_ALL[i];
        break;
      }
    }
    return fkDeferrability;
  }

  private final transient String name;
  private final transient int id;
  private final int ordinal;

  private ForeignKeyDeferrability(final int id, final String name)
  {
    ordinal = nextOrdinal++;
    this.id = id;
    this.name = name;
  }

  private int getForeignKeyDeferrabilityId()
  {
    return id;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.EnumType#getName()
   */
  public String getName()
  {
    return name;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.EnumType#getId()
   */
  public int getId()
  {
    return id;
  }

  /**
   * {@inheritDoc}
   * 
   * @see Object#toString()
   */
  public String toString()
  {
    return getName();
  }

  Object readResolve()
    throws ObjectStreamException
  {
    return VALUES[ordinal]; // Canonicalize
  }

}
