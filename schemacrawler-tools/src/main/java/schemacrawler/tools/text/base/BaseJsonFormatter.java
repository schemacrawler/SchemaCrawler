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

package schemacrawler.tools.text.base;


import java.util.Arrays;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.DatabaseInfo;
import schemacrawler.schema.DatabaseProperty;
import schemacrawler.schema.JdbcDriverInfo;
import schemacrawler.schema.JdbcDriverProperty;
import schemacrawler.schema.SchemaCrawlerInfo;
import schemacrawler.schemacrawler.Options;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.text.utility.org.json.JSONArray;
import schemacrawler.tools.text.utility.org.json.JSONException;
import schemacrawler.tools.text.utility.org.json.JSONObject;

/**
 * Text formatting of schema.
 * 
 * @author Sualeh Fatehi
 */
public abstract class BaseJsonFormatter<O extends Options>
  extends BaseFormatter<O>
{

  protected static final Logger LOGGER = Logger
    .getLogger(BaseJsonFormatter.class.getName());

  protected final JSONObject jsonDatabase;

  protected BaseJsonFormatter(final O options,
                              final boolean printVerboseDatabaseInfo,
                              final OutputOptions outputOptions)
    throws SchemaCrawlerException
  {
    super(options, printVerboseDatabaseInfo, outputOptions);
    jsonDatabase = new JSONObject();
  }

  public void begin()
    throws SchemaCrawlerException
  {
    if (!outputOptions.isNoHeader())
    {
      out.println("[");
    }
  }

  public void end()
    throws SchemaCrawlerException
  {
    try
    {
      jsonDatabase.write(out, 2);

      if (outputOptions.isNoFooter())
      {
        out.println(",");
      }
      else
      {
        out.println("]");
      }

      out.close();
    }
    catch (final JSONException e)
    {
      throw new SchemaCrawlerException("Could not write database", e);
    }
  }

  public void handle(final DatabaseInfo dbInfo)
  {
    if (outputOptions.isNoInfo() || dbInfo == null)
    {
      return;
    }

    try
    {
      final JSONObject jsonDbInfo = new JSONObject();
      jsonDatabase.put("databaseInfo", jsonDbInfo);

      jsonDbInfo.put("databaseProductName", dbInfo.getProductName());
      jsonDbInfo.put("databaseProductVersion", dbInfo.getProductVersion());
      jsonDbInfo.put("databaseUserName", dbInfo.getUserName());

      if (printVerboseDatabaseInfo && dbInfo.getProperties().length > 0)
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

  public void handle(final JdbcDriverInfo driverInfo)
  {
    if (outputOptions.isNoInfo() || driverInfo == null)
    {
      return;
    }

    try
    {
      final JSONObject jsonDriverInfo = new JSONObject();
      jsonDatabase.put("jdbcDriverInfo", jsonDriverInfo);

      jsonDriverInfo.put("driverName", driverInfo.getDriverName());
      jsonDriverInfo.put("driverVersion", driverInfo.getDriverVersion());
      jsonDriverInfo.put("driverClassName", driverInfo.getDriverClassName());
      jsonDriverInfo.put("url", driverInfo.getConnectionUrl());
      jsonDriverInfo.put("isJDBCCompliant",
                         Boolean.toString(driverInfo.isJdbcCompliant()));

      final JdbcDriverProperty[] jdbcDriverProperties = driverInfo
        .getDriverProperties();
      if (printVerboseDatabaseInfo && jdbcDriverProperties.length > 0)
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

  public void handle(final SchemaCrawlerInfo schemaCrawlerInfo)
  {
    if (outputOptions.isNoInfo() || schemaCrawlerInfo == null)
    {
      return;
    }

    try
    {
      final JSONObject jsonSchemaCrawlerInfo = new JSONObject();
      jsonDatabase.put("schemaCrawlerInfo", jsonSchemaCrawlerInfo);

      jsonSchemaCrawlerInfo
        .put("productName", schemaCrawlerInfo.getSchemaCrawlerProductName());
      jsonSchemaCrawlerInfo.put("productVersion",
                                schemaCrawlerInfo.getSchemaCrawlerVersion());

      if (printVerboseDatabaseInfo)
      {
        final SortedMap<String, String> systemProperties = new TreeMap<String, String>(schemaCrawlerInfo
          .getSystemProperties());
        if (!systemProperties.isEmpty())
        {
          final JSONObject jsonSystemProperties = new JSONObject();
          jsonSchemaCrawlerInfo.put("systemProperties", jsonSystemProperties);
          for (final Entry<String, String> systemProperty: systemProperties
            .entrySet())
          {
            jsonSystemProperties.put(systemProperty.getKey(),
                                     systemProperty.getValue());
          }
        }
      }
    }
    catch (final JSONException e)
    {
      LOGGER.log(Level.FINER,
                 "Error outputting SchemaCrawlerInfo: " + e.getMessage(),
                 e);
    }
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
      final String[] choices = driverProperty.getChoices();
      if (choices != null && choices.length > 0)
      {
        jsonDriverProperty.put("choices", Arrays.asList(choices));
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
