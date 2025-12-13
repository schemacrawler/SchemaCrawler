/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.model.implementation;


import schemacrawler.crawl.ImmutableDatabaseProperty;
import schemacrawler.crawl.SchemaCrawler;

import static java.util.Comparator.naturalOrder;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import schemacrawler.schema.DatabaseInfo;
import us.fatehi.utility.property.BaseProductVersion;
import us.fatehi.utility.property.Property;

/**
 * Database and connection information. Created from metadata returned by a JDBC call, and other
 * sources of information.
 */
public final class MutableDatabaseInfo extends BaseProductVersion implements DatabaseInfo {

  @Serial private static final long serialVersionUID = 4051323422934251828L;

  private final String userName;
  // Mutable properties collection
  private final Set<Property> serverInfo;
  private final Set<Property> databaseProperties;

  public MutableDatabaseInfo(
      final String databaseProductName,
      final String databaseProductVersion,
      final String userName) {
    super(databaseProductName, databaseProductVersion);
    this.userName = userName;

    serverInfo = new HashSet<>();
    databaseProperties = new HashSet<>();
  }

  /** {@inheritDoc} */
  @Override
  public Collection<Property> getProperties() {
    final List<Property> properties = new ArrayList<>(databaseProperties);
    properties.sort(naturalOrder());
    return properties;
  }

  @Override
  public Collection<Property> getServerInfo() {
    return new TreeSet<>(serverInfo);
  }

  /** {@inheritDoc} */
  @Override
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

  public void addAll(final Collection<ImmutableDatabaseProperty> dbProperties) {
    if (dbProperties != null) {
      databaseProperties.addAll(dbProperties);
    }
  }

  public void addServerInfo(final Property property) {
    if (property != null) {
      serverInfo.add(property);
    }
  }
}
