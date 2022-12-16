/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
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

import java.sql.SQLException;
import java.util.Collection;

import schemacrawler.ProductVersion;

/** JDBC driver information. */
public interface JdbcDriverInfo extends ProductVersion {

  /**
   * Gets the database connection URL.
   *
   * @return Database connection URL
   */
  String getConnectionUrl();

  /**
   * Gets the class name of the JDBC driver.
   *
   * @return Class name of the JDBC driver
   */
  String getDriverClassName();

  /**
   * Retrieves JDBC driver's major version number.
   *
   * @return JDBC driver's major version
   */
  int getDriverMajorVersion();

  /**
   * Retrieves this JDBC driver's minor version number.
   *
   * @return JDBC driver's minor version number
   */
  int getDriverMinorVersion();

  /**
   * Gets the name of the JDBC driver.
   *
   * @return Name of the JDBC driver
   */
  default String getDriverName() {
    return getProductName();
  }

  /**
   * Gets all the JDBC driver properties, and their values.
   *
   * @return JDBC driver properties
   */
  Collection<JdbcDriverProperty> getDriverProperties();

  /**
   * Gets the version of the JDBC driver.
   *
   * @return Version of the JDBC driver
   */
  default String getDriverVersion() {
    return getProductVersion();
  }

  /**
   * Gets the major JDBC version number supported by JDBC driver.
   *
   * @return JDBC version major number
   * @exception SQLException if a database access error occurs
   */
  int getJdbcMajorVersion();

  /**
   * Gets the minor JDBC version number supported by JDBC driver.
   *
   * @return JDBC version major number
   * @exception SQLException if a database access error occurs
   */
  int getJdbcMinorVersion();

  /**
   * Whether the JDBC driver class name is available. It may either not have been retrieved, or may
   * have been loaded by another classloader.
   *
   * @return True if the JDBC driver class name is available.
   */
  boolean hasDriverClassName();

  /**
   * Reports whether this JDBC driver is a genuine JDBC Compliant <sup><font size=-2>TM</font></sup>
   * driver.
   *
   * <p>JDBC compliance requires full support for the JDBC API and full support for SQL 92 Entry
   * Level.
   *
   * @return <code>true</code> if this driver is JDBC Compliant; <code>false</code> otherwise
   */
  boolean isJdbcCompliant();
}
