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


import java.util.Collection;
import java.util.logging.Level;

import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.lint.Lint;
import schemacrawler.tools.lint.LintCollector;
import schemacrawler.tools.lint.LintedCatalog;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.text.base.BaseJsonFormatter;
import schemacrawler.tools.text.utility.org.json.JSONArray;
import schemacrawler.tools.text.utility.org.json.JSONException;
import schemacrawler.tools.text.utility.org.json.JSONObject;
import sf.util.StringFormat;

final class LintJsonFormatter
  extends BaseJsonFormatter<LintOptions>
  implements LintTraversalHandler
{

  LintJsonFormatter(final LintOptions options,
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
      final JSONObject jsonDatabase = new JSONObject();
      try
      {
        jsonRoot.accumulate("database_lints", jsonDatabase);

        final JSONArray jsonLints = handleLints(lints);
        jsonDatabase.put("lints", jsonLints);
      }
      catch (final JSONException e)
      {
        LOGGER
          .log(Level.FINER,
               e,
               new StringFormat("Error outputting table: %s", e.getMessage()));
      }
    }
  }

  @Override
  public void handle(final Table table)
  {
    final Collection<Lint<?>> lints = LintCollector.getLint(table);
    if (lints != null && !lints.isEmpty())
    {
      final JSONObject jsonTable = new JSONObject();
      try
      {
        jsonRoot.accumulate("table_lints", jsonTable);

        jsonTable.put("name", table.getName());
        jsonTable.put("fullName", table.getFullName());
        jsonTable.put("type", table.getTableType());

        final JSONArray jsonLints = handleLints(lints);
        jsonTable.put("lints", jsonLints);
      }
      catch (final JSONException e)
      {
        LOGGER
          .log(Level.FINER,
               e,
               new StringFormat("Error outputting table: %s", e.getMessage()));
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

  private JSONArray handleLints(final Collection<Lint<?>> lints)
  {
    final JSONArray jsonLints = new JSONArray();
    if (lints != null && !lints.isEmpty())
    {
      for (final Lint<?> lint: lints)
      {
        try
        {
          final JSONObject jsonLint = new JSONObject();
          jsonLints.put(jsonLint);
          jsonLint.put("id", lint.getLinterId());
          jsonLint.put("severity", lint.getSeverity().name());
          jsonLint.put("description", lint.getMessage());
          jsonLint.put("value", lint.getValueAsString());
        }
        catch (final JSONException e)
        {
          LOGGER
            .log(Level.FINER,
                 e,
                 new StringFormat("Error outputting lint: %s", e.getMessage()));
        }
      }
    }
    return jsonLints;
  }

}
