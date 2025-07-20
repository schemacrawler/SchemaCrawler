/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility.datasource;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import static us.fatehi.utility.Utility.isBlank;
import static us.fatehi.utility.Utility.requireNotBlank;
import us.fatehi.utility.Builder;
import us.fatehi.utility.TemplatingUtility;

public class DatabaseConnectionSourceBuilder implements Builder<DatabaseConnectionSource> {

  public static DatabaseConnectionSourceBuilder builder(final String connectionUrlTemplate) {
    return new DatabaseConnectionSourceBuilder().withConnectionUrl(connectionUrlTemplate);
  }

  private String connectionUrlTemplate;
  private String defaultDatabase;
  private String defaultHost;
  private int defaultPort;
  private Map<String, String> defaultUrlx;
  private UserCredentials userCredentials;
  private Consumer<Connection> connectionInitializer;
  private String providedDatabase;
  private String providedHost;
  private Integer providedPort;
  private Map<String, String> providedUrlx;

  private DatabaseConnectionSourceBuilder() {
    defaultHost = "localhost";
    defaultDatabase = "";
    userCredentials = new MultiUseUserCredentials();
    connectionInitializer = connection -> {};
  }

  @Override
  public DatabaseConnectionSource build() {
    final String connectionUrl = toURL();
    final Map<String, String> connectionUrlx = toUrlx();
    final DatabaseConnectionSource databaseConnectionSource =
        DatabaseConnectionSources.newDatabaseConnectionSource(
            connectionUrl, connectionUrlx, userCredentials, connectionInitializer);
    return databaseConnectionSource;
  }

  public Consumer<Connection> getConnectionInitializer() {
    return connectionInitializer;
  }

  public DatabaseConnectionSourceBuilder withConnectionInitializer(
      final Consumer<Connection> connectionInitializer) {
    if (connectionInitializer == null) {
      this.connectionInitializer = connection -> {};
    } else {
      this.connectionInitializer = connectionInitializer;
    }
    return this;
  }

  public DatabaseConnectionSourceBuilder withConnectionUrl(final String connectionUrlTemplate) {
    this.connectionUrlTemplate = connectionUrlTemplate;
    return this;
  }

  public DatabaseConnectionSourceBuilder withDatabase(final String database) {
    providedDatabase = database;
    return this;
  }

  public DatabaseConnectionSourceBuilder withDefaultDatabase(final String defaultDatabase) {
    this.defaultDatabase = defaultDatabase;
    return this;
  }

  public DatabaseConnectionSourceBuilder withDefaultHost(final String defaultHost) {
    this.defaultHost = defaultHost;
    return this;
  }

  public DatabaseConnectionSourceBuilder withDefaultPort(final int defaultPort) {
    this.defaultPort = defaultPort;
    return this;
  }

  public DatabaseConnectionSourceBuilder withDefaultUrlx(final Map<String, String> defaultUrlx) {
    this.defaultUrlx = defaultUrlx;
    return this;
  }

  public DatabaseConnectionSourceBuilder withDefaultUrlx(
      final String property, final boolean value) {
    return withDefaultUrlx(property, String.valueOf(value));
  }

  public DatabaseConnectionSourceBuilder withDefaultUrlx(
      final String property, final String value) {
    if (defaultUrlx == null) {
      defaultUrlx = new HashMap<>();
    }
    defaultUrlx.put(property, value);
    return this;
  }

  public DatabaseConnectionSourceBuilder withHost(final String host) {
    providedHost = host;
    return this;
  }

  public DatabaseConnectionSourceBuilder withPort(final Integer port) {
    providedPort = port;
    return this;
  }

  public DatabaseConnectionSourceBuilder withUrlx(final Map<String, String> urlx) {
    providedUrlx = urlx;
    return this;
  }

  public DatabaseConnectionSourceBuilder withUserCredentials(
      final UserCredentials userCredentials) {
    if (userCredentials == null) {
      this.userCredentials = new MultiUseUserCredentials();
    } else {
      this.userCredentials = userCredentials;
    }
    return this;
  }

  String toURL() {

    requireNotBlank(connectionUrlTemplate, "No database connection URL template provided");

    final String host;
    if (isBlank(providedHost)) {
      host = defaultHost;
    } else {
      host = providedHost;
    }

    final int port;
    if (providedPort == null || providedPort < 0 || providedPort > 65535) {
      port = defaultPort;
    } else {
      port = providedPort;
    }

    final String database;
    if (isBlank(providedDatabase)) {
      database = defaultDatabase;
    } else {
      database = providedDatabase;
    }

    final Map<String, String> map = new HashMap<>();
    map.put("host", host);
    map.put("port", String.valueOf(port));
    map.put("database", database);

    final String url = TemplatingUtility.expandTemplate(connectionUrlTemplate, map);

    return url;
  }

  Map<String, String> toUrlx() {
    final Map<String, String> urlx = new HashMap<>();
    if (defaultUrlx != null) {
      urlx.putAll(defaultUrlx);
    }
    if (providedUrlx != null) {
      urlx.putAll(providedUrlx);
    }
    return urlx;
  }
}
