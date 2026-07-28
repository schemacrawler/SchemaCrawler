/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.plugins.dbplugins.model;

import static us.fatehi.utility.Utility.isBlank;

import org.jspecify.annotations.NonNull;
import schemacrawler.plugins.dbplugins.yaml.JsonUtility;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;
import us.fatehi.utility.datasource.DatabaseServerType;

/** Represents database server type information in YAML. */
@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
public record DatabaseServerTypeDefinition(@NonNull String server, @NonNull String name) {

  public DatabaseServerTypeDefinition() {
    this(null, null);
  }

  public DatabaseServerTypeDefinition {
    server = isBlank(server) ? null : server;
    name = isBlank(name) ? null : name;
  }

  public DatabaseServerType toDatabaseServerType() {
    if (isBlank(server)) {
      return DatabaseServerType.UNKNOWN;
    }
    return new DatabaseServerType(server, name);
  }

  @Override
  public String toString() {
    return JsonUtility.mapper.writeValueAsString(this);
  }
}
