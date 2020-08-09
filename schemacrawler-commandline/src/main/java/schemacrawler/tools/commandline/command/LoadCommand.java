/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.tools.commandline.command;


import static java.util.Objects.requireNonNull;

import java.sql.Connection;
import java.util.logging.Level;

import picocli.CommandLine.Command;
import picocli.CommandLine.ExecutionException;
import picocli.CommandLine.Model;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.InfoLevel;
import schemacrawler.schemacrawler.LoadOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.tools.catalogloader.CatalogLoader;
import schemacrawler.tools.catalogloader.CatalogLoaderRegistry;
import schemacrawler.tools.commandline.state.BaseStateHolder;
import schemacrawler.tools.commandline.state.SchemaCrawlerShellState;
import schemacrawler.SchemaCrawlerLogger;
import sf.util.StringFormat;

@Command(name = "load",
         header = "** Load database metadata into memory",
         description = {
           ""
         },
         headerHeading = "",
         synopsisHeading = "Shell Command:%n",
         customSynopsis = {
           "load"
         },
         optionListHeading = "Options:%n")
public class LoadCommand
  extends BaseStateHolder
  implements Runnable
{

  private static final SchemaCrawlerLogger LOGGER =
    SchemaCrawlerLogger.getLogger(LoadCommand.class.getName());

  @Option(names = {
    "-i", "--info-level"
  }, required = true, description = {
    "<infolevel> is one of ${COMPLETION-CANDIDATES}",
    "The info level determines the amount of database metadata retrieved, "
    + "and also determines the time taken to crawl the schema",
    "Optional, defaults to standard\n"
  })
  private InfoLevel infolevel;

  @Option(names = {
    "--load-row-counts"
  }, description = {
    "Loads row counts for each table",
    "This can be a time consuming operation",
    "Optional, defaults to false\n"
  })
  private boolean isLoadRowCounts;

  @Spec
  private Model.CommandSpec spec;

  public LoadCommand(final SchemaCrawlerShellState state)
  {
    super(state);
  }

  public InfoLevel getInfoLevel()
  {
    return infolevel;
  }

  public boolean isLoadRowCounts()
  {
    return isLoadRowCounts;
  }

  @Override
  public void run()
  {
    if (!state.isConnected())
    {
      throw new ExecutionException(spec.commandLine(),
                                   "Not connected to the database");
    }

    final SchemaCrawlerOptionsBuilder schemaCrawlerOptionsBuilder =
      state.getSchemaCrawlerOptionsBuilder();

    final LoadOptionsBuilder loadOptionsBuilder = LoadOptionsBuilder
      .builder()
      .fromOptions(schemaCrawlerOptionsBuilder.getLoadOptions());

    if (infolevel != null)
    {
      loadOptionsBuilder.withSchemaInfoLevel(infolevel.toSchemaInfoLevel());
    }

    loadOptionsBuilder.loadRowCounts(isLoadRowCounts);

    schemaCrawlerOptionsBuilder.withLoadOptionsBuilder(loadOptionsBuilder);

    final Catalog catalog = loadCatalog();
    state.setCatalog(catalog);
    LOGGER.log(Level.INFO, "Loaded catalog");
  }

  private Catalog loadCatalog()
  {
    try (
      final Connection connection = state
        .getDataSource()
        .get()
    )
    {
      LOGGER.log(Level.INFO, new StringFormat("infolevel=%s", infolevel));

      final Config additionalConfiguration = state.getAdditionalConfiguration();
      final SchemaRetrievalOptions schemaRetrievalOptions = state
        .getSchemaRetrievalOptionsBuilder()
        .toOptions();
      final SchemaCrawlerOptions schemaCrawlerOptions = state
        .getSchemaCrawlerOptionsBuilder()
        .toOptions();

      final CatalogLoaderRegistry catalogLoaderRegistry =
        new CatalogLoaderRegistry();
      final CatalogLoader catalogLoader =
        catalogLoaderRegistry.lookupCatalogLoader(schemaRetrievalOptions
                                                    .getDatabaseServerType()
                                                    .getDatabaseSystemIdentifier());
      LOGGER.log(Level.CONFIG,
                 new StringFormat("Catalog loader: %s", getClass().getName()));

      catalogLoader.setAdditionalConfiguration(additionalConfiguration);
      catalogLoader.setConnection(connection);
      catalogLoader.setSchemaRetrievalOptions(schemaRetrievalOptions);
      catalogLoader.setSchemaCrawlerOptions(schemaCrawlerOptions);

      final Catalog catalog = catalogLoader.loadCatalog();
      requireNonNull(catalog, "Catalog could not be retrieved");

      return catalog;

    }
    catch (final Exception e)
    {
      throw new ExecutionException(spec.commandLine(),
                                   "Cannot load catalog",
                                   e);
    }
  }

}
