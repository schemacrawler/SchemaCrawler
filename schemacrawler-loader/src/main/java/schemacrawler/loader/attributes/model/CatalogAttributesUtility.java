/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
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
