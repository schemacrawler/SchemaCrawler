/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.plugins.dbplugins.model;

import static us.fatehi.utility.Utility.requireNotBlank;

import java.util.List;
import java.util.Set;

/** Represents a YAML database plugin definition. */
public record DatabaseConnectorDefinition(
    DatabaseServerTypeDefinition databaseServerType,
    String urlTemplate,
    String urlPrefix,
    Set<String> allowedDriverProperties,
    StandardOptionsDefinition standardOptions,
    List<AdditionalOptionDefinition> additionalOptions,
    SchemaRetrievalDefinition schemaRetrieval,
    LimitDefinition limit) {

  public DatabaseConnectorDefinition() {
    this(
        new DatabaseServerTypeDefinition(),
        "jdbc:unknown:${database}",
        "jdbc:unknown:",
        Set.of(),
        new StandardOptionsDefinition(),
        List.of(),
        new SchemaRetrievalDefinition(),
        new LimitDefinition());
  }

  public DatabaseConnectorDefinition {
    databaseServerType =
        databaseServerType == null ? new DatabaseServerTypeDefinition() : databaseServerType;
    urlTemplate = requireNotBlank(urlTemplate, "No database plugin URL template provided");
    urlPrefix = requireNotBlank(urlPrefix, "No database plugin URL prefix provided");
    standardOptions = standardOptions == null ? new StandardOptionsDefinition() : standardOptions;
    allowedDriverProperties =
        allowedDriverProperties == null ? Set.of() : Set.copyOf(allowedDriverProperties);
    additionalOptions = additionalOptions == null ? List.of() : List.copyOf(additionalOptions);
    schemaRetrieval = schemaRetrieval == null ? new SchemaRetrievalDefinition() : schemaRetrieval;
    limit = limit == null ? new LimitDefinition() : limit;
  }
}
