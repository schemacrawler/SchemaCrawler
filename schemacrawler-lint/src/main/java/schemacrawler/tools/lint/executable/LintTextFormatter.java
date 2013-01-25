/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2013, Sualeh Fatehi.
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

package schemacrawler.tools.lint.executable;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.lint.Lint;
import schemacrawler.tools.lint.LintSeverity;
import schemacrawler.tools.lint.LintedDatabase;
import schemacrawler.tools.lint.SimpleLintCollector;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.text.base.BaseTabularFormatter;
import schemacrawler.tools.text.utility.TextFormattingHelper.DocumentHeaderType;
import sf.util.Multimap;

final class LintTextFormatter
  extends BaseTabularFormatter<LintOptions>
  implements LintFormatter
{

  LintTextFormatter(final LintOptions options, final OutputOptions outputOptions)
    throws SchemaCrawlerException
  {
    super(options, false, outputOptions);
  }

  @Override
  public void handle(final LintedDatabase database)
    throws SchemaCrawlerException
  {
    final Collection<Lint<?>> lints = SimpleLintCollector.getLint(database);
    if (lints != null && !lints.isEmpty())
    {
      out.print(formattingHelper.createObjectStart(""));

      final String nameRow = formattingHelper.createNameRow("",
                                                            "[database]",
                                                            true);
      out.println(nameRow);

      printLints(lints);
      out.println(formattingHelper.createObjectEnd());

      out.flush();
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.traversal.SchemaTraversalHandler#handle(schemacrawler.schema.Table)
   */
  @Override
  public void handle(final Table table)
  {
    final Collection<Lint<?>> lints = SimpleLintCollector.getLint(table);
    if (lints != null && !lints.isEmpty())
    {
      out.print(formattingHelper.createObjectStart(""));
      printTableName(table);
      printLints(lints);
      out.println(formattingHelper.createObjectEnd());

      out.flush();
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.traversal.SchemaTraversalHandler#handleTablesEnd()
   */
  @Override
  public void handleEnd()
  {
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.traversal.SchemaTraversalHandler#handleTablesStart()
   */
  @Override
  public void handleStart()
  {
    out.println(formattingHelper.createHeader(DocumentHeaderType.subTitle,
                                              "Lints"));
  }

  private void printLints(final Collection<Lint<?>> lints)
  {
    out.println(formattingHelper.createEmptyRow());

    final Multimap<LintSeverity, Lint<?>> multiMap = new Multimap<LintSeverity, Lint<?>>();
    for (final Lint<?> lint: lints)
    {
      multiMap.add(lint.getSeverity(), lint);
    }
    final List<LintSeverity> severities = Arrays.asList(LintSeverity.values());
    Collections.reverse(severities);
    for (final LintSeverity severity: severities)
    {
      if (!multiMap.containsKey(severity))
      {
        continue;
      }

      out.println(formattingHelper.createNameRow("", String
        .format("[lint, %s]", severity), false));
      final List<Lint<?>> lintsById = new ArrayList<Lint<?>>(multiMap.get(severity));
      for (final Lint<?> lint: lintsById)
      {
        final Object lintValue = lint.getValue();
        if (lintValue instanceof Boolean)
        {
          if ((Boolean) lintValue)
          {
            out.println(formattingHelper.createRow("", lint.getMessage(), ""));
          }
        }
        else
        {
          out.println(formattingHelper.createRow("",
                                                 lint.getMessage(),
                                                 lint.getValueAsString()));
        }
      }
    }
  }

  private void printTableName(final Table table)
  {
    final String nameRow = formattingHelper.createNameRow(table.getFullName(),
                                                          "[" + table.getTableType()
                                                              + "]",
                                                          true);
    out.println(nameRow);
  }

}
