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

package schemacrawler.schema;


import java.io.Serializable;

/**
 * JDBC driver information.
 *
 * @author Sualeh Fatehi
 */
public interface JdbcDriverInfo
  extends Serializable {

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
  JdbcDriverProperty[] getDriverProperties();

  /**
   * Gets the JDBC driver version.
   *
   * @return Driver version.
   */
  String getDriverVersion();

  /**
   * Reports whether this JDBC driver is a genuine JDBC Compliant<sup><font size=-2>TM</font></sup> driver. <P> JDBC
   * compliance requires full support for the JDBC API and full support for SQL 92 Entry Level.
   *
   * @return <code>true</code> if this driver is JDBC Compliant; <code>false</code> otherwise
   */
  boolean isJdbcCompliant();

}
