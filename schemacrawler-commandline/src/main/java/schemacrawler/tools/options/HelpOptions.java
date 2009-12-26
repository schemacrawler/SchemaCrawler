/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2009, Sualeh Fatehi.
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

package schemacrawler.tools.options;


import schemacrawler.schemacrawler.Options;
import sf.util.Utility;

public class HelpOptions
  implements Options
{

  private static final long serialVersionUID = -2497570007150087268L;

  public static String about()
  {
    return Utility.readFully(HelpOptions.class
      .getResourceAsStream("/help/SchemaCrawler.txt"));
  }

  private final String title;
  private final String resourceApplicationOptions = "/help/ApplicationOptions.txt";
  private String resourceConnections = "/help/Connections.txt";
  private final String resourceConfig = "/help/Config.txt";
  private final String resourceSchemaCrawlerOptions = "/help/SchemaCrawlerOptions.txt";

  private boolean hideConfig;

  public HelpOptions(final String title)
  {
    this.title = title;
  }

  public String getResourceConnections()
  {
    return resourceConnections;
  }

  public String getTitle()
  {
    return title;
  }

  public boolean isHideConfig()
  {
    return hideConfig;
  }

  public void setHideConfig(final boolean hideConfig)
  {
    this.hideConfig = hideConfig;
  }

  public void setResourceConnections(final String resourceConnections)
  {
    this.resourceConnections = resourceConnections;
  }

}
