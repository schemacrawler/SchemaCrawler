/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.scribe.command;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

import java.util.ServiceLoader;
import java.util.function.Supplier;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import schemacrawler.tools.command.SchemaCrawlerCommandProvider;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.OutputOptionsBuilder;

public class ScribeCommandProviderTest {

  @Test
  public void scribeCommandIsSupportedViaServiceLoader() {
    boolean found = false;
    for (final SchemaCrawlerCommandProvider provider :
        ServiceLoader.load(SchemaCrawlerCommandProvider.class)) {
      if (provider instanceof ScribeCommandProvider) {
        found = true;
      }
    }
    assertThat(found, is(true));
  }

  @Test
  public void supportsCommandName() {
    final ScribeCommandProvider provider = new ScribeCommandProvider();
    assertThat(provider.supportsSchemaCrawlerCommand("scribe", null, null, null), is(true));
    assertThat(provider.supportsSchemaCrawlerCommand("schemaspy", null, null, null), is(false));
  }

  @Test
  public void supportsOutputFormatOnlyForKnownFormat() {
    final ScribeCommandProvider provider = new ScribeCommandProvider();
    final OutputOptions unsupportedOutputOptions =
        OutputOptionsBuilder.builder().withOutputFormatValue("unsupported").toOptions();
    final OutputOptions supportedOutputOptions =
        OutputOptionsBuilder.builder().withOutputFormatValue("okf").toOptions();
    assertThat(provider.supportsOutputFormat("scribe", unsupportedOutputOptions), is(false));
    assertThat(provider.supportsOutputFormat("scribe", supportedOutputOptions), is(true));
  }

  @Test
  public void commandLineCommandHasExpectedOptions() {
    final ScribeCommandProvider provider = new ScribeCommandProvider();
    final Object pluginCommand = provider.getCommandLineCommand();

    assertThat(optionNamed(pluginCommand, "language"), is(true));
    assertThat(optionNamed(pluginCommand, "include-lint"), is(true));
    assertThat(optionNamed(pluginCommand, "generate-diagrams"), is(false));
    assertThat(optionNamed(pluginCommand, "expanded-output"), is(true));
    // Output format and title are global options (-F/--output-format, -m/--title); declaring
    // them here as well would collide with those options and break command-line assembly.
    assertThat(optionNamed(pluginCommand, "output-format"), is(false));
    assertThat(optionNamed(pluginCommand, "title"), is(false));
    assertThat(optionNamed(pluginCommand, "include-table-counts"), is(false));
    assertThat(optionNamed(pluginCommand, "locale"), is(false));
  }

  @Test
  @Disabled
  public void outputFormatsFooterListsSupportedFormats() throws Exception {
    final ScribeCommandProvider provider = new ScribeCommandProvider();
    final Object pluginCommand = provider.getCommandLineCommand();
    @SuppressWarnings("unchecked")
    final Supplier<String[]> helpFooterSupplier =
        (Supplier<String[]>)
            pluginCommand.getClass().getMethod("getHelpFooter").invoke(pluginCommand);
    final String footer = String.join(" ", helpFooterSupplier.get());
    assertThat(footer, containsString("okf"));
  }

  private boolean optionNamed(final Object pluginCommand, final String optionName) {
    try {
      @SuppressWarnings("unchecked")
      final Iterable<Object> options = (Iterable<Object>) pluginCommand;
      for (final Object option : options) {
        final String name = (String) option.getClass().getMethod("getName").invoke(option);
        if (name.equals(optionName)) {
          return true;
        }
      }
      return false;
    } catch (final ReflectiveOperationException e) {
      throw new RuntimeException("Could not inspect command options", e);
    }
  }
}
