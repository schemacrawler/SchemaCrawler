/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.plugins.dbplugins.model;

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.isBlank;
import static us.fatehi.utility.Utility.requireNotBlank;

import java.util.List;
import java.util.Set;

/** Represents a YAML database plugin definition. */
public record DatabaseConnectorDefinition(
    String server,
    String name,
    String urlTemplate,
    String urlPrefix,
    Integer defaultPort,
    List<UrlPropertyDefinition> defaultUrlProperties,
    Set<String> allowedDriverProperties,
    List<AdditionalOptionDefinition> additionalOptions,
    HelpDefinition help,
    SchemaRetrievalDefinition schemaRetrieval,
    SchemaSupportDefinition schemaSupport,
    String identifierQuoteString,
    LimitDefinition limit) {

  public DatabaseConnectorDefinition() {
    this(
        "unknown",
        "Unknown",
        "jdbc:unknown:${database}",
        "jdbc:unknown:",
        null,
        List.of(),
        Set.of(),
        List.of(),
        new HelpDefinition(),
        new SchemaRetrievalDefinition(),
        new SchemaSupportDefinition(),
        null,
        new LimitDefinition());
  }

  public DatabaseConnectorDefinition {
    server = requireNotBlank(server, "No database plugin server provided");
    name = requireNotBlank(name, "No database plugin name provided");
    urlTemplate = requireNotBlank(urlTemplate, "No database plugin URL template provided");
    urlPrefix = requireNotBlank(urlPrefix, "No database plugin URL prefix provided");
    defaultUrlProperties =
        defaultUrlProperties == null ? List.of() : List.copyOf(defaultUrlProperties);
    allowedDriverProperties =
        allowedDriverProperties == null ? Set.of() : Set.copyOf(allowedDriverProperties);
    additionalOptions = additionalOptions == null ? List.of() : List.copyOf(additionalOptions);
    help = help == null ? new HelpDefinition(null, null, null, null) : help;
    schemaRetrieval =
        schemaRetrieval == null ? new SchemaRetrievalDefinition(null) : schemaRetrieval;
    schemaSupport = schemaSupport == null ? new SchemaSupportDefinition(null, null) : schemaSupport;
    identifierQuoteString = isBlank(identifierQuoteString) ? null : identifierQuoteString;
    limit = limit == null ? new LimitDefinition(null, null, null, null) : limit;
    requireNonNull(help, "No help definition provided");
    requireNonNull(schemaRetrieval, "No schema retrieval definition provided");
    requireNonNull(schemaSupport, "No schema support definition provided");
    requireNonNull(limit, "No limit definition provided");
  }
}
