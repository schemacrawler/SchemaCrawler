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
import static org.hamcrest.Matchers.not;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ServiceLoader;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import schemacrawler.scribe.command.options.SchemaScribeOptions;
import schemacrawler.scribe.output.ScribeOutputContext;
import schemacrawler.scribe.renderer.ScribeRenderer;
import schemacrawler.scribe.renderer.ScribeSupport;
import schemacrawler.tools.command.SchemaCrawlerCommandProvider;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.OutputOptionsBuilder;

public class SchemaScribeCommandProviderTest {

  @Test
  public void scribeCommandIsSupportedViaServiceLoader() {
    boolean found = false;
    for (final SchemaCrawlerCommandProvider provider :
        ServiceLoader.load(SchemaCrawlerCommandProvider.class)) {
      if (provider instanceof SchemaScribeCommandProvider) {
        found = true;
      }
    }
    assertThat(found, is(true));
  }

  @Test
  public void supportsCommandName() {
    final SchemaScribeCommandProvider provider = new SchemaScribeCommandProvider();
    assertThat(provider.supportsSchemaCrawlerCommand("scribe", null, null, null), is(true));
    assertThat(provider.supportsSchemaCrawlerCommand("schemaspy", null, null, null), is(false));
  }

  @Test
  public void supportsOutputFormatWithNoRendererOnClasspath() {
    final SchemaScribeCommandProvider provider = new SchemaScribeCommandProvider();
    final OutputOptions outputOptions =
        OutputOptionsBuilder.builder().withOutputFormatValue("text").toOptions();
    assertThat(provider.supportsOutputFormat("scribe", outputOptions), is(false));
  }

  @Test
  public void commandLineCommandHasExpectedOptions() {
    final SchemaScribeCommandProvider provider = new SchemaScribeCommandProvider();
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
  public void outputFormatsFooterListsRegisteredRenderers(@TempDir final Path tempDir)
      throws Exception {
    final ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
    final URLClassLoader stubClassLoader = newStubRendererClassLoader(tempDir, originalClassLoader);
    Thread.currentThread().setContextClassLoader(stubClassLoader);
    try {
      final SchemaScribeCommandProvider provider = new SchemaScribeCommandProvider();
      final Object pluginCommand = provider.getCommandLineCommand();
      @SuppressWarnings("unchecked")
      final Supplier<String[]> helpFooterSupplier =
          (Supplier<String[]>)
              pluginCommand.getClass().getMethod("getHelpFooter").invoke(pluginCommand);
      final String footer = String.join(" ", helpFooterSupplier.get());
      assertThat(footer, not(containsString("stub")));
    } finally {
      Thread.currentThread().setContextClassLoader(originalClassLoader);
    }
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

  private URLClassLoader newStubRendererClassLoader(final Path tempDir, final ClassLoader parent)
      throws IOException {
    final Path servicesDir = tempDir.resolve("META-INF/services");
    Files.createDirectories(servicesDir);
    Files.writeString(
        servicesDir.resolve("schemacrawler.scribe.renderer.ScribeRenderer"),
        StubScribeRenderer.class.getName(),
        StandardCharsets.UTF_8,
        StandardOpenOption.CREATE,
        StandardOpenOption.TRUNCATE_EXISTING);
    final URL[] urls = {tempDir.toUri().toURL()};
    return new URLClassLoader(urls, parent);
  }

  /** Test-only renderer, registered dynamically via an isolated classloader per test. */
  public static final class StubScribeRenderer implements ScribeRenderer {

    @Override
    public String getSupportedOutputFormat() {
      return "stub";
    }

    @Override
    public void render(
        final ScribeSupport support,
        final SchemaScribeOptions options,
        final ScribeOutputContext output) {
      try (Writer writer = output.openWriter("index.txt")) {
        writer.write("stub");
      } catch (final IOException e) {
        throw new UncheckedIOException(e);
      }
    }
  }
}
