/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.commandline;

import static java.util.Objects.requireNonNull;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.addPluginCommands;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.catalogLoaderPluginCommands;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.commandPluginCommands;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.erModelLoaderPluginCommands;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.newCommandLine;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import org.jline.console.SystemRegistry;
import org.jline.console.impl.SystemRegistryImpl;
import org.jline.picocli.PicocliCommandRegistry;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.MaskingCallback;
import org.jline.reader.ParsedLine;
import org.jline.reader.Parser;
import org.jline.reader.impl.DefaultParser;
import org.jline.terminal.Terminal;
import org.jline.utils.InfoCmp.Capability;
import picocli.CommandLine;
import picocli.CommandLine.IFactory;

/**
 * Wires the picocli commands to a JLine terminal, and dispatches lines typed in the shell. The
 * JLine console registry classes are deprecated in JLine 4, but are what the picocli registry
 * targets.
 */
@SuppressWarnings("deprecation")
final class ShellSession {

  static CommandLine newShellCommandLine(final IFactory factory) {
    final SchemaCrawlerShellCommands commands = new SchemaCrawlerShellCommands();
    final CommandLine commandLine = newCommandLine(commands, factory);
    final CommandLine loadCommandLine = commandLine.getSubcommands().getOrDefault("load", null);
    if (loadCommandLine != null) {
      addPluginCommands(loadCommandLine, catalogLoaderPluginCommands);
      addPluginCommands(loadCommandLine, erModelLoaderPluginCommands);
      commandLine.addSubcommand(loadCommandLine);
    }
    final CommandLine executeCommandLine =
        commandLine.getSubcommands().getOrDefault("execute", null);
    if (executeCommandLine != null) {
      addPluginCommands(executeCommandLine, commandPluginCommands);
      commandLine.addSubcommand(executeCommandLine);
    }
    commandLine.setExecutionExceptionHandler(
        (ex, cmdLine, parseResult) -> {
          if (ex != null && ex.getMessage() != null) {
            cmdLine.getErr().printf("ERROR: %s%n", ex.getMessage());
          }
          return 0;
        });
    return commandLine;
  }

  private final Terminal terminal;
  private final CommandLine commandLine;
  private final SystemRegistry systemRegistry;
  private final Completer completer;
  private final LineReader reader;

  ShellSession(final Terminal terminal, final IFactory factory) {
    this.terminal = requireNonNull(terminal, "No terminal provided");
    commandLine = newShellCommandLine(factory);

    final Supplier<Path> workingDir = () -> Path.of(".");
    final PicocliCommandRegistry picocliCommands = new PicocliCommandRegistry(commandLine);
    final Parser parser = new DefaultParser().escapeChars(new char[0]);

    systemRegistry = new SystemRegistryImpl(parser, terminal, workingDir, null);
    systemRegistry.setCommandRegistries(picocliCommands);

    completer = systemRegistry.completer();

    reader =
        LineReaderBuilder.builder()
            .terminal(terminal)
            .completer(completer)
            .parser(parser)
            .variable(LineReader.LIST_MAX, 3) // max tab completion candidates
            .build();
    picocliCommands.setLineReader(reader);
  }

  void cleanUp() {
    systemRegistry.cleanUp();
  }

  List<String> complete(final String line) {
    final ParsedLine parsedLine = reader.getParser().parse(line, line.length());
    final List<Candidate> candidates = new ArrayList<>();
    completer.complete(reader, parsedLine, candidates);
    return candidates.stream().map(Candidate::value).toList();
  }

  /**
   * Runs a line typed in the shell.
   *
   * @throws EndOfFileException if the user asked to leave the shell
   */
  void execute(final String line) throws Exception {
    if (line.startsWith("help")) {
      final ParsedLine parsedLine = reader.getParser().parse(line, 0);
      final String[] arguments = parsedLine.words().toArray(new String[0]);
      commandLine.execute(arguments);
    } else if ("cls".equals(line) || "clear".equals(line)) {
      terminal.puts(Capability.clear_screen);
      terminal.flush();
    } else if ("quit".equals(line) || "terminate".equals(line)) {
      throw new EndOfFileException(line);
    } else {
      systemRegistry.execute(line);
    }
  }

  LineReader reader() {
    return reader;
  }

  String readLine(final String prompt) {
    return reader.readLine(prompt, null, (MaskingCallback) null, null);
  }

  void trace(final Exception e) {
    systemRegistry.trace(e);
  }
}
