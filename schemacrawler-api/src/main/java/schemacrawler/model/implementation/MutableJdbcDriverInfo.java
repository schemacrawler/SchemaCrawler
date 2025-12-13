/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.model.implementation;


import schemacrawler.crawl.ImmutableJdbcDriverProperty;
import schemacrawler.crawl.SchemaCrawler;

import static java.util.Comparator.naturalOrder;
import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.isBlank;
import static us.fatehi.utility.Utility.requireNotBlank;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import schemacrawler.schema.JdbcDriverInfo;
import schemacrawler.schema.JdbcDriverProperty;
import us.fatehi.utility.property.BaseProductVersion;

/**
 * JDBC driver information. Created from metadata returned by a JDBC call, and other sources of
 * information.
 */
public final class MutableJdbcDriverInfo extends BaseProductVersion implements JdbcDriverInfo {

  @Serial private static final long serialVersionUID = 8030156654422512161L;

  private final String connectionUrl;
  private final int driverMajorVersion;
  private final int driverMinorVersion;
  private final int jdbcMajorVersion;
  private final int jdbcMinorVersion;
  private final String driverClassName;
  private final boolean jdbcCompliant;
  // Mutable properties collection
  private final Set<ImmutableJdbcDriverProperty> jdbcDriverProperties;

  public MutableJdbcDriverInfo(
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
    jdbcDriverProperties = new HashSet<>();
    this.connectionUrl = requireNotBlank(connectionUrl, "No database connection URL provided");
  }

  /** {@inheritDoc} */
  @Override
  public String getConnectionUrl() {
    return connectionUrl;
  }

  /** {@inheritDoc} */
  @Override
  public String getDriverClassName() {
    return driverClassName;
  }

  @Override
  public int getDriverMajorVersion() {
    return driverMajorVersion;
  }

  @Override
  public int getDriverMinorVersion() {
    return driverMinorVersion;
  }

  /** {@inheritDoc} */
  @Override
  public Collection<JdbcDriverProperty> getDriverProperties() {
    final List<JdbcDriverProperty> properties = new ArrayList<>(jdbcDriverProperties);
    properties.sort(naturalOrder());
    return properties;
  }

  @Override
  public int getJdbcMajorVersion() {
    return jdbcMajorVersion;
  }

  @Override
  public int getJdbcMinorVersion() {
    return jdbcMinorVersion;
  }

  @Override
  public boolean hasDriverClassName() {
    return !isBlank(driverClassName);
  }

  /** {@inheritDoc} */
  @Override
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

  /**
   * Adds a JDBC driver property.
   *
   * @param jdbcDriverProperty JDBC driver property
   */
  public void addJdbcDriverProperty(final ImmutableJdbcDriverProperty jdbcDriverProperty) {
    jdbcDriverProperties.add(jdbcDriverProperty);
  }
}
