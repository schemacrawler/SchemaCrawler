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
package schemacrawler.tools.options;


import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.Options;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;

public abstract class BundledDriverOptions
  implements Options
{

  private static final long serialVersionUID = 2160456864554076419L;

  private final String title;
  private final String resourceConnections;
  private final String configResource;

  protected BundledDriverOptions(final String title,
                                 final String resourceConnections,
                                 final String configResource)
  {
    this.title = title;
    this.resourceConnections = resourceConnections;
    this.configResource = configResource;
  }

  public final Config getConfig()
  {
    return Config.loadResource(configResource);
  }

  public final HelpOptions getHelpOptions()
  {
    return new HelpOptions(title, resourceConnections);
  }

  public final SchemaCrawlerOptions getSchemaCrawlerOptions(final InfoLevel infoLevel)
  {
    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions(getConfig());
    if (infoLevel != null)
    {
      schemaCrawlerOptions.setSchemaInfoLevel(infoLevel.getSchemaInfoLevel());
    }
    return schemaCrawlerOptions;
  }

}
