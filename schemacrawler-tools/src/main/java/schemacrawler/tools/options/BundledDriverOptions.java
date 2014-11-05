/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2014, Sualeh Fatehi.
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


import java.sql.Connection;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.InformationSchemaViews;
import schemacrawler.schemacrawler.Options;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.executable.BaseExecutable;
import schemacrawler.tools.executable.Executable;

public abstract class BundledDriverOptions
  implements Options
{

  private final class NoOpExecutable
    extends BaseExecutable
  {
    private NoOpExecutable(final String command)
    {
      super(command);
    }

    @Override
    public void execute(final Connection connection)
      throws Exception
    {
      // No-op
    }
  }

  private static final long serialVersionUID = 2160456864554076419L;

  private final HelpOptions helpOptions;
  private final String configResource;
  private final String informationSchemaViewsResourceFolder;

  protected BundledDriverOptions()
  {
    helpOptions = new HelpOptions();
    configResource = null;
    informationSchemaViewsResourceFolder = null;
  }

  protected BundledDriverOptions(final String title,
                                 final String helpResource,
                                 final String configResource,
                                 final String informationSchemaViewsResourceFolder)
  {
    helpOptions = new HelpOptions(title, helpResource);
    this.configResource = configResource;
    this.informationSchemaViewsResourceFolder = informationSchemaViewsResourceFolder;
  }

  public final Config getConfig()
  {
    final Config config = Config.loadResource(configResource);

    InformationSchemaViews informationSchemaViews = new InformationSchemaViews();
    informationSchemaViews.loadResourceFolder(informationSchemaViewsResourceFolder);
    config.putAll(informationSchemaViews.toConfig());

    return config;
  }

  public final HelpOptions getHelpOptions()
  {
    return helpOptions;
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

  public final boolean hasConfig()
  {
    return configResource != null;
  }

  public Executable newPostExecutable()
    throws SchemaCrawlerException
  {
    return new NoOpExecutable("no-op");
  }

  public Executable newPreExecutable()
    throws SchemaCrawlerException
  {
    return new NoOpExecutable("no-op");
  }

}
