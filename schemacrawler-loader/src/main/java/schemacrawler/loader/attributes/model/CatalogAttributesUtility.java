/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.loader.attributes.model;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;

import java.io.Reader;
import schemacrawler.schemacrawler.exceptions.ConfigurationException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.dataformat.yaml.YAMLMapper;
import us.fatehi.utility.UtilityMarker;
import us.fatehi.utility.ioresource.InputResource;

@UtilityMarker
public class CatalogAttributesUtility {

  private static final ObjectMapper mapper = new YAMLMapper();

  /**
   * Pass in a reader at this point, since
   *
   * @param catalogAttributesFile
   */
  public static CatalogAttributes readCatalogAttributes(final InputResource inputResource) {
    requireNonNull(inputResource, "No input resource provided");
    try (final Reader reader = inputResource.openNewInputReader(UTF_8)) {
      final CatalogAttributes catalogAttributes = mapper.readValue(reader, CatalogAttributes.class);
      return catalogAttributes;
    } catch (final Exception e) {
      throw new ConfigurationException("Cannot read catalog attributes", e);
    }
  }

  private CatalogAttributesUtility() {
    // Prevent instantiation
  }
}
