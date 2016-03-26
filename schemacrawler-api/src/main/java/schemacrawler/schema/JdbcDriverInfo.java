/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi <sualeh@hotmail.com>.
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
import java.util.Collection;

/**
 * JDBC driver information.
 *
 * @author Sualeh Fatehi
 */
public interface JdbcDriverInfo
  extends Serializable
{

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
   * Gets the name of the JDBC driver.
   *
   * @return Driver name
   */
  String getDriverName();

  /**
   * Gets all the JDBC driver properties, and their values.
   *
   * @return JDBC driver properties
   */
  Collection<JdbcDriverProperty> getDriverProperties();

  /**
   * Gets the JDBC driver version.
   *
   * @return Driver version.
   */
  String getDriverVersion();

  /**
   * Reports whether this JDBC driver is a genuine JDBC Compliant
   * <sup><font size=-2>TM</font></sup> driver.
   * <P>
   * JDBC compliance requires full support for the JDBC API and full
   * support for SQL 92 Entry Level.
   *
   * @return <code>true</code> if this driver is JDBC Compliant;
   *         <code>false</code> otherwise
   */
  boolean isJdbcCompliant();

}
