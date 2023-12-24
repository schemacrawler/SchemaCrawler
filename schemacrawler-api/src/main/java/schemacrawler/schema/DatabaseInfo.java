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

package schemacrawler.schema;

import java.util.Collection;

import schemacrawler.ProductVersion;

/** Database and connection information. */
public interface DatabaseInfo extends ProductVersion {

  /**
   * Gets the name of the database product.
   *
   * @return Name of the database product
   */
  default String getDatabaseProductName() {
    return getProductName();
  }

  /**
   * Gets the version of the database product.
   *
   * @return Version of the database product
   */
  default String getDatabaseProductVersion() {
    return getProductVersion();
  }

  /**
   * Gets all database properties.
   *
   * @return Database properties
   */
  Collection<DatabaseProperty> getProperties();

  /**
   * Gets the schema from the database connection.
   *
   * @return Schema
   */
  Collection<Property> getServerInfo();

  /**
   * Gets the user name as known to this database.
   *
   * @return Database user name
   */
  String getUserName();
}
