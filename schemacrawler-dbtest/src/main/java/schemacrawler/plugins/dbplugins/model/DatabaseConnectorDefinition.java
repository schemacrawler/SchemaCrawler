/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.plugins.dbplugins.model;

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.requireNotBlank;

import java.util.List;
import java.util.Set;

/** Represents a YAML database plugin definition. */
public record DatabaseConnectorDefinition(
    DatabaseServerTypeDefinition databaseServerType,
    String urlTemplate,
    String urlPrefix,
    StandardOptionsDefinition standardOptions,
    Set<String> allowedDriverProperties,
    List<AdditionalOptionDefinition> additionalOptions,
    SchemaRetrievalDefinition schemaRetrieval,
    LimitDefinition limit) {

  public DatabaseConnectorDefinition() {
    this(
        new DatabaseServerTypeDefinition(),
        "jdbc:unknown:${database}",
        "jdbc:unknown:",
        new StandardOptionsDefinition(),
        Set.of(),
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
    schemaRetrieval =
        schemaRetrieval == null ? new SchemaRetrievalDefinition(null, null, null) : schemaRetrieval;
    limit = limit == null ? new LimitDefinition(null, null, null, null) : limit;
    requireNonNull(databaseServerType, "No database server type definition provided");
    requireNonNull(standardOptions, "No standard options definition provided");
    requireNonNull(schemaRetrieval, "No schema retrieval definition provided");
    requireNonNull(limit, "No limit definition provided");
  }
}
