/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.commandline.utility;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import picocli.AutoComplete;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.codegen.docgen.manpage.ManPageGenerator;
import schemacrawler.schemacrawler.exceptions.IORuntimeException;
import schemacrawler.schemacrawler.exceptions.InternalRuntimeException;
import schemacrawler.tools.commandline.SchemaCrawlerCommandLineCommands;
import schemacrawler.tools.commandline.command.ConfigFileCommand;
import schemacrawler.tools.commandline.command.ConnectCommand;
import schemacrawler.tools.commandline.command.ExecuteCommand;
import schemacrawler.tools.commandline.command.FilterCommand;
import schemacrawler.tools.commandline.command.GrepCommand;
import schemacrawler.tools.commandline.command.LimitCommand;
import schemacrawler.tools.commandline.command.LoadCommand;
import schemacrawler.tools.commandline.command.LogCommand;
import schemacrawler.tools.commandline.state.ShellState;
import schemacrawler.tools.commandline.state.StateFactory;
import us.fatehi.utility.UtilityMarker;

@UtilityMarker
public final class GenerateCliSupport {

  @CommandLine.Command(
      name = "generate-schemacrawler-cli-support",
      mixinStandardHelpOptions = true,
      description = "Generate SchemaCrawler CLI man pages and bash completion scripts")
  private static final class GenerateCliSupportCommand implements Runnable {

    @CommandLine.Option(
        names = {"-o", "--output-dir"},
        description = "Output directory for generated files",
        required = true)
    private Path outputDir;

    @CommandLine.Option(
        names = {"-s", "--completion-script"},
        description = "File name for the generated bash completion script",
        required = true)
    private String completionScriptFilename;

    @Override
    public void run() {
      boolean isErrored = false;

      outputDir = createOutputDirectory();
      final Path completionScript = outputDir.resolve(completionScriptFilename).toAbsolutePath();

      if (generateAsciiDoc()) {
        isErrored = true;
      }
      if (generateAutoComplete(completionScript)) {
        isErrored = true;
      }

      if (isErrored) {
        throw new InternalRuntimeException("Could not generate CLI support files");
      }
    }

    protected Path createOutputDirectory() {
      try {
        Files.createDirectories(outputDir);
      } catch (final IOException e) {
        throw new IORuntimeException("Could not create output directory", e);
      }
      return outputDir.toAbsolutePath();
    }

    private CommandLine createCommandLine(final Object commands) {
      final ShellState state = new ShellState();
      final StateFactory stateFactory = new StateFactory(state);
      return CommandLineUtility.newCommandLine(commands, stateFactory);
    }

    private boolean generateAsciiDoc() {

      @Command(
          name = "schemacrawler",
          subcommands = {
            ConfigFileCommand.class,
            ConnectCommand.class,
            FilterCommand.class,
            GrepCommand.class,
            LimitCommand.class,
            LoadCommand.class,
            ExecuteCommand.class,
            LogCommand.class,
          })
      final class SchemaCrawlerCli {}

      boolean isErrored = false;
      final boolean[] verbosity = new boolean[3];
      Arrays.fill(verbosity, true);
      try {
        ManPageGenerator.generateManPage(
            outputDir.toFile(),
            null,
            verbosity,
            true,
            createCommandLine(new SchemaCrawlerCli()).getCommandSpec());
      } catch (final IOException e) {
        isErrored = true;
        LOGGER.log(Level.SEVERE, "Could not generate man pages in AsciiDoc format", e);
      }
      return isErrored;
    }

    private boolean generateAutoComplete(final Path completionScript) {
      boolean isErrored = false;
      try {
        final CommandLine commandLine = createCommandLine(new SchemaCrawlerCommandLineCommands());
        AutoComplete.bash("schemacrawler", completionScript.toFile(), null, commandLine);
      } catch (final IOException e) {
        isErrored = true;
        LOGGER.log(Level.SEVERE, "Could not generate bash completion scripts", e);
      }
      return isErrored;
    }
  }

  private static final Logger LOGGER = Logger.getLogger(GenerateCliSupport.class.getName());

  public static void main(final String[] args) {
    final int exitCode = new CommandLine(new GenerateCliSupportCommand()).execute(args);
    if (exitCode != 0) {
      throw new RuntimeException("Could not generate CLI support files");
    }
  }
}
