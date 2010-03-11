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


import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import schemacrawler.schema.DatabaseInfo;
import schemacrawler.schema.DatabaseProperty;

/**
 * Database and connection information. Created from metadata returned
 * by a JDBC call, and other sources of information.
 * 
 * @author Sualeh Fatehi sualeh@hotmail.com
 */
final class MutableDatabaseInfo
  implements DatabaseInfo
{

  private static final long serialVersionUID = 4051323422934251828L;

  private static final String NEWLINE = System.getProperty("line.separator");

  private String userName;
  private String productName;
  private String productVersion;
  private final Set<DatabaseProperty> databaseProperties = new HashSet<DatabaseProperty>();

  /**
   * {@inheritDoc}
   */
  public String getProductName()
  {
    return productName;
  }

  /**
   * {@inheritDoc}
   */
  public String getProductVersion()
  {
    return productVersion;
  }

  /**
   * {@inheritDoc}
   */
  public DatabaseProperty[] getProperties()
  {
    final DatabaseProperty[] properties = databaseProperties
      .toArray(new DatabaseProperty[databaseProperties.size()]);
    Arrays.sort(properties);
    return properties;
  }

  /**
   * {@inheritDoc}
   */
  public String getUserName()
  {
    return userName;
  }

  /**
   * {@inheritDoc}
   * 
   * @see Object#toString()
   */
  @Override
  public String toString()
  {
    final StringBuilder info = new StringBuilder();
    info.append("-- database: ").append(getProductName()).append(" ")
      .append(getProductVersion()).append(NEWLINE);
    return info.toString();
  }

  void addAll(final Collection<MutableDatabaseProperty> dbProperties)
  {
    if (dbProperties != null)
    {
      this.databaseProperties.addAll(dbProperties);
    }
  }

  void setProductName(final String productName)
  {
    this.productName = productName;
  }

  void setProductVersion(final String productVersion)
  {
    this.productVersion = productVersion;
  }

  void setUserName(final String userName)
  {
    this.userName = userName;
  }

}
