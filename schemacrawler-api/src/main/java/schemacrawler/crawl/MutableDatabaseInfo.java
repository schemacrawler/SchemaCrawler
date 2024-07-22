/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2024, Sualeh Fatehi <sualeh@hotmail.com>.
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

import static java.util.Comparator.naturalOrder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import schemacrawler.BaseProductVersion;
import schemacrawler.schema.DatabaseInfo;
import us.fatehi.utility.property.Property;

/**
 * Database and connection information. Created from metadata returned by a JDBC call, and other
 * sources of information.
 */
final class MutableDatabaseInfo extends BaseProductVersion implements DatabaseInfo {

  private static final long serialVersionUID = 4051323422934251828L;

  private final String userName;
  // Mutable properties collection
  private final Set<Property> serverInfo;
  private final Set<Property> databaseProperties;

  public MutableDatabaseInfo(
      final String databaseProductName,
      final String databaseProductVersion,
      final String userName) {
    super(databaseProductName, databaseProductVersion);
    this.userName = userName;

    serverInfo = new HashSet<>();
    databaseProperties = new HashSet<>();
  }

  /** {@inheritDoc} */
  @Override
  public Collection<Property> getProperties() {
    final List<Property> properties = new ArrayList<>(databaseProperties);
    properties.sort(naturalOrder());
    return properties;
  }

  @Override
  public Collection<Property> getServerInfo() {
    return new TreeSet<>(serverInfo);
  }

  /** {@inheritDoc} */
  @Override
  public String getUserName() {
    return userName;
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    final StringBuilder info = new StringBuilder(1024);
    info.append("-- database: ")
        .append(getProductName())
        .append(' ')
        .append(getProductVersion())
        .append(System.lineSeparator());
    return info.toString();
  }

  void addAll(final Collection<ImmutableDatabaseProperty> dbProperties) {
    if (dbProperties != null) {
      databaseProperties.addAll(dbProperties);
    }
  }

  void addServerInfo(final Property property) {
    if (property != null) {
      serverInfo.add(property);
    }
  }
}
