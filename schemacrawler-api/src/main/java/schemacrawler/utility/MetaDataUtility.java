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

package schemacrawler.utility;


import schemacrawler.crawl.JavaSqlType;
import schemacrawler.crawl.JavaSqlTypesUtility;
import schemacrawler.schema.Column;

/**
 * SchemaCrawler utility methods.
 * 
 * @author sfatehi
 */
public final class MetaDataUtility
{

  public enum Connectivity
  {

    OneToOne,
    OneToMany;
  }

  public static Connectivity getConnectivity(final Column fkColumn)
  {
    if (fkColumn == null)
    {
      return null;
    }
    if (fkColumn.isPartOfPrimaryKey() || fkColumn.isPartOfUniqueIndex())
    {
      return Connectivity.OneToOne;
    }
    else
    {
      return Connectivity.OneToMany;
    }
  }

  /**
   * Lookup java.sql.Types type, and return more detailed information,
   * including the mapped Java class.
   * 
   * @param type
   *        java.sql.Types type
   * @return JavaSqlType type
   */
  public static JavaSqlType lookupSqlDataType(final int type)
  {
    return JavaSqlTypesUtility.lookupSqlDataType(type);
  }

  /**
   * Lookup java.sql.Types type, and return more detailed information,
   * including the mapped Java class.
   * 
   * @param typeName
   *        java.sql.Types type name
   * @return JavaSqlType type
   */
  public static JavaSqlType lookupSqlDataType(final String typeName)
  {
    return JavaSqlTypesUtility.lookupSqlDataType(typeName);
  }

  private MetaDataUtility()
  {
    // Prevent instantiation
  }

}
