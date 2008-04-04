/*
 * SchemaCrawler
 * Copyright (c) 2000-2008, Sualeh Fatehi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package schemacrawler.schemacrawler;


import java.util.HashMap;
import java.util.Map;

/**
 * The database specific views to get additional database metadata in a
 * standard format.
 * 
 * @author Sualeh Fatehi
 */
public final class InformationSchemaViews
  implements Options
{

  private static final long serialVersionUID = 3587581365346059044L;

  private static final String KEY_INFORMATION_SCHEMA_VIEWS = "select.INFORMATION_SCHEMA.VIEWS";
  private static final String KEY_INFORMATION_SCHEMA_TRIGGERS = "select.INFORMATION_SCHEMA.TRIGGERS";
  private static final String KEY_INFORMATION_SCHEMA_TABLE_CONSTRAINTS = "select.INFORMATION_SCHEMA.TABLE_CONSTRAINTS";
  private static final String KEY_GET_INDEX_INFO = "getIndexInfo";
  private static final String KEY_INFORMATION_SCHEMA_ROUTINES = "select.INFORMATION_SCHEMA.ROUTINES";
  private static final String KEY_INFORMATION_SCHEMA_CHECK_CONSTRAINTS = "select.INFORMATION_SCHEMA.CHECK_CONSTRAINTS";

  private final Map<String, Query> informationSchemaQueries;

  /**
   * Creates empty information schema views.
   */
  public InformationSchemaViews()
  {
    this(null);
  }

  /**
   * Information schema views from a map.
   * 
   * @param informationSchemaViewsSql
   *        Map of information schema view definitions.
   */
  public InformationSchemaViews(final Map<String, String> informationSchemaViewsSql)
  {
    informationSchemaQueries = new HashMap<String, Query>();
    if (informationSchemaViewsSql != null)
    {
      final String[] keys = new String[] {
          KEY_INFORMATION_SCHEMA_VIEWS,
          KEY_INFORMATION_SCHEMA_TRIGGERS,
          KEY_INFORMATION_SCHEMA_TABLE_CONSTRAINTS,
          KEY_GET_INDEX_INFO,
          KEY_INFORMATION_SCHEMA_ROUTINES,
          KEY_INFORMATION_SCHEMA_CHECK_CONSTRAINTS,
      };
      for (final String key: keys)
      {
        if (informationSchemaViewsSql.containsKey(key))
        {
          try
          {
            final Query query = new Query(key, informationSchemaViewsSql
              .get(key));
            informationSchemaQueries.put(key, query);
          }
          catch (final IllegalArgumentException e)
          {
            // Ignore
          }
        }
      }
    }
  }

  /**
   * Gets the table check constraints SQL from the additional
   * configuration.
   * 
   * @return Table check constraints SQL.
   */
  public Query getCheckConstraints()
  {
    return informationSchemaQueries
      .get(KEY_INFORMATION_SCHEMA_CHECK_CONSTRAINTS);
  }

  /**
   * Gets the index info SQL from the additional configuration.
   * 
   * @return Index info constraints SQL.
   */
  public Query getIndexInfo()
  {
    return informationSchemaQueries.get(KEY_GET_INDEX_INFO);
  }

  /**
   * Gets the procedure definitions SQL from the additional
   * configuration.
   * 
   * @return Procedure defnitions SQL.
   */
  public Query getRoutines()
  {
    return informationSchemaQueries.get(KEY_INFORMATION_SCHEMA_ROUTINES);
  }

  /**
   * Gets the table constraints SQL from the additional configuration.
   * 
   * @return Table constraints SQL.
   */
  public Query getTableConstraints()
  {
    return informationSchemaQueries
      .get(KEY_INFORMATION_SCHEMA_TABLE_CONSTRAINTS);
  }

  /**
   * Gets the trigger definitions SQL from the additional configuration.
   * 
   * @return Trigger defnitions SQL.
   */
  public Query getTriggers()
  {
    return informationSchemaQueries.get(KEY_INFORMATION_SCHEMA_TRIGGERS);
  }

  /**
   * Gets the view definitions SQL from the additional configuration.
   * 
   * @return View defnitions SQL.
   */
  public Query getViews()
  {
    return informationSchemaQueries.get(KEY_INFORMATION_SCHEMA_VIEWS);
  }

  public boolean hasCheckConstraintsSql()
  {
    return informationSchemaQueries
      .containsKey(KEY_INFORMATION_SCHEMA_CHECK_CONSTRAINTS);
  }

  public boolean hasIndexInfoSql()
  {
    return informationSchemaQueries.containsKey(KEY_GET_INDEX_INFO);
  }

  public boolean hasRoutinesSql()
  {
    return informationSchemaQueries
      .containsKey(KEY_INFORMATION_SCHEMA_ROUTINES);
  }

  public boolean hasTableConstraintsSql()
  {
    return informationSchemaQueries
      .containsKey(KEY_INFORMATION_SCHEMA_TABLE_CONSTRAINTS);
  }

  public boolean hasTriggerSql()
  {
    return informationSchemaQueries
      .containsKey(KEY_INFORMATION_SCHEMA_TRIGGERS);
  }

  public boolean hasViewsSql()
  {
    return informationSchemaQueries.containsKey(KEY_INFORMATION_SCHEMA_VIEWS);
  }

  /**
   * Sets the table check constraints SQL from the additional
   * configuration.
   * 
   * @param sql
   *        Table check constraints SQL.
   */
  public void setCheckConstraintsSql(final String sql)
  {
    informationSchemaQueries
      .put(KEY_INFORMATION_SCHEMA_CHECK_CONSTRAINTS,
           new Query(KEY_INFORMATION_SCHEMA_CHECK_CONSTRAINTS, sql));
  }

  /**
   * Sets the index info SQL from the additional configuration.
   * 
   * @param sql
   *        Index info constraints SQL.
   */
  public void setIndexInfoSql(final String sql)
  {
    informationSchemaQueries.put(KEY_GET_INDEX_INFO,
                                 new Query(KEY_GET_INDEX_INFO, sql));
  }

  /**
   * Sets the procedure definitions SQL from the additional
   * configuration.
   * 
   * @param sql
   *        Procedure defnitions SQL.
   */
  public void setRoutinesSql(final String sql)
  {
    informationSchemaQueries
      .put(KEY_INFORMATION_SCHEMA_ROUTINES,
           new Query(KEY_INFORMATION_SCHEMA_ROUTINES, sql));
  }

  /**
   * Sets the table constraints SQL from the additional configuration.
   * 
   * @param sql
   *        Table constraints SQL.
   */
  public void setTableConstraintsSql(final String sql)
  {
    informationSchemaQueries
      .put(KEY_INFORMATION_SCHEMA_TABLE_CONSTRAINTS,
           new Query(KEY_INFORMATION_SCHEMA_TABLE_CONSTRAINTS, sql));
  }

  /**
   * Sets the trigger definitions SQL from the additional configuration.
   * 
   * @param sql
   *        Trigger defnitions SQL.
   */
  public void setTriggersSql(final String sql)
  {
    informationSchemaQueries
      .put(KEY_INFORMATION_SCHEMA_TRIGGERS,
           new Query(KEY_INFORMATION_SCHEMA_TRIGGERS, sql));
  }

  /**
   * Sets the view definitions SQL from the additional configuration.
   * 
   * @param sql
   *        View defnitions SQL.
   */
  public void setViewsSql(final String sql)
  {
    informationSchemaQueries.put(KEY_INFORMATION_SCHEMA_VIEWS,
                                 new Query(KEY_INFORMATION_SCHEMA_VIEWS, sql));
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    return informationSchemaQueries.toString();
  }

}
