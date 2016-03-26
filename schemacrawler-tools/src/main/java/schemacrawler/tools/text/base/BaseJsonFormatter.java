/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi.
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

package schemacrawler.tools.text.base;


import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.CrawlInfo;
import schemacrawler.schema.DatabaseInfo;
import schemacrawler.schema.DatabaseProperty;
import schemacrawler.schema.JdbcDriverInfo;
import schemacrawler.schema.JdbcDriverProperty;
import schemacrawler.schema.SchemaCrawlerInfo;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.text.utility.JsonFormattingHelper;
import schemacrawler.tools.text.utility.org.json.JSONArray;
import schemacrawler.tools.text.utility.org.json.JSONException;
import schemacrawler.tools.text.utility.org.json.JSONObject;
import sf.util.StringFormat;

/**
 * Text formatting of schema.
 *
 * @author Sualeh Fatehi
 */
public abstract class BaseJsonFormatter<O extends BaseTextOptions>
  extends BaseFormatter<O>
{

  protected static final Logger LOGGER = Logger
    .getLogger(BaseJsonFormatter.class.getName());

  protected final JSONObject jsonRoot;

  protected BaseJsonFormatter(final O options,
                              final boolean printVerboseDatabaseInfo,
                              final OutputOptions outputOptions)
                                throws SchemaCrawlerException
  {
    super(options, printVerboseDatabaseInfo, outputOptions);
    jsonRoot = new JSONObject();
  }

  @Override
  public void begin()
    throws SchemaCrawlerException
  {
    if (!options.isNoHeader())
    {
      formattingHelper.append("[").println();
    }
  }

  @Override
  public void end()
    throws SchemaCrawlerException
  {
    ((JsonFormattingHelper) formattingHelper).write(jsonRoot);

    if (options.isNoFooter())
    {
      formattingHelper.append(",").println();
    }
    else
    {
      formattingHelper.append("]").println();
    }

    super.end();
  }

  @Override
  public void handle(final CrawlInfo crawlInfo)
    throws SchemaCrawlerException
  {
    if (options.isNoInfo() || crawlInfo == null)
    {
      return;
    }

    try
    {
      final JSONObject jsonSchemaCrawlerHeaderInfo = new JSONObject();
      jsonRoot.put("schemaCrawlerHeaderInfo", jsonSchemaCrawlerHeaderInfo);

      jsonSchemaCrawlerHeaderInfo
        .put("crawlTimestamp", formatTimestamp(crawlInfo.getCrawlTimestamp()));
      jsonSchemaCrawlerHeaderInfo.put("title", crawlInfo.getTitle());
    }
    catch (final JSONException e)
    {
      LOGGER.log(Level.FINER,
                 e,
                 new StringFormat("Error outputting SchemaCrawlerHeaderInfo: %s",
                                  e.getMessage()));
    }
  }

  @Override
  public void handle(final DatabaseInfo dbInfo)
  {
    if (!printVerboseDatabaseInfo || options.isNoInfo() || dbInfo == null)
    {
      return;
    }

    try
    {
      final JSONObject jsonDbInfo = new JSONObject();
      jsonRoot.put("databaseInfo", jsonDbInfo);

      jsonDbInfo.put("databaseProductName", dbInfo.getProductName());
      jsonDbInfo.put("databaseProductVersion", dbInfo.getProductVersion());
      jsonDbInfo.put("databaseUserName", dbInfo.getUserName());

      if (printVerboseDatabaseInfo && dbInfo.getProperties().size() > 0)
      {
        final JSONArray jsonDbProperties = new JSONArray();
        jsonDbInfo.put("databaseProperties", jsonDbProperties);
        for (final DatabaseProperty property: dbInfo.getProperties())
        {
          final JSONObject jsonDbProperty = new JSONObject();
          jsonDbProperties.put(jsonDbProperty);

          jsonDbProperty.put("name", property.getName());
          jsonDbProperty.put("description", property.getDescription());
          jsonDbProperty.put("value", property.getValue());
        }
      }
    }
    catch (final JSONException e)
    {
      LOGGER.log(Level.FINER,
                 e,
                 new StringFormat("Error outputting DatabaseInfo: %s",
                                  e.getMessage()));
    }

  }

  @Override
  public void handle(final JdbcDriverInfo driverInfo)
  {
    if (!printVerboseDatabaseInfo || options.isNoInfo() || driverInfo == null)
    {
      return;
    }

    try
    {
      final JSONObject jsonDriverInfo = new JSONObject();
      jsonRoot.put("jdbcDriverInfo", jsonDriverInfo);

      jsonDriverInfo.put("driverName", driverInfo.getDriverName());
      jsonDriverInfo.put("driverVersion", driverInfo.getDriverVersion());
      jsonDriverInfo.put("driverClassName", driverInfo.getDriverClassName());
      jsonDriverInfo.put("url", driverInfo.getConnectionUrl());
      jsonDriverInfo.put("isJDBCCompliant",
                         Boolean.toString(driverInfo.isJdbcCompliant()));

      final Collection<JdbcDriverProperty> jdbcDriverProperties = driverInfo
        .getDriverProperties();
      if (jdbcDriverProperties.size() > 0)
      {
        final JSONArray jsonJdbcDriverProperties = new JSONArray();
        jsonDriverInfo.put("jdbcDriverProperties", jsonJdbcDriverProperties);
        for (final JdbcDriverProperty driverProperty: jdbcDriverProperties)
        {
          jsonJdbcDriverProperties.put(printJdbcDriverProperty(driverProperty));
        }
      }
    }
    catch (final JSONException e)
    {
      LOGGER.log(Level.FINER,
                 e,
                 new StringFormat("Error outputting JdbcDriverInfo: %s",
                                  e.getMessage()));
    }

  }

  @Override
  public void handle(final SchemaCrawlerInfo schemaCrawlerInfo)
  {
    if (!printVerboseDatabaseInfo || options.isNoInfo()
        || schemaCrawlerInfo == null)
    {
      return;
    }

    try
    {
      final JSONObject jsonSchemaCrawlerInfo = new JSONObject();
      jsonRoot.put("schemaCrawlerInfo", jsonSchemaCrawlerInfo);

      jsonSchemaCrawlerInfo
        .put("productName", schemaCrawlerInfo.getSchemaCrawlerProductName());
      jsonSchemaCrawlerInfo.put("productVersion",
                                schemaCrawlerInfo.getSchemaCrawlerVersion());
    }
    catch (final JSONException e)
    {
      LOGGER.log(Level.FINER,
                 e,
                 new StringFormat("Error outputting SchemaCrawlerInfo: %s",
                                  e.getMessage()));
    }
  }

  @Override
  public void handleHeaderEnd()
    throws SchemaCrawlerException
  {
  }

  @Override
  public void handleHeaderStart()
    throws SchemaCrawlerException
  {
  }

  @Override
  public void handleInfoEnd()
    throws SchemaCrawlerException
  {
  }

  @Override
  public void handleInfoStart()
    throws SchemaCrawlerException
  {
  }

  private JSONObject printJdbcDriverProperty(final JdbcDriverProperty driverProperty)
  {
    final JSONObject jsonDriverProperty = new JSONObject();

    try
    {
      final Collection<String> choices = driverProperty.getChoices();
      if (choices != null && choices.size() > 0)
      {
        jsonDriverProperty.put("choices", choices);
      }
      final String value = driverProperty.getValue();

      jsonDriverProperty.put("name", driverProperty.getName());
      jsonDriverProperty.put("description", driverProperty.getDescription());
      jsonDriverProperty.put("required", driverProperty.isRequired());
      jsonDriverProperty.put("value", value);
    }
    catch (final JSONException e)
    {
      LOGGER.log(Level.FINER,
                 e,
                 new StringFormat("Error outputting JdbcDriverProperty: %s",
                                  e.getMessage()));
    }

    return jsonDriverProperty;
  }

}
