/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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
import java.time.LocalDateTime;

import schemacrawler.ProductVersion;

public interface CrawlInfo
  extends Serializable
{

  /**
   * Gets the timestamp of when the database was crawled.
   *
   * @return Timestamp
   */
  LocalDateTime getCrawlTimestamp();

  /**
   * @deprecated
   * Gets the name of the RDBMS vendor and product.
   *
   * @return Name of the RDBMS vendor and product
   */
  @Deprecated
  String getDatabaseInfo();

  /**
   * Gets the version of the RDBMS vendor and product.
   *
   * @return Name and version of the RDBMS vendor and product
   */
  ProductVersion getDatabaseVersion();

  /**
   * @deprecated
   * Gets the name of the JDBC driver.
   *
   * @return Driver name
   */
  @Deprecated
  String getJdbcDriverInfo();

  /**
   * Gets the name and version of the JDBC driver.
   *
   * @return Driver name and version
   */
  ProductVersion getJdbcDriverVersion();

  /**
   * @deprecated
   * Get JVM system information.
   *
   * @return JVM system information
   */
  @Deprecated
  String getJvmSystemInfo();

  /**
   * Get JVM system information.
   *
   * @return JVM system information
   */
  ProductVersion getJvmVersion();

  /**
   * @deprecated
   * Get operating system information.
   *
   * @return Operating system information
   */
  @Deprecated
  String getOperatingSystemInfo();

  /**
   * Get operating system information.
   *
   * @return Operating system information
   */
  ProductVersion getOperatingSystemVersion();

  /**
   * Unique identifier for each SchemaCrawler run.
   *
   * @return Unique identifier
   */
  String getRunId();

  /**
   * Gets the SchemaCrawler version.
   *
   * @return SchemaCrawler version
   */
  ProductVersion getSchemaCrawlerVersion();

}
