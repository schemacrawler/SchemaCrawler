/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2012, Sualeh Fatehi.
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


import java.io.Serializable;

import schemacrawler.schemacrawler.Config;
import sf.util.Utility;

public class LinterConfig
  implements Serializable
{

  private static final long serialVersionUID = 83079182550531365L;

  private final String linterId;
  private LintSeverity severity;
  private final Config config;

  public LinterConfig(final String linterId)
  {
    if (Utility.isBlank(linterId))
    {
      throw new IllegalArgumentException("No linter id provided");
    }
    this.linterId = linterId;
    config = new Config();
  }

  public boolean getBooleanValue(final String propertyName)
  {
    return config.getBooleanValue(propertyName);
  }

  public int getIntegerValue(final String propertyName, final int defaultValue)
  {
    return config.getIntegerValue(propertyName, defaultValue);
  }

  public String getLinterId()
  {
    return linterId;
  }

  public LintSeverity getSeverity()
  {
    return severity;
  }

  public String getStringValue(final String propertyName,
                               final String defaultValue)
  {
    return config.getStringValue(propertyName, defaultValue);
  }

  public void putAll(final Config config2)
  {
    if (config != null)
    {
      config.putAll(config);
    }
  }

  public void setConfigValue(final String propertyName, final String value)
  {
    config.put(propertyName, value);
  }

  public void setSeverity(final LintSeverity severity)
  {
    this.severity = severity;
  }

}
