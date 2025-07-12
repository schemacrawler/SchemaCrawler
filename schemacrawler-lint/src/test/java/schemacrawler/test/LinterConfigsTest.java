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
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.tools.lint.config.LinterConfigUtility.readLinterConfigs;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;

import schemacrawler.schemacrawler.exceptions.ConfigurationException;
import schemacrawler.test.utility.FileHasContent;
import schemacrawler.tools.command.lint.options.LintOptions;
import schemacrawler.tools.command.lint.options.LintOptionsBuilder;
import schemacrawler.tools.lint.config.LinterConfig;
import schemacrawler.tools.lint.config.LinterConfigs;

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
            "no String-argument constructor/factory method to deserialize from String value ('Apple')"));
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
        hasSameContentAs(classpathResource("schemacrawler-linter-configs-1.json")));
  }

  private Path serialized(final List<LinterConfig> linterConfigsList) throws Exception {

    @JsonPropertyOrder(
        value = {"linterId", "runLinter", "severity", "threshold", "config"},
        alphabetic = true)
    class JacksonMixin {}

    final JsonMapper jsonMapper = new JsonMapper();
    jsonMapper.enable(SerializationFeature.INDENT_OUTPUT);
    jsonMapper.addMixIn(Object.class, JacksonMixin.class);
    jsonMapper.setVisibility(
        jsonMapper
            .getSerializationConfig()
            .getDefaultVisibilityChecker()
            .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
            .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
            .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
            .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
    return FileHasContent.text(jsonMapper.writeValueAsString(linterConfigsList));
  }
}
