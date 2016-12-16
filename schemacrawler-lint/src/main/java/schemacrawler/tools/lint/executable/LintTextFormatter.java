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
package schemacrawler.tools.lint.executable;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.lint.Lint;
import schemacrawler.tools.lint.LintCollector;
import schemacrawler.tools.lint.LintSeverity;
import schemacrawler.tools.lint.LintedCatalog;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.text.base.BaseTabularFormatter;
import schemacrawler.tools.text.utility.TextFormattingHelper.DocumentHeaderType;
import sf.util.Color;
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
    final Collection<Lint<?>> lints = LintCollector.getLint(catalog);
    if (lints != null && !lints.isEmpty())
    {
      formattingHelper.writeObjectStart();

      formattingHelper
        .writeObjectNameRow("", "Database", "[database]", Color.white);

      printLints(lints);
      formattingHelper.writeObjectEnd();
    }
  }

  @Override
  public void handle(final Table table)
  {
    final Collection<Lint<?>> lints = LintCollector.getLint(table);
    if (lints != null && !lints.isEmpty())
    {
      formattingHelper.writeObjectStart();

      formattingHelper.println();
      formattingHelper.println();

      final String tableType = "[" + table.getTableType() + "]";
      formattingHelper.writeObjectNameRow(nodeId(table),
                                          table.getFullName(),
                                          tableType,
                                          colorMap.getColor(table));
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
          formattingHelper.writeRow("",
                                    lint.getMessage(),
                                    lint.getValueAsString());
        }
      }
    }
  }
}
