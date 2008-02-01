/*
 * SchemaCrawler
 * Copyright (c) 2000-2008, Sualeh Fatehi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package schemacrawler.schema;


import java.io.Serializable;

/**
 * JDBC driver information.
 * 
 * @author Sualeh Fatehi
 */
public interface JdbcDriverInfo
  extends Serializable
{

  /**
   * Database connection URL.
   * 
   * @return Database connection URL.
   */
  String getConnectionUrl();

  /**
   * Class name of the JDBC driver.
   * 
   * @return Class name of the JDBC driver.
   */
  String getDriverClassName();

  /**
   * Name of the driver.
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
   * Driver version.
   * 
   * @return Driver version.
   */
  String getDriverVersion();

  /**
   * Reports whether this driver is a genuine JDBC Compliant<sup><font
   * size=-2>TM</font></sup> driver.
   * <P>
   * JDBC compliance requires full support for the JDBC API and full
   * support for SQL 92 Entry Level.
   * 
   * @return <code>true</code> if this driver is JDBC Compliant;
   *         <code>false</code> otherwise
   */
  boolean isJdbcCompliant();

}
