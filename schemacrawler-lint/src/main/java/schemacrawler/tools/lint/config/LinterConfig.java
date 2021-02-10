/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2021, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.tools.lint.config;

import static us.fatehi.utility.Utility.isBlank;
import static us.fatehi.utility.Utility.requireNotBlank;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.inclusionrule.RegularExpressionRule;
import schemacrawler.tools.lint.LintSeverity;
import schemacrawler.tools.options.Config;
import us.fatehi.utility.ObjectToString;

public class LinterConfig implements Serializable, Comparable<LinterConfig> {

  private static final long serialVersionUID = 83079182550531365L;

  @JsonProperty("id")
  private final String linterId;

  private final Map<String, Object> config;

  @JsonProperty("run")
  private boolean runLinter;

  private LintSeverity severity;
  private int threshold;

  @JsonProperty("table-inclusion-pattern")
  private String tableInclusionPattern;

  @JsonProperty("table-exclusion-pattern")
  private String tableExclusionPattern;

  @JsonProperty("column-inclusion-pattern")
  private String columnInclusionPattern;

  @JsonProperty("column-exclusion-pattern")
  private String columnExclusionPattern;

  @JsonCreator
  public LinterConfig(@JsonProperty("id") final String linterId) {
    this.linterId = requireNotBlank(linterId, "No linter id provided");
    runLinter = true; // default value
    threshold = Integer.MAX_VALUE; // default value
    config = new HashMap<>();
  }

  @Override
  public int compareTo(final LinterConfig other) {
    if (other == null) {
      return -1;
    }

    int comparison = 0;

    if (comparison == 0) {
      comparison =
          (severity == null ? LintSeverity.low : severity)
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
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof LinterConfig)) {
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
    return new Config(config);
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
    final int prime = 31;
    int result = 1;
    result = prime * result + (linterId == null ? 0 : linterId.hashCode());
    result = prime * result + (severity == null ? 0 : severity.hashCode());
    return result;
  }

  public boolean isRunLinter() {
    return runLinter;
  }

  public void put(final String key, final String value) {
    if (isBlank(key)) {
      return;
    }
    config.put(key, value);
  }

  public void setColumnExclusionPattern(final String columnExclusionPattern) {
    this.columnExclusionPattern = columnExclusionPattern;
  }

  public void setColumnInclusionPattern(final String columnInclusionPattern) {
    this.columnInclusionPattern = columnInclusionPattern;
  }

  public void setContext(final Map<String, Object> config) {
    if (config != null) {
      // Shade with the linter config
      final Map<String, Object> linterConfig = new HashMap<>();
      linterConfig.putAll(config);
      linterConfig.putAll(this.config);
      //
      this.config.clear();
      this.config.putAll(linterConfig);
    }
  }

  public void setRunLinter(final boolean runLinter) {
    this.runLinter = runLinter;
  }

  public void setSeverity(final LintSeverity severity) {
    this.severity = severity;
  }

  public void setTableExclusionPattern(final String tableExclusionPattern) {
    this.tableExclusionPattern = tableExclusionPattern;
  }

  public void setTableInclusionPattern(final String tableInclusionPattern) {
    this.tableInclusionPattern = tableInclusionPattern;
  }

  public void setThreshold(final int threshold) {
    this.threshold = threshold;
  }

  @Override
  public String toString() {
    return ObjectToString.toString(this);
  }
}
