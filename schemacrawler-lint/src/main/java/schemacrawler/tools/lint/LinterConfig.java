/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi.
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
package schemacrawler.tools.lint;


import static sf.util.Utility.isBlank;

import java.io.Serializable;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.InclusionRule;
import schemacrawler.schemacrawler.RegularExpressionRule;
import sf.util.ObjectToString;

public class LinterConfig
  implements Serializable, Comparable<LinterConfig>
{

  private static final long serialVersionUID = 83079182550531365L;

  private final String linterId;
  private boolean runLinter;
  private LintSeverity severity;
  private int threshold;
  private final Config config;
  private String tableInclusionPattern;
  private String tableExclusionPattern;
  private String columnInclusionPattern;
  private String columnExclusionPattern;

  public LinterConfig(final String linterId)
  {
    if (isBlank(linterId))
    {
      throw new IllegalArgumentException("No linter id provided");
    }
    this.linterId = linterId;
    runLinter = true; // default value
    threshold = Integer.MAX_VALUE; // default value
    config = new Config();
  }

  @Override
  public int compareTo(final LinterConfig other)
  {
    if (other == null)
    {
      return -1;
    }

    int comparison = 0;

    if (comparison == 0)
    {
      comparison = (severity == null? LintSeverity.low: severity)
        .compareTo(other.severity == null? LintSeverity.low: other.severity);
    }

    if (comparison == 0)
    {
      comparison = linterId.compareTo(other.linterId);
    }

    return comparison;
  }

  public InclusionRule getColumnInclusionRule()
  {
    return new RegularExpressionRule(columnInclusionPattern,
                                     columnExclusionPattern);
  }

  public Config getConfig()
  {
    return config;
  }

  public String getLinterId()
  {
    return linterId;
  }

  public LintSeverity getSeverity()
  {
    return severity;
  }

  public InclusionRule getTableInclusionRule()
  {
    return new RegularExpressionRule(tableInclusionPattern,
                                     tableExclusionPattern);
  }

  public int getThreshold()
  {
    return threshold;
  }

  public boolean isRunLinter()
  {
    return runLinter;
  }

  public void put(final String key, final String value)
  {
    config.put(key, value);
  }

  public void putAll(final Config config)
  {
    this.config.putAll(config);
  }

  public void setColumnExclusionPattern(final String columnExclusionPattern)
  {
    this.columnExclusionPattern = columnExclusionPattern;
  }

  public void setColumnInclusionPattern(final String columnInclusionPattern)
  {
    this.columnInclusionPattern = columnInclusionPattern;
  }

  public void setRunLinter(final boolean runLinter)
  {
    this.runLinter = runLinter;
  }

  public void setSeverity(final LintSeverity severity)
  {
    this.severity = severity;
  }

  public void setTableExclusionPattern(final String tableExclusionPattern)
  {
    this.tableExclusionPattern = tableExclusionPattern;
  }

  public void setTableInclusionPattern(final String tableInclusionPattern)
  {
    this.tableInclusionPattern = tableInclusionPattern;
  }

  public void setThreshold(final int threshold)
  {
    this.threshold = threshold;
  }

  @Override
  public String toString()
  {
    return ObjectToString.toString(this);
  }

}
