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


import schemacrawler.schema.Column;
import schemacrawler.schema.Database;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;

abstract class BaseLinter
  implements Linter
{

  private LintCollector lintCollector;

  private LintSeverity severity = LintSeverity.medium;

  @Override
  public String getId()
  {
    return getClass().getName();
  }

  @Override
  public LintCollector getLintCollector()
  {
    return lintCollector;
  }

  @Override
  public final LintSeverity getLintSeverity()
  {
    return severity;
  }

  @Override
  public void lint(final Database database)
  {
    for (final Schema schema: database.getSchemas())
    {
      for (final Table table: schema.getTables())
      {
        lint(table);
      }
    }
  }

  @Override
  public void setLintCollector(final LintCollector lintCollector)
  {
    this.lintCollector = lintCollector;
  }

  @Override
  public final void setLintSeverity(final LintSeverity severity)
  {
    if (severity != null)
    {
      this.severity = severity;
    }
  }

  private Lint newLint(final String objectName,
                       final String message,
                       final Object value)
  {
    return new BaseLint(getId(), objectName, getLintSeverity(), message, value)
    {

      private static final long serialVersionUID = 3158466712611884766L;

      @Override
      public String getValueAsString()
      {
        return convertLintValueToString(getValue());
      }
    };
  }

  protected void addLint(final Column column,
                         final String message,
                         final Object value)
  {
    if (lintCollector != null)
    {
      Lint lint = newLint(column.getFullName(), message, value);
      lintCollector.addLint(column, lint);
    }
  }

  protected void addLint(final Database database,
                         final String message,
                         final Object value)
  {
    if (lintCollector != null)
    {
      Lint lint = newLint((String) null, message, value);
      lintCollector.addLint(database, lint);
    }
  }

  protected void addLint(final Table table,
                         final String message,
                         final Object value)
  {
    if (lintCollector != null)
    {
      Lint lint = newLint(table.getFullName(), message, value);
      lintCollector.addLint(table, lint);
    }
  }

  protected abstract String convertLintValueToString(Object value);

  protected abstract void lint(Table table);

}
