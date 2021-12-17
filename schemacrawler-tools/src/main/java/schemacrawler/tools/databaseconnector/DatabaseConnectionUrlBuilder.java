/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2022, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.tools.databaseconnector;

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.isBlank;

import java.util.HashMap;
import java.util.Map;

import us.fatehi.utility.TemplatingUtility;

public class DatabaseConnectionUrlBuilder {

  public static DatabaseConnectionUrlBuilder builder(final String connectionUrlTemplate) {
    return new DatabaseConnectionUrlBuilder(connectionUrlTemplate);
  }

  private final String connectionUrlTemplate;
  private String defaultDatabase;
  private String defaultHost;
  private int defaultPort;
  private Map<String, String> defaultUrlx;

  private String providedDatabase;
  private String providedHost;
  private Integer providedPort;
  private Map<String, String> providedUrlx;

  private DatabaseConnectionUrlBuilder(final String connectionUrlTemplate) {
    this.connectionUrlTemplate =
        requireNonNull(connectionUrlTemplate, "No database connection URL template provided");

    this.defaultHost = "localhost";
    this.defaultDatabase = "";
  }

  public String toURL() {

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

  public Map<String, String> toUrlx() {
    final Map<String, String> urlx = new HashMap<>();
    if (defaultUrlx != null) {
      urlx.putAll(defaultUrlx);
    }
    if (providedUrlx != null) {
      urlx.putAll(providedUrlx);
    }
    return urlx;
  }

  public DatabaseConnectionUrlBuilder withDatabase(final String database) {
    this.providedDatabase = database;
    return this;
  }

  public DatabaseConnectionUrlBuilder withDefaultDatabase(final String defaultDatabase) {
    this.defaultDatabase = defaultDatabase;
    return this;
  }

  public DatabaseConnectionUrlBuilder withDefaultHost(final String defaultHost) {
    this.defaultHost = defaultHost;
    return this;
  }

  public DatabaseConnectionUrlBuilder withDefaultPort(final int defaultPort) {
    this.defaultPort = defaultPort;
    return this;
  }

  public DatabaseConnectionUrlBuilder withDefaultUrlx(final Map<String, String> defaultUrlx) {
    this.defaultUrlx = defaultUrlx;
    return this;
  }

  public DatabaseConnectionUrlBuilder withHost(final String host) {
    this.providedHost = host;
    return this;
  }

  public DatabaseConnectionUrlBuilder withPort(final Integer port) {
    this.providedPort = port;
    return this;
  }

  public DatabaseConnectionUrlBuilder withUrlx(final Map<String, String> urlx) {
    this.providedUrlx = urlx;
    return this;
  }
}
