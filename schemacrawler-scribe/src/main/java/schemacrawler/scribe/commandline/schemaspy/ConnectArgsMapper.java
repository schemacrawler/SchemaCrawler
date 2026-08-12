/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.scribe.commandline.schemaspy;

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.isBlank;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import schemacrawler.tools.databaseconnector.DatabaseConnectorRegistry;

/**
 * Maps SchemaSpy connection options to SchemaCrawler {@code --server}/{@code --url}, {@code
 * --host}, {@code --port}, {@code --database}, {@code --user}, {@code --password}, and repeated
 * {@code --urlx} argument tokens.
 *
 * <p>Resolution order for {@code -t}:
 *
 * <ol>
 *   <li>SchemaSpy 6.x built-in type enum ({@link DatabaseType})
 *   <li>Registered SchemaCrawler server identifier ({@link DatabaseConnectorRegistry})
 * </ol>
 */
final class ConnectArgsMapper {

  /** Input record for connection options. */
  record ConnectArgs(
      String databaseType,
      String database,
      String host,
      Integer port,
      boolean sso,
      String user,
      String password,
      String connectionProperties) {}

  private record DatabaseTypeResolution(String server, boolean isUrlFallback) {}

  private final ConnectArgs input;

  ConnectArgsMapper(final ConnectArgs input) {
    this.input = requireNonNull(input, "No connect input provided");
  }

  /**
   * Returns argument tokens for the SchemaCrawler connection group.
   *
   * @param maskPassword if {@code true}, replaces the password value with {@code ******}
   * @return connection argument tokens
   */
  List<String> toArgs(final boolean maskPassword) {
    final DatabaseTypeResolution resolution = resolveDatabaseType(input.databaseType());
    final List<String> args = new ArrayList<>();

    if (resolution.isUrlFallback()) {
      args.add("--url");
    } else {
      args.add("--server");
      args.add(resolution.server());
      args.add("--host");
      args.add(input.host());
      if (input.port() != null) {
        args.add("--port");
        args.add(String.valueOf(input.port()));
      }
      args.add("--database");
    }
    args.add(input.database());

    if (!input.sso() && !isBlank(input.user())) {
      args.add("--user");
      args.add(input.user());
    }
    if (!input.sso() && !isBlank(input.password())) {
      args.add("--password");
      args.add(maskPassword ? "******" : input.password());
    }
    addConnectionProperties(args);
    return args;
  }

  private void addConnectionProperties(final List<String> args) {
    if (isBlank(input.connectionProperties())) {
      return;
    }

    final String[] pairs = input.connectionProperties().split("\\s*[;,]\\s*");
    for (final String pair : pairs) {
      if (isBlank(pair)) {
        continue;
      }
      args.add("--urlx");
      args.add(pair.trim());
    }
  }

  /**
   * Returns a formatted listing of all supported database type identifiers.
   *
   * @return human-readable multiline listing
   */
  String getAllSupportedDatabaseTypes() {
    final List<String> allTypes = new ArrayList<>();
    allTypes.add("SchemaSpy database types:");
    allTypes.add("  " + DatabaseType.supportedDatabaseTypes());
    final DatabaseConnectorRegistry registry = DatabaseConnectorRegistry.getRegistry();
    final List<String> serverTypes =
        registry.getDatabaseServerTypes().stream()
            .map(st -> st.getDatabaseSystemIdentifier())
            .collect(Collectors.toList());
    if (!serverTypes.isEmpty()) {
      allTypes.add("\nSchemaCrawler database servers:");
      allTypes.add("  " + String.join(", ", serverTypes));
    }
    return String.join("\n", allTypes);
  }

  /**
   * Resolves a SchemaSpy {@code -t} type identifier to a SchemaCrawler server or URL-fallback.
   *
   * @param typeIdentifier the value of {@code -t}
   * @return resolution result
   * @throws ExecutionRuntimeException if the identifier is not recognised
   */
  DatabaseTypeResolution resolveDatabaseType(final String typeIdentifier) {
    requireNonNull(typeIdentifier, "No database type provided");
    final Optional<DatabaseType> schemaSpyType = DatabaseType.fromType(typeIdentifier);
    if (schemaSpyType.isPresent()) {
      final DatabaseType type = schemaSpyType.get();
      if (type.isUrlFallback()) {
        return new DatabaseTypeResolution(null, true);
      }
      return new DatabaseTypeResolution(type.getSchemaCrawlerServer().orElseThrow(), false);
    }
    final DatabaseConnectorRegistry registry = DatabaseConnectorRegistry.getRegistry();
    if (registry.hasDatabaseSystemIdentifier(typeIdentifier)) {
      return new DatabaseTypeResolution(typeIdentifier, false);
    }
    throw new ExecutionRuntimeException(
        "Unknown database type <%s>. Supported database types:\n%s"
            .formatted(typeIdentifier, getAllSupportedDatabaseTypes()));
  }
}
