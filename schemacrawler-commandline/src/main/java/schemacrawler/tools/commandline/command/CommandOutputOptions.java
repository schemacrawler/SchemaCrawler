/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.commandline.command;

import static us.fatehi.utility.Utility.isBlank;

import java.nio.file.Path;
import java.util.Optional;
import picocli.CommandLine.Option;

public final class CommandOutputOptions {

  @Option(
      names = {"-o", "--output-file"},
      description = {
        "Generate output in a named file",
        "<outputfile> is the path to the output file",
        "Optional, defaults to the console (stdout) for text output, "
            + "and a random file in the current directory for binary output"
      })
  private Path outputFile;

  @Option(
      names = {"-F", "--output-format"},
      description = {
        "Format of the SchemaCrawler output",
        "Supported formats are dependent on the SchemaCrawler command being executed",
        "",
        "For the schema output commands, <outputformat> is one of:",
        "  text - For text output (default)",
        "  html - For HTML5 output",
        "",
        "You can generate a database diagram using Graphviz",
        "For a diagram <outputformat> is one of dot, svg, or png",
        "For a complete list Graphviz output formats, see http://www.graphviz.org/",
        "You can generate HTML output format with an embedded SVG diagram "
            + "with an <outputformat> of htmlx",
        "IMPORTANT: Graphviz needs to be installed, and available on the system PATH",
        "http://www.graphviz.org/",
        "If Graphviz is not installed, a DOT file is produced.",
        "Or, use:",
        "  scdot - For Graphviz DOT output, for schema commands only",
        "",
        "Optional, defaults to the format specified by the output file, " + "otherwise, text"
      })
  private String outputFormatValue;

  @Option(
      names = {"-m", "--title"},
      description = {"Shows the title on the output", "Optional, defaults to no title being shown"})
  private String title;

  public Optional<Path> getOutputFile() {
    if (outputFile == null) {
      return Optional.empty();
    } else {
      return Optional.of(outputFile.normalize().toAbsolutePath());
    }
  }

  public Optional<String> getOutputFormatValue() {
    if (isBlank(outputFormatValue)) {
      return Optional.empty();
    } else {
      return Optional.of(outputFormatValue);
    }
  }

  public Optional<String> getTitle() {
    if (isBlank(title)) {
      return Optional.empty();
    } else {
      return Optional.of(title);
    }
  }
}
