/*
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2015, Sualeh Fatehi.
 * This library is free software; you can redistribute it and/or modify it under
 * the terms
 * of the GNU Lesser General Public License as published by the Free Software
 * Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package schemacrawler.tools.text.base;


import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.CrawlHeaderInfo;
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
  public void handle(final CrawlHeaderInfo crawlHeaderInfo)
    throws SchemaCrawlerException
  {
    if (options.isNoInfo() || crawlHeaderInfo == null)
    {
      return;
    }

    try
    {
      final JSONObject jsonSchemaCrawlerHeaderInfo = new JSONObject();
      jsonRoot.put("schemaCrawlerHeaderInfo", jsonSchemaCrawlerHeaderInfo);

      jsonSchemaCrawlerHeaderInfo.put("crawlTimestamp",
                                      formatTimestamp(crawlHeaderInfo
                                        .getCrawlTimestamp()));
      jsonSchemaCrawlerHeaderInfo.put("title", crawlHeaderInfo.getTitle());
    }
    catch (final JSONException e)
    {
      LOGGER.log(Level.FINER,
                 "Error outputting SchemaCrawlerHeaderInfo: " + e.getMessage(),
                 e);
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
                 "Error outputting DatabaseInfo: " + e.getMessage(),
                 e);
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
                 "Error outputting JdbcDriverInfo: " + e.getMessage(),
                 e);
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
                 "Error outputting SchemaCrawlerInfo: " + e.getMessage(),
                 e);
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
                 "Error outputting JdbcDriverProperty: " + e.getMessage(),
                 e);
    }

    return jsonDriverProperty;
  }

}
