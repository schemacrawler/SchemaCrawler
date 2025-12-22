/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility.database;

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.isBlank;
import static us.fatehi.utility.Utility.requireNotBlank;

import java.io.Serial;
import us.fatehi.utility.property.BaseProductVersion;

/**
 * JDBC driver information. Created from metadata returned by a JDBC call, and other sources of
 * information.
 */
public final class JdbcDriverInformation extends BaseProductVersion {

  @Serial private static final long serialVersionUID = 7192167974028174124L;

  private final String connectionUrl;
  private final int driverMajorVersion;
  private final int driverMinorVersion;
  private final int jdbcMajorVersion;
  private final int jdbcMinorVersion;
  private final String driverClassName;
  private final boolean jdbcCompliant;

  public JdbcDriverInformation(
      final String driverName,
      final String driverClassName,
      final String driverVersion,
      final int driverMajorVersion,
      final int driverMinorVersion,
      final int jdbcMajorVersion,
      final int jdbcMinorVersion,
      final boolean jdbcCompliant,
      final String connectionUrl) {
    super(driverName, driverVersion);
    this.driverClassName =
        requireNonNull(driverClassName, "No database driver Java class name provided");
    this.driverMajorVersion = driverMajorVersion;
    this.driverMinorVersion = driverMinorVersion;
    this.jdbcMajorVersion = jdbcMajorVersion;
    this.jdbcMinorVersion = jdbcMinorVersion;
    this.jdbcCompliant = jdbcCompliant;
    this.connectionUrl = requireNotBlank(connectionUrl, "No database connection URL provided");
  }

  public String getConnectionUrl() {
    return connectionUrl;
  }

  public String getDriverClassName() {
    return driverClassName;
  }

  public int getDriverMajorVersion() {
    return driverMajorVersion;
  }

  public int getDriverMinorVersion() {
    return driverMinorVersion;
  }

  /**
   * Gets the name of the JDBC driver.
   *
   * @return Name of the JDBC driver
   */
  public String getDriverName() {
    return getProductName();
  }

  /**
   * Gets the version of the JDBC driver.
   *
   * @return Version of the JDBC driver
   */
  public String getDriverVersion() {
    return getProductVersion();
  }

  public int getJdbcMajorVersion() {
    return jdbcMajorVersion;
  }

  public int getJdbcMinorVersion() {
    return jdbcMinorVersion;
  }

  public boolean hasDriverClassName() {
    return !isBlank(driverClassName);
  }

  public boolean isJdbcCompliant() {
    return jdbcCompliant;
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    final StringBuilder info = new StringBuilder(1024);
    info.append("-- driver: ")
        .append(getProductName())
        .append(' ')
        .append(getProductVersion())
        .append(System.lineSeparator());
    info.append("-- driver class: ").append(getDriverClassName()).append(System.lineSeparator());
    info.append("-- url: ").append(getConnectionUrl()).append(System.lineSeparator());
    return info.toString();
  }
}
