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
package schemacrawler.crawl;


import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

import schemacrawler.analysis.associations.ProposedWeakAssociation;
import schemacrawler.analysis.associations.WeakAssociationsAnalyzer;
import schemacrawler.schema.Column;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.Retriever;
import schemacrawler.utility.MetaDataUtility;
import schemacrawler.SchemaCrawlerLogger;
import sf.util.string.StringFormat;

@Retriever
final class WeakAssociationsRetriever
{

  private static final SchemaCrawlerLogger LOGGER =
    SchemaCrawlerLogger.getLogger(WeakAssociationsRetriever.class.getName());

  private final MutableCatalog catalog;

  public WeakAssociationsRetriever(final MutableCatalog catalog)
  {
    this.catalog = requireNonNull(catalog, "No catalog provided");
  }

  public void retrieveWeakAssociations()
  {
    final List<Table> allTables = new ArrayList<>(catalog.getTables());
    final WeakAssociationsAnalyzer weakAssociationsAnalyzer =
      new WeakAssociationsAnalyzer(allTables);
    final Collection<ProposedWeakAssociation> proposedWeakAssociations =
      weakAssociationsAnalyzer.analyzeTables();

    for (final ProposedWeakAssociation proposedWeakAssociation : proposedWeakAssociations)
    {
      createWeakAssociation(proposedWeakAssociation);
    }
  }

  private void createWeakAssociation(final ProposedWeakAssociation proposedWeakAssociation)
  {
    LOGGER.log(Level.INFO,
               new StringFormat("Adding weak association <%s> ",
                                proposedWeakAssociation));

    final Column pkColumn = proposedWeakAssociation.getKey();
    final Column fkColumn = proposedWeakAssociation.getValue();
    final boolean isPkColumnPartial = pkColumn instanceof ColumnPartial;
    final boolean isFkColumnPartial = fkColumn instanceof ColumnPartial;

    if (pkColumn == null
        || fkColumn == null
        || isFkColumnPartial && isPkColumnPartial)
    {
      return;
    }

    final String foreignKeyName =
      MetaDataUtility.constructForeignKeyName(pkColumn, fkColumn);

    final WeakAssociation weakAssociation = new WeakAssociation(foreignKeyName);
    weakAssociation.addColumnReference(pkColumn, fkColumn);

    if (fkColumn instanceof MutableColumn)
    {
      ((MutableTable) fkColumn.getParent()).addWeakAssociation(weakAssociation);
    }

    if (pkColumn instanceof MutableColumn)
    {
      ((MutableTable) pkColumn.getParent()).addWeakAssociation(weakAssociation);
    }

  }

}
