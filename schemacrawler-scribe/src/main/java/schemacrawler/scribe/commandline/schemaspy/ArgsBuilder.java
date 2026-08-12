/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.scribe.commandline.schemaspy;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Orchestrates SchemaSpy-to-SchemaCrawler argument mapping across all command groups.
 *
 * <p>Input is supplied as five typed records — one per command group. Each group is mapped by a
 * dedicated {@code *ArgsMapper} class. Call {@link #toArgs(boolean)} to obtain the full
 * concatenated argument list ready to pass to {@code SchemaCrawlerCommandLine.execute()}.
 */
final class ArgsBuilder {

  private final ConnectArgsMapper connectMapper;
  private final ExecuteArgsMapper executeMapper;
  private final LimitArgsMapper limitMapper;
  private final LoadArgsMapper loadMapper;
  private final LogArgsMapper logMapper;

  ArgsBuilder(
      final LogArgsMapper.LogArgs logInput,
      final ConnectArgsMapper.ConnectArgs connectInput,
      final LimitArgsMapper.LimitArgs limitInput,
      final LoadArgsMapper.LoadArgs loadInput,
      final ExecuteArgsMapper.ExecuteArgs executeInput) {
    this.logMapper = new LogArgsMapper(requireNonNull(logInput, "No log input provided"));
    this.connectMapper =
        new ConnectArgsMapper(requireNonNull(connectInput, "No connect input provided"));
    this.limitMapper = new LimitArgsMapper(requireNonNull(limitInput, "No limit input provided"));
    this.loadMapper = new LoadArgsMapper(requireNonNull(loadInput, "No load input provided"));
    this.executeMapper =
        new ExecuteArgsMapper(requireNonNull(executeInput, "No execute input provided"));
  }

  /**
   * Returns all SchemaCrawler argument tokens concatenated from all groups.
   *
   * @param maskPassword if {@code true}, replaces the password value with {@code ******}
   * @return complete list of SchemaCrawler CLI argument tokens
   */
  List<String> toArgs(final boolean maskPassword) {
    final List<String> args = new ArrayList<>();
    args.addAll(logArgs());
    args.addAll(connectArgs(maskPassword));
    args.addAll(limitArgs());
    args.addAll(loadArgs());
    args.addAll(executeArgs());
    return args;
  }

  /** Returns argument tokens for the connection group. */
  List<String> connectArgs(final boolean maskPassword) {
    return connectMapper.toArgs(maskPassword);
  }

  /** Returns argument tokens for the execute/output group. */
  List<String> executeArgs() {
    return executeMapper.toArgs();
  }

  /**
   * Returns a formatted listing of all supported database type identifiers.
   *
   * @return human-readable multiline listing
   */
  String getAllSupportedDatabaseTypes() {
    return connectMapper.getAllSupportedDatabaseTypes();
  }

  /** Returns argument tokens for the limit group. */
  List<String> limitArgs() {
    return limitMapper.toArgs();
  }

  /** Returns argument tokens for the load group. */
  List<String> loadArgs() {
    return loadMapper.toArgs();
  }

  /** Returns argument tokens for the log/config group. */
  List<String> logArgs() {
    return logMapper.toArgs();
  }

  /**
   * Validates that the given type identifier is recognised, throwing if not.
   *
   * @param typeIdentifier the value of {@code -t}
   * @throws schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException if unknown
   */
  void validateDatabaseType(final String typeIdentifier) {
    connectMapper.resolveDatabaseType(typeIdentifier);
  }
}
