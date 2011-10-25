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
import java.util.List;

import schemacrawler.schema.Database;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;

abstract class BaseLinter
  implements Linter
{

  public void lint(final Database database)
  {
    for (final Schema schema: database.getSchemas())
    {
      for (final Table table: schema.getTables())
      {
        final Lint lint = lint(table);
        addLint(table, lint);
      }
    }
  }

  private final void addLint(final Table table, final Lint lint)
  {
    if (lint != null)
    {
      final List<Lint> lints = table.getAttribute(Lint.LINT_KEY,
                                                  new ArrayList<Lint>());
      lints.add(lint);
      table.setAttribute(Lint.LINT_KEY, lints);
    }
  }

  protected abstract Lint lint(Table table);

}
