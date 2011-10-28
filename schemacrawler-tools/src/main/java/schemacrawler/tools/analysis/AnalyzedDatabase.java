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
package schemacrawler.tools.analysis;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.ColumnMap;
import schemacrawler.schema.Database;
import schemacrawler.schema.DatabaseInfo;
import schemacrawler.schema.JdbcDriverInfo;
import schemacrawler.schema.NamedObject;
import schemacrawler.schema.Schema;
import schemacrawler.schema.SchemaCrawlerInfo;
import schemacrawler.schema.Table;
import schemacrawler.tools.options.InfoLevel;

public final class AnalyzedDatabase
  implements Database
{

  private static final long serialVersionUID = -3953296149824921463L;

  public static final Lint[] getLint(final Table table)
  {
    if (table == null)
    {
      return null;
    }
    else
    {
      final Collection<Lint> lintList = table.getAttribute(Lint.LINT_KEY,
                                                           new HashSet<Lint>());
      final Lint[] lintArray = lintList.toArray(new BaseLint[lintList.size()]);
      Arrays.sort(lintArray);
      return lintArray;
    }
  }

  public static final ColumnMap[] getWeakAssociations(final Table table)
  {
    if (table == null)
    {
      return null;
    }
    else
    {
      return table.getAttribute(WeakAssociationsAnalyzer.WEAK_ASSOCIATIONS_KEY,
                                new ColumnMap[0]);
    }
  }

  private final Database database;

  public AnalyzedDatabase(final Database database, final InfoLevel infoLevel)
  {
    if (database == null)
    {
      throw new IllegalArgumentException("No database provided");
    }
    this.database = database;

    if (infoLevel.ordinal() >= InfoLevel.lint.ordinal())
    {
      final LintCollector lintCollector = new LintCollector();
      final ServiceLoader<Linter> lintLoaders = ServiceLoader
        .load(Linter.class);
      for (final Linter linter: lintLoaders)
      {
        linter.setLintCollector(lintCollector);
        linter.lint(database);
      }
    }

    if (infoLevel.ordinal() >= InfoLevel.maximum.ordinal())
    {
      final List<Table> allTables = new ArrayList<Table>();
      for (final Schema schema: database.getSchemas())
      {
        for (final Table table: schema.getTables())
        {
          allTables.add(table);
        }
      }
      final WeakAssociationsAnalyzer weakAssociationsAnalyzer = new WeakAssociationsAnalyzer(allTables);
      weakAssociationsAnalyzer.analyzeTables();
    }

  }

  @Override
  public int compareTo(final NamedObject o)
  {
    return database.compareTo(o);
  }

  @Override
  public Object getAttribute(final String name)
  {
    return database.getAttribute(name);
  }

  @Override
  public <T> T getAttribute(final String name, final T defaultValue)
  {
    return database.getAttribute(name, defaultValue);
  }

  @Override
  public Map<String, Object> getAttributes()
  {
    return database.getAttributes();
  }

  @Override
  public DatabaseInfo getDatabaseInfo()
  {
    return database.getDatabaseInfo();
  }

  @Override
  public String getFullName()
  {
    return database.getFullName();
  }

  @Override
  public JdbcDriverInfo getJdbcDriverInfo()
  {
    return database.getJdbcDriverInfo();
  }

  @Override
  public String getName()
  {
    return database.getName();
  }

  @Override
  public String getRemarks()
  {
    return database.getRemarks();
  }

  @Override
  public Schema getSchema(final String name)
  {
    return database.getSchema(name);
  }

  @Override
  public SchemaCrawlerInfo getSchemaCrawlerInfo()
  {
    return database.getSchemaCrawlerInfo();
  }

  @Override
  public Schema[] getSchemas()
  {
    return database.getSchemas();
  }

  @Override
  public ColumnDataType getSystemColumnDataType(final String name)
  {
    return database.getSystemColumnDataType(name);
  }

  @Override
  public ColumnDataType[] getSystemColumnDataTypes()
  {
    return database.getSystemColumnDataTypes();
  }

  @Override
  public void setAttribute(final String name, final Object value)
  {
    database.setAttribute(name, value);
  }

}
