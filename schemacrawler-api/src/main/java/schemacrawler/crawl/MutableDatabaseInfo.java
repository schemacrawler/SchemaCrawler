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

package schemacrawler.crawl;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
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

  private String userName;
  private String productName;
  private String productVersion;
  private final Set<DatabaseProperty> databaseProperties = new HashSet<>();

  /**
   * {@inheritDoc}
   */
  @Override
  public String getProductName()
  {
    return productName;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getProductVersion()
  {
    return productVersion;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Collection<DatabaseProperty> getProperties()
  {
    final List<DatabaseProperty> properties = new ArrayList<>(databaseProperties);
    Collections.sort(properties);
    return properties;
  }

  /**
   * {@inheritDoc}
   */
  @Override
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
    final StringBuilder info = new StringBuilder(1024);
    info.append("-- database: ").append(getProductName()).append(' ')
      .append(getProductVersion()).append(System.lineSeparator());
    return info.toString();
  }

  void addAll(final Collection<ImmutableDatabaseProperty> dbProperties)
  {
    if (dbProperties != null)
    {
      databaseProperties.addAll(dbProperties);
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
