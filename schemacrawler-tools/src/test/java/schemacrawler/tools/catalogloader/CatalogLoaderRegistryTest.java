/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2021, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.tools.catalogloader;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import org.junit.jupiter.api.Test;

import schemacrawler.schemacrawler.SchemaCrawlerException;

public class CatalogLoaderRegistryTest {

  @Test
  public void catalogLoaderRegistry() throws SchemaCrawlerException {
    final CatalogLoaderRegistry registry = new CatalogLoaderRegistry();

    // Look up test catalog loader
    assertThat(registry.hasDatabaseSystemIdentifier("test-db"), is(true));
    assertThat(registry.lookupCatalogLoader("test-db"), is(not(nullValue())));
    assertThat(
        registry.lookupCatalogLoader("test-db").getClass().getSimpleName(),
        is("TestCatalogLoader"));
    assertThat(
        registry.lookupCatalogLoader("test-db").getDatabaseSystemIdentifier(), is("test-db"));

    // Look up unknown catalog loader
    assertThat(registry.hasDatabaseSystemIdentifier("some-db"), is(false));
    assertThat(registry.lookupCatalogLoader("some-db"), is(not(nullValue())));
    assertThat(
        registry.lookupCatalogLoader("some-db").getClass().getSimpleName(),
        is("SchemaCrawlerCatalogLoader"));
    assertThat(
        registry.lookupCatalogLoader("some-db").getDatabaseSystemIdentifier(), is(nullValue()));
  }
}
