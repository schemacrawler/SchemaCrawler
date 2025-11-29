/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.commandline.command;

import static schemacrawler.tools.commandline.utility.CommandLineUtility.matchedOptionValues;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import picocli.CommandLine.Command;
import picocli.CommandLine.ExecutionException;
import picocli.CommandLine.Model;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParseResult;
import picocli.CommandLine.Spec;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.InfoLevel;
import schemacrawler.schemacrawler.LoadOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.tools.commandline.state.BaseStateHolder;
import schemacrawler.tools.commandline.state.ShellState;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.utility.SchemaCrawlerUtility;
import us.fatehi.utility.string.ObjectToStringFormat;
import us.fatehi.utility.string.StringFormat;

@Command(
    name = "load",
    header = "** Load database metadata into memory using a chain of catalog loaders",
    description = {""},
    headerHeading = "",
    synopsisHeading = "Shell Command:%n",
    customSynopsis = {"load"},
    optionListHeading = "Options:%n",
    footer = {
      "",
      "For additional options, specific to individual catalog loaders,",
      "run SchemaCrawler with: `-h loaders`",
      "or from the SchemaCrawler interactive shell: `help loaders`"
    })
public class LoadCommand extends BaseStateHolder implements Runnable {

  private static final Logger LOGGER = Logger.getLogger(LoadCommand.class.getName());

  @Option(
      names = {"-i", "--info-level"},
      required = true,
      description = {
        "<infolevel> is one of ${COMPLETION-CANDIDATES}",
        "The info level determines the amount of database metadata retrieved, "
            + "and also determines the time taken to crawl the schema",
        "Optional, defaults to standard"
      })
  private InfoLevel infolevel;

  @Spec private Model.CommandSpec spec;

  public LoadCommand(final ShellState state) {
    super(state);
  }

  public InfoLevel getInfoLevel() {
    return infolevel;
  }

  @Override
  public void run() {

    try {
      // Parse and save command options
      saveCommandOptions();

      if (state.isDeferCatalogLoad()) {
        LOGGER.log(Level.CONFIG, "Not loading catalog, since this is deferred");
        return;
      }

      if (!state.isConnected()) {
        throw new ExecutionException(spec.commandLine(), "Not connected to the database");
      }

      final Catalog catalog = loadCatalog();
      state.setCatalog(catalog);
      LOGGER.log(Level.INFO, "Loaded catalog");

    } catch (final Exception e) {
      throw new ExecutionException(spec.commandLine(), "Cannot load catalog", e);
    }
  }

  private Catalog loadCatalog() {
    try {
      LOGGER.log(Level.INFO, new StringFormat("infolevel=%s", infolevel));

      final SchemaRetrievalOptions schemaRetrievalOptions = state.getSchemaRetrievalOptions();
      final SchemaCrawlerOptions schemaCrawlerOptions = state.getSchemaCrawlerOptions();
      final Config additionalConfig = state.getConfig();

      return SchemaCrawlerUtility.getCatalog(
          state.getDataSource(), schemaRetrievalOptions, schemaCrawlerOptions, additionalConfig);

    } catch (final Exception e) {
      throw new ExecutionException(spec.commandLine(), "Cannot load catalog", e);
    }
  }

  private void saveCommandOptions() {
    final SchemaCrawlerOptions schemaCrawlerOptions = state.getSchemaCrawlerOptions();

    final LoadOptionsBuilder loadOptionsBuilder =
        LoadOptionsBuilder.builder().fromOptions(schemaCrawlerOptions.loadOptions());

    if (infolevel != null) {
      loadOptionsBuilder.withSchemaInfoLevel(infolevel.toSchemaInfoLevel());
    }

    state.withLoadOptions(loadOptionsBuilder.toOptions());

    final ParseResult parseResult = spec.commandLine().getParseResult();
    final Map<String, Object> catalogLoaderOptions = matchedOptionValues(parseResult);
    LOGGER.log(Level.INFO, "Loaded command loader options");
    LOGGER.log(Level.CONFIG, new ObjectToStringFormat(catalogLoaderOptions));
    state.setCatalogLoaderOptions(catalogLoaderOptions);
  }
}
