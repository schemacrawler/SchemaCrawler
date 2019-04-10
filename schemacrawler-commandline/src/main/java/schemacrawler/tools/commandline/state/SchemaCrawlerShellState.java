/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2019, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.tools.commandline.state;


import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;

import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaRetrievalOptionsBuilder;
import schemacrawler.tools.options.OutputOptionsBuilder;
import sf.util.SchemaCrawlerLogger;

public class SchemaCrawlerShellState
{

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger
    .getLogger(SchemaCrawlerShellState.class.getName());
  private Config additionalConfiguration;
  private Config baseConfiguration;
  private Catalog catalog;
  private DataSource dataSource;
  private OutputOptionsBuilder outputOptionsBuilder;
  private SchemaCrawlerOptionsBuilder schemaCrawlerOptionsBuilder;
  private SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder;

  public Config getBaseConfiguration()
  {
    if (baseConfiguration != null)
    {
      return baseConfiguration;
    }
    else
    {
      return new Config();
    }
  }

  public void setBaseConfiguration(final Config baseConfiguration)
  {
    if (baseConfiguration != null)
    {
      this.baseConfiguration = baseConfiguration;
    }
    else
    {
      this.baseConfiguration = new Config();
    }
  }

  public void disconnect()
  {
    if (dataSource instanceof AutoCloseable)
    {
      try
      {
        ((AutoCloseable) dataSource).close();
      }
      catch (final Exception e)
      {
        // Ignore errors
      }
    }
    dataSource = null;
  }

  public Config getAdditionalConfiguration()
  {
    return additionalConfiguration;
  }

  public void setAdditionalConfiguration(final Config additionalConfiguration)
  {
    this.additionalConfiguration = additionalConfiguration;
  }

  public Catalog getCatalog()
  {
    return catalog;
  }

  public void setCatalog(final Catalog catalog)
  {
    this.catalog = catalog;
  }

  public DataSource getDataSource()
  {
    return dataSource;
  }

  public void setDataSource(final DataSource dataSource)
  {
    this.dataSource = dataSource;
  }

  public OutputOptionsBuilder getOutputOptionsBuilder()
  {
    return outputOptionsBuilder;
  }

  public void setOutputOptionsBuilder(final OutputOptionsBuilder outputOptionsBuilder)
  {
    this.outputOptionsBuilder = outputOptionsBuilder;
  }

  public SchemaCrawlerOptionsBuilder getSchemaCrawlerOptionsBuilder()
  {
    return schemaCrawlerOptionsBuilder;
  }

  public void setSchemaCrawlerOptionsBuilder(final SchemaCrawlerOptionsBuilder schemaCrawlerOptionsBuilder)
  {
    this.schemaCrawlerOptionsBuilder = schemaCrawlerOptionsBuilder;
  }

  public SchemaRetrievalOptionsBuilder getSchemaRetrievalOptionsBuilder()
  {
    return schemaRetrievalOptionsBuilder;
  }

  public void setSchemaRetrievalOptionsBuilder(final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder)
  {
    this.schemaRetrievalOptionsBuilder = schemaRetrievalOptionsBuilder;
  }

  public boolean isConnected()
  {
    try (final Connection connection = dataSource.getConnection())
    {
      LOGGER.log(Level.INFO,
                 "Connected to: " + connection.getMetaData()
                   .getDatabaseProductName());
    }
    catch (final NullPointerException | SQLException e)
    {
      LOGGER.log(Level.WARNING, e.getMessage(), e);
      return false;
    }

    return true;
  }

  public boolean isLoaded()
  {
    return catalog != null;
  }

  public void sweep()
  {
    catalog = null;
    additionalConfiguration = null;
    schemaCrawlerOptionsBuilder = null;
    schemaRetrievalOptionsBuilder = null;
    outputOptionsBuilder = null;

    disconnect();
  }

}
