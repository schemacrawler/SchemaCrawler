/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package schemacrawler.test;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;

import java.nio.file.Path;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;

import schemacrawler.loader.attributes.model.CatalogAttributes;
import schemacrawler.loader.attributes.model.CatalogAttributesUtility;
import schemacrawler.schemacrawler.exceptions.ConfigurationException;
import schemacrawler.test.utility.FileHasContent;
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
        hasSameContentAs(classpathResource("attributes.json")));
  }

  private Path serialized(final CatalogAttributes catalogAttributes) throws Exception {

    @JsonPropertyOrder(
        value = {"catalogName", "schemaName", "name", "remarks", "attributes"},
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
    return FileHasContent.text(jsonMapper.writeValueAsString(catalogAttributes));
  }
}
