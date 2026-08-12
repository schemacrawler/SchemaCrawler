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

/**
 * Maps SchemaSpy catalog/schema/table filter options to SchemaCrawler {@code --schemas} and {@code
 * --tables} argument tokens.
 *
 * <p>Schema priority order: {@code -schemaSpec} &gt; {@code -schemas} &gt; {@code -s}.
 *
 * <p>Table pattern: when both {@code -i} (include) and {@code -I} (exclude) are supplied, they are
 * combined using an AND-NOT lookahead regex.
 */
final class LimitArgsMapper {

  /** Input record for limit/filter options. */
  record LimitArgs(
      String catalog,
      String schema,
      String schemas,
      String schemaRegex,
      String includeTableRegex,
      String excludeTableRegex) {}

  private final LimitArgs input;

  LimitArgsMapper(final LimitArgs input) {
    this.input = requireNonNull(input, "No limit input provided");
  }

  /**
   * Returns argument tokens for the SchemaCrawler limit group.
   *
   * <p>Covers: {@code --schemas}, {@code --tables}.
   *
   * @return limit argument tokens
   */
  List<String> toArgs() {
    final List<String> args = new ArrayList<>();

    final String schemaFilter = resolveSchemaFilter();
    if (schemaFilter != null) {
      args.add("--schemas");
      args.add(schemaFilter);
    }

    final String tableFilter = resolveTableFilter();
    if (tableFilter != null) {
      args.add("--tables");
      args.add(tableFilter);
    }

    args.add("--routines");
    args.add(".*");
    args.add("--sequences");
    args.add(".*");
    args.add("--synonyms");
    args.add(".*");

    return args;
  }

  private String convertSchemasListToRegex(final String schemaList) {
    final String[] parts = schemaList.split(",");
    final List<String> escaped = new ArrayList<>();
    for (final String s : parts) {
      final String trimmed = s.trim();
      if (!trimmed.isEmpty()) {
        escaped.add("\\Q" + trimmed + "\\E");
      }
    }
    if (escaped.isEmpty()) {
      return null;
    }
    return "(" + String.join("|", escaped) + ")";
  }

  private String resolveSchemaFilter() {
    final String schemaPattern = resolveSchemaPattern();
    if (!isBlank(input.catalog())) {
      if (schemaPattern == null) {
        return "\\Q" + input.catalog().trim() + "\\E\\..*";
      }
      return "\\Q" + input.catalog().trim() + "\\E\\." + schemaPattern;
    }

    if (schemaPattern == null) {
      return null;
    }

    return ".*\\." + schemaPattern;
  }

  private String resolveSchemaPattern() {
    if (!isBlank(input.schemaRegex())) {
      return input.schemaRegex();
    }
    if (!isBlank(input.schemas())) {
      return convertSchemasListToRegex(input.schemas());
    }
    if (!isBlank(input.schema())) {
      return "\\Q" + input.schema().trim() + "\\E";
    }
    return null;
  }

  private String resolveTableFilter() {
    final boolean hasInclude = !isBlank(input.includeTableRegex());
    final boolean hasExclude = !isBlank(input.excludeTableRegex());
    if (!hasInclude && !hasExclude) {
      return null;
    }
    if (hasInclude && !hasExclude) {
      return input.includeTableRegex();
    }
    if (!hasInclude) {
      return "(?!" + input.excludeTableRegex() + ").*";
    }
    return "(?=.*" + input.includeTableRegex() + ")(?!" + input.excludeTableRegex() + ").*";
  }
}
