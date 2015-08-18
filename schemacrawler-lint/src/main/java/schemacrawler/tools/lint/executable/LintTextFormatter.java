/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2015, Sualeh Fatehi.
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


import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.lint.Lint;
import schemacrawler.tools.lint.LintSeverity;
import schemacrawler.tools.lint.LintedCatalog;
import schemacrawler.tools.lint.SimpleLintCollector;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.text.base.BaseTabularFormatter;
import schemacrawler.tools.text.utility.TextFormattingHelper.DocumentHeaderType;
import schemacrawler.utility.NamedObjectSort;
import sf.util.Multimap;

final class LintTextFormatter
  extends BaseTabularFormatter<LintOptions>
  implements LintTraversalHandler
{

  LintTextFormatter(final LintOptions options,
                    final OutputOptions outputOptions)
                      throws SchemaCrawlerException
  {
    super(options, false, outputOptions);
  }

  @Override
  public void handle(final LintedCatalog catalog)
    throws SchemaCrawlerException
  {
    final Collection<Lint<?>> lints = SimpleLintCollector.getLint(catalog);
    if (lints != null && !lints.isEmpty())
    {
      formattingHelper.writeObjectStart();

      formattingHelper.writeObjectNameRow("", "Database", "[database]",
                                          Color.white);

      printLints(lints);
      formattingHelper.writeObjectEnd();
    }
  }

  @Override
  public void handle(final Collection<? extends Table> tables)
    throws SchemaCrawlerException
  {
    if (tables == null || tables.isEmpty())
    {
      return;
    }
    final List<? extends Table> tablesList = new ArrayList<>(tables);
    Collections.sort(tablesList, NamedObjectSort
      .getNamedObjectSort(options.isAlphabeticalSortForTables()));
    for (Table table: tablesList)
    {
      handle(table);
    }
  }

  private void handle(final Table table)
  {
    final Collection<Lint<?>> lints = SimpleLintCollector.getLint(table);
    if (lints != null && !lints.isEmpty())
    {
      formattingHelper.writeObjectStart();

      formattingHelper.println();
      formattingHelper.println();

      final String tableType = "[" + table.getTableType() + "]";
      formattingHelper.writeObjectNameRow(nodeId(table), table.getFullName(),
                                          tableType, colorMap.getColor(table));
      printLints(lints);
      formattingHelper.writeObjectEnd();
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
    formattingHelper.writeHeader(DocumentHeaderType.subTitle, "Lints");
  }

  private void printLints(final Collection<Lint<?>> lints)
  {
    formattingHelper.writeEmptyRow();

    final Multimap<LintSeverity, Lint<?>> multiMap = new Multimap<>();
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

      formattingHelper.writeNameRow("", String.format("[lint, %s]", severity));
      final List<Lint<?>> lintsById = new ArrayList<>(multiMap.get(severity));
      for (final Lint<?> lint: lintsById)
      {
        final Object lintValue = lint.getValue();
        if (lintValue instanceof Boolean)
        {
          if ((Boolean) lintValue)
          {
            formattingHelper.writeRow("", lint.getMessage(), "");
          }
        }
        else
        {
          formattingHelper.writeRow("", lint.getMessage(),
                                    lint.getValueAsString());
        }
      }
    }
  }
}
