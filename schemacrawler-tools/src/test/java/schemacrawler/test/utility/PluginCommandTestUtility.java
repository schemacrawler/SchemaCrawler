/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test.utility;

import static java.util.Objects.requireNonNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static us.fatehi.test.utility.extensions.FileHasContent.classpathResource;
import static us.fatehi.test.utility.extensions.FileHasContent.hasSameContentAs;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;

import java.util.Collection;
import schemacrawler.tools.executable.commandline.PluginCommand;
import schemacrawler.tools.executable.commandline.PluginCommandOption;
import us.fatehi.test.utility.TestWriter;
import us.fatehi.test.utility.extensions.TestContext;

public class PluginCommandTestUtility {

  public static void testPluginCommand(
      final TestContext testContext, final PluginCommand pluginCommand) {
    requireNonNull(testContext, "No test context provided");
    requireNonNull(pluginCommand, "No plugin command provided");

    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      out.println(pluginCommand.getName());
      out.println();
      out.println(pluginCommand.getHelpHeader());
      final String[] helpDescription = pluginCommand.getHelpDescription().get();
      writeStringArray(out, helpDescription);
      out.println();
      final Collection<PluginCommandOption> options = pluginCommand.getOptions();
      for (final PluginCommandOption pluginCommandOption : options) {
        out.println(pluginCommandOption.getName());
        final String[] optionHelpText = pluginCommandOption.getHelpText();
        writeStringArray(out, optionHelpText);
        out.println();
      }
      final String[] helpFooter = pluginCommand.getHelpFooter().get();
      writeStringArray(out, helpFooter);
    }
    assertThat(
        outputOf(testout), hasSameContentAs(classpathResource(testContext.testMethodFullName())));
  }

  private static void writeStringArray(final TestWriter out, final String[] helpText) {
    for (final String helpLine : helpText) {
      out.println(helpLine);
    }
  }

  private PluginCommandTestUtility() {
    // Prevent instantiation
  }
}
