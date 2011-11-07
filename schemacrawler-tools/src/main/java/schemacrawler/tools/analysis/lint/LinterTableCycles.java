/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2011, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */
package schemacrawler.tools.analysis.lint;


import schemacrawler.schema.Database;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.ForeignKeyColumnMap;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import sf.util.DirectedGraph;

public class LinterTableCycles
  extends BaseLinter
{

  @Override
  public String getDescription()
  {
    return getSummary();
  }

  @Override
  public String getSummary()
  {
    return "cycles in table relationships";
  }

  @Override
  public void lint(final Database database)
  {
    final DirectedGraph<Table> tablesGraph = new DirectedGraph<Table>();
    for (final Schema schema: database.getSchemas())
    {
      for (final Table table: schema.getTables())
      {
        tablesGraph.addVertex(table);
        final ForeignKey[] foreignKeys = table.getForeignKeys();
        for (final ForeignKey foreignKey: foreignKeys)
        {
          final ForeignKeyColumnMap[] columnPairs = foreignKey.getColumnPairs();
          for (final ForeignKeyColumnMap columnPair: columnPairs)
          {
            tablesGraph.addDirectedEdge(columnPair.getPrimaryKeyColumn()
              .getParent(), columnPair.getForeignKeyColumn().getParent());
          }
        }
      }
    }
    if (tablesGraph.containsCycle())
    {
      addLint(database, getSummary(), Boolean.TRUE);
    }
  }

  @Override
  protected void lint(final Table table)
  {
    // No-op
  }

}
