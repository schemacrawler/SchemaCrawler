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

package schemacrawler.tools.lint.executable;


import java.util.ArrayList;
import java.util.List;

import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.Procedure;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.lint.Lint;
import schemacrawler.tools.lint.SimpleLintCollector;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.text.base.BaseTabularFormatter;
import schemacrawler.tools.text.utility.TextFormattingHelper.DocumentHeaderType;
import schemacrawler.tools.traversal.SchemaTraversalHandler;

/**
 * Text formatting of schema.
 * 
 * @author Sualeh Fatehi
 */
final class LintTextFormatter
  extends BaseTabularFormatter<LintOptions>
  implements SchemaTraversalHandler
{

  /**
   * Text formatting of schema.
   * 
   * @param schemaTextDetailType
   *        Types for text formatting of schema
   * @param options
   *        Options for text formatting of schema
   * @param outputOptions
   *        Options for text formatting of schema
   * @throws SchemaCrawlerException
   *         On an exception
   */
  LintTextFormatter(final LintOptions options, final OutputOptions outputOptions)
    throws SchemaCrawlerException
  {
    super(options, false, outputOptions);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.traversal.SchemaTraversalHandler#handle(schemacrawler.schema.ColumnDataType)
   */
  @Override
  public void handle(final ColumnDataType columnDataType)
    throws SchemaCrawlerException
  {

  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.traversal.SchemaTraversalHandler#handle(schemacrawler.schema.Procedure)
   */
  @Override
  public void handle(final Procedure procedure)
  {

  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.traversal.SchemaTraversalHandler#handle(schemacrawler.schema.Table)
   */
  @Override
  public void handle(final Table table)
  {
    final Lint<?>[] lints = SimpleLintCollector.getLint(table);
    if (lints != null && lints.length > 0)
    {
      out.print(formattingHelper.createObjectStart(""));
      printTableName(table);
      printTableLints(lints);
      out.println(formattingHelper.createObjectEnd());

      out.flush();
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.traversal.SchemaTraversalHandler#handleColumnDataTypesEnd()
   */
  @Override
  public void handleColumnDataTypesEnd()
  {
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.traversal.SchemaTraversalHandler#handleColumnDataTypesStart()
   */
  @Override
  public void handleColumnDataTypesStart()
  {
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.traversal.SchemaTraversalHandler#handleProceduresEnd()
   */
  @Override
  public void handleProceduresEnd()
    throws SchemaCrawlerException
  {
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.traversal.SchemaTraversalHandler#handleProceduresStart()
   */
  @Override
  public void handleProceduresStart()
    throws SchemaCrawlerException
  {
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.traversal.SchemaTraversalHandler#handleTablesEnd()
   */
  @Override
  public void handleTablesEnd()
    throws SchemaCrawlerException
  {
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.traversal.SchemaTraversalHandler#handleTablesStart()
   */
  @Override
  public void handleTablesStart()
    throws SchemaCrawlerException
  {
    out.println(formattingHelper.createHeader(DocumentHeaderType.subTitle,
                                              "Tables"));
  }

  private void printTableLints(final Lint<?>[] lints)
  {
    out.println(formattingHelper.createEmptyRow());

    final Multimap<String, Lint> multiMap = new Multimap<String, Lint>();
    for (final Lint<?> lint: lints)
    {
      multiMap.add(lint.getId(), lint);
    }
    for (final String lintId: multiMap.keySet())
    {
      out.println(formattingHelper.createNameRow(lintId, String
        .format("[lint, %s]", "**medium"), false));
      final List<Lint> lintsById = new ArrayList<Lint>(multiMap.get(lintId));
      for (final Lint<?> lint: lintsById)
      {
        final Object lintValue = lint.getValue();
        if (lintValue instanceof Boolean)
        {
          if ((Boolean) lintValue)
          {
            out
              .println(formattingHelper.createDescriptionRow(lint.getMessage()));
          }
        }
        else
        {
          out
            .println(formattingHelper.createNameValueRow(lint.getMessage(),
                                                         lint
                                                           .getValueAsString()));
        }
      }
    }
  }

  private void printTableName(final Table table)
  {
    final String nameRow = formattingHelper.createNameRow(table.getFullName(),
                                                          "[" + table.getType()
                                                              + "]",
                                                          true);
    out.println(nameRow);
  }

}
