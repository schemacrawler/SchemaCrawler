/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2015, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */
package schemacrawler.tools.lint;


import static sf.util.Utility.isBlank;

import java.io.Serializable;

import schemacrawler.schemacrawler.Config;
import sf.util.ObjectToString;

public class LinterConfig
  implements Serializable
{

  private static final long serialVersionUID = 83079182550531365L;

  private final String id;
  private boolean runLinter;
  private LintSeverity severity;
  private final Config config;
  private String tableInclusionPattern;
  private String tableExclusionPattern;

  public LinterConfig(final String id)
  {
    if (isBlank(id))
    {
      throw new IllegalArgumentException("No linter id provided");
    }
    this.id = id;
    runLinter = true; // default value
    config = new Config();
  }

  public Config getConfig()
  {
    return config;
  }

  public String getId()
  {
    return id;
  }

  public LintSeverity getSeverity()
  {
    return severity;
  }

  public String getTableExclusionPattern()
  {
    return tableExclusionPattern;
  }

  public String getTableInclusionPattern()
  {
    return tableInclusionPattern;
  }

  public boolean isRunLinter()
  {
    return runLinter;
  }

  public void putAll(final Config config2)
  {
    if (config != null)
    {
      config.putAll(config2);
    }
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

  @Override
  public String toString()
  {
    return ObjectToString.toString(this);
  }

}
