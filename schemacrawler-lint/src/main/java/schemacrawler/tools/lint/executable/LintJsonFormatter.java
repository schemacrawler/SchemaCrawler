/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2012, Sualeh Fatehi.
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


import java.io.Writer;
import java.util.logging.Level;

import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.lint.Lint;
import schemacrawler.tools.lint.LintedDatabase;
import schemacrawler.tools.lint.SimpleLintCollector;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.text.base.BaseJsonFormatter;
import schemacrawler.tools.text.utility.org.json.JSONArray;
import schemacrawler.tools.text.utility.org.json.JSONException;
import schemacrawler.tools.text.utility.org.json.JSONObject;

final class LintJsonFormatter
  extends BaseJsonFormatter<LintOptions>
  implements LintFormatter
{

  LintJsonFormatter(final LintOptions options,
                    final OutputOptions outputOptions,
                    final Writer writer)
    throws SchemaCrawlerException
  {
    super(options, false, outputOptions, writer);
  }

  @Override
  public void handle(final LintedDatabase database)
    throws SchemaCrawlerException
  {
    final Lint<?>[] lints = SimpleLintCollector.getLint(database);
    if (lints != null && lints.length > 0)
    {
      final JSONObject jsonDatabase = new JSONObject();
      try
      {
        jsonRoot.accumulate("database_lints", jsonDatabase);

        final JSONArray jsonLints = handleLints(lints);
        jsonDatabase.put("lints", jsonLints);
      }
      catch (final JSONException e)
      {
        LOGGER.log(Level.FINER, "Error outputting Table: " + e.getMessage(), e);
      }
    }
  }

  /**
   * Provides information on the database schema.
   * 
   * @param table
   *        Table metadata.
   */
  @Override
  public void handle(final Table table)
  {
    final Lint<?>[] lints = SimpleLintCollector.getLint(table);
    if (lints != null && lints.length > 0)
    {
      final JSONObject jsonTable = new JSONObject();
      try
      {
        jsonRoot.accumulate("table_lints", jsonTable);

        jsonTable.put("name", table.getName());
        jsonTable.put("fullName", table.getFullName());
        jsonTable.put("type", table.getType());

        final JSONArray jsonLints = handleLints(lints);
        jsonTable.put("lints", jsonLints);
      }
      catch (final JSONException e)
      {
        LOGGER.log(Level.FINER, "Error outputting Table: " + e.getMessage(), e);
      }
    }
  }

  @Override
  public void handleEnd()
  {
  }

  @Override
  public void handleStart()
  {
  }

  private JSONArray handleLints(final Lint<?>[] lints)
  {
    final JSONArray jsonLints = new JSONArray();
    if (lints != null && lints.length > 0)
    {
      for (final Lint<?> lint: lints)
      {
        try
        {
          final JSONObject jsonLint = new JSONObject();
          jsonLints.put(jsonLint);
          jsonLint.put("id", lint.getId());
          jsonLint.put("severity", lint.getSeverity().name());
          jsonLint.put("description", lint.getMessage());
          jsonLint.put("value", lint.getValueAsString());
        }
        catch (final JSONException e)
        {
          LOGGER
            .log(Level.FINER, "Error outputting Lint: " + e.getMessage(), e);
        }
      }
    }
    return jsonLints;
  }

}
