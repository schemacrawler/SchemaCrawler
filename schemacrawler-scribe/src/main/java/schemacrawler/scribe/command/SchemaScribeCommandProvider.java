/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.scribe.command;

import static schemacrawler.scribe.command.SchemaScribeCommand.COMMAND;
import static schemacrawler.tools.executable.commandline.PluginCommand.newPluginCommand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;

import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import schemacrawler.scribe.command.options.SchemaScribeOptions;
import schemacrawler.scribe.command.options.SchemaScribeOptionsBuilder;
import schemacrawler.scribe.renderer.ScribeRenderer;
import schemacrawler.tools.command.AbstractSchemaCrawlerCommandProvider;
import schemacrawler.tools.executable.commandline.PluginCommand;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.OutputOptions;

/** Registers the Scribe command with the SchemaCrawler command line. */
public final class SchemaScribeCommandProvider extends AbstractSchemaCrawlerCommandProvider {

  /**
   * Builds a help footer listing whichever {@link ScribeRenderer} modules are on the classpath for
   * this run, for use with the global {@code --output-format} option. There is no static,
   * compile-time list of formats since renderer formats are discovered only via {@link
   * ServiceLoader} at runtime, by design — the core module never depends on, or knows about, any
   * specific renderer module.
   *
   * @return Help footer lines listing the currently-registered output formats
   */
  private static String[] outputFormatsFooter() {
    final List<String> formats = new ArrayList<>();
    final ClassLoader classLoader = SchemaScribeCommandProvider.class.getClassLoader();
    for (final ScribeRenderer renderer : ServiceLoader.load(ScribeRenderer.class, classLoader)) {
      formats.add(renderer.getSupportedOutputFormat());
    }
    Collections.sort(formats);
    if (formats.isEmpty()) {
      return new String[0];
    }
    return new String[] {
      "Scribe report output formats (for --output-format): %s".formatted(String.join(", ", formats))
    };
  }

  /** Creates the Scribe command provider, supporting only the {@code scribe} command. */
  public SchemaScribeCommandProvider() {
    super(COMMAND);
  }

  /**
   * {@inheritDoc}
   *
   * <p>Note: The currently-registered output formats are listed in this command's help footer,
   * built dynamically (see {@link #outputFormatsFooter()}).
   */
  @Override
  public PluginCommand getCommandLineCommand() {
    final PluginCommand pluginCommand =
        newPluginCommand(COMMAND, null, SchemaScribeCommandProvider::outputFormatsFooter);
    pluginCommand
        .addOption(
            "language",
            String.class,
            "BCP 47 language tag for localized report text, for example, fr or de-DE; "
                + "the default is the system locale")
        .addOption("include-lint", Boolean.class, "Whether to include lint results in the report")
        .addOption(
            "expanded-output",
            Boolean.class,
            "Whether to write the report as expanded files and folders instead of a single ZIP "
                + "archive");
    return pluginCommand;
  }

  /** {@inheritDoc} */
  @Override
  public SchemaScribeCommand newCommand(final String command, final Config config) {
    if (!supportsCommand(command)) {
      throw new ExecutionRuntimeException("Unsupported command <%s>".formatted(command));
    }
    final SchemaScribeOptions options =
        SchemaScribeOptionsBuilder.builder().fromConfig(config).toOptions();
    final SchemaScribeCommand scribeCommand = new SchemaScribeCommand();
    scribeCommand.configure(options);
    return scribeCommand;
  }

  /** {@inheritDoc} */
  @Override
  public boolean supportsOutputFormat(final String command, final OutputOptions outputOptions) {
    if (outputOptions == null) {
      return false;
    }
    final String format = outputOptions.getOutputFormatValue();
    return "okf".equals(format);
  }
}
