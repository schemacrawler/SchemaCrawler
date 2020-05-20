/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.schemacrawler;


import static java.util.Objects.requireNonNull;
import static sf.util.IOUtility.readResourceFully;
import static sf.util.Utility.isBlank;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import sf.util.ObjectToString;
import sf.util.TemplatingUtility;

/**
 * The database specific views to get additional database metadata in a standard
 * format.
 */
public final class InformationSchemaViewsBuilder
  implements
  OptionsBuilder<InformationSchemaViewsBuilder, InformationSchemaViews>
{

  public static InformationSchemaViewsBuilder builder()
  {
    return new InformationSchemaViewsBuilder();
  }

  public static InformationSchemaViewsBuilder builder(final InformationSchemaViews informationSchemaViews)
  {
    return new InformationSchemaViewsBuilder().fromOptions(
      informationSchemaViews);
  }

  public static InformationSchemaViews newInformationSchemaViews()
  {
    return new InformationSchemaViews();
  }

  public static InformationSchemaViews newInformationSchemaViews(final Config config)
  {
    return new InformationSchemaViewsBuilder()
      .fromConfig(config)
      .toOptions();
  }

  private final Map<InformationSchemaKey, String> informationSchemaQueries;

  private InformationSchemaViewsBuilder()
  {
    informationSchemaQueries = new EnumMap<>(InformationSchemaKey.class);
  }

  /**
   * Information schema views from a map.
   *
   * @param informationSchemaViewsSql
   *   Map of information schema view definitions.
   */
  @Override
  public InformationSchemaViewsBuilder fromConfig(final Config informationSchemaViewsSql)
  {
    if (informationSchemaViewsSql == null)
    {
      return this;
    }

    for (final InformationSchemaKey key : InformationSchemaKey.values())
    {
      if (informationSchemaViewsSql.containsKey(key.getLookupKey()))
      {
        try
        {
          informationSchemaQueries.put(key,
                                       informationSchemaViewsSql.get(key.getLookupKey()));
        }
        catch (final IllegalArgumentException e)
        {
          // Ignore
        }
      }
    }

    return this;
  }

  @Override
  public InformationSchemaViewsBuilder fromOptions(final InformationSchemaViews informationSchemaViews)
  {
    if (informationSchemaViews == null)
    {
      return this;
    }

    informationSchemaQueries.putAll(informationSchemaViews.getAllInformationSchemaViews());

    return this;
  }

  @Override
  public Config toConfig()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public InformationSchemaViews toOptions()
  {
    return new InformationSchemaViews(informationSchemaQueries);
  }

  /**
   * Information schema views from a map.
   *
   * @param classpath
   *   Classpath location for SQL queries.
   * @return Builder
   */
  public InformationSchemaViewsBuilder fromResourceFolder(final String classpath)
  {
    if (isBlank(classpath))
    {
      return this;
    }

    for (final InformationSchemaKey key : InformationSchemaKey.values())
    {
      final String resource;
      if (classpath == null)
      {
        resource = key.getResource();
      }
      else
      {
        resource = String.format("%s/%s", classpath, key.getResource());
      }
      final String sql = readResourceFully(resource);
      if (!isBlank(sql))
      {
        informationSchemaQueries.put(key, sql);
      }
    }

    return this;
  }

  public void substituteAll(final String templateKey,
                            final String templateValue)
  {
    final Map<String, String> map = new HashMap<>();
    map.put(templateKey, templateValue);
    for (final Map.Entry<InformationSchemaKey, String> query : informationSchemaQueries.entrySet())
    {
      String sql = query.getValue();
      sql = TemplatingUtility.expandTemplate(sql, map);
      query.setValue(sql);
    }
  }

  @Override
  public String toString()
  {
    return ObjectToString.toString(informationSchemaQueries);
  }

  /**
   * Sets definitions SQL.
   *
   * @param key
   *   SQL query key
   * @param sql
   *   Definitions SQL.
   * @return Builder
   */
  public InformationSchemaViewsBuilder withSql(final InformationSchemaKey key,
                                               final String sql)
  {
    requireNonNull(key, "No key provided");
    if (isBlank(sql))
    {
      informationSchemaQueries.remove(key);
    }
    else
    {
      informationSchemaQueries.put(key, sql);
    }
    return this;
  }

}
