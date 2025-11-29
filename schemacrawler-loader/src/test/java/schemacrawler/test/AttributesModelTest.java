/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static us.fatehi.test.utility.extensions.FileHasContent.classpathResource;
import static us.fatehi.test.utility.extensions.FileHasContent.hasSameContentAndTypeAs;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import schemacrawler.loader.attributes.model.CatalogAttributes;
import schemacrawler.loader.attributes.model.CatalogAttributesUtility;
import schemacrawler.schemacrawler.exceptions.ConfigurationException;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;
import us.fatehi.test.utility.extensions.FileHasContent;
import us.fatehi.utility.ioresource.InputResource;
import us.fatehi.utility.ioresource.InputResourceUtility;

public class AttributesModelTest {

  @Test
  @DisplayName("Invalid attributes file format")
  public void testParseBad2() {
    final ConfigurationException exception =
        assertThrows(
            ConfigurationException.class,
            () -> {
              final InputResource inputResource =
                  InputResourceUtility.createInputResource("/attributes-bad-2.yaml.bad").get();
              /*final CatalogAttributes catalogAttributes =*/ CatalogAttributesUtility
                  .readCatalogAttributes(inputResource);
            });
    assertThat(exception.getCause().getMessage(), endsWith("line: 1, column: 2]"));
  }

  @Test
  @DisplayName("Valid attributes file format, but incorrect data")
  public void testParseBad3() {
    final ConfigurationException exception =
        assertThrows(
            ConfigurationException.class,
            () -> {
              final InputResource inputResource =
                  InputResourceUtility.createInputResource("/attributes-bad-3.yaml").get();
              /*final CatalogAttributes catalogAttributes =*/ CatalogAttributesUtility
                  .readCatalogAttributes(inputResource);
            });
    assertThat(exception.getCause().getMessage(), endsWith("line: 1, column: 1]"));
  }

  @Test
  @DisplayName("\u263A Valid attributes file")
  public void testParseGood() throws Exception {

    final InputResource inputResource =
        InputResourceUtility.createInputResource("/attributes.yaml").get();
    final CatalogAttributes catalogAttributes =
        CatalogAttributesUtility.readCatalogAttributes(inputResource);

    assertThat(
        outputOf(serialized(catalogAttributes)),
        hasSameContentAndTypeAs(classpathResource("attributes.json"), "json"));
  }

  private Path serialized(final CatalogAttributes catalogAttributes) throws Exception {

    @JsonPropertyOrder(
        value = {"catalogName", "schemaName", "name", "remarks", "attributes"},
        alphabetic = true)
    @JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        creatorVisibility = JsonAutoDetect.Visibility.NONE)
    class JacksonMixin {}

    final JsonMapper jsonMapper =
        JsonMapper.builder()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .addMixIn(Object.class, JacksonMixin.class)
            .build();

    return FileHasContent.text(jsonMapper.writeValueAsString(catalogAttributes));
  }
}
