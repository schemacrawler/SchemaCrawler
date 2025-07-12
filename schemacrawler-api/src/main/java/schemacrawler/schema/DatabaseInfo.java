/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.schema;

import java.util.Collection;
import us.fatehi.utility.property.ProductVersion;
import us.fatehi.utility.property.Property;

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
  Collection<Property> getProperties();

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
