/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.lint.config;

import static us.fatehi.utility.Utility.requireNotBlank;

import java.beans.ConstructorProperties;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.inclusionrule.RegularExpressionRule;
import schemacrawler.tools.lint.LintSeverity;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.ConfigUtility;
import us.fatehi.utility.ObjectToString;

public final class LinterConfig implements Serializable, Comparable<LinterConfig> {

  @Serial private static final long serialVersionUID = 83079182550531365L;

  private final String linterId;
  private final Map<String, Object> config;
  private final boolean runLinter;
  private final LintSeverity severity;
  private final int threshold;

  private final String tableInclusionPattern;
  private final String tableExclusionPattern;
  private final String columnInclusionPattern;
  private final String columnExclusionPattern;

  @ConstructorProperties({
    "id",
    "run",
    "severity",
    "threshold",
    "table-inclusion-pattern",
    "table-exclusion-pattern",
    "column-inclusion-pattern",
    "column-exclusion-pattern",
    "config"
  })
  public LinterConfig(
      final String linterId,
      final Boolean runLinter,
      final LintSeverity severity,
      final Integer threshold,
      final String tableInclusionPattern,
      final String tableExclusionPattern,
      final String columnInclusionPattern,
      final String columnExclusionPattern,
      final Map<String, Object> configMap) {
    this.linterId = requireNotBlank(linterId, "No linter id provided");
    this.runLinter = runLinter == null ? true : runLinter;
    this.severity = severity;
    this.threshold = threshold == null ? Integer.MAX_VALUE : threshold;
    this.tableInclusionPattern = tableInclusionPattern;
    this.tableExclusionPattern = tableExclusionPattern;
    this.columnInclusionPattern = columnInclusionPattern;
    this.columnExclusionPattern = columnExclusionPattern;
    this.config = configMap == null ? new HashMap<>() : new HashMap<>(configMap);
  }

  @Override
  public int compareTo(final LinterConfig other) {
    if (other == null) {
      return -1;
    }

    int comparison = 0;

    if (comparison == 0) {
      comparison =
          -1
              * (severity == null ? LintSeverity.low : severity)
                  .compareTo(other.severity == null ? LintSeverity.low : other.severity);
    }

    if (comparison == 0) {
      comparison = linterId.compareTo(other.linterId);
    }

    return comparison;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if ((obj == null) || !(obj instanceof LinterConfig)) {
      return false;
    }
    final LinterConfig other = (LinterConfig) obj;
    if (!Objects.equals(linterId, other.linterId)) {
      return false;
    }
    if (severity != other.severity) {
      return false;
    }
    return true;
  }

  public InclusionRule getColumnInclusionRule() {
    return new RegularExpressionRule(columnInclusionPattern, columnExclusionPattern);
  }

  public Config getConfig() {
    return ConfigUtility.fromMap(config);
  }

  public String getLinterId() {
    return linterId;
  }

  public LintSeverity getSeverity() {
    return severity;
  }

  public InclusionRule getTableInclusionRule() {
    return new RegularExpressionRule(tableInclusionPattern, tableExclusionPattern);
  }

  public int getThreshold() {
    return threshold;
  }

  @Override
  public int hashCode() {
    return Objects.hash(linterId, severity);
  }

  public boolean isRunLinter() {
    return runLinter;
  }

  @Override
  public String toString() {
    return ObjectToString.toString(this);
  }

  void setContext(final Map<String, Object> config) {
    if (config != null) {
      // Shade with the linter config
      final Map<String, Object> linterConfig = new HashMap<>(config);
      linterConfig.putAll(this.config);
      //
      this.config.clear();
      this.config.putAll(linterConfig);
    }
  }
}
