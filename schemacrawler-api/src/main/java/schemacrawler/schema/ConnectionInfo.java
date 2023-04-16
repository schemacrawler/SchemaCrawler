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

import java.io.Serializable;
import java.sql.SQLException;

/** Connection information. */
public interface ConnectionInfo extends Serializable {

  /**
   * Gets the database connection URL.
   *
   * @return Database connection URL
   */
  String getConnectionUrl();

  /**
   * Gets the name of the database product.
   *
   * @return Name of the database product
   */
  String getDatabaseProductName();

  /**
   * Gets the version of the database product.
   *
   * @return Version of the database product
   */
  String getDatabaseProductVersion();

  /**
   * Gets the Java class name of the JDBC driver.
   *
   * @return Java class name of the JDBC driver
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
  String getDriverName();

  /**
   * Gets the version of the JDBC driver.
   *
   * @return Version of the JDBC driver
   */
  String getDriverVersion();

  JdbcDriverInfo getJdbcDriverInfo();

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
   * Gets the database user name.
   *
   * @return Database user name
   */
  String getUserName();
}
