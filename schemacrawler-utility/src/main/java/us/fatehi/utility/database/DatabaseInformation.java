/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility.database;

import java.io.Serial;
import us.fatehi.utility.property.BaseProductVersion;

/**
 * Database and connection information. Created from metadata returned by a JDBC call, and other
 * sources of information.
 */
public final class DatabaseInformation extends BaseProductVersion {

  @Serial private static final long serialVersionUID = 5471696122071524872L;

  private final String userName;

  public DatabaseInformation(
      final String databaseProductName,
      final String databaseProductVersion,
      final String userName) {
    super(databaseProductName, databaseProductVersion);
    this.userName = userName;
  }

  /**
   * Gets the name of the database product.
   *
   * @return Name of the database product
   */
  public String getDatabaseProductName() {
    return getProductName();
  }

  /**
   * Gets the version of the database product.
   *
   * @return Version of the database product
   */
  public String getDatabaseProductVersion() {
    return getProductVersion();
  }

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
}
