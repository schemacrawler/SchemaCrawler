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

import static java.util.Objects.requireNonNull;

import java.sql.Connection;

import schemacrawler.crawl.SchemaCrawler;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.SchemaCrawlerException;

public class SchemaCrawlerCatalogLoader extends BaseCatalogLoader {

  public SchemaCrawlerCatalogLoader() {
    super(0);
  }

  @Override
  public String getDescription() {
    return "Loader for SchemaCrawler metadata catalog";
  }

  @Override
  public String getName() {
    return "schemacrawlerloader";
  }

  @Override
  public void loadCatalog() throws SchemaCrawlerException {
    if (isLoaded()) {
      return;
    }

    final Connection connection = getConnection();
    requireNonNull(connection, "No connection provided");

    final SchemaCrawler schemaCrawler =
        new SchemaCrawler(connection, getSchemaRetrievalOptions(), getSchemaCrawlerOptions());
    final Catalog catalog = schemaCrawler.crawl();
    setCatalog(catalog);
  }
}
