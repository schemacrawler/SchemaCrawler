/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
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


import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.HashSet;

import org.junit.Test;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.InclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.test.utility.BaseDatabaseTest;
import schemacrawler.utility.DatabaseObjectFullNameFilter;
import schemacrawler.utility.DatabaseObjectFullNameFilter.Builder;

public class DatabaseObjectFullNameFilterTest
  extends BaseDatabaseTest
{

  private final class TableNameFilter
    implements InclusionRule
  {
    private static final long serialVersionUID = 1L;

    private final String tableName;

    public TableNameFilter(final String tableName)
    {
      this.tableName = tableName;
    }

    @Override
    public boolean test(final String t)
    {
      return t != null && t.endsWith(tableName);
    }
  }

  @Test
  public void tableFilter1()
    throws Exception
  {
    final Builder<Table> databaseObjectFullNameFilterBuilder = DatabaseObjectFullNameFilter
      .databaseObjectFullNameFilter();
    databaseObjectFullNameFilterBuilder.withConnection(getConnection());

    final Collection<Table> filteredTables = tableFilter(databaseObjectFullNameFilterBuilder,
                                                         "Global Counts");
    assertEquals(2, filteredTables.size());
  }

  @Test
  public void tableFilter2()
    throws Exception
  {
    final Builder<Table> databaseObjectFullNameFilterBuilder = DatabaseObjectFullNameFilter
      .databaseObjectFullNameFilter();
    databaseObjectFullNameFilterBuilder.withIdentifierQuoteString("")
      .withConnection(getConnection());

    final Collection<Table> filteredTables = tableFilter(databaseObjectFullNameFilterBuilder,
                                                         "Global Counts");
    assertEquals(0, filteredTables.size());
  }

  private Collection<Table> tableFilter(final Builder<Table> tableFilterBuilder,
                                        final String tableName)
    throws Exception
  {
    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevelBuilder.maximum());
    final Catalog catalog = getCatalog(schemaCrawlerOptions);

    tableFilterBuilder.withInclusionRule(new TableNameFilter(tableName));

    final DatabaseObjectFullNameFilter<Table> filter = tableFilterBuilder
      .build();
    final Collection<Table> filteredTables = new HashSet<>();
    for (final Table table: catalog.getTables())
    {
      if (filter.test(table))
      {
        filteredTables.add(table);
      }
    }
    return filteredTables;
  }

}
