/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2013, Sualeh Fatehi.
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

  private final String id;
  private LintSeverity severity;
  private final Config config;

  public LinterConfig(final String id)
  {
    if (Utility.isBlank(id))
    {
      throw new IllegalArgumentException("No linter id provided");
    }
    this.id = id;
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

  public void putAll(final Config config2)
  {
    if (config != null)
    {
      config.putAll(config2);
    }
  }

  public void setSeverity(final LintSeverity severity)
  {
    this.severity = severity;
  }

}
