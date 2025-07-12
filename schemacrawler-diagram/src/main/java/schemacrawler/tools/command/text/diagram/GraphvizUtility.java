/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.command.text.diagram;

import static schemacrawler.tools.command.text.diagram.options.DiagramOutputFormat.plain;
import static schemacrawler.tools.command.text.diagram.options.DiagramOutputFormat.png;
import static schemacrawler.tools.command.text.diagram.options.DiagramOutputFormat.ps;
import static schemacrawler.tools.command.text.diagram.options.DiagramOutputFormat.svg;
import static schemacrawler.tools.command.text.diagram.options.DiagramOutputFormat.xdot;
import static us.fatehi.utility.PropertiesUtility.getSystemConfigurationProperty;
import static us.fatehi.utility.Utility.isClassAvailable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.tools.command.text.diagram.options.DiagramOutputFormat;
import us.fatehi.utility.ProcessExecutor;
import us.fatehi.utility.string.FileContents;
import us.fatehi.utility.string.StringFormat;

public final class GraphvizUtility {

  private static final Logger LOGGER = Logger.getLogger(GraphvizUtility.class.getName());

  private static final String SC_GRAPHVIZ_PROC_DISABLE = "SC_GRAPHVIZ_PROC_DISABLE";

  public static boolean isGraphvizAvailable() {

    final boolean disableGraphviz =
        Boolean.valueOf(getSystemConfigurationProperty(SC_GRAPHVIZ_PROC_DISABLE, "false"));
    if (disableGraphviz) {
      LOGGER.log(Level.CONFIG, "Not creating a native process for Grahviz, since this is disabled");
      return false;
    }

    final List<String> command = new ArrayList<>();
    command.add("dot");
    command.add("-V");

    LOGGER.log(
        Level.FINE, new StringFormat("Checking if Graphviz is available:%n%s", command.toString()));

    final ProcessExecutor processExecutor = new ProcessExecutor();
    processExecutor.setCommandLine(command);

    Integer exitCode;
    try {
      exitCode = processExecutor.call();
      LOGGER.log(
          Level.CONFIG,
          new StringFormat(
              "Graphviz stdout:%n%s", new FileContents(processExecutor.getProcessOutput())));
      LOGGER.log(
          Level.CONFIG,
          new StringFormat(
              "Graphviz stderr:%n%s", new FileContents(processExecutor.getProcessError())));
    } catch (final Exception e) {
      LOGGER.log(Level.WARNING, "Could not execute Graphviz command", e);
      LOGGER.log(
          Level.WARNING,
          new StringFormat(
              "Graphviz stderr:%n%s", new FileContents(processExecutor.getProcessError())));

      exitCode = Integer.MIN_VALUE;
    }
    final boolean successful = exitCode != null && exitCode == 0;
    LOGGER.log(
        Level.CONFIG,
        new StringFormat(
            "Checking if diagram can be generated with Graphviz - "
                + "\n is Graphviz installed? = <%s>",
            successful));

    return successful;
  }

  public static boolean isGraphvizJavaAvailable(final DiagramOutputFormat diagramOutputFormat) {
    final String className = "guru.nidi.graphviz.engine.Graphviz";
    final boolean hasClass = isClassAvailable(className);
    final boolean supportsFormat =
        EnumSet.of(svg, png, ps, xdot, plain).contains(diagramOutputFormat);

    LOGGER.log(
        Level.CONFIG,
        new StringFormat(
            "Checking if diagram can be generated with the Graphviz Java library - "
                + "\n can load <%s>? = <%b>, "
                + "\n can generate format <%s>? = <%b>",
            className, hasClass, diagramOutputFormat.getDescription(), supportsFormat));

    return hasClass && supportsFormat;
  }

  private GraphvizUtility() {
    // Prevent instantiation
  }
}
