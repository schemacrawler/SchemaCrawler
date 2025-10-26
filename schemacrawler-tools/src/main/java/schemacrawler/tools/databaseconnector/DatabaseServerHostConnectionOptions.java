/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.databaseconnector;

import static java.util.Objects.requireNonNull;

import java.util.Collections;
import java.util.Map;

public record DatabaseServerHostConnectionOptions(
    String databaseSystemIdentifier,
    String host,
    Integer port,
    String database,
    Map<String, String> urlx)
    implements DatabaseConnectionOptions {

  public DatabaseServerHostConnectionOptions {
    databaseSystemIdentifier = requireNonNull(databaseSystemIdentifier, "No server provided");
    urlx = urlx == null ? Collections.emptyMap() : Collections.unmodifiableMap(urlx);
  }
}
