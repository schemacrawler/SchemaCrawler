/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.analysis.associations;


import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;

import schemacrawler.crawl.Retriever;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Table;

public final class WeakAssociationsRetriever
  implements Retriever
{

  private final Catalog catalog;

  public WeakAssociationsRetriever(final Catalog catalog)
  {
    this.catalog = requireNonNull(catalog, "No catalog provided");
  }

  public void retrieveWeakAssociations()
  {
    final List<Table> allTables = new ArrayList<>(catalog.getTables());
    final WeakAssociationsAnalyzer weakAssociationsAnalyzer = new WeakAssociationsAnalyzer(allTables);
    weakAssociationsAnalyzer.analyzeTables();
  }

}
