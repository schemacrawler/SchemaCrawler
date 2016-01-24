/*
 * SchemaCrawler
 * Copyright (c) 2000-2016, Sualeh Fatehi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package schemacrawler.test;


import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.stream.Collectors;

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

    final Collection<Table> filteredTables = catalog.getTables().stream()
      .filter(tableFilterBuilder.build()).collect(Collectors.toSet());
    return filteredTables;
  }

}
