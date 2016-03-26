/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi.
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
package schemacrawler.integration.test;


import java.sql.Connection;
import java.util.Collection;

import javax.sql.DataSource;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.Routine;
import schemacrawler.schemacrawler.DatabaseConnectionOptions;
import schemacrawler.schemacrawler.RegularExpressionInclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.utility.SchemaCrawlerUtility;

public class TestSqlServerCatalog
{

  public static void main(final String[] args)
    throws Exception
  {
    final DataSource dataSource = new DatabaseConnectionOptions("jdbc:jtds:sqlserver://scsqlserver.cdf972bn8znp.us-east-1.rds.amazonaws.com:1433/SCHEMACRAWLER");
    final Connection connection = dataSource.getConnection("schemacrawler",
                                                           "schemacrawler");

    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions
      .setSchemaInclusionRule(new RegularExpressionInclusionRule("SCHEMACRAWLER.dbo"));
    schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevelBuilder.maximum());

    final Catalog catalog = SchemaCrawlerUtility
      .getCatalog(connection, schemaCrawlerOptions);

    final Collection<Routine> routines = catalog.getRoutines();
    System.out.println(routines);
  }

}
