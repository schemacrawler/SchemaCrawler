/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static schemacrawler.tools.lint.config.LinterConfigUtility.readLinterConfigs;
import static us.fatehi.test.utility.extensions.FileHasContent.classpathResource;
import static us.fatehi.test.utility.extensions.FileHasContent.hasSameContentAndTypeAs;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import schemacrawler.schemacrawler.exceptions.ConfigurationException;
import schemacrawler.tools.command.lint.options.LintOptions;
import schemacrawler.tools.command.lint.options.LintOptionsBuilder;
import schemacrawler.tools.lint.config.LinterConfig;
import schemacrawler.tools.lint.config.LinterConfigs;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;
import us.fatehi.test.utility.extensions.FileHasContent;

public class LinterConfigsTest {

  @Test
  @DisplayName("No linter config file")
  public void testParseBad0() {
    final LintOptions lintOptions = LintOptionsBuilder.builder().toOptions();

    final LinterConfigs linterConfigs = readLinterConfigs(lintOptions);

    assertThat(linterConfigs.size(), is(0));
  }

  @Test
  @DisplayName("Missing linter config file")
  public void testParseBad1() {
    final ConfigurationException exception =
        assertThrows(
            ConfigurationException.class,
            () -> {
              final LintOptions lintOptions =
                  LintOptionsBuilder.builder().withLinterConfigs("/missing.yaml").toOptions();

              /*final LinterConfigs linterConfigs = */ readLinterConfigs(lintOptions);
            });
    assertThat(
        exception.getMessage(), is("Could not load linter configs from file </missing.yaml>"));
  }

  @Test
  @DisplayName("Invalid linter config file")
  public void testParseBad2() {
    final ConfigurationException exception =
        assertThrows(
            ConfigurationException.class,
            () -> {
              final LintOptions lintOptions =
                  LintOptionsBuilder.builder()
                      .withLinterConfigs("/schemacrawler-linter-configs-bad-2.yaml.bad")
                      .toOptions();

              /*final LinterConfigs linterConfigs =*/ readLinterConfigs(lintOptions);
            });
    assertThat(exception.getCause().getCause().getMessage(), endsWith("line: 1, column: 1]"));
  }

  @Test
  @DisplayName("Valid but incorrect linter config file")
  public void testParseBad3() {
    final ConfigurationException exception =
        assertThrows(
            ConfigurationException.class,
            () -> {
              final LintOptions lintOptions =
                  LintOptionsBuilder.builder()
                      .withLinterConfigs("/schemacrawler-linter-configs-bad-3.yaml")
                      .toOptions();

              /*final LinterConfigs linterConfigs =*/ readLinterConfigs(lintOptions);
            });
    assertThat(
        exception.getCause().getCause().getMessage(),
        containsString(
            "no String-argument constructor/factory method to deserialize from String value"
                + " ('Apple')"));
  }

  @Test
  @DisplayName("\u263A Valid linter config file")
  public void testParseGood() throws Exception {

    final LintOptions lintOptions =
        LintOptionsBuilder.builder()
            .withLinterConfigs("/schemacrawler-linter-configs-1.yaml")
            .toOptions();

    final LinterConfigs linterConfigs = readLinterConfigs(lintOptions);
    final List<LinterConfig> linterConfigsList = new ArrayList<>();
    for (final LinterConfig linterConfig : linterConfigs) {
      linterConfigsList.add(linterConfig);
    }

    assertThat(
        outputOf(serialized(linterConfigsList)),
        hasSameContentAndTypeAs(classpathResource("schemacrawler-linter-configs-1.json"), "json"));
  }

  private Path serialized(final List<LinterConfig> linterConfigsList) throws Exception {

    @JsonPropertyOrder(
        value = {"linterId", "runLinter", "severity", "threshold", "config"},
        alphabetic = true)
    @JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        creatorVisibility = JsonAutoDetect.Visibility.NONE)
    class JacksonMixin {}

    final JsonMapper mapper =
        JsonMapper.builder()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .addMixIn(Object.class, JacksonMixin.class)
            .build();
    return FileHasContent.text(mapper.writeValueAsString(linterConfigsList));
  }
}
