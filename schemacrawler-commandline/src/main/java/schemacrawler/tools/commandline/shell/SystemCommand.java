/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.commandline.shell;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import schemacrawler.Version;
import schemacrawler.tools.commandline.state.BaseStateHolder;
import schemacrawler.tools.commandline.state.ShellState;
import schemacrawler.tools.commandline.state.StateUtility;
import schemacrawler.tools.commandline.utility.CommandLineUtility;

@Command(
    name = "system",
    aliases = {"sys", "sys-info"},
    header = "** Display SchemaCrawler version and system information",
    headerHeading = "",
    synopsisHeading = "Shell Command:%n",
    customSynopsis = {"system"},
    optionListHeading = "Options:%n")
public class SystemCommand extends BaseStateHolder implements Runnable {

  @Option(
      names = {"-C", "--is-connected"},
      description = "Checks whether the shell has a connection to a database")
  private boolean isconnected;

  @Option(
      names = {"-L", "--is-loaded"},
      description = "Checks whether the shell has loaded database metadata")
  private boolean isloaded;

  @Option(names = "--show-stacktrace", description = "Shows stack trace from previous command")
  private boolean showstacktrace;

  @Option(names = "--show-state", description = "Shows internal state")
  private boolean showstate;

  @Option(
      names = {"-E", "--show-environment"},
      description = "Shows SchemaCrawler plugins and environment")
  private boolean showenvironment;

  @Option(
      names = {"-V", "--version"},
      description = "Display SchemaCrawler version and system information")
  private boolean versionRequested;

  public SystemCommand(final ShellState state) {
    super(state);
  }

  public boolean isShowEnvironment() {
    return showenvironment;
  }

  public boolean isVersionRequested() {
    return versionRequested;
  }

  @Override
  public void run() {
    if (versionRequested) {
      showVersion();
    } else if (showenvironment) {
      showEnvironment();
    } else if (isconnected) {
      showConnected();
    } else if (isloaded) {
      showLoaded();
    } else if (showstacktrace) {
      showStackTrace();
    } else if (showstate) {
      showState();
    } else {
      // Default - show all the information
      showEnvironment();
      System.out.println();

      showConnected();
      System.out.println();

      System.out.println("Load Information:");
      showLoaded();
      System.out.println();
    }
  }

  private void showConnected() {
    System.out.println(CommandLineUtility.getConnectionInfo(state));
  }

  private void showEnvironment() {
    CommandLineUtility.printEnvironment(state);
  }

  private void showLoaded() {
    final boolean isLoadedState = state.isLoaded();
    System.out.printf("  Database metadata is %sloaded%n", isLoadedState ? "" : "not ");
  }

  private void showStackTrace() {
    final Throwable lastExceptionState = state.getLastException();
    if (lastExceptionState != null) {
      lastExceptionState.printStackTrace(System.out);
    }
  }

  private void showState() {
    StateUtility.logState(state, true);
  }

  private void showVersion() {
    System.out.println(Version.about());
  }
}
