/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
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
import java.time.Instant;
import us.fatehi.utility.property.ProductVersion;

public interface CrawlInfo extends Serializable {

  /**
   * Gets the timestamp of when the database was crawled, in UTC to the second.
   *
   * @return Timestamp
   */
  String getCrawlTimestamp();

  /**
   * Gets the timestamp of when the database was crawled.
   *
   * @return Timestamp
   */
  Instant getCrawlTimestampInstant();

  /**
   * Gets the version of the RDBMS vendor and product.
   *
   * @return Name and version of the RDBMS vendor and product
   */
  ProductVersion getDatabaseVersion();

  /**
   * Gets the name and version of the JDBC driver.
   *
   * @return Driver name and version
   */
  ProductVersion getJdbcDriverVersion();

  /**
   * Get JVM system information.
   *
   * @return JVM system information
   */
  ProductVersion getJvmVersion();

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
