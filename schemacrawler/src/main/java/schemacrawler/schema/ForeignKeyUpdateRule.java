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
 * Foreign key update and delete rules.
 */
public final class ForeignKeyUpdateRule
  implements EnumType
{

  private static final long serialVersionUID = 3256728394182833712L;

  private static final ForeignKeyUpdateRule[] FK_UPDATE_RULE_ALL = {
    new ForeignKeyUpdateRule(DatabaseMetaData.importedKeyNoAction, "no action"),
    new ForeignKeyUpdateRule(DatabaseMetaData.importedKeyCascade, "cascade"),
    new ForeignKeyUpdateRule(DatabaseMetaData.importedKeySetNull, "set null"),
    new ForeignKeyUpdateRule(DatabaseMetaData.importedKeySetDefault,
        "set default"),
    new ForeignKeyUpdateRule(DatabaseMetaData.importedKeyRestrict, "restrict"),
  };

  /**
   * Find the enumeration value corresponding to the string.
   * 
   * @param fkUpdateRuleId
   *          Id
   * @return Enumeration value
   */
  public static ForeignKeyUpdateRule valueOfFromId(final int fkUpdateRuleId)
  {
    ForeignKeyUpdateRule fkUpdateRule = null;
    for (int i = 0; i < FK_UPDATE_RULE_ALL.length; i++)
    {
      if (FK_UPDATE_RULE_ALL[i].getId() == fkUpdateRuleId)
      {
        fkUpdateRule = FK_UPDATE_RULE_ALL[i];
        break;
      }
    }
    return fkUpdateRule;
  }

  /**
   * Value of the enumeration from the code.
   * 
   * @param fkUpdateRuleName
   *          Code
   * @return Enumeration value
   */
  public static ForeignKeyUpdateRule valueOf(final String fkUpdateRuleName)
  {
    ForeignKeyUpdateRule fkUpdateRule = null;
    for (int i = 0; i < FK_UPDATE_RULE_ALL.length; i++)
    {
      if (FK_UPDATE_RULE_ALL[i].getName().equalsIgnoreCase(fkUpdateRuleName))
      {
        fkUpdateRule = FK_UPDATE_RULE_ALL[i];
        break;
      }
    }
    return fkUpdateRule;
  }

  private final transient String name;
  private final transient int id;

  private ForeignKeyUpdateRule(final int foreignKeyUpdateRuleId,
                               final String foreignKeyUpdateRuleName)
  {
    ordinal = nextOrdinal++;
    id = foreignKeyUpdateRuleId;
    name = foreignKeyUpdateRuleName;
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
   * @see schemacrawler.schema.EnumType#getName()
   */
  public String getName()
  {
    return name;
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

  // The 4 declarations below are necessary for serialization
  private static int nextOrdinal;
  private final int ordinal;
  private static final ForeignKeyUpdateRule[] VALUES = FK_UPDATE_RULE_ALL;

  Object readResolve()
    throws ObjectStreamException
  {
    return VALUES[ordinal]; // Canonicalize
  }
}
