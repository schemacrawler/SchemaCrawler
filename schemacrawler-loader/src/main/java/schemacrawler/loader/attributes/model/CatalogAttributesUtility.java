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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import schemacrawler.schemacrawler.exceptions.ConfigurationException;
import us.fatehi.utility.UtilityMarker;
import us.fatehi.utility.ioresource.InputResource;

@UtilityMarker
public class CatalogAttributesUtility {

  /**
   * Pass in a reader at this point, since
   *
   * @param catalogAttributesFile
   */
  public static CatalogAttributes readCatalogAttributes(final InputResource inputResource) {
    requireNonNull(inputResource, "No input resource provided");
    try (final Reader reader = inputResource.openNewInputReader(UTF_8)) {

      final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

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
